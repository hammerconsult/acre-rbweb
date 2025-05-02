/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class RREOAnexo06ItemDespesa {

    private String descricao;
    private BigDecimal dotacaoAtualizada;
    private BigDecimal despesaEmpenhadaAteOBimestreExercicio;
    private BigDecimal despesaEmpenhadaAteOBimestreExercicioAnterior;
    private BigDecimal despesaLiquidadaAteOBimestreExercicio;
    private BigDecimal despesaLiquidadaAteOBimestreExercicioAnterior;
    private BigDecimal restoAPagarAteOBimestreExercicio;
    private BigDecimal restoAPagarAteOBimestreExercicioAnterior;
    private Integer nivel;

    public RREOAnexo06ItemDespesa() {
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

    public BigDecimal getDespesaEmpenhadaAteOBimestreExercicio() {
        return despesaEmpenhadaAteOBimestreExercicio;
    }

    public void setDespesaEmpenhadaAteOBimestreExercicio(BigDecimal despesaEmpenhadaAteOBimestreExercicio) {
        this.despesaEmpenhadaAteOBimestreExercicio = despesaEmpenhadaAteOBimestreExercicio;
    }

    public BigDecimal getDespesaEmpenhadaAteOBimestreExercicioAnterior() {
        return despesaEmpenhadaAteOBimestreExercicioAnterior;
    }

    public void setDespesaEmpenhadaAteOBimestreExercicioAnterior(BigDecimal despesaEmpenhadaAteOBimestreExercicioAnterior) {
        this.despesaEmpenhadaAteOBimestreExercicioAnterior = despesaEmpenhadaAteOBimestreExercicioAnterior;
    }

    public BigDecimal getRestoAPagarAteOBimestreExercicio() {
        return restoAPagarAteOBimestreExercicio;
    }

    public void setRestoAPagarAteOBimestreExercicio(BigDecimal restoAPagarAteOBimestreExercicio) {
        this.restoAPagarAteOBimestreExercicio = restoAPagarAteOBimestreExercicio;
    }

    public BigDecimal getRestoAPagarAteOBimestreExercicioAnterior() {
        return restoAPagarAteOBimestreExercicioAnterior;
    }

    public void setRestoAPagarAteOBimestreExercicioAnterior(BigDecimal restoAPagarAteOBimestreExercicioAnterior) {
        this.restoAPagarAteOBimestreExercicioAnterior = restoAPagarAteOBimestreExercicioAnterior;
    }
}

