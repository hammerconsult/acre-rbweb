package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author Mateus
 * @since 14/01/2016 08:26
 */
public class DemonstrativoAcao {
    private String acao;
    private String tipoAcao;
    private String fonte;
    private String orgao;
    private String unidade;
    private String unidadeGestora;
    private BigDecimal creditoInicial;
    private BigDecimal creditoAdicional;
    private BigDecimal anulacaoCredito;
    private BigDecimal creditoAtual;
    private BigDecimal valorEmpenho;
    private BigDecimal percentualEmpenho;
    private BigDecimal valorLiquidacao;
    private BigDecimal percentualLiquidacao;
    private BigDecimal valorPagamento;
    private BigDecimal percentualPagamento;

    public DemonstrativoAcao() {
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getTipoAcao() {
        return tipoAcao;
    }

    public void setTipoAcao(String tipoAcao) {
        this.tipoAcao = tipoAcao;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public BigDecimal getCreditoInicial() {
        return creditoInicial;
    }

    public void setCreditoInicial(BigDecimal creditoInicial) {
        this.creditoInicial = creditoInicial;
    }

    public BigDecimal getCreditoAdicional() {
        return creditoAdicional;
    }

    public void setCreditoAdicional(BigDecimal creditoAdicional) {
        this.creditoAdicional = creditoAdicional;
    }

    public BigDecimal getAnulacaoCredito() {
        return anulacaoCredito;
    }

    public void setAnulacaoCredito(BigDecimal anulacaoCredito) {
        this.anulacaoCredito = anulacaoCredito;
    }

    public BigDecimal getCreditoAtual() {
        return creditoAtual;
    }

    public void setCreditoAtual(BigDecimal creditoAtual) {
        this.creditoAtual = creditoAtual;
    }

    public BigDecimal getValorEmpenho() {
        return valorEmpenho;
    }

    public void setValorEmpenho(BigDecimal valorEmpenho) {
        this.valorEmpenho = valorEmpenho;
    }

    public BigDecimal getPercentualEmpenho() {
        return percentualEmpenho;
    }

    public void setPercentualEmpenho(BigDecimal percentualEmpenho) {
        this.percentualEmpenho = percentualEmpenho;
    }

    public BigDecimal getValorLiquidacao() {
        return valorLiquidacao;
    }

    public void setValorLiquidacao(BigDecimal valorLiquidacao) {
        this.valorLiquidacao = valorLiquidacao;
    }

    public BigDecimal getPercentualLiquidacao() {
        return percentualLiquidacao;
    }

    public void setPercentualLiquidacao(BigDecimal percentualLiquidacao) {
        this.percentualLiquidacao = percentualLiquidacao;
    }

    public BigDecimal getValorPagamento() {
        return valorPagamento;
    }

    public void setValorPagamento(BigDecimal valorPagamento) {
        this.valorPagamento = valorPagamento;
    }

    public BigDecimal getPercentualPagamento() {
        return percentualPagamento;
    }

    public void setPercentualPagamento(BigDecimal percentualPagamento) {
        this.percentualPagamento = percentualPagamento;
    }
}
