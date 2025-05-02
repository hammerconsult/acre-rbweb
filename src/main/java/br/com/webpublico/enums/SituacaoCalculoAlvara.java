package br.com.webpublico.enums;


import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoCalculoAlvara implements EnumComDescricao {

    EM_ABERTO("Em Aberto"),
    CALCULADO("Calculado"),
    RECALCULADO("Recalculado"),
    EFETIVADO("Efetivado"),
    ESTORNADO("Estornado");

    private String descricao;

    private SituacaoCalculoAlvara(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
