package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOItemLoteBaixa;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcDamDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcParcelaValorDividaDAO;
import br.com.webpublico.negocios.tributario.services.ServiceIntegracaoTributarioContabil;
import br.com.webpublico.nfse.facades.DeclaracaoMensalServicoFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ArrecadacaoFacade {

    private static final Logger logger = LoggerFactory.getLogger(ArrecadacaoFacade.class);

    private final BigDecimal CEM = new BigDecimal("100");
    private final int NUMERO_CASAS = 4;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private GeraValorDividaInconsistencia geraDebitoInconsistencia;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private CalculoITBIFacade calculoITBIFacade;
    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;
    private LoteBaixa loteBaixa;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConfiguracaoDAMFacade configuracaoDAMFacade;
    private Map<Integer, Exercicio> exercicios;
    private ConfiguracaoTributario configuracaoTributario;
    @Resource
    private SessionContext context;
    @Resource
    private UserTransaction userTransaction;
    private JdbcDamDAO damDAO;
    private JdbcParcelaValorDividaDAO parcelaValorDividaDAO;
    private JdbcCalculoDAO calculoDAO;
    @EJB
    private LoteBaixaFacade loteBaixaFacade;
    @EJB
    private ContaCorrenteTributariaFacade contaCorrenteTributariaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private BloqueioJudicialFacade bloqueioJudicialFacade;

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        damDAO = (JdbcDamDAO) ap.getBean("damDAO");
        parcelaValorDividaDAO = (JdbcParcelaValorDividaDAO) ap.getBean("jdbcParcelaValorDividaDAO");
        calculoDAO = (JdbcCalculoDAO) ap.getBean("calculoDAO");
    }

    public void efetuarPagamento(ItemLoteBaixa item, UsuarioSistema usuario, AssistenteArrecadacao assistente) {
        try {
            begin();
            DAM dam = item.getDam();
            if (dam != null && dam.getId() != null) {
                dam = recuperarDAM(dam);
                pagarParcela(dam, item, usuario);
            } else {
                pagarParcelaInconsistente(item, usuario, assistente);
            }
            commit();
        } catch (Exception e) {
            logger.error("Erro ao efetuarPagamento: {}", e);
            rollBack();

        }
    }

    private void rollBack() {
        try {
            userTransaction.rollback();
        } catch (SystemException e1) {
            logger.error("Erro no rollBack: {}", e1);
        }
    }

    private void begin() throws NotSupportedException, SystemException {
        userTransaction = context.getUserTransaction();
        userTransaction.begin();
    }


    private void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException {
        userTransaction.commit();
    }

    public void estornarPagamento(DAM dam, UsuarioSistema usuario) {
        try {
            begin();
            if (dam != null) {
                dam = recuperarDAM(dam);
                if (!damTemPagamento(dam)) {
                    retornarParcelaParaAberto(dam);
                }
            }
            commit();
        } catch (Exception e) {
            logger.error("Erro ao estornarPagamento: {}", e);
            rollBack();
        }
    }

    public boolean parcelaTemPagamento(ParcelaValorDivida parcela) {
        String sql = "SELECT item.id FROM ItemLoteBaixa item " +
            "INNER JOIN loteBaixa ON loteBaixa.id = item.lotebaixa_id " +
            "INNER JOIN itemDam ON itemDam.dam_id = item.dam_id " +
            "WHERE itemDam.parcela_id = :idParcela " +
            "  AND lotebaixa.situacaolotebaixa IN ('BAIXADO', 'BAIXADO_INCONSITENTE')";
        List<BigDecimal> retorno = em.createNativeQuery(sql).setParameter("idParcela", parcela.getId()).getResultList();
        return !retorno.isEmpty();
    }

    public boolean damTemPagamento(DAM dam) {
        String sql = "SELECT item.id FROM ItemLoteBaixa item " +
            "INNER JOIN loteBaixa ON loteBaixa.id = item.lotebaixa_id " +
            "WHERE item.dam_id = :idDam " +
            "  AND lotebaixa.situacaolotebaixa IN ('BAIXADO', 'BAIXADO_INCONSITENTE')";
        List<BigDecimal> retorno = em.createNativeQuery(sql).setParameter("idDam", dam.getId()).getResultList();
        return !retorno.isEmpty();
    }


    public void retornarParcelaParaAberto(DAM dam) {
        for (ItemDAM itemDam : dam.getItens()) {
            ParcelaValorDivida parcela = itemDam.getParcela();
            if (parcela != null && !parcelaTemPagamento(parcela)) {
                parcela.getSituacoes().size();
                adicionarSituacaoParcela(parcela, recuperarSaldoUltimaSituacaoAberto(parcela), SituacaoParcela.EM_ABERTO);
                baixarOutraOpcaoParcelasDaCotaUnicaOrCotaUnidaDasParcelas(parcela);
                atualizar(parcela);
            }
        }
    }

    private BigDecimal recuperarSaldoUltimaSituacaoAberto(ParcelaValorDivida parcela) {
        return recuperarSaldoUltimaSituacao(parcela.getId(), SituacaoParcela.EM_ABERTO);
    }


    private void baixarOutraOpcaoParcelasDaCotaUnicaOrCotaUnidaDasParcelas(ParcelaValorDivida parcela) {
        List<ParcelaValorDivida> parcelas = buscarParcelasPorValorDividaAndDiferenteDaPassadaPorParametro(parcela);
        if (!parcelas.isEmpty()) {
            if (parcela.isCotaUnica()) {
                for (ParcelaValorDivida parcelaValorDivida : parcelas) {
                    criarNovaSituacaoParcelaValorDividaPorParcelaValorDivida(parcelaValorDivida);
                }
            } else {
                try {
                    ValorDivida valorDivida = parcela.getValorDivida();
                    ParcelaValorDivida cotaUnica = valorDivida.getCotaUnica();
                    validarOutroasParcelasPagas(parcelas);
                    criarNovaSituacaoParcelaValorDividaPorParcelaValorDivida(cotaUnica);
                } catch (ExcecaoNegocioGenerica eng) {
                    logger.debug(eng.getMessage());
                }
            }
        }
    }

    private void validarOutroasParcelasPagas(List<ParcelaValorDivida> parcelas) {
        for (ParcelaValorDivida parcelaValorDivida : parcelas) {
            if (!parcelaValorDivida.isCotaUnica()) {
                if (parcelaValorDivida.isSituacaoPago()) {
                    throw new ExcecaoNegocioGenerica("A parcela ID: " + parcelaValorDivida.getId() + " - " + parcelaValorDivida.toString() + " do valor dívida ID: " + parcelaValorDivida.getValorDivida().getId() + " - " + parcelaValorDivida.getValorDivida().toString() + " também está paga. A cota única não será aberta.");
                }
            }
        }
    }

    private void criarNovaSituacaoParcelaValorDividaPorParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        SituacaoParcelaValorDivida situacaoAnterior = consultaDebitoFacade.getUltimaSituacao(parcelaValorDivida);
        em.persist(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcelaValorDivida, situacaoAnterior.getSaldo()));
    }

    private List<ParcelaValorDivida> buscarParcelasPorValorDividaAndDiferenteDaPassadaPorParametro(ParcelaValorDivida parcela) {
        Query q = em.createQuery("select pv from ParcelaValorDivida pv "
            + " where pv.valorDivida = :valorDivida "
            + " and pv <> :parcela ");
        q.setParameter("valorDivida", parcela.getValorDivida());
        q.setParameter("parcela", parcela);
        return q.getResultList();
    }

    public DAM recuperarDAM(DAM dam) {
        try {
            dam = em.find(DAM.class, dam.getId());
            Hibernate.initialize(dam.getItens());
            return dam;
        } catch (Exception e) {
            return dam;
        }
    }

    private void pagarParcela(DAM dam, ItemLoteBaixa item, UsuarioSistema usuario) {
        try {
            for (ItemDAM itemDam : dam.getItens()) {
                SituacaoParcela situacaoParcela = SituacaoParcela.PAGO;
                boolean pagarParcela = true;
                if (DAM.Tipo.SUBVENCAO.equals(itemDam.getDAM().getTipo())) {
                    pagarParcela = itemDam.getParcela().getSituacaoAtual().getSituacaoParcela().isAguardandoPagamentoSubvencao();

                    situacaoParcela = SituacaoParcela.PAGO_SUBVENCAO;
                    List<DAM> outrosDamsDaParcela = buscarOutrosDamsDeSubvencaoDaParcela(dam, itemDam);
                    for (DAM outroDAM : outrosDamsDaParcela) {
                        if (DAM.Situacao.ABERTO.equals(outroDAM.getSituacao())) {
                            pagarParcela = false;
                        }
                    }
                    if (pagarParcela) {
                        pagarParcela = todosDAMsDessaParcelaForamEmitidosNaSubvencao(itemDam.getParcela(), (dam.getItens().size() > 1));
                    }
                }
                if (itemDam.getParcela() != null && SituacaoParcela.AGUARDANDO_PAGAMENTO_BLOQUEIO_JUDICIAL.equals(itemDam.getParcela().getSituacaoAtual().getSituacaoParcela())) {
                    situacaoParcela = SituacaoParcela.PAGO_BLOQUEIO_JUDICIAL;
                }
                if (pagarParcela) {
                    pagaParcelaConsistente(itemDam, situacaoParcela);
                    if (item.getValorDiferenca().compareTo(BigDecimal.ZERO) != 0) {
                        BigDecimal valorDam = itemDam.getDAM().getValorTotal();
                        BigDecimal valorItemDam = itemDam.getValorTotal();
                        BigDecimal diferencaPago = item.getValorDiferenca();
                        BigDecimal valorProporcional = valorItemDam.multiply(diferencaPago).divide(valorDam, 2, BigDecimal.ROUND_HALF_UP);
                        adicionarParcelaAContaCorrente(valorProporcional, itemDam, item);
                    }
                }
            }
            damDAO.atualizar(dam.getId(), DAM.Situacao.PAGO, usuario);
        } catch (Exception e) {
            logger.error("Erro ao pagar a parcela {}", e);
        }
    }

    private void adicionarParcelaAContaCorrente(BigDecimal valorDiferenca, ItemDAM itemDAM, ItemLoteBaixa itemLoteBaixa) {
        Pessoa pessoa = contaCorrenteTributariaFacade.buscarPessoaDaParcela(itemDAM.getParcela());
        if (pessoa != null) {
            if (isPessoaValidaParaContaCorrente(pessoa)) {
                TipoDiferencaContaCorrente tipo = valorDiferenca.compareTo(BigDecimal.ZERO) > 0 ? TipoDiferencaContaCorrente.CREDITO_ARRECADACAO : TipoDiferencaContaCorrente.RESIDUO_ARRECADACAO;
                ContaCorrenteTributaria contaCorrenteTributaria = contaCorrenteTributariaFacade.buscarOuCriarContaCorrentePorPessoa(pessoa, ContaCorrenteTributaria.Origem.ARRECADACAO);
                if (contaCorrenteTributaria != null) {
                    BigDecimal valorDiferencaSemprePositivo = valorDiferenca;
                    if (valorDiferenca.compareTo(BigDecimal.ZERO) < 0) {
                        valorDiferencaSemprePositivo = valorDiferenca.multiply(BigDecimal.valueOf(-1));
                    }
                    contaCorrenteTributariaFacade.adicionarParcelaNaContaCorrente(contaCorrenteTributaria, itemDAM.getParcela(), tipo, valorDiferencaSemprePositivo, ContaCorrenteTributaria.Origem.ARRECADACAO, itemLoteBaixa);
                    contaCorrenteTributariaFacade.salvar(contaCorrenteTributaria);
                }
            }
        }
    }

    private boolean isPessoaValidaParaContaCorrente(Pessoa pessoa) {
        boolean pessoaValida = true;
        if (Util.valida_CpfCnpj(pessoa.getCpf_Cnpj())) {
            if (pessoa instanceof PessoaFisica) {
                if (pessoaFacade.hasOutraPessoaComMesmoCpf((PessoaFisica) pessoa, true)) {
                    pessoaValida = false;
                }
            } else {
                if (pessoaFacade.hasOutraPessoaComMesmoCnpj((PessoaJuridica) pessoa, true)) {
                    pessoaValida = false;
                }
            }
        } else {
            pessoaValida = false;
        }
        return pessoaValida;
    }

    private void removerParcelaDaContaCorrente(ItemLoteBaixa item, ItemDAM itemDAM) {
        if (item.getValorDiferenca().compareTo(BigDecimal.ZERO) != 0) {
            Pessoa pessoa = contaCorrenteTributariaFacade.buscarPessoaDaParcela(itemDAM.getParcela());
            if (pessoa != null) {
                ContaCorrenteTributaria contaCorrenteTributaria = contaCorrenteTributariaFacade.buscarContaCorrentePorPessoa(pessoa);
                if (contaCorrenteTributaria != null) {
                    contaCorrenteTributariaFacade.removerParcelaDaContaCorrente(contaCorrenteTributaria, itemDAM.getParcela(), ContaCorrenteTributaria.Origem.ARRECADACAO, item);
                    contaCorrenteTributariaFacade.salvar(contaCorrenteTributaria);
                }
            }
        }
    }

    private List<DAM> buscarOutrosDamsDeSubvencaoDaParcela(DAM dam, ItemDAM itemDAM) {
        String sql = "select dam.* from DAM dam " +
            "inner join ItemDam itemDam on itemdam.dam_id = dam.id " +
            "inner join SubvencaoParcela sp on sp.dam_id = dam.id " +
            "inner join SubvencaoEmpresas se on se.id = sp.subvencaoempresas_id " +
            "inner join SubvencaoProcesso sub on sub.id = se.subvencaoprocesso_id " +
            "where itemdam.parcela_id = :idParcela " +
            "  and sub.situacao = :situacaoSubvencao " +
            "  and dam.id <> :idDam " +
            "  and dam.tipo = :tipoDam";
        Query q = em.createNativeQuery(sql, DAM.class);
        q.setParameter("idDam", dam.getId());
        q.setParameter("idParcela", itemDAM.getParcela().getId());
        q.setParameter("tipoDam", DAM.Tipo.SUBVENCAO.name());
        q.setParameter("situacaoSubvencao", SituacaoSubvencao.EFETIVADO.name());
        return q.getResultList();
    }

    private boolean todosDAMsDessaParcelaForamEmitidosNaSubvencao(ParcelaValorDivida parcelaValorDivida, boolean damAgrupado) {
        String sql = "select sp.id from SubvencaoParcela sp " +
            "inner join SubvencaoEmpresas se on se.id = sp.subvencaoempresas_id " +
            "inner join SubvencaoProcesso sub on sub.id = se.subvencaoprocesso_id " +
            "where sub.situacao = :situacaoSubvencao " +
            "  and sp.parcelavalordivida_id = :idParcela " +
            "  and sp.dam_id is null";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", parcelaValorDivida.getId());
        q.setParameter("situacaoSubvencao", SituacaoSubvencao.EFETIVADO.name());
        List resultList = q.getResultList();
        if (damAgrupado) {
            return resultList.size() == 1;
        }
        return resultList.isEmpty();
    }

    private Object atualizar(Object entity) {
        return em.merge(entity);
    }

    private void pagaParcelaConsistente(ItemDAM itemDam, SituacaoParcela situacaoParcela) throws AtributosNulosException {
        parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(itemDam.getParcela(), itemDam.getParcela().getSituacaoAtual(), situacaoParcela);
        parcelaValorDividaDAO.baixarOutraOpcaoPagamentoEmAberto(itemDam.getParcela());
    }

    private void adicionarSituacaoParcela(ParcelaValorDivida parcela, BigDecimal valor, SituacaoParcela sit) {
        parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(parcela, parcela.getSituacaoAtual(), sit);
    }

    private ProcessoInconsistencia inicializaProcessoCalculoInconsistencia(ItemLoteBaixa item, ConfiguracaoDAM configuracaoDAM) {
        ProcessoInconsistencia processo = new ProcessoInconsistencia();
        processo.setCompleto(Boolean.TRUE);
        processo.setDataLancamento(item.getLoteBaixa().getDataPagamento());
        processo.setDescricao("Lançamento por inconsistência");
        processo.setLoteBaixa(item.getLoteBaixa());
        processo.setExercicio(getExercicio(Calendar.getInstance().get(Calendar.YEAR)));
        processo.setDivida(configuracaoDAM != null ? configuracaoDAM.getDividaDamNaoEncontrado() : getConfiguracaoTributario().getDividaInconsistencia());
        return processo;
    }

    private ConfiguracaoTributario getConfiguracaoTributario() {
        if (configuracaoTributario == null) {
            configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        }
        return configuracaoTributario;
    }

    public Exercicio getExercicio(Integer ano) {
        if (exercicios == null) {
            exercicios = Maps.newHashMap();
        }
        if (!exercicios.containsKey(ano)) {
            exercicios.put(ano, exercicioFacade.getExercicioPorAno(ano));
        }
        return exercicios.get(ano);
    }

    private CalculoInconsistencia gerarCalculoInsconsistente(ItemLoteBaixa itemLoteBaixa, ProcessoInconsistencia processo, ConfiguracaoDAM configuracaoDAM) {
        CalculoInconsistencia calculo = new CalculoInconsistencia();
        calculo.setCadastro(null);
        calculo.setDataCalculo(new Date());
        calculo.setItemLoteBaixa(itemLoteBaixa);
        calculo.setProcessoCalcInconsistencia(processo);
        calculo.setSimulacao(Boolean.FALSE);
        calculo.setVencimento(new Date());
        calculo.setTributo(configuracaoDAM != null ? configuracaoDAM.getTributoDamNaoEncontrado() : getConfiguracaoTributario().getTributoInconsistencia());
        calculo.setValorEfetivo(itemLoteBaixa.getValorPago());
        calculo.setValorReal(itemLoteBaixa.getValorPago());
        CalculoPessoa pessoa = new CalculoPessoa();
        pessoa.setCalculo(calculo);
        pessoa.setPessoa(configuracaoDAM != null ? configuracaoDAM.getPessoaDamNaoEncontrado() : getConfiguracaoTributario().getContribuinteInconsistencia());
        calculo.getPessoas().add(pessoa);
        return calculo;
    }

    private void pagarParcelaInconsistente(ItemLoteBaixa item, UsuarioSistema usuario, AssistenteArrecadacao assistente) {
        try {
            ConfiguracaoDAM configuracaoDAM = getConfiguracaoDAM(item.getCodigoBarrasInformado());
            ProcessoInconsistencia processo = inicializaProcessoCalculoInconsistencia(item, configuracaoDAM);
            processo.getCalculos().add(gerarCalculoInsconsistente(item, processo, configuracaoDAM));
            processo = (ProcessoInconsistencia) atualizar(processo);
            assistente.getValoresDividaInconsistencia().addAll(geraDebitoInconsistencia.lancaValorDividaInconsistente(processo, configuracaoDAM, usuario));
        } catch (Exception e) {
            logger.error("Exception ao pagar parcela inconsistente", e);
        }
    }

    private ConfiguracaoDAM getConfiguracaoDAM(String codigoBarrasInformado) {
        if (codigoBarrasInformado != null && !"".equals(codigoBarrasInformado)) {
            String codigoFebraban = codigoBarrasInformado.substring(18, 22);
            return configuracaoDAMFacade.buscarConfiguracaoPeloCodigoFebraban(codigoFebraban);
        }
        return null;
    }


    private void depoisDeEstornarParcelamento(ProcessoParcelamento parcelamento) {
        try {
            processoParcelamentoFacade.depoisDeEstornarPagamentoParcelamento(parcelamento);
        } catch (Exception ex) {

        }
    }

    public Calculo buscarCalculoOriginalDaDividaAtiva(Calculo calculoDividaAtiva) {
        String sql = "select cal.id from ItemInscricaoDividaAtiva iida\n" +
            "inner join InscricaoDividaParcela idp on idp.iteminscricaodividaativa_id = iida.id\n" +
            "inner join ParcelaValorDivida pvd on pvd.id = idp.parcelavalordivida_id\n" +
            "inner join ValorDivida vd on vd.id = pvd.valordivida_id\n" +
            "inner join Calculo cal on cal.id = vd.calculo_id\n" +
            "where iida.id = :idCalculoDA";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCalculoDA", calculoDividaAtiva.getId());
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return em.find(Calculo.class, ((BigDecimal) resultList.get(0)).longValue());
        }
        return null;
    }

    public Calculo initializeAndUnproxy(Calculo calculo) {
        if (calculo == null) {
            return null;
        }
        Hibernate.initialize(calculo);
        if (calculo instanceof HibernateProxy) {
            calculo = (Calculo) ((HibernateProxy) calculo).getHibernateLazyInitializer().getImplementation();
        }
        return calculo;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteArrecadacao efetuaPagamento(AssistenteArrecadacao assistente, UsuarioSistema usuario) {
        loteBaixa = assistente.getLoteBaixa();
        for (ItemLoteBaixa item : loteBaixa.getItemLoteBaixa()) {
            try {
                efetuarPagamento(item, usuario, assistente);
            } catch (Exception e) {
                logger.error("Não arrecadou o item " + item.getId());
            }
            assistente.conta();
        }
        return assistente;
    }

    public void gerarDamsDeInconsistencia(LoteBaixa loteBaixa, UsuarioSistema usuario, List<ValorDivida> valoresDividaInconsistencia) {
        if (!valoresDividaInconsistencia.isEmpty()) {
            for (ValorDivida valorDivida : valoresDividaInconsistencia) {
                geraDebitoInconsistencia.geraDAM(valorDivida, usuario, loteBaixa);
            }
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void gerarIntegracao(LoteBaixa loteBaixa, TipoIntegracao tipo) {
        gerarIntegracaoSync(loteBaixa, tipo);
    }

    public void gerarIntegracaoSync(LoteBaixa loteBaixa, TipoIntegracao tipo) {
        try {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            ServiceIntegracaoTributarioContabil service = (ServiceIntegracaoTributarioContabil) ap.getBean("serviceIntegracaoTributarioContabil");
            List<VOItemLoteBaixa> itensLoteBaixa = loteBaixaFacade.buscarVOItensLoteBaixaDoLote(loteBaixa.getId());
            service.integrarArrecadacao(loteBaixa, itensLoteBaixa, tipo);
        } catch (Exception e) {
            logger.error("Erro ao gerarIntegracao: {}", e);
        }
    }

    public void executarDependenciasEstorno(LoteBaixa loteBaixa) {
        for (ItemLoteBaixa item : loteBaixa.getItemLoteBaixa()) {
            try {
                begin();
                DAM dam = recuperarDAM(item.getDam());
                if (dam != null) {
                    for (ItemDAM itemDAM : dam.getItens()) {
                        try {
                            executarDepoisDeEstornar(itemDAM.getParcela().getValorDivida().getCalculo());
                            removerParcelaDaContaCorrente(item, itemDAM);
                        } catch (Exception e) {
                            logger.error("Não efetuou o pós pagamento ", e);
                        }
                    }
                }
                commit();
            } catch (Exception e) {
                logger.error("Não efetuou o pós pagamento ", e);
            }
        }
    }

    private void executarDepoisDeEstornar(Calculo calculo) {
        calculo = initializeAndUnproxy(calculo);
        Calculo.TipoCalculo tipo = calculo.getTipoCalculo();
        switch (tipo) {
            case PARCELAMENTO:
                depoisDeEstornarParcelamento((ProcessoParcelamento) calculo);
                break;
            case ALVARA_CONSTRUCAO_HABITESE:
                depoisDeEstornarAlvaraConstrucao(((CalculoAlvaraConstrucaoHabitese) calculo));
                break;
        }
    }

    private void depoisDeEstornarAlvaraConstrucao(CalculoAlvaraConstrucaoHabitese calculo) {
        AlvaraConstrucao alvaraConstrucao = calculo.getProcCalcAlvaraConstruHabit().getAlvaraConstrucao();
        Habitese habitese = calculo.getProcCalcAlvaraConstruHabit().getHabitese();
        if (alvaraConstrucao == null) {
            alvaraConstrucao = habitese.getAlvaraConstrucao();
        }
        ProcRegularizaConstrucao procRegularizaConstrucao = alvaraConstrucao.getProcRegularizaConstrucao();
        switch (calculo.getSubTipoCalculo()) {
            case ALVARA:
                if (ProcRegularizaConstrucao.Situacao.ALVARA_CONSTRUCAO.equals(procRegularizaConstrucao.getSituacao())) {
                    alvaraConstrucao.setSituacao(AlvaraConstrucao.Situacao.EFETIVADO);
                    alvaraConstrucao.setDocumentoOficial(null);
                    procRegularizaConstrucao.setSituacao(ProcRegularizaConstrucao.Situacao.AGUARDANDO_ALVARA_CONSTRUCAO);
                }
                break;
            case VISTORIA:
                if (ProcRegularizaConstrucao.Situacao.TAXA_VISTORIA.equals(procRegularizaConstrucao.getSituacao())) {
                    procRegularizaConstrucao.setSituacao(ProcRegularizaConstrucao.Situacao.AGUARDANDO_TAXA_VISTORIA);
                }
                break;
            case HABITESE:
                if (ProcRegularizaConstrucao.Situacao.HABITESE.equals(procRegularizaConstrucao.getSituacao())) {
                    procRegularizaConstrucao.setSituacao(ProcRegularizaConstrucao.Situacao.AGUARDANDO_HABITESE);
                    habitese.setSituacao(Habitese.Situacao.EFETIVADO);
                }
                break;
        }
    }

    public AssistenteArrecadacao estornarPagamento(AssistenteArrecadacao assistente, UsuarioSistema usuario) {
        for (ItemLoteBaixa itemLoteBaixa : assistente.getLoteBaixa().getItemLoteBaixa()) {
            try {
                estornarDam(itemLoteBaixa.getDam(), assistente.getLoteBaixa(), usuario);
            } catch (Exception e) {
                logger.error("Erro ao estornaPagamento: {}", e);
            } finally {
                assistente.conta();
            }
        }
        return assistente;
    }

    private void estornarDam(DAM dam, LoteBaixa loteBaixa, UsuarioSistema usuario) {
        if (dam != null) {
            if (verificarSeDamDoLoteEstaPagoApenasNesseLote(loteBaixa.getId(), dam.getId())) {
                if (loteBaixa.getArquivosLoteBaixa() != null &&
                    !loteBaixa.getArquivosLoteBaixa().isEmpty() &&
                    loteBaixa.getArquivosLoteBaixa().get(0).getArquivoLoteBaixa().isDaf607()) {
                    damDAO.atualizar(dam.getId(), DAM.Situacao.CANCELADO, usuario);
                } else {
                    damDAO.atualizar(dam.getId(), DAM.Situacao.ABERTO, usuario);
                }
            }
            estornarParcelas(dam, loteBaixa, usuario);
        }
    }

    private void estornarParcelas(DAM dam, LoteBaixa loteBaixa, UsuarioSistema usuario) {
        List<Long> idsCalculoEstornado = Lists.newArrayList();
        for (ParcelaParaPagamento parcela : buscarSituacaoParcelasDoDamQueEstaoPagosApenasNesseLote(dam.getId(), loteBaixa.getId())) {
            SituacaoParcela situacaoParcela;
            if (bloqueioJudicialFacade.isParcelaBloqueioJudicial(parcela.idParcela)) {
                situacaoParcela = SituacaoParcela.AGUARDANDO_PAGAMENTO_BLOQUEIO_JUDICIAL;
            } else if (loteBaixa.getArquivosLoteBaixa() != null && !loteBaixa.getArquivosLoteBaixa().isEmpty()
                && loteBaixa.getArquivosLoteBaixa().get(0) != null && loteBaixa.getArquivosLoteBaixa().get(0).getArquivoLoteBaixa().isDaf607()) {
                situacaoParcela = SituacaoParcela.CANCELAMENTO;
            } else {
                situacaoParcela = buscarSituacaoQueAParcelaIraReceber(parcela.idParcela, dam, parcela.idSituacao);
            }
            parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(
                parcela.idParcela,
                parcela.referencia,
                recuperarSaldoUltimaSituacao(parcela.idParcela, situacaoParcela),
                situacaoParcela
            );
            List<ParcelaParaPagamento> outrasParcelas = buscarOutrasParcelasDeOpcaoPagamentoDiferentePagasPorEstaParcela(parcela);
            for (ParcelaParaPagamento outraParcela : outrasParcelas) {
                situacaoParcela = buscarSituacaoQueAParcelaIraReceber(outraParcela.idParcela, dam, outraParcela.idSituacao);
                parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(
                    outraParcela.idParcela,
                    outraParcela.referencia,
                    recuperarSaldoUltimaSituacao(outraParcela.idParcela, situacaoParcela),
                    situacaoParcela
                );
                DAM damOutraOpcao = buscarUltimoDamCanceladoDaParcela(outraParcela.idParcela);
                if (damOutraOpcao != null) {
                    damDAO.atualizar(damOutraOpcao.getId(), DAM.Situacao.ABERTO, usuario);
                }
            }

            if (!idsCalculoEstornado.contains(parcela.idCalculo)) {
                idsCalculoEstornado.add(parcela.idCalculo);

                if (Calculo.TipoCalculo.ITBI.equals(parcela.getTipoCalculoEnumValue())) {
                    estornarPagamentoITBI(parcela.idCalculo);
                }
            }
        }
    }

    private DAM buscarUltimoDamCanceladoDaParcela(Long idParcela) {
        String sql = "select  DAM.* from itemdam item " +
            "inner join dam on item.DAM_ID = dam.id " +
            "where item.PARCELA_ID  = :idParcela " +
            "and dam.emissao = (select max(sdam.emissao) from dam sdam inner join itemdam sitemdam on sitemdam.dam_id = sdam.id  " +
            "where sitemdam.parcela_id = item.parcela_id) and dam.SITUACAO = :situacaoDAM ";
        Query q = em.createNativeQuery(sql, DAM.class);
        q.setParameter("idParcela", idParcela);
        q.setParameter("situacaoDAM", DAM.Situacao.CANCELADO.name());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (DAM) resultList.get(0);
        } else {
            return null;
        }
    }

    private void estornarPagamentoITBI(Long idCalculo) {
        ProcessoCalculoITBI processo = calculoITBIFacade.recuperarProcessoPeloIdCalculo(idCalculo);

        if (processo != null) {
            ConsultaParcela consultaITBI = new ConsultaParcela();
            consultaITBI.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, processo.getUltimoCalculo().getId());
            consultaITBI.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
            List<ResultadoParcela> parcelasITBI = consultaITBI.executaConsulta().getResultados();

            if (!parcelasITBI.isEmpty()) {
                calculoDAO.atualizarSituacaoITBI(processo.getId(), SituacaoITBI.PROCESSADO.name());
            }
        }
    }

    public BigDecimal recuperarSaldoUltimaSituacao(Long idParcela, SituacaoParcela situacaoParcela) {
        String sql = " SELECT saldo FROM situacaoparcelavalordivida " +
            " WHERE parcela_id = :parcela " +
            " AND situacaoparcela = :situacao " +
            " ORDER BY datalancamento DESC ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("parcela", idParcela);
        q.setParameter("situacao", situacaoParcela.name());
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (BigDecimal) resultList.get(0);
        }
        return BigDecimal.ZERO;
    }

    public Boolean verificarSeDamDoLoteEstaPagoApenasNesseLote(Long idLoteBaixa, Long idDam) {
        Query query = em.createNativeQuery("SELECT dam.id FROM dam  " +
            "INNER JOIN itemlotebaixa ilb ON ilb.dam_id = dam.id " +
            "WHERE ilb.lotebaixa_id = :idLoteBaixa " +
            "AND dam.situacao = :situacaoDam " +
            "AND dam.id = :idDam " +
            "AND NOT exists(SELECT it.id FROM itemlotebaixa it " +
            "              INNER JOIN lotebaixa lb ON lb.id = it.lotebaixa_id " +
            "                  WHERE it.dam_id = dam.id AND lb.situacaolotebaixa IN (:baixado, :baixadoInconsistente) " +
            "                  AND lb.id <> ilb.lotebaixa_id)");

        query.setParameter("idLoteBaixa", idLoteBaixa);
        query.setParameter("idDam", idDam);
        query.setParameter("situacaoDam", DAM.Situacao.PAGO.name());
        query.setParameter("baixado", SituacaoLoteBaixa.BAIXADO.name());
        query.setParameter("baixadoInconsistente", SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name());
        return !query.getResultList().isEmpty();
    }

    public List<ParcelaParaPagamento> buscarSituacaoParcelasDoDamQueEstaoPagosApenasNesseLote(Long idDam, Long idLoteBaixa) {
        Query query = em.createNativeQuery("SELECT pvd.id as idparcela, spvd.id as idsituacao, spvd.referencia, pvd.valordivida_id as idvalordivida, pvd.opcaopagamento_id as idopcaopagamento, calculo.id as idcalculo, calculo.tipoCalculo " +
            "FROM dam " +
            "INNER JOIN itemlotebaixa ilb ON ilb.dam_id = dam.id " +
            "INNER JOIN itemdam ON itemdam.dam_id = dam.id " +
            "INNER JOIN parcelavalordivida pvd ON pvd.id = itemdam.parcela_id " +
            "INNER JOIN situacaoparcelavalordivida spvd ON spvd.id = pvd.situacaoatual_id " +
            "INNER JOIN valordivida vd on vd.id = pvd.valordivida_id " +
            "INNER JOIN calculo calculo on calculo.id = vd.calculo_id " +
            "WHERE ilb.lotebaixa_id = :idLoteBaixa " +
            "AND spvd.situacaoparcela in (:situacaoparcela) " +
            "AND dam.id = :idDam " +
            "AND NOT exists(SELECT it.id FROM itemlotebaixa it " +
            "              INNER JOIN lotebaixa lb ON lb.id = it.lotebaixa_id " +
            "                  WHERE it.dam_id = dam.id AND lb.situacaolotebaixa IN (:baixado, :baixadoInconsistente) " +
            "                  AND lb.id <> ilb.lotebaixa_id) ");

        query.setParameter("idLoteBaixa", idLoteBaixa);
        query.setParameter("idDam", idDam);
        query.setParameter("situacaoparcela", Lists.newArrayList(SituacaoParcela.PAGO.name(), SituacaoParcela.PAGO_SUBVENCAO.name(),
            SituacaoParcela.PAGO_BLOQUEIO_JUDICIAL.name()));
        query.setParameter("baixado", SituacaoLoteBaixa.BAIXADO.name());
        query.setParameter("baixadoInconsistente", SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name());
        List<ParcelaParaPagamento> parcelas = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) query.getResultList()) {
            parcelas.add(new ParcelaParaPagamento((BigDecimal) obj[0], (BigDecimal) obj[1], (String) obj[2], (BigDecimal) obj[3], (BigDecimal) obj[4], (BigDecimal) obj[5], (String) obj[6]));
        }

        return parcelas;
    }

    public List<ParcelaParaPagamento> buscarOutrasParcelasDeOpcaoPagamentoDiferentePagasPorEstaParcela(ParcelaParaPagamento parcela) {
        String sql = "SELECT pvd.id as idparcela, spvd.id as idsituacao, spvd.referencia, pvd.valordivida_id as idvalordivida, pvd.opcaopagamento_id as idopcaopagamento, calculo.id as idcalculo, calculo.tipoCalculo " +
            "FROM parcelavalordivida pvd " +
            "INNER JOIN situacaoparcelavalordivida spvd ON spvd.id = pvd.SITUACAOATUAL_ID " +
            "INNER JOIN valordivida vd ON vd.id = pvd.valordivida_id " +
            "INNER JOIN calculo calculo on calculo.id = vd.calculo_id " +
            "WHERE vd.id = :idValorDivida AND pvd.opcaopagamento_id <> :idOpcaoPagamento " +
            "AND spvd.SITUACAOPARCELA = :situacaoParcela " +
            " and not exists  " +
            "( select 1 " +
            "from parcelavalordivida pvd2 " +
            "inner join situacaoparcelavalordivida spvd2 on spvd2.id = pvd2.SITUACAOATUAL_ID " +
            "inner join itemdam on itemdam.parcela_id = pvd2.id " +
            "inner join dam on dam.id = itemdam.dam_id " +
            "inner join itemlotebaixa ilb on ilb.dam_id = dam.id " +
            "inner join lotebaixa lb on lb.id = ilb.lotebaixa_id  " +
            "where pvd2.valordivida_id = pvd.valordivida_id  " +
            "and spvd2.SITUACAOPARCELA = :situacaoparcelaPago " +
            "and lb.SITUACAOLOTEBAIXA in (:baixado, :baixadoInconsistente) " +
            ") ";

        Query query = em.createNativeQuery(sql);

        query.setParameter("idValorDivida", parcela.idValorDivida);
        query.setParameter("idOpcaoPagamento", parcela.idOpcaoPagamento);
        query.setParameter("situacaoParcela", SituacaoParcela.BAIXADO_OUTRA_OPCAO.name());
        query.setParameter("situacaoparcelaPago", SituacaoParcela.PAGO.name());
        query.setParameter("baixado", SituacaoLoteBaixa.BAIXADO.name());
        query.setParameter("baixadoInconsistente", SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name());
        List<ParcelaParaPagamento> parcelas = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) query.getResultList()) {
            parcelas.add(new ParcelaParaPagamento((BigDecimal) obj[0], (BigDecimal) obj[1], (String) obj[2], (BigDecimal) obj[3], (BigDecimal) obj[4], (BigDecimal) obj[5], (String) obj[6]));
        }
        return parcelas;
    }

    private SituacaoParcela buscarSituacaoQueAParcelaIraReceber(Long idParcela, DAM dam, Long idSituacao) {
        String sql = "SELECT processo.tipo FROM ProcessoDebito processo " +
            "INNER JOIN ItemProcessoDebito item ON item.processoDebito_id = processo.id " +
            "WHERE item.parcela_id = :idParcela " +
            "  AND processo.situacao = :situacaoProcesso " +
            "ORDER BY processo.lancamento DESC";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", idParcela);
        q.setParameter("situacaoProcesso", SituacaoProcessoDebito.FINALIZADO.name());
        List<String> tipos = q.getResultList();
        if (!tipos.isEmpty()) {
            return TipoProcessoDebito.valueOf(tipos.get(0)).getSituacaoParcela();
        }
        if (DAM.Tipo.SUBVENCAO.equals(dam.getTipo())) {
            SituacaoParcelaValorDivida situacaoAtual = em.find(SituacaoParcelaValorDivida.class, idSituacao);
            if (SituacaoParcela.PAGO_SUBVENCAO.equals(situacaoAtual.getSituacaoParcela())) {
                return SituacaoParcela.AGUARDANDO_PAGAMENTO_SUBVENCAO;
            }
        }
        return SituacaoParcela.EM_ABERTO;
    }

    public AssistenteArrecadacao reprocessaVariosLotes(AssistenteArrecadacao assistente) {
        List<LoteBaixa> lotes = em.createQuery("select lote from LoteBaixa lote where lote.dataPagamento >= :inicio and lote.dataPagamento <= :fim" +
                " order by lote.codigoLote")
            .setParameter("inicio", assistente.getInicio())
            .setParameter("fim", assistente.getFim()).getResultList();
        assistente.setTotal(lotes.size());
        for (LoteBaixa lote : lotes) {
            apagarIntegracaoECria(lote);
            assistente.conta();
        }
        return assistente;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void apagarIntegracaoECria(LoteBaixa loteBaixa) {
        try {
            TipoIntegracao tipoIntegracao = TipoIntegracao.ARRECADACAO;
            if (loteBaixa.isEstornado()) {
                tipoIntegracao = TipoIntegracao.ESTORNO_ARRECADACAO;
            }
            loteBaixa = apagarIntegracao(loteBaixa, tipoIntegracao);
            gerarIntegracao(loteBaixa, tipoIntegracao);
        } catch (Exception e) {
            logger.error("Erro ao apagarIntegracaoECria: {}", e);
            rollBack();
        }
    }

    private LoteBaixa apagarIntegracao(LoteBaixa loteBaixa, TipoIntegracao tipoIntegracao) throws NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        begin();
        loteBaixa.setIntegraContasAcrecimos(true);
        loteBaixa = em.merge(loteBaixa);
        Query q = em.createQuery("select item from ItemVDNaoIntegrado item where item.loteBaixa = :lote " +
                "and item.tipoIntegracao = :tipoIntegracao")
            .setParameter("lote", loteBaixa);
        q.setParameter("tipoIntegracao", tipoIntegracao);

        List<ItemVDNaoIntegrado> naoIntegrados = q.getResultList();
        for (ItemVDNaoIntegrado naoIntegrado : naoIntegrados) {
            em.remove(naoIntegrado);
        }
        q = em.createQuery("select item from LoteBaixaIntegracao item " +
                "where item.loteBaixa = :lote" +
                "  and item.itemItengracao.tipo = :tipoIntegracao")
            .setParameter("lote", loteBaixa);
        q.setParameter("tipoIntegracao", tipoIntegracao);
        List<LoteBaixaIntegracao> integracoes = q.getResultList();
        for (LoteBaixaIntegracao integracao : integracoes) {
            em.remove(integracao);
        }
        commit();
        return loteBaixa;
    }

    public static class AssistenteArrecadacao extends AssistenteBarraProgresso {
        private LoteBaixa loteBaixa;
        private Date inicio, fim;
        private String log;
        private List<ValorDivida> valoresDividaInconsistencia;

        public AssistenteArrecadacao(UsuarioSistema usuarioSistema, LoteBaixa loteBaixa, Integer total) {
            this.loteBaixa = loteBaixa;
            setTotal(total);
            setCalculados(0);
            setDescricaoProcesso("Arrecadação do lote " + loteBaixa.getCodigoLote());
            setUsuarioSistema(usuarioSistema);
            valoresDividaInconsistencia = Lists.newArrayList();
        }

        public AssistenteArrecadacao(UsuarioSistema usuarioSistema, Date inicio, Date fim) {
            this.inicio = inicio;
            this.fim = fim;
            setCalculados(0);
            setDescricaoProcesso("Arrecadação");
            setUsuarioSistema(usuarioSistema);
            valoresDividaInconsistencia = Lists.newArrayList();
        }

        public String getLog() {
            return log;
        }

        public void setLog(String log) {
            this.log = log;
        }

        public LoteBaixa getLoteBaixa() {
            return loteBaixa;
        }

        public Date getInicio() {
            return inicio;
        }

        public Date getFim() {
            return fim;
        }

        public List<ValorDivida> getValoresDividaInconsistencia() {
            return valoresDividaInconsistencia;
        }
    }


    private class ParcelaParaPagamento {
        protected Long idParcela;
        protected Long idSituacao;
        protected Long idValorDivida;
        protected Long idCalculo;
        protected Long idOpcaoPagamento;
        protected String referencia;
        protected String tipoCalculo;

        public ParcelaParaPagamento(BigDecimal idParcela, BigDecimal idSituacao, String referencia, BigDecimal idValorDivida, BigDecimal idOpcaoPagamento, BigDecimal idCalculo, String tipoCalculo) {
            this.idParcela = idParcela.longValue();
            this.idSituacao = idSituacao.longValue();
            this.referencia = referencia;
            this.idValorDivida = idValorDivida.longValue();
            this.idCalculo = idCalculo.longValue();
            this.idOpcaoPagamento = idOpcaoPagamento.longValue();
            this.tipoCalculo = tipoCalculo;
        }

        public Calculo.TipoCalculo getTipoCalculoEnumValue() {
            return Calculo.TipoCalculo.valueOf(tipoCalculo);
        }
    }

    public boolean lotePossuiValorValorOriginalTributoMenorQueDesconto(Long idLoteBaixa) {
        String sql = " select count(*) " +
            "from lotebaixa lb " +
            "         inner join itemlotebaixa ilb on ilb.LOTEBAIXA_ID = lb.id " +
            "         inner join itemdam idam on idam.dam_id = ilb.DAM_ID " +
            "         inner join tributodam tdam on tdam.ITEMDAM_ID = idam.id " +
            "         inner join dam dam on dam.id = idam.DAM_ID " +
            "where tdam.VALORORIGINAL < tdam.DESCONTO " +
            "  and lb.ID = :idLoteBaixa " +
            "order by lb.ID desc ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idLoteBaixa", idLoteBaixa);
        try {
            BigDecimal count = (BigDecimal) q.getResultList().get(0);
            return count.compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception ex) {
            logger.error("Erro ao verificar se possui tributos com valor original menor que o desconto.", ex);
            return false;
        }
    }

}
