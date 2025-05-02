/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author major
 */
@Entity
@Audited

public class SubContaFonteRec extends SuperEntidade implements Serializable, Comparable<SubContaFonteRec> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    @ManyToOne
    private SubConta subConta;

    public SubContaFonteRec() {
    }

    public SubContaFonteRec(FonteDeRecursos fonteDeRecursos, ContaDeDestinacao contaDeDestinacao, SubConta subConta) {
        this.fonteDeRecursos = fonteDeRecursos;
        this.contaDeDestinacao = contaDeDestinacao;
        this.subConta = subConta;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.SubContaFonteRec[ id=" + id + " ]";
    }

    @Override
    public int compareTo(SubContaFonteRec o) {
        return this.contaDeDestinacao.getExercicio().getAno().compareTo(o.getContaDeDestinacao().getExercicio().getAno());
    }
}
