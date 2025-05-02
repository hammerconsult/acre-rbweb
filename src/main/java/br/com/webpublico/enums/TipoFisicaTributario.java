/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author gustavo
 */
@GrupoDiagrama(nome = "Tributario")
public enum TipoFisicaTributario {

    AUTONOMO("Autônomo"),
    AMBULANTE("Ambulante"),
    FEIRANTE("Feirante"),
    PRODUCAO_AGRICOLA("Produtor Agropecuário"),
    OUTROS("Outros");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoFisicaTributario(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
