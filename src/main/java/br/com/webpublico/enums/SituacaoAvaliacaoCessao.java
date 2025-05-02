package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 08/05/14
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
public enum SituacaoAvaliacaoCessao {
    APROVADO("APROVADO"),
    REJEITADO("REJEITADO");

    private String descricao;

    private SituacaoAvaliacaoCessao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
