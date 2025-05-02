package br.com.webpublico.enums.rh.concursos;

public enum MetodoAvaliacao {

    POR_NOTA("Por Nota"),
    POR_OBJETIVO("Por Objetivo");


    private String descricao;

    private MetodoAvaliacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isPorNota() {
        return this.equals(POR_NOTA);
    }

    public boolean isPorObjetivo() {
        return this.equals(POR_OBJETIVO);
    }


    @Override
    public String toString() {
        return descricao;
    }
}
