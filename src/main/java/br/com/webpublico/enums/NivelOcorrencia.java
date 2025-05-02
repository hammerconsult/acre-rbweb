/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author daniel
 */
public enum NivelOcorrencia {

    ERRO("Erro"),
    INFORMACAO("Informação"),
    ALERTA("Alerta");

    private String descricao;

    NivelOcorrencia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
