package br.com.webpublico.pncp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EsferaPncp {

    FEDERAL("F"),
    ESTADUAL("E"),
    MUNICIPAL("M"),
    DISTRITAL("D"),
    NAO_SE_APLICA("N");

    private final String descricao;

    EsferaPncp(String descricao) {
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
