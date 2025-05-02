package br.com.webpublico.enums;

/**
 * Created by mga on 06/08/19.
 */
public enum SituacaoSolicitacaoUnidadeRequerente {

    EM_ELABORACAO("Em Elaboração"),
    EFETIVADA("Efetivada"),
    APROVADA("Aprovada"),
    REJEITADA("Rejeitada");

    private SituacaoSolicitacaoUnidadeRequerente(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public static SituacaoSolicitacaoUnidadeRequerente[] situacoesParaEfetivacao() {
        SituacaoSolicitacaoUnidadeRequerente situacoes[] = {REJEITADA, APROVADA};
        return situacoes;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
