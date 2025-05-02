/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author lucas
 */
public enum TipoRecursoLicitacao {

    PEDIDO_DE_IMPUGNACAO("Pedido de Impugnação"),
    RECURSO("Recurso"),
    ESCLARECIMENTO("Esclarecimento");

    private String descricao;

    private TipoRecursoLicitacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
