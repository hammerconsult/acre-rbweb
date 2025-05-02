package br.com.webpublico.pncp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BeneficioPncp {
    PARTICIPACAO_EXCLUSIVA(1, "Participação exclusiva para ME/EPP"),
    SUBCONTRATACAO(2, "Subcontratação para ME/EPP"),
    COTA_RESERVADA(3, "Cota reservada para ME/EPP"),
    SEM_BENEFICIO(4, "Sem benefício"),
    NAO_SE_APLICA(5, "Não se aplica");

    private final Integer codigo;
    private final String descricao;

    BeneficioPncp(Integer codigo, String descricao) {
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
