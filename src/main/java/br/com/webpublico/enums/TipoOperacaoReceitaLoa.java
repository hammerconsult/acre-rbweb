/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author major
 */
public enum TipoOperacaoReceitaLoa {

    PREVISAO("Previsão"),
    ESTORNO_PREVISAO("Estorno da Previsão");
    private String descricao;

    private TipoOperacaoReceitaLoa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
