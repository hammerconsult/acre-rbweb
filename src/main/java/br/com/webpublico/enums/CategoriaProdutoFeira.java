package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum CategoriaProdutoFeira implements EnumComDescricao {
    CEREAL("Cereais"),
    HORTALICA("Hortaliças"),
    FRUTA("Frutas"),
    LEGUMINOSA("Leguminosas"),
    CARNE_OVO("Carnes e Ovos"),
    LEITE_DERIVADOS("Leites e Derivados"),
    OLEO_GORDURA("Óleos e Gorduras"),
    ACUCAR_DOCE("Açúcares e Doces");

    private String descricao;

    CategoriaProdutoFeira(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
