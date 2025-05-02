/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author fabio
 */
@GrupoDiagrama(nome = "Fiscalizacao")
public enum TipoCalculoMultaFiscalizacao {

    ANUAL("Anual"),
    MENSAL("Mensal");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoCalculoMultaFiscalizacao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
