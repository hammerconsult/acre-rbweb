package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.ConfiguracaoTransporteSaldoSubContaDestino;
import br.com.webpublico.entidadesauxiliares.AssistenteAberturaFechamentoExercicio;
import br.com.webpublico.entidadesauxiliares.BarraProgressoItens;
import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.PatrimonioLiquido;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.execucao.ConfiguracaoTransporteSaldoSubContaFacade;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/09/14
 * Time: 15:46
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class AberturaFechamentoExercicioFacade extends SuperFacadeContabil<AberturaFechamentoExercicio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private EmpenhoEstornoFacade empenhoEstornoFacade;
    @EJB
    private ObrigacaoAPagarFacade obrigacaoAPagarFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private PlanoDeContasFacade planoDeContasFacade;
    @EJB
    private SaldoContaContabilFacade saldoContaContabilFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private ConfigEmpenhoRestoFacade configEmpenhoRestoFacade;
    @EJB
    private ConfigCancelamentoRestoFacade configCancelamentoRestoFacade;
    @EJB
    private ConfigAberturaFechamentoExercicioFacade configAberturaFechamentoExercicioFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private TipoContaAuxiliarFacade tipoContaAuxiliarFacade;
    @EJB
    private ConfiguracaoTransporteSaldoSubContaFacade configuracaoTransporteSaldoSubContaFacade;
    private AssistenteAberturaFechamentoExercicio assistenteAberturaFechamentoExercicio;

    public AberturaFechamentoExercicioFacade() {
        super(AberturaFechamentoExercicio.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    //GETTERS
    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public UnidadeGestoraFacade getUnidadeGestoraFacade() {
        return unidadeGestoraFacade;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public PlanoDeContasFacade getPlanoDeContasFacade() {
        return planoDeContasFacade;
    }
//METODOS RECUPERADORES

    @Override
    public Object recuperar(Class entidade, Object id) {
        return recuperar(id);
    }

    @Override
    public AberturaFechamentoExercicio recuperar(Object id) {
        AberturaFechamentoExercicio o = em.find(AberturaFechamentoExercicio.class, id);
        o.getPrescricaoEmpenhosProcessados().size();
        o.getPrescricaoEmpenhosNaoProcessados().size();
        o.getInscricaoEmpenhosProcessados().size();
        o.getInscricaoEmpenhosNaoProcessados().size();
        o.getReceitasARealizar().size();
        o.getReceitasReestimativas().size();
        o.getReceitasDeducaoPrevisaoInicial().size();
        o.getReceitasDeducaoReceitaRealizada().size();
        o.getReceitasRealizada().size();
        o.getReceitasExtras().size();
        o.getDotacoes().size();
        o.getCreditosAdicionaisSuplementares().size();
        o.getCreditosAdicionaisEspecial().size();
        o.getCreditosAdicionaisExtraordinario().size();
        o.getAnulacaoDotacao().size();
        o.getCretitosAdicionaisPorSuperavitFinanceira().size();
        o.getCretitosAdicionaisPorExcessoDeArrecadacao().size();
        o.getCretitosAdicionaisPorAnulacaoDeDotacao().size();
        o.getCretitosAdicionaisPorOperacaoDeCredito().size();
        o.getCretitosAdicionaisPorReservaDeContigencia().size();
        o.getCretitosAdicionaisPorAnulacaoDeCredito().size();
        o.getEmpenhosALiquidarInscritosRestoAPagarNaoProcessados().size();
        o.getEmpenhosLiquidadosInscritosRestoAPagarProcessados().size();
        o.getEmpenhosCreditoEmpenhadoPago().size();
        o.getPagoDosRestosAPagarProcessados().size();
        o.getPagoDosRestosAPagarNaoProcessados().size();
        o.getCanceladoDosRestosAPagarProcessados().size();
        o.getCanceladoDosRestosAPagarNaoProcessados().size();
        o.getDestinacaoDeRecurso().size();
        o.getResultadoDiminutivoExercicio().size();
        o.getResultadoAumentativoExercicio().size();
        o.getPlanoDeContas().size();
        o.getConfiguracoes().size();
        o.getOccs().size();
        o.getTransporteDeSaldo().size();
        o.getTransporteDeSaldoFinanceiro().size();
        o.getTransporteDeSaldoCreditoReceber().size();
        o.getTransporteDeSaldoDividaAtiva().size();
        o.getTransporteDeSaldoExtra().size();
        o.getTransporteDeSaldoDividaPublica().size();
        o.getTransporteDeSaldoDeContasAuxiliares().size();
        o.getInscricaoRestoPagarProcessados().size();
        o.getInscricaoRestoPagarNaoProcessados().size();
        o.getTransferenciaResultadoPublico().size();
        o.getTransferenciaResultadoPrivado().size();
        o.getTransferenciaAjustesPublico().size();
        o.getTransferenciaAjustesPrivado().size();
        o.getObrigacoesAPagar().size();
        return o;
    }

    public String getSqlPrescricaoRestoAPagar(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades, TipoRestosProcessado tipoRestosProcessado) {
        String sql = "";
        if (TipoRestosProcessado.PROCESSADOS.equals(tipoRestosProcessado)) {
            sql = "select ID, sum(valor) from ( " +
                "   SELECT E.ID, sum(coalesce(l.valor,0)) as valor  " +
                "   FROM EMPENHO E " +
                "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
                "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
                "   WHERE " +
                "   E.CATEGORIAORCAMENTARIA = 'RESTO' " +
                "   and e.tiporestosprocessados = :tipo " +
                "   AND E.EXERCICIO_ID = :EXE " +
                "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
                getUnidades(listaUnidades) +
                "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
                "   group by E.ID" +
                "    " +
                "union all " +
                " " +
                "   SELECT E.ID, sum(coalesce(le.valor,0)) * -1 as valor  " +
                "   FROM EMPENHO E " +
                "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
                "   inner join liquidacaoestorno le on le.liquidacao_id = l.id and trunc(le.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
                "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
                "   WHERE " +
                "   E.CATEGORIAORCAMENTARIA = 'RESTO' " +
                "   and e.tiporestosprocessados = :tipo " +
                "   AND E.EXERCICIO_ID = :EXE " +
                "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
                getUnidades(listaUnidades) +
                "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
                "   group by E.ID   " +
                "    " +
                "union all " +
                " " +
                "   SELECT E.ID, sum(coalesce(p.valor,0)) * -1 as valor  " +
                "   FROM EMPENHO E " +
                "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
                "   inner join pagamento p on p.liquidacao_id = l.id and trunc(p.datapagamento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
                "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
                "   WHERE " +
                "   E.CATEGORIAORCAMENTARIA = 'RESTO' " +
                "   and e.tiporestosprocessados = :tipo " +
                "   AND E.EXERCICIO_ID = :EXE " +
                "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
                getUnidades(listaUnidades) +
                "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
                "   group by E.ID" +
                "    " +
                "union all " +
                " " +
                "   SELECT E.ID, sum(coalesce(pe.valor,0)) as valor  " +
                "   FROM EMPENHO E " +
                "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
                "   inner join pagamento p on p.liquidacao_id = l.id and trunc(p.datapagamento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
                "   inner join pagamentoestorno pe on pe.pagamento_id = p.id and trunc(pe.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
                "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
                "   WHERE " +
                "   E.CATEGORIAORCAMENTARIA = 'RESTO' " +
                "   and e.tiporestosprocessados = :tipo " +
                "   AND E.EXERCICIO_ID = :EXE " +
                "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
                getUnidades(listaUnidades) +
                "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
                "   group by E.ID   " +
                ") " +
                "GROUP BY ID " +
                "HAVING SUM(VALOR) <> 0 ";
        } else {
            sql = "select ID, sum(valor) from ( " +
                "   SELECT E.ID, sum(coalesce(e.valor,0)) as valor  " +
                "   FROM EMPENHO E " +
                "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
                "   WHERE " +
                "   E.CATEGORIAORCAMENTARIA = 'RESTO' " +
                "   and e.tiporestosprocessados = :tipo " +
                "   AND E.EXERCICIO_ID = :EXE " +
                "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
                getUnidades(listaUnidades) +
                "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
                "   group by E.ID " +
                " " +
                "union all " +
                " " +
                "   SELECT E.ID, sum(coalesce(ee.valor,0)) * -1 as valor  " +
                "   FROM EMPENHO E " +
                "   inner join empenhoestorno ee on ee.empenho_id = e.id and trunc(ee.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
                "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
                "   WHERE " +
                "   E.CATEGORIAORCAMENTARIA = 'RESTO' " +
                "   and e.tiporestosprocessados = :tipo " +
                "   AND E.EXERCICIO_ID = :EXE " +
                "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
                getUnidades(listaUnidades) +
                "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
                "   group by E.ID " +
                " " +
                "union all " +
                " " +
                "   SELECT E.ID, sum(coalesce(l.valor,0)) * -1 as valor  " +
                "   FROM EMPENHO E " +
                "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
                "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
                "   WHERE " +
                "   E.CATEGORIAORCAMENTARIA = 'RESTO' " +
                "   and e.tiporestosprocessados = :tipo " +
                "   AND E.EXERCICIO_ID = :EXE " +
                "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
                getUnidades(listaUnidades) +
                "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, e.dataempenho) " +
                "   group by E.ID" +
                "    " +
                "union all " +
                " " +
                "   SELECT E.ID, sum(coalesce(le.valor,0)) as valor  " +
                "   FROM EMPENHO E " +
                "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
                "   inner join liquidacaoestorno le on le.liquidacao_id = l.id and trunc(le.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
                "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
                "   WHERE " +
                "   E.CATEGORIAORCAMENTARIA = 'RESTO' " +
                "   and e.tiporestosprocessados = :tipo " +
                "   AND E.EXERCICIO_ID = :EXE " +
                "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
                getUnidades(listaUnidades) +
                "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
                "   group by E.ID   " +
                ") " +
                "GROUP BY ID " +
                "HAVING SUM(VALOR) <> 0 ";
        }
        return sql;
    }

    public List<PrescricaoEmpenho> getPrescricaoRestoAPagar(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades, TipoRestosProcessado tipoRestosProcessado) {
        String sql = getSqlPrescricaoRestoAPagar(selecionado, listaUnidades, tipoRestosProcessado);
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("EXE", selecionado.getExercicio().getId());
        consulta.setParameter("tipo", tipoRestosProcessado.name());
        List<PrescricaoEmpenho> retorno = new ArrayList<PrescricaoEmpenho>();
        List resultList = consulta.getResultList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;

            Empenho empenho = em.find(Empenho.class, ((BigDecimal) objeto[0]).longValue());
            BigDecimal valor = (BigDecimal) objeto[1];

            retorno.add(new PrescricaoEmpenho(empenho, selecionado, tipoRestosProcessado, valor));
        }
        return retorno;
    }

    public List<InscricaoEmpenho> getEmpenhosRestosProcessadosAInscrever(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {
        String sql = getSqlInscricaoDeRestoaAPagarProcessados(listaUnidades);
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("EXE", selecionado.getExercicio().getId());
        List<InscricaoEmpenho> retorno = new ArrayList<InscricaoEmpenho>();
        List resultList = consulta.getResultList();

        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;

            Empenho empenho = em.find(Empenho.class, ((BigDecimal) objeto[0]).longValue());
            empenho.getObrigacoesPagar().size();
            BigDecimal valor = (BigDecimal) objeto[1];

            retorno.add(new InscricaoEmpenho(empenho, selecionado, TipoRestosProcessado.PROCESSADOS, valor, BigDecimal.ZERO, BigDecimal.ZERO));
        }
        return retorno;
    }

    public List<TransporteDeSaldoAbertura> getTransportesSaldoAbertura(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades, List<Conta> contas) {
        String sql = "select vworgao.id as orgao, vw.id as unidade, s.totalcredito as credito, s.totaldebito as debito, c.id as conta" +
            " from saldocontacontabil s " +
            " inner join conta c on s.contacontabil_id = c.id " +
            "   INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.unidadeorganizacional_id = VW.SUBORDINADA_ID " +
            "   INNER JOIN VWHIERARQUIAORCAMENTARIA VWORGAO ON VW.SUPERIOR_ID = VWORGAO.SUBORDINADA_ID" +
            " where c.exercicio_id = :exercicio" +
            " and trunc(s.datasaldo) = to_date(:dataReferencia,'dd/MM/yyyy') " +
            " and s.tipobalancete = 'TRANSPORTE'" +
            " AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy')) " +
            " AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VWORGAO.INICIOVIGENCIA AND COALESCE(VWORGAO.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy')) " +
            getUnidades(listaUnidades) +
            getContas(contas);

        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("exercicio", selecionado.getExercicio().getId());
        return criarTransporteDeSaldoAbertura(selecionado, consulta.getResultList());
    }

    private List<TransporteDeSaldoAbertura> criarTransporteDeSaldoAbertura(AberturaFechamentoExercicio selecionado, List resultList) {
        List<TransporteDeSaldoAbertura> retorno = new ArrayList<TransporteDeSaldoAbertura>();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            HierarquiaOrganizacional orgao = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[0]).longValue());
            HierarquiaOrganizacional unidade = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
            BigDecimal credito = (BigDecimal) objeto[2];
            BigDecimal debito = (BigDecimal) objeto[3];
            Conta conta = em.find(Conta.class, ((BigDecimal) objeto[4]).longValue());
            retorno.add(new TransporteDeSaldoAbertura(orgao, unidade, unidade.getSubordinada(), conta, credito, debito, selecionado));
        }
        return retorno;
    }

    public List<Empenho> getEmpenhosRestosProcessadosAInscreverDoExercicioAnterior(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {

        String sql = "SELECT distinct e.* FROM EMPENHO E  " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON E.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            " WHERE  E.CATEGORIAORCAMENTARIA = 'RESTO'  " +
            " AND E.EXERCICIO_ID = :EXE " +
            " and trunc(e.dataempenho) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy')  " +
            " and e.transportado = 1  " +
            " and e.tiporestosprocessados = 'PROCESSADOS' " +
            getUnidades(listaUnidades);
        Query consulta = em.createNativeQuery(sql, Empenho.class);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + selecionado.getExercicio().getAno());
        consulta.setParameter("EXE", selecionado.getExercicio().getId());
        List<Empenho> resultList = consulta.getResultList();
        return resultList;
    }

    private String getSqlInscricaoDeRestoaAPagarProcessados(List<HierarquiaOrganizacional> listaUnidades) {
        return "select ID, sum(valor) as saldo from ( " +
            "   SELECT  E.ID, sum(coalesce(l.valor,0)) as valor  " +
            "   FROM EMPENHO E " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.ID " +
            "    " +
            "union all " +
            " " +
            "   SELECT E.ID, sum(coalesce(le.valor,0)) * -1 as valor  " +
            "   FROM EMPENHO E " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join liquidacaoestorno le on le.liquidacao_id = l.id and trunc(le.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.ID" +
            "    " +
            "union all " +
            " " +
            "   SELECT E.ID, sum(coalesce(p.valor,0)) * -1 as valor  " +
            "   FROM EMPENHO E " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join pagamento p on p.liquidacao_id = l.id and trunc(p.datapagamento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.ID " +
            "  " +
            " union all " +
            "  " +
            "   SELECT E.ID, sum(coalesce(pe.valor,0)) as valor  " +
            "   FROM EMPENHO E " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join pagamento p on p.liquidacao_id = l.id and trunc(p.datapagamento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join pagamentoestorno pe on pe.pagamento_id = p.id and trunc(pe.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.ID " +
            ")  " +
            "group by ID " +
            "having SUM(VALOR) > 0";
    }

    public List<AberturaInscricaoResto> getEmpenhosRestosNaoProcessadosAInscreverAberturaDoExercicioAnterior(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {

        String sql = "SELECT distinct e.* FROM EMPENHO E  " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON E.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            " WHERE  E.CATEGORIAORCAMENTARIA = 'RESTO'  " +
            " AND E.EXERCICIO_ID = :EXE " +
            " and trunc(e.dataempenho) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy')  " +
            " and e.transportado = 1  " +
            " and e.tiporestosprocessados = 'NAO_PROCESSADOS' " +
            getUnidades(listaUnidades);
        Query consulta = em.createNativeQuery(sql, Empenho.class);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + selecionado.getExercicio().getAno());
        consulta.setParameter("EXE", selecionado.getExercicio().getId());
        List<AberturaInscricaoResto> retorno = new ArrayList<AberturaInscricaoResto>();
        List<Empenho> resultList = consulta.getResultList();
        for (Empenho o : resultList) {
            retorno.add(new AberturaInscricaoResto(o, selecionado, TipoRestosProcessado.NAO_PROCESSADOS, o.getValor()));
        }
        return retorno;
    }

    public List<AberturaInscricaoResto> getEmpenhosRestosNaoProcessadosAInscreverAbertura(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {
        String sql = getSqlInscricaoDeRestoAPagarNaoProcessados(listaUnidades);
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        List<AberturaInscricaoResto> retorno = new ArrayList<AberturaInscricaoResto>();
        List resultList = consulta.getResultList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;

            Empenho empenho = em.find(Empenho.class, ((BigDecimal) objeto[0]).longValue());
            BigDecimal valor = (BigDecimal) objeto[1];

            retorno.add(new AberturaInscricaoResto(empenho, selecionado, TipoRestosProcessado.NAO_PROCESSADOS, valor));
        }
        return retorno;
    }

    public List<InscricaoEmpenho> getEmpenhosRestosNaoProcessadosAInscrever(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {
        String sql = getSqlInscricaoDeRestoAPagarNaoProcessados(listaUnidades);
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("EXE", selecionado.getExercicio().getId());

        List<InscricaoEmpenho> retorno = new ArrayList<InscricaoEmpenho>();
        List resultList = consulta.getResultList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;

            Empenho empenho = em.find(Empenho.class, ((BigDecimal) objeto[0]).longValue());
            empenho.getObrigacoesPagar().size();
            BigDecimal valor = (BigDecimal) objeto[1];
            BigDecimal saldoObrigacaoPagarAntesEmp = (BigDecimal) objeto[2];
            BigDecimal saldoObrigacaoPagarDepoisEmp = (BigDecimal) objeto[3];

            retorno.add(new InscricaoEmpenho(empenho, selecionado, TipoRestosProcessado.NAO_PROCESSADOS, valor, saldoObrigacaoPagarAntesEmp, saldoObrigacaoPagarDepoisEmp));
        }
        return retorno;
    }

    private String getSqlInscricaoDeRestoAPagarNaoProcessados(List<HierarquiaOrganizacional> listaUnidades) {
        return "select id, sum(valor) as saldo, sum(saldoObrigacaoPagarAntesEmp) as saldoObrigacaoPagarAntesEmp, sum(saldoObrigacaoPagarDepoisEmp) as saldoObrigacaoPagarDepoisEmp from ( " +
            "   SELECT E.id, sum(coalesce(e.valor,0)) as valor, sum(coalesce(e.saldoObrigacaoPagarAntesEmp,0)) as saldoObrigacaoPagarAntesEmp,  sum(coalesce(e.saldoObrigacaoPagarDepoisEmp,0)) as saldoObrigacaoPagarDepoisEmp " +
            "   FROM EMPENHO E " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.id, E.DATAEMPENHO " +
            " " +
            "union all " +
            " " +
            "   SELECT  E.id, sum(coalesce(ee.valor,0)) * -1 as valor, 0 as saldoObrigacaoPagarAntesEmp, 0 as saldoObrigacaoPagarDepoisEmp  " +
            "   FROM EMPENHO E " +
            "   inner join empenhoestorno ee on ee.empenho_id = e.id and trunc(ee.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.id, E.DATAEMPENHO " +
            " " +
            "union all " +
            " " +
            "   SELECT E.id, sum(coalesce(l.valor,0)) * -1 as valor, 0 as saldoObrigacaoPagarAntesEmp, 0 as saldoObrigacaoPagarDepoisEmp  " +
            "   FROM EMPENHO E " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.id, E.DATAEMPENHO " +
            "    " +
            "union all " +
            " " +
            "   SELECT E.id, sum(coalesce(le.valor,0)) as valor, 0 as saldoObrigacaoPagarAntesEmp, 0 as saldoObrigacaoPagarDepoisEmp " +
            "   FROM EMPENHO E " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join liquidacaoestorno le on le.liquidacao_id = l.id and trunc(le.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.id " +
            ")  " +
            "group by id " +
            "having SUM(VALOR) <> 0 ";
    }

    public List<ReceitaFechamentoExercicio> getReceitasARealizar(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {
        String sql = "select contareceita, unidade,  fonte , cdest, sum(valor) as valor from ( " +
            " SELECT VW.id AS UNIDADE, C.id AS CONTARECEITA, F.id AS FONTE, RALT.contaDeDestinacao_id  as cdest, " +
            " CASE  " +
            " WHEN RALT.TIPOALTERACAOORC = 'PREVISAO' THEN RALT.VALOR " +
            " WHEN RALT.TIPOALTERACAOORC = 'ANULACAO' THEN RALT.VALOR * -1 " +
            " end as valor " +
            "   FROM RECEITAALTERACAOORC RALT " +
            " INNER JOIN ALTERACAOORC ALT ON RALT.ALTERACAOORC_ID = ALT.ID " +
            " inner join RECEITALOA rl on ralt.RECEITALOA_ID = rl.id " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN FONTEDERECURSOS F ON RALT.FONTEDERECURSO_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RL.ENTIDADE_ID = VW.SUBORDINADA_ID " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND trunc(ALT.DATAEFETIVACAO) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            getUnidades(listaUnidades) +
            "  " +
            " UNION ALL " +
            "  " +
            " SELECT VW.id AS UNIDADE, C.id AS CONTARECEITA, F.id AS FONTE, RALT.contaDeDestinacao_id  as cdest, " +
            " CASE  " +
            " WHEN RALT.TIPOALTERACAOORC = 'PREVISAO' THEN RALT.VALOR * -1" +
            " WHEN RALT.TIPOALTERACAOORC = 'ANULACAO' THEN RALT.VALOR  " +
            " end as valor " +
            "   FROM estornoalteracaoorc est " +
            " INNER JOIN ALTERACAOORC ALT ON est.alteracaoorc_id = alt.id" +
            " inner join RECEITAALTERACAOORC RALT on RALT.ALTERACAOORC_ID = ALT.ID " +
            " inner join RECEITALOA rl on ralt.RECEITALOA_ID = rl.id " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN FONTEDERECURSOS F ON RALT.FONTEDERECURSO_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RL.ENTIDADE_ID = VW.SUBORDINADA_ID " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND trunc(est.dataEstorno) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            getUnidades(listaUnidades) +
            "  " +
            " UNION ALL " +
            "  " +
            " SELECT VW.id AS UNIDADE, C.id AS CONTARECEITA, F.id AS FONTE, cd.id as cdest, RLF.VALOR AS VALOR  FROM RECEITALOAFONTE RLF  " +
            " inner join RECEITALOA rl on RLF.RECEITALOA_ID = rl.id " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN CONTADEDESTINACAO CD ON RLF.DESTINACAODERECURSOS_ID = CD.ID " +
            " INNER JOIN FONTEDERECURSOS F ON CD.FONTEDERECURSOS_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RL.ENTIDADE_ID = VW.SUBORDINADA_ID " +
            " INNER JOIN LOA LOA ON RL.LOA_ID = LOA.ID " +
            " INNER JOIN LDO LDO ON LOA.LDO_ID = LDO.ID " +
            " inner join exercicio e on LDO.EXERCICIO_ID = e.id " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND E.ID = :EXE" +
            " and rl.operacaoreceita in " + OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaNormal()) + "" +
            getUnidades(listaUnidades) +
            "  " +
            " UNION ALL " +
            "  " +
            " SELECT VW.id AS UNIDADE, C.id AS CONTARECEITA, F.id AS FONTE, cd.id as cdest, RLF.VALOR * -1 AS VALOR  FROM RECEITALOAFONTE RLF  " +
            " inner join RECEITALOA rl on RLF.RECEITALOA_ID = rl.id " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN CONTADEDESTINACAO CD ON RLF.DESTINACAODERECURSOS_ID = CD.ID " +
            " INNER JOIN FONTEDERECURSOS F ON CD.FONTEDERECURSOS_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RL.ENTIDADE_ID = VW.SUBORDINADA_ID " +
            " INNER JOIN LOA LOA ON RL.LOA_ID = LOA.ID " +
            " INNER JOIN LDO LDO ON LOA.LDO_ID = LDO.ID " +
            " inner join exercicio e on LDO.EXERCICIO_ID = e.id " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND E.ID = :EXE" +
            " and rl.operacaoreceita in " + OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()) + "" +
            getUnidades(listaUnidades) +
            "  " +
            " UNION ALL " +
            "  " +
            " SELECT VW.id AS UNIDADE, C.id AS CONTARECEITA, F.id AS FONTE, cd.id as cdest, RF.VALOR * -1 as valor  FROM LANCRECEITAFONTE RF " +
            " INNER JOIN LANCAMENTORECEITAORC RR ON RF.LANCRECEITAORC_ID = RR.ID " +
            " INNER JOIN RECEITALOA RL ON RR.RECEITALOA_ID = RL.ID " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN RECEITALOAFONTE RLF ON RF.RECEITALOAFONTE_ID = RLF.ID " +
            " INNER JOIN CONTADEDESTINACAO CD ON RLF.DESTINACAODERECURSOS_ID = CD.ID " +
            " INNER JOIN FONTEDERECURSOS F ON CD.FONTEDERECURSOS_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RL.ENTIDADE_ID = VW.SUBORDINADA_ID " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND trunc(RR.DATALANCAMENTO) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " and rl.operacaoreceita in " + OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaNormal()) + "" +
            getUnidades(listaUnidades) +
            "  " +
            " UNION ALL " +
            "  " +
            " SELECT VW.id AS UNIDADE, C.id AS CONTARECEITA, F.id AS FONTE, cd.id as cdest, RF.VALOR as valor  FROM LANCRECEITAFONTE RF " +
            " INNER JOIN LANCAMENTORECEITAORC RR ON RF.LANCRECEITAORC_ID = RR.ID " +
            " INNER JOIN RECEITALOA RL ON RR.RECEITALOA_ID = RL.ID " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN RECEITALOAFONTE RLF ON RF.RECEITALOAFONTE_ID = RLF.ID " +
            " INNER JOIN CONTADEDESTINACAO CD ON RLF.DESTINACAODERECURSOS_ID = CD.ID " +
            " INNER JOIN FONTEDERECURSOS F ON CD.FONTEDERECURSOS_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RL.ENTIDADE_ID = VW.SUBORDINADA_ID " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND trunc(RR.DATALANCAMENTO) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " and rl.operacaoreceita in " + OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()) + "" +
            getUnidades(listaUnidades) +
            "  " +
            " UNION ALL " +
            "  " +
            " SELECT VW.id AS UNIDADE, C.id AS CONTARECEITA, F.id AS FONTE, cd.id as cdest, RF.VALOR * -1 AS VALOR FROM RECEITAORCFONTEESTORNO RF " +
            " INNER JOIN RECEITAORCESTORNO RR ON RF.RECEITAORCESTORNO_id = RR.ID " +
            " INNER JOIN RECEITALOA RL ON RR.RECEITALOA_ID = RL.ID " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN RECEITALOAFONTE RLF ON RF.RECEITALOAFONTE_ID = RLF.ID " +
            " INNER JOIN CONTADEDESTINACAO CD ON RLF.DESTINACAODERECURSOS_ID = CD.ID " +
            " INNER JOIN FONTEDERECURSOS F ON CD.FONTEDERECURSOS_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RL.ENTIDADE_ID = VW.SUBORDINADA_ID " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND trunc(RR.DATAESTORNO) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " and RR.operacaoreceitarealizada in " + OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()) + "" +
            getUnidades(listaUnidades) +
            "  " +
            " union all " +
            "  " +
            " SELECT VW.id AS UNIDADE, C.id AS CONTARECEITA, F.id AS FONTE, cd.id as cdest, RF.VALOR AS VALOR FROM RECEITAORCFONTEESTORNO RF " +
            " INNER JOIN RECEITAORCESTORNO RR ON RF.RECEITAORCESTORNO_id = RR.ID " +
            " INNER JOIN RECEITALOA RL ON RR.RECEITALOA_ID = RL.ID " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN RECEITALOAFONTE RLF ON RF.RECEITALOAFONTE_ID = RLF.ID " +
            " INNER JOIN CONTADEDESTINACAO CD ON RLF.DESTINACAODERECURSOS_ID = CD.ID " +
            " INNER JOIN FONTEDERECURSOS F ON CD.FONTEDERECURSOS_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RL.ENTIDADE_ID = VW.SUBORDINADA_ID " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND trunc(RR.DATAESTORNO) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " and RR.operacaoreceitarealizada in " + OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaNormal()) + "" +
            getUnidades(listaUnidades) +
            " ) " +
            " GROUP BY UNIDADE, CONTARECEITA, FONTE, cdest " +
            " having sum(valor) <> 0" +
            " order by unidade, contareceita, fonte, cdest ";


        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("EXE", selecionado.getExercicio().getId());
        List resultList = consulta.getResultList();
        return retornarReceita(resultList, TipoReceitaFechamentoExercicio.RECEITA_A_REALIZAR, selecionado);
    }

    private List<ReceitaFechamentoExercicio> retornarReceita(List resultList, TipoReceitaFechamentoExercicio tipoReceitaFechamentoExercicio, AberturaFechamentoExercicio selecionado) {
        List<ReceitaFechamentoExercicio> retorno = new ArrayList<ReceitaFechamentoExercicio>();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;

            Conta c = em.find(Conta.class, ((BigDecimal) objeto[0]).longValue());
            HierarquiaOrganizacional ho = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
            FonteDeRecursos fonte = em.find(FonteDeRecursos.class, ((BigDecimal) objeto[2]).longValue());
            ContaDeDestinacao contaDeDestinacao = em.find(ContaDeDestinacao.class, ((BigDecimal) objeto[3]).longValue());
            BigDecimal valor = (BigDecimal) objeto[4];

            retorno.add(new ReceitaFechamentoExercicio(c, ho, fonte, contaDeDestinacao, valor, tipoReceitaFechamentoExercicio, selecionado));
        }
        return retorno;
    }

    public List<ReceitaFechamentoExercicio> getReceitasRealizadas(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {
        String sql = " SELECT (select c.id from conta c " +
            " inner join PLANODECONTAS pc on C.PLANODECONTAS_ID = pc.id " +
            " inner join PLANODECONTASEXERCICIO pce on PCE.PLANODERECEITAS_ID = PC.ID where c.codigo = contareceita and pce.exercicio_id = :exercicio) as contareceita, " +
            " unidade, " +
            " fonte, " +
            " cdest, " +
            " sum(valor) as valor " +
            " from ( " +
            " SELECT VW.ID as unidade, C.codigo as contareceita, F.ID as fonte, CD.id as cdest, RF.VALOR as valor  FROM LANCRECEITAFONTE RF " +
            " INNER JOIN LANCAMENTORECEITAORC RR ON RF.LANCRECEITAORC_ID = RR.ID " +
            " INNER JOIN RECEITALOA RL ON RR.RECEITALOA_ID = RL.ID " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN RECEITALOAFONTE RLF ON RF.RECEITALOAFONTE_ID = RLF.ID " +
            " INNER JOIN CONTADEDESTINACAO CD ON RLF.DESTINACAODERECURSOS_ID = CD.ID " +
            " INNER JOIN FONTEDERECURSOS F ON CD.FONTEDERECURSOS_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RR.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND trunc(RR.DATALANCAMENTO) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " and RR.operacaoreceitarealizada in " + OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaNormal()) + "" +
            getUnidades(listaUnidades) +
            " union all " +
            " SELECT VW.ID AS UNIDADE,  C.codigo AS CONTARECEITA, F.ID AS FONTE, CD.id as cdest, RF.VALOR * -1 AS VALOR FROM RECEITAORCFONTEESTORNO RF " +
            " INNER JOIN RECEITAORCESTORNO RR ON RF.RECEITAORCESTORNO_id = RR.ID " +
            " INNER JOIN RECEITALOA RL ON RR.RECEITALOA_ID = RL.ID " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN RECEITALOAFONTE RLF ON RF.RECEITALOAFONTE_ID = RLF.ID " +
            " INNER JOIN CONTADEDESTINACAO CD ON RLF.DESTINACAODERECURSOS_ID = CD.ID " +
            " INNER JOIN FONTEDERECURSOS F ON CD.FONTEDERECURSOS_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RR.UNIDADEORGANIZACIONALORC = VW.SUBORDINADA_ID " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND trunc(RR.DATAESTORNO) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " and RR.operacaoreceitarealizada in " + OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaNormal()) + "" +
            getUnidades(listaUnidades) +
            " union all " +
            " SELECT VW.ID as unidade,  " +
            " CASE '1.' || SUBSTR(C.CODIGO,3,17) " +
            "      WHEN '1.7.2.0.01.0.1.00' then '1.7.2.1.01.0.1.00' " +
            "      WHEN '1.7.2.0.02.0.1.00' THEN '1.7.2.1.02.0.1.00' " +
            "      WHEN '1.7.2.0.04.0.1.00' THEN '1.7.2.1.04.0.1.00' " +
            "      WHEN '1.7.6.0.01.0.1.00' THEN '1.7.6.1.01.0.1.00' " +
            "      WHEN '1.7.6.0.02.0.1.00' THEN '1.7.6.1.02.0.1.00' " +
            "      WHEN '1.7.6.0.03.0.1.00' THEN '1.7.6.1.03.0.1.00' " +
            "  ELSE '1.' || SUBSTR(C.CODIGO,3,17) end as contareceita, " +
            " F.ID as fonte, CD.id as cdest, RF.VALOR *-1 as valor  FROM LANCRECEITAFONTE RF " +
            " INNER JOIN LANCAMENTORECEITAORC RR ON RF.LANCRECEITAORC_ID = RR.ID " +
            " INNER JOIN RECEITALOA RL ON RR.RECEITALOA_ID = RL.ID " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN RECEITALOAFONTE RLF ON RF.RECEITALOAFONTE_ID = RLF.ID " +
            " INNER JOIN CONTADEDESTINACAO CD ON RLF.DESTINACAODERECURSOS_ID = CD.ID " +
            " INNER JOIN FONTEDERECURSOS F ON CD.FONTEDERECURSOS_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RR.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND trunc(RR.DATALANCAMENTO) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " and RR.operacaoreceitarealizada in " + OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()) + "" +
            getUnidades(listaUnidades) +
            " union all " +
            " SELECT VW.ID AS UNIDADE, " +
            " CASE '1.' || SUBSTR(C.CODIGO,3,17) " +
            "      WHEN '1.7.2.0.01.0.1.00' then '1.7.2.1.01.0.1.00' " +
            "      WHEN '1.7.2.0.02.0.1.00' THEN '1.7.2.1.02.0.1.00' " +
            "      WHEN '1.7.2.0.04.0.1.00' THEN '1.7.2.1.04.0.1.00' " +
            "      WHEN '1.7.6.0.01.0.1.00' THEN '1.7.6.1.01.0.1.00' " +
            "      WHEN '1.7.6.0.02.0.1.00' THEN '1.7.6.1.02.0.1.00' " +
            "      WHEN '1.7.6.0.03.0.1.00' THEN '1.7.6.1.03.0.1.00' " +
            "  ELSE '1.' || SUBSTR(C.CODIGO,3,17) end as contareceita, " +
            " F.ID AS FONTE, CD.id as cdest, RF.VALOR  AS VALOR FROM RECEITAORCFONTEESTORNO RF " +
            " INNER JOIN RECEITAORCESTORNO RR ON RF.RECEITAORCESTORNO_id = RR.ID " +
            " INNER JOIN RECEITALOA RL ON RR.RECEITALOA_ID = RL.ID " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN RECEITALOAFONTE RLF ON RF.RECEITALOAFONTE_ID = RLF.ID " +
            " INNER JOIN CONTADEDESTINACAO CD ON RLF.DESTINACAODERECURSOS_ID = CD.ID " +
            " INNER JOIN FONTEDERECURSOS F ON CD.FONTEDERECURSOS_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RR.UNIDADEORGANIZACIONALORC = VW.SUBORDINADA_ID " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND trunc(RR.DATAESTORNO) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " and RR.operacaoreceitarealizada in " + OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()) + "" +
            getUnidades(listaUnidades) +
            ") " +
            " GROUP BY UNIDADE, CONTARECEITA, FONTE, cdest " +
            " order by unidade, contareceita, fonte, cdest ";

        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("exercicio", selecionado.getExercicio().getId());
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        List resultList = consulta.getResultList();
        return retornarReceita(resultList, TipoReceitaFechamentoExercicio.RECEITA_REALIZADA, selecionado);
    }

    public List<ReceitaExtraFechamentoExercicio> buscarReceitasExtras(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> unidades) {
        String sql = " select * from ( " +
            "          SELECT vw.ID, " +
            "                 rec.CONTAEXTRAORCAMENTARIA_ID, " +
            "                 rec.FONTEDERECURSOS_ID, " +
            "                 rec.contaDeDestinacao_id, " +
            "                 rec.valor                          - " +
            "                 (SELECT COALESCE(SUM(est.valor), 0) " +
            "                 FROM receitaextraestorno est " +
            "                 WHERE TRUNC(est.dataestorno) <= to_date(:dataReferencia, 'dd/mm/yyyy') " +
            "                 AND est.receitaextra_id      = rec.id " +
            "                 ) - " +
            "                 (SELECT COALESCE(SUM(re.valor), 0) " +
            "                 FROM pagamentoextra desp " +
            "                 INNER JOIN pagamentoreceitaextra pre " +
            "                 ON desp.id = pre.pagamentoextra_id " +
            "                 INNER JOIN receitaextra re " +
            "                 ON re.id                    = pre.receitaextra_id " +
            "                 WHERE TRUNC(desp.datapagto) <= to_date(:dataReferencia, 'dd/mm/yyyy') " +
            "                 AND pre.receitaextra_id     = rec.id " +
            "                 ) + " +
            "                 (SELECT COALESCE(SUM(re.valor), 0) " +
            "                 FROM pagamentoextraestorno est " +
            "                 INNER JOIN PagamentoEstornoRecExtra pere " +
            "                 ON pere.pagamentoextraestorno_id = est.id " +
            "                 INNER JOIN receitaextra re " +
            "                 ON re.id                     = pere.receitaextra_id " +
            "                 WHERE TRUNC(est.dataestorno) <= to_date(:dataReferencia, 'dd/mm/yyyy') " +
            "                 AND pere.receitaextra_id     = rec.id " +
            "                 ) - " +
            "                 (SELECT COALESCE(SUM(re.valor), 0) " +
            "                 FROM AJUSTEDEPOSITO ad " +
            "                 INNER JOIN AjusteDepositoReceitaExtra adre " +
            "                 ON adre.ajustedeposito_id = ad.id " +
            "                 INNER JOIN receitaextra re " +
            "                 ON re.id                   = adre.receitaextra_id " +
            "                 WHERE TRUNC(ad.dataajuste) <= to_date(:dataReferencia, 'dd/mm/yyyy') " +
            "                 AND ad.tipoajuste          = :aumentativo " +
            "                 AND adre.receitaextra_id   = rec.id " +
            "                 ) + " +
            "                 (SELECT COALESCE(SUM(re.valor), 0) " +
            "                 FROM AJUSTEDEPOSITOESTORNO EST " +
            "                 INNER JOIN AJUSTEDEPOSITO ad " +
            "                 ON ad.id = est.ajustedeposito_id " +
            "                 INNER JOIN AjusteDepositoReceitaExtra adre " +
            "                 ON adre.ajustedeposito_id = ad.id " +
            "                 INNER JOIN receitaextra re " +
            "                 ON re.id                     = adre.receitaextra_id " +
            "                 WHERE TRUNC(est.dataestorno) <= to_date(:dataReferencia, 'dd/mm/yyyy') " +
            "                 AND ad.tipoajuste            = :aumentativo " +
            "                 AND adre.receitaextra_id     = rec.id " +
            "                 ) as VALOR, " +
            "                 REc.SALDO, " +
            "                 REc.VALORESTORNADO, " +
            "                 REc.TIPOCONSIGNACAO, " +
            "                 REc.SITUACAORECEITAEXTRA, " +
            "                 REc.PESSOA_ID, " +
            "                 REc.classeCredor_ID, " +
            "                 REc.SUBCONTA_ID, " +
            "                 REc.NUMERO, " +
            "                 REc.COMPLEMENTOHISTORICO, " +
            "                 REc.HISTORICONOTA, " +
            "                 REc.HISTORICORAZAO " +
            "            from RECEITAEXTRA rec " +
            "           INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON rec.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID  " +
            "           where rec.EXERCICIO_ID =  :exercicio " +
            "             and TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            "             AND trunc(REc.DATARECEITA) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            getUnidades(unidades) +
            "  ) where valor > 0 ";

        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("exercicio", selecionado.getExercicio().getId());
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("aumentativo", TipoAjuste.AUMENTATIVO.name());
        List<Object[]> resultado = consulta.getResultList();

        List<ReceitaExtraFechamentoExercicio> retorno = Lists.newArrayList();
        for (Object[] obj : resultado) {
            HierarquiaOrganizacional hierarquia = em.find(HierarquiaOrganizacional.class, ((BigDecimal) obj[0]).longValue());
            Conta conta = em.find(Conta.class, ((BigDecimal) obj[1]).longValue());
            FonteDeRecursos fonte = em.find(FonteDeRecursos.class, ((BigDecimal) obj[2]).longValue());
            ContaDeDestinacao contaDeDestinacao = em.find(ContaDeDestinacao.class, ((BigDecimal) obj[3]).longValue());
            Pessoa pessoa = em.find(Pessoa.class, ((BigDecimal) obj[9]).longValue());
            ClasseCredor classeCredor = em.find(ClasseCredor.class, ((BigDecimal) obj[10]).longValue());
            SubConta subConta = em.find(SubConta.class, ((BigDecimal) obj[11]).longValue());
            ReceitaExtraFechamentoExercicio receita = new ReceitaExtraFechamentoExercicio();
            receita.setConta(conta);
            receita.setHierarquiaOrganizacional(hierarquia);
            receita.setUnidadeOrganizacional(hierarquia.getSubordinada());
            receita.setFonteDeRecursos(fonte);
            receita.setValor((BigDecimal) obj[4]);
            receita.setSaldo((BigDecimal) obj[5]);
            receita.setValorEstornado((BigDecimal) obj[6]);
            receita.setTipoConsignacao(obj[7] != null ? TipoConsignacao.valueOf((String) obj[7]) : null);
            receita.setSituacaoReceitaExtra(SituacaoReceitaExtra.valueOf((String) obj[8]));
            receita.setPessoa(pessoa);
            receita.setClasseCredor(classeCredor);
            receita.setSubConta(subConta);
            receita.setNumeroOriginal((String) obj[12]);
            receita.setComplementoHistorico((String) obj[13]);
            receita.setHistoricoNota((String) obj[14]);
            receita.setHistoricoRazao((String) obj[15]);
            receita.setAberturaFechamentoExercicio(selecionado);
            receita.setContaDeDestinacao(contaDeDestinacao);
            retorno.add(receita);
        }
        return retorno;
    }

    public List<ReceitaFechamentoExercicio> getReceitasParaDeducaoReceitaRealizada(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {
        String sql = "select contareceita, unidade,  fonte, cdest, sum(valor) as valor from ( " +
            " SELECT VW.ID as unidade, C.ID as contareceita, F.ID as fonte, CD.id as cdest, RF.VALOR as valor  FROM LANCRECEITAFONTE RF " +
            " INNER JOIN LANCAMENTORECEITAORC RR ON RF.LANCRECEITAORC_ID = RR.ID " +
            " INNER JOIN RECEITALOA RL ON RR.RECEITALOA_ID = RL.ID " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN RECEITALOAFONTE RLF ON RF.RECEITALOAFONTE_ID = RLF.ID " +
            " INNER JOIN CONTADEDESTINACAO CD ON RLF.DESTINACAODERECURSOS_ID = CD.ID " +
            " INNER JOIN FONTEDERECURSOS F ON CD.FONTEDERECURSOS_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RR.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND trunc(RR.DATALANCAMENTO) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " and RR.operacaoreceitarealizada in " + OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()) + "" +
            getUnidades(listaUnidades) +
            " " +
            " union all " +
            " " +
            " SELECT VW.ID AS UNIDADE, C.ID AS CONTARECEITA, F.ID AS FONTE, CD.id as cdest, RF.VALOR * -1 AS VALOR FROM RECEITAORCFONTEESTORNO RF " +
            " INNER JOIN RECEITAORCESTORNO RR ON RF.RECEITAORCESTORNO_id = RR.ID " +
            " INNER JOIN RECEITALOA RL ON RR.RECEITALOA_ID = RL.ID " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN RECEITALOAFONTE RLF ON RF.RECEITALOAFONTE_ID = RLF.ID " +
            " INNER JOIN CONTADEDESTINACAO CD ON RLF.DESTINACAODERECURSOS_ID = CD.ID " +
            " INNER JOIN FONTEDERECURSOS F ON CD.FONTEDERECURSOS_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RR.UNIDADEORGANIZACIONALORC = VW.SUBORDINADA_ID " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND trunc(RR.DATAESTORNO) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " and RR.operacaoreceitarealizada in " + OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()) + "" +
            getUnidades(listaUnidades) +
            ") " +
            " GROUP BY UNIDADE, CONTARECEITA, FONTE, cdest " +
            " order by unidade, contareceita, fonte, cdest";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        List resultList = consulta.getResultList();
        return retornarReceita(resultList, TipoReceitaFechamentoExercicio.DEDUCAO_RECEITA_REALIZADA, selecionado);
    }

    public List<ReceitaFechamentoExercicio> getReceitasParaDeducaoPrevisaoInicialDaReceita(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {
        String sql = "SELECT C.ID AS CONTARECEITA, VW.ID AS UNIDADE,  F.ID AS FONTE, cd.id as cdest, sum(RLF.VALOR) AS VALOR  FROM RECEITALOAFONTE RLF  " +
            " inner join RECEITALOA rl on RLF.RECEITALOA_ID = rl.id " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN CONTADEDESTINACAO CD ON RLF.DESTINACAODERECURSOS_ID = CD.ID " +
            " INNER JOIN FONTEDERECURSOS F ON CD.FONTEDERECURSOS_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON RL.ENTIDADE_ID = VW.SUBORDINADA_ID " +
            " INNER JOIN LOA LOA ON RL.LOA_ID = LOA.ID " +
            " INNER JOIN LDO LDO ON LOA.LDO_ID = LDO.ID " +
            " inner join exercicio e on LDO.EXERCICIO_ID = e.id " +
            " WHERE TO_DATE(:data,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:data,'dd/MM/yyyy')) " +
            " AND E.id = :EXE" +
            getUnidades(listaUnidades) +
            " and rl.operacaoreceita in " + OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()) + "" +
            " GROUP By VW.ID , C.ID , F.ID, cd.id" +
            " ORDER BY VW.ID , C.ID , F.ID, cd.id";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("data", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("EXE", selecionado.getExercicio().getId());

        List resultList = consulta.getResultList();
        return retornarReceita(resultList, TipoReceitaFechamentoExercicio.DEDUCAO_INICIAL_RECEITA, selecionado);
    }

    public List<ReceitaFechamentoExercicio> getReceitasParaReestimativa(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {
        String sql = " select contareceita,unidade,fonte, cdest, sum(valor) from (" +
            " SELECT C.ID AS CONTARECEITA, VW.ID AS UNIDADE,  F.ID AS FONTE, ralt.contaDeDestinacao_id as cdest, " +
            " sum( " +
            " CASE  " +
            " WHEN RALT.TIPOALTERACAOORC = 'PREVISAO' THEN RALT.VALOR " +
            " WHEN RALT.TIPOALTERACAOORC = 'ANULACAO' THEN RALT.VALOR * -1 " +
            " end) as valor " +
            "   FROM RECEITAALTERACAOORC RALT " +
            " INNER JOIN ALTERACAOORC ALT ON RALT.ALTERACAOORC_ID = ALT.ID " +
            " inner join RECEITALOA rl on ralt.RECEITALOA_ID = rl.id " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN FONTEDERECURSOS F ON RALT.FONTEDERECURSO_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON rl.entidade_id = VW.SUBORDINADA_ID " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND trunc(ALT.DATAEFETIVACAO) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            getUnidades(listaUnidades) +
            " GROUP BY  VW.ID, C.ID, F.ID, ralt.contaDeDestinacao_id" +
            "  " +
            " union all " +
            " " +
            " SELECT C.ID AS CONTARECEITA, VW.ID AS UNIDADE,  F.ID AS FONTE, ralt.contaDeDestinacao_id as cdest, " +
            " sum( " +
            " CASE  " +
            " WHEN RALT.TIPOALTERACAOORC = 'PREVISAO' THEN RALT.VALOR * -1" +
            " WHEN RALT.TIPOALTERACAOORC = 'ANULACAO' THEN RALT.VALOR  " +
            " end) as valor " +
            "   FROM estornoalteracaoorc est " +
            " INNER JOIN ALTERACAOORC ALT ON est.alteracaoorc_id = alt.id" +
            " inner join RECEITAALTERACAOORC RALT on RALT.ALTERACAOORC_ID = ALT.ID " +
            " inner join RECEITALOA rl on ralt.RECEITALOA_ID = rl.id " +
            " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID " +
            " INNER JOIN FONTEDERECURSOS F ON RALT.FONTEDERECURSO_ID = F.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON rl.entidade_id = VW.SUBORDINADA_ID " +
            " WHERE TO_DATE(:dataReferencia,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataReferencia,'dd/MM/yyyy')) " +
            " AND trunc(est.dataEstorno) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            getUnidades(listaUnidades) +
            " GROUP BY  VW.ID, C.ID, F.ID, ralt.contaDeDestinacao_id" +
            " ) dados" +
            " GROUP BY contareceita,unidade,fonte, cdest" +
            " order BY contareceita,unidade,fonte, cdest";

        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        List resultList = consulta.getResultList();

        return retornarReceita(resultList, TipoReceitaFechamentoExercicio.REESTIMATIVA, selecionado);
    }

    public List<DotacaoFechamentoExercicio> getAnulacaoDotacao(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {
        String sql = "select orgao, unidade, funcao, subfuncao, programa, projetoatividade,subprojetoatividade, naturezadespesa, fonte, cdest, sum(valor) as valor, extensaofonterecurso_id from ( " +
            "SELECT vworgao.id AS orgao, " +
            "  vw.id           AS unidade, " +
            "  fu.id           AS funcao, " +
            "  subfu.id        AS subfuncao, " +
            "  prg.id          AS programa, " +
            "  acao.id         AS projetoatividade, " +
            "  sub.id          AS subprojetoatividade, " +
            "  c.id            AS naturezadespesa, " +
            "  fonte.id        AS fonte, " +
            "  cd.id           AS cdest, " +
            "  SUM(anul.valor) AS valor, " +
            "  pfonte.extensaofonterecurso_id " +
            " FROM alteracaoorc alt  " +
            " INNER JOIN anulacaoorc anul ON alt.id = anul.alteracaoorc_id " +
            " INNER JOIN fontedespesaorc fo ON anul.fontedespesaorc_id = fo.id " +
            " INNER JOIN provisaoppafonte pfonte ON fo.provisaoppafonte_id = pfonte.id " +
            " INNER JOIN contadedestinacao cd ON pfonte.destinacaoderecursos_id = cd.id " +
            " INNER JOIN fontederecursos fonte ON cd.fontederecursos_id = fonte.id " +
            " INNER JOIN despesaorc desp ON fo.despesaorc_id = desp.id " +
            " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.id " +
            " INNER JOIN vwhierarquiaorcamentaria vw ON prov.unidadeorganizacional_id = vw.subordinada_id " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN subacaoppa sub ON prov.subacaoppa_id = sub.id " +
            " INNER JOIN acaoppa acao ON sub.acaoppa_id = acao.id " +
            " INNER JOIN tipoacaoppa tipo ON acao.tipoacaoppa_id = tipo.id " +
            " INNER JOIN programappa prg ON acao.programa_id = prg.id " +
            " INNER JOIN subfuncao subfu ON acao.subfuncao_id = subfu.id " +
            " INNER JOIN funcao fu ON acao.funcao_id = fu.id " +
            " INNER JOIN conta c ON prov.contadedespesa_id = c.id " +
            " WHERE trunc(alt.dataefetivacao) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " and alt.status in ('DEFERIDO','ESTORNADA') " +
            getUnidades(listaUnidades) +
            " GROUP BY vworgao.id,  vw.id ,  fu.id ,  subfu.id ,  prg.id ,  acao.id ,  sub.id ,  c.id ,  fonte.id, cd.id, pfonte.extensaofonterecurso_id " +
            "  " +
            " union all " +
            "  " +
            " SELECT vworgao.id AS orgao, " +
            "   vw.id           AS unidade, " +
            "   fu.id           AS funcao, " +
            "   subfu.id        AS subfuncao, " +
            "   prg.id          AS programa, " +
            "   acao.id         AS projetoatividade, " +
            "   sub.id          AS subprojetoatividade, " +
            "   c.id            AS naturezadespesa, " +
            "   fonte.id        AS fonte, " +
            "   cd.id           AS cdest, " +
            "   SUM(est.valor*-1) AS valor," +
            "   pfonte.extensaofonterecurso_id " +
            " FROM estornoalteracaoorc alt  " +
            " INNER JOIN estornoanulacaoorc est ON alt.id = est.estornoalteracaoorc_id " +
            " INNER JOIN anulacaoorc anul ON anul.id = est.anulacaoorc_id " +
            " INNER JOIN fontedespesaorc fo ON anul.fontedespesaorc_id = fo.id " +
            " INNER JOIN provisaoppafonte pfonte ON fo.provisaoppafonte_id = pfonte.id " +
            " INNER JOIN contadedestinacao cd ON pfonte.destinacaoderecursos_id = cd.id " +
            " INNER JOIN fontederecursos fonte ON cd.fontederecursos_id = fonte.id " +
            " INNER JOIN despesaorc desp ON fo.despesaorc_id = desp.id " +
            " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.id " +
            " INNER JOIN vwhierarquiaorcamentaria vw ON prov.unidadeorganizacional_id = vw.subordinada_id " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN subacaoppa sub ON prov.subacaoppa_id = sub.id " +
            " INNER JOIN acaoppa acao ON sub.acaoppa_id = acao.id " +
            " INNER JOIN tipoacaoppa tipo ON acao.tipoacaoppa_id = tipo.id " +
            " INNER JOIN programappa prg ON acao.programa_id = prg.id " +
            " INNER JOIN subfuncao subfu ON acao.subfuncao_id = subfu.id " +
            " INNER JOIN funcao fu ON acao.funcao_id = fu.id " +
            " INNER JOIN conta c ON prov.contadedespesa_id = c.id " +
            " WHERE trunc(alt.dataestorno) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            getUnidades(listaUnidades) +
            " GROUP BY vworgao.id,  vw.id ,  fu.id ,  subfu.id ,  prg.id ,  acao.id ,  sub.id ,  c.id ,  fonte.id, cd.id, pfonte.extensaofonterecurso_id " +
            " ) " +
            " group by orgao, unidade, funcao, subfuncao, programa, projetoatividade,subprojetoatividade, naturezadespesa, fonte, cdest, extensaofonterecurso_id " +
            " order by orgao, unidade, funcao, subfuncao, programa, projetoatividade,subprojetoatividade,  naturezadespesa, fonte, cdest ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        List resultList = consulta.getResultList();

        return criarDotacaoFechamentoExercicio(selecionado, resultList, TipoDotacaoFechamentoExercicio.ANULACAO_DOTACAO);
    }

    public List<DotacaoFechamentoExercicio> getCreditoAdicionalPorOrigem(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades, OrigemSuplementacaoORC origemSuplementacaoORC, TipoDotacaoFechamentoExercicio tipo) {
        String sql = "select orgao, unidade, funcao, subfuncao, programa, projetoatividade,subprojetoatividade, naturezadespesa, fonte, cdest, sum(valor) as valor, extensaofonterecurso_id from (  " +
            "SELECT vworgao.id AS orgao, " +
            "  vw.id           AS unidade, " +
            "  fu.id           AS funcao, " +
            "  subfu.id        AS subfuncao, " +
            "  prg.id          AS programa, " +
            "  acao.id         AS projetoatividade, " +
            "  sub.id          AS subprojetoatividade, " +
            "  c.id            AS naturezadespesa, " +
            "  fonte.id        AS fonte, " +
            "  cd.id           AS cdest, " +
            "  SUM(su.valor)   AS valor," +
            "  pfonte.extensaofonterecurso_id " +
            " FROM alteracaoorc alt " +
            " INNER JOIN suplementacaoorc su ON alt.id = su.alteracaoorc_id " +
            " INNER JOIN fontedespesaorc fo ON su.fontedespesaorc_id = fo.id " +
            " INNER JOIN provisaoppafonte pfonte ON fo.provisaoppafonte_id = pfonte.id " +
            " INNER JOIN contadedestinacao cd ON pfonte.destinacaoderecursos_id = cd.id " +
            " INNER JOIN fontederecursos fonte ON cd.fontederecursos_id = fonte.id " +
            " INNER JOIN despesaorc desp ON fo.despesaorc_id = desp.id " +
            " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.id " +
            " INNER JOIN vwhierarquiaorcamentaria vw ON prov.unidadeorganizacional_id = vw.subordinada_id " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN subacaoppa sub ON prov.subacaoppa_id = sub.id " +
            " INNER JOIN acaoppa acao ON sub.acaoppa_id = acao.id " +
            " INNER JOIN tipoacaoppa tipo ON acao.tipoacaoppa_id = tipo.id " +
            " INNER JOIN programappa prg ON acao.programa_id = prg.id " +
            " INNER JOIN subfuncao subfu ON acao.subfuncao_id = subfu.id " +
            " INNER JOIN funcao fu ON acao.funcao_id = fu.id " +
            " INNER JOIN conta c ON prov.contadedespesa_id = c.id " +
            " WHERE trunc(alt.dataefetivacao) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " AND su.origemSuplemtacao = :origem " +
            " and alt.status in ('DEFERIDO','ESTORNADA') " +
            getUnidades(listaUnidades) +
            " GROUP BY vworgao.id,  vw.id ,  fu.id ,  subfu.id ,  prg.id ,  acao.id ,  sub.id ,  c.id ,  fonte.id, cd.id, pfonte.extensaofonterecurso_id " +
            " " +
            "union all " +
            " " +
            " " +
            "SELECT vworgao.id AS orgao, " +
            "  vw.id           AS unidade, " +
            "  fu.id           AS funcao, " +
            "  subfu.id        AS subfuncao, " +
            "  prg.id          AS programa, " +
            "  acao.id         AS projetoatividade, " +
            "  sub.id          AS subprojetoatividade, " +
            "  c.id            AS naturezadespesa, " +
            "  fonte.id        AS fonte, " +
            "  cd.id           AS cdest, " +
            "  SUM(su.valor * -1)   AS valor," +
            "  pfonte.extensaofonterecurso_id " +
            " FROM estornoalteracaoorc alt " +
            " INNER JOIN estornosuplementacaoorc est ON alt.id = est.estornoalteracaoorc_id " +
            " INNER JOIN suplementacaoorc su ON su.id = est.suplementacaoorc_id " +
            " INNER JOIN fontedespesaorc fo ON su.fontedespesaorc_id = fo.id " +
            " INNER JOIN provisaoppafonte pfonte ON fo.provisaoppafonte_id = pfonte.id " +
            " INNER JOIN contadedestinacao cd ON pfonte.destinacaoderecursos_id = cd.id " +
            " INNER JOIN fontederecursos fonte ON cd.fontederecursos_id = fonte.id " +
            " INNER JOIN despesaorc desp ON fo.despesaorc_id = desp.id " +
            " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.id " +
            " INNER JOIN vwhierarquiaorcamentaria vw ON prov.unidadeorganizacional_id = vw.subordinada_id " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN subacaoppa sub ON prov.subacaoppa_id = sub.id " +
            " INNER JOIN acaoppa acao ON sub.acaoppa_id = acao.id " +
            " INNER JOIN tipoacaoppa tipo ON acao.tipoacaoppa_id = tipo.id " +
            " INNER JOIN programappa prg ON acao.programa_id = prg.id " +
            " INNER JOIN subfuncao subfu ON acao.subfuncao_id = subfu.id " +
            " INNER JOIN funcao fu ON acao.funcao_id = fu.id " +
            " INNER JOIN conta c ON prov.contadedespesa_id = c.id " +
            " WHERE trunc(alt.dataestorno) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " AND su.origemSuplemtacao = :origem " +
            getUnidades(listaUnidades) +
            " GROUP BY vworgao.id,  vw.id ,  fu.id ,  subfu.id ,  prg.id ,  acao.id ,  sub.id ,  c.id ,  fonte.id, cd.id, pfonte.extensaofonterecurso_id " +
            " ) " +
            " group by orgao, unidade, funcao, subfuncao, programa, projetoatividade,subprojetoatividade, naturezadespesa, fonte, cdest, extensaofonterecurso_id " +
            " order by orgao, unidade, funcao, subfuncao, programa, projetoatividade,subprojetoatividade, naturezadespesa, fonte, cdest";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("origem", origemSuplementacaoORC.name());
        List resultList = consulta.getResultList();
        return criarDotacaoFechamentoExercicio(selecionado, resultList, tipo);
    }

    public List<DotacaoFechamentoExercicio> getCreditoAdicionalPorTipo(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades, TipoDespesaORC tipoDespesaORC, TipoDotacaoFechamentoExercicio tipo) {
        String sql = "select orgao, unidade, funcao, subfuncao, programa, projetoatividade, subprojetoatividade, naturezadespesa, fonte, cdest, sum(valor), extensaofonterecurso_id from ( " +
            "SELECT vworgao.id AS orgao, " +
            "  vw.id           AS unidade, " +
            "  fu.id           AS funcao, " +
            "  subfu.id        AS subfuncao, " +
            "  prg.id          AS programa, " +
            "  acao.id         AS projetoatividade, " +
            "  sub.id          AS subprojetoatividade, " +
            "  c.id            AS naturezadespesa, " +
            "  fonte.id        AS fonte, " +
            "  cd.id           AS cdest, " +
            "  SUM(su.valor)   AS valor," +
            "  pfonte.extensaofonterecurso_id " +
            " FROM alteracaoorc alt " +
            " INNER JOIN suplementacaoorc su ON alt.id = su.alteracaoorc_id " +
            " INNER JOIN fontedespesaorc fo ON su.fontedespesaorc_id = fo.id " +
            " INNER JOIN provisaoppafonte pfonte ON fo.provisaoppafonte_id = pfonte.id " +
            " INNER JOIN contadedestinacao cd ON pfonte.destinacaoderecursos_id = cd.id " +
            " INNER JOIN fontederecursos fonte ON cd.fontederecursos_id = fonte.id " +
            " INNER JOIN despesaorc desp ON fo.despesaorc_id = desp.id " +
            " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.id " +
            " INNER JOIN vwhierarquiaorcamentaria vw ON prov.unidadeorganizacional_id = vw.subordinada_id " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN subacaoppa sub ON prov.subacaoppa_id = sub.id " +
            " INNER JOIN acaoppa acao ON sub.acaoppa_id = acao.id " +
            " INNER JOIN tipoacaoppa tipo ON acao.tipoacaoppa_id = tipo.id " +
            " INNER JOIN programappa prg ON acao.programa_id = prg.id " +
            " INNER JOIN subfuncao subfu ON acao.subfuncao_id = subfu.id " +
            " INNER JOIN funcao fu ON acao.funcao_id = fu.id " +
            " INNER JOIN conta c ON prov.contadedespesa_id = c.id " +
            " WHERE trunc(alt.dataefetivacao) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " AND su.tipodespesaorc = :tipoDespesa " +
            " AND alt.status        in ('DEFERIDO','ESTORNADA') " +
            getUnidades(listaUnidades) +
            " GROUP BY vworgao.id, vw.id, fu.id, subfu.id, prg.id, acao.id, sub.id, c.id, fonte.id, cd.id, pfonte.extensaofonterecurso_id " +
            "  " +
            " union all " +
            "  " +
            " SELECT vworgao.id AS orgao, " +
            "   vw.id           AS unidade, " +
            "   fu.id           AS funcao, " +
            "   subfu.id        AS subfuncao, " +
            "   prg.id          AS programa, " +
            "   acao.id         AS projetoatividade, " +
            "   sub.id          AS subprojetoatividade, " +
            "   c.id            AS naturezadespesa, " +
            "   fonte.id        AS fonte, " +
            "   cd.id           AS cdest, " +
            "   SUM(su.valor*-1)   AS valor," +
            "   pfonte.extensaofonterecurso_id " +
            " FROM estornoalteracaoorc alt " +
            " INNER JOIN estornosuplementacaoorc est ON alt.id = est.estornoalteracaoorc_id " +
            " inner join suplementacaoorc su on est.suplementacaoorc_id = su.id  " +
            " INNER JOIN fontedespesaorc fo ON su.fontedespesaorc_id = fo.id " +
            " INNER JOIN provisaoppafonte pfonte ON fo.provisaoppafonte_id = pfonte.id " +
            " INNER JOIN contadedestinacao cd ON pfonte.destinacaoderecursos_id = cd.id " +
            " INNER JOIN fontederecursos fonte ON cd.fontederecursos_id = fonte.id " +
            " INNER JOIN despesaorc desp ON fo.despesaorc_id = desp.id " +
            " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.id " +
            " INNER JOIN vwhierarquiaorcamentaria vw ON prov.unidadeorganizacional_id = vw.subordinada_id " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN subacaoppa sub ON prov.subacaoppa_id = sub.id " +
            " INNER JOIN acaoppa acao ON sub.acaoppa_id = acao.id " +
            " INNER JOIN tipoacaoppa tipo ON acao.tipoacaoppa_id = tipo.id " +
            " INNER JOIN programappa prg ON acao.programa_id = prg.id " +
            " INNER JOIN subfuncao subfu ON acao.subfuncao_id = subfu.id " +
            " INNER JOIN funcao fu ON acao.funcao_id = fu.id " +
            " INNER JOIN conta c ON prov.contadedespesa_id = c.id " +
            " WHERE trunc(alt.dataestorno) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " AND su.tipodespesaorc = :tipoDespesa  " +
            getUnidades(listaUnidades) +
            " GROUP BY vworgao.id, vw.id, fu.id, subfu.id, prg.id, acao.id, sub.id, c.id, fonte.id, cd.id, pfonte.extensaofonterecurso_id " +
            " ) " +
            " having  sum(valor) <> 0 " +
            " group by orgao, unidade, funcao, subfuncao, programa, projetoatividade, subprojetoatividade, naturezadespesa, fonte, cdest, extensaofonterecurso_id " +
            " ORDER BY orgao, unidade, funcao, subfuncao, programa, projetoatividade, subprojetoatividade, naturezadespesa, fonte, cdest ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("tipoDespesa", tipoDespesaORC.name());
        List resultList = consulta.getResultList();
        return criarDotacaoFechamentoExercicio(selecionado, resultList, tipo);
    }

    public List<DotacaoFechamentoExercicio> getCreditoDisponivel(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {
        String sql = "select   " +
            " orgao,unidade,funcao, subfuncao,programa, projetoatividade,subprojetoatividade,naturezadespesa,fonte, cdest,sum(valor), extensaoFonte  " +
            " from (  " +
            " select   " +
            " vworgao.id as orgao,   " +
            " vw.id as unidade,   " +
            " fu.id as funcao,  " +
            " subfu.id as subfuncao,  " +
            " prg.id as programa,   " +
            " acao.id as projetoatividade,   " +
            " sub.id as subprojetoatividade,  " +
            " c.id as naturezadespesa,  " +
            " fonte.id as fonte,  " +
            " e.contaDeDestinacao_id as cdest,  " +
            " sum(e.valor*-1)  as valor," +
            " e.extensaofonterecurso_id as extensaoFonte " +
            " from empenho e  " +
            " inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))   " +
            " inner join vwhierarquiaorcamentaria vworgao on vw.superior_id = vworgao.subordinada_id  " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vworgao.iniciovigencia and coalesce(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))  " +
            " inner join exercicio ex on e.exercicio_id = ex.id   " +
            " inner join fontedespesaorc fdesp on e.fontedespesaorc_id = fdesp.id  " +
            " inner join fontederecursos fonte on e.fontederecursos_id = fonte.id  " +
            " inner join despesaorc desp on e.despesaorc_id = desp.id  " +
            " inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id  " +
            " INNER JOIN PROVISAOPPAFONTE pf on fdesp.PROVISAOPPAFONTE_ID = pf.id " +
            " inner join conta c on e.contadespesa_id = c.id  " +
            " inner join subacaoppa sub on prov.subacaoppa_id = sub.id  " +
            " inner join acaoppa acao on sub.acaoppa_id = acao.id  " +
            " inner join tipoacaoppa tipo on acao.tipoacaoppa_id = tipo.id  " +
            " inner join programappa prg on acao.programa_id = prg.id  " +
            " inner join subfuncao subfu on acao.subfuncao_id = subfu.id  " +
            " inner join funcao fu on acao.funcao_id = fu.id  " +
            " where e.categoriaorcamentaria = 'NORMAL'  " +
            " and trunc(e.dataempenho) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            getUnidades(listaUnidades) +
            "  group by vworgao.id,vw.id,fu.id,subfu.id,prg.id,acao.id, sub.id,c.id, fonte.id, e.contaDeDestinacao_id, e.extensaofonterecurso_id  " +
            " union all  " +
            "   " +
            " select   " +
            " vworgao.id as orgao,   " +
            " vw.id as unidade,   " +
            " fu.id as funcao,  " +
            " subfu.id as subfuncao,  " +
            " prg.id as programa,   " +
            " acao.id as projetoatividade,   " +
            " sub.id as subprojetoatividade,  " +
            " c.id as naturezadespesa,  " +
            " fonte.id as fonte,  " +
            " e.contaDeDestinacao_id as cdest,  " +
            " sum(est.valor) as valor," +
            " e.extensaofonterecurso_id as extensaoFonte  " +
            " from empenhoestorno est  " +
            " inner join empenho e on est.empenho_id = e.id  " +
            " inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))   " +
            " inner join vwhierarquiaorcamentaria vworgao on vw.superior_id = vworgao.subordinada_id  " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vworgao.iniciovigencia and coalesce(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))  " +
            " inner join exercicio ex on e.exercicio_id = ex.id   " +
            " inner join fontedespesaorc fdesp on e.fontedespesaorc_id = fdesp.id  " +
            " inner join fontederecursos fonte on e.fontederecursos_id = fonte.id  " +
            " inner join despesaorc desp on e.despesaorc_id = desp.id  " +
            " inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id  " +
            " INNER JOIN PROVISAOPPAFONTE pf on fdesp.PROVISAOPPAFONTE_ID = pf.id" +
            " inner join conta c on e.contadespesa_id = c.id  " +
            " inner join subacaoppa sub on prov.subacaoppa_id = sub.id  " +
            " inner join acaoppa acao on sub.acaoppa_id = acao.id  " +
            " inner join tipoacaoppa tipo on acao.tipoacaoppa_id = tipo.id  " +
            " inner join programappa prg on acao.programa_id = prg.id  " +
            " inner join subfuncao subfu on acao.subfuncao_id = subfu.id  " +
            " inner join funcao fu on acao.funcao_id = fu.id  " +
            " where e.categoriaorcamentaria = 'NORMAL'  " +
            " and est.categoriaorcamentaria = 'NORMAL'  " +
            " and trunc(est.dataestorno) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            getUnidades(listaUnidades) +
            "  group by vworgao.id,vw.id,fu.id,subfu.id,prg.id,acao.id, sub.id,c.id, fonte.id, e.contaDeDestinacao_id, e.extensaofonterecurso_id  " +
            " union all   " +
            "   " +
            " select   " +
            " vworgao.id as orgao,   " +
            " vw.id as unidade,   " +
            " fu.id as funcao,  " +
            " subfu.id as subfuncao,  " +
            " prg.id as programa,   " +
            " acao.id as projetoatividade,   " +
            " sub.id as subprojetoatividade,  " +
            " c.id as naturezadespesa,  " +
            " fonte.id as fonte,  " +
            " cd.id as cdest,  " +
            " sum(pfon.valor) as valor," +
            " pfon.extensaofonterecurso_id as extensaoFonte  " +
            " from provisaoppadespesa prov  " +
            " inner join subacaoppa sub on prov.subacaoppa_id = sub.id  " +
            " inner join vwhierarquiaorcamentaria vw on prov.unidadeorganizacional_id = vw.subordinada_id  " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))   " +
            " inner join vwhierarquiaorcamentaria vworgao on vw.superior_id = vworgao.subordinada_id  " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vworgao.iniciovigencia and coalesce(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))  " +
            " inner join exercicio ex on sub.exercicio_id = ex.id   " +
            " inner join provisaoppafonte pfon on prov.id = pfon.provisaoppadespesa_id  " +
            " inner join contadedestinacao cd on pfon.destinacaoderecursos_id = cd.id  " +
            " inner join fontederecursos fonte on cd.fontederecursos_id = fonte.id  " +
            " inner join conta c on prov.contadedespesa_id = c.id  " +
            " inner join acaoppa acao on sub.acaoppa_id = acao.id  " +
            " inner join tipoacaoppa tipo on acao.tipoacaoppa_id = tipo.id  " +
            " inner join programappa prg on acao.programa_id = prg.id  " +
            " inner join subfuncao subfu on acao.subfuncao_id = subfu.id  " +
            " inner join funcao fu on acao.funcao_id = fu.id  " +
            " where ex.id = :exe  " +
            getUnidades(listaUnidades) +
            "  group by vworgao.id,vw.id,fu.id,subfu.id,prg.id,acao.id, sub.id,c.id, fonte.id, cd.id, pfon.extensaofonterecurso_id  " +
            " union all  " +
            "   " +
            " select   " +
            " vworgao.id as orgao,   " +
            " vw.id as unidade,   " +
            " fu.id as funcao,  " +
            " subfu.id as subfuncao,  " +
            " prg.id as programa,   " +
            " acao.id as projetoatividade,   " +
            " sub.id as subprojetoatividade,  " +
            " c.id as naturezadespesa,  " +
            " fonte.id as fonte,  " +
            " cd.id as cdest,  " +
            " sum(su.valor) as valor," +
            " pfonte.extensaofonterecurso_id as extensaoFonte  " +
            " from alteracaoorc alt  " +
            " inner join suplementacaoorc su on alt.id = su.alteracaoorc_id  " +
            " inner join fontedespesaorc fo on su.fontedespesaorc_id = fo.id  " +
            " inner join provisaoppafonte pfonte on fo.provisaoppafonte_id = pfonte.id  " +
            " inner join contadedestinacao cd on pfonte.destinacaoderecursos_id = cd.id  " +
            " inner join fontederecursos fonte on cd.fontederecursos_id = fonte.id  " +
            " inner join despesaorc desp on fo.despesaorc_id = desp.id  " +
            " inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id  " +
            " inner join vwhierarquiaorcamentaria vw on prov.unidadeorganizacional_id = vw.subordinada_id " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))  " +
            " inner join vwhierarquiaorcamentaria vworgao on vw.superior_id = vworgao.subordinada_id " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vworgao.iniciovigencia and coalesce(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " inner join subacaoppa sub on prov.subacaoppa_id = sub.id  " +
            " inner join acaoppa acao on sub.acaoppa_id = acao.id  " +
            " inner join tipoacaoppa tipo on acao.tipoacaoppa_id = tipo.id  " +
            " inner join programappa prg on acao.programa_id = prg.id  " +
            " inner join subfuncao subfu on acao.subfuncao_id = subfu.id  " +
            " inner join funcao fu on acao.funcao_id = fu.id  " +
            " inner join conta c on prov.contadedespesa_id = c.id  " +
            " where trunc(alt.dataefetivacao) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " and alt.status in ('DEFERIDO','ESTORNADA')" +
            getUnidades(listaUnidades) +
            "  group by vworgao.id,vw.id,fu.id,subfu.id,prg.id,acao.id, sub.id,c.id, fonte.id, cd.id, pfonte.extensaofonterecurso_id  " +
            " union all  " +
            "   " +
            " select   " +
            " vworgao.id as orgao,   " +
            " vw.id as unidade,   " +
            " fu.id as funcao,  " +
            " subfu.id as subfuncao,  " +
            " prg.id as programa,   " +
            " acao.id as projetoatividade,   " +
            " sub.id as subprojetoatividade,  " +
            " c.id as naturezadespesa,  " +
            " fonte.id as fonte,  " +
            " cd.id as cdest,  " +
            " sum(anul.valor *-1) as valor," +
            " pfonte.extensaofonterecurso_id as extensaoFonte  " +
            " from alteracaoorc alt  " +
            " inner join anulacaoorc anul on alt.id = anul.alteracaoorc_id  " +
            " inner join fontedespesaorc fo on anul.fontedespesaorc_id = fo.id  " +
            " inner join provisaoppafonte pfonte on fo.provisaoppafonte_id = pfonte.id  " +
            " inner join contadedestinacao cd on pfonte.destinacaoderecursos_id = cd.id  " +
            " inner join fontederecursos fonte on cd.fontederecursos_id = fonte.id  " +
            " inner join despesaorc desp on fo.despesaorc_id = desp.id  " +
            " inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id  " +
            " inner join vwhierarquiaorcamentaria vw on prov.unidadeorganizacional_id = vw.subordinada_id " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))  " +
            " inner join vwhierarquiaorcamentaria vworgao on vw.superior_id = vworgao.subordinada_id " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vworgao.iniciovigencia and coalesce(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " inner join subacaoppa sub on prov.subacaoppa_id = sub.id  " +
            " inner join acaoppa acao on sub.acaoppa_id = acao.id  " +
            " inner join tipoacaoppa tipo on acao.tipoacaoppa_id = tipo.id  " +
            " inner join programappa prg on acao.programa_id = prg.id  " +
            " inner join subfuncao subfu on acao.subfuncao_id = subfu.id  " +
            " inner join funcao fu on acao.funcao_id = fu.id  " +
            " inner join conta c on prov.contadedespesa_id = c.id  " +
            " where trunc(alt.dataefetivacao) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " and alt.status in ('DEFERIDO','ESTORNADA')" +
            getUnidades(listaUnidades) +
            "  group by vworgao.id,vw.id,fu.id,subfu.id,prg.id,acao.id, sub.id,c.id, fonte.id, cd.id, pfonte.extensaofonterecurso_id  " +
            " union all  " +
            "   " +
            " select   " +
            " vworgao.id as orgao,   " +
            " vw.id as unidade,   " +
            " fu.id as funcao,  " +
            " subfu.id as subfuncao,  " +
            " prg.id as programa,   " +
            " acao.id as projetoatividade,   " +
            " sub.id as subprojetoatividade,  " +
            " c.id as naturezadespesa,  " +
            " fonte.id as fonte,  " +
            " cd.id as cdest,  " +
            " sum(est.valor) as valor," +
            " pfonte.extensaofonterecurso_id as extensaoFonte  " +
            " from EstornoAlteracaoOrc alt  " +
            " inner join estornoanulacaoorc est on alt.id = est.estornoalteracaoorc_id  " +
            " inner join anulacaoorc su on est.anulacaoorc_id = su.id" +
            " inner join fontedespesaorc fo on su.fontedespesaorc_id = fo.id  " +
            " inner join provisaoppafonte pfonte on fo.provisaoppafonte_id = pfonte.id  " +
            " inner join contadedestinacao cd on pfonte.destinacaoderecursos_id = cd.id  " +
            " inner join fontederecursos fonte on cd.fontederecursos_id = fonte.id  " +
            " inner join despesaorc desp on fo.despesaorc_id = desp.id  " +
            " inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id  " +
            " inner join vwhierarquiaorcamentaria vw on prov.unidadeorganizacional_id = vw.subordinada_id " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))  " +
            " inner join vwhierarquiaorcamentaria vworgao on vw.superior_id = vworgao.subordinada_id " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vworgao.iniciovigencia and coalesce(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " inner join subacaoppa sub on prov.subacaoppa_id = sub.id  " +
            " inner join acaoppa acao on sub.acaoppa_id = acao.id  " +
            " inner join tipoacaoppa tipo on acao.tipoacaoppa_id = tipo.id  " +
            " inner join programappa prg on acao.programa_id = prg.id  " +
            " inner join subfuncao subfu on acao.subfuncao_id = subfu.id  " +
            " inner join funcao fu on acao.funcao_id = fu.id  " +
            " inner join conta c on prov.contadedespesa_id = c.id  " +
            " where trunc(alt.dataestorno) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            getUnidades(listaUnidades) +
            "  group by vworgao.id,vw.id,fu.id,subfu.id,prg.id,acao.id, sub.id,c.id, fonte.id, cd.id, pfonte.extensaofonterecurso_id  " +
            " union all  " +
            "   " +
            " select   " +
            " vworgao.id as orgao,   " +
            " vw.id as unidade,   " +
            " fu.id as funcao,  " +
            " subfu.id as subfuncao,  " +
            " prg.id as programa,   " +
            " acao.id as projetoatividade,   " +
            " sub.id as subprojetoatividade,  " +
            " c.id as naturezadespesa,  " +
            " fonte.id as fonte,  " +
            " cd.id as cdest,  " +
            " sum(est.valor *-1) as valor," +
            " pfonte.extensaofonterecurso_id as extensaoFonte  " +
            " from EstornoAlteracaoOrc alt  " +
            " inner join EstornoSuplementacaoOrc est on alt.id = est.estornoalteracaoorc_id  " +
            " inner join suplementacaoorc su on est.suplementacaoorc_id = su.id" +
            " inner join fontedespesaorc fo on su.fontedespesaorc_id = fo.id  " +
            " inner join provisaoppafonte pfonte on fo.provisaoppafonte_id = pfonte.id  " +
            " inner join contadedestinacao cd on pfonte.destinacaoderecursos_id = cd.id  " +
            " inner join fontederecursos fonte on cd.fontederecursos_id = fonte.id  " +
            " inner join despesaorc desp on fo.despesaorc_id = desp.id  " +
            " inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id  " +
            " inner join vwhierarquiaorcamentaria vw on prov.unidadeorganizacional_id = vw.subordinada_id " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))  " +
            " inner join vwhierarquiaorcamentaria vworgao on vw.superior_id = vworgao.subordinada_id " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vworgao.iniciovigencia and coalesce(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " inner join subacaoppa sub on prov.subacaoppa_id = sub.id  " +
            " inner join acaoppa acao on sub.acaoppa_id = acao.id  " +
            " inner join tipoacaoppa tipo on acao.tipoacaoppa_id = tipo.id  " +
            " inner join programappa prg on acao.programa_id = prg.id  " +
            " inner join subfuncao subfu on acao.subfuncao_id = subfu.id  " +
            " inner join funcao fu on acao.funcao_id = fu.id  " +
            " inner join conta c on prov.contadedespesa_id = c.id  " +
            " where trunc(alt.dataestorno) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            getUnidades(listaUnidades) +
            "  group by vworgao.id,vw.id,fu.id,subfu.id,prg.id,acao.id, sub.id,c.id, fonte.id, cd.id,  pfonte.extensaofonterecurso_id  " +
            " )  " +
            " group by orgao,unidade,funcao, subfuncao,programa, projetoatividade,subprojetoatividade,naturezadespesa,fonte, cdest, extensaoFonte  " +
            " having  sum(valor) <> 0 " +
            " order by orgao,unidade,funcao, subfuncao,programa, projetoatividade,subprojetoatividade,naturezadespesa,fonte, cdest ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("exe", selecionado.getExercicio().getId());
        List resultList = consulta.getResultList();
        return criarDotacaoFechamentoExercicio(selecionado, resultList, TipoDotacaoFechamentoExercicio.DOTACAO);
    }

    private List<DotacaoFechamentoExercicio> criarDotacaoFechamentoExercicio(AberturaFechamentoExercicio selecionado, List resultList, TipoDotacaoFechamentoExercicio tipo) {
        List<DotacaoFechamentoExercicio> retorno = Lists.newArrayList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;

            HierarquiaOrganizacional orgao = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[0]).longValue());
            HierarquiaOrganizacional unidade = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
            Funcao funcao = em.find(Funcao.class, ((BigDecimal) objeto[2]).longValue());
            SubFuncao subFuncao = em.find(SubFuncao.class, ((BigDecimal) objeto[3]).longValue());
            ProgramaPPA programaPPA = em.find(ProgramaPPA.class, ((BigDecimal) objeto[4]).longValue());
            AcaoPPA acaoPPA = em.find(AcaoPPA.class, ((BigDecimal) objeto[5]).longValue());
            SubAcaoPPA subAcaoPPA = em.find(SubAcaoPPA.class, ((BigDecimal) objeto[6]).longValue());
            Conta conta = em.find(Conta.class, ((BigDecimal) objeto[7]).longValue());
            FonteDeRecursos fonteDeRecursos = em.find(FonteDeRecursos.class, ((BigDecimal) objeto[8]).longValue());
            ContaDeDestinacao contaDeDestinacao = em.find(ContaDeDestinacao.class, ((BigDecimal) objeto[9]).longValue());
            ExtensaoFonteRecurso extensaoFonteRecurso = em.find(ExtensaoFonteRecurso.class, ((BigDecimal) objeto[11]).longValue());
            BigDecimal valor = (BigDecimal) objeto[10];
            retorno.add(new DotacaoFechamentoExercicio(orgao, unidade, funcao, subFuncao, programaPPA, acaoPPA, subAcaoPPA, conta, fonteDeRecursos, contaDeDestinacao, valor, selecionado, tipo, extensaoFonteRecurso));
        }
        return retorno;
    }

    private List<DespesaFechamentoExercicio> criarDespesaFechamentoExercicio(AberturaFechamentoExercicio selecionado, List resultList, TipoDespesaFechamentoExercicio tipo) {
        List<DespesaFechamentoExercicio> retorno = Lists.newArrayList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;

            HierarquiaOrganizacional orgao = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[0]).longValue());
            HierarquiaOrganizacional unidade = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
            Funcao funcao = em.find(Funcao.class, ((BigDecimal) objeto[2]).longValue());
            SubFuncao subFuncao = em.find(SubFuncao.class, ((BigDecimal) objeto[3]).longValue());
            ProgramaPPA programaPPA = em.find(ProgramaPPA.class, ((BigDecimal) objeto[4]).longValue());
            AcaoPPA acaoPPA = em.find(AcaoPPA.class, ((BigDecimal) objeto[5]).longValue());
            SubAcaoPPA subAcaoPPA = em.find(SubAcaoPPA.class, ((BigDecimal) objeto[6]).longValue());
            Conta conta = em.find(Conta.class, ((BigDecimal) objeto[7]).longValue());
            FonteDeRecursos fonteDeRecursos = em.find(FonteDeRecursos.class, ((BigDecimal) objeto[8]).longValue());
            ContaDeDestinacao contaDeDestinacao = em.find(ContaDeDestinacao.class, ((BigDecimal) objeto[9]).longValue());
            Empenho empenho = em.find(Empenho.class, ((BigDecimal) objeto[10]).longValue());
            BigDecimal valor = (BigDecimal) objeto[11];
            retorno.add(new DespesaFechamentoExercicio(orgao, unidade, funcao, subFuncao, programaPPA, acaoPPA, subAcaoPPA, conta, fonteDeRecursos, contaDeDestinacao, selecionado, empenho, valor, tipo));
        }
        return retorno;
    }

    private List<TransporteObrigacaoAPagar> criarTransporteObrigacaoAPagar(AberturaFechamentoExercicio selecionado, List resultList) {
        List<TransporteObrigacaoAPagar> retorno = Lists.newArrayList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;

            HierarquiaOrganizacional orgao = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[0]).longValue());
            HierarquiaOrganizacional unidade = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
            Funcao funcao = em.find(Funcao.class, ((BigDecimal) objeto[2]).longValue());
            SubFuncao subFuncao = em.find(SubFuncao.class, ((BigDecimal) objeto[3]).longValue());
            ProgramaPPA programaPPA = em.find(ProgramaPPA.class, ((BigDecimal) objeto[4]).longValue());
            AcaoPPA acaoPPA = em.find(AcaoPPA.class, ((BigDecimal) objeto[5]).longValue());
            SubAcaoPPA subAcaoPPA = em.find(SubAcaoPPA.class, ((BigDecimal) objeto[6]).longValue());
            Conta conta = em.find(Conta.class, ((BigDecimal) objeto[7]).longValue());
            FonteDeRecursos fonteDeRecursos = em.find(FonteDeRecursos.class, ((BigDecimal) objeto[8]).longValue());
            ObrigacaoAPagar obrigacaoAPagar = em.find(ObrigacaoAPagar.class, ((BigDecimal) objeto[9]).longValue());
            BigDecimal valor = (BigDecimal) objeto[10];
            ContaDeDestinacao contaDeDestinacao = em.find(ContaDeDestinacao.class, ((BigDecimal) objeto[11]).longValue());
            retorno.add(new TransporteObrigacaoAPagar(orgao, unidade, funcao, subFuncao, programaPPA, acaoPPA, subAcaoPPA, conta, fonteDeRecursos, selecionado, obrigacaoAPagar, valor, contaDeDestinacao));
        }
        return retorno;
    }

    public List<DespesaFechamentoExercicio> getEmpenhosALiquidarRestosNaoProcessados(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {
        String sql = "select  " +
            " vworgao.id as orgao, " +
            " vw.id as unidade,  " +
            " fu.id as funcao,    " +
            " subfu.id as subfuncao,   " +
            " prg.id as programa,  " +
            " acao.id as projetoatividade,  " +
            " sub.id as subprojetoatividade,    " +
            " c.id as naturezadespesa,    " +
            " fonte.id as fonte,    " +
            " cdest.id as cdest,    " +
            " e.id as empenho,  " +
            " sum(dados.valor) as saldo  " +
            " from ( " +
            "   SELECT E.id, sum(coalesce(e.valor,0)) as valor  " +
            "   FROM EMPENHO E " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.id " +
            " " +
            " union all " +
            " " +
            "   SELECT  E.id, sum(coalesce(ee.valor,0)) * -1 as valor  " +
            "   FROM EMPENHO E " +
            "   inner join empenhoestorno ee on ee.empenho_id = e.id and trunc(ee.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.id " +
            " " +
            " union all " +
            " " +
            "   SELECT E.id, sum(coalesce(l.valor,0)) * -1 as valor  " +
            "   FROM EMPENHO E " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.id " +
            "    " +
            " union all " +
            " " +
            "   SELECT E.id, sum(coalesce(le.valor,0)) as valor  " +
            "   FROM EMPENHO E " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join liquidacaoestorno le on le.liquidacao_id = l.id and trunc(le.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.id " +
            " " +
            ") dados " +
            " inner join empenho e on dados.id = e.id " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON E.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID  " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))   " +
            " inner join vwhierarquiaorcamentaria vworgao on vw.superior_id = vworgao.subordinada_id   " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vworgao.iniciovigencia and coalesce(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))   " +
            " inner join fontederecursos fonte on e.fontederecursos_id = fonte.id   " +
            " inner join fontedespesaorc fo on e.fontedespesaorc_id = fo.id   " +
            " inner join despesaorc desp on fo.despesaorc_id = desp.id   " +
            " inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id   " +
            " inner join subacaoppa sub on prov.subacaoppa_id = sub.id   " +
            " inner join acaoppa acao on sub.acaoppa_id = acao.id   " +
            " inner join tipoacaoppa tipo on acao.tipoacaoppa_id = tipo.id   " +
            " inner join programappa prg on acao.programa_id = prg.id   " +
            " inner join subfuncao subfu on acao.subfuncao_id = subfu.id   " +
            " inner join funcao fu on acao.funcao_id = fu.id   " +
            " inner join conta c on e.contadespesa_id = c.id   " +
            " inner join contaDeDestinacao cdest on e.contaDeDestinacao_id = cdest.id   " +
            " group by vworgao.id,vw.id,fu.id,subfu.id,prg.id,acao.id, sub.id,c.id, fonte.id,e.id, cdest.id " +
            " having SUM(dados.VALOR) > 0";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("EXE", selecionado.getExercicio().getId());
        return criarDespesaFechamentoExercicio(selecionado, consulta.getResultList(), TipoDespesaFechamentoExercicio.EMPENHO_A_LIQUIDAR_NAO_PROCESSADOS);
    }

    public List<TransporteObrigacaoAPagar> getObrigacoesAPagar(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> unidades) {
        String sql = "select   " +
            " vworgao.id as orgao,  " +
            " vw.id as unidade,   " +
            " fu.id as funcao,     " +
            " subfu.id as subfuncao,    " +
            " prg.id as programa,   " +
            " acao.id as projetoatividade,   " +
            " sub.id as subprojetoatividade,     " +
            " c.ID as naturezadespesa,     " +
            " fonte.id as fonte,     " +
            " dados.id as obrigacaoAPagar,   " +
            " dados.valor as saldo, " +
            " obr.CONTADEDESTINACAO_ID as contaDeDestinacao   " +
            " from (  " +
            "select obr.id, coalesce(obr.valor, 0) as valor " +
            "  from obrigacaoapagar obr " +
            " where obr.saldoempenho > 0 " +
            "   AND obr.EXERCICIO_ID = :EXE " +
            "   and trunc(obr.datalancamento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')    " +
            "union all " +
            "select obr.id, coalesce(obr.valor, 0) as valor " +
            "  from obrigacaoapagar obr " +
            "where obr.id in ( " +
            "select desdObr.OBRIGACAOAPAGAR_ID from (  " +
            "   SELECT E.id, sum(coalesce(e.valor,0)) as valor   " +
            "   FROM EMPENHO E  " +
            "   WHERE  " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL'  " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')     " +
            "   group by E.id, E.DATAEMPENHO  " +
            "  " +
            "union all  " +
            "  " +
            "   SELECT  E.id, sum(coalesce(ee.valor,0)) * -1 as valor   " +
            "   FROM EMPENHO E  " +
            "   inner join empenhoestorno ee on ee.empenho_id = e.id and trunc(ee.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')  " +
            "   WHERE  " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL'  " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')     " +
            "   group by E.id, E.DATAEMPENHO  " +
            "  " +
            "union all  " +
            "  " +
            "   SELECT E.id, sum(coalesce(l.valor,0)) * -1 as valor   " +
            "   FROM EMPENHO E  " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')  " +
            "   WHERE  " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL'  " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')     " +
            "   group by E.id, E.DATAEMPENHO  " +
            "     " +
            "union all  " +
            "  " +
            "   SELECT E.id, sum(coalesce(le.valor,0)) as valor   " +
            "   FROM EMPENHO E  " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')  " +
            "   inner join liquidacaoestorno le on le.liquidacao_id = l.id and trunc(le.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')  " +
            "   WHERE  " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL'  " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')     " +
            "   group by E.id  " +
            ")  emp " +
            "inner join DESDOBRAMENTOEMPENHO desdEmp on desdEmp.EMPENHO_ID = emp.id " +
            "inner join DESDOBRAMENTOOBRIGACAOPAGA desdObr on desdEmp.DESDOBRAMENTOOBRIGACAOPAGAR_ID = desdObr.id " +
            "group by desdObr.OBRIGACAOAPAGAR_ID  " +
            "having SUM(emp.VALOR) <> 0 ) " +
            " " +
            "union all " +
            " " +
            "select obr.id, coalesce(obr.valor, 0) as valor " +
            "  from obrigacaoapagar obr " +
            "   " +
            "where obr.id in ( " +
            "select desdObr.OBRIGACAOAPAGAR_ID from (  " +
            "   SELECT  E.ID, sum(coalesce(l.valor,0)) as valor   " +
            "   FROM EMPENHO E  " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')  " +
            "   WHERE  " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL'  " +
            "   AND E.EXERCICIO_ID = :EXE  " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')     " +
            "   group by E.ID  " +
            "     " +
            "union all  " +
            "  " +
            "   SELECT E.ID, sum(coalesce(le.valor,0)) * -1 as valor   " +
            "   FROM EMPENHO E  " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')  " +
            "   inner join liquidacaoestorno le on le.liquidacao_id = l.id and trunc(le.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')  " +
            "   WHERE  " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL'  " +
            "   AND E.EXERCICIO_ID = :EXE  " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')     " +
            "   group by E.ID " +
            "     " +
            "union all  " +
            "  " +
            "   SELECT E.ID, sum(coalesce(p.valor,0)) * -1 as valor   " +
            "   FROM EMPENHO E  " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')  " +
            "   inner join pagamento p on p.liquidacao_id = l.id and trunc(p.datapagamento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')  " +
            "   WHERE  " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL'  " +
            "   AND E.EXERCICIO_ID = :EXE  " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')     " +
            "   group by E.ID  " +
            "   " +
            " union all  " +
            "   " +
            "   SELECT E.ID, sum(coalesce(pe.valor,0)) as valor   " +
            "   FROM EMPENHO E  " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')  " +
            "   inner join pagamento p on p.liquidacao_id = l.id and trunc(p.datapagamento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')  " +
            "   inner join pagamentoestorno pe on pe.pagamento_id = p.id and trunc(pe.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')  " +
            "   WHERE  " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL'  " +
            "   AND E.EXERCICIO_ID = :EXE  " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')     " +
            "   group by E.ID  " +
            ")  emp " +
            "inner join DESDOBRAMENTOEMPENHO desdEmp on desdEmp.EMPENHO_ID = emp.id " +
            "inner join DESDOBRAMENTOOBRIGACAOPAGA desdObr on desdEmp.DESDOBRAMENTOOBRIGACAOPAGAR_ID = desdObr.id " +
            "group by desdObr.OBRIGACAOAPAGAR_ID  " +
            "having SUM(emp.VALOR) > 0) " +
            " ) dados " +
            " inner join obrigacaoapagar obr on dados.id = obr.id " +
            "INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON obr.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID   " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))    " +
            getUnidades(unidades) +
            " inner join vwhierarquiaorcamentaria vworgao on vw.superior_id = vworgao.subordinada_id    " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vworgao.iniciovigencia and coalesce(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))    " +
            " inner join fontederecursos fonte on obr.fontederecursos_id = fonte.id    " +
            " inner join fontedespesaorc fo on obr.fontedespesaorc_id = fo.id    " +
            " inner join despesaorc desp on fo.despesaorc_id = desp.id    " +
            " inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id    " +
            " inner join subacaoppa sub on prov.subacaoppa_id = sub.id    " +
            " inner join acaoppa acao on sub.acaoppa_id = acao.id    " +
            " inner join tipoacaoppa tipo on acao.tipoacaoppa_id = tipo.id    " +
            " inner join programappa prg on acao.programa_id = prg.id    " +
            " inner join subfuncao subfu on acao.subfuncao_id = subfu.id    " +
            " inner join funcao fu on acao.funcao_id = fu.id    " +
            " inner join conta c on obr.contadespesa_id = c.id " +
            " group by vworgao.id, vw.id, fu.id, subfu.id, prg.id, acao.id, sub.id, fonte.id, dados.id , c.ID, dados.valor, obr.CONTADEDESTINACAO_ID  ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("EXE", selecionado.getExercicio().getId());
        return criarTransporteObrigacaoAPagar(selecionado, consulta.getResultList());
    }

    public List<DespesaFechamentoExercicio> getEmpenhosLiquidadosRestosProcessados(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {
        String sql = "select " +
            "vworgao.id as orgao, " +
            "vw.id as unidade, " +
            "fu.id as funcao,   " +
            "subfu.id as subfuncao,  " +
            "prg.id as programa, " +
            "acao.id as projetoatividade, " +
            "sub.id as subprojetoatividade,   " +
            "c.id as naturezadespesa,   " +
            "fonte.id as fonte,   " +
            " e.CONTADEDESTINACAO_ID as contaDeDestinacao,  " +
            "e.id as empenho, " +
            "sum(dados.valor) as saldo " +
            "from ( " +
            " " +
            "   SELECT  E.id, sum(coalesce(l.valor,0)) as valor " +
            "   FROM EMPENHO E " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')   " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.id" +
            "   " +
            "union all " +
            " " +
            "   SELECT E.id, sum(coalesce(le.valor,0)) * -1 as valor " +
            "   FROM EMPENHO E " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join liquidacaoestorno le on le.liquidacao_id = l.id and trunc(le.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')   " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.id " +
            "   " +
            "union all " +
            " " +
            "   SELECT E.id, sum(coalesce(p.valor,0)) * -1 as valor " +
            "   FROM EMPENHO E " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join pagamento p on p.liquidacao_id = l.id and trunc(p.datapagamento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')   " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.id " +
            " " +
            " union all " +
            " " +
            "   SELECT E.id, sum(coalesce(pe.valor,0)) as valor " +
            "   FROM EMPENHO E " +
            "   inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and trunc(l.dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join pagamento p on p.liquidacao_id = l.id and trunc(p.datapagamento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join pagamentoestorno pe on pe.pagamento_id = p.id and trunc(pe.dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "   inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id " +
            "   WHERE " +
            "   E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            "   AND E.EXERCICIO_ID = :EXE " +
            "   and trunc(e.dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataReferencia, 'dd/MM/yyyy')   " +
            getUnidades(listaUnidades) +
            "   and trunc(e.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(e.dataempenho)) " +
            "   group by E.id " +
            ") dados " +
            " inner join empenho e on dados.id = e.id " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON E.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))  " +
            " inner join vwhierarquiaorcamentaria vworgao on vw.superior_id = vworgao.subordinada_id  " +
            "        and to_date(:dataReferencia,'dd/mm/yyyy') between vworgao.iniciovigencia and coalesce(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy'))  " +
            " inner join fontederecursos fonte on e.fontederecursos_id = fonte.id  " +
            " inner join fontedespesaorc fo on e.fontedespesaorc_id = fo.id  " +
            " inner join despesaorc desp on fo.despesaorc_id = desp.id  " +
            " inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id  " +
            " inner join subacaoppa sub on prov.subacaoppa_id = sub.id  " +
            " inner join acaoppa acao on sub.acaoppa_id = acao.id  " +
            " inner join tipoacaoppa tipo on acao.tipoacaoppa_id = tipo.id  " +
            " inner join programappa prg on acao.programa_id = prg.id  " +
            " inner join subfuncao subfu on acao.subfuncao_id = subfu.id  " +
            " inner join funcao fu on acao.funcao_id = fu.id  " +
            " inner join conta c on e.contadespesa_id = c.id  " +
            " group by vworgao.id,vw.id,fu.id,subfu.id,prg.id,acao.id, sub.id,c.id, fonte.id, e.CONTADEDESTINACAO_ID, e.id " +
            " having SUM(dados.VALOR) > 0 ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("EXE", selecionado.getExercicio().getId());
        return criarDespesaFechamentoExercicio(selecionado, consulta.getResultList(), TipoDespesaFechamentoExercicio.EMPENHO_A_LIQUIDAR_PROCESSADOS);
    }

    public List<DespesaFechamentoExercicio> getEmpenhosCreditosEmpenhoPago(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades) {
        String sql = "select orgao,unidade,funcao, subfuncao,programa, projetoatividade,subprojetoatividade,naturezadespesa,fonte, contaDeDestinacao, empenho, sum(valor)  from ( " +
            "SELECT vworgao.id AS orgao, " +
            "  vw.id           AS unidade, " +
            "  fu.id           AS funcao, " +
            "  subfu.id        AS subfuncao, " +
            "  prg.id          AS programa, " +
            "  acao.id         AS projetoatividade, " +
            "  sub.id          AS subprojetoatividade, " +
            "  c.id            AS naturezadespesa, " +
            "  fonte.id        AS fonte, " +
            "  e.CONTADEDESTINACAO_ID as contaDeDestinacao,  " +
            "  e.id            AS empenho, " +
            "  SUM(pag.valor)    AS valor " +
            " FROM EMPENHO E " +
            " inner join liquidacao l on e.id = l.empenho_id " +
            " inner join pagamento pag on l.id = pag.liquidacao_id " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON E.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN fontederecursos fonte ON e.fontederecursos_id = fonte.id " +
            " INNER JOIN fontedespesaorc fo ON e.fontedespesaorc_id = fo.id " +
            " INNER JOIN despesaorc desp ON fo.despesaorc_id = desp.id " +
            " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.id " +
            " INNER JOIN subacaoppa sub ON prov.subacaoppa_id = sub.id " +
            " INNER JOIN acaoppa acao ON sub.acaoppa_id = acao.id " +
            " INNER JOIN tipoacaoppa tipo ON acao.tipoacaoppa_id = tipo.id " +
            " INNER JOIN programappa prg ON acao.programa_id = prg.id " +
            " INNER JOIN subfuncao subfu ON acao.subfuncao_id = subfu.id " +
            " INNER JOIN funcao fu ON acao.funcao_id = fu.id " +
            " INNER JOIN conta c ON e.contadespesa_id     = c.id " +
            " WHERE E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            " AND trunc(pag.datapagamento) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " AND E.EXERCICIO_ID = :EXE " +
            getUnidades(listaUnidades) +
            " GROUP BY vworgao.id,  vw.id,  fu.id,  subfu.id,  prg.id,  acao.id,  sub.id,  c.id,  fonte.id, e.CONTADEDESTINACAO_ID, e.id " +
            " union all " +
            " SELECT vworgao.id AS orgao, " +
            "  vw.id           AS unidade, " +
            "  fu.id           AS funcao, " +
            "  subfu.id        AS subfuncao, " +
            "  prg.id          AS programa, " +
            "  acao.id         AS projetoatividade, " +
            "  sub.id          AS subprojetoatividade, " +
            "  c.id            AS naturezadespesa, " +
            "  fonte.id        AS fonte, " +
            "  e.CONTADEDESTINACAO_ID as contaDeDestinacao,  " +
            "  e.id            AS empenho, " +
            "  SUM(est.valor*-1)    AS valor " +
            " FROM EMPENHO E " +
            " inner join liquidacao l on e.id = l.empenho_id " +
            " inner join pagamento pag on l.id = pag.liquidacao_id " +
            " inner join pagamentoestorno est on pag.id = est.pagamento_id " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON E.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            " AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN fontederecursos fonte ON e.fontederecursos_id = fonte.id " +
            " INNER JOIN fontedespesaorc fo ON e.fontedespesaorc_id = fo.id " +
            " INNER JOIN despesaorc desp ON fo.despesaorc_id = desp.id " +
            " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.id " +
            " INNER JOIN subacaoppa sub ON prov.subacaoppa_id = sub.id " +
            " INNER JOIN acaoppa acao ON sub.acaoppa_id = acao.id " +
            " INNER JOIN tipoacaoppa tipo ON acao.tipoacaoppa_id = tipo.id " +
            " INNER JOIN programappa prg ON acao.programa_id = prg.id " +
            " INNER JOIN subfuncao subfu ON acao.subfuncao_id = subfu.id " +
            " INNER JOIN funcao fu ON acao.funcao_id = fu.id " +
            " INNER JOIN conta c ON e.contadespesa_id     = c.id " +
            " WHERE E.CATEGORIAORCAMENTARIA = 'NORMAL' " +
            " AND trunc(est.dataestorno) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " AND E.EXERCICIO_ID = :EXE " +
            getUnidades(listaUnidades) +
            " GROUP BY vworgao.id,  vw.id,  fu.id,  subfu.id,  prg.id,  acao.id,  sub.id,  c.id,  fonte.id,  e.id, e.CONTADEDESTINACAO_ID " +
            " ) " +
            " group by orgao,unidade,funcao, subfuncao,programa, projetoatividade,subprojetoatividade,naturezadespesa,fonte, contaDeDestinacao, empenho " +
            " order by orgao,unidade,funcao, subfuncao,programa, projetoatividade,subprojetoatividade,naturezadespesa,fonte, contaDeDestinacao, empenho ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("EXE", selecionado.getExercicio().getId());
        return criarDespesaFechamentoExercicio(selecionado, consulta.getResultList(), TipoDespesaFechamentoExercicio.CREDITO_EMPENHADO_PAGO);
    }

    public List<DespesaFechamentoExercicio> getTotalPagoDosRestosAPagar(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaUnidades, TipoRestosProcessado tipoRestosProcessado) {
        String sql = "select orgao, unidade, funcao, subfuncao, programa, projetoatividade, subprojetoatividade, naturezadespesa, FONTE, contaDeDestinacao,  empenho, sum(valor) as valor from ( " +
            " SELECT vworgao.id AS orgao, " +
            "  vw.id           AS unidade, " +
            "  fu.id           AS funcao, " +
            "  subfu.id        AS subfuncao, " +
            "  prg.id          AS programa, " +
            "  acao.id         AS projetoatividade, " +
            "  sub.id          AS subprojetoatividade, " +
            "  c.id            AS naturezadespesa, " +
            "  fonte.id        AS fonte, " +
            "  e.CONTADEDESTINACAO_ID as contaDeDestinacao,  " +
            "  e.id            AS empenho, " +
            "  SUM(P.valor)    AS valor " +
            " FROM EMPENHO E " +
            " INNER JOIN LIQUIDACAO L ON E.ID = L.EMPENHO_ID " +
            " INNER JOIN PAGAMENTO P ON L.ID = P.LIQUIDACAO_ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON E.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            "        AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            "        AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN fontederecursos fonte ON e.fontederecursos_id = fonte.id " +
            " INNER JOIN fontedespesaorc fo ON e.fontedespesaorc_id = fo.id " +
            " INNER JOIN despesaorc desp ON fo.despesaorc_id = desp.id " +
            " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.id " +
            " INNER JOIN subacaoppa sub ON prov.subacaoppa_id = sub.id " +
            " INNER JOIN acaoppa acao ON sub.acaoppa_id = acao.id " +
            " INNER JOIN tipoacaoppa tipo ON acao.tipoacaoppa_id = tipo.id " +
            " INNER JOIN programappa prg ON acao.programa_id = prg.id " +
            " INNER JOIN subfuncao subfu ON acao.subfuncao_id = subfu.id " +
            " INNER JOIN funcao fu ON acao.funcao_id = fu.id " +
            " inner join conta c on e.contadespesa_id = c.id  " +
            " WHERE E.CATEGORIAORCAMENTARIA = 'RESTO' " +
            " AND E.TIPORESTOSPROCESSADOS = :tipo  " +
            " AND trunc(p.datapagamento) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " AND E.EXERCICIO_ID = :EXE " +
            getUnidades(listaUnidades) +
            " GROUP BY vworgao.id,  vw.id,  fu.id,  subfu.id,  prg.id,  acao.id,  sub.id,  c.id,  fonte.id, e.CONTADEDESTINACAO_ID, e.id" +
            " " +
            "union all" +
            " " +
            "SELECT vworgao.id AS orgao, " +
            "  vw.id           AS unidade, " +
            "  fu.id           AS funcao, " +
            "  subfu.id        AS subfuncao, " +
            "  prg.id          AS programa, " +
            "  acao.id         AS projetoatividade, " +
            "  sub.id          AS subprojetoatividade, " +
            "  c.id            AS naturezadespesa, " +
            "  fonte.id        AS fonte, " +
            "  e.CONTADEDESTINACAO_ID as contaDeDestinacao,  " +
            "  e.id            AS empenho, " +
            "  SUM(PE.valor)*-1    AS valor " +
            " FROM EMPENHO E " +
            " INNER JOIN LIQUIDACAO L ON E.ID = L.EMPENHO_ID " +
            " INNER JOIN PAGAMENTO P ON L.ID = P.LIQUIDACAO_ID " +
            " INNER JOIN PAGAMENTOESTORNO PE ON P.ID = PE.PAGAMENTO_ID" +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON E.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            "        AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            "        AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN fontederecursos fonte ON e.fontederecursos_id = fonte.id " +
            " INNER JOIN fontedespesaorc fo ON e.fontedespesaorc_id = fo.id " +
            " INNER JOIN despesaorc desp ON fo.despesaorc_id = desp.id " +
            " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.id " +
            " INNER JOIN subacaoppa sub ON prov.subacaoppa_id = sub.id " +
            " INNER JOIN acaoppa acao ON sub.acaoppa_id = acao.id " +
            " INNER JOIN tipoacaoppa tipo ON acao.tipoacaoppa_id = tipo.id " +
            " INNER JOIN programappa prg ON acao.programa_id = prg.id " +
            " INNER JOIN subfuncao subfu ON acao.subfuncao_id = subfu.id " +
            " INNER JOIN funcao fu ON acao.funcao_id = fu.id " +
            " inner join conta c on e.contadespesa_id = c.id  " +
            " WHERE E.CATEGORIAORCAMENTARIA = 'RESTO' " +
            " AND E.TIPORESTOSPROCESSADOS = :tipo  " +
            " AND trunc(pe.dataestorno) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            " AND E.EXERCICIO_ID = :EXE " +
            getUnidades(listaUnidades) +
            " GROUP BY vworgao.id,  vw.id,  fu.id,  subfu.id,  prg.id,  acao.id,  sub.id,  c.id,  fonte.id,  e.CONTADEDESTINACAO_ID, e.id " +
            " ) group by  orgao, unidade, funcao, subfuncao, programa, projetoatividade, subprojetoatividade, naturezadespesa, FONTE, contaDeDestinacao, empenho";
        Query consulta = em.createNativeQuery(sql);

        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("EXE", selecionado.getExercicio().getId());
        consulta.setParameter("tipo", tipoRestosProcessado.name());
        return criarDespesaFechamentoExercicio(selecionado, consulta.getResultList(), tipoRestosProcessado.equals(TipoRestosProcessado.PROCESSADOS) ? TipoDespesaFechamentoExercicio.PAGO_RESTO_PAGAR_PROCESSADO : TipoDespesaFechamentoExercicio.PAGO_RESTO_PAGAR_NAO_PROCESSADO);
    }

    public List<DespesaFechamentoExercicio> getTotalCanceladosProcessadosDoExercicioAnterior(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaDeUnidades) {
        String sql = "SELECT vworgao.id AS orgao, " +
            "   vw.id           AS unidade, " +
            "   fu.id           AS funcao, " +
            "   subfu.id        AS subfuncao, " +
            "   prg.id          AS programa, " +
            "   acao.id         AS projetoatividade, " +
            "   sub.id          AS subprojetoatividade, " +
            "   c.id            AS naturezadespesa, " +
            "   fonte.id        AS fonte, " +
            "   e.CONTADEDESTINACAO_ID as contaDeDestinacao,  " +
            "   e.id            AS empenho, " +
            "   SUM(EST.VALOR)    AS valor " +
            " FROM EMPENHOESTORNO EST " +
            " INNER JOIN EMPENHO E ON E.ID = EST.EMPENHO_ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON E.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            "        AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            "        AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN fontederecursos fonte ON e.fontederecursos_id = fonte.id " +
            " INNER JOIN fontedespesaorc fo ON e.fontedespesaorc_id = fo.id " +
            " INNER JOIN despesaorc desp ON fo.despesaorc_id = desp.id " +
            " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.id " +
            " INNER JOIN subacaoppa sub ON prov.subacaoppa_id = sub.id " +
            " INNER JOIN acaoppa acao ON sub.acaoppa_id = acao.id " +
            " INNER JOIN tipoacaoppa tipo ON acao.tipoacaoppa_id = tipo.id " +
            " INNER JOIN programappa prg ON acao.programa_id = prg.id " +
            " INNER JOIN subfuncao subfu ON acao.subfuncao_id = subfu.id " +
            " INNER JOIN funcao fu ON acao.funcao_id = fu.id " +
            " inner join conta c on e.contadespesa_id = c.id  " +
            " WHERE EST.CATEGORIAORCAMENTARIA = 'RESTO' " +
            " AND E.TIPORESTOSPROCESSADOS = 'PROCESSADOS'  " +
            " AND trunc(EST.dataestorno) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            getUnidades(listaDeUnidades) +
            " GROUP BY vworgao.id,  vw.id,  fu.id,  subfu.id,  prg.id,  acao.id,  sub.id,  c.id,  fonte.id, e.CONTADEDESTINACAO_ID, e.id";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + selecionado.getExercicio().getAno());
        return criarDespesaFechamentoExercicio(selecionado, consulta.getResultList(), TipoDespesaFechamentoExercicio.CANCELADO_RESTO_PAGAR_PROCESSADO);
    }

    public List<DespesaFechamentoExercicio> getTotalCanceladosNaoProcessadosDoExercicioAnterior(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaDeUnidades) {
        String sql = "SELECT vworgao.id AS orgao, " +
            "   vw.id           AS unidade, " +
            "   fu.id           AS funcao, " +
            "   subfu.id        AS subfuncao, " +
            "   prg.id          AS programa, " +
            "   acao.id         AS projetoatividade, " +
            "   sub.id          AS subprojetoatividade, " +
            "   c.id            AS naturezadespesa, " +
            "   fonte.id        AS fonte, " +
            "   e.CONTADEDESTINACAO_ID as contaDeDestinacao,  " +
            "   e.id            AS empenho, " +
            "   SUM(EST.VALOR)    AS valor " +
            " FROM EMPENHO E " +
            " INNER JOIN EMPENHOESTORNO EST ON E.ID = EST.EMPENHO_ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON E.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            "        AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            "        AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            " INNER JOIN fontederecursos fonte ON e.fontederecursos_id = fonte.id " +
            " INNER JOIN fontedespesaorc fo ON e.fontedespesaorc_id = fo.id " +
            " INNER JOIN despesaorc desp ON fo.despesaorc_id = desp.id " +
            " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.id " +
            " INNER JOIN subacaoppa sub ON prov.subacaoppa_id = sub.id " +
            " INNER JOIN acaoppa acao ON sub.acaoppa_id = acao.id " +
            " INNER JOIN tipoacaoppa tipo ON acao.tipoacaoppa_id = tipo.id " +
            " INNER JOIN programappa prg ON acao.programa_id = prg.id " +
            " INNER JOIN subfuncao subfu ON acao.subfuncao_id = subfu.id " +
            " INNER JOIN funcao fu ON acao.funcao_id = fu.id " +
            " inner join conta c on e.contadespesa_id = c.id  " +
            " WHERE EST.CATEGORIAORCAMENTARIA = 'RESTO' " +
            " AND E.TIPORESTOSPROCESSADOS = 'NAO_PROCESSADOS'  " +
            " AND trunc(EST.dataestorno) BETWEEN TO_DATE(:dataInicial,'dd/MM/yyyy') AND TO_DATE(:dataReferencia,'dd/MM/yyyy') " +
            getUnidades(listaDeUnidades) +
            " GROUP BY vworgao.id,  vw.id,  fu.id,  subfu.id,  prg.id,  acao.id,  sub.id,  c.id,  fonte.id, e.CONTADEDESTINACAO_ID, e.id";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + selecionado.getExercicio().getAno());
        return criarDespesaFechamentoExercicio(selecionado, consulta.getResultList(), TipoDespesaFechamentoExercicio.CANCELADO_RESTO_PAGAR_NAO_PROCESSADO);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<FonteDeRecursoFechamentoExercicio> getDisponibilidadePorDestinacaoDeRecurso(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaDeUnidades) {
        String sql = "SELECT orgao, unidade, id, CONTADEDESTINACAO_ID, COALESCE(SUM(valor), 0) AS valor " +
            "FROM " +
            "  (SELECT  vworgao.id AS orgao, vw.id as unidade, font.id as id, e.CONTADEDESTINACAO_ID, SUM(pag.valorfinal) AS valor " +
            "  FROM pagamento pag " +
            "  INNER JOIN liquidacao liq ON pag.liquidacao_id = liq.id " +
            "  INNER JOIN empenho e  ON liq.empenho_id = e.id " +
            "  INNER JOIN fontederecursos FONT  ON e.fontederecursos_id = FONT.id " +
            "  INNER JOIN exercicio ex  ON e.exercicio_id = ex.id " +
            "  INNER JOIN VWHIERARQUIAORCAMENTARIA VW  ON VW.SUBORDINADA_ID = pag.UNIDADEORGANIZACIONAL_ID " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            "  WHERE trunc(pag.datapagamento) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(pag.datapagamento)) " +
            "        AND trunc(pag.datapagamento) BETWEEN vworgao.INICIOVIGENCIA AND COALESCE(vworgao.FIMVIGENCIA, trunc(pag.datapagamento)) " +
            "        AND trunc(pag.datapagamento) BETWEEN to_date(:dataInicial,'dd/mm/yyyy') AND to_date(:dataReferencia,'dd/mm/yyyy') " +
            getUnidades(listaDeUnidades) +
            "        group by vworgao.id, vw.id,font.id, e.CONTADEDESTINACAO_ID " +
            "         " +
            "  UNION ALL " +
            "   " +
            "  SELECT vworgao.id AS orgao, vw.id as unidade, font.id as id, e.CONTADEDESTINACAO_ID ,SUM(est.valorfinal) * - 1 AS valor " +
            "  FROM pagamentoestorno est " +
            "  INNER JOIN pagamento pag  ON est.pagamento_id = pag.id " +
            "  INNER JOIN liquidacao liq  ON liq.id = pag.liquidacao_id " +
            "  INNER JOIN empenho e  ON liq.empenho_id = e.id " +
            "  INNER JOIN fontederecursos FONT  ON e.fontederecursos_id = FONT.id " +
            "  INNER JOIN exercicio ex  ON e.exercicio_id = ex.id " +
            "  INNER JOIN VWHIERARQUIAORCAMENTARIA VW  ON VW.SUBORDINADA_ID = est.UNIDADEORGANIZACIONAL_ID " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            "  WHERE trunc(est.dataestorno) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(est.dataestorno)) " +
            "         AND trunc(est.dataestorno) BETWEEN vworgao.INICIOVIGENCIA AND COALESCE(vworgao.FIMVIGENCIA, trunc(est.dataestorno)) " +
            "         AND trunc(est.dataestorno) BETWEEN to_date(:dataInicial,'dd/mm/yyyy') AND to_date(:dataReferencia,'dd/mm/yyyy') " +
            getUnidades(listaDeUnidades) +
            "        group by vworgao.id, vw.id,font.id, e.CONTADEDESTINACAO_ID  " +
            "          " +
            "  UNION ALL " +
            "   " +
            "  SELECT vworgao.id AS orgao, vw.id as unidade, font.id as id, pg.CONTADEDESTINACAO_ID,SUM(COALESCE(pg.valor,0)) AS VALOR " +
            "  FROM pagamentoextra pg " +
            "  INNER JOIN fontederecursos FONT  ON pg.fontederecursos_id = FONT.id " +
            "  INNER JOIN contaextraorcamentaria ce  ON pg.contaextraorcamentaria_id = ce.id " +
            "  INNER JOIN vwhierarquiaorcamentaria VW  ON pg.unidadeorganizacional_id = vw.subordinada_id " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            "  WHERE ce.tipocontaextraorcamentaria = 'DEPOSITOS_CONSIGNACOES' " +
            "  AND FONT.exercicio_id               = :exe " +
            "  AND trunc(pg.DATAPAGTO) BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia, trunc(pg.DATAPAGTO)) " +
            "  AND trunc(pg.DATAPAGTO) BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia, trunc(pg.DATAPAGTO)) " +
            "  AND trunc(pg.datapagto) BETWEEN to_date(:dataInicial,'dd/mm/yyyy') AND to_date(:dataReferencia,'dd/mm/yyyy') " +
            getUnidades(listaDeUnidades) +
            "        group by vworgao.id, vw.id,font.id, pg.CONTADEDESTINACAO_ID " +
            "   " +
            "  UNION ALL " +
            "   " +
            "  SELECT vworgao.id AS orgao, vw.id as unidade, font.id as id, re.CONTADEDESTINACAO_ID,SUM(COALESCE(est.valor,0)) * - 1 AS VALOR " +
            "  FROM pagamentoextraestorno est " +
            "  INNER JOIN pagamentoextra re  ON est.pagamentoextra_id = re.id " +
            "  INNER JOIN vwhierarquiaorcamentaria vw  ON re.unidadeorganizacional_id = vw.subordinada_id " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            "  INNER JOIN contaextraorcamentaria ce  ON re.contaextraorcamentaria_id = ce.id " +
            "  INNER JOIN fontederecursos FONT  ON re.fontederecursos_id = FONT.id " +
            "  WHERE FONT.exercicio_id           = :exe " +
            "  AND ce.tipocontaextraorcamentaria = 'DEPOSITOS_CONSIGNACOES' " +
            "  AND trunc(est.dataestorno) BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia, trunc(est.dataestorno)) " +
            "  AND trunc(est.dataestorno) BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia, trunc(est.dataestorno)) " +
            "  AND trunc(est.dataestorno) BETWEEN to_date(:dataInicial,'dd/mm/yyyy') AND to_date(:dataReferencia,'dd/mm/yyyy') " +
            getUnidades(listaDeUnidades) +
            "        group by vworgao.id, vw.id,font.id, re.CONTADEDESTINACAO_ID  " +
            "   " +
            "  UNION ALL " +
            "   " +
            "  SELECT vworgao.id AS orgao, vw.id as unidade, font.id as id, pg.CONTADEDESTINACAO_ID,SUM(COALESCE(pg.valor,0)) AS VALOR " +
            "  FROM pagamentoextra pg " +
            "  INNER JOIN fontederecursos FONT  ON pg.fontederecursos_id = FONT.id " +
            "  INNER JOIN contaextraorcamentaria ce  ON pg.contaextraorcamentaria_id = ce.id " +
            "  INNER JOIN vwhierarquiaorcamentaria VW  ON pg.unidadeorganizacional_id       = vw.subordinada_id " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            "  WHERE ce.tipocontaextraorcamentaria <> 'DEPOSITOS_CONSIGNACOES' " +
            "  AND FONT.exercicio_id                = :exe " +
            "  AND trunc(pg.DATAPAGTO) BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia, trunc(pg.DATAPAGTO)) " +
            "  AND trunc(pg.DATAPAGTO) BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia, trunc(pg.DATAPAGTO)) " +
            getUnidades(listaDeUnidades) +
            "        group by vworgao.id, vw.id,font.id, pg.CONTADEDESTINACAO_ID  " +
            "   " +
            "  UNION ALL " +
            "   " +
            "  SELECT vworgao.id AS orgao, vw.id as unidade, font.id as id, re.CONTADEDESTINACAO_ID,SUM(COALESCE(est.valor,0)) * - 1 AS VALOR " +
            "  FROM pagamentoextraestorno est " +
            "  INNER JOIN pagamentoextra re  ON est.pagamentoextra_id = re.id " +
            "  INNER JOIN vwhierarquiaorcamentaria vw  ON re.unidadeorganizacional_id = vw.subordinada_id " +
            " INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            "  INNER JOIN contaextraorcamentaria ce  ON re.contaextraorcamentaria_id = ce.id " +
            "  INNER JOIN fontederecursos FONT  ON re.fontederecursos_id = FONT.id " +
            "  WHERE FONT.exercicio_id            = :exe " +
            "  AND ce.tipocontaextraorcamentaria <> 'DEPOSITOS_CONSIGNACOES' " +
            "  AND trunc(est.dataestorno) BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia, trunc(est.dataestorno)) " +
            "  AND trunc(est.dataestorno) BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia, trunc(est.dataestorno)) " +
            "  AND trunc(est.dataestorno) BETWEEN to_date(:dataInicial,'dd/mm/yyyy') AND to_date(:dataReferencia,'dd/mm/yyyy') " +
            getUnidades(listaDeUnidades) +
            "        group by vworgao.id, vw.id,font.id, re.CONTADEDESTINACAO_ID  " +
            "   " +
            "  UNION ALL " +
            "   " +
            " select vworgao.id AS orgao, vw.id as unidade, font.id as id, cd.id, SUM(COALESCE(lanc.valor, 0)) AS VALOR " +
            " from LANCAMENTORECEITAORC lanc " +
            "         inner join LancReceitaFonte lrf on lanc.ID = lrf.LANCRECEITAORC_ID " +
            "         inner join receitaloafonte rlf on lrf.RECEITALOAFONTE_ID = rlf.ID " +
            "         inner join contadedestinacao cd on rlf.DESTINACAODERECURSOS_ID = cd.id " +
            "         INNER JOIN fontederecursos FONT ON cd.fontederecursos_id = FONT.id " +
            "         INNER JOIN vwhierarquiaorcamentaria vw ON lanc.unidadeorganizacional_id = vw.subordinada_id " +
            "         INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            "         inner join eventocontabil eve on lrf.EVENTOCONTABIL_ID = eve.ID " +
            " WHERE trunc(lanc.DATALANCAMENTO) BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia, trunc(lanc.DATALANCAMENTO)) " +
            "  and trunc(lanc.DATALANCAMENTO) BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia, trunc(lanc.DATALANCAMENTO)) " +
            "  AND trunc(lanc.DATALANCAMENTO) BETWEEN to_date(:dataInicial, 'dd/mm/yyyy') AND to_date(:dataReferencia, 'dd/mm/yyyy') " +
            getUnidades(listaDeUnidades) +
            "  and eve.CODIGO = :eventoNormalDeducao " +
            "  and lanc.OPERACAORECEITAREALIZADA = :operacaoDeducaoFundeb " +
            "        group by vworgao.id, vw.id,font.id, cd.id  " +
            "   " +
            "  UNION ALL " +
            "   " +
            " select vworgao.id AS orgao, vw.id as unidade, font.id as id, cd.id, SUM(COALESCE(lanc.valor, 0)) * -1 AS VALOR" +
            " from RECEITAORCESTORNO lanc" +
            "         inner join RECEITAORCFONTEESTORNO lrf on lanc.ID = lrf.RECEITAORCESTORNO_ID" +
            "         inner join receitaloafonte rlf on lrf.RECEITALOAFONTE_ID = rlf.ID" +
            "         inner join contadedestinacao cd on rlf.DESTINACAODERECURSOS_ID = cd.id" +
            "         INNER JOIN fontederecursos FONT ON cd.fontederecursos_id = FONT.id" +
            "         INNER JOIN vwhierarquiaorcamentaria vw ON lanc.UNIDADEORGANIZACIONALORC = vw.subordinada_id" +
            "         INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id" +
            "         inner join eventocontabil eve on lrf.EVENTOCONTABIL_ID = eve.ID" +
            " WHERE trunc(lanc.DATAESTORNO) BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia, trunc(lanc.DATAESTORNO))" +
            "  and trunc(lanc.DATAESTORNO) BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia, trunc(lanc.DATAESTORNO))" +
            "  AND trunc(lanc.DATAESTORNO) BETWEEN to_date(:dataInicial, 'dd/mm/yyyy') AND to_date(:dataReferencia, 'dd/mm/yyyy') " +
            getUnidades(listaDeUnidades) +
            "  and eve.CODIGO = :eventoEstornoDeducao " +
            "  and lanc.OPERACAORECEITAREALIZADA = :operacaoDeducaoFundeb " +
            "        group by vworgao.id, vw.id,font.id, cd.id  " +
            "  ) " +
            "  group by orgao, unidade, id, CONTADEDESTINACAO_ID " +
            "  order by orgao, unidade, id, CONTADEDESTINACAO_ID ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("exe", selecionado.getExercicio().getId());
        consulta.setParameter("operacaoDeducaoFundeb", OperacaoReceita.DEDUCAO_RECEITA_FUNDEB.name());
        consulta.setParameter("eventoNormalDeducao", "25111");
        consulta.setParameter("eventoEstornoDeducao", "26111");
        return criarFonteDeRecursosFechamentoExercicio(selecionado, consulta.getResultList());
    }

    private List<FonteDeRecursoFechamentoExercicio> criarFonteDeRecursosFechamentoExercicio(AberturaFechamentoExercicio selecionado, List resultList) {
        List<FonteDeRecursoFechamentoExercicio> retorno = Lists.newArrayList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            HierarquiaOrganizacional orgao = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[0]).longValue());
            HierarquiaOrganizacional unidade = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
            FonteDeRecursos fonteDeRecursos = em.find(FonteDeRecursos.class, ((BigDecimal) objeto[2]).longValue());
            ContaDeDestinacao contaDeDestinacao = em.find(ContaDeDestinacao.class, ((BigDecimal) objeto[3]).longValue());
            BigDecimal valor = (BigDecimal) objeto[4];

            retorno.add(new FonteDeRecursoFechamentoExercicio(orgao, unidade, fonteDeRecursos, contaDeDestinacao, valor, selecionado));
        }
        return retorno;
    }

    public List<ContaFechamentoExercicio> getResultadoDiminutivoOuAumentativoDoExercicio(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaDeUnidades, Conta contaContabil, TipoContaFechamentoExercicio tipo) {
        String sql = "select vworgao.id as orgao, " +
            " vw.id as unidade, " +
            " SALDOfinal.totalcredito - SALDOfinal.totaldebito as saldo, " +
            " c.id as conta " +
            " FROM CONTA C " +
            " inner join contacontabil cc on c.id = cc.id " +
            " INNER JOIN " +
            "    (SELECT scc.CONTACONTABIL_ID, " +
            "      scc.unidadeorganizacional_id, " +
            "      SUM(scc.totalcredito) AS totalcredito, " +
            "      SUM(scc.totaldebito)  AS totaldebito " +
            "    FROM SALDOCONTACONTABIL SCC " +
            "    INNER JOIN CONTA C    ON SCC.CONTACONTABIL_ID = C.ID " +
            "    INNER JOIN " +
            "      (SELECT MAX(trunc(DATASALDO)) AS maxdata,        unidadeorganizacional_id,        contacontabil_id, tipobalancete FROM SALDOCONTACONTABIL " +
            "        WHERE trunc(DATASALDO) between TO_DATE(:dataInicial, 'DD/MM/YYYY') and TO_DATE(:dataReferencia, 'DD/MM/YYYY') " +
            "        GROUP BY unidadeorganizacional_id,        contacontabil_id, tipobalancete " +
            "      ) datasaldo " +
            "    ON trunc(scc.datasaldo)                 = trunc(datasaldo.maxdata) " +
            "    and scc.tipobalancete                    = datasaldo.tipobalancete " +
            "    AND scc.UNIDADEORGANIZACIONAL_ID = datasaldo.unidadeorganizacional_id " +
            "    AND scc.contacontabil_id         = datasaldo.contacontabil_id " +
            "    where scc.tipobalancete in (:transporte, :mensal) " +
            "    GROUP BY scc.CONTACONTABIL_ID, scc.unidadeorganizacional_id " +
            "    ) SALDOFINAL ON SALDOFINAL.CONTACONTABIL_ID = C.ID " +
            "    inner join unidadeorganizacional uo on saldofinal.unidadeorganizacional_id = uo.id " +
            "    INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON uo.id = VW.SUBORDINADA_ID " +
            "    AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            "    INNER JOIN vwhierarquiaorcamentaria vworgao ON vw.superior_id = vworgao.subordinada_id " +
            "    AND to_date(:dataReferencia,'dd/mm/yyyy') BETWEEN vworgao.iniciovigencia AND COALESCE(vworgao.fimvigencia,to_date(:dataReferencia,'dd/mm/yyyy')) " +
            "    where c.codigo like :codigoConta" +
            getUnidades(listaDeUnidades);
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + DataUtil.getAno(selecionado.getDataGeracao()));
        consulta.setParameter("codigoConta", contaContabil.getCodigoSemZerosAoFinal() + "%");
        consulta.setParameter("transporte", TipoBalancete.TRANSPORTE.name());
        consulta.setParameter("mensal", TipoBalancete.MENSAL.name());
        return criarContaFechamentoExercicio(selecionado, consulta.getResultList(), tipo);
    }

    private List<ContaFechamentoExercicio> criarContaFechamentoExercicio(AberturaFechamentoExercicio selecionado, List resultList, TipoContaFechamentoExercicio tipo) {
        List<ContaFechamentoExercicio> retorno = new ArrayList<ContaFechamentoExercicio>();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            HierarquiaOrganizacional orgao = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[0]).longValue());
            HierarquiaOrganizacional unidade = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
            BigDecimal valor = (BigDecimal) objeto[2];
            Conta conta = em.find(Conta.class, ((BigDecimal) objeto[3]).longValue());

            retorno.add(new ContaFechamentoExercicio(orgao, unidade, conta, valor, selecionado, tipo));
        }
        return retorno;
    }

    public List<TransporteDeSaldoFechamentoExercicio> getContasTransporte(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaDeUnidades) {
        String sql = "SELECT  " +
            "orgao, " +
            "UNIDADE, " +
            "CASE  " +
            "WHEN (SUM(TOTALCREDITO) - SUM(TOTALDEBITO)) > 0 THEN (SUM(TOTALCREDITO) - SUM(TOTALDEBITO)) " +
            "ELSE 0 END AS TOTALCREDITO, " +
            "CASE " +
            "WHEN (SUM(TOTALCREDITO) - SUM(TOTALDEBITO)) < 0 THEN (SUM(TOTALCREDITO) - SUM(TOTALDEBITO)) * -1 " +
            "else 0 end as totaldebito, " +
            "conta " +
            "from ( " +
            "select  " +
            "VWORGAO.ID as orgao, " +
            "VW.id as unidade, " +
            "SALDOfinal.totalcredito, " +
            "SALDOfinal.totaldebito, " +
            "SALDOfinal.data, " +
            "SALDOFINAL.tipo,  " +
            "c.id as conta " +
            "FROM CONTA C " +
            "inner join contacontabil cc on c.id = cc.id " +
            "INNER JOIN " +
            "   (SELECT scc.CONTACONTABIL_ID, " +
            "     scc.unidadeorganizacional_id, " +
            "     SUM(scc.totalcredito) AS totalcredito, " +
            "     SUM(scc.totaldebito)  AS totaldebito, " +
            "     scc.tipobalancete as tipo, " +
            "     scc.datasaldo as data " +
            "   FROM SALDOCONTACONTABIL SCC " +
            "   INNER JOIN CONTA C    ON SCC.CONTACONTABIL_ID = C.ID " +
            "   INNER JOIN " +
            "     (SELECT trunc(MAX(DATASALDO)) AS maxdata,        unidadeorganizacional_id,        contacontabil_id,tipobalancete      FROM SALDOCONTACONTABIL " +
            "       WHERE trunc(DATASALDO) <= TO_DATE(:dataReferencia, 'DD/MM/YYYY') " +
            "       GROUP BY unidadeorganizacional_id,        contacontabil_id, tipobalancete " +
            "     ) datasaldo " +
            "   ON trunc(scc.datasaldo) = trunc(datasaldo.maxdata) " +
            "   AND scc.UNIDADEORGANIZACIONAL_ID = datasaldo.unidadeorganizacional_id " +
            "   AND scc.contacontabil_id         = datasaldo.contacontabil_id " +
            "   and scc.tipobalancete            = datasaldo.tipobalancete " +
            "   GROUP BY scc.CONTACONTABIL_ID, scc.unidadeorganizacional_id,scc.tipobalancete,scc.datasaldo " +
            "   ) SALDOFINAL ON SALDOFINAL.CONTACONTABIL_ID = C.ID " +
            "   INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON saldofinal.unidadeorganizacional_id = VW.SUBORDINADA_ID " +
            "   INNER JOIN VWHIERARQUIAORCAMENTARIA VWORGAO ON VW.SUPERIOR_ID = VWORGAO.SUBORDINADA_ID" +
            "   AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy')) " +
            "   AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VWORGAO.INICIOVIGENCIA AND COALESCE(VWORGAO.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy')) " +
            "   WHERE C.EXERCICIO_ID = :exercicio ";
        getUnidades(listaDeUnidades);
        sql += "   )   " +
            "   GROUP BY ORGAO ,UNIDADE, CONTA " +
            "   HAVING (SUM(TOTALCREDITO) - sum(totaldebito)) <> 0";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("exercicio", selecionado.getExercicio().getId());
        return criarTransporteDeSaldo(selecionado, consulta.getResultList());
    }

    private List<TransporteDeSaldoFechamentoExercicio> criarTransporteDeSaldo(AberturaFechamentoExercicio selecionado, List resultList) {
        List<TransporteDeSaldoFechamentoExercicio> retorno = new ArrayList<TransporteDeSaldoFechamentoExercicio>();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            HierarquiaOrganizacional orgao = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[0]).longValue());
            HierarquiaOrganizacional unidade = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
            BigDecimal credito = (BigDecimal) objeto[2];
            BigDecimal debito = (BigDecimal) objeto[3];
            Conta conta = em.find(Conta.class, ((BigDecimal) objeto[4]).longValue());

            retorno.add(new TransporteDeSaldoFechamentoExercicio(orgao, unidade, unidade.getSubordinada(), conta, credito, debito, selecionado));
        }
        return retorno;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @Asynchronous
    public Future<AberturaFechamentoExercicio> buscarContaAuxiliar(AssistenteAberturaFechamentoExercicio assistenteAberturaFechamentoExercicio) {
        this.assistenteAberturaFechamentoExercicio = assistenteAberturaFechamentoExercicio;

        AberturaFechamentoExercicio entity = assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio();
        BarraProgressoItens barraProgressoItens = assistenteAberturaFechamentoExercicio.getBarraProgressoItens();
        barraProgressoItens.inicializa();
        try {
            List<TransporteSaldoContaAuxiliarDetalhada> contasAuxiliaresTransporte = getContasAuxiliaresTransporte(assistenteAberturaFechamentoExercicio);
            assistenteAberturaFechamentoExercicio.setContasAuxiliaresTransporte(contasAuxiliaresTransporte);
        } catch (ValidacaoException ve) {
            for (FacesMessage facesMessage : ve.getAllMensagens()) {
                assistenteAberturaFechamentoExercicio.getMensagens().add(facesMessage.getDetail());
            }
            assistenteAberturaFechamentoExercicio.getBarraProgressoItens().finaliza();
            throw ve;
        } catch (Exception e) {
            logger.error("Erro na Abertura e Fechamento: ", e);
            assistenteAberturaFechamentoExercicio.getMensagens().add(e.getMessage());
            assistenteAberturaFechamentoExercicio.getBarraProgressoItens().finaliza();
            throw e;
        }
        assistenteAberturaFechamentoExercicio.getBarraProgressoItens().finaliza();
        return new AsyncResult<>(entity);
    }

    public List<TransporteSaldoContaAuxiliarDetalhada> getContasAuxiliaresTransporte(AssistenteAberturaFechamentoExercicio assistenteAberturaFechamentoExercicio) {
        assistenteAberturaFechamentoExercicio.getBarraProgressoItens().setMensagens("Buscando contas auxiliares");
        AberturaFechamentoExercicio selecionado = assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio();
        List<HierarquiaOrganizacional> listaDeUnidades = assistenteAberturaFechamentoExercicio.getListaDeUnidades();
        String sql = "SELECT" +
            "    contacontabil," +
            "    UNIDADE," +
            "    CASE" +
            "        WHEN (SUM(TOTALCREDITO) - SUM(TOTALDEBITO)) > 0 THEN (SUM(TOTALCREDITO) - SUM(TOTALDEBITO))" +
            "        ELSE 0 END AS TOTALCREDITO," +
            "    CASE" +
            "        WHEN (SUM(TOTALCREDITO) - SUM(TOTALDEBITO)) < 0 THEN (SUM(TOTALCREDITO) - SUM(TOTALDEBITO)) * -1" +
            "        else 0 end as totaldebito," +
            "    (select cax.id from conta c1 inner join contaauxiliardetalhada cax on cax.id =c1.id where c1.codigo = conta and cax.contacontabil_id = contacontabil and rownum = 1) as conta" +
            " from (" +
            "         select" +
            "             xx.id as contacontabil," +
            "             VW.id as unidade," +
            "             SALDOfinal.totalcredito," +
            "             SALDOfinal.totaldebito," +
            "             SALDOfinal.data," +
            "             SALDOFINAL.tipo," +
            "             c.codigo as conta             " +
            "         FROM CONTA C" +
            "                  inner join contaauxiliardetalhada cc on c.id = cc.id" +
            "                  inner join contacontabil x on cc.CONTACONTABIL_ID = x.id" +
            "                  inner join conta xx on x.id = xx.id" +
            "                  INNER JOIN" +
            "              (SELECT scc.CONTACONTABIL_ID," +
            "                      scc.unidadeorganizacional_id," +
            "                      SUM(scc.totalcredito) AS totalcredito," +
            "                      SUM(scc.totaldebito)  AS totaldebito," +
            "                      scc.tipobalancete as tipo," +
            "                      scc.datasaldo as data" +
            "               FROM SALDOCONTACONTABIL SCC" +
            "                        INNER JOIN CONTA C    ON SCC.CONTACONTABIL_ID = C.ID" +
            "                        INNER JOIN" +
            "                    (SELECT MAX(trunc(DATASALDO)) AS maxdata,        unidadeorganizacional_id,        contacontabil_id,tipobalancete      FROM SALDOCONTACONTABIL" +
            "                     WHERE trunc(DATASALDO) <= TO_DATE(:dataReferencia, 'DD/MM/YYYY')" +
            "                         and trunc(DATASALDO) >= TO_DATE(:dataInicial, 'DD/MM/YYYY')" +
            "                     GROUP BY unidadeorganizacional_id,        contacontabil_id, tipobalancete" +
            "                    ) datasaldo" +
            "                    ON trunc(scc.datasaldo) = trunc(datasaldo.maxdata)" +
            "                        AND scc.UNIDADEORGANIZACIONAL_ID = datasaldo.unidadeorganizacional_id" +
            "                        AND scc.contacontabil_id         = datasaldo.contacontabil_id" +
            "                        and scc.tipobalancete            = datasaldo.tipobalancete" +
            "               GROUP BY scc.CONTACONTABIL_ID, scc.unidadeorganizacional_id,scc.tipobalancete,scc.datasaldo" +
            "              ) SALDOFINAL ON SALDOFINAL.CONTACONTABIL_ID = C.ID" +
            "                  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON saldofinal.unidadeorganizacional_id = VW.SUBORDINADA_ID" +
            "             AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy'))" +
            "         WHERE C.EXERCICIO_ID = :exercicio ";
        sql += getUnidades(listaDeUnidades);
        sql += "   )   " +
            "   GROUP BY contacontabil ,UNIDADE, CONTA " +
            "   HAVING (SUM(TOTALCREDITO) - sum(totaldebito)) <> 0";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("dataInicial", "01/01/" + selecionado.getExercicio().getAno());
        consulta.setParameter("exercicio", selecionado.getExercicio().getId());
        return criarTransporteDeContaAuxiliar(assistenteAberturaFechamentoExercicio, consulta.getResultList());
    }


    private List<TransporteSaldoContaAuxiliarDetalhada> criarTransporteDeContaAuxiliar(AssistenteAberturaFechamentoExercicio assistenteAberturaFechamentoExercicio, List resultList) {
        AberturaFechamentoExercicio selecionado = assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio();
        assistenteAberturaFechamentoExercicio.getBarraProgressoItens().setMensagens("Preparando saldos de conta auxiliar para transporte");
        assistenteAberturaFechamentoExercicio.getBarraProgressoItens().setTotal(resultList.size());

        List<TransporteSaldoContaAuxiliarDetalhada> retorno = Lists.newArrayList();
        HashMap<String, TransporteSaldoContaAuxiliarDetalhada> contasAuxiliaresDetalhadas = Maps.newHashMap();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            HierarquiaOrganizacional unidade = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
            BigDecimal credito = (BigDecimal) objeto[2];
            BigDecimal debito = (BigDecimal) objeto[3];
            ContaContabil contaContabil = em.find(ContaContabil.class, ((BigDecimal) objeto[0]).longValue());
            ContaAuxiliarDetalhada conta = em.find(ContaAuxiliarDetalhada.class, ((BigDecimal) objeto[4]).longValue());
            String hashContaAuxiliar = montarHash(conta);
            TransporteSaldoContaAuxiliarDetalhada transporte;
            if (contasAuxiliaresDetalhadas.get(hashContaAuxiliar) == null) {
                TreeMap mapaContaAuxiliarDetalhada = montarContaAuxiliarDetalhadaNova(selecionado, conta, unidade.getSubordinada());
                TreeMap mapaContaAuxiliar = montarContaAuxiliarNova(selecionado, conta, unidade.getSubordinada());
                transporte = new TransporteSaldoContaAuxiliarDetalhada(contaContabil, unidade, unidade.getSubordinada(), conta, mapaContaAuxiliarDetalhada, mapaContaAuxiliar, credito, debito, selecionado);
            } else {
                transporte = contasAuxiliaresDetalhadas.get(hashContaAuxiliar);
                if (transporte.getDebito().compareTo(BigDecimal.ZERO) > 0) {
                    if (credito.compareTo(BigDecimal.ZERO) > 0) {
                        transporte.setDebito(transporte.getDebito().subtract(credito));
                    } else {
                        transporte.setDebito(transporte.getDebito().add(debito));
                    }
                    if (transporte.getDebito().compareTo(BigDecimal.ZERO) < 0) {
                        transporte.setCredito(transporte.getDebito().multiply(new BigDecimal("-1")));
                        transporte.setDebito(BigDecimal.ZERO);
                    }
                } else {
                    if (credito.compareTo(BigDecimal.ZERO) > 0) {
                        transporte.setCredito(transporte.getCredito().add(credito));
                    } else {
                        transporte.setCredito(transporte.getCredito().subtract(debito));
                    }
                    if (transporte.getCredito().compareTo(BigDecimal.ZERO) < 0) {
                        transporte.setDebito(transporte.getCredito().multiply(new BigDecimal("-1")));
                        transporte.setCredito(BigDecimal.ZERO);
                    }
                }
            }

            if (transporte.getCredito().subtract(transporte.getDebito()).compareTo(BigDecimal.ZERO) == 0) {
                contasAuxiliaresDetalhadas.remove(hashContaAuxiliar);
                retorno.remove(transporte);
            } else {
                contasAuxiliaresDetalhadas.put(hashContaAuxiliar, transporte);
                Util.adicionarObjetoEmLista(retorno, transporte);
            }
            assistenteAberturaFechamentoExercicio.getBarraProgressoItens().setProcessados(assistenteAberturaFechamentoExercicio.getBarraProgressoItens().getProcessados() + 1);
        }
        return retorno;
    }

    private String montarHash(ContaAuxiliarDetalhada cad) {
        return cad.getCodigo() +
            (cad.getUnidadeOrganizacional() != null ? cad.getUnidadeOrganizacional().getId() : "-") +
            (cad.getConta() != null ? cad.getConta().getId() : "-") +
            (cad.getContaContabil() != null ? cad.getContaContabil().getId() : "-") +
            (cad.getContaDeDestinacao() != null ? cad.getContaDeDestinacao().getId() : "-") +
            (cad.getExercicioOrigem() != null ? cad.getExercicioOrigem().getId() : "-") +
            cad.getSubSistema() +
            (cad.getEs() != null ? cad.getEs() : "-") +
            cad.getClassificacaoFuncional() +
            (cad.getCodigoCO() != null ? cad.getCodigoCO() : "-") +
            cad.getDividaConsolidada();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    private TreeMap montarContaAuxiliarNova(AberturaFechamentoExercicio selecionado, ContaAuxiliarDetalhada contaAuxiliar, UnidadeOrganizacional unidadeOrganizacional) {
        Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(selecionado.getExercicio().getAno() + 1);
        TreeMap mapaContaAuxiliar = new TreeMap();
        Conta contaDeDestinacao;
        Conta conta;
        Exercicio proximoExercicioDestinacao;
        String[] codigo = contaAuxiliar.getCodigo().split("\\.");
        switch (contaAuxiliar.getTipoContaAuxiliar().getCodigo()) {
            case "91":
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar1(unidadeOrganizacional));
                break;
            case "92":
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar2(unidadeOrganizacional,
                    getFinanceiroPermanente(codigo[2])));
                break;
            case "93":
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar3(unidadeOrganizacional,
                    getFinanceiroPermanente(codigo[2]),
                    Integer.valueOf(codigo[3])));
                break;
            case "94":
                contaDeDestinacao = contaAuxiliar.getContaDeDestinacao();
                proximoExercicioDestinacao = exercicioFacade.getExercicioPorAno(contaDeDestinacao.getExercicio().getAno() + 1);
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar4(unidadeOrganizacional,
                    getFinanceiroPermanente(codigo[2]),
                    buscarContaEquivalente(contaDeDestinacao, proximoExercicioDestinacao)));
                break;
            case "95":
                contaDeDestinacao = contaAuxiliar.getContaDeDestinacao();
                proximoExercicioDestinacao = exercicioFacade.getExercicioPorAno(contaDeDestinacao.getExercicio().getAno() + 1);
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar5(unidadeOrganizacional,
                    buscarContaEquivalente(contaDeDestinacao, proximoExercicioDestinacao)));
                break;
            case "96":
                conta = contaAuxiliar.getConta();
                Conta contaReceita = buscarContaEquivalente(conta, proximoExercicio);
                contaDeDestinacao = contaAuxiliar.getContaDeDestinacao();
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar6(unidadeOrganizacional,
                    buscarContaEquivalente(contaDeDestinacao, proximoExercicio),
                    (!Strings.isNullOrEmpty(contaReceita.getCodigoSICONFI()) ?
                        contaReceita.getCodigoSICONFI() :
                        contaReceita.getCodigo()).replace(".", "")));
                break;
            case "97":
                conta = contaAuxiliar.getConta();
                contaDeDestinacao = contaAuxiliar.getContaDeDestinacao();
                Conta contaDespesa = buscarContaEquivalente(conta, proximoExercicio);
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar7(unidadeOrganizacional,
                    codigo[2],
                    buscarContaEquivalente(contaDeDestinacao, proximoExercicio),
                    (contaDespesa.getCodigoSICONFI() != null ?
                        contaDespesa.getCodigoSICONFI() :
                        contaDespesa.getCodigo().replace(".", "")),
                    selecionado.getExercicio().getAno() <= 2021 ? Integer.valueOf(codigo[5]) : null));
                break;
            case "98":
                contaDeDestinacao = contaAuxiliar.getContaDeDestinacao();
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar8(unidadeOrganizacional,
                    getFinanceiroPermanente(codigo[2]),
                    Integer.valueOf(codigo[3]),
                    buscarContaEquivalente(contaDeDestinacao, proximoExercicio)));
                break;
            case "99":
                contaDeDestinacao = contaAuxiliar.getContaDeDestinacao();
                conta = contaAuxiliar.getConta();
                proximoExercicioDestinacao = exercicioFacade.getExercicioPorAno(contaDeDestinacao.getExercicio().getAno() + 1);
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar9(unidadeOrganizacional,
                    codigo[2],
                    buscarContaEquivalente(contaDeDestinacao, proximoExercicioDestinacao),
                    buscarContaEquivalente(conta, proximoExercicio),
                    selecionado.getExercicio().getAno() <= 2021 ? Integer.valueOf(codigo[5]) : null,
                    contaAuxiliar.getExercicioOrigem().getAno(),
                    contaAuxiliar.getExercicioOrigem()));
                break;
        }
        return mapaContaAuxiliar;
    }

    private SubSistema getFinanceiroPermanente(String subSistema) {
        return "1".equals(subSistema) ? SubSistema.FINANCEIRO : "2".equals(subSistema) ? SubSistema.PERMANENTE : null;
    }

    private TreeMap montarContaAuxiliarDetalhadaNova(AberturaFechamentoExercicio selecionado, ContaAuxiliarDetalhada contaAuxiliar, UnidadeOrganizacional unidadeOrganizacional) {
        Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(selecionado.getExercicio().getAno() + 1);
        TreeMap mapaContaAuxiliarDetalhada = new TreeMap();
        Conta contaDeDestinacao;
        Conta conta;
        Exercicio proximoExercicioDestinacao;
        String[] codigo = contaAuxiliar.getCodigo().split("\\.");
        switch (contaAuxiliar.getTipoContaAuxiliar().getCodigo()) {
            case "91":
                mapaContaAuxiliarDetalhada.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(unidadeOrganizacional));
                break;
            case "92":
                mapaContaAuxiliarDetalhada.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(unidadeOrganizacional,
                    getFinanceiroPermanente(codigo[2])));
                break;
            case "93":
                mapaContaAuxiliarDetalhada.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada3(unidadeOrganizacional,
                    getFinanceiroPermanente(codigo[2]),
                    Integer.valueOf(codigo[3])));
                break;
            case "94":
                contaDeDestinacao = contaAuxiliar.getContaDeDestinacao();
                proximoExercicioDestinacao = exercicioFacade.getExercicioPorAno(contaDeDestinacao.getExercicio().getAno() + 1);
                mapaContaAuxiliarDetalhada.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(unidadeOrganizacional,
                    getFinanceiroPermanente(codigo[2]),
                    buscarContaEquivalente(contaDeDestinacao, proximoExercicioDestinacao),
                    proximoExercicio));
                break;
            case "95":
                contaDeDestinacao = contaAuxiliar.getContaDeDestinacao();
                proximoExercicioDestinacao = exercicioFacade.getExercicioPorAno(contaDeDestinacao.getExercicio().getAno() + 1);
                mapaContaAuxiliarDetalhada.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(unidadeOrganizacional,
                    buscarContaEquivalente(contaDeDestinacao, proximoExercicioDestinacao),
                    proximoExercicio));
                break;
            case "96":
                conta = contaAuxiliar.getConta();
                contaDeDestinacao = contaAuxiliar.getContaDeDestinacao();
                mapaContaAuxiliarDetalhada.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada6(unidadeOrganizacional,
                    buscarContaEquivalente(contaDeDestinacao, proximoExercicio),
                    buscarContaEquivalente(conta, proximoExercicio),
                    proximoExercicio));
                break;
            case "97":
                conta = contaAuxiliar.getConta();
                contaDeDestinacao = contaAuxiliar.getContaDeDestinacao();
                Conta contaDespesa = buscarContaEquivalente(conta, proximoExercicio);
                mapaContaAuxiliarDetalhada.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada7(unidadeOrganizacional,
                    codigo[2],
                    buscarContaEquivalente(contaDeDestinacao, proximoExercicio),
                    contaDespesa,
                    selecionado.getExercicio().getAno() <= 2021 ? Integer.valueOf(codigo[5]) : null));
                break;
            case "98":
                contaDeDestinacao = contaAuxiliar.getContaDeDestinacao();
                mapaContaAuxiliarDetalhada.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada8(unidadeOrganizacional,
                    getFinanceiroPermanente(codigo[2]),
                    Integer.valueOf(codigo[3]),
                    buscarContaEquivalente(contaDeDestinacao, proximoExercicio)));
                break;
            case "99":
                contaDeDestinacao = contaAuxiliar.getContaDeDestinacao();
                proximoExercicioDestinacao = exercicioFacade.getExercicioPorAno(contaDeDestinacao.getExercicio().getAno() + 1);
                conta = contaAuxiliar.getConta();
                mapaContaAuxiliarDetalhada.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(unidadeOrganizacional,
                    codigo[2],
                    buscarContaEquivalente(contaDeDestinacao, proximoExercicioDestinacao),
                    buscarContaEquivalente(conta, proximoExercicio),
                    selecionado.getExercicio().getAno() <= 2021 ? Integer.valueOf(codigo[5]) : null,
                    contaAuxiliar.getExercicioOrigem().getAno(),
                    proximoExercicio,
                    contaAuxiliar.getExercicioOrigem()));
                break;
        }
        return mapaContaAuxiliarDetalhada;
    }

    public List<OCCFechamentoExercicio> getConfiguracoesDeOccPorTipo(AberturaFechamentoExercicio selecionado, EntidadeOCC entidadeOCC, TipoDuplicarConfiguracaoFechamentoExercicio tipo) {
        String sql = "select occ.* from origemcontacontabil occ " +
            "inner join occconta oc on occ.id = oc.id " +
            "inner join tagocc tag on occ.tagocc_id = tag.id " +
            "where to_date(:data,'dd/mm/yyyy') between trunc(occ.iniciovigencia) and coalesce(trunc(occ.fimvigencia), to_date(:data,'dd/mm/yyyy')) " +
            "and tag.entidadeocc = :entidadeOCC";
        Query consulta = em.createNativeQuery(sql, OrigemContaContabil.class);
        consulta.setParameter("data", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("entidadeOCC", entidadeOCC.name());
        List<OCCFechamentoExercicio> retorno = new ArrayList<OCCFechamentoExercicio>();
        List<OrigemContaContabil> resultList = consulta.getResultList();
        for (OrigemContaContabil o : resultList) {
            retorno.add(new OCCFechamentoExercicio(o, selecionado, tipo));
        }
        return retorno;
    }

    public List<ConfiguracaoEventoFechamentoExercicio> getConfiguracoesDeEventoRECEITA(AberturaFechamentoExercicio selecionado, List<TipoEventoContabil> tipos, TipoDuplicarConfiguracaoFechamentoExercicio tipo) {
        String sql = " select distinct eve.id as evento,null as configuracao from eventocontabil eve " +
            " inner join itemeventoclp item on eve.id = item.eventocontabil_id " +
            " inner join clp on item.clp_id = clp.id " +
            " inner join lcp on lcp.clp_id = clp.id " +
            " where to_date(:data,'dd/mm/yyyy') between trunc(eve.iniciovigencia) and coalesce(trunc(eve.fimvigencia), to_date(:data,'dd/mm/yyyy')) " +
            " and to_date(:data,'dd/mm/yyyy') between trunc(clp.iniciovigencia) and coalesce(trunc(clp.fimvigencia), to_date(:data,'dd/mm/yyyy')) " +
            " and eve.tipoeventocontabil in " + getEventosAsString(tipos) +
            " and eve.id not in ( " +
            "             select conf.eventocontabil_id from configuracaoevento conf " +
            "             where to_date(:data,'dd/mm/yyyy') between trunc(conf.iniciovigencia) and coalesce(trunc(conf.fimvigencia), to_date(:data,'dd/mm/yyyy')) ) " +
            "  " +
            " union all " +
            "  " +
            " select distinct eve.id as evento,conf.id as configuracao from eventocontabil eve " +
            " inner join itemeventoclp item on eve.id = item.eventocontabil_id " +
            " inner join clp on item.clp_id = clp.id " +
            " inner join lcp on lcp.clp_id = clp.id " +
            " inner join configuracaoevento conf on eve.id = conf.eventocontabil_id " +
            " inner join configreceitarealizada cc on conf.id = cc.id" +
            " where to_date(:data,'dd/mm/yyyy') between trunc(eve.iniciovigencia) and coalesce(trunc(eve.fimvigencia), to_date(:data,'dd/mm/yyyy')) " +
            " and to_date(:data,'dd/mm/yyyy') between trunc(clp.iniciovigencia) and coalesce(trunc(clp.fimvigencia), to_date(:data,'dd/mm/yyyy')) " +
            " and to_date(:data,'dd/mm/yyyy') between trunc(conf.iniciovigencia) and coalesce(trunc(conf.fimvigencia), to_date(:data,'dd/mm/yyyy')) " +
            " and eve.tipoeventocontabil in " + getEventosAsString(tipos) +
            "  " +
            " union all " +
            "  " +
            " select distinct eve.id as evento,conf.id as configuracao from eventocontabil eve " +
            " inner join itemeventoclp item on eve.id = item.eventocontabil_id " +
            " inner join clp on item.clp_id = clp.id " +
            " inner join lcp on lcp.clp_id = clp.id " +
            " inner join configuracaoevento conf on eve.id = conf.eventocontabil_id " +
            " inner join configcreditoreceber cc on conf.id = cc.id" +
            " where to_date(:data,'dd/mm/yyyy') between trunc(eve.iniciovigencia) and coalesce(trunc(eve.fimvigencia), to_date(:data,'dd/mm/yyyy')) " +
            " and to_date(:data,'dd/mm/yyyy') between trunc(clp.iniciovigencia) and coalesce(trunc(clp.fimvigencia), to_date(:data,'dd/mm/yyyy')) " +
            " and to_date(:data,'dd/mm/yyyy') between trunc(conf.iniciovigencia) and coalesce(trunc(conf.fimvigencia), to_date(:data,'dd/mm/yyyy')) " +
            " and eve.tipoeventocontabil in " + getEventosAsString(tipos) +
            "  " +
            " union all " +
            "  " +
            " select distinct eve.id as evento,conf.id as configuracao from eventocontabil eve " +
            " inner join itemeventoclp item on eve.id = item.eventocontabil_id " +
            " inner join clp on item.clp_id = clp.id " +
            " inner join lcp on lcp.clp_id = clp.id " +
            " inner join configuracaoevento conf on eve.id = conf.eventocontabil_id " +
            " inner join ConfigDividaAtivaContabil cc on conf.id = cc.id" +
            " where to_date(:data,'dd/mm/yyyy') between trunc(eve.iniciovigencia) and coalesce(trunc(eve.fimvigencia), to_date(:data,'dd/mm/yyyy')) " +
            " and to_date(:data,'dd/mm/yyyy') between trunc(clp.iniciovigencia) and coalesce(trunc(clp.fimvigencia), to_date(:data,'dd/mm/yyyy')) " +
            " and to_date(:data,'dd/mm/yyyy') between trunc(conf.iniciovigencia) and coalesce(trunc(conf.fimvigencia), to_date(:data,'dd/mm/yyyy')) " +
            " and eve.tipoeventocontabil in " + getEventosAsString(tipos);
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("data", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        List<ConfiguracaoEventoFechamentoExercicio> retorno = new ArrayList<ConfiguracaoEventoFechamentoExercicio>();
        List resultList = consulta.getResultList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            EventoContabil eventoContabil = em.find(EventoContabil.class, ((BigDecimal) objeto[0]).longValue());

            ConfiguracaoEvento config = null;
            if (objeto[1] != null) {
                config = em.find(ConfiguracaoEvento.class, ((BigDecimal) objeto[1]).longValue());
            }

            retorno.add(new ConfiguracaoEventoFechamentoExercicio(config, eventoContabil, selecionado, tipo));
        }
        return retorno;
    }

    public String getEventosAsString(List<TipoEventoContabil> tipoEventoContabils) {
        String tiposEventos = "(";

        for (TipoEventoContabil tipoEventoContabil : tipoEventoContabils) {
            tiposEventos += "'" + tipoEventoContabil.name() + "',";
        }
        tiposEventos = tiposEventos.substring(0, tiposEventos.length() - 1) + ")";
        return tiposEventos;
    }

    private String getUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        if (listaUnidades != null) {
            if (!listaUnidades.isEmpty()) {
                String retorno = " and vw.subordinada_id in (";
                for (HierarquiaOrganizacional uni : listaUnidades) {
                    retorno += "" + uni.getSubordinada().getId() + ",";
                }
                retorno += ")";
                return retorno.replace(",)", ")");
            }
        }
        return "";
    }

    private String getContas(List<Conta> contas) {
        if (contas != null) {
            if (!contas.isEmpty()) {
                String complemento = "c.codigo like ";
                String or = " or ";
                String retorno = " and (" + complemento;
                for (Conta conta : contas) {
                    retorno += "'" + conta.getCodigoSemZerosAoFinal() + "%' " + or + complemento;
                }

                retorno = retorno.substring(0, retorno.length() - (complemento.length() + or.length()));
                return retorno += ")";
            }
        }
        return "";
    }

    @Override
    public void remover(AberturaFechamentoExercicio entity) {
        try {
            removerSaldoFonteDespesaOrc(entity);
            removerTransporteSaldo(entity);
            super.remover(entity);
        } catch (ExcecaoNegocioGenerica e) {
            e.printStackTrace();
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void removerSaldoFonteDespesaOrc(AberturaFechamentoExercicio entity) {
        for (PrescricaoEmpenho prescricaoEmpenho : entity.getPrescricaoEmpenhosNaoProcessados()) {
            FonteDespesaORC fonteDespesaORC = prescricaoEmpenho.getEmpenho().getFonteDespesaORC();
            UnidadeOrganizacional unidade = prescricaoEmpenho.getEmpenhoEstorno().getUnidadeOrganizacional();
            Date data = prescricaoEmpenho.getEmpenhoEstorno().getDataEstorno();
            SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(fonteDespesaORC, OperacaoORC.EMPENHORESTO_NAO_PROCESSADO, TipoOperacaoORC.NORMAL,
                prescricaoEmpenho.getValor(), data, unidade, prescricaoEmpenho.getId().toString(),
                prescricaoEmpenho.getClass().getSimpleName(),
                prescricaoEmpenho.getEmpenho().getNumero(),
                prescricaoEmpenho.getEmpenho().getHistoricoRazao());
            empenhoFacade.getSaldoFonteDespesaORCFacade().gerarSaldoOrcamentario(vo);
        }

        for (PrescricaoEmpenho prescricaoEmpenho : entity.getPrescricaoEmpenhosProcessados()) {
            FonteDespesaORC fonteDespesaORC = prescricaoEmpenho.getEmpenho().getFonteDespesaORC();
            UnidadeOrganizacional unidade = prescricaoEmpenho.getEmpenhoEstorno().getUnidadeOrganizacional();
            Date data = prescricaoEmpenho.getEmpenhoEstorno().getDataEstorno();
            SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(fonteDespesaORC, OperacaoORC.EMPENHORESTO_PROCESSADO,
                TipoOperacaoORC.NORMAL, prescricaoEmpenho.getValor(), data, unidade, prescricaoEmpenho.getId().toString(), prescricaoEmpenho.getClass().getSimpleName(),
                prescricaoEmpenho.getEmpenho().getNumero(),
                prescricaoEmpenho.getEmpenho().getHistoricoRazao());
            empenhoFacade.getSaldoFonteDespesaORCFacade().gerarSaldoOrcamentario(vo);
        }

        for (InscricaoEmpenho inscricaoEmpenho : entity.getInscricaoEmpenhosProcessados()) {
            FonteDespesaORC fonteDespesaORC = inscricaoEmpenho.getEmpenho().getFonteDespesaORC();
            UnidadeOrganizacional unidade = inscricaoEmpenho.getEmpenho().getUnidadeOrganizacional();
            Date data = entity.getDataGeracao();
            SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(fonteDespesaORC, OperacaoORC.EMPENHORESTO_PROCESSADO,
                TipoOperacaoORC.ESTORNO, inscricaoEmpenho.getValor(), data, unidade, inscricaoEmpenho.getId().toString(),
                inscricaoEmpenho.getClass().getSimpleName(),
                inscricaoEmpenho.getEmpenho().getNumero(),
                inscricaoEmpenho.getEmpenho().getHistoricoRazao());
            empenhoFacade.getSaldoFonteDespesaORCFacade().gerarSaldoOrcamentario(vo);
        }

        for (InscricaoEmpenho inscricaoEmpenho : entity.getInscricaoEmpenhosNaoProcessados()) {
            FonteDespesaORC fonteDespesaORC = inscricaoEmpenho.getEmpenho().getFonteDespesaORC();
            UnidadeOrganizacional unidade = inscricaoEmpenho.getEmpenho().getUnidadeOrganizacional();
            Date data = entity.getDataGeracao();
            SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(fonteDespesaORC, OperacaoORC.EMPENHORESTO_NAO_PROCESSADO,
                TipoOperacaoORC.ESTORNO, inscricaoEmpenho.getValor(), data, unidade, inscricaoEmpenho.getId().toString(),
                inscricaoEmpenho.getClass().getSimpleName(),
                inscricaoEmpenho.getEmpenho().getNumero(),
                inscricaoEmpenho.getEmpenho().getHistoricoRazao());
            empenhoFacade.getSaldoFonteDespesaORCFacade().gerarSaldoOrcamentario(vo);
        }
    }

    private void removerTransporteSaldo(AberturaFechamentoExercicio entity) {
        Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(entity.getExercicio().getAno() + 1);
        for (TransporteDeSaldoFechamentoExercicio transporte : entity.getTransporteDeSaldo()) {
            Date data = recuperarDataDoTranporte(proximoExercicio);
            UnidadeOrganizacional unidadeOrganizacional = transporte.getUnidadeOrganizacional();
            Conta conta = recuperarContaContabilEquivalente(transporte.getConta(), proximoExercicio);
            SaldoContaContabil saldoContaContabil = saldoContaContabilFacade.ultimoSaldoPorData(data, conta, unidadeOrganizacional, TipoBalancete.ABERTURA);
            em.remove(saldoContaContabil);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @Asynchronous
    public Future<AberturaFechamentoExercicio> inicializarFechamento(AssistenteAberturaFechamentoExercicio assistenteAberturaFechamentoExercicio) {
        this.assistenteAberturaFechamentoExercicio = assistenteAberturaFechamentoExercicio;

        AberturaFechamentoExercicio entity = assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio();
        BarraProgressoItens barraProgressoItens = assistenteAberturaFechamentoExercicio.getBarraProgressoItens();
        barraProgressoItens.inicializa();
        barraProgressoItens.setMensagens("Salvando...");
        try {
            entity = salvarRegistro();
        } catch (ValidacaoException ve) {
            for (FacesMessage facesMessage : ve.getAllMensagens()) {
                assistenteAberturaFechamentoExercicio.getMensagens().add(facesMessage.getDetail());
            }
            assistenteAberturaFechamentoExercicio.getBarraProgressoItens().finaliza();
            throw ve;
        } catch (Exception e) {
            logger.error("Erro na Abertura e Fechamento: ", e);
            assistenteAberturaFechamentoExercicio.getMensagens().add(e.getMessage());
            assistenteAberturaFechamentoExercicio.getBarraProgressoItens().finaliza();
            throw e;
        }
        assistenteAberturaFechamentoExercicio.getBarraProgressoItens().finaliza();
        return new AsyncResult<>(entity);
    }

    private AberturaFechamentoExercicio salvarRegistro() {
        AberturaFechamentoExercicio entity = assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio();
        if (entity.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }

        Integer totalDeItens = assistenteAberturaFechamentoExercicio.getTotalDeItensDeTodasListas();
        adicionarQuantidadeRegistroParaProcessar(totalDeItens);

        prescricaoDeEmpenhoProcessadosENaoProcessados();
        HashMap<ObrigacaoAPagar, Empenho> mapEmpenhoDaObrigacao = Maps.newHashMap();
        transportarObrigacoesAPagar(mapEmpenhoDaObrigacao);
        inscricaoDeEmpenhoProcessadosENaoProcessados(mapEmpenhoDaObrigacao);

        execucaoDaReceita();
        execucaoDaDespesa();
        execucaoDeRestoAPagar();
        execucaoDisponibilidadeDeRecurso();
        execucaoResultadoDiminutivoDoExercicio();
        execucaoResultadoAumentativoDoExercicio();
        duplicarPlanoDeContas();
        transporteDeSaldo();
        assistenteAberturaFechamentoExercicio.setAberturaFechamentoExercicio(entity);
        aberturaSaldoRestoAPagar();
        entity = em.merge(entity);
        return entity;
    }

    private void adicionarQuantidadeRegistroParaProcessar(int size) {
        assistenteAberturaFechamentoExercicio.getBarraProgressoItens().setTotal(size);
        assistenteAberturaFechamentoExercicio.setTotal(size);
    }

    private void contarRegistroProcessado() {
        assistenteAberturaFechamentoExercicio.getBarraProgressoItens().setProcessados(assistenteAberturaFechamentoExercicio.getBarraProgressoItens().getProcessados() + 1);
        assistenteAberturaFechamentoExercicio.conta();
    }

    private void aberturaSaldoRestoAPagar() {
        assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Transporte de saldo de abertura.");
        Exercicio exercicio = assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getExercicio();
        Date data = recuperarDataDoTranporte(exercicio);
        for (AberturaInscricaoResto aberturaInscricaoResto : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getInscricaoRestoPagarProcessados()) {
            contabilizacaoAberturaRestoAPagar(aberturaInscricaoResto, data, exercicio);
            contarRegistroProcessado();
        }
        for (AberturaInscricaoResto aberturaInscricaoResto : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getInscricaoRestoPagarNaoProcessados()) {
            contabilizacaoAberturaRestoAPagar(aberturaInscricaoResto, data, exercicio);
            contarRegistroProcessado();
        }
        for (TransporteDeSaldoAbertura transporteDeSaldoAbertura : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransferenciaResultadoPublico()) {
            contabilizarTransporteSaldoAbertura(transporteDeSaldoAbertura, data, exercicio);
            contarRegistroProcessado();
        }
        for (TransporteDeSaldoAbertura transporteDeSaldoAbertura : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransferenciaResultadoPrivado()) {
            contabilizarTransporteSaldoAbertura(transporteDeSaldoAbertura, data, exercicio);
            contarRegistroProcessado();
        }
        for (TransporteDeSaldoAbertura transporteDeSaldoAbertura : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransferenciaAjustesPublico()) {
            contabilizarTransporteSaldoAbertura(transporteDeSaldoAbertura, data, exercicio);
            contarRegistroProcessado();
        }
        for (TransporteDeSaldoAbertura transporteDeSaldoAbertura : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransferenciaAjustesPrivado()) {
            contabilizarTransporteSaldoAbertura(transporteDeSaldoAbertura, data, exercicio);
            contarRegistroProcessado();
        }
    }

    private void contabilizarTransporteSaldoAbertura(TransporteDeSaldoAbertura transporteDeSaldoAbertura, Date data, Exercicio exercicio) {
        if (transporteDeSaldoAbertura.getEventoContabil() != null) {
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(transporteDeSaldoAbertura.toString());
            parametroEvento.setDataEvento(data);
            parametroEvento.setUnidadeOrganizacional(transporteDeSaldoAbertura.getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(transporteDeSaldoAbertura.getEventoContabil());
            parametroEvento.setExercicio(exercicio);
            parametroEvento.setIdOrigem(transporteDeSaldoAbertura.getId().toString());
            parametroEvento.setClasseOrigem(transporteDeSaldoAbertura.getClass().getSimpleName());
            parametroEvento.setTipoBalancete(TipoBalancete.ABERTURA);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(transporteDeSaldoAbertura.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(transporteDeSaldoAbertura, item));
            objetos.add(new ObjetoParametro(transporteDeSaldoAbertura.getConta(), item));

            item.setObjetoParametros(objetos);
            parametroEvento.getItensParametrosEvento().add(item);
            contabilizacaoFacade.contabilizar(parametroEvento);
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado Evento Contábil para " + transporteDeSaldoAbertura.toString() + ".");
        }
    }

    private void contabilizacaoAberturaRestoAPagar(AberturaInscricaoResto aberturaInscricaoResto, Date data, Exercicio exercicio) {
        if (aberturaInscricaoResto.getEventoContabil() != null) {
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(aberturaInscricaoResto.toString());
            parametroEvento.setDataEvento(data);
            parametroEvento.setUnidadeOrganizacional(aberturaInscricaoResto.getEmpenho().getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(aberturaInscricaoResto.getEventoContabil());
            parametroEvento.setExercicio(exercicio);
            parametroEvento.setIdOrigem(aberturaInscricaoResto.getId().toString());
            parametroEvento.setClasseOrigem(aberturaInscricaoResto.getClass().getSimpleName());
            parametroEvento.setTipoBalancete(TipoBalancete.ABERTURA);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(aberturaInscricaoResto.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(aberturaInscricaoResto, item));

            item.setObjetoParametros(objetos);
            parametroEvento.getItensParametrosEvento().add(item);
            contabilizacaoFacade.contabilizar(parametroEvento);
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado Evento Contábil para " + aberturaInscricaoResto.toString() + ".");
        }
    }

    private void transporteDeSaldo() {
        transporteDeSaldoContabil();
        transporteDeSaldoFinanceiro();
        transporteDeSaldoCreditoReceber();
        transporteDeSaldoDividaAtiva();
        transporteDeSaldoExtra();
        transportarSaldoDividaPublica();
        transportarReceitasExtras();
        transportarSaldoAuxiliar();
    }

    private void transportarReceitasExtras() {
        if (!assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getReceitasExtras().isEmpty()) {
            assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Transportar receitas extras.");
            Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getExercicio().getAno() + 1);
            PlanoDeContasExercicio planoDeContasExercicio = planoDeContasFacade.getPlanoDeContasExercicioFacade().recuperarPorExercicio(proximoExercicio);
            if (planoDeContasExercicio == null) {
                throw new ExcecaoNegocioGenerica("Nenhum Plano de Contas Exercício foi encontrado para o exercício de " + proximoExercicio.getAno() + ".");
            }
            if (planoDeContasExercicio.getPlanoContabil() == null) {
                throw new ExcecaoNegocioGenerica("Nenhum Plano de Contas Contábil foi encontrado para o Plano de Contas Exercício de " + proximoExercicio.getAno() + ".");
            }

            for (ReceitaExtraFechamentoExercicio receitaExtraFechamentoExercicio : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getReceitasExtras()) {
                ReceitaExtra receitaExtra = new ReceitaExtra();
                receitaExtra.setContaExtraorcamentaria(recuperarContaExtraEquivalente(receitaExtraFechamentoExercicio.getConta(), proximoExercicio));
                receitaExtra.setDataReceita(recuperarDataDoTranporte(proximoExercicio));
                receitaExtra.setUnidadeOrganizacional(receitaExtraFechamentoExercicio.getUnidadeOrganizacional());
                receitaExtra.setExercicio(proximoExercicio);
                receitaExtra.setNumero(singletonGeradorCodigoContabil.getNumeroReceitaExtra(receitaExtra.getExercicio(), receitaExtra.getUnidadeOrganizacional(), receitaExtra.getDataReceita()));
                receitaExtra.setValor(receitaExtraFechamentoExercicio.getValor());
                receitaExtra.setTransportado(true);
                receitaExtra.setFonteDeRecursos(recuperarFonteDeRecurso(receitaExtraFechamentoExercicio.getFonteDeRecursos(), proximoExercicio));
                receitaExtra.setContaDeDestinacao((ContaDeDestinacao) buscarContaEquivalente(receitaExtraFechamentoExercicio.getContaDeDestinacao(), proximoExercicio));
                receitaExtra.setComplementoHistorico(Util.cortarString(("Nº " + receitaExtraFechamentoExercicio.getNumeroOriginal() + "/" + assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getExercicio().getAno() + " - " + receitaExtraFechamentoExercicio.getComplementoHistorico()), 3000));
                receitaExtra.setSaldo(receitaExtraFechamentoExercicio.getSaldo());
                receitaExtra.setValorEstornado(receitaExtraFechamentoExercicio.getValorEstornado());
                receitaExtra.setTipoConsignacao(receitaExtraFechamentoExercicio.getTipoConsignacao());
                receitaExtra.setHistoricoNota(receitaExtraFechamentoExercicio.getHistoricoNota());
                receitaExtra.setHistoricoRazao(receitaExtraFechamentoExercicio.getHistoricoRazao());
                receitaExtra.setSituacaoReceitaExtra(receitaExtraFechamentoExercicio.getSituacaoReceitaExtra());
                receitaExtra.setPessoa(receitaExtraFechamentoExercicio.getPessoa());
                receitaExtra.setClasseCredor(receitaExtraFechamentoExercicio.getClasseCredor());
                receitaExtra.setSubConta(receitaExtraFechamentoExercicio.getSubConta());
                em.persist(receitaExtra);
                contarRegistroProcessado();
            }
        }
    }

    private void transporteDeSaldoExtra() {
        if (!assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransporteDeSaldoExtra().isEmpty()) {
            assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Transporte de saldo extra.");
            Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getExercicio().getAno() + 1);
            PlanoDeContasExercicio planoDeContasExercicio = planoDeContasFacade.getPlanoDeContasExercicioFacade().recuperarPorExercicio(proximoExercicio);
            if (planoDeContasExercicio == null) {
                throw new ExcecaoNegocioGenerica("Nenhum Plano de Contas Exercício foi encontrado para o exercício de " + proximoExercicio.getAno() + ".");
            }
            if (planoDeContasExercicio.getPlanoExtraorcamentario() == null) {
                throw new ExcecaoNegocioGenerica("Nenhum Plano de Contas de Extra foi encontrado para o Plano de Contas Exercício de " + proximoExercicio.getAno() + ".");
            }

            for (TransporteExtra transporte : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransporteDeSaldoExtra()) {
                SaldoExtraorcamentario saldo = new SaldoExtraorcamentario();
                saldo.setUnidadeOrganizacional(transporte.getUnidadeOrganizacional());
                saldo.setContaExtraorcamentaria(recuperarContaExtra(transporte.getConta(), proximoExercicio));
                saldo.setFonteDeRecursos(recuperarFonteDeRecurso(transporte.getFonte(), proximoExercicio));
                saldo.setContaDeDestinacao((ContaDeDestinacao) recuperarContaDeDestinacao((Conta) transporte.getContaDeDestinacao(), proximoExercicio));
                saldo.setDataSaldo(transporte.getData());
                saldo.setValor(transporte.getValor());
                em.persist(saldo);
                contarRegistroProcessado();
            }
        }
    }

    private void transportarSaldoDividaPublica() {
        if (!assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransporteDeSaldoDividaPublica().isEmpty()) {
            assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Transportar saldo de dívida pública.");
            Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getExercicio().getAno() + 1);
            for (TransporteSaldoDividaPublica transporte : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransporteDeSaldoDividaPublica()) {
                SaldoDividaPublica saldo = new SaldoDividaPublica();
                saldo.setUnidadeOrganizacional(transporte.getUnidadeOrganizacional());
                saldo.setDividapublica(transporte.getDividapublica());
                saldo.setFonteDeRecursos(recuperarFonteDeRecurso(transporte.getFonte(), proximoExercicio));
                saldo.setContaDeDestinacao((ContaDeDestinacao) buscarContaEquivalente(transporte.getContaDeDestinacao(), proximoExercicio));
                saldo.setData(transporte.getData());
                saldo.setIntervalo(transporte.getIntervalo());
                saldo.setInscricao(transporte.getInscricao());
                saldo.setApropriacao(transporte.getApropriacao());
                saldo.setAtualizacao(transporte.getAtualizacao());
                saldo.setPagamento(transporte.getPagamento());
                saldo.setReceita(transporte.getReceita());
                saldo.setCancelamento(transporte.getCancelamento());
                saldo.setTransferenciaCredito(transporte.getTransferenciaCredito());
                saldo.setTransferenciaDebito(transporte.getTransferenciaDebito());
                saldo.setEmpenhado(transporte.getEmpenhado());
                em.persist(saldo);
                contarRegistroProcessado();
            }
        }
    }

    private void transporteDeSaldoDividaAtiva() {
        if (!assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransporteDeSaldoDividaAtiva().isEmpty()) {
            assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Transporte de saldo de dívida ativa.");
            Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getExercicio().getAno() + 1);
            PlanoDeContasExercicio planoDeContasExercicio = planoDeContasFacade.getPlanoDeContasExercicioFacade().recuperarPorExercicio(proximoExercicio);
            if (planoDeContasExercicio == null) {
                throw new ExcecaoNegocioGenerica("Nenhum Plano de Contas Exercício foi encontrado para o exercício de " + proximoExercicio.getAno() + ".");
            }
            if (planoDeContasExercicio.getPlanoDeReceitas() == null) {
                throw new ExcecaoNegocioGenerica("Nenhum Plano de Contas de Receita foi encontrado para o Plano de Contas Exercício de " + proximoExercicio.getAno() + ".");
            }

            for (TransporteDividaAtiva transporte : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransporteDeSaldoDividaAtiva()) {
                SaldoDividaAtivaContabil saldo = new SaldoDividaAtivaContabil();
                saldo.setUnidadeOrganizacional(transporte.getUnidadeOrganizacional());
                saldo.setContaReceita(recuperarContaReceita(transporte.getConta(), proximoExercicio));
                saldo.setDataSaldo(transporte.getData());
                saldo.setInscricao(transporte.getInscricao());
                saldo.setAtualizacao(transporte.getAtualizacao());
                saldo.setProvisao(transporte.getProvisao());
                saldo.setReversao(transporte.getReversao());
                saldo.setRecebimento(transporte.getRecebimento());
                saldo.setBaixa(transporte.getBaixa());
                saldo.setAumentativo(transporte.getAumentativo());
                saldo.setDiminutivo(transporte.getDiminutivo());
                saldo.setNaturezaDividaAtiva(transporte.getNaturezaDividaAtiva());
                saldo.setIntervalo(transporte.getIntervalo());
                saldo.setContaDeDestinacao((ContaDeDestinacao) recuperarContaDeDestinacao(transporte.getContaDeDestinacao(), proximoExercicio));
                em.persist(saldo);
                contarRegistroProcessado();
            }
        }
    }

    private void transporteDeSaldoCreditoReceber() {
        if (!assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransporteDeSaldoCreditoReceber().isEmpty()) {
            assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Transporte de saldo de créditos a receber.");
            Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getExercicio().getAno() + 1);
            PlanoDeContasExercicio planoDeContasExercicio = planoDeContasFacade.getPlanoDeContasExercicioFacade().recuperarPorExercicio(proximoExercicio);
            if (planoDeContasExercicio == null) {
                throw new ExcecaoNegocioGenerica("Nenhum Plano de Contas Exercício foi encontrado para o exercício de " + proximoExercicio.getAno() + ".");
            }
            if (planoDeContasExercicio.getPlanoDeReceitas() == null) {
                throw new ExcecaoNegocioGenerica("Nenhum Plano de Contas de Receita foi encontrado para o Plano de Contas Exercício de " + proximoExercicio.getAno() + ".");
            }

            for (TransporteCreditoReceber transporte : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransporteDeSaldoCreditoReceber()) {
                SaldoCreditoReceber saldo = new SaldoCreditoReceber();
                saldo.setUnidadeOrganizacional(transporte.getUnidadeOrganizacional());
                saldo.setContaReceita(recuperarContaReceita(transporte.getConta(), proximoExercicio));
                saldo.setContaDeDestinacao((ContaDeDestinacao) buscarContaEquivalente(transporte.getContaDeDestinacao(), proximoExercicio));
                saldo.setDataSaldo(transporte.getData());
                saldo.setReconhecimentoCredito(transporte.getReconhecimentoCredito());
                saldo.setDeducaoReconhecimento(transporte.getDeducaoReconhecimento());
                saldo.setProvisaoPerdaCredito(transporte.getProvisaoPerdaCredito());
                saldo.setReversaoProvisaoPerdaCredito(transporte.getReversaoProvisaoPerdaCredito());
                saldo.setBaixaReconhecimento(transporte.getBaixaReconhecimento());
                saldo.setBaixaDeducao(transporte.getBaixaDeducao());
                saldo.setRecebimento(transporte.getRecebimento());
                saldo.setAtualizacao(transporte.getAtualizacao());
                saldo.setAumentativo(transporte.getAumentativo());
                saldo.setDiminutivo(transporte.getDiminutivo());
                saldo.setNaturezaCreditoReceber(transporte.getNaturezaCreditoReceber());
                saldo.setIntervalo(transporte.getIntervalo());
                em.persist(saldo);
                contarRegistroProcessado();
            }
        }
    }

    private Conta recuperarContaExtra(Conta conta, Exercicio proximoExercicio) {
        List<Conta> contas = planoDeContasFacade.getContaFacade().recuperarContaExtraorcamentariaEquivalentePorId(conta, proximoExercicio);
        if (contas == null || contas.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Nenhuma Conta Equivalente Extra foi encontrada para a conta " + conta.getCodigo() + " no exercício de " + proximoExercicio.getAno() + ".");
        }
        return contas.get(0);
    }

    private Conta recuperarContaDeDestinacao(Conta conta, Exercicio proximoExercicio) {
        List<Conta> contas = planoDeContasFacade.getContaFacade().recuperarContaDeDestinacaoEquivalentePorId(conta, proximoExercicio);
        if (contas == null || contas.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Nenhuma Conta de Destinação foi encontrada para a conta " + conta.getCodigo() + " no exercício de " + proximoExercicio.getAno() + ".");
        }
        return contas.get(0);
    }

    private Conta recuperarContaReceita(Conta conta, Exercicio proximoExercicio) {
        List<Conta> contas = planoDeContasFacade.getContaFacade().recuperarContaReceitaEquivalentePorId(conta, proximoExercicio);
        if (contas == null || contas.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Nenhuma Conta Equivalente de Receita foi encontrada para a conta " + conta.getCodigo() + " no exercício de " + proximoExercicio.getAno() + ".");
        }
        return contas.get(0);
    }

    private void transporteDeSaldoFinanceiro() {
        if (!assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransporteDeSaldoFinanceiro().isEmpty()) {
            assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Transporte de saldo financeiro.");
            Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getExercicio().getAno() + 1);
            PlanoDeContasExercicio planoDeContasExercicio = planoDeContasFacade.getPlanoDeContasExercicioFacade().recuperarPorExercicio(proximoExercicio);
            if (planoDeContasExercicio == null) {
                throw new ExcecaoNegocioGenerica("Nenhum Plano de Contas Exercício foi encontrado para o exercício de " + proximoExercicio.getAno() + ".");
            }
            if (planoDeContasExercicio.getPlanoDeDestinacaoDeRecursos() == null) {
                throw new ExcecaoNegocioGenerica("Nenhum Plano de Contas de Destinação de Fonte de Recurso foi encontrado para o Plano de Contas Exercício de " + proximoExercicio.getAno() + ".");
            }

            for (TransporteSaldoFinanceiro transporteSaldoFinanceiro : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransporteDeSaldoFinanceiro()) {
                if (transporteSaldoFinanceiro.getConfiguracaoDestino() != null) {
                    movimentarSaldoSubContaUsandoConfiguracao(transporteSaldoFinanceiro);
                } else {
                    movimentarSaldoSubConta(proximoExercicio, transporteSaldoFinanceiro);
                }
                contarRegistroProcessado();
            }
        }
    }

    private void movimentarSaldoSubConta(Exercicio proximoExercicio, TransporteSaldoFinanceiro transporteSaldoFinanceiro) {
        SaldoSubConta saldoSubConta = new SaldoSubConta();
        saldoSubConta.setDataSaldo(transporteSaldoFinanceiro.getData());
        saldoSubConta.setFonteDeRecursos(recuperarFonteDeRecurso(transporteSaldoFinanceiro.getFonte(), proximoExercicio));
        saldoSubConta.setContaDeDestinacao((ContaDeDestinacao) buscarContaEquivalente(transporteSaldoFinanceiro.getContaDeDestinacao(), proximoExercicio));
        saldoSubConta.setUnidadeOrganizacional(transporteSaldoFinanceiro.getUnidadeOrganizacional());
        saldoSubConta.setSubConta(transporteSaldoFinanceiro.getConta());
        saldoSubConta.setTotalCredito(transporteSaldoFinanceiro.getCredito());
        saldoSubConta.setTotalDebito(transporteSaldoFinanceiro.getDebito());
        em.persist(saldoSubConta);
    }

    private void movimentarSaldoSubContaUsandoConfiguracao(TransporteSaldoFinanceiro transporteSaldoFinanceiro) {
        SaldoSubConta saldoSubConta = new SaldoSubConta();
        saldoSubConta.setDataSaldo(transporteSaldoFinanceiro.getData());
        saldoSubConta.setFonteDeRecursos(transporteSaldoFinanceiro.getConfiguracaoDestino().getContaDeDestinacao().getFonteDeRecursos());
        saldoSubConta.setContaDeDestinacao(transporteSaldoFinanceiro.getConfiguracaoDestino().getContaDeDestinacao());
        saldoSubConta.setUnidadeOrganizacional(transporteSaldoFinanceiro.getConfiguracaoDestino().getUnidadeOrganizacional());
        saldoSubConta.setSubConta(transporteSaldoFinanceiro.getConfiguracaoDestino().getSubConta());
        saldoSubConta.setTotalCredito(transporteSaldoFinanceiro.getCredito());
        saldoSubConta.setTotalDebito(transporteSaldoFinanceiro.getDebito());
        em.persist(saldoSubConta);
    }

    private FonteDeRecursos recuperarFonteDeRecurso(FonteDeRecursos fonte, Exercicio proximoExercicio) {
        return fonteDeRecursosFacade.recuperaPorCodigoExercicio(fonte, proximoExercicio);
    }

    private void transporteDeSaldoContabil() {
        if (!assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransporteDeSaldo().isEmpty()) {
            assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Transporte de saldo contábil.");
            Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getExercicio().getAno() + 1);
            PlanoDeContasExercicio planoDeContasExercicio = planoDeContasFacade.getPlanoDeContasExercicioFacade().recuperarPorExercicio(proximoExercicio);
            if (planoDeContasExercicio == null) {
                throw new ExcecaoNegocioGenerica("Nenhum Plano de Contas Exercício foi encontrado para o exercício de " + proximoExercicio.getAno() + ".");
            }
            if (planoDeContasExercicio.getPlanoContabil() == null) {
                throw new ExcecaoNegocioGenerica("Nenhum Plano de Contas Contábil foi encontrado para o Plano de Contas Exercício de " + proximoExercicio.getAno() + ".");
            }

            for (TransporteDeSaldoFechamentoExercicio transporte : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransporteDeSaldo()) {
                SaldoContaContabil saldo = new SaldoContaContabil();
                saldo.setContaContabil(recuperarContaContabilEquivalente(transporte.getConta(), proximoExercicio));
                saldo.setDataSaldo(recuperarDataDoTranporte(proximoExercicio));
                saldo.setTotalDebito(transporte.getDebito());
                saldo.setTotalCredito(transporte.getCredito());
                saldo.setTipoBalancete(TipoBalancete.TRANSPORTE);
                saldo.setUnidadeOrganizacional(transporte.getUnidadeOrganizacional());
                em.persist(saldo);
                contarRegistroProcessado();
            }
        }
    }

    private void transportarSaldoAuxiliar() {
        if (!assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransporteDeSaldoDeContasAuxiliares().isEmpty()) {
            assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Transportar saldo auxiliar.");
            Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getExercicio().getAno() + 1);
            PlanoDeContasExercicio planoDeContasExercicio = planoDeContasFacade.getPlanoDeContasExercicioFacade().recuperarPorExercicio(proximoExercicio);
            if (planoDeContasExercicio == null) {
                throw new ExcecaoNegocioGenerica("Nenhum Plano de Contas Exercício foi encontrado para o exercício de " + proximoExercicio.getAno() + ".");
            }

            for (TransporteSaldoContaAuxiliarDetalhada transporte : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getTransporteDeSaldoDeContasAuxiliares()) {
                ContaAuxiliarDetalhada contaAuxiliarDetalhada = tipoContaAuxiliarFacade.gerarMapContaAuxiliarDetalhada(transporte.getContaAuxiliarDetalhada().getTipoContaAuxiliar(),
                    (ContaContabil) recuperarContaContabilEquivalente(transporte.getContaAuxiliarDetalhada().getContaContabil(), proximoExercicio),
                    transporte.getNovaContaAuxiliarDetalhada(),
                    proximoExercicio);

                ContaAuxiliar contaAuxiliar = tipoContaAuxiliarFacade.gerarMapContaAuxiliar(transporte.getContaAuxiliarDetalhada().getTipoContaAuxiliar(),
                    (ContaContabil) recuperarContaContabilEquivalente(transporte.getContaAuxiliarDetalhada().getContaContabil(), proximoExercicio),
                    transporte.getContaAuxiliarSiconfi(),
                    proximoExercicio);

                gerarSaldoTransporte(proximoExercicio, transporte, contaAuxiliarDetalhada);
                gerarSaldoTransporte(proximoExercicio, transporte, contaAuxiliar);
                contarRegistroProcessado();
            }
        }
    }

    private void gerarSaldoTransporte(Exercicio proximoExercicio, TransporteSaldoContaAuxiliarDetalhada transporte, Conta conta) {
        SaldoContaContabil saldo = new SaldoContaContabil();
        saldo.setContaContabil(conta);
        saldo.setDataSaldo(recuperarDataDoTranporte(proximoExercicio));
        saldo.setTotalDebito(transporte.getDebito());
        saldo.setTotalCredito(transporte.getCredito());
        saldo.setTipoBalancete(TipoBalancete.TRANSPORTE);
        saldo.setUnidadeOrganizacional(transporte.getUnidadeOrganizacional());
        em.persist(saldo);
    }

    private Date recuperarDataDoTranporte(Exercicio proximoExercicio) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, proximoExercicio.getAno());
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Conta recuperarContaContabilEquivalente(Conta conta, Exercicio proximoExercicio) {
        conta = planoDeContasFacade.getContaFacade().recuperar(conta.getId());
        List<ContaContabil> contas = planoDeContasFacade.getContaFacade().recuperarContaContabilEquivalentePorId(conta, proximoExercicio);
        if (contas.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Nenhuma conta Equivalente foi encontrada para a Conta " + conta.getCodigo() + " no exercicio de " + proximoExercicio.getAno() + ".");
        }
        if (contas.size() > 1) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais que 1(UMA) conta Equivalente para a Conta " + conta.getCodigo() + " no exercicio de " + proximoExercicio.getAno() + ".");
        }
        return contas.get(0);
    }

    private Conta recuperarContaExtraEquivalente(Conta conta, Exercicio proximoExercicio) {
        conta = planoDeContasFacade.getContaFacade().recuperar(conta.getId());
        List<ContaExtraorcamentaria> contas = planoDeContasFacade.getContaFacade().recuperarContaExtraEquivalentePorId(conta, proximoExercicio);
        if (contas.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Nenhuma conta Equivalente foi encontrada para a Conta " + conta.getCodigo() + " no exercicio de " + proximoExercicio.getAno() + ".");
        }
        if (contas.size() > 1) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais que 1(UMA) conta Equivalente para a Conta " + conta.getCodigo() + " no exercicio de " + proximoExercicio.getAno() + ".");
        }
        return contas.get(0);
    }

    private void duplicarPlanoDeContas() {
        if (!assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getPlanoDeContas().isEmpty()) {
            assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Duplicar plano de contas.");

            Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getExercicio().getAno() + 1);
            for (PlanoDeContasFechamentoExercicio planoDeContasFechamentoExercicio : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getPlanoDeContas()) {

                PlanoDeContas planoDeContas = planoDeContasFacade.recuperar(planoDeContasFechamentoExercicio.getPlanoDeContas().getId());
                PlanoDeContas novoPlanoDeContas = planoDeContasFacade.salvarPlanoDuplicadoRetornando(planoDeContas, proximoExercicio);
                planoDeContasFechamentoExercicio.setPlanoDeContasNovo(novoPlanoDeContas);
                contarRegistroProcessado();
            }
        }
    }

    private void execucaoResultadoDiminutivoDoExercicio() {
        assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Execução do resultado diminutivo do exercício.");
        for (ContaFechamentoExercicio conta : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getResultadoDiminutivoExercicio()) {
            contabilizacaoExecucaoResultadoDoExercicio(conta);
            contarRegistroProcessado();
        }
    }

    private void execucaoResultadoAumentativoDoExercicio() {
        assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Execução do resultado aumentativo do exercício.");
        for (ContaFechamentoExercicio conta : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getResultadoAumentativoExercicio()) {
            contabilizacaoExecucaoResultadoDoExercicio(conta);
            contarRegistroProcessado();
        }
    }

    private void contabilizacaoExecucaoResultadoDoExercicio(ContaFechamentoExercicio conta) {
        if (conta.getEventoContabil() != null) {
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(conta.toString());
            parametroEvento.setDataEvento(conta.getAberturaFechamentoExercicio().getDataGeracao());
            parametroEvento.setUnidadeOrganizacional(conta.getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(conta.getEventoContabil());
            parametroEvento.setExercicio(conta.getAberturaFechamentoExercicio().getExercicio());
            parametroEvento.setIdOrigem(conta.getId().toString());
            parametroEvento.setClasseOrigem(conta.getClass().getSimpleName());
            parametroEvento.setTipoBalancete(TipoBalancete.ENCERRAMENTO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(conta.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> listaObj = new ArrayList<ObjetoParametro>();
            listaObj.add(new ObjetoParametro(conta, item));
            listaObj.add(new ObjetoParametro(conta.getConta(), item));

            item.setObjetoParametros(listaObj);
            parametroEvento.getItensParametrosEvento().add(item);
            contabilizacaoFacade.contabilizar(parametroEvento);
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado Evento Contábil para " + conta.toString() + ".");
        }
    }

    private void execucaoDisponibilidadeDeRecurso() {
        assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Execução de disponibilidade de recurso.");
        for (FonteDeRecursoFechamentoExercicio fonte : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getDestinacaoDeRecurso()) {

            if (fonte.getEventoContabil() != null) {
                ParametroEvento parametroEvento = new ParametroEvento();
                parametroEvento.setComplementoHistorico(fonte.toString());
                parametroEvento.setDataEvento(fonte.getAberturaFechamentoExercicio().getDataGeracao());
                parametroEvento.setUnidadeOrganizacional(fonte.getUnidadeOrganizacional());
                parametroEvento.setEventoContabil(fonte.getEventoContabil());
                parametroEvento.setExercicio(fonte.getAberturaFechamentoExercicio().getExercicio());
                parametroEvento.setIdOrigem(fonte.getId().toString());
                parametroEvento.setClasseOrigem(fonte.getClass().getSimpleName());
                parametroEvento.setTipoBalancete(TipoBalancete.ENCERRAMENTO);

                ItemParametroEvento item = new ItemParametroEvento();
                item.setValor(fonte.getValor());
                item.setParametroEvento(parametroEvento);
                item.setTagValor(TagValor.LANCAMENTO);

                List<ObjetoParametro> listaObj = new ArrayList<ObjetoParametro>();
                listaObj.add(new ObjetoParametro(fonte, item));
                listaObj.add(new ObjetoParametro(fonte.getFonteDeRecursos(), item));

                item.setObjetoParametros(listaObj);
                parametroEvento.getItensParametrosEvento().add(item);
                contabilizacaoFacade.contabilizar(parametroEvento);
            } else {
                throw new ExcecaoNegocioGenerica("Não foi encontrado Evento Contábil para " + fonte.toString() + ".");
            }
            contarRegistroProcessado();
        }
    }

    private void execucaoDeRestoAPagar() {
        assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Execução de resto a pagar.");
        for (DespesaFechamentoExercicio despesaFechamentoExercicio : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getEmpenhosALiquidarInscritosRestoAPagarNaoProcessados()) {
            contabilizaExecucaoDeRestoAPagar(despesaFechamentoExercicio);
            contarRegistroProcessado();
        }
        for (DespesaFechamentoExercicio despesaFechamentoExercicio : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getEmpenhosLiquidadosInscritosRestoAPagarProcessados()) {
            contabilizaExecucaoDeRestoAPagar(despesaFechamentoExercicio);
            contarRegistroProcessado();
        }
        for (DespesaFechamentoExercicio despesaFechamentoExercicio : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getEmpenhosCreditoEmpenhadoPago()) {
            contabilizaExecucaoDeRestoAPagar(despesaFechamentoExercicio);
            contarRegistroProcessado();
        }
        for (DespesaFechamentoExercicio despesaFechamentoExercicio : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getPagoDosRestosAPagarProcessados()) {
            contabilizaExecucaoDeRestoAPagar(despesaFechamentoExercicio);
            contarRegistroProcessado();
        }
        for (DespesaFechamentoExercicio despesaFechamentoExercicio : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getPagoDosRestosAPagarNaoProcessados()) {
            contabilizaExecucaoDeRestoAPagar(despesaFechamentoExercicio);
            contarRegistroProcessado();
        }
        for (DespesaFechamentoExercicio despesaFechamentoExercicio : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getCanceladoDosRestosAPagarProcessados()) {
            contabilizaExecucaoDeRestoAPagar(despesaFechamentoExercicio);
            contarRegistroProcessado();
        }
        for (DespesaFechamentoExercicio despesaFechamentoExercicio : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getCanceladoDosRestosAPagarNaoProcessados()) {
            contabilizaExecucaoDeRestoAPagar(despesaFechamentoExercicio);
            contarRegistroProcessado();
        }
    }

    private void contabilizaExecucaoDeRestoAPagar(DespesaFechamentoExercicio despesa) {
        if (despesa.getEventoContabil() != null) {
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(despesa.toString());
            parametroEvento.setDataEvento(despesa.getAberturaFechamentoExercicio().getDataGeracao());
            parametroEvento.setUnidadeOrganizacional(despesa.getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(despesa.getEventoContabil());
            parametroEvento.setExercicio(despesa.getAberturaFechamentoExercicio().getExercicio());
            parametroEvento.setIdOrigem(despesa.getId().toString());
            parametroEvento.setClasseOrigem(despesa.getClass().getSimpleName());
            parametroEvento.setTipoBalancete(TipoBalancete.ENCERRAMENTO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(despesa.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> listaObj = new ArrayList<ObjetoParametro>();
            listaObj.add(new ObjetoParametro(despesa, item));
            listaObj.add(new ObjetoParametro(despesa.getConta(), item));
            listaObj.add(new ObjetoParametro(despesa.getFonteDeRecursos(), item));
            listaObj.add(new ObjetoParametro(despesa.getEmpenho().getClasseCredor(), item));
            listaObj.add(new ObjetoParametro(despesa.getEmpenho().getFonteDespesaORC(), item));
            listaObj.add(new ObjetoParametro(despesa.getEmpenho().getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa(), item));

            item.setObjetoParametros(listaObj);
            parametroEvento.getItensParametrosEvento().add(item);
            contabilizacaoFacade.contabilizar(parametroEvento);
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado Evento Contábil para " + despesa.toString() + ".");
        }
    }

    private void execucaoDaDespesa() {
        assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Execução da despesa.");
        for (DotacaoFechamentoExercicio dotacao : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getDotacoes()) {
            contabilizaExecucaoDespesa(dotacao);
            contarRegistroProcessado();
        }
        for (DotacaoFechamentoExercicio dotacao : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getCreditosAdicionaisSuplementares()) {
            contabilizaExecucaoDespesa(dotacao);
            contarRegistroProcessado();
        }
        for (DotacaoFechamentoExercicio dotacao : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getCreditosAdicionaisEspecial()) {
            contabilizaExecucaoDespesa(dotacao);
            contarRegistroProcessado();
        }
        for (DotacaoFechamentoExercicio dotacao : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getCreditosAdicionaisExtraordinario()) {
            contabilizaExecucaoDespesa(dotacao);
            contarRegistroProcessado();
        }
        for (DotacaoFechamentoExercicio dotacao : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getAnulacaoDotacao()) {
            contabilizaExecucaoDespesa(dotacao);
            contarRegistroProcessado();
        }
        for (DotacaoFechamentoExercicio dotacao : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getCretitosAdicionaisPorSuperavitFinanceira()) {
            contabilizaExecucaoDespesa(dotacao);
            contarRegistroProcessado();
        }
        for (DotacaoFechamentoExercicio dotacao : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getCretitosAdicionaisPorExcessoDeArrecadacao()) {
            contabilizaExecucaoDespesa(dotacao);
            contarRegistroProcessado();
        }
        for (DotacaoFechamentoExercicio dotacao : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getCretitosAdicionaisPorAnulacaoDeDotacao()) {
            contabilizaExecucaoDespesa(dotacao);
            contarRegistroProcessado();
        }
        for (DotacaoFechamentoExercicio dotacao : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getCretitosAdicionaisPorOperacaoDeCredito()) {
            contabilizaExecucaoDespesa(dotacao);
            contarRegistroProcessado();
        }
        for (DotacaoFechamentoExercicio dotacao : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getCretitosAdicionaisPorReservaDeContigencia()) {
            contabilizaExecucaoDespesa(dotacao);
            contarRegistroProcessado();
        }
        for (DotacaoFechamentoExercicio dotacao : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getCretitosAdicionaisPorAnulacaoDeCredito()) {
            contabilizaExecucaoDespesa(dotacao);
            contarRegistroProcessado();
        }
    }

    private void contabilizaExecucaoDespesa(DotacaoFechamentoExercicio dotacao) {
        if (dotacao.getEventoContabil() != null) {
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(dotacao.toString());
            parametroEvento.setDataEvento(dotacao.getAberturaFechamentoExercicio().getDataGeracao());
            parametroEvento.setUnidadeOrganizacional(dotacao.getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(dotacao.getEventoContabil());
            parametroEvento.setExercicio(dotacao.getAberturaFechamentoExercicio().getExercicio());
            parametroEvento.setIdOrigem(dotacao.getId().toString());
            parametroEvento.setClasseOrigem(dotacao.getClass().getSimpleName());
            parametroEvento.setTipoBalancete(TipoBalancete.ENCERRAMENTO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(dotacao.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> listaObj = new ArrayList<ObjetoParametro>();
            listaObj.add(new ObjetoParametro(dotacao, item));
            listaObj.add(new ObjetoParametro(dotacao.getConta(), item));
            listaObj.add(new ObjetoParametro(dotacao.getFonteDeRecursos(), item));

            FonteDespesaORC fonteDespesaORC = empenhoFacade.getFonteDespesaORCFacade().recuperaPorContaFonteUnidade(dotacao.getConta(), dotacao.getUnidadeOrganizacional(), dotacao.getFonteDeRecursos(), dotacao.getSubProjetoAtividade(), dotacao.getProjetoAtividade());
            if (fonteDespesaORC != null) {
                listaObj.add(new ObjetoParametro(fonteDespesaORC, item));
            }
            item.setObjetoParametros(listaObj);
            parametroEvento.getItensParametrosEvento().add(item);
            contabilizacaoFacade.contabilizar(parametroEvento);
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado Evento Contábil para " + dotacao.toString() + ".");
        }
    }

    private void execucaoDaReceita() {
        assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Execução da receita.");
        for (ReceitaFechamentoExercicio receitaFechamentoExercicio : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getReceitasARealizar()) {
            contabilizaExecucaoReceita(receitaFechamentoExercicio);
            contarRegistroProcessado();
        }
        for (ReceitaFechamentoExercicio receitaFechamentoExercicio : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getReceitasReestimativas()) {
            contabilizaExecucaoReceita(receitaFechamentoExercicio);
            contarRegistroProcessado();
        }
        for (ReceitaFechamentoExercicio receitaFechamentoExercicio : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getReceitasDeducaoPrevisaoInicial()) {
            contabilizaExecucaoReceita(receitaFechamentoExercicio);
            contarRegistroProcessado();
        }
        for (ReceitaFechamentoExercicio receitaFechamentoExercicio : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getReceitasDeducaoReceitaRealizada()) {
            contabilizaExecucaoReceita(receitaFechamentoExercicio);
            contarRegistroProcessado();
        }
        for (ReceitaFechamentoExercicio receitaFechamentoExercicio : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getReceitasRealizada()) {
            contabilizaExecucaoReceita(receitaFechamentoExercicio);
            contarRegistroProcessado();
        }
    }

    private void contabilizaExecucaoReceita(ReceitaFechamentoExercicio receita) {
        if (receita.getEventoContabil() != null) {
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(receita.toString());
            parametroEvento.setDataEvento(receita.getAberturaFechamentoExercicio().getDataGeracao());
            parametroEvento.setUnidadeOrganizacional(receita.getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(receita.getEventoContabil());
            parametroEvento.setExercicio(receita.getAberturaFechamentoExercicio().getExercicio());
            parametroEvento.setIdOrigem(receita.getId().toString());
            parametroEvento.setClasseOrigem(receita.getClass().getSimpleName());
            parametroEvento.setTipoBalancete(TipoBalancete.ENCERRAMENTO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(receita.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> listaObj = new ArrayList<ObjetoParametro>();
            listaObj.add(new ObjetoParametro(receita, item));
            listaObj.add(new ObjetoParametro(receita.getConta(), item));
            listaObj.add(new ObjetoParametro(receita.getFonteDeRecursos(), item));

            item.setObjetoParametros(listaObj);
            parametroEvento.getItensParametrosEvento().add(item);
            contabilizacaoFacade.contabilizar(parametroEvento);
        } else {
            throw new ValidacaoException("Não foi encontrado Evento Contábil para " + receita.toString() + ".");
        }
    }

    private void prescricaoDeEmpenhoProcessadosENaoProcessados() {
        assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Prescrição de empenhos processados e não processados.");
        for (PrescricaoEmpenho prescricaoEmpenho : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getPrescricaoEmpenhosProcessados()) {
            contabilizacaoPrescricaoEmpenho(assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio(), prescricaoEmpenho);
            contarRegistroProcessado();
        }
        for (PrescricaoEmpenho prescricaoEmpenho : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getPrescricaoEmpenhosNaoProcessados()) {
            contabilizacaoPrescricaoEmpenho(assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio(), prescricaoEmpenho);
            contarRegistroProcessado();
        }
    }

    private void contabilizacaoPrescricaoEmpenho(AberturaFechamentoExercicio selecionado, PrescricaoEmpenho prescricaoEmpenho) {
        if (prescricaoEmpenho.getEventoContabil() == null) {
            throw new ExcecaoNegocioGenerica("O Evento contábil de Inscrição " + prescricaoEmpenho.getTipoRestos().getDescricao() + " não foi selecionado.");
        }
        Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getExercicio().getAno() + 1);
        EmpenhoEstorno empenhoEstorno = new EmpenhoEstorno();
        empenhoEstorno.setEventoContabil(prescricaoEmpenho.getEventoContabil());
        empenhoEstorno.setValor(prescricaoEmpenho.getValor());
        empenhoEstorno.setCategoriaOrcamentaria(CategoriaOrcamentaria.RESTO);
        empenhoEstorno.setTipoEmpenhoEstorno(TipoEmpenhoEstorno.PRESCRICAO);
        empenhoEstorno.setComplementoHistorico(prescricaoEmpenho.getEmpenho().getComplementoHistorico());
        empenhoEstorno.setDataEstorno(selecionado.getDataGeracao());
        empenhoEstorno.setEmpenho(prescricaoEmpenho.getEmpenho());
        empenhoEstorno.setUnidadeOrganizacional(prescricaoEmpenho.getEmpenho().getUnidadeOrganizacional());
        empenhoEstorno.setUnidadeOrganizacionalAdm(prescricaoEmpenho.getEmpenho().getUnidadeOrganizacionalAdm());
        empenhoEstorno.setExercicio(proximoExercicio);
        empenhoEstornoFacade.gerarNumeroEstornoEmpenho(empenhoEstorno);
        prescricaoEmpenho.setEmpenhoEstorno(empenhoEstorno);
        empenhoEstornoFacade.salvarNovoParaFechamentoExercicio(prescricaoEmpenho);
    }

    private void transportarObrigacoesAPagar(HashMap<ObrigacaoAPagar, Empenho> mapEmpenhoDaObrigacao) {
        if (!assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getObrigacoesAPagar().isEmpty()) {
            assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Transportando obrigações a pagar.");
            Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getExercicio().getAno() + 1);
            for (TransporteObrigacaoAPagar transporteObrigacaoAPagar : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getObrigacoesAPagar()) {
                contabilizacaoTransporteObrigacaoAPagar(transporteObrigacaoAPagar, proximoExercicio, mapEmpenhoDaObrigacao);
                contarRegistroProcessado();
            }
        }
    }

    private void contabilizacaoTransporteObrigacaoAPagar(TransporteObrigacaoAPagar transporteObrigacaoAPagar, Exercicio proximoExercicio, HashMap<ObrigacaoAPagar, Empenho> mapEmpenhoDaObrigacao) {
        if (transporteObrigacaoAPagar.getObrigacaoAPagar().getFonteDespesaORC() != null) {

            ObrigacaoAPagar obrigacaoAPagar = obrigacaoAPagarFacade.recuperar(transporteObrigacaoAPagar.getObrigacaoAPagar().getId());

            ObrigacaoAPagar novoObrigacaoAPagar = (ObrigacaoAPagar) Util.clonarObjeto(obrigacaoAPagar);

            novoObrigacaoAPagar.setId(null);
            novoObrigacaoAPagar.setObrigacaoAPagar(obrigacaoAPagar);
            novoObrigacaoAPagar.setValor(transporteObrigacaoAPagar.getValor());
            novoObrigacaoAPagar.setTransportado(Boolean.TRUE);
            novoObrigacaoAPagar.setSaldo(transporteObrigacaoAPagar.getObrigacaoAPagar().getSaldo());
            novoObrigacaoAPagar.setDataLancamento(recuperarDataDoTranporte(proximoExercicio));
            novoObrigacaoAPagar.setExercicio(proximoExercicio);
            novoObrigacaoAPagar.setContaDespesa(buscarContaEquivalente(obrigacaoAPagar.getContaDespesa(), proximoExercicio));
            novoObrigacaoAPagar.setFonteDeRecursos(buscarFonteEquivalente(obrigacaoAPagar.getFonteDeRecursos(), proximoExercicio));
            novoObrigacaoAPagar.setContaDeDestinacao((ContaDeDestinacao) buscarContaEquivalente(obrigacaoAPagar.getContaDeDestinacao(), proximoExercicio));
            novoObrigacaoAPagar.setDesdobramentos(duplicarDesdobramentoObrigacaoAPagar(obrigacaoAPagar, novoObrigacaoAPagar, proximoExercicio));
            novoObrigacaoAPagar.setDocumentosFiscais(duplicarDoctosFiscais(obrigacaoAPagar, novoObrigacaoAPagar));
            novoObrigacaoAPagar.setNumero(singletonGeradorCodigoContabil.getNumeroObrigacaoAPagar(novoObrigacaoAPagar.getExercicio(), novoObrigacaoAPagar.getUnidadeOrganizacional(), novoObrigacaoAPagar.getDataLancamento()));
            transporteObrigacaoAPagar.setObrigacaoAPagar(novoObrigacaoAPagar);
            if (obrigacaoAPagar.getEmpenho() != null) {
                mapEmpenhoDaObrigacao.put(novoObrigacaoAPagar, obrigacaoAPagar.getEmpenho());
            }
            obrigacaoAPagarFacade.salvarNovoParaFechamentoExercicio(transporteObrigacaoAPagar);
        } else {
            throw new ExcecaoNegocioGenerica("A Obrigação a Pagar " + transporteObrigacaoAPagar.getObrigacaoAPagar() + " não possui Despesa Orçamentária.");
        }
    }

    private Conta buscarContaEquivalente(Conta conta, Exercicio exercicioDestino) {
        List<Conta> contas = contaFacade.buscarContasEquivalentesPorIdOrigemAndExercicioAndDType(conta, exercicioDestino);
        if (contas != null && !contas.isEmpty()) {
            return contas.get(0);
        }
        throw new ExcecaoNegocioGenerica("Não existe Conta equivalente cadastrada para a Conta " + conta.getCodigo() + " em " + exercicioDestino.getAno() + ".");
    }

    private FonteDeRecursos buscarFonteEquivalente(FonteDeRecursos fonteDeRecursos, Exercicio proximoExercicio) {
        FonteDeRecursos fonteEquivalente = buscarFonteEquivalenteSemValidar(fonteDeRecursos, proximoExercicio);
        if (fonteEquivalente != null) {
            return fonteEquivalente;
        }
        throw new ExcecaoNegocioGenerica("Não existe Fonte de Recurso equivalente cadastrada para a Fonte de Recurso " + fonteDeRecursos.getCodigo() + " em " + proximoExercicio.getAno() + ".");
    }


    private FonteDeRecursos buscarFonteEquivalenteSemValidar(FonteDeRecursos fonteDeRecursos, Exercicio proximoExercicio) {
        List<FonteDeRecursos> fontesEquivalentes = fonteDeRecursosFacade.buscarFontesDeRecursoEquivalentesPorId(fonteDeRecursos, proximoExercicio);
        if (!fontesEquivalentes.isEmpty()) {
            return fontesEquivalentes.get(0);
        }
        return null;
    }

    private void inscricaoDeEmpenhoProcessadosENaoProcessados(HashMap<ObrigacaoAPagar, Empenho> mapEmpenhoDaObrigacao) {
        if (!assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getInscricaoEmpenhosProcessados().isEmpty() ||
            !assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getInscricaoEmpenhosNaoProcessados().isEmpty() ||
            !mapEmpenhoDaObrigacao.isEmpty()) {
            assistenteAberturaFechamentoExercicio.setDescricaoProcesso("Abertura e Fechamento de Exercício - Inscrição de empenhos processados e não processados.");
            Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getExercicio().getAno() + 1);
            for (InscricaoEmpenho inscricaoEmpenho : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getInscricaoEmpenhosProcessados()) {
                contabilizacaoInscricaoEmpenho(inscricaoEmpenho, proximoExercicio);
                contarRegistroProcessado();
            }
            for (InscricaoEmpenho inscricaoEmpenho : assistenteAberturaFechamentoExercicio.getAberturaFechamentoExercicio().getInscricaoEmpenhosNaoProcessados()) {
                contabilizacaoInscricaoEmpenho(inscricaoEmpenho, proximoExercicio);
                contarRegistroProcessado();
            }
            for (Map.Entry<ObrigacaoAPagar, Empenho> obrigacao : mapEmpenhoDaObrigacao.entrySet()) {
                obrigacao.getKey().setEmpenho(empenhoFacade.buscarEmpenhoNovoPorEmpenho(obrigacao.getValue()));
                obrigacaoAPagarFacade.salvar(obrigacao.getKey());
            }
        }
    }

    private void contabilizacaoInscricaoEmpenho(InscricaoEmpenho inscricaoEmpenho, Exercicio proximoExercicio) {
        if (inscricaoEmpenho.getEventoContabil() == null) {
            throw new ExcecaoNegocioGenerica("O Evento contábil de Inscrição " + inscricaoEmpenho.getTipoRestos().getDescricao() + " não foi selecionado.");
        }
        if (inscricaoEmpenho.getEmpenho().getFonteDespesaORC() != null) {

            Empenho e = empenhoFacade.recuperar(inscricaoEmpenho.getEmpenho().getId());

            Empenho novoEmpenho = (Empenho) Util.clonarObjeto(e);

            novoEmpenho.setId(null);
            novoEmpenho.setCategoriaOrcamentaria(CategoriaOrcamentaria.RESTO);
            novoEmpenho.setContrato(e.getContrato());
            novoEmpenho.setModalidadeLicitacao(e.getModalidadeLicitacao());
            novoEmpenho.setEmpenho(e);
            novoEmpenho.setTipoRestosProcessados(inscricaoEmpenho.getTipoRestos());
            novoEmpenho.setEventoContabil(inscricaoEmpenho.getEventoContabil());
            novoEmpenho.setValor(inscricaoEmpenho.getValor());
            novoEmpenho.setTransportado(Boolean.TRUE);
            novoEmpenho.setFonteDeRecursos(buscarFonteEquivalente(e.getFonteDeRecursos(), proximoExercicio));
            novoEmpenho.setContaDespesa((ContaDespesa) buscarContaEquivalente(e.getContaDespesa(), proximoExercicio));
            novoEmpenho.setCodigoContaTCE(novoEmpenho.getContaDespesa().getCodigoTCEOrCodigoSemPonto());
            novoEmpenho.setContaDeDestinacao((ContaDeDestinacao) buscarContaEquivalente(e.getContaDeDestinacao(), proximoExercicio));
            novoEmpenho.setSaldo(TipoRestosProcessado.NAO_PROCESSADOS.equals(inscricaoEmpenho.getTipoRestos()) ? inscricaoEmpenho.getValor() : BigDecimal.ZERO);
            novoEmpenho.setSaldoObrigacaoPagarAntesEmp(TipoRestosProcessado.NAO_PROCESSADOS.equals(inscricaoEmpenho.getTipoRestos()) ? inscricaoEmpenho.getSaldoObrigacaoPagarAntesEmp() : BigDecimal.ZERO);
            novoEmpenho.setSaldoObrigacaoPagarDepoisEmp(TipoRestosProcessado.NAO_PROCESSADOS.equals(inscricaoEmpenho.getTipoRestos()) ? inscricaoEmpenho.getSaldoObrigacaoPagarDepoisEmp() : BigDecimal.ZERO);
            novoEmpenho.setDataEmpenho(recuperarDataDoTranporte(proximoExercicio));
            novoEmpenho.setExercicio(proximoExercicio);
            novoEmpenho.setExercicioOriginal(e.getExercicio());
            novoEmpenho.setNumeroOriginal(e.getNumero());
            novoEmpenho.setDesdobramentos(new ArrayList<DesdobramentoEmpenho>());
            novoEmpenho.setEmpenhoEstornos(new ArrayList<EmpenhoEstorno>());
            novoEmpenho.setLiquidacoes(new ArrayList<Liquidacao>());
            novoEmpenho.setObrigacoesPagar(Lists.<EmpenhoObrigacaoPagar>newArrayList());
            novoEmpenho.setEmendas(Lists.newArrayList());
            if (!e.getEmendas().isEmpty()) {
                for (EmendaEmpenho emenda : e.getEmendas()) {
                    EmendaEmpenho novaEmenda = new EmendaEmpenho();
                    novaEmenda.setEmenda(emenda.getEmenda());
                    novaEmenda.setEmpenho(novoEmpenho);
                    novaEmenda.setValor(emenda.getValor());
                    novoEmpenho.getEmendas().add(novaEmenda);
                }
            }
            if (!e.getObrigacoesPagar().isEmpty()) {
                for (EmpenhoObrigacaoPagar empenhoObrigacaoPagarAntigo : e.getObrigacoesPagar()) {
                    EmpenhoObrigacaoPagar empenhoObrigacaoPagarNovo = new EmpenhoObrigacaoPagar();
                    empenhoObrigacaoPagarNovo.setEmpenho(novoEmpenho);
                    empenhoObrigacaoPagarNovo.setObrigacaoAPagar(obrigacaoAPagarFacade.buscarObrigacaoPagarNovaPorObrigacaoPagar(empenhoObrigacaoPagarAntigo.getObrigacaoAPagar()));
                    novoEmpenho.getObrigacoesPagar().add(empenhoObrigacaoPagarNovo);
                }
                novoEmpenho.setDesdobramentos(duplicarDesdobramentosEmpenho(e, novoEmpenho));
            } else if (!e.getDesdobramentos().isEmpty()) {
                novoEmpenho.setDesdobramentos(duplicarDesdobramentosEmpenho(e, novoEmpenho));
            }
            if (TipoRestosProcessado.PROCESSADOS.equals(inscricaoEmpenho.getTipoRestos())) {
                novoEmpenho.setLiquidacoes(duplicarLiquidacoes(e, novoEmpenho));
            }
            empenhoFacade.geraNumeroEmpenho(novoEmpenho);
            inscricaoEmpenho.setEmpenhoInscrito(novoEmpenho);
            empenhoFacade.salvarNovoParaFechamentoExercicio(inscricaoEmpenho);
        } else {
            throw new ExcecaoNegocioGenerica("O Empenho " + inscricaoEmpenho.getEmpenho() + " não possiu Despesa Orçamentária.");
        }
    }

    public List<MovimentoDespesaORC> recuperarMovimentosDespesaOrc(TipoOperacaoORC tipoOperacaoORC, OperacaoORC operacaoORC) {
        try {
            String sql = "select m.* from movimentodespesaorc m " +
                " where to_char(m.datamovimento,'yyyy') = '2014' " +
                " and m.operacaoorc = '" + operacaoORC.name() + "' " +
                " and m.tipooperacaoorc = '" + tipoOperacaoORC.name() + "' " +
                " order by m.id ";
            Query consulta = em.createNativeQuery(sql, MovimentoDespesaORC.class);
            return (List<MovimentoDespesaORC>) consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<MovimentoDespesaORC>();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<EntidadeContabil> recuperarMovimentosApuracaoReprocessamento(Exercicio exercicio, EventosReprocessar er) {
        try {
            String sql = "select i.* from inscricaoempenho i " +
                " inner join aberturafechaexercicio ab on i.aberturafechamentoexercicio_id = ab.id " +
                " inner join empenho e on i.empenho_id = e.id " +
                " inner join exercicio ex on ab.exercicio_id = ex.id " +
                " where ex.id = :exercicio " +
                " and trunc(ab.dataGeracao) between to_date(:di,'dd/mm/yyyy') and to_date(:df,'dd/mm/yyyy')";
            if (er.hasUnidades()) {
                sql += " and e.unidadeorganizacional_id in (:unidades) ";
            }
            Query consulta = em.createNativeQuery(sql, InscricaoEmpenho.class);
            consulta.setParameter("exercicio", exercicio.getId());
            consulta.setParameter("di", DataUtil.getDataFormatada(er.getDataInicial()));
            consulta.setParameter("df", DataUtil.getDataFormatada(er.getDataFinal()));
            if (er.hasUnidades()) {
                consulta.setParameter("unidades", er.getIdUnidades());
            }
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<EntidadeContabil> recuperarMovimentosApuracaoPrescricaoReprocessamento(Exercicio exercicio, EventosReprocessar er) {
        try {
            String sql = "select est.* from PrescricaoEmpenho i " +
                " inner join aberturafechaexercicio ab on i.aberturafechamentoexercicio_id = ab.id " +
                " inner join empenhoestorno est on i.empenhoEstorno_id = est.id " +
                " inner join exercicio ex on ab.exercicio_id = ex.id " +
                " where ex.id = :exercicio " +
                " and trunc(ab.dataGeracao) between to_date(:di,'dd/mm/yyyy') and to_date(:df,'dd/mm/yyyy')";
            if (er.hasUnidades()) {
                sql += " and est.unidadeorganizacional_id in (:unidades) ";
            }
            Query consulta = em.createNativeQuery(sql, EmpenhoEstorno.class);
            consulta.setParameter("exercicio", exercicio.getId());
            consulta.setParameter("di", DataUtil.getDataFormatada(er.getDataInicial()));
            consulta.setParameter("df", DataUtil.getDataFormatada(er.getDataFinal()));
            if (er.hasUnidades()) {
                consulta.setParameter("unidades", er.getIdUnidades());
            }
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<EntidadeContabil> recuperarMovimentosTransporteDeSaldoAbertura(Exercicio exercicio, Date dataInicial, Date dataFinal) {
        try {
            String sql = "select i.* from TransporteDeSaldoAbertura i " +
                " inner join aberturafechaexercicio ab on i.aberturafechamentoexercicio_id = ab.id " +
                " inner join exercicio ex on ab.exercicio_id = ex.id " +
                " where ex.id = :exercicio " +
                " and trunc(ab.dataGeracao) between to_date(:di,'dd/mm/yyyy') and to_date(:df,'dd/mm/yyyy')";
            Query consulta = em.createNativeQuery(sql, TransporteDeSaldoAbertura.class);
            consulta.setParameter("exercicio", exercicio.getId());
            consulta.setParameter("di", DataUtil.getDataFormatada(dataInicial));
            consulta.setParameter("df", DataUtil.getDataFormatada(dataFinal));
            return consulta.getResultList();

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        if (entidadeContabil instanceof InscricaoEmpenho) {
            InscricaoEmpenho inscricaoEmpenho = (InscricaoEmpenho) entidadeContabil;
            Empenho empenho = inscricaoEmpenho.getEmpenho();
            empenho = empenhoFacade.recuperar(empenho.getId());
            inscricaoEmpenho.setEmpenho(empenho);

            EventoContabil evento = recuperarEvento(inscricaoEmpenho);
            inscricaoEmpenho.setEventoContabil(evento);
            empenhoFacade.contabilizarEmpenhoParaFechamentoDeExercicio(empenho, inscricaoEmpenho);
        } else if (entidadeContabil instanceof EmpenhoEstorno) {
            empenhoEstornoFacade.contabilizarEmpenhoEstornoParaFechamentoDeExercicio((EmpenhoEstorno) entidadeContabil);
        } else if (entidadeContabil instanceof AberturaInscricaoResto) {
            AberturaInscricaoResto transporteAberturaInscricao = (AberturaInscricaoResto) entidadeContabil;
            contabilizacaoAberturaRestoAPagar(transporteAberturaInscricao, transporteAberturaInscricao.getData(), transporteAberturaInscricao.getAberturaFechamentoExercicio().getExercicio());
        } else if (entidadeContabil instanceof TransporteDeSaldoAbertura) {
            TransporteDeSaldoAbertura transporteDeSaldoAbertura = (TransporteDeSaldoAbertura) entidadeContabil;
            atualizarEventoAberturaTransferencia(transporteDeSaldoAbertura);
            contabilizarTransporteSaldoAbertura(transporteDeSaldoAbertura, transporteDeSaldoAbertura.getAberturaFechamentoExercicio().getDataGeracao(), transporteDeSaldoAbertura.getAberturaFechamentoExercicio().getExercicio());
        }
    }

    private void atualizarEventoAberturaTransferencia(TransporteDeSaldoAbertura transporteDeSaldoAbertura) {
        transporteDeSaldoAbertura.setEventoContabil(buscarEventoPelaConfiguracaoVerificandoNatureza(transporteDeSaldoAbertura.getTipoMovimentoContabil(),
            transporteDeSaldoAbertura.getUnidadeOrganizacional(),
            transporteDeSaldoAbertura.getData()));
    }

    private EventoContabil buscarEventoPelaConfiguracaoVerificandoNatureza(TipoMovimentoContabil receitaARealizarPositivoDevedor, UnidadeOrganizacional unidadeOrganizacional, Date data) {
        return getConfigAberturaFechamentoExercicioFacade().buscarEventoAberturaFechamentoExercicio(
            data,
            PatrimonioLiquido.buscarPorNatureza(getEntidadeFacade().recuperarEntidadePorUnidadeOrcamentaria(unidadeOrganizacional).getTipoUnidade()),
            receitaARealizarPositivoDevedor);
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        return Lists.newArrayList();
    }

    private EventoContabil recuperarEvento(InscricaoEmpenho inscricaoEmpenho) {
        if (TipoRestosProcessado.PROCESSADOS.equals(inscricaoEmpenho.getTipoRestos())) {
            return getConfigEmpenhoRestoFacade().recuperarEventoPorContaDespesa(
                inscricaoEmpenho.getEmpenho().getDespesaORC().getContaDeDespesa(),
                TipoLancamento.NORMAL,
                inscricaoEmpenho.getEmpenho().getDataEmpenho(),
                inscricaoEmpenho.getTipoRestos(),
                !inscricaoEmpenho.getEmpenho().getObrigacoesPagar().isEmpty()
            ).getEventoContabil();
        } else if (TipoRestosProcessado.NAO_PROCESSADOS.equals(inscricaoEmpenho.getTipoRestos())) {
            return getConfigEmpenhoRestoFacade().recuperarEventoPorContaDespesa(
                inscricaoEmpenho.getEmpenho().getDespesaORC().getContaDeDespesa(),
                TipoLancamento.NORMAL,
                inscricaoEmpenho.getEmpenho().getDataEmpenho(),
                inscricaoEmpenho.getTipoRestos(),
                !inscricaoEmpenho.getEmpenho().getObrigacoesPagar().isEmpty()
            ).getEventoContabil();
        } else {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar o Evento para a Inscrição de Empenho " + inscricaoEmpenho);
        }
    }


    private List<Liquidacao> duplicarLiquidacoes(Empenho empenhoantigo, Empenho novoEmpenho) {
        List<Liquidacao> retorno = Lists.newArrayList();
        empenhoantigo = empenhoFacade.recuperarComFind(empenhoantigo.getId());
        for (Liquidacao liquidacaoAntiga : empenhoantigo.getLiquidacoes()) {
            if (liquidacaoAntiga.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                Liquidacao liquidacao = (Liquidacao) Util.clonarObjeto(liquidacaoAntiga);
                liquidacao.setId(null);
                liquidacao.setDataLiquidacao(novoEmpenho.getDataEmpenho());
                liquidacao.setExercicio(novoEmpenho.getExercicio());
                liquidacao.setExercicioOriginal(empenhoantigo.getExercicio());
                liquidacao.setCategoriaOrcamentaria(CategoriaOrcamentaria.RESTO);
                liquidacao.setSaldo(liquidacaoAntiga.getSaldo());
                liquidacao.setValor(liquidacaoAntiga.getSaldo());
                liquidacao.setTransportado(Boolean.TRUE);
                liquidacao.setEmpenho(novoEmpenho);
                liquidacao.setLiquidacao(liquidacaoAntiga);
                liquidacao.setMovimentoDespesaORC(null);
                liquidacao.setNumeroOriginal(liquidacaoAntiga.getNumero());
                empenhoFacade.getLiquidacaoFacade().geraNumeroLiquidacao(liquidacao);
                liquidacao.setLiquidacaoEstornos(Lists.<LiquidacaoEstorno>newArrayList());
                liquidacao.setPagamentos(Lists.<Pagamento>newArrayList());
                liquidacao.setObrigacoesPagar(Lists.<LiquidacaoObrigacaoPagar>newArrayList());
                liquidacao.setDesdobramentos(duplicarDesdobramento(liquidacaoAntiga, liquidacao));
                liquidacao.setDoctoFiscais(duplicarDoctosFiscais(liquidacaoAntiga, liquidacao));
                retorno.add(liquidacao);
            }

        }
        return retorno;
    }

    private List<LiquidacaoDoctoFiscal> duplicarDoctosFiscais(Liquidacao liquidacaoAntiga, Liquidacao liquidacaoNova) {
        List<LiquidacaoDoctoFiscal> retorno = Lists.newArrayList();
        for (LiquidacaoDoctoFiscal doctoAntigo : liquidacaoAntiga.getDoctoFiscais()) {
            LiquidacaoDoctoFiscal liquidacaoDoctoFiscal = new LiquidacaoDoctoFiscal();
            liquidacaoDoctoFiscal.setLiquidacao(liquidacaoNova);
            liquidacaoDoctoFiscal.setValorLiquidado(doctoAntigo.getValorLiquidado());
            liquidacaoDoctoFiscal.setSaldo(doctoAntigo.getSaldo());
            liquidacaoDoctoFiscal.setDoctoFiscalLiquidacao(doctoAntigo.getDoctoFiscalLiquidacao());
            retorno.add(liquidacaoDoctoFiscal);
        }
        return retorno;
    }

    private List<ObrigacaoAPagarDoctoFiscal> duplicarDoctosFiscais(ObrigacaoAPagar obrigacaoAPagarAntiga, ObrigacaoAPagar obrigacaoAPagarNova) {
        List<ObrigacaoAPagarDoctoFiscal> retorno = Lists.newArrayList();
        for (ObrigacaoAPagarDoctoFiscal doctoAntigo : obrigacaoAPagarAntiga.getDocumentosFiscais()) {
            ObrigacaoAPagarDoctoFiscal obrigacaoAPagarDoctoFiscal = new ObrigacaoAPagarDoctoFiscal();
            obrigacaoAPagarDoctoFiscal.setObrigacaoAPagar(obrigacaoAPagarNova);
            obrigacaoAPagarDoctoFiscal.setValor(doctoAntigo.getValor());
            obrigacaoAPagarDoctoFiscal.setSaldo(doctoAntigo.getSaldo());
            obrigacaoAPagarDoctoFiscal.setDocumentoFiscal(doctoAntigo.getDocumentoFiscal());
            retorno.add(obrigacaoAPagarDoctoFiscal);
        }
        return retorno;
    }

    private List<Desdobramento> duplicarDesdobramento(Liquidacao liquidacaoAntiga, Liquidacao liquidacaoNova) {
        List<Desdobramento> retorno = Lists.newArrayList();
        for (Desdobramento desdobramentoAntigo : liquidacaoAntiga.getDesdobramentos()) {
            Desdobramento desdobramento = new Desdobramento();
            desdobramento.setLiquidacao(liquidacaoNova);
            desdobramento.setEmpenho(liquidacaoNova.getEmpenho());
            desdobramento.setValor(liquidacaoNova.getValor());
            desdobramento.setConta(desdobramentoAntigo.getConta());
            desdobramento.setSaldo(liquidacaoNova.getSaldo());
            desdobramento.setDesdobramento(desdobramentoAntigo);
            if (liquidacaoNova.getEmpenho() != null &&
                liquidacaoNova.getEmpenho().getDesdobramentos() != null &&
                !liquidacaoNova.getEmpenho().getDesdobramentos().isEmpty()) {
                for (DesdobramentoEmpenho desdobramentoEmpenho : liquidacaoNova.getEmpenho().getDesdobramentos()) {
                    if (desdobramentoEmpenho.getConta() != null && desdobramento.getConta() != null
                        && desdobramento.getConta().getCodigo().equals(desdobramentoEmpenho.getConta().getCodigo())) {
                        desdobramento.setDesdobramentoEmpenho(desdobramentoEmpenho);
                    }
                }
            }
            if (desdobramentoAntigo.getDesdobramentoObrigacaoPagar() != null) {
                desdobramento.setDesdobramentoObrigacaoPagar(obrigacaoAPagarFacade.buscarDesdobramentobrigacaoPagarNovaPorDesdobramentoObrigacaoPagar(desdobramentoAntigo.getDesdobramentoObrigacaoPagar()));
            }
            retorno.add(desdobramento);
        }

        return retorno;
    }

    private List<DesdobramentoEmpenho> duplicarDesdobramentosEmpenho(Empenho empenhoAntigo, Empenho empenhoNovo) {
        List<DesdobramentoEmpenho> retorno = Lists.newArrayList();
        for (DesdobramentoEmpenho desdobramentoAntigo : empenhoAntigo.getDesdobramentos()) {
            DesdobramentoEmpenho desdobramento = new DesdobramentoEmpenho();
            desdobramento.setEmpenho(empenhoNovo);
            desdobramento.setValor(empenhoNovo.getValor());
            desdobramento.setConta(desdobramentoAntigo.getConta());
            desdobramento.setSaldo(empenhoNovo.getSaldo());
            if (desdobramentoAntigo.getDesdobramentoObrigacaoPagar() != null) {
                desdobramento.setDesdobramentoObrigacaoPagar(obrigacaoAPagarFacade.buscarDesdobramentobrigacaoPagarNovaPorDesdobramentoObrigacaoPagar(desdobramentoAntigo.getDesdobramentoObrigacaoPagar()));
            }
            desdobramento.setDesdobramentoEmpenho(desdobramentoAntigo);
            retorno.add(desdobramento);
        }

        return retorno;
    }

    private List<DesdobramentoObrigacaoPagar> duplicarDesdobramentoObrigacaoAPagar(ObrigacaoAPagar obrigacaoAPagarAntiga, ObrigacaoAPagar obrigacaoAPagarNova, Exercicio proximoExercicio) {
        List<DesdobramentoObrigacaoPagar> retorno = Lists.newArrayList();
        for (DesdobramentoObrigacaoPagar desdobramentoAntigo : obrigacaoAPagarAntiga.getDesdobramentos()) {
            DesdobramentoObrigacaoPagar desdobramento = new DesdobramentoObrigacaoPagar();
            desdobramento.setObrigacaoAPagar(obrigacaoAPagarNova);
            desdobramento.setValor(desdobramentoAntigo.getValor());
            desdobramento.setSaldo(desdobramentoAntigo.getSaldo());
            desdobramento.setConta(buscarContaEquivalente(desdobramentoAntigo.getConta(), proximoExercicio));
            desdobramento.setEventoContabil(desdobramentoAntigo.getEventoContabil());
            desdobramento.setDesdobramentoObrigacaoPagar(desdobramentoAntigo);
            retorno.add(desdobramento);
        }
        return retorno;
    }

    public List<TransporteSaldoFinanceiro> recuperarSaldoFinanceiro(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaDeUnidades) {
        String sql = getSqlSaldoFinanceiro() + getUnidades(listaDeUnidades);
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("exercicio", selecionado.getExercicio().getId());
        return criarTransporteDeSaldoFinanceiro(selecionado, consulta.getResultList());
    }

    public String getSqlSaldoFinanceiro() {
        return "SELECT  " +
            " VWORGAO.ID as orgao, " +
            " VW.id as unidade, " +
            " s.totalcredito, " +
            " s.totaldebito, " +
            " cf.id as conta, " +
            " fr.id as fonte, " +
            " s.contaDeDestinacao_id as contaDeDestinacao " +
            " FROM SALDOSUBCONTA S " +
            " inner join SUBCONTA cf on S.SUBCONTA_ID = cf.id " +
            " inner join fontederecursos fr on s.fontederecursos_id = fr.id " +
            " inner join (select trunc(max(saldo.datasaldo)) as datasaldo, saldo.subconta_id, saldo.unidadeorganizacional_id, saldo.fontederecursos_id from SALDOSUBCONTA saldo  " +
            " group by saldo.subconta_id, saldo.unidadeorganizacional_id, saldo.fontederecursos_id) maiorData " +
            " on s.subconta_id = maiorData.subconta_id  " +
            " and s.unidadeorganizacional_id = maiorData.unidadeorganizacional_id " +
            " and s.fontederecursos_id = maiordata.fontederecursos_id " +
            " and trunc(maiordata.datasaldo) = trunc(s.datasaldo) " +
            "   INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.unidadeorganizacional_id = VW.SUBORDINADA_ID " +
            "   INNER JOIN VWHIERARQUIAORCAMENTARIA VWORGAO ON VW.SUPERIOR_ID = VWORGAO.SUBORDINADA_ID" +
            "   AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy')) " +
            "   AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VWORGAO.INICIOVIGENCIA AND COALESCE(VWORGAO.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy')) " +
            " WHERE FR.EXERCICIO_ID = :exercicio " +
            "   and (S.TOTALCREDITO <> 0 or S.TOTALDEBITO <> 0) and (S.TOTALCREDITO - S.totaldebito <> 0) ";
    }

    private List<TransporteSaldoFinanceiro> criarTransporteDeSaldoFinanceiro(AberturaFechamentoExercicio selecionado, List resultList) {
        List<TransporteSaldoFinanceiro> retorno = Lists.newArrayList();
        Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(selecionado.getExercicio().getAno() + 1);
        Date data = recuperarDataDoTranporte(proximoExercicio);
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            List<TransporteSaldoFinanceiro> transportesDasConfiguracoes = buscarTransporteSaldoSubContaUsandoAConfiguracao(objeto, selecionado, proximoExercicio, data);
            if (!transportesDasConfiguracoes.isEmpty()) {
                retorno.addAll(transportesDasConfiguracoes);
            } else {
                retorno.add(montarTransporteSaldoSubContaPadrao(objeto, selecionado, proximoExercicio, data));
            }
        }
        return retorno;
    }

    private TransporteSaldoFinanceiro montarTransporteSaldoSubContaPadrao(Object[] objeto, AberturaFechamentoExercicio selecionado, Exercicio proximoExercicio, Date data) {
        HierarquiaOrganizacional orgao = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[0]).longValue());
        HierarquiaOrganizacional unidade = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
        BigDecimal credito = (BigDecimal) objeto[2];
        BigDecimal debito = (BigDecimal) objeto[3];
        SubConta conta = em.find(SubConta.class, ((BigDecimal) objeto[4]).longValue());
        FonteDeRecursos fonte = em.find(FonteDeRecursos.class, ((BigDecimal) objeto[5]).longValue());
        ContaDeDestinacao contaDeDestinacao = em.find(ContaDeDestinacao.class, ((BigDecimal) objeto[6]).longValue());
        String mensagemDeErro = "";
        if (buscarFonteEquivalenteSemValidar(fonte, proximoExercicio) == null) {
            mensagemDeErro = " Não existe Fonte de Recurso equivalente cadastrada para a Fonte de Recurso " + fonte.getCodigo() + " em " + proximoExercicio.getAno() + ".";
        }
        if (hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(data, unidade.getSubordinada(), TipoHierarquiaOrganizacional.ORCAMENTARIA) == null) {
            mensagemDeErro += " Não existe Hierarquia Organizacional Orçamentária com o código " + unidade.getCodigo() + " vigente em " + DataUtil.getDataFormatada(data) + ".";
        }
        return new TransporteSaldoFinanceiro(orgao, unidade, unidade.getSubordinada(), conta, fonte, contaDeDestinacao, credito, debito, data, selecionado, !mensagemDeErro.isEmpty(), mensagemDeErro);
    }

    private List<TransporteSaldoFinanceiro> buscarTransporteSaldoSubContaUsandoAConfiguracao(Object[] objeto, AberturaFechamentoExercicio selecionado, Exercicio proximoExercicio, Date data) {
        HierarquiaOrganizacional unidade = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
        ContaDeDestinacao contaDeDestinacao = em.find(ContaDeDestinacao.class, ((BigDecimal) objeto[6]).longValue());
        BigDecimal credito = (BigDecimal) objeto[2];
        BigDecimal debito = (BigDecimal) objeto[3];
        List<TransporteSaldoFinanceiro> retorno = Lists.newArrayList();
        List<ConfiguracaoTransporteSaldoSubContaDestino> destinos = configuracaoTransporteSaldoSubContaFacade.buscarDestinosPorSubContaUnidadeContaDeDestinacaoEValorDaOrigem(
            ((BigDecimal) objeto[4]).longValue(), unidade.getSubordinada().getId(), contaDeDestinacao.getId(), credito.subtract(debito), proximoExercicio
        );
        if (destinos != null && !destinos.isEmpty()) {
            for (ConfiguracaoTransporteSaldoSubContaDestino destino : destinos) {
                destino.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(data, destino.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA));
                HierarquiaOrganizacional hoOrgao = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(data, destino.getHierarquiaOrganizacional().getSuperior(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
                Conta contaEquivalente = buscarContaEquivalente(contaDeDestinacao, proximoExercicio);
                //Se o saldo for continuar com a mesma conta equivalente ele deve manter os valores de debito e credito anteriores debitando o valor remanecente caso exista mais de 1 destino.
                if (contaEquivalente != null && contaEquivalente.equals(destino.getContaDeDestinacao())) {
                    BigDecimal valorADebitar = destino.getConfiguracaoOrigem().getValor().subtract(destino.getValor());
                    retorno.add(new TransporteSaldoFinanceiro(destino, data, selecionado, hoOrgao, credito, debito.add(valorADebitar)));
                } else {
                    retorno.add(new TransporteSaldoFinanceiro(destino, data, selecionado, hoOrgao, destino.getValor(), BigDecimal.ZERO));
                }
            }
        }
        return retorno;
    }

    public List<TransporteCreditoReceber> recuperarSaldoCreditoReceber(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaDeUnidades) {
        String sql = "SELECT " +
            " VWORGAO.ID as orgao, " +
            " VW.id as unidade, " +
            " s.contareceita_id, " +
            " s.RECONHECIMENTOCREDITO," +
            " s.DEDUCAORECONHECIMENTO, " +
            " s.PROVISAOPERDACREDITO, " +
            " s.REVERSAOPROVISAOPERDACREDITO, " +
            " s.BAIXARECONHECIMENTO, " +
            " s.BAIXADEDUCAO, " +
            " s.RECEBIMENTO, " +
            " s.atualizacao," +
            " s.AUMENTATIVO," +
            " s.DIMINUTIVO, " +
            " s.naturezaCreditoReceber, " +
            " s.intervalo, " +
            " s.contaDeDestinacao_id " +
            " FROM SALDOCREDITORECEBER S " +
            " inner join conta c on S.CONTARECEITA_ID = c.id " +
            " INNER JOIN (SELECT trunc(MAX(saldo.datasaldo)) AS DATASALDO, SALDO.CONTARECEITA_ID, SALDO.UNIDADEORGANIZACIONAL_ID," +
            "                saldo.naturezaCreditoReceber, " +
            "                saldo.intervalo, " +
            "                saldo.contaDeDestinacao_id " +
            "           FROM SALDOCREDITORECEBER SALDO  " +
            "           GROUP BY SALDO.CONTARECEITA_ID, SALDO.UNIDADEORGANIZACIONAL_ID, saldo.naturezaCreditoReceber,saldo.intervalo, saldo.contaDeDestinacao_id) MAIORDATA " +
            "      ON S.contareceita_id = MAIORDATA.contareceita_id " +
            "      AND S.UNIDADEORGANIZACIONAL_ID = MAIORDATA.UNIDADEORGANIZACIONAL_ID " +
            "      and trunc(maiordata.datasaldo) = trunc(s.datasaldo) " +
            "      and s.naturezaCreditoReceber = maiordata.naturezaCreditoReceber " +
            "      and s.intervalo = maiordata.intervalo " +
            "      and s.contaDeDestinacao_id  = maiordata.contaDeDestinacao_id " +
            "   INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.unidadeorganizacional_id = VW.SUBORDINADA_ID " +
            "   INNER JOIN VWHIERARQUIAORCAMENTARIA VWORGAO ON VW.SUPERIOR_ID = VWORGAO.SUBORDINADA_ID" +
            "   AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy')) " +
            "   AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VWORGAO.INICIOVIGENCIA AND COALESCE(VWORGAO.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy')) " +
            " WHERE C.EXERCICIO_ID = :exercicio " + getUnidades(listaDeUnidades) +
            " and ((s.RECONHECIMENTOCREDITO + s.REVERSAOPROVISAOPERDACREDITO + BAIXADEDUCAO + AUMENTATIVO + atualizacao) - (DEDUCAORECONHECIMENTO + PROVISAOPERDACREDITO + BAIXARECONHECIMENTO + RECEBIMENTO + DIMINUTIVO)) <> 0 " +
            " order by s.UNIDADEORGANIZACIONAL_ID, s.contareceita_id ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("exercicio", selecionado.getExercicio().getId());
        return criarTransporteCreditoReceber(selecionado, consulta.getResultList());

    }

    private List<TransporteCreditoReceber> criarTransporteCreditoReceber(AberturaFechamentoExercicio selecionado, List resultList) {
        List<TransporteCreditoReceber> retorno = Lists.newArrayList();
        Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(selecionado.getExercicio().getAno() + 1);
        Date data = recuperarDataDoTranporte(proximoExercicio);
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            HierarquiaOrganizacional orgao = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[0]).longValue());
            HierarquiaOrganizacional unidade = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
            Conta conta = em.find(Conta.class, ((BigDecimal) objeto[2]).longValue());
            BigDecimal reconhecimentoCredito = (BigDecimal) objeto[3];
            BigDecimal deducaoReconhecimento = (BigDecimal) objeto[4];
            BigDecimal provisao = (BigDecimal) objeto[5];
            BigDecimal reversao = (BigDecimal) objeto[6];
            BigDecimal baixareconhecimento = (BigDecimal) objeto[7];
            BigDecimal baixadeducao = (BigDecimal) objeto[8];
            BigDecimal recebimento = (BigDecimal) objeto[9];
            BigDecimal atualizacao = (BigDecimal) objeto[10];
            BigDecimal aumentativo = (BigDecimal) objeto[11];
            BigDecimal diminutivo = (BigDecimal) objeto[12];
            BigDecimal saldo = (reconhecimentoCredito.add(reversao).add(baixadeducao).add(atualizacao).add(aumentativo)).subtract((recebimento.add(deducaoReconhecimento).add(provisao).add(baixareconhecimento).add(diminutivo)));
            NaturezaDividaAtivaCreditoReceber naturezaCreditoReceber = NaturezaDividaAtivaCreditoReceber.valueOf((String) objeto[13]);
            Intervalo intervalo = Intervalo.valueOf((String) objeto[14]);
            ContaDeDestinacao contaDeDestinacao = em.find(ContaDeDestinacao.class, ((BigDecimal) objeto[15]).longValue());
            if (BigDecimal.ZERO.compareTo(saldo) != 0) {
                retorno.add(new TransporteCreditoReceber(orgao, unidade, unidade.getSubordinada(),
                    conta, reconhecimentoCredito, deducaoReconhecimento,
                    provisao, reversao, baixareconhecimento, baixadeducao, recebimento, atualizacao, data, selecionado,
                    contaDeDestinacao, naturezaCreditoReceber, intervalo, saldo, aumentativo, diminutivo));
            }
        }
        return retorno;
    }

    public List<TransporteDividaAtiva> recuperarSaldoDividaAtiva(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaDeUnidades) {
        String sql = "SELECT " +
            " VWORGAO.ID as orgao, " +
            " VW.id as unidade, " +
            " s.contareceita_id, " +
            " s.INSCRICAO, " +
            " s.ATUALIZACAO, " +
            " s.PROVISAO, " +
            " s.REVERSAO, " +
            " s.RECEBIMENTO,  " +
            " s.BAIXA,  " +
            " s.AUMENTATIVO,  " +
            " s.DIMINUTIVO, " +
            " s.naturezaDividaAtiva, " +
            " s.intervalo, " +
            " s.contaDeDestinacao_id " +
            " FROM SALDODIVIDAATIVACONTABIL S " +
            " inner join conta c on S.CONTARECEITA_ID = c.id " +
            " INNER JOIN (SELECT trunc(MAX(SALDO.datasaldo)) AS DATASALDO, SALDO.CONTARECEITA_ID, SALDO.UNIDADEORGANIZACIONAL_ID,  " +
            "                saldo.naturezaDividaAtiva, " +
            "                saldo.intervalo, " +
            "                saldo.contaDeDestinacao_id " +
            "               FROM SALDODIVIDAATIVACONTABIL SALDO  " +
            "               GROUP BY SALDO.CONTARECEITA_ID, SALDO.UNIDADEORGANIZACIONAL_ID, " +
            "                saldo.naturezaDividaAtiva, saldo.intervalo, saldo.contaDeDestinacao_id) MAIORDATA " +
            " ON S.contareceita_id = MAIORDATA.contareceita_id  " +
            " AND S.UNIDADEORGANIZACIONAL_ID = MAIORDATA.UNIDADEORGANIZACIONAL_ID  " +
            " and s.naturezaDividaAtiva = maiordata.naturezaDividaAtiva  " +
            " and s.intervalo = maiordata.intervalo  " +
            " and s.contaDeDestinacao_id= maiordata.contaDeDestinacao_id " +
            " and trunc(maiordata.datasaldo) = trunc(s.datasaldo) " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.unidadeorganizacional_id = VW.SUBORDINADA_ID  " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORGAO ON VW.SUPERIOR_ID = VWORGAO.SUBORDINADA_ID " +
            " AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy'))  " +
            " AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VWORGAO.INICIOVIGENCIA AND COALESCE(VWORGAO.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy'))  " +
            " WHERE C.EXERCICIO_ID = :exercicio " +
            getUnidades(listaDeUnidades);
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("exercicio", selecionado.getExercicio().getId());
        return criarTransporteDividaAtiva(selecionado, consulta.getResultList());
    }

    private List<TransporteDividaAtiva> criarTransporteDividaAtiva(AberturaFechamentoExercicio selecionado, List resultList) {
        List<TransporteDividaAtiva> retorno = Lists.newArrayList();
        Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(selecionado.getExercicio().getAno() + 1);
        Date data = recuperarDataDoTranporte(proximoExercicio);
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            HierarquiaOrganizacional orgao = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[0]).longValue());
            HierarquiaOrganizacional unidade = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
            Conta conta = em.find(Conta.class, ((BigDecimal) objeto[2]).longValue());
            BigDecimal inscricao = (BigDecimal) objeto[3];
            BigDecimal atualizacao = (BigDecimal) objeto[4];
            BigDecimal provisao = (BigDecimal) objeto[5];
            BigDecimal reversao = (BigDecimal) objeto[6];
            BigDecimal recebimento = (BigDecimal) objeto[7];
            BigDecimal baixa = (BigDecimal) objeto[8];
            BigDecimal aumentativo = (BigDecimal) objeto[9];
            BigDecimal diminutivo = (BigDecimal) objeto[10];
            NaturezaDividaAtivaCreditoReceber naturezaDividaAtiva = NaturezaDividaAtivaCreditoReceber.valueOf((String) objeto[11]);
            Intervalo intervalo = Intervalo.valueOf((String) objeto[12]);
            BigDecimal saldo = (inscricao.add(atualizacao).add(reversao).add(aumentativo)).subtract((recebimento.add(provisao).add(baixa).add(diminutivo)));
            ContaDeDestinacao contaDeDestinacao = em.find(ContaDeDestinacao.class, ((BigDecimal) objeto[13]).longValue());
            if (BigDecimal.ZERO.compareTo(saldo) != 0) {
                retorno.add(new TransporteDividaAtiva(orgao, unidade, unidade.getSubordinada(),
                    conta, inscricao, atualizacao, provisao, reversao, recebimento, baixa, aumentativo, diminutivo, data, selecionado,
                    contaDeDestinacao, naturezaDividaAtiva, intervalo, saldo));
            }
        }
        return retorno;
    }

    public List<TransporteExtra> recuperarSaldoExtra(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> listaDeUnidades) {
        String sql = "SELECT " +
            " VWORGAO.ID as orgao, " +
            " VW.id as unidade, " +
            " s.CONTAEXTRAORCAMENTARIA_ID, " +
            " s.FONTEDERECURSOS_ID, " +
            " s.contaDeDestinacao_ID, " +
            " s.valor " +
            " FROM SALDOEXTRAORCAMENTARIO S " +
            " INNER JOIN CONTA C ON S.CONTAEXTRAORCAMENTARIA_ID = C.ID " +
            " INNER JOIN FONTEDERECURSOS F ON S.FONTEDERECURSOS_ID = F.ID " +
            " INNER JOIN (SELECT trunc(MAX(DATASALDO)) AS DATASALDO, SALDO.CONTAEXTRAORCAMENTARIA_ID, SALDO.UNIDADEORGANIZACIONAL_ID, saldo.FONTEDERECURSOS_ID, saldo.contaDeDestinacao_ID  " +
            "                       FROM SALDOEXTRAORCAMENTARIO SALDO  " +
            "                   GROUP BY SALDO.CONTAEXTRAORCAMENTARIA_ID, SALDO.UNIDADEORGANIZACIONAL_ID, SALDO.FONTEDERECURSOS_ID, saldo.contaDeDestinacao_ID) MAIORDATA " +
            " ON S.CONTAEXTRAORCAMENTARIA_ID = MAIORDATA.CONTAEXTRAORCAMENTARIA_ID  " +
            " AND S.UNIDADEORGANIZACIONAL_ID = MAIORDATA.UNIDADEORGANIZACIONAL_ID " +
            " AND S.FONTEDERECURSOS_ID = MAIORDATA.FONTEDERECURSOS_ID  " +
            " AND S.contaDeDestinacao_ID = MAIORDATA.contaDeDestinacao_ID  " +
            " and trunc(maiordata.datasaldo) = trunc(s.datasaldo) " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.unidadeorganizacional_id = VW.SUBORDINADA_ID   " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORGAO ON VW.SUPERIOR_ID = VWORGAO.SUBORDINADA_ID  " +
            " AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy'))   " +
            " AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VWORGAO.INICIOVIGENCIA AND COALESCE(VWORGAO.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy'))   " +
            " WHERE C.EXERCICIO_ID = :exercicio " +
            " and f.EXERCICIO_ID = :exercicio " +
            " and s.valor <> 0" +
            getUnidades(listaDeUnidades);
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("exercicio", selecionado.getExercicio().getId());
        return criarTransporteExtra(selecionado, consulta.getResultList());
    }

    private List<TransporteExtra> criarTransporteExtra(AberturaFechamentoExercicio selecionado, List resultList) {
        List<TransporteExtra> retorno = Lists.newArrayList();
        Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(selecionado.getExercicio().getAno() + 1);
        Date data = recuperarDataDoTranporte(proximoExercicio);
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            HierarquiaOrganizacional orgao = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[0]).longValue());
            HierarquiaOrganizacional unidade = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
            Conta conta = em.find(Conta.class, ((BigDecimal) objeto[2]).longValue());
            FonteDeRecursos fonte = em.find(FonteDeRecursos.class, ((BigDecimal) objeto[3]).longValue());
            ContaDeDestinacao contaDeDestinacao = em.find(ContaDeDestinacao.class, ((BigDecimal) objeto[4]).longValue());
            BigDecimal valor = (BigDecimal) objeto[5];

            retorno.add(new TransporteExtra(orgao, unidade, unidade.getSubordinada(), conta, fonte, contaDeDestinacao, valor, data, selecionado));
        }
        return retorno;
    }

    public List<TransporteSaldoDividaPublica> recuperarSaldoDividaPublica(AberturaFechamentoExercicio selecionado, List<HierarquiaOrganizacional> unidades) {
        String sql = "SELECT VWORGAO.ID as orgao,  " +
            "       VW.id as unidade,  " +
            "       s.dividapublica_id,  " +
            "       s.FONTEDERECURSOS_ID,  " +
            "       s.contaDeDestinacao_ID,  " +
            "       s.intervalo, " +
            "       s.inscricao, " +
            "       s.atualizacao, " +
            "       s.apropriacao, " +
            "       s.pagamento, " +
            "       s.receita, " +
            "       s.cancelamento, " +
            "       s.transferenciacredito, " +
            "       s.transferenciadebito, " +
            "       s.empenhado " +
            " FROM Saldodividapublica S  " +
            " INNER JOIN FONTEDERECURSOS F ON S.FONTEDERECURSOS_ID = F.ID  " +
            " INNER JOIN (SELECT trunc(MAX(data)) AS DATASALDO, SALDO.dividapublica_id, SALDO.UNIDADEORGANIZACIONAL_ID, saldo.FONTEDERECURSOS_ID, saldo.intervalo, saldo.contaDeDestinacao_ID " +
            "                       FROM Saldodividapublica SALDO   " +
            "                   GROUP BY SALDO.dividapublica_id, SALDO.UNIDADEORGANIZACIONAL_ID, SALDO.FONTEDERECURSOS_ID, saldo.intervalo, saldo.contaDeDestinacao_ID) MAIORDATA " +
            " ON S.dividapublica_id = MAIORDATA.dividapublica_id   " +
            " AND S.UNIDADEORGANIZACIONAL_ID = MAIORDATA.UNIDADEORGANIZACIONAL_ID  " +
            " AND S.FONTEDERECURSOS_ID = MAIORDATA.FONTEDERECURSOS_ID " +
            " and s.intervalo = maiordata.intervalo " +
            " and s.contaDeDestinacao_ID = maiordata.contaDeDestinacao_ID " +
            " and trunc(maiordata.datasaldo) = trunc(s.data) " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.unidadeorganizacional_id = VW.SUBORDINADA_ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORGAO ON VW.SUPERIOR_ID = VWORGAO.SUBORDINADA_ID " +
            " AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy')) " +
            " AND TO_DATE(:dataReferencia,'dd/mm/yyyy') BETWEEN VWORGAO.INICIOVIGENCIA AND COALESCE(VWORGAO.FIMVIGENCIA,TO_DATE(:dataReferencia,'dd/mm/yyyy')) " +
            " WHERE f.EXERCICIO_ID = :exercicio  " +
            " and (s.inscricao <> 0 or s.atualizacao <> 0  " +
            " or s.apropriacao <> 0 or s.pagamento <> 0  " +
            " or s.receita <> 0 or s.cancelamento <> 0  " +
            " or s.transferenciacredito <> 0 or s.transferenciadebito <> 0  " +
            " or s.empenhado <> 0) " +
            getUnidades(unidades);
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
        consulta.setParameter("exercicio", selecionado.getExercicio().getId());
        return criarTransporteSaldoDividaPublica(selecionado, consulta.getResultList());
    }

    private List<TransporteSaldoDividaPublica> criarTransporteSaldoDividaPublica(AberturaFechamentoExercicio selecionado, List resultList) {
        List<TransporteSaldoDividaPublica> retorno = Lists.newArrayList();
        Exercicio proximoExercicio = exercicioFacade.getExercicioPorAno(selecionado.getExercicio().getAno() + 1);
        Date data = recuperarDataDoTranporte(proximoExercicio);
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            HierarquiaOrganizacional orgao = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[0]).longValue());
            HierarquiaOrganizacional unidade = em.find(HierarquiaOrganizacional.class, ((BigDecimal) objeto[1]).longValue());
            DividaPublica dividaPublica = em.find(DividaPublica.class, ((BigDecimal) objeto[2]).longValue());
            FonteDeRecursos fonte = em.find(FonteDeRecursos.class, ((BigDecimal) objeto[3]).longValue());
            ContaDeDestinacao contaDeDestinacao = em.find(ContaDeDestinacao.class, ((BigDecimal) objeto[4]).longValue());

            TransporteSaldoDividaPublica transporteSaldoDividaPublica = new TransporteSaldoDividaPublica();
            transporteSaldoDividaPublica.setAberturaFechamentoExercicio(selecionado);
            transporteSaldoDividaPublica.setData(data);
            transporteSaldoDividaPublica.setOrgao(orgao);
            transporteSaldoDividaPublica.setUnidade(unidade);
            transporteSaldoDividaPublica.setUnidadeOrganizacional(unidade.getSubordinada());
            transporteSaldoDividaPublica.setDividapublica(dividaPublica);
            transporteSaldoDividaPublica.setFonte(fonte);
            transporteSaldoDividaPublica.setContaDeDestinacao(contaDeDestinacao);
            transporteSaldoDividaPublica.setIntervalo(Intervalo.valueOf((String) objeto[5]));
            transporteSaldoDividaPublica.setInscricao((BigDecimal) objeto[6]);
            transporteSaldoDividaPublica.setAtualizacao((BigDecimal) objeto[7]);
            transporteSaldoDividaPublica.setApropriacao((BigDecimal) objeto[8]);
            transporteSaldoDividaPublica.setPagamento((BigDecimal) objeto[9]);
            transporteSaldoDividaPublica.setReceita((BigDecimal) objeto[10]);
            transporteSaldoDividaPublica.setCancelamento((BigDecimal) objeto[11]);
            transporteSaldoDividaPublica.setTransferenciaCredito((BigDecimal) objeto[12]);
            transporteSaldoDividaPublica.setTransferenciaDebito((BigDecimal) objeto[13]);
            transporteSaldoDividaPublica.setEmpenhado((BigDecimal) objeto[14]);

            BigDecimal soma = transporteSaldoDividaPublica.getInscricao().add(
                transporteSaldoDividaPublica.getReceita()).add(
                transporteSaldoDividaPublica.getTransferenciaCredito()).add(
                transporteSaldoDividaPublica.getAtualizacao());

            BigDecimal subtracao = transporteSaldoDividaPublica.getApropriacao().add(transporteSaldoDividaPublica.getPagamento()).add(transporteSaldoDividaPublica.getTransferenciaDebito()).add(transporteSaldoDividaPublica.getCancelamento());

            transporteSaldoDividaPublica.setSaldo(soma.subtract(subtracao));

            if (BigDecimal.ZERO.compareTo(transporteSaldoDividaPublica.getSaldo()) != 0) {
                retorno.add(transporteSaldoDividaPublica);
            }
        }
        return retorno;
    }


    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public ConfigEmpenhoRestoFacade getConfigEmpenhoRestoFacade() {
        return configEmpenhoRestoFacade;
    }

    public ConfigAberturaFechamentoExercicioFacade getConfigAberturaFechamentoExercicioFacade() {
        return configAberturaFechamentoExercicioFacade;
    }

    public ConfigCancelamentoRestoFacade getConfigCancelamentoRestoFacade() {
        return configCancelamentoRestoFacade;
    }

    public ConfiguracaoContabilFacade getConfiguracaoContabilFacade() {
        return configuracaoContabilFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }
}
