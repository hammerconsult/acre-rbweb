/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Andre
 */
public enum SituacaoContratoCEASA implements EnumComDescricao {

    ALTERADO("Alterado"),
    ATIVO("Ativo"),
    ENCERRADO("Encerrado"),
    RENOVADO("Renovado"),
    RESCINDIDO("Rescindido");
    String descricao;

    SituacaoContratoCEASA(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
