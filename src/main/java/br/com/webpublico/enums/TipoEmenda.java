package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoEmenda implements EnumComDescricao {
    INDIVIDUAL_COM_FINALIDADE_ESPECIFICA("Individual com Finalidade Específica"),
    INDIVIDUAL_EMENDA_ESPECIAL("Individual Emenda Especial"),
    BANCADA("Bancada"),
    ESPECIAL("Especial"),
    COMISSAO("Comissão");
    private String descricao;

    TipoEmenda(String descricao) {
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
