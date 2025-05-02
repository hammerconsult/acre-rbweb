package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.seguranca.service.QueryReprocessamentoService;

import java.util.List;

/**
 * Created by renatoromanini on 03/01/2018.
 */
public class ReprocessamentoSaldoExtraOrcamentario {
    private List<ReceitaExtra> receitas;
    private List<ReceitaExtraEstorno> receitasEstornos;
    private List<PagamentoExtra> despesa;
    private List<PagamentoExtraEstorno> despesaEstorno;
    private List<AjusteDeposito> ajusteDeposito;
    private List<AjusteDepositoEstorno> ajusteDepositoEstorno;
    private String queryReceita;
    private String queryReceitaEstorno;
    private String queryPagamento;
    private String queryPagamentoEstorno;
    private String queryAjuste;
    private String queryAjusteEstorno;

    public ReprocessamentoSaldoExtraOrcamentario() {
        queryAjuste = QueryReprocessamentoService.getService().getReprocessamentoExtraorcamentarioAjuste();
        queryAjusteEstorno = QueryReprocessamentoService.getService().getReprocessamentoExtraorcamentarioAjusteEstorno();
        queryPagamento = QueryReprocessamentoService.getService().getReprocessamentoExtraorcamentarioPagamento();
        queryPagamentoEstorno = QueryReprocessamentoService.getService().getReprocessamentoExtraorcamentarioPagamentoEstorno();
        queryReceita = QueryReprocessamentoService.getService().getReprocessamentoExtraorcamentarioReceita();
        queryReceitaEstorno = QueryReprocessamentoService.getService().getReprocessamentoExtraorcamentarioReceitaEstorno();
    }

    public List<ReceitaExtra> getReceitas() {
        return receitas;
    }

    public void setReceitas(List<ReceitaExtra> receitas) {
        this.receitas = receitas;
    }

    public List<ReceitaExtraEstorno> getReceitasEstornos() {
        return receitasEstornos;
    }

    public void setReceitasEstornos(List<ReceitaExtraEstorno> receitasEstornos) {
        this.receitasEstornos = receitasEstornos;
    }

    public List<PagamentoExtra> getDespesa() {
        return despesa;
    }

    public void setDespesa(List<PagamentoExtra> despesa) {
        this.despesa = despesa;
    }

    public List<PagamentoExtraEstorno> getDespesaEstorno() {
        return despesaEstorno;
    }

    public void setDespesaEstorno(List<PagamentoExtraEstorno> despesaEstorno) {
        this.despesaEstorno = despesaEstorno;
    }

    public List<AjusteDeposito> getAjusteDeposito() {
        return ajusteDeposito;
    }

    public void setAjusteDeposito(List<AjusteDeposito> ajusteDeposito) {
        this.ajusteDeposito = ajusteDeposito;
    }

    public List<AjusteDepositoEstorno> getAjusteDepositoEstorno() {
        return ajusteDepositoEstorno;
    }

    public void setAjusteDepositoEstorno(List<AjusteDepositoEstorno> ajusteDepositoEstorno) {
        this.ajusteDepositoEstorno = ajusteDepositoEstorno;
    }

    public String getQueryReceita() {
        return queryReceita;
    }

    public void setQueryReceita(String queryReceita) {
        this.queryReceita = queryReceita;
    }

    public String getQueryReceitaEstorno() {
        return queryReceitaEstorno;
    }

    public void setQueryReceitaEstorno(String queryReceitaEstorno) {
        this.queryReceitaEstorno = queryReceitaEstorno;
    }

    public String getQueryPagamento() {
        return queryPagamento;
    }

    public void setQueryPagamento(String queryPagamento) {
        this.queryPagamento = queryPagamento;
    }

    public String getQueryPagamentoEstorno() {
        return queryPagamentoEstorno;
    }

    public void setQueryPagamentoEstorno(String queryPagamentoEstorno) {
        this.queryPagamentoEstorno = queryPagamentoEstorno;
    }

    public String getQueryAjuste() {
        return queryAjuste;
    }

    public void setQueryAjuste(String queryAjuste) {
        this.queryAjuste = queryAjuste;
    }

    public String getQueryAjusteEstorno() {
        return queryAjusteEstorno;
    }

    public void setQueryAjusteEstorno(String queryAjusteEstorno) {
        this.queryAjusteEstorno = queryAjusteEstorno;
    }
}
