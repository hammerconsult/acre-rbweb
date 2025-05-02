package br.com.webpublico.enums;

public enum TipoMovimentoItemContrato {
    ORIGINAL("Original"),
    DESPESA("Despesa"),
    VALOR_EXECUCAO_PRE_EXECUCAO("Valor/Execução (Pré)"),
    VALOR_EXECUCAO_POS_EXECUCAO("Valor/Execução (Pós)"),
    VALOR_VARIACAO("Valor/Variação");

    private String descricao;

    TipoMovimentoItemContrato(String descricao) {
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
