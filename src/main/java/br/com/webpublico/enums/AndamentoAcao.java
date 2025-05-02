/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

/**
 * @author arthur
 */
public enum AndamentoAcao {
    ANDAMENTO("Andamento"),
    NOVA("Nova");
    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    private AndamentoAcao(String descricao) {
        this.descricao = descricao;
    }
}
