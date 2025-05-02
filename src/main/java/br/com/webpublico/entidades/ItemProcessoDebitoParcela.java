package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by William on 31/03/2017.
 */
@Entity
@Audited
public class ItemProcessoDebitoParcela extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ItemProcessoDebito itemProcessoDebito;
    @ManyToOne
    private ItemParcelaValorDivida itemParcelaValorDivida;
    @ManyToOne
    private Tributo tributo;
    private BigDecimal valor;
    private BigDecimal valorOriginal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemProcessoDebito getItemProcessoDebito() {
        return itemProcessoDebito;
    }

    public void setItemProcessoDebito(ItemProcessoDebito itemProcessoDebito) {
        this.itemProcessoDebito = itemProcessoDebito;
    }

    public ItemParcelaValorDivida getItemParcelaValorDivida() {
        return itemParcelaValorDivida;
    }

    public void setItemParcelaValorDivida(ItemParcelaValorDivida itemParcelaValorDivida) {
        this.itemParcelaValorDivida = itemParcelaValorDivida;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public BigDecimal getValor() {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }
}
