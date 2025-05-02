/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author William
 */
public enum TipoGravidade {

    LEVE("Leve"),
    MEDIA("Média"),
    GRAVE("Grave"),
    GRAVISSIMA("Gravíssima");
    private String descricao;

    private TipoGravidade(String descricao) {
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
        return descricao;
    }
}
