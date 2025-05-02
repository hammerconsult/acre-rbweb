/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Terminal-2
 */
public enum UtilizacaoImovel {

    RESIDENCIAL("1-Residencial "), COMERCIAL("2-Comercial");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private UtilizacaoImovel(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
