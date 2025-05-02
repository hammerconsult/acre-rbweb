package br.com.webpublico.enums;

/**
 * Created by carlos on 02/09/15.
 */
public enum IntensidadeAvaliacaoQuantitativaPPRA {
    BAIXA("Baixa"),
    MÉDIA("Média"),
    ALTA("Alta");
    private String descricao;

    IntensidadeAvaliacaoQuantitativaPPRA(String descricao) {
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
