/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author java
 */
public enum TipoIndicador {

    MOEDA ("Moeda"),
    INDICE_PERCENTUAL ("Indice Percentual"),
    INDICE_VALOR ("Indice Valor");

    private String descricao;

    private TipoIndicador(String descricao){
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
