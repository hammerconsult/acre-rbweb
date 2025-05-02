/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author wiplash
 */
public enum TipoRegistroOBN600Tipo1 {

    UM("1"),
    CODIGO_DV_AGENCIA_BANCARIA_EMITENTE("Código/DV da agencia Bancaria da UG emitente."),
    CODIGO_UG_GESTAO_EMITENTE("Código da UG/Gestão emitente das Obs."),
    CONTA_UG_DV("Conta da UG/DV"),
    BRANCOS("Brancos"),
    NOME_UG("Nome da UG");

    private TipoRegistroOBN600Tipo1(String descricao) {
        this.descricao = descricao;
    }
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
