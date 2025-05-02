/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author claudio
 */
@GrupoDiagrama(nome = "CadastroEconomico")
public enum StatusAidf implements EnumComDescricao {
    ABERTO("Aberto"),
    BAIXADO("Baixado"),
    DECLARADA("Declarada"),
    EXTRAVIADA("Extraviada"),
    INATIVA("Inativa"),
    CANCELADO("Cancelado");

    private String descricao;

    private StatusAidf(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


}
