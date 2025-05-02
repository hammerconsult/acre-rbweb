/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Wellington
 */
public enum SituacaoCalculoDividaDiversa {

    NOVO("Novo"),
    EM_ABERTO("Em Aberto"),
    EFETIVADO("Efetivado"),
    CANCELADO("Cancelado"),
    AGUARDANDO("Aguardando"),
    DIVIDA_ATIVA("Dívida Ativa"),
    REFIS("Refis"),
    PAGO_REFIS("Pago_Refis"),
    ESTORNADO("Estornado");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private SituacaoCalculoDividaDiversa(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;

    }
}
