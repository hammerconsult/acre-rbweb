/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author gustavo
 */
@Entity

@Audited
public class ItemCalculoTaxasDiversas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoTaxasDiversas calculoTaxasDiversas;
    @ManyToOne
    private TributoTaxaDividasDiversas tributoTaxaDividasDiversas;
    private BigDecimal valorReal;
    private BigDecimal valorUFM;
    private Integer quantidadeTributoTaxas;
    @Transient
    private Long criadoEm;

    public ItemCalculoTaxasDiversas() {
        valorReal = BigDecimal.ZERO;
        valorUFM = BigDecimal.ZERO;
        criadoEm = System.nanoTime();
    }

    public CalculoTaxasDiversas getCalculoTaxasDiversas() {
        return calculoTaxasDiversas;
    }

    public Integer getQuantidadeTributoTaxas() {
        return quantidadeTributoTaxas;
    }

    public void setQuantidadeTributoTaxas(Integer quantidadeTributoTaxas) {
        this.quantidadeTributoTaxas = quantidadeTributoTaxas;
    }


    public void setCalculoTaxasDiversas(CalculoTaxasDiversas calculoTaxasDiversas) {
        this.calculoTaxasDiversas = calculoTaxasDiversas;
    }

    public TributoTaxaDividasDiversas getTributoTaxaDividasDiversas() {
        return tributoTaxaDividasDiversas;
    }

    public void setTributoTaxaDividasDiversas(TributoTaxaDividasDiversas tributoTaxaDividasDiversas) {
        this.tributoTaxaDividasDiversas = tributoTaxaDividasDiversas;
    }

    public BigDecimal getValorReal() {
        return valorReal;
    }

    public void setValorReal(BigDecimal valorReal) {
        this.valorReal = valorReal;
    }

    public BigDecimal getValorUFM() {
        return valorUFM;
    }

    public void setValorUFM(BigDecimal valorUFM) {
        this.valorUFM = valorUFM;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ItemCalculoTaxasDiversas[ id=" + id + " ]";
    }
}
