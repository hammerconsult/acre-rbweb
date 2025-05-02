/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author terminal3
 */
public enum TipoSanguineo {

    AP("A+"),
    BP("B+"),
    AN("A-"),
    BN("B-"),
    ABP("AB+"),
    ABN("AB-"),
    OP("O+"),
    ON("O-"),
    NAO_INFORMADO("Não Informado");
    private String sangue;

    private TipoSanguineo(String sangue) {
        this.sangue = sangue;
    }

    public String getSangue() {
        return sangue;
    }

    @Override
    public String toString() {
        return sangue;
    }
}
