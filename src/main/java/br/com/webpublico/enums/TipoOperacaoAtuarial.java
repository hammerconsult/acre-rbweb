/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Claudio
 */
public enum TipoOperacaoAtuarial implements EnumComDescricao {
    PROVISAO("Provisão"),
    REVERSAO("Reversão");

    private String descricao;

    TipoOperacaoAtuarial(String descricao) {
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
