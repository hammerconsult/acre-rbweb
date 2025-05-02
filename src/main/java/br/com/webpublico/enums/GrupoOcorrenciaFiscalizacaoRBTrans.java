/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author AndreGustavo
 */
public enum GrupoOcorrenciaFiscalizacaoRBTrans {

    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    private GrupoOcorrenciaFiscalizacaoRBTrans(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
