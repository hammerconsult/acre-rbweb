/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author Jaime
 */
@GrupoDiagrama(nome = "CadastroImobiliario")
public enum TipoProprietario {

    HERANÇA("Herança"),
    COMPRAVENDA("Compra e Venda"),
    PROCESSO("Processo");

    private String proprietario;
    private String descricao;

    private TipoProprietario(String p) {
        proprietario = p;
        descricao = p;
    }

    public String getTipoProprietario() {
        return proprietario;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return proprietario;
    }
}
