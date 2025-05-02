/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author daniel
 */
public enum TipoOcorrencia {

    SIMULACAO("Simulação"),
    CALCULO("Cálculo"),
    EFETIVACAO("Efetivação");

    private String descricao;

    TipoOcorrencia(String descricao) {
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
