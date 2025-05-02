package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.consultaparcela.DTO.ValoresPagosParcela;
import br.com.webpublico.negocios.tributario.services.ServiceConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ResultadoValidacao;
import com.google.common.collect.Lists;
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
 * Created by Fabio on 06/08/2018.
 */
@Stateless
public class ProcessoCreditoContaCorrenteFacade extends AbstractFacade<CreditoContaCorrente> {

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
    private ProcessoDebitoFacade processoDebitoFacade;
    @EJB
    private ContaCorrenteTributariaFacade contaCorrenteTributariaFacade;
    private ServiceConsultaDebitos serviceConsultaDebitos;

    public ProcessoCreditoContaCorrenteFacade() {
        super(CreditoContaCorrente.class);
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        serviceConsultaDebitos = (ServiceConsultaDebitos) ap.getBean("serviceConsultaDebitos");
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
    public void salvar(CreditoContaCorrente entity) throws ValidacaoException {
        em.merge(entity);
    }

    @Override
    public void salvarNovo(CreditoContaCorrente entity) throws ValidacaoException {
        em.persist(entity);
    }

    public ResultadoValidacao encerrarProcesso(CreditoContaCorrente processo) {
        ResultadoValidacao retorno = validarEncerramento(processo);
        if (retorno.isValidado()) {
            processo.setSituacao(SituacaoProcessoDebito.FINALIZADO);

            ContaCorrenteTributaria contaCorrente = contaCorrenteTributariaFacade.buscarOuCriarContaCorrentePorPessoa(processo.getPessoa(), ContaCorrenteTributaria.Origem.PROCESSO);
            for (ItemCreditoContaCorrente item : processo.getItens()) {
                contaCorrenteTributariaFacade.adicionarParcelaNaContaCorrente(contaCorrente, item.getParcela(),
                    processo.getTipoDiferencaContaCorrente(), item.getDiferencaContaCorrente(), ContaCorrenteTributaria.Origem.PROCESSO, null);
            }
            contaCorrenteTributariaFacade.salvar(contaCorrente);
            retorno.limpaMensagens();
            em.merge(processo);
        }
        return retorno;
    }

    private ResultadoValidacao validarEncerramento(CreditoContaCorrente processo) {
        ResultadoValidacao retorno = new ResultadoValidacao();
        if (processo.getSituacao() != SituacaoProcessoDebito.EM_ABERTO) {
            retorno.addErro(null, "Não foi possível continuar!", "O processo selecionado encontra-se " + processo.getSituacao().getDescricao() + ". Somente processos EM ABERTO podem ser finalizados.");
        }
        for (ItemCreditoContaCorrente item : processo.getItens()) {
            if (contaCorrenteTributariaFacade.verificarParcelaNaContaCorrente(item.getParcela())) {
                retorno.addErro(null, "Não foi possível continuar!", "O processo possui parcelas que já existem em conta corrente!");
            }
        }
        return retorno;
    }

    public void estornarProcesso(CreditoContaCorrente processo, SituacaoProcessoDebito situacao) throws ValidacaoException {
        try {
            validarEstorno(processo);
            processo.setSituacao(situacao);
            List<ParcelaContaCorrenteTributaria> parcelaParaRemover = Lists.newArrayList();

            ContaCorrenteTributaria contaCorrente = contaCorrenteTributariaFacade.buscarContaCorrentePorPessoa(processo.getPessoa());
            if (contaCorrente != null) {
                for (ItemCreditoContaCorrente item : processo.getItens()) {
                    for (ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria : contaCorrente.getParcelas()) {
                        if (parcelaContaCorrenteTributaria.getParcelaValorDivida().getId().equals(item.getParcela().getId()) &&
                            ContaCorrenteTributaria.Origem.PROCESSO.equals(parcelaContaCorrenteTributaria.getOrigem())) {
                            parcelaParaRemover.add(parcelaContaCorrenteTributaria);
                        }
                    }
                }
                for (ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria : parcelaParaRemover) {
                    contaCorrente.getParcelas().remove(parcelaContaCorrenteTributaria);
                }
                contaCorrenteTributariaFacade.salvar(contaCorrente);
            }
            em.merge(processo);
        } catch (ValidacaoException ve) {
            ve.lancarException();
        }
    }

    private void validarEstorno(CreditoContaCorrente processo) {
        ValidacaoException ve = new ValidacaoException();
        if (processo.getSituacao() != SituacaoProcessoDebito.FINALIZADO) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo selecionado encontra-se " + processo.getSituacao().getDescricao() + ". Somente processos FINALIZADOS podem ser estornados.");
        }
        if (hasParcelaUtitizadaNaContaCorrente(processo)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo não pode ser estornado, pois existe uma ou mais parcelas que já foram utilizadas na conta corrente!");
        }
        ve.lancarException();
    }

    private boolean hasParcelaUtitizadaNaContaCorrente(CreditoContaCorrente processo) {
        List<Long> idsParcelas = Lists.newArrayList();
        for (ItemCreditoContaCorrente itemCreditoContaCorrente : processo.getItens()) {
            idsParcelas.add(itemCreditoContaCorrente.getParcela().getId());
        }
        if (!idsParcelas.isEmpty()) {
            String sql = "select pcct.id from parcelacontacorrentetrib pcct " +
                " where pcct.parcelavalordivida_id in (:idsParcelas) " +
                "   and pcct.calculocontacorrente_id is not null";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idsParcelas", idsParcelas);
            return !q.getResultList().isEmpty();
        }
        return false;
    }


    @Override
    public List<CreditoContaCorrente> lista() {
        return em.createQuery("from CreditoContaCorrente p order by p.codigo").getResultList();
    }

    @Override
    public CreditoContaCorrente recuperar(Object id) {
        CreditoContaCorrente p = em.find(CreditoContaCorrente.class, id);
        p.getItens().size();
        p.getArquivos().size();
        return p;
    }

    public void adicionarItem(ResultadoParcela parcela, CreditoContaCorrente selecionado, List<ResultadoParcela> resultadoConsulta) {
        ItemCreditoContaCorrente item = new ItemCreditoContaCorrente();
        item.setCreditoContaCorrente(selecionado);
        item.setReferencia(parcela.getReferencia());
        item.setParcela(recuperarParcelaValorDivida(parcela.getIdParcela()));
        item.setResultadoParcela(parcela);
        item.setDiferencaContaCorrente(parcela.getValorTotal().subtract(parcela.getValorPago()));
        if (item.getDiferencaContaCorrente().compareTo(BigDecimal.ZERO) <= 0) {
            item.setDiferencaContaCorrente(parcela.getValorPago());
        }
        selecionado.getItens().add(item);
        resultadoConsulta.remove(parcela);
    }

    public boolean validarItemProcesso(CreditoContaCorrente selecionado, ResultadoParcela resultadoParcelas) {
        boolean valida = true;
        for (ItemCreditoContaCorrente item : selecionado.getItens()) {
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
        String sql = " select max(coalesce(obj.codigo,0)) from CreditoContaCorrente obj "
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

    public boolean verificarCodigoEmUso(CreditoContaCorrente processo) {
        if (processo == null || processo.getCodigo() == null) {
            return false;
        }
        String hql = "from CreditoContaCorrente pd where pd.codigo = :codigo and pd.exercicio = :exercicio ";
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

    private void buscarValoresPagosNaArrecadacaoComProcessoAberto(ResultadoParcela parcela) {
        ValoresPagosParcela valoresPagosParcela = serviceConsultaDebitos.buscarValorPagoDaParcelaNaArrecadacao(parcela.getIdParcela());
        if (valoresPagosParcela != null && valoresPagosParcela.getTotalPago().compareTo(BigDecimal.ZERO) > 0) {
            parcela.setValorPago(valoresPagosParcela.getTotalPago());
            if (parcela.getPagamento() == null) {
                parcela.setPagamento(valoresPagosParcela.getDataPagamento());
            }
        } else {
            buscarValorDePagamentoAvulsoDeParcelasAntigas(parcela, new Date());
            if (parcela.getValorPago().compareTo(BigDecimal.ZERO) <= 0) {
                valoresPagosParcela = serviceConsultaDebitos.recuperaValoresPagosParcela(parcela, null, null);
                if (valoresPagosParcela != null) {
                    parcela.setValorPago(valoresPagosParcela.getTotalPago());
                    if (parcela.getPagamento() == null) {
                        parcela.setPagamento(valoresPagosParcela.getDataPagamento());
                    }
                }
            }
        }
    }

    public void buscarValorPagoNaArrecadacaoCompensacao(Compensacao compensacao, List<ResultadoParcela> resultadoParcelas) {
        for (ResultadoParcela parcela : resultadoParcelas) {
            if (SituacaoProcessoDebito.EM_ABERTO.equals(compensacao.getSituacao())) {
                buscarValoresPagosNaArrecadacaoComProcessoAberto(parcela);
            } else {
                buscarValoresPagosNaArrecadacaoComProcessoFinalizado(parcela, ContaCorrenteTributaria.Origem.COMPENSACAO);
            }
        }
    }

    public void buscarValorPagoNaArrecadacaoRestituicao(Restituicao restituicao, List<ResultadoParcela> resultadoParcelas) {
        for (ResultadoParcela parcela : resultadoParcelas) {
            if (SituacaoProcessoDebito.EM_ABERTO.equals(restituicao.getSituacao())) {
                buscarValoresPagosNaArrecadacaoComProcessoAberto(parcela);
            } else {
                buscarValoresPagosNaArrecadacaoComProcessoFinalizado(parcela, null);
            }
        }
    }

    private void buscarValoresPagosNaArrecadacaoComProcessoFinalizado(ResultadoParcela parcela, ContaCorrenteTributaria.Origem origem) {
        ValoresPagosParcela valoresPagosParcela;
        if (ContaCorrenteTributaria.Origem.COMPENSACAO.equals(origem)) {
            valoresPagosParcela = contaCorrenteTributariaFacade.buscarValorPagosNaCompensacao(parcela.getIdParcela());
        } else {
            valoresPagosParcela = contaCorrenteTributariaFacade.buscarValorePagoNaRestituicao(parcela.getIdParcela());
        }

        if (valoresPagosParcela != null) {
            if (valoresPagosParcela.getTotalPago() != null) {
                parcela.setValorPago(valoresPagosParcela.getTotalPago());
            }
            if (valoresPagosParcela.getDataPagamento() != null) {
                parcela.setPagamento(valoresPagosParcela.getDataPagamento());
            }
        }
    }

    public void buscarValorPagoNaArrecadacao
        (List<ResultadoParcela> resultadoParcelas, ContaCorrenteTributaria.Origem origem) {
        for (ResultadoParcela parcela : resultadoParcelas) {
            if (!ContaCorrenteTributaria.Origem.COMPENSACAO.equals(origem)) {
                buscarValoresPagosNaArrecadacaoComProcessoAberto(parcela);
            } else {
                buscarValoresPagosNaArrecadacaoComProcessoFinalizado(parcela, origem);
            }
        }
    }

    public void buscarValorDePagamentoAvulsoDeParcelasAntigas(ResultadoParcela resultadoParcela, Date calcularAte) {
        try {
            String sql = "SELECT MAX(DATAPAGAMENTO), SUM(VALORPAGO) " +
                "  FROM PAGAMENTOAVULSO PGTO " +
                " WHERE PGTO.PARCELAVALORDIVIDA_ID = :parcela " +
                "   AND PGTO.DATAPAGAMENTO <= :calcularAte " +
                "   AND COALESCE(PGTO.ATIVO, 1) = 1";
            Query q = em.createNativeQuery(sql);
            q.setParameter("parcela", resultadoParcela.getIdParcela());
            q.setParameter("calcularAte", calcularAte);
            if (!q.getResultList().isEmpty()) {
                Object[] obj = (Object[]) q.getResultList().get(0);
                if (resultadoParcela.getPagamento() == null) {
                    resultadoParcela.setPagamento(obj[0] != null ? (Date) obj[0] : null);
                }
                resultadoParcela.setValorPago(obj[1] != null ? (BigDecimal) obj[1] : BigDecimal.ZERO);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public CreditoContaCorrente buscarUltimoProcessoPorParcelValorDivida(Long pvd) {
        String sql = "select ccc.* from ItemCreditoContaCorrente item " +
            "          inner join CreditoContaCorrente ccc on ccc.id = item.creditocontacorrente_id " +
            "          where item.parcela_id = :pvd " +
            "            and ccc.situacao = :situacao " +
            "          order by ccc.id desc";
        Query q = em.createNativeQuery(sql, CreditoContaCorrente.class);
        try {
            q.setMaxResults(1);
            q.setParameter("pvd", pvd);
            q.setParameter("situacao", SituacaoProcessoDebito.FINALIZADO.name());
            return (CreditoContaCorrente) q.getSingleResult();
        } catch (Exception no) {
            return null;
        }
    }
}
