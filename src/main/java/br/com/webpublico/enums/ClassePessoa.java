/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Wellington
 */
public enum ClassePessoa {
    EXTRA("Extra - Contribuinte Normal"),
    INTRA("Intra - Orgão Municipal"),
    INTER("Inter - Orgão Estadual/Federal");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private ClassePessoa(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
