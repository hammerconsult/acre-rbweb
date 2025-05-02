/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class LRFAnexo05ItemRelatorioBensDireitoRPPS {
    private String descricao;
    private BigDecimal valorMesAnterior;
    private BigDecimal valorExercicio;
    private BigDecimal valorExercicioAnterior;
    private Integer nivel;

    public LRFAnexo05ItemRelatorioBensDireitoRPPS() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorExercicio() {
        return valorExercicio;
    }

    public void setValorExercicio(BigDecimal valorExercicio) {
        this.valorExercicio = valorExercicio;
    }

    public BigDecimal getValorExercicioAnterior() {
        return valorExercicioAnterior;
    }

    public void setValorExercicioAnterior(BigDecimal valorExercicioAnterior) {
        this.valorExercicioAnterior = valorExercicioAnterior;
    }

    public BigDecimal getValorMesAnterior() {
        return valorMesAnterior;
    }

    public void setValorMesAnterior(BigDecimal valorMesAnterior) {
        this.valorMesAnterior = valorMesAnterior;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
