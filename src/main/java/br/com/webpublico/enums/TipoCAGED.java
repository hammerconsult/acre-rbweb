/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author andre
 */
public enum TipoCAGED implements EnumComDescricao {

    ADMISSAO("Admissão"),
    DEMISSAO("Demissão");
    private String descricao;

    private TipoCAGED(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}

