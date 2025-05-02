/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author cheles
 */
public enum TipoFiltroCredencialRBTrans {

    PERMISSAO("Permissão"),
    CMC("CMC");
    private String descricao;

    private TipoFiltroCredencialRBTrans(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
