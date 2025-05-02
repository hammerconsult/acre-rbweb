/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Renato
 */
public enum TipoVencimentoParcela {

    NAO_ALTERAR("Não alterar"),
    PRIMEIRA_EM_ABERTO("Primeira em aberto"),
    PRIMEIRO_VENCIMENTO("Primeiro vencimento");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoVencimentoParcela(String descricao) {
        this.descricao = descricao;
    }
}
