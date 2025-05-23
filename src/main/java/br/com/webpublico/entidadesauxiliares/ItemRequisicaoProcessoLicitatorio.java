package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ExecucaoContratoItem;
import br.com.webpublico.entidades.ExecucaoProcessoItem;
import br.com.webpublico.entidades.ItemReconhecimentoDivida;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoRequisicaoCompra;

public class ItemRequisicaoProcessoLicitatorio {

    private Long idItem;
    private ObjetoCompra objetoCompra;
    private ExecucaoContratoItem itemExecucaoContrato;
    private ExecucaoProcessoItem itemExecucaoProcesso;
    private ItemReconhecimentoDivida itemReconhecimentoDivida;
    private TipoRequisicaoCompra tipoProcesso;
    private TipoControleLicitacao tipoControle;
    private TipoContaDespesa tipoContaDespesa;

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public ExecucaoContratoItem getItemExecucaoContrato() {
        return itemExecucaoContrato;
    }

    public void setItemExecucaoContrato(ExecucaoContratoItem itemExecucaoContrato) {
        this.itemExecucaoContrato = itemExecucaoContrato;
    }

    public ExecucaoProcessoItem getItemExecucaoProcesso() {
        return itemExecucaoProcesso;
    }

    public void setItemExecucaoProcesso(ExecucaoProcessoItem itemExecucaoProcesso) {
        this.itemExecucaoProcesso = itemExecucaoProcesso;
    }

    public ItemReconhecimentoDivida getItemReconhecimentoDivida() {
        return itemReconhecimentoDivida;
    }

    public void setItemReconhecimentoDivida(ItemReconhecimentoDivida itemReconhecimentoDivida) {
        this.itemReconhecimentoDivida = itemReconhecimentoDivida;
    }

    public TipoRequisicaoCompra getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoRequisicaoCompra tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public boolean isControleValor() {
        return TipoControleLicitacao.VALOR.equals(tipoControle);
    }
}
