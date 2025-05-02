package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum NaturezaDividaAtivaCreditoReceber implements EnumComDescricao {
    ORIGINAL("Original"),
    AJUSTE_DE_PERDAS("Ajuste de Perdas");

    private String descricao;

    NaturezaDividaAtivaCreditoReceber(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
