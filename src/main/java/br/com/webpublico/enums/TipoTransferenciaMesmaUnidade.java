/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author juggernaut
 */
public enum TipoTransferenciaMesmaUnidade implements EnumComDescricao {
    APLICACAO("Aplicação"),
    RESGATE("Resgate"),
    ARRECADACAO("Arrecadação");

    TipoTransferenciaMesmaUnidade(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    @Override
    public String getDescricao() {
        return descricao;
    }
}
