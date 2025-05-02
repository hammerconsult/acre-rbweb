package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum ConsolidadoDetalhado implements EnumComDescricao {

    CONSOLIDADO("Consolidado"),
    DETALHADO("Detalhado");
    private String descricao;

    ConsolidadoDetalhado(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isConsolidado() {
        return CONSOLIDADO.equals(this);
    }
}
