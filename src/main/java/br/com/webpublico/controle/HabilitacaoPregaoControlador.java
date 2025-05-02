package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.HabilitacaoPregaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 23/04/14
 * Time: 09:54
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "habilitar-fornecedores", pattern = "/licitacao/habilitar-fornecedores/", viewId = "/faces/administrativo/licitacao/habilitacaofornecedor/edita.xhtml")
})
public class HabilitacaoPregaoControlador extends PrettyControlador<Licitacao> implements CRUD, Serializable {

    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private PregaoFacade pregaoFacade;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private LicitacaoDoctoFornecedorFacade licitacaoDoctoFornecedorFacade;
    @EJB
    private HabilitacaoPregaoFacade habilitacaoPregaoFacade;
    @EJB
    private FornecedorFacade fornecedorFacade;
    @EJB
    private StatusFornecedorLicitacaoFacade statusFornecedorLicitacaoFacade;

    private List<LicitacaoFornecedor> licitacaoFornecedoresVencedores;
    private LicitacaoFornecedor fornecedorSelecionado;
    private LicitacaoDoctoFornecedor documentoDoFornecedorSelecionado;
    private StatusLicitacao statusDaLicitacao;
    private List<ItemPropostaFornecedor> itensVencidosDoFornecedor;

    public HabilitacaoPregaoControlador() {
        super(Licitacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/licitacao/habilitar-pregao/";
    }

    public List<LicitacaoFornecedor> getLicitacaoFornecedoresVencedores() {
        return licitacaoFornecedoresVencedores;
    }

    public void setLicitacaoFornecedoresVencedores(List<LicitacaoFornecedor> licitacaoFornecedoresVencedores) {
        this.licitacaoFornecedoresVencedores = licitacaoFornecedoresVencedores;
    }

    public boolean isLicitacaoHomologada() {
        if (statusDaLicitacao == null) {
            return false;
        }
        return TipoSituacaoLicitacao.HOMOLOGADA.equals(statusDaLicitacao.getTipoSituacaoLicitacao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return pregaoFacade;
    }

    @URLAction(mappingId = "habilitar-fornecedores", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void habilitacaoDoPregao() {
    }

    @Override
    public void salvar() {
//        if (validarCamposObrigatorio()) {
//
//            licitacaoFacade.salvarHabilitacaoFornecedor(licitacaoFornecedoresVencedores, selecionado);
//            pregaoFacade.salvarHabilitacaoPregao(listaDeItemPregao, selecionado);
//
//            FacesUtil.redirecionamentoInterno(getCaminhoPadrao());
//            FacesUtil.addInfo("Registro salvo com sucesso!", " O Pregão " + selecionado.toString() + " com o(s) " + selecionado.getListaDeItemPregao().size() + " Item(ns) foi salvo com sucesso!");
//        } else {
//            FacesUtil.addError("Atenção", "O campo Pregão é obrigatório!");
//        }
    }

    @Override
    public void cancelar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao());
    }

    public LicitacaoFornecedor getFornecedorSelecionado() {
        return fornecedorSelecionado;
    }

    public void setFornecedorSelecionado(LicitacaoFornecedor fornecedorSelecionado) {
        this.fornecedorSelecionado = fornecedorSelecionado;
    }

    public LicitacaoDoctoFornecedor getDocumentoDoFornecedorSelecionado() {
        return documentoDoFornecedorSelecionado;
    }

    public void setDocumentoDoFornecedorSelecionado(LicitacaoDoctoFornecedor documentoDoFornecedorSelecionado) {
        this.documentoDoFornecedorSelecionado = documentoDoFornecedorSelecionado;
    }

    public ConverterAutoComplete getConverterLicitacao() {
        return new ConverterAutoComplete(Licitacao.class, licitacaoFacade);
    }

    public List<Licitacao> buscarLicitacoes(String parte) {
        return licitacaoFacade.buscarLicitacoesPorModalidadeAndNumeroOrDescricaoOrExercico(parte.trim());
    }

    public Boolean desabilitaBotaoAdicionarDocumentoFornecedor(DoctoHabilitacao documentoHabilitacao) {
        try {
            for (LicitacaoDoctoFornecedor licitacaoDoctoFornecedor : fornecedorSelecionado.getDocumentosFornecedor()) {
                if (licitacaoDoctoFornecedor.getDoctoHabilitacao().equals(documentoHabilitacao)) {
                    return Boolean.TRUE;
                }
            }

            return Boolean.FALSE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    public String tituloBotaoAdicionarDocumentoFornecedor(DoctoHabilitacao documentoHabilitacao) {
        return desabilitaBotaoAdicionarDocumentoFornecedor(documentoHabilitacao) ? "O documento já foi adicionado a lista de documentos apresentados." : "Clique para vincular este documento.";
    }

    public void passarDocumentoDaLicitacaoParaFornecedor(ActionEvent evento) {
        this.documentoDoFornecedorSelecionado = new LicitacaoDoctoFornecedor();
        this.documentoDoFornecedorSelecionado.setDoctoHabilitacao((DoctoHabilitacao) evento.getComponent().getAttributes().get("itemOrigem"));
        FacesUtil.atualizarComponente("form-documento-fornecedor");
        FacesUtil.executaJavaScript("dialogDocumentoFornecedor.show()");
    }

    public void removerDocumentoDoFornecedor(LicitacaoDoctoFornecedor item) {
        fornecedorSelecionado.getDocumentosFornecedor().remove(item);
    }

    public List<SelectItem> getTipoEmpresa() {
        return licitacaoFacade.getTipoEmpresa();
    }

    @Override
    public void setSelecionado(Licitacao selecionado) {
        if (selecionado == null) {
            licitacaoFornecedoresVencedores = null;
        }
        super.setSelecionado(selecionado);
    }

    public void carregarDependencias(SelectEvent ev) {
        Licitacao l = (Licitacao) ev.getObject();
        selecionado = habilitacaoPregaoFacade.recuperar(l.getId());
        statusDaLicitacao = selecionado.getStatusAtualLicitacao();
        if (selecionado.getAbertaEm() == null) {
            FacesUtil.addOperacaoNaoPermitida("A Licitação selecionada não possui Data de Abertura.");
            FacesUtil.addOperacaoNaoPermitida("Por favor, informe a data de abertura antes de habilitar o(s) fornecedor(es).");
            return;
        }

        adicionarFornecedorNaListaDeFornecedores();

        if (licitacaoFornecedoresVencedores == null || licitacaoFornecedoresVencedores.isEmpty()) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), "A licitação não possui vencedores.");
            return;
        }
        carregarItensDosFornecedoresVencedores();
        definirStatusAtualDosFornecedoresParaSeusItens();
    }

    private void adicionarFornecedorNaListaDeFornecedores() {
        licitacaoFornecedoresVencedores = new ArrayList<>();
        if (selecionado.isPregao() || selecionado.isRDCPregao()) {
            if (temPregaoRealizadoParaLicitacao()) {
                for (ItemPregao itemPregao : getPregao().getListaDeItemPregao()) {
                    LicitacaoFornecedor fornecedorVencedorDoItem = selecionado.getFornecedorVencedorDoPregao(itemPregao);
                    if (fornecedorVencedorDoItem != null) {
                        if (!licitacaoFornecedoresVencedores.contains(fornecedorVencedorDoItem)) {
                            licitacaoFornecedoresVencedores.add(fornecedorVencedorDoItem);
                        }
                    }
                }

            }
        } else {
            licitacaoFornecedoresVencedores = selecionado.getFornecedores();
        }
    }

    public boolean isItemAdjudicado(Pessoa pessoa) {
        return !getStatusFornecedorLicitacaoFacade().buscarItensVencidosFornecedorPorStatus(selecionado, pessoa, TipoClassificacaoFornecedor.getHabilitados(), SituacaoItemProcessoDeCompra.ADJUDICADO).isEmpty();

    }

    private boolean temPregaoRealizadoParaLicitacao() {
        if (getPregao() == null) {
            FacesUtil.addAtencao("A licitação não possui pregão realizado.");
            return false;
        }
        return true;
    }

    private Pregao getPregao() {
        return habilitacaoPregaoFacade.getPregaoFacade().recuperarPregaoAPartirDaLicitacao(selecionado);
    }

    private void carregarItensDosFornecedoresVencedores() {
        for (LicitacaoFornecedor fornec : licitacaoFornecedoresVencedores) {
            if (selecionado.isPregao() || selecionado.isRDCPregao()) {
                fornec.getEmpresa().setItensPropostaFornecedor(licitacaoFacade.getItensVencidosPeloFornecedorPorStatusNoPregao(selecionado,
                    fornec.getEmpresa(), TipoClassificacaoFornecedor.getNaoHabilitados(), Lists.newArrayList(SituacaoItemProcessoDeCompra.values())));
            } else {
                List<ItemPropostaFornecedor> itens = licitacaoFacade.getItensVencidosPeloFornecedorPorStatus(selecionado,
                    SituacaoItemProcessoDeCompra.HOMOLOGADO, fornec.getEmpresa(), TipoClassificacaoFornecedor.getTodosTipos());
                fornec.getEmpresa().setItensPropostaFornecedor(itens);
            }
        }
    }

    private boolean isPregao() {
        return selecionado.isPregao();
    }

    private void definirStatusAtualDosFornecedoresParaSeusItens() {
        for (LicitacaoFornecedor lf : licitacaoFornecedoresVencedores) {
            for (ItemPropostaFornecedor ipf : lf.getEmpresa().getItensPropostaFornecedor()) {
                if (isPregao() || selecionado.isRDCPregao()) {
                    ipf.setItemPregao(pregaoFacade.getItemPregaoDoItemPropostaFornecedor(ipf));
                }
            }
        }
    }

    public boolean todosItensMarcados() {
        try {
            for (ItemPropostaFornecedor ipf : itensVencidosDoFornecedor) {
                if (ipf.getSelecionado() == null || !ipf.getSelecionado()) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public String getSituacaoDoFornecedor(ItemPropostaFornecedor ipf) {
        try {
            return ipf.getItemPregao().getStatusFornecedorVencedor().getDescricao();
        } catch (NullPointerException npe) {
        }
        return TipoClassificacaoFornecedor.INABILITADO.getDescricao();
    }

    public void carregarDocumentosNecessariosLicitacao(ActionEvent ev) {
        if (podeLancarDocumentos()) {
            fornecedorSelecionado = (LicitacaoFornecedor) ev.getComponent().getAttributes().get("fornecedor");
            FacesUtil.executaJavaScript("dialogHabFornecedor.show()");
        }
    }

    public void carregarItensVencidosDoFornecedor(ActionEvent ev) {
        itensVencidosDoFornecedor = Lists.newArrayList();
        if (podeLancarDocumentos()) {
            fornecedorSelecionado = (LicitacaoFornecedor) ev.getComponent().getAttributes().get("fornecedor");
            for (ItemPropostaFornecedor ipf : fornecedorSelecionado.getEmpresa().getItensPropostaFornecedor()) {
                ItemPropostaFornecedor item = (ItemPropostaFornecedor) Util.clonarObjeto(ipf);
                item.setItemPregao(pregaoFacade.getItemPregaoDoItemPropostaFornecedor(ipf));
                itensVencidosDoFornecedor.add(item);
            }
            FacesUtil.executaJavaScript("dialogItensDoFornecedor.show()");
        }
    }

    private boolean podeLancarDocumentos() {
        if ((selecionado.isPregao() || selecionado.isRDCPregao()) && !licitacaoSelecionadaEstaVinculadaAUmPregao()) {
            FacesUtil.addOperacaoNaoPermitida("Esta licitação é de modalidade pregão. Portanto será permitido lançar o(s) documento(s) somente depois da realização do pregão.");
            return false;
        }
        return true;
    }

    private boolean licitacaoSelecionadaEstaVinculadaAUmPregao() {
        if (licitacaoFacade.licitacaoPossuiVinculoComPregao(selecionado) != null) {
            return true;
        }
        return false;
    }

    private void atualizarItens() {
        if (!isPregao() && !selecionado.isRDCPregao()) {
            return;
        }
        if (!fornecedorSelecionado.hasDocumentos()) {
            for (ItemPropostaFornecedor ipf : fornecedorSelecionado.getEmpresa().getItensPropostaFornecedor()) {
                ipf.getItemPregao().setStatusFornecedorVencedor(TipoClassificacaoFornecedor.INABILITADO);
            }
        }
    }

    public void confirmarDocumentoSelecionadoDoFornecedor() {
        try {
            documentoDoFornecedorSelecionado.validarCamposObrigatoriosDocumentosSelecionados();
            licitacaoFacade.verificarStatusLicitacao(selecionado);
        } catch (StatusLicitacaoException se) {
            FacesUtil.redirecionamentoInterno(getUrlAtual());
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return;
        }
        documentoDoFornecedorSelecionado.setLicitacaoFornecedor(fornecedorSelecionado);
        fornecedorSelecionado.setDocumentosFornecedor(Util.adicionarObjetoEmLista(fornecedorSelecionado.getDocumentosFornecedor(), documentoDoFornecedorSelecionado));
        FacesUtil.atualizarComponente("formulario-hab-fornecedor");
        FacesUtil.executaJavaScript("dialogDocumentoFornecedor.hide()");
        adicionarDocumentoAoCadastroDoFornecedor(documentoDoFornecedorSelecionado);
    }

    public void adicionarDocumentoAoCadastroDoFornecedor(LicitacaoDoctoFornecedor doctoFornecedor) {
        if (doctoFornecedor == null) {
            return;
        }
        try {
            Fornecedor fornecedor = fornecedorFacade.buscarFornecedorPorPessoa(fornecedorSelecionado.getEmpresa());
            if (fornecedor != null) {
                fornecedor.getDocumentosFornecedor().add(new DocumentoFornecedor(fornecedor, doctoFornecedor.getDoctoHabilitacao(), doctoFornecedor.getNumeroDaCertidao(), doctoFornecedor.getDataDeEmissao(), doctoFornecedor.getDataDeValidade()));
                fornecedorFacade.salvarMerge(fornecedor);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void confirmarTodosDocumentosDoFornecedor() {
        TipoClassificacaoFornecedor tipo = fornecedorSelecionado.processarClassificacaoDesteFornecedor();
        if (TipoClassificacaoFornecedor.HABILITADO.equals(tipo)) {
            fornecedorSelecionado.setTipoClassificacaoFornecedor(tipo);
            processarHabilitacaoDocumentos();
        } else {
            FacesUtil.executaJavaScript("dialogHabFornecedor.hide()");
            FacesUtil.atualizarComponente("FormularioOperacaoDocumentoForcedor");
            FacesUtil.executaJavaScript("dialogOperacaoDocumentoFornecedor.show()");

        }
    }

    public void habilitarFornecedor() {
        fornecedorSelecionado.setTipoClassificacaoFornecedor(TipoClassificacaoFornecedor.HABILITADO);
        processarHabilitacaoDocumentos();
    }

    public void habilitarParcialmenteFornecedor() {
        fornecedorSelecionado.setTipoClassificacaoFornecedor(TipoClassificacaoFornecedor.HABILITADOCOMRESSALVA);
        processarHabilitacaoDocumentos();
    }

    public void inabilitarFornecedor() {
        fornecedorSelecionado.setTipoClassificacaoFornecedor(TipoClassificacaoFornecedor.INABILITADO);
        processarHabilitacaoDocumentos();
    }

    private void processarHabilitacaoDocumentos() {
        selecionado.setFornecedores(Util.adicionarObjetoEmLista(selecionado.getFornecedores(), fornecedorSelecionado));
        FacesUtil.atualizarComponente("Formulario:painel-fornecedores-vencedores");
        FacesUtil.executaJavaScript("dialogHabFornecedor.hide()");
        atualizarItens();
        habilitacaoPregaoFacade.salvarLicitacaoComItens(selecionado, fornecedorSelecionado);
        notificarUsuarioSobreStatusFornecedor();
    }

    public String getDocumentosNaoApresentados() {
        StringBuilder retorno = new StringBuilder();
        List<String> naoApresentados = Lists.newArrayList();
        List<String> vencidos = Lists.newArrayList();
        if (fornecedorSelecionado != null) {
            for (DoctoHabilitacao doctoLicitacao : selecionado.getListaDeDoctoHabilitacao()) {
                boolean possuiDocumento = false;
                for (LicitacaoDoctoFornecedor documentoFornecedor : fornecedorSelecionado.getDocumentosFornecedor()) {
                    if (doctoLicitacao.equals(documentoFornecedor.getDoctoHabilitacao())) {
                        if (doctoLicitacao.getRequerValidade() && documentoFornecedor.isDocumentoVencido()) {
                            vencidos.add("<li>" + doctoLicitacao.getDescricao());
                        }
                        possuiDocumento = true;
                        break;
                    }
                }

                if (!possuiDocumento) {
                    naoApresentados.add("<li>" + doctoLicitacao.getDescricao());
                }
            }
        }
        if (!naoApresentados.isEmpty()) {
            retorno.append("<h3>A empresa não apresentou o(s) documento(s) citado(s) a baixo. Deseja habilitá-lo mesmo assim?</h3>");
            retorno.append(StringUtils.join(naoApresentados, "</li> "));
        }
        if (!vencidos.isEmpty()) {
            retorno.append("<h3>A empresa apresentou o(s) documento(s) citado(s) a baixo, porém estão <i>vencidos</i>. Deseja habilitá-lo mesmo assim?</h3>");
            retorno.append(StringUtils.join(vencidos, "</li> "));
        }
        return retorno.toString();
    }

    private void notificarUsuarioSobreStatusFornecedor() {
        if (fornecedorSelecionado.isFornecedorInabilitado()) {
            FacesUtil.addError(fornecedorSelecionado.getTipoClassificacaoFornecedor().getDescricao(), fornecedorSelecionado.getMensagemDeJustificativa());
            return;
        }
        if (fornecedorSelecionado.isFornecedorHabilitadoComRessalva()) {
            FacesUtil.addWarn(fornecedorSelecionado.getTipoClassificacaoFornecedor().getDescricao(), fornecedorSelecionado.getMensagemDeJustificativa());
            return;
        }
        if (fornecedorSelecionado.isFornecedorHabilitado()) {
            FacesUtil.addInfo(fornecedorSelecionado.getTipoClassificacaoFornecedor().getDescricao(), "O fornecedor foi habilitado com sucesso.");
            return;
        }
    }

    public void marcarTodosItens() {
        for (ItemPropostaFornecedor ipf : itensVencidosDoFornecedor) {
            marcarItemPropostaFornecedor(ipf);
        }
    }

    public void desmarcarTodosItens() {
        for (ItemPropostaFornecedor ipf : itensVencidosDoFornecedor) {
            desmarcarItemPropostaFornecedor(ipf);
        }
    }

    public void marcarItemPropostaFornecedor(ItemPropostaFornecedor ipf) {
        if (ipf.getItemPregao() != null) {
            ipf.getItemPregao().setStatusFornecedorVencedor(fornecedorSelecionado.getTipoClassificacaoFornecedor());
        }
        ipf.setSelecionado(Boolean.TRUE);
    }

    public void desmarcarItemPropostaFornecedor(ItemPropostaFornecedor ipf) {
        if (ipf.getItemPregao() != null) {
            ipf.getItemPregao().setStatusFornecedorVencedor(TipoClassificacaoFornecedor.INABILITADO);
        }
        ipf.setSelecionado(Boolean.FALSE);
    }

    public boolean botaoHabilitarItensDisabled() {
        if (!fornecedorSelecionado.isFornecedorHabilitadoOuRessalva()) {
            return true;
        }
        return false;
    }

    public void confirmarHabilitacaoDeItens() {
        try {
            salvarHabilitacaoItens();
        } catch (Exception e) {
        }
    }

    public void desabilitarTodosItens() {
        try {
            if (!CollectionUtils.isEmpty(getFornecedorSelecionado().getEmpresa().getItensPropostaFornecedor())) {
                desmarcarTodosItens();
                salvarHabilitacaoItens();
            }
        } catch (Exception ex) {

        }
    }

    private void salvarHabilitacaoItens() {
        fornecedorSelecionado.getEmpresa().setItensPropostaFornecedor(itensVencidosDoFornecedor);
        habilitacaoPregaoFacade.salvarItensDaHabilitacaoDoPregao(fornecedorSelecionado.getEmpresa().getItensPropostaFornecedor());
        itensVencidosDoFornecedor = Lists.newArrayList();
        FacesUtil.executaJavaScript("dialogItensDoFornecedor.hide()");
    }

    public String getCorDaDescricao(String descricao) {
        if ("Habilitado".equalsIgnoreCase(descricao)) {
            return "green";
        }
        if ("Habilitado com Ressalva".equalsIgnoreCase(descricao)) {
            return "goldenrod";
        }
        return "#cd0a0a";
    }

    public List<SelectItem> getTiposAvaliacao() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoAvaliacaoLicitacao tipoAvaliacao : TipoAvaliacaoLicitacao.values()) {
            retorno.add(new SelectItem(tipoAvaliacao, tipoAvaliacao.getDescricao()));
        }
        return retorno;
    }

    public StatusLicitacao getStatusDaLicitacao() {
        return statusDaLicitacao;
    }

    public void setStatusDaLicitacao(StatusLicitacao statusDaLicitacao) {
        this.statusDaLicitacao = statusDaLicitacao;
    }

    public StatusFornecedorLicitacaoFacade getStatusFornecedorLicitacaoFacade() {
        return statusFornecedorLicitacaoFacade;
    }

    public List<ItemPropostaFornecedor> getItensVencidosDoFornecedor() {
        return itensVencidosDoFornecedor;
    }

    public void setItensVencidosDoFornecedor(List<ItemPropostaFornecedor> itensVencidosDoFornecedor) {
        this.itensVencidosDoFornecedor = itensVencidosDoFornecedor;
    }
}
