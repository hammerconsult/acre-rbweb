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

public class CEPLogradouro implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nome;
    private String tipo;
    private String complemento;
    private String cep;
    private String codigoIBGE;
    @ManyToOne
    private CEPLocalidade localidade;
    @ManyToOne
    private CEPBairro bairro;

    public CEPLogradouro() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CEPBairro getBairro() {
        return bairro;
    }

    public void setBairro(CEPBairro bairro) {
        this.bairro = bairro;
    }

    public CEPLocalidade getLocalidade() {
        return localidade;
    }

    public void setLocalidade(CEPLocalidade localidade) {
        this.localidade = localidade;
    }

    public String getNome() {
        return nome;
    }

    public String getNomeCompleto() {
        return (tipo != null ? tipo + " " : "")
                + (nome != null ? nome + " " : "");
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCodigoIBGE() {
        return codigoIBGE;
    }

    public void setCodigoIBGE(String codigoIBGE) {
        this.codigoIBGE = codigoIBGE;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CEPLogradouro other = (CEPLogradouro) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return (tipo != null ? tipo + " " : "")
                + (nome != null ? nome + " " : "")
                + (complemento != null ? complemento : "");
    }
}
