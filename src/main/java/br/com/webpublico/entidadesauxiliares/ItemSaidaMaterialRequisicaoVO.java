package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemAprovacaoMaterial;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ItemSaidaMaterialRequisicaoVO implements Comparable<ItemSaidaMaterialRequisicaoVO> {

    private ItemAprovacaoMaterial itemAvaliacaoMaterial;
    private List<ItemSaidaMaterialVO> itensSaida;

    public ItemSaidaMaterialRequisicaoVO() {
        itensSaida = Lists.newArrayList();
    }

    public List<ItemSaidaMaterialVO> getItensSaida() {
        return itensSaida;
    }

    public void setItensSaida(List<ItemSaidaMaterialVO> itensSaida) {
        this.itensSaida = itensSaida;
    }

    public ItemAprovacaoMaterial getItemAvaliacaoMaterial() {
        return itemAvaliacaoMaterial;
    }

    public void setItemAvaliacaoMaterial(ItemAprovacaoMaterial itemAvaliacaoMaterial) {
        this.itemAvaliacaoMaterial = itemAvaliacaoMaterial;
    }

    public BigDecimal getQuantidadeTotalItemSaidaMaterial() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemSaidaMaterialVO item : getItensSaida()) {
            if (item.hasQuantidade()) {
                total = total.add(item.getQuantidade());
            }
        }
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemSaidaMaterialRequisicaoVO that = (ItemSaidaMaterialRequisicaoVO) o;
        return Objects.equals(itemAvaliacaoMaterial, that.itemAvaliacaoMaterial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemAvaliacaoMaterial);
    }


    @Override
    public int compareTo(ItemSaidaMaterialRequisicaoVO o) {
        if (o.getItemAvaliacaoMaterial().getMaterial() != null
            && !Util.isStringNulaOuVazia(o.getItemAvaliacaoMaterial().getMaterial().getDescricao())
            && getItemAvaliacaoMaterial().getMaterial() != null && !Util.isStringNulaOuVazia(getItemAvaliacaoMaterial().getMaterial().getDescricao())) {
            return StringUtil.removeAcentuacao(getItemAvaliacaoMaterial().getMaterial().getDescricao())
                .compareToIgnoreCase(StringUtil.removeAcentuacao(o.getItemAvaliacaoMaterial().getMaterial().getDescricao()));
        }
        return 0;
    }
}
