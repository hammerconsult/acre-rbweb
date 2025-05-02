package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoPainelPortal implements EnumComDescricao {
    GRAFICO("Gráfico"),
    LISTA_VERTICAL("Lista Vertical"),
    LISTA_HORIZONTAL("Lista Horizontal");
    private String descricao;

    TipoPainelPortal(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isGrafico() {
        return GRAFICO.equals(this);
    }
}
