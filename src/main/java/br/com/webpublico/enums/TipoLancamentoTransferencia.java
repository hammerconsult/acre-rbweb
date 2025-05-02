/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author juggernaut
 */
public enum TipoLancamentoTransferencia {

    NORMAL("Normal"),
    ESTORNO("Estorno");

    private TipoLancamentoTransferencia(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }
}
