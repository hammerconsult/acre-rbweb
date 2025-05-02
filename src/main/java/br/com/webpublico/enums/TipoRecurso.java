/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

public enum TipoRecurso {

    ORCAMENTARIO("Orçamentário"),
    EXTRAORCAMENTARIO("Extra-orçamentário");

    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    private TipoRecurso(String descricao) {
        this.descricao = descricao;
    }
}
