/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author terminal1
 */
public enum TipoTelefone {

    RESIDENCIAL("Residêncial"),
    FAX("Fax"),
    FIXO("Fixo"),
    CELULAR("Celular"),
    OUTROS("Outros"),
    COMERCIAL("Comercial"),
    RECADO("Recado");
    private String tipoFone;

    private TipoTelefone(String tipoFone) {
        this.tipoFone = tipoFone;
    }

    public String getTipoFone() {
        return tipoFone;
    }

    public void setTipoFone(String tipoFone) {
        this.tipoFone = tipoFone;
    }
}
