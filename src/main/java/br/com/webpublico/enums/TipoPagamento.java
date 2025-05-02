package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 19/07/13
 * Time: 14:18
 * To change this template use File | Settings | File Templates.
 */
public enum TipoPagamento {
    ANUAL("Anual"),
    MENSAL("Mensal");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    private TipoPagamento(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
