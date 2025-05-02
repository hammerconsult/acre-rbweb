package br.com.webpublico.enums;

/**
 * Created by israeleriston on 25/08/16.
 */
public enum GrauDetalhamento {

    UM("1", 1),
    DOIS("2", 2),
    TRES("3", 3),
    QUATRO("4", 4);

    private String descricao;
    private Integer grau;

    GrauDetalhamento(String descricao, Integer grau) {
        this.descricao = descricao;
        this.grau = grau;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getGrau() {
        return grau;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
