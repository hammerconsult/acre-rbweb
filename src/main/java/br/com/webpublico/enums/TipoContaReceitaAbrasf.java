package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoContaReceitaAbrasf implements EnumComDescricao {
    ICMS("ICMS"),
    IPVA("IPVA"),
    FPM("FPM"),
    OUTROS("Outros");

    private String descricao;

    TipoContaReceitaAbrasf(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
