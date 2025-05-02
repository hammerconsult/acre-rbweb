package br.com.webpublico.enums;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
public enum TipoCasa {
    MADEIRA("Madeira"),
    ALVENARIA("Alvenaria"),
    MISTA("Mista");

    private String descricao;

    TipoCasa(String descricao) {
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
