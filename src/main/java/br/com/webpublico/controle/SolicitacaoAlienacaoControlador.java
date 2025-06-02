package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SolicitacaoAlienacaoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

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
 * User: Wellington
 * Date: 04/11/14
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "solicitacaoAlienacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoSolicitacaoAlienacao", pattern = "/solicitacao-de-alienacao/novo/", viewId = "/faces/administrativo/patrimonio/solicitacaoalienacao/edita.xhtml"),
    @URLMapping(id = "novaSolicitacaoAlienacaoRecuperada", pattern = "/solicitacao-de-alienacao-recuperada/novo/#{solicitacaoAlienacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoalienacao/edita.xhtml"),
    @URLMapping(id = "editarSolicitacaoAlienacao", pattern = "/solicitacao-de-alienacao/editar/#{solicitacaoAlienacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoalienacao/edita.xhtml"),
    @URLMapping(id = "listarSolicitacaoAlienacao", pattern = "/solicitacao-de-alienacao/listar/", viewId = "/faces/administrativo/patrimonio/solicitacaoalienacao/lista.xhtml"),
    @URLMapping(id = "verSolicitacaoAlienacao", pattern = "/solicitacao-de-alienacao/ver/#{solicitacaoAlienacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoalienacao/visualizar.xhtml")
})
public class SolicitacaoAlienacaoControlador extends PrettyControlador<SolicitacaoAlienacao> implements Serializable, CRUD {

    @EJB
    private SolicitacaoAlienacaoFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacionalAdm;
    private List<Bem> bensDisponiveis;
    private List<Bem> bensSelecionados;
    private CompletableFuture<List<Bem>> futurePesquisaBens;
    private CompletableFuture futureSalvarAndConcluir;
    private LazyDataModel<ItemSolicitacaoAlienacao> model;
    private FiltroPesquisaBem filtroPesquisaBem;
    private AssistenteMovimentacaoBens assistenteMovimentacao;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    public SolicitacaoAlienacaoControlador() {
        super(SolicitacaoAlienacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-de-alienacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoSolicitacaoAlienacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            super.novo();
            inicializarSolicitacaoAlienacao();
            novoFiltroPesquisaBem();
            novoAssistenteMovimentacao();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataSolicitacao(), OperacaoMovimentacaoBem.SOLICITACAO_ALIENACAO_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    @URLAction(mappingId = "novaSolicitacaoAlienacaoRecuperada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaSolicitacaoRecuperada() {
        SolicitacaoAlienacao solicitacaoRejeitada = facade.recuperarComDependenciaArquivoComposicao(super.getId());
        selecionado = new SolicitacaoAlienacao();
        selecionado.setUnidadeAdministrativa(solicitacaoRejeitada.getUnidadeAdministrativa());
        selecionado.setDetentorArquivoComposicao(solicitacaoRejeitada.getDetentorArquivoComposicao());
        selecionado.setItensLoteSolicitacaoAlienacao(new ArrayList<ItemSolicitacaoAlienacao>());
        selecionado.setDescricao(solicitacaoRejeitada.getDescricao());
        inicializarSolicitacaoAlienacao();
        setHierarquiaOrganizacionalAdm(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            selecionado.getUnidadeAdministrativa(),
            getDataOperacao()));
        novoFiltroPesquisaBem();
        montarFiltroPesquisa();
    }

    @URLAction(mappingId = "verSolicitacaoAlienacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarComDependenciaArquivoComposicao(getId());
        criarItensPaginacao();
    }

    @URLAction(mappingId = "editarSolicitacaoAlienacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = facade.recuperarComDependenciaArquivoComposicao(getId());
        if (selecionado.foiRejeitada()) {
            FacesUtil.addAtencao("Esta solicitação de alienação encontra-se rejeitada, não poderá sofrer alterações.");
            redirecionarParaVer();
        }
        setHierarquiaOrganizacionalAdm(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            selecionado.getUnidadeAdministrativa(),
            getDataOperacao()));
        inicializarListas();
        novoFiltroPesquisaBem();
        montarFiltroPesquisa();
    }

    public void inicializarListas() {
        bensSelecionados = new ArrayList<>();
        bensDisponiveis = new ArrayList<>();
    }

    private void inicializarSolicitacaoAlienacao() {
        inicializarListas();
        selecionado.setDataSolicitacao(getDataOperacao());
        selecionado.setResponsavel(facade.getBemFacade().getSistemaFacade().getUsuarioCorrente());
        selecionado.setTipoBem(TipoBem.MOVEIS);
    }

    public void pesquisarBensAoEditar() {
        if (isOperacaoEditar()) {
            pesquisaBens();
        }
    }

    private void popularListaBensSelecionados() {
        if (isOperacaoEditar()) {
            List<Bem> bensRecuperados = assistenteMovimentacao.getBensSalvos();
            if (bensRecuperados != null && !bensRecuperados.isEmpty()) {
                bensDisponiveis.addAll(bensRecuperados);
                bensSelecionados.addAll(bensRecuperados);
                Collections.sort(bensSelecionados);
            }
        }
    }

    public void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void acompanharPesquisa() throws ExecutionException, InterruptedException {
        if (futurePesquisaBens != null && futurePesquisaBens.isDone()) {
            bensDisponiveis.addAll(futurePesquisaBens.get());
            validarUnidadeControleSetorial();
            futurePesquisaBens = null;
            popularListaBensSelecionados();
            Collections.sort(bensDisponiveis);
            FacesUtil.executaJavaScript("terminarPesquisa()");
        }
    }

    private void validarUnidadeControleSetorial() {
        if (!facade.getParametroPatrimonioFacade().hasUnidadesControleSetorial(hierarquiaOrganizacionalAdm)) {
            FacesUtil.addAtencao("A Unidade Administrativa selecionada não possui nenhuma Unidade de Controle Setorial configurada nos Parâmetros do Patrimônio. " +
                " Sendo assim,  a pesquisa considerou os bens do controle setorial da unidade: " + hierarquiaOrganizacionalAdm + ".");
        }
    }

    public void finalizarPesquisa() {
        FacesUtil.atualizarComponente("Formulario");
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    public List<SelectItem> retornaHierarquiaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (hierarquiaOrganizacionalAdm != null && hierarquiaOrganizacionalAdm.getSubordinada() != null) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(
                hierarquiaOrganizacionalAdm.getSubordinada(),
                getDataOperacao())) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }
        return toReturn;
    }

    private void criarItensPaginacao() {
        filtroPesquisaBem = new FiltroPesquisaBem();
        model = new LazyDataModel<ItemSolicitacaoAlienacao>() {
            @Override
            public List<ItemSolicitacaoAlienacao> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                filtroPesquisaBem.setSelecionado(selecionado);
                filtroPesquisaBem.setPrimeiroRegistro(first);
                filtroPesquisaBem.setQuantidadeRegistro(pageSize);
                setRowCount(facade.quantidadeItens(filtroPesquisaBem));
                return facade.recuperarItemSolicitacaoAlienacao(filtroPesquisaBem);
            }
        };
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            validarItensSelecionados();
            novoAssistenteMovimentacao();
            bloquearMovimentacaoBens();
            assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
            assistenteMovimentacao.setDataAtual(facade.getSistemaFacade().getDataOperacao());
            assistenteMovimentacao.setExecutando(true);
            assistenteMovimentacao.setDescricaoProcesso("Salvando a solicitação de alienação de bens móveis...");
            futureSalvarAndConcluir = AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                () -> facade.salvarRetornando(selecionado, bensSelecionados, assistenteMovimentacao));
            facade.salvarRetornando(selecionado, bensSelecionados, assistenteMovimentacao);
            FacesUtil.executaJavaScript("acompanharSalvar()");
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
        } catch (MovimentacaoBemException ex) {
            if (!isOperacaoNovo()) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
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

    public void concluirSolicitacaoAlienacao() {
        try {
            novoAssistenteMovimentacao();
            validarDataLancamentoAndDataOperacaoBem();
            bloquearMovimentacaoBens();
            List<Number> idsItemSolicitacao = facade.buscarIdsItemSolicitacao(selecionado);
            assistenteMovimentacao.setTotal(idsItemSolicitacao.size());
            futureSalvarAndConcluir = AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                () -> facade.concluirSolicitacaoAlienacao(selecionado,assistenteMovimentacao,idsItemSolicitacao));
            facade.concluirSolicitacaoAlienacao(selecionado, assistenteMovimentacao, idsItemSolicitacao);
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (MovimentacaoBemException ex) {
            if (!isOperacaoNovo()) {
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
            validarHierarquiaAdministrativa();
            montarFiltroPesquisa();
            novoAssistenteMovimentacao();
            if (verificarBensBloqueadoSingletonAoPesquisar()) return;
            inicializarListas();
            futurePesquisaBens = AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                () -> facade.pesquisarBens(filtroPesquisaBem,assistenteMovimentacao));
             facade.pesquisarBens(filtroPesquisaBem, assistenteMovimentacao);
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
            facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(SolicitacaoAlienacao.class, selecionado.getUnidadeAdministrativa(), assistenteMovimentacao);
        }
        if (facade.getSingletonBloqueioPatrimonio().verificarBensBloqueadoSingletonAoPesquisar(assistenteMovimentacao)) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("Formulario");
            assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
        }
        if (isOperacaoNovo() && !assistenteMovimentacao.hasInconsistencias()) {
            facade.getConfigMovimentacaoBemFacade().verificarBensSelecionadosSeDisponivelConfiguracaoBloqueio(assistenteMovimentacao);
        }
        if (!isOperacaoVer()) {
            for (Bem bem : bensSelecionados) {
                facade.getSingletonBloqueioPatrimonio().bloquearBem(bem, assistenteMovimentacao);
            }
        }
        FacesUtil.executaJavaScript("aguarde.hide()");
        FacesUtil.atualizarComponente("Formulario");
        assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
    }

    private void desbloquearMovimentacaoSingleton() {
        facade.getSingletonBloqueioPatrimonio().desbloquearMovimentacaoSingleton(assistenteMovimentacao, SolicitacaoAlienacao.class);
    }

    public void acompanharAndamentoSalvar() {
        if (futureSalvarAndConcluir != null && futureSalvarAndConcluir.isDone()) {
            try {
                SolicitacaoAlienacao solicitacaoSalva = (SolicitacaoAlienacao) futureSalvarAndConcluir.get();
                if (solicitacaoSalva != null) {
                    selecionado = solicitacaoSalva;
                    desbloquearMovimentacaoSingleton();
                    FacesUtil.executaJavaScript("finalizarSalvar()");
                }
            } catch (Exception ex) {
                assistenteMovimentacao.setBloquearAcoesTela(true);
                FacesUtil.executaJavaScript("clearInterval(timerSalvar)");
                FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                FacesUtil.atualizarComponente("Formulario");
                futureSalvarAndConcluir = null;
                desbloquearMovimentacaoSingleton();
                assistenteMovimentacao.descobrirETratarException(ex);
            }
        }
    }

    public void finalizarSalvar() {
        futureSalvarAndConcluir = null;
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        redirecionarParaVer();
    }

    private void novoAssistenteMovimentacao() {
        if (configMovimentacaoBem == null) {
            recuperarConfiguracaoMovimentacaoBem();
        }
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataSolicitacao(), operacao);
        assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistenteMovimentacao.setSelecionado(selecionado);
        assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
        assistenteMovimentacao.setUnidadeOrganizacional(selecionado.getUnidadeAdministrativa());
        assistenteMovimentacao.setBensSelecionados(bensSelecionados);
    }

    private void validarHierarquiaAdministrativa() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacionalAdm == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarItensSelecionados() {
        ValidacaoException ve = new ValidacaoException();
        if (bensSelecionados.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione ao menos um bem para continuar a operação.");
        }
        validarDataLancamentoAndDataOperacaoBem();
        ve.lancarException();
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataSolicitacao(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public void gerarRelatorioConferencia() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + facade.getSistemaFacade().getMunicipio().toUpperCase());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE SOLICITAÇÃO DE ALIENAÇÃO DE BENS MÓVEIS");
            dto.adicionarParametro("idSolicitacaoAlienacao", selecionado.getId());
            dto.setNomeRelatorio("RELATÓRIO DE SOLICITAÇÃO DE ALIENAÇÃO DE BENS MÓVEIS");
            dto.setApi("administrativo/solicitacao-alienacao/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String formatarValor(BigDecimal valor) {
        return Util.formataValor(valor);
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

    public BigDecimal getValorTotalLiquidoBensSelecionados() {
        BigDecimal total = BigDecimal.ZERO;
        if (bensSelecionados != null) {
            for (Bem bemSelecionado : bensSelecionados) {
                total = total.add(bemSelecionado.getValorLiquido());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalOriginalItens() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            total = facade.getValorOriginalEstadoResultante(selecionado);
        }
        return total;
    }

    public BigDecimal getValorTotalAjusteItens() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            total = facade.getValorAjusteEstadoResultante(selecionado);
        }
        return total;
    }

    public BigDecimal getTotalVlLiquidoItens() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            total = total.add(getValorTotalOriginalItens().subtract(getValorTotalAjusteItens()));
        }
        return total;
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

    private void novoFiltroPesquisaBem() {
        filtroPesquisaBem = new FiltroPesquisaBem(selecionado.getTipoBem(), selecionado);
        filtroPesquisaBem.setDataReferencia(new Date());
        filtroPesquisaBem.setDataOperacao(new Date());
        filtroPesquisaBem.setEstadoConservacaoBem(EstadoConservacaoBem.INSERVIVEL);
    }

    private void montarFiltroPesquisa() {
        filtroPesquisaBem.setHierarquiaAdministrativa(getHierarquiaOrganizacionalAdm());
        List<Long> idsUnidades = facade.getParametroPatrimonioFacade().buscarUnidadeControleSetorial(hierarquiaOrganizacionalAdm);
        if (!idsUnidades.isEmpty()) {
            filtroPesquisaBem.getIdsUnidadesControleSetorial().addAll(idsUnidades);
        }
    }

    public LazyDataModel<ItemSolicitacaoAlienacao> getModel() {
        return model;
    }

    public void setModel(LazyDataModel<ItemSolicitacaoAlienacao> model) {
        this.model = model;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalAdm() {
        return hierarquiaOrganizacionalAdm;
    }

    public void setHierarquiaOrganizacionalAdm(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacionalAdm = hierarquiaOrganizacional;
        if (hierarquiaOrganizacionalAdm != null) {
            selecionado.setUnidadeAdministrativa(hierarquiaOrganizacionalAdm.getSubordinada());
        }
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

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteMovimentacao) {
        this.assistenteMovimentacao = assistenteMovimentacao;
    }

    public FiltroPesquisaBem getFiltroPesquisaBem() {
        return filtroPesquisaBem;
    }

    public void setFiltroPesquisaBem(FiltroPesquisaBem filtroPesquisaBem) {
        this.filtroPesquisaBem = filtroPesquisaBem;
    }
}
