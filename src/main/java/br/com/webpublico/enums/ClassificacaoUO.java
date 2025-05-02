/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author major
 */
public enum ClassificacaoUO {
    PODEREXECUTIVO("Poder Executivo"),
    PODERLEGISLATIVO("Poder Legislátivo"),
    RPPS("RPPS"),
    DEMAISENTIDADES("Demais Entidades");
    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    private ClassificacaoUO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


}
