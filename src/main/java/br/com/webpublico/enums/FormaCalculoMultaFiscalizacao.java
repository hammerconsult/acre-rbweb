/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author fabio
 */
@GrupoDiagrama(nome = "Fiscalizacao")
public enum FormaCalculoMultaFiscalizacao implements EnumComDescricao {

    QUANTIDADE("Quantidade"),
    VALOR("Valor");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private FormaCalculoMultaFiscalizacao(String descricao) {
        this.descricao = descricao;
    }
}
