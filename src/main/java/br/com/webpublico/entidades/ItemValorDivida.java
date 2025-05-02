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

@GrupoDiagrama(nome = "Tributario")
@Entity
@Audited

public class ItemValorDivida implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal valor;
    @ManyToOne
    private ValorDivida valorDivida;
    @ManyToOne
    private Tributo tributo;
    @Transient
    private Long criadoEm;
    private Boolean isento;
    private Boolean imune;

    public ItemValorDivida() {
        isento = Boolean.FALSE;
        imune = Boolean.FALSE;
        criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Boolean getIsento() {
        return isento != null ? isento : false;
    }

    public void setIsento(Boolean isento) {
        this.isento = isento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            this.valor = BigDecimal.ZERO;
        }
    }

    public ValorDivida getValorDivida() {
        return valorDivida;
    }

    public void setValorDivida(ValorDivida valorDivida) {
        this.valorDivida = valorDivida;
    }

    public Boolean getImune() {
        return imune != null ? imune : false;
    }

    public void setImune(Boolean imune) {
        this.imune = imune;
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
        return "br.com.webpublico.entidades.novas.ItemValorDivida[ id=" + id + " ]";
    }
}
