/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

@GrupoDiagrama(nome = "RecursosHumanos")
public enum TipoEmpenhoFP {
    POR_CONTRATO("Por Contrato"),
    POR_PESSOA("Por Pessoa"),
    POR_FONTE("Por Fonte");
    private String descricao;

    TipoEmpenhoFP(String descricao) {
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
