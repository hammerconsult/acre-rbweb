/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import org.hibernate.envers.Audited;

/**
 *
 * @author fabio
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@GrupoDiagrama(nome = "Fiscalizacao")
public class ItensCalculoFiscalizacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoFiscalizacao calculoFiscalizacao;
    @ManyToOne
    private Tributo tributo;
    private BigDecimal valor;
    @Transient
    private Long criadoEm;

    public ItensCalculoFiscalizacao() {
        criadoEm = System.nanoTime();
        valor = BigDecimal.ZERO;
    }

    public CalculoFiscalizacao getCalculoFiscalizacao() {
        return calculoFiscalizacao;
    }

    public void setCalculoFiscalizacao(CalculoFiscalizacao calculoFiscalizacao) {
        this.calculoFiscalizacao = calculoFiscalizacao;
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


    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

}
