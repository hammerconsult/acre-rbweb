/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Claudio
 */
public enum TipoInfracaoFiscalizacao {
    LEVE("Leve"),
    GRAVE("Grave"),
    GRAVISSIMA("Gravíssima");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoInfracaoFiscalizacao(String descricao) {
        this.descricao = descricao;
    }
}
