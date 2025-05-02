/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.migracao;

import java.util.Date;

/**
 * @author Munif
 */
public class MensagemMigracao {

    private Date quando;
    private String mensagem;

    public MensagemMigracao() {
        this.quando = new Date();
    }

    public MensagemMigracao(String mensagem) {
        this.quando = new Date();
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Date getQuando() {
        return quando;
    }

    public void setQuando(Date quando) {
        this.quando = quando;
    }
}
