package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum OrigemNotaAvulsa implements EnumComDescricao {
    WEBPUBLICO("Webpublico"),
    PORTAL("Portal do Contribuinte");

    String descricao;

    OrigemNotaAvulsa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
