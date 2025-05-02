package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum NaturezaDocumentoMarcaFogo implements EnumComDescricao {
    PROCESSO("Processo"),
    PROCURADOR("Procurador");

    private final String descricao;

    NaturezaDocumentoMarcaFogo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
