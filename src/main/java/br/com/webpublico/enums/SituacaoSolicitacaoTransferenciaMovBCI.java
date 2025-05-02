package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoSolicitacaoTransferenciaMovBCI implements EnumComDescricao {

    ABERTA("Aberta"),
    DEFERIDA("Deferida"),
    INDEFERIDA("Indeferida");

    private String descricao;

    SituacaoSolicitacaoTransferenciaMovBCI(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
