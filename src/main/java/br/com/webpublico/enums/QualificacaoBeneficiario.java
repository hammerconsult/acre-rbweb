/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author venon
 */
public enum QualificacaoBeneficiario {

    INCAPAZ("Incapaz"),
    ESPOLIO("Espólio"),
    MASSA_FALIDA("Massa Falida"),
    MENOR("Menor"),
    OUTRO("Outro");
    private String descricao;

    private QualificacaoBeneficiario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
