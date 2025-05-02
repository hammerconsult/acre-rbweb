package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoQuadroPaginaInicialPortal implements EnumComDescricao {
    PADRAO("Padrão"),
    PRINCIPAL("Principal"),
    RELEVANTES("Relevantes"),
    MENU_HORIZONTAL("Menu Horizontal");
    private String descricao;

    TipoQuadroPaginaInicialPortal(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
