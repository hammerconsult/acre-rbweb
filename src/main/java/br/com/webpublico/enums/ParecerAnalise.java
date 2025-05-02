/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author André Gustavo
 */
public enum ParecerAnalise implements EnumComDescricao {
    DEFERIDO("Deferido"),
    INDEFERIDO("Indeferido");
    private String descricao;

    ParecerAnalise(String descricao) {
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
