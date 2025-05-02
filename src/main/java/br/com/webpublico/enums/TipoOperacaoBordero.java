package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoOperacaoBordero implements EnumComDescricao {
    PAGAMENTO("Pagamento"),
    PAGAMENTO_EXTRA("Despesa Extraorçamentária"),
    TRANSFERENCIA_FINANCEIRA("Transferência Financeira"),
    TRANSFERENCIA_FINANCEIRA_MESMA_UNIDADE("Transferência Financeira Mesma Unidade"),
    LIBERACAO_FINANCEIRA("Liberação Financeira");
    private String descricao;

    TipoOperacaoBordero(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isPagamento() {
        return TipoOperacaoBordero.PAGAMENTO.equals(this);
    }

    public boolean isPagamentoExtra() {
        return TipoOperacaoBordero.PAGAMENTO_EXTRA.equals(this);
    }

    public boolean isTransferenciaFinanceira() {
        return TipoOperacaoBordero.TRANSFERENCIA_FINANCEIRA.equals(this);
    }

    public boolean isTransferenciaFinanceiraMesmaUnidade() {
        return TipoOperacaoBordero.TRANSFERENCIA_FINANCEIRA_MESMA_UNIDADE.equals(this);
    }

    public boolean isLiberacaoFinanceira() {
        return TipoOperacaoBordero.LIBERACAO_FINANCEIRA.equals(this);
    }
}
