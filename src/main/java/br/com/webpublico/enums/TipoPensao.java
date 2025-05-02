/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author andre
 */
public enum TipoPensao implements EnumComDescricao {

    VITALICIA("Vitalícia"),
    TEMPORARIA("Temporária"),
    VITALICIA_INVALIDEZ("Vitalícia - Invalidez"),
    TEMPORARIA_INVALIDEZ("Temporária – Invalidez");
    private String descricao;

    private TipoPensao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
