package br.com.webpublico.enums;

public enum TipoResponsavelFiscal {
    TECNICO("Técnico"),
    FISCAL("Fiscal");

    String descricao;

    TipoResponsavelFiscal(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
