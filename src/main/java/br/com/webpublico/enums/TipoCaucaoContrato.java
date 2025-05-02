/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Felipe Marinzeck
 */
public enum TipoCaucaoContrato {
    EM_DINHEIRO("Em Dinheiro"),
    TITULOS_DA_DIVIDA_PUBLICA("Em Títulos da Dívida Pública"),
    SEGURO_GARANTIA("Seguro Garantia"),
    FIANCA_BANCARIA("Fiança Bancária");

    private String descricao;

    private TipoCaucaoContrato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
