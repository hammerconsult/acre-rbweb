/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.hibernate.envers.Audited;

/**
 *
 * @author java
 */
@Entity
@Audited
public class ContratoRendaCNAE implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Invisivel
    @Transient
    private Long criadoEm;
    @ManyToOne
    private CNAE cnae;
    @ManyToOne
    private ContratoRendasPatrimoniais contratoRendasPatrimoniais;

    public ContratoRendaCNAE() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public ContratoRendasPatrimoniais getContratoRendasPatrimoniais() {
        return contratoRendasPatrimoniais;
    }

    public void setContratoRendasPatrimoniais(ContratoRendasPatrimoniais contratoRendasPatrimoniais) {
        this.contratoRendasPatrimoniais = contratoRendasPatrimoniais;
    }


     @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }


    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ContratoRendaCNAE[ id=" + id + " ]";
    }
}
