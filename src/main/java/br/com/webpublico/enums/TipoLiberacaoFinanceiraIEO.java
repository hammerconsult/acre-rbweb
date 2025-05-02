/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author venon
 */
public enum TipoLiberacaoFinanceiraIEO {

    COTA_DIFERIDA("Cota Diferida"),
    REPASSE_DIFERIDO("Repasse Diferido"),
    SUBREPASSE_DIFERIDO("Sub-repasse Diferido"),
    REPASSE_PREVIDENCIARIO_DIFERIDO("Repasse Previdenciário Diferido"),
    TRANSFERENCIAS_DIVERSAS_CONCEDIDAS_RECEBIDAS("Transferências Diversas Concedidas e Recebidas");
    private String descricao;

    private TipoLiberacaoFinanceiraIEO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
