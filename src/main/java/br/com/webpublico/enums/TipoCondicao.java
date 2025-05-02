/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Gustavo
 */
public enum TipoCondicao {

    IGUAL("="),
    MENOR("<"),
    MENORIGUAL("<="),
    MAIOR(">"),
    MAIORIGUAL(">=");
    String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoCondicao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
