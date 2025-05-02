package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoRevisaoControleLicenciamentoAmbiental implements EnumComDescricao {
    VALIDADO("Validado"),
    REJEITADO("Rejeitado");

    private final String descricao;

    SituacaoRevisaoControleLicenciamentoAmbiental(String descricao) {
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
