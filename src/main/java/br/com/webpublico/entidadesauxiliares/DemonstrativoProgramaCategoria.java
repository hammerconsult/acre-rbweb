package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class DemonstrativoProgramaCategoria {

    private String orgao;
    private String unidade;
    private String fonteDeRecurso;
    private String programa;
    private BigDecimal dotacaoInicial;
    private BigDecimal creditoAtual;
    private BigDecimal empenhadoCorrente;
    private BigDecimal empenhadoCapital;
    private BigDecimal liquidadoCorrente;
    private BigDecimal liquidadoCapital;
    private BigDecimal pagoCorrente;
    private BigDecimal pagoCapital;
    private BigDecimal saldoCorrente;
    private BigDecimal saldoCapital;

    public DemonstrativoProgramaCategoria() {
        dotacaoInicial = BigDecimal.ZERO;
        creditoAtual = BigDecimal.ZERO;
        empenhadoCorrente = BigDecimal.ZERO;
        empenhadoCapital = BigDecimal.ZERO;
        liquidadoCorrente = BigDecimal.ZERO;
        liquidadoCapital = BigDecimal.ZERO;
        pagoCorrente = BigDecimal.ZERO;
        pagoCapital = BigDecimal.ZERO;
        saldoCorrente = BigDecimal.ZERO;
        saldoCapital = BigDecimal.ZERO;
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

    public String getFonteDeRecurso() {
        return fonteDeRecurso;
    }

    public void setFonteDeRecurso(String fonteDeRecurso) {
        this.fonteDeRecurso = fonteDeRecurso;
    }

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }

    public BigDecimal getDotacaoInicial() {
        return dotacaoInicial;
    }

    public void setDotacaoInicial(BigDecimal dotacaoInicial) {
        this.dotacaoInicial = dotacaoInicial;
    }

    public BigDecimal getCreditoAtual() {
        return creditoAtual;
    }

    public void setCreditoAtual(BigDecimal creditoAtual) {
        this.creditoAtual = creditoAtual;
    }

    public BigDecimal getEmpenhadoCorrente() {
        return empenhadoCorrente;
    }

    public void setEmpenhadoCorrente(BigDecimal empenhadoCorrente) {
        this.empenhadoCorrente = empenhadoCorrente;
    }

    public BigDecimal getEmpenhadoCapital() {
        return empenhadoCapital;
    }

    public void setEmpenhadoCapital(BigDecimal empenhadoCapital) {
        this.empenhadoCapital = empenhadoCapital;
    }

    public BigDecimal getLiquidadoCorrente() {
        return liquidadoCorrente;
    }

    public void setLiquidadoCorrente(BigDecimal liquidadoCorrente) {
        this.liquidadoCorrente = liquidadoCorrente;
    }

    public BigDecimal getLiquidadoCapital() {
        return liquidadoCapital;
    }

    public void setLiquidadoCapital(BigDecimal liquidadoCapital) {
        this.liquidadoCapital = liquidadoCapital;
    }

    public BigDecimal getPagoCorrente() {
        return pagoCorrente;
    }

    public void setPagoCorrente(BigDecimal pagoCorrente) {
        this.pagoCorrente = pagoCorrente;
    }

    public BigDecimal getPagoCapital() {
        return pagoCapital;
    }

    public void setPagoCapital(BigDecimal pagoCapital) {
        this.pagoCapital = pagoCapital;
    }

    public BigDecimal getSaldoCorrente() {
        return saldoCorrente;
    }

    public void setSaldoCorrente(BigDecimal saldoCorrente) {
        this.saldoCorrente = saldoCorrente;
    }

    public BigDecimal getSaldoCapital() {
        return saldoCapital;
    }

    public void setSaldoCapital(BigDecimal saldoCapital) {
        this.saldoCapital = saldoCapital;
    }
}
