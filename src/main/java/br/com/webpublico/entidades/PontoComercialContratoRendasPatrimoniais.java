/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Andre
 */
@Entity
@Audited

@GrupoDiagrama(nome = "Tributario")
@Table(name = "PTOCOMERCIALCONTRATORENDAS")
public class PontoComercialContratoRendasPatrimoniais implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PontoComercial pontoComercial;
    @ManyToOne
    private ContratoRendasPatrimoniais contratoRendasPatrimoniais;
    private BigDecimal valorCalculadoMes;
    private BigDecimal valorTotalContrato;
    @Transient
    private Long criadoEm;
    @Etiqueta("Área (m²)")
    private BigDecimal area;
    private BigDecimal valorMetroQuadrado;

    public PontoComercialContratoRendasPatrimoniais() {
        criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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

    public ContratoRendasPatrimoniais getContratoRendasPatrimoniais() {
        return contratoRendasPatrimoniais;
    }

    public void setContratoRendasPatrimoniais(ContratoRendasPatrimoniais contratoRendasPatrimoniais) {
        this.contratoRendasPatrimoniais = contratoRendasPatrimoniais;
    }

    public BigDecimal getValorCalculadoMes() {
        return valorCalculadoMes != null ? valorCalculadoMes.setScale(4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorCalculadoMes(BigDecimal valorCalculadoMes) {
        this.valorCalculadoMes = valorCalculadoMes;
    }

    public BigDecimal getValorTotalContrato() {
        return valorTotalContrato != null ? valorTotalContrato.setScale(4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorTotalContrato(BigDecimal valorTotalContrato) {
        this.valorTotalContrato = valorTotalContrato;
    }

    public BigDecimal getArea() {
        return area;
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

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(obj, this);
    }

    public boolean equalsID(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PontoComercialContratoRendasPatrimoniais)) {
            return false;
        }
        PontoComercialContratoRendasPatrimoniais other = (PontoComercialContratoRendasPatrimoniais) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return pontoComercial.toString();
    }
}
