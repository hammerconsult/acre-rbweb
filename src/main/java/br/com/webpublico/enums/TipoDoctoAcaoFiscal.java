/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Claudio
 */
public enum TipoDoctoAcaoFiscal {
    ORDEMSERVICO("Ordem de Serviço"),
    AUTOINFRACAO("Auto de Infração"),
    HOMOLOGACAO("Termo de Homologação"),
    FISCALIZACAO("Termo de Fiscalização"),
    RELATORIO_FISCAL("Relatório Fiscal"),
    FINALIZACAO("Termo de Finalização");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoDoctoAcaoFiscal(String descricao) {
        this.descricao = descricao;
    }
}
