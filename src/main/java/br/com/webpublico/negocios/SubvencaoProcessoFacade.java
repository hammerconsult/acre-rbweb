package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoSubvencao;
import br.com.webpublico.enums.TipoPassageiro;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 18/12/13
 * Time: 18:13
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SubvencaoProcessoFacade extends AbstractFacade<SubvencaoProcesso> {

    private static final BigDecimal CEM = new BigDecimal(100);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private GeraValorDividaPagamentoSubvencao geraDebito;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DividaFacade dividaFacade;

    public SubvencaoProcessoFacade() {
        super(SubvencaoProcesso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public List<CadastroEconomicoSubvencao> buscarCadastrosEconomicoVigenteNoParametro(TipoPassageiro tipoPassageiro, Date dataReferencia) {
        String sql = "  select distinct cs.* " +
            "   from CadastroEconomicoSubvencao cs " +
            "   inner join CONTRIBUINTEDEBITOOUTORGA contribuinte on contribuinte.CADASTROECONOMICO_ID = cs.CADASTROECONOMICO_ID " +
            "   inner join OutorgaIPO ipo on ipo.CONTRIBUINTEDEBITOOUTORGA_ID = contribuinte.id " +
            "   where (cs.vigenciainicial <= :dataReferencia and cs.vigenciaFinal >= :dataReferencia or cs.vigenciaFinal is null) " +
            "   and (ipo.TIPOPASSAGEIRO = :tipoPassageiro)";
        Query q = em.createNativeQuery(sql, CadastroEconomicoSubvencao.class);
        q.setParameter("tipoPassageiro", tipoPassageiro.name());
        q.setParameter("dataReferencia", dataReferencia);
        return q.getResultList();
    }

    public List<EmpresaDevedoraSubvencao> recuperarEmpresasDevedoras(CadastroEconomico cadastroEconomico) {
        String sql = ("select empresadevedora.* from SUBVENCAOPARAMETRO param " +
            " inner join cadastroeconomicosubvencao cmcsub on cmcsub.SUBVENCAOPARAMETRO_ID = param.id " +
            " inner join EMPRESADEVEDORASUBVENCAO empresadevedora on empresadevedora.EMPRESACREDORA_ID = cmcsub.id " +
            " where cmcsub.CADASTROECONOMICO_ID = :cmc order by empresadevedora.ordem ");
        Query q = em.createNativeQuery(sql, EmpresaDevedoraSubvencao.class);
        q.setParameter("cmc", cadastroEconomico);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    public List<SubvencaoProcesso> buscarProcessosParaBloqueio(BloqueioOutorga bloqueioOutorga) {
        String sql = "select subvencao.* from subvencaoprocesso subvencao " +
            "inner join SUBVENCAOEMPRESAS empresa on subvencao.ID = empresa.SUBVENCAOPROCESSO_ID " +
            "where empresa.BLOQUEIOOUTORGA_ID = :bloqueio " +
            "  and subvencao.SITUACAO = :situacao ";
        Query q = em.createNativeQuery(sql, SubvencaoProcesso.class);
        q.setParameter("bloqueio", bloqueioOutorga.getId());
        q.setParameter("situacao", SituacaoSubvencao.EFETIVADO.name());
        return q.getResultList();
    }

    public ParcelaValorDivida buscarParcelaValorDivida(Long id) {
        String hql = "select pvd from ParcelaValorDivida pvd where pvd.id = :id";
        Query q = em.createQuery(hql, ParcelaValorDivida.class);
        q.setParameter("id", id);
        if (!q.getResultList().isEmpty()) {
            ParcelaValorDivida parcela = (ParcelaValorDivida) q.getSingleResult();
            parcela.getItensParcelaValorDivida().size();
            parcela.getSituacoes().size();
            if (parcela.getValorDivida() != null) {
                if (parcela.getValorDivida().getCalculo() != null) {
                    parcela.getValorDivida().getCalculo().getPessoas().size();
                }
            }
            return parcela;
        } else {
            return null;
        }
    }

    @Override
    public SubvencaoProcesso recuperar(Object id) {
        SubvencaoProcesso processo = em.find(SubvencaoProcesso.class, id);
        processo.getSubvencaoEmpresas().size();
        for (SubvencaoEmpresas subvencaoEmpresas : processo.getSubvencaoEmpresas()) {
            subvencaoEmpresas.getSubvencaoParcela().size();
            if (subvencaoEmpresas.getBloqueioOutorga() != null) {
                Hibernate.initialize(subvencaoEmpresas.getBloqueioOutorga().getParametros());
                Hibernate.initialize(subvencaoEmpresas.getBloqueioOutorga().getDadosBloqueioOutorgas());
            }
        }
        return processo;
    }

    public List<DividaSubvencao> recuperarDividaDoParametro() {
        Query q = em.createQuery(" select divida from DividaSubvencao  divida" +
            " join divida.subvencaoParametro " +
            " where divida.dataInicialVigencia <= to_date(to_char(sysdate,'dd/MM/yyyy'), 'dd/MM/yyyy') and (divida.dataFinalVigencia is null or divida.dataFinalVigencia >= to_date(to_char(sysdate,'dd/MM/yyyy'), 'dd/MM/yyyy'))");
        return q.getResultList();
    }

    public Integer exercicioInicial(Long idDivida) {
        Query q = em.createNativeQuery("select exercicio.ano  from dividasubvencao divida" +
            " inner join exercicio on divida.exercicioinicial_id = exercicio.id" +
            " where divida.divida_id = :idDivida");
        q.setParameter("idDivida", idDivida);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return ((BigDecimal) resultList.get(0)).intValue();
        }
        return 0;
    }

    public Integer exercicioFinal(Long idDivida) {
        Query q = em.createNativeQuery("select exercicio.ano  from dividasubvencao divida" +
            " inner join exercicio on divida.exerciciofinal_id = exercicio.id" +
            " where divida.divida_id = :idDivida");
        q.setParameter("idDivida", idDivida);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return ((BigDecimal) resultList.get(0)).intValue();
        }
        return 0;
    }

    public void estornarParcelas(ParcelaValorDivida parcela, SubvencaoProcesso subvencao) {
        ParcelaValorDivida pvd = buscarParcelaValorDivida(parcela.getId());
        SituacaoParcelaValorDivida situacao = new SituacaoParcelaValorDivida();
        situacao.setSituacaoParcela(SituacaoParcela.EM_ABERTO);
        situacao.setDataLancamento(new Date());
        situacao.setParcela(pvd);
        situacao.setSaldo(pvd.getUltimaSituacao().getSaldo());
        estornarPagamentoAvulso(parcela, subvencao);
        em.persist(situacao);
    }

    public void estornarParcelasGeradasResidual(ParcelaValorDivida parcela) {
        SituacaoParcelaValorDivida situacao = new SituacaoParcelaValorDivida();
        situacao.setSituacaoParcela(SituacaoParcela.CANCELAMENTO);
        situacao.setDataLancamento(new Date());
        situacao.setParcela(parcela);
        situacao.setSaldo(parcela.getSituacaoAtual().getSaldo());
        em.merge(situacao);
    }

    public void estornarPagamentoAvulso(ParcelaValorDivida pvd, SubvencaoProcesso subvencao) {
        List<PagamentoAvulso> pagamentos = buscarPagamentoAvulsoDaSubvencao(pvd, subvencao);
        if (pagamentos != null) {
            Query q = em.createQuery("delete from PagamentoAvulso pa where pa.subvencao.id = :idSubvencao");
            q.setParameter("idSubvencao", subvencao.getId());
            q.executeUpdate();
        }
    }

    public void pagarParcelaPorSubvencao(ParcelaValorDivida pvd) {
        SituacaoParcelaValorDivida situacao = new SituacaoParcelaValorDivida();
        situacao.setSituacaoParcela(SituacaoParcela.AGUARDANDO_PAGAMENTO_SUBVENCAO);
        situacao.setDataLancamento(new Date());
        situacao.setParcela(pvd);
        situacao.setSaldo(pvd.getUltimaSituacao().getSaldo());
        em.persist(situacao);
    }

    private List<PagamentoAvulso> buscarPagamentoAvulsoDaSubvencao(ParcelaValorDivida pvd, SubvencaoProcesso subvencao) {
        String hql = "select pagamento from PagamentoAvulso pagamento where pagamento.parcelaValorDivida = :parcela " +
            " and pagamento.subvencao = :subvencao order by dataPagamento desc, id desc ";
        Query q = em.createQuery(hql);
        q.setParameter("parcela", pvd);
        q.setParameter("subvencao", subvencao);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    public Boolean existeLancamentoParaACompetencia(Mes mes, Long idExercicio, TipoPassageiro tipoPassageiro) {
        String sql = " select processo.id from subvencaoprocesso processo" +
            " where processo.mes = :mes and processo.exercicio_id = :exercicio " +
            " and processo.tipoPassageiro = :tipoPassageiro " +
            " and processo.situacao = :situacao ";
        Query q = em.createNativeQuery(sql);

        q.setParameter("exercicio", idExercicio);
        q.setParameter("mes", mes.name());
        q.setParameter("situacao", SituacaoSubvencao.EFETIVADO.name());
        q.setParameter("tipoPassageiro", tipoPassageiro.name());
        return !q.getResultList().isEmpty();
    }

    public Boolean hasLancamentoValidoParaACompetencia(OutorgaIPO outorgaIPO) {
        String sql = " select processo.id from subvencaoprocesso processo" +
            " inner join SubvencaoEmpresas empresas on empresas.subvencaoProcesso_id = processo.id " +
            " where empresas.outorgaIPO_id = :idOutorgaIPO " +
            " and processo.situacao = :situacao ";
        Query q = em.createNativeQuery(sql);

        q.setParameter("idOutorgaIPO", outorgaIPO.getId());
        q.setParameter("situacao", SituacaoSubvencao.EFETIVADO.name());
        return !q.getResultList().isEmpty();
    }

    public Boolean hasLancamentoValidoParaACompetencia(Mes mes, Long idExercicio, TipoPassageiro tipoPassageiro, Date dataInicial, Date dataFinal) {
        String sql = " select processo.id from subvencaoprocesso processo" +
            " where processo.mes = :mes and processo.exercicio_id = :exercicio " +
            " and processo.tipoPassageiro = :tipoPassageiro " +
            " and processo.situacao = :situacao " +
            " and processo.dataReferenciaParametro BETWEEN :dataInicial and :dataFinal";
        Query q = em.createNativeQuery(sql);

        q.setParameter("exercicio", idExercicio);
        q.setParameter("mes", mes.name());
        q.setParameter("situacao", SituacaoSubvencao.EFETIVADO.name());
        q.setParameter("tipoPassageiro", tipoPassageiro.name());
        q.setParameter("dataInicial", dataInicial);
        q.setParameter("dataFinal", dataFinal);
        return !q.getResultList().isEmpty();
    }

    public Boolean hasLancamentoValidoParaACompetenciaEEmpresa(OutorgaIPO outorgaIPO) {
        String sql = " select processo.id from subvencaoprocesso processo" +
            " inner join SubvencaoEmpresas empresas on empresas.subvencaoProcesso_id = processo.id " +
            " where empresas.outorgaIPO_id = :idOutorgaIPO " +
            " and processo.situacao in (:situacoes)";
        Query q = em.createNativeQuery(sql);

        List<String> situacoes = Lists.newArrayList(SituacaoSubvencao.EFETIVADO.name(), SituacaoSubvencao.EM_ABERTO.name());

        q.setParameter("idOutorgaIPO", outorgaIPO.getId());
        q.setParameter("situacoes", situacoes);
        return !q.getResultList().isEmpty();
    }

    public SubvencaoProcesso salva(SubvencaoProcesso processo) {
        return em.merge(processo);
    }

    public SubvencaoEmpresas buscarEmpresaDaUltimaSubvencaoLancadaPorCMC(OutorgaIPO outorgaIPO, CadastroEconomico cmc) {
        String sql = "SELECT EMPRESA.* FROM SUBVENCAOPROCESSO subvencao " +
            "   INNER JOIN SUBVENCAOEMPRESAS EMPRESA ON EMPRESA.SUBVENCAOPROCESSO_ID = SUBVENCAO.ID " +
            "   INNER JOIN cadastroeconomico cmc ON EMPRESA.CADASTROECONOMICO_ID = cmc.id " +
            "   where cmc.id = :idCmc and EMPRESA.outorgaIPO_id = :idOutorgaIPO " +
            "     and subvencao.SITUACAO = :situacao " +
            " order by subvencao.NUMERODOPROCESSO desc ";
        Query q = em.createNativeQuery(sql, SubvencaoEmpresas.class);
        q.setParameter("idCmc", cmc.getId());
        q.setParameter("idOutorgaIPO", outorgaIPO.getId());
        q.setParameter("situacao", SituacaoSubvencao.EFETIVADO.name());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (SubvencaoEmpresas) resultList.get(0);
        } else {
            return null;
        }
    }

    public SubvencaoProcesso buscarSubvencaoEmAberto() {
        String sql = "select s.* from subvencaoprocesso s where SITUACAO = 'EM_ABERTO'";
        Query q = em.createNativeQuery(sql, SubvencaoProcesso.class);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (SubvencaoProcesso) resultList.get(0);
        }
        return null;
    }

    public SubvencaoProcesso buscarUltimaSubvencaoLancadaDeOutroMes(Mes mes) {
        String sql = "SELECT subvencao.* FROM SUBVENCAOPROCESSO subvencao " +
            "        where subvencao.MES <> :MES  ";
        sql += "     order by subvencao.NUMERODOPROCESSO desc ";
        Query q = em.createNativeQuery(sql, SubvencaoProcesso.class);
        q.setParameter("MES", mes.name());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (SubvencaoProcesso) resultList.get(0);
        } else {
            return null;
        }
    }

    public SubvencaoProcesso primeiraSubvencaoDoMes(Exercicio ex, Mes mes) {
        String sql = " select * " +
            " from SUBVENCAOPROCESSO" +
            " where NUMERODOPROCESSO = (SELECT min(numerodoprocesso) from subvencaoprocesso s where EXERCICIO_ID = :ex and mes = :mes)";
        Query q = em.createNativeQuery(sql, SubvencaoProcesso.class);
        q.setParameter("ex", ex.getId());
        q.setParameter("mes", mes.name());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (SubvencaoProcesso) resultList.get(0);
        }
        return null;
    }

    public SubvencaoEmpresas salvarSubvencaoEmpresa(SubvencaoEmpresas empresa) {
        return em.merge(empresa);
    }

    public List<CalculoPagamentoSubvencao> recuperarCalculo(SubvencaoEmpresas pagamentoSubvencao) {
        String hql = "select calc from CalculoPagamentoSubvencao calc where calc.pagamentoSubvencao = :pagamento";
        Query q = em.createQuery(hql);
        q.setParameter("pagamento", pagamentoSubvencao);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<SubvencaoEmpresas> pagarParcelaAndCancelarDam(SubvencaoProcesso processo, SubvencaoEmpresas subvencaoEmpresa, AssistenteBarraProgresso assistente) throws Exception {
        for (SubvencaoParcela parcela : subvencaoEmpresa.getSubvencaoParcela()) {
            ParcelaValorDivida pvd = buscarParcelaValorDivida(parcela.getParcelaValorDivida().getId());
            damFacade.cancelarDamsDaParcela(pvd, assistente.getUsuarioSistema());
            pagarParcelaPorSubvencao(pvd);
            gerarNovaParcelaSubvencaoResidual(processo, subvencaoEmpresa, parcela, pvd);
            assistente.conta();
        }
        return new AsyncResult(subvencaoEmpresa);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gerarNovaParcelaSubvencaoResidual(SubvencaoProcesso processo, SubvencaoEmpresas subvencaoEmpresa, SubvencaoParcela parcela, ParcelaValorDivida pvd) throws Exception {
        if (parcela.getValorResidual().compareTo(BigDecimal.ZERO) == 0) {
            atribuirSituacaoDeAcordoComProcesso(pvd, SituacaoParcela.PAGO_SUBVENCAO);
        } else {
            atribuirSituacaoDeAcordoComProcesso(pvd, SituacaoParcela.AGUARDANDO_PAGAMENTO_SUBVENCAO);
            geraDebito.geraDebito(gerarProcessoCalculoSubvencao(subvencaoEmpresa, pvd, parcela, SituacaoParcela.EM_ABERTO));
        }
        em.merge(processo);
    }

    private void atribuirSituacaoDeAcordoComProcesso(ParcelaValorDivida parcela, SituacaoParcela situacao) {
        salvarSituacaoParcela(parcela, situacao);
    }

    private void salvarSituacaoParcela(ParcelaValorDivida parcela, SituacaoParcela situacao) {
        SituacaoParcelaValorDivida situacaoParcela = new SituacaoParcelaValorDivida();
        situacaoParcela.setSaldo(parcela.getUltimaSituacao().getSaldo());
        situacaoParcela.setDataLancamento(new Date());
        situacaoParcela.setParcela(parcela);
        situacaoParcela.setSituacaoParcela(situacao);
        em.persist(situacaoParcela);
    }

    public void pagarParcelaAndCancelarDamIndividual(SubvencaoProcesso processo, SubvencaoEmpresas subvencao) throws Exception {
        for (SubvencaoParcela parcela : subvencao.getSubvencaoParcela()) {
            ParcelaValorDivida pvd = buscarParcelaValorDivida(parcela.getParcelaValorDivida().getId());
            damFacade.cancelarDamsDaParcela(pvd, sistemaFacade.getUsuarioCorrente());
            pagarParcelaPorSubvencao(pvd);
            gerarNovaParcelaSubvencaoResidual(processo, subvencao, parcela, pvd);
        }
    }

    private ProcessoCalculoSubvencao gerarProcessoCalculoSubvencao(SubvencaoEmpresas pagamento, ParcelaValorDivida pvd,
                                                                   SubvencaoParcela subvencaoParcela, SituacaoParcela situacaoParcela) {

        CalculoPagamentoSubvencao calculoPagamentoSubvencao = new CalculoPagamentoSubvencao();
        criarCalculo(calculoPagamentoSubvencao, pagamento, subvencaoParcela, pvd, situacaoParcela);
        ProcessoCalculoSubvencao processo = new ProcessoCalculoSubvencao();
        calculoPagamentoSubvencao.setProcessoCalculo(processo);
        calculoPagamentoSubvencao.setProcessoCalculoSubvencao(processo);
        criarProcessoPagamentoSubvencao(processo, calculoPagamentoSubvencao, pagamento, pvd);
        return em.merge(processo);
    }

    private void criarProcessoPagamentoSubvencao(ProcessoCalculoSubvencao processo, CalculoPagamentoSubvencao calculoPagamentoSubvencao, SubvencaoEmpresas pagamento, ParcelaValorDivida pvd) {
        processo.getCalculos().add(calculoPagamentoSubvencao);
        processo.setCompleto(Boolean.TRUE);
        processo.setDataLancamento(pagamento.getSubvencaoProcesso().getDataLancamento());
        processo.setDivida(pvd.getValorDivida().getDivida());
        processo.setExercicio(pvd.getValorDivida().getExercicio());
    }

    private void criarCalculo(CalculoPagamentoSubvencao calculoPagamentoSubvencao, SubvencaoEmpresas pagamentoSubvencao,
                              SubvencaoParcela subvencaoParcela, ParcelaValorDivida pvd, SituacaoParcela situacaoParcela) {

        calculoPagamentoSubvencao.setPagamentoSubvencao(pagamentoSubvencao);
        calculoPagamentoSubvencao.setDataCalculo(subvencaoParcela.getDataLancamento());
        calculoPagamentoSubvencao.setSimulacao(Boolean.FALSE);
        calculoPagamentoSubvencao.setCadastro(pagamentoSubvencao.getCadastroEconomico());
        calculoPagamentoSubvencao.setTipoCalculo(Calculo.TipoCalculo.PAGAMENTO_SUBVENCAO);
        calculoPagamentoSubvencao.setSituacaoParcelaGerada(situacaoParcela);
        calculoPagamentoSubvencao.setVencimentoOriginalParcela(subvencaoParcela.getParcelaValorDivida().getVencimento());
        calculoPagamentoSubvencao.setSequenciaParcela(subvencaoParcela.getParcelaValorDivida().getSequenciaParcela());
        calculoPagamentoSubvencao.setQuantidadeParcela(subvencaoParcela.getParcelaValorDivida().getQuantidadeParcela());
        calculoPagamentoSubvencao.setDividaAtiva(subvencaoParcela.getParcelaValorDivida().getDividaAtiva());
        calculoPagamentoSubvencao.setDividaAtivaAjuizada(subvencaoParcela.getParcelaValorDivida().getDividaAtivaAjuizada());
        if (calculoPagamentoSubvencao.getSituacaoParcelaGerada().equals(SituacaoParcela.EM_ABERTO)) {
            calculoPagamentoSubvencao.setReferencia(subvencaoParcela.getReferencia());
            criarItemDoCalculo(subvencaoParcela, pvd, calculoPagamentoSubvencao);
        }
        for (CalculoPessoa cp : pvd.getValorDivida().getCalculo().getPessoas()) {
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setPessoa(cp.getPessoa());
            calculoPessoa.setCalculo(calculoPagamentoSubvencao);
            calculoPagamentoSubvencao.getPessoas().add(calculoPessoa);
        }
    }

    private void criarItemDoCalculo(SubvencaoParcela parcela, ParcelaValorDivida pvd, CalculoPagamentoSubvencao calculoPagamentoSubvencao) {
        lancarTributo(parcela, pvd, calculoPagamentoSubvencao);
    }

    private void lancarTributo(SubvencaoParcela parcela, ParcelaValorDivida pvd,
                               CalculoPagamentoSubvencao calculoPagamentoSubvencao) {

        BigDecimal valorOriginal = pvd.getValor();
        BigDecimal diferenca = BigDecimal.ZERO;

        BigDecimal saldoImpostoTaxa = atribuirPercentualImpostoTaxa(parcela);

        for (ItemParcelaValorDivida itemParcelaValorDivida : pvd.getItensParcelaValorDivida()) {

            BigDecimal valorTributo = itemParcelaValorDivida.getValor();
            BigDecimal percentualTributo = valorOriginal.divide(CEM, 8, RoundingMode.HALF_UP);
            percentualTributo = valorTributo.divide(percentualTributo, 8, RoundingMode.HALF_UP);

            valorTributo = percentualTributo.divide(CEM, 8, RoundingMode.HALF_UP).multiply(saldoImpostoTaxa);
            valorTributo = valorTributo.setScale(2, RoundingMode.HALF_UP);

            diferenca = criarItemCalculoSubvencao(calculoPagamentoSubvencao, diferenca, itemParcelaValorDivida, valorTributo);
        }

        if (diferenca.compareTo(saldoImpostoTaxa) != 0) {
            ItensCalculoSubvencao primeiroItem = calculoPagamentoSubvencao.getItensCalculoSubvencao().get(0);
            primeiroItem.setValor(primeiroItem.getValor().add(diferenca.subtract(saldoImpostoTaxa)));
        }
        atribuirValorTotalSubvencao(calculoPagamentoSubvencao);
    }

    private void atribuirValorTotalSubvencao(CalculoPagamentoSubvencao calculoPagamentoSubvencao) {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ItensCalculoSubvencao item : calculoPagamentoSubvencao.getItensCalculoSubvencao()) {
            valorTotal = valorTotal.add(item.getValor());
        }
        calculoPagamentoSubvencao.setValorEfetivo(valorTotal);
        calculoPagamentoSubvencao.setValorReal(valorTotal);
    }

    private BigDecimal criarItemCalculoSubvencao(CalculoPagamentoSubvencao calculoPagamentoSubvencao, BigDecimal diferenca, ItemParcelaValorDivida itemParcelaValorDivida, BigDecimal valorTributo) {
        ItensCalculoSubvencao item = new ItensCalculoSubvencao();
        item.setTributo(itemParcelaValorDivida.getItemValorDivida().getTributo());
        item.setValor(valorTributo);
        item.setCalculoPagamentoSubvencao(calculoPagamentoSubvencao);
        calculoPagamentoSubvencao.getItensCalculoSubvencao().add(item);
        diferenca = diferenca.add(valorTributo);
        return diferenca;
    }

    private BigDecimal atribuirPercentualImpostoTaxa(SubvencaoParcela parcela) {
        BigDecimal percentualImpostoTaxa = parcela.getTotal().divide(CEM, 8, RoundingMode.HALF_UP);
        percentualImpostoTaxa = parcela.getValorOriginal().divide(percentualImpostoTaxa, 8, RoundingMode.HALF_UP);
        percentualImpostoTaxa = percentualImpostoTaxa.divide(CEM, 8, RoundingMode.HALF_UP);

        return percentualImpostoTaxa.multiply(parcela.getValorResidual());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<DAM> gerarDAMIndividualSubvencao(List<SubvencaoParcela> parcelas, Exercicio exercicio) throws Exception {
        List<DAM> dams = Lists.newArrayList();
        for (SubvencaoParcela subvencaoParcela : parcelas) {
            dams.add(damFacade.gerarDAMUnicoSubvencaoViaApi(subvencaoParcela));
        }
        return dams;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public DAM gerarDAMComposto(List<SubvencaoParcela> parcelas, Exercicio exercicio, SubvencaoProcesso subvencao) {
        return damFacade.gerarDAMCompostoSubvencaoViaApi(parcelas);
    }

    public List<SubvencaoProcesso> buscarSubvencoesDaParcela(Long idParcela) {
        String sql = "select subvencao.* from subvencaoprocesso subvencao " +
            " inner join SUBVENCAOEMPRESAS subempresa on subempresa.SUBVENCAOPROCESSO_ID = subvencao.id " +
            " inner join SUBVENCAOPARCELA subparcela on subparcela.SUBVENCAOEMPRESAS_ID = subempresa.id " +
            " where subparcela.PARCELAVALORDIVIDA_ID = :pvd " +
            " and (subvencao.situacao = :situacaoEfetivado or subvencao.situacao = :situacaoAberto)" +
            " order by subparcela.DATALANCAMENTO ";
        Query q = em.createNativeQuery(sql, SubvencaoProcesso.class);
        q.setParameter("pvd", idParcela);
        q.setParameter("situacaoEfetivado", SituacaoSubvencao.EFETIVADO.name());
        q.setParameter("situacaoAberto", SituacaoSubvencao.EM_ABERTO.name());
        List<SubvencaoProcesso> subvencoes = q.getResultList();
        return (subvencoes != null && !subvencoes.isEmpty()) ? subvencoes : Lists.<SubvencaoProcesso>newArrayList();
    }

    public Object[] buscarSuvencaoEfetivadaParcelaResidual(Long idParcela) {
        String sql = "  select processo.id as idProcesso, empresas.id as idEmpresa from subvencaoprocesso processo " +
            "inner join subvencaoempresas empresas on processo.id = empresas.subvencaoprocesso_id " +
            "inner join subvencaoparcela parcelas on empresas.id = parcelas.subvencaoempresas_id " +
            "inner join calculopagamentosubvencao calculo on empresas.id = calculo.pagamentosubvencao_id " +
            "inner join calculo calc on calc.id = calculo.id " +
            "inner join valordivida vd on vd.calculo_id = calc.id " +
            "inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
            "where pvd.id = :idParcela " +
            "and processo.situacao = :situacaoEfetivado " +
            "order by parcelas.datalancamento";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", idParcela);
        q.setParameter("situacaoEfetivado", SituacaoSubvencao.EFETIVADO.name());

        List obj = q.getResultList();
        Object[] subvencoes = null;
        if (obj != null && !obj.isEmpty()) {
            subvencoes = (Object[]) obj.get(0);
        }
        return subvencoes;
    }

    public ResultadoParcela recuperarResultadoParcela(Long idParcela) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.PARCELA_ID,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL, idParcela);
        return consultaParcela.executaConsulta().getResultados().get(0);
    }

    public boolean isSubvencaoParcelaParcelamento(SubvencaoParcela subvencaoParcela) {
        if (subvencaoParcela != null) {
            ResultadoParcela resultadoParcela = recuperarResultadoParcela(subvencaoParcela.getParcelaValorDivida().getId());
            Divida divida = dividaFacade.recuperar(resultadoParcela.getIdDivida());
            return divida.getIsParcelamento();
        }
        return false;
    }

    public boolean isSubvencaoEmpresaComDebitoResidualParcelamento(SubvencaoEmpresas subvencaoEmpresa) {
        if (isSubvencaoParcelaParcelamento(subvencaoEmpresa.getUltimaSubvencaoParcela())) {
            return true;
        }
        return false;
    }

    public boolean isSubvencaoProcessoComDebitoResidualParcelamento(SubvencaoProcesso processo) {
        for (SubvencaoEmpresas subvencaoEmpresa : processo.getSubvencaoEmpresas()) {
            if (isSubvencaoEmpresaComDebitoResidualParcelamento(subvencaoEmpresa)) {
                return true;
            }
        }
        return false;
    }
}

