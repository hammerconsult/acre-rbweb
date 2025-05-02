/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Edi
 */
public enum NumeroInicialContaReceita implements EnumComDescricao {

    CONTA_INICIADA_COM_NOVE("Conta de Receita Iniciada com 9"),
    CONTA_NAO_INICIADA_COM_NOVE("Conta de Receita não Iniciada com 9");
    private String descricao;

    NumeroInicialContaReceita(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
