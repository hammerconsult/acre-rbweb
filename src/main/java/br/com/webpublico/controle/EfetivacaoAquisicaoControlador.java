package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOItensAquisicao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoAquisicao;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EfetivacaoAquisicaoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaAquisicaoMovel", pattern = "/efetivacao-aquisicao-bem-movel/novo/", viewId = "/faces/administrativo/patrimonio/aquisicao/movel/edita.xhtml"),
    @URLMapping(id = "editarAquisicaoMovel", pattern = "/efetivacao-aquisicao-bem-movel/editar/#{efetivacaoAquisicaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/aquisicao/movel/edita.xhtml"),
    @URLMapping(id = "listarAquisicaoMovel", pattern = "/efetivacao-aquisicao-bem-movel/listar/", viewId = "/faces/administrativo/patrimonio/aquisicao/movel/lista.xhtml"),
    @URLMapping(id = "verAquisicaoMovel", pattern = "/efetivacao-aquisicao-bem-movel/ver/#{efetivacaoAquisicaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/aquisicao/movel/visualizar.xhtml"),

    @URLMapping(id = "novaAquisicaoImovel", pattern = "/efetivacao-aquisicao-bem-imovel/novo/", viewId = "/faces/administrativo/patrimonio/aquisicao/imovel/edita.xhtml"),
    @URLMapping(id = "editarAquisicaoImovel", pattern = "/efetivacao-aquisicao-bem-imovel/editar/#{efetivacaoAquisicaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/aquisicao/imovel/edita.xhtml"),
    @URLMapping(id = "listarAquisicaoImovel", pattern = "/efetivacao-aquisicao-bem-imovel/listar/", viewId = "/faces/administrativo/patrimonio/aquisicao/imovel/lista.xhtml"),
    @URLMapping(id = "verAquisicaoImovel", pattern = "/efetivacao-aquisicao-bem-imovel/ver/#{efetivacaoAquisicaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/aquisicao/imovel/visualizar.xhtml")
})
public class EfetivacaoAquisicaoControlador extends PrettyControlador<Aquisicao> implements Serializable, CRUD {

    @EJB
    private EfetivacaoAquisicaoFacade facade;
    private Future<Aquisicao> futureSalvar;
    private Future<List<ItemSolicitacaoAquisicao>> futurePesquisar;
    private Future<VOItensAquisicao> futurePesquisaItens;
    private List<ItemSolicitacaoAquisicao> itensSolicitacaoAquisicao;
    private List<ItemAquisicao> itensAquisicao;
    private List<DoctoFiscalSolicitacaoAquisicao> documentosFiscais;
    private AssistenteMovimentacaoBens assistenteBarraProgresso;
    private DoctoFiscalSolicitacaoAquisicao documentoFiscal;
    private ItemDoctoItemAquisicao itemDoctoItemAquisicao;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    public EfetivacaoAquisicaoControlador() {
        super(Aquisicao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novaAquisicaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaAquisicaoMovel() {
        try {
            novo();
            selecionado.setTipoBem(TipoBem.MOVEIS);
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "novaAquisicaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaAquisicaoImovel() {
        novo();
        selecionado.setTipoBem(TipoBem.IMOVEIS);
    }

    @Override
    public void novo() {
        super.novo();
        selecionado.setNumero(null);
        selecionado.setDataDeAquisicao(getDataOperacao());
        selecionado.setUsuario(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setSituacao(SituacaoAquisicao.EM_ELABORACAO);
        itensSolicitacaoAquisicao = Lists.newArrayList();
    }

    @URLAction(mappingId = "verAquisicaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verAquisicaoMovel() {
        ver();
    }

    @URLAction(mappingId = "verAquisicaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verAquisicaoImovel() {
        ver();
    }

    @URLAction(mappingId = "editarAquisicaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarAquisicaoMovel() {
        editar();
        if (SituacaoAquisicao.FINALIZADO.equals(selecionado.getSituacao())) {
            redirecionarParaVer();
        }
    }

    @URLAction(mappingId = "editarAquisicaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarAquisicaoImovel() {
        editar();
    }

    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = facade.recuperar(selecionado.getId());
    }

    @Override
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarComDepenciasDocumentosFiscais(getId());
        itensSolicitacaoAquisicao = Lists.newArrayList();
        itensAquisicao = Lists.newArrayList();

        selecionado.getSolicitacaoAquisicao().setRequisicaoDeCompra(facade.getRequisicaoDeCompraFacade().recuperarComDependenciasRequisicaoExecucao(selecionado.getSolicitacaoAquisicao().getRequisicaoDeCompra().getId()));
    }

    @Override
    public String getCaminhoPadrao() {
        switch (selecionado.getTipoBem()) {
            case IMOVEIS:
                return "/efetivacao-aquisicao-bem-imovel/";
            case MOVEIS:
                return "/efetivacao-aquisicao-bem-movel/";
            default:
                return "";
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void validarEfetivacao() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        validarAvaliacaoSolicitacao(ve);
        verificarSeUnidadePossuiEntidade(ve);
        validarItensSolicitacaoAquisicao(ve);
        for (DoctoFiscalSolicitacaoAquisicao doctoFiscalSol : documentosFiscais) {
            doctoFiscalSol = facade.getSolicitacaoAquisicaoFacade().recuperarItensDocumentoFiscal(doctoFiscalSol);
            for (ItemDoctoItemAquisicao item : doctoFiscalSol.getItens()) {
                facade.getSolicitacaoAquisicaoFacade().verificarAssociacaoDosGruposObjetoCompraComGrupoBem(ve, selecionado.getDataDeAquisicao(), item.getItemRequisicaoDeCompra().getDescricao(), item.getItemRequisicaoDeCompra().getObjetoCompra());
            }
        }
        ve.lancarException();
        validarRegrasConfiguracaoMovimentacaoBem();
    }

    private void validarItensSolicitacaoAquisicao(ValidacaoException ve) {
        if (itensSolicitacaoAquisicao == null || itensSolicitacaoAquisicao.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Os itens da solicitação de aquisição não foram carregados. Verifique se a mesma possui os itens em seu cadastro.");
        }
        ve.lancarException();
    }

    public String formatarValor(BigDecimal valor) {
        return Util.formataValor(valor);
    }

    @Override
    public void salvar() {
        try {
            validarEfetivacao();
            iniciarAssistenteBarraProgresso();
            futureSalvar = facade.efetivarAquisicao(selecionado, assistenteBarraProgresso, itensSolicitacaoAquisicao);
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

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataDeAquisicao(), OperacaoMovimentacaoBem.AQUISICAO_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    private void validarRegrasConfiguracaoMovimentacaoBem() {
        recuperarConfiguracaoMovimentacaoBem();
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataDeAquisicao(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public void buscarItensAquisicao() {
        futurePesquisaItens = facade.buscarItensEfetivacao(selecionado);
        FacesUtil.executaJavaScript("iniciarPesquisa()");
    }

    public void buscarDadosSolicitacaoAquisicao() {
        documentosFiscais = Lists.newArrayList();
        if (selecionado.getSolicitacaoAquisicao() != null) {
            SolicitacaoAquisicao solicitacaoAquisicao = facade.getSolicitacaoAquisicaoFacade().recuperarComDependenciasDocumentos(selecionado.getSolicitacaoAquisicao().getId());
            if (solicitacaoAquisicao != null) {
                selecionado.setSolicitacaoAquisicao(solicitacaoAquisicao);
                if (solicitacaoAquisicao.getDetentorArquivoComposicao() != null) {
                    selecionado.setDetentorArquivoComposicao(solicitacaoAquisicao.getDetentorArquivoComposicao());
                }
                futurePesquisar = facade.getSolicitacaoAquisicaoFacade().buscarItensSolicitacaoAquisicao(solicitacaoAquisicao);
                FacesUtil.executaJavaScript("iniciarPesquisa()");
            } else {
                FacesUtil.addOperacaoNaoPermitida("Não foi possível recuperar os dados da solicitação de aquisição de bens móveis.");
            }
        }
    }

    public BigDecimal getValorTotalDosBens() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (!itensAquisicao.isEmpty()) {
            for (ItemAquisicao itemAquisicao : itensAquisicao) {
                valorTotal = valorTotal.add(itemAquisicao.getValorOriginal());
            }
        }
        return valorTotal;
    }

    public BigDecimal getValorTotalLancamento() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (!itensAquisicao.isEmpty()) {
            for (ItemAquisicao itemAquisicao : itensAquisicao) {
                valorTotal = valorTotal.add(itemAquisicao.getItemSolicitacaoAquisicao().getItemRequisicaoCompra().getValorUnitario());
            }
        }
        return valorTotal;
    }

    public BigDecimal getValorTotalLiquidado() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (!itensAquisicao.isEmpty()) {
            for (ItemAquisicao itemAquisicao : itensAquisicao) {
                valorTotal = valorTotal.add(itemAquisicao.getEstadoResultante().getValorLiquidado());
            }
        }
        return valorTotal;
    }


    public BigDecimal getValorTotalItensSolicitacao() {
        BigDecimal total = BigDecimal.ZERO;
        if (!itensSolicitacaoAquisicao.isEmpty()) {
            for (ItemSolicitacaoAquisicao item : itensSolicitacaoAquisicao) {
                total = total.add(item.getValorDoLancamento());
            }
        }
        return total;
    }

    public void visualizarSeguradoraNoDialog(ItemDoctoItemAquisicao item) {
        this.itemDoctoItemAquisicao = item;
        FacesUtil.executaJavaScript("dglSeguradora.show();");
    }

    public void visualizarGarantiaNoDialog(ItemDoctoItemAquisicao item) {
        this.itemDoctoItemAquisicao = item;
        FacesUtil.executaJavaScript("dglGarantia.show();");
    }

    public BigDecimal getValorTotalItensDocumentoFiscal() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (documentoFiscal != null) {
            for (ItemDoctoItemAquisicao item : documentoFiscal.getItens()) {
                valorTotal = valorTotal.add(item.getItemDoctoFiscalLiquidacao().getValorTotal());
            }
        }
        return valorTotal;
    }

    public BigDecimal getValorTotalDocumentoFiscal() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (documentosFiscais != null) {
            for (DoctoFiscalSolicitacaoAquisicao doc : documentosFiscais) {
                valorTotal = valorTotal.add(doc.getDocumentoFiscal().getTotal());
            }
        }
        return valorTotal;
    }

    public void visualizarItemDocumentoFiscal(DoctoFiscalSolicitacaoAquisicao documento) {
        documentoFiscal = facade.getSolicitacaoAquisicaoFacade().recuperarItensDocumentoFiscal(documento);
        for (ItemDoctoItemAquisicao item : documentoFiscal.getItens()) {
            ObjetoCompra objetoCompra = item.getItemRequisicaoDeCompra().getObjetoCompra();
            objetoCompra.setGrupoContaDespesa(facade.getObjetoCompraFacade().criarGrupoContaDespesa(objetoCompra.getTipoObjetoCompra(), objetoCompra.getGrupoObjetoCompra()));
        }
    }

    private void verificarSeUnidadePossuiEntidade(ValidacaoException vl) {
        UnidadeOrganizacional unidadeOrganizacional = facade.getRequisicaoDeCompraFacade().getUnidadeAdministrativa(selecionado.getSolicitacaoAquisicao().getRequisicaoDeCompra(), selecionado.getDataDeAquisicao());
        Entidade entidade = facade.getEntidadeFacade().recuperarEntidadePorUnidadeOrganizacional(unidadeOrganizacional);
        if (entidade == null) {
            vl.adicionarMensagemDeOperacaoNaoPermitida("A unidade organizacional " + unidadeOrganizacional + " deve estar associada a uma entidade.");
        }
    }

    private void validarAvaliacaoSolicitacao(ValidacaoException vl) {
        if (selecionado.isSolicitacaoRecusada()) {
            if (Strings.isNullOrEmpty(selecionado.getSolicitacaoAquisicao().getMotivo())) {
                vl.adicionarMensagemDeCampoObrigatorio("O campo Motivo deve ser informado.");
            }
            vl.lancarException();
        }
    }

    public Boolean getHabilitarTermoDeResponsabilidade() {
        if (itensAquisicao != null && !itensAquisicao.isEmpty()) {
            for (ItemAquisicao itemAquisicao : itensAquisicao) {
                if (!SituacaoEventoBem.FINALIZADO.equals(itemAquisicao.getSituacaoEventoBem())) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public List<SelectItem> getSituacoesParaEfetivacao() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(Aquisicao.SituacaoEfetivacao.ACEITA, Aquisicao.SituacaoEfetivacao.ACEITA.getDescricao()));
        toReturn.add(new SelectItem(Aquisicao.SituacaoEfetivacao.REJEITADA, Aquisicao.SituacaoEfetivacao.REJEITADA.getDescricao()));
        return toReturn;
    }

    public void acompanharPesquisa() {
        if (isOperacaoNovo() && futurePesquisar != null && futurePesquisar.isDone()) {
            FacesUtil.executaJavaScript("terminarPesquisa()");
        }
        if (!isOperacaoEditar() && futurePesquisaItens != null && futurePesquisaItens.isDone()) {
            FacesUtil.executaJavaScript("terminarPesquisa()");
        }
    }

    public void finalizarPesquisa() throws ExecutionException, InterruptedException {
        if (isOperacaoNovo()) {
            itensSolicitacaoAquisicao = Lists.newArrayList();
            itensSolicitacaoAquisicao.addAll(futurePesquisar.get());
        } else {
            itensAquisicao = Lists.newArrayList();
            itensSolicitacaoAquisicao = Lists.newArrayList();
            itensSolicitacaoAquisicao.addAll(futurePesquisaItens.get().getItensSolicitacaoAquisicao());
            itensAquisicao.addAll(futurePesquisaItens.get().getItensEfetivacaoAquisicao());
        }
        if (selecionado.getSolicitacaoAquisicao() != null) {
            documentosFiscais = facade.getSolicitacaoAquisicaoFacade().buscarDocumentoFiscal(selecionado.getSolicitacaoAquisicao());
        }
        futurePesquisar = null;
        futurePesquisaItens = null;
        FacesUtil.atualizarComponente("Formulario");
    }

    private void iniciarAssistenteBarraProgresso() {
        assistenteBarraProgresso = new AssistenteMovimentacaoBens(getDataOperacao(), operacao);
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setTotal(getItensSolicitacaoAquisicao().size());
        assistenteBarraProgresso.setSelecionado(selecionado);
        recuperarConfiguracaoMovimentacaoBem();
        assistenteBarraProgresso.setConfigMovimentacaoBem(configMovimentacaoBem);
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public List<SolicitacaoAquisicao> completarSolicitacaoAquisicao(String parte) {
        return facade.getSolicitacaoAquisicaoFacade().buscarFiltrandoSolicitacaoAguardandoEfetivacao(parte.trim(), selecionado.getTipoBem());
    }

    public void finalizarProcesssoSalvar() {
        futureSalvar = null;
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        if (selecionado.isSolicitacaoAceita()) {
            FacesUtil.addAtencao("Ao realizar esta aquisição de bens, é de responsabilidade do usuário realizar a liquidação do mesmo dentro do exercício corrente!");
        }
        redirecionarParaVer();
    }

    public void consultarFutureSalvar() {
        if (futureSalvar != null && futureSalvar.isDone()) {
            try {
                Aquisicao aquisicaoSalva = futureSalvar.get();
                if (aquisicaoSalva != null) {
                    selecionado = aquisicaoSalva;
                    FacesUtil.executaJavaScript("finalizarSalvar()");
                }
            } catch (Exception ex) {
                FacesUtil.addOperacaoNaoRealizada("Não foi possível salvar a efetivação de aquisição " + ex.getMessage());
            }
        }
    }

    public void limparDadosSolicitacao() {
        selecionado.setSolicitacaoAquisicao(null);
        if (itensSolicitacaoAquisicao != null) {
            itensSolicitacaoAquisicao.clear();
        }
        if (documentosFiscais != null) {
            documentosFiscais.clear();
        }
    }

    public Future<List<ItemSolicitacaoAquisicao>> getFuturePesquisar() {
        return futurePesquisar;
    }

    public void setFuturePesquisar(Future<List<ItemSolicitacaoAquisicao>> futurePesquisar) {
        this.futurePesquisar = futurePesquisar;
    }

    public List<ItemSolicitacaoAquisicao> getItensSolicitacaoAquisicao() {
        return itensSolicitacaoAquisicao;
    }

    public void setItensSolicitacaoAquisicao(List<ItemSolicitacaoAquisicao> itensSolicitacaoAquisicao) {
        this.itensSolicitacaoAquisicao = itensSolicitacaoAquisicao;
    }

    public AssistenteMovimentacaoBens getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteMovimentacaoBens assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public String getGrupoPatrimonial(GrupoObjetoCompra grupo) {
        try {
            return facade.getGrupoObjetoCompraGrupoBemFacade().recuperarAssociacaoDoGrupoObjetoCompra(grupo, selecionado.getDataDeAquisicao()).getGrupoBem().toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public DoctoFiscalSolicitacaoAquisicao getDocumentoFiscal() {
        return documentoFiscal;
    }

    public void setDocumentoFiscal(DoctoFiscalSolicitacaoAquisicao documentoFiscal) {
        this.documentoFiscal = documentoFiscal;
    }

    public List<DoctoFiscalSolicitacaoAquisicao> getDocumentosFiscais() {
        return documentosFiscais;
    }

    public void setDocumentosFiscais(List<DoctoFiscalSolicitacaoAquisicao> documentosFiscais) {
        this.documentosFiscais = documentosFiscais;
    }

    public List<ItemAquisicao> getItensAquisicao() {
        return itensAquisicao;
    }

    public void setItensAquisicao(List<ItemAquisicao> itensAquisicao) {
        this.itensAquisicao = itensAquisicao;
    }

    public ItemDoctoItemAquisicao getItemDoctoItemAquisicao() {
        return itemDoctoItemAquisicao;
    }

    public void setItemDoctoItemAquisicao(ItemDoctoItemAquisicao itemDoctoItemAquisicao) {
        this.itemDoctoItemAquisicao = itemDoctoItemAquisicao;
    }
}
