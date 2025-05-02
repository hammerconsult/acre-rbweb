/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author andre
 */
public enum ClasseAfastamento {
    FALTA_JUSTIFICADA("Falta Justificada"),
    FALTA_INJUSTIFICADA("Falta Injustificada"),
    AFASTAMENTO("Afastamento");

    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    private ClasseAfastamento(String descricao) {
        this.descricao = descricao;
    }
}
