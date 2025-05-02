package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemSolicitacaoExterno;
import br.com.webpublico.entidades.UnidadeGestora;

public class AgrupadorItemAdesaoAta {

    private UnidadeGestora unidadeGestora;
    private ItemSolicitacaoExterno itemSolicitacaoExterno;
    private Boolean adesaoRealizada;

    public AgrupadorItemAdesaoAta(UnidadeGestora unidadeGestora, ItemSolicitacaoExterno itemSolicitacaoExterno) {
        this.unidadeGestora = unidadeGestora;
        this.itemSolicitacaoExterno = itemSolicitacaoExterno;
        this.adesaoRealizada = false;
    }

    public Boolean getAdesaoRealizada() {
        return adesaoRealizada;
    }

    public void setAdesaoRealizada(Boolean adesaoRealizada) {
        this.adesaoRealizada = adesaoRealizada;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public ItemSolicitacaoExterno getItemSolicitacaoExterno() {
        return itemSolicitacaoExterno;
    }

    public void setItemSolicitacaoExterno(ItemSolicitacaoExterno itemSolicitacaoExterno) {
        this.itemSolicitacaoExterno = itemSolicitacaoExterno;
    }
}
