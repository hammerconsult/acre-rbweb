package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContaDeDestinacao;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.entidades.ReprocessamentoHistorico;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.entidadesauxiliares.ReprocessamentoSubConta;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.seguranca.service.QueryReprocessamentoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mateus on 02/02/2015.
 */
@Stateless
public class ReprocessamentoSaldoSubContaFacade {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AtualizaEventoContabilFacade atualizaEventoContabilFacade;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<AssistenteReprocessamento> reprocessar(AssistenteReprocessamento assistente) {
        AsyncResult<AssistenteReprocessamento> retorno = new AsyncResult<>(assistente);
        assistente.setQueryReprocessamento(QueryReprocessamentoService.getService().getReprocessamentoFinanceiro());
        assistente.getAssistenteBarraProgresso().inicializa();
        if (!assistente.getQueryReprocessamento().isEmpty()) {
            excluirSaldo(assistente);
        }

        List<ReprocessamentoSubConta> itens = buscarMovimentos(assistente);
        assistente.getAssistenteBarraProgresso().finaliza();

        assistente.getAssistenteBarraProgresso().inicializa();
        assistente.getAssistenteBarraProgresso().setTotal(itens.size());
        assistente.getHistorico().setTotal(itens.size());
        gerarSaldoFinanceiro(assistente, itens);

        assistente.finalizar();
        salvarHistorico(assistente.getHistorico());
        return retorno;
    }

    void excluirSaldo(AssistenteReprocessamento assistente) {
        if (assistente.getExcluirSaldos()) {
            assistente.getAssistenteBarraProgresso().setMensagem("EXCLUINDO SALDOS E MOVIMENTOS FINANCEIROS");
            saldoSubContaFacade.excluirSaldosPeriodo(assistente.getDataInicial(), assistente.getDataFinal(), assistente.getIdsContaFinanceira(), assistente.getContaDeDestinacao());
        }
    }

    private void gerarSaldoFinanceiro(AssistenteReprocessamento assistente, List<ReprocessamentoSubConta> itens) {
        String objetosUtilizados = "";
        for (ReprocessamentoSubConta item : itens) {
            try {
                objetosUtilizados = "Unidade: " + item.getUnidadeOrganizacional() +
                    "; Evento Contábil: " + item.getEventoContabil() +
                    "; Movimentação Financeira: " + item.getTipoMovimento() +
                    "; Conta de Destinação: " + item.getContaDeDestinacao();
                assistente.getAssistenteBarraProgresso().setMensagem("GERANDO SALDO FINANCEIRO PARA O DIA " + DataUtil.getDataFormatada(item.getDataMovimento()));
                saldoSubContaFacade.gerarSaldoFinanceiro(
                    item.getUnidadeOrganizacional(),
                    item.getSubConta(),
                    item.getContaDeDestinacao(),
                    item.getValorDebito().compareTo(BigDecimal.ZERO) > 0 ? item.getValorDebito() : item.getValorCredito(),
                    item.getValorDebito().compareTo(BigDecimal.ZERO) > 0 ? TipoOperacao.DEBITO : TipoOperacao.CREDITO,
                    item.getDataMovimento(),
                    item.getEventoContabil(),
                    item.getHistorico(),
                    item.getTipoMovimento(),
                    item.getUuid(),
                    false);
                assistente.adicionarHistoricoLogSemErro(item.getSubConta(), objetosUtilizados);
                assistente.historicoContaSemErro();
            } catch (OptimisticLockException optex) {
                throw new ExcecaoNegocioGenerica("Saldo financeiro está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
            } catch (Exception ex) {
                assistente.adicionarReprocessandoErroLog(ex.getMessage(), item.getSubConta(), objetosUtilizados);
                assistente.historicoContaComErro();
            } finally {
                assistente.historicoConta();
            }
        }
    }

    public List<ReprocessamentoSubConta> buscarMovimentos(AssistenteReprocessamento assistente) {
        if (assistente.getQueryReprocessamento().isEmpty()) {
            return Lists.newArrayList();
        }
        assistente.getAssistenteBarraProgresso().setMensagem("RECUPERAR MOVIMENTOS FINANCEIROS");
        String sql = sqlMovimentosFinanceiros(assistente).toString();
        return preencherObjetos(sql, assistente);
    }

    public List<ReprocessamentoSubConta> preencherObjetos(String sql, AssistenteReprocessamento assistente) {
        assistente.getAssistenteBarraProgresso().setMensagem("PREENCHENDO OBJETOS PARA REPROCESSAR");

        Query q = em.createNativeQuery(sql);
        q.setParameter("DATAINICIAL", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("DATAFINAL", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.setParameter("EXERCICIO_ID", assistente.getExercicio().getId());
        q.setParameter("tipoAjusteAumentativo", TipoAjusteDisponivel.recuperarListaName(TipoAjusteDisponivel.retornarAumentativo()));
        q.setParameter("tipoAjusteDiminutivo", TipoAjusteDisponivel.recuperarListaName(TipoAjusteDisponivel.retornarDiminutivo()));

        List<ReprocessamentoSubConta> retorno = new ArrayList<>();
        if (!q.getResultList().isEmpty()) {
            assistente.getAssistenteBarraProgresso().setTotal(q.getResultList().size());

            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ReprocessamentoSubConta item = new ReprocessamentoSubConta();
                item.setDataMovimento((Date) obj[0]);
                item.setValorDebito((BigDecimal) obj[1]);
                item.setValorCredito((BigDecimal) obj[2]);
                item.setSubConta(recuperarSubConta(((BigDecimal) obj[3]).longValue()));
                item.setContaDeDestinacao(recuperarContaDeDestinacao(((BigDecimal) obj[4]).longValue()));
                item.setUnidadeOrganizacional(unidadeOrganizacionalFacade.getUnidadePelaSubordinada(((BigDecimal) obj[5]).longValue(), item.getDataMovimento(), item.getDataMovimento()));
                item.setTipoMovimento(MovimentacaoFinanceira.valueOf((String) obj[6]));
                item.setEventoContabil(recuperarEventoContabil(((BigDecimal) obj[7]).longValue()));
                item.setHistorico((String) obj[8]);
                retorno.add(item);
                assistente.getAssistenteBarraProgresso().setProcessados(assistente.getAssistenteBarraProgresso().getProcessados() + 1);
            }
        }
        return retorno;
    }

    public EventoContabil recuperarEventoContabil(Object id) {
        return em.find(EventoContabil.class, id);
    }

    private ContaDeDestinacao recuperarContaDeDestinacao(Object id) {
        return em.find(ContaDeDestinacao.class, id);
    }

    public SubConta recuperarSubConta(Object id) {
        return em.find(SubConta.class, id);
    }

    private StringBuilder sqlMovimentosFinanceiros(AssistenteReprocessamento assistente) {
        StringBuilder sql = new StringBuilder();
        sql.append(assistente.getQueryReprocessamento()
                .replace("$OperacoesReceitaDeducao", OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()))
                .replace("$Filtros", assistente.getFiltro()));
        return sql;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void salvarHistorico(ReprocessamentoHistorico historico) {
        atualizaEventoContabilFacade.salvarObjetoReprocessamento(historico);
    }

    public EntityManager getEm() {
        return em;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
