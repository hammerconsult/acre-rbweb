package br.com.webpublico.pncp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ItemCategoriaPncp {

    BENS_IMOVEIS(1, "Bens Imóveis"),
    BENS_MOVEIS(2, "Bens Móveis"),
    NAO_SE_APLICA(3, "Não se aplica");

    private final Integer codigo;
    private final String descricao;

    ItemCategoriaPncp(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return codigo.toString();
    }

    @JsonValue
    public Integer toValue() {
        return codigo;
    }
}
