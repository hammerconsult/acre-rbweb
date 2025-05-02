package br.com.webpublico.enums;


import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoProcessoSuspensaoCassacaoAlvara implements EnumComDescricao {

    EM_ABERTO("Em Aberto"),
    EFETIVADO("Efetivado"),
    ENCERRADO("Encerrado");

    private final String descricao;

    SituacaoProcessoSuspensaoCassacaoAlvara(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
