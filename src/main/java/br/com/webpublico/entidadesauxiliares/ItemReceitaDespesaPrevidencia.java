/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author wiplash
 */
public class ItemReceitaDespesaPrevidencia {

    private String descricao;
    private BigDecimal valorExercicioMenosDois;
    private BigDecimal valorExercicioMenosTres;
    private BigDecimal valorExercicioMenosQuatro;

    public ItemReceitaDespesaPrevidencia() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorExercicioMenosDois() {
        return valorExercicioMenosDois;
    }

    public void setValorExercicioMenosDois(BigDecimal valorExercicioMenosDois) {
        this.valorExercicioMenosDois = valorExercicioMenosDois;
    }

    public BigDecimal getValorExercicioMenosTres() {
        return valorExercicioMenosTres;
    }

    public void setValorExercicioMenosTres(BigDecimal valorExercicioMenosTres) {
        this.valorExercicioMenosTres = valorExercicioMenosTres;
    }

    public BigDecimal getValorExercicioMenosQuatro() {
        return valorExercicioMenosQuatro;
    }

    public void setValorExercicioMenosQuatro(BigDecimal valorExercicioMenosQuatro) {
        this.valorExercicioMenosQuatro = valorExercicioMenosQuatro;
    }
}
