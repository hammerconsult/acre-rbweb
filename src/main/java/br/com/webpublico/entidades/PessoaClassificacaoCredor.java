/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author major
 */
@Entity

@Audited
public class PessoaClassificacaoCredor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne
    private ClassificacaoCredor classificacaoCredor;
    @Transient
    private Long criadoEm;

    public PessoaClassificacaoCredor() {
        criadoEm = System.nanoTime();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClassificacaoCredor getClassificacaoCredor() {
        return classificacaoCredor;
    }

    public void setClassificacaoCredor(ClassificacaoCredor classificacaoCredor) {
        this.classificacaoCredor = classificacaoCredor;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
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
        return "Classificação: " + classificacaoCredor.getDescricao();
    }
}
