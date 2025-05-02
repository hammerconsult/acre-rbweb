/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Fabio
 */
public enum TipoProcessoTramite {

    INTERNO("Interno"),
    EXTERNO("Externo");
    private String descricao;

    private TipoProcessoTramite(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
