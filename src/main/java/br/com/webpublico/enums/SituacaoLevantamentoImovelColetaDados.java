package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoLevantamentoImovelColetaDados implements EnumComDescricao {

    EM_COLETA("Em Coleta"),
    APROVADO("Aprovado");
    private String descricao;

    SituacaoLevantamentoImovelColetaDados(String descricao) {
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
