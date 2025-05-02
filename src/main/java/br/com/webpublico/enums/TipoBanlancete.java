/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author major
 */
public enum TipoBanlancete {
    TRANSPORTE("Transporte"),
    ABERTURA("Abertura"),
    MENSAL("Mensal"),
    APURACAO("Apuração"),
    ENCERRAMENTO("Encerramento");

    private String descricao;

    private TipoBanlancete(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }


}
