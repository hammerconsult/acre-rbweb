/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author boy
 */
public enum TipoValorAssociacao {

    VALOR_PERCENTUAL("Porcentagem"),
    VALOR_FIXO("Valor Fixo");

    private TipoValorAssociacao(String descricao) {
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
