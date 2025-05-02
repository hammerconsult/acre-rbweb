package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

import java.util.List;

public enum TipoPrestacaoDeContas implements EnumComDescricao {
    SISTEMA("Sistema"),
    SICONFI("SICONFI"),
    PCM("PCM");

    private String descricao;

    TipoPrestacaoDeContas(String descricao) {
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

    public static String montarClausulaIn(List<TipoPrestacaoDeContas> tipos) {
        String retorno = "";
        String juncao = "(";
        for (TipoPrestacaoDeContas tipo : tipos) {
            retorno += juncao + "'" + tipo.name() + "'";
            juncao = ", ";
        }
        retorno += ") ";
        return retorno;
    }
}
