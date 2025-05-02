/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author André Gustavo
 */
public enum TipoCiencia {

    CORRESPONDENCIA("Correspondência"),
    DIARIO_OFICIAL("Diário Oficial"),
    OFFICE_BOY("Office Boy");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoCiencia(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
