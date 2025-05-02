/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;


/**
 * @author claudio
 */
public enum StatusIrregularidade {

    EMBARGADO("Embargado"),
    NAO_LICENCIADO("Não Licenciado"),
    LICENCIADO("Licenciado");
    private String descricao;

    private StatusIrregularidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
