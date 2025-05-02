package br.com.webpublico.enums;

/**
 * Created by William on 27/01/2017.
 */
public enum SituacaoContribuicaoMelhoria {
    EM_ABERTO("Em Aberto"),
    EFETIVADO("Efetivado");

    private String descricao;

    SituacaoContribuicaoMelhoria(String descricao) {
        this.descricao = descricao;
    }

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
}
