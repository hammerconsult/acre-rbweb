package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoSolicitacaoAlvaraImediato implements EnumComDescricao {
    CONSTRUCAO("Construção"),
    REGULARIZACAO("Regularização");

    private String descricao;

    TipoSolicitacaoAlvaraImediato(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
