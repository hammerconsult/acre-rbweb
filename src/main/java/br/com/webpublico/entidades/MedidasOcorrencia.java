/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.envers.Audited;
import javax.persistence.*;

/**
 *
 * @author William
 */
@Entity
@Audited

public class MedidasOcorrencia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private MedidaAdministrativa medidaAdministrativa;
    @ManyToOne
    private OcorrenciaFiscalizacaoRBTrans ocorrenciaFiscalizaRBTrans;
    @Transient
    private Date criadoEm;

    public MedidasOcorrencia() {
        criadoEm = new Date();
    }

    public OcorrenciaFiscalizacaoRBTrans getOcorrenciaFiscalizaRBTrans() {
        return ocorrenciaFiscalizaRBTrans;
    }

    public void setOcorrenciaFiscalizaRBTrans(OcorrenciaFiscalizacaoRBTrans ocorrenciaFiscalizaRBTrans) {
        this.ocorrenciaFiscalizaRBTrans = ocorrenciaFiscalizaRBTrans;
    }

    public MedidaAdministrativa getMedidaAdministrativa() {
        return medidaAdministrativa;
    }

    public void setMedidaAdministrativa(MedidaAdministrativa medidaAdministrativa) {
        this.medidaAdministrativa = medidaAdministrativa;
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
        if (!(object instanceof MedidasOcorrencia)) {
            return false;
        }
        MedidasOcorrencia other = (MedidasOcorrencia) object;
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
        final MedidasOcorrencia other = (MedidasOcorrencia) obj;
        if ((this.criadoEm != other.criadoEm && (this.criadoEm == null || !this.criadoEm.equals(other.criadoEm)))
                || ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.OcorrenciaPermissaoTaxi[ id=" + id + " ]";
    }
}
