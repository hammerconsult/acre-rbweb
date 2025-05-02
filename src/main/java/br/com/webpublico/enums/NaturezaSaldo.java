/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Renato Romanini
 */
public enum NaturezaSaldo implements EnumComDescricao {
    DEVEDOR("Devedor"),
    CREDOR("Credor"),
    QUALQUER("Qualquer");
    private String descricao;

    NaturezaSaldo(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
