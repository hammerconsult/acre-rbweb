/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * @author Andre
 */
@Entity
@Audited

@GrupoDiagrama(nome = "Tributario")
@Table(name = "PTOCOMERCIALCONTRATOCEASA")
public class PontoComercialContratoCEASA implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PontoComercial pontoComercial;
    @ManyToOne
    private ContratoCEASA contratoCEASA;
    @Transient
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date criadoEm;
    private BigDecimal valorCalculadoMes;
    private BigDecimal valorTotalContrato;
    @Etiqueta("Área (m²)")
    private BigDecimal area;
    private BigDecimal valorMetroQuadrado;
    @Transient
    private BigDecimal valorReal;
    @Transient
    private BigDecimal valorTotalMes;


    public PontoComercialContratoCEASA() {
        criadoEm = new Date();
        this.valorReal = BigDecimal.ZERO;
    }


    public BigDecimal getValorReal() {
        return valorReal != null ? valorReal.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorReal(BigDecimal valorReal) {
        this.valorReal = valorReal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PontoComercial getPontoComercial() {
        return pontoComercial;
    }

    public void setPontoComercial(PontoComercial pontoComercial) {
        this.pontoComercial = pontoComercial;
    }

    public ContratoCEASA getContratoCEASA() {
        return contratoCEASA;
    }

    public void setContratoCEASA(ContratoCEASA contratoCEASA) {
        this.contratoCEASA = contratoCEASA;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getValorCalculadoMes() {
        return valorCalculadoMes != null ? valorCalculadoMes.setScale(4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorCalculadoMes(BigDecimal valorCalculadoMes) {
        this.valorCalculadoMes = valorCalculadoMes;
    }

    public BigDecimal getValorTotalContrato() {
        return valorTotalContrato != null ? valorTotalContrato.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorTotalContrato(BigDecimal valorTotalContrato) {
        this.valorTotalContrato = valorTotalContrato;
    }

    public BigDecimal getArea() {
        return area != null ? area.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getValorMetroQuadrado() {
        return valorMetroQuadrado != null ? valorMetroQuadrado.setScale(4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorMetroQuadrado(BigDecimal valorMetroQuadrado) {
        this.valorMetroQuadrado = valorMetroQuadrado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public BigDecimal getValorTotalMes() {
        return valorTotalMes != null ? valorTotalMes.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorTotalMes(BigDecimal valorTotalMes) {
        this.valorTotalMes = valorTotalMes;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PontoComercialContratoCEASA other = (PontoComercialContratoCEASA) obj;
        if ((this.criadoEm != other.criadoEm && (this.criadoEm == null || !this.criadoEm.equals(other.criadoEm)))
                || ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))) {
            return false;
        }
        return true;
    }

    public boolean equalsID(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PontoComercialContratoCEASA)) {
            return false;
        }
        PontoComercialContratoCEASA other = (PontoComercialContratoCEASA) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.pontoComercial.toString();
    }
}
