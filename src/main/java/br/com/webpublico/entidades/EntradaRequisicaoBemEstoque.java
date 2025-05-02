/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Materiais")
public class EntradaRequisicaoBemEstoque implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EntradaCompraMaterial entradaRequisicao;
    @OneToOne
    private BensEstoque bensEstoque;
    @Invisivel
    @Transient
    private Long criadoEm;

    public EntradaRequisicaoBemEstoque() {
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

    public EntradaCompraMaterial getEntradaRequisicao() {
        return entradaRequisicao;
    }

    public void setEntradaRequisicao(EntradaCompraMaterial entradaRequisicao) {
        this.entradaRequisicao = entradaRequisicao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.EntradaRequisicaoBemEstoque[ id=" + id + " ]";
    }

}
