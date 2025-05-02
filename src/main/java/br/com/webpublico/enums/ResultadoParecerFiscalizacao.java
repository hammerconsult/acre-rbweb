/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Leonardo
 */
public enum ResultadoParecerFiscalizacao {

    INDEFERIDO("Indeferido"),
    DEFERIDO("Deferido");

    private String descricao;

    private ResultadoParecerFiscalizacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
