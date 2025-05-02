package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum LicencaCnae implements EnumComDescricao {
    DEFERIDO("Deferida"),
    DISPENSADO("Dispensado");

    private String descricao;

    LicencaCnae(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
