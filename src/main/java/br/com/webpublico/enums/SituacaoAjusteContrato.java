package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoAjusteContrato implements EnumComDescricao {

    EM_ELABORACAO("Em Elaboração"),
    APROVADO("Aprovado");
    private String descricao;

    SituacaoAjusteContrato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isEmElaboracao() {
        return SituacaoAjusteContrato.EM_ELABORACAO.equals(this);
    }

    public boolean isAprovado() {
        return SituacaoAjusteContrato.APROVADO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
