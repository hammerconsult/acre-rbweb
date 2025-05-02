/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author boy
 */
public enum TipoPensionista {

    PENSIONISTA("Pensionista"),
    PENSIONISTA_SERVIDOR("Pensionista/Servidor"),
    PENSIONISTA_APOSENTADO("Pensionista/Aposentado"),
    PENSIONISTA_APOSENTADO_SERVIDOR("Pensionista/Aposentado/Servidor");
    private String descricao;

    private TipoPensionista(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
