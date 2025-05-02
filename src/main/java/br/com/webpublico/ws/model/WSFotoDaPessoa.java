package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoParte;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: scarpini
 * Date: 29/03/14
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
public class WSFotoDaPessoa implements Serializable {

    protected Long id;
    private byte[] foto;
    private String nome;
    private String mime;

    public WSFotoDaPessoa() {
    }

    public WSFotoDaPessoa(Arquivo arquivo) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : arquivo.getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        foto = buffer.toByteArray();
        nome = arquivo.getNome();
        mime = arquivo.getMimeType();
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
