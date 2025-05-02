/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author major
 */
public enum TipoRecursoSubConta implements EnumComDescricao {

    RECURSO_TESOURO("Recurso do Tesouro"),
    RECURSO_UNIDADE("Recurso da Unidade"),
    CONVENIO_CONGENERE("Convênio e Congênere"),
    OPERACAO_CREDITO("Operação de Crédito"),
    RECURSO_VINCULADO("Recurso Vinculado");
    private String descricao;

    TipoRecursoSubConta(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
