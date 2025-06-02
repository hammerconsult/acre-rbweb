package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.entidadesauxiliares.contabil.LiquidacaoVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.SituacaoDocumentoFiscalEntradaMaterial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.administrativo.EmpenhoDocumentoFiscalDTO;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaEntradaCompra", pattern = "/entrada-por-compra/novo/", viewId = "/faces/administrativo/materiais/entradamaterial/compra/edita.xhtml"),
    @URLMapping(id = "editarEntradaCompra", pattern = "/entrada-por-compra/editar/#{entradaMaterialCompraControlador.id}/", viewId = "/faces/administrativo/materiais/entradamaterial/compra/edita.xhtml"),
    @URLMapping(id = "listarEntradaCompra", pattern = "/entrada-por-compra/listar/", viewId = "/faces/administrativo/materiais/entradamaterial/compra/lista.xhtml"),
    @URLMapping(id = "verEntradaCompra", pattern = "/entrada-por-compra/ver/#{entradaMaterialCompraControlador.id}/", viewId = "/faces/administrativo/materiais/entradamaterial/compra/visualizar.xhtml")
})
public class EntradaMaterialCompraControlador extends EntradaMaterialControlador {

    private static final Logger logger = LoggerFactory.getLogger(EntradaMaterialCompraControlador.class);
    private DoctoFiscalEntradaCompra doctoFiscalEntradaCompra;
    private CardapioRequisicaoCompra cardapioRequisicaoCompra;
    private MaterialVO materialVO;
    private List<GuiaDistribuicaoVO> guiasDistribuicaoVo;
    private Map<ItemEntradaMaterial, List<MaterialVO>> mapMateriais;
    private List<String> mensagemDocumentosDuplicados;
    private List<LiquidacaoVO> liquidacoesVO;
    private List<SolicitacaoEstornoEntradaMaterial> solicitacoesEstornoEntrada;
    private List<ItemEntradaReservaEstoqueVO> itensReservaEstoqueVO;
    private FiltroEmpenhoDocumentoFiscal filtroEmpenhoDocumentoFiscal;

    public EntradaMaterialCompraControlador() {
        metadata = new EntidadeMetaData(EntradaCompraMaterial.class);
    }

    @Override
    @URLAction(mappingId = "novaEntradaCompra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setSituacao(SituacaoEntradaMaterial.EM_ELABORACAO);
        guiasDistribuicaoVo = Lists.newArrayList();
        selecionado.setTipoIngresso(facade.getTipoIngressoFacade().recuperarTipoIngresso(TipoBem.ESTOQUE, TipoIngressoBaixa.COMPRA));
    }

    @Override
    @URLAction(mappingId = "editarEntradaCompra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        verificarSessao();
        if (!permiteEdicao()) {
            FacesUtil.addOperacaoNaoPermitida("Não é permitido editar uma entrada de material por compra com atesto definitivo ou que já teve documento liquidado.");
            redirecionaParaVer();
        }
        preencherItensEntrada();
        liquidacoesVO = Lists.newArrayList();
    }

    @URLAction(mappingId = "verEntradaCompra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        buscarUnidadesOrcamentariasEmpenho();
        recuperarCardapioRequisicaoCompra();
        getSelecionado().setRequisicaoDeCompra(facade.getRequisicaoDeCompraFacade().recuperarComDependenciasRequisicaoExecucao(getSelecionado().getRequisicaoDeCompra().getId()));
        if (isIntegracaoCardapio()) {
            SituacaoRequisicaoMaterial situacao = selecionado.getSituacao().isAtestoDefinitivo() ? SituacaoRequisicaoMaterial.TRANSFERENCIA_TOTAL_CONCLUIDA : SituacaoRequisicaoMaterial.NAO_AVALIADA;
            guiasDistribuicaoVo = facade.getCardapioRequisicaoCompraFacade().buscarGuiaDistribuicaoVO(cardapioRequisicaoCompra, situacao);
        }
        liquidacoesVO = Lists.newArrayList();
        buscarSolicitacaoEstornoEntrada();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/entrada-por-compra/";
    }

    @Override
    public EntradaCompraMaterial getSelecionado() {
        return (EntradaCompraMaterial) selecionado;
    }

    @Override
    public void salvar() {
        try {
            validarRegrasDoNegocio();
            definirSituacaoEntradaPorCompra();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionaParaVer();
        } catch (ValidacaoException vex) {
            FacesUtil.printAllFacesMessages(vex.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void definirSituacaoEntradaPorCompra() {
        selecionado.setSituacao(SituacaoEntradaMaterial.ATESTO_PROVISORIO_AGUARDANDO_LIQUIDACAO);
        Optional<ItemEntradaMaterial> any = selecionado.getItens()
            .stream()
            .filter(mat -> mat.getMaterial().getStatusMaterial().isAguardando() || mat.getMaterial().getStatusMaterial().isIndeferido())
            .findAny();
        if (any.isPresent()) {
            selecionado.setSituacao(SituacaoEntradaMaterial.ATESTO_PROVISORIO_COM_PENDENCIA);
        }
    }

    public boolean hasSolicitacaoEstornoEntradaGerada() {
        return !Util.isListNullOrEmpty(solicitacoesEstornoEntrada);
    }

    public void gerarSolicitacaoAndReservaEstorno() {
        try {
            inicializarListasSolicitacaoEstornoEntrada();
            Set<Liquidacao> liquidacoes = Sets.newHashSet();

            getSelecionado().getDocumentosFiscais().forEach(doc -> liquidacoes.addAll(facade.getLiquidacaoFacade().buscarLiquidacaoComSaldoPorDocumentoFiscal(doc.getDoctoFiscalLiquidacao())));

            liquidacoes.forEach(liquidacao -> {
                if (liquidacao.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                    SolicitacaoLiquidacaoEstorno novaSolEstLiq = new SolicitacaoLiquidacaoEstorno();
                    novaSolEstLiq.setLiquidacao(liquidacao);
                    novaSolEstLiq.setDataSolicitacao(new Date());
                    novaSolEstLiq.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
                    novaSolEstLiq.setHistorico("Solicitação gerada através da entrada por compra " + selecionado + ".");
                    novaSolEstLiq.setValor(liquidacao.getSaldo());

                    SolicitacaoEstornoEntradaMaterial novaSolEstEnt = new SolicitacaoEstornoEntradaMaterial();
                    novaSolEstEnt.setEntradaCompraMaterial(getSelecionado());
                    novaSolEstEnt.setSolicitacaoLiquidacaoEst(novaSolEstLiq);
                    solicitacoesEstornoEntrada.add(novaSolEstEnt);
                }
            });

            validarGeracaoSolicitacaoEstornoEntrada();
            HashSet<Material> materiaisSet = new HashSet<>();
            HashSet<LocalEstoque> locaislSet = new HashSet<>();
            selecionado.getItens().forEach(item -> {
                materiaisSet.add(item.getMaterial());
                locaislSet.add(item.getLocalEstoque());
            });

            List<ConsolidadorDeEstoque> consolidadoresEstoque = Lists.newArrayList();
            locaislSet.forEach(local -> consolidadoresEstoque.add(facade.getLocalEstoqueFacade().getNovoConsolidadorComReservaEstoque(local, materiaisSet, facade.getSistemaFacade().getDataOperacao())));
            selecionado.getItens().forEach(itemEnt -> {
                ItemEntradaReservaEstoqueVO novoItemRes = new ItemEntradaReservaEstoqueVO();
                novoItemRes.setItemEntradaMaterial(itemEnt);
                ReservaEstoque novaReserva = new ReservaEstoque(itemEnt.getLocalEstoque(), itemEnt);
                novoItemRes.setReservaEstoque(novaReserva);

                for (ConsolidadorDeEstoque cons : consolidadoresEstoque) {
                    novoItemRes.setQuantidadeEstoque(cons.getFisicoConsolidado(itemEnt.getMaterial()));
                }
                itensReservaEstoqueVO.add(novoItemRes);
            });
            FacesUtil.executaJavaScript("dlgSolEst.show()");
            FacesUtil.atualizarComponente("form-sol-est");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarGeracaoSolicitacaoEstornoEntrada() {
        ValidacaoException ve = new ValidacaoException();
        if (!hasSolicitacaoEstornoEntradaGerada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Liquidação não possui saldo para gerar a solicitação de estorno da entrada.");
        }
        ve.lancarException();

        BigDecimal valorTotalSolEst = BigDecimal.ZERO;
        for (SolicitacaoEstornoEntradaMaterial sol : solicitacoesEstornoEntrada) {
            valorTotalSolEst = valorTotalSolEst.add(sol.getSolicitacaoLiquidacaoEst().getValor());
        }
        BigDecimal valorTotalEntrada = selecionado.getValorTotalEntrada();
        if (valorTotalSolEst.compareTo(valorTotalEntrada) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total das solicitações de estorno deve ser igual ao valor total da entrada.  Não é permitido estorno parcial.");
        }
        ve.lancarException();
    }

    public void removerSolicitacaoEstornoEntrada() {
        try {
            validarRemoverSolicitacaoEstorno();
            facade.removerSolicitacaoEstornoEntrada(solicitacoesEstornoEntrada, itensReservaEstoqueVO);
            inicializarListasSolicitacaoEstornoEntrada();
            FacesUtil.addOperacaoRealizada("Solicitação de estorno removida com sucesso.");
            redirecionaParaVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarRemoverSolicitacaoEstorno() {
        ValidacaoException ve = new ValidacaoException();
        if (hasSolicitacaoEstornoEntradaGerada()
            && solicitacoesEstornoEntrada.stream().anyMatch(sol -> sol.getSolicitacaoLiquidacaoEst().hasLiquidacaoEstornada())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido remover solicitações de estorno com liquidação já estornada.");
        }
        ve.lancarException();
    }


    private void inicializarListasSolicitacaoEstornoEntrada() {
        solicitacoesEstornoEntrada = Lists.newArrayList();
        itensReservaEstoqueVO = Lists.newArrayList();
    }

    public void confirmarSolicitacaoEstornoEntrada() {
        try {
            facade.salvarSolicitacaoEstorno(solicitacoesEstornoEntrada, itensReservaEstoqueVO);
            FacesUtil.addOperacaoRealizada("Solicitação de estorno gerada com sucesso.");
            redirecionaParaVer();
        } catch (ValidacaoException vex) {
            FacesUtil.printAllFacesMessages(vex.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void cancelarSolicitacaoEstornoEntrada() {
        inicializarListasSolicitacaoEstornoEntrada();
        FacesUtil.executaJavaScript("dlgSolEst.hide()");
    }

    public boolean hasEstoqueNegativoSolicitacaoEstorno() {
        return !Util.isListNullOrEmpty(itensReservaEstoqueVO)
            && itensReservaEstoqueVO.stream().anyMatch(ItemEntradaReservaEstoqueVO::isEstoqueNegativo);
    }

    private void buscarSolicitacaoEstornoEntrada() {
        solicitacoesEstornoEntrada = facade.buscarSolicitacaoEstornoEntrada(getSelecionado());
        if (hasSolicitacaoEstornoEntradaGerada()) {
            itensReservaEstoqueVO = Lists.newArrayList();
            for (ItemEntradaMaterial item : selecionado.getItens()) {
                ItemEntradaReservaEstoqueVO novoItemRes = new ItemEntradaReservaEstoqueVO();
                novoItemRes.setItemEntradaMaterial(item);
                novoItemRes.setReservaEstoque(facade.buscarReservaEstoqueSolicitacaoEstornoEntrada(item));
                itensReservaEstoqueVO.add(novoItemRes);
            }
        }
    }

    private void verificarSessao() {
        if (isSessao()) {
            try {
                Web.pegaTodosFieldsNaSessao(this);
                Web.pegaTodosFieldsNaSessao(selecionado);
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public List<ItemDoctoItemEntrada> getItensDoctoItemEntrada() {
        return getSelecionado().getDocumentosFiscais()
            .stream()
            .flatMap(doc -> doc.getItens()
                .stream()).filter(itemDoc -> itemEntradaMaterial.equals(itemDoc.getItemEntradaMaterial()))
            .collect(Collectors.toList());
    }

    public BigDecimal getQuantidadeTotalItemDoc() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (ItemDoctoItemEntrada item : getItensDoctoItemEntrada()) {
                total = total.add(item.getQuantidade());
            }
            return total;
        } catch (Exception ex) {
            return total;
        }
    }

    public void buscarUnidadesOrcamentariasEmpenho() {
        if (getSelecionado().getRequisicaoDeCompra() != null) {
            List<HierarquiaOrganizacional> hierarquiasOrcamentaria = facade.getHierarquiaOrganizacionalFacade().buscarUnidadesOrcamentariasRequisicaoCompra(getSelecionado().getRequisicaoDeCompra());
            setHierarquiasOrcamentaria(hierarquiasOrcamentaria);
        }
    }

    public boolean hasMaisUmaOrcamentariaEmpenho() {
        return !Util.isListNullOrEmpty(getHierarquiasOrcamentaria()) && getHierarquiasOrcamentaria().size() > 1;
    }

    public void preencherItensEntrada() {
        try {
            validarRequisicaoCompra(getSelecionado().getRequisicaoDeCompra());
            getSelecionado().setRequisicaoDeCompra(facade.getRequisicaoDeCompraFacade().recuperar(getSelecionado().getRequisicaoDeCompra().getId()));
            recuperarCardapioRequisicaoCompra();
            if (isIntegracaoCardapio()) {
                guiasDistribuicaoVo = facade.getCardapioRequisicaoCompraFacade().buscarGuiaDistribuicaoVO(cardapioRequisicaoCompra, SituacaoRequisicaoMaterial.NAO_AVALIADA);
            }
            buscarUnidadesOrcamentariasEmpenho();
            criarItensEntrada();
            atribuirUnidadeOrganizacionalEntrada();
            recuperarHierarquiaAministrativa();
            buscarLocaisEstoque();
            getSelecionado().setHistorico(isOperacaoNovo() ? getSelecionado().getRequisicaoDeCompra().getDescricao() : selecionado.getHistorico());
        } catch (ValidacaoException ve) {
            limparAtributosAoValidarRequisicaoEntrada();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void recuperarCardapioRequisicaoCompra() {
        cardapioRequisicaoCompra = facade.getCardapioRequisicaoCompraFacade().recuperarCardapioRequisicaoCompra(getSelecionado().getRequisicaoDeCompra());
    }

    public void listenerUnidadeAdm() {
        adicionarUnidadeDaHierarquia();
        buscarLocaisEstoque();
    }

    private void buscarLocaisEstoque() {
        try {
            if (isIntegracaoCardapio()) {
                LocalEstoque localEstoquePai = facade.getCardapioFacade().buscarCardapioLocalEstoquePai(cardapioRequisicaoCompra.getCardapio());
                if (localEstoquePai != null) {
                    setLocalEstoque(localEstoquePai);
                    setLocaisEstoque(Lists.newArrayList(localEstoquePai));
                    setHierarquiaAdministrativa(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), localEstoquePai.getUnidadeOrganizacional(), selecionado.getDataEntrada()));
                }
            } else {
                List<LocalEstoque> locaisEstoque = facade.getLocalEstoqueFacade().buscarLocaisEstoquePorUnidade("", selecionado.getUnidadeOrganizacional());
                setLocaisEstoque(locaisEstoque);
            }
            if (getLocaisEstoque().size() == 1 && !hasMaisUmaOrcamentariaEmpenho()) {
                LocalEstoqueOrcamentario leo = facade.getLocalEstoqueFacade().buscarLocalEstoqueOrcamentario(getLocaisEstoque().get(0), getHierarquiasOrcamentaria().get(0).getSubordinada());
                if (leo != null)
                    for (ItemEntradaMaterial item : selecionado.getItens()) {
                        item.setLocalEstoqueOrcamentario(leo);
                    }
            }
        } catch (ExcecaoNegocioGenerica ve) {
            FacesUtil.addOperacaoNaoPermitida(ve.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void recuperarHierarquiaAministrativa() {
        if (!isIntegracaoCardapio()) {
            HierarquiaOrganizacional orgao = facade.getHierarquiaOrganizacionalFacade().buscarOrgaoDaUnidade(selecionado.getUnidadeOrganizacional(), selecionado.getDataEntrada());
            List<HierarquiaOrganizacional> ho = facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaAdmiministrativaLocalEstoueGestorMaterial("", orgao);
            if (ho.size() == 1 && isOperacaoNovo()) {
                setHierarquiaAdministrativa(ho.get(0));
            }
        }
    }

    private void validarRequisicaoCompra(RequisicaoDeCompra requisicao) {
        ValidacaoException ve = new ValidacaoException();
        if (DataUtil.dataSemHorario(requisicao.getDataRequisicao()).compareTo(DataUtil.dataSemHorario(getSelecionado().getDataEntrada())) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido realizar entrada de materiais para requisição de compra com data posterior a data de operação.");
        }
        ve.lancarException();
        if (isOperacaoNovo()) {
            EntradaCompraMaterial entrada = facade.recuperarEntradaDaRequisicao(requisicao);
            if (entrada != null && !entrada.getSituacao().isAtestoDefinitivoEstornado()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já foi realizada uma entrada para a requisição selecionada.");
            }

        }
        ve.lancarException();
    }

    private void limparAtributosAoValidarRequisicaoEntrada() {
        getSelecionado().setRequisicaoDeCompra(null);
        getSelecionado().setItens(Lists.newArrayList());
    }

    private void criarItensEntrada() {
        mapMateriais = Maps.newHashMap();
        if (isOperacaoNovo()) {
            selecionado.setItens(Lists.newArrayList());
            for (ItemRequisicaoDeCompra itemRequisicao : getSelecionado().getRequisicaoDeCompra().getItens()) {

                BigDecimal quantidadeRestante = facade.getRequisicaoDeCompraFacade().buscarQuantidadeRestante(itemRequisicao);
                itemRequisicao.setQuantidadeDisponivel(quantidadeRestante);
                if (itemRequisicao.getQuantidadeDisponivel().compareTo(BigDecimal.ZERO) > 0) {
                    ItemEntradaMaterial itemEntrada = new ItemEntradaMaterial(getSelecionado(), itemRequisicao);

                    if (isIntegracaoCardapio() && hasLocalEstoque()) {
                        itemEntrada.getLocalEstoqueOrcamentario().setLocalEstoque(getLocalEstoque());
                        itemEntrada.getLocalEstoqueOrcamentario().setUnidadeOrcamentaria(getHierarquiasOrcamentaria().get(0).getSubordinada());
                        Material material = facade.getCardapioRequisicaoCompraFacade().getMaterialGuiaDistribuicao(cardapioRequisicaoCompra.getCardapio(), itemRequisicao);
                        if (material != null) {
                            itemEntrada.setMaterial(material);
                        }
                    }
                    selecionado.getItens().add(itemEntrada);
                }
            }
        }
        criarMapMateriais();
    }

    private void criarMapMateriais() {
        for (ItemEntradaMaterial item : selecionado.getItens()) {
            if (!mapMateriais.containsKey(item)) {
                List<Material> materiais = facade.getMaterialFacade().buscarMateriaisPorObjetoCompra(item.getObjetoCompra());
                List<MaterialVO> materiaisVos = Lists.newArrayList();
                for (Material mat : materiais) {
                    boolean isMovimentado = facade.getMaterialFacade().verificarSeMaterialTemEntradaNoEstoque(mat);
                    MaterialVO novoMatVO = new MaterialVO(mat, isMovimentado);
                    materiaisVos.add(novoMatVO);
                }
                mapMateriais.put(item, Lists.newArrayList(materiaisVos));
                if (!Util.isListNullOrEmpty(materiais) && materiais.size() == 1) {
                    item.setMaterial(materiais.get(0));
                }
                if (item.getItemCompraMaterial().getItemRequisicaoDeCompra().isDerivacaoObjetoCompra()) {
                    materiais.stream()
                        .filter(mat -> mat.getDerivacaoComponente() != null && mat.getDerivacaoComponente().equals(item.getItemCompraMaterial().getItemRequisicaoDeCompra().getDerivacaoComponente()))
                        .forEach(item::setMaterial);
                }
            }
        }
    }

    public void editarMaterial(ItemEntradaMaterial item) {
        selecionarItemEntrada(item);
        Material material = facade.getMaterialFacade().recuperar(item.getMaterial().getId());
        boolean isMovimentado = facade.getMaterialFacade().verificarSeMaterialTemEntradaNoEstoque(material);
        if (material.getObjetoCompra().isObjetoDerivacao() && material.getDerivacaoComponente() == null) {
            material.setDerivacaoComponente(item.getItemCompraMaterial().getItemRequisicaoDeCompra().getDerivacaoComponente());
        }
        materialVO = new MaterialVO(material, isMovimentado);
        FacesUtil.atualizarComponente("form-material");
        FacesUtil.executaJavaScript("dialogMaterial.show()");
    }

    public void confirmarMaterial() {
        try {
            Util.validarCampos(materialVO.getMaterial());
            Material material = facade.getMaterialFacade().salvarRetornando(materialVO.getMaterial());
            itemEntradaMaterial.setMaterial(material);

            MaterialVO novoMapVO = new MaterialVO(material, false);
            mapMateriais.put(itemEntradaMaterial, Lists.newArrayList(novoMapVO));
            cancelarMaterial();
            FacesUtil.executaJavaScript("dialogMaterial.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public void removerMaterial(ItemEntradaMaterial item) {
        try {
            Material material = item.getMaterial();
            List<MaterialVO> materiaisMap = mapMateriais.get(item);
            facade.getMaterialFacade().remover(material);

            for (ItemEntradaMaterial itemEnt : selecionado.getItens()) {
                if (itemEnt.getMaterial() != null && itemEnt.getMaterial().equals(material)) {
                    materiaisMap.removeIf(m -> m.getMaterial().equals(material));
                    mapMateriais.put(itemEnt, materiaisMap);
                    itemEnt.setMaterial(null);
                }
            }
            FacesUtil.atualizarComponente("Formulario:tab-view-geral:tabela-item-entrada");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public void novoMaterial(ItemEntradaMaterial item) {
        selecionarItemEntrada(item);
        Material material = new Material();
        material.setDataRegistro(facade.getSistemaFacade().getDataOperacao());
        material.setObjetoCompra(itemEntradaMaterial.getObjetoCompra());
        material.setUnidadeMedida(item.getItemCompraMaterial().getItemRequisicaoDeCompra().getUnidadeMedida());
        material.setDerivacaoComponente(item.getItemCompraMaterial().getItemRequisicaoDeCompra().getDerivacaoComponente());
        buscarAssociacaoGrupoMaterial(material);
        materialVO = new MaterialVO(material, false);
        FacesUtil.atualizarComponente("form-material");
        FacesUtil.executaJavaScript("dialogMaterial.show()");
    }

    @Override
    public void validarRegrasDoNegocio() throws ValidacaoException {
        validarCamposObrigatorios();
        ValidacaoException vex = new ValidacaoException();
        super.validarRegrasDoNegocio();
        validarLocalEstoqueAlmoxarifado(vex);
        vex.lancarException();
    }

    public void validarCamposObrigatorios() {
        selecionado.realizarValidacoes();

        ValidacaoException vex = new ValidacaoException();
        if (selecionado.getUnidadeOrganizacional() == null) {
            vex.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa deve ser informado.");
        }
        for (ItemEntradaMaterial item : selecionado.getItens()) {
            if (item.getMaterial() == null) {
                vex.adicionarMensagemDeCampoObrigatorio("Informe o material para o item " + (selecionado.getItens().indexOf(item) + 1) + " da lista.");
            }
            if (item.getQuantidade() == null) {
                vex.adicionarMensagemDeOperacaoNaoPermitida("Informe a quantidade do item " + item.getDescricao() + ".");
            }
            if (item.getQuantidade().compareTo(item.getItemCompraMaterial().getItemRequisicaoDeCompra().getQuantidade()) > 0) {
                vex.adicionarMensagemDeOperacaoNaoPermitida("Informe para o item " + item.getDescricao() + " uma quantidade igual ou inferior a requerida.");
            }
            if (item.getQuantidade().compareTo(BigDecimal.ZERO) < 0) {
                vex.adicionarMensagemDeOperacaoNaoPermitida("Informe para o item " + item.getDescricao() + " uma quantidade igual ou superior a zero.");
            }
        }
        if (Util.isListNullOrEmpty(getSelecionado().getDocumentosFiscais())) {
            vex.adicionarMensagemDeOperacaoNaoPermitida("Deve haver pelo menos um documento na entrada por compra.");
        }
        vex.lancarException();
        if (selecionado.getValorTotalEntrada().compareTo(getSelecionado().getValorTotalDocumento()) != 0) {
            vex.adicionarMensagemDeOperacaoNaoPermitida("O valor total dos documentos fiscais deve ser igual ao valor total da entrada.");
        }
        vex.lancarException();
    }

    private void validarLocalEstoqueAlmoxarifado(ValidacaoException vex) {
        for (ItemEntradaMaterial item : selecionado.getItens()) {
            if (item.getLocalEstoqueOrcamentario() == null) {
                vex.adicionarMensagemDeCampoObrigatorio("O campo local de estoque orçamentário deve ser informado.");

            } else if (item.getLocalEstoque() == null) {
                vex.adicionarMensagemDeCampoObrigatorio("O campo local de estoque deve ser informado para o item " + item.getDescricao() + ".");

            } else if (item.getLocalEstoqueOrcamentario().getUnidadeOrcamentaria() == null) {
                vex.adicionarMensagemDeCampoObrigatorio("O campo unidade orçamentária do local de estoque deve ser informado.");

            } else if (!item.getLocalEstoque().ehTipoAlmoxarifado()) {
                vex.adicionarMensagemDeOperacaoNaoPermitida("O local de estoque do item " + item.getDescricao() + " não é do tipo " + TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO.getDescricao() + ".");

            } else if (!selecionado.getUnidadeOrganizacional().equals(item.getLocalEstoque().getUnidadeOrganizacional())) {
                vex.adicionarMensagemDeOperacaoNaoPermitida("A unidade administrativa: " + item.getLocalEstoque().getUnidadeOrganizacional()
                    + "  do local de estoque está diferente da unidade da entrada por compra.");
            }
        }
    }

    public void novoDocumentoFiscal() {
        try {
            valdiarAdicionarNovoDocumento();
            doctoFiscalEntradaCompra = new DoctoFiscalEntradaCompra();
            doctoFiscalEntradaCompra.setEntradaCompraMaterial(getSelecionado());
            doctoFiscalEntradaCompra.setSituacao(SituacaoDocumentoFiscalEntradaMaterial.AGUARDANDO_LIQUIDACAO);
            doctoFiscalEntradaCompra.setDoctoFiscalLiquidacao(new DoctoFiscalLiquidacao());
            doctoFiscalEntradaCompra.getDoctoFiscalLiquidacao().setDataDocto(facade.getSistemaFacade().getDataOperacao());
            FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view-geral:tipo-documento-fiscal_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private boolean temAlgumItemComSaldoDisponivel() {
        for (ItemEntradaMaterial itemEntrada : selecionado.getItens()) {
            BigDecimal quantidadeTotalJaLancadaDoItem = getSelecionado().getQuantidadeTotalJaLancadaDoItem(itemEntrada.getItemCompraMaterial().getItemRequisicaoDeCompra());
            if (quantidadeTotalJaLancadaDoItem.compareTo(itemEntrada.getQuantidade()) != 0) {
                return true;
            }
        }
        return false;
    }

    private void valdiarAdicionarNovoDocumento() {
        ValidacaoException ve = new ValidacaoException();
        if (getSelecionado().getRequisicaoDeCompra() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório informar a requisição antes de adicionar o(s) documento(s) fiscal(is).");
        }
        if (!temAlgumItemComSaldoDisponivel()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Todos os itens já foram lançados nos documentos adicionados.");
        }
        ve.lancarException();
    }

    public void selecionarItemEntrada(ItemEntradaMaterial item) {
        this.itemEntradaMaterial = item;
    }

    public void selecionarDocumentoFiscal(DoctoFiscalEntradaCompra docto) {
        doctoFiscalEntradaCompra = docto;
    }

    public void novoFiltroEmpenhoDocumentoFiscal(DoctoFiscalEntradaCompra docEnt) {
        List<Long> idsItemReq = getIdsItemRequisicaoAdicionadoDocumentoFiscal(docEnt);
        filtroEmpenhoDocumentoFiscal = new FiltroEmpenhoDocumentoFiscal(getSelecionado().getRequisicaoDeCompra().getId(), idsItemReq);
        FacesUtil.executaJavaScript("rcBuscarEmpenhoDocumentoFiscal()");
    }

    public void adicionarDocumentoFiscalVerificandoDuplicidade() {
        try {
            doctoFiscalEntradaCompra.getDoctoFiscalLiquidacao().realizarValidacoes();
            validarDocumentoFiscal();
            validarDuplicidadeDocumentoFiscal();
            if (Util.isListNullOrEmpty(mensagemDocumentosDuplicados)) {
                adicionarDocumentoFiscal();
            } else {
                FacesUtil.executaJavaScript("dlgValidacaoDocumento.show()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void adicionarDocumentoFiscal() {
        try {
            atualizarValorDocumento();
            Util.adicionarObjetoEmLista(getSelecionado().getDocumentosFiscais(), doctoFiscalEntradaCompra);
            cancelarDocumentoFiscal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void validarDuplicidadeDocumentoFiscal() {
        try {
            mensagemDocumentosDuplicados = new ArrayList<>();
//            Pessoa fornecedorRequisicao = facade.getRequisicaoDeCompraFacade().getFornecedorRequisicao(getSelecionado().getRequisicaoDeCompra());


            List<DoctoFiscalEntradaCompra> documentoList = facade.buscarDocumentosFiscaisDuplicados(
                doctoFiscalEntradaCompra.getDoctoFiscalLiquidacao().getNumero(),
                doctoFiscalEntradaCompra.getDoctoFiscalLiquidacao().getChaveDeAcesso(),
                doctoFiscalEntradaCompra.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal(),
                getSelecionado().getRequisicaoDeCompra().getFornecedor()
            );

            if (!Util.isListNullOrEmpty(documentoList)) {
                documentoList.forEach(doc -> {
                    if (!doc.equals(doctoFiscalEntradaCompra)) {
                        String numeroEntrada = (doc.getEntradaCompraMaterial().getNumero() != null) ? " Nº " + doc.getEntradaCompraMaterial().getNumero() : " ";
                        mensagemDocumentosDuplicados.add("Atenção! Foi encontrado um registro anterior de Entrada por Compra " +
                            "com o Documento Fiscal com esse Tipo do Documento e Número ou Chave de Acesso. Entrada por Compra" +
                            numeroEntrada + " - " + doc.getEntradaCompraMaterial().getSituacao().getDescricao() +
                            ", verifique e prossiga com cautela a fim de evitar duplicidade. ".concat(
                                Util.linkBlack("/entrada-por-compra/ver/" + doc.getEntradaCompraMaterial().getId(), "Clique aqui para visualizar.")
                            )
                        );
                    }
                });
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void validarDocumentoFiscal() {
        ValidacaoException ve = new ValidacaoException();
        if (doctoFiscalEntradaCompra.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal().getObrigarChaveDeAcesso()
            && doctoFiscalEntradaCompra.getDoctoFiscalLiquidacao().getChaveDeAcesso().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Chave de Acesso deve ser informado.");
        }
        ve.lancarException();
        for (DoctoFiscalEntradaCompra df : getSelecionado().getDocumentosFiscais()) {
            if (!df.equals(doctoFiscalEntradaCompra) &&
                df.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal().equals(doctoFiscalEntradaCompra.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal()) &&
                (
                    (df.getDoctoFiscalLiquidacao().getNumero() != null && doctoFiscalEntradaCompra.getDoctoFiscalLiquidacao().getNumero() != null &&
                        df.getDoctoFiscalLiquidacao().getNumero().equals(doctoFiscalEntradaCompra.getDoctoFiscalLiquidacao().getNumero())) ||
                        (df.getDoctoFiscalLiquidacao().getChaveDeAcesso() != null && doctoFiscalEntradaCompra.getDoctoFiscalLiquidacao().getChaveDeAcesso() != null &&
                            df.getDoctoFiscalLiquidacao().getChaveDeAcesso().equals(doctoFiscalEntradaCompra.getDoctoFiscalLiquidacao().getChaveDeAcesso()))
                )) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não deve ter mais de um documento fiscal com o mesmo <b>Tipo do Documento</b> e <b>Número</b> ou <b>Chave de Acesso</b>.");
            }
        }
        ve.lancarException();
    }

    public void removerDocumentoFiscal(DoctoFiscalEntradaCompra docto) {
        getSelecionado().getDocumentosFiscais().remove(docto);
    }

    public void cancelarDocumentoFiscal() {
        doctoFiscalEntradaCompra = null;
        FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view-geral:novo-documento')");
    }

    public void carregarItensDocumentoFiscal(DoctoFiscalEntradaCompra docto) {
        selecionarDocumentoFiscal(docto);
        for (ItemEntradaMaterial itemEntrada : selecionado.getItens()) {

            boolean isItemNovoNoDocumento = isItemNovoNoDocumento(itemEntrada);
            if (isItemNovoNoDocumento) {
                ItemDoctoItemEntrada itemDocItemEnt = new ItemDoctoItemEntrada();
                itemDocItemEnt.setDoctoFiscalEntradaCompra(doctoFiscalEntradaCompra);
                itemDocItemEnt.setItemEntradaMaterial(itemEntrada);
                itemDocItemEnt.setQuantidade(getQuantidadeDisponivelAtual(itemDocItemEnt));

                Util.adicionarObjetoEmLista(doctoFiscalEntradaCompra.getItens(), itemDocItemEnt);
            }
        }
        FacesUtil.executaJavaScript("itensDocumentoFiscal.show()");
    }

    private boolean isItemNovoNoDocumento(ItemEntradaMaterial itemEntrada) {
        boolean isNovoItem = true;
        for (DoctoFiscalEntradaCompra docEnt : getSelecionado().getDocumentosFiscais()) {
            for (ItemDoctoItemEntrada item : docEnt.getItens()) {
                if (item.getDoctoFiscalEntradaCompra().equals(doctoFiscalEntradaCompra) && itemEntrada.equals(item.getItemEntradaMaterial())) {
                    isNovoItem = false;
                    break;
                }
            }
        }
        return isNovoItem;
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalLocalEstoque(String parte) {
        if (selecionado.getUnidadeOrganizacional() == null) {
            return Lists.newArrayList();
        }
        HierarquiaOrganizacional orgao = facade.getHierarquiaOrganizacionalFacade().buscarOrgaoDaUnidade(selecionado.getUnidadeOrganizacional(), selecionado.getDataEntrada());
        return facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaAdmiministrativaLocalEstoueGestorMaterial(parte.trim(), orgao);
    }

    private void atribuirUnidadeOrganizacionalEntrada() {
        UnidadeOrganizacional unidadeAdm = isOperacaoNovo()
            ? facade.getRequisicaoDeCompraFacade().getUnidadeAdministrativa(getSelecionado().getRequisicaoDeCompra(), getSelecionado().getDataEntrada())
            : selecionado.getUnidadeOrganizacional();
        selecionado.setUnidadeOrganizacional(unidadeAdm);
        setHierarquiaAdministrativa(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unidadeAdm, selecionado.getDataEntrada()));
    }

    public void atualizarQuantidadeDisponivelDoItem(ItemDoctoItemEntrada itemDocEnt) {
        if (!itemDocEnt.hasQuantidade()) {
            itemDocEnt.setQuantidade(BigDecimal.ZERO);
            FacesUtil.addOperacaoNaoPermitida("A quantidade deve ser maior que zero(0).");
            return;
        }
        if (getQuantidadeDisponivelAtual(itemDocEnt).compareTo(BigDecimal.ZERO) < 0) {
            itemDocEnt.setQuantidade(BigDecimal.ZERO);
            FacesUtil.addOperacaoNaoPermitida("A quantidade informada deve ser menor ou igual a quantidade disponível.");
        }
    }

    public BigDecimal getQuantidadeDisponivelAtual(ItemDoctoItemEntrada itemDocto) {
        BigDecimal qtdeUtilizada = getSelecionado().getQuantidadeTotalJaLancadaDoItem(itemDocto.getItemEntradaMaterial().getItemCompraMaterial().getItemRequisicaoDeCompra());
        return itemDocto.getItemEntradaMaterial().getQuantidade().subtract(qtdeUtilizada);
    }

    public void limparQuantidadeDistribuidasItens() {
        doctoFiscalEntradaCompra.getItens().forEach(item -> {
            item.setQuantidade(BigDecimal.ZERO);
        });
    }

    public void confirmarItensDocumento() {
        removerItensDocumentoComQuantidadeZerada();
        atualizarValorDocumento();
        cancelarDocumentoFiscal();
    }

    private void atualizarValorDocumento() {
        BigDecimal valorTotalDocto = doctoFiscalEntradaCompra.getValorTotalDocumento().setScale(2, RoundingMode.HALF_EVEN);
        doctoFiscalEntradaCompra.getDoctoFiscalLiquidacao().setValor(valorTotalDocto);
        doctoFiscalEntradaCompra.getDoctoFiscalLiquidacao().setTotal(valorTotalDocto);
    }

    public void cancelarItensDoDocumento() {
        limparItensDoDocumentoSelecionado();
        cancelarDocumentoFiscal();
    }

    private void limparItensDoDocumentoSelecionado() {
        doctoFiscalEntradaCompra.setItens(Lists.newArrayList());
    }

    private void removerItensDocumentoComQuantidadeZerada() {
        doctoFiscalEntradaCompra.getItens().removeIf(item -> !item.hasQuantidade());
    }

    public List<RequisicaoDeCompra> completarRequisicaoBensDeEstoque(String parte) {
        return facade.getRequisicaoDeCompraFacade().buscarRequisicoesEntradaPorCompra(parte.trim());
    }

    public List<Modelo> completarModelo(String parte) {
        Material material = materialVO.getMaterial();
        if (material.getMarca() != null && material.getMarca().getId() != null) {
            return facade.getModeloFacade().listaPorMarca(parte.trim(), material.getMarca());
        }
        FacesUtil.addError(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Selecione uma marca.");
        return new ArrayList<>();
    }

    public void cancelarMaterial() {
        materialVO = null;
    }

    public List<SelectItem> getMateriais(ItemEntradaMaterial item) {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        List<MaterialVO> materiais = mapMateriais.get(item);
        for (MaterialVO mat : materiais) {
            if (mat.getMaterial() != null) {
                toReturn.add(new SelectItem(mat.getMaterial(), mat.getMaterial().toString() + " - " + mat.getMaterial().getUnidadeMedida().getDescricao()));
            }
        }
        return toReturn;
    }

    public Boolean hasMaterialCadastrado(ItemEntradaMaterial item) {
        return item != null && mapMateriais != null && !mapMateriais.get(item).isEmpty();
    }

    public List<SelectItem> getLotesMateriais(Material material) {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (material != null) {
            List<LoteMaterial> retorno = facade.getLoteMaterialFacade().buscarFiltrandoPorMaterial("", material);
            if (Util.isListNullOrEmpty(retorno)) {
                FacesUtil.addAtencao("Nenhum lote cadastrado para o material " + material);
                return toReturn;
            }
            for (LoteMaterial loteMaterial : retorno) {
                toReturn.add(new SelectItem(loteMaterial, loteMaterial.toString()));
            }
        }
        return toReturn;
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(dto);
            dto.setApi("administrativo/entrada-compra-material/");
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

    public void montarDtoSemApi(RelatorioDTO dto) {
        List<EmpenhoDocumentoFiscalDTO> dtos = Lists.newArrayList();

        for (DoctoFiscalEntradaCompra docEnt : getSelecionado().getDocumentosFiscais()) {
            DoctoFiscalLiquidacao doc = docEnt.getDoctoFiscalLiquidacao();

            List<Long> idsItemReq = getIdsItemRequisicaoAdicionadoDocumentoFiscal(docEnt);
            FiltroEmpenhoDocumentoFiscal filtro = new FiltroEmpenhoDocumentoFiscal(getSelecionado().getRequisicaoDeCompra().getId(), idsItemReq);
            List<EmpenhoDocumentoFiscal> empenhosDocumentosFiscais = facade.getRequisicaoDeCompraFacade().buscarEmpenhosDocumentoFiscal(filtro);

            for (EmpenhoDocumentoFiscal empenhoDoc : empenhosDocumentosFiscais) {
                EmpenhoDocumentoFiscalDTO empDocDTO = new EmpenhoDocumentoFiscalDTO();
                String empenhoDescricao = "Empenho " + empenhoDoc.getEmpenho().getNumero() + "/" + empenhoDoc.getEmpenho().getExercicio() +
                    " - R$ " + facade.getRequisicaoDeCompraFacade().getTotalizadorItensEmpenho(empenhoDoc);

                empDocDTO.setId(doc.getId());
                empDocDTO.setEmpenhoDescricao(empenhoDescricao);
                dtos.add(empDocDTO);
            }
        }
        dto.adicionarParametro("empenhos", dtos);
        String nomeRelatorio = "RELATÓRIO DE ENTRADA DE MATERIAIS POR COMPRA";
        dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("NOME_RELATORIO", nomeRelatorio);
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("MODULO", "Materiais");
        dto.adicionarParametro("idEntradaMaterial", getSelecionado().getId());
        dto.adicionarParametro("TIPO_REQUISICAO", getSelecionado().getRequisicaoDeCompra().getTipoRequisicao().getDescricao());
        dto.setNomeRelatorio(nomeRelatorio);
    }

    @NotNull
    private static List<Long> getIdsItemRequisicaoAdicionadoDocumentoFiscal(DoctoFiscalEntradaCompra docEnt) {
        List<Long> idsItemReq = docEnt.getItens().stream().map(item -> item.getItemEntradaMaterial().getItemCompraMaterial().getItemRequisicaoDeCompra().getId()).collect(Collectors.toList());
        return idsItemReq;
    }


    public void definirLocalEstoqueComoNull() {
        getItemEntradaMaterial().setLocalEstoquePai(null);
        getSelecionado().setUnidadeOrganizacional(null);
        getItemEntradaMaterial().setMaterial(null);
    }

    public List<SelectItem> getLocaisEstoqueAdm() {
        List<SelectItem> toReturn = Lists.newArrayList();
        if (selecionado.getUnidadeOrganizacional() != null) {
            toReturn.add(new SelectItem(null, ""));
            for (LocalEstoque le : getLocaisEstoque()) {
                toReturn.add(new SelectItem(le, le.getCodigoDescricao()));
            }
        }
        return toReturn;
    }

    public void validarAssociativa(ItemEntradaMaterial itemEntradaMaterial) {
        if (itemEntradaMaterial.getMaterial() != null) {
            Material material = itemEntradaMaterial.getMaterial();
            GrupoMaterial grupoMaterial = material.getGrupo();
            GrupoObjetoCompra grupoObjetoCompra = material.getObjetoCompra().getGrupoObjetoCompra();
            try {
                AssociacaoGrupoObjetoCompraGrupoMaterial associacao = facade.getAssociacaoGrupoObjetoCompraGrupoMaterialFacade().buscarAssociacaoPorGrupoObjetoCompraVigente(grupoObjetoCompra, facade.getSistemaFacade().getDataOperacao());
                if (!grupoMaterial.equals(associacao.getGrupoMaterial())) {
                    FacesUtil.addOperacaoNaoPermitida(" O grupo material " + grupoMaterial +
                        " do objeto de compra " + material.getObjetoCompra().getCodigo() +
                        " é diferente do grupo material " + associacao.getGrupoMaterial() + " recuperado da associativa.");
                    itemEntradaMaterial.setMaterial(null);
                }
            } catch (ExcecaoNegocioGenerica ex) {
                itemEntradaMaterial.setMaterial(null);
                FacesUtil.atualizarComponente("Formulario:tab-view-geral:tabela-item-entrada");
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
    }

    public void buscarAssociacaoGrupoMaterial(Material material) {
        if (material.getObjetoCompra() != null) {
            try {
                AssociacaoGrupoObjetoCompraGrupoMaterial associacao = facade.getAssociacaoGrupoObjetoCompraGrupoMaterialFacade().buscarAssociacaoPorGrupoObjetoCompraVigente(material.getObjetoCompra().getGrupoObjetoCompra(), facade.getSistemaFacade().getDataOperacao());
                material.setGrupo(associacao.getGrupoMaterial());
            } catch (ExcecaoNegocioGenerica ex) {
                FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
            }
        }
    }

    public boolean permiteEdicao() {
        return !selecionado.getSituacao().isAtestoDefinitivo()
            && !getSelecionado().hasDocumentosLiquidado()
            && selecionado.getNumero() == null;
    }

    public void buscarLiquidacoesVOPorDocumentoFiscalEntrada(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        liquidacoesVO = facade.getRequisicaoDeCompraFacade().buscarLiquidacoesVOPorDocumentoFiscal(doctoFiscalLiquidacao);
    }

    public boolean hasLocalEstoque() {
        return getLocalEstoque() != null;
    }

    public Boolean isIntegracaoCardapio() {
        return cardapioRequisicaoCompra != null;
    }

    public List<GuiaDistribuicaoVO> getGuiasDistribuicaoVo() {
        return guiasDistribuicaoVo;
    }

    public void setGuiasDistribuicaoVo(List<GuiaDistribuicaoVO> guiasDistribuicaoVo) {
        this.guiasDistribuicaoVo = guiasDistribuicaoVo;
    }

    public DoctoFiscalEntradaCompra getDoctoFiscalEntradaCompra() {
        return doctoFiscalEntradaCompra;
    }

    public void setDoctoFiscalEntradaCompra(DoctoFiscalEntradaCompra doctoFiscalEntradaCompra) {
        this.doctoFiscalEntradaCompra = doctoFiscalEntradaCompra;
    }

    public MaterialVO getMaterialVO() {
        return materialVO;
    }

    public void setMaterialVO(MaterialVO materialVO) {
        this.materialVO = materialVO;
    }

    public List<String> getMensagemDocumentosDuplicados() {
        return mensagemDocumentosDuplicados;
    }

    public void setMensagemDocumentosDuplicados(List<String> mensagemDocumentosDuplicados) {
        this.mensagemDocumentosDuplicados = mensagemDocumentosDuplicados;
    }

    public List<LiquidacaoVO> getLiquidacoesVO() {
        return liquidacoesVO;
    }

    public void setLiquidacoesVO(List<LiquidacaoVO> liquidacoesVO) {
        this.liquidacoesVO = liquidacoesVO;
    }

    public List<SolicitacaoEstornoEntradaMaterial> getSolicitacoesEstornoEntrada() {
        return solicitacoesEstornoEntrada;
    }

    public void setSolicitacoesEstornoEntrada(List<SolicitacaoEstornoEntradaMaterial> solicitacoesEstornoEntrada) {
        this.solicitacoesEstornoEntrada = solicitacoesEstornoEntrada;
    }

    public List<ItemEntradaReservaEstoqueVO> getItensReservaEstoqueVO() {
        return itensReservaEstoqueVO;
    }

    public void setItensReservaEstoqueVO(List<ItemEntradaReservaEstoqueVO> itensReservaEstoqueVO) {
        this.itensReservaEstoqueVO = itensReservaEstoqueVO;
    }

    public FiltroEmpenhoDocumentoFiscal getFiltroEmpenhoDocumentoFiscal() {
        return filtroEmpenhoDocumentoFiscal;
    }

    public void setFiltroEmpenhoDocumentoFiscal(FiltroEmpenhoDocumentoFiscal filtroEmpenhoDocumentoFiscal) {
        this.filtroEmpenhoDocumentoFiscal = filtroEmpenhoDocumentoFiscal;
    }
}
