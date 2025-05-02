package br.com.webpublico.enums;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
public enum MotivoRequisicaoUmBeneficio {
    JAZIGO_DA_FAMILIA("Jazigo da Familia"),
    URNA_COMPRADA_PELA_FAMILIA_AMIGO("Urna comprada pela família/amigo"),
    URNA_CEDIDA_PELO_ESTADO("Urna cedida pelo Estado"),
    TFD("TFD"),
    OUTROS("Outros");

    private String descricao;

    MotivoRequisicaoUmBeneficio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
