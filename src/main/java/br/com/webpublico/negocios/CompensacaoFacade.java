package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ResultadoValidacao;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Fabio on 06/08/2018.
 */
@Stateless
public class CompensacaoFacade extends AbstractFacade<Compensacao> {

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
    @EJB
    private ContaCorrenteTributariaFacade contaCorrenteTributariaFacade;
    @EJB
    private ProcessoCreditoContaCorrenteFacade processoCreditoContaCorrenteFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private DAMFacade damFacade;

    public CompensacaoFacade() {
        super(Compensacao.class);
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

    public ContaCorrenteTributariaFacade getContaCorrenteTributariaFacade() {
        return contaCorrenteTributariaFacade;
    }

    @Override
    public void salvar(Compensacao entity) throws ValidacaoException {
        em.merge(entity);
    }

    @Override
    public void salvarNovo(Compensacao entity) throws ValidacaoException {
        em.persist(entity);
    }

    private void baixarOutraOpcaoParcelasDaCotaUnicaOrCotaUnidaDasParcelas(ParcelaValorDivida parcela, Compensacao processo, SituacaoParcela novaSituacaoParcela, SituacaoParcela parcelaParaPesquisa) {
        List<ParcelaValorDivida> parcelasOutraOpcao = consultaDebitoFacade.buscarParcelasDaCotaUnicaOrACotaUnicaDasParcelas(parcela, parcelaParaPesquisa);
        if (!parcelasOutraOpcao.isEmpty()) {
            for (ParcelaValorDivida parcelaDaOutraOpcao : parcelasOutraOpcao) {
                if (!hasParcelaNoProcesso(processo, parcelaDaOutraOpcao)) {
                    SituacaoParcelaValorDivida situacaoAnterior = consultaDebitoFacade.getUltimaSituacao(parcelaDaOutraOpcao);
                    em.persist(new SituacaoParcelaValorDivida(novaSituacaoParcela, parcelaDaOutraOpcao, situacaoAnterior.getSaldo()));
                }
            }
        }
    }

    private boolean hasParcelaNoProcesso(Compensacao processo, ParcelaValorDivida parcela) {
        for (ItemCompensacao item : processo.getItens()) {
            if (item.getParcela().getId().equals(parcela.getId())) {
                return true;
            }
        }
        return false;
    }

    public ResultadoValidacao encerrarProcesso(Compensacao processo) {
        ResultadoValidacao retorno = validarEncerramento(processo);
        if (retorno.isValidado()) {
            try {
                for (ItemCompensacao itemCompensacao : processo.getItens()) {
                    itemCompensacao.setImposto(itemCompensacao.getResultadoParcela().getValorImposto());
                    itemCompensacao.setTaxa(itemCompensacao.getResultadoParcela().getValorTaxa());
                    itemCompensacao.setJuros(itemCompensacao.getResultadoParcela().getValorJuros());
                    itemCompensacao.setMulta(itemCompensacao.getResultadoParcela().getValorMulta());
                    itemCompensacao.setCorrecao(itemCompensacao.getResultadoParcela().getValorCorrecao());
                    itemCompensacao.setHonorarios(itemCompensacao.getResultadoParcela().getValorHonorarios());

                    SituacaoParcelaValorDivida situacaoAnterior = consultaDebitoFacade.getUltimaSituacao(itemCompensacao.getParcela());
                    SituacaoParcelaValorDivida novaSituacao = new SituacaoParcelaValorDivida(SituacaoParcela.COMPENSACAO, itemCompensacao.getParcela(), situacaoAnterior.getSaldo());
                    novaSituacao.setGeraReferencia(false);
                    novaSituacao.setReferencia(situacaoAnterior.getReferencia());
                    em.persist(novaSituacao);

                    baixarOutraOpcaoParcelasDaCotaUnicaOrCotaUnidaDasParcelas(itemCompensacao.getParcela(), processo, SituacaoParcela.BAIXADO_OUTRA_OPCAO, SituacaoParcela.EM_ABERTO);
                }
                processo.setSituacao(SituacaoProcessoDebito.FINALIZADO);
                em.merge(processo);
            } catch (Exception ex) {
                logger.error("Erro ao efetivar o processo de compensação: {}", ex);
            }
        }
        return retorno;
    }

    private ResultadoValidacao validarEncerramento(Compensacao processo) {
        ResultadoValidacao retorno = new ResultadoValidacao();
        if (!SituacaoProcessoDebito.EM_ABERTO.equals(processo.getSituacao())) {
            retorno.addErro(null, "Não foi possível continuar!", "O processo selecionado encontra-se " + processo.getSituacao().getDescricao() + ". Somente processos EM ABERTO podem ser finalizados.");
        }
        for (ItemCompensacao item : processo.getItens()) {
            if (contaCorrenteTributariaFacade.verificarParcelaNaContaCorrente(item.getParcela())) {
                retorno.addErro(null, "Não foi possível continuar!", "O processo possui parcelas que já existem em conta corrente!");
            }
        }
        return retorno;
    }

    public void estornarProcesso(Compensacao processo, SituacaoProcessoDebito situacao) throws ValidacaoException {
        try {
            validarEstorno(processo);
            processo.setSituacao(situacao);

            ContaCorrenteTributaria contaCorrente = contaCorrenteTributariaFacade.buscarContaCorrentePorPessoa(processo.getPessoa());
            if (contaCorrente != null) {
                for (CreditoCompensacao credito : processo.getCreditos()) {
                    for (ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria : contaCorrente.getParcelasCreditos()) {
                        if (parcelaContaCorrenteTributaria.getParcelaValorDivida().equals(credito.getParcela())) {
                            parcelaContaCorrenteTributaria.setCompensada(false);
                            parcelaContaCorrenteTributaria.setValorCompesado(parcelaContaCorrenteTributaria.getValorCompesado().subtract(credito.getValorCompensado()));
                        }
                    }
                }
                contaCorrente = contaCorrenteTributariaFacade.salvarRetornando(contaCorrente);
            }

            for (ItemCompensacao item : processo.getItens()) {
                ParcelaValorDivida parcela = em.find(ParcelaValorDivida.class, item.getParcela().getId());
                if (SituacaoParcela.COMPENSACAO.equals(parcela.getUltimaSituacao().getSituacaoParcela())) {
                    List<ParcelaValorDivida> parcelasOutraOpcao = consultaDebitoFacade.buscarParcelasDaCotaUnicaOrACotaUnicaDasParcelas(parcela, SituacaoParcela.PAGO);
                    if (parcelasOutraOpcao.isEmpty()) {
                        processoDebitoFacade.salvarSituacaoParcela(parcela, SituacaoParcela.EM_ABERTO);
                        baixarOutraOpcaoParcelasDaCotaUnicaOrCotaUnidaDasParcelas(parcela, processo, SituacaoParcela.EM_ABERTO, SituacaoParcela.BAIXADO_OUTRA_OPCAO);
                    } else {
                        processoDebitoFacade.salvarSituacaoParcela(parcela, SituacaoParcela.BAIXADO_OUTRA_OPCAO);
                    }
                }

                Long idCalculoCompensacao = buscarIdCalculoDaCompensacao(processo);
                if (idCalculoCompensacao != null) {
                    List<ParcelaValorDivida> parcelasResidual = consultaDebitoFacade.recuperarParcelasDoCalculo(idCalculoCompensacao);
                    for (ParcelaValorDivida parcelaResidual : parcelasResidual) {
                        if (SituacaoParcela.EM_ABERTO.equals(parcelaResidual.getUltimaSituacao().getSituacaoParcela())) {
                            processoDebitoFacade.salvarSituacaoParcela(parcelaResidual, SituacaoParcela.CANCELAMENTO);
                        }
                    }
                }

                if (contaCorrente != null) {
                    List<ParcelaContaCorrenteTributaria> parcelaParaRemover = Lists.newArrayList();
                    for (ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria : contaCorrente.getParcelas()) {
                        if (parcelaContaCorrenteTributaria.getParcelaValorDivida().getId().equals(item.getParcela().getId()) &&
                            ContaCorrenteTributaria.Origem.COMPENSACAO.equals(parcelaContaCorrenteTributaria.getOrigem())) {
                            parcelaParaRemover.add(parcelaContaCorrenteTributaria);
                        }
                    }
                    for (ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria : parcelaParaRemover) {
                        contaCorrente.getParcelas().remove(parcelaContaCorrenteTributaria);
                    }
                    contaCorrente = contaCorrenteTributariaFacade.salvarRetornando(contaCorrente);
                }
            }
            em.merge(processo);
        } catch (ValidacaoException ve) {
            ve.lancarException();
        }
    }

    private Long buscarIdCalculoDaCompensacao(Compensacao compensacao) {
        String sql = "select cal.id from CalculoContaCorrente cal where cal.compensacao_id = :idCompensacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCompensacao", compensacao.getId());
        List resultList = q.getResultList();
        return !resultList.isEmpty() ? ((BigDecimal) resultList.get(0)).longValue() : null;
    }

    private void validarEstorno(Compensacao processo) {
        ValidacaoException ve = new ValidacaoException();
        if (!SituacaoProcessoDebito.FINALIZADO.equals(processo.getSituacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo selecionado encontra-se " + processo.getSituacao().getDescricao() + ". Somente processos FINALIZADOS podem ser estornados.");
        }
        if (hasParcelaUtitizadaNaContaCorrente(processo)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo não pode ser estornado, pois existe uma ou mais parcelas que já foram utilizadas na conta corrente!");
        }
        ve.lancarException();
    }

    private boolean hasParcelaUtitizadaNaContaCorrente(Compensacao processo) {
        List<Long> idsParcelas = Lists.newArrayList();
        for (ItemCompensacao itemCompensacao : processo.getItens()) {
            idsParcelas.add(itemCompensacao.getParcela().getId());
        }
        if (!idsParcelas.isEmpty()) {
            String sql = " select pcct.id from parcelacontacorrentetrib pcct " +
                " where pcct.parcelavalordivida_id in (:idsParcelas) " +
                " and pcct.calculocontacorrente_id is not null " +
                " and pcct.calculocontacorrente_id not in (select c.id from calculocontacorrente c " +
                "                                          where c.compensacao_id = :idCompensacao) ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idsParcelas", idsParcelas);
            q.setParameter("idCompensacao", processo.getId());
            return !q.getResultList().isEmpty();
        }
        return false;
    }


    @Override
    public List<Compensacao> lista() {
        return em.createQuery("from Compensacao p order by p.codigo").getResultList();
    }

    @Override
    public Compensacao recuperar(Object id) {
        Compensacao p = em.find(Compensacao.class, id);
        Hibernate.initialize(p.getItens());
        Hibernate.initialize(p.getCreditos());
        Hibernate.initialize(p.getArquivos());
        return p;
    }

    public void adicionarItem(ResultadoParcela parcela, Compensacao selecionado, List<ResultadoParcela> resultadoConsulta, BigDecimal valorCompensado) {
        ItemCompensacao item = new ItemCompensacao();
        item.setCompensacao(selecionado);
        item.setReferencia(parcela.getReferencia());
        item.setParcela(recuperarParcelaValorDivida(parcela.getIdParcela()));
        item.setResultadoParcela(parcela);
        item.setValorCompensado(valorCompensado);
        item.setValorResidual(parcela.getValorTotal().subtract(valorCompensado));
        selecionado.getItens().add(item);
        resultadoConsulta.remove(parcela);
    }

    public boolean validarItemProcesso(Compensacao selecionado, ResultadoParcela resultadoParcelas) {
        boolean valida = true;
        for (ItemCompensacao item : selecionado.getItens()) {
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

    public Long recuperarProximoCodigoPorExercicio(Exercicio exercicio) {
        String sql = " select max(coalesce(obj.codigo,0)) from Compensacao obj "
            + " where obj.exercicio = :exercicio";
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

    public boolean verificarCodigoEmUso(Compensacao processo) {
        if (processo == null || processo.getCodigo() == null) {
            return false;
        }
        String hql = "from Compensacao pd where pd.codigo = :codigo and pd.exercicio = :exercicio ";
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

    public NfseFacade getNfseFacade() {
        return nfseFacade;
    }

    public PermissaoTransporteFacade getPermissaoTransporteFacade() {
        return permissaoTransporteFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    public ProcessoCreditoContaCorrenteFacade getProcessoCreditoContaCorrenteFacade() {
        return processoCreditoContaCorrenteFacade;
    }

    public Compensacao buscarUltimoProcessoCompensacaoPorParcelaValorDivida(Long pvd) {
        String sql = "select p.*" +
            "           from ItemCompensacao item " +
            "          inner join Compensacao p on p.id = item.compensacao_id" +
            "         where item.parcela_id = :pvd" +
            "           and p.situacao = :situacao" +
            "           order by p.id desc";
        Query q = em.createNativeQuery(sql, Compensacao.class);
        try {
            q.setMaxResults(1);
            q.setParameter("pvd", pvd);
            q.setParameter("situacao", SituacaoProcessoDebito.FINALIZADO.name());
            return (Compensacao) q.getSingleResult();
        } catch (Exception no) {
            return null;
        }
    }

    public Compensacao buscarCompensacaoFinalizadaParaItemCreditoContaCorrente(ItemCreditoContaCorrente itemCreditoContaCorrente) {
        String sql = "select p.* from Compensacao p " +
            "inner join CREDITOCOMPENSACAO item on item.COMPENSACAO_ID=p.ID " +
            "where PARCELA_ID = :parcela";
        Query q = em.createNativeQuery(sql, Compensacao.class);
        try {
            q.setParameter("parcela", itemCreditoContaCorrente.getParcela().getId());
            q.setMaxResults(1);
            Compensacao compensacao = (Compensacao) q.getSingleResult();
            if (SituacaoProcessoDebito.FINALIZADO.equals(compensacao.getSituacao())) {
                for (CreditoCompensacao creditoCompensacao : compensacao.getCreditos()) {
                    if (creditoCompensacao.getValorCompensado().compareTo(BigDecimal.ZERO) > 0) {
                        return compensacao;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Erro ao buscar a compensacao: {}", ex);
        }
        return null;
    }

    public Compensacao atualizarContaCorrente(Compensacao compensacao) {
        BigDecimal valorCompensado = compensacao.getValorCompensado();
        for (CreditoCompensacao credito : compensacao.getCreditos()) {
            if (valorCompensado.compareTo(credito.getDiferencaAtualizada()) < 0) {
                contaCorrenteTributariaFacade.adicionarValorAParcelaCompensada(compensacao.getPessoa(), credito.getParcela(), credito.getDiferencaAtualizada(), valorCompensado);
                credito.setValorCompensado(valorCompensado);
                break;
            } else {
                contaCorrenteTributariaFacade.compensarParcelaCompensada(compensacao.getPessoa(), credito.getParcela(), credito.getDiferencaAtualizada());
                credito.setValorCompensado(credito.getDiferencaAtualizada());
                valorCompensado = valorCompensado.subtract(credito.getDiferencaAtualizada());
            }
        }
        return em.merge(compensacao);
    }

    public List<ResultadoParcela> buscarParcelaDaCompensacao(Long idParcela) {
        String sql = "select cc.id from ItemCompensacao icp " +
            "inner join CalculoContaCorrente cc on cc.Compensacao_id = icp.Compensacao_id " +
            "inner join ValorDivida vd on vd.calculo_id = cc.id " +
            "where icp.parcela_id = :idParcela";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", idParcela);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.CALCULO_ID,
                br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IN, resultList);
            return consultaParcela.executaConsulta().getResultados();
        }
        return Lists.newArrayList();
    }
}
