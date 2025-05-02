/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class LRFAnexo06ItemRelatorio {

    private String descricao;
    private BigDecimal valorDezembroExAnterior;
    private BigDecimal valorBimestreAtual;
    private BigDecimal valorBimestreAnterior;
    private Integer nivel;

    public LRFAnexo06ItemRelatorio() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorBimestreAnterior() {
        return valorBimestreAnterior;
    }

    public void setValorBimestreAnterior(BigDecimal valorBimestreAnterior) {
        this.valorBimestreAnterior = valorBimestreAnterior;
    }

    public BigDecimal getValorBimestreAtual() {
        return valorBimestreAtual;
    }

    public void setValorBimestreAtual(BigDecimal valorBimestreAtual) {
        this.valorBimestreAtual = valorBimestreAtual;
    }

    public BigDecimal getValorDezembroExAnterior() {
        return valorDezembroExAnterior;
    }

    public void setValorDezembroExAnterior(BigDecimal valorDezembroExAnterior) {
        this.valorDezembroExAnterior = valorDezembroExAnterior;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
