package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoExportacaoDebitosIPTU implements EnumComDescricao {
    ABERTO("Aberto"),
    GERANDO("Gerando"),
    CONCLUIDO("Concluído");

    private final String descricao;

    SituacaoExportacaoDebitosIPTU(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
