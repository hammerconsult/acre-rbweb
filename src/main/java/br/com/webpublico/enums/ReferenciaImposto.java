/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Terminal-2
 */
public enum ReferenciaImposto {

    IMPOSTO("Imposto"), TSU("TSU"), IMPOSTO_TSU("Imposto + TSU");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private ReferenciaImposto(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
