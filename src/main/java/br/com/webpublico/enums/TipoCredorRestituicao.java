package br.com.webpublico.enums;

/**
 * Created by andregustavo on 18/05/2015.
 */
public enum TipoCredorRestituicao {
    CONTRIBUINTE("Contribuinte"),
    PROCURADOR("Procurador");

    private String descricao;

    TipoCredorRestituicao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
