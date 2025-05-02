package br.com.webpublico.pncp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SituacaoContratacaoPncp {

    DIVULGADA_NO_PNCP(1, "Divulgada no PNCP"),
    REVOGADA(2, "Revogada"),
    ANULADA(3, "Anulada"),
    SUSPENSA(4, "Suspensa");

    private final Integer codigo;
    private final String descricao;

    SituacaoContratacaoPncp(Integer codigo, String descricao) {
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
