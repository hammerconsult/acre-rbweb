package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteBloqueioJudicial;
import br.com.webpublico.entidadesauxiliares.VOCertidaDividaAtiva;
import br.com.webpublico.entidadesauxiliares.VOItemCertidaoDividaAtiva;
import br.com.webpublico.entidadesauxiliares.ValoresAtualizadosCDA;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDamDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcParcelaValorDividaDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class BloqueioJudicialFacade extends AbstractFacade<BloqueioJudicial> {
    private final BigDecimal CEM = new BigDecimal(100);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ProcessoJudicialFacade processoJudicialFacade;
    @EJB
    private GeraValorDividaBloqueioJudicial geraValorDivida;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private LoteBaixaFacade loteBaixaFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    public BloqueioJudicialFacade() {
        super(BloqueioJudicial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public BloqueioJudicial recuperar(Object id) {
        BloqueioJudicial bloqueioJudicial = super.recuperar(id);
        Hibernate.initialize(bloqueioJudicial.getParcelas());
        Hibernate.initialize(bloqueioJudicial.getHistoricoSituacoes());
        Hibernate.initialize(bloqueioJudicial.getCdasBloqueioJudicial());
        if (bloqueioJudicial.getDetentorArquivoComposicao() != null && !bloqueioJudicial.getDetentorArquivoComposicao().getArquivosComposicao().isEmpty()) {
            Hibernate.initialize(bloqueioJudicial.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        bloqueioJudicial.setLoteBaixa(buscarLoteBaixa(bloqueioJudicial.getId()));
        return bloqueioJudicial;
    }

    public ProcessoJudicialFacade getProcessoJudicialFacade() {
        return processoJudicialFacade;
    }

    public UsuarioSistema buscarUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public Date buscarDataOperacao() {
        return sistemaFacade.getDataOperacao();
    }

    public Date montarVencimentoDam() {
        return DataUtil.ultimoDiaUtil(DataUtil.ultimoDiaMes(sistemaFacade.getDataOperacao()), feriadoFacade).getTime();
    }

    public DAM buscarOuGerarDam(ResultadoParcela parcela) throws Exception {
        return damFacade.buscarOuGerarDam(parcela);
    }

    public UsuarioSistema recuperarPermissoesUsuarioTributario() {
        UsuarioSistema usuarioSistema = buscarUsuarioCorrente();
        usuarioSistema = em.find(UsuarioSistema.class, usuarioSistema.getId());
        Hibernate.initialize(usuarioSistema.getVigenciaTribUsuarios());
        for (VigenciaTribUsuario vigenciaTribUsuario : usuarioSistema.getVigenciaTribUsuarios()) {
            Hibernate.initialize(vigenciaTribUsuario.getTipoUsuarioTribUsuarios());
        }
        return usuarioSistema;
    }

    public Exercicio buscarExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public BloqueioJudicial salvarBloqueio(BloqueioJudicial selecionado) {
        adicionarHistoricoSituacoes(selecionado, buscarUsuarioCorrente());
        return em.merge(selecionado);
    }

    public void alterarSituacaoParcelas(BloqueioJudicial selecionado) {
        List<ParcelaValorDivida> parcelas = buscarParcelas(selecionado);
        for (ParcelaValorDivida parcela : parcelas) {
            alterarSituacaoParcela(parcela, SituacaoParcela.AGUARDANDO_PAGAMENTO_BLOQUEIO_JUDICIAL);
        }
    }

    public ParcelaValorDivida recuperarParcela(Long idParcela) {
        ParcelaValorDivida pvd = em.find(ParcelaValorDivida.class, idParcela);
        Hibernate.initialize(pvd.getItensParcelaValorDivida());
        return pvd;
    }

    private void adicionarHistoricoSituacoes(BloqueioJudicial selecionado, UsuarioSistema usuarioSistema) {
        HistoricoBloqueioJudicial historico = new HistoricoBloqueioJudicial();
        historico.setBloqueioJudicial(selecionado);
        historico.setSituacao(selecionado.getSituacao());
        historico.setDataHistorico(new Date());
        historico.setUsuarioHistorico(usuarioSistema);

        selecionado.getHistoricoSituacoes().add(historico);
    }

    public Long buscarProximoCodigoPorExercicio(Long idExercicio) {
        try {
            String slq = " select coalesce(max(bl.codigo), 0) from bloqueiojudicial bl " +
                " where bl.exercicio_id = :idExercicio ";
            Query q = em.createNativeQuery(slq);
            q.setParameter("idExercicio", idExercicio);

            List codigos = q.getResultList();
            if (codigos != null && !codigos.isEmpty()) {
                return ((BigDecimal) codigos.get(0)).longValue() + 1;
            }
        } catch (Exception e) {
            return 1L;
        }
        return 1L;
    }

    public List<ProcessoJudicial> buscarProcessosJudiciais(String parte) {
        return buscarProcessosJudiciais(parte, null);
    }

    public List<ProcessoJudicial> buscarProcessosJudiciais(String parte, Pessoa pessoa) {
        String sql = " select max(pj.id), pj.numeroProcessoForum from ProcessoJudicial pj " +
            "inner join ProcessoJudicialCDA pCda on pCda.processoJudicial_id = pj.id " +
            "inner join CertidaoDividaAtiva cda on cda.id = pCda.certidaoDividaAtiva_id " +
            " where replace(replace(pj.numeroProcessoForum,'.',''),'-','') like :parte ";
        if (pessoa != null) {
            sql += " and cda.pessoa_id = :idPessoa ";
        }
        sql += " group by pj.numeroProcessoForum ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("parte", "%" + StringUtil.retornaApenasNumeros(parte.trim()) + "%");
        if (pessoa != null) {
            q.setParameter("idPessoa", pessoa.getId());
        }

        List<Object[]> retorno = q.getResultList();
        List<ProcessoJudicial> processos = Lists.newArrayList();

        if (retorno != null) {
            for (Object[] processo : retorno) {
                ProcessoJudicial processoJudicial = em.find(ProcessoJudicial.class, ((BigDecimal) processo[0]).longValue());
                if (processoJudicial != null) {
                    processos.add(processoJudicial);
                }
            }
        }
        return processos;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Map<ResultadoParcela, Set<Long>> buscarParcelasCDA(BloqueioJudicial bloqueioJudicial, boolean verificarBloqueioJudicial) {
        Map<ResultadoParcela, Set<Long>> mapaParcelasCda = Maps.newHashMap();
        List<Long> certidoes = buscarIdsCdas(bloqueioJudicial);

        for (Long idCda : certidoes) {
            List<Long> idsItemCda = buscarIdsCalculoItemCda(idCda);
            for (Long idItemCda : idsItemCda) {
                List<ResultadoParcela> parcelasDoCalculo = certidaoDividaAtivaFacade.buscarParcelasDoCalculo(idItemCda, verificarBloqueioJudicial);
                parcelasDoCalculo = filtrarParcelas(parcelasDoCalculo);
                for (ResultadoParcela parcelaDoCalculo : parcelasDoCalculo) {
                    if (!mapaParcelasCda.containsKey(parcelaDoCalculo)) {
                        mapaParcelasCda.put(parcelaDoCalculo, Sets.newHashSet(idCda));
                    } else {
                        mapaParcelasCda.get(parcelaDoCalculo).add(idCda);
                    }
                }
            }
        }
        return mapaParcelasCda;
    }

    public List<ResultadoParcela> filtrarParcelas(List<ResultadoParcela> parcelas) {
        return Lists.newArrayList(Iterables.filter(parcelas, new Predicate<ResultadoParcela>() {
            @Override
            public boolean apply(ResultadoParcela resultadoParcela) {
                return resultadoParcela != null && (resultadoParcela.isEmAberto());
            }
        }));
    }

    public List<ResultadoParcela> buscarParcelasPeloId(List<Long> idsParcela) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, idsParcela);

        List<ResultadoParcela> parcelas = consultaParcela.executaConsulta().getResultados();
        return parcelas != null ? parcelas : Lists.newArrayList();
    }

    public List<Long> buscarIdsCdas(BloqueioJudicial bloqueioJudicial) {
        String sql = " select distinct cda.id from certidaodividaativa cda " +
            " where cda.situacaocertidaoda <> :situacaoCDA " +
            " and cda.id in (select p.certidaodividaativa_id from processojudicialcda p " +
            "                inner join processojudicial pj on p.processojudicial_id = pj.id " +
            "                where pj.situacao = :situacaoPj " +
            "                and replace(replace(replace(pj.numeroprocessoforum, '.', ''), '-', ''), '/', '') like :numeroProcesso) " +
            " and cda.pessoa_id = :idPessoa ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("situacaoCDA", SituacaoCertidaoDA.JUNCAO_CDALEGADA.name());
        q.setParameter("situacaoPj", SituacaoCadastralPessoa.ATIVO.name());
        q.setParameter("numeroProcesso", StringUtil.retornaApenasNumeros(bloqueioJudicial.getProcessoJudicial().getNumeroProcessoForum()));
        q.setParameter("idPessoa", bloqueioJudicial.getContribuintePenhora().getId());

        List<BigDecimal> idsCda = q.getResultList();
        return idsCda != null ? Util.converterToLongList(idsCda) : Collections.<Long>emptyList();
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4L)
    public Future<AssistenteBloqueioJudicial> processarBloqueioJudicial(AssistenteBloqueioJudicial assistente) throws Exception {
        BloqueioJudicial bloqueioJudicial = (BloqueioJudicial) assistente.getSelecionado();
        assistente.setTotal(1 + bloqueioJudicial.getParcelas().size() + assistente.getTotalParcelasResiduais());
        assistente.setMensagem("Iniciando...");
        LoteBaixa loteBaixa = criarLoteBaixa(bloqueioJudicial);
        gerarParcelasResiduais(assistente, bloqueioJudicial, loteBaixa);

        loteBaixa = em.merge(loteBaixa);
        loteBaixaFacade.consisteLote(loteBaixa);
        bloqueioJudicial.setSituacao(SituacaoProcessoDebito.FINALIZADO);
        adicionarHistoricoSituacoes(bloqueioJudicial, assistente.getUsuarioSistema());

        bloqueioJudicial = em.merge(bloqueioJudicial);
        assistente.conta();

        assistente.setSelecionado(bloqueioJudicial);
        return new AsyncResult<>(assistente);
    }

    private void gerarParcelasResiduais(AssistenteBloqueioJudicial assistente, BloqueioJudicial bloqueioJudicial, LoteBaixa loteBaixa) throws Exception {
        ProcessoCalcBloqJudicial processoCalculo = new ProcessoCalcBloqJudicial();
        processoCalculo.setBloqueioJudicial(bloqueioJudicial);
        assistente.setMensagem("Gerando lote baixa...");
        for (ResultadoParcela parcelaOriginal : assistente.getParcelasOriginais()) {
            ParcelaValorDivida parcela = recuperarParcela(parcelaOriginal.getIdParcela());

            Optional<ResultadoParcela> opParcelaOriginada = assistente.getParcelasOriginadas().stream()
                .filter((po) -> po.getIdParcela().equals(parcelaOriginal.getIdParcela()))
                .findFirst();

            if (opParcelaOriginada.isPresent()) {
                ResultadoParcela parcelaOriginada = opParcelaOriginada.get();

                adicionarItensDoLoteBaixa(bloqueioJudicial, loteBaixa, parcela, parcelaOriginada.getValorTotal(), assistente);

                if (parcelaOriginal.getValorTotal().compareTo(parcelaOriginada.getValorTotal()) > 0) {
                    BigDecimal proporcaoResidual = (parcelaOriginada.getValorTotal().multiply(CEM)).divide(parcelaOriginal.getValorTotal(), 8, RoundingMode.HALF_UP);
                    proporcaoResidual = CEM.subtract(proporcaoResidual);
                    CalculoBloqueioJudicial calculo = criarCalculo(processoCalculo, parcela, bloqueioJudicial);
                    adicionarItensAoCalculo(parcela, calculo, proporcaoResidual);
                    processoCalculo.getCalculosBloqueio().add(calculo);
                }
            }
            assistente.conta();
        }
        processoCalculo.setDataLancamento(new Date());
        processoCalculo.setExercicio(assistente.getExercicio());
        processoCalculo = em.merge(processoCalculo);
        bloqueioJudicial.setProcessoCalculo(processoCalculo);

        assistente.setMensagem("Gerando parcelas residuais...");
        for (CalculoBloqueioJudicial calculo : processoCalculo.getCalculosBloqueio()) {
            geraValorDivida.geraValorDivida(calculo);
            assistente.conta();
        }
        assistente.setMensagem("Finalizando...");
    }

    private ParcelaBloqueioJudicial recuperarParcelaBloqueioPeloIdParcela(List<ParcelaBloqueioJudicial> parcelas, Long idParcela) {
        if (parcelas != null && !parcelas.isEmpty()) {
            for (ParcelaBloqueioJudicial parcela : parcelas) {
                if (parcela.getIdParcela().equals(idParcela)) {
                    return parcela;
                }
            }
        }
        return null;
    }

    private void adicionarItensDoLoteBaixa(BloqueioJudicial bloqueioJudicial, LoteBaixa loteBaixa, ParcelaValorDivida parcela, BigDecimal valorTotal, AssistenteBloqueioJudicial assistente) throws Exception {
        loteBaixa.setValorTotal(loteBaixa.getValorTotal().add(valorTotal));
        loteBaixa.setQuantidadeParcelas((loteBaixa.getQuantidadeParcelas() != null ? loteBaixa.getQuantidadeParcelas() : 0) + 1);

        DAM dam = damFacade.geraDAM(parcela, assistente.getVencimentoDam(), assistente.getExercicio(), assistente.getUsuarioSistema());

        ItemLoteBaixa itemLoteBaixa = new ItemLoteBaixa();
        itemLoteBaixa.setDam(dam);
        itemLoteBaixa.setDamInformado(dam.getNumeroDAM());
        itemLoteBaixa.setCodigoBarrasInformado(dam.getCodigoBarras());
        itemLoteBaixa.setValorPago(dam.getValorTotal());
        itemLoteBaixa.setDataPagamento(bloqueioJudicial.getDataProcesso());

        for (ItemDAM itemDam : dam.getItens()) {
            ItemLoteBaixaParcela it = new ItemLoteBaixaParcela();
            it.setItemDam(itemDam);
            it.setItemLoteBaixa(itemLoteBaixa);
            itemLoteBaixa.getItemParcelas().add(it);
            itemLoteBaixa.setDataPagamento(bloqueioJudicial.getDataProcesso());
        }
        itemLoteBaixa.setLoteBaixa(loteBaixa);
        loteBaixa.getItemLoteBaixa().add(itemLoteBaixa);
    }

    private LoteBaixa criarLoteBaixa(BloqueioJudicial bloqueioJudicial) {
        LoteBaixa loteBaixa = new LoteBaixa();
        loteBaixa.setSituacaoLoteBaixa(SituacaoLoteBaixa.EM_ABERTO);
        loteBaixa.setFormaPagamento(LoteBaixa.FormaPagamento.NORMAL);
        loteBaixa.setTipoDePagamentoBaixa(TipoDePagamentoBaixa.BAIXA_MANUAL);
        loteBaixa.setDataFinanciamento(bloqueioJudicial.getDataProcesso());
        loteBaixa.setDataPagamento(bloqueioJudicial.getDataProcesso());
        loteBaixa.setCodigoLote(loteBaixaFacade.gerarCodigoLote(loteBaixa.getDataPagamento()));
        return loteBaixa;
    }

    private BigDecimal proporcionalizarValor(BigDecimal valor, BigDecimal percentual) {
        return valor.multiply(percentual).divide(CEM, 8, RoundingMode.HALF_UP);
    }

    private void alterarSituacaoParcela(ParcelaValorDivida parcela, SituacaoParcela situacaoParcela) {
        JdbcParcelaValorDividaDAO jdbcParcela = JdbcParcelaValorDividaDAO.getInstance();
        jdbcParcela.inserirSituacaoParcelaValorDivida(parcela, parcela.getSituacaoAtual(), situacaoParcela);
    }

    private void estornarDAM(Long idParcela, Long idBloqueio) {
        Long idDam = recuperarIdDAMPeloIdParcela(idParcela, idBloqueio);
        JdbcDamDAO jdbcDamDAO = (JdbcDamDAO) Util.getSpringBeanPeloNome("damDAO");
        jdbcDamDAO.atualizar(idDam, DAM.Situacao.CANCELADO);
    }

    private Long recuperarIdDAMPeloIdParcela(Long idParcela, Long idBloqueio) {
        String sql = " select distinct dam.id from dam " +
            " inner join itemdam on dam.id = itemdam.dam_id " +
            " inner join parcelabloqueiojudicial pb on pb.idparcela = itemdam.parcela_id " +
            " inner join parcelavalordivida pvd on pvd.id = pb.idparcela " +
            " where pvd.id = :idParcela " +
            " and pb.bloqueiojudicial_id = :idBloqueio " +
            " and dam.situacao <> :cancelado";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", idParcela);
        q.setParameter("idBloqueio", idBloqueio);
        q.setParameter("cancelado", DAM.Situacao.CANCELADO.name());

        List<BigDecimal> ids = q.getResultList();
        return (ids != null && !ids.isEmpty()) ? ids.get(0).longValue() : null;
    }

    private CalculoBloqueioJudicial criarCalculo(ProcessoCalcBloqJudicial processoCalculo, ParcelaValorDivida parcela, BloqueioJudicial bloqueioJudicial) {
        CalculoBloqueioJudicial calculo = new CalculoBloqueioJudicial();
        calculo.setProcessoCalcBloqJudicial(processoCalculo);
        calculo.setDivida(parcela.getValorDivida().getDivida());
        calculo.setDividaAtiva(parcela.getDividaAtiva());
        calculo.setDividaAtivaAjuizada(parcela.getDividaAtivaAjuizada());
        calculo.setVencimento(parcela.getVencimento());
        calculo.setCadastro(parcela.getValorDivida().getCalculo().getCadastro());
        calculo.setDataCalculo(new Date());
        calculo.setExercicioParcelaOriginal(parcela.getValorDivida().getExercicio());
        calculo.setSequenciaParcela(parcela.getSequenciaParcela());
        calculo.setQuantidadeParcela(buscarQuantidadeParcelas(parcela));
        calculo.setTipoCalculo(Calculo.TipoCalculo.BLOQUEIO_JUDICIAL);
        calculo.setReferencia(parcela.getSituacaoAtual().getReferencia());
        calculo.setIdParcelaOriginal(parcela.getId());

        adicionarPessoaAoCalculo(calculo, bloqueioJudicial.getContribuinteBloqueio());

        ParcelaBloqueioJudicial parcelaBloqueio = recuperarParcelaBloqueioPeloIdParcela(bloqueioJudicial.getParcelas(), parcela.getId());
        if (parcelaBloqueio != null) calculo.setParcelaBloqueioJudicial(parcelaBloqueio);
        return calculo;
    }

    private void adicionarPessoaAoCalculo(CalculoBloqueioJudicial calculo, Pessoa pessoa) {
        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setCalculo(calculo);
        calculoPessoa.setPessoa(pessoa);
        if (calculo.getPessoas() == null) {
            calculo.setPessoas(Lists.newArrayList());
        }
        calculo.getPessoas().add(calculoPessoa);
    }

    private void adicionarItensAoCalculo(ParcelaValorDivida parcela, CalculoBloqueioJudicial calculo, BigDecimal proporcaoResidual) {
        if (parcela != null) {
            BigDecimal valorTotal = parcela.getItensParcelaValorDivida().stream()
                .map(ItemParcelaValorDivida::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            valorTotal = valorTotal.multiply(proporcaoResidual.divide(CEM, 2, RoundingMode.HALF_UP));
            for (ItemParcelaValorDivida itemParcela : parcela.getItensParcelaValorDivida()) {
                BigDecimal valorItem = proporcionalizarValor(itemParcela.getValor(), proporcaoResidual);

                ItemCalculoBloqueioJudicial itemBloqueio = new ItemCalculoBloqueioJudicial();
                itemBloqueio.setCalculoBloqueioJudicial(calculo);
                itemBloqueio.setTributo(itemParcela.getTributo());
                itemBloqueio.setProporcaoResidual(proporcaoResidual);
                itemBloqueio.setValor(valorItem);

                calculo.setValorEfetivo(calculo.getValorEfetivo().add(itemBloqueio.getValor()));
                calculo.setValorReal(calculo.getValorReal().add(itemBloqueio.getValor()));
                calculo.getItensBloqueio().add(itemBloqueio);
            }
            BigDecimal valorTotalItens = calculo.getItensBloqueio()
                .stream()
                .map(ItemCalculoBloqueioJudicial::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal diferenca = valorTotal.subtract(valorTotalItens);
            if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
                ItemCalculoBloqueioJudicial primeiroItem = calculo.getItensBloqueio().get(0);
                primeiroItem.setValor(primeiroItem.getValor().add(diferenca));
                calculo.setValorEfetivo(calculo.getValorEfetivo().add(diferenca));
                calculo.setValorReal(calculo.getValorReal().add(diferenca));
            }
        }
    }

    private List<ParcelaValorDivida> buscarParcelas(BloqueioJudicial bloqueioJudicial) {
        List<ParcelaValorDivida> parcelas = Lists.newArrayList();
        for (ParcelaBloqueioJudicial parcela : bloqueioJudicial.getParcelas()) {
            parcelas.add(recuperarParcela(parcela.getIdParcela()));
        }
        return parcelas;
    }

    public BloqueioJudicial buscarProcessoBloqueioJudicialPeloIdParcelaOriginal(Long idParcela) {
        String sql = " select bloqueio.* from bloqueiojudicial bloqueio " +
            " inner join parcelabloqueiojudicial parcelas on parcelas.bloqueiojudicial_id = bloqueio.id " +
            " where parcelas.idparcela = :idParcela and bloqueio.situacao <> :estornado ";

        return inicializarBloqueioJudicial(idParcela, sql);
    }

    private BloqueioJudicial inicializarBloqueioJudicial(Long idParcela, String sql) {
        Query q = em.createNativeQuery(sql, BloqueioJudicial.class);
        q.setParameter("idParcela", idParcela);
        q.setParameter("estornado", SituacaoProcessoDebito.ESTORNADO.name());

        List<BloqueioJudicial> bloqueios = q.getResultList();

        if (bloqueios != null && !bloqueios.isEmpty()) {
            BloqueioJudicial bloqueioJudicial = bloqueios.get(0);
            Hibernate.initialize(bloqueioJudicial.getParcelas());

            return bloqueioJudicial;
        }
        return null;
    }

    public BloqueioJudicial buscarProcessoBloqueioJudicialPeloIdParcela(Long idParcela) {
        String sql = " select bloqueio.* from bloqueiojudicial bloqueio " +
            " inner join processocalcbloqjudicial processo on bloqueio.id = processo.bloqueiojudicial_id " +
            " inner join calculobloqueiojudicial calculo on processo.id = calculo.processocalcbloqjudicial_id " +
            " inner join calculo calc on calc.id = calculo.id " +
            " inner join valordivida vd on calc.id = vd.calculo_id " +
            " inner join parcelavalordivida pvd on vd.id = pvd.valordivida_id " +
            " where pvd.id = :idParcela " +
            " and bloqueio.situacao <> :estornado";

        return inicializarBloqueioJudicial(idParcela, sql);
    }

    public BloqueioJudicial estornarProcesso(BloqueioJudicial bloqueioJudicial, ValidacaoException ve) {
        LoteBaixa loteBaixa = buscarLoteBaixa(bloqueioJudicial.getId());
        if (isLoteAbertoOrEstornado(loteBaixa)) {
            estornarParcelasProcesso(bloqueioJudicial);
            estornarParcelasResiduais(bloqueioJudicial);
            bloqueioJudicial.setSituacao(SituacaoProcessoDebito.ESTORNADO);
            loteBaixa.setSituacaoLoteBaixa(SituacaoLoteBaixa.ESTORNADO);
            em.merge(loteBaixa);
        } else if (isLoteBaixado(loteBaixa)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O lote de Arrecadação " + loteBaixa.getCodigoLote() + " se encontra baixado. Para realizar o estorno do processo, " +
                "o lote de arrecadação deve ser estornado primeiro!");
            ve.lancarException();
        } else {
            estornarParcelasProcesso(bloqueioJudicial);
            estornarParcelasResiduais(bloqueioJudicial);
            bloqueioJudicial.setSituacao(SituacaoProcessoDebito.ESTORNADO);
        }
        return em.merge(bloqueioJudicial);
    }

    private void estornarParcelasResiduais(BloqueioJudicial bloqueioJudicial) {
        List<ResultadoParcela> parcelasResiduais = buscarParcelasResiduais(bloqueioJudicial.getId());
        for (ResultadoParcela parcela : parcelasResiduais) {
            ParcelaValorDivida pvd = em.find(ParcelaValorDivida.class, parcela.getIdParcela());
            alterarSituacaoParcela(pvd, SituacaoParcela.CANCELAMENTO);
        }
    }

    private void estornarParcelasProcesso(BloqueioJudicial bloqueioJudicial) {
        for (ParcelaBloqueioJudicial parcelaBloqueio : bloqueioJudicial.getParcelas()) {
            ParcelaValorDivida parcela = recuperarParcela(parcelaBloqueio.getIdParcela());
            alterarSituacaoParcela(parcela, SituacaoParcela.EM_ABERTO);
            estornarDAM(parcela.getId(), bloqueioJudicial.getId());
        }
    }

    public List<Long> montarIdsParcelaBloqueio(BloqueioJudicial bloqueioJudicial) {
        List<Long> ids = Lists.newArrayList();
        for (ParcelaBloqueioJudicial parcela : bloqueioJudicial.getParcelas()) {
            ids.add(parcela.getIdParcela());
        }
        return ids;
    }

    private boolean isLoteAbertoOrEstornado(LoteBaixa loteBaixa) {
        return loteBaixa != null && (SituacaoLoteBaixa.EM_ABERTO.equals(loteBaixa.getSituacaoLoteBaixa()) || SituacaoLoteBaixa.ESTORNADO.equals(
            loteBaixa.getSituacaoLoteBaixa()));
    }

    private boolean isLoteBaixado(LoteBaixa loteBaixa) {
        return loteBaixa != null && (SituacaoLoteBaixa.BAIXADO.equals(loteBaixa.getSituacaoLoteBaixa()) || SituacaoLoteBaixa.BAIXADO_INCONSITENTE.equals(
            loteBaixa.getSituacaoLoteBaixa()));
    }

    private LoteBaixa buscarLoteBaixa(Long idBloqueioJudicial) {
        String sql = " select distinct lb.id from lotebaixa lb " +
            " inner join itemlotebaixa ilb on lb.id = ilb.lotebaixa_id " +
            " inner join itemlotebaixaparcela ilbp on ilb.id = ilbp.itemlotebaixa_id " +
            " inner join itemdam idam on ilbp.itemdam_id = idam.id " +
            " inner join dam on idam.dam_id = dam.id " +
            " inner join parcelabloqueiojudicial pb on pb.idparcela = idam.parcela_id " +
            " inner join bloqueiojudicial bl on pb.bloqueiojudicial_id = bl.id " +
            " where bl.id = :idBloqueio and bl.situacao <> :estornado " +
            " and lb.situacaolotebaixa <> :estornado ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idBloqueio", idBloqueioJudicial);
        q.setParameter("estornado", SituacaoProcessoDebito.ESTORNADO.name());

        List<BigDecimal> idLotes = q.getResultList();
        return !idLotes.isEmpty() ? loteBaixaFacade.recuperar(idLotes.get(0).longValue()) : null;
    }

    public List<ResultadoParcela> buscarParcelasResiduais(Long idBloqueio) {
        ConsultaParcela consultaParcela = montarConsultaParcelaResidual(idBloqueio);
        List<ResultadoParcela> parcelas = consultaParcela.executaConsulta().getResultados();
        return parcelas != null ? parcelas : Lists.newArrayList();
    }

    private ConsultaParcela montarConsultaParcelaResidual(Long idBloqueio) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addComplementoJoin(" inner join calculobloqueiojudicial calc on vw.calculo_id = calc.id ");
        consultaParcela.addComplementoJoin(" inner join processocalcbloqjudicial processo on calc.processocalcbloqjudicial_id = processo.id ");
        consultaParcela.addComplementoJoin(" inner join bloqueiojudicial bl on processo.bloqueiojudicial_id = bl.id ");
        consultaParcela.addComplementoDoWhere(" and bl.id = " + idBloqueio);
        return consultaParcela;
    }

    public void imprimirDAM(DAM dam) throws Exception {
        if (dam != null) {
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamUnicoViaApi(Lists.newArrayList(dam));
        }
    }

    public CertidaoDividaAtiva recuperarCdaPeloId(Long idCda) {
        return em.find(CertidaoDividaAtiva.class, idCda);
    }

    public void pagarCdasDaParcelaResidualDoBloqueioJudicial(CalculoBloqueioJudicial calculo) {
        BloqueioJudicial bloqueioJudicial = buscarBloqueioJudicialPeloCalculo(calculo.getId());
        if (bloqueioJudicial != null) {
            for (CDABloqueioJudicial cdaBloqueioJudicial : bloqueioJudicial.getCdasBloqueioJudicial()) {
                List<ResultadoParcela> parcelas = Lists.newArrayList();
                List<VOItemCertidaoDividaAtiva> itens = certidaoDividaAtivaFacade.buscarVOItensCertidao(cdaBloqueioJudicial.getIdCda());
                for (VOItemCertidaoDividaAtiva item : itens) {
                    parcelas.addAll(certidaoDividaAtivaFacade.buscarParcelasDoCalculo(item.getIdItemInscricao()));
                }

                boolean todasPagas = true;
                for (ResultadoParcela parcela : parcelas) {
                    if (!SituacaoParcela.getsituacoesPago().contains(parcela.getSituacaoNameEnum())) {
                        todasPagas = false;
                    }
                }

                if (todasPagas) {
                    CertidaoDividaAtiva cda = recuperarCdaPeloId(cdaBloqueioJudicial.getIdCda());
                    cda.setSituacaoCertidaoDA(SituacaoCertidaoDA.QUITADA);
                    em.merge(cda);
                }
            }
        }
    }

    private BloqueioJudicial buscarBloqueioJudicialPeloCalculo(Long idCalculo) {
        String sql = " select bj.* from bloqueiojudicial bj " +
            " inner join processocalcbloqjudicial proc on bj.id = proc.bloqueiojudicial_id " +
            " inner join calculobloqueiojudicial calc on proc.id = calc.processocalcbloqjudicial_id " +
            " where calc.id = :idCalculo ";

        Query q = em.createNativeQuery(sql, BloqueioJudicial.class);
        q.setParameter("idCalculo", idCalculo);

        List<BloqueioJudicial> bloqueios = q.getResultList();

        if (bloqueios != null && !bloqueios.isEmpty()) {
            BloqueioJudicial bloqueioJudicial = bloqueios.get(0);
            Hibernate.initialize(bloqueioJudicial.getCdasBloqueioJudicial());

            return bloqueioJudicial;
        }
        return null;
    }

    public boolean isParcelaBloqueioJudicial(Long idParcela) {
        ParcelaValorDivida parcela = em.find(ParcelaValorDivida.class, idParcela);
        if (parcela != null && parcela.getSituacaoAtual() != null && parcela.getSituacaoAtual().getSituacaoParcela() != null) {
            return parcela.getSituacaoAtual().getSituacaoParcela().isParcelaBloqueioJudicial();
        }
        return false;
    }

    public Long buscarIdCalculoParcelaResidual(Long idParcela) {
        BloqueioJudicial bloqueioJudicial = buscarProcessoBloqueioJudicialPeloIdParcelaOriginal(idParcela);

        String sql = " select calc.id from calculobloqueiojudicial calc " +
            " inner join calculo c on c.id = calc.id " +
            " inner join parcelabloqueiojudicial pb on calc.parcelabloqueiojudicial_id = pb.id " +
            " where pb.idparcela = :idParcela " +
            (bloqueioJudicial != null ? " and  pb.bloqueiojudicial_id = :idBloqueio " : "");

        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", idParcela);
        if (bloqueioJudicial != null) {
            q.setParameter("idBloqueio", bloqueioJudicial.getId());
        }

        List<BigDecimal> calculos = q.getResultList();
        return (calculos != null && !calculos.isEmpty()) ? calculos.get(0).longValue() : null;
    }

    public void adicionarValorAbaterCalculo(Long idCalculo, BigDecimal valorDiferenca) {
        CalculoBloqueioJudicial calculo = em.find(CalculoBloqueioJudicial.class, idCalculo);
        if (calculo != null) {
            calculo.setValorDiferenca(valorDiferenca);
            em.merge(calculo);
        }
    }

    public boolean validaAutorizacaoUsuario(UsuarioSistema usuarioSistema, AutorizacaoTributario autorizacaoTributario) {
        return usuarioSistemaFacade.validaAutorizacaoUsuario(usuarioSistema, autorizacaoTributario);
    }

    public ValoresAtualizadosCDA buscarValorAtualizadoCDA(CertidaoDividaAtiva cda) {
        return certidaoDividaAtivaFacade.valorAtualizadoDaCertidao(cda);
    }

    public Long buscarQuantidadeParcelas(ParcelaValorDivida parcela) {
        try {
            String sql = " select count(p.id) from parcelavalordivida p " +
                " inner join valordivida v on v.id = p.valordivida_id " +
                " where v.id = :idValorDivida " +
                " and p.opcaopagamento_id = :idOpcaoPgto ";

            Query q = em.createNativeQuery(sql);
            q.setParameter("idValorDivida", parcela.getValorDivida().getId());
            q.setParameter("idOpcaoPgto", parcela.getOpcaoPagamento().getId());

            List<BigDecimal> quantidades = q.getResultList();

            return (quantidades != null && !quantidades.isEmpty()) ? quantidades.get(0).longValue() : 1L;
        } catch (Exception e) {
            return 1L;
        }
    }

    public ResultadoParcela buscarCalculoParcelaOriginalInfoConsultaDeDebitos(Long idCalculo) {
        String sql = " select pvd.id, calc.tipocalculo from calculo calc " +
            " inner join valordivida vd on calc.id = vd.calculo_id " +
            " inner join parcelavalordivida pvd on vd.id = pvd.valordivida_id " +
            " where pvd.id = (select calc_bloqueio.idparcelaoriginal from calculobloqueiojudicial calc_bloqueio " +
            "                 where calc_bloqueio.id = :idCalc) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCalc", idCalculo);

        List<Object[]> resultados = q.getResultList();

        if (resultados != null && !resultados.isEmpty()) {
            ResultadoParcela resultadoParcela = new ResultadoParcela();
            Object[] object = resultados.get(0);

            resultadoParcela.setIdParcela(((BigDecimal) object[0]).longValue());
            resultadoParcela.setTipoCalculo((String) object[1]);

            return resultadoParcela;
        }
        return null;
    }

    public List<Long> buscarIdsCalculoItemCda(Long idCda) {
        String sql = " select distinct itemins.id as iditeminscricao " +
            " from itemcertidaodividaativa item " +
            " inner join iteminscricaodividaativa itemins on itemins.id = item.iteminscricaodividaativa_id " +
            " inner join Calculo cal on cal.id = itemins.id " +
            " inner join certidaodividaativa cda on cda.id = item.certidao_id " +
            " inner join inscricaodividaativa ins on ins.id = itemins.inscricaodividaativa_id " +
            " inner join exercicio ex on ex.id = ins.exercicio_id " +
            " inner join divida on divida.id = itemins.divida_id " +
            " where item.certidao_id = :idCda " +
            " and (cal.cadastro_id = cda.cadastro_id or itemins.pessoa_id = cda.pessoa_id) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCda", idCda);

        List<BigDecimal> ids = q.getResultList();
        return ids != null ? Util.converterToLongList(ids) : Collections.<Long>emptyList();
    }

    public ResultadoParcela buscarResultadoParcelaPeloIdParcela(Long idParcela) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, idParcela);
        List<ResultadoParcela> parcelas = consultaParcela.executaConsulta().getResultados();
        return (parcelas != null && !parcelas.isEmpty()) ? parcelas.get(0) : null;
    }

    public VOCertidaDividaAtiva buscarVOCertidaoDividaAtiva(Long idCda) {
        String sql = " select cda.id, cda.numero, ex.ano, cda.datacertidao, " +
            "                 cda.situacaojudicial, cda.situacaocertidaoda" +
            " from certidaodividaativa cda" +
            " inner join exercicio ex on cda.exercicio_id = ex.id " +
            " where cda.id = :idCda ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCda", idCda);

        List<Object[]> cdas = q.getResultList();

        if (cdas != null && !cdas.isEmpty()) {
            Object[] obj = cdas.get(0);
            VOCertidaDividaAtiva cda = new VOCertidaDividaAtiva();
            cda.setIdCda(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
            cda.setNumeroCda(obj[1] != null ? obj[1].toString() : null);
            cda.setAno(obj[2] != null ? ((BigDecimal) obj[2]).intValue() : null);
            cda.setDataCda(obj[3] != null ? (Date) obj[3] : null);
            if (obj[4] != null) {
                SituacaoJudicial situacaoJudicial = Util.traduzirEnum(SituacaoJudicial.class, (String) obj[4]);
                cda.setSituacaoJudicial(situacaoJudicial != null ? situacaoJudicial.getDescricao() : "");
            }
            if (obj[5] != null) {
                SituacaoCertidaoDA situacaoCertidaoDA = Util.traduzirEnum(SituacaoCertidaoDA.class, (String) obj[5]);
                cda.setSituacaoCda(situacaoCertidaoDA != null ? situacaoCertidaoDA.getDescricao() : "");
            }
            return cda;
        }
        return null;
    }
}
