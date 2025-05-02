package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoSituacaoObra implements EnumComDescricao {
    EM_ANDAMENTO("Em Andamento"),
    PARALIZADA("Paralizada"),
    CANCELADA("Cancelada"),
    CONCLUIDA("Concluída");

    private String descricao;

    TipoSituacaoObra(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
