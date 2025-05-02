package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoMovimentoAdministrativo;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EfetivacaoTransferenciaGrupoBemFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-efet-grupo-bem", pattern = "/efetivacao-transferencia-grupo-bem-movel/novo/", viewId = "/faces/administrativo/patrimonio/efetivacao-transf-grupo-bem/edita.xhtml"),
    @URLMapping(id = "listar-efet-grupo-bem", pattern = "/efetivacao-transferencia-grupo-bem-movel/listar/", viewId = "/faces/administrativo/patrimonio/efetivacao-transf-grupo-bem/lista.xhtml"),
    @URLMapping(id = "ver-efet-grupo-bem", pattern = "/efetivacao-transferencia-grupo-bem-movel/ver/#{efetivacaoTransferenciaGrupoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacao-transf-grupo-bem/visualizar.xhtml"),
})
public class EfetivacaoTransferenciaGrupoBemControlador extends PrettyControlador<EfetivacaoTransferenciaGrupoBem> implements Serializable, CRUD {

    @EJB
    private EfetivacaoTransferenciaGrupoBemFacade facade;
    private SituacaoMovimentoAdministrativo situacao;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private FiltroPesquisaBem filtroPesquisaBem;
    private BemVo bemVo;
    private AssistenteMovimentacaoBens assistenteMovimentacao;

    public EfetivacaoTransferenciaGrupoBemControlador() {
        super(EfetivacaoTransferenciaGrupoBem.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/efetivacao-transferencia-grupo-bem-movel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-efet-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        try {
            super.novo();
            selecionado.setDataEfetivacao(getDataOperacao());
            selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente());
            novoAssistenteMovimentacao();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "ver-efet-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarComDependenciasAqruivo(getId());
        recuperarHierarquiaDaUnidade();
        novoAssistenteMovimentacao();
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataEfetivacao(), OperacaoMovimentacaoBem.EFETIVACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL);
        if (configMovimentacaoBem != null) {
            assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
        }
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        if (assistenteMovimentacao.getConfigMovimentacaoBem() != null) {
            assistenteMovimentacao.getConfigMovimentacaoBem().validarDatasMovimentacao(selecionado.getDataEfetivacao(), getDataOperacao(), operacao);
        }
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(SituacaoMovimentoAdministrativo.FINALIZADO, "Aceitar"));
        toReturn.add(new SelectItem(SituacaoMovimentoAdministrativo.RECUSADO, "Recusar"));
        return toReturn;
    }

    public boolean isRecusada() {
        return situacao != null && situacao.isRecusado();
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void novoFiltroPesquisaBem() {
        filtroPesquisaBem = new FiltroPesquisaBem(selecionado);
        filtroPesquisaBem.setDataOperacao(getDataOperacao());
        filtroPesquisaBem.setDataReferencia(getDataOperacao());
        filtroPesquisaBem.setOperacao(operacao);
        boolean isSolicitacaoRecusada = hasSolicitacaoSelecionada() && selecionado.getSolicitacao().isRecusada();
        filtroPesquisaBem.setIdSelecionado((isOperacaoNovo() || isSolicitacaoRecusada) ? selecionado.getSolicitacao().getId() : selecionado.getId());
        filtroPesquisaBem.setTipoEventoBem((isOperacaoNovo() || isSolicitacaoRecusada) ? TipoEventoBem.SOLICITACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL : TipoEventoBem.TRANSFERENCIA_GRUPO_PATRIMONIAL_ORIGINAL_RECEBIDA);
    }

    public void acompanharFutureSalvar() {
        if (assistenteMovimentacao.getCompletableFuture() != null && assistenteMovimentacao.getCompletableFuture().isDone()) {
            try {
                AssistenteMovimentacaoBens assistente = assistenteMovimentacao.getCompletableFuture().get();
                if (assistente.getSelecionado() != null) {
                    selecionado = (EfetivacaoTransferenciaGrupoBem) assistente.getSelecionado();
                    desbloquearMovimentacaoSingleton();
                    FacesUtil.executaJavaScript("finalizaFuture()");
                }
            } catch (Exception ex) {
                assistenteMovimentacao.descobrirETratarException(ex);
                assistenteMovimentacao.setBloquearAcoesTela(true);
                fecharAguarde();
                FacesUtil.executaJavaScript("clearInterval(timerSalvar)");
                FacesUtil.executaJavaScript("closeDialog(dlgProcesso)");
                assistenteMovimentacao.setCompletableFuture(null);
                desbloquearMovimentacaoSingleton();
                assistenteMovimentacao.setMensagens(Lists.newArrayList(ex.getMessage()));
                FacesUtil.atualizarComponente("Formulario");
            }
        }
    }

    public void atribuirNullDadosSolicitacao() {
        if (hasSolicitacaoSelecionada()) {
            selecionado.getSolicitacao().setSituacao(SituacaoMovimentoAdministrativo.AGUARDANDO_EFETIVACAO);
            selecionado.getSolicitacao().setMotivoRecusa(null);
            selecionado.setSolicitacao(null);
            situacao = null;
            assistenteMovimentacao.setBensMovimentadosVo(new ArrayList<BemVo>());
        }
    }

    public void atribuirNullMotivoRejeicao() {
        if (hasSolicitacaoSelecionada()) {
            selecionado.getSolicitacao().setSituacao(situacao);
            selecionado.getSolicitacao().setMotivoRecusa(null);
        }
    }

    public void finalizarFutureSalvar() {
        assistenteMovimentacao.setCompletableFuture(null);
        redirecionarParaVer();
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
    }

    public void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void acompanharPesquisa() throws ExecutionException, InterruptedException {
        if (assistenteMovimentacao.getFuturePesquisaBemVo() != null && assistenteMovimentacao.getFuturePesquisaBemVo().isDone()) {
            assistenteMovimentacao.setBensMovimentadosVo(assistenteMovimentacao.getFuturePesquisaBemVo().get());
            assistenteMovimentacao.setFuturePesquisaBemVo(null);
            FacesUtil.executaJavaScript("finalizarPesquisa()");
        }
    }

    public void finalizarPesquisa() {
        FacesUtil.atualizarComponente("Formulario:tab-view:grid-geral");
        FacesUtil.atualizarComponente("Formulario:tab-view:grid-solicitacao");
        FacesUtil.atualizarComponente("Formulario:tab-view:tabelaBens");
        FacesUtil.atualizarComponente("Formulario:tab-view:inconsistencias");
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            validarRegrasGerais();
            validarDataLancamentoAndDataOperacaoBem();
            bloquearMovimentacaoBens();
            assistenteMovimentacao.setSituacaoMovimento(situacao);
            CompletableFuture<AssistenteMovimentacaoBens> completableFuture =
                AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                    () -> facade.salvarEfetivacao(assistenteMovimentacao));
            assistenteMovimentacao.setCompletableFuture(completableFuture);
            FacesUtil.executaJavaScript("iniciaFuture()");
        } catch (MovimentacaoBemException me) {
            assistenteMovimentacao.setBloquearAcoesTela(true);
            fecharAguarde();
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.printAllFacesMessages(me.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentacaoSingleton();
            fecharAguarde();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentacaoSingleton();
            fecharAguarde();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentacaoSingleton();
            fecharAguarde();
            descobrirETratarException(e);
        }
    }

    private void fecharAguarde() {
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    public void pesquisarBens() {
        try {
            assistenteMovimentacao.setUnidadeOrganizacional(selecionado.getSolicitacao().getUnidadeOrganizacional());
            recuperarHierarquiaDaUnidade();
            validarDataLancamentoAndDataOperacaoBem();
            novoFiltroPesquisaBem();
            assistenteMovimentacao.setFuturePesquisaBemVo(facade.getPesquisaBemFacade().pesquisarBensPorTipoEvento(filtroPesquisaBem, assistenteMovimentacao));
            FacesUtil.executaJavaScript("iniciarPesquisa()");
        } catch (ValidacaoException ve) {
            fecharAguarde();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            fecharAguarde();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public List<ObjetoCompra> completarObjetoCompra(String filtro) {
        return facade.getObjetoCompraFacade().completaObjetoCompra(filtro);
    }

    public void iniciarSubstituicaoObjetoCompra(BemVo bemVo) {
        this.bemVo = bemVo;
        FacesUtil.executaJavaScript("dlgEditarBem.show()");
    }

    public void desfazerSubstituicaoObjetoCompra(BemVo bemVo) {
        bemVo.setObjetoCompra(null);
    }

    public void confirmarSubstituicaoObjetoCompra() {
        cancelarSubstituicaoObjetoCompra();
    }

    public void cancelarSubstituicaoObjetoCompra() {
        setBemVo(null);
        FacesUtil.executaJavaScript("dlgEditarBem.hide()");
        FacesUtil.atualizarComponente("Formulario:tab-view:tabelaBens");
    }

    public String getAceitaRecusada() {
        return selecionado.getSolicitacao().getSituacao().isFinalizado() ? "Aceita" : "Recusda";
    }

    public boolean hasSolicitacaoSelecionada() {
        return selecionado.getSolicitacao() != null;
    }

    private void bloquearMovimentacaoBens() {
        facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(SolicitacaoTransferenciaGrupoBem.class, selecionado.getSolicitacao().getUnidadeOrganizacional(), assistenteMovimentacao);
    }

    private void desbloquearMovimentacaoSingleton() {
        facade.getSingletonBloqueioPatrimonio().desbloquearMovimentacaoSingleton(assistenteMovimentacao, SolicitacaoTransferenciaGrupoBem.class);
    }

    private void novoAssistenteMovimentacao() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataEfetivacao(), operacao);
        assistenteMovimentacao.setSelecionado(selecionado);
        recuperarConfiguracaoMovimentacaoBem();
    }

    public List<SolicitacaoTransferenciaGrupoBem> completarSolicitacao(String parte) {
        return facade.getSolicitacaoTransferenciaGrupoBemFacade().buscarSolicitacoesPorSituacao(parte, SituacaoMovimentoAdministrativo.AGUARDANDO_EFETIVACAO);
    }

    private void recuperarHierarquiaDaUnidade() {
        if (selecionado.getSolicitacao() != null && selecionado.getSolicitacao().getUnidadeOrganizacional() != null) {
            HierarquiaOrganizacional hierarquiaAdm = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                selecionado.getSolicitacao().getUnidadeOrganizacional(),
                selecionado.getDataEfetivacao());
            setHierarquiaAdministrativa(hierarquiaAdm);
        }
    }

    private void validarRegrasGerais() {
        ValidacaoException ve = new ValidacaoException();
        if (!assistenteMovimentacao.hasBensMovimentadosVo()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum bem recuperado para efetivar a solicitação de transferência grupo bem.");
        }
        if (situacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Aceitar/Recusar solicitação deve ser informado.");

        } else if (isRecusada() && Strings.isNullOrEmpty(selecionado.getSolicitacao().getMotivoRecusa())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo da Recusa deve ser informado.");
        }
        ve.lancarException();
    }

    public SituacaoMovimentoAdministrativo getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMovimentoAdministrativo situacao) {
        this.situacao = situacao;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public FiltroPesquisaBem getFiltroPesquisaBem() {
        return filtroPesquisaBem;
    }

    public void setFiltroPesquisaBem(FiltroPesquisaBem filtroPesquisaBem) {
        this.filtroPesquisaBem = filtroPesquisaBem;
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteMovimentacao) {
        this.assistenteMovimentacao = assistenteMovimentacao;
    }

    public BemVo getBemVo() {
        return bemVo;
    }

    public void setBemVo(BemVo bemVo) {
        this.bemVo = bemVo;
    }
}
