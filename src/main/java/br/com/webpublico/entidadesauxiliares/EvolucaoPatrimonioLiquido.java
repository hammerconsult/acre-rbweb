package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Fabio on 10/05/2016.
 */
public class EvolucaoPatrimonioLiquido {

    private String descricao;
    private BigDecimal valorAnoOrigem;
    private BigDecimal percentualAnoOrigem;
    private BigDecimal valorAnoAnterior1;
    private BigDecimal percentualAnoAnterior1;
    private BigDecimal valorAnoAnterior2;
    private BigDecimal percentualAnoAnterior2;
    private BigDecimal valorAnoAnterior3;
    private BigDecimal percentualAnoAnterior3;

    public EvolucaoPatrimonioLiquido() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPercentualAnoOrigem() {
        return percentualAnoOrigem;
    }

    public void setPercentualAnoOrigem(BigDecimal percentualAnoOrigem) {
        this.percentualAnoOrigem = percentualAnoOrigem;
    }

    public BigDecimal getPercentualAnoAnterior1() {
        return percentualAnoAnterior1;
    }

    public void setPercentualAnoAnterior1(BigDecimal percentualAnoAnterior1) {
        this.percentualAnoAnterior1 = percentualAnoAnterior1;
    }

    public BigDecimal getPercentualAnoAnterior2() {
        return percentualAnoAnterior2;
    }

    public void setPercentualAnoAnterior2(BigDecimal percentualAnoAnterior2) {
        this.percentualAnoAnterior2 = percentualAnoAnterior2;
    }
    public BigDecimal getPercentualAnoAnterior3() {
        return percentualAnoAnterior3;
    }

    public void setPercentualAnoAnterior3(BigDecimal percentualAnoAnterior3) {
        this.percentualAnoAnterior3 = percentualAnoAnterior3;
    }

    public BigDecimal getValorAnoOrigem() {
        return valorAnoOrigem;
    }

    public void setValorAnoOrigem(BigDecimal valorAnoOrigem) {
        this.valorAnoOrigem = valorAnoOrigem;
    }

    public BigDecimal getValorAnoAnterior1() {
        return valorAnoAnterior1;
    }

    public void setValorAnoAnterior1(BigDecimal valorAnoAnterior1) {
        this.valorAnoAnterior1 = valorAnoAnterior1;
    }

    public BigDecimal getValorAnoAnterior2() {
        return valorAnoAnterior2;
    }

    public void setValorAnoAnterior2(BigDecimal valorAnoAnterior2) {
        this.valorAnoAnterior2 = valorAnoAnterior2;
    }

    public BigDecimal getValorAnoAnterior3() {
        return valorAnoAnterior3;
    }

    public void setValorAnoAnterior3(BigDecimal valorAnoAnterior3) {
        this.valorAnoAnterior3 = valorAnoAnterior3;
    }
}
