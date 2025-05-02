/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPrazo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import org.hibernate.envers.Audited;

/**
 *
 * @author Munif
 */
@GrupoDiagrama(nome = "Protocolo")
@Audited
@Etiqueta("Item da Rota")
@Entity

public class ItemRota implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Obrigatorio
    private double prazo;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Enumerated(EnumType.STRING)
    private TipoPrazo tipoPrazo;

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ItemRota() {
        this.dataRegistro = new Date();
        this.tipoPrazo = TipoPrazo.HORAS;
    }

    public ItemRota(UnidadeOrganizacional unidadeOrganizacional, double prazo, Date dataRegistro) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.prazo = prazo;
        this.dataRegistro = dataRegistro;
        this.tipoPrazo = TipoPrazo.HORAS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPrazo() {
        return prazo;
    }

    public void setPrazo(double prazo) {
        this.prazo = prazo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public TipoPrazo getTipoPrazo() {
        return tipoPrazo;
    }

    public void setTipoPrazo(TipoPrazo tipoPrazo) {
        this.tipoPrazo = tipoPrazo;
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
        if (!(object instanceof ItemRota)) {
            return false;
        }
        ItemRota other = (ItemRota) object;
        if ((this.dataRegistro == null && other.dataRegistro != null) || (this.dataRegistro != null && !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return unidadeOrganizacional.toString();
    }
}
