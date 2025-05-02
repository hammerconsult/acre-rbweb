/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author gustavo
 */
public enum MotivoInativacaoImovel {

    UNIFICACAO("Unificação"),
    DESMEMBRAMENTO("Desmembramento");

    private String descricao;

    private MotivoInativacaoImovel(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return this.descricao;
    }
}
