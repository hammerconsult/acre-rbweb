/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Claudio
 */
public enum TipoCadastroFiscalizacao {
    IMOBILIARIO("Imobiliário"),
    ECONOMICO("Econômico"),
    RURAL("Rural"),
    PESSOA("Pessoa");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoCadastroFiscalizacao(String descricao) {
        this.descricao = descricao;
    }
}
