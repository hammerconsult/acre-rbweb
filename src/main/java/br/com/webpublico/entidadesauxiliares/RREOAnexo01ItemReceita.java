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
public class RREOAnexo01ItemReceita {

    private String descricao;
    private String nome;
    private BigDecimal previsaoInicial;
    private BigDecimal previsaoAtualizada;
    private BigDecimal receitaRealizadaNoBimestre;
    private BigDecimal receitaRealizadaNoBimestrePercentual;
    private BigDecimal receitaRealizadaAteBimestre;
    private BigDecimal receitaRealizadaAteBimestrePercentual;
    private BigDecimal saldoARealizar;
    private Integer nivel;
    private Integer numeroLinhaNoXLS;

    public RREOAnexo01ItemReceita() {
        previsaoInicial = BigDecimal.ZERO;
        previsaoAtualizada = BigDecimal.ZERO;
        receitaRealizadaAteBimestre = BigDecimal.ZERO;
        receitaRealizadaAteBimestrePercentual = BigDecimal.ZERO;
        receitaRealizadaNoBimestre = BigDecimal.ZERO;
        receitaRealizadaNoBimestrePercentual = BigDecimal.ZERO;
        saldoARealizar = BigDecimal.ZERO;
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
        if (this.previsaoAtualizada.compareTo(BigDecimal.ZERO) != 0) {
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
        if (this.previsaoAtualizada.compareTo(BigDecimal.ZERO) != 0) {
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getNumeroLinhaNoXLS() {
        return numeroLinhaNoXLS;
    }

    public void setNumeroLinhaNoXLS(Integer numeroLinhaNoXLS) {
        this.numeroLinhaNoXLS = numeroLinhaNoXLS;
    }
}
