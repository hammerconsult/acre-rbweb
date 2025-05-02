package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SolicitacaoAlteracaoConservacaoBem;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SolicitacaoAlteracaoConservacaoBemFacade;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@ManagedBean(name = "solicitacaoAlteracaoConservacaoBemControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-sol-alteracao-conserv", pattern = "/solicitacao-alteracao-conservacao-bens-moveis/novo/", viewId = "/faces/administrativo/patrimonio/solicitacao-alteracao-conservacao/edita.xhtml"),
    @URLMapping(id = "editar-sol-alteracao-conserv", pattern = "/solicitacao-alteracao-conservacao-bens-moveis/editar/#{solicitacaoAlteracaoConservacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacao-alteracao-conservacao/edita.xhtml"),
    @URLMapping(id = "listar-sol-alteracao-conserv", pattern = "/solicitacao-alteracao-conservacao-bens-moveis/listar/", viewId = "/faces/administrativo/patrimonio/solicitacao-alteracao-conservacao/lista.xhtml"),
    @URLMapping(id = "ver-sol-alteracao-conserv", pattern = "/solicitacao-alteracao-conservacao-bens-moveis/ver/#{solicitacaoAlteracaoConservacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacao-alteracao-conservacao/visualizar.xhtml"),
})
public class SolicitacaoAlteracaoConservacaoBemControlador extends PrettyControlador<SolicitacaoAlteracaoConservacaoBem> implements Serializable, CRUD {

    @EJB
    private SolicitacaoAlteracaoConservacaoBemFacade facade;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private FiltroPesquisaBem filtroPesquisaBem;
    private AssistenteMovimentacaoBens assistenteMovimentacao;

    public SolicitacaoAlteracaoConservacaoBemControlador() {
        super(SolicitacaoAlteracaoConservacaoBem.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-alteracao-conservacao-bens-moveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "nova-sol-alteracao-conserv", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        try {
            super.novo();
            selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setDataSolicitacao(getDataOperacao());
            selecionado.setTipoBem(TipoBem.MOVEIS);
            novoFiltroPesquisaBem();
            novoAssistente();
            inicializarListas();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "ver-sol-alteracao-conserv", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarComDependenciasArquivo(getId());
        recuperarHierarquiaDaUnidade();
    }

    @URLAction(mappingId = "editar-sol-alteracao-conserv", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        selecionado = facade.recuperarComDependenciasArquivo(getId());
        operacao = Operacoes.EDITAR;
        if (!selecionado.isEmElaboracao()) {
            redirecionarParaVer();
            FacesUtil.addOperacaoNaoPermitida("A solicitação encontrasse-se" + selecionado.getSituacao().getDescricao() + " não pode ser alterada.");
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
            selecionado.setUnidadeOrganizacional(hierarquiaAdministrativa.getSubordinada());
            validarRegrasGerais();
            validarDataLancamentoAndDataOperacaoBem();
            bloquearMovimentacaoBens();
            assistenteMovimentacao.setFutureSalvar(facade.salvarSolicitacao(assistenteMovimentacao));
            FacesUtil.executaJavaScript("iniciaFuture()");
        } catch (MovimentacaoBemException ex) {
            blocoCatchMovimentacaoBemException(ex);
        } catch (ValidacaoException ex) {
            blocoCatchValidacaoException(ex);
        } catch (ExcecaoNegocioGenerica ex) {
            blocoCatchExcecaoNegocioGenerica(ex);
        } catch (Exception e) {
            blocoCatchException(e);
            assistenteMovimentacao.descobrirETratarException(e);
        }
    }

    public void concluir() {
        try {
            validarDataLancamentoAndDataOperacaoBem();
            bloquearMovimentacaoBens();
            assistenteMovimentacao.setFutureSalvar(facade.concluirSolicitacao(assistenteMovimentacao));
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

    public void consultarFutureSalvar() {
        if (assistenteMovimentacao.getFutureSalvar() != null && assistenteMovimentacao.getFutureSalvar().isDone()) {
            try {
                AssistenteMovimentacaoBens assistente = assistenteMovimentacao.getFutureSalvar().get();
                if (assistente.getSelecionado() != null) {
                    selecionado = (SolicitacaoAlteracaoConservacaoBem) assistente.getSelecionado();
                    desbloquearMovimentacaoSingleton();
                    FacesUtil.executaJavaScript("finalizaFuture()");
                }
            } catch (Exception ex) {
                assistenteMovimentacao.descobrirETratarException(ex);
                assistenteMovimentacao.setBloquearAcoesTela(true);
                FacesUtil.executaJavaScript("clearInterval(timerSalvar)");
                FacesUtil.executaJavaScript("closeDialog(dlgProcesso)");
                assistenteMovimentacao.setFutureSalvar(null);
                desbloquearMovimentacaoSingleton();
            }
        }
    }

    public void finalizarFutureSalvar() {
        assistenteMovimentacao.setFutureSalvar(null);
        redirecionarParaVer();
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
    }

    private void blocoCatchException(Exception e) {
        desbloquearMovimentacaoSingleton();
        FacesUtil.executaJavaScript("aguarde.hide()");
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
            selecionado.realizarValidacoes();
            validarUnidadeAdministrativa();
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
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataSolicitacao(), OperacaoMovimentacaoBem.SOLICITACAO_ALTERACAO_CONSERVACAO_BEM);
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
        filtroPesquisaBem.setTipoEventoBem(TipoEventoBem.SOLICITACAO_ALTERACAO_CONSERVACAO_BEM);
        filtroPesquisaBem.setIdSelecionado(selecionado.getId());
        filtroPesquisaBem.setOperacao(operacao);
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
            facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(SolicitacaoAlteracaoConservacaoBem.class, selecionado.getUnidadeOrganizacional(), assistenteMovimentacao);
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
        facade.getSingletonBloqueioPatrimonio().desbloquearMovimentacaoSingleton(assistenteMovimentacao, SolicitacaoAlteracaoConservacaoBem.class);
    }

    public boolean isBensSelecionados() {
        return assistenteMovimentacao != null && assistenteMovimentacao.hasBensSelecionadoVo();
    }

    public void atribuirUnidadeAPesquisaBem() {
        try {
            if (assistenteMovimentacao.getConfigMovimentacaoBem() == null) {
                novoAssistente();
            }
            if (hierarquiaAdministrativa != null) {
                filtroPesquisaBem.setHierarquiaAdministrativa(hierarquiaAdministrativa);
                inicializarListas();
                desbloquearMovimentacaoSingleton();
                assistenteMovimentacao.setMensagens(new ArrayList<String>());
            }
        } catch (ExcecaoNegocioGenerica e) {
            setHierarquiaAdministrativa(null);
            FacesUtil.atualizarComponente("Formulario:tab-view-principal:op-dados-gerais");
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public List<SelectItem> getSituacaoConservacaoBem() {
        if (selecionado.getEstadoConservacao() != null) {
            return Util.getListSelectItem(selecionado.getEstadoConservacao().getSituacoes());
        }
        return new ArrayList<>();
    }

    public List<SelectItem> getEstadoConservacaoAlteracaoConservacao() {
        return Util.getListSelectItem(Arrays.asList(EstadoConservacaoBem.getValoresPossiveisParaAlteracaoConservacao(TipoBem.MOVEIS)));
    }

    private void novoAssistente() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataSolicitacao(), operacao);
        assistenteMovimentacao.setSelecionado(selecionado);
        assistenteMovimentacao.setUnidadeOrganizacional(selecionado.getUnidadeOrganizacional());
        assistenteMovimentacao.setTipoEventoBem(TipoEventoBem.SOLICITACAO_ALTERACAO_CONSERVACAO_BEM);
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

    public List<SelectItem> retornaHierarquiaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (hierarquiaAdministrativa != null && hierarquiaAdministrativa.getSubordinada() != null) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(
                hierarquiaAdministrativa.getSubordinada(),
                getDataOperacao())) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }
        return toReturn;
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
        if (!assistenteMovimentacao.hasBensSelecionadoVo()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum bem foi selecionado para a solicitação de alteração de conservação.");
        }
        ve.lancarException();
        for (BemVo bemVo : assistenteMovimentacao.getBensSelecionadosVo()) {
            if (selecionado.getEstadoConservacao().equals(bemVo.getEstadoResultante().getEstadoBem())
                && selecionado.getSituacaoConservacao().equals(bemVo.getEstadoResultante().getSituacaoConservacaoBem())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O bem " + bemVo.getBem() + ": " + AssistenteMovimentacaoBens.MSG_ERRO_SITUACOES_IGUAIS);
            }
        }
        ve.lancarException();
    }

    public boolean canAlterarOrExcluir() {
        if (isGestorPatrimonio()) {
            selecionado.isEmElaboracao();
        }
        return selecionado.getResponsavel() != null
            && selecionado.getResponsavel().equals(facade.getSistemaFacade().getUsuarioCorrente())
            && selecionado.isEmElaboracao();
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
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }
}
