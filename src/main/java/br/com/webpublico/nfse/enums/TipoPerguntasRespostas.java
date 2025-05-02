package br.com.webpublico.nfse.enums;

/**
 * Created by William on 19/01/2019.
 */
public enum TipoPerguntasRespostas {
    PADRAO("Padrão"),
    CAMPANHA("Campanha");

    private String descricao;

    TipoPerguntasRespostas(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
