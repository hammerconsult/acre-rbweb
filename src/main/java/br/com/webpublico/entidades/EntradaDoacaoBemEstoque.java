/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Invisivel;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author cheles
 */
public class EntradaDoacaoBemEstoque implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EntradaIncorporacaoMaterial entrada;
    @OneToOne
    private BensEstoque bensEstoque;
    @Invisivel
    @Transient
    private Long criadoEm;

    public EntradaDoacaoBemEstoque() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BensEstoque getBensEstoque() {
        return bensEstoque;
    }

    public void setBensEstoque(BensEstoque bensEstoque) {
        this.bensEstoque = bensEstoque;
    }

    public EntradaIncorporacaoMaterial getEntrada() {
        return entrada;
    }

    public void setEntrada(EntradaIncorporacaoMaterial entrada) {
        this.entrada = entrada;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EntradaDoacaoBemEstoque)) {
            return false;
        }
        EntradaDoacaoBemEstoque other = (EntradaDoacaoBemEstoque) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.EntradaBensEstoque[ id=" + id + " ]";
    }

}
