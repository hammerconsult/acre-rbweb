/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author major
 */
public enum TipoReceitaExtra {

    NORMAL("Normal"),
    CONSIGNACAO_EXERCICIO("Consignação do exercício"),
    CONSIGNACAO_RESTO_PAGAR_PROCESSADOS("Consignação de restos à pagar processados"),
    CONSIGNACAO_RESTO_PAGAR_NAO_PROCESSADOS("Consignação de restos à pagar não processados");
    private String descricao;

    private TipoReceitaExtra(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
