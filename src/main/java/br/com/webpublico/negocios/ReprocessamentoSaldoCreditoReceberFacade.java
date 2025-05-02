package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CreditoReceber;
import br.com.webpublico.entidades.LancamentoReceitaOrc;
import br.com.webpublico.entidades.ReceitaORCEstorno;
import br.com.webpublico.entidades.ReprocessamentoHistorico;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoHistoricoFacade;
import br.com.webpublico.seguranca.service.QueryReprocessamentoService;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mateus on 07/05/2015.
 */
@Stateless
public class ReprocessamentoSaldoCreditoReceberFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoCreditoReceberFacade saldoCreditoReceberFacade;
    @EJB
    private LancamentoReceitaOrcFacade lancamentoReceitaOrcFacade;
    @EJB
    private ReceitaORCEstornoFacade receitaORCEstornoFacade;
    @EJB
    private ReprocessamentoHistoricoFacade reprocessamentoHistoricoFacade;

    private void excluirMovimentos(AssistenteReprocessamento assistente) {
        saldoCreditoReceberFacade.excluirSaldosPeriodo(assistente.getDataInicial(), assistente.getDataFinal());
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<AssistenteReprocessamento> reprocessar(AssistenteReprocessamento assistente) {
        AsyncResult<AssistenteReprocessamento> result = new AsyncResult<>(assistente);
        assistente.setQueryReprocessamentoCreditoReceber(QueryReprocessamentoService.getService().getReprocessamentoCreditoReceber());
        assistente.setQueryReprocessamentoCreditoReceberReceita(QueryReprocessamentoService.getService().getReprocessamentoCreditoReceberReceita());
        assistente.setQueryReprocessamentoCreditoReceberReceitaEstorno(QueryReprocessamentoService.getService().getReprocessamentoCreditoReceberReceitaEstorno());
        assistente.getAssistenteBarraProgresso().inicializa();
        assistente.getAssistenteBarraProgresso().setMensagem("EXCLUINDO SALDOS CRÉDITOS A RECEBER");
        if (!assistente.getQueryReprocessamentoCreditoReceber().isEmpty() &&
            !assistente.getQueryReprocessamentoCreditoReceberReceita().isEmpty() &&
            !assistente.getQueryReprocessamentoCreditoReceberReceitaEstorno().isEmpty()) {
            excluirMovimentos(assistente);
        }

        gerarSaldoCreditoReceberParaCreditoReceber(assistente);
        gerarSaldoCreditoReceberParaReceitaRealizada(assistente);
        gerarSaldoCreditoReceberParaEstornoReceita(assistente);
        assistente.finalizar();
        salvarHistorico(assistente.getHistorico());
        return result;
    }

    private void gerarSaldoCreditoReceberParaCreditoReceber(AssistenteReprocessamento assistente) {
        assistente.getAssistenteBarraProgresso().setMensagem("RECUPERANDO MOVIMENTOS DE CRÉDITOS A RECEBER");
        List<CreditoReceber> creditosReceber = buscarCreditosReceber(assistente);
        assistente.getAssistenteBarraProgresso().setTotal(creditosReceber.size());
        assistente.getHistorico().atualizarTotal(creditosReceber.size());

        for (CreditoReceber creditoReceber : creditosReceber) {
            assistente.getAssistenteBarraProgresso().setMensagem("GERANDO SALDO CRÉDITOS A RECEBER NA DATA " + DataUtil.getDataFormatada(creditoReceber.getDataCredito()));
            try {
                saldoCreditoReceberFacade.gerarSaldoCreditoReceber(creditoReceber, false);
                assistente.adicionarHistoricoLogSemErro(creditoReceber, "");
                assistente.historicoContaSemErro();
            } catch (OptimisticLockException optex) {
                throw new ExcecaoNegocioGenerica("Saldo créditos a receber está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
            } catch (Exception ex) {
                assistente.adicionarReprocessandoErroLog(ex.getMessage(), creditoReceber, "");
                assistente.historicoContaComErro();
            } finally {
                assistente.historicoConta();
            }
        }
        assistente.getAssistenteBarraProgresso().finaliza();
    }

    private void gerarSaldoCreditoReceberParaReceitaRealizada(AssistenteReprocessamento assistente) {
        assistente.getAssistenteBarraProgresso().inicializa();
        assistente.getAssistenteBarraProgresso().setMensagem("RECUPERANDO MOVIMENTOS DE RECEITA REALIZADA");
        List<LancamentoReceitaOrc> receitas = buscarReceitaRealizada(assistente);
        assistente.getAssistenteBarraProgresso().setTotal(receitas.size());
        assistente.getHistorico().atualizarTotal(receitas.size());

        for (LancamentoReceitaOrc receita : receitas) {
            assistente.getAssistenteBarraProgresso().setMensagem("GERANDO SALDO CRÉDITOS A RECEBER PARA RECEITA REALIZADA NA DATA " + DataUtil.getDataFormatada(receita.getDataLancamento()));
            try {
                receita = lancamentoReceitaOrcFacade.recuperar(receita.getId());
                lancamentoReceitaOrcFacade.gerarSaldoDividaAtivaAndCreditoReceber(receita);
                assistente.adicionarHistoricoLogSemErro(receita, "");
                assistente.historicoContaSemErro();
            } catch (OptimisticLockException optex) {
                throw new ExcecaoNegocioGenerica("Saldo créditos a receber está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
            } catch (Exception ex) {
                assistente.adicionarReprocessandoErroLog(ex.getMessage(), receita, "");
                assistente.historicoContaComErro();
            } finally {
                assistente.historicoConta();
            }
        }
        assistente.getAssistenteBarraProgresso().finaliza();
    }

    private void gerarSaldoCreditoReceberParaEstornoReceita(AssistenteReprocessamento assistente) {
        assistente.getAssistenteBarraProgresso().inicializa();
        assistente.getAssistenteBarraProgresso().setMensagem("RECUPERANDO MOVIMENTOS DE ESTORNO RECEITA REALIZADA");
        List<ReceitaORCEstorno> estornosReceita = buscarEstornoReceitaRealizada(assistente);
        assistente.getAssistenteBarraProgresso().setTotal(estornosReceita.size());
        assistente.getHistorico().atualizarTotal(estornosReceita.size());
        for (ReceitaORCEstorno est : estornosReceita) {
            try {
                est = receitaORCEstornoFacade.recuperar(est.getId());
                assistente.getAssistenteBarraProgresso().setMensagem("GERANDO SALDO CRÉDITOS A RECEBER PARA ESTORNO RECEITA REALIZADA NA DATA " + DataUtil.getDataFormatada(est.getDataEstorno()));
                receitaORCEstornoFacade.gerarSaldoDividaAtivaAndCreditoReceber(est);
                assistente.adicionarHistoricoLogSemErro(est, "");
                assistente.historicoContaSemErro();
            } catch (OptimisticLockException optex) {
                throw new ExcecaoNegocioGenerica("Saldo créditos a receber está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
            } catch (Exception ex) {
                assistente.adicionarReprocessandoErroLog(ex.getMessage(), est, "");
                assistente.historicoContaComErro();
            } finally {
                assistente.historicoConta();
            }
        }
    }

    private List<CreditoReceber> buscarCreditosReceber(AssistenteReprocessamento assistente) {
        if (assistente.getQueryReprocessamentoCreditoReceber().isEmpty()) {
            return Lists.newArrayList();
        }
        StringBuilder sql = new StringBuilder();
        sql.append(assistente.getQueryReprocessamentoCreditoReceber());
        Query q = em.createNativeQuery(sql.toString(), CreditoReceber.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        List<CreditoReceber> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return resultado;
        }
    }

    private List<LancamentoReceitaOrc> buscarReceitaRealizada(AssistenteReprocessamento assistente) {
        if (assistente.getQueryReprocessamentoCreditoReceberReceita().isEmpty()) {
            return Lists.newArrayList();
        }
        StringBuilder sql = new StringBuilder();
        sql.append(assistente.getQueryReprocessamentoCreditoReceberReceita());
        Query q = em.createNativeQuery(sql.toString(), LancamentoReceitaOrc.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.setParameter("operacao", OperacaoReceita.RECEITA_CREDITOS_RECEBER_BRUTA.name());
        List<LancamentoReceitaOrc> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return resultado;
        }
    }

    private List<ReceitaORCEstorno> buscarEstornoReceitaRealizada(AssistenteReprocessamento assistente) {
        if (assistente.getQueryReprocessamentoCreditoReceberReceitaEstorno().isEmpty()) {
            return Lists.newArrayList();
        }
        StringBuilder sql = new StringBuilder();
        sql.append(assistente.getQueryReprocessamentoCreditoReceberReceitaEstorno());
        Query q = em.createNativeQuery(sql.toString(), ReceitaORCEstorno.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.setParameter("operacao", OperacaoReceita.RECEITA_CREDITOS_RECEBER_BRUTA.name());
        List<ReceitaORCEstorno> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return resultado;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void salvarHistorico(ReprocessamentoHistorico historico) {
        reprocessamentoHistoricoFacade.salvar(historico);
    }
}
