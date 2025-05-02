package br.com.webpublico.enums;

/**
 * @author fabio
 */
public enum TipoDiferencaContaCorrente {

    CREDITO_ARRECADACAO("Crédito da Arrecadação"),
    RESIDUO_ARRECADACAO(" Resíduo da Arrecadação");
    private String descricao;

    private TipoDiferencaContaCorrente(String descricao) {
        this.descricao = descricao;
    }


    public String getDescricao() {
        return descricao;
    }

}
