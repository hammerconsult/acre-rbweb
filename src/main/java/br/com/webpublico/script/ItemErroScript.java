/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script;

import br.com.webpublico.enums.TiposErroScript;

/**
 * @author gecen
 */
public class ItemErroScript {


    private TiposErroScript tiposErroScript;
    private String nomeFunction;
    private String mensagem;
    private String mensagemException;

    public ItemErroScript(TiposErroScript tiposErroScript, String nomeFunction, String mensagem, String mensagemException) {
        this.tiposErroScript = tiposErroScript;
        this.nomeFunction = nomeFunction;
        this.mensagem = mensagem;
        this.mensagemException = mensagemException;
    }

    public TiposErroScript getTiposErroScript() {
        return tiposErroScript;
    }

    public void setTiposErroScript(TiposErroScript tiposErroScript) {
        this.tiposErroScript = tiposErroScript;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagemException() {
        return mensagemException;
    }

    public void setMensagemException(String mensagemException) {
        this.mensagemException = mensagemException;
    }

    public String getNomeFunction() {
        return nomeFunction;
    }

    public void setNomeFunction(String nomeFunction) {
        this.nomeFunction = nomeFunction;
    }


}
