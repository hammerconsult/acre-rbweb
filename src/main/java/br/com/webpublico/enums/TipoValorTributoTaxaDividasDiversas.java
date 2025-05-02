/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Claudio
 */
public enum TipoValorTributoTaxaDividasDiversas {

    FIXO("Fixo"),
    VARIAVEL("Variável");

    private TipoValorTributoTaxaDividasDiversas(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
