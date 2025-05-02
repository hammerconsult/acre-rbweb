/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author boy
 */
@Audited
@Entity

@Etiqueta("Invalidez Pensão")
@GrupoDiagrama(nome = "RecursosHumanos")
public class InvalidezPensao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início da Vigência")
    @Obrigatorio
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Final da Vigência")
    private Date finalVigencia;
    @Etiqueta("Isento de IRRF")
    private Boolean isentoIRRF;
    private Boolean isentoPrevidencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Transient
    private Date dataRegistro;
    @ManyToOne
    private Pensionista pensionista;
    @ManyToOne
    private Medico medico;
    @OneToOne
    private ProvimentoFP provimentoFP;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "invalidezPensao", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<InvalidezPensaoCid> cids;
    @OneToMany(mappedBy = "invalidezPensao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvalidezMedico> invalidezMedicos;

    public InvalidezPensao() {
        dataRegistro = new Date();
        isentoIRRF = false;
        isentoPrevidencia = false;
        cids = Lists.newArrayList();
        invalidezMedicos = Lists.newArrayList();
    }

    public Pensionista getPensionista() {
        return pensionista;
    }

    public void setPensionista(Pensionista pensionista) {
        this.pensionista = pensionista;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Boolean getIsentoIRRF() {
        return isentoIRRF != null ? isentoIRRF : false;
    }

    public void setIsentoIRRF(Boolean isentoIRRF) {
        this.isentoIRRF = isentoIRRF;
    }

    public List<InvalidezMedico> getInvalidezMedicos() {
        return invalidezMedicos;
    }

    public void setInvalidezMedicos(List<InvalidezMedico> invalidezMedicos) {
        this.invalidezMedicos = invalidezMedicos;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public Boolean getIsentoPrevidencia() {
        return isentoPrevidencia != null ? isentoPrevidencia : false;
    }

    public void setIsentoPrevidencia(Boolean isentoPrevidencia) {
        this.isentoPrevidencia = isentoPrevidencia;
    }

    public List<InvalidezPensaoCid> getCids() {
        return cids;
    }

    public void setCids(List<InvalidezPensaoCid> cids) {
        this.cids = cids;
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
        if (!(object instanceof InvalidezPensao)) {
            return false;
        }
        InvalidezPensao other = (InvalidezPensao) object;
        if ((this.dataRegistro != other.dataRegistro && (this.dataRegistro == null || !this.dataRegistro.equals(other.dataRegistro)))
                || ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getInicioVigencia() + " - " + getFinalVigencia();
    }
}
