/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Edi
 */
public enum TipoConfigAlteracaoOrc implements EnumComDescricao {

    SUPLEMENTAR("Crédito Adicional"),
    ANULACAO("Anulação de Crédito"),
    RECEITA("Previsão Adicional");
    private String descricao;

    TipoConfigAlteracaoOrc(String descricao) {
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
