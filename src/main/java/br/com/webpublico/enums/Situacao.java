package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 30/12/13
 * Time: 09:24
 * To change this template use File | Settings | File Templates.
 */
public enum Situacao implements EnumComDescricao {

    ATIVO("Ativo"),
    INATIVO("Inativo");

    private String descricao;

    Situacao(String descricao) {
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
