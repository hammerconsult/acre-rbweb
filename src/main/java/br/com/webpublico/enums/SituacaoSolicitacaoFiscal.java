package br.com.webpublico.enums;

public enum SituacaoSolicitacaoFiscal {

    AGUARDANDO_APROVACAO("Aguardando Aprovação"),
    APROVADA("Aprovada"),
    REJEITADA("Rejeitada");

    String descricao;

    SituacaoSolicitacaoFiscal(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
