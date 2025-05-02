package br.com.webpublico.ws.model;

import java.io.Serializable;

public class FotoPessoaDTO implements Serializable {

    private Long id;
    private String foto;

    public FotoPessoaDTO() {
    }

    public FotoPessoaDTO(Long id, String foto) {
        this.id = id;
        this.foto = foto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
