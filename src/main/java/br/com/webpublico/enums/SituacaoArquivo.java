/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author gustavo
 */
public enum SituacaoArquivo {

    NAO_PROCESSADO("Não Processado"),
    PROCESSADO("Processado");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private SituacaoArquivo(String descricao) {
        this.descricao = descricao;
    }
}
