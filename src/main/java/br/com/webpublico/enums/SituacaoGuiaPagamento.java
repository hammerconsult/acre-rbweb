package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 08/06/15
 * Time: 10:09
 * To change this template use File | Settings | File Templates.
 */
public enum SituacaoGuiaPagamento {

    ABERTA("Aberta"),
    DEFERIDA("Deferida"),
    INDEFERIDA("Indeferida");
    private String descricao;

    private SituacaoGuiaPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
