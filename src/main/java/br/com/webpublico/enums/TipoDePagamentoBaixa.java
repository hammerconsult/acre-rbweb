/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Heinz
 */

public enum TipoDePagamentoBaixa {

    BAIXA_MANUAL("Baixa Manual"),
    INTERNET_BANKING("Internet Banking"),
    BAIXA_ARQUIVO_RETORNO("Baixa por Arquivo de Retorno");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoDePagamentoBaixa(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }


}
