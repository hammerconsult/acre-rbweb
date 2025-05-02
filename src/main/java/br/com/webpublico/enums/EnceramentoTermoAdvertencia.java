/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author Renato
 */
@GrupoDiagrama(nome = "fiscalizacaogeral")
public enum EnceramentoTermoAdvertencia {

    EXIGENCIA_CUMPRIDAS("Exigência foram cumpridas"),
    INFRACOES_CONSIGNADAS_AUTO_INFRACAO("Infrações consignadas no auto da Infração");
    private String descricao;

    private EnceramentoTermoAdvertencia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
