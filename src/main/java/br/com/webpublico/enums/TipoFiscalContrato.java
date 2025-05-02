/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Felipe Marinzeck
 */
public enum TipoFiscalContrato {
    INTERNO("Interno"),
    EXTERNO("Externo");

    private String descricao;

    TipoFiscalContrato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isInterno(){
        return INTERNO.equals(this);
    }

    public boolean isExterno(){
        return EXTERNO.equals(this);
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
