/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author reidocrime
 */
public enum TipoDiaNaoUtil {
    FERIADO_NACIONAL("Feriado Nacional"),
    FERIADO_ESTADUAL("Feriado Estadual"),
    FERIADO_MUNICIPAL("Feriado Municipal"),
    RECESSO("Recesso");

    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    private TipoDiaNaoUtil(String descricao) {
        this.descricao = descricao;
    }
}
