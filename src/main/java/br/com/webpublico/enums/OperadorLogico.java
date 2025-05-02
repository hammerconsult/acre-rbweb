package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum OperadorLogico implements EnumComDescricao {

    AND("E", "and"),
    OR("Ou", "or");

    private final String descricao;
    private final String condicional;

    OperadorLogico(String descricao, String condicional) {
        this.descricao = descricao;
        this.condicional = condicional;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public String getCondicional() {
        return condicional;
    }
}
