package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemIntencaoRegistroPreco;
import br.com.webpublico.entidades.ItemParticipanteIRP;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class ResumoItemIRPParticipanteVO {

    private ItemIntencaoRegistroPreco itemIrp;
    private List<ItemParticipanteIRP> itensParticipantes;

    public ResumoItemIRPParticipanteVO() {
        itensParticipantes = Lists.newArrayList();
    }

    public ItemIntencaoRegistroPreco getItemIrp() {
        return itemIrp;
    }

    public void setIrp(ItemIntencaoRegistroPreco irp) {
        this.itemIrp = irp;
    }

    public List<ItemParticipanteIRP> getItensParticipantes() {
        return itensParticipantes;
    }

    public void setItensParticipantes(List<ItemParticipanteIRP> itensParticipantes) {
        this.itensParticipantes = itensParticipantes;
    }

    public ItemParticipanteIRP getItemParticipanteGerenciador() {
        for (ItemParticipanteIRP itemPart : getItensParticipantes()) {
            if (itemPart.getParticipanteIRP().getGerenciador()) {
                return itemPart;
            }
        }
        return null;
    }

    public BigDecimal getValorOrQuantidadeTotalParticipantes() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemParticipanteIRP item : itensParticipantes) {
            total = total.add(item.getItemIntencaoRegistroPreco().isTipoControlePorValor() ? item.getValor() : item.getQuantidade());
        }
        return total;
    }

    public BigDecimal getValorOrQuantidadeTotalParticipanteNaoGerenciador() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemParticipanteIRP item : itensParticipantes) {
            if (!item.getParticipanteIRP().getGerenciador()) {
                total = total.add(item.getItemIntencaoRegistroPreco().isTipoControlePorValor() ? item.getValor() : item.getQuantidade());
            }
        }
        return total;
    }
}
