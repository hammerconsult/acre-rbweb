/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoNaturezaAtividadeFP;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author peixe
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class ValorUnidadeOrganizacional implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //U. O. do tipo ADIMINISTRATIVA
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private ItemLaudo itemLaudo;
    @Enumerated(EnumType.STRING)
    private TipoNaturezaAtividadeFP tipoNaturezaAtividadeFP;
    private BigDecimal valor;
    @Temporal(TemporalType.TIMESTAMP)
    @Transient
    private Date dataRegistro;

    public ValorUnidadeOrganizacional() {
        dataRegistro = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public ItemLaudo getItemLaudo() {
        return itemLaudo;
    }

    public void setItemLaudo(ItemLaudo itemLaudo) {
        this.itemLaudo = itemLaudo;
    }

    public TipoNaturezaAtividadeFP getTipoNaturezaAtividadeFP() {
        return tipoNaturezaAtividadeFP;
    }

    public void setTipoNaturezaAtividadeFP(TipoNaturezaAtividadeFP tipoNaturezaAtividadeFP) {
        this.tipoNaturezaAtividadeFP = tipoNaturezaAtividadeFP;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValorUnidadeOrganizacional other = (ValorUnidadeOrganizacional) obj;
        if ((this.dataRegistro != other.dataRegistro && (this.dataRegistro == null || !this.dataRegistro.equals(other.dataRegistro)))
                || ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))) {
            return false;
        }
        return true;
    }

    public boolean equalsID(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ValorUnidadeOrganizacional)) {
            return false;
        }
        ValorUnidadeOrganizacional other = (ValorUnidadeOrganizacional) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)) || (this.dataRegistro == null && other.dataRegistro != null) || (this.dataRegistro != null && !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return tipoNaturezaAtividadeFP + " - " + unidadeOrganizacional + " - " + valor;
    }
}
