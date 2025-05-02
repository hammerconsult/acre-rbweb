/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Renato
 */
public enum OperacaoDiarioDividaPublica {

    MOVIMENTO("Movimento"),
    EMPENHO("Empenho"),
    RECEITA_REALIZADA("Receita Realizada");
    private String descricao;

    private OperacaoDiarioDividaPublica(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
