package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.entidadesauxiliares.VOItemBaixaPatrimonial;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoBaixa;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SolicitacaoBaixaPatrimonialFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 04/06/14
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "solicitacaoBaixaPatrimonialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSolicitacaoBaixaPatrimonioMovel", pattern = "/solicitacao-baixa-bem-movel/novo/", viewId = "/faces/administrativo/patrimonio/solicitacaoBaixa/movel/edita.xhtml"),
    @URLMapping(id = "editarSolicitacaoPatrimonioMovel", pattern = "/solicitacao-baixa-bem-movel/editar/#{solicitacaoBaixaPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoBaixa/movel/edita.xhtml"),
    @URLMapping(id = "listarSolicitacaoPatrimonioMovel", pattern = "/solicitacao-baixa-bem-movel/listar/", viewId = "/faces/administrativo/patrimonio/solicitacaoBaixa/movel/lista.xhtml"),
    @URLMapping(id = "verSolicitacaoPatrimonioMovel", pattern = "/solicitacao-baixa-bem-movel/ver/#{solicitacaoBaixaPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoBaixa/movel/visualizar.xhtml"),

    @URLMapping(id = "novaSolicitacaoBaixaPatrimonioImovel", pattern = "/solicitacao-baixa-bem-imovel/novo/", viewId = "/faces/administrativo/patrimonio/solicitacaoBaixa/imovel/edita.xhtml"),
    @URLMapping(id = "editarSolicitacaoPatrimonioImovel", pattern = "/solicitacao-baixa-bem-imovel/editar/#{solicitacaoBaixaPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoBaixa/imovel/edita.xhtml"),
    @URLMapping(id = "listarSolicitacaoPatrimonioImovel", pattern = "/solicitacao-baixa-bem-imovel/listar/", viewId = "/faces/administrativo/patrimonio/solicitacaoBaixa/imovel/lista.xhtml"),
    @URLMapping(id = "verSolicitacaoPatrimonioImovel", pattern = "/solicitacao-baixa-bem-imovel/ver/#{solicitacaoBaixaPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoBaixa/imovel/visualizar.xhtml")
})
public class SolicitacaoBaixaPatrimonialControlador extends PrettyControlador<SolicitacaoBaixaPatrimonial> implements Serializable, CRUD {

    @EJB
    private SolicitacaoBaixaPatrimonialFacade facade;
    private List<VOItemBaixaPatrimonial> bensPorUnidade;
    private CompletableFuture<List<Bem>> futurePesquisaBens;
    private CompletableFuture<List<VOItemBaixaPatrimonial>> futurePesquisaItensLeilao;
    private CompletableFuture<SolicitacaoBaixaPatrimonial> futureSalvarAndConcluir;
    private List<Bem> bensDisponiveis;
    private List<Bem> bensSelecionados;
    private AssistenteMovimentacaoBens assistenteMovimentacao;
    private FiltroPesquisaBem filtroPesquisaBem;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    public SolicitacaoBaixaPatrimonialControlador() {
        super(SolicitacaoBaixaPatrimonial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novaSolicitacaoBaixaPatrimonioMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaSolicitacaoDeBaixaDeBemMovel() {
        try {
            super.novo();
            selecionado.setTipoBem(TipoBem.MOVEIS);
            novo();
            iniciarAssistenteBarraProgresso();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "novaSolicitacaoBaixaPatrimonioImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaSolicitacaoDeBaixaDeBemImovel() {
        try {
            super.novo();
            selecionado.setTipoBem(TipoBem.IMOVEIS);
            novo();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @Override
    public void novo() {
        inicializarSelecionado();
        inicializarFiltro();
        inicializarListas();
    }

    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = facade.recuperarComDependenciasArquivo(getId());
        novoFiltroSolicitacao();
        inicializarListas();
    }

    private void novoFiltroSolicitacao() {
        filtroPesquisaBem = new FiltroPesquisaBem(selecionado.getTipoBem(), selecionado);
    }

    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarComDependenciasArquivo(getId());
        bensPorUnidade = Lists.newArrayList();
        bensPorUnidade.addAll(facade.buscarBensUnidadePorSolicitacaoBaixa(selecionado));
    }

    public void inicializarListas() {
        bensSelecionados = Lists.newArrayList();
        bensDisponiveis = Lists.newArrayList();
    }

    @URLAction(mappingId = "verSolicitacaoPatrimonioMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verSolicitacaoDeBaixaDeBemMovel() {
        ver();
    }

    @URLAction(mappingId = "verSolicitacaoPatrimonioImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verSolicitacaoDeBaixaDeBemImovel() {
        ver();
    }

    @URLAction(mappingId = "editarSolicitacaoPatrimonioMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarSolicitacaoDeBaixaBemMovel() {
        editar();
    }

    @URLAction(mappingId = "editarSolicitacaoPatrimonioImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarSolicitacaoDeBaixaBemImovel() {
        editar();
    }

    public Boolean botaoExcluir() {
        return facade.podeEditarSolicitacao(selecionado);
    }

    private void inicializarFiltro() {
        novoFiltroSolicitacao();
    }

    private void inicializarSelecionado() {
        selecionado.setCodigo(null);
        selecionado.setDataSolicitacao(getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @Override
    public String getCaminhoPadrao() {
        switch (selecionado.getTipoBem()) {
            case MOVEIS:
                return "/solicitacao-baixa-bem-movel/";
            case IMOVEIS:
                return "/solicitacao-baixa-bem-imovel/";
            default:
                return "";
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void iniciarAssistenteBarraProgresso() {
        recuperarConfiguracaoMovimentacaoBem();
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataSolicitacao(), operacao);
        assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistenteMovimentacao.setSelecionado(selecionado);
        assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
        adicionarUnidadesAssistente();
    }

    private void adicionarUnidadesAssistente() {
        if (selecionado.isTipoBaixaAlienacao()) {
            if (selecionado.getLeilaoAlienacao() != null) {
                assistenteMovimentacao.setBensSelecionados(assistenteMovimentacao.getBensSalvos());
                List<HierarquiaOrganizacional> hierarquias = facade.buscarHierarquiaAdministrativaItemLoteLeilao(selecionado.getLeilaoAlienacao());
                for (HierarquiaOrganizacional ho : hierarquias) {
                    assistenteMovimentacao.getUnidades().add(ho.getSubordinada());
                }
            }
        } else {
            assistenteMovimentacao.setBensSelecionados(bensSelecionados);
            if (selecionado.getHierarquiaAdministrativa() != null) {
                assistenteMovimentacao.getUnidades().add(selecionado.getHierarquiaAdministrativa().getSubordinada());
            }
        }
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataSolicitacao(), OperacaoMovimentacaoBem.SOLICITACAO_BAIXA_PATRIMONIAL);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    private void validarRegrasConfiguracaoMovimentacaoBem() {
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataSolicitacao(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void validarItensSelecionados() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.isTipoBaixaAlienacao() && bensSelecionados.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione ao menos um bem para continuar a operação.");
        }
        if (selecionado.isTipoBaixaAlienacao()) {
            for (VOItemBaixaPatrimonial voItemBaixaPatrimonial : bensPorUnidade) {
                if (voItemBaixaPatrimonial.getBensAgrupados().isEmpty()) {
                    ve.adicionarMensagemDeCampoObrigatorio("Selecione ao menos um bem para continuar a operação.");
                }
            }
        }
        ve.lancarException();
    }

    public void buscarBensAoEditar() {
        if (isOperacaoEditar()) {
            switch (selecionado.getTipoBaixa()) {
                case ALIENACAO:
                    processarSelecaoLeilaoAlienacao();
                    break;
                case INCORPORACAO_INDEVIDA:
                    processarSelecaoIncorporacao();
                    break;
                case AQUISICAO_INDEVIDA:
                    processarSelecaoAquisicao();
                    break;
                default:
                    filtroPesquisaBem.setHierarquiaAdministrativa(selecionado.getHierarquiaAdministrativa());
                    pesquisaBens();
                    break;
            }
        }
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            iniciarAssistenteBarraProgresso();
            validarRegrasConfiguracaoMovimentacaoBem();
            validarItensSelecionados();
            bloquearMovimentacaoBens();
            futureSalvarAndConcluir = AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                () -> facade.salvarRetornando(selecionado, bensSelecionados, assistenteMovimentacao, bensPorUnidade));
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (MovimentacaoBemException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            if (!isOperacaoNovo() || selecionado.isTipoBaixaAlienacao()) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            desbloquearMovimentacaoSingleton();
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

    public void concluirSolicitacaoBaixa() {
        try {
            iniciarAssistenteBarraProgresso();
            validarRegrasConfiguracaoMovimentacaoBem();
            bloquearMovimentacaoBens();
            futureSalvarAndConcluir = AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                () -> facade.concluirSolicitacaoBaixa(selecionado, bensPorUnidade, assistenteMovimentacao));
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (MovimentacaoBemException ex) {
            if (!isOperacaoNovo() || selecionado.isTipoBaixaAlienacao()) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            desbloquearMovimentacaoSingleton();
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

    @Override
    public void excluir() {
        try {
            recuperarConfiguracaoMovimentacaoBem();
            facade.remover(selecionado, configMovimentacaoBem);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            redireciona();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void pesquisaBens() {
        try {
            validarPesquisaBem();
            iniciarAssistenteBarraProgresso();
            validarRegrasConfiguracaoMovimentacaoBem();
            if (verificarBensBloqueadoSingletonAoPesquisar()) return;
            inicializarListas();
            atribuirFitlroPesquisaBaixaPorIncorporacaoIndevida();
            atribuirFitlroPesquisaBaixaPorAquisicaoIndevida();
            futurePesquisaBens = AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                () -> facade.pesquisarBens(filtroPesquisaBem, assistenteMovimentacao));
            FacesUtil.executaJavaScript("iniciarPesquisa()");
        } catch (MovimentacaoBemException me) {
            FacesUtil.printAllFacesMessages(me.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void atribuirFitlroPesquisaBaixaPorIncorporacaoIndevida() {
        if (selecionado.getEfetivacaoIncorporacao() != null) {
            filtroPesquisaBem.setIdsBem(facade.getEfetivacaoSolicitacaoIncorporacaoMovelFacade().buscarIdsBens(selecionado.getEfetivacaoIncorporacao()));
            filtroPesquisaBem.setHierarquiaAdministrativa(selecionado.getHierarquiaAdministrativa());
        }
    }

    private void atribuirFitlroPesquisaBaixaPorAquisicaoIndevida() {
        if (selecionado.getAquisicao() != null && selecionado.isBaixaPorAquisicaoIndevida()) {
            filtroPesquisaBem.setIdsBem(facade.getEfetivacaoAquisicaoFacade().buscarIdsBens(selecionado.getAquisicao()));
        }
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
        if (isOperacaoVer()) {
            for (UnidadeOrganizacional unidade : assistenteMovimentacao.getUnidades()) {
                facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(SolicitacaoBaixaPatrimonial.class, unidade, assistenteMovimentacao);
            }
        } else {
            if (facade.getSingletonBloqueioPatrimonio().verificarBensBloqueadoSingletonAoPesquisar(assistenteMovimentacao)) {
                FacesUtil.executaJavaScript("aguarde.hide()");
                FacesUtil.atualizarComponente("Formulario");
                assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
            }
            if (!isOperacaoVer()) {
                if (isOperacaoNovo() && !assistenteMovimentacao.hasInconsistencias() && !selecionado.isTipoBaixaAlienacao()) {
                    facade.getConfigMovimentacaoBemFacade().verificarBensSelecionadosSeDisponivelConfiguracaoBloqueio(assistenteMovimentacao);
                }
                if (selecionado.isTipoBaixaAlienacao()) {
                    bensSelecionados.addAll(assistenteMovimentacao.getBensSalvos());
                }
                for (Bem bem : bensSelecionados) {
                    facade.getSingletonBloqueioPatrimonio().bloquearBem(bem, assistenteMovimentacao);
                }
            }
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("Formulario");
            assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
        }
    }

    private void desbloquearMovimentacaoSingleton() {
        for (UnidadeOrganizacional unidade : assistenteMovimentacao.getUnidades()) {
            facade.getSingletonBloqueioPatrimonio().desbloquearMovimentoPorUnidade(SolicitacaoBaixaPatrimonial.class, unidade);
        }
        facade.getSingletonBloqueioPatrimonio().desbloquearBens(assistenteMovimentacao.getBensSelecionados());
        assistenteMovimentacao.setBloquearAcoesTela(false);
    }

    public void processarSelecaoLeilaoAlienacao() {
        try {
            iniciarAssistenteBarraProgresso();
            validarRegrasConfiguracaoMovimentacaoBem();
            if (selecionado.getLeilaoAlienacao() != null && selecionado.isTipoBaixaAlienacao()) {
                futurePesquisaItensLeilao = AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                    () -> facade.buscarBensDaUnidadeArrematadosLeilaoAlienacao(selecionado.getLeilaoAlienacao(), assistenteMovimentacao));
                FacesUtil.executaJavaScript("iniciarPesquisa()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void processarSelecaoIncorporacao() {
        if (selecionado.getEfetivacaoIncorporacao() != null && selecionado.isBaixaPorIncorporacaoIndevida()) {
            selecionado.setHierarquiaAdministrativa(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                selecionado.getEfetivacaoIncorporacao().getSolicitacaoIncorporacao().getUnidadeAdministrativa(),
                facade.getSistemaFacade().getDataOperacao()));
            selecionado.setMotivo("Baixa por incorporação indevida " + selecionado.getEfetivacaoIncorporacao());
            pesquisaBens();
        }
    }

    public void processarSelecaoAquisicao() {
        if (selecionado.getAquisicao() != null && selecionado.isBaixaPorAquisicaoIndevida()) {
            UnidadeOrganizacional unidadeOrg = facade.getEfetivacaoAquisicaoFacade().getRequisicaoDeCompraFacade().getUnidadeAdministrativa(selecionado.getAquisicao().getSolicitacaoAquisicao().getRequisicaoDeCompra(), getDataOperacao());
            HierarquiaOrganizacional hoAdmVigente = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                unidadeOrg,
                facade.getSistemaFacade().getDataOperacao());
            if (hoAdmVigente == null) {
                hoAdmVigente = facade.getHierarquiaOrganizacionalFacade().recuperaHierarquiaOrganizacionalPelaUnidadeUltima(unidadeOrg.getId());
            }
            selecionado.setHierarquiaAdministrativa(hoAdmVigente);
            selecionado.setMotivo("Baixa por aquisicação indevida " + selecionado.getAquisicao());
            pesquisaBens();
        }
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        ve.lancarException();
        if (!selecionado.isTipoBaixaAlienacao() && selecionado.getHierarquiaAdministrativa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Unidade Administrativa deve ser informada.");
        }
        if (selecionado.isTipoBaixaAlienacao()) {
            if (selecionado.getLeilaoAlienacao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A Efetivação da Alienação deve ser informada.");
            }
            ve.lancarException();
            if (selecionado.getDataSolicitacao() != null
                && DataUtil.dataSemHorario(selecionado.getDataSolicitacao()).before(DataUtil.dataSemHorario(selecionado.getLeilaoAlienacao().getDataEfetivacao()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data da solicitação de baixa deve ser posterior ou igual a data da efetivação da alienação " + DataUtil.getDataFormatada(selecionado.getLeilaoAlienacao().getDataEfetivacao()) + ".");
            }
            if (assistenteMovimentacao.hasInconsistencias()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existem bens bloqueados em outro processo não permitindo a continuação da baixa. Finalize o processo anterior para continuar.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposDeBaixaMovel() {
        return Util.getListSelectItem(Arrays.asList(TipoBaixa.values()));
    }

    public List<SelectItem> getTiposDeBaixaImovel() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (TipoBaixa obj : TipoBaixa.values()) {
            if (!TipoBaixa.TRANSFERENCIA.equals(obj) || !TipoBaixa.REDISTRIBUICAO.equals(obj)) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }
        return toReturn;
    }


    private void atribuirHierarquiaOrganizacionalDoPesquisaBem() {
        if (selecionado.getHierarquiaAdministrativa() != null) {
            filtroPesquisaBem.setHierarquiaAdministrativa(selecionado.getHierarquiaAdministrativa());
        }
    }

    public void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void processaSelecaoTipoBaixa() {
        selecionado.setLeilaoAlienacao(null);
    }

    public List<LeilaoAlienacao> completarLeilaoAlienacaoParaBaixar(String parte) {
        return facade.getLeilaoAlienacaoFacade().buscarLeilaoAlienacaoDisponiveisParaBaixa(parte);
    }

    public List<EfetivacaoSolicitacaoIncorporacaoMovel> completarEfetivacaoIncorporacao(String parte) {
        return facade.getEfetivacaoSolicitacaoIncorporacaoMovelFacade().buscarEfetivacao(parte.trim());
    }

    public List<Aquisicao> completarAquisicao(String parte) {
        return facade.getEfetivacaoAquisicaoFacade().buscarAquisicaoFinalizada(parte.trim(), TipoBem.MOVEIS);
    }

    public void processaSelecaoDeHierarquia() {
        bensDisponiveis = Lists.newArrayList();
        atribuirHierarquiaOrganizacionalDoPesquisaBem();
    }

    public BigDecimal getValorTotalOriginalBensSelecionados() {
        BigDecimal total = BigDecimal.ZERO;
        if (bensSelecionados != null) {
            for (Bem bemSelecionado : bensSelecionados) {
                total = total.add(bemSelecionado.getValorOriginal());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalAjusteBensSelecionados() {
        BigDecimal total = BigDecimal.ZERO;
        if (bensSelecionados != null) {
            for (Bem bemSelecionado : bensSelecionados) {
                total = total.add(bemSelecionado.getValorDosAjustes());
            }
        }
        return total;
    }

    public boolean desabilitarTipoBaixa() {
        return isOperacaoEditar() || ((bensSelecionados != null && !bensSelecionados.isEmpty()) || (bensPorUnidade != null && !bensPorUnidade.isEmpty()));
    }

    public Boolean mostrarBotaoSelecionarTodos() {
        try {
            return bensDisponiveis.size() != bensSelecionados.size();
        } catch (NullPointerException ex) {
            return Boolean.FALSE;
        }
    }

    public void desmarcarTodos() {
        bensSelecionados.clear();
    }

    public Boolean itemSelecionado(Bem bem) {
        return bensSelecionados.contains(bem);
    }

    public void desmarcarItem(Bem bem) {
        bensSelecionados.remove(bem);
    }

    public void selecionarItem(Bem bem) {
        bensSelecionados.add(bem);
    }

    public void selecionarTodos() {
        try {
            desmarcarTodos();
            bensSelecionados.addAll(bensDisponiveis);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void gerarRelatorio() {
        try {
            if (!isOperacaoNovo()) {
                RelatorioDTO dto = new RelatorioDTO();
                String nomeRelatorio = "RELATÓRIO DE SOLICITAÇÃO DE BAIXA DE BENS " + selecionado.getTipoBem().getDescricao().toUpperCase();
                dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
                dto.setNomeParametroBrasao("BRASAO");
                dto.adicionarParametro("MODULO", "Patrimônio");
                dto.adicionarParametro("ENTIDADE", getDescricaoHierarquia().toUpperCase());
                dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
                dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
                dto.adicionarParametro("DATA_SOLICITACAO", DataUtil.getDataFormatada(selecionado.getDataSolicitacao()));
                dto.adicionarParametro("CONDICAO", " AND SOLICITACAO.ID = " + selecionado.getId());
                dto.setNomeRelatorio(nomeRelatorio);
                dto.setApi("administrativo/solicitacao-baixa-patrimonial/");
                ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
                FacesUtil.addMensagemRelatorioSegundoPlano();
            }
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    private String getDescricaoHierarquia() {
        try {
            if (!selecionado.isTipoBaixaAlienacao()) {
                UnidadeOrganizacional unidadeSolicitacao = selecionado.getHierarquiaAdministrativa().getSubordinada();
                return facade.getHierarquiaOrganizacionalFacade().getDescricaoHierarquia(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unidadeSolicitacao, selecionado.getDataSolicitacao());
            }
        } catch (NullPointerException ex) {
            return "";
        }
        return "";
    }

    private void getUsuarioSistema(HashMap parameters) {
        if (facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getLogin());
        }
    }

    public List<SelectItem> buscarHierarquiaOrganizacionalOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (selecionado.getHierarquiaAdministrativa() != null && selecionado.getHierarquiaAdministrativa().getSubordinada() != null) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(selecionado.getHierarquiaAdministrativa().getSubordinada(), getDataOperacao())) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }
        return toReturn;
    }

    private void validarPesquisaBem() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getHierarquiaAdministrativa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa deve ser informado.");
        }
        if (Strings.isNullOrEmpty(selecionado.getMotivo())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo deve ser informado.");
        }
        ve.lancarException();
    }

    public void consultarFutureSalvar() {
        if (futureSalvarAndConcluir != null && futureSalvarAndConcluir.isDone()) {
            try {
                SolicitacaoBaixaPatrimonial solicitacaoSalva = futureSalvarAndConcluir.get();
                if (solicitacaoSalva != null) {
                    desbloquearMovimentacaoSingleton();
                    selecionado = solicitacaoSalva;
                    FacesUtil.executaJavaScript("finalizarSalvar()");
                }
            } catch (Exception ex) {
                FacesUtil.executaJavaScript("clearInterval(timerSalvar)");
                FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                FacesUtil.atualizarComponente("Formulario");
                FacesUtil.executaJavaScript("aguarde.hide()");
                assistenteMovimentacao.setBloquearAcoesTela(true);
                futureSalvarAndConcluir = null;
                desbloquearMovimentacaoSingleton();
                assistenteMovimentacao.descobrirETratarException(ex);
            }
        }
    }

    public void finalizarProcesssoSalvar() {
        futureSalvarAndConcluir = null;
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        redirecionarParaVer();
    }

    public void acompanharPesquisa() throws ExecutionException, InterruptedException {
        if (!selecionado.isTipoBaixaAlienacao()) {
            if (futurePesquisaBens != null && futurePesquisaBens.isDone()) {
                List<Bem> bensConsulta = futurePesquisaBens.get();
                bensDisponiveis.addAll(bensConsulta);
                if (isOperacaoNovo() && (selecionado.isBaixaPorIncorporacaoIndevida() || selecionado.isBaixaPorAquisicaoIndevida())) {
                    bensSelecionados.addAll(bensDisponiveis);
                }
                if (isOperacaoEditar()) {
                    List<Bem> bensSalvoNaSolicitacao = assistenteMovimentacao.getBensSalvos();
                    bensDisponiveis.addAll(bensSalvoNaSolicitacao);
                    bensSelecionados.addAll(bensSalvoNaSolicitacao);
                }
                Collections.sort(bensDisponiveis);
                futurePesquisaBens = null;
                FacesUtil.executaJavaScript("terminarPesquisa()");
            }
        } else {
            if (futurePesquisaItensLeilao != null && futurePesquisaItensLeilao.isDone()) {
                bensPorUnidade = Lists.newArrayList();
                bensPorUnidade.addAll(futurePesquisaItensLeilao.get());
                futurePesquisaItensLeilao = null;
                FacesUtil.executaJavaScript("terminarPesquisa()");
            }
        }
    }

    public void finalizarPesquisa() {
        FacesUtil.atualizarComponente("Formulario");
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        if (selecionado != null && selecionado.getHierarquiaAdministrativa() != null) {
            return selecionado.getHierarquiaAdministrativa().getSubordinada();
        }
        return null;
    }

    public void redirecionarParaSolicitacaoBaixaMovel(SolicitacaoBaixaPatrimonial selecionado) {
        this.selecionado = selecionado;
        this.selecionado.setTipoBem(TipoBem.MOVEIS);
        redirecionarParaVer();
    }

    public void redirecionarParaSolicitacaoBaixaImovel(SolicitacaoBaixaPatrimonial selecionado) {
        this.selecionado = selecionado;
        this.selecionado.setTipoBem(TipoBem.IMOVEIS);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public CompletableFuture<List<Bem>> getFuturePesquisaBens() {
        return futurePesquisaBens;
    }

    public void setFuturePesquisaBens(CompletableFuture<List<Bem>> futurePesquisaBens) {
        this.futurePesquisaBens = futurePesquisaBens;
    }

    public List<Bem> getBensDisponiveis() {
        return bensDisponiveis;
    }

    public void setBensDisponiveis(List<Bem> bensDisponiveis) {
        this.bensDisponiveis = bensDisponiveis;
    }

    public List<Bem> getBensSelecionados() {
        return bensSelecionados;
    }

    public void setBensSelecionados(List<Bem> bensSelecionados) {
        this.bensSelecionados = bensSelecionados;
    }

    public FiltroPesquisaBem getFiltroPesquisaBem() {
        return filtroPesquisaBem;
    }

    public void setFiltroPesquisaBem(FiltroPesquisaBem filtroPesquisaBem) {
        this.filtroPesquisaBem = filtroPesquisaBem;
    }

    public List<VOItemBaixaPatrimonial> getBensPorUnidade() {
        return bensPorUnidade;
    }

    public void setBensPorUnidade(List<VOItemBaixaPatrimonial> bensPorUnidade) {
        this.bensPorUnidade = bensPorUnidade;
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteBarraProgresso) {
        this.assistenteMovimentacao = assistenteBarraProgresso;
    }
}
