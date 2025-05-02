package br.com.webpublico.enums;

public enum TipoPrePosExecucao {
    PRE_EXECUCAO("Pré Execução"),
    POS_EXECUCAO("Pós Execução");

    private String descricao;

    TipoPrePosExecucao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
