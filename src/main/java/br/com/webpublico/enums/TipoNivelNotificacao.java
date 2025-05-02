package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoNivelNotificacao implements EnumComDescricao {
    ESTOQUE_MINIMO("Estoque Mínimo"),
    PONTO_REPOSICAO("Ponto de Reposição");
    private String descricao;

    TipoNivelNotificacao(String descricao) {
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
}
