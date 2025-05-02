/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author Renato
 */
@GrupoDiagrama(nome = "Divida Ativa")
public enum SituacaoInscricaoDividaAtiva {

    ABERTO("ABERTO"),
    FINALIZADO("FINALIZADO"),
    CANCELADO("CANCELADO");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private SituacaoInscricaoDividaAtiva(String descricao) {
        this.descricao = descricao;
    }
}
