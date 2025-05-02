package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemControleProcesso;
import br.com.webpublico.entidadesauxiliares.ItemControleProcessoMovimento;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.TipoProcesso;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AlteracaoItemProcessoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-alt-item-processo", pattern = "/alteracao-item-processo/novo/", viewId = "/faces/administrativo/licitacao/alteracao-item-processo/edita.xhtml"),
    @URLMapping(id = "listar-alt-item-processo", pattern = "/alteracao-item-processo/listar/", viewId = "/faces/administrativo/licitacao/alteracao-item-processo/lista.xhtml"),
    @URLMapping(id = "ver-alt-item-processo", pattern = "/alteracao-item-processo/ver/#{alteracaoItemProcessoControlador.id}/", viewId = "/faces/administrativo/licitacao/alteracao-item-processo/visualizar.xhtml")
})
public class AlteracaoItemProcessoControlador extends PrettyControlador<AlteracaoItemProcesso> implements Serializable, CRUD {

    @EJB
    private AlteracaoItemProcessoFacade facade;
    private List<ItemControleProcesso> itens;
    private ObjetoCompra objetoCompra;
    private TipoControleLicitacao tipoControle;

    public AlteracaoItemProcessoControlador() {
        super(AlteracaoItemProcesso.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/alteracao-item-processo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "nova-alt-item-processo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setTipoProcesso(TipoProcesso.LICITACAO);
        selecionado.setTipoAlteracaoItem(TipoAlteracaoItem.TIPO_CONTROLE);
        setTipoControle(TipoControleLicitacao.VALOR);
    }

    @URLAction(mappingId = "ver-alt-item-processo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        buscarItens();
    }

    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarRegrasEspecificas();
            selecionado = facade.salvarRetornando(selecionado, itens);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao salvar a alteração do item processo ", e);
            descobrirETratarException(e);
        }
    }

    private void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getLicitacao() == null && selecionado.getContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo " + selecionado.getTipoProcesso().getDescricao() + " deve ser informado.");
        }
        if (!hasItemSelecionado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione ao menos um item para continuar.");
        }
        ve.lancarException();
        if (selecionado.getTipoAlteracaoItem().isAlteracaoObjetoCompra()) {
            itens.stream().filter(item -> item.getSelecionado() && item.getObjetoCompraContrato() == null)
                .map(item -> "O campo objeto de compra contrato deve ser informado.")
                .forEach(ve::adicionarMensagemDeCampoObrigatorio);
        }
        ve.lancarException();
    }

    public void buscarItens() {
        try {
            validarLicitacao();
            itens = facade.buscarItensProcesso(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void recuperarObjetoCompra(ItemControleProcesso item) {
        if (item.getObjetoCompraContrato() == null) {
            FacesUtil.addCampoObrigatorio("O campo objeto de compra contrato deve ser informado.");
            return;
        }
        objetoCompra = facade.getObjetoCompraFacade().recuperar(item.getObjetoCompraContrato().getId());
        FacesUtil.executaJavaScript("$('#modalTabelaEspecificacao').modal('show')");
    }

    public void selecionarEspecificacao(ActionEvent evento) {
        ObjetoDeCompraEspecificacao especificacao = (ObjetoDeCompraEspecificacao) evento.getComponent().getAttributes().get("objeto");
        itens.stream().filter(item ->  item.getObjetoCompraContrato() !=null && item.getObjetoCompraContrato().equals(objetoCompra))
            .forEach(item -> item.setDescricaoComplementarContrato(especificacao.getTexto()));
    }

    public void atribuirNullObjetoCompraContrato(ItemControleProcesso item) {
        if (!item.getSelecionado()){
            item.setObjetoCompraContrato(null);
            item.setDescricaoComplementarContrato(null);
        }
    }

    private void validarLicitacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoProcesso().isLicitacao() && selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Processo não atende licitações de maior desconto.");
        }
        ve.lancarException();
    }

    public void listenerTipoAlteracao() {
        limparDadosGerais();
        if (selecionado.getTipoProcesso().isContrato()) {
            FacesUtil.executaJavaScript("setaFoco('Formulario:acContrato_input')");
        } else {
            FacesUtil.executaJavaScript("setaFoco('Formulario:acLicitacao_input')");
        }
    }

    public void recalcularValoresMovimentos(ItemControleProcesso item) {
        Map<Long, BigDecimal> mapValorUnitario = Maps.newHashMap();
        for (ItemControleProcessoMovimento movimento : item.getMovimentos()) {
            movimento.setTipoControle(item.getTipoControle());
            movimento.setQuantidade(item.getTipoControle().isTipoControlePorValor() ? BigDecimal.ONE : movimento.getQuantidadeOriginal());
            movimento.setValorUnitario(item.getTipoControle().isTipoControlePorValor() ? movimento.getValorTotalOriginal() : movimento.getValorUnitarioOriginal());
            movimento.setValorTotal(movimento.getValorTotalCalculado(movimento.getQuantidade(), movimento.getValorUnitario()));

            if (movimento.getTipoProcesso().isSolicitacaoCompra()) {
                mapValorUnitario.put(item.getNumeroItem(), movimento.getValorUnitarioOriginal());
            }
        }

        for (ItemControleProcessoMovimento movimento : item.getMovimentos()) {
            BigDecimal valorUnitario = mapValorUnitario.get(item.getNumeroItem());
            if (movimento.getTipoProcesso().isParticipanteIrp()) {
                movimento.setQuantidade(item.getTipoControle().isTipoControlePorValor() ? BigDecimal.ONE : movimento.getQuantidadeOriginal());
                movimento.setValorUnitario(item.getTipoControle().isTipoControlePorValor() ? movimento.getValorTotalOriginal() : valorUnitario);
                movimento.setValorTotal(movimento.getValorTotalCalculado(movimento.getQuantidadeOriginal(), valorUnitario));
            }
            if (movimento.getTipoProcesso().isIrp()) {
                movimento.setQuantidade(item.getTipoControle().isTipoControlePorValor() ? BigDecimal.ONE : movimento.getQuantidadeOriginal());
                movimento.setValorUnitario(item.getTipoControle().isTipoControlePorValor() ? movimento.getValorTotalOriginal() : valorUnitario);
                movimento.setValorTotal(movimento.getValorTotalCalculado(movimento.getQuantidadeOriginal(), valorUnitario));
            }
        }

        gerarHistorico();
    }

    private void gerarHistorico() {
        StringBuilder historico = new StringBuilder();
        for (ItemControleProcesso item : itens) {
            if (item.getSelecionado()) {
                historico.append("Item nº " + item.getNumeroItem() + " - " + item.getObjetoCompraProcesso()).append(", ");
                historico.append("<div style='color: darkblue'>").append("De <b>").append(item.getTipoControleOriginal().getDescricao()).append("</b>")
                    .append(" para <b>").append(item.getTipoControle().getDescricao()).append("</b></div>");
            }
        }
        selecionado.setHistoricoProcesso(historico.toString());
    }

    public void selecionarTodosItens(boolean selecinado) {
        for (ItemControleProcesso item : itens) {
            item.setSelecionado(selecinado);
        }
    }

    public boolean todosItensSelcionados() {
        boolean todosSelecionados = true;
        if (hasItens()) {
            for (ItemControleProcesso item : itens) {
                if (!item.getSelecionado()) {
                    todosSelecionados = false;
                    break;
                }
            }
        }
        return todosSelecionados;
    }

    public boolean hasItemSelecionado() {
        boolean selecionado = false;
        if (hasItens()) {
            for (ItemControleProcesso item : itens) {
                if (item.getSelecionado()) {
                    selecionado = true;
                    break;
                }
            }
        }
        return selecionado;
    }

    public void aplicarTipoControleParaTodosItens() {
        for (ItemControleProcesso item : itens) {
            item.setSelecionado(true);
            item.setTipoControle(tipoControle);
            recalcularValoresMovimentos(item);
        }
    }

    public boolean hasItens() {
        return itens != null && !itens.isEmpty();
    }

    public void listenerObjetoCompraContrato(ItemControleProcesso item) {
        try {
            facade.atribuirGrupoContaDespesaObjetoCompra(item.getObjetoCompraContrato());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void limparDadosGerais() {
        selecionado.setContrato(null);
        selecionado.setLicitacao(null);
        selecionado.setHistoricoProcesso(null);
        itens = Lists.newArrayList();
    }

    public List<ObjetoCompra> completarObjetoCompra(String parte) {
        return facade.getObjetoCompraFacade().buscarObjetoCompraPorSituacao(parte.trim(), SituacaoObjetoCompra.DEFERIDO);
    }

    public List<Licitacao> completarLicitacao(String parte) {
        return facade.getLicitacaoFacade().buscarLicitacaoPorNumeroModalidadeOrNumeroLicitacao(parte.trim());
    }

    public List<Contrato> completarContrato(String parte) {
        return facade.getContratoFacade().buscarFiltrandoPorSituacao(parte.trim(), SituacaoContrato.APROVADO, SituacaoContrato.DEFERIDO);
    }

    public List<SelectItem> getTiposAlteracaoItem() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(TipoAlteracaoItem.TIPO_CONTROLE, TipoAlteracaoItem.TIPO_CONTROLE.getDescricao()));
        if (selecionado.getTipoProcesso().isContrato()) {
            toReturn.add(new SelectItem(TipoAlteracaoItem.OBJETO_COMPRA, TipoAlteracaoItem.OBJETO_COMPRA.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposProcesso() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(TipoProcesso.LICITACAO, TipoProcesso.LICITACAO.getDescricao()));
        return toReturn;
    }

    public List<SelectItem> getTiposControle() {
        return Util.getListSelectItemSemCampoVazio(TipoControleLicitacao.values());
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public List<ItemControleProcesso> getItens() {
        return itens;
    }

    public void setItens(List<ItemControleProcesso> itens) {
        this.itens = itens;
    }

    public void atualizarValorUnitario(ItemControleProcessoMovimento item) {
        item.setValorUnitario(item.getValorTotal());
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }
}
