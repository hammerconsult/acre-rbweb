/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Gustavo
 */
@Entity

@Audited
public class ItemCalculoIss implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Servico servico;
    private BigDecimal aliquota;
    private BigDecimal baseCalculo;
    private BigDecimal faturamento;
    private BigDecimal valorCalculado;
    @ManyToOne
    private CalculoISS calculo;
    @ManyToOne
    private Tributo tributo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public CalculoISS getCalculo() {
        return calculo;
    }

    public void setCalculo(CalculoISS calculo) {
        this.calculo = calculo;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public BigDecimal getFaturamento() {
        return faturamento != null ? faturamento : BigDecimal.ZERO;
    }

    public void setFaturamento(BigDecimal faturamento) {
        this.faturamento = faturamento;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public BigDecimal getValorCalculado() {
        return valorCalculado;
    }

    public void setValorCalculado(BigDecimal valorCalculado) {
        this.valorCalculado = valorCalculado;
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
        if (!(object instanceof ItemCalculoIss)) {
            return false;
        }
        ItemCalculoIss other = (ItemCalculoIss) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Aliquota:" + getCalculo().getAliquota()
                + " - Faturamento: " + faturamento
                + " - Mes: " + getCalculo().getProcessoCalculoISS().getMesReferencia()
                + " - Ano: " + getCalculo().getProcessoCalculo().getExercicio()
                + " - S/M: " + getCalculo().getAusenciaMovimento()
                + " - ID: " + getCalculo().getId();
    }


}
