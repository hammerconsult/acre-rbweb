package br.com.webpublico.controle;

import br.com.webpublico.entidades.Cotacao;
import br.com.webpublico.entidades.FormularioCotacao;
import br.com.webpublico.entidades.IntencaoRegistroPreco;
import br.com.webpublico.entidadesauxiliares.ItemFormularioCompraVO;
import br.com.webpublico.entidadesauxiliares.LoteFormularioCompraVO;
import br.com.webpublico.enums.TipoApuracaoLicitacao;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.negocios.ObjetoCompraFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ManagedBean
@ViewScoped
public class TabelaLoteItemProcessoCompraControlador implements Serializable {

    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    private List<LoteFormularioCompraVO> lotesVO;
    private List<ItemFormularioCompraVO> itensVO;
    private ItemFormularioCompraVO itemVOSelecionado;
    private TipoMovimentoProcessoLicitatorio tipoProcesso;
    private Object selecionado;
    private TipoApuracaoLicitacao tipoVisualizacaoTabela;

    public void preRenderComponente(Object selecionado, TipoMovimentoProcessoLicitatorio tipoProcesso) {
        this.selecionado = selecionado;
        this.tipoProcesso = tipoProcesso;
        tipoVisualizacaoTabela = TipoApuracaoLicitacao.LOTE;
        lotesVO = Lists.newArrayList();
        itensVO = Lists.newArrayList();
        buscarLotesAndItens();
        FacesUtil.executaJavaScript("carregarTodasUnidadesMedidasDaTela()");
    }

    public List<SelectItem> getTiposVisualizacaoTabela() {
        List<SelectItem> tipos = Lists.newArrayList();
        tipos.add(new SelectItem(TipoApuracaoLicitacao.LOTE, "Lote"));
        tipos.add(new SelectItem(TipoApuracaoLicitacao.ITEM, "Item"));
        return tipos;
    }

    public void buscarLotesAndItens() {
        switch (tipoProcesso) {
            case IRP:
                if (!hasLotesVO()) {
                    setLotesVO(LoteFormularioCompraVO.popularFormularioCompraVOFromIRP(getIrp().getLotes()));
                    ordernarLoteAndItens();
                    criarGrupoContaDespesa();
                }
                break;
            case FORMULARIO_COTACAO:
                if (!hasLotesVO()) {
                    setLotesVO(LoteFormularioCompraVO.popularFormularioCompraVOFromFormulario(getFormularioCotacao().getLotesFormulario()));
                    ordernarLoteAndItens();
                    criarGrupoContaDespesa();
                }
                break;
            case COTACAO:
                if (!hasLotesVO()) {
                    setLotesVO(LoteFormularioCompraVO.popularFormularioCompraVOFromCotacao(getCotacao().getLotes()));
                    ordernarLoteAndItens();
                    criarGrupoContaDespesa();
                }
                break;
            case SOLICITACAO_COMPRA:
                break;
        }
    }

    public void selecionarItemVO(ItemFormularioCompraVO itemVO){
        itemVOSelecionado = itemVO;
    }

    private void ordernarLoteAndItens() {
        Collections.sort(lotesVO);
        lotesVO.forEach(lote -> Collections.sort(lote.getItens()));
        lotesVO.forEach(lote -> itensVO.addAll(lote.getItens()));
    }

    private void criarGrupoContaDespesa() {
        lotesVO.stream()
            .flatMap(lote -> lote.getItens().stream())
            .forEach(item -> item.getObjetoCompra().setGrupoContaDespesa(objetoCompraFacade.criarGrupoContaDespesa(item.getObjetoCompra().getTipoObjetoCompra(), item.getObjetoCompra().getGrupoObjetoCompra())));
    }

    public boolean hasLotesVO() {
        return !Util.isListNullOrEmpty(lotesVO);
    }

    public IntencaoRegistroPreco getIrp() {
        return (IntencaoRegistroPreco) selecionado;
    }

    public FormularioCotacao getFormularioCotacao() {
        return (FormularioCotacao) selecionado;
    }

    public Cotacao getCotacao() {
        return (Cotacao) selecionado;
    }

    public List<LoteFormularioCompraVO> getLotesVO() {
        return lotesVO;
    }

    public void setLotesVO(List<LoteFormularioCompraVO> lotesVO) {
        this.lotesVO = lotesVO;
    }

    public List<ItemFormularioCompraVO> getItensVO() {
        return itensVO;
    }

    public void setItensVO(List<ItemFormularioCompraVO> itensVO) {
        this.itensVO = itensVO;
    }

    public TipoMovimentoProcessoLicitatorio getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoMovimentoProcessoLicitatorio tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public TipoApuracaoLicitacao getTipoVisualizacaoTabela() {
        return tipoVisualizacaoTabela;
    }

    public void setTipoVisualizacaoTabela(TipoApuracaoLicitacao tipoVisualizacaoTabela) {
        this.tipoVisualizacaoTabela = tipoVisualizacaoTabela;
    }

    public ItemFormularioCompraVO getItemVOSelecionado() {
        return itemVOSelecionado;
    }

    public void setItemVOSelecionado(ItemFormularioCompraVO itemVOSelecionado) {
        this.itemVOSelecionado = itemVOSelecionado;
    }
}
