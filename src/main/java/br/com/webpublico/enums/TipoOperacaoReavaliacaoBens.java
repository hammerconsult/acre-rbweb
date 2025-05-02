package br.com.webpublico.enums;

import com.google.common.collect.Lists;

import java.util.List;

public enum TipoOperacaoReavaliacaoBens {

    REAVALIACAO_BENS_MOVEIS_AUMENTATIVA("Reavaliação de bens móveis aumentativa"),
    REAVALIACAO_BENS_MOVEIS_DIMINUTIVA("Reavaliação de bens móveis diminutiva"),
    REAVALIACAO_BENS_IMOVEIS_AUMENTATIVA("Reavaliação de bens Imóveis aumentativa"),
    REAVALIACAO_BENS_IMOVEIS_DIMINUTIVA("Reavaliação de bens Imóveis diminutiva");
    private String descricao;

    TipoOperacaoReavaliacaoBens(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static List<TipoOperacaoReavaliacaoBens> getOperacoesBemMovel() {
        List<TipoOperacaoReavaliacaoBens> toReturn = Lists.newArrayList();
        toReturn.add(REAVALIACAO_BENS_MOVEIS_AUMENTATIVA);
        toReturn.add(REAVALIACAO_BENS_MOVEIS_DIMINUTIVA);
        return toReturn;
    }

    public static List<TipoOperacaoReavaliacaoBens> getOperacoesBemImovel() {
        List<TipoOperacaoReavaliacaoBens> toReturn = Lists.newArrayList();
        toReturn.add(REAVALIACAO_BENS_IMOVEIS_AUMENTATIVA);
        toReturn.add(REAVALIACAO_BENS_IMOVEIS_DIMINUTIVA);
        return toReturn;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
