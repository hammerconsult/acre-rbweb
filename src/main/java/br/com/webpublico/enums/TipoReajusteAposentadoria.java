/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author andre
 */
public enum TipoReajusteAposentadoria {

    MEDIA("Média"),
    PARIDADE("Paridade");
    private String descricao;

    private TipoReajusteAposentadoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
