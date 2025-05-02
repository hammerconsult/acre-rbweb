/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author java
 */
public enum PeriodicidadeIndicador {

    MENSAL ("Mensal"),
    DIARIA ("Diária"),
    ANUAL ("Anual");
    private String descricao;

    private PeriodicidadeIndicador(String descricao){
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }


}
