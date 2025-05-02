/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author claudio
 */
public enum StatusVistoria {

    ABERTA("Aberta"),
    FINALIZADA("Finalizada"),
    TRAMITANDO("Tramitando");

    private String descricao;

    private StatusVistoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
