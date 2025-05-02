/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author peixe
 */
public enum TipoPromocao {

    ANTIGUIDADE("Antiguidade", 1),
    MERECIMENTO("Merecimento", 2);
    private String descricao;
    private Integer numero;

    private TipoPromocao(String descricao, Integer numero) {
        this.descricao = descricao;
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }
}
