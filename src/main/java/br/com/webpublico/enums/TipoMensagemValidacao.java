/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author GhouL
 */
public enum TipoMensagemValidacao {
    INFORMACAO("SEVERITY_INFO"),
    ALERTA("SEVERITY_WARN"),
    ERRO("SEVERITY_ERROR"),
    ERRO_FATAL("SEVERITY_FATAL");

    String descricao;

    private TipoMensagemValidacao(String descricao) {
        this.descricao = descricao;
    }
}
