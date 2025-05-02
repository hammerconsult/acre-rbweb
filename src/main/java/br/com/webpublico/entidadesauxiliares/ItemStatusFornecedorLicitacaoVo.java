package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemProcessoDeCompra;
import br.com.webpublico.enums.SituacaoItemProcessoDeCompra;
import com.google.common.collect.ComparisonChain;

import java.math.BigDecimal;
import java.util.Objects;

public class ItemStatusFornecedorLicitacaoVo implements Comparable<ItemStatusFornecedorLicitacaoVo> {

    private ItemProcessoDeCompra itemProcessoCompra;
    private ItemProcessoDeCompra itemProcessoCompraAdjudicado;
    private ItemProcessoDeCompra itemProcessoCompraHomologado;
    private LoteStatusFornecedorLicitacao loteStatus;
    private BigDecimal valorUnitario;
    private SituacaoItemProcessoDeCompra situacao;
    private Boolean selecionado;

    public ItemStatusFornecedorLicitacaoVo(ItemProcessoDeCompra itemProcessoCompra, LoteStatusFornecedorLicitacao loteStatus, BigDecimal valorUnitario,SituacaoItemProcessoDeCompra situacao) {
        this.itemProcessoCompra = itemProcessoCompra;
        this.loteStatus = loteStatus;
        this.valorUnitario = valorUnitario;
        this.situacao = situacao;
        this.selecionado = false;
    }

    public SituacaoItemProcessoDeCompra getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoItemProcessoDeCompra situacao) {
        this.situacao = situacao;
    }

    public ItemProcessoDeCompra getItemProcessoCompraAdjudicado() {
        return itemProcessoCompraAdjudicado;
    }

    public void setItemProcessoCompraAdjudicado(ItemProcessoDeCompra itemProcessoCompraAdjudicado) {
        this.itemProcessoCompraAdjudicado = itemProcessoCompraAdjudicado;
    }

    public ItemProcessoDeCompra getItemProcessoCompraHomologado() {
        return itemProcessoCompraHomologado;
    }

    public void setItemProcessoCompraHomologado(ItemProcessoDeCompra itemProcessoCompraHomologado) {
        this.itemProcessoCompraHomologado = itemProcessoCompraHomologado;
    }

    public ItemProcessoDeCompra getItemProcessoCompra() {
        return itemProcessoCompra;
    }

    public void setItemProcessoCompra(ItemProcessoDeCompra itemProcessoCompra) {
        this.itemProcessoCompra = itemProcessoCompra;
    }

    public LoteStatusFornecedorLicitacao getLoteStatus() {
        return loteStatus;
    }

    public void setLoteStatus(LoteStatusFornecedorLicitacao loteStatus) {
        this.loteStatus = loteStatus;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemStatusFornecedorLicitacaoVo that = (ItemStatusFornecedorLicitacaoVo) o;
        return Objects.equals(itemProcessoCompra, that.itemProcessoCompra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemProcessoCompra);
    }

    @Override
    public int compareTo(ItemStatusFornecedorLicitacaoVo o) {
        try {
            return ComparisonChain.start().compare(getLoteStatus().getNumero(), o.getLoteStatus().getNumero())
                .compare(getItemProcessoCompra().getNumero(), o.getItemProcessoCompra().getNumero()).result();
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
