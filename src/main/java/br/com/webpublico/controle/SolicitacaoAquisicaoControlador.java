package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.GrupoContaDespesa;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoAquisicao;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SolicitacaoAquisicaoFacade;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSolicitacaoAquisicaoMovel", pattern = "/solicitacao-aquisicao-bem-movel/novo/", viewId = "/faces/administrativo/patrimonio/solicitacao-aquisicao/movel/edita.xhtml"),
    @URLMapping(id = "editarSolicitacaoAquisicaoMovel", pattern = "/solicitacao-aquisicao-bem-movel/editar/#{solicitacaoAquisicaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacao-aquisicao/movel/edita.xhtml"),
    @URLMapping(id = "listarSolicitacaoAquisicaoMovel", pattern = "/solicitacao-aquisicao-bem-movel/listar/", viewId = "/faces/administrativo/patrimonio/solicitacao-aquisicao/movel/lista.xhtml"),
    @URLMapping(id = "verSolicitacaoAquisicaoMovel", pattern = "/solicitacao-aquisicao-bem-movel/ver/#{solicitacaoAquisicaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacao-aquisicao/movel/visualizar.xhtml")

})
public class SolicitacaoAquisicaoControlador extends PrettyControlador<SolicitacaoAquisicao> implements Serializable, CRUD {

    @EJB
    private SolicitacaoAquisicaoFacade facade;
    private DoctoFiscalSolicitacaoAquisicao doctoFiscalSolicitacao;
    private ItemDoctoItemAquisicao itemDoctoSolicitacao;
    private List<ItemRequisicaoDeCompra> itensRequisicao;
    private List<ItemSolicitacaoAquisicao> itensSolicitacao;
    private Future<List<ItemSolicitacaoAquisicao>> futurePesquisar;
    private Future<SolicitacaoAquisicao> futureSalvar;
    private AssistenteMovimentacaoBens assistenteMovimentacao;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    public SolicitacaoAquisicaoControlador() {
        super(SolicitacaoAquisicao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novaSolicitacaoAquisicaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaSolicitacaoAquisicaoMovel() {
        try {
            novo();
            selecionado.setTipoBem(TipoBem.MOVEIS);
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "verSolicitacaoAquisicaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verSolicitacaoAquisicaoMovel() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarComDependenciasDocumentos(getId());
        if (selecionado.getRequisicaoDeCompra().isTipoExecucaoProcesso()) {
            selecionado.getRequisicaoDeCompra().setExecucoes(facade.getRequisicaoDeCompraFacade().buscarRequisicaoExecucao(selecionado.getRequisicaoDeCompra().getId()));
        }
        recuperarItensRequisicao();
        atribuirGrupoPatrimonialItemDocumentoFiscal();
    }

    @URLAction(mappingId = "editarSolicitacaoAquisicaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarSolicitacaoAquisicaoMovel() {
        editar();
        if (SituacaoAquisicao.FINALIZADO.equals(selecionado.getSituacao())) {
            redirecionarParaVer();
            FacesUtil.addAtencao("Solicitação já concluída, não será possível realizar alterações.");
        }
        selecionado.setDataSolicitacao(facade.getSistemaFacade().getDataOperacao());
        recuperarItensRequisicao();
        calcularQuantidadeRestanteItemRequisicaoCompra();
        atribuirGrupoPatrimonialItemDocumentoFiscal();
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void novo() {
        super.novo();
        selecionado.setNumero(null);
        selecionado.setDataSolicitacao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuario(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = facade.recuperarComDependenciasDocumentos(getId());
    }

    @Override
    public String getCaminhoPadrao() {
        switch (selecionado.getTipoBem()) {
            case IMOVEIS:
                return "/solicitacao-aquisicao-bem-imovel/";
            case MOVEIS:
                return "/solicitacao-aquisicao-bem-movel/";
            default:
                return "";
        }
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            iniciarAssistenteBarraProgresso();
            futureSalvar = facade.salvarSolicitacao(selecionado, assistenteMovimentacao);
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (ValidacaoException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica eng) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(eng.getMessage());
        } catch (Exception e) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    public void concluirSolicitacao() {
        try {
            validarConcluir();
            iniciarAssistenteBarraProgresso();
            futureSalvar = facade.concluirSolicitacao(selecionado, assistenteMovimentacao, itensSolicitacao);
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (ValidacaoException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    private void atribuirGrupoPatrimonialItemDocumentoFiscal() {
        for (DoctoFiscalSolicitacaoAquisicao docto : selecionado.getDocumentosFiscais()) {
            for (ItemDoctoItemAquisicao item : docto.getItens()) {
                ObjetoCompra objetoCompra = item.getItemRequisicaoDeCompra().getObjetoCompra();
                GrupoContaDespesa grupoContaDespesa = facade.getObjetoCompraFacade().criarGrupoContaDespesa(objetoCompra.getTipoObjetoCompra(), objetoCompra.getGrupoObjetoCompra());
                objetoCompra.setGrupoContaDespesa(grupoContaDespesa);
            }
        }
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataSolicitacao(), OperacaoMovimentacaoBem.SOLICITACAO_AQUISICAO_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    private void validarRegrasConfiguracaoMovimentacaoBem() {
        recuperarConfiguracaoMovimentacaoBem();
        configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataSolicitacao(), facade.getSistemaFacade().getDataOperacao(), operacao);
    }

    private void iniciarAssistenteBarraProgresso() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(facade.getSistemaFacade().getDataOperacao(), operacao);
        assistenteMovimentacao.zerarContadoresProcesso();
        assistenteMovimentacao.setSelecionado(selecionado);
    }

    public void buscarItensSolicitacaoAquisicao() {
        itensSolicitacao = Lists.newArrayList();
        futurePesquisar = facade.buscarItensSolicitacaoAquisicao(selecionado);
        FacesUtil.executaJavaScript("iniciarPesquisa()");
    }

    public void acompanharPesquisa() {
        if (futurePesquisar != null && futurePesquisar.isDone()) {
            FacesUtil.executaJavaScript("terminarPesquisa()");
        }
    }

    public void finalizarPesquisa() throws ExecutionException, InterruptedException {
        itensSolicitacao.addAll(futurePesquisar.get());
        for (ItemSolicitacaoAquisicao item : itensSolicitacao) {
            ObjetoCompra objetoCompra = item.getObjetoCompra();
            objetoCompra.setGrupoContaDespesa(facade.getObjetoCompraFacade().criarGrupoContaDespesa(objetoCompra.getTipoObjetoCompra(),objetoCompra.getGrupoObjetoCompra()));
        }
        FacesUtil.atualizarComponente("Formulario");
        futurePesquisar = null;
    }

    public void consultarFutureSalvar() {
        if (futureSalvar != null && futureSalvar.isDone()) {
            try {
                SolicitacaoAquisicao solicitacaoSalva = futureSalvar.get();
                if (!assistenteMovimentacao.getMensagensValidacaoFacesUtil().isEmpty()) {
                    ValidacaoException ve = new ValidacaoException();
                    for (FacesMessage mensagemValidacao : assistenteMovimentacao.getMensagensValidacaoFacesUtil()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida(mensagemValidacao.getDetail());
                    }
                    FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                    FacesUtil.executaJavaScript("closeDialog(aguarde)");
                    FacesUtil.executaJavaScript("clearInterval(timer)");
                    FacesUtil.printAllFacesMessages(ve.getMensagens());
                } else if (solicitacaoSalva != null && solicitacaoSalva.getId() != null) {
                    selecionado = solicitacaoSalva;
                    FacesUtil.executaJavaScript("finalizarSalvar()");
                }
            } catch (Exception ex) {
                FacesUtil.addOperacaoNaoRealizada("Não foi possível salvar a solicitação de aquisição " + ex.getMessage());
            }
        }
    }

    public void finalizarProcesssoSalvar() {
        futureSalvar = null;
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        redirecionarParaVer();
    }

    private void recuperarItensRequisicao() {
        try {
            itensRequisicao = facade.getRequisicaoDeCompraFacade().buscarItensRequisicao(selecionado.getRequisicaoDeCompra());
            for (ItemRequisicaoDeCompra item : itensRequisicao) {
                ObjetoCompra objetoCompra = item.getObjetoCompra();
                objetoCompra.setGrupoContaDespesa(facade.getObjetoCompraFacade().criarGrupoContaDespesa(objetoCompra.getTipoObjetoCompra(),objetoCompra.getGrupoObjetoCompra()));
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarNovoDocumentoFiscal() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getRequisicaoDeCompra() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Requisição de Compra deve ser informado.");
        }
        ve.lancarException();
    }

    public void adicionarDocumento() {
        try {
            validarDocumentoFiscal();
            doctoFiscalSolicitacao.getDocumentoFiscal().setValor(doctoFiscalSolicitacao.getDocumentoFiscal().getTotal());
            removerItemSolicitacaoAquisicao();
            Util.adicionarObjetoEmLista(selecionado.getDocumentosFiscais(), doctoFiscalSolicitacao);
            cancelarDocumento();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    private void criarItemDocumentoFiscal() {
        for (ItemRequisicaoDeCompra itemReq : itensRequisicao) {
            ItemDoctoFiscalLiquidacao itemDoctoFiscalLiquidacao = criarItemDoctoFiscalLiquidacao(itemReq);
            ItemDoctoItemAquisicao itemDoctoItemSol = criarItemDoctoItemAquisicao(itemReq, itemDoctoFiscalLiquidacao);
            Util.adicionarObjetoEmLista(doctoFiscalSolicitacao.getItens(), itemDoctoItemSol);

            BigDecimal valorTotalDocumento = doctoFiscalSolicitacao.getDocumentoFiscal().getValor();
            valorTotalDocumento = valorTotalDocumento.add(itemDoctoFiscalLiquidacao.getValorTotal());
            doctoFiscalSolicitacao.getDocumentoFiscal().setValor(valorTotalDocumento);
            if (itemDoctoFiscalLiquidacao.getQuantidade() != null && itemDoctoFiscalLiquidacao.getQuantidade().compareTo(BigDecimal.ZERO) > 0) {
                Util.adicionarObjetoEmLista(doctoFiscalSolicitacao.getDocumentoFiscal().getListaItemDoctoFiscalLiquidacao(), itemDoctoFiscalLiquidacao);
            }
            Collections.sort(doctoFiscalSolicitacao.getItens());
        }
    }

    private ItemDoctoItemAquisicao criarItemDoctoItemAquisicao(ItemRequisicaoDeCompra itemRequisicaoCompra, ItemDoctoFiscalLiquidacao itemDoctoFiscalLiquidacao) {
        ItemDoctoItemAquisicao itemDoctoSol = new ItemDoctoItemAquisicao();
        itemDoctoSol.setDoctoFiscalSolicitacao(doctoFiscalSolicitacao);
        itemDoctoSol.setItemRequisicaoDeCompra(itemRequisicaoCompra);
        itemDoctoSol.setItemDoctoFiscalLiquidacao(itemDoctoFiscalLiquidacao);
        itemDoctoSol.setQuantidadeDisponivel(itemRequisicaoCompra.getQuantidadeDisponivel());
        return itemDoctoSol;
    }

    private ItemDoctoFiscalLiquidacao criarItemDoctoFiscalLiquidacao(ItemRequisicaoDeCompra itemRequisicaoCompra) {
        ItemDoctoFiscalLiquidacao itemDoctoFiscalLiquidacao = new ItemDoctoFiscalLiquidacao();
        itemDoctoFiscalLiquidacao.setDoctoFiscalLiquidacao(doctoFiscalSolicitacao.getDocumentoFiscal());
        itemDoctoFiscalLiquidacao.setQuantidade(BigDecimal.ZERO);
        itemDoctoFiscalLiquidacao.setValorUnitario(itemRequisicaoCompra.getValorUnitario());
        return itemDoctoFiscalLiquidacao;
    }

    public void novoDocumento() {
        try {
            validarNovoDocumentoFiscal();
            doctoFiscalSolicitacao = new DoctoFiscalSolicitacaoAquisicao();
            doctoFiscalSolicitacao.setSolicitacaoAquisicao(selecionado);
            recuperarItensRequisicao();
            calcularQuantidadeRestanteItemRequisicaoCompra();
            verificarAssociacaoGrupoObjetoCompraNovoDocumentoFiscal();
            criarItemDocumentoFiscal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void distribuirItensNoDocumentoFiscal() {
        try {
            doctoFiscalSolicitacao.setItens(Lists.<ItemDoctoItemAquisicao>newArrayList());
            for (ItemRequisicaoDeCompra itemReq : itensRequisicao) {
                for (int x = 0; x < itemReq.getQuantidade().intValue(); x++) {
                    ItemDoctoFiscalLiquidacao itemDoctoFiscalLiquidacao = criarItemDoctoFiscalLiquidacao(itemReq);
                    ItemDoctoItemAquisicao itemDoctoItemSol = criarItemDoctoItemAquisicao(itemReq, itemDoctoFiscalLiquidacao);
                    itemDoctoItemSol.setSelecionado(true);
                    itemDoctoItemSol.setQuantidadeDisponivel(BigDecimal.ZERO);
                    Util.adicionarObjetoEmLista(doctoFiscalSolicitacao.getItens(), itemDoctoItemSol);

                    itemDoctoFiscalLiquidacao.setQuantidade(BigDecimal.ONE);
                    DoctoFiscalLiquidacao documentoFiscal = doctoFiscalSolicitacao.getDocumentoFiscal();
                    if (itemDoctoFiscalLiquidacao.getQuantidade() != null && itemDoctoFiscalLiquidacao.getQuantidade().compareTo(BigDecimal.ZERO) > 0) {
                        Util.adicionarObjetoEmLista(documentoFiscal.getListaItemDoctoFiscalLiquidacao(), itemDoctoFiscalLiquidacao);
                    }
                    documentoFiscal.setTotal(doctoFiscalSolicitacao.getValorTotalItensSelecionados());
                }
            }
            Collections.sort(doctoFiscalSolicitacao.getItens());
            FacesUtil.executaJavaScript("dlgDistribuirItem.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }


    private void verificarAssociacaoGrupoObjetoCompraNovoDocumentoFiscal() {
        if (itensRequisicao != null && !itensRequisicao.isEmpty()) {
            for (ItemRequisicaoDeCompra item : itensRequisicao) {
                GrupoObjetoCompraGrupoBem grupoObjetoCompraGrupoBem = facade.getGrupoObjetoCompraGrupoBemFacade().recuperarAssociacaoDoGrupoObjetoCompra(item.getObjetoCompra().getGrupoObjetoCompra(), facade.getSistemaFacade().getDataOperacao());
                if (grupoObjetoCompraGrupoBem == null) {
                    FacesUtil.addAtencao("O item requisitado: " + item.getDescricao() + " não possui associação com o grupo objeto de compra: " + item.getObjetoCompra().getGrupoObjetoCompra() + ".");
                }
            }
        }
    }

    private void calcularQuantidadeRestanteItemRequisicaoCompra() {
        for (ItemRequisicaoDeCompra itemReq : itensRequisicao) {
            itemReq.setQuantidadeDisponivel(BigDecimal.ZERO);
            BigDecimal quantidadeDisponivel = facade.getRequisicaoDeCompraFacade().buscarQuantidadeRestante(itemReq);
            BigDecimal quantidadeNoDocumento = getQuantidadeUtilizadaItemRequisicaoCompra(itemReq);
            itemReq.setQuantidadeDisponivel(quantidadeDisponivel.subtract(quantidadeNoDocumento));
        }
    }

    private BigDecimal getQuantidadeUtilizadaItemRequisicaoCompra(ItemRequisicaoDeCompra itemRequisicaoDeCompra) {
        BigDecimal quantidade = BigDecimal.ZERO;
        for (DoctoFiscalSolicitacaoAquisicao doc : selecionado.getDocumentosFiscais()) {
            for (ItemDoctoItemAquisicao itemDoc : doc.getItens()) {
                if (itemDoc.getItemRequisicaoDeCompra().getId().equals(itemRequisicaoDeCompra.getId())
                    && itemDoc.getItemDoctoFiscalLiquidacao().getQuantidade() != null) {
                    quantidade = quantidade.add(itemDoc.getItemDoctoFiscalLiquidacao().getQuantidade());
                }
            }
        }
        return quantidade;
    }

    private BigDecimal getQuantidadeUtilizadaItemDocumento(ItemDoctoItemAquisicao itemDocumento) {
        BigDecimal quantidade = BigDecimal.ZERO;
        for (DoctoFiscalSolicitacaoAquisicao doc : selecionado.getDocumentosFiscais()) {
            for (ItemDoctoItemAquisicao itemDoc : doc.getItens()) {
                if (!itemDoc.equals(itemDocumento)
                    && itemDoc.getItemRequisicaoDeCompra().getId().equals(itemDocumento.getId())
                    && itemDoc.getItemDoctoFiscalLiquidacao().getQuantidade() != null) {
                    quantidade = quantidade.add(itemDoc.getItemDoctoFiscalLiquidacao().getQuantidade());
                }
            }
        }
        return quantidade;
    }

    public void listenerRequisicaoCompra(){
        cancelarDocumento();
        if (selecionado.getRequisicaoDeCompra().isTipoExecucaoProcesso()) {
            selecionado.getRequisicaoDeCompra().setExecucoes(facade.getRequisicaoDeCompraFacade().buscarRequisicaoExecucao(selecionado.getRequisicaoDeCompra().getId()));
        }
    }

    public void cancelarDocumento() {
        doctoFiscalSolicitacao = null;
        itemDoctoSolicitacao = null;
    }

    public void removerDocumento(DoctoFiscalSolicitacaoAquisicao documento) {
        try {
            selecionado.getDocumentosFiscais().remove(documento);
        } catch (Exception ex) {
            logger.error("Erro ao remover documento {}", ex);
        }
    }

    public void visualizarItensDoDocumento(DoctoFiscalSolicitacaoAquisicao doc) {
        doctoFiscalSolicitacao = doc;
    }

    public void editarDocumento(DoctoFiscalSolicitacaoAquisicao doctoSol) {
        doctoFiscalSolicitacao = (DoctoFiscalSolicitacaoAquisicao) Util.clonarObjeto(doctoSol);

        List<ItemRequisicaoDeCompra> itensRequisicaoAdicionadosDocumentos = getItensRequisicaoAdicionadosDocumentos();
        List<ItemRequisicaoDeCompra> itensRequisicaoNaoAdicionadosDocumentos = getItensRequisicaoNaoAdicionadosDocumentos(itensRequisicaoAdicionadosDocumentos);

        for (ItemRequisicaoDeCompra itemReq : itensRequisicaoNaoAdicionadosDocumentos) {
            ItemDoctoFiscalLiquidacao itemDoctoFiscalLiquidacao = criarItemDoctoFiscalLiquidacao(itemReq);
            ItemDoctoItemAquisicao itemDoctoItemSol = criarItemDoctoItemAquisicao(itemReq, itemDoctoFiscalLiquidacao);

            Util.adicionarObjetoEmLista(doctoFiscalSolicitacao.getItens(), itemDoctoItemSol);
            Util.adicionarObjetoEmLista(doctoFiscalSolicitacao.getDocumentoFiscal().getListaItemDoctoFiscalLiquidacao(), itemDoctoFiscalLiquidacao);
        }
        atribuirQtdeDisponivelItemDocumento();
        Collections.sort(doctoFiscalSolicitacao.getItens());
    }

    private void atribuirQtdeDisponivelItemDocumento() {
        for (ItemDoctoItemAquisicao item : doctoFiscalSolicitacao.getItens()) {
            BigDecimal qtdeRequisicao = facade.getRequisicaoDeCompraFacade().buscarQuantidadeRestante(item.getItemRequisicaoDeCompra());
            BigDecimal qtdeDocumento = getQuantidadeUtilizadaItemRequisicaoCompra(item.getItemRequisicaoDeCompra());
            BigDecimal qtdeDisponivel = qtdeRequisicao.subtract(qtdeDocumento).add(item.getItemDoctoFiscalLiquidacao().getQuantidade());
            item.setQuantidadeDisponivel(qtdeDisponivel);
        }
    }

    private List<ItemRequisicaoDeCompra> getItensRequisicaoNaoAdicionadosDocumentos(List<ItemRequisicaoDeCompra> itensRequisicaoAdicionados) {
        List<ItemRequisicaoDeCompra> itens = Lists.newArrayList();
        for (ItemRequisicaoDeCompra itemReq : itensRequisicao) {
            if (!itensRequisicaoAdicionados.contains(itemReq)) {
                itens.add(itemReq);
            }
        }
        return itens;
    }

    private List<ItemRequisicaoDeCompra> getItensRequisicaoAdicionadosDocumentos() {
        List<ItemRequisicaoDeCompra> itens = Lists.newArrayList();
        for (DoctoFiscalSolicitacaoAquisicao docto : selecionado.getDocumentosFiscais()) {
            for (ItemDoctoItemAquisicao item : docto.getItens()) {
                item.setSelecionado(true);
                itens.add(item.getItemRequisicaoDeCompra());
            }
        }
        return itens;
    }

    private void validarDocumentoFiscal() {
        Util.validarCampos(doctoFiscalSolicitacao.getDocumentoFiscal());
        ValidacaoException ve = new ValidacaoException();
        if (doctoFiscalSolicitacao.getDocumentoFiscal().getDataAtesto() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de atesto deve ser informado.");
        }
        ve.lancarException();
        if (doctoFiscalSolicitacao.getItens().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione ao menos um item do documento.");
        }
        ve.lancarException();
        for (ItemDoctoItemAquisicao item : doctoFiscalSolicitacao.getItens()) {
            if (item.getSelecionado()) {
                ItemDoctoFiscalLiquidacao itemDoctoLiq = item.getItemDoctoFiscalLiquidacao();
                ItemRequisicaoDeCompra itemReq = item.getItemRequisicaoDeCompra();
                if (itemDoctoLiq.getQuantidade() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Informe a quantidade do documento para o item: " + itemReq.getNumero() + ".");
                }
                ve.lancarException();
                BigDecimal qtdeRequisitadaDisponivel = facade.getRequisicaoDeCompraFacade().buscarQuantidadeRestante(itemReq);
                if (itemDoctoLiq.getQuantidade().compareTo(qtdeRequisitadaDisponivel) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade informada no documento para o item nº " + itemReq.getNumero() + " não deve ser maior que a quantidade requisitada, que é de " + qtdeRequisitadaDisponivel + ".");
                }
                BigDecimal quantidadePorItemDocumento = item.getQuantidadeDisponivel().add(item.getItemDoctoFiscalLiquidacao().getQuantidade());
                if (itemDoctoLiq.getQuantidade().compareTo(quantidadePorItemDocumento) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade informada no documento para o item nº " + itemReq.getNumero() + " não deve ser maior que a quantidade restante, que é de " + quantidadePorItemDocumento + ".");
                }
                if (itemDoctoLiq.getQuantidade().longValue() <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade informada no documento para o item nº" + itemReq.getNumero() + " não deve ser menor ou igual zero.");
                }
            }
        }
        if (doctoFiscalSolicitacao.getDocumentoFiscal().getTipoDocumentoFiscal().getObrigarChaveDeAcesso() && doctoFiscalSolicitacao.getDocumentoFiscal().getChaveDeAcesso().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Chave de Acesso deve ser informado.");
        }
        BigDecimal valorTotalItensSelecionados = doctoFiscalSolicitacao.getValorTotalItensSelecionados();
        if (doctoFiscalSolicitacao.getDocumentoFiscal().getTotal().compareTo(valorTotalItensSelecionados) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total dos itens fiscais deve ser igual ao valor total do documento  " + doctoFiscalSolicitacao.getDocumentoFiscal().getTipoDocumentoFiscal().getDescricao() + " de " + Util.formataValor(doctoFiscalSolicitacao.getDocumentoFiscal().getTotal()));
        }
        ve.lancarException();
    }

    public BigDecimal getValorTotalItensSolicitacao() {

        BigDecimal total = BigDecimal.ZERO;
        if (itensSolicitacao != null && !itensSolicitacao.isEmpty()) {
            for (ItemSolicitacaoAquisicao item : itensSolicitacao) {
                total = total.add(item.getValorDoLancamento());
            }
        }
        return total;
    }

    public boolean hasDocumentosFiscais() {
        return selecionado.getDocumentosFiscais() != null && !selecionado.getDocumentosFiscais().isEmpty();
    }

    private void validarSalvar() {
        Util.validarCampos(selecionado);
        hasInventarioAberto();
        ValidacaoException ve = new ValidacaoException();
        validarDadosContrato(ve);
        if (!hasDocumentosFiscais()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar, é necessário adicionar documento fiscal referente aos itens da requisição de compra.");
        }
        ve.lancarException();
        for (DoctoFiscalSolicitacaoAquisicao doc : selecionado.getDocumentosFiscais()) {
            for (ItemDoctoItemAquisicao itemDoc : doc.getItens()) {
                facade.verificarAssociacaoDosGruposObjetoCompraComGrupoBem(ve, selecionado.getDataSolicitacao(), itemDoc.getItemRequisicaoDeCompra().getDescricao(), itemDoc.getItemRequisicaoDeCompra().getObjetoCompra());
            }
        }
        validarDataDocumentoDataEmpenho(ve);
        validarRegrasConfiguracaoMovimentacaoBem();
        ve.lancarException();
    }

    private void validarDadosContrato(ValidacaoException ve) {
        if (selecionado.getRequisicaoDeCompra().isTipoContrato() && selecionado.getRequisicaoDeCompra().getContrato() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A requisição de compra " + selecionado.getRequisicaoDeCompra() + " não possui contrato informado.");
        }
        ve.lancarException();
    }

    public void validarDataDocumentoDataEmpenho(ValidacaoException ve) {
        if (selecionado.getRequisicaoDeCompra() != null) {
            Date dataOperacao = DataUtil.dataSemHorario(new Date());
            Date maiorDataEmpenho = facade.getEmpenhoFacade().buscarMaiorDataEmpenhosPorRequisicaoDeCompra(selecionado.getRequisicaoDeCompra());
            if (maiorDataEmpenho == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado nenhum empenho para a requisição de compra selecionada.<b> " +
                    selecionado.getRequisicaoDeCompra().toStringAutoComplete() + " </b>.");
            } else {
                for (DoctoFiscalSolicitacaoAquisicao doc : selecionado.getDocumentosFiscais()) {
                    Date dataDocumento = DataUtil.dataSemHorario(doc.getDocumentoFiscal().getDataDocto());
                    if (dataDocumento.compareTo(DataUtil.dataSemHorario(maiorDataEmpenho)) < 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido que o documento <b> " + doc.getDocumentoFiscal().toString()
                            + " </b> possua a data do documento inferior a data do empenho. <b>Data do empenho: " + DataUtil.getDataFormatada(maiorDataEmpenho) + ".</b>");
                    }
                    if (dataDocumento.compareTo(dataOperacao) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido que o documento <b> " + doc.getDocumentoFiscal().toString()
                            + " </b> possua a data do documento superior a data de hoje. <b>Data servidor: " + DataUtil.getDataFormatada(dataOperacao) + ".</b>");
                    }
                }
            }
        }
    }

    private void validarConcluir() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        verificarSeTodosItensRequisicaoForamIncluidosNoDocumento(ve);
        facade.verificarQuantidadeNoDocumentoComItensSolicitacaoCriados(selecionado.getQuantidadeTotalItensDocumento().intValue(), itensSolicitacao.size());
        for (DoctoFiscalSolicitacaoAquisicao doc : selecionado.getDocumentosFiscais()) {
            for (ItemDoctoItemAquisicao item : doc.getItens()) {
                facade.verificarAssociacaoDosGruposObjetoCompraComGrupoBem(ve, selecionado.getDataSolicitacao(), item.getItemRequisicaoDeCompra().getDescricao(), item.getItemRequisicaoDeCompra().getObjetoCompra());
            }
        }
        if (selecionado.getValorTotalDocumento().compareTo(getValorTotalItensSolicitacao()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total da solicitação está diferente do valor total do(s) documento(s).");
        }
        validarDataDocumentoDataEmpenho(ve);
        validarRegrasConfiguracaoMovimentacaoBem();
        ve.lancarException();
    }

    private void verificarSeTodosItensRequisicaoForamIncluidosNoDocumento(ValidacaoException ve) {
        if (selecionado.getQuantidadeTotalItensDocumento().compareTo(calcularQuantidadeRequisitada()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi(ram) atribuído(s) todo(s) documentos fiscais a(os) item(ns) da requisição de compra.");
        }
        ve.lancarException();
    }

    public String formatarValor(BigDecimal valor) {
        return Util.formataValor(valor);
    }

    public void limparDadosRequiscao() {
        selecionado.setRequisicaoDeCompra(null);
        if (itensRequisicao != null && !itensRequisicao.isEmpty()) {
            itensRequisicao.clear();
        }
        cancelarDocumento();
    }

    public List<RequisicaoDeCompra> buscarRequisicaoDeCompra(String filtro) {
        return facade.getRequisicaoDeCompraFacade().buscarFiltrandoRequisicaoCompraSemMovimentacao(filtro, selecionado.getTipoBem());
    }

    public BigDecimal calcularQuantidadeRequisitada() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemRequisicaoDeCompra itens : itensRequisicao) {
            BigDecimal qtdeRequisitadaDisponivel = facade.getRequisicaoDeCompraFacade().buscarQuantidadeRestante(itens);
            total = total.add(qtdeRequisitadaDisponivel);
        }
        return total;
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public Boolean isTodosItensSelecionados() {
        try {
            boolean selecionado = true;
            for (ItemDoctoItemAquisicao item : doctoFiscalSolicitacao.getItens()) {
                if (!item.getSelecionado()) {
                    selecionado = false;
                    break;
                }
            }
            return selecionado;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    public void selecionarTodosItens() {
        for (ItemDoctoItemAquisicao item : doctoFiscalSolicitacao.getItens()) {
            item.setSelecionado(true);
        }
    }

    public void desselecionarTodosItens() {
        for (ItemDoctoItemAquisicao item : doctoFiscalSolicitacao.getItens()) {
            item.setSelecionado(false);
        }
    }

    public void selecionarItem(ItemDoctoItemAquisicao item) {
        item.setSelecionado(true);
    }

    public void desselecionarItem(ItemDoctoItemAquisicao item) {
        item.getItemDoctoFiscalLiquidacao().setQuantidade(BigDecimal.ZERO);
        item.setSelecionado(false);
    }

    private void removerItemSolicitacaoAquisicao() {
        Iterator<ItemDoctoItemAquisicao> iterator = doctoFiscalSolicitacao.getItens().iterator();
        while (iterator.hasNext()) {
            ItemDoctoItemAquisicao item = iterator.next();
            if (!item.getSelecionado()) {
                iterator.remove();
            }
        }
    }

    public void novaSeguradora(ItemDoctoItemAquisicao item) {
        itemDoctoSolicitacao = item;
        item.setSeguradora(new Seguradora());
        FacesUtil.executaJavaScript("dglSeguradora.show()");
    }

    public void adicionarSeguradora() {
        try {
            Util.validarCampos(itemDoctoSolicitacao.getSeguradora());
            FacesUtil.executaJavaScript("dglSeguradora.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void editarSeguradora(ItemDoctoItemAquisicao item) {
        itemDoctoSolicitacao = item;
        FacesUtil.executaJavaScript("dglSeguradora.show()");
    }

    public void selecionarItemDoctoItemAquisicao(ItemDoctoItemAquisicao itemDocto) {
        itemDoctoSolicitacao = itemDocto;
    }

    public void cancelarSeguradora() {
        itemDoctoSolicitacao.setSeguradora(null);
        itemDoctoSolicitacao = null;
    }

    public void novaGarantia(ItemDoctoItemAquisicao item) {
        itemDoctoSolicitacao = item;
        itemDoctoSolicitacao.setGarantia(new Garantia());
        FacesUtil.executaJavaScript("dglGarantia.show()");
    }

    public void adicionarGarantia() {
        try {
            Util.validarCampos(itemDoctoSolicitacao.getGarantia());
            FacesUtil.executaJavaScript("dglGarantia.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void editarGarantia(ItemDoctoItemAquisicao item) {
        itemDoctoSolicitacao = item;
        FacesUtil.executaJavaScript("dglGarantia.show()");
    }

    public void detalhesGarantia(ItemDoctoItemAquisicao itemDoctoItemAquisicao) {
        itemDoctoSolicitacao = itemDoctoItemAquisicao;
        FacesUtil.executaJavaScript("dglGarantia.show()");
    }

    public void cancelarGarantia() {
        itemDoctoSolicitacao.setGarantia(null);
        itemDoctoSolicitacao = null;
    }

    public void hasInventarioAberto() throws ExcecaoNegocioGenerica {
        if (selecionado.getTipoBem().isMovel()) {
            UnidadeOrganizacional unidade = facade.getRequisicaoDeCompraFacade().getUnidadeAdministrativa(selecionado.getRequisicaoDeCompra(), selecionado.getDataSolicitacao());
            HierarquiaOrganizacional hoAdm = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unidade, facade.getSistemaFacade().getDataOperacao());
            facade.getInventarioBensMoveisFacade().hasInventarioAberto(hoAdm);
        }
    }

    public void redirecionarParaSolicitacaoAquisicao(SolicitacaoAquisicao solicitacaoAquisicao) {
        selecionado = solicitacaoAquisicao;
        selecionado.setTipoBem(TipoBem.MOVEIS);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public List<TipoDocumentoFiscal> completarTipoDocumentoFiscal(String parte) {
        List<Empenho> empenhos = facade.getEmpenhoFacade().buscarEmpenhosPorRequisicaoCompra(selecionado.getRequisicaoDeCompra().getId());
        List<Long> contasDeDespesa = Lists.newArrayList();
        List<String> tiposContasDespesa = Lists.newArrayList();
        if (empenhos != null && !empenhos.isEmpty()) {
            for (Empenho empenho : empenhos) {
                if (!contasDeDespesa.contains(empenho.getContaDespesa().getId())) {
                    contasDeDespesa.add(empenho.getContaDespesa().getId());
                }
                if (!tiposContasDespesa.contains(empenho.getTipoContaDespesa().name())) {
                    tiposContasDespesa.add(empenho.getTipoContaDespesa().name());
                }
            }
        }
        List<TipoDocumentoFiscal> documentos = facade.getConfigContaDespesaTipoDocumentoFacade().buscarTiposDeDocumentosPorContaDeDespesa(contasDeDespesa, tiposContasDespesa, parte);
        if (!documentos.isEmpty()) {
            return documentos;
        }
        return facade.getTipoDocumentoFiscalFacade().buscarTiposDeDocumentosAtivos(parte.trim());
    }

    public List<ItemRequisicaoDeCompra> getItensRequisicao() {
        return itensRequisicao;
    }

    public void setItensRequisicao(List<ItemRequisicaoDeCompra> itensRequisicao) {
        this.itensRequisicao = itensRequisicao;
    }

    public List<ItemSolicitacaoAquisicao> getItensSolicitacao() {
        return itensSolicitacao;
    }

    public void setItensSolicitacao(List<ItemSolicitacaoAquisicao> itensSolicitacao) {
        this.itensSolicitacao = itensSolicitacao;
    }

    public Future<List<ItemSolicitacaoAquisicao>> getFuturePesquisar() {
        return futurePesquisar;
    }

    public void setFuturePesquisar(Future<List<ItemSolicitacaoAquisicao>> futurePesquisar) {
        this.futurePesquisar = futurePesquisar;
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteMovimentacao) {
        this.assistenteMovimentacao = assistenteMovimentacao;
    }

    public ItemDoctoItemAquisicao getItemDoctoSolicitacao() {
        return itemDoctoSolicitacao;
    }

    public void setItemDoctoSolicitacao(ItemDoctoItemAquisicao itemDoctoSolicitacao) {
        this.itemDoctoSolicitacao = itemDoctoSolicitacao;
    }

    public DoctoFiscalSolicitacaoAquisicao getDoctoFiscalSolicitacao() {
        return doctoFiscalSolicitacao;
    }

    public void setDoctoFiscalSolicitacao(DoctoFiscalSolicitacaoAquisicao doctoFiscalSolicitacao) {
        this.doctoFiscalSolicitacao = doctoFiscalSolicitacao;
    }
}
