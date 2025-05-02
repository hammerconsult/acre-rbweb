/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class LRFAnexo05ItemRelatorioAporte {
    private String descricao;
    private BigDecimal previsaoInicial;
    private BigDecimal previsaoAtualizada;
    private BigDecimal receitaRealizadaNoBimestre;
    private BigDecimal receitaRealizadaAteOBimestreExercicio;
    private BigDecimal receitaRealizadaAteOBimestreExercicioAnterior;
    private Integer nivel;

    public LRFAnexo05ItemRelatorioAporte() {
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

    public BigDecimal getReceitaRealizadaAteOBimestreExercicio() {
        return receitaRealizadaAteOBimestreExercicio;
    }

    public void setReceitaRealizadaAteOBimestreExercicio(BigDecimal receitaRealizadaAteOBimestreExercicio) {
        this.receitaRealizadaAteOBimestreExercicio = receitaRealizadaAteOBimestreExercicio;
    }

    public BigDecimal getReceitaRealizadaAteOBimestreExercicioAnterior() {
        return receitaRealizadaAteOBimestreExercicioAnterior;
    }

    public void setReceitaRealizadaAteOBimestreExercicioAnterior(BigDecimal receitaRealizadaAteOBimestreExercicioAnterior) {
        this.receitaRealizadaAteOBimestreExercicioAnterior = receitaRealizadaAteOBimestreExercicioAnterior;
    }

    public BigDecimal getReceitaRealizadaNoBimestre() {
        return receitaRealizadaNoBimestre;
    }

    public void setReceitaRealizadaNoBimestre(BigDecimal receitaRealizadaNoBimestre) {
        this.receitaRealizadaNoBimestre = receitaRealizadaNoBimestre;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
