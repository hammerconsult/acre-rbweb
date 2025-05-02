/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Claudio
 */
public enum TipoPlano implements EnumComDescricao {
    FINANCEIRO("Financeiro"),
    PREVIDENCIARIO("Previdênciário");

    private String descricao;

    TipoPlano(String descricao) {
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
