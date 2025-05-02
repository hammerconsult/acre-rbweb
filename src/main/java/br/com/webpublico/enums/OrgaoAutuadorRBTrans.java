/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author André Gustavo
 */
public enum OrgaoAutuadorRBTrans {

    ORGAO_101_100("101-100"),
    ORGAO_101_200("101-200"),
    ORGAO_201_390("201-390");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private OrgaoAutuadorRBTrans(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
