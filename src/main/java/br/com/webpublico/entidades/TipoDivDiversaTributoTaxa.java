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

/**
 * @author Wellington
 */
@GrupoDiagrama(nome = "DividaDiversa")
@Entity

@Audited
public class TipoDivDiversaTributoTaxa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @ManyToOne
    private TipoDividaDiversa tipoDividaDiversa;
    @ManyToOne
    private TributoTaxaDividasDiversas tributoTaxaDividasDiversas;
    @Transient
    private Long criadoEm;

    public TipoDivDiversaTributoTaxa() {
        criadoEm = System.nanoTime();
    }

    public TipoDividaDiversa getTipoDividaDiversa() {
        return tipoDividaDiversa;
    }

    public void setTipoDividaDiversa(TipoDividaDiversa tipoDividaDiversa) {
        this.tipoDividaDiversa = tipoDividaDiversa;
    }

    public TributoTaxaDividasDiversas getTributoTaxaDividasDiversas() {
        return tributoTaxaDividasDiversas;
    }

    public void setTributoTaxaDividasDiversas(TributoTaxaDividasDiversas tributoTaxaDividasDiversas) {
        this.tributoTaxaDividasDiversas = tributoTaxaDividasDiversas;
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
}
