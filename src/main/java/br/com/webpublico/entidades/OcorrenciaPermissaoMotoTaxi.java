/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.com.webpublico.enums.TipoPermissaoMotoTaxi;
import org.hibernate.envers.Audited;
import javax.persistence.*;

/**
 *
 * @author magraowar
 */
@Entity
@Audited

@Table(name = "OCORRENCIAPERMOTOTAXI")
public class OcorrenciaPermissaoMotoTaxi implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoPermissaoMotoTaxi tipoPermissaoMotoTaxi;
    @ManyToOne
    private OcorrenciaFiscalizacaoRBTrans ocorrenciaFiscalizaRBTrans;

    public OcorrenciaFiscalizacaoRBTrans getOcorrenciaFiscalizaRBTrans() {
        return ocorrenciaFiscalizaRBTrans;
    }

    public void setOcorrenciaFiscalizaRBTrans(OcorrenciaFiscalizacaoRBTrans ocorrenciaFiscalizaRBTrans) {
        this.ocorrenciaFiscalizaRBTrans = ocorrenciaFiscalizaRBTrans;
    }



    public TipoPermissaoMotoTaxi getTipoPermissaoMotoTaxi() {
        return tipoPermissaoMotoTaxi;
    }

    public void setTipoPermissaoMotoTaxi(TipoPermissaoMotoTaxi tipoPermissaoMotoTaxi) {
        this.tipoPermissaoMotoTaxi = tipoPermissaoMotoTaxi;
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

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OcorrenciaPermissaoMotoTaxi)) {
            return false;
        }
        OcorrenciaPermissaoMotoTaxi other = (OcorrenciaPermissaoMotoTaxi) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.OcorrenciaPermissaoMotoTaxi[ id=" + id + " ]";
    }
}
