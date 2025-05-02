package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum OrigemItemPregaoLanceVencedor implements EnumComDescricao {
    PREGAO("Pregão"),
    PROXIMO_VENCEDOR_LICITACAO("Próximo Vencedor Licitação"),
    REPACTUACAO_PRECO("Repactuação de Preço");
    private String descricao;

    OrigemItemPregaoLanceVencedor(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
