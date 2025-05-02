package br.com.webpublico.enums.conciliacaocontabil;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoMovimentoHashContabil implements EnumComDescricao {

    ORCAMENTARIO("ORCAMENTARIO"),
    FINANCEIRO("FINANCEIRO"),
    EXTRAORCAMENTARIO("EXTRAORCAMENTARIO"),
    DIVIDAPUBLICA("DIVIDA PUBLICA"),
    CONTABIL("CONTABIL");

    private String descricao;

    TipoMovimentoHashContabil(String descricao) {
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
