package br.com.webpublico.nfse.enums;

public enum TipoPartida {
    CREDITO("1 - Crédito"),
    DEBITO("2 - Débito");

    private String descricao;

    TipoPartida(String descricao) {
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
