/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author magneto
 */
public enum ClasseDaConta {

    CONTABIL("Contábil"),
    DESTINACAO("Destinação"),
    DESPESA("Despesa"),
    RECEITA("Receita"),
    AUXILIAR("Auxiliar"),
    EXTRAORCAMENTARIA("Extraorçamentária");
    private String descricao;

    ClasseDaConta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isClasseContabil() {
        return ClasseDaConta.CONTABIL.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
