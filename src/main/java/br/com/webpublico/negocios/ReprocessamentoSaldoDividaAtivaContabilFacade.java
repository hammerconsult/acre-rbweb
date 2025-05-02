package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
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
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mateus on 07/05/2015.
 */
@Stateless
public class ReprocessamentoSaldoDividaAtivaContabilFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoDividaAtivaContabilFacade saldoDividaAtivaContabilFacade;
    @EJB
    private LancamentoReceitaOrcFacade lancamentoReceitaOrcFacade;
    @EJB
    private ReceitaORCEstornoFacade receitaORCEstornoFacade;
    @EJB
    private ReprocessamentoHistoricoFacade reprocessamentoHistoricoFacade;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<AssistenteReprocessamento> reprocessar(AssistenteReprocessamento assistente) {
        AsyncResult<AssistenteReprocessamento> retorno = new AsyncResult<>(assistente);
        assistente.setQueryDividaAtiva(QueryReprocessamentoService.getService().getReprocessamentoDividaAtiva());
        assistente.setQueryReceita(QueryReprocessamentoService.getService().getReprocessamentoDividaAtivaReceita());
        assistente.setQueryEstornoReceita(QueryReprocessamentoService.getService().getReprocessamentoDividaAtivaEstornoReceita());
        assistente.getAssistenteBarraProgresso().inicializa();
        if (assistente.getExcluirSaldos() &&
            !assistente.getQueryDividaAtiva().isEmpty() &&
            !assistente.getQueryReceita().isEmpty() &&
            !assistente.getQueryEstornoReceita().isEmpty()) {
            assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Excluindo Saldos Dívida Ativa...</font> </b>");
            saldoDividaAtivaContabilFacade.excluirSaldosPeriodo(assistente.getDataInicial(), assistente.getDataFinal());

            reprocessarDividaAtiva(assistente);
            reprocessarReceita(assistente);
            reprocessarEstornoReceita(assistente);
        }
        assistente.finalizar();
        salvarHistorico(assistente.getHistorico());
        return retorno;
    }

    private void reprocessarDividaAtiva(AssistenteReprocessamento assistente) {
        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Recuperando Lançamentos de Dívida Ativa...</font> </b>");
        List<DividaAtivaContabil> dividasAtivas = recuperarDividasAtivas(assistente.getDataInicial(), assistente.getDataFinal(), assistente.getQueryDividaAtiva());
        assistente.getAssistenteBarraProgresso().setTotal(dividasAtivas.size());
        assistente.getHistorico().atualizarTotal(dividasAtivas.size());

        Collections.sort(dividasAtivas, new Comparator<DividaAtivaContabil>() {
            @Override
            public int compare(DividaAtivaContabil o1, DividaAtivaContabil o2) {
                return o1.getDataDivida().compareTo(o2.getDataDivida());
            }
        });

        for (DividaAtivaContabil dividaAtivaContabil : dividasAtivas) {
            assistente.getAssistenteBarraProgresso().setMensagem("Gerando Saldo Dívida Ativa na data " + DataUtil.getDataFormatada(dividaAtivaContabil.getDataDivida()));
            try {
                saldoDividaAtivaContabilFacade.gerarSaldoDividaAtiva(dividaAtivaContabil, null);
                assistente.adicionarHistoricoLogSemErro(dividaAtivaContabil, "");
                assistente.historicoContaSemErro();
            } catch (OptimisticLockException optex) {
                throw new ExcecaoNegocioGenerica("Saldo dívida ativa está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
            } catch (Exception ex) {
                assistente.adicionarReprocessandoErroLog(ex.getMessage(), dividaAtivaContabil, "");
                assistente.historicoContaComErro();
            } finally {
                assistente.historicoConta();
            }
        }
        assistente.getAssistenteBarraProgresso().finaliza();
    }

    private void reprocessarReceita(AssistenteReprocessamento assistente) {
        assistente.getAssistenteBarraProgresso().inicializa();
        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Recuperando Lançamentos de Receita...</font> </b>");
        List<LancamentoReceitaOrc> receitas = recuperarLancamentosReceitaOrc(assistente.getDataInicial(), assistente.getDataFinal(), assistente.getQueryReceita());
        assistente.getAssistenteBarraProgresso().setTotal(receitas.size());
        assistente.getHistorico().atualizarTotal(receitas.size());

        Collections.sort(receitas, new Comparator<LancamentoReceitaOrc>() {
            @Override
            public int compare(LancamentoReceitaOrc o1, LancamentoReceitaOrc o2) {
                return o1.getDataLancamento().compareTo(o2.getDataLancamento());
            }
        });

        for (LancamentoReceitaOrc lancamentoReceitaOrc : receitas) {
            assistente.getAssistenteBarraProgresso().setMensagem("Gerando Saldo Dívida Ativa na data " + DataUtil.getDataFormatada(lancamentoReceitaOrc.getDataLancamento()));
            try {
                lancamentoReceitaOrc = lancamentoReceitaOrcFacade.recuperar(lancamentoReceitaOrc.getId());
                lancamentoReceitaOrcFacade.gerarSaldoDividaAtivaAndCreditoReceber(lancamentoReceitaOrc);
                assistente.adicionarHistoricoLogSemErro(lancamentoReceitaOrc, "");
                assistente.historicoContaSemErro();
            } catch (OptimisticLockException optex) {
                throw new ExcecaoNegocioGenerica("Saldo dívida ativa está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
            } catch (Exception ex) {
                assistente.adicionarReprocessandoErroLog(ex.getMessage(), lancamentoReceitaOrc, "");
                assistente.historicoContaComErro();
            } finally {
                assistente.historicoConta();
            }
        }
        assistente.getAssistenteBarraProgresso().finaliza();
    }

    private void reprocessarEstornoReceita(AssistenteReprocessamento assistente) {
        assistente.getAssistenteBarraProgresso().inicializa();
        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Recuperando Lançamentos de Estorno de Receita...</font> </b>");
        List<ReceitaORCEstorno> estornos = recuperarReceitasOrcEstorno(assistente.getDataInicial(), assistente.getDataFinal(), assistente.getQueryEstornoReceita());
        assistente.getAssistenteBarraProgresso().setTotal(estornos.size());
        assistente.getHistorico().atualizarTotal(estornos.size());

        Collections.sort(estornos, new Comparator<ReceitaORCEstorno>() {
            @Override
            public int compare(ReceitaORCEstorno o1, ReceitaORCEstorno o2) {
                return o1.getDataEstorno().compareTo(o2.getDataEstorno());
            }
        });

        for (ReceitaORCEstorno receitaORCEstorno : estornos) {
            assistente.getAssistenteBarraProgresso().setMensagem("Gerando Saldo Dívida Ativa na data " + DataUtil.getDataFormatada(receitaORCEstorno.getDataEstorno()));
            try {
                receitaORCEstorno = receitaORCEstornoFacade.recuperar(receitaORCEstorno.getId());
                receitaORCEstornoFacade.gerarSaldoDividaAtivaAndCreditoReceber(receitaORCEstorno);
                assistente.adicionarHistoricoLogSemErro(receitaORCEstorno, "");
                assistente.historicoContaSemErro();
            } catch (OptimisticLockException optex) {
                throw new ExcecaoNegocioGenerica("Saldo dívida ativa está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
            } catch (Exception ex) {
                assistente.adicionarReprocessandoErroLog(ex.getMessage(), receitaORCEstorno, "");
                assistente.historicoContaComErro();
            } finally {
                assistente.historicoConta();
            }
        }
    }

    private List<DividaAtivaContabil> recuperarDividasAtivas(Date dataInicial, Date dataFinal, String queryReprocessamento) {

        StringBuilder sql = new StringBuilder();
        sql.append(queryReprocessamento);
        Query q = em.createNativeQuery(sql.toString(), DividaAtivaContabil.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));

        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return (List<DividaAtivaContabil>) q.getResultList();
        }
    }

    private List<LancamentoReceitaOrc> recuperarLancamentosReceitaOrc(Date dataInicial, Date dataFinal, String queryReprocessamento) {
        StringBuilder sql = new StringBuilder();
        sql.append(queryReprocessamento);
        Query q = em.createNativeQuery(sql.toString(), LancamentoReceitaOrc.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return (List<LancamentoReceitaOrc>) q.getResultList();
        }
    }

    private List<ReceitaORCEstorno> recuperarReceitasOrcEstorno(Date dataInicial, Date dataFinal, String queryReprocessamento) {
        StringBuilder sql = new StringBuilder();
        sql.append(queryReprocessamento);
        Query q = em.createNativeQuery(sql.toString(), ReceitaORCEstorno.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return (List<ReceitaORCEstorno>) q.getResultList();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void salvarHistorico(ReprocessamentoHistorico historico) {
        reprocessamentoHistoricoFacade.salvar(historico);
    }
}
