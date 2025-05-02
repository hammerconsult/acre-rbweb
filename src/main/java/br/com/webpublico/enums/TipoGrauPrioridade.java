package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoGrauPrioridade implements EnumComDescricao {
    BAIXA("Baixa"),
    MEDIA("Média"),
    ALTA("Alta");
    private String descricao;

    TipoGrauPrioridade(String descricao) {
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
