package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoCardapio implements EnumComDescricao  {

    EM_ELABORACAO("Em elaboração"),
    CONCLUIDO("Concluído"),
    AGUARDANDO("Aguardando aprovação"),
    APROVADO("Aprovado"),
    REJEITADO("Rejeitado"),
    CANCELADO("Cancelado");

    private String descricao;

    SituacaoCardapio(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
