/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author juggernaut
 */
public class LRFAnexo01ItemRelatorio {

    private String descricao;
    private BigDecimal previsaoInicial;
    private BigDecimal previsaoAtualizada;
    private BigDecimal receitaRealizadaNoBimestre;
    private BigDecimal receitaRealizadaNoBimestrePercentual;
    private BigDecimal receitaRealizadaAteBimestre;
    private BigDecimal receitaRealizadaAteBimestrePercentual;
    private BigDecimal saldoARealizar;
    private Integer nivel;

    public LRFAnexo01ItemRelatorio() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPrevisaoAtualizada() {
        return previsaoAtualizada;
    }

    public void setPrevisaoAtualizada(BigDecimal previsaoAtualizada) {
        this.previsaoAtualizada = previsaoAtualizada;
    }

    public BigDecimal getPrevisaoInicial() {
        return previsaoInicial;
    }

    public void setPrevisaoInicial(BigDecimal previsaoInicial) {
        this.previsaoInicial = previsaoInicial;
    }

    public BigDecimal getReceitaRealizadaAteBimestre() {
        return receitaRealizadaAteBimestre;
    }

    public void setReceitaRealizadaAteBimestre(BigDecimal receitaRealizadaAteBimestre) {
        this.receitaRealizadaAteBimestre = receitaRealizadaAteBimestre;
        if (!this.previsaoAtualizada.equals(BigDecimal.ZERO)) {
            this.receitaRealizadaAteBimestrePercentual = (this.receitaRealizadaAteBimestre.divide(this.previsaoAtualizada, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        } else {
            this.receitaRealizadaAteBimestrePercentual = new BigDecimal(0);
        }
        this.saldoARealizar = (this.previsaoAtualizada.subtract(this.receitaRealizadaAteBimestre));
    }

    public BigDecimal getReceitaRealizadaNoBimestre() {
        return receitaRealizadaNoBimestre;
    }

    public void setReceitaRealizadaNoBimestre(BigDecimal receitaRealizadaNoBimestre) {
        this.receitaRealizadaNoBimestre = receitaRealizadaNoBimestre;
        if (!this.previsaoAtualizada.equals(BigDecimal.ZERO)) {
            this.receitaRealizadaNoBimestrePercentual = (this.receitaRealizadaNoBimestre.divide(this.previsaoAtualizada, 4, RoundingMode.HALF_UP)).multiply(new BigDecimal(100));
        } else {
            this.receitaRealizadaNoBimestrePercentual = new BigDecimal(0);
        }

    }

    public BigDecimal getReceitaRealizadaAteBimestrePercentual() {
        return receitaRealizadaAteBimestrePercentual;
    }

    public void setReceitaRealizadaAteBimestrePercentual(BigDecimal receitaRealizadaAteBimestrePercentual) {
        this.receitaRealizadaAteBimestrePercentual = receitaRealizadaAteBimestrePercentual;
    }

    public BigDecimal getReceitaRealizadaNoBimestrePercentual() {
        return receitaRealizadaNoBimestrePercentual;
    }

    public void setReceitaRealizadaNoBimestrePercentual(BigDecimal receitaRealizadaNoBimestrePercentual) {
        this.receitaRealizadaNoBimestrePercentual = receitaRealizadaNoBimestrePercentual;
    }

    public BigDecimal getSaldoARealizar() {
        return saldoARealizar;
    }

    public void setSaldoARealizar(BigDecimal saldoARealizar) {
        this.saldoARealizar = saldoARealizar;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
