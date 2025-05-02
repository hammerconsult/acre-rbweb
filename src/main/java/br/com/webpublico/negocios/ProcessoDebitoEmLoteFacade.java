package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDamDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcParcelaValorDividaDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.ResultadoValidacao;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ProcessoDebitoEmLoteFacade extends AbstractFacade<ProcessoDebitoEmLote> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private ProcessoDebitoFacade processoDebitoFacade;
    private JdbcParcelaValorDividaDAO parcelaValorDividaDAO;
    private JdbcDamDAO jdbcDamDAO;

    public ProcessoDebitoEmLoteFacade() {
        super(ProcessoDebitoEmLote.class);
    }

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        parcelaValorDividaDAO = (JdbcParcelaValorDividaDAO) ap.getBean("jdbcParcelaValorDividaDAO");
        jdbcDamDAO = (JdbcDamDAO) ap.getBean("damDAO");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    @Override
    public void salvar(ProcessoDebitoEmLote entity) throws RuntimeException {
        throw new RuntimeException("Método inválido: Utilize o método salva(ProcessoDebito)");
    }

    @Override
    public void salvarNovo(ProcessoDebitoEmLote entity) throws RuntimeException {
        throw new RuntimeException("Método inválido: Utilize o método salvaNovo(ProcessoDebito)");
    }

    public void deletarItens(List<ParcelaProcessoDebitoEmLote> itens) {
        for (ParcelaProcessoDebitoEmLote item : itens) {
            if (item.getId() != null) {
                em.remove(em.find(ParcelaProcessoDebitoEmLote.class, item.getId()));
            }
        }
    }

    public List<ItemProcessoDebitoEmLote> salvarItensRetornando(List<ItemProcessoDebitoEmLote> itens) {
        List<ItemProcessoDebitoEmLote> retorno = Lists.newArrayList();
        for (ItemProcessoDebitoEmLote item : itens) {
            retorno.add(em.merge(item));
        }
        return retorno;
    }

    public ProcessoDebitoEmLote salvaRetornando(ProcessoDebitoEmLote entity) {
        return em.merge(entity);
    }

    public boolean codigoEmUso(ProcessoDebitoEmLote processo) {
        if (processo == null || processo.getCodigo() == null) {
            return false;
        }
        String hql = "from ProcessoDebitoEmLote pd where pd.codigo = :codigo and pd.exercicio = :exercicio and pd.tipo = '" + processo.getTipo() + "'";
        if (processo.getId() != null) {
            hql += " and pd != :processoDebito";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", processo.getCodigo());
        q.setParameter("exercicio", processo.getExercicio());
        if (processo.getId() != null) {
            q.setParameter("processoDebito", processo);
        }
        return !q.getResultList().isEmpty();
    }

    private Map<String, List<ParcelaProcessoDebitoEmLote>> agruparItensPorCadastro(ProcessoDebitoEmLote processoDebitoEmLote) {
        Map<String, List<ParcelaProcessoDebitoEmLote>> itensAgrupadosPorCadastro = new HashMap<>();

        for (ParcelaProcessoDebitoEmLote parcela : buscarItemProcesso(0, processoDebitoEmLote.getParcelasProcessoEmLote().size(), processoDebitoEmLote, Lists.newArrayList())) {
            if (itensAgrupadosPorCadastro.containsKey(parcela.getResultadoParcela().getCadastro())) {
                if (!itensAgrupadosPorCadastro.get(parcela.getResultadoParcela().getCadastro()).contains(parcela)) {
                    itensAgrupadosPorCadastro.get(parcela.getResultadoParcela().getCadastro()).add(parcela);
                }
            } else {
                itensAgrupadosPorCadastro.put(parcela.getResultadoParcela().getCadastro(), Lists.newArrayList(parcela));
            }
        }
        return itensAgrupadosPorCadastro;
    }

    private List<ItemProcessoDebito> converterItemLoteParaItemIndividual(List<ParcelaProcessoDebitoEmLote> parcelas, ProcessoDebito processoDebito) {
        List<ItemProcessoDebito> itens = Lists.newArrayList();

        for (ParcelaProcessoDebitoEmLote parcela : parcelas) {
            ItemProcessoDebito item = new ItemProcessoDebito();

            item.setParcela(parcela.getParcela());
            item.setResultadoParcela(parcela.getResultadoParcela());
            item.setProcessoDebito(processoDebito);

            itens.add(item);

        }
        return itens;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ResultadoValidacao encerrarProcesso(ProcessoDebitoEmLote processoLote, UsuarioSistema usuario) {
        ResultadoValidacao retorno = validarEncerramento(processoLote);
        if (retorno.isValidado()) {

            List<ItemProcessoDebitoEmLote> itensProcessoDebitoEmLoteParaEfetivar = Lists.newArrayList();

            for (List<ParcelaProcessoDebitoEmLote> parcelas : agruparItensPorCadastro(processoLote).values()) {

                ProcessoDebito processoPorCadastro = new ProcessoDebito();
                ItemProcessoDebitoEmLote itemProcessoDebitoEmLote = new ItemProcessoDebitoEmLote();

                processoPorCadastro.setExercicio(processoLote.getExercicio());
                processoPorCadastro.setTipo(processoLote.getTipo());
                processoPorCadastro.setLancamento(processoLote.getLancamento());
                processoPorCadastro.setNumeroProtocolo(processoLote.getNumeroProtocolo());
                processoPorCadastro.setAtoLegal(processoLote.getAtoLegal());
                processoPorCadastro.setMotivo(processoLote.getMotivo());
                processoPorCadastro.setUsuarioIncluiu(processoLote.getUsuarioIncluiu());
                processoPorCadastro.setSituacao(SituacaoProcessoDebito.FINALIZADO);
                if (processoLote.getDetentorArquivoComposicao() != null) {
                    processoPorCadastro.setDetentorArquivoComposicao(processoLote.getDetentorArquivoComposicao());
                }

                processoPorCadastro = processoDebitoFacade.salvaRetornando(processoPorCadastro);
                processoPorCadastro.setItens(converterItemLoteParaItemIndividual(parcelas, processoPorCadastro));
                processoDebitoFacade.salvarItens(processoPorCadastro.getItens());

                itemProcessoDebitoEmLote.setProcessoDebito(processoPorCadastro);
                itemProcessoDebitoEmLote.setProcessoDebitoEmLote(processoLote);

                itensProcessoDebitoEmLoteParaEfetivar.add(itemProcessoDebitoEmLote);
            }
            processoLote.setItens(salvarItensRetornando(itensProcessoDebitoEmLoteParaEfetivar));
            for (ItemProcessoDebitoEmLote itemProcessoEmLote : processoLote.getItens()) {
                itemProcessoEmLote.getProcessoDebito().setCodigo(processoDebitoFacade.recuperarProximoCodigoPorExercicioTipo(itemProcessoEmLote.getProcessoDebito().getExercicio(), itemProcessoEmLote.getProcessoDebito().getTipo()));
                for (ItemProcessoDebito item : processoDebitoFacade.buscarItemProcesso(0, itemProcessoEmLote.getProcessoDebito().getItens().size(), itemProcessoEmLote.getProcessoDebito(), Lists.newArrayList())) {
                    item.getResultadoParcela().getCadastro();
                    item.setProcessoDebito(itemProcessoEmLote.getProcessoDebito());
                    executarAntesDeProcessar(itemProcessoEmLote.getProcessoDebito(), item.getParcela(), usuario);
                    processarSituacaoDeAcordoComProcesso(itemProcessoEmLote.getProcessoDebito(), item.getParcela());
                }
                retorno.limpaMensagens();
                processoDebitoFacade.salvaRetornando(itemProcessoEmLote.getProcessoDebito());
            }
            em.createNativeQuery("update ProcessoDebitoEmLote set situacao = '" + SituacaoProcessoDebito.FINALIZADO.name() + "' where id = " + processoLote.getId()).executeUpdate();
        }
        return retorno;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @Asynchronous
    public void executarDependenciasDoProcesso(ProcessoDebitoEmLote processoLote) {
        for (ItemProcessoDebitoEmLote itemProcessoEmLote : processoLote.getItens()) {
            int inicial = 0;
            List<ItemProcessoDebito> itens = processoDebitoFacade.buscarItemProcesso(inicial, 100, itemProcessoEmLote.getProcessoDebito(), Lists.<ItemProcessoDebito>newArrayList());
            while (!itens.isEmpty()) {
                for (ItemProcessoDebito item : itens) {
                    ParcelaValorDivida parcela = em.find(ParcelaValorDivida.class, item.getParcela().getId());
                    executarDepoisDeProcessar(itemProcessoEmLote.getProcessoDebito(), parcela);
                }
                inicial += 100;
                itens = processoDebitoFacade.buscarItemProcesso(inicial, 100, itemProcessoEmLote.getProcessoDebito(), Lists.<ItemProcessoDebito>newArrayList());
            }
        }
    }

    private void executarAntesDeProcessar(ProcessoDebito processo, ParcelaValorDivida parcela, UsuarioSistema usuario) {
        if (TipoProcessoDebito.BAIXA.equals(processo.getTipo())) {
            DAM damBaixa = consultaDebitoFacade.getDamFacade().verificarSeExisteDamVencidoAndRetornarDamDaParcela(parcela, processo.getDataPagamento());
            if (damBaixa == null) {
                try {
                    damBaixa = consultaDebitoFacade.getDamFacade().geraDAM(parcela, processo.getDataPagamento());
                } catch (Exception e) {
                    logger.error("Erro ao gerar o dam no processo de débito: {}", e);
                }
            }
            if (!DAM.Situacao.ABERTO.equals(damBaixa.getSituacao())) {
                jdbcDamDAO.atualizar(damBaixa.getId(), DAM.Situacao.ABERTO, usuario);
            }
        }
    }

    private void executarDepoisDeProcessar(ProcessoDebito processo, ParcelaValorDivida parcela) {
        realizarOperacoesPorTipoDoCalculo(processo, parcela.getValorDivida().getCalculo());
    }

    private void realizarOperacoesPorTipoDoCalculo(ProcessoDebito processo, Calculo calculo) {
        Calculo.TipoCalculo tipo = calculo.getTipoCalculo();
        switch (tipo) {
            case RB_TRANS:
                calculo = initializeAndUnproxy(calculo);
                if (((CalculoRBTrans) calculo).getTipoCalculoRBTRans().equals(TipoCalculoRBTRans.RENOVACAO_PERMISSAO)) {
                    PermissaoTransporte permissao = permissaoTransporteFacade.recuperaPermissaoPorCMC((CadastroEconomico) calculo.getCadastro());
                    if (permissao != null) {
                        permissao.setHabilitarVerificacaoDocumentos(true);
                        permissao.setDocumentosApresentados(false);
                        em.merge(permissao);
                    }
                    break;
                }
            case INSCRICAO_DA:
                calculo = initializeAndUnproxy(calculo);
                certidaoDividaAtivaFacade.alterarSituacaoDaCdaPorTipoProcessoDebito(processo, calculo);
                break;
        }
    }

    public void processarSituacaoDeAcordoComProcesso(ProcessoDebito processo, ParcelaValorDivida parcela) {
        baixarOutraOpcaoParcelasDaCotaUnicaOrCotaUnidaDasParcelas(parcela, processo, SituacaoParcela.BAIXADO_OUTRA_OPCAO, SituacaoParcela.EM_ABERTO);
        salvarSituacaoParcela(parcela, processo.getTipo().getSituacaoParcela());
    }

    private void baixarOutraOpcaoParcelasDaCotaUnicaOrCotaUnidaDasParcelas(ParcelaValorDivida parcela,
                                                                           ProcessoDebito processo,
                                                                           SituacaoParcela novaSituacaoParcela,
                                                                           SituacaoParcela situacoesParaPesquisa) {
        List<ParcelaValorDivida> parcelasOutraOpcao = buscarParcelasDaCotaUnicaOrACotaUnicaDasParcelas(parcela, situacoesParaPesquisa);
        if (!parcelasOutraOpcao.isEmpty()) {
            for (ParcelaValorDivida parcelaDaOutraOpcao : parcelasOutraOpcao) {
                if (!hasParcelaNoProcesso(processo, parcelaDaOutraOpcao)) {
                    em.persist(new SituacaoParcelaValorDivida(novaSituacaoParcela, parcelaDaOutraOpcao, parcela.getSituacaoAtual().getSaldo()));
                }
            }
        }
    }

    private boolean hasParcelaNoProcesso(ProcessoDebito processo, ParcelaValorDivida parcela) {
        List resultList = em.createNativeQuery("select i.id from itemprocessodebito i where " +
                " i.processodebito_id = :idProcesso and i.parcela_id = :idParcela")
            .setParameter("idParcela", parcela.getId())
            .setParameter("idProcesso", processo.getId())
            .getResultList();
        return !resultList.isEmpty();
    }

    public List<ParcelaValorDivida> buscarParcelasDaCotaUnicaOrACotaUnicaDasParcelas(ParcelaValorDivida parcela, SituacaoParcela situacaoParcela) {
        String sql = "select pvd.* from parcelavalordivida pvd "
            + " inner join situacaoparcelavalordivida situacao on situacao.id = pvd.situacaoatual_id "
            + " where pvd.valordivida_id = :valorDivida and pvd.opcaopagamento_id <> :opcaoPagamento "
            + " and situacao.situacaoparcela = :situacao";
        Query q = em.createNativeQuery(sql, ParcelaValorDivida.class);
        q.setParameter("valorDivida", parcela.getValorDivida().getId());
        q.setParameter("opcaoPagamento", parcela.getOpcaoPagamento().getId());
        q.setParameter("situacao", situacaoParcela.name());
        return q.getResultList();
    }

    public void salvarSituacaoParcela(ParcelaValorDivida parcela, SituacaoParcela situacao) {
        salvarSituacaoParcela(parcela, situacao, false);
    }

    public void salvarSituacaoParcela(ParcelaValorDivida parcela, SituacaoParcela situacao, boolean recuperar) {
        if (recuperar) {
            parcela = em.find(ParcelaValorDivida.class, parcela.getId());
        }
        SituacaoParcelaValorDivida toAdd = new SituacaoParcelaValorDivida();
        toAdd.setGeraReferencia(false);
        toAdd.setReferencia(parcela.getUltimaSituacao().getReferencia());
        toAdd.setSaldo(parcela.getUltimaSituacao().getSaldo());
        toAdd.setDataLancamento(new Date());
        toAdd.setParcela(parcela);
        toAdd.setSituacaoParcela(situacao);
        em.persist(toAdd);
    }

    private ResultadoValidacao validarEncerramento(ProcessoDebitoEmLote processo) {
        ResultadoValidacao retorno = new ResultadoValidacao();
        if (processo.getSituacao() != SituacaoProcessoDebito.EM_ABERTO) {
            retorno.addErro(null, "Não foi possível continuar!", "O processo selecionado encontra-se " + processo.getSituacao().getDescricao() + ". Somente processos EM ABERTO podem ser finalizados.");
        }
        return retorno;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<ResultadoValidacao> estornar(AssistenteBarraProgresso assistenteBarraProgresso,
                                               ProcessoDebitoEmLote processoEmLote,
                                               SituacaoProcessoDebito situacao) {
        assistenteBarraProgresso.setDescricaoProcesso("Validando estorno de processo de débito...");
        assistenteBarraProgresso.setTotal(0);
        ResultadoValidacao retorno = validarEstorno(processoEmLote);
        if (retorno.isValidado()) {
            assistenteBarraProgresso.setDescricaoProcesso("Estornando itens do processo de " + processoEmLote.getTipo().getDescricao());
            for (ItemProcessoDebitoEmLote itemProcessoEmLote : processoEmLote.getItens()) {
                assistenteBarraProgresso.setTotal(contarItens(processoEmLote, Lists.newArrayList()));
                int inicial = 0;
                List<ItemProcessoDebito> itens = processoDebitoFacade.buscarItemProcesso(inicial, 500, itemProcessoEmLote.getProcessoDebito(), Lists.<ItemProcessoDebito>newArrayList());
                while (!itens.isEmpty()) {
                    for (ItemProcessoDebito item : itens) {
                        estornarItemProcesso(assistenteBarraProgresso, itemProcessoEmLote.getProcessoDebito(), item);
                    }
                    inicial += 500;
                    itens = processoDebitoFacade.buscarItemProcesso(inicial, 500, itemProcessoEmLote.getProcessoDebito(), Lists.<ItemProcessoDebito>newArrayList());
                }
                assistenteBarraProgresso.setDescricaoProcesso("Atualizando situação do processo de " + itemProcessoEmLote.getProcessoDebito().getTipo().getDescricao());
                itemProcessoEmLote.getProcessoDebito().setMotivoEstorno(processoEmLote.getMotivoEstorno());
                itemProcessoEmLote.getProcessoDebito().setDataEstorno(processoEmLote.getDataEstorno());
                itemProcessoEmLote.getProcessoDebito().setSituacao(situacao);
                em.merge(itemProcessoEmLote.getProcessoDebito());
            }
            assistenteBarraProgresso.setDescricaoProcesso("Atualizando situação do processo de " + processoEmLote.getTipo().getDescricao());
            assistenteBarraProgresso.setTotal(0);
            processoEmLote.setSituacao(situacao);
            em.merge(processoEmLote);
            retorno.limpaMensagens();
            retorno.addInfo(null, "Processo de Débitos estornado com sucesso!", "");
        }
        return new AsyncResult<>(retorno);
    }

    private void estornarItemProcesso(AssistenteBarraProgresso assistenteBarraProgresso,
                                      ProcessoDebito processo, ItemProcessoDebito item) {
        if (!isPago(item.getResultadoParcela()) &&
            item.getResultadoParcela().getSituacaoEnumValue().name().equals(processo.getTipo().getSituacaoParcela().name())) {
            if (!parcelaValorDividaDAO.hasOutraOpcaoPagamento(item.getParcela())) {
                parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(item.getParcela().getId(),
                    item.getParcela().getSituacaoAtual().getReferencia(), item.getParcela().getSituacaoAtual().getSaldo(),
                    SituacaoParcela.EM_ABERTO);
            } else {
                if (parcelaValorDividaDAO.hasOutraOpcaoPagamentoPago(item.getParcela())) {
                    parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(item.getParcela().getId(),
                        item.getParcela().getSituacaoAtual().getReferencia(), item.getParcela().getSituacaoAtual().getSaldo(),
                        SituacaoParcela.BAIXADO_OUTRA_OPCAO);
                } else {
                    parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(item.getParcela().getId(),
                        item.getParcela().getSituacaoAtual().getReferencia(), item.getParcela().getSituacaoAtual().getSaldo(),
                        SituacaoParcela.EM_ABERTO);
                    parcelaValorDividaDAO.abrirOutraOpcaoPagamentoBaixado(item.getParcela());
                }
            }
        }
        assistenteBarraProgresso.conta();
    }

    public boolean isPago(ResultadoParcela resultadoParcela) {
        return SituacaoParcela.PAGO.equals(resultadoParcela.getSituacaoEnumValue()) ||
            SituacaoParcela.PAGO_PARCELAMENTO.equals(resultadoParcela.getSituacaoEnumValue()) ||
            SituacaoParcela.PAGO_REFIS.equals(resultadoParcela.getSituacaoEnumValue()) ||
            SituacaoParcela.PAGO_SUBVENCAO.equals(resultadoParcela.getSituacaoEnumValue()) ||
            SituacaoParcela.BAIXADO_OUTRA_OPCAO.equals(resultadoParcela.getSituacaoEnumValue()) ||
            SituacaoParcela.COMPENSACAO.equals(resultadoParcela.getSituacaoEnumValue()) ||
            SituacaoParcela.PAGO_BLOQUEIO_JUDICIAL.equals(resultadoParcela.getSituacaoEnumValue());
    }

    private ResultadoValidacao validarEstorno(ProcessoDebitoEmLote processo) {
        ResultadoValidacao retorno = new ResultadoValidacao();
        if (!SituacaoProcessoDebito.FINALIZADO.equals(processo.getSituacao())) {
            retorno.addErro(null, "Não foi possível continuar!", "O processo selecionado encontra-se " + processo.getSituacao().getDescricao() + ". Somente processos FINALIZADOS podem ser estornados.");
        }
        return retorno;
    }

    public ResultadoValidacao adicionarItem(ProcessoDebito processo, ParcelaValorDivida parcela) {
        ResultadoValidacao retorno = validarItem(parcela);
        if (retorno.isValidado()) {
            ItemProcessoDebito item = new ItemProcessoDebito();
            item.setParcela(parcela);
            item.setProcessoDebito(processo);
            item.setSituacaoAnterior(parcela.getUltimaSituacao().getSituacaoParcela());
            item.setSituacaoProxima(processo.getTipo().getSituacaoParcela());
            if (processo.getItens() == null) {
                processo.setItens(new ArrayList<ItemProcessoDebito>());
            }
            processo.getItens().add(item);
        }
        return retorno;
    }

    private ResultadoValidacao validarItem(ParcelaValorDivida parcela) {
        ResultadoValidacao retorno = new ResultadoValidacao();
        if (parcela == null || parcela.getId() == null) {
            retorno.addErro(null, "Não foi possível continuar!", "Parcela não pode ser nula.");
        }
        return retorno;
    }

    @Override
    public List<ProcessoDebitoEmLote> lista() {
        return em.createQuery("from ProcessoDebitoEmLote p order by p.codigo").getResultList();
    }

    @Override
    public ProcessoDebitoEmLote recuperar(Object id) {
        ProcessoDebitoEmLote p = em.find(ProcessoDebitoEmLote.class, id);
        Hibernate.initialize(p.getParcelasProcessoEmLote());
        Hibernate.initialize(p.getItens());
        if (p.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(p.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return p;
    }

    public ProcessoDebito recuperarItensDoProcesso(Long idProcesso) {
        ProcessoDebito processoDebito = em.find(ProcessoDebito.class, idProcesso);
        Hibernate.initialize(processoDebito.getItens());
        return processoDebito;
    }

    public Long recuperarProximoCodigoPorExercicioTipo(Exercicio exercicio, TipoProcessoDebito tipoProcessoDebito) {
        String sql = " select max(coalesce(obj.codigo,0)) from ProcessoDebitoEmLote obj "
            + " where obj.exercicio = :exercicio"
            + " and obj.tipo = '" + tipoProcessoDebito.name() + "'";
        Query query = em.createQuery(sql);
        query.setParameter("exercicio", exercicio);
        query.setMaxResults(1);
        try {
            Long resultado = (Long) query.getSingleResult();
            if (resultado == null) {
                resultado = 0l;
            }

            return resultado + 1;
        } catch (Exception e) {
            return 1l;
        }
    }

    public ResultadoValidacao adicionarItem(ParcelaValorDivida parcela, ProcessoDebitoEmLote processoDebitoEmlote, List<ParcelaProcessoDebitoEmLote> itens, List<ParcelaValorDivida> listaParcela) {
        ResultadoValidacao rv = new ResultadoValidacao();

        if (validarItemProcesso(parcela, processoDebitoEmlote)) {
            try {
                ParcelaProcessoDebitoEmLote itemParcela = new ParcelaProcessoDebitoEmLote();
                itemParcela.setProcessoDebitoEmLote(processoDebitoEmlote);
                itemParcela.setParcela(parcela);
                itemParcela.setSituacaoProxima(processoDebitoEmlote.getSituacaoParcela());
                itemParcela.setSituacaoAnterior(consultaDebitoFacade.getUltimaSituacao(parcela).getSituacaoParcela());
                itemParcela.setSituacaoProxima(processoDebitoEmlote.getTipo().getSituacaoParcela());
                itens.add(itemParcela);
                listaParcela.remove(parcela);

                rv.addInfo(null, "Sucesso!", "Parcela " + parcela.getSequenciaParcela() + " adicionada ao processo.");
            } catch (Exception e) {
                logger.error(e.getMessage());
                rv.addErro(null, "Não foi possível continuar!", "Ocorreu um erro ao adicionar a parcela: " + e.getMessage());
            }
        } else {
            rv.addErro(null, "Não foi possível continuar!", "A parcela selecionada já foi adicionada ao processo!");
        }

        return rv;
    }

    public void adicionarItem(ResultadoParcela resultadoParcelas, ProcessoDebitoEmLote processoDebitoLote, List<ParcelaProcessoDebitoEmLote> itens, List<ResultadoParcela> resultadoConsulta) {
        ValidacaoException ve = new ValidacaoException();
        if (permitirAdicionarItem(itens, resultadoParcelas)) {
            ParcelaProcessoDebitoEmLote parcela = new ParcelaProcessoDebitoEmLote();
            parcela.setProcessoDebitoEmLote(processoDebitoLote);
            parcela.setReferencia(resultadoParcelas.getReferencia());
            parcela.setParcela(recuperarParcelaValorDivida(resultadoParcelas.getIdParcela()));
            parcela.setSituacaoProxima(processoDebitoLote.getSituacaoParcela());
            parcela.setSituacaoAnterior(SituacaoParcela.fromDto(resultadoParcelas.getSituacaoEnumValue()));
            parcela.setSituacaoProxima(processoDebitoLote.getTipo().getSituacaoParcela());
            parcela.setResultadoParcela(resultadoParcelas);
            itens.add(parcela);
            resultadoConsulta.remove(resultadoParcelas);
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A parcela selecionada: " + resultadoParcelas.getReferencia() + " - " + resultadoParcelas.getDivida() + " - " + resultadoParcelas.getParcela() + " já foi adicionada ao processo!");
            ve.lancarException();
        }
    }

    private boolean permitirAdicionarItem(List<ParcelaProcessoDebitoEmLote> itens, ResultadoParcela resultadoParcelas) {
        boolean valida = true;
        for (ParcelaProcessoDebitoEmLote item : itens) {
            if (item.getParcela().getId().equals(resultadoParcelas.getIdParcela())) {
                valida = false;
                break;
            }
        }
        return valida;
    }

    private ParcelaValorDivida recuperarParcelaValorDivida(Long id) {
        Query q = em.createQuery(" select pvd from ParcelaValorDivida pvd where id = :id");
        q.setParameter("id", id);
        return (ParcelaValorDivida) q.getResultList().get(0);
    }

    public String buscarNumeroDam(ParcelaValorDivida pvd) {
        StringBuilder sql = new StringBuilder(" select dam.numerodam from itemdam item");
        sql.append(" inner join dam on item.dam_id = dam.id");
        sql.append("  inner join parcelavalordivida pvd on item.parcela_id = pvd.id");
        sql.append("  where pvd.id = :parcela and dam.situacao = 'ABERTO'");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("parcela", pvd.getId());
        if (!q.getResultList().isEmpty()) {
            return (String) q.getSingleResult();
        }
        return null;
    }


    public boolean validarItemProcesso(ParcelaValorDivida parcela, ProcessoDebitoEmLote processoDebitoEmlote) {
        boolean retorno = true;
        for (ParcelaProcessoDebitoEmLote itemPparcela : processoDebitoEmlote.getParcelasProcessoEmLote()) {
            if (itemPparcela.getParcela().equals(parcela)) {
                retorno = false;
                break;
            }
        }
        return retorno;
    }

    public List<String> recuperarCadastrosDoProcesso(ProcessoDebito processo) {
        List<String> toReturn = Lists.newArrayList();

        StringBuilder sb = new StringBuilder();
        sb.append(" select cad from ProcessoDebito obj ");
        sb.append(" inner join obj.itens i ");
        sb.append(" inner join i.parcela p ");
        sb.append(" inner join p.valorDivida vd ");
        sb.append(" inner join vd.calculo c ");
        sb.append(" inner join c.cadastro cad ");
        sb.append(" where obj.id = :idProcesso");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idProcesso", processo.getId());
        List<Cadastro> cadastros = new ArrayList(new HashSet(q.getResultList()));
        if (cadastros != null) {
            for (Cadastro cadastro : cadastros) {
                toReturn.add(cadastro.getNumeroCadastro());
            }
        }

        sb = new StringBuilder();
        sb.append(" select pes.pessoa from ProcessoDebito obj ");
        sb.append(" inner join obj.itens i ");
        sb.append(" inner join i.parcela p ");
        sb.append(" inner join p.valorDivida vd ");
        sb.append(" inner join vd.calculo c ");
        sb.append(" inner join c.pessoas pes ");
        sb.append(" where obj.id = :idProcesso and c.cadastro is null ");

        q = em.createQuery(sb.toString());
        q.setParameter("idProcesso", processo.getId());
        List<Pessoa> pessoas = new ArrayList(new HashSet(q.getResultList()));
        if (pessoas != null) {
            for (Pessoa pessoa : pessoas) {
                toReturn.add(pessoa.getCpf_Cnpj());
            }
        }
        return toReturn;
    }

    public ProcessoDebito buscarUltimoProcessoDebitoPorParcelValorDivida(Long pvd) {
        String sql = "select p.*" +
            "           from itemprocessodebito item " +
            "          inner join processodebito p on p.id = item.processodebito_id" +
            "         where item.parcela_id = :pvd" +
            "           and p.situacao = :finalizado " +
            "           order by p.id desc";
        Query q = em.createNativeQuery(sql, ProcessoDebito.class);
        try {
            q.setMaxResults(1);
            q.setParameter("pvd", pvd);
            q.setParameter("finalizado", SituacaoProcessoDebito.FINALIZADO.name());
            return (ProcessoDebito) q.getSingleResult();
        } catch (Exception no) {
            return null;
        }
    }

    public int contarItens(ProcessoDebitoEmLote entity, List<ParcelaProcessoDebitoEmLote> itensNotIn) {
        if (entity.getId() == null) {
            return 0;
        }
        Query q = getQuery("select count(ppdl.id) ", entity, itensNotIn);
        return ((Number) q.getSingleResult()).intValue();
    }

    private Query getQuery(String select, ProcessoDebitoEmLote entity, List<ParcelaProcessoDebitoEmLote> itensNotIn) {
        List<Long> ids = Lists.newArrayList();
        for (ParcelaProcessoDebitoEmLote parcela : itensNotIn) {
            ids.add(parcela.getId());
        }
        String sql = select + " from ParcelaProcessoDebitoEmLote ppdl where ppdl.processoDebitoEmLote = :processoDebitoEmLote ";
        if (!itensNotIn.isEmpty()) {
            sql += " and ppdl.id not in (:itens)";
        }
        Query q = em.createQuery(sql);
        q.setParameter("processoDebitoEmLote", entity);
        if (!itensNotIn.isEmpty()) {
            q.setParameter("itens", ids);
        }
        return q;
    }

    public List<ParcelaProcessoDebitoEmLote> buscarItemProcesso(int primeiroRegistro, int qtdeRegistro,
                                                                ProcessoDebitoEmLote entity, List<ParcelaProcessoDebitoEmLote> itensNotIn) {
        if (entity.getId() == null) {
            return Lists.newArrayList();
        }
        Query q = getQuery("select ppdl ", entity, itensNotIn);
        q.setFirstResult(primeiroRegistro);
        q.setMaxResults(qtdeRegistro);
        List<ParcelaProcessoDebitoEmLote> list = q.getResultList();
        for (ParcelaProcessoDebitoEmLote parcela : list) {
            if (parcela.getResultadoParcela() == null) {
                ConsultaParcela consulta = new ConsultaParcela();
                List<ResultadoParcela> resultados = consulta
                    .addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, parcela.getParcela().getId())
                    .executaConsulta()
                    .getResultados();
                parcela.setResultadoParcela(resultados.get(0));
            }
        }
        return list;
    }

    public ProcessoDebito buscarProcessoPorCadastro(Long idCadastro) {
        String sql = " select proc.* " +
            " from processodebito proc" +
            "  inner join itemprocessodebito item on proc.id = item.processodebito_id " +
            "  inner join parcelavalordivida pvd on item.parcela_id = pvd.id " +
            "  inner join valordivida vld on pvd.valordivida_id = vld.id " +
            "  inner join calculo calc on vld.calculo_id = calc.id " +
            "  inner join cadastro cad on calc.cadastro_id = cad.id " +
            "   where cad.id = :idCadastro " +
            "    and proc.situacao = :situacao ";
        Query q = em.createNativeQuery(sql, ProcessoDebito.class);
        q.setParameter("idCadastro", idCadastro);
        q.setParameter("situacao", SituacaoProcessoDebito.EM_ABERTO.name());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (ProcessoDebito) resultList.get(0);
        }
        return null;
    }

    public List<Long> buscarIdDividaPorTipoCadastro(TipoCadastroTributario tipoCadastroTributario) {
        List<Long> ids = Lists.newArrayList();
        List<Divida> dividas = dividaFacade.listaDividasPorTipoCadastro(tipoCadastroTributario);
        for (Divida divida : dividas) {
            ids.add(divida.getId());
        }
        return ids;
    }

    public List<Long> buscarIdDividaFiscalizacao() {
        List<Long> ids = Lists.newArrayList();
        List<Divida> dividas = dividaFacade.buscarDividasDeFiscalizacaoISSQN(Strings.EMPTY);
        for (Divida divida : dividas) {
            ids.add(divida.getId());
            if (divida.getDivida() != null) {
                ids.add(divida.getDivida().getId());
            }
        }
        return ids;
    }
}
