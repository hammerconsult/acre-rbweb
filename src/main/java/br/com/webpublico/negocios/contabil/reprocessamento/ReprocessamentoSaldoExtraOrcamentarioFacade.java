package br.com.webpublico.negocios.contabil.reprocessamento;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SaldoExtraorcamentarioFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by renatoromanini on 03/01/2018.
 */
@Stateless
public class ReprocessamentoSaldoExtraOrcamentarioFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoExtraorcamentarioFacade saldoExtraorcamentarioFacade;
    @EJB
    private ReprocessamentoHistoricoFacade reprocessamentoHistoricoFacade;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<AssistenteReprocessamento> reprocessar(AssistenteReprocessamento assistente) {
        AsyncResult<AssistenteReprocessamento> retorno = new AsyncResult<>(assistente);
        assistente.getAssistenteBarraProgresso().inicializa();
        if (!assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryPagamentoEstorno().isEmpty() &&
            !assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryPagamento().isEmpty() &&
            !assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryReceitaEstorno().isEmpty() &&
            !assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryReceita().isEmpty() &&
            !assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryAjuste().isEmpty() &&
            !assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryAjusteEstorno().isEmpty()) {
            excluirMovimentos(assistente);
        }
        recuperarMovimentos(assistente);
        gerarSaldo(assistente);
        assistente.finalizar();
        salvarHistorico(assistente.getHistorico());
        return retorno;
    }

    private void excluirMovimentos(AssistenteReprocessamento assistente) {
        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Excluindo Saldos...</font> </b>");
        saldoExtraorcamentarioFacade.excluirSaldosPeriodo(assistente.getDataInicial(), assistente.getDataFinal());
    }

    public void recuperarMovimentos(AssistenteReprocessamento assistente) {
        assistente.getReprocessamentoSaldoExtraOrcamentario().setReceitas(saldoExtraorcamentarioFacade.getReceitaExtra(assistente));
        assistente.getReprocessamentoSaldoExtraOrcamentario().setReceitasEstornos(saldoExtraorcamentarioFacade.getReceitaExtraEstorno(assistente));
        assistente.getReprocessamentoSaldoExtraOrcamentario().setDespesa(saldoExtraorcamentarioFacade.getPagamentoExtra(assistente));
        assistente.getReprocessamentoSaldoExtraOrcamentario().setDespesaEstorno(saldoExtraorcamentarioFacade.getPagamentoExtraEstorno(assistente));
        assistente.getReprocessamentoSaldoExtraOrcamentario().setAjusteDeposito(saldoExtraorcamentarioFacade.getAjusteDepositoNormal(assistente));
        assistente.getReprocessamentoSaldoExtraOrcamentario().setAjusteDepositoEstorno(saldoExtraorcamentarioFacade.getAjusteDepositoEstorno(assistente));
        Integer total = assistente.getReprocessamentoSaldoExtraOrcamentario().getReceitas().size() +
            assistente.getReprocessamentoSaldoExtraOrcamentario().getReceitasEstornos().size() +
            assistente.getReprocessamentoSaldoExtraOrcamentario().getDespesa().size() +
            assistente.getReprocessamentoSaldoExtraOrcamentario().getDespesaEstorno().size() +
            assistente.getReprocessamentoSaldoExtraOrcamentario().getAjusteDeposito().size() +
            assistente.getReprocessamentoSaldoExtraOrcamentario().getAjusteDepositoEstorno().size();
        assistente.setTotal(total);
    }

    public void gerarSaldo(AssistenteReprocessamento assistente) {
        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Gerando Saldo de Receitas Extra...</font> </b>");
        for (ReceitaExtra entity : assistente.getReprocessamentoSaldoExtraOrcamentario().getReceitas()) {
            gerarSaldoAtualizandoHistorticoEAssistente(assistente, entity, entity.getDataReceita(), TipoOperacao.CREDITO, entity.getValor(), entity.getContaExtraorcamentaria(), entity.getContaDeDestinacao(), entity.getUnidadeOrganizacional());
        }
        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Gerando Saldo de Receitas Extra Estorno...</font> </b>");
        for (ReceitaExtraEstorno entity : assistente.getReprocessamentoSaldoExtraOrcamentario().getReceitasEstornos()) {
            gerarSaldoAtualizandoHistorticoEAssistente(assistente, entity, entity.getDataEstorno(), TipoOperacao.DEBITO, entity.getValor(), entity.getReceitaExtra().getContaExtraorcamentaria(), entity.getReceitaExtra().getContaDeDestinacao(), entity.getReceitaExtra().getUnidadeOrganizacional());
        }
        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Gerando Saldo de Despesa Extra...</font> </b>");
        for (PagamentoExtra entity : assistente.getReprocessamentoSaldoExtraOrcamentario().getDespesa()) {
            gerarSaldoAtualizandoHistorticoEAssistente(assistente, entity, entity.getDataPagto(), TipoOperacao.DEBITO, entity.getValor(), entity.getContaExtraorcamentaria(), entity.getContaDeDestinacao(), entity.getUnidadeOrganizacional());
        }
        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Gerando Saldo de Despesa Extra Estorno...</font> </b>");
        for (PagamentoExtraEstorno entity : assistente.getReprocessamentoSaldoExtraOrcamentario().getDespesaEstorno()) {
            gerarSaldoAtualizandoHistorticoEAssistente(assistente, entity, entity.getDataEstorno(), TipoOperacao.CREDITO, entity.getValor(), entity.getPagamentoExtra().getContaExtraorcamentaria(), entity.getPagamentoExtra().getContaDeDestinacao(), entity.getPagamentoExtra().getUnidadeOrganizacional());
        }
        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Gerando Saldo De Ajuste em Depósito...</font> </b>");
        for (AjusteDeposito entity : assistente.getReprocessamentoSaldoExtraOrcamentario().getAjusteDeposito()) {
            if (entity.isAjusteDiminutivo()) {
                gerarSaldoAtualizandoHistorticoEAssistente(assistente, entity, entity.getDataAjuste(), TipoOperacao.CREDITO, entity.getValor(), entity.getContaExtraorcamentaria(), entity.getContaDeDestinacao(), entity.getUnidadeOrganizacional());
            } else {
                gerarSaldoAtualizandoHistorticoEAssistente(assistente, entity, entity.getDataAjuste(), TipoOperacao.DEBITO, entity.getValor(), entity.getContaExtraorcamentaria(), entity.getContaDeDestinacao(), entity.getUnidadeOrganizacional());
            }
        }
        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Gerando Saldo De Ajuste em Depósito Estorno...</font> </b>");
        for (AjusteDepositoEstorno entity : assistente.getReprocessamentoSaldoExtraOrcamentario().getAjusteDepositoEstorno()) {
            if (entity.getAjusteDeposito().isAjusteDiminutivo()) {
                gerarSaldoAtualizandoHistorticoEAssistente(assistente, entity, entity.getDataEstorno(), TipoOperacao.DEBITO, entity.getValor(), entity.getAjusteDeposito().getContaExtraorcamentaria(), entity.getAjusteDeposito().getContaDeDestinacao(), entity.getUnidadeOrganizacional());
            } else {
                gerarSaldoAtualizandoHistorticoEAssistente(assistente, entity, entity.getDataEstorno(), TipoOperacao.CREDITO, entity.getValor(), entity.getAjusteDeposito().getContaExtraorcamentaria(), entity.getAjusteDeposito().getContaDeDestinacao(), entity.getUnidadeOrganizacional());
            }
        }
    }

    private void gerarSaldoAtualizandoHistorticoEAssistente(AssistenteReprocessamento assistente, Object objetoReferencia, Date data, TipoOperacao tipo, BigDecimal valor, Conta contaExtra, ContaDeDestinacao contaDeDestinacao, UnidadeOrganizacional uniOrg) {
        String objetosUtilizados = "";
        try {
            objetosUtilizados = "Data: " + DataUtil.getDataFormatada(data) +
                "; Operação: " + tipo.toString() +
                "; Valor: " + Util.formatarValor(valor) +
                "; Conta Extra: " + contaExtra.toString() +
                "; Conta Destinação: " + contaDeDestinacao.toString() +
                "; Unidade: " + uniOrg.toString();
            saldoExtraorcamentarioFacade.gerarSaldoExtraorcamentario(data, tipo, valor, contaExtra, contaDeDestinacao, uniOrg, false);
            assistente.adicionarHistoricoLogSemErro(objetoReferencia, objetosUtilizados);
            assistente.historicoContaSemErro();
        } catch (OptimisticLockException optex) {
            throw new ExcecaoNegocioGenerica("Saldo extraorçamentário está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
        } catch (Exception ex) {
            assistente.adicionarReprocessandoErroLog(ex.getMessage(), objetoReferencia, objetosUtilizados);
            assistente.historicoContaComErro();
        } finally {
            assistente.historicoConta();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void salvarHistorico(ReprocessamentoHistorico historico) {
        reprocessamentoHistoricoFacade.salvarNovo(historico);
    }
}
