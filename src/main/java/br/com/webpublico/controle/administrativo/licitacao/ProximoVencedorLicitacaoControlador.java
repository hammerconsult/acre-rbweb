package br.com.webpublico.controle.administrativo.licitacao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.licitacao.ProximoVencedorLicitacao;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.entidadesauxiliares.PropostaFornecedorProximoVencedorVo;
import br.com.webpublico.entidadesauxiliares.ProximoVencedorLicitacaoItemVo;
import br.com.webpublico.enums.TipoClassificacaoFornecedor;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.administrativo.licitacao.ProximoVencedorLicitacaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-proximo-venc-licitacao", pattern = "/proximo-vencedor-licitacao/novo/", viewId = "/faces/administrativo/licitacao/proximo-vencedor-licitacao/edita.xhtml"),
    @URLMapping(id = "listar-proximo-venc-licitacao", pattern = "/proximo-vencedor-licitacao/listar/", viewId = "/faces/administrativo/licitacao/proximo-vencedor-licitacao/lista.xhtml"),
    @URLMapping(id = "ver-proximo-venc-licitacao", pattern = "/proximo-vencedor-licitacao/ver/#{proximoVencedorLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/proximo-vencedor-licitacao/visualizar.xhtml")
})
public class ProximoVencedorLicitacaoControlador extends PrettyControlador<ProximoVencedorLicitacao> implements Serializable, CRUD {

    @EJB
    private ProximoVencedorLicitacaoFacade facade;
    private List<LicitacaoFornecedor> fornecedoresNaoHabilitadosDocumentacao;
    private Pregao pregao;
    private List<ProximoVencedorLicitacaoItemVo> itens;
    private ProximoVencedorLicitacaoItemVo itemVoSelecionado;
    private ItemPregao itemPregaoSelecionado;
    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;
    private BigDecimal percentualDiferencaLote;


    public ProximoVencedorLicitacaoControlador() {
        super(ProximoVencedorLicitacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/proximo-vencedor-licitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-proximo-venc-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "ver-proximo-venc-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        pregao = facade.getPregaoFacade().buscarPregaoPoLicitacao(selecionado.getLicitacao());
    }

    public void salvarGerandoProcesso() {
        try {
            selecionado = facade.salvarRetornando(selecionado, itens, pregao);
            redirecionarParaVerOrEditar(selecionado.getId(), "ver");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica eng) {
            logger.error(eng.getMessage(), eng);
            FacesUtil.addErrorPadrao(eng);
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            preecherFornecedoresNaoHabilitados();
            FacesUtil.executaJavaScript("dlgAlerta.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica eng) {
            logger.error(eng.getMessage(), eng);
            FacesUtil.addErrorPadrao(eng);
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void criarItensPregaoLote(ProximoVencedorLicitacaoItemVo itemVo) {
        itemVoSelecionado = itemVo;
        itemPregaoSelecionado = facade.getPregaoFacade().recuperarItemPregaoLoteProcesso(itemVo.getItemPregao().getId());
        pregao = facade.getPregaoFacade().recuperarPregaoPorLote(itemPregaoSelecionado.getPregao().getId());
        itemPregaoSelecionado.getItemPregaoLanceVencedor().setValor(itemVo.getValorProximoLance());
        percentualDiferencaLote = facade.getPregaoFacade().criarItensPregaoLoteRetornandoPercentualDiferencaoLote(itemPregaoSelecionado, pregao);
        FacesUtil.atualizarComponente(":FormularioValorPorItem");
    }

    public void confirmarDistribuicaoItensPorLote() {
        try {
            facade.getPregaoFacade().validarRegrasItensLote(itemPregaoSelecionado);
            itemVoSelecionado.setLoteDistribuido(true);
            itemVoSelecionado.setItemPregao(itemPregaoSelecionado);
            Util.adicionarObjetoEmLista(itens, itemVoSelecionado);
            Util.adicionarObjetoEmLista(pregao.getListaDeItemPregao(), itemPregaoSelecionado);
            FacesUtil.executaJavaScript("valorPorItem.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void aplicarPercentualParaItens() {
        facade.getPregaoFacade().aplicarPercentualParaItens(itemPregaoSelecionado, percentualDiferencaLote);
    }

    private void preecherFornecedoresNaoHabilitados() {
        fornecedoresNaoHabilitadosDocumentacao = Lists.newArrayList();
        for (ProximoVencedorLicitacaoItemVo item : itens) {
            if (item.hasItemSelecionado() && item.getLicitacaoFornecedor() != null && TipoClassificacaoFornecedor.getNaoHabilitados().contains(item.getLicitacaoFornecedor().getTipoClassificacaoFornecedor())) {
                fornecedoresNaoHabilitadosDocumentacao.add(item.getLicitacaoFornecedor());
            }
        }
    }

    public List<PropostaFornecedor> completarVencedorAtual(String parte) {
        return facade.getPropostaFornecedorFacade().buscarPropostaVencedorasLancePregaoPorLicitacao(parte, selecionado.getLicitacao());
    }

    public void validarProximoLanceVencedor(ProximoVencedorLicitacaoItemVo item) {
        if (item.getValorProximoLance() != null && !isMaiorDesconto() && item.getValorProximoLance().compareTo(item.getProximoVencedor().getLancePregao().getValor()) > 0) {
            item.setValorProximoLance(BigDecimal.ZERO);
            item.setLoteDistribuido(false);
            FacesUtil.addOperacaoNaoPermitida(" O próximo lance vencedor deve ser um valor menor ou igual ao lance vencedor atual");
        }

        if (item.getValorProximoLance() != null && isMaiorDesconto() && item.getValorProximoLance().compareTo(item.getProximoVencedor().getLancePregao().getPercentualDesconto()) < 0) {
            item.setValorProximoLance(BigDecimal.ZERO);
            item.setLoteDistribuido(false);
            FacesUtil.addOperacaoNaoPermitida(" O próximo lance vencedor deve ser um valor maior ou igual ao lance vencedor atual");
        }
    }

    public void selecionarProximoVencedor(ProximoVencedorLicitacaoItemVo item, PropostaFornecedorProximoVencedorVo pv) {
        try {
            if (item != null) {
                for (PropostaFornecedorProximoVencedorVo vencedor : item.getProximosVencedores()) {
                    if (!vencedor.equals(pv)) {
                        vencedor.setSelecionado(false);
                    }
                }
                item.setProximoVencedor(pv);
                if (item.getProximoVencedor().getPropostaFornecedor().equals(selecionado.getVencedorAtual())) {
                    item.setProximoVencedor(null);
                    throw new ExcecaoNegocioGenerica("O fornecedor selecionado já é o primeiro colocado para item.");
                }
                if (item.hasItemSelecionado()) {
                    item.setLanceVencedor(item.getProximoVencedor().getLancePregao());
                    item.setValorProximoLance(isMaiorDesconto() ? item.getProximoVencedor().getLancePregao().getPercentualDesconto() : item.getProximoVencedor().getLancePregao().getValor());
                    item.setLicitacaoFornecedor(facade.getLicitacaoFornecedor(selecionado.getLicitacao(), item.getProximoVencedor().getPropostaFornecedor()));
                }
            }
        } catch (ExcecaoNegocioGenerica e) {
            item.setLanceVencedor(null);
            item.setLicitacaoFornecedor(null);
            item.setValorProximoLance(null);
            item.setProximoVencedor(null);
            pv.setSelecionado(false);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public boolean hasFornecedoresNaoHabilitadosDocumentacao() {
        return fornecedoresNaoHabilitadosDocumentacao != null && !fornecedoresNaoHabilitadosDocumentacao.isEmpty();
    }

    public List<Licitacao> completarLicitacao(String parte) {
        return facade.getLicitacaoFacade().buscarLicitacaoHomologadaPregaoRealizado(parte.trim());
    }

    public void limparDadosLicitacao() {
        selecionado.setLicitacao(null);
        limparDadosFornecedorVencedor();
    }

    public void limparDadosFornecedorVencedor() {
        selecionado.setVencedorAtual(null);
        setItens(Lists.<ProximoVencedorLicitacaoItemVo>newArrayList());
        setFornecedoresNaoHabilitadosDocumentacao(Lists.<LicitacaoFornecedor>newArrayList());
    }

    public void validarSalvar() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (!hasItemSelecionado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar um próximo vencedor para continuar.");
        }
        ve.lancarException();
        for (ProximoVencedorLicitacaoItemVo item : itens) {
            if (item.getProximoVencedor() != null
                && (item.getValorProximoLance() == null || item.getValorProximoLance().compareTo(BigDecimal.ZERO) <= 0)) {
                String nomeCampo = isMaiorDesconto() ? "Próximo Desconto" : "Próximo Lance";
                ve.adicionarMensagemDeCampoObrigatorio("O campo " + nomeCampo + " deve ser informado para o item: <b>" + item.getItemPregao().getNumeroItem() + "</br>.");
            }
            if (item.hasPropostaNoLance() && !item.getLoteDistribuido() && isApuracaoPorLote()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O lote precisar ter seus itens distribuídos para o próximo vencedor: " + item.getProximoVencedor() + ".");
            }
        }
        ve.lancarException();
    }

    private boolean hasItemSelecionado() {
        return itens.stream().anyMatch(ProximoVencedorLicitacaoItemVo::hasItemSelecionado);
    }

    public void criarItens() {
        try {
            itens = Lists.newArrayList();
            List<ItemPregao> itensVencidos = facade.getItemPregaoFacade().buscarItensVencidosPorFornecedor(selecionado.getLicitacao(), selecionado.getVencedorAtual());
            if (itensVencidos.isEmpty()) {
                throw new ValidacaoException("O fornecedor selecionado não possui itens vencidos no pregão.");
            }
            for (ItemPregao itemPregao : itensVencidos) {
                ProximoVencedorLicitacaoItemVo item = new ProximoVencedorLicitacaoItemVo();
                item.setProximosVencedores(Lists.newArrayList());
                item.setItemPregao(itemPregao);
                item.setProximoVencedorLicitacao(selecionado);
                item.setLoteDistribuido(false);

                BigDecimal valorLanceAtual = isMaiorDesconto()
                    ? itemPregao.getItemPregaoLanceVencedor().getPercentualDesconto()
                    : itemPregao.getItemPregaoLanceVencedor().getValor();
                item.setValorLanceAtual(valorLanceAtual);

                buscarProximosVencedores(itemPregao, item);
                classificarProximosVencedores(item);
                Util.adicionarObjetoEmLista(itens, item);
            }
            Collections.sort(getItens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public boolean isApuracaoPorLote() {
        return selecionado.getLicitacao() != null
            && selecionado.getLicitacao().getTipoApuracao().isPorLote();
    }

    public boolean isMaiorDesconto() {
        return selecionado.getLicitacao() != null
            && selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto();
    }

    public boolean isLoteDistribuido() {
        if (isApuracaoPorLote()) {
            for (ProximoVencedorLicitacaoItemVo itemVo : itens) {
                if (itemVo.hasPropostaNoLance() && itemVo.getLoteDistribuido()) {
                    return true;
                }
            }
        }
        return false;
    }

    public String redirecionarParaPregao() {
        if (pregao.getLicitacao().getTipoApuracao().isPorItem()) {
            return "/pregao/por-item/ver/" + pregao.getId() + "/";
        }
        return "/pregao/por-lote/ver/" + pregao.getId() + "/";
    }

    private void buscarProximosVencedores(ItemPregao itemPregao, ProximoVencedorLicitacaoItemVo item) {
        try {
            List<PropostaFornecedor> propostas = facade.getPregaoFacade().buscarPropostaLancePregao(itemPregao);
            for (PropostaFornecedor prop : propostas) {
                PropostaFornecedorProximoVencedorVo novaPropProximoVenc = new PropostaFornecedorProximoVencedorVo(prop);

                LancePregao lancePregao = buscarUltimoLanceProposta(itemPregao, prop);
                novaPropProximoVenc.setLancePregao(lancePregao);
                if (novaPropProximoVenc.getPropostaFornecedor().equals(selecionado.getVencedorAtual())) {
                    novaPropProximoVenc.setClassificacao(1);
                }
                item.getProximosVencedores().add(novaPropProximoVenc);
            }
            Collections.sort(item.getProximosVencedores());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private LancePregao buscarUltimoLanceProposta(ItemPregao itemPregao, PropostaFornecedor prop) {
        LancePregao lancePregao = facade.getPregaoFacade().buscarValorLanceUltimaRodada(prop, itemPregao);
        BigDecimal valorLance = isMaiorDesconto() ? lancePregao.getPercentualDesconto() : lancePregao.getValor();

        if (valorLance.compareTo(BigDecimal.ZERO) == 0) {
            if (isApuracaoPorLote()) {
                valorLance = facade.getPropostaFornecedorFacade().buscarValorLoteProposta(prop, itemPregao.getItemPregaoLoteProcesso().getLoteProcessoDeCompra());
            } else {
                valorLance = facade.getPropostaFornecedorFacade().buscarValorItemProposta(prop, itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra());
            }
            if (isMaiorDesconto()) {
                lancePregao.setPercentualDesconto(valorLance);
            } else {
                lancePregao.setValor(valorLance);
            }
        }
        return lancePregao;
    }

    private void classificarProximosVencedores(ProximoVencedorLicitacaoItemVo item) {
        int classificacao = 2;
        for (PropostaFornecedorProximoVencedorVo proxVenc : item.getProximosVencedores()) {
            if (!proxVenc.getPropostaFornecedor().equals(selecionado.getVencedorAtual())) {
                proxVenc.setClassificacao(classificacao);
                classificacao++;
            }
        }
        Collections.sort(item.getProximosVencedores());
    }

    public List<LicitacaoFornecedor> getFornecedoresNaoHabilitadosDocumentacao() {
        return fornecedoresNaoHabilitadosDocumentacao;
    }

    public void setFornecedoresNaoHabilitadosDocumentacao(List<LicitacaoFornecedor> fornecedoresNaoHabilitadosDocumentacao) {
        this.fornecedoresNaoHabilitadosDocumentacao = fornecedoresNaoHabilitadosDocumentacao;
    }

    public Pregao getPregao() {
        return pregao;
    }

    public void setPregao(Pregao pregao) {
        this.pregao = pregao;
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.PROXIMO_VENCEDOR_LICITACAO);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

    public List<ProximoVencedorLicitacaoItemVo> getItens() {
        return itens;
    }

    public void setItens(List<ProximoVencedorLicitacaoItemVo> itens) {
        this.itens = itens;
    }

    public BigDecimal getPercentualDiferencaLote() {
        return percentualDiferencaLote;
    }

    public void setPercentualDiferencaLote(BigDecimal percentualDiferencaLote) {
        this.percentualDiferencaLote = percentualDiferencaLote;
    }

    public ItemPregao getItemPregaoSelecionado() {
        return itemPregaoSelecionado;
    }

    public void setItemPregaoSelecionado(ItemPregao itemPregaoSelecionado) {
        this.itemPregaoSelecionado = itemPregaoSelecionado;
    }

    public ProximoVencedorLicitacaoItemVo getItemVoSelecionado() {
        return itemVoSelecionado;
    }

    public void setItemVoSelecionado(ProximoVencedorLicitacaoItemVo itemVoSelecionado) {
        this.itemVoSelecionado = itemVoSelecionado;
    }

}
