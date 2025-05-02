package br.com.webpublico.enums;

/**
 * Created by venom on 03/11/14.
 */
public enum ModalidadeConfea {
    CIVIL("1 - Civil"),
    ELETRICISTA("2 - Eletricista"),
    MECANICA_METALURGICA("3 - Mecânica e Metalúrgica"),
    QUIMICA("4 - Química"),
    GEOLOGIA_MINAS("5 - Geologia e Minas"),
    AGRIMENSURA("6 - Agrimensura"),
    AGRONOMIA("1 - Agronomia"),
    ESPECIAIS("2 - Especiais");
    private String descricao;

    ModalidadeConfea(String descricao) {
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
