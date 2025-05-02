/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class RGFAnexo03Item {
    private String descricao;
    private BigDecimal exercicioAnterior;
    private BigDecimal primeiroQuadrimestre;
    private BigDecimal segundoQuadrimestre;
    private BigDecimal terceiroQuadrimestre;

    public RGFAnexo03Item() {
        exercicioAnterior = BigDecimal.ZERO;
        primeiroQuadrimestre = BigDecimal.ZERO;
        segundoQuadrimestre = BigDecimal.ZERO;
        terceiroQuadrimestre = BigDecimal.ZERO;
    }

    public RGFAnexo03Item(String descricao) {
        this.descricao = descricao;
        exercicioAnterior = BigDecimal.ZERO;
        primeiroQuadrimestre = BigDecimal.ZERO;
        segundoQuadrimestre = BigDecimal.ZERO;
        terceiroQuadrimestre = BigDecimal.ZERO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getExercicioAnterior() {
        return exercicioAnterior;
    }

    public void setExercicioAnterior(BigDecimal exercicioAnterior) {
        this.exercicioAnterior = exercicioAnterior;
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
