/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author arthur
 */
@GrupoDiagrama(nome = "Componente")
public enum TipoFormulaItemDemonstrativo {

    PROJETADO("Projetado"),
    REALIZADO("Realizado");
    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    private TipoFormulaItemDemonstrativo(String descricao) {
        this.descricao = descricao;
    }
}
