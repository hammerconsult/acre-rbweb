package br.com.webpublico.enums;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
public enum TipoEnergiaEletrica {
    RELOGIO("Relógio"),
    CLANDESTINO("Clandestino (Rabicho)");

    private String descricao;

    TipoEnergiaEletrica(String descricao) {
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
