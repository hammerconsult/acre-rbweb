/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author boy
 */
public enum DescansoSemanal {

    SABADO_DOMINGO("Sábado e Domingo"),
    DOMINGO("Domingo");
    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    private DescansoSemanal(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
