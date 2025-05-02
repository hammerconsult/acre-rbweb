/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author fabio
 */
public enum TipoGrupoUsuario {

    AUTORIZACAO(true),
    NEGACAO(false);
    private final boolean permitido;
    private String descricao;

    private TipoGrupoUsuario(boolean permitido) {
        this.permitido = permitido;
    }

    public boolean isPermitido() {
        return permitido;
    }

    public String getDescricao() {
        if (this.isPermitido()) {
            descricao = "Autorização";
        } else {
            descricao = "Negação";
        }
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
