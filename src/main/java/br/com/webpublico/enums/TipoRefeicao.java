package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoRefeicao implements EnumComDescricao {

    CAFE_MANHA("Café da Manhã"),
    ALMOCO("Almoço"),
    LANCHE("Lanche"),
    JANTA("Janta"),
    DESJEJUM("Desjejum"),
    OUTRO("Outro");
    private String descricao;

    TipoRefeicao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
