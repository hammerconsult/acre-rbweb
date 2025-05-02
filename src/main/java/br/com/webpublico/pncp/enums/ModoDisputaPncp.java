package br.com.webpublico.pncp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ModoDisputaPncp {

    ABERTO(1, "Aberto"),
    FECHADO(2, "Fechado"),
    ABERTO_FECHADO(3, "Aberto-Fechado"),
    DISPENSA_COM_DISPUTA(4, "Dispensa Com Disputa"),
    NAO_SE_APLICA(5, "Não se aplica"),
    FECHADO_ABERTO(6, "Fechado-Aberto");

    private final Integer codigo;
    private final String descricao;

    ModoDisputaPncp(Integer codigo, String descricao) {
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
