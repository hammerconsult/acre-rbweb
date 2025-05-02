/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author everton
 */
@Entity

@Audited
@Etiqueta("Invalidez do Aposentado")
@GrupoDiagrama(nome = "RecursosHumanos")
public class InvalidezAposentado implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Aposentadoria aposentadoria;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início da Vigência")
    @Obrigatorio
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Final da Vigência")
    private Date finalVigencia;
    @Etiqueta("CID")
    @Obrigatorio
    @OneToMany(mappedBy = "invalidezAposentado", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    private List<InvalidezCid> invalidezCids;
    @Invisivel
    @Etiqueta("Número do Laudo")
    private String numeroLaudo;
    @Etiqueta("Médico")
    @Obrigatorio
    @OneToMany(mappedBy = "invalidezAposentado", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    private List<InvalidezMedico> invalidezMedicos;
    @Etiqueta("Isento de IRRF")
    @Obrigatorio
    private Boolean isentoIRRF;
    @Etiqueta("Isento de Previdência")
    private Boolean isentoPrevidencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Transient
    private Date dataRegistro;

    public InvalidezAposentado() {
        dataRegistro = new Date();
        invalidezCids = new LinkedList<>();
        invalidezMedicos = new LinkedList<>();
        isentoIRRF = false;
        isentoPrevidencia = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsID(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvalidezAposentado)) {
            return false;
        }
        InvalidezAposentado other = (InvalidezAposentado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InvalidezAposentado other = (InvalidezAposentado) obj;
        if ((this.dataRegistro != other.dataRegistro && (this.dataRegistro == null || !this.dataRegistro.equals(other.dataRegistro)))
                || ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String strToString = new String();

        strToString = strToString.concat("Início da Vigência: " + Util.dateToString(inicioVigencia) + " ");
        if (finalVigencia != null) {
            strToString = strToString.concat("Final da Vigência: " + Util.dateToString(finalVigencia) + " ");
        }

        strToString = strToString.concat("CID: " + invalidezCids);
        strToString = strToString.concat("Médicos:" + invalidezMedicos);
        return strToString;
    }

    public Aposentadoria getAposentadoria() {
        return aposentadoria;
    }

    public void setAposentadoria(Aposentadoria aposentadoria) {
        this.aposentadoria = aposentadoria;
    }

    public List<InvalidezCid> getInvalidezCids() {
        return invalidezCids;
    }

    public void setInvalidezCids(List<InvalidezCid> invalidezCids) {
        this.invalidezCids = invalidezCids;
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

    public String getNumeroLaudo() {
        return numeroLaudo;
    }

    public void setNumeroLaudo(String numeroLaudo) {
        this.numeroLaudo = numeroLaudo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Boolean getIsentoPrevidencia() {
        return isentoPrevidencia != null ? isentoPrevidencia : false;
    }

    public void setIsentoPrevidencia(Boolean isentoPrevidencia) {
        this.isentoPrevidencia = isentoPrevidencia;
    }
}
