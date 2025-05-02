package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoFaleConosco implements EnumComDescricao {

    ENVIADA("Enviada"),
    EM_ANALISE("Em Análise"),
    CONCLUIDA("Concluída");
    private String descricao;

    SituacaoFaleConosco(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
