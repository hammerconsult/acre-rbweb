/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoAcaoFiscal;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Claudio
 */
@GrupoDiagrama(nome = "Fiscalizacao")
@Entity

@Audited
public class SituacoesAcaoFiscal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AcaoFiscal acaoFiscal;
    @Enumerated(EnumType.STRING)
    private SituacaoAcaoFiscal situacaoAcaoFiscal;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date data;

    public SituacoesAcaoFiscal() {
    }

    public SituacoesAcaoFiscal(Date dataSituacao, AcaoFiscal acaoFiscal, SituacaoAcaoFiscal situacaoAcaoFiscal) {
        this.data = dataSituacao;
        this.acaoFiscal = acaoFiscal;
        this.situacaoAcaoFiscal = situacaoAcaoFiscal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AcaoFiscal getAcaoFiscal() {
        return acaoFiscal;
    }

    public void setAcaoFiscal(AcaoFiscal acaoFiscal) {
        this.acaoFiscal = acaoFiscal;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public SituacaoAcaoFiscal getSituacaoAcaoFiscal() {
        return situacaoAcaoFiscal;
    }

    public void setSituacaoAcaoFiscal(SituacaoAcaoFiscal situacaoAcaoFiscal) {
        this.situacaoAcaoFiscal = situacaoAcaoFiscal;
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
        if (!(object instanceof SituacoesAcaoFiscal)) {
            return false;
        }
        SituacoesAcaoFiscal other = (SituacoesAcaoFiscal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.SituacoesFiscais[ id=" + id + " ]";
    }
}
