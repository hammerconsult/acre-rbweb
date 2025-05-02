package br.com.webpublico.enums;

/**
 * Created by venom on 21/10/14.
 */
public enum TipoConcurso {
    CONCURSO("Concurso Público"),
    SELECAO("Seleção");
    private String descricao;

    TipoConcurso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
