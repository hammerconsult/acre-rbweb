/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author Camila
 */
@GrupoDiagrama(nome = "RecursosHumanos")
public enum TipoFolhaDePagamentoSicap {
    MENSAL("Mensal"),
    SALARIO_13("13° Salário");

    private String descricao;

    TipoFolhaDePagamentoSicap(String descricao) {
        this.descricao = descricao;
    }

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
