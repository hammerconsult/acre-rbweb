package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoEmissaoMarcaFogo implements EnumComDescricao {
    PRIMEIRA_VIA("Primeira Via"),
    SEGUNDA_VIA("Segunda Via");

    private final String descricao;

    TipoEmissaoMarcaFogo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
