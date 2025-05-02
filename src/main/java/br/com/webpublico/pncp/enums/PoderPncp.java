package br.com.webpublico.pncp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PoderPncp {

    LEGISLATIVO("L"),
    EXECUTIVO("E"),
    JUDICIARIO("J"),
    NAO_SE_APLICA("N");
    private final String descricao;

    PoderPncp(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @JsonValue
    public String toValue() {
        return descricao;
    }
}
