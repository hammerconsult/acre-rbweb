/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author gustavo
 */
public enum TipoValorAdicional {
    VALOR("Valor"),
    PERCENTUAL_FIXO("Percentual Fixo"),
    PERCENTUAL_MULTIPLICADO("Percentual Mutiplicado pelo Número de Parcelas");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoValorAdicional(String descricao) {
        this.descricao = descricao;
    }
}
