package br.com.webpublico.enums;

/**
 * Created by william on 14/08/17.
 */
public enum TipoOrdenacaoSubvencao {
    DATA_LANCAMENTO_CRESCENTE("Data de Lançamento – Crescente"),
    DATA_LANCAMENTO_DECRESCENTE("Data de Lançamento – Decrescente"),
    DATA_VENCIMENTO_CRESCENTE("Data de Vencimento – Crescente"),
    DATA_VENCIMENTO_DECRESCENTE("Data de Vencimento – Decrescente"),
    NATUREZA_TRIBUTARIA("Natureza – Tributária"),
    NATUREZA_NAO_TRIBUTARIA("Natureza – Não Tributária"),
    VALOR_TOTAL_CRESCENTE("Valor Total – Crescente"),
    VALOR_TOTAL_DECRESCENTE("Valor Total – Decrescente");

    private String descricao;

    TipoOrdenacaoSubvencao(String descricao) {
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
