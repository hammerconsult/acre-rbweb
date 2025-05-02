package br.com.webpublico.nfse.domain;

public enum SituacaoSolicitacaoAcesso {
    AGUARDANDO_AVALIACAO("Aguardando Avaliação"),
    APROVADO("Aprovado"),
    REJEITADO("Rejeitado");

    private String descricao;

    SituacaoSolicitacaoAcesso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
