/*
 * Codigo gerado automaticamente em Fri Feb 11 14:10:10 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CalculoIPTU;
import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.HistoricoImpressaoDAM;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.ImpressaoCarneIPTU;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.tributario.TipoPerfil;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.services.ServiceDAM;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class ImpressaoCarneIPTUFacade extends AbstractFacade<CalculoIPTU> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExportacaoIPTUFacade exportacaoIPTUFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private PixFacade pixFacade;
    @EJB
    ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;

    public ImpressaoCarneIPTUFacade() {
        super(CalculoIPTU.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ImpressaoCarneIPTU> buscarCarnesIPTU(Integer exercicio, String inscricaoInicial, String inscricaoFinal) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ")
            .append(" iptu.id as iptu, ")  //0
            .append(" pvd.id, ") //1
            .append(" ci.inscricaocadastral, ") //2
            .append(" dam.numerodam, ") //3
            .append(" pvd.valor, ") //4
            .append(" case WHEN TRUNC(coalesce(dam.vencimento, pvd.VENCIMENTO)) < TRUNC(to_date('")
            .append(DataUtil.getDataFormatada(new Date(), "dd/MM/yyyy"))
            .append("','dd/MM/yyyy')) then 'VENCIDO' else dam.codigobarras end as codigobarras, ") //5
            .append(" coalesce(dam.vencimento, pvd.VENCIMENTO) as vencimento, ") //6
            .append(" sum(carne.fracaoideal) as fracaoideal, ") //7
            .append(" sum(carne.areaconstruida) as areaconstruida, ") //8
            .append(" sum(carne.areaexcedente) as areaexcedente, ") //9
            .append(" sum(carne.vlrvenalterreno) as vlrvenalterreno, ") //10
            .append(" sum(carne.vlrvenaledificacao) as vlrvenaledificacao, ") //11
            .append(" sum(carne.vlrvenalexcedente) as vlrvenalexcedente, ") //12
            .append(" max(carne.aliquota) as aliquota, ") //13
            .append(" case when op.promocional = 1 then 'Única' else pvd.sequenciaparcela end as sequenciaparcela, ") //14
            .append(" (substr(dam.codigoBarras, 0, 11) || ")
            .append("  substr(dam.codigoBarras, 15, 11) || ")
            .append("  substr(dam.codigoBarras, 29, 11) || ")
            .append("  substr(dam.codigoBarras, 43, 11)) as barras, ") //15
            .append("  (select coalesce(dam.VALORORIGINAL, pvd.valor) + coalesce(dam.MULTA, 0) + coalesce(dam.JUROS, 0) ")
            .append(" + coalesce(dam.CORRECAOMONETARIA, 0) - cast(coalesce(sum(ipvd.valor * (desconto.desconto/100)),0) AS NUMBER(12, 2)) ")
            .append("  from descontoitemparcela desconto ")
            .append("  inner join itemparcelavalordivida ipvd on ipvd.id = desconto.itemparcelavalordivida_id ")
            .append("  where ipvd.parcelavalordivida_id = pvd.id and desconto.desconto > 0 ")
            .append("  and to_date(:dataOperacao, 'dd/MM/yyyy') between DESCONTO.INICIO and DESCONTO.FIM) as valorcomdesconto, ") //16
            .append(" coalesce((select sum(ipvd.valor) from itemparcelavalordivida ipvd ")
            .append("  inner join itemvalordivida ivd on ivd.id = ipvd.itemvalordivida_id ")
            .append("  inner join tributo on tributo.id = ivd.tributo_id ")
            .append("  left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd.id ")
            .append("  where ipvd.parcelavalordivida_id = pvd.id and tributo.tipotributo = 'IMPOSTO'),0) as imposto, ") //17
            .append(" coalesce((select sum(ipvd.valor) from itemparcelavalordivida ipvd")
            .append("  inner join itemvalordivida ivd on ivd.id = ipvd.itemvalordivida_id ")
            .append("  inner join tributo on tributo.id = ivd.tributo_id ")
            .append("  left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd.id ")
            .append("  where ipvd.parcelavalordivida_id = pvd.id and tributo.tipotributo = 'TAXA'),0) as taxa, ") //18
            .append("  (select cast(coalesce(sum(ipvd.valor * (desconto.desconto/100)),0) AS NUMBER(12, 2)) ")
            .append("  from descontoitemparcela desconto ")
            .append("  inner join itemparcelavalordivida ipvd on ipvd.id = desconto.itemparcelavalordivida_id ")
            .append("  where ipvd.parcelavalordivida_id = pvd.id and desconto.desconto >0 ")
            .append("  and to_date(:dataOperacao, 'dd/MM/yyyy') between DESCONTO.INICIO and DESCONTO.FIM) as desconto, ") //19
            .append(" (select sum(i.valorreal) from itemcalculoiptu i inner join eventoconfiguradoiptu configurado on configurado.id = i.evento_id inner join eventocalculo e on e.id = configurado.eventoCALCULO_id where E.tipoLancamento = 'TAXA' and i.calculoiptu_id = iptu.id ) as totaltaxa, ") //20
            .append(" (select sum(i.valorreal) from itemcalculoiptu i inner join eventoconfiguradoiptu configurado on configurado.id = i.evento_id inner join eventocalculo e on e.id = configurado.eventoCALCULO_id where E.tipoLancamento = 'IMPOSTO' and i.calculoiptu_id = iptu.id ) as totalimposto, ") //21
            .append(" calculo.valorefetivo as valorcalculo, ") //22
            .append(" exercicio.ano, ") //23
            .append(" coalesce(pf.nome, pj.razaosocial) as contribuinte, ") //24
            .append(" coalesce(pf.cpf, pj.cnpj) as cpfcnpj, ") //25

            .append(" max((select vp.VALOR ")
            .append("  from construcao_valoratributo civa ")
            .append("  inner join valoratributo va on va.ID = civa.ATRIBUTOS_ID ")
            .append("  inner join valorpossivel vp on vp.id = va.VALORDISCRETO_ID ")
            .append("  inner join atributo on atributo.id = civa.ATRIBUTOS_KEY ")
            .append("  inner join construcao constru on constru.id = civa.CONSTRUCAO_ID ")
            .append("  where constru.ID = construcao.id and coalesce(constru.cancelada,0) = 0 ")
            .append("  and atributo.IDENTIFICACAO = 'qualidade_construcao')) as qualidade, ") //26

            .append(" max((select vp.VALOR ")
            .append("  from construcao_valoratributo civa ")
            .append("  inner join valoratributo va on va.ID = civa.ATRIBUTOS_ID ")
            .append("  inner join valorpossivel vp on vp.id = va.VALORDISCRETO_ID ")
            .append("  inner join atributo on atributo.id = civa.ATRIBUTOS_KEY ")
            .append("  inner join construcao constru on constru.id = civa.CONSTRUCAO_ID ")
            .append("  where constru.ID = construcao.id and coalesce(constru.cancelada,0) = 0 ")
            .append("  and atributo.IDENTIFICACAO = 'utilizacao_imovel')) as utilizacao_imovel, ") //27

            .append(" max((select vp.VALOR ")
            .append("  from construcao_valoratributo civa ")
            .append("  inner join valoratributo va on va.ID = civa.ATRIBUTOS_ID ")
            .append("  inner join valorpossivel vp on vp.id = va.VALORDISCRETO_ID ")
            .append("  inner join atributo on atributo.id = civa.ATRIBUTOS_KEY ")
            .append("  inner join construcao constru on constru.id = civa.CONSTRUCAO_ID ")
            .append("  where constru.id = construcao.id and coalesce(constru.cancelada,0) = 0 ")
            .append("  and atributo.IDENTIFICACAO = 'tipo_construcao')) as tipo_construcao, ") //28

            .append(" max((select max(su.nome) ")
            .append("  from SERVICOURBANO su ")
            .append("  inner join FaceServico fs on fs.SERVICOURBANO_ID = su.id ")
            .append("  inner join testada on testada.FACE_ID = fs.FACE_ID ")
            .append("  inner join CADASTROIMOBILIARIO ci on ci.lote_id = testada.LOTE_ID ")
            .append("  where ci.id = construcao.imovel_id ")
            .append("  and su.IDENTIFICACAO in ('coleta_lixo_alternado', 'coleta_lixo_diario'))) as coleta_lixo, ") //29

            .append(" max((select max(su.nome) ")
            .append("  from SERVICOURBANO su ")
            .append("  inner join FaceServico fs on fs.SERVICOURBANO_ID = su.id ")
            .append("  inner join testada on testada.FACE_ID = fs.FACE_ID ")
            .append("  inner join CADASTROIMOBILIARIO ci on ci.lote_id = testada.LOTE_ID ")
            .append("  where ci.id = construcao.imovel_id ")
            .append("  and su.IDENTIFICACAO = 'iluminacao_publica')) as iluminacao_publica, ") //30

            .append(" max((select vp.VALOR ")
            .append("  from lote_valoratributo civa ")
            .append("  inner join valoratributo va on va.ID = civa.ATRIBUTOS_ID ")
            .append("  inner join valorpossivel vp on vp.id = va.VALORDISCRETO_ID ")
            .append("  inner join atributo on atributo.id = civa.ATRIBUTOS_KEY ")
            .append("  inner join lote on lote.id = civa.LOTE_ID ")
            .append("  inner join cadastroimobiliario cad on cad.LOTE_ID = lote.id ")
            .append("  where cad.id = construcao.imovel_id ")
            .append("  and atributo.IDENTIFICACAO = 'topografia')) as topografia, ") //31

            .append(" max((select vp.VALOR ")
            .append("  from lote_valoratributo civa ")
            .append("  inner join valoratributo va on va.ID = civa.ATRIBUTOS_ID ")
            .append("  inner join valorpossivel vp on vp.id = va.VALORDISCRETO_ID ")
            .append("  inner join atributo on atributo.id = civa.ATRIBUTOS_KEY ")
            .append("  inner join lote on lote.id = civa.LOTE_ID ")
            .append("  inner join cadastroimobiliario cad on cad.LOTE_ID = lote.id ")
            .append("  where cad.id = construcao.imovel_id ")
            .append("  and atributo.IDENTIFICACAO = 'pedologia' and rownum =1)) as pedologia, ") //32

            .append(" max((select vp.VALOR ")
            .append("  from lote_valoratributo civa ")
            .append("  inner join valoratributo va on va.ID = civa.ATRIBUTOS_ID ")
            .append("  inner join valorpossivel vp on vp.id = va.VALORDISCRETO_ID ")
            .append("  inner join atributo on atributo.id = civa.ATRIBUTOS_KEY ")
            .append("  inner join lote on lote.id = civa.LOTE_ID ")
            .append("  inner join cadastroimobiliario cad on cad.LOTE_ID = lote.id ")
            .append("  where cad.id = construcao.imovel_id ")
            .append("  and atributo.IDENTIFICACAO = 'situacao')) as situacao, ") //33

            .append(" max((select vp.VALOR ")
            .append("  from lote_valoratributo civa ")
            .append("  inner join valoratributo va on va.ID = civa.ATRIBUTOS_ID ")
            .append("  inner join valorpossivel vp on vp.id = va.VALORDISCRETO_ID ")
            .append("  inner join atributo on atributo.id = civa.ATRIBUTOS_KEY ")
            .append("  inner join lote on lote.id = civa.LOTE_ID ")
            .append("  inner join cadastroimobiliario cad on cad.LOTE_ID = lote.id ")
            .append("  where cad.id = construcao.imovel_id ")
            .append("  and atributo.IDENTIFICACAO = 'patrimonio')) as patrimonio,  ") //34
            .append(" dam.situacao as situacaoDam, ") //35
            .append(" dam.tipo as tipoDam, ") //36
            .append(" (coalesce(dam.juros, 0) + coalesce(dam.multa, 0) + coalesce(dam.correcaomonetaria, 0)) as acrescimos, ") //37
            .append(" dam.qrcodepix as qrCodePix ") //38

            .append(" from parcelavalordivida pvd ")
            .append(" inner join situacaoparcelavalordivida situacao on situacao.id = pvd.situacaoAtual_id ")
            .append(" inner join opcaopagamento op on op.id = pvd.opcaopagamento_id ")
            .append(" left join itemdam on itemdam.parcela_id = pvd.id ")
            .append(" left join dam on dam.id = itemdam.dam_id ")
            .append(" inner join valordivida vd on vd.id = pvd.valordivida_id ")
            .append(" inner join exercicio on exercicio.id = vd.exercicio_id ")
            .append(" inner join calculoiptu iptu on iptu.id = vd.calculo_id ")
            .append(" inner join calculo on calculo.id = iptu.id ")
            .append(" inner join cadastroimobiliario ci on ci.id = iptu.cadastroimobiliario_id ")
            .append(" left join construcao on construcao.imovel_id = ci.id and coalesce(CONSTRUCAO.CANCELADA,0) = 0")
            .append(" left join carneiptu carne on carne.calculo_id = iptu.id and (carne.construcao_id = construcao.id or carne.construcao_id is null) ")
            .append(" inner join propriedade on propriedade.imovel_id = ci.id ")
            .append(" inner join pessoa on pessoa.id = propriedade.pessoa_id ")
            .append(" left join pessoafisica pf on pf.id = pessoa.id ")
            .append(" left join pessoajuridica pj on pj.id = pessoa.id ")
            .append(" where situacao.situacaoparcela = :situacaoEmAberto ")
            .append(" and propriedade.id = (select max(prop.id) from propriedade prop where prop.imovel_id = ci.id and prop.finalVigencia is null) ")
            .append(" and dam.id = (select max(itemdam.dam_id) from ItemDam ")
            .append("     inner join dam damAux on damAux.id = itemdam.dam_id ")
            .append("     where itemdam.parcela_id = pvd.id and damAux.tipo = :tipoDam and damAux.situacao = :damAberto) ")
            .append(" and exercicio.ano = :exercicio ")
            .append(" and ci.inscricaocadastral >= :inscricaoInicial ")
            .append(" and ci.inscricaocadastral <= :inscricaoFinal ")
            .append(" and (op.promocional = 0 or TRUNC(coalesce(dam.vencimento, pvd.VENCIMENTO)) >= TRUNC(to_date('")
            .append(DataUtil.getDataFormatada(new Date(), "dd/MM/yyyy"))
            .append("','dd/MM/yyyy'))) ")

            .append(" group by iptu.id, ")
            .append("   pvd.id, ")
            .append("   ci.inscricaocadastral, ")
            .append("   dam.numerodam, ")
            .append("   pvd.valor, dam.MULTA, dam.JUROS, dam.CORRECAOMONETARIA, dam.VALORORIGINAL, ")
            .append("   case WHEN TRUNC(coalesce(dam.vencimento, pvd.VENCIMENTO)) < TRUNC(to_date('")
            .append(DataUtil.getDataFormatada(new Date(), "dd/MM/yyyy"))
            .append("','dd/MM/yyyy')) then 'VENCIDO' else dam.codigobarras end, ") //5
            .append("  coalesce(dam.vencimento, pvd.VENCIMENTO), ") //6
            .append("   case when op.promocional = 1 then 'Única' else pvd.sequenciaparcela end, ")
            .append("   (substr(dam.codigoBarras, 0, 11) || ")
            .append("   substr(dam.codigoBarras, 15, 11) || ")
            .append("   substr(dam.codigoBarras, 29, 11) || ")
            .append("   substr(dam.codigoBarras, 43, 11)),  ")
            .append("   calculo.valorefetivo, ")
            .append("   exercicio.ano, ")
            .append("   coalesce(pf.nome, pj.razaosocial), ")
            .append("   coalesce(pf.cpf, pj.cnpj), ")
            .append("   dam.situacao, ")
            .append("   dam.tipo, ")
            .append("   dam.qrcodepix ")
            .append(" order by iptu.id, pvd.id");

        Query q = em.createNativeQuery(sql.toString());

        q.setParameter("situacaoEmAberto", SituacaoParcela.EM_ABERTO.name());
        q.setParameter("tipoDam", DAM.Tipo.UNICO.name());
        q.setParameter("damAberto", DAM.Situacao.ABERTO.name());
        q.setParameter("exercicio", exercicio != null ? exercicio : sistemaFacade.getExercicioCorrente().getAno());
        q.setParameter("inscricaoInicial", inscricaoInicial);
        q.setParameter("inscricaoFinal", inscricaoFinal);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));

        List<Object[]> lista = q.getResultList();
        List<ImpressaoCarneIPTU> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            ImpressaoCarneIPTU carne = new ImpressaoCarneIPTU();
            carne.setIdCalculo(((BigDecimal) obj[0]).longValue());
            carne.setIdParcela(((BigDecimal) obj[1]).longValue());
            carne.setInscricaoCadastral(((String) obj[2]));
            carne.setNumeroDAM(obj[3] != null ? ((String) obj[3]) : null);
            carne.setValor(((BigDecimal) obj[4]));
            carne.setCodigoDeBarras(obj[5] != null ? ((String) obj[5]) : null);
            carne.setVencimento((Date) obj[6]);
            carne.setFracaoIdeal(((BigDecimal) obj[7]));
            carne.setAreaConstruida(((BigDecimal) obj[8]));
            carne.setAreaExcedente(((BigDecimal) obj[9]));
            carne.setVlrVenalTerreno(((BigDecimal) obj[10]));
            carne.setVlrVenalEdificacao(((BigDecimal) obj[11]));
            carne.setVlrVenalExcedente(((BigDecimal) obj[12]));
            carne.setAliquota(((BigDecimal) obj[13]));
            carne.setSequenciaParcela(((String) obj[14]));
            carne.setBarras(obj[15] != null ? ((String) obj[15]) : null);//
            carne.setValorComDesconto(((BigDecimal) obj[16]));
            carne.setImposto(((BigDecimal) obj[17]));
            carne.setTaxa(((BigDecimal) obj[18]));
            carne.setDesconto(((BigDecimal) obj[19]));
            carne.setTotalTaxa(((BigDecimal) obj[20]));
            carne.setTotalImposto(((BigDecimal) obj[21]));
            carne.setValorCalculo(((BigDecimal) obj[22]));
            carne.setAno(((BigDecimal) obj[23]).longValue());
            carne.setContribuinte(((String) obj[24]));
            carne.setCpfCnpj(formatarCpfCnpj((String) obj[25]));

            carne.setQualidade(obj[26] != null ? (String) obj[26] : "");
            carne.setUtilizacao(obj[27] != null ? (String) obj[27] : "");
            carne.setTipoConstrucao(obj[28] != null ? (String) obj[28] : "");
            carne.setColetaLixo(obj[29] != null ? (String) obj[29] : "");
            carne.setIluminacaoPublica(obj[30] != null ? (String) obj[30] : "");
            carne.setTopografia(obj[31] != null ? (String) obj[31] : "");
            carne.setPedologia(obj[32] != null ? (String) obj[32] : "");
            carne.setSituacaoLote(obj[33] != null ? (String) obj[33] : "");
            carne.setPatrimonio(obj[34] != null ? (String) obj[34] : "");
            carne.setSituacaoDam(obj[35] != null ? (String) obj[35] : "");
            carne.setTipoDam(obj[36] != null ? (String) obj[36] : "");
            carne.setAcrescimos(obj[37] != null ? (BigDecimal) obj[37] : BigDecimal.ZERO);
            carne.setQrCodePix(obj[38] != null ? (String) obj[38] : null);

            retorno.add(carne);
        }
        return retorno;
    }

    private String formatarCpfCnpj(String cpfCnpj) {
        if (!Strings.isNullOrEmpty(cpfCnpj)) {
            if (cpfCnpj.trim().length() == 11) {
                return Util.formatarCpf(cpfCnpj);
            } else if (cpfCnpj.trim().length() == 14) {
                return Util.formatarCnpj(cpfCnpj);
            }
        }
        return "";
    }

    public void verificarEGerarDamCarnesIptu(List<ImpressaoCarneIPTU> carnes, HistoricoImpressaoDAM.TipoImpressao tipoImpressao) {
        for (ImpressaoCarneIPTU carne : carnes) {
            if (new Date().after(carne.getVencimento()) && !carne.getSequenciaParcela().equals("Única")) {
                if (carne.getNumeroDAM() == null || DAM.Situacao.CANCELADO.name().equals(carne.getSituacaoDam()) || DAM.Tipo.COMPOSTO.name().equals(carne.getTipoDam())) {
                    carne.setCodigoDeBarras("VENCIDO");
                }
            } else {
                DAM dam = damFacade.buscarDAMPorNumeroDAM(carne.getNumeroDAM());
                if (dam != null) {
                    pixFacade.gerarQrCodePIX(Lists.newArrayList(dam));
                    damFacade.salvarHistoricoImpressao(dam, sistemaFacade.getUsuarioCorrente(), tipoImpressao);
                }
            }
        }
    }

    public ResultadoParcela buscarResultadoParcelaDaParcela(Long idParcela) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, idParcela);
        consultaParcela.executaConsulta();
        return consultaParcela.getResultados().get(0);
    }

    public ExportacaoIPTUFacade getExportacaoIPTUFacade() {
        return exportacaoIPTUFacade;
    }

    public ByteArrayOutputStream gerarImpressaoIPTUPortal(List<ImpressaoCarneIPTU> iptus, Integer ano, String inscricaoCadastral) throws IOException {
        ServiceDAM serviceDAM = (ServiceDAM) Util.getSpringBeanPeloNome("serviceDAM");
        RelatorioDTO dto = new RelatorioDTO();
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        inserirQrCodeCarne(iptus);

        dto.setNomeRelatorio("Carnê de IPTU");
        dto.adicionarParametro("MODULO", "Tributário");
        dto.adicionarParametro("EXERCICIO", ano != null ? ano : sistemaFacade.getExercicioCorrente().getAno());
        dto.adicionarParametro("CADASTRO_INICIAL", inscricaoCadastral);
        dto.adicionarParametro("CADASTRO_FINAL", inscricaoCadastral);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("TIPO_PERFIL", TipoPerfil.getDescricaoHomologacao());
        dto.adicionarParametro("HOMOLOGACAO", serviceDAM.isAmbienteHomologacao());
        dto.adicionarParametro("MSG_PIX", "Pagamento Via QrCode PIX");
        dto.setApi("tributario/carne-iptu/");
        dto.setLoginUsuario(HistoricoImpressaoDAM.TipoImpressao.PORTAL.getDescricao());
        dto.setUrlWebpublico(configuracao.getUrlWebpublico());

        byte[] bytes = ReportService.getInstance().gerarRelatorioSincrono(dto);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
        baos.write(bytes);
        return baos;
    }

    private void inserirQrCodeCarne(List<ImpressaoCarneIPTU> carnes) {
        for (ImpressaoCarneIPTU carne : carnes) {
            DAM dam = damFacade.buscarDAMPorNumeroDAM(carne.getNumeroDAM());
            if (dam != null) {
                pixFacade.gerarQrCodePIX(Lists.newArrayList(dam));
            }
        }
    }
}
