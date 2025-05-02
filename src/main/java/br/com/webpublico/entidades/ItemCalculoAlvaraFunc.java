package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wanderleydc
 */
@GrupoDiagrama(nome = "Alvara")
@Audited
@Entity

public class ItemCalculoAlvaraFunc implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoAlvaraFuncionamento calculoAlvaraFunc;
    @ManyToOne
    private Tributo tributo;
    private BigDecimal valorReal;
    private BigDecimal valorEfetivo;

    public CalculoAlvaraFuncionamento getCalculoAlvaraFunc() {
        return calculoAlvaraFunc;
    }

    public void setCalculoAlvaraFunc(CalculoAlvaraFuncionamento calculoAlvaraFunc) {
        this.calculoAlvaraFunc = calculoAlvaraFunc;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public BigDecimal getValorEfetivo() {
        return valorEfetivo;
    }

    public void setValorEfetivo(BigDecimal valorEfetivo) {
        this.valorEfetivo = valorEfetivo;
    }

    public BigDecimal getValorReal() {
        return valorReal;
    }

    public void setValorReal(BigDecimal valorReal) {
        this.valorReal = valorReal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof ItemCalculoAlvaraFunc)) {
            return false;
        }
        ItemCalculoAlvaraFunc other = (ItemCalculoAlvaraFunc) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ItemCalculoAlvaraFunc[ id=" + id + " ]";
    }

}
