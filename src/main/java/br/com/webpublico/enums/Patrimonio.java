/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author daniel
 */

public enum Patrimonio {
    PARTICULAR("1-Particular"),
    MUNICIPAL("2-Municipal"),
    ESTADUAL("3-Estadual"),
    FEDERAL("4-Estadual"),
    RELIGIOSO("5-Religioso");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    private Patrimonio(String descricao) {
        this.descricao = descricao;
    }


}
