/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author reidocrime
 */

@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
public class EntidadeBeneficCertidoes extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DoctoHabilitacao doctoHabilitacao;
    @ManyToOne
    private EntidadeBeneficiaria entidadeBeneficiaria;
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;
    private String observacao;

    public EntidadeBeneficCertidoes() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DoctoHabilitacao getDoctoHabilitacao() {
        return doctoHabilitacao;
    }

    public void setDoctoHabilitacao(DoctoHabilitacao doctoHabilitacao) {
        this.doctoHabilitacao = doctoHabilitacao;
    }

    public EntidadeBeneficiaria getEntidadeBeneficiaria() {
        return entidadeBeneficiaria;
    }

    public void setEntidadeBeneficiaria(EntidadeBeneficiaria entidadeBeneficiaria) {
        this.entidadeBeneficiaria = entidadeBeneficiaria;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public String toString() {
        return this.doctoHabilitacao.toString();
    }

}
