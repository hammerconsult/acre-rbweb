/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

public enum TipoFatoContabil {
    NORMAL("Normal"),
    ESTORNO("Estorno");
    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    private TipoFatoContabil(String descricao) {
        this.descricao = descricao;
    }
}
