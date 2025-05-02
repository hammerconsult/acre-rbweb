package br.com.webpublico.enums;

public enum TipoRenumerarObjetoCompra {
    CODIGO_OBJETO_COMPRA("Código do Objeto de Compra"),
    DESCRICAO_OBJETO_COMPRA("Descrição do Objeto de Compra");

    private String descricao;

    TipoRenumerarObjetoCompra(String descricao) {
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
