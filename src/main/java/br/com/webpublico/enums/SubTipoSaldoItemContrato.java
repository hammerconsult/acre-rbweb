package br.com.webpublico.enums;

public enum SubTipoSaldoItemContrato {
    VARIACAO("Variação"),
    EXECUCAO("Execução");
    private String descricao;

    SubTipoSaldoItemContrato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isValorVariacao() {
        return VARIACAO.equals(this);
    }

    public boolean isValorExecucao() {
        return EXECUCAO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
