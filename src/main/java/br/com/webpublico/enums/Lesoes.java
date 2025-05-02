package br.com.webpublico.enums;

/**
 * Created by carlos on 23/06/15.
 */
public enum Lesoes {

    MUSCULAR("Muscular"),
    ARTICULAR("Articular"),
    OSSEA("Óssea"),
    MUSCULAR_ARTICULAR("Muscular e Articular"),
    MUSCULAR_OSSEA("Muscular e Óssea"),
    ARTICULAR_OSSEA("Articular e Óssea"),
    MUSCULAR_ARTICULAR_OSSEA("Muscular, Articular e Óssea");
    private String descricao;

    private Lesoes(String descricao) {
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
