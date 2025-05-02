package br.com.webpublico.dte.enums;

public enum SituacaoLoteNotificacaoDte {
    ABERTO("Aberto"), FINALIZADO("Finalizado");

    private String descricao;

    SituacaoLoteNotificacaoDte(String descricao) {
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
