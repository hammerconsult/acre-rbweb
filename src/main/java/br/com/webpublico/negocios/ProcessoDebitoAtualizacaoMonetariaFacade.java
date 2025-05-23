package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoParcelamento;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoProcessoDebito;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.nfse.domain.LoteDeclaracaoMensalServico;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorAcrescimos;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.ResultadoValidacao;
import br.com.webpublico.util.Util;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by William on 30/03/2017.
 */
@Stateless
public class ProcessoDebitoAtualizacaoMonetariaFacade extends AbstractFacade<ProcessoDebito> {

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
    private NfseFacade nfseFacade;
    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;
    @EJB
    private ProcessoDebitoFacade processoDebitoFacade;

    public ProcessoDebitoAtualizacaoMonetariaFacade() {
        super(ProcessoDebito.class);
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
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

    public ProcessoDebitoFacade getProcessoDebitoFacade() {
        return processoDebitoFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }


    @Override
    public void salvar(ProcessoDebito entity) throws ValidacaoException {
        em.merge(entity);
    }

    @Override
    public void salvarNovo(ProcessoDebito entity) throws ValidacaoException {
        em.persist(entity);
    }


    private ParcelaValorDivida recuperarParcelaValorDividaAndSituacoes(ParcelaValorDivida parcelaValorDivida) {
        ParcelaValorDivida parcela = em.find(ParcelaValorDivida.class, parcelaValorDivida.getId());
        parcela.getSituacoes().size();
        return parcela;
    }

    public ResultadoValidacao encerrarProcesso(ProcessoDebito processo) {
        ResultadoValidacao retorno = validarEncerramento(processo);
        if (retorno.isValidado()) {
            processo.setSituacao(SituacaoProcessoDebito.FINALIZADO);
            for (ItemProcessoDebito item : processo.getItens()) {
                BigDecimal valorTotal = BigDecimal.ZERO;
                ParcelaValorDivida parcela = recuperarParcelaValorDividaAndSituacoes(item.getParcela());
                for (ItemProcessoDebitoParcela itemProcessoDebitoParcela : item.getItemProcessoDebitoParcela()) {
                    for (ItemParcelaValorDivida itemParcelaValorDivida : parcela.getItensParcelaValorDivida()) {
                        if (itemParcelaValorDivida.getTributo().getId().equals(itemProcessoDebitoParcela.getTributo().getId())) {
                            itemParcelaValorDivida.setValor(itemProcessoDebitoParcela.getValor());
                        }
                    }
                    valorTotal = valorTotal.add(itemProcessoDebitoParcela.getValor());
                }
                parcela.setValor(valorTotal);
                SituacaoParcela situacaoAtual = criarNovaSituacaoParcelaValorDivida(parcela);
                atualizarItemParcelamento(item);
                item.setSituacaoProxima(situacaoAtual);
                em.merge(item);
                em.merge(parcela);
            }
            retorno.limpaMensagens();
            em.merge(processo);
        }
        return retorno;
    }

    private SituacaoParcela criarNovaSituacaoParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        SituacaoParcelaValorDivida situacaoAntiga = buscarUltimaSituacaoParcelaDiferenteDeIsolamento(parcelaValorDivida);
        SituacaoParcelaValorDivida novaSituacao = new SituacaoParcelaValorDivida();
        novaSituacao.setParcela(parcelaValorDivida);
        novaSituacao.setDataLancamento(new Date());
        novaSituacao.setReferencia(situacaoAntiga.getReferencia());
        novaSituacao.setSaldo(parcelaValorDivida.getValor());
        novaSituacao.setSituacaoParcela(situacaoAntiga.getSituacaoParcela());
        novaSituacao.setGeraReferencia(false);
        parcelaValorDivida.setSituacaoAtual(novaSituacao);
        em.persist(novaSituacao);

        return situacaoAntiga.getSituacaoParcela();
    }

    private void atualizarItemParcelamento(ItemProcessoDebito itemProcessoDebito) {
        for (ItemProcessoDebitoParcelamento itemProcessoDebitoParcelamento : itemProcessoDebito.getItemProcessoDebitoParcelamento()) {
            ItemProcessoParcelamento itemProcessoParcelamento = em.find(ItemProcessoParcelamento.class, itemProcessoDebitoParcelamento.getItemProcessoParcelamento().getId());
            itemProcessoParcelamento.setTaxa(itemProcessoDebitoParcelamento.getTaxa());
            itemProcessoParcelamento.setImposto(itemProcessoDebitoParcelamento.getImposto());
            itemProcessoParcelamento.setJuros(itemProcessoDebitoParcelamento.getJuros());
            itemProcessoParcelamento.setMulta(itemProcessoDebitoParcelamento.getMulta());
            itemProcessoParcelamento.setCorrecao(itemProcessoDebitoParcelamento.getCorrecao());
            itemProcessoParcelamento.setHonorarios(itemProcessoDebitoParcelamento.getHonorarios());
            em.merge(itemProcessoParcelamento);
        }
    }

    private SituacaoParcelaValorDivida buscarUltimaSituacaoParcelaDiferenteDeIsolamento(ParcelaValorDivida pvd) {
        String sql = "  select situacao.* from SITUACAOPARCELAVALORDIVIDA SITUACAO where PARCELA_ID= :idParcela and situacaoparcela <> :situacaoParcela" +
            "       order by datalancamento desc, id desc";
        Query q = em.createNativeQuery(sql, SituacaoParcelaValorDivida.class);
        q.setParameter("idParcela", pvd.getId());
        q.setParameter("situacaoParcela", SituacaoParcela.ISOLAMENTO.name());
        return (SituacaoParcelaValorDivida) q.getResultList().get(0);
    }

    private void salvarSituacaoParcela(ParcelaValorDivida parcela, SituacaoParcela situacao, BigDecimal saldo) {
        SituacaoParcelaValorDivida novaSituacao = new SituacaoParcelaValorDivida();
        novaSituacao.setSaldo(saldo);
        novaSituacao.setDataLancamento(new Date());
        novaSituacao.setParcela(parcela);
        novaSituacao.setSituacaoParcela(situacao);
        parcela.setSituacaoAtual(novaSituacao);
        em.persist(novaSituacao);
    }

    private ResultadoValidacao validarEncerramento(ProcessoDebito processo) {
        ResultadoValidacao retorno = new ResultadoValidacao();
        if (processo.getSituacao() != SituacaoProcessoDebito.EM_ABERTO) {
            retorno.addErro(null, "Não foi possível continuar!", "O processo selecionado encontra-se " + processo.getSituacao().getDescricao() + ". Somente processos EM ABERTO podem ser finalizados.");
        }
        return retorno;
    }

    public void estornarProcesso(ProcessoDebito processo, SituacaoProcessoDebito situacao) {
        try {
            validarEstorno(processo);
            processo.setSituacao(situacao);

            for (ItemProcessoDebito item : processo.getItens()) {
                BigDecimal valorTotal = BigDecimal.ZERO;
                ParcelaValorDivida parcela = recuperarParcelaValorDividaAndSituacoes(item.getParcela());
                for (ItemProcessoDebitoParcela itemProcessoDebitoParcela : item.getItemProcessoDebitoParcela()) {
                    for (ItemParcelaValorDivida itemParcelaValorDivida : parcela.getItensParcelaValorDivida()) {
                        if (itemParcelaValorDivida.getTributo().getId().equals(itemProcessoDebitoParcela.getTributo().getId())) {
                            itemParcelaValorDivida.setValor(itemProcessoDebitoParcela.getValorOriginal());
                        }
                    }
                    valorTotal = valorTotal.add(itemProcessoDebitoParcela.getValorOriginal());
                }
                parcela.setValor(valorTotal);
                salvarSituacaoParcela(parcela, SituacaoParcela.ISOLAMENTO, valorTotal);
                atualizarItemParcelamento(item);
                em.merge(parcela);
            }
            em.merge(processo);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    private void validarEstorno(ProcessoDebito processo) {
        ValidacaoException ve = new ValidacaoException();
        if (processo.getSituacao() != SituacaoProcessoDebito.FINALIZADO) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo selecionado encontra-se " + processo.getSituacao().getDescricao() + ". Somente processos FINALIZADOS podem ser estornados.");
        }
        ve.lancarException();
    }


    @Override
    public List<ProcessoDebito> lista() {
        return em.createQuery("from ProcessoDebito p order by p.codigo").getResultList();
    }

    public List<ProcessoDebito> recuperaProcessoPorTipo(TipoProcessoDebito tipoProcessoDebito) {
        return em.createQuery("from ProcessoDebito p where p.tipo = '" + tipoProcessoDebito.name() + "' order by p.codigo").getResultList();
    }

    @Override
    public ProcessoDebito recuperar(Object id) {
        ProcessoDebito processoDebito = super.recuperar(id);
        for (ItemProcessoDebito item : processoDebito.getItens()) {
            Hibernate.initialize(item.getItemProcessoDebitoParcela());
            Hibernate.initialize(item.getItemProcessoDebitoParcelamento());
        }
        return processoDebito;
    }

    public void adicionarItem(ResultadoParcela resultadoParcelas, ProcessoDebito selecionado, List<ResultadoParcela> resultadoConsulta) {
        if (validarItemProcesso(selecionado, resultadoParcelas)) {
            ItemProcessoDebito itemProcessoDebito = new ItemProcessoDebito();
            itemProcessoDebito.setProcessoDebito(selecionado);
            itemProcessoDebito.setReferencia(resultadoParcelas.getReferencia());
            itemProcessoDebito.setParcela(recuperarParcelaValorDivida(resultadoParcelas.getIdParcela()));
            itemProcessoDebito.setSituacaoAnterior(SituacaoParcela.fromDto(resultadoParcelas.getSituacaoEnumValue()));
            itemProcessoDebito.setSituacaoProxima(SituacaoParcela.ISOLAMENTO);
            criarItemProcessoDebitoParcela(itemProcessoDebito);
            criarItemProcessoDebitoParcelamento(itemProcessoDebito);
            selecionado.getItens().add(itemProcessoDebito);
            resultadoConsulta.remove(resultadoParcelas);
        } else {
            FacesUtil.addError("Não foi possível continuar!", "A parcela selecionada já foi adicionada ao processo!");
        }
    }

    private void criarItemProcessoDebitoParcela(ItemProcessoDebito itemProcessoDebito) {
        for (ItemParcelaValorDivida itemParcelaValorDivida : itemProcessoDebito.getParcela().getItensParcelaValorDivida()) {
            ItemProcessoDebitoParcela itemProcessoDebitoParcela = new ItemProcessoDebitoParcela();
            itemProcessoDebitoParcela.setItemParcelaValorDivida(itemParcelaValorDivida);
            itemProcessoDebitoParcela.setValor(itemParcelaValorDivida.getValor());
            itemProcessoDebitoParcela.setValorOriginal(itemParcelaValorDivida.getValor());
            itemProcessoDebitoParcela.setTributo(itemParcelaValorDivida.getTributo());
            itemProcessoDebitoParcela.setItemProcessoDebito(itemProcessoDebito);
            itemProcessoDebito.getItemProcessoDebitoParcela().add(itemProcessoDebitoParcela);
        }
    }

    private void criarItemProcessoDebitoParcelamento(ItemProcessoDebito itemProcessoDebito) {
        List<ItemProcessoParcelamento> itensProcessoParcelamento = buscarItemProcessoParcelamento(itemProcessoDebito.getParcela());
        if (itensProcessoParcelamento != null) {
            for (ItemProcessoParcelamento item : itensProcessoParcelamento) {
                ResultadoParcela resultadoParcela = getAcrescimosAtualizados(itemProcessoDebito, item);
                ItemProcessoDebitoParcelamento itemProcessoDebitoParcelamento = new ItemProcessoDebitoParcelamento();
                itemProcessoDebitoParcelamento.setImposto(resultadoParcela.getValorImposto());
                itemProcessoDebitoParcelamento.setTaxa(resultadoParcela.getValorTaxa());
                if (resultadoParcela.getValorJuros().compareTo(BigDecimal.ZERO) > 0 && resultadoParcela.getValorJuros().compareTo(item.getJuros()) != 0) {
                    itemProcessoDebitoParcelamento.setJuros(resultadoParcela.getValorJuros());
                } else {
                    itemProcessoDebitoParcelamento.setJuros(item.getJuros());
                }

                if (resultadoParcela.getValorMulta().compareTo(BigDecimal.ZERO) > 0 && resultadoParcela.getValorMulta().compareTo(item.getMulta()) != 0) {
                    itemProcessoDebitoParcelamento.setMulta(resultadoParcela.getValorMulta());
                } else {
                    itemProcessoDebitoParcelamento.setMulta(item.getMulta());
                }

                if (resultadoParcela.getValorCorrecao().compareTo(BigDecimal.ZERO) > 0 && resultadoParcela.getValorCorrecao().compareTo(item.getCorrecao()) != 0) {
                    itemProcessoDebitoParcelamento.setCorrecao(resultadoParcela.getValorCorrecao());
                } else {
                    itemProcessoDebitoParcelamento.setCorrecao(item.getCorrecao());
                }

                if (resultadoParcela.getValorHonorarios().compareTo(BigDecimal.ZERO) > 0 && resultadoParcela.getValorHonorarios().compareTo(item.getHonorarios()) != 0) {
                    itemProcessoDebitoParcelamento.setHonorarios(resultadoParcela.getValorHonorarios());
                } else {
                    itemProcessoDebitoParcelamento.setHonorarios(item.getHonorarios());
                }
                itemProcessoDebitoParcelamento.setItemProcessoDebito(itemProcessoDebito);
                itemProcessoDebitoParcelamento.setItemProcessoParcelamento(item);
                itemProcessoDebito.getItemProcessoDebitoParcelamento().add(itemProcessoDebitoParcelamento);
            }
        }
    }

    private ResultadoParcela getAcrescimosAtualizados(ItemProcessoDebito itemProcessoDebito, ItemProcessoParcelamento item) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, itemProcessoDebito.getParcela().getId());
        consultaParcela.executaConsulta();
        ResultadoParcela resultadoParcela = consultaParcela.getResultados().get(0);
        CalculadorAcrescimos calculadorAcrescimos = new CalculadorAcrescimos(consultaParcela.getServiceConsultaDebitos());
        resultadoParcela.setSituacao(SituacaoParcela.EM_ABERTO.name());
        calculadorAcrescimos.limparCaches();
        resultadoParcela = calculadorAcrescimos.preencherValoresAcrescimosImpostoTaxaResultadoParcela(
            resultadoParcela, item.getProcessoParcelamento().getDataProcessoParcelamento(), null, null, null,
            null, null);
        return resultadoParcela;
    }

    private List<ItemProcessoParcelamento> buscarItemProcessoParcelamento(ParcelaValorDivida parcela) {
        String sql = " select item.* from ItemProcessoParcelamento item " +
            "  inner join PROCESSOPARCELAMENTO processo on item.PROCESSOPARCELAMENTO_ID = processo.id " +
            "  where PARCELAVALORDIVIDA_ID = :pvd " +
            "  and processo.SITUACAOPARCELAMENTO <> :situacaoEstornado";
        Query q = em.createNativeQuery(sql, ItemProcessoParcelamento.class);
        q.setParameter("situacaoEstornado", SituacaoParcelamento.ESTORNADO.name());
        q.setParameter("pvd", parcela.getId());
        Util.imprimeSQL(sql, q);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    private boolean validarItemProcesso(ProcessoDebito selecionado, ResultadoParcela resultadoParcelas) {
        boolean valida = true;
        for (ItemProcessoDebito item : selecionado.getItens()) {
            if (item.getParcela().getId().equals(resultadoParcelas.getIdParcela())) {
                valida = false;
                break;
            }
        }
        return valida;
    }

    public ParcelaValorDivida recuperarParcelaValorDivida(Long id) {
        String sql = " select pvd.* from ParcelaValorDivida pvd where id = :id";
        Query q = em.createNativeQuery(sql, ParcelaValorDivida.class);
        q.setParameter("id", id);
        ParcelaValorDivida pvd = (ParcelaValorDivida) q.getResultList().get(0);
        pvd.getItensParcelaValorDivida().size();
        return pvd;
    }

    public boolean temSituacaoPosteriorAEfetivacaoDoProcesso(ParcelaValorDivida parcela, Date dataLancamento) {
        String sql = "select spvd.id from SituacaoParcelaValorDivida spvd " +
            " where spvd.parcela_id = :idParcela " +
            " and spvd.dataLancamento > :dataLancamento" +
            " and spvd.situacaoParcela <> :situacaoIsolado ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", parcela.getId());
        q.setParameter("dataLancamento", dataLancamento);
        q.setParameter("situacaoIsolado", SituacaoParcela.ISOLAMENTO.name());
        return !q.getResultList().isEmpty() && q.getResultList().size() > 1;
    }

    public ItemProcessoDebito salvarItem(ItemProcessoDebito itemProcessoDebito) {
        return em.merge(itemProcessoDebito);
    }
}
