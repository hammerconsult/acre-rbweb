package br.com.webpublico.enums;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
public enum CondicaoOcupacao {
    PROPRIA("Própria"),
    ALUGADA("Alugada"),
    CEDIDA("Cedida");

    private String descricao;

    CondicaoOcupacao(String descricao) {
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
