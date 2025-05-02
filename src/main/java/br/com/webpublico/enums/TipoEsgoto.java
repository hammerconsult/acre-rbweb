package br.com.webpublico.enums;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
public enum TipoEsgoto {
    FOSSA("Fossa"),
    SAERB("SAERB"),
    CEU_ABERTO("Céu aberto");

    private String descricao;

    TipoEsgoto(String descricao) {
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
