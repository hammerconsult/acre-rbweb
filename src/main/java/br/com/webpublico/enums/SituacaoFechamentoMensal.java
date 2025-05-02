package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoFechamentoMensal implements EnumComDescricao {
    ABERTO("Aberto"),
    FECHADO("Fechado");
    private String descricao;

    SituacaoFechamentoMensal(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAberto() {
        return ABERTO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
