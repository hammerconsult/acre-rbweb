package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoSimNao implements EnumComDescricao {
    SIM("Sim"),
    NAO("Não");

    private String descricao;

    TipoSimNao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
