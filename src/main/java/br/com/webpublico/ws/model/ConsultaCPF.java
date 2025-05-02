package br.com.webpublico.ws.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: jujuba
 * Date: 11/01/15
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class ConsultaCPF implements Serializable {

    private boolean valido;
    private String mensagem;
    private WSPessoa wsPessoa;

    public ConsultaCPF() {
    }

    public ConsultaCPF(String mensagem, boolean valido) {
        this.mensagem = mensagem;
        this.valido = valido;
    }

    public ConsultaCPF(boolean valido, String mensagem, WSPessoa wsPessoa) {
        this.valido = valido;
        this.mensagem = mensagem;
        this.wsPessoa = wsPessoa;
    }

    public WSPessoa getWsPessoa() {
        return wsPessoa;
    }

    public void setWsPessoa(WSPessoa wsPessoa) {
        this.wsPessoa = wsPessoa;
    }

    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
