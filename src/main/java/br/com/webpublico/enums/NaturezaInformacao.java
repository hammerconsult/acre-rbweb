/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Renato Romanini
 */
public enum NaturezaInformacao implements EnumComDescricao {
    PATRIMONIAL("Patrimonial", "P"),
    ORCAMENTARIO("Orçamentário", "O"),
    CONTROLE("Controle", "C");
    private String descricao;
    private String codigo;

    NaturezaInformacao(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
