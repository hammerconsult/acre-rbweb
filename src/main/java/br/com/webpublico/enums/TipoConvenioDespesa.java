package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoConvenioDespesa implements EnumComDescricao {
    CONVENIO("1 - Convênio"),
    TERMO_DE_COLABORACAO("2 - Termo de Colaboração"),
    TERMO_DE_FOMENTO("3 - Termo de Fomento"),
    TERMO_DE_COMPROMISSO("4 - Termo de Compromisso");
    private String descricao;

    TipoConvenioDespesa(String descricao) {
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
