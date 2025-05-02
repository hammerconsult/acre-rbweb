package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoAlteracaoItem implements EnumComDescricao {

    TIPO_CONTROLE("Tipo de Controle"),
    OBJETO_COMPRA("Objeto de Compra");
    private String descricao;

    TipoAlteracaoItem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAlteracaoTipoControle(){
        return TIPO_CONTROLE.equals(this);
    }

    public boolean isAlteracaoObjetoCompra(){
        return OBJETO_COMPRA.equals(this);
    }
}
