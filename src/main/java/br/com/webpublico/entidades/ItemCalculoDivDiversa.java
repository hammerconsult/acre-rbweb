/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Wellington
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Divida Diversa")
public class ItemCalculoDivDiversa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Invisivel
    @Transient
    private Long criadoEm;
    @ManyToOne
    private CalculoDividaDiversa calculoDividaDiversa;
    @ManyToOne
    private TributoTaxaDividasDiversas tributoTaxaDividasDiversas;
    private BigDecimal quantidade;
    private BigDecimal valorReal;
    private BigDecimal valorUFM;

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

    public ItemCalculoDivDiversa() {
        this.criadoEm = System.nanoTime();
    }

    public CalculoDividaDiversa getCalculoDividaDiversa() {
        return calculoDividaDiversa;
    }

    public void setCalculoDividaDiversa(CalculoDividaDiversa calculoDividaDiversa) {
        this.calculoDividaDiversa = calculoDividaDiversa;
    }

    public TributoTaxaDividasDiversas getTributoTaxaDividasDiversas() {
        return tributoTaxaDividasDiversas;
    }

    public void setTributoTaxaDividasDiversas(TributoTaxaDividasDiversas tributoTaxaDividasDiversas) {
        this.tributoTaxaDividasDiversas = tributoTaxaDividasDiversas;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
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

    public BigDecimal getValorUFMTotal() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (quantidade != null && valorUFM != null) {
            valorTotal = quantidade.multiply(valorUFM);
        }
        return valorTotal;
    }

    public BigDecimal getValorRealTotal() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (quantidade != null && valorReal != null) {
            valorTotal = quantidade.multiply(valorReal);
        }
        return valorTotal;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public String toString() {
        return "";
    }

}
