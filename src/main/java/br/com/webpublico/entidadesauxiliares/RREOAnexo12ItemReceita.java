/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class RREOAnexo12ItemReceita {
    private String descricao;
    private BigDecimal previsaoInicial;
    private BigDecimal previsaoAtualizada;
    private BigDecimal receitaRealizadaAteSemestre;
    private BigDecimal receitaRealizadaPercentual;
    private Integer nivel;

    public RREOAnexo12ItemReceita() {
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

    public BigDecimal getReceitaRealizadaPercentual() {
        return receitaRealizadaPercentual;
    }

    public void setReceitaRealizadaPercentual(BigDecimal receitaRealizadaPercentual) {
        this.receitaRealizadaPercentual = receitaRealizadaPercentual;
    }

    public BigDecimal getReceitaRealizadaAteSemestre() {
        return receitaRealizadaAteSemestre;
    }

    public void setReceitaRealizadaAteSemestre(BigDecimal receitaRealizadaAteSemestre) {
        this.receitaRealizadaAteSemestre = receitaRealizadaAteSemestre;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
