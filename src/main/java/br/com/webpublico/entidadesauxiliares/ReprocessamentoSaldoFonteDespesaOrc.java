package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.DespesaORC;
import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.HierarquiaOrganizacional;

/**
 * Criado por Mateus
 * Data: 15/12/2016.
 */
public class ReprocessamentoSaldoFonteDespesaOrc {
    private Boolean reprocessarSaldoInicial;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private DespesaORC despesaORC;
    private FonteDespesaORC fonteDespesaORC;
    private String queryDotacao;
    private String querySolicitacoEmpenho;
    private String queryAlteracaoOrcamentaria;
    private String queryEmpenhado;
    private String queryLiquidado;
    private String queryPago;
    private String queryReservaDotacao;
    private String queryReservadoPorLicitacaoNormal;
    private String queryCancelamentoReservaDotacao;
    private String queryReservadoPorLicitacaoEstorno;

    public ReprocessamentoSaldoFonteDespesaOrc() {
        reprocessarSaldoInicial = Boolean.FALSE;
    }

    public Boolean getReprocessarSaldoInicial() {
        return reprocessarSaldoInicial;
    }

    public void setReprocessarSaldoInicial(Boolean reprocessarSaldoInicial) {
        this.reprocessarSaldoInicial = reprocessarSaldoInicial;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public String getQueryDotacao() {
        return queryDotacao;
    }

    public void setQueryDotacao(String queryDotacao) {
        this.queryDotacao = queryDotacao;
    }

    public String getQuerySolicitacoEmpenho() {
        return querySolicitacoEmpenho;
    }

    public void setQuerySolicitacoEmpenho(String querySolicitacoEmpenho) {
        this.querySolicitacoEmpenho = querySolicitacoEmpenho;
    }

    public String getQueryAlteracaoOrcamentaria() {
        return queryAlteracaoOrcamentaria;
    }

    public void setQueryAlteracaoOrcamentaria(String queryAlteracaoOrcamentaria) {
        this.queryAlteracaoOrcamentaria = queryAlteracaoOrcamentaria;
    }

    public String getQueryEmpenhado() {
        return queryEmpenhado;
    }

    public void setQueryEmpenhado(String queryEmpenhado) {
        this.queryEmpenhado = queryEmpenhado;
    }

    public String getQueryLiquidado() {
        return queryLiquidado;
    }

    public void setQueryLiquidado(String queryLiquidado) {
        this.queryLiquidado = queryLiquidado;
    }

    public String getQueryPago() {
        return queryPago;
    }

    public void setQueryPago(String queryPago) {
        this.queryPago = queryPago;
    }

    public String getQueryReservaDotacao() {
        return queryReservaDotacao;
    }

    public void setQueryReservaDotacao(String queryReservaDotacao) {
        this.queryReservaDotacao = queryReservaDotacao;
    }

    public String getQueryReservadoPorLicitacaoNormal() {
        return queryReservadoPorLicitacaoNormal;
    }

    public void setQueryReservadoPorLicitacaoNormal(String queryReservadoPorLicitacaoNormal) {
        this.queryReservadoPorLicitacaoNormal = queryReservadoPorLicitacaoNormal;
    }

    public String getQueryCancelamentoReservaDotacao() {
        return queryCancelamentoReservaDotacao;
    }

    public void setQueryCancelamentoReservaDotacao(String queryCancelamentoReservaDotacao) {
        this.queryCancelamentoReservaDotacao = queryCancelamentoReservaDotacao;
    }

    public String getQueryReservadoPorLicitacaoEstorno() {
        return queryReservadoPorLicitacaoEstorno;
    }

    public void setQueryReservadoPorLicitacaoEstorno(String queryReservadoPorLicitacaoEstorno) {
        this.queryReservadoPorLicitacaoEstorno = queryReservadoPorLicitacaoEstorno;
    }
}
