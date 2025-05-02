package br.com.webpublico.enums;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
public enum TipoAsfalto {
    ASFALTADA("Asfaltada"),
    TIJOLADA("Tijolada"),
    TERRA_BARRO("Terra/Barro"),
    INTRANSITAVEL("Intransitável");

    private String descricao;

    TipoAsfalto(String descricao) {
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
