package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum Perecibilidade implements EnumComDescricao {
    PERECIVEL("Perecível"),
    SEMI_PERECIVEL("Semi-Perecível"),
    NAO_SE_APLICA("Não se Aplica");
    private String descricao;

    Perecibilidade(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public boolean isNaoSeAplica(){
        return NAO_SE_APLICA.equals(this);
    }
}

