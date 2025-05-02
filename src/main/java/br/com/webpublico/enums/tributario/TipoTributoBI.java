package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoTributoBI implements EnumComDescricao {
    IPTU ("IPTU"),
    ISSQN ("ISSQN");

    private final String descricao;

    TipoTributoBI(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
