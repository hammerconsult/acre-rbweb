package br.com.webpublico.enums.rh;

import java.io.Serializable;

public enum TipoCalculo implements Serializable {

    TODOS("Calcular Todos"),
    LOTE("Calcular Lote"),
    INDIVIDUAL("Individual"),
    ORGAO("Órgão"),
    ENTIDADE("Calcular Entidade");

    private String descricao;

    TipoCalculo(String descricao) {
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
