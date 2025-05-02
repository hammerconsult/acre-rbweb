/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author venon
 */
public enum OrigemRecurso {

    ORCAMENTARIO("Orçamentário"),
    EXTRAORCAMENTARIO("Extraorçamentário");
    private String descricao;

    OrigemRecurso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
