package br.com.webpublico.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EsferaGovernamental {

    FEDERAL("F"),
    ESTADUAL("E"),
    MUNICIPAL("M"),
    DISTRITAL("D"),
    NAO_SE_APLICA("N");

    private String descricao;

    EsferaGovernamental(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
