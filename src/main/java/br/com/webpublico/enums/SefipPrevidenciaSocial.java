/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author andre
 */
public enum SefipPrevidenciaSocial {

    NO_PRAZO("1", "No Prazo"),
    EM_ATRASO("2", "Em Atraso"),
    NAO_GERA("3", "Não Gera");

    private String codigo;
    private String descricao;

    private SefipPrevidenciaSocial(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
