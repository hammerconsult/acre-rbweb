/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author gustavo
 */
public enum TipoPeriodoValorEstimado {

    MENSAL("Mensal"),
    ANUAL("Anual");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoPeriodoValorEstimado(String descricao) {
        this.descricao = descricao;
    }
}
