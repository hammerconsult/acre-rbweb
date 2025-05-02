/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Heinz
 */
@Entity

@Audited
public class ItemValidacaoFiscalizacao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private TipoDocumentoFiscal tipoDocumentoFiscal;

    @ManyToOne
    private VistoriaFiscalizacao vistoriaFiscalizacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoDocumentoFiscal getTipoDocumentoFiscal() {
        return tipoDocumentoFiscal;
    }

    public void setTipoDocumentoFiscal(TipoDocumentoFiscal tipoDocumentoFiscal) {
        this.tipoDocumentoFiscal = tipoDocumentoFiscal;
    }

    public VistoriaFiscalizacao getVistoriaFiscalizacao() {
        return vistoriaFiscalizacao;
    }

    public void setVistoriaFiscalizacao(VistoriaFiscalizacao vistoriaFiscalizacao) {
        this.vistoriaFiscalizacao = vistoriaFiscalizacao;
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
        if (!(object instanceof ItemValidacaoFiscalizacao)) {
            return false;
        }
        ItemValidacaoFiscalizacao other = (ItemValidacaoFiscalizacao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ItemValidacao[ id=" + id + " ]";
    }

}
