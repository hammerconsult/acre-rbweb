/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Romanini
 */
public enum NaturezaSaldoContaEquivalente {

    NORMAL("Normal"),
    INVERTER("Inverter");
    private String descricao;

    private NaturezaSaldoContaEquivalente(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
