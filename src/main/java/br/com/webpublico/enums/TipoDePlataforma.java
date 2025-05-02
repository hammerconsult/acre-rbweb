package br.com.webpublico.enums;

public enum TipoDePlataforma {
    WINDOWS("Windows"),
    LINUX("Linux");

    private String descricao;

    TipoDePlataforma(String descricao) {
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
