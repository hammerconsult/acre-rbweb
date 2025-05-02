package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 24/07/2015.
 */
public class DetalhamentoLiquidacao {
    private String orgao;
    private String unidade;
    private String unidadeGestora;
    private String conta;
    private String fonteDeRecursos;
    private BigDecimal valorMes1;
    private BigDecimal valorMes2;
    private BigDecimal valorMes3;
    private BigDecimal valorMes4;
    private BigDecimal valorMes5;
    private BigDecimal valorMes6;
    private BigDecimal valorMes7;
    private BigDecimal valorMes8;
    private BigDecimal valorMes9;
    private BigDecimal valorMes10;
    private BigDecimal valorMes11;
    private BigDecimal valorMes12;
    private BigDecimal total;

    public DetalhamentoLiquidacao() {
        valorMes1 = BigDecimal.ZERO;
        valorMes2 = BigDecimal.ZERO;
        valorMes3 = BigDecimal.ZERO;
        valorMes4 = BigDecimal.ZERO;
        valorMes5 = BigDecimal.ZERO;
        valorMes6 = BigDecimal.ZERO;
        valorMes7 = BigDecimal.ZERO;
        valorMes8 = BigDecimal.ZERO;
        valorMes9 = BigDecimal.ZERO;
        valorMes10 = BigDecimal.ZERO;
        valorMes11 = BigDecimal.ZERO;
        valorMes12 = BigDecimal.ZERO;
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

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(String fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public BigDecimal getValorMes1() {
        return valorMes1;
    }

    public void setValorMes1(BigDecimal valorMes1) {
        this.valorMes1 = valorMes1;
    }

    public BigDecimal getValorMes2() {
        return valorMes2;
    }

    public void setValorMes2(BigDecimal valorMes2) {
        this.valorMes2 = valorMes2;
    }

    public BigDecimal getValorMes3() {
        return valorMes3;
    }

    public void setValorMes3(BigDecimal valorMes3) {
        this.valorMes3 = valorMes3;
    }

    public BigDecimal getValorMes4() {
        return valorMes4;
    }

    public void setValorMes4(BigDecimal valorMes4) {
        this.valorMes4 = valorMes4;
    }

    public BigDecimal getValorMes5() {
        return valorMes5;
    }

    public void setValorMes5(BigDecimal valorMes5) {
        this.valorMes5 = valorMes5;
    }

    public BigDecimal getValorMes6() {
        return valorMes6;
    }

    public void setValorMes6(BigDecimal valorMes6) {
        this.valorMes6 = valorMes6;
    }

    public BigDecimal getValorMes7() {
        return valorMes7;
    }

    public void setValorMes7(BigDecimal valorMes7) {
        this.valorMes7 = valorMes7;
    }

    public BigDecimal getValorMes8() {
        return valorMes8;
    }

    public void setValorMes8(BigDecimal valorMes8) {
        this.valorMes8 = valorMes8;
    }

    public BigDecimal getValorMes9() {
        return valorMes9;
    }

    public void setValorMes9(BigDecimal valorMes9) {
        this.valorMes9 = valorMes9;
    }

    public BigDecimal getValorMes10() {
        return valorMes10;
    }

    public void setValorMes10(BigDecimal valorMes10) {
        this.valorMes10 = valorMes10;
    }

    public BigDecimal getValorMes11() {
        return valorMes11;
    }

    public void setValorMes11(BigDecimal valorMes11) {
        this.valorMes11 = valorMes11;
    }

    public BigDecimal getValorMes12() {
        return valorMes12;
    }

    public void setValorMes12(BigDecimal valorMes12) {
        this.valorMes12 = valorMes12;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
