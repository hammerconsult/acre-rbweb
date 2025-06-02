package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;


/**
 * jorge created on 22/01/20.
 */

public enum TipoDamRbtrans implements EnumComDescricao {

    RENOVACAO_PERMISSAO("Renovação"),
    VISTORIA_VEICULO("Vistoria"),
    ISS("ISSQN FIXO");

    private String descricao;


    TipoDamRbtrans(String descricao) {
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





