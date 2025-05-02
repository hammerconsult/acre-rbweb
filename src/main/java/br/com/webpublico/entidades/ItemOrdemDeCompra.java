package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
public class ItemOrdemDeCompra implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private OrdemDeCompraContrato ordemDeCompraContrato;
    @ManyToOne
    private ItemContrato itemContrato;
    private BigDecimal quantidade;
    @Transient
    @Invisivel
    private BigDecimal quantidadeEmExecucao;
    @Transient
    @Invisivel
    private BigDecimal quantidadeEmOutrasOrdensDeCompra;

    @Transient
    @Invisivel
    private Long criadoEm;

    public ItemOrdemDeCompra() {
        criadoEm = System.nanoTime();
    }

    public BigDecimal getQuantidadeEmExecucao() {
        if (quantidadeEmExecucao == null) {
            quantidadeEmExecucao = BigDecimal.ZERO;
        }

        return quantidadeEmExecucao;
    }

    public void setQuantidadeEmExecucao(BigDecimal quantidadeEmExecucao) {
        this.quantidadeEmExecucao = quantidadeEmExecucao;
    }

    public BigDecimal getQuantidadeEmOutrasOrdensDeCompra() {
        if (quantidadeEmOutrasOrdensDeCompra == null){
            quantidadeEmOutrasOrdensDeCompra = BigDecimal.ZERO;
        }
        return quantidadeEmOutrasOrdensDeCompra;
    }

    public void setQuantidadeEmOutrasOrdensDeCompra(BigDecimal quantidadeEmOutrasOrdensDeCompra) {
        this.quantidadeEmOutrasOrdensDeCompra = quantidadeEmOutrasOrdensDeCompra;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrdemDeCompraContrato getOrdemDeCompraContrato() {
        return ordemDeCompraContrato;
    }

    public void setOrdemDeCompraContrato(OrdemDeCompraContrato ordemDeCompraContrato) {
        this.ordemDeCompraContrato = ordemDeCompraContrato;
    }

    public ItemContrato getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(ItemContrato itemContrato) {
        this.itemContrato = itemContrato;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
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
