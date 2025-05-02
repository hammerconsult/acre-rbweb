/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Terminal-2
 */
public enum TipoPrazoParcela {

    DIARIO("Diário"),
    MENSAL("Mensal");

    private String descricao;

    private TipoPrazoParcela(String descricao) {
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
