package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum OpcaoCredor implements EnumComDescricao {

    CONTRIBUINTE("Contribuinte"),
    PROCURADOR("Procurador");

    String descricao;

    OpcaoCredor(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
