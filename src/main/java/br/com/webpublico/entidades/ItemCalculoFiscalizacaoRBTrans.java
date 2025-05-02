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
 * @author AndreGustavo
 */
@Entity
@Audited
@Table(name = "ITEMCALCFISCRBTRANS")
public class ItemCalculoFiscalizacaoRBTrans implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal valor;
    @OneToOne
    private Tributo tributo;
    @ManyToOne
    private CalculoFiscalizacaoRBTrans calculoFiscalizacaoRBTrans;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalculoFiscalizacaoRBTrans getCalculoFiscalizacaoRBTrans() {
        return calculoFiscalizacaoRBTrans;
    }

    public void setCalculoFiscalizacaoRBTrans(CalculoFiscalizacaoRBTrans calculoFiscalizacaoRBTrans) {
        this.calculoFiscalizacaoRBTrans = calculoFiscalizacaoRBTrans;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
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
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemCalculoFiscalizacaoRBTrans)) {
            return false;
        }
        ItemCalculoFiscalizacaoRBTrans other = (ItemCalculoFiscalizacaoRBTrans) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ItemCalculoFiscalizacaoRBTrans[ id=" + id + " ]";
    }
}
