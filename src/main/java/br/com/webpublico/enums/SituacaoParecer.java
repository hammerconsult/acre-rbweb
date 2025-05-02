package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoParecer implements EnumComDescricao {
    DEFERIDO("Deferido"),
    INDEFERIDO("Indeferido");
    private String descricao;

    SituacaoParecer(String descricao) {
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
