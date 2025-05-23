package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoSolicitacaoITBI implements EnumComDescricao {
    EM_ANALISE("Em Análise"),
    REJEITADA("Rejeitada"),
    APROVADA("Aprovada"),
    DESIGNADA("Designada"),
    HOMOLOGADA("Homologada"),
    AVALIADA("Avaliada"),
    REAVALIAR("Reavaliar");

    private final String descricao;

    private SituacaoSolicitacaoITBI(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
