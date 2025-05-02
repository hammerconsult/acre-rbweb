package br.com.webpublico.enums;

/**
 * Created by William on 08/02/2016.
 */
public enum TipoMovimentacaoContrato {
    ORDINARIA("Ordinária"),
    SUBSTITUICAO("Substituição"),
    REDUCAO("Redução"),
    RENOVACAO("Renovação"),
    REFORCO("Reforço");

    private String descricao;

    TipoMovimentacaoContrato(String descricao) {
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

