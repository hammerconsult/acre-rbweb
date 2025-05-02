/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author peixe
 */
@Entity

@Audited
@CorEntidade(value = "#9400D3")
@GrupoDiagrama(nome = "RecursosHumanos")
public class BaseCargo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Cargo cargo;
    @ManyToOne
    private BasePeriodoAquisitivo basePeriodoAquisitivo;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @Transient
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public BaseCargo() {
        dataRegistro = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BasePeriodoAquisitivo getBasePeriodoAquisitivo() {
        return basePeriodoAquisitivo;
    }

    public void setBasePeriodoAquisitivo(BasePeriodoAquisitivo basePeriodoAquisitivo) {
        this.basePeriodoAquisitivo = basePeriodoAquisitivo;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BaseCargo)) {
            return false;
        }
        BaseCargo other = (BaseCargo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)) || (this.dataRegistro == null && other.dataRegistro != null) || (this.dataRegistro != null && !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return cargo + " - " + basePeriodoAquisitivo;
    }
}
