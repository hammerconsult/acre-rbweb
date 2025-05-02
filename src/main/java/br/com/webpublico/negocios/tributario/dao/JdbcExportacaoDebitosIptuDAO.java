package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteExportacaoDebitosIPTU;
import br.com.webpublico.entidadesauxiliares.VODam;
import br.com.webpublico.entidadesauxiliares.VOParcelaExportacaoDebitosIPTU;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.negocios.DAMFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.ParcelaIPTUExtractor;
import br.com.webpublico.negocios.tributario.setter.ExportacaoDebitoLinhaSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoExportacaoDebitosIPTU;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Repository(value = "exportacaoDebitosIptuDAO")
public class JdbcExportacaoDebitosIptuDAO extends AbstractJdbc {
    private static final Logger logger = LoggerFactory.getLogger(JdbcExportacaoDebitosIptuDAO.class);

    @Autowired
    private SingletonGeradorId geradorDeIds;


    private final SingletonGeradorCodigoExportacaoDebitosIPTU singletonGeradorCodigoExportacaoDebitosIPTU;
    private final DAMFacade damFacade;

    public JdbcExportacaoDebitosIptuDAO() {
        this.singletonGeradorCodigoExportacaoDebitosIPTU = (SingletonGeradorCodigoExportacaoDebitosIPTU) Util.getFacadeViaLookup("java:module/SingletonGeradorCodigoExportacaoDebitosIPTU");
        this.damFacade = (DAMFacade) Util.getFacadeViaLookup("java:module/DAMFacade");
    }

    public List<VOParcelaExportacaoDebitosIPTU> buscarParcelasIPTU(AssistenteExportacaoDebitosIPTU assistente) {
        Long idDividaIPTU = buscarIdDividaIPTU();

        String sql = " select sum(count(pvd.id)) over () as total, c.cadastro_id as idCadastro, pvd.id as idParcela, div.descricao as divida, ex.ano as exercicio, " +
            "                 pvd.valor as valorparcela, op.promocional as cotaunica, pvd.sequenciaparcela, cp.pessoa_id as idPessoa, " +
            "                 coalesce(pvd.vencimento, sysdate) as vencimento, pvd.valordivida_id as valordivida, " +
            "                 max(case when dam.id is not null then 1 else 0 end) as hasdam, max(dam.id) as id_dam, pvd.id as idparcela, " +
            "                 max(dam.codigobarras) as cod_barras, max(dam.vencimento) as vencimento, " +
            "                max((select dam_cota_unica.codigobarras " +
            "                 from parcelavalordivida pvd_cota_unica " +
            "                 inner join itemdam idam_cota_unica on pvd_cota_unica.id = idam_cota_unica.parcela_id " +
            "                 inner join dam dam_cota_unica on idam_cota_unica.dam_id = dam_cota_unica.id and dam_cota_unica.situacao = :situacaoDam and dam_cota_unica.tipo = :tipoDam " +
            "                 inner join opcaopagamento op_cota_unica on pvd_cota_unica.opcaopagamento_id = op_cota_unica.id and coalesce(op_cota_unica.promocional, 0) = :promocional " +
            "                 where pvd_cota_unica.valordivida_id = pvd.valordivida_id order by dam_cota_unica.id desc fetch first 1 rows only)) as cod_barras_cota_unica, " +
            "                max((select ci.inscricaocadastral || ';' || to_char(coalesce(eb.valor, 0)) " +
            "                 from cadastroimobiliario ci " +
            "                 left join eventocalculobci eb on ci.id = eb.cadastroimobiliario_id " +
            "                 left join eventoconfiguradobci ec on eb.eventocalculo_id = ec.id and ec.identificacao = :identificacao " +
            "                 where ci.id = c.cadastro_id order by ci.id desc fetch first 1 rows only)) as dadosCadastro, " +
            "                max((select coalesce(pf.cpf, pj.cnpj, '') || ';' || case when pf.id is not null then '1' else '2' end " +
            "                 from pessoa pes " +
            "                 left join pessoafisica pf on pes.id = pf.id " +
            "                 left join pessoajuridica pj on pes.id = pj.id " +
            "                 where pes.id = cp.pessoa_id order by pes.id desc fetch first 1 rows only)) as dadosPessoa" +
            " from parcelavalordivida pvd " +
            " inner join valordivida vd on pvd.valordivida_id = vd.id " +
            " inner join calculo c on vd.calculo_id = c.id " +
            " inner join divida div on vd.divida_id = div.id " +
            " inner join exercicio ex on vd.exercicio_id = ex.id " +
            " inner join opcaopagamento op on pvd.opcaopagamento_id = op.id " +
            " inner join situacaoparcelavalordivida spvd on pvd.situacaoatual_id = spvd.id " +
            " inner join calculopessoa cp on c.id = cp.calculo_id " +
            " left join itemdam idam on pvd.id = idam.parcela_id " +
            " left join dam on idam.dam_id = dam.id and dam.situacao = :situacaoDam and dam.tipo = :tipoDam " +
            " where div.id = :idDividaIPTU " +
            " and cp.id = (select cp2.id from calculopessoa cp2 where cp2.calculo_id = c.id order by cp2.id desc fetch first 1 rows only) " +
            " and spvd.situacaoparcela = :situacao " +
            " and exists (select ci.id from CadastroImobiliario ci " +
            "                        where ci.id = c.cadastro_id " +
            "                        and ci.inscricaoCadastral between :inscricaoInicial and :inscricaoFinal " +
            "                        and coalesce(ci.ativo,0) = 1) " +
            " and (dam.id is null or dam.id = (select idam2.dam_id from itemdam idam2 where idam2.parcela_id = pvd.id order by id desc fetch first 1 rows only)) " +
            (assistente.getSelecionado().getExercicio() != null ? " and ex.id = :idExercicio " : "") +
            (assistente.getSelecionado().getVencimentoIncial() != null ? " and trunc(pvd.vencimento) >= to_date(:vencimentoInicial, 'dd/MM/yyyy') " : "") +
            (assistente.getSelecionado().getVencimentoFinal() != null ? " and trunc(pvd.vencimento) <= to_date(:vencimentoFinal, 'dd/MM/yyyy') " : "") +
            " group by c.cadastro_id, pvd.id, div.descricao, ex.ano, pvd.valor, op.promocional, pvd.sequenciaparcela, cp.pessoa_id, " +
            "          coalesce(pvd.vencimento, sysdate), pvd.valordivida_id, pvd.id ";

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate());
        final MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("idDividaIPTU", idDividaIPTU);
        parameters.addValue("situacao", SituacaoParcela.EM_ABERTO.name());
        parameters.addValue("inscricaoInicial", assistente.getSelecionado().getInscricaoInicial());
        parameters.addValue("inscricaoFinal", assistente.getSelecionado().getInscricaoFinal());
        parameters.addValue("situacaoDam", DAM.Situacao.ABERTO.name());
        parameters.addValue("tipoDam", DAM.Tipo.UNICO.name());
        parameters.addValue("promocional", 1);
        parameters.addValue("identificacao", EventoConfiguradoBCI.Identificacao.VALOR_VENAL.name());

        if (assistente.getSelecionado().getExercicio() != null) {
            parameters.addValue("idExercicio", assistente.getSelecionado().getExercicio().getId());
        }
        if (assistente.getSelecionado().getVencimentoIncial() != null) {
            parameters.addValue("vencimentoInicial", DataUtil.getDataFormatada(assistente.getSelecionado().getVencimentoIncial()));
        }
        if (assistente.getSelecionado().getVencimentoFinal() != null) {
            parameters.addValue("vencimentoFinal", DataUtil.getDataFormatada(assistente.getSelecionado().getVencimentoFinal()));
        }

        List<VOParcelaExportacaoDebitosIPTU> parcelasIPTU = Lists.newArrayList();
        parcelasIPTU.addAll(namedParameterJdbcTemplate.query(sql, parameters, new ParcelaIPTUExtractor(assistente)));
        parcelasIPTU.addAll(gerarDansEmLote(assistente));

        return parcelasIPTU;
    }

    private Long buscarIdDividaIPTU() {
        return getJdbcTemplate().queryForObject(" select ct.dividaiptu_id from configuracaotributario ct order by ct.vigencia desc fetch first 1 rows only ", Long.class);
    }

    private List<VOParcelaExportacaoDebitosIPTU> gerarDansEmLote(AssistenteExportacaoDebitosIPTU assistente) {
        List<VOParcelaExportacaoDebitosIPTU> parcelasIPTU = Lists.newArrayList();


        for (Map.Entry<Long, List<VOParcelaExportacaoDebitosIPTU>> valorDivida : assistente.getParcelasPorValorDivida().entrySet()) {
            List<VOParcelaExportacaoDebitosIPTU> parcelas = valorDivida.getValue();

            VODam damCotaUnica = adicionarDAMCotaUnica(assistente, parcelasIPTU, parcelas);

            for (VOParcelaExportacaoDebitosIPTU parcela : parcelas) {
                DAM dam = damFacade.gerarDAMUnicoViaApi(null, parcela.getParcela());

                VODam voDam = criarVODAM((damCotaUnica != null ? damCotaUnica.getCodigoBarras() : dam.getCodigoBarras()),
                        dam.getCodigoBarras(), parcela.getParcela().getVencimento());

                parcela.setDam(voDam);
                parcelasIPTU.add(parcela);

                assistente.conta();
            }
        }

        popularListas(assistente);

        return parcelasIPTU;
    }

    private void adicionarInformacoesDAM(VOParcelaExportacaoDebitosIPTU parcela, VODam voDam, List<VOParcelaExportacaoDebitosIPTU> parcelasIPTU, AssistenteExportacaoDebitosIPTU assistente, Long numeroDAM, String codigo) {
        parcela.setDam(voDam);
        parcelasIPTU.add(parcela);

        DAM dam = criarDAM(assistente.getExercicio(), parcela.getParcela(), numeroDAM, codigo);
        criarItemDam(dam, parcela.getParcela());
        assistente.getImpressoesDAM().add(criarHistoricoImpressaoDAM(dam, parcela.getParcela().getIdParcela(), assistente.getUsuarioSistema()));

        assistente.getDans().add(dam);
    }

    private void popularListas(AssistenteExportacaoDebitosIPTU assistente) {
        for (DAM dam : assistente.getDans()) {
            assistente.getItensDAM().addAll(dam.getItens());
            for (ItemDAM itemDAM : dam.getItens()) {
                assistente.getTributosDAM().addAll(itemDAM.getTributoDAMs());
            }
        }
    }

    private VODam adicionarDAMCotaUnica(AssistenteExportacaoDebitosIPTU assistente, List<VOParcelaExportacaoDebitosIPTU> parcelasIPTU, List<VOParcelaExportacaoDebitosIPTU> parcelas) {
        VOParcelaExportacaoDebitosIPTU parcelaCotaUnica = recuperarCotaUnica(parcelas);
        VODam damCotaUnica = null;
        if (parcelaCotaUnica != null) {
            DAM dam = damFacade.gerarDAMUnicoViaApi(null, parcelaCotaUnica.getParcela());
            damCotaUnica = criarVODAM(dam.getCodigoBarras(), dam.getCodigoBarras(), parcelaCotaUnica.getParcela().getVencimento());
            assistente.conta();
        }
        return damCotaUnica;
    }

    private VOParcelaExportacaoDebitosIPTU recuperarCotaUnica(List<VOParcelaExportacaoDebitosIPTU> parcelas) {
        Iterator<VOParcelaExportacaoDebitosIPTU> iterator = parcelas.iterator();

        while (iterator.hasNext()) {
            VOParcelaExportacaoDebitosIPTU parcelaIPTU = iterator.next();
            if (parcelaIPTU.getParcela().getCotaUnica()) {
                iterator.remove();
                return parcelaIPTU;
            }
        }
        return null;
    }

    private VODam criarVODAM(String codigoBarrasCotaUnica, String codigo, Date vencimento) {
        VODam dam = new VODam();
        dam.setCodigoBarras(codigo);
        dam.setVencimento(vencimento);
        dam.setCodigoBarrasCotaUnica(codigoBarrasCotaUnica);
        return dam;
    }

    private DAM criarDAM(Exercicio exercicio, ResultadoParcela parcela, Long numeroDAM, String codigo) {
        DAM dam = new DAM();
        dam.setCodigoBarras(codigo);
        dam.setNumeroDAM(numeroDAM + "/" + exercicio.getAno());
        dam.setCodigoBarras(codigo);
        dam.setVencimento(parcela.getVencimento());
        dam.setEmissao(new Date());
        dam.setValorOriginal(parcela.getValorOriginal());
        dam.setJuros(parcela.getValorJuros());
        dam.setMulta(parcela.getValorMulta());
        dam.setCorrecaoMonetaria(parcela.getValorCorrecao());
        dam.setDesconto(parcela.getValorDesconto());
        dam.setSituacao(DAM.Situacao.ABERTO);
        dam.setTipo(DAM.Tipo.UNICO);
        dam.setExercicio(exercicio);
        dam.setNumero(numeroDAM);
        dam.setHonorarios(parcela.getValorHonorarios());
        return dam;
    }

    private void criarItemDam(DAM dam, ResultadoParcela resultadoParcela) {
        ItemDAM itemDAM = new ItemDAM();
        itemDAM.setDAM(dam);
        itemDAM.setDesconto(resultadoParcela.getValorDesconto());
        itemDAM.setCorrecaoMonetaria(resultadoParcela.getValorCorrecao());
        itemDAM.setJuros(resultadoParcela.getValorJuros());
        itemDAM.setMulta(resultadoParcela.getValorMulta());
        itemDAM.setOutrosAcrescimos(BigDecimal.ZERO);
        itemDAM.setValorOriginalDevido(resultadoParcela.getValorOriginal());
        itemDAM.setParcela(new ParcelaValorDivida(resultadoParcela.getIdParcela()));
        itemDAM.setHonorarios(resultadoParcela.getValorHonorarios());

        adicionarTributosDans(resultadoParcela, itemDAM);

        dam.getItens().add(itemDAM);
    }

    private HistoricoImpressaoDAM criarHistoricoImpressaoDAM(DAM dam, Long idParcela, UsuarioSistema usuario) {
        HistoricoImpressaoDAM historico = new HistoricoImpressaoDAM();
        historico.setDam(dam);
        historico.setTipoImpressao(HistoricoImpressaoDAM.TipoImpressao.SISTEMA);
        historico.setUsuarioSistema(usuario);
        historico.setParcela(new ParcelaValorDivida(idParcela));
        return historico;
    }

    private void adicionarTributosDans(ResultadoParcela parcela, ItemDAM itemDAM) {
        itemDAM.getTributoDAMs().add(new TributoDAM(new Tributo(6194076L), parcela.getValorImposto(), parcela.getValorDesconto(), itemDAM));
        itemDAM.getTributoDAMs().add(new TributoDAM(new Tributo(6208252L), parcela.getValorTaxa(), BigDecimal.ZERO, itemDAM));
    }

    public ConfiguracaoDAM buscarConfiguracaoDAMIptu() {
        String sql = " select config.* from configuracaotributario ct " +
            " inner join divida dv on ct.dividaiptu_id = dv.id " +
            " inner join configuracaodam config on dv.configuracaodam_id = config.id " +
            " order by ct.vigencia desc fetch first 1 rows only ";

        try {
            List<ConfiguracaoDAM> configuracoes = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(ConfiguracaoDAM.class));
            return (configuracoes != null && !configuracoes.isEmpty()) ? configuracoes.get(0) : null;
        } catch (Exception e) {
            logger.error("Erro ao buscar pessoa (jdbcArquivoIPTUDAO). ", e);
        }
        return null;
    }

    public void inserirLinhasDoArquivo(AssistenteExportacaoDebitosIPTU assistente) {
        if (assistente.getLinhas() != null && !assistente.getLinhas().isEmpty()) {
            String insert = " insert into exportacaodebitosiptulinha (id, indice, exportacaodebitosiptu_id, linha) " +
                " values (?, ?, ?, ?) ";

            getJdbcTemplate().batchUpdate(insert, new ExportacaoDebitoLinhaSetter(geradorDeIds, assistente));
        }
    }

    public void excluirLinhasExportacaoIPTU(ExportacaoDebitosIPTU exportacaoDebitosIPTU) {
        String sql = " delete from exportacaodebitosiptulinha " +
            " where exportacaodebitosiptu_id = ? ";

        getJdbcTemplate().update(sql, exportacaoDebitosIPTU.getId());
    }

    public void adicionarNumeroRemessa(AssistenteExportacaoDebitosIPTU assistente) {
        if (assistente != null && assistente.getSelecionado() != null && assistente.getSelecionado().getId() != null) {
            Long numeroRemessa = recuperarNumeroRemessa(assistente);
            if (numeroRemessa != null) {
                String update = " update exportacaodebitosiptu set numeroremessa = ? where id = ? ";
                assistente.getSelecionado().setNumeroRemessa(numeroRemessa);
                getJdbcTemplate().update(update, numeroRemessa, assistente.getSelecionado().getId());
            }
        }
    }

    private Long recuperarNumeroRemessa(AssistenteExportacaoDebitosIPTU assistente) {
        if (assistente.isPerfilDev()) {
            return singletonGeradorCodigoExportacaoDebitosIPTU.getProximoCodigo(SistemaFacade.PerfilApp.DEV, assistente.getExercicio());
        }
        return singletonGeradorCodigoExportacaoDebitosIPTU.getProximoCodigo(SistemaFacade.PerfilApp.PROD, assistente.getExercicio());
    }

}

