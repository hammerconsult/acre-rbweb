/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author munif
 */
@Entity

public class CEPLocalidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nome;
    private String nomeSimples;
    private String tipo;
    private String cep;
    @ManyToOne
    private CEPUF uf;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeSimples() {
        return nomeSimples;
    }

    public void setNomeSimples(String nomeSimples) {
        this.nomeSimples = nomeSimples;
    }

    public CEPUF getUf() {
        return uf;
    }

    public void setUf(CEPUF uf) {
        this.uf = uf;
    }

    public CEPUF getCepuf() {
        return uf;
    }

    public void setCepuf(CEPUF uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CEPLocalidade other = (CEPLocalidade) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return nome;
    }
}
