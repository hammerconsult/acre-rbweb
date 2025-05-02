/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author venon
 */
public enum TipoDesdobramento {

    EMPENHO("Empenho"),
    LIQUIDACAO("Liquidação");
    private String descricao;

    private TipoDesdobramento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
