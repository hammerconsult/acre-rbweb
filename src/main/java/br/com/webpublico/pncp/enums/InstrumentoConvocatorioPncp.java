package br.com.webpublico.pncp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum InstrumentoConvocatorioPncp {

    EDITAL(1, "Edital"),
    AVISO_CONTRATACAO_DIRETA(2, "Aviso de Contratação Direta"),
    ATO_AUTORIZA_CONTRATACAO_DIRETA(3, "Ato que autoriza a Contratação Direta");

    private final Integer codigo;
    private final String descricao;

    InstrumentoConvocatorioPncp(Integer codigo, String descricao) {
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
