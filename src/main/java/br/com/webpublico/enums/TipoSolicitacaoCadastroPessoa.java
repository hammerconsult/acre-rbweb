package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoSolicitacaoCadastroPessoa implements EnumComDescricao {
    TRIBUTARIO("Tributário"),
    CONTABIL("Contábil");
    private String descricao;

    TipoSolicitacaoCadastroPessoa(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
