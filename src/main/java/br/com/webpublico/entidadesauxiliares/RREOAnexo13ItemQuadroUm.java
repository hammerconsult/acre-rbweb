package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 17/08/2014.
 */
public class RREOAnexo13ItemQuadroUm {
    private String descricao;
    private BigDecimal valorExAnterior;
    private BigDecimal registroNoBimestre;
    private BigDecimal registroAteBimestre;
    private BigDecimal saldoAtual;

    public RREOAnexo13ItemQuadroUm() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorExAnterior() {
        return valorExAnterior;
    }

    public void setValorExAnterior(BigDecimal valorExAnterior) {
        this.valorExAnterior = valorExAnterior;
    }

    public BigDecimal getRegistroNoBimestre() {
        return registroNoBimestre;
    }

    public void setRegistroNoBimestre(BigDecimal registroNoBimestre) {
        this.registroNoBimestre = registroNoBimestre;
    }

    public BigDecimal getRegistroAteBimestre() {
        return registroAteBimestre;
    }

    public void setRegistroAteBimestre(BigDecimal registroAteBimestre) {
        this.registroAteBimestre = registroAteBimestre;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }
}
