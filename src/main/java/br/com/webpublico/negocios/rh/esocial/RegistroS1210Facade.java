package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocial;
import br.com.webpublico.entidades.rh.esocial.VinculoFPEventoEsocial;
import br.com.webpublico.enums.TipoClasseCredor;
import br.com.webpublico.enums.rh.esocial.TipoRegimePrevidenciario;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class RegistroS1210Facade extends AbstractFacade<RegistroEventoEsocial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;

    @EJB
    private FichaRPAFacade fichaRPAFacade;

    public RegistroS1210Facade() {
        super(RegistroEventoEsocial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoEmpregadorESocialFacade getConfiguracaoEmpregadorESocialFacade() {
        return configuracaoEmpregadorESocialFacade;
    }

    public FolhaDePagamentoFacade getFolhaDePagamentoFacade() {
        return folhaDePagamentoFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public void setPessoaFisicaFacade(PessoaFisicaFacade pessoaFisicaFacade) {
        this.pessoaFisicaFacade = pessoaFisicaFacade;
    }

    @Override
    public RegistroEventoEsocial recuperar(Object id) {
        RegistroEventoEsocial registro = super.recuperar(id);
        Hibernate.initialize(registro.getItemVinculoFP());
        return registro;
    }

    public List<VinculoFPEventoEsocial> buscarPrestadoresDeServico(Date dataOperacao, List<Long> idsUnidade, Integer mes, Integer ano, RegistroEventoEsocial selecionado) {

        List<Long> idsClasseCredor = Lists.newArrayList();
        for (ClasseCredor classes : buscarClassesCredoresDaConfiguracao()) {
            idsClasseCredor.add(classes.getId());
        }

        String sql = " select pessoa_id, pessoa, sum(valor), " +
            "                 (select pag.datapagamento from empenho emp " +
            "                  inner join liquidacao liq on emp.id = liq.empenho_id " +
            "                  inner join pagamento pag on liq.id = pag.liquidacao_id " +
            "                  inner join pessoa p on emp.fornecedor_id = p.id " +
            "                  where p.id = dados.pessoa_id " +
            "                  and extract(year from pag.datapagamento) = :ano " +
            "                  and extract(month from pag.datapagamento) = :mes " +
            "                  and rownum = 1) as dataPagto, " +
            "                 (select pag.id from empenho emp + " +
            "                  inner join liquidacao liq on emp.id = liq.empenho_id + " +
            "                  inner join pagamento pag on liq.id = pag.liquidacao_id + " +
            "                  inner join pessoa p on emp.fornecedor_id = p.id + " +
            "                  where p.id = dados.pessoa_id + " +
            "                  and extract(year from pag.datapagamento) = :ano + " +
            "                  and extract(month from pag.datapagamento) = :mes + " +
            "                  and rownum = 1) as idPgto " +
            " from (select pessoa_id as pessoa_id, pessoa as pessoa, sum(valor) as valor " +
            "       from (select p.id as pessoa_id, pf.cpf || ' - ' || pf.nome as pessoa, " +
            "                    coalesce(sum(pag.valor), 0) as valor " +
            "             from empenho emp " +
            "             inner join liquidacao liq on emp.id = liq.empenho_id " +
            "             inner join pagamento pag on liq.id = pag.liquidacao_id " +
            "             inner join pessoa p on emp.fornecedor_id = p.id " +
            "             inner join pessoafisica pf on p.id = pf.id " +
            "             inner join classecredor cc on emp.classecredor_id = cc.id " +
            "             inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = emp.unidadeorganizacional_id " +
            "             inner join hierarquiaorgcorre cor on vworc.id = cor.hierarquiaorgorc_id " +
            "             inner join vwhierarquiaadministrativa vwadm on vwadm.id = cor.hierarquiaorgadm_id " +
            "             where to_date(:dataOperacao, 'dd/mm/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "             and to_date(:dataOperacao, 'dd/mm/yyyy') between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "             and to_date(:dataOperacao, 'dd/mm/yyyy') between cor.datainicio and coalesce(cor.datafim, to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "             and extract(year from pag.datapagamento) = :ano " +
            "             and extract(month from pag.datapagamento) = :mes " +
            "             and vwadm.subordinada_id in :idsUnidade ";

        if (!idsClasseCredor.isEmpty()) {
            sql += " and cc.id in :idsClasseCredor ";
        }

        sql += " group by p.id, pf.cpf || ' - ' || pf.nome " +
            "             union all " +
            "             select p.id as pessoa_id, pf.cpf || ' - ' || pf.nome as pessoa, coalesce(sum(est.valor) * - 1, 0) as valor " +
            "             from empenho emp " +
            "             inner join liquidacao liq on emp.id = liq.empenho_id " +
            "             inner join pagamento pag on liq.id = pag.liquidacao_id " +
            "             inner join pagamentoestorno est on pag.id = est.pagamento_id " +
            "             inner join pessoa p on emp.fornecedor_id = p.id " +
            "             inner join pessoafisica pf on p.id = pf.id " +
            "             inner join classecredor cc on emp.classecredor_id = cc.id " +
            "             inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = emp.unidadeorganizacional_id " +
            "             inner join hierarquiaorgcorre cor on vworc.id = cor.hierarquiaorgorc_id " +
            "             inner join vwhierarquiaadministrativa vwadm on vwadm.id = cor.hierarquiaorgadm_id " +
            "             where to_date(:dataOperacao, 'dd/mm/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "             and to_date(:dataOperacao, 'dd/mm/yyyy') between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "             and to_date(:dataOperacao, 'dd/mm/yyyy') between cor.datainicio and coalesce(cor.datafim, to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "             and extract(year from est.dataestorno) = :ano " +
            "             and extract(month from est.dataestorno) = :mes " +
            "             and vwadm.subordinada_id in :idsUnidade ";

        if (!idsClasseCredor.isEmpty()) {
            sql += " and cc.id in :idsClasseCredor ";
        }

        sql += " group by p.id, pf.cpf || ' - ' || pf.nome) reg " +
            "       group by pessoa_id, pessoa) dados " +
            " inner join pessoa p on dados.pessoa_id = p.id " +
            " inner join pessoafisica pf on p.id = pf.id " +
            " where valor <> 0 " +
            " group by pessoa_id, pessoa ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("vwSubordinada", idsUnidade);
        q.setParameter("prestador", TipoClasseCredor.PRESTADOR_SERVICO.name());
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        if (!idsClasseCredor.isEmpty()) {
            q.setParameter("idsClasseCredor", idsClasseCredor);
        }

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            List<VinculoFPEventoEsocial> prestadores = Lists.newArrayList();
            for (Object[] obj : (List<Object[]>) resultList) {
                VinculoFPEventoEsocial prestadorEvento = new VinculoFPEventoEsocial();
                prestadorEvento.setIdVinculo(Long.parseLong(obj[0].toString()));
                prestadorEvento.setNome((String) obj[1]);
                prestadorEvento.setValorPrestador((BigDecimal) obj[2]);
                prestadorEvento.setDataPagamentoPrestador((Date) obj[3]);
                prestadorEvento.setIdVinculo(Long.parseLong(obj[4].toString()));
                prestadorEvento.setRegistroEventoEsocial(selecionado);

                prestadores.add(prestadorEvento);
            }
            return prestadores;
        }
        return null;
    }

    public List<ClasseCredor> buscarClassesCredoresDaConfiguracao() {
        return configuracaoRHFacade.buscarClassesCredoresDaConfiguracao();
    }

    public List<VinculoFPEventoEsocial> getVinculoFPEventoEsocials1210(RegistroEventoEsocial selecionado,
                                                                       Entidade entidade,
                                                                       VinculoFP vinculoFP, boolean apenasNaoEnviados,
                                                                       TipoRegimePrevidenciario tipoRegimePrevidenciario,
                                                                       PessoaFisica pessoaVinculo, HierarquiaOrganizacional hierarquia) {
        try {
            List<Long> idsUnidade = hierarquia != null ? Lists.newArrayList(hierarquia.getSubordinada().getId()) : montarIdsUnidade(selecionado, entidade);

            ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial = getConfiguracaoEmpregadorESocialFacade().recuperarPorEntidade(entidade);
            List<VinculoFPEventoEsocial> vinculoFPS = Lists.newArrayList();
            List<VinculoFPEventoEsocial> prestadoresRPA = Lists.newArrayList();
            Map<String, VinculoFPEventoEsocial> mapVinculosEsocial = new HashMap<>();

            vinculoFPS = folhaDePagamentoFacade.buscarVinculosPorTipoFolhaMesEAnoAndFolhaEfetivadaAndUnidadesEventoS1210(selecionado, idsUnidade, vinculoFP, TipoArquivoESocial.S1210, pessoaVinculo, configuracaoEmpregadorESocial, tipoRegimePrevidenciario);

            if (tipoRegimePrevidenciario == null || TipoRegimePrevidenciario.RGPS.equals(tipoRegimePrevidenciario)) {
                Long empregadorId = configuracaoEmpregadorESocial != null ? configuracaoEmpregadorESocial.getId() : null;
                prestadoresRPA = fichaRPAFacade.buscarPrestadorServicoPorMesAndAnoAndUnidades(selecionado, pessoaVinculo, idsUnidade, TipoArquivoESocial.S1210, empregadorId);
            }

            if (vinculoFPS != null && !vinculoFPS.isEmpty()) {
                vinculoFPS.forEach(vinculo -> {
                    mapVinculosEsocial.put(vinculo.getCpf(), vinculo);
                });
            }

            if (prestadoresRPA != null && !prestadoresRPA.isEmpty()) {
                prestadoresRPA.forEach(prestador -> {
                    if (mapVinculosEsocial.containsKey(prestador.getCpf())) {
                        Long idVinculo = mapVinculosEsocial.get(prestador.getCpf()).getIdVinculo();
                        prestador.setIdVinculo(idVinculo);
                    }
                    mapVinculosEsocial.put(prestador.getCpf(), prestador);
                });
            }

            for (VinculoFPEventoEsocial vinculoEsocial : mapVinculosEsocial.values()) {
                List<Long> idFichas = Lists.newArrayList();
                idFichas = folhaDePagamentoFacade.buscarIdsFichasS1210(selecionado, vinculoEsocial.getIdPessoa(), idsUnidade);
                if (tipoRegimePrevidenciario == null || TipoRegimePrevidenciario.RGPS.equals(tipoRegimePrevidenciario)) {
                    idFichas.addAll(fichaRPAFacade.buscarIdsFichas(selecionado, vinculoEsocial.getIdPessoa()));
                }
                vinculoEsocial.setIdsFichas(idFichas);
            }

            if (!mapVinculosEsocial.isEmpty()) {
                if (apenasNaoEnviados) {
                    return buscarVinculosQueNaoForamEnviados(selecionado, configuracaoEmpregadorESocial, mapVinculosEsocial);
                }
                return Lists.newArrayList(mapVinculosEsocial.values());
            }
            FacesUtil.addOperacaoRealizada("Nenhum Servidor encontrado para os filtros informados.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        return null;
    }

    private List<Long> montarIdsUnidade(RegistroEventoEsocial selecionado, Entidade entidade) {
        ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial = getConfiguracaoEmpregadorESocialFacade().recuperarPorEntidade(entidade);
        validarConfigEmpregadorESocial(configuracaoEmpregadorESocial, selecionado, entidade);
        configuracaoEmpregadorESocial = getConfiguracaoEmpregadorESocialFacade().recuperar(configuracaoEmpregadorESocial.getId());
        return getContratoFPFacade().montarIdOrgaoEmpregador(configuracaoEmpregadorESocial);
    }

    private void validarConfigEmpregadorESocial(ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial, RegistroEventoEsocial selecionado, Entidade entidade) {
        ValidacaoException ve = new ValidacaoException();
        if (ve.getMensagens().isEmpty()) {
            if (configuracaoEmpregadorESocial == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado configuração para a Entidade " + entidade);
            }
        }
        ve.lancarException();
    }

    @NotNull
    private List<VinculoFPEventoEsocial> buscarVinculosQueNaoForamEnviados(@NotNull RegistroEventoEsocial registroEventoEsocial,
                                                                           ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial,
                                                                           Map<String, VinculoFPEventoEsocial> mapVinculosEsocial) {
        List<VinculoFPEventoEsocial> vinculoPrimeiroEnvio = Lists.newArrayList();
        List<RegistroESocial> registrosEnviados = folhaDePagamentoFacade.
            recuperarRegistroEsocialAnoMesEmpregador(TipoArquivoESocial.S1210,
                registroEventoEsocial.getMes().getNumeroMes(), registroEventoEsocial.getExercicio().getAno(),
                configuracaoEmpregadorESocial);

        if (registrosEnviados == null || registrosEnviados.isEmpty()) {
            return Lists.newArrayList(mapVinculosEsocial.values());
        }
        for (VinculoFPEventoEsocial vinculoEsocial : mapVinculosEsocial.values()) {
            boolean enviado = false;
            for (RegistroESocial registrosEnviado : registrosEnviados) {
                if (vinculoEsocial.getIdsFichas().get(0).toString().contains(registrosEnviado.getIdentificadorWP())) {
                    enviado = true;
                    break;
                }
            }
            if (!enviado) {
                vinculoPrimeiroEnvio.add(vinculoEsocial);
            }
        }
        return vinculoPrimeiroEnvio;
    }

}
