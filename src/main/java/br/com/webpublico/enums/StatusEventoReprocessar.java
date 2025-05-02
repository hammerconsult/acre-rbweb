/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author wiplash
 */
public enum StatusEventoReprocessar {

    PENDENTE("Pendente"),
    IGNORADO("Ignorado"),
    REPROCESSADO("Reprocessado");
    private String descricao;

    private StatusEventoReprocessar(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
