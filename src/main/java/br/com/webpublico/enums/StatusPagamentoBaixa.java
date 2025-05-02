/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author cheles
 */
@GrupoDiagrama(nome = "RBTrans")
public enum StatusPagamentoBaixa {
    PAGO("Pago"),
    NAO_PAGO("Não Pago"),
    SEM_DEFINICAO("Sem Definição");
    private String descricao;

    private StatusPagamentoBaixa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
