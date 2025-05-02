package br.com.webpublico.enums;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
public enum TipoBeneficioCedido {
    JAZIGO("Jazigo"),
    URNA("Urna"),
    JAZIGO_E_URNA("Jazigo e Urna");

    private String descricao;

    TipoBeneficioCedido(String descricao) {
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
