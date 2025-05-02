package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoITBI implements EnumComDescricao {
    ABERTO("Aberto"),
    PROCESSADO("Processado"),
    PAGO("Pago"),
    EMITIDO("Emitido"),
    ASSINADO("Assinado"),
    RETIFICADO("Retificado"),
    CANCELADO("Cancelado");

    private final String descricao;

    private SituacaoITBI(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
