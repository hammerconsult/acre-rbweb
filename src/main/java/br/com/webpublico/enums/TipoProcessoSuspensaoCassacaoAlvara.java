package br.com.webpublico.enums;


import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoProcessoSuspensaoCassacaoAlvara implements EnumComDescricao {

    SUSPENSAO("Suspensão"),
    CASSACAO("Cassação");

    private final String descricao;

    TipoProcessoSuspensaoCassacaoAlvara(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
