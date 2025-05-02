package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoPerfil implements EnumComDescricao {
    HOMOLOGACAO( "HOMOLOGAÇÃO");

    String descricao;

    TipoPerfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static String getDescricaoHomologacao() {
        return HOMOLOGACAO.getDescricao();
    }
}
