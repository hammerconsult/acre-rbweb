package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoColetaDados implements EnumComDescricao {
    POR_BCI("Por Cadastro Imobiliário"),
    POR_PLANILHA_EXTERNA("Por Planilha Externa");
    private String descricao;

    TipoColetaDados(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public boolean isBCI(){
        return POR_BCI.equals(this);
    }

    public boolean isPlanilha(){
        return POR_PLANILHA_EXTERNA.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
