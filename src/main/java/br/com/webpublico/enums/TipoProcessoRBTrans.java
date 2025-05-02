/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author André Gustavo
 */
public enum TipoProcessoRBTrans {

    AUTUACAO("Autuação"),
    PENALIDADE("Penalidade"),
    MULTA("Multa"),
    INFORMATIVO("Informativo"),
    CONSELHO("Conselho");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoProcessoRBTrans(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
