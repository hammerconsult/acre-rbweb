package br.com.webpublico.enums.rh.ponto;

public enum StatusSolicitacaoFaltas {

    EM_ANALISE("Em Análise"),
    APROVADO("Aprovado"),
    REPROVADO("Reprovado");


    StatusSolicitacaoFaltas(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isReprovado() {
        return REPROVADO.equals(this);
    }

    public boolean isEmAnalise() {
        return EM_ANALISE.equals(this);
    }
}
