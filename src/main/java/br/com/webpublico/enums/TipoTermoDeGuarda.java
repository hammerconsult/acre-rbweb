/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

/**
 * @author arthur
 */
public enum TipoTermoDeGuarda {

    INDIVIDUAL("Individual"),
    COLETIVO("Coletivo");
    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    private TipoTermoDeGuarda(String descricao) {
        this.descricao = descricao;
    }
}

