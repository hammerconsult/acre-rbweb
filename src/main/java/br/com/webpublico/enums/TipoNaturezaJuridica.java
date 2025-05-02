/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author Wellington
 */
public enum TipoNaturezaJuridica {

    JURIDICA("Jurídica"),FISICA("Física");

    private String descricao;

    private TipoNaturezaJuridica(String descricao) {
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
