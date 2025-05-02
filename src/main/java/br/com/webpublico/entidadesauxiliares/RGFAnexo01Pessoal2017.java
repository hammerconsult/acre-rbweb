/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class RGFAnexo01Pessoal2017 {

    private String descricao;
    private BigDecimal liquidacao;
    private BigDecimal inscritasRestos;

    public RGFAnexo01Pessoal2017() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getInscritasRestos() {
        return inscritasRestos;
    }

    public void setInscritasRestos(BigDecimal inscritasRestos) {
        this.inscritasRestos = inscritasRestos;
    }

    public BigDecimal getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(BigDecimal liquidacao) {
        this.liquidacao = liquidacao;
    }
}
