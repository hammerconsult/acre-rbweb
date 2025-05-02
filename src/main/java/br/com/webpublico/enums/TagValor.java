/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author major
 */
public enum TagValor {

    LANCAMENTO("Lançamento"),
    DESDOBRAMENTO("Desdobramento da Despesa"),
    RETENCAO("Retenção");
    private String descricao;

    private TagValor(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
