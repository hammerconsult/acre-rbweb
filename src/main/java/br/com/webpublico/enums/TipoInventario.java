/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

/**
 * @author arthur
 */
public enum TipoInventario {
    AMOSTRAGEM("Amostragem"),
    GERAL("Geral");
    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    private TipoInventario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
