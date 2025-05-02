/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author magraowar
 */
public enum TipoInscricaoContribuinte {
    CONTRIBUINTE("Contribuinte"),
    INSCRICAO("Inscrição");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoInscricaoContribuinte(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
