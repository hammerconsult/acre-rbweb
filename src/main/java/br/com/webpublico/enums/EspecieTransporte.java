/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author cheles
 */
public enum EspecieTransporte {

    TAXI("Táxi"),
    MOTO_TAXI("Moto-Táxi"),
    FRETE("Frete"),
    TRANSPORTE_COLETIVO("Transporte Coletivo"),
    MOTOT_FRETE("Moto-Frete"),
    CLANDESTINO("Clandestino");
    private String descricao;

    private EspecieTransporte(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
