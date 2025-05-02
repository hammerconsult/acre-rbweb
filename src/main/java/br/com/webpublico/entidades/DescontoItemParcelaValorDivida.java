package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author daniel
 */
@Entity(name = "DescItemParcelaValorDivida")

@Audited
public class DescontoItemParcelaValorDivida implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DescontoOpcaoPagamento descontoOpcaoPagamento;
    @ManyToOne
    private ItemParcelaValorDivida itemParcelaValorDivida;
    private BigDecimal valorDesconto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DescontoOpcaoPagamento getDescontoOpcaoPagamento() {
        return descontoOpcaoPagamento;
    }

    public void setDescontoOpcaoPagamento(DescontoOpcaoPagamento descontoOpcaoPagamento) {
        this.descontoOpcaoPagamento = descontoOpcaoPagamento;
    }

    public ItemParcelaValorDivida getItemParcelaValorDivida() {
        return itemParcelaValorDivida;
    }

    public void setItemParcelaValorDivida(ItemParcelaValorDivida itemParcelaValorDivida) {
        this.itemParcelaValorDivida = itemParcelaValorDivida;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorLiquido) {
        this.valorDesconto = valorLiquido;
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
        if (!(object instanceof DescontoItemParcelaValorDivida)) {
            return false;
        }
        DescontoItemParcelaValorDivida other = (DescontoItemParcelaValorDivida) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.valorDesconto.toString();
    }
}
