package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoViagemSaud implements EnumComDescricao {
    CADASTRADA("Cadastrada"),
    APROVADA("Aprovada"),
    REJEITADA("Rejeitada");

    private final String descricao;

    SituacaoViagemSaud(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
