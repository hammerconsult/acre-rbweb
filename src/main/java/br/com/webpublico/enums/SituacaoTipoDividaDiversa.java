package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 25/01/15
 * Time: 10:55
 * To change this template use File | Settings | File Templates.
 */
public enum SituacaoTipoDividaDiversa {
    ATIVO("Ativo"),
    INATIVO("Inativo");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    private SituacaoTipoDividaDiversa(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
