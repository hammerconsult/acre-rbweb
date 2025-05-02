/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author william
 */
public enum TipoImovelSituacao {

    PROPRIO("1 - Próprio"),
    ALUGADO("2 - Alugado"),
    INVADIDO("3 - Invadido"),
    CEDIDO("4 - Cedido");
    private String descricao;

    private TipoImovelSituacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
