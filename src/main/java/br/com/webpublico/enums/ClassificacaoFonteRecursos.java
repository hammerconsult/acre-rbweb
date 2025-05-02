/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author major
 */
public enum ClassificacaoFonteRecursos {

    ORDINARIA("Ordinária"),
    VINCULADA("Vinculada");
    private String descricao;

    private ClassificacaoFonteRecursos(String descricao) {
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
