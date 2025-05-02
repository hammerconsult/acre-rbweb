/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Usuario
 */
@Etiqueta("Grupo Diária")
@Audited
@Entity

public class GrupoPessoasDiarias implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne
    private ClasseCredor classeCredor;
    @ManyToOne
    private ContaCorrenteBancPessoa contaCorrenteBanc;
    @ManyToOne
    private GrupoDiaria grupoDiaria;
    @Transient
    private Long criadoEm;

    public GrupoPessoasDiarias() {
        criadoEm = System.nanoTime();
    }

    public GrupoPessoasDiarias(Pessoa pessoa, ClasseCredor classeCredor, ContaCorrenteBancPessoa contaCorrenteBanc, GrupoDiaria grupoDiaria, Long criadoEm) {
        this.pessoa = pessoa;
        this.classeCredor = classeCredor;
        this.contaCorrenteBanc = contaCorrenteBanc;
        this.grupoDiaria = grupoDiaria;
        this.criadoEm = criadoEm;
    }

    public GrupoDiaria getGrupoDiaria() {
        return grupoDiaria;
    }

    public void setGrupoDiaria(GrupoDiaria grupoDiaria) {
        this.grupoDiaria = grupoDiaria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public ContaCorrenteBancPessoa getContaCorrenteBanc() {
        return contaCorrenteBanc;
    }

    public void setContaCorrenteBanc(ContaCorrenteBancPessoa contaCorrenteBanc) {
        this.contaCorrenteBanc = contaCorrenteBanc;
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
        return "br.com.webpublico.entidades.GrupoPessoasDiarias[ id=" + id + " ]";
    }
}
