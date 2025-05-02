/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.envers.Audited;

/**
 * @author major
 */
@Entity
@Audited
public class ArquivoRemBancoBordero implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ArquivoRemessaBanco arquivoRemessaBanco;
    @ManyToOne
    private Bordero bordero;
    @Transient
    private Long criadoEm;

    public ArquivoRemBancoBordero() {
        criadoEm = System.nanoTime();
    }

    public ArquivoRemBancoBordero(ArquivoRemessaBanco arquivoRemessaBanco, Bordero bordero) {
        this.arquivoRemessaBanco = arquivoRemessaBanco;
        this.bordero = bordero;
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArquivoRemessaBanco getArquivoRemessaBanco() {
        return arquivoRemessaBanco;
    }

    public void setArquivoRemessaBanco(ArquivoRemessaBanco arquivoRemessaBanco) {
        this.arquivoRemessaBanco = arquivoRemessaBanco;
    }

    public Bordero getBordero() {
        return bordero;
    }

    public void setBordero(Bordero bordero) {
        this.bordero = bordero;
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
        return "br.com.webpublico.entidades.ArquivoRemessaBancoBordero[ id=" + id + " ]";
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }
}
