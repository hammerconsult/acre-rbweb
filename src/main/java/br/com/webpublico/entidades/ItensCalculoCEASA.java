/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author fabio
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@GrupoDiagrama(nome = "Fiscalizacao")
public class ItensCalculoCEASA implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoCEASA calculoCEASA;
    @ManyToOne
    private Tributo tributo;
    private BigDecimal valor;
    @Transient
    private Long criadoEm;


    public ItensCalculoCEASA() {
        criadoEm = System.nanoTime();
    }

    public CalculoCEASA getCalculoCEASA() {
        return calculoCEASA;
    }

    public void setCalculoCEASA(CalculoCEASA calculoCEASA) {
        this.calculoCEASA = calculoCEASA;
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
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
