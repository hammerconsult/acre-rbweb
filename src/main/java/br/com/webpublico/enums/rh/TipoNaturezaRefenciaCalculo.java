package br.com.webpublico.enums.rh;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoNaturezaRefenciaCalculo  implements EnumComDescricao {

    DIAS("Dias");

    String descricao;

    TipoNaturezaRefenciaCalculo(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
