/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Felipe Marinzeck
 */
public enum TipoPenalidadeContrato {
    ADVERTENCIA("Advertência"),
    MULTA("Multa"),
    SUSPENSAO_TEMPORARIA("Suspensão Temporária"),
    DECLARACAO_INIDONEIDADE("Declaração de Inidoneidade");

    private String descricao;

    private TipoPenalidadeContrato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
