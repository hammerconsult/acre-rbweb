package br.com.webpublico.enums;

/**
 * Created by venom on 03/11/14.
 */
public enum GrupoConfea {
    ENGENHARIA("1 - Engenharia"),
    AGRONOMIA("3 - Agronomia"),
    ESPECIAIS("4 - Especiais");
    private String descricao;

    GrupoConfea(String descricao) {
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
