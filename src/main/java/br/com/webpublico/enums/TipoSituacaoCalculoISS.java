/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 *
 * @author William
 */
public enum TipoSituacaoCalculoISS implements EnumComDescricao {

    LANCADO("Lançado"),
    ESTORNADO("Estornado");
    public String descricao;

    private TipoSituacaoCalculoISS(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
