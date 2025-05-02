package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 28/08/13
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */
public enum StatusReadaptacao {

    CESSADA("Cessada"),
    PRORROGADA("Prorrogada"),
    PERMANENTE("Caráter Permanente");


    private StatusReadaptacao(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
