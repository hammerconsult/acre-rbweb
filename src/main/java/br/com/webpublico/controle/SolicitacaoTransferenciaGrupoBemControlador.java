package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SolicitacaoTransferenciaGrupoBem;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SolicitacaoTransferenciaGrupoBemFacade;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-sol-transf-grupo-bem", pattern = "/solicitacao-transferencia-grupo-bem-movel/novo/", viewId = "/faces/administrativo/patrimonio/solicitacao-transf-grupo-bem/edita.xhtml"),
    @URLMapping(id = "editar-sol-transf-grupo-bem", pattern = "/solicitacao-transferencia-grupo-bem-movel/editar/#{solicitacaoTransferenciaGrupoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacao-transf-grupo-bem/edita.xhtml"),
    @URLMapping(id = "listar-sol-transf-grupo-bem", pattern = "/solicitacao-transferencia-grupo-bem-movel/listar/", viewId = "/faces/administrativo/patrimonio/solicitacao-transf-grupo-bem/lista.xhtml"),
    @URLMapping(id = "ver-sol-transf-grupo-bem", pattern = "/solicitacao-transferencia-grupo-bem-movel/ver/#{solicitacaoTransferenciaGrupoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacao-transf-grupo-bem/visualizar.xhtml"),
})
public class SolicitacaoTransferenciaGrupoBemControlador extends PrettyControlador<SolicitacaoTransferenciaGrupoBem> implements Serializable, CRUD {

    @EJB
    private SolicitacaoTransferenciaGrupoBemFacade facade;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private FiltroPesquisaBem filtroPesquisaBem;
    private AssistenteMovimentacaoBens assistenteMovimentacao;

    public SolicitacaoTransferenciaGrupoBemControlador() {
        super(SolicitacaoTransferenciaGrupoBem.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-transferencia-grupo-bem-movel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "nova-sol-transf-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        try {
            super.novo();
            selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setDataSolicitacao(getDataOperacao());
            selecionado.setSituacao(SituacaoMovimentoAdministrativo.EM_ELABORACAO);
            novoFiltroPesquisaBem();
            novoAssistente();
            inicializarListas();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "ver-sol-transf-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarComDependenciasArquivo(getId());
        recuperarHierarquiaDaUnidade();
    }

    @URLAction(mappingId = "editar-sol-transf-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        selecionado = facade.recuperarComDependenciasArquivo(getId());
        operacao = Operacoes.EDITAR;
        if (!selecionado.isEmElaboracao()) {
            redirecionarParaVer();
            FacesUtil.addOperacaoNaoPermitida("A solicitação encontrasse-se " + selecionado.getSituacao().getDescricao() + " não pode ser alterada.");
        }
        recuperarHierarquiaDaUnidade();
        novoFiltroPesquisaBem();
        novoAssistente();
    }

    @Override
    public void salvar() {
        try {
            validarUnidadeAdministrativa();
            selecionado.realizarValidacoes();
            validarRegrasGerais();
            validarDataLancamentoAndDataOperacaoBem();
            bloquearMovimentacaoBens();
            CompletableFuture<AssistenteMovimentacaoBens> completableFuture =
                AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                    () -> facade.salvarSolicitacao(assistenteMovimentacao));
            assistenteMovimentacao.setCompletableFuture(completableFuture);
            FacesUtil.executaJavaScript("iniciaFuture()");
        } catch (MovimentacaoBemException ex) {
            blocoCatchMovimentacaoBemException(ex);
        } catch (ValidacaoException ex) {
            blocoCatchValidacaoException(ex);
        } catch (ExcecaoNegocioGenerica ex) {
            blocoCatchExcecaoNegocioGenerica(ex);
        } catch (Exception e) {
            blocoCatchException(e);
        }
    }

    public void concluir() {
        try {
            validarDataLancamentoAndDataOperacaoBem();
            bloquearMovimentacaoBens();
            CompletableFuture<AssistenteMovimentacaoBens> completableFuture = AsyncExecutor.getInstance()
                .execute(assistenteMovimentacao,
                    () -> facade.concluirSolicitacao(assistenteMovimentacao));
            assistenteMovimentacao.setCompletableFuture(completableFuture);
            FacesUtil.executaJavaScript("iniciaFuture()");
        } catch (MovimentacaoBemException ex) {
            blocoCatchMovimentacaoBemException(ex);
        } catch (ValidacaoException ex) {
            blocoCatchValidacaoException(ex);
        } catch (ExcecaoNegocioGenerica ex) {
            blocoCatchExcecaoNegocioGenerica(ex);
        } catch (Exception e) {
            blocoCatchException(e);
            descobrirETratarException(e);
        }
    }

    @Override
    public void excluir() {
        try {
            facade.remover(selecionado, assistenteMovimentacao);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            redireciona();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void consultarCompletableFuture() {
        if (assistenteMovimentacao.getCompletableFuture() != null && assistenteMovimentacao.getCompletableFuture().isDone()) {
            try {
                AssistenteMovimentacaoBens assistente = assistenteMovimentacao.getCompletableFuture().get();
                if (assistente.getSelecionado() != null) {
                    selecionado = (SolicitacaoTransferenciaGrupoBem) assistente.getSelecionado();
                    desbloquearMovimentacaoSingleton();
                    FacesUtil.executaJavaScript("finalizaFuture()");
                }
            } catch (Exception ex) {
                assistenteMovimentacao.descobrirETratarException(ex);
                assistenteMovimentacao.setBloquearAcoesTela(true);
                FacesUtil.executaJavaScript("clearInterval(timerSalvar)");
                FacesUtil.executaJavaScript("closeDialog(dlgProcesso)");
                assistenteMovimentacao.setCompletableFuture(null);
                desbloquearMovimentacaoSingleton();
            }
        }
    }

    public void finalizarCompletableFuture() {
        assistenteMovimentacao.setCompletableFuture(null);
        redirecionarParaVer();
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
    }

    private void blocoCatchException(Exception e) {
        desbloquearMovimentacaoSingleton();
        FacesUtil.executaJavaScript("aguarde.hide()");
        assistenteMovimentacao.descobrirETratarException(e);
    }

    private void blocoCatchValidacaoException(ValidacaoException ex) {
        desbloquearMovimentacaoSingleton();
        FacesUtil.executaJavaScript("aguarde.hide()");
        FacesUtil.printAllFacesMessages(ex.getMensagens());
    }

    private void blocoCatchMovimentacaoBemException(MovimentacaoBemException ex) {
        if (!isOperacaoNovo()) {
            redireciona();
        }
        FacesUtil.printAllFacesMessages(ex.getMensagens());
    }

    private void blocoCatchExcecaoNegocioGenerica(ExcecaoNegocioGenerica ex) {
        desbloquearMovimentacaoSingleton();
        FacesUtil.executaJavaScript("aguarde.hide()");
        FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
    }

    public void pesquisarBensPorTipoEvento() {
        try {
            novoFiltroPesquisaBem();
            novoAssistente();
            assistenteMovimentacao.setFuturePesquisaBemVo(facade.getPesquisaBemFacade().pesquisarBensPorTipoEvento(filtroPesquisaBem, assistenteMovimentacao));
            FacesUtil.executaJavaScript("iniciarPesquisa()");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void pesquisarBens() {
        try {
            Util.validarCampos(selecionado);
            validarDataLancamentoAndDataOperacaoBem();
            if (verificarBensBloqueadoSingletonAoPesquisar()) return;
            inicializarListas();
            assistenteMovimentacao.setFuturePesquisaBemVo(facade.getPesquisaBemFacade().pesquisarBens(filtroPesquisaBem, assistenteMovimentacao));
            FacesUtil.executaJavaScript("iniciarPesquisa()");
        } catch (MovimentacaoBemException me) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(me.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void acompanharPesquisa() throws ExecutionException, InterruptedException {
        if (assistenteMovimentacao.getFuturePesquisaBemVo() != null && assistenteMovimentacao.getFuturePesquisaBemVo().isDone()) {
            List<BemVo> bens = assistenteMovimentacao.getFuturePesquisaBemVo().get();
            assistenteMovimentacao.setBensDisponiveisVo(bens);
            if (isOperacaoVer()) {
                assistenteMovimentacao.setBensMovimentadosVo(bens);
            }
            assistenteMovimentacao.setFuturePesquisaBemVo(null);
            FacesUtil.executaJavaScript("finalizarPesquisa()");
        }
    }

    public void finalizarPesquisa() {
        if (isOperacaoEditar()) {
            popularListaBensSelecionados();
        }
        FacesUtil.atualizarComponente("Formulario:tab-view-principal:tabelaBens");
        FacesUtil.atualizarComponente("Formulario:tab-view-principal:inconsistencias");
        FacesUtil.atualizarComponente("Formulario:panel-msg-incons");
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataSolicitacao(), OperacaoMovimentacaoBem.SOLICITACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL);
        if (configMovimentacaoBem != null) {
            assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
        }
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        if (assistenteMovimentacao.getConfigMovimentacaoBem() != null) {
            assistenteMovimentacao.getConfigMovimentacaoBem().validarDatasMovimentacao(selecionado.getDataSolicitacao(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void inicializarListas() {
        assistenteMovimentacao.setBensSelecionadosVo(new ArrayList<BemVo>());
        assistenteMovimentacao.setBensDisponiveisVo(new ArrayList<BemVo>());
    }

    private void novoFiltroPesquisaBem() {
        filtroPesquisaBem = new FiltroPesquisaBem(TipoBem.MOVEIS, selecionado);
        filtroPesquisaBem.setDataOperacao(selecionado.getDataSolicitacao());
        filtroPesquisaBem.setDataReferencia(selecionado.getDataSolicitacao());
        filtroPesquisaBem.setTipoEventoBem(TipoEventoBem.SOLICITACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL);
        filtroPesquisaBem.setIdSelecionado(selecionado.getId());
        filtroPesquisaBem.setOperacao(operacao);
        if (selecionado.getGrupoBemOrigem() != null) {
            filtroPesquisaBem.setGrupoBem(selecionado.getGrupoBemOrigem());
        }
        if (hierarquiaAdministrativa != null) {
            filtroPesquisaBem.setHierarquiaAdministrativa(hierarquiaAdministrativa);
        }
    }

    private void popularListaBensSelecionados() {
        if (selecionado.getUnidadeOrganizacional().equals(hierarquiaAdministrativa.getSubordinada())) {
            assistenteMovimentacao.setBensSelecionadosVo(new ArrayList<BemVo>());
            if (assistenteMovimentacao.hasBensMovimentadosVo()) {
                assistenteMovimentacao.getBensDisponiveisVo().addAll(assistenteMovimentacao.getBensMovimentadosVo());
                assistenteMovimentacao.getBensSelecionadosVo().addAll(assistenteMovimentacao.getBensMovimentadosVo());
            }
        }
        Util.ordenarListas(assistenteMovimentacao.getBensDisponiveisVo());
    }


    public void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    private boolean verificarBensBloqueadoSingletonAoPesquisar() {
        if (facade.getSingletonBloqueioPatrimonio().verificarBensBloqueadoSingletonAoPesquisar(assistenteMovimentacao)) {
            if (isOperacaoNovo()) {
                FacesUtil.executaJavaScript("aguarde.hide()");
                FacesUtil.atualizarComponente("Formulario");
                assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
            } else {
                FacesUtil.addOperacaoNaoPermitida(assistenteMovimentacao.getMensagens().get(0));
                redireciona();
            }
        }
        return false;
    }

    private void bloquearMovimentacaoBens() {
        if (!isOperacaoNovo()) {
            facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(SolicitacaoTransferenciaGrupoBem.class, selecionado.getUnidadeOrganizacional(), assistenteMovimentacao);
        }
        if (facade.getSingletonBloqueioPatrimonio().verificarBensBloqueadoSingletonAoPesquisar(assistenteMovimentacao)) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("Formulario");
            assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
        }
        if (isOperacaoNovo() && !assistenteMovimentacao.hasInconsistencias()) {
            facade.getConfigMovimentacaoBemFacade().verificarBensSelecionadosSeDisponivelConfiguracaoBloqueio(assistenteMovimentacao);
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("Formulario");
            assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
        }
        if (!isOperacaoVer()) {
            for (BemVo bemVo : assistenteMovimentacao.getBensSelecionadosVo()) {
                facade.getSingletonBloqueioPatrimonio().bloquearBem(bemVo.getBem(), assistenteMovimentacao);
            }
        }
    }

    private void desbloquearMovimentacaoSingleton() {
        facade.getSingletonBloqueioPatrimonio().desbloquearMovimentacaoSingleton(assistenteMovimentacao, SolicitacaoTransferenciaGrupoBem.class);
    }

    public boolean isBensSelecionados() {
        return assistenteMovimentacao != null && assistenteMovimentacao.hasBensSelecionadoVo();
    }

    public void atribuirFiltroPesquisaBem() {
        try {
            if (assistenteMovimentacao.getConfigMovimentacaoBem() == null) {
                novoAssistente();
            }
            if (hierarquiaAdministrativa != null) {
                filtroPesquisaBem.setHierarquiaAdministrativa(hierarquiaAdministrativa);
            }
            if (selecionado.getGrupoBemOrigem() != null) {
                filtroPesquisaBem.setGrupoBem(selecionado.getGrupoBemOrigem());
            }
            inicializarListas();
            desbloquearMovimentacaoSingleton();
            assistenteMovimentacao.setMensagens(new ArrayList<String>());
        } catch (ExcecaoNegocioGenerica e) {
            setHierarquiaAdministrativa(null);
            selecionado.setGrupoBemOrigem(null);
            FacesUtil.atualizarComponente("Formulario:tab-view-principal:op-dados-gerais");
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public List<GrupoBem> completarGrupoBem(String parte) {
        return facade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(parte.trim(), TipoBem.MOVEIS);
    }

    private void novoAssistente() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataSolicitacao(), operacao);
        assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistenteMovimentacao.setSelecionado(selecionado);
        if (hierarquiaAdministrativa != null) {
            assistenteMovimentacao.setUnidadeOrganizacional(hierarquiaAdministrativa.getSubordinada());
        }
        assistenteMovimentacao.setTipoEventoBem(TipoEventoBem.SOLICITACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL);
        recuperarConfiguracaoMovimentacaoBem();
    }

    private void validarUnidadeAdministrativa() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaAdministrativa == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa deve ser informado.");
        }
        ve.lancarException();
    }

    public boolean isGestorPatrimonio() {
        return facade.getUsuarioSistemaFacade().isGestorPatrimonio(facade.getSistemaFacade().getUsuarioCorrente(), facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
    }


    private void recuperarHierarquiaDaUnidade() {
        if (selecionado.getUnidadeOrganizacional() != null) {
            setHierarquiaAdministrativa(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                selecionado.getUnidadeOrganizacional(),
                selecionado.getDataSolicitacao()));
        }
    }

    private void validarRegrasGerais() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getGrupoBemOrigem().equals(selecionado.getGrupoBemDestino())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A transferência deve acontecer entre grupos distintos.");
        }
        if (!assistenteMovimentacao.hasBensSelecionadoVo()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum bem foi selecionado para a solicitação de transferência.");
        }
        ve.lancarException();
    }

    public boolean canEditar() {
        return selecionado.getResponsavel() != null
            && selecionado.getResponsavel().equals(facade.getSistemaFacade().getUsuarioCorrente())
            && selecionado.isEmElaboracao();
    }

    public boolean canExcluir() {
        return selecionado.getResponsavel() != null
            && selecionado.getResponsavel().equals(facade.getSistemaFacade().getUsuarioCorrente())
            && (selecionado.isEmElaboracao() || selecionado.getSituacao().isAguardandoEfetivacao());
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

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteBarraProgresso) {
        this.assistenteMovimentacao = assistenteBarraProgresso;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        if (hierarquiaAdministrativa != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaAdministrativa.getSubordinada());
        }
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }
}
