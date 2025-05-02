package br.com.webpublico.ws.model;

import java.io.Serializable;

/**
 * Created by Buzatto on 07/03/2017.
 */
public class WSUF implements Serializable {

    private Long id;
    private String uf;
    private String nome;

    public WSUF() {
    }

    public WSUF(Long id, String uf, String nome) {
        this.id = id;
        this.uf = uf;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WSUF wsuf = (WSUF) o;

        if (id != null ? !id.equals(wsuf.id) : wsuf.id != null) return false;
        if (uf != null ? !uf.equals(wsuf.uf) : wsuf.uf != null) return false;
        return !(nome != null ? !nome.equals(wsuf.nome) : wsuf.nome != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uf != null ? uf.hashCode() : 0);
        result = 31 * result + (nome != null ? nome.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WSUF{" +
            "id=" + id +
            ", uf='" + uf + '\'' +
            ", nome='" + nome + '\'' +
            '}';
    }
}
