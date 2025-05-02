/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author major
 */
public enum AgrupadorNota {

    EMPENHO_ID("Empenho"),
    LIQUIDACAO_ID("Liquidação"),
    HISTORICO_ID("Histórico"),
    ORGAO_ID("Orgão"),
    UNIDADE_ID("Unidade"),
    NUMERO("Número"),
    TIPO("Tipo"),
    DESPESAORC_ID("Despesa Orçamentária"),
    DATAEMPENHO("Data do Empenho"),
    DATALIQUIDACAO("Data da Liquidação"),
    DATAPAGAMENTO("Data do Pagamento"),
    PESSOA_ID("Fornecedor"),
    FONTEDESPESAORC_ID("Fonte de Despesa"),
    NUMEROPAGAMENTO("Número do Pagamento"),
    STATUS("Status"),
    SUBCONTA_ID("Sub Conta"),
    EXERCICIO_ID("Exercício");
    private String descricao;

    private AgrupadorNota(String descricao) {
        this.descricao = descricao;
    }


    public String getDescricao() {
        return descricao;
    }

}
