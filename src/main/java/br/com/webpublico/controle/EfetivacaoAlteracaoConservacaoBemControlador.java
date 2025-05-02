package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EfetivacaoAlteracaoConservacaoBemFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
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
import java.util.concurrent.ExecutionException;


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAlteracaoConservacaoBemMovel", pattern = "/efetivacao-alteracao-de-conservacao-de-bens-moveis/novo/", viewId = "/faces/administrativo/patrimonio/alteracaoconservacao/movel/edita.xhtml"),
    @URLMapping(id = "listarAlteracaoConservacaoBemMovel", pattern = "/efetivacao-alteracao-de-conservacao-de-bens-moveis/listar/", viewId = "/faces/administrativo/patrimonio/alteracaoconservacao/movel/lista.xhtml"),
    @URLMapping(id = "verAlteracaoConservacaoBemMovel", pattern = "/efetivacao-alteracao-de-conservacao-de-bens-moveis/ver/#{efetivacaoAlteracaoConservacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/alteracaoconservacao/movel/visualizar.xhtml"),

    @URLMapping(id = "novoAlteracaoConservacaoBemImovel", pattern = "/alteracao-de-conservacao-de-bens-imoveis/novo/", viewId = "/faces/administrativo/patrimonio/alteracaoconservacao/imovel/edita.xhtml"),
    @URLMapping(id = "editarAlteracaoConservacaoBemImovel", pattern = "/alteracao-de-conservacao-de-bens-imoveis/editar/#{efetivacaoAlteracaoConservacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/alteracaoconservacao/imovel/edita.xhtml"),
    @URLMapping(id = "listarAlteracaoConservacaoBemImovel", pattern = "/alteracao-de-conservacao-de-bens-imoveis/listar/", viewId = "/faces/administrativo/patrimonio/alteracaoconservacao/imovel/lista.xhtml"),
    @URLMapping(id = "verAlteracaoConservacaoBemImovel", pattern = "/alteracao-de-conservacao-de-bens-imoveis/ver/#{efetivacaoAlteracaoConservacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/alteracaoconservacao/imovel/visualizar.xhtml"),

    @URLMapping(id = "novoAlteracaoConservacaoBemIntangivel", pattern = "/alteracao-de-conservacao-de-bens-intangiveis/novo/", viewId = "/faces/administrativo/patrimonio/alteracaoconservacao/intangivel/edita.xhtml"),
    @URLMapping(id = "editarAlteracaoConservacaoBemIntangivel", pattern = "/alteracao-de-conservacao-de-bens-intangiveis/editar/#{efetivacaoAlteracaoConservacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/alteracaoconservacao/intangivel/edita.xhtml"),
    @URLMapping(id = "listarAlteracaoConservacaoBemIntangivel", pattern = "/alteracao-de-conservacao-de-bens-intangiveis/listar/", viewId = "/faces/administrativo/patrimonio/alteracaoconservacao/intangivel/lista.xhtml"),
    @URLMapping(id = "verAlteracaoConservacaoBemIntangivel", pattern = "/alteracao-de-conservacao-de-bens-intangiveis/ver/#{efetivacaoAlteracaoConservacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/alteracaoconservacao/intangivel/visualizar.xhtml")
})
public class EfetivacaoAlteracaoConservacaoBemControlador extends PrettyControlador<EfetivacaoAlteracaoConservacaoBem> implements Serializable, CRUD {

    @EJB
    private EfetivacaoAlteracaoConservacaoBemFacade facade;
    private SituacaoMovimentoAdministrativo situacao;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private FiltroPesquisaBem filtroPesquisaBem;
    private AssistenteMovimentacaoBens assistenteMovimentacao;

    public EfetivacaoAlteracaoConservacaoBemControlador() {
        super(EfetivacaoAlteracaoConservacaoBem.class);
    }

    @Override
    public String getCaminhoPadrao() {
        switch (selecionado.getTipoBem()) {
            case MOVEIS:
                return "/efetivacao-alteracao-de-conservacao-de-bens-moveis/";
            case IMOVEIS:
                return "/alteracao-de-conservacao-de-bens-imoveis/";
            case INTANGIVEIS:
                return "/alteracao-de-conservacao-de-bens-intagiveis/";
            default:
                return "/alteracao-de-conservacao-de-bens/";
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoAlteracaoConservacaoBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoMovel() {
        try {
            novo();
            selecionado.setTipoBem(TipoBem.MOVEIS);
            novoAssistenteMovimentacao();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "novoAlteracaoConservacaoBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoImovel() {
        novo();
        selecionado.setTipoBem(TipoBem.IMOVEIS);
        novoFiltroPesquisaBem();
    }

    @URLAction(mappingId = "novoAlteracaoConservacaoBemIntangivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoIntangivel() {
        novo();
        selecionado.setTipoBem(TipoBem.INTANGIVEIS);
        novoFiltroPesquisaBem();
    }

    @URLAction(mappingId = "verAlteracaoConservacaoBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verMovel() {
        ver();
        novoAssistenteMovimentacao();
    }

    @URLAction(mappingId = "verAlteracaoConservacaoBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verImovel() {
        ver();
    }

    @URLAction(mappingId = "verAlteracaoConservacaoBemIntangivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verIntangivel() {
        ver();
    }

    @URLAction(mappingId = "editarAlteracaoConservacaoBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarImovel() {
        editar();
    }

    @URLAction(mappingId = "editarAlteracaoConservacaoBemIntangivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarIntangivel() {
        editar();
    }

    @Override
    public void novo() {
        super.novo();
        selecionado.setDataEfetivacao(getDataOperacao());
        selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @Override
    public void editar() {
        selecionado = facade.recuperarComDependenciasAqruivo(getId());
        operacao = Operacoes.EDITAR;
        filtroPesquisaBem.setOperacao(operacao);
    }

    @Override
    public void ver() {
        selecionado = facade.recuperarComDependenciasAqruivo(getId());
        operacao = Operacoes.VER;
        recuperarHierarquiaDaUnidade();
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataEfetivacao(), OperacaoMovimentacaoBem.EFETIVACAO_ALTERACAO_CONSERVACAO_BEM);
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
        filtroPesquisaBem = new FiltroPesquisaBem(selecionado.getTipoBem(), selecionado);
        filtroPesquisaBem.setDataOperacao(getDataOperacao());
        filtroPesquisaBem.setDataReferencia(getDataOperacao());
        filtroPesquisaBem.setOperacao(operacao);
        filtroPesquisaBem.setIdSelecionado(isOperacaoNovo() ? selecionado.getSolicitacaoAlteracaoConsBem().getId() : selecionado.getId());
        filtroPesquisaBem.setTipoEventoBem(isOperacaoNovo() ? TipoEventoBem.SOLICITACAO_ALTERACAO_CONSERVACAO_BEM : TipoEventoBem.EFETIVACAO_ALTERACAO_CONSERVACAO_BEM);
    }

    public void acompanharFutureSalvar() {
        if (assistenteMovimentacao.getFutureSalvar() != null && assistenteMovimentacao.getFutureSalvar().isDone()) {
            try {
                AssistenteMovimentacaoBens assistente = assistenteMovimentacao.getFutureSalvar().get();
                if (assistente.getSelecionado() != null) {
                    selecionado = (EfetivacaoAlteracaoConservacaoBem) assistente.getSelecionado();
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

    public void atribuirNullDadosSolicitacao() {
        if (hasSolicitacaoSelecionada()) {
            selecionado.getSolicitacaoAlteracaoConsBem().setSituacao(SituacaoMovimentoAdministrativo.AGUARDANDO_EFETIVACAO);
            selecionado.getSolicitacaoAlteracaoConsBem().setMotivoRecusa(null);
            selecionado.setSolicitacaoAlteracaoConsBem(null);
            situacao = null;
            assistenteMovimentacao.setBensMovimentadosVo(new ArrayList<BemVo>());
        }
    }

    public void atribuirNullMotivoRejeicao() {
        if (hasSolicitacaoSelecionada()) {
            selecionado.getSolicitacaoAlteracaoConsBem().setSituacao(situacao);
            selecionado.getSolicitacaoAlteracaoConsBem().setMotivoRecusa(null);
        }
    }

    public void finalizarFutureSalvar() {
        assistenteMovimentacao.setFutureSalvar(null);
        redirecionarParaVer();
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
    }

    public void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void acompanharPesquisa() throws ExecutionException, InterruptedException {
        if (assistenteMovimentacao.getFuturePesquisaBemVo() != null && assistenteMovimentacao.getFuturePesquisaBemVo().isDone()) {
            assistenteMovimentacao.setBensMovimentadosVo(assistenteMovimentacao.getFuturePesquisaBemVo().get());
            for (BemVo bemVo : assistenteMovimentacao.getBensMovimentadosVo()) {
                assistenteMovimentacao.getBensSelecionados().add(bemVo.getBem());
            }
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
            assistenteMovimentacao.setFutureSalvar(facade.salvarEfetivacao(assistenteMovimentacao));
            FacesUtil.executaJavaScript("iniciaFuture()");
        } catch (MovimentacaoBemException me) {
            assistenteMovimentacao.setBloquearAcoesTela(true);
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.printAllFacesMessages(me.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    public void pesquisaBens() {
        try {
            assistenteMovimentacao.setUnidadeOrganizacional(selecionado.getSolicitacaoAlteracaoConsBem().getUnidadeOrganizacional());
            recuperarHierarquiaDaUnidade();
            validarDataLancamentoAndDataOperacaoBem();
            novoFiltroPesquisaBem();
            assistenteMovimentacao.setFuturePesquisaBemVo(facade.getPesquisaBemFacade().pesquisarBensPorTipoEvento(filtroPesquisaBem, assistenteMovimentacao));
            FacesUtil.executaJavaScript("iniciarPesquisa()");
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public boolean hasSolicitacaoSelecionada() {
        return selecionado.getSolicitacaoAlteracaoConsBem() != null;
    }

    private void bloquearMovimentacaoBens() {
        facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(SolicitacaoAlteracaoConservacaoBem.class, selecionado.getSolicitacaoAlteracaoConsBem().getUnidadeOrganizacional(), assistenteMovimentacao);
    }

    private void desbloquearMovimentacaoSingleton() {
        facade.getSingletonBloqueioPatrimonio().desbloquearMovimentacaoSingleton(assistenteMovimentacao, SolicitacaoAlteracaoConservacaoBem.class);
    }

    private void novoAssistenteMovimentacao() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataEfetivacao(), operacao);
        assistenteMovimentacao.setSelecionado(selecionado);
        recuperarConfiguracaoMovimentacaoBem();
    }

    public List<SolicitacaoAlteracaoConservacaoBem> completarSolicitacao(String parte) {
        return facade.getSolicitacaoAlteracaoConservacaoBemFacade().buscarSolicitacoesPorSituacao(parte, SituacaoMovimentoAdministrativo.AGUARDANDO_EFETIVACAO);
    }

    private void recuperarHierarquiaDaUnidade() {
        if (selecionado.getSolicitacaoAlteracaoConsBem() != null && selecionado.getSolicitacaoAlteracaoConsBem().getUnidadeOrganizacional() != null) {
            HierarquiaOrganizacional hierarquiaAdm = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                selecionado.getSolicitacaoAlteracaoConsBem().getUnidadeOrganizacional(),
                selecionado.getDataEfetivacao());
            setHierarquiaAdministrativa(hierarquiaAdm);
        }
    }

    private void validarRegrasGerais() {
        ValidacaoException ve = new ValidacaoException();
        if (!assistenteMovimentacao.hasBensMovimentadosVo()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum bem recuperado para efetivar a alteração de conservação.");
        }
        if (situacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Aceitar/Recusar Solicitação deve ser informado.");

        } else if (isRecusada() && Strings.isNullOrEmpty(selecionado.getSolicitacaoAlteracaoConsBem().getMotivoRecusa())) {
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
}
