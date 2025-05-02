/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author andre
 */
public enum TipoReferenciaFP {

    FAIXA("Faixa"),
    VALOR_PERCENTUAL("Valor Percentual"),
    VALOR_VALOR("Valor Valor");
    private String descricao;

    private TipoReferenciaFP(String d) {
        descricao = d;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
