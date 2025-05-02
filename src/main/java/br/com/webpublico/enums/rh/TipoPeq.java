package br.com.webpublico.enums.rh;

public enum TipoPeq {
    MAGISTERIO("Servidor Magistério"),
    APOIO("Servidor Apoio");

    private String descricao;

    TipoPeq(String descricao) {
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
