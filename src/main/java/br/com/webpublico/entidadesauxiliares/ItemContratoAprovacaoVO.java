package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemContrato;
import br.com.webpublico.entidades.MovimentoItemContrato;

public class ItemContratoAprovacaoVO {

    private ItemContrato itemContrato;
    private GrupoContaDespesa grupoContaDespesa;
    private MovimentoItemContrato movimentoItemExecucao;
    private MovimentoItemContrato movimentoItemVariacao;


    public ItemContrato getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(ItemContrato itemContrato) {
        this.itemContrato = itemContrato;
    }

    public MovimentoItemContrato getMovimentoItemExecucao() {
        return movimentoItemExecucao;
    }

    public void setMovimentoItemExecucao(MovimentoItemContrato movimentoItemExecucao) {
        this.movimentoItemExecucao = movimentoItemExecucao;
    }

    public MovimentoItemContrato getMovimentoItemVariacao() {
        return movimentoItemVariacao;
    }

    public void setMovimentoItemVariacao(MovimentoItemContrato movimentoItemVariacao) {
        this.movimentoItemVariacao = movimentoItemVariacao;
    }

    public GrupoContaDespesa getGrupoContaDespesa() {
        return grupoContaDespesa;
    }

    public void setGrupoContaDespesa(GrupoContaDespesa grupoContaDespesa) {
        this.grupoContaDespesa = grupoContaDespesa;
    }
}
