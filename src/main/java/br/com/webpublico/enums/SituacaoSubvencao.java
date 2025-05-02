package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 19/12/13
 * Time: 10:13
 * To change this template use File | Settings | File Templates.
 */
public enum SituacaoSubvencao {
    EM_ABERTO("Em Aberto"),
    EFETIVADO("Efetivado"),
    ESTORNADO("Estornado");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    private SituacaoSubvencao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
