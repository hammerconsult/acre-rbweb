/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class RREOAnexo05ItemResultadoNominal {

    private String descricao;
    private BigDecimal noBimestre;
    private BigDecimal ateOBimestre;
    private Integer nivel;

    public RREOAnexo05ItemResultadoNominal() {
    }

    public BigDecimal getAteOBimestre() {
        return ateOBimestre;
    }

    public void setAteOBimestre(BigDecimal ateOBimestre) {
        this.ateOBimestre = ateOBimestre;
    }

    public BigDecimal getNoBimestre() {
        return noBimestre;
    }

    public void setNoBimestre(BigDecimal noBimestre) {
        this.noBimestre = noBimestre;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
