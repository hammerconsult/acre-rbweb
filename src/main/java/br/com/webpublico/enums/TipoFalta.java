/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author peixe
 */
public enum TipoFalta {

    FALTA_JUSTIFICADA("Falta Justificada"),
    FALTA_INJUSTIFICADA("Falta Injustificada");
    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    private TipoFalta(String descricao) {
        this.descricao = descricao;
    }
}
