package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 01/10/14
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ItemAbastecObjetoFrota extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AbastecimentoObjetoFrota abastecimentoObjetoFrota;
    @ManyToOne
    private ItemCotaAbastecimento itemCotaAbastecimento;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    @Transient
    private BigDecimal quantidadeCota;
    @Transient
    private BigDecimal quantidadeEmOutrosAbastecimento;
    @Transient
    private BigDecimal quantidadeDisponivel;
    @Transient
    private BigDecimal valorAbastecimento;

    public ItemAbastecObjetoFrota() {
        this.setQuantidade(BigDecimal.ZERO);
        this.setQuantidadeEmOutrosAbastecimento(BigDecimal.ZERO);
        this.setQuantidadeDisponivel(BigDecimal.ZERO);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AbastecimentoObjetoFrota getAbastecimentoObjetoFrota() {
        return abastecimentoObjetoFrota;
    }

    public void setAbastecimentoObjetoFrota(AbastecimentoObjetoFrota abastecimentoObjetoFrota) {
        this.abastecimentoObjetoFrota = abastecimentoObjetoFrota;
    }

    public ItemCotaAbastecimento getItemCotaAbastecimento() {
        return itemCotaAbastecimento;
    }

    public void setItemCotaAbastecimento(ItemCotaAbastecimento itemCotaAbastecimento) {
        this.itemCotaAbastecimento = itemCotaAbastecimento;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getQuantidadeEmOutrosAbastecimento() {
        return quantidadeEmOutrosAbastecimento;
    }

    public void setQuantidadeEmOutrosAbastecimento(BigDecimal quantidadeEmOutrosAbastecimento) {
        this.quantidadeEmOutrosAbastecimento = quantidadeEmOutrosAbastecimento;
    }

    public BigDecimal getQuantidadeCota() {
        return quantidadeCota;
    }

    public void setQuantidadeCota(BigDecimal quantidadeCota) {
        this.quantidadeCota = quantidadeCota;
    }

    public BigDecimal getQuantidadeDisponivel() {
        quantidadeDisponivel = BigDecimal.ZERO;
        if (quantidadeCota != null && quantidadeEmOutrosAbastecimento != null) {
            quantidadeDisponivel = quantidadeCota.subtract(quantidadeEmOutrosAbastecimento);
        }
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorAbastecimento() {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) == 0 ||
            valorUnitario == null || valorUnitario.compareTo(BigDecimal.ZERO) == 0) {
            valorAbastecimento = BigDecimal.ZERO;
        } else {
            valorAbastecimento = quantidade.multiply(valorUnitario);
        }
        return valorAbastecimento;
    }

    public void setValorAbastecimento(BigDecimal valorAbastecimento) {
        this.valorAbastecimento = valorAbastecimento;
    }
}

