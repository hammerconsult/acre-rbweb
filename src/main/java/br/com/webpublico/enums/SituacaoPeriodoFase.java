package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 15/10/13
 * Time: 15:30
 * To change this template use File | Settings | File Templates.
 */
public enum SituacaoPeriodoFase {
    ABERTO("Aberto"),
    FECHADO("Fechado");

    private SituacaoPeriodoFase(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
