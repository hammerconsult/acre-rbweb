package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoSituacaoSancao implements EnumComDescricao {
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDA("Concluída");
    private String descricao;

    TipoSituacaoSancao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
