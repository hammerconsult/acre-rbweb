package br.com.webpublico.enums.rh;

import br.com.webpublico.interfaces.EnumComDescricao;


public enum TipoVinculoFP implements EnumComDescricao {

    SERVIDOR("Servidor"),
    APOSENTADO("Aposentado"),
    PENSIONISTA("Pensionista");
    private String descricao;

    TipoVinculoFP(String descricao) {
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
