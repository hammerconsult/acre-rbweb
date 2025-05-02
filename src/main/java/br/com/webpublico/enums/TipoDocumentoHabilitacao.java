package br.com.webpublico.enums;

/**
 * Created by hudson on 19/11/15.
 */
public enum TipoDocumentoHabilitacao {
    FISCAL("Fiscal"),
    OUTROS("Outros");

    private String descricao;

    TipoDocumentoHabilitacao(String descricao) {
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
