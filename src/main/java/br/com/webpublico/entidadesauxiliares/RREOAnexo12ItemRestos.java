/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class RREOAnexo12ItemRestos {
    private String descricao;
    private BigDecimal inscritosExAnteriores;
    private BigDecimal cancelados;
    private Integer nivel;

    public RREOAnexo12ItemRestos() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getCancelados() {
        return cancelados;
    }

    public void setCancelados(BigDecimal cancelados) {
        this.cancelados = cancelados;
    }

    public BigDecimal getInscritosExAnteriores() {
        return inscritosExAnteriores;
    }

    public void setInscritosExAnteriores(BigDecimal inscritosExAnteriores) {
        this.inscritosExAnteriores = inscritosExAnteriores;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
