package br.com.webpublico.entidades.rh.portal;

public enum RHStatusSolicitacaoAfastamentoPortal {

    EM_ANALISE("Em Análise"),
    APROVADO("Aprovado"),
    REPROVADO("Reprovado");

    private String descricao;

    private RHStatusSolicitacaoAfastamentoPortal(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isReprovado() {
        return REPROVADO.equals(this);
    }

    public boolean isEmAnalise() {
        return EM_ANALISE.equals(this);
    }
}
