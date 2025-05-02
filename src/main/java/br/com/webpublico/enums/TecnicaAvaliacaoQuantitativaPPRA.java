package br.com.webpublico.enums;

/**
 * Created by carlos on 02/09/15.
 */
public enum TecnicaAvaliacaoQuantitativaPPRA {
    QUANTITATIVA("Quantitativa"),
    QUALITATIVA("Qualitativa");
    private String descricao;

    TecnicaAvaliacaoQuantitativaPPRA(String descricao) {
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
