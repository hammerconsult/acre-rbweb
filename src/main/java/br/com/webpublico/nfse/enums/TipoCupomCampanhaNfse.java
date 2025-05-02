package br.com.webpublico.nfse.enums;

public enum TipoCupomCampanhaNfse {
    POR_VALOR("Por Valor"), POR_NOTA("Por Nota");

    private String descricao;

    TipoCupomCampanhaNfse(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isPorNota() {
        return this.equals(POR_NOTA);
    }

    public boolean isPorValor() {
        return this.equals(POR_VALOR);
    }
}
