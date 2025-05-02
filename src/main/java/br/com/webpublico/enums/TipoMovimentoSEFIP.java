/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author boy
 */
public enum TipoMovimentoSEFIP implements EnumComDescricao {

    AFASTAMENTO_SEFIP("Afastamento"),
    RETORNO_SEFIP("Retorno"),
    RESCISAO("Rescisão");
    private String descricao;

    private TipoMovimentoSEFIP(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
