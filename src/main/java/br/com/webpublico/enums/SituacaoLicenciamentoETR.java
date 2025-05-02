package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoLicenciamentoETR implements EnumComDescricao {
    APROVADO("Aprovado"),
    EM_ESTUDO("Em Estudo"),
    EM_EXIGENCIA("Em Exigência"),
    RETORNO_EXIGENCIA("Retorno de Exigência"),
    SUSPENSO("Suspenso");

    private String descricao;

    SituacaoLicenciamentoETR(String descricao) {
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
