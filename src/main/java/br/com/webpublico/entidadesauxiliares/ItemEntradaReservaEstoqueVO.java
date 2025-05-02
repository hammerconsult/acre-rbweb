package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemEntradaMaterial;
import br.com.webpublico.entidades.ReservaEstoque;

import java.math.BigDecimal;

public class ItemEntradaReservaEstoqueVO {

    private ItemEntradaMaterial itemEntradaMaterial;
    private ReservaEstoque reservaEstoque;
    private BigDecimal quantidadeEstoque;

    public ItemEntradaMaterial getItemEntradaMaterial() {
        return itemEntradaMaterial;
    }

    public void setItemEntradaMaterial(ItemEntradaMaterial itemEntradaMaterial) {
        this.itemEntradaMaterial = itemEntradaMaterial;
    }

    public ReservaEstoque getReservaEstoque() {
        return reservaEstoque;
    }

    public void setReservaEstoque(ReservaEstoque reservaEstoque) {
        this.reservaEstoque = reservaEstoque;
    }

    public BigDecimal getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(BigDecimal quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public BigDecimal getEstoqueAtualizado() {
        return quantidadeEstoque.subtract(itemEntradaMaterial.getQuantidade());
    }

    public boolean isEstoqueNegativo() {
        return getEstoqueAtualizado().compareTo(BigDecimal.ZERO) < 0;
    }
}
