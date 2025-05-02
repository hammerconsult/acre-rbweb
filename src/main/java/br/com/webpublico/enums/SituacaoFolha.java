/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author peixe
 */
public enum SituacaoFolha {

    PROCESSADA("Processada"),
    EM_PROCESSAMENTO("Em Processamento"),
    PROCESSADA_COM_ERROS("Processada com Erros");
    private String descricao;

    private SituacaoFolha(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
