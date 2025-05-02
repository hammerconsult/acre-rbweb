/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum PatrimonioDoLote implements EnumComDescricao {

    PARTICULAR("1-Particular"),
    MUNICIPAL("2-Municipal"),
    ESTADUAL("3-Estadual"),
    FEDERAL("4-Federal"),
    RELIGIOSO("5-Religioso"),
    SEM_FINS_LUCRATIVOS("6-Entidade sem Fins Lucrativos");
    private String descricao;

    private PatrimonioDoLote(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

}
