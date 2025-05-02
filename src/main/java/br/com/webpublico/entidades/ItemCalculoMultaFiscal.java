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
 * @author Leonardo
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public class ItemCalculoMultaFiscal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoMultaFiscalizacao calculoMultaFiscalizacao;
    @ManyToOne
    private Tributo tributo;
    private BigDecimal valor;
    @Transient
    private Long criadoEm;

    public ItemCalculoMultaFiscal() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalculoMultaFiscalizacao getCalculoMultaFiscalizacao() {
        return calculoMultaFiscalizacao;
    }

    public void setCalculoMultaFiscalizacao(CalculoMultaFiscalizacao calculoMultaFiscalizacao) {
        this.calculoMultaFiscalizacao = calculoMultaFiscalizacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ItemCalculoMultaFiscal[ id=" + id + " ]";
    }

}
