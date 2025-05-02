package br.com.webpublico.enums.administrativo;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoCotacao implements EnumComDescricao {
    EM_ELABORACAO("Em Elaboração"),
    CONCLUIDO("Concluído");
    private String descricao;

    SituacaoCotacao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public boolean isEmElaboracao() {
        return SituacaoCotacao.EM_ELABORACAO.equals(this);
    }

    public boolean isConcluido() {
        return SituacaoCotacao.CONCLUIDO.equals(this);
    }
}
