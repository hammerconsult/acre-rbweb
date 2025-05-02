package br.com.webpublico.enums;

public enum TipoEntradaMaterial {

    COMPRA("Compra"),
    TRANSFERENCIA("Transferência"),
    INCORPORACAO("Incorporação");
    private String descricao;

    TipoEntradaMaterial(String descricao) {
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
