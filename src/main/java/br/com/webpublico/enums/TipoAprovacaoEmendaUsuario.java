package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoAprovacaoEmendaUsuario implements EnumComDescricao {

    PREFEITURA("Prefeitura"),
    CAMARA("Câmara");

    private String descricao;

    TipoAprovacaoEmendaUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
