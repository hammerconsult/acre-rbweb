/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Claudio
 */
public enum SituacaoLancamento {
    AGUARDANDO("Aguardando"),
    PROCESSADO("Processado"),
    CANCELADO("Cancelado");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private SituacaoLancamento(String descricao) {
        this.descricao = descricao;
    }


}
