package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * Created by mateus on 07/05/18.
 */
public enum Intervalo implements EnumComDescricao {
    CURTO_PRAZO("Curto Prazo"),
    LONGO_PRAZO("Longo Prazo");

    private String descricao;

    Intervalo(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public boolean isLongoPrazo() {
        return LONGO_PRAZO.equals(this);
    }

    public boolean isCurtoPrazo() {
        return CURTO_PRAZO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
