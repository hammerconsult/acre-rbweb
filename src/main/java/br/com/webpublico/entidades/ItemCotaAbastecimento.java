package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 30/09/14
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ItemCotaAbastecimento extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CotaAbastecimento cotaAbastecimento;
    @ManyToOne
    private ItemContrato itemContrato;
    private BigDecimal quantidadeCota;

    private BigDecimal valorUnitario;
    @Transient
    private BigDecimal quantidadeEmOutrasCotas;
    @Transient
    private BigDecimal quantidadeDisponivel;
    @Transient
    private BigDecimal quantidadeTotal;


    public ItemCotaAbastecimento() {
        quantidadeCota = BigDecimal.ZERO;
        quantidadeEmOutrasCotas = BigDecimal.ZERO;
        quantidadeDisponivel = BigDecimal.ZERO;
        quantidadeTotal = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CotaAbastecimento getCotaAbastecimento() {
        return cotaAbastecimento;
    }

    public void setCotaAbastecimento(CotaAbastecimento cotaAbastecimento) {
        this.cotaAbastecimento = cotaAbastecimento;
    }

    public ItemContrato getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(ItemContrato itemContrato) {
        this.itemContrato = itemContrato;
    }

    public BigDecimal getQuantidadeCota() {
        return quantidadeCota;
    }

    public void setQuantidadeCota(BigDecimal quantidadeCota) {
        this.quantidadeCota = quantidadeCota;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getQuantidadeEmOutrasCotas() {
        return quantidadeEmOutrasCotas;
    }

    public void setQuantidadeEmOutrasCotas(BigDecimal quantidadeEmOutrasCotas) {
        this.quantidadeEmOutrasCotas = quantidadeEmOutrasCotas;
    }

    public BigDecimal getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(BigDecimal quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public BigDecimal getQuantidadeDisponivel() {
        quantidadeDisponivel = quantidadeTotal.subtract(quantidadeEmOutrasCotas);
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }
}
