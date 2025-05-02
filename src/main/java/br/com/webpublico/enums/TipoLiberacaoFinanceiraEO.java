/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author venon
 */
public enum TipoLiberacaoFinanceiraEO {

    COTA("Cota"),
    REPASSE("Repasse"),
    SUBREPASSE("Sub-repasse"),
    REPASSEPREVIDENCIARIO("Repasse Previdenciário"),
    TRANSFERENCIASDIVERSAS("Transferências diversas concedidas e recebidas");
    private String descricao;

    private TipoLiberacaoFinanceiraEO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
