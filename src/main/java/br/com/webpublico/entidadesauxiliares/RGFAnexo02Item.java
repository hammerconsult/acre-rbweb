package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 29/08/2014.
 */
public class RGFAnexo02Item {
    private String descricao;
    private BigDecimal saldoAnterior;
    private BigDecimal primeiroQuadrimestre;
    private BigDecimal segundoQuadrimestre;
    private BigDecimal terceiroQuadrimestre;

    public RGFAnexo02Item() {
        saldoAnterior = BigDecimal.ZERO;
        primeiroQuadrimestre = BigDecimal.ZERO;
        segundoQuadrimestre = BigDecimal.ZERO;
        terceiroQuadrimestre = BigDecimal.ZERO;
    }

    public RGFAnexo02Item(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getPrimeiroQuadrimestre() {
        return primeiroQuadrimestre;
    }

    public void setPrimeiroQuadrimestre(BigDecimal primeiroQuadrimestre) {
        this.primeiroQuadrimestre = primeiroQuadrimestre;
    }

    public BigDecimal getSegundoQuadrimestre() {
        return segundoQuadrimestre;
    }

    public void setSegundoQuadrimestre(BigDecimal segundoQuadrimestre) {
        this.segundoQuadrimestre = segundoQuadrimestre;
    }

    public BigDecimal getTerceiroQuadrimestre() {
        return terceiroQuadrimestre;
    }

    public void setTerceiroQuadrimestre(BigDecimal terceiroQuadrimestre) {
        this.terceiroQuadrimestre = terceiroQuadrimestre;
    }
}
