/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author GhouL
 */
public enum SituacaoProcessoDebito implements EnumComDescricao {

    EM_ABERTO("Em Aberto"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado"),
    ESTORNADO("Estornado");
    String descricao;

    private SituacaoProcessoDebito(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return this.descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
