/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author mga
 */
public enum TipoConsignacao {

    NAO_APLICAVEL("Não Aplicável"),
    EXERCICIO("Consignação do Exercício"),
    RESTO_PAGAR_PROCESSADO("Consignação de Restos à pagar Processado"),
    RESTO_PAGAR_NAO_PROCESSADO("Consignação de Restos à pagar não Processado"),
    CONSIGNACAO_AMORTIZACAO_PRECATORIO_EXERCICIO("Consignação de Amortização de Precatórios do Exercício"),
    CONSIGNACAO_AMORTIZACAO_DIVIDA_PUBLICA_EXERCICIO("Consignação de Amortização de Dívida Pública do Exercício"),
    CONSIGNACAO_JUROS_DIVIDA_PUBLICA_EXERCICIO("Consignação de Juros de Dívida Pública do Exercício"),
    CONSIGNACAO_ENCARGOS_DIVIDA_PUBLICA_EXERCICIO("Consignação de Encargos de Dívida Pública do Exercício");

    private String descricao;

    TipoConsignacao(String descricao) {
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
