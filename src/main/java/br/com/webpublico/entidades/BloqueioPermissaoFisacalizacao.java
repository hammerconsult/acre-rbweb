/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author user
 */
@Entity
@Audited
@Table(name = "BLOQUEIOPERMFISC")
public class BloqueioPermissaoFisacalizacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FiscalizacaoRBTrans fiscalizacaoRBTrans;
    @ManyToOne
    private PermissaoTransporte permissaoTransporte;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataInicial;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataFinal;

    public FiscalizacaoRBTrans getFiscalizacaoRBTrans() {
        return fiscalizacaoRBTrans;
    }

    public void setFiscalizacaoRBTrans(FiscalizacaoRBTrans fiscalizacaoRBTrans) {
        this.fiscalizacaoRBTrans = fiscalizacaoRBTrans;
    }

    public PermissaoTransporte getPermissaoTransporte() {
        return permissaoTransporte;
    }

    public void setPermissaoTransporte(PermissaoTransporte permissaoTransporte) {
        this.permissaoTransporte = permissaoTransporte;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
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
        if (!(object instanceof BloqueioPermissaoFisacalizacao)) {
            return false;
        }
        BloqueioPermissaoFisacalizacao other = (BloqueioPermissaoFisacalizacao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.BloqueioPermissaoFisacalizacao[ id=" + id + " ]";
    }
}
