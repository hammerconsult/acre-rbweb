/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class LRFAnexo06ItemRelatorioDespesa {

    private String descricao;
    private BigDecimal dotacaoAtualizada;
    private BigDecimal despesaLiquidadaNoBimestre;
    private BigDecimal despesaLiquidadaAteOBimestreExercicio;
    private BigDecimal despesaLiquidadaAteOBimestreExercicioAnterior;
    private Integer nivel;

    public LRFAnexo06ItemRelatorioDespesa() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getDespesaLiquidadaAteOBimestreExercicio() {
        return despesaLiquidadaAteOBimestreExercicio;
    }

    public void setDespesaLiquidadaAteOBimestreExercicio(BigDecimal despesaLiquidadaAteOBimestreExercicio) {
        this.despesaLiquidadaAteOBimestreExercicio = despesaLiquidadaAteOBimestreExercicio;
    }

    public BigDecimal getDespesaLiquidadaAteOBimestreExercicioAnterior() {
        return despesaLiquidadaAteOBimestreExercicioAnterior;
    }

    public void setDespesaLiquidadaAteOBimestreExercicioAnterior(BigDecimal despesaLiquidadaAteOBimestreExercicioAnterior) {
        this.despesaLiquidadaAteOBimestreExercicioAnterior = despesaLiquidadaAteOBimestreExercicioAnterior;
    }

    public BigDecimal getDespesaLiquidadaNoBimestre() {
        return despesaLiquidadaNoBimestre;
    }

    public void setDespesaLiquidadaNoBimestre(BigDecimal despesaLiquidadaNoBimestre) {
        this.despesaLiquidadaNoBimestre = despesaLiquidadaNoBimestre;
    }

    public BigDecimal getDotacaoAtualizada() {
        return dotacaoAtualizada;
    }

    public void setDotacaoAtualizada(BigDecimal dotacaoAtualizada) {
        this.dotacaoAtualizada = dotacaoAtualizada;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}

