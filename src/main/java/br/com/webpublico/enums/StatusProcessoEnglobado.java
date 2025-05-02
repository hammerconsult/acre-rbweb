package br.com.webpublico.enums;

/**
 * Criado por Mateus
 * Data: 27/04/2017.
 */
public enum StatusProcessoEnglobado {
    INCORPORADO("Incorporado"),
    DESMEMBRADO("Desmembrado");

    StatusProcessoEnglobado(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
