package br.com.webpublico.enums;

/**
 * Created by zaca on 26/05/17.
 */
public enum TipoContratante {
    TITULAR("Titular"),
    SUBSTITUTO("Substituto");

    private String descricao;

    TipoContratante(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
