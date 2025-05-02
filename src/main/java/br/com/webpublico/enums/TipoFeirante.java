package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoFeirante implements EnumComDescricao {

    LIDER("Líder"),
    FEIRANTE("Feirante");
    private String descricao;

    TipoFeirante(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
