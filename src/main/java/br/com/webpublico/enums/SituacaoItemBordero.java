package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 07/05/14
 * Time: 21:24
 * To change this template use File | Settings | File Templates.
 */
public enum SituacaoItemBordero {

    BORDERO("Ordem Bancária"),
    EFETUADO("Efetuada"),
    PAGO("Paga"),
    INDEFIRIDO("Indeferida"),
    DEFERIDO("Deferida");

    private String descricao;

    private SituacaoItemBordero(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}

