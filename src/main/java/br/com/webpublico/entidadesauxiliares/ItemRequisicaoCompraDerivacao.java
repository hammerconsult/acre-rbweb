package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.DerivacaoObjetoCompra;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ItemRequisicaoCompraDerivacao implements Serializable {

    private ItemRequisicaoCompraVO itemRequisicaoOriginal;
    private DerivacaoObjetoCompra derivacaoObjetoCompra;
    private BigDecimal quantidadDisponivel;
    private List<ItemRequisicaoCompraVO> itensRequisicaoComponente;

    public ItemRequisicaoCompraDerivacao() {
        itensRequisicaoComponente = Lists.newArrayList();
    }

    public DerivacaoObjetoCompra getDerivacaoObjetoCompra() {
        return derivacaoObjetoCompra;
    }

    public void setDerivacaoObjetoCompra(DerivacaoObjetoCompra derivacaoObjetoCompra) {
        this.derivacaoObjetoCompra = derivacaoObjetoCompra;
    }

    public ItemRequisicaoCompraVO getItemRequisicaoOriginal() {
        return itemRequisicaoOriginal;
    }

    public void setItemRequisicaoOriginal(ItemRequisicaoCompraVO itemRequisicaoOriginal) {
        this.itemRequisicaoOriginal = itemRequisicaoOriginal;
    }

    public List<ItemRequisicaoCompraVO> getItensRequisicaoComponente() {
        return itensRequisicaoComponente;
    }

    public void setItensRequisicaoComponente(List<ItemRequisicaoCompraVO> itensRequisicaoComponente) {
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
        for (ItemRequisicaoCompraVO item : itensRequisicaoComponente) {
            quantidade = quantidade.add(item.getQuantidade());
        }
        return quantidade;
    }

    public BigDecimal getQuantidadeUtilizadaOutrosItens(ItemRequisicaoCompraVO itemReq) {
        BigDecimal quantidade = BigDecimal.ZERO;
        for (ItemRequisicaoCompraVO item : itensRequisicaoComponente) {
            if (!item.equals(itemReq)) {
                quantidade = quantidade.add(item.getQuantidade());
            }
        }
        return quantidade;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ItemRequisicaoCompraDerivacao that = (ItemRequisicaoCompraDerivacao) object;
        return Objects.equals(derivacaoObjetoCompra, that.derivacaoObjetoCompra)
            && Objects.equals(itemRequisicaoOriginal.getItemContrato(), that.itemRequisicaoOriginal.getItemContrato());
    }

    @Override
    public int hashCode() {//não utilizar assim
        return Objects.hash(derivacaoObjetoCompra, itemRequisicaoOriginal.getItemContrato());
    }
}
