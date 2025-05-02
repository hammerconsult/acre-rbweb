package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.tributario.StatusSolicitacaoPIX;
import br.com.webpublico.util.CarneUtil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class PixFacade implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(PixFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private ConfiguracaoPixFacade configuracaoPixFacade;

    public List<PixDTO> gerarQrCodePIX(List<DAM> dans) {
        try {
            ConfiguracaoPix configuracaoPix = configuracaoPixFacade.buscarConfiguracaoPix();

            if (isConfiguracaoPixValida(configuracaoPix)) {
                Map<Long, ConfiguracaoPixDam> mapConfiguracaoPix = Maps.newHashMap();
                for (ConfiguracaoPixDam configuracaoPixDam : configuracaoPix.getConfiguracoesPorDam()) {
                    mapConfiguracaoPix.put(configuracaoPixDam.getConfiguracaoDAM().getId(), configuracaoPixDam);
                }

                if (!mapConfiguracaoPix.isEmpty()) {
                    String url = configuracaoPix.getUrlIntegrador() + "/integracao/gerar-pix";

                    List<PixDTO> lotePix = popularLotePix(dans, mapConfiguracaoPix);

                    if (!lotePix.isEmpty()) {
                        WrapperPixDTO wrapperPixDTO = new WrapperPixDTO();
                        wrapperPixDTO.getLotePix().addAll(lotePix);
                        wrapperPixDTO.setAppKey(configuracaoPix.getAppKey());
                        wrapperPixDTO.setUrlToken(configuracaoPix.getUrlToken());
                        wrapperPixDTO.setUrlQrCode(configuracaoPix.getUrlQrCode());

                        ResponseEntity<WrapperPixDTO> responseEntity = new RestTemplate().postForEntity(url, wrapperPixDTO, WrapperPixDTO.class);

                        wrapperPixDTO = responseEntity.getBody();

                        if (wrapperPixDTO != null && wrapperPixDTO.getLotePix() != null) {
                            salvarPix(wrapperPixDTO);
                            return wrapperPixDTO.getLotePix();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao gerar qrcode pix. ", e);

        }
        return Lists.newArrayList();
    }

    private boolean isConfiguracaoPixValida(ConfiguracaoPix configuracaoPix) {
        if (configuracaoPix != null && !StringUtils.isBlank(configuracaoPix.getUrlIntegrador())
            && !StringUtils.isBlank(configuracaoPix.getBase())
            && !StringUtils.isBlank(configuracaoPix.getAppKey())
            && !StringUtils.isBlank(configuracaoPix.getUrlToken())
            && !StringUtils.isBlank(configuracaoPix.getUrlQrCode())
            && !configuracaoPix.getConfiguracoesPorDam().isEmpty()) {
            return true;
        }
        logger.debug("Configuracao pix invalida ou inexistente.");
        return false;
    }

    private void salvarPix(WrapperPixDTO wrapperPixDTO) {
        for (PixDTO pixDTO : wrapperPixDTO.getLotePix()) {
            if (pixDTO != null && ((pixDTO.getOcorrencias() == null || pixDTO.getOcorrencias().isEmpty()) && !StringUtils.isBlank(pixDTO.getQrCode()))) {
                PIX pix = null;
                DAM dam = damFacade.recuperar(pixDTO.getIdDam());

                if (dam != null) {
                    if (dam.getPix() != null && dam.getPix().getId() != null) {
                        pix = em.find(PIX.class, dam.getPix().getId());
                    }
                    if (pix == null) {
                        pix = new PIX();
                    }
                    pix.setValidadeEmSegundos(pixDTO.getQuantidadeSegundoExpiracao());
                    pix.setQrCode(pixDTO.getQrCode());
                    pix.setTxid(pixDTO.getCodigoConciliacaoSolicitante());
                    pix.setValor(pixDTO.getValorOriginalSolicitacao());
                    pix.setDataSolicitacao(pixDTO.getTimestampCriacaoSolicitacao() != null ?
                        DataUtil.parseDateRFC3339(pixDTO.getTimestampCriacaoSolicitacao()) : null);

                    StatusPIX statusPIX = new StatusPIX();
                    statusPIX.setDataSituacao(new Date());
                    statusPIX.setStatusSolicitacao(StatusSolicitacaoPIX.ATIVA);
                    statusPIX.setPix(pix);

                    pix.getStatusSolicitacao().add(statusPIX);

                    pix = em.merge(pix);

                    dam.setPix(pix);
                    dam.setQrCodePIX(pix.getQrCode());
                    em.merge(dam);
                }
            } else {
                if (pixDTO != null && pixDTO.getIdDam() != null) {
                    DAM dam = damFacade.recuperar(pixDTO.getIdDam());
                    for (OcorrenciasPixDTO ocorrencia : pixDTO.getOcorrencias()) {
                        OcorrenciaPix ocorrenciaPix = new OcorrenciaPix(ocorrencia);
                        ocorrenciaPix.setDam_id(dam.getId());
                        em.persist(ocorrenciaPix);
                    }
                }
            }
        }
    }

    private List<PixDTO> popularLotePix(List<DAM> dans, Map<Long, ConfiguracaoPixDam> mapConfiguracaoPix) {
        List<PixDTO> lotePix = Lists.newArrayList();
        for (DAM dam : dans) {
            if (canAdicionarQrCodeAoDam(dam.getId())) {
                PixDTO pix = popularPixDTO(dam);
                if (pix != null) {
                    Long idConfiguracaoDam = buscarConfiguracaoDamPeloIdDam(dam.getId());

                    if (idConfiguracaoDam != null && mapConfiguracaoPix.get(idConfiguracaoDam) != null) {
                        ConfiguracaoPixDam configuracaoPixDam = mapConfiguracaoPix.get(idConfiguracaoDam);
                        pix.setNumeroConvenio(Long.valueOf(configuracaoPixDam.getNumeroConvenio()));
                        pix.setCodigoSolicitacaoBancoCentralBrasil(configuracaoPixDam.getChavePix());
                        pix.setChaveAcesso(configuracaoPixDam.getChaveAcesso());
                        pix.setAuthorization(configuracaoPixDam.getAuthorization());

                        lotePix.add(pix);
                    } else {
                        logger.debug("Erro ao gerar qrCode pix. Configuracao dam nao encontrada.");
                    }
                }
            }
        }
        return lotePix;
    }

    private boolean canAdicionarQrCodeAoDam(Long idDam) {
        String sql = " select dam.id from dam " +
            " inner join itemdam on dam.id = itemdam.dam_id " +
            " inner join parcelavalordivida pvd on itemdam.parcela_id = pvd.id " +
            " inner join valordivida vd on pvd.valordivida_id = vd.id " +
            " inner join divida d on vd.divida_id = d.id " +
            " where dam.id = :idDam " +
            " and coalesce(d.gerarqrcodepix, 0) = :gerarQrCode ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idDam", idDam);
        q.setParameter("gerarQrCode", 0);

        return q.getResultList().isEmpty();
    }

    private PixDTO popularPixDTO(DAM dam) {
        if (StringUtils.isBlank(dam.getQrCodePIX()) || dam.getPix() == null || dam.getPix().getQrCode() == null) {
            VOPessoa pessoa = buscarDadosPessoaDoDam(dam.getId());

            PixDTO pix = new PixDTO();
            pix.setIdDam(dam.getId());
            pix.setVencimento(dam.getVencimento());
            String cpfCnpj = (pessoa != null && pessoa.getCpfCnpj() != null) ? StringUtil.retornaApenasNumeros(pessoa.getCpfCnpj()) : "";
            pix.setCpfDevedor(cpfCnpj.length() == 11 ? cpfCnpj : "");
            pix.setCnpjDevedor(cpfCnpj.length() == 14 ? cpfCnpj : "");
            pix.setNomeDevedor((pessoa != null && pessoa.getNome() != null) ? pessoa.getNome().toUpperCase() : "");
            pix.setIndicadorCodigoBarras("S");
            pix.setCodigoGuiaRecebimento(CarneUtil.montarCodigoBarrasDaLinhaDigitavel(dam.getCodigoBarras()));
            pix.setDescricaoSolicitacaoPagamento("Arrecadação Pix");
            pix.setValorOriginalSolicitacao(dam.getValorTotal());

            return pix;
        }
        return null;
    }

    private VOPessoa buscarDadosPessoaDoDam(Long idDam) {
        String sql = " select coalesce(pf.nome, pj.razaosocial) as nome, " +
            "                 coalesce(pf.cpf, pj.cnpj) as cpf " +
            " from dam " +
            " inner join itemdam idam on dam.id = idam.dam_id " +
            " inner join parcelavalordivida pvd on idam.parcela_id = pvd.id " +
            " inner join valordivida vd on pvd.valordivida_id = vd.id " +
            " inner join calculo c on vd.calculo_id = c.id " +
            " inner join calculopessoa cp on c.id = cp.calculo_id " +
            " inner join pessoa pes on cp.pessoa_id = pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " where dam.id = :idDam " +
            " order by dam.id desc fetch first 1 rows only ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idDam", idDam);

        List<Object[]> dados = q.getResultList();

        if (dados != null && !dados.isEmpty()) {
            Object[] obj = dados.get(0);
            VOPessoa pessoa = new VOPessoa();
            pessoa.setNome(obj[0] != null ? (String) obj[0] : null);
            pessoa.setCpfCnpj(obj[1] != null ? (String) obj[1] : null);

            return pessoa;
        }
        logger.debug("Pessoa nao encontrada para o dam: {}", idDam);
        return null;
    }

    public Long buscarConfiguracaoDamPeloIdDam(Long idDam) {
        String sql = " select distinct cd.id " +
            " from parcelavalordivida pvd " +
            " inner join valordivida vd on pvd.valordivida_id = vd.id " +
            " inner join divida d on vd.divida_id = d.id " +
            " inner join configuracaodam cd on d.configuracaodam_id = cd.id " +
            " inner join itemdam on pvd.id = itemdam.parcela_id " +
            " where itemdam.dam_id = :idDam " +
            " order by cd.id desc fetch first 1 rows only ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idDam", idDam);

        List<BigDecimal> ids = q.getResultList();

        return (ids != null && !ids.isEmpty()) ? ids.get(0).longValue() : null;
    }

    @Asynchronous
    public void processarPagamentoPix(List<PayloadPixDTO> payloads) {
        try {
            for (PayloadPixDTO payload : payloads) {
                DAM dam = recuperarDamPeloTxid(payload.getTxid());

                if (dam != null) {
                    consultaDebitoFacade.baixarParcelasDoDam(dam);

                    if (dam.getPix() != null) {
                        PIX pix = em.find(PIX.class, dam.getPix().getId());
                        pix.setEndToEndId(payload.getEndToEndId());
                        pix.setValorPago(new BigDecimal(payload.getValor().replace(",", ".")));
                        pix.setInfoPagador(payload.getInfoPagador());
                        pix.setDataPagamento(formatarData(payload));

                        StatusPIX statusPIX = new StatusPIX();
                        statusPIX.setDataSituacao(new Date());
                        statusPIX.setStatusSolicitacao(StatusSolicitacaoPIX.CONCLUIDA);
                        statusPIX.setPix(pix);

                        pix.getStatusSolicitacao().add(statusPIX);

                        em.merge(pix);
                    }
                    logger.debug("Pagamento do dam {} efetuado.", dam.getNumeroDAM());
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao processar pagamento pix. ", e);
        }
    }

    private Date formatarData(PayloadPixDTO payload) {
        try {
            return DataUtil.getDateParse(payload.getHorario(), "yyyy-MM-dd-hh.mm.ss");
        } catch (Exception e) {
            return new Date();
        }
    }

    private DAM recuperarDamPeloTxid(String txid) {
        try {
            String sql = " select dam.* from dam " +
                " inner join pix on dam.pix_id = pix.id " +
                " where pix.txid = :txid ";

            Query q = em.createNativeQuery(sql, DAM.class);
            q.setParameter("txid", txid);

            List<DAM> dans = q.getResultList();

            if (dans != null && !dans.isEmpty()) {
                DAM dam = dans.get(0);
                Hibernate.initialize(dam.getPix().getStatusSolicitacao());

                if (!StatusSolicitacaoPIX.CONCLUIDA.equals(dam.getPix().getStatusAtual().getStatusSolicitacao())) {
                    return dam;
                }
            }
            return (dans != null && !dans.isEmpty()) ? dans.get(0) : null;
        } catch (Exception e) {
            logger.error("Erro ao recuperar dam pelo txid. ", e);
        }
        return null;
    }

    private void baixarParcelasDaCotaUnicaOrCotaUnicaDaParcela(ParcelaValorDivida parcela) throws Exception {
        List<ParcelaValorDivida> demaisParcelas = consultaDebitoFacade.buscarParcelasDaCotaUnicaOrACotaUnicaDasParcelas(parcela, SituacaoParcela.EM_ABERTO);

        if (demaisParcelas != null && !demaisParcelas.isEmpty()) {
            gerarDansDemaisParcelas(demaisParcelas);

            for (ParcelaValorDivida outraParcela : demaisParcelas) {
                SituacaoParcelaValorDivida situacaoAnterior = consultaDebitoFacade.getUltimaSituacao(outraParcela);
                em.persist(new SituacaoParcelaValorDivida(SituacaoParcela.BAIXADO_OUTRA_OPCAO, outraParcela, situacaoAnterior.getSaldo()));
            }
        }
    }

    private void gerarDansDemaisParcelas(List<ParcelaValorDivida> demaisParcelas) throws Exception {
        for (ParcelaValorDivida parcela : demaisParcelas) {
            gerarDamEmNovaTransacao(parcela);
        }
    }

    private void gerarDamEmNovaTransacao(ParcelaValorDivida parcela) throws Exception {
        DAM dam = damFacade.recuperaDAMPeloIdParcela(parcela.getId());
        if (dam == null) {
            Exercicio exercicio = damFacade.getExercicioFacade().recuperarExercicioPeloAno(Calendar.getInstance().get(Calendar.YEAR));

            Calendar calendar = DataUtil.ultimoDiaMes(new Date());
            Date vencimentoDam = calendar.getTime();

            if (parcela.getVencimento() != null) {
                if (DataUtil.dataSemHorario(parcela.getVencimento()).compareTo(DataUtil.dataSemHorario(new Date())) > 0) {
                    vencimentoDam = parcela.getVencimento();
                }
            }
            vencimentoDam = damFacade.ajustarDataUtil(vencimentoDam);
            damFacade.gerarDAM(new ParcelaValorDivida(parcela.getId()), vencimentoDam, exercicio, true, null);
        }
    }

    public void gerarQrCodePIXPeloIdValorDivida(Long idValorDivida) {
        if (idValorDivida != null) {
            DAM dam = recuperarDamPeloIdValorDivida(idValorDivida);
            if (dam != null) {
                gerarQrCodePIX(Lists.newArrayList(dam));
            }
        }
    }

    private DAM recuperarDamPeloIdValorDivida(Long idValorDivida) {
        String sql = " select dam.* from dam " +
            " inner join itemdam idam on dam.id = idam.dam_id " +
            " inner join parcelavalordivida pvd on idam.parcela_id = pvd.id " +
            " inner join valordivida vd on pvd.valordivida_id = vd.id " +
            " where vd.id = :idValorDivida ";

        Query q = em.createNativeQuery(sql, DAM.class);
        q.setParameter("idValorDivida", idValorDivida);

        List<DAM> dans = q.getResultList();
        return (dans != null && !dans.isEmpty()) ? dans.get(0) : null;
    }

}
