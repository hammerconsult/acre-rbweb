/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Heinz
 */
public enum SituacaoVistoria implements EnumComDescricao {
    EM_ANDAMENTO("1 - Em andamento"),
    FINALIZADA("2 - Finalizada"),
    CANCELADA("3 - Cancelada"),
    PARALIZADA("4 - Paralizada"),
    OUTROS("9 - Outros");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private SituacaoVistoria(String descricao) {
        this.descricao = descricao;
    }

}
