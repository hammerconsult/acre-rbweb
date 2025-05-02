package br.com.webpublico.enums.rh.cbo;

public enum TipoCBO {

    OCUPACAO("Ocupação"),
    SINONIMO("Sinônimo");

    private String descricao;

    TipoCBO(String descricao) {
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
