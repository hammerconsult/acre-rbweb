/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author andre
 */
public enum StatusCompetencia implements EnumComDescricao {
    ABERTA("Aberta"),
    EFETIVADA("Efetivada"),
    FECHADA("Fechada");

    private String descricao;

    private StatusCompetencia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
