package br.com.webpublico.enums;

import com.beust.jcommander.internal.Lists;

import java.util.List;

public enum Operador {

    IGUAL("=", "Igual"),
    MAIOR(">", "Maior"),
    MENOR("<", "Menor"),
    DIFERENTE("<>", "Diferente"),
    MAIOR_IGUAL(">=", "Maior ou Igual"),
    MENOR_IGUAL("<=", "Menor ou Igual"),
    LIKE("like", "Contém"),
    NOT_LIKE("not like", "Não Contém"),
    IN("in", "Entre"),
    NOT_IN("not in", "Fora"),
    IS_NULL("is null", "Vazio"),
    IS_NOT_NULL("is not null", "Diferente de Vazio");

    private String operador;
    private String descricao;

    Operador(String operador, String descricao) {
        this.operador = operador;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getOperador() {
        return operador;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static List<Operador> getOperadoresRelacionais() {
        return Lists.newArrayList(
                IGUAL,
                MAIOR,
                MENOR,
                DIFERENTE,
                MAIOR_IGUAL,
                MENOR_IGUAL
        );
    }
}
