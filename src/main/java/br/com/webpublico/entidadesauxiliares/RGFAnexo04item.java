/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class RGFAnexo04item {

    private String descricao;
    private BigDecimal valorNoQuadrimestre;
    private BigDecimal valorAteQuadrimestre;

    public RGFAnexo04item() {
        valorNoQuadrimestre = BigDecimal.ZERO;
        valorAteQuadrimestre = BigDecimal.ZERO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorAteQuadrimestre() {
        return valorAteQuadrimestre;
    }

    public void setValorAteQuadrimestre(BigDecimal valorAteQuadrimestre) {
        this.valorAteQuadrimestre = valorAteQuadrimestre;
    }

    public BigDecimal getValorNoQuadrimestre() {
        return valorNoQuadrimestre;
    }

    public void setValorNoQuadrimestre(BigDecimal valorNoQuadrimestre) {
        this.valorNoQuadrimestre = valorNoQuadrimestre;
    }
}
