package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoFaleConosco implements EnumComDescricao {

    PEDIDO_AJUDA("Pedido de Ajuda"),
    SUGESTAO_MELHORIA("Sugestão de Melhoria"),
    INFORME_PROBLEMA("Informe de Problema");
    private String descricao;

    TipoFaleConosco(String descricao) {
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
