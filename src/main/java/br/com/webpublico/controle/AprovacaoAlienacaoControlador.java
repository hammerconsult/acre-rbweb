package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOItemSolicitacaoAlienacao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoAlienacao;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AprovacaoAlienacaoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 08/12/14
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaEfetivacaoSolicitacaoAlienacao", pattern = "/aprovacao-solicitacao-de-alienacao/novo/", viewId = "/faces/administrativo/patrimonio/aprovacaosolicitacaoalienacao/edita.xhtml"),
    @URLMapping(id = "editarEfetivacaoSolicitacaoAlienacao", pattern = "/aprovacao-solicitacao-de-alienacao/editar/#{aprovacaoAlienacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/aprovacaosolicitacaoalienacao/edita.xhtml"),
    @URLMapping(id = "listarEfetivacaoSolicitacaoAlienacao", pattern = "/aprovacao-solicitacao-de-alienacao/listar/", viewId = "/faces/administrativo/patrimonio/aprovacaosolicitacaoalienacao/lista.xhtml"),
    @URLMapping(id = "verEfetivacaoSolicitacaoAlienacao", pattern = "/aprovacao-solicitacao-de-alienacao/ver/#{aprovacaoAlienacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/aprovacaosolicitacaoalienacao/visualizar.xhtml")
})
public class AprovacaoAlienacaoControlador extends PrettyControlador<AprovacaoAlienacao> implements Serializable, CRUD {

    @EJB
    private AprovacaoAlienacaoFacade facade;
    private AprovacaoAlienacaoAtoLegal itemAtoLegal;
    private List<VOItemSolicitacaoAlienacao> itensSolicitados;
    private CompletableFuture<List<VOItemSolicitacaoAlienacao>> futureBensSolicitados;
    private CompletableFuture futureSalvar;
    private String motivoRejeicao;
    private LazyDataModel<ItemAprovacaoAlienacao> model;
    private AssistenteMovimentacaoBens assistenteMovimentacao;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    public AprovacaoAlienacaoControlador() {
        super(AprovacaoAlienacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/aprovacao-solicitacao-de-alienacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaEfetivacaoSolicitacaoAlienacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        try {
            iniciarEfetivacaoSolicitacaoAlienacao();
            iniciarAssistenteMovimentacao();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataEfetivacao(), OperacaoMovimentacaoBem.APROVACAO_ALIENACAO_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    @URLAction(mappingId = "verEfetivacaoSolicitacaoAlienacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarComDependencia(getId());
        criarItensPaginacao();
    }

    @URLAction(mappingId = "editarEfetivacaoSolicitacaoAlienacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = facade.recuperarComDependencia(getId());
        redirecionarParaVer();
        FacesUtil.addAtencao("Esta aprovação de alienação foi " + selecionado.getSituacaoEfetivacao().getDescricao() + ", não será permitido realizar alterações.");
    }

    private void criarItensPaginacao() {
        model = new LazyDataModel<ItemAprovacaoAlienacao>() {
            @Override
            public List<ItemAprovacaoAlienacao> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setRowCount(facade.quantidadeItens(selecionado));
                return facade.recuperarItemAprocaoAlienacaoCriteria(first, pageSize, selecionado);
            }
        };
    }

    private void iniciarAssistenteMovimentacao() {
        if (configMovimentacaoBem == null) {
            recuperarConfiguracaoMovimentacaoBem();
        }
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataEfetivacao(), operacao);
        assistenteMovimentacao.zerarContadoresProcesso();
        assistenteMovimentacao.setTotal(itensSolicitados.size());
        assistenteMovimentacao.setSelecionado(selecionado);
        assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
        assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistenteMovimentacao.setDataAtual(facade.getSistemaFacade().getDataOperacao());
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataEfetivacao(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public List<SelectItem> situacoesParaEfetivacao() {
        return Util.getListSelectItem(SolicitacaoAlienacao.situacoesParaEfetivacao());
    }

    private void iniciarEfetivacaoSolicitacaoAlienacao() {
        selecionado.setDataEfetivacao(getDataOperacao());
        selecionado.setResponsavel(facade.getBemFacade().getSistemaFacade().getUsuarioCorrente());
        this.itemAtoLegal = new AprovacaoAlienacaoAtoLegal();
        itensSolicitados = Lists.newArrayList();
    }

    public List<SolicitacaoAlienacao> completaSolicitacaoAlienacaoParaEfetivacao(String parte) {
        return facade.getSolicitacaoAlienacaoFacade().buscarSolicitacoesAlienacaoPorSituacao(parte, SituacaoAlienacao.AGUARDANDO_APROVACAO);
    }

    public AprovacaoAlienacaoAtoLegal getItemAtoLegal() {
        return itemAtoLegal;
    }

    public void setItemAtoLegal(AprovacaoAlienacaoAtoLegal itemAtoLegal) {
        this.itemAtoLegal = itemAtoLegal;
    }

    private void validarAprovacao() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        if (itensSolicitados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A consulta não retornou bem para ser aprovado.");
        }

        if (selecionado.getRejeitada() && Strings.isNullOrEmpty(motivoRejeicao)) {
            ve.adicionarMensagemDeCampoObrigatorio("O motivo da rejeição deve ser informado.");
        }
        if (selecionado.getAprovada() && selecionado.getItensAtoLegal().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione ao menos um Ato Legal.");
        }
        ve.lancarException();
    }

    public String getFonteRecurso(Bem bem) {
        return facade.getSolicitacaoAlienacaoFacade().recuperarFonteRecurso(bem.getId());
    }

    public void buscarBensDaSolicitacaoAlienacao() {
        try {
            iniciarAssistenteMovimentacao();
            validarDataLancamentoAndDataOperacaoBem();
            itensSolicitados = Lists.newArrayList();
            if (selecionado.getSolicitacaoAlienacao() != null) {
                SolicitacaoAlienacao solicitacaoAlienacao = facade.getSolicitacaoAlienacaoFacade().recuperarComDependenciaArquivoComposicao(selecionado.getSolicitacaoAlienacao().getId());
                assistenteMovimentacao.setDescricaoProcesso("Buscando bens para aprovação.");
                futureBensSolicitados =  AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                    () -> facade.pesquisarBensParaAprovacao(solicitacaoAlienacao, assistenteMovimentacao));
                FacesUtil.executaJavaScript("iniciarPesquisa()");
            }
        } catch (ValidacaoException ve) {
            selecionado.setSolicitacaoAlienacao(null);
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void acompanharPesquisa() {
        if (futureBensSolicitados != null && futureBensSolicitados.isDone()) {
            if (!assistenteMovimentacao.getMensagens().isEmpty()) {
                futureBensSolicitados = null;
                ValidacaoException ve = new ValidacaoException();
                for (String msg : assistenteMovimentacao.getMensagens()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(msg);
                }
                FacesUtil.executaJavaScript("closeDialog(dlgPesquisa)");
                FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            }
            FacesUtil.executaJavaScript("terminarPesquisa()");
        }
    }

    public void finalizarPesquisa() throws ExecutionException, InterruptedException {
        if (futureBensSolicitados != null && assistenteMovimentacao.getMensagens().isEmpty()) {
            itensSolicitados.addAll(futureBensSolicitados.get());
        }
    }

    @Override
    public void salvar() {
        try {
            validarAprovacao();
            iniciarAssistenteMovimentacao();
            validarDataLancamentoAndDataOperacaoBem();
            bloquearMovimento();
            selecionado.getSolicitacaoAlienacao().setMotivoRejeicao(motivoRejeicao);
            assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
            assistenteMovimentacao.setDataAtual(facade.getSistemaFacade().getDataOperacao());
            assistenteMovimentacao.setExecutando(true);
            assistenteMovimentacao.setDescricaoProcesso("Salvando a aprovação de alienação de bens móveis...");
            futureSalvar = AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                () -> facade.salvarAprovacao(selecionado, assistenteMovimentacao, itensSolicitados));
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (MovimentacaoBemException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            redireciona();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    private void bloquearMovimento() {
        facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(AprovacaoAlienacao.class, selecionado.getSolicitacaoAlienacao().getUnidadeAdministrativa(), assistenteMovimentacao);
    }

    private void desbloquearMovimentoSingleton() {
        if (selecionado.getSolicitacaoAlienacao() != null) {
            facade.getSingletonBloqueioPatrimonio().desbloquearMovimentoPorUnidade(AprovacaoAlienacao.class, selecionado.getSolicitacaoAlienacao().getUnidadeAdministrativa());
        }
    }

    public void acompanharAndamentoSalvar() {
        if (futureSalvar != null && futureSalvar.isDone()) {
            try {
                AprovacaoAlienacao aprovacaoSalva = (AprovacaoAlienacao) futureSalvar.get();
                if (aprovacaoSalva != null) {
                    selecionado = aprovacaoSalva;
                    desbloquearMovimentoSingleton();
                    FacesUtil.executaJavaScript("finalizarSalvar()");
                }
            } catch (Exception ex) {
                assistenteMovimentacao.setBloquearAcoesTela(true);
                FacesUtil.executaJavaScript("clearInterval(timerSalvar)");
                FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                FacesUtil.executaJavaScript("aguarde.hide()");
                FacesUtil.atualizarComponente("Formulario");
                futureSalvar = null;
                desbloquearMovimentoSingleton();
                assistenteMovimentacao.descobrirETratarException(ex);
            }
        }
    }

    public void finalizarSalvar() {
        redirecionarParaVer();
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        futureSalvar = null;
    }

    public Boolean getRenderizarMotivodaRejeicao() {
        return selecionado.getSituacaoEfetivacao() != null && selecionado.getRejeitada();
    }

    public void adicionarAtoLegal() {
        try {
            validarAtolegal();
            itemAtoLegal.setAprovacaoAlienacao(selecionado);
            selecionado.getItensAtoLegal().add(itemAtoLegal);
            itemAtoLegal = new AprovacaoAlienacaoAtoLegal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAtolegal() {
        ValidacaoException ve = new ValidacaoException();
        itemAtoLegal.realizarValidacoes();
        for (AprovacaoAlienacaoAtoLegal item : selecionado.getItensAtoLegal()) {
            if (item.getAtoLegal().equals(itemAtoLegal.getAtoLegal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Ato Legal já adicionado.");
                break;
            }
        }
        ve.lancarException();
    }

    public void removerAtoLegal(AprovacaoAlienacaoAtoLegal atoLegal) {
        selecionado.getItensAtoLegal().remove(atoLegal);
    }

    public List<VOItemSolicitacaoAlienacao> getItensSolicitados() {
        return itensSolicitados;
    }

    public void setItensSolicitados(List<VOItemSolicitacaoAlienacao> itensSolicitados) {
        this.itensSolicitados = itensSolicitados;
    }

    public BigDecimal getValorTotalOriginal() {
        BigDecimal total = BigDecimal.ZERO;
        if (itensSolicitados != null) {
            for (VOItemSolicitacaoAlienacao bem : itensSolicitados) {
                total = total.add(bem.getValorOriginal());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalAjuste() {
        BigDecimal total = BigDecimal.ZERO;
        if (itensSolicitados != null) {
            for (VOItemSolicitacaoAlienacao bem : itensSolicitados) {
                total = total.add(bem.getValorAjuste());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalLiquido() {
        BigDecimal total = BigDecimal.ZERO;
        if (itensSolicitados != null) {
            total = total.add(getValorTotalOriginal().subtract(getValorTotalAjuste()));
        }
        return total;
    }

    public void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void finalizarProcesssoSalvar() {
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        redirecionarParaVer();
    }

    public BigDecimal getValorTotalOriginalCriteria() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            total = facade.getValorOriginalEstadoResultante(selecionado);
        }
        return total;
    }

    public BigDecimal getValorTotalAjusteCriteria() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            total = facade.getValorAjusteEstadoResultante(selecionado);
        }
        return total;
    }

    public BigDecimal gettValorTotalLiquidoCriteria() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            total = total.add(getValorTotalOriginalCriteria().subtract(getValorTotalAjusteCriteria()));
        }
        return total;
    }

    public String formatarValor(BigDecimal valor) {
        return Util.formataValor(valor);
    }

    public void selecionarAtoLegal(ActionEvent evento) {
        itemAtoLegal.setAtoLegal((AtoLegal) evento.getComponent().getAttributes().get("objeto"));
        adicionarAtoLegal();
    }

    public LazyDataModel<ItemAprovacaoAlienacao> getModel() {
        return model;
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteBarraProgresso) {
        this.assistenteMovimentacao = assistenteBarraProgresso;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }
}
