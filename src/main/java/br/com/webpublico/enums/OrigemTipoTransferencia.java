/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author mga
 */
public enum OrigemTipoTransferencia implements EnumComDescricao {

    RECEBIDO("Recebido"),
    CONCEDIDO("Concedido");
    private String descricao;

    OrigemTipoTransferencia(String descricao) {
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
