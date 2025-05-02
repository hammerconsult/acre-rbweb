package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoPrazoPrescricional implements EnumComDescricao {
    UM_ANO("1 Ano", 1),
    DOIS_ANOS("2 Anos", 2),
    TRES_ANOS("3 Anos", 3),
    QUATRO_ANOS("4 Anos", 4),
    CINCO_ANOS("5 Anos", 5);

    private String descricao;
    private Integer ano;

    private TipoPrazoPrescricional(String descricao, Integer ano) {
        this.descricao = descricao;
        this.ano = ano;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public Integer getAno() {
        return ano;
    }
}
