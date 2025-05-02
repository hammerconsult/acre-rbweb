package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class FavoritoDTO implements Serializable {
    private Long id;
    private String url;
    private String nome;

    public FavoritoDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
