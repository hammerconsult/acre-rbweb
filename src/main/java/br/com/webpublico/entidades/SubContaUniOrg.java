/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author venon
 */
@Entity
@GrupoDiagrama(nome = "Bancario")
@Audited

public class SubContaUniOrg implements Serializable, Comparable<SubContaUniOrg> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private SubConta subConta;
    @ManyToOne
    private Exercicio exercicio;
    @Transient
    private Long criadoEm;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public SubContaUniOrg() {
        this.criadoEm = System.nanoTime();
    }

    public SubContaUniOrg(UnidadeOrganizacional unidadeOrganizacional, SubConta subConta, Exercicio exercicio) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.subConta = subConta;
        this.exercicio = exercicio;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
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
        return "Unidade: " + unidadeOrganizacional + " - Subconta: " + subConta;
    }

    @Override
    public int compareTo(SubContaUniOrg o) {
        return this.exercicio.getAno().compareTo(o.getExercicio().getAno());
    }
}
