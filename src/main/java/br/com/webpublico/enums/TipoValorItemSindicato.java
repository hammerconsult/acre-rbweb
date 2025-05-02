/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author andre
 */
public enum TipoValorItemSindicato {

    VALOR_FIXO("Valor Fixo"),
    VALOR_PERCENTUAL("Valor Percentual"),
    VALOR_DIA_TRABALHADO("Valor Dia Trabalhado");
    private String descricao;

    private TipoValorItemSindicato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
