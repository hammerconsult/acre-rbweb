/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author peixe
 */
public enum TipoHierarquia {

    SECRETARIA("Secretaria"),
    OUTROS("Outros");
    private String descricao;

    private TipoHierarquia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
