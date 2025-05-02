package br.com.webpublico.ws.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 19/08/15
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public class WSArquivo implements Serializable {

    private String nome;
    private String mimeType;
    private byte[] dados;

    public WSArquivo() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getDados() {
        return dados;
    }

    public void setDados(byte[] dados) {
        this.dados = dados;
    }
}
