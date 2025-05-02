/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author gustavo
 */
public enum TipoDistribuicaoEmolumento {

    PRIMEIRAPARCELA("Primeira Parcela"),
    RATEIO_ENTRE_PARCELAS("Rateio entre as Parcelas");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoDistribuicaoEmolumento(String descricao) {
        this.descricao = descricao;
    }
}
