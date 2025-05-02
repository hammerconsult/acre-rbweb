package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoMateriaPrima implements EnumComDescricao {

    AREIA("Areia"),
    ARGILA("Argila"),
    LATERITA("Laterita");

    TipoMateriaPrima(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
