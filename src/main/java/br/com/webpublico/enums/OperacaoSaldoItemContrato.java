package br.com.webpublico.enums;

public enum OperacaoSaldoItemContrato {
    PRE_EXECUCAO("Pré"),
    POS_EXECUCAO("Pós");

    private String descricao;

    OperacaoSaldoItemContrato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static OperacaoSaldoItemContrato getOperacaoSaldoPorOperacaoMovimentoContratual(OperacaoMovimentoAlteracaoContratual operacao) {
        return OperacaoMovimentoAlteracaoContratual.getOperacoesPreExecucao().contains(operacao)
            ? OperacaoSaldoItemContrato.PRE_EXECUCAO
            : OperacaoSaldoItemContrato.POS_EXECUCAO;
    }

    public boolean isPosExecucao(){
        return POS_EXECUCAO.equals(this);
    }
}
