package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoSolicitacaoReconhecimentoDivida implements EnumComDescricao {
    EM_ELABORACAO("Em Elaboração"),
    CONCLUIDO("Concluído");

    private String descricao;

    SituacaoSolicitacaoReconhecimentoDivida(String descricao) {
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
