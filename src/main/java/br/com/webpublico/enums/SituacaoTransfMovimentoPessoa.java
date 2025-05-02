/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Edi
 */
public enum SituacaoTransfMovimentoPessoa {

    ABERTA("Aberta"),
    DEFERIDA("Deferida"),
    INDEFERIDA("Indeferida");
    private String descricao;

    SituacaoTransfMovimentoPessoa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
