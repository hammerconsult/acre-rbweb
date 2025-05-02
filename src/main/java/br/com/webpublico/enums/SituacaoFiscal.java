/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Claudio
 */
public enum SituacaoFiscal {
    ATIVO("1 - Ativo"),
    INATIVO("2 - Inativo");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private SituacaoFiscal(String descricao) {
        this.descricao = descricao;
    }
}
