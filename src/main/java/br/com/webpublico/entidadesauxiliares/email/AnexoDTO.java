package br.com.webpublico.entidadesauxiliares.email;

import java.io.Serializable;

public class AnexoDTO implements Serializable {

    private byte[] anexo;
    private String nome;
    private String mimeType;

    public byte[] getAnexo() {
        return anexo;
    }

    public void setAnexo(byte[] anexo) {
        this.anexo = anexo;
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
}
