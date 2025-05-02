/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author arthur
 */
public enum EsferaDoPoder {

    EXECUTIVO("Executivo", "1", 1, "E"),
    LEGISLATIVO("Legislativo", "2", 3, "L"),
    JUDICIARIO("Judiciário", "3", 2, "J");
    private String descricao;
    private String codigoSiprev;
    private Integer codigoESocial;
    private String codigoPncp;

    EsferaDoPoder(String descricao, String codigoSiprev, Integer codigoESocial, String codigoPncp) {
        this.descricao = descricao;
        this.codigoSiprev = codigoSiprev;
        this.codigoESocial = codigoESocial;
        this.codigoPncp = codigoPncp;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigoSiprev() {
        return codigoSiprev;
    }

    public Integer getCodigoESocial() {
        return codigoESocial;
    }

    @JsonValue
    public String getCodigoPncp() {
        return codigoPncp;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isExecutivo(){
        return EsferaDoPoder.EXECUTIVO.equals(this);
    }

    public boolean isLegislativo(){
        return EsferaDoPoder.LEGISLATIVO.equals(this);
    }
}

