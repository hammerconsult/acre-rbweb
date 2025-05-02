/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author gustavo
 */
public enum TipoITBI implements EnumComDescricao {

    IMOBILIARIO("Urbano"),
    RURAL("Rural");

    private final String descricao;

    @Override
    public String getDescricao() {
        return descricao;
    }

    TipoITBI(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
