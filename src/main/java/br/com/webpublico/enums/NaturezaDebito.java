/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author venon
 */
public enum NaturezaDebito implements EnumComDescricao {

    COMUM("Comum"),
    ALIMENTAR("Alimentar");
    private String descricao;

    NaturezaDebito(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
