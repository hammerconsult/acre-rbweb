package br.com.webpublico.enums;

public enum StatusProduto {
    NOVO("Novo"),
    ALTERADO("Alterado"),
    EXCLUIDO("Excluído");

    private String descricao;

    StatusProduto(String descricao) {
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
