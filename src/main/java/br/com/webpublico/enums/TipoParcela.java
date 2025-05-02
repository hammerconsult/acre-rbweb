/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author daniel
 */
public enum TipoParcela {

    FIXA("Vencimento Fixo"),
    PERIODICA("Vencimento Periodico");
    private String descricao;

    private TipoParcela(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
