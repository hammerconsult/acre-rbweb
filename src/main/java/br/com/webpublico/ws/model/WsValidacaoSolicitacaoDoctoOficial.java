package br.com.webpublico.ws.model;

import java.io.Serializable;

public class WsValidacaoSolicitacaoDoctoOficial implements Serializable {

    protected Boolean solicitou;
    protected String mensagem;

    public WsValidacaoSolicitacaoDoctoOficial() {
        this.solicitou = false;
    }

    public Boolean getSolicitou() {
        return solicitou;
    }

    public void setSolicitou(Boolean solicitou) {
        this.solicitou = solicitou;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
