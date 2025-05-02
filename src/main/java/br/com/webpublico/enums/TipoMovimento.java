package br.com.webpublico.enums;

/**
 * Created by carlos on 21/07/16.
 */
public enum TipoMovimento {
    LIBERACAO_FINANCEIRA("Liberação Financeira"),
    TRANSFERENCIA_FINANCEIRA("Transferência Financeira");

    TipoMovimento(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
