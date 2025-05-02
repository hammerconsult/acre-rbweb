/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Paschualleto
 */
public enum FonteRecursoOperCredito {

    INTERNO("Interno"),
    EXTERNO("Externo");

    private String descricao;

    private FonteRecursoOperCredito(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
