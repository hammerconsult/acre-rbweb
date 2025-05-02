/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author cheles
 */
public enum TipoParametroRBTrans {

    TAXI("Táxi"),
    MOTO_TAXI("Moto-Táxi"),
    FRETE("Frete"),
    TERMO("Termo"),
    VENCIMENTO("Vencimento do Licenciamento"),
    OUTORGA("Outorga"),
    FISCALIZACAO("Fiscalização");
    private String descricao;

    private TipoParametroRBTrans(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
