package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LoteTransferenciaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean(name = "loteTransferenciaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLoteTransferenciaMovel", pattern = "/lote-de-transferencia-de-bens-moveis/novo/", viewId = "/faces/administrativo/patrimonio/solicitacaotransferencia/movel/edita.xhtml"),
    @URLMapping(id = "editarLoteTransferenciaMovel", pattern = "/lote-de-transferencia-de-bens-moveis/editar/#{loteTransferenciaControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaotransferencia/movel/edita.xhtml"),
    @URLMapping(id = "novaSolicitacaoTransfBemMovelRecusada", pattern = "/lote-de-transferencia-de-bens-moveis/novo/#{loteTransferenciaControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaotransferencia/movel/edita.xhtml"),
    @URLMapping(id = "listarLoteTransferenciaMovel", pattern = "/lote-de-transferencia-de-bens-moveis/listar/", viewId = "/faces/administrativo/patrimonio/solicitacaotransferencia/movel/lista.xhtml"),
    @URLMapping(id = "verLoteTransferenciaMovel", pattern = "/lote-de-transferencia-de-bens-moveis/ver/#{loteTransferenciaControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaotransferencia/movel/visualizar.xhtml"),

    @URLMapping(id = "novoLoteTransferenciaImovel", pattern = "/lote-de-transferencia-de-bens-imoveis/novo/", viewId = "/faces/administrativo/patrimonio/solicitacaotransferencia/imovel/edita.xhtml"),
    @URLMapping(id = "editarLoteTransferenciaImovel", pattern = "/lote-de-transferencia-de-bens-imoveis/editar/#{loteTransferenciaControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaotransferencia/imovel/edita.xhtml"),
    @URLMapping(id = "listarLoteTransferenciaImovel", pattern = "/lote-de-transferencia-de-bens-imoveis/listar/", viewId = "/faces/administrativo/patrimonio/solicitacaotransferencia/imovel/lista.xhtml"),
    @URLMapping(id = "verLoteTransferenciaImovel", pattern = "/lote-de-transferencia-de-bens-imoveis/ver/#{loteTransferenciaControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaotransferencia/imovel/visualizar.xhtml")
})
public class LoteTransferenciaControlador extends PrettyControlador<LoteTransferenciaBem> implements CRUD {

    @EJB
    private LoteTransferenciaFacade facade;
    private FiltroPesquisaBem filtroPesquisaBem;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrigem;
    private HierarquiaOrganizacional hierarquiaOrganizacionalDestino;
    private AssistenteMovimentacaoBens assistente;
    private Future<List<Bem>> futurePesquisaBem;
    private Future<LoteTransferenciaBem> futureTransfereBem;
    private List<Bem> bensDisponiveis;
    private List<Bem> bensSelecionados;
    private List<Bem> bensFiltradosFilterBy;
    private ConfigMovimentacaoBem configMovimentacaoBem;
    private List<HierarquiaOrganizacional> orcamentariasOrigem;
    private List<HierarquiaOrganizacional> orcamentariasDestino;
    private static String xhtmlNovoEditar;
    private String avisoTipoTranf;

    public LoteTransferenciaControlador() {
        super(LoteTransferenciaBem.class);
    }

    @Override
    public String getCaminhoPadrao() {
        switch (selecionado.getTipoBem()) {
            case IMOVEIS:
                return "/lote-de-transferencia-de-bens-imoveis/";
            case MOVEIS:
                return "/lote-de-transferencia-de-bens-moveis/";
            default:
                return "";
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

    @URLAction(mappingId = "novoLoteTransferenciaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaTransferenciaMovel() {
        try {
            novo();
            selecionado.setTipoBem(TipoBem.MOVEIS);
            inicializarAtributosOperacaoNovo();
            recuperarConfiguracaoMovimentacaoBem();
            xhtmlNovoEditar = "/administrativo/patrimonio/solicitacaotransferencia/movel/edita.xhtml";
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "novaSolicitacaoTransfBemMovelRecusada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaSolicitacaoTransfBemMovelRecusada() {
        try {
            novaSolicitacaoApartirRecusada();
            recuperarConfiguracaoMovimentacaoBem();
            xhtmlNovoEditar = "/administrativo/patrimonio/solicitacaotransferencia/movel/edita.xhtml";
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public List<HierarquiaOrganizacional> buscarUnidadesDestinoMesmaUnidadeResponsavel(String parte) {
        List<HierarquiaOrganizacional> hierarquiaOrganizacionalList = facade.buscarUnidadesPorSubordinada(parte, hierarquiaOrganizacionalOrigem.getSubordinada(), selecionado.getTipoTransferencia());
        return hierarquiaOrganizacionalList;
    }

    public void buscarUnidadesOrcMesmaUnidadeOrigem() {
        if (Util.isNotNull(selecionado.getUnidadeOrigem()) && hierarquiaOrganizacionalOrigem != null) {
            setOrcamentariasOrigem(facade.buscarUnidadesOrcamentaria(hierarquiaOrganizacionalOrigem.getSubordinada().getId()));
        }
    }

    public void buscarUnidadesOrcMesmaUnidadeDestino() {
        if (Util.isNotNull(selecionado.getUnidadeDestino())) {
            setOrcamentariasDestino(facade.buscarUnidadesOrcamentaria(hierarquiaOrganizacionalDestino.getSubordinada().getId()));
        }
    }

    @URLAction(mappingId = "novoLoteTransferenciaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaTransferenciaImovel() {
        novo();
        selecionado.setTipoBem(TipoBem.IMOVEIS);
        inicializarAtributosOperacaoNovo();
        xhtmlNovoEditar = "/administrativo/patrimonio/solicitacaotransferencia/imovel/edita.xhtml";
    }

    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verLoteTransferenciaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verTransferenciaMovel() {
        ver();
        buscarUnidadesOrcMesmaUnidadeOrigem();
        buscarUnidadesOrcMesmaUnidadeDestino();
        xhtmlNovoEditar = "/administrativo/patrimonio/solicitacaotransferencia/movel/edita.xhtml";
    }

    @URLAction(mappingId = "verLoteTransferenciaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verTransferenciaImovel() {
        ver();
        xhtmlNovoEditar = "/administrativo/patrimonio/solicitacaotransferencia/imovel/edita.xhtml";
    }

    @URLAction(mappingId = "editarLoteTransferenciaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarTransferenciaMovel() {
        editar();
        if (SituacaoDaSolicitacao.RECUSADA.equals(selecionado.getSituacaoTransferenciaBem())) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoNaoPermitida("Não é permitido editar uma solicitação de transferência recusada.");
        }
        xhtmlNovoEditar = "/administrativo/patrimonio/solicitacaotransferencia/movel/edita.xhtml";
    }

    @URLAction(mappingId = "editarLoteTransferenciaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarTransferenciaImovel() {
        editar();
        xhtmlNovoEditar = "/administrativo/patrimonio/solicitacaotransferencia/imovel/edita.xhtml";
    }

    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        inicializarAtributosOperacaoEdita();
        recuperarAtributos();
        bensDisponiveis = facade.buscarBensVinculadosTransferencia(selecionado, null);
        bensSelecionados = bensDisponiveis;
    }

    @Override
    public void editar() {
        super.editar();
        inicializarAtributosOperacaoEdita();
        recuperarAtributos();
        buscarUnidadesOrcMesmaUnidadeOrigem();
        buscarUnidadesOrcMesmaUnidadeDestino();
    }

    public List<SelectItem> getTiposTransferencia() {
        return Util.getListSelectItem(TipoTransferenciaUnidadeBem.values());
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataHoraCriacao(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataHoraCriacao(), OperacaoMovimentacaoBem.SOLICITACAO_TRANSFERENCIA_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    private void novaSolicitacaoApartirRecusada() {
        LoteTransferenciaBem solicitacaoRecusada = facade.recuperarSemDependencias(super.getId());
        selecionado = new LoteTransferenciaBem();
        selecionado.setUnidadeOrigem(solicitacaoRecusada.getUnidadeOrigem());
        selecionado.setResponsavelOrigem(solicitacaoRecusada.getResponsavelOrigem());
        hierarquiaOrganizacionalOrigem = recuperarHierarquiaDaUnidade(selecionado.getUnidadeOrigem());
        selecionado.setUnidadeDestino(solicitacaoRecusada.getUnidadeDestino());
        selecionado.setResponsavelDestino(solicitacaoRecusada.getResponsavelDestino());
        hierarquiaOrganizacionalDestino = recuperarHierarquiaDaUnidade(selecionado.getUnidadeDestino());
        selecionado.setTipoBem(solicitacaoRecusada.getTipoBem());
        selecionado.setDescricao(solicitacaoRecusada.getDescricao());
        selecionado.setDetentorArquivoComposicao(solicitacaoRecusada.getDetentorArquivoComposicao());
        selecionado.setMotivoRecusa(null);
        selecionado.setId(null);
        selecionado.setTipoTransferencia(solicitacaoRecusada.getTipoTransferencia());
        selecionado.setSituacaoTransferenciaBem(SituacaoDaSolicitacao.EM_ELABORACAO);
        selecionado.setTransferencias(new ArrayList<TransferenciaBem>());
        inicializarAtributosOperacaoNovo();
        iniciarListas();
        novoFiltroPesquisaBem();
        filtroPesquisaBem.setHierarquiaAdministrativa(hierarquiaOrganizacionalOrigem);
    }

    private void inicializarAtributosOperacaoNovo() {
        selecionado.setSituacaoTransferenciaBem(SituacaoDaSolicitacao.EM_ELABORACAO);
        selecionado.setDataHoraCriacao(getDataOperacao());
        bensDisponiveis = Lists.newArrayList();
        bensSelecionados = Lists.newArrayList();
        inicializarAtributosOperacaoEdita();
    }

    private void inicializarAtributosOperacaoEdita() {
        novoFiltroPesquisaBem();
    }


    public void buscarBensAoEditar() {
        if (isOperacaoEditar()) {
            novoFiltroPesquisaBem();
            filtroPesquisaBem.setHierarquiaAdministrativa(getHierarquiaOrganizacionalOrigem());
            pesquisar();
        }
    }

    public boolean hasBemSelecionado() {
        return bensSelecionados != null && !bensSelecionados.isEmpty();
    }

    private void novoFiltroPesquisaBem() {
        filtroPesquisaBem = new FiltroPesquisaBem(selecionado.getTipoBem(), selecionado);
        filtroPesquisaBem.setDataOperacao(getDataOperacao());
    }


    @Override
    public void salvar() {
        try {
            novoAssistenteMovimentacao();
            validarSalvar();
            bloquearMovimentacaoBens();
            if (selecionado.isEmElaboracao()) {
                selecionado = facade.salvarSolicitacao(selecionado, configMovimentacaoBem);
                criarTransferencias();
            } else {
                selecionado = facade.salvarRetornando(selecionado);
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                redireciona();
            }
        } catch (MovimentacaoBemException ex) {
            if (!isOperacaoNovo()) {
                redirecionarAposSalvar();
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
            FacesUtil.executaJavaScript("aguarde.hide()");
            desbloquearMovimentacaoSingleton();
            assistente.descobrirETratarException(e);
        }
    }

    public void concluirTransferencia() {
        try {
            novoAssistenteMovimentacao();
            verificarConcluir();
            bloquearMovimentacaoBens();
            futureTransfereBem = facade.concluirTransferencia(selecionado, assistente);
            FacesUtil.executaJavaScript("iniciaTransferencia()");
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

    private void desbloquearMovimentacaoSingleton() {
        facade.getSingletonBloqueioPatrimonio().desbloquearMovimentacaoSingleton(assistente, LoteTransferenciaBem.class);
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

    public void pesquisar() {
        try {
            Util.validarCampos(selecionado);
            novoAssistenteMovimentacao();
            validarDataLancamentoAndDataOperacaoBem();
            if (verificarBensBloqueadoSingletonAoPesquisar()) return;
            iniciarListas();
            futurePesquisaBem = facade.pesquisarBem(filtroPesquisaBem, assistente);
            FacesUtil.executaJavaScript("iniciaPesquisa();");
        } catch (MovimentacaoBemException me) {
            FacesUtil.printAllFacesMessages(me.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private boolean verificarBensBloqueadoSingletonAoPesquisar() {
        if (facade.getSingletonBloqueioPatrimonio().verificarBensBloqueadoSingletonAoPesquisar(assistente)) {
            if (isOperacaoNovo()) {
                FacesUtil.executaJavaScript("aguarde.hide()");
                FacesUtil.atualizarComponente("Formulario");
                assistente.lancarMensagemBensBloqueioSingleton();
            } else {
                FacesUtil.addOperacaoNaoPermitida(assistente.getMensagens().get(0));
                redireciona();
            }
        }
        return false;
    }

    private void bloquearMovimentacaoBens() {
        if (isOperacaoVer()) {
            facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(LoteTransferenciaBem.class, selecionado.getUnidadeOrigem(), assistente);
        }
        if (facade.getSingletonBloqueioPatrimonio().verificarBensBloqueadoSingletonAoPesquisar(assistente)) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("Formulario");
            assistente.lancarMensagemBensBloqueioSingleton();
        }
        if (isOperacaoNovo() && !assistente.hasInconsistencias()) {
            facade.getConfigMovimentacaoBemFacade().verificarBensSelecionadosSeDisponivelConfiguracaoBloqueio(assistente);
        }
        if (!isOperacaoVer()) {
            for (Bem bem : bensSelecionados) {
                facade.getSingletonBloqueioPatrimonio().bloquearBem(bem, assistente);
            }
        }
        FacesUtil.executaJavaScript("aguarde.hide()");
        FacesUtil.atualizarComponente("Formulario");
        assistente.lancarMensagemBensBloqueioSingleton();
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (!hasBemSelecionado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe ao menos um bem para ser transferido.");
        }
        validarUnidadeOrigemDiferenteDestino(ve);
        validarEntidadeSequenciaRegistroPatrimonial(ve);
        validarDataLancamentoAndDataOperacaoBem();

        if (selecionado.isEmElaboracao()) {
            validarFase(ve, bensSelecionados);
        }
        ve.lancarException();
    }

    private void validarFase(ValidacaoException ve, List<Bem> bens) {
        Set<UnidadeOrganizacional> unidades = Sets.newHashSet();
        for (Bem bem : bens) {
            unidades.add(bem.getUltimoEstado().getDetentoraOrcamentaria());
        }
        for (UnidadeOrganizacional unidade : unidades) {
            Exercicio exercicio = facade.getExercicioFacade().getExercicioPorAno(DataUtil.getAno(selecionado.getDataHoraCriacao()));
            if (facade.getFaseFacade().temBloqueioFaseParaRecurso(xhtmlNovoEditar, selecionado.getDataHoraCriacao(), unidade, exercicio)) {
                HierarquiaOrganizacional hierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataHoraCriacao(), unidade, TipoHierarquiaOrganizacional.ORCAMENTARIA);
                ve.adicionarMensagemDeOperacaoNaoRealizada("A data " + DataUtil.getDataFormatada(selecionado.getDataHoraCriacao()) + " está fora do período fase para a unidade " + hierarquiaOrganizacional.toString() + "!");
                break;
            }
        }
    }

    private void validarUnidadeOrigemDiferenteDestino(ValidacaoException ve) {
        if (selecionado.getUnidadeDestino() != null && selecionado.getUnidadeOrigem() != null) {
            if (selecionado.getUnidadeDestino().equals(selecionado.getUnidadeOrigem())) {
                List<HierarquiaOrganizacional> hierarquiasOrcamentaria = facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(selecionado.getUnidadeDestino(), getDataOperacao());
                if (hierarquiasOrcamentaria != null && hierarquiasOrcamentaria.size() <= 1) {
                    ve.adicionarMensagemDeCampoObrigatorio("A unidade de destino deve ser diferente da unidade de origem.");
                }
            }
        }
    }

    private void validarEntidadeSequenciaRegistroPatrimonial(ValidacaoException ve) {
        if (selecionado.getTipoBem().isMovel()) {
            Entidade entidadeOrigem = facade.getEntidadeFacade().recuperarEntidadePorUnidadeOrganizacional(hierarquiaOrganizacionalOrigem.getSubordinada());
            Entidade entidadeDestino = facade.getEntidadeFacade().recuperarEntidadePorUnidadeOrganizacional(hierarquiaOrganizacionalDestino.getSubordinada());
            if (!entidadeOrigem.equals(entidadeDestino)) {
                EntidadeSequenciaPropria origem = facade.getParametroPatrimonioFacade().recuperarSequenciaPropriaPorTipoGeracao(entidadeOrigem, selecionado.getTipoBem());
                EntidadeSequenciaPropria destino = facade.getParametroPatrimonioFacade().recuperarSequenciaPropriaPorTipoGeracao(entidadeDestino, selecionado.getTipoBem());
                if (origem == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("A entidade " + entidadeOrigem.getNome() + " da unidade de origem não possui uma Sequência de Geração do Código de Identificação Móvel no parâmetro");
                }
                if (destino == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("A entidade " + entidadeDestino.getNome() + " da unidade de destino não possui uma Sequência de Geração do Código de Identificação Móvel no parâmetro");
                }
                if (origem != null && destino != null && !origem.equals(destino)) {
                    ve.adicionarMensagemDeCampoObrigatorio("Não é permitida a transferência de bens móveis entre unidades que não seguem a mesma Sequência de Geração do Código de Identificação Móvel no parâmetro.]");
                }
            }
        }
    }

    private void verificarConcluir() {
        validarDataLancamentoAndDataOperacaoBem();
        ValidacaoException ve = new ValidacaoException();
        validarFase(ve, bensDisponiveis);
        ve.lancarException();
    }

    private void criarTransferencias() {
        assistente.setTotal(bensSelecionados.size());
        futureTransfereBem = facade.criarTransferenciaBem(bensSelecionados, selecionado, assistente);
        FacesUtil.executaJavaScript("iniciaTransferencia()");
    }

    public void consultarAndamentoTransferencia() {
        try {
            if (futureTransfereBem != null && futureTransfereBem.isDone()) {
                selecionado = futureTransfereBem.get();
                if (selecionado != null) {
                    FacesUtil.executaJavaScript("encerrarTransferencia()");
                }
                futurePesquisaBem = null;
            }
        } catch (Exception ex) {
            assistente.setBloquearAcoesTela(true);
            FacesUtil.executaJavaScript("clearInterval(timer)");
            FacesUtil.executaJavaScript("closeDialog(dlgPesquisa)");
            FacesUtil.atualizarComponente("Formulario");
            futureTransfereBem = null;
            desbloquearMovimentacaoSingleton();
            assistente.descobrirETratarException(ex);
        }
    }

    public void redirecionarAposSalvar() {
        desbloquearMovimentacaoSingleton();
        FacesUtil.addOperacaoRealizada("Solicitação de transferência de bens salva com sucesso.");
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrigem() {
        return hierarquiaOrganizacionalOrigem;
    }

    public void setHierarquiaOrganizacionalOrigem(HierarquiaOrganizacional hierarquiaOrganizacionalOrigem) {
        this.hierarquiaOrganizacionalOrigem = hierarquiaOrganizacionalOrigem;
        if (hierarquiaOrganizacionalOrigem != null) {
            selecionado.setUnidadeOrigem(hierarquiaOrganizacionalOrigem.getSubordinada());
            filtroPesquisaBem.setHierarquiaAdministrativa(hierarquiaOrganizacionalOrigem);
            try {
                selecionado.setResponsavelOrigem(facade.getParametroPatrimonioFacade().recuperarResponsavelVigente(hierarquiaOrganizacionalOrigem.getSubordinada(), getDataOperacao()).getResponsavel());
            } catch (ExcecaoNegocioGenerica ex) {
                selecionado.setResponsavelOrigem(null);
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalDestino() {
        return hierarquiaOrganizacionalDestino;
    }

    public void setHierarquiaOrganizacionalDestino(HierarquiaOrganizacional hierarquiaOrganizacionalDestino) {
        this.hierarquiaOrganizacionalDestino = hierarquiaOrganizacionalDestino;
        if (hierarquiaOrganizacionalDestino != null) {
            selecionado.setUnidadeDestino(hierarquiaOrganizacionalDestino.getSubordinada());
            try {
                selecionado.setResponsavelDestino(facade.getParametroPatrimonioFacade().recuperarResponsavelVigente(hierarquiaOrganizacionalDestino.getSubordinada(), getDataOperacao()).getResponsavel());
            } catch (ExcecaoNegocioGenerica ex) {
                selecionado.setResponsavelDestino(null);
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
    }

    public Boolean mostrarBotaoExcluir() {
        return selecionado.isEmElaboracao() || selecionado.isAguardandoAprovacao();
    }

    public Boolean mostrarBotaoEditarSolicitacaoBemMovelEmElaboracao() {
        return selecionado.isEmElaboracao();
    }

    public Boolean mostrarBotaoEditar() {
        return selecionado.isEmElaboracao() || selecionado.isRecusada();
    }

    public Boolean mostrarBotaoConcluir() {
        return selecionado.isEmElaboracao();
    }


    public String formatarValor(BigDecimal valor) {
        return Util.formataValor(valor);
    }

    public List<SelectItem> retornaHierarquiaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (hierarquiaOrganizacionalOrigem != null && hierarquiaOrganizacionalOrigem.getSubordinada() != null) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hierarquiaOrganizacionalOrigem.getSubordinada(), getDataOperacao())) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getHierarquiasOrcamentariasPesquisaBem() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (!Util.isListNullOrEmpty(orcamentariasOrigem)) {
            for (HierarquiaOrganizacional obj : orcamentariasOrigem) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }
        return toReturn;
    }

    private void novoAssistenteMovimentacao() {
        if (configMovimentacaoBem == null) {
            recuperarConfiguracaoMovimentacaoBem();
        }
        assistente = new AssistenteMovimentacaoBens(selecionado.getDataHoraCriacao(), operacao);
        assistente.zerarContadoresProcesso();
        assistente.setSelecionado(selecionado);
        assistente.setConfigMovimentacaoBem(configMovimentacaoBem);
        assistente.setUnidadeOrganizacional(selecionado.getUnidadeOrigem());
        assistente.setBensSelecionados(bensSelecionados);
    }

    private void recuperarAtributos() {
        setHierarquiaOrganizacionalOrigem(recuperarHierarquiaDaUnidade(selecionado.getUnidadeOrigem()));
        setHierarquiaOrganizacionalDestino(recuperarHierarquiaDaUnidade(selecionado.getUnidadeDestino()));
        iniciarListas();
        if (!isOperacaoVer()) {
            novoAssistenteMovimentacao();
        }
    }

    private HierarquiaOrganizacional recuperarHierarquiaDaUnidade(UnidadeOrganizacional unidade) {
        return facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(getDataOperacao(), unidade, TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    private void iniciarListas() {
        bensSelecionados = Lists.newArrayList();
        bensDisponiveis = Lists.newArrayList();
    }

    public void consultarAndamentoPesquisa() {
        if (futurePesquisaBem != null && futurePesquisaBem.isDone()) {
            FacesUtil.executaJavaScript("encerrarPesquisa()");
        }
    }

    public void encerrarPesquisa() throws ExecutionException, InterruptedException {
        bensDisponiveis = futurePesquisaBem.get();
        if (isOperacaoEditar()) {
            List<Bem> bensSalvos = assistente.getBensSalvos();
            bensSelecionados.addAll(bensSalvos);
            bensDisponiveis.addAll(bensSalvos);
        }
        Collections.sort(bensDisponiveis);
        Collections.sort(bensSelecionados);
        FacesUtil.atualizarComponente("Formulario:tabViewPrincipal:tableencontrados");
        FacesUtil.atualizarComponente("Formulario:tabViewPrincipal:panelAnexos");
        FacesUtil.atualizarComponente("Formulario:alertInconsistencias");
        FacesUtil.atualizarComponente("Formulario:tabViewPrincipal:outputPanelInconsistencias");
    }

    public BigDecimal valorTotalDosAjustes() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (bensDisponiveis != null && !bensDisponiveis.isEmpty()) {
            for (Bem bem : bensDisponiveis) {
                valorTotal = valorTotal.add(bem.getValorDosAjustes());
            }
        }
        return valorTotal;
    }

    public BigDecimal valorTotalDosBens() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (bensDisponiveis != null && !bensDisponiveis.isEmpty()) {
            for (Bem bem : bensDisponiveis) {
                valorTotal = valorTotal.add(bem.getValorOriginal());
            }
        }
        return valorTotal;
    }

    public void selecionarTodos() {
        desmarcarTodos();
        if (bensFiltradosFilterBy != null) {
            bensSelecionados.addAll(bensFiltradosFilterBy);
        } else {
            bensSelecionados.addAll(bensDisponiveis);
        }
    }

    public void desmarcarTodos() {
        if (bensFiltradosFilterBy != null) {
            bensSelecionados.removeAll(bensFiltradosFilterBy);
        } else {
            bensSelecionados = Lists.newArrayList();
        }
    }

    public Boolean mostrarBotaoSelecionarTodos() {
        try {
            if (bensFiltradosFilterBy != null) {
                for (Bem bem : bensFiltradosFilterBy) {
                    if (!bensSelecionados.contains(bem)) {
                        return true;
                    }
                }
                return false;
            }
            return bensDisponiveis.size() != bensSelecionados.size();
        } catch (NullPointerException ex) {
            return Boolean.FALSE;
        }
    }

    public Boolean mostrarBotaoSelecionar(Bem b) {
        for (Bem bem : bensSelecionados) {
            if (bem.getId().equals(b.getId())) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public void desmarcar(Bem bem) {
        bensSelecionados.remove(bem);
    }

    public void selecionar(Bem bem) {
        bensSelecionados.add(bem);
    }

    public void gerarRelatorio(String tipoExtensaoRelatorio) {
        try {
            if (selecionado != null) {
                Entidade entidade = facade.getEntidadeFacade().recuperarEntidadePorUnidadeOrganizacional(selecionado.getUnidadeOrigem());
                String nomeRelatorio = "RELATÓRIO DE SOLICITAÇÃO DE TRANSFERÊNCIA DE BENS " + selecionado.getTipoBem().getDescricao().toUpperCase();
                RelatorioDTO dto = new RelatorioDTO();
                dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensaoRelatorio));
                dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
                dto.setNomeParametroBrasao("BRASAO");
                dto.adicionarParametro("MODULO", "Patrimônio");
                dto.adicionarParametro("ENTIDADE", entidade != null ? entidade.getNome().toUpperCase() : selecionado.getUnidadeOrigem().getDescricao().toUpperCase());
                dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
                dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
                dto.adicionarParametro("DATA_OPERACAO", facade.getSistemaFacade().getDataOperacao());
                dto.adicionarParametro("CONDICAO", " WHERE SOLICITACAO.ID = " + selecionado.getId());
                dto.setNomeRelatorio(nomeRelatorio);
                dto.setApi("administrativo/lote-transferencia/");
                ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
                FacesUtil.addMensagemRelatorioSegundoPlano();
            }
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public AssistenteMovimentacaoBens getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteMovimentacaoBens assistente) {
        this.assistente = assistente;
    }

    public List<Bem> getBensDisponiveis() {
        return bensDisponiveis;
    }


    public FiltroPesquisaBem getFiltroPesquisaBem() {
        return filtroPesquisaBem;
    }

    public void setFiltroPesquisaBem(FiltroPesquisaBem filtroPesquisaBem) {
        this.filtroPesquisaBem = filtroPesquisaBem;
    }

    public void setBensDisponiveis(List<Bem> bensDisponiveis) {
        this.bensDisponiveis = bensDisponiveis;
    }

    public List<Bem> getBensFiltradosFilterBy() {
        return bensFiltradosFilterBy;
    }

    public void setBensFiltradosFilterBy(List<Bem> bensFiltradosFilterBy) {
        this.bensFiltradosFilterBy = bensFiltradosFilterBy;
    }

    public String getAvisoTipoTranf() {
        return avisoTipoTranf;
    }

    public void setAvisoTipoTranf(String avisoTipoTranf) {
        this.avisoTipoTranf = avisoTipoTranf;
    }

    public List<HierarquiaOrganizacional> getOrcamentariasOrigem() {
        return orcamentariasOrigem;
    }

    public void setOrcamentariasOrigem(List<HierarquiaOrganizacional> orcamentariasOrigem) {
        this.orcamentariasOrigem = orcamentariasOrigem;
    }

    public List<HierarquiaOrganizacional> getOrcamentariasDestino() {
        return orcamentariasDestino;
    }

    public void setOrcamentariasDestino(List<HierarquiaOrganizacional> orcamentariasDestino) {
        this.orcamentariasDestino = orcamentariasDestino;
    }

    public void limparCamposUnidadeDestino() {
        setHierarquiaOrganizacionalDestino(null);
        setOrcamentariasDestino(null);
        selecionado.setResponsavelDestino(null);
    }

    public void limparCamposAoTrocarTipoTransferencia() {
        limparCamposUnidadeDestino();
        filtroPesquisaBem.setHierarquiaAdministrativa(null);
        filtroPesquisaBem.setHierarquiaOrcamentaria(null);
        filtroPesquisaBem.setHierarquiasOrcamentarias(null);
        filtroPesquisaBem.setGrupoBem(null);
        filtroPesquisaBem.setBem(null);
        setBensDisponiveis(null);
        if (assistente != null) {
            assistente.setMensagens(null);
        }
    }
}
