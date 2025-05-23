package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.DerivacaoObjetoCompra;
import br.com.webpublico.entidades.ItemRequisicaoDeCompra;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class ItemRequisicaoCompraDerivacao {

    private ItemRequisicaoDeCompra itemRequisicaoOriginal;
    private DerivacaoObjetoCompra derivacaoObjetoCompra;
    private BigDecimal quantidadDisponivel;
    private List<ItemRequisicaoDeCompra> itensRequisicaoComponente;

    public ItemRequisicaoCompraDerivacao() {
        itensRequisicaoComponente = Lists.newArrayList();
    }

    public DerivacaoObjetoCompra getDerivacaoObjetoCompra() {
        return derivacaoObjetoCompra;
    }

    public void setDerivacaoObjetoCompra(DerivacaoObjetoCompra derivacaoObjetoCompra) {
        this.derivacaoObjetoCompra = derivacaoObjetoCompra;
    }

    public ItemRequisicaoDeCompra getItemRequisicaoOriginal() {
        return itemRequisicaoOriginal;
    }

    public void setItemRequisicaoOriginal(ItemRequisicaoDeCompra itemRequisicaoOriginal) {
        this.itemRequisicaoOriginal = itemRequisicaoOriginal;
    }

    public List<ItemRequisicaoDeCompra> getItensRequisicaoComponente() {
        return itensRequisicaoComponente;
    }

    public void setItensRequisicaoComponente(List<ItemRequisicaoDeCompra> itensRequisicaoComponente) {
        this.itensRequisicaoComponente = itensRequisicaoComponente;
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadDisponivel;
    }

    public void setQuantidadDisponivel(BigDecimal quantidadDisponivel) {
        this.quantidadDisponivel = quantidadDisponivel;
    }

    public BigDecimal getQuantidadeUtilizada() {
        BigDecimal quantidade = BigDecimal.ZERO;
        for (ItemRequisicaoDeCompra item : itensRequisicaoComponente) {
            quantidade = quantidade.add(item.getQuantidade());
        }
        return quantidade;
    }

    public BigDecimal getQuantidadeUtilizadaOutrosItens(ItemRequisicaoDeCompra itemReq) {
        BigDecimal quantidade = BigDecimal.ZERO;
        for (ItemRequisicaoDeCompra item : itensRequisicaoComponente) {
            if (!item.equals(itemReq)) {
                quantidade = quantidade.add(item.getQuantidade());
            }
        }
        return quantidade;
    }
}
