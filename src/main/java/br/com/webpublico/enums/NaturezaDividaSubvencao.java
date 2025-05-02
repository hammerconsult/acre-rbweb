package br.com.webpublico.enums;

/**
 * Created by william on 11/08/17.
 */
public enum NaturezaDividaSubvencao {
    TRIBUTARIA("Tributária"),
    NAO_TRIBUTARIA("Não Tributária");

    private String descricao;

    NaturezaDividaSubvencao(String descricao) {
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
