package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum AmbitoCnae implements EnumComDescricao {
    SANITARIO("Sanitário"),
    INFRAESTRUTURA("Infraestrutura"),
    AMBIENTAL("Ambiental");

    private String descricao;

    AmbitoCnae(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
