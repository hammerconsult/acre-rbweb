/*
 * Codigo gerado automaticamente em Wed Apr 18 18:47:14 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.entidadesauxiliares.contabil.MovimentoDespesaORCVO;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.entidadesauxiliares.contabil.MovimentoDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DotacaoSolicitacaoMaterialFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@ManagedBean(name = "dotacaoSolicitacaoMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaDotacaoSolicitacaoCompra", pattern = "/dotacao-de-solicitacao-de-compra/novo/", viewId = "/faces/administrativo/licitacao/reservadedotacao/edita.xhtml"),
    @URLMapping(id = "editarDotacaoSolicitacaoCompra", pattern = "/dotacao-de-solicitacao-de-compra/editar/#{dotacaoSolicitacaoMaterialControlador.id}/", viewId = "/faces/administrativo/licitacao/reservadedotacao/edita.xhtml"),
    @URLMapping(id = "verDotacaoSolicitacaoCompra", pattern = "/dotacao-de-solicitacao-de-compra/ver/#{dotacaoSolicitacaoMaterialControlador.id}/", viewId = "/faces/administrativo/licitacao/reservadedotacao/visualizar.xhtml"),
    @URLMapping(id = "listarDotacaoSolicitacaoCompra", pattern = "/dotacao-de-solicitacao-de-compra/listar/", viewId = "/faces/administrativo/licitacao/reservadedotacao/lista.xhtml")
})
public class DotacaoSolicitacaoMaterialControlador extends PrettyControlador<DotacaoSolicitacaoMaterial> implements Serializable, CRUD {

    @EJB
    private DotacaoSolicitacaoMaterialFacade facade;
    private List<FonteDespesaORC> fontesDespesaOrcamentaria;
    private Set<FonteDespesaORC> fontesReprocessamento;
    private List<DotacaoSolicitacaoMaterialItemVO> dotacoesTipo;
    private DotacaoSolicitacaoMaterialItemVO dotacaoTipo;
    private DotacaoSolicitacaoMaterialItemFonteVO dotacaoFonte;
    private UnidadeOrganizacional unidadeOrcamentaria;
    private DespesaORC despesaORC;
    private BigDecimal saldoFonteDespesaORC;
    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;
    private ConfiguracaoReservaDotacao configuracaoReservaDotacao;

    public DotacaoSolicitacaoMaterialControlador() {
        super(DotacaoSolicitacaoMaterial.class);
    }

    @URLAction(mappingId = "novaDotacaoSolicitacaoCompra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setRealizadaEm(facade.getSistemaFacade().getDataOperacao());
        dotacoesTipo = Lists.newArrayList();
        fontesReprocessamento = Sets.newHashSet();
    }

    @URLAction(mappingId = "editarDotacaoSolicitacaoCompra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarConfiguracaoReservaDotacao();
        if (isObrigaReservaExecucaoContrato()) {
            FacesUtil.addOperacaoNaoPermitida("Não é permitido editar um registro onde a reserva de dotação foi gerada na execução de contrato/sem contrato.");
            redirecionarVisualizar();
        }
        criarDotacaoSolicitacaoMaterialTipoFonteVOFromObjeto();
        ordenarDotacaoFonte();
    }

    @URLAction(mappingId = "verDotacaoSolicitacaoCompra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        criarDotacaoSolicitacaoMaterialTipoFonteVOFromObjeto();
        ordenarDotacaoFonte();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/dotacao-de-solicitacao-de-compra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public void salvar() {
        try {
            validarDotacao();
            selecionado = facade.salvarRetornando(selecionado, dotacoesTipo);
            reprocessarSaldoOrcamentario(fontesReprocessamento);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            FacesUtil.executaJavaScript("dialogVisualizarReserva.show()");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    @Override
    public void excluir() {
        try {
            facade.validarExclusao(selecionado);
            fontesReprocessamento = getFontesDespesaOrcDistintasParaReprocessamento();
            facade.remover(selecionado);
            reprocessarSaldoOrcamentario(fontesReprocessamento);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void reprocessarSaldoOrcamentario(Set<FonteDespesaORC> fontes) {
        for (FonteDespesaORC fonte : fontes) {
            AssistenteReprocessamento assistente = new AssistenteReprocessamento();
            ReprocessamentoSaldoFonteDespesaOrc reprocessamento = new ReprocessamentoSaldoFonteDespesaOrc();
            reprocessamento.setDespesaORC(fonte.getDespesaORC());
            reprocessamento.setFonteDespesaORC(fonte);
            assistente.setDataInicial(DataUtil.removerDias(selecionado.getRealizadaEm(), 1));
            assistente.setDataFinal(new Date());
            assistente.setReprocessamentoSaldoFonteDespesaOrc(reprocessamento);
            assistente.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
            facade.getReprocessamentoSaldoFonteDespesaOrcFacade().reprocessar(assistente);
        }
    }

    private Set<FonteDespesaORC> getFontesDespesaOrcDistintasParaReprocessamento() {
        Set<FonteDespesaORC> fontes = new HashSet<>();
        selecionado.getItens().stream()
            .flatMap(item -> item.getFontes().stream())
            .filter(font -> font.getFonteDespesaORC() != null && font.getGeraReservaOrc())
            .forEach(font -> fontes.add(font.getFonteDespesaORC()));
        return fontes;
    }

    private void criarDotacaoSolicitacaoMaterialTipoFonteVOFromObjeto() {
        dotacoesTipo = Lists.newArrayList();
        fontesReprocessamento = Sets.newHashSet();
        Map<TipoObjetoCompra, BigDecimal> mapTipoObjetoCompraValorProcesso = Maps.newHashMap();

        selecionado.getItens().forEach(dotTipo -> {
            DotacaoSolicitacaoMaterialItemVO novaDotTipoVO = new DotacaoSolicitacaoMaterialItemVO();
            novaDotTipoVO.setTipoObjetoCompra(dotTipo.getTipoObjetoCompra());
            novaDotTipoVO.setSubTipoObjetoCompra(selecionado.getSolicitacaoMaterial().getSubTipoObjetoCompra());
            novaDotTipoVO.setValor(isOperacaoEditar() ? BigDecimal.ZERO :dotTipo.getValor());
            Collections.sort(dotTipo.getFontes());
            mapTipoObjetoCompraValorProcesso.put(dotTipo.getTipoObjetoCompra(), dotTipo.getValor());

            FiltroDotacaoProcessoCompraVO filtro = new FiltroDotacaoProcessoCompraVO();
            filtro.setSolicitacaoMaterial(selecionado.getSolicitacaoMaterial());
            filtro.setExercicio(selecionado.getSolicitacaoMaterial().getExercicio());
            filtro.setTipoObjetoCompra(novaDotTipoVO.getTipoObjetoCompra());

            dotTipo.getFontes().forEach(dotFonte -> {
                AgrupadorReservaSolicitacaoCompraVO agrupador = novoAgrupadorReserva(novaDotTipoVO.getTipoObjetoCompra(), dotFonte.getExercicio(), novaDotTipoVO.getAgrupadorFontes());

                DotacaoSolicitacaoMaterialItemFonteVO novaDotFonteVO = new DotacaoSolicitacaoMaterialItemFonteVO();
                novaDotFonteVO.setDataLancamento(dotFonte.getDataLancamento());
                novaDotFonteVO.setFonteDespesaORC(dotFonte.getFonteDespesaORC());
                novaDotFonteVO.setTipoOperacao(dotFonte.getTipoOperacao());
                novaDotFonteVO.setTipoReserva(dotFonte.getTipoReserva());
                novaDotFonteVO.setExercicio(dotFonte.getExercicio());
                novaDotFonteVO.setValorReservado(dotFonte.getValor());
                novaDotFonteVO.setId(dotFonte.getId());
                novaDotFonteVO.setIdOrigem(dotFonte.getIdOrigem());
                novaDotFonteVO.setFonteAgrupadora(!hasDotacaoFonteVONoAgruapdor(novaDotTipoVO, novaDotFonteVO));
                if (dotFonte.getFonteDespesaORC() != null) {
                    novaDotFonteVO.setFonteJaExecutada(facade.verificarVinculoComExecucao(dotFonte));
                    fontesReprocessamento.add(dotFonte.getFonteDespesaORC());
                }

                Util.adicionarObjetoEmLista(agrupador.getDotacoesFonte(), novaDotFonteVO);
                Util.adicionarObjetoEmLista(novaDotTipoVO.getAgrupadorFontes(), agrupador);

                if (novaDotFonteVO.getFonteAgrupadora() && novaDotFonteVO.getFonteDespesaORC() != null) {
                    filtro.setFonteDespesaORC(novaDotFonteVO.getFonteDespesaORC());
                    List<DotacaoProcessoCompraVO> reservas = facade.getDotacaoProcessoCompraFacade().buscarValoresReservaDotacaoProcessoCompra(filtro);
                    if (!Util.isListNullOrEmpty(reservas) && reservas.size() == 1) {
                        novaDotFonteVO.setDotacaoProcessoCompraVO(reservas.get(0));
                    }
                }
            });
            Util.adicionarObjetoEmLista(dotacoesTipo, novaDotTipoVO);
        });
        atualizarReservaAposAjusteProcesso(mapTipoObjetoCompraValorProcesso);
    }

    private void atualizarReservaAposAjusteProcesso(Map<TipoObjetoCompra, BigDecimal> mapTipoObjetoCompraValorProcesso) {
        if (isOperacaoEditar()) {
            Map<TipoObjetoCompra, BigDecimal> mapTipoObjetoCompraValor = facade.getMapTipoObjetoCompraValor(selecionado.getSolicitacaoMaterial());
            mapTipoObjetoCompraValor.keySet()
                .stream()
                .filter(tipoObjProcesso -> !mapTipoObjetoCompraValorProcesso.containsKey(tipoObjProcesso))
                .forEach(tipoObjProcesso -> {
                DotacaoSolicitacaoMaterialItemVO novaDotTipoVO = new DotacaoSolicitacaoMaterialItemVO();
                novaDotTipoVO.setTipoObjetoCompra(tipoObjProcesso);
                novaDotTipoVO.setSubTipoObjetoCompra(selecionado.getSolicitacaoMaterial().getSubTipoObjetoCompra());
                Util.adicionarObjetoEmLista(dotacoesTipo, novaDotTipoVO);
            });

            dotacoesTipo.forEach(dotTipo -> {
                mapTipoObjetoCompraValor.keySet()
                    .stream()
                    .filter(tipoObjProc -> dotTipo.getTipoObjetoCompra().equals(tipoObjProc))
                    .forEach(tipoObjProc -> {
                    BigDecimal valorProcesso = mapTipoObjetoCompraValor.get(tipoObjProc);
                    BigDecimal valorAtualizado = dotTipo.getTipoObjetoCompra().equals(tipoObjProc) ? valorProcesso : BigDecimal.ZERO;
                    dotTipo.setValor(valorAtualizado);
                });
            });
        }
    }

    public boolean hasDotacaoFonteVONoAgruapdor(DotacaoSolicitacaoMaterialItemVO dotacaoItem, DotacaoSolicitacaoMaterialItemFonteVO dotFonte) {
        if (dotFonte.getFonteDespesaORC() != null) {
            return dotacaoItem.getAgrupadorFontes().stream()
                .flatMap(agrup -> agrup.getDotacoesFonte().stream())
                .anyMatch(font -> font.getFonteDespesaORC() != null && font.getFonteDespesaORC().equals(dotFonte.getFonteDespesaORC()));
        }
        return false;
    }

    private void ordenarDotacaoFonte() {
        dotacoesTipo.forEach(item -> Collections.sort(item.getAgrupadorFontes()));
        dotacoesTipo.forEach(item -> item.getAgrupadorFontes().forEach(agrup -> Collections.sort(agrup.getDotacoesFonte())));
    }

    public List<SolicitacaoMaterial> completarSolicitacaoCompra(String parte) {
        return facade.getSolicitacaoMaterialFacade().buscarSolicitacaoMaterialDisponivelParaReservaPorNumeroAndDescricao(parte.trim());
    }

    public void processarSolicitacaoCompra() {
        try {
            recuperarConfiguracaoReservaDotacao();
            validarObrigatoriedadeReservaDotacao();
            criarDotacaoSolicitacaoMaterialItem();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void recuperarConfiguracaoReservaDotacao() {
        configuracaoReservaDotacao = facade.getConfiguracaoLicitacaoFacade().buscarConfiguracaoReservaDotacao(selecionado.getSolicitacaoMaterial().getModalidadeLicitacao(), selecionado.getSolicitacaoMaterial().getTipoNaturezaDoProcedimento());
    }

    private void validarObrigatoriedadeReservaDotacao() {
        ValidacaoException ve = new ValidacaoException();
        if (isObrigaReservaExecucaoContrato()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para o(a) " + configuracaoReservaDotacao.getModalidadeLicitacao().getDescricao() + "/" + configuracaoReservaDotacao.getNaturezaProcedimento()
                + ", a reserva de dotação deve ser realizada na execução de contrato/sem contrato.");
            selecionado.setSolicitacaoMaterial(null);
        }
        ve.lancarException();
    }

    private boolean isObrigaReservaExecucaoContrato() {
        return configuracaoReservaDotacao != null && configuracaoReservaDotacao.getTipoReservaDotacao().isReservaExecucao();
    }

    private void criarDotacaoSolicitacaoMaterialItem() {
        setDotacoesTipo(Lists.newArrayList());
        Map<TipoObjetoCompra, BigDecimal> mapTipoObjetoCompraValor = facade.getMapTipoObjetoCompraValor(selecionado.getSolicitacaoMaterial());

        mapTipoObjetoCompraValor.keySet().forEach(tipoObjetoCompra -> {
            DotacaoSolicitacaoMaterialItemVO novoDotTipo = new DotacaoSolicitacaoMaterialItemVO();
            novoDotTipo.setTipoObjetoCompra(tipoObjetoCompra);
            novoDotTipo.setSubTipoObjetoCompra(selecionado.getSolicitacaoMaterial().getSubTipoObjetoCompra());
            novoDotTipo.setValor(mapTipoObjetoCompraValor.get(tipoObjetoCompra));
            dotacoesTipo.add(novoDotTipo);
        });
    }

    public void inserirFonte(DotacaoSolicitacaoMaterialItemVO dotTipo) {
        try {
            dotacaoTipo = dotTipo;
            dotacaoFonte = new DotacaoSolicitacaoMaterialItemFonteVO();
            dotacaoFonte.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
            dotacaoFonte.setValorReservado(dotacaoTipo.getValorAReservarOriginal());
            dotacaoFonte.setSaldoNormalReservado(dotacaoTipo.getValorAReservarOriginal());

            FacesUtil.executaJavaScript("dlgInserirFonte.show()");
            FacesUtil.atualizarComponente("formInserirFonte");
            FacesUtil.executaJavaScript("setaFoco('formInserirFonte:conta-dest')");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void adicionarFonte() {
        try {
            validarFonte();
            AgrupadorReservaSolicitacaoCompraVO novoAgrupador = novoAgrupadorReserva(dotacaoTipo.getTipoObjetoCompra(), dotacaoFonte.getExercicio(), dotacaoTipo.getAgrupadorFontes());
            Util.adicionarObjetoEmLista(novoAgrupador.getDotacoesFonte(), dotacaoFonte);
            Util.adicionarObjetoEmLista(dotacaoTipo.getAgrupadorFontes(), novoAgrupador);
            Collections.sort(dotacaoTipo.getAgrupadorFontes());
            if (dotacaoFonte.getFonteDespesaORC() != null) {
                fontesReprocessamento.add(dotacaoFonte.getFonteDespesaORC());
            }
            cancelarFonte();
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private AgrupadorReservaSolicitacaoCompraVO novoAgrupadorReserva(TipoObjetoCompra tipoObjetoCompra, Exercicio exercicio, List<AgrupadorReservaSolicitacaoCompraVO> agrupadorReservas) {
        AgrupadorReservaSolicitacaoCompraVO novoAgrupador = new AgrupadorReservaSolicitacaoCompraVO(tipoObjetoCompra, exercicio);
        for (AgrupadorReservaSolicitacaoCompraVO agrupList : agrupadorReservas) {
            if (agrupList.equals(novoAgrupador)) {
                novoAgrupador = agrupList;
                break;
            }
        }
        return novoAgrupador;
    }

    private void validarFonte() {
        ValidacaoException ve = new ValidacaoException();
        if (dotacaoFonte.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (dotacaoFonte.getTipoOperacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Operação deve ser informado.");
        }
        if (isExercicioFonteIgualExercicioLogado() && dotacaoFonte.getFonteDespesaORC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Destinação de Recurso deve ser informado.");
        }
        if (dotacaoFonte.getValorReservado() == null || dotacaoFonte.getValorReservado().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo valor deve ser informado e deve ser maior que zero(0).");
        }
        ve.lancarException();

        if (dotacaoFonte.getTipoOperacao().isNormal()) {
            for (AgrupadorReservaSolicitacaoCompraVO agrup : dotacaoTipo.getAgrupadorFontes()) {
                for (DotacaoSolicitacaoMaterialItemFonteVO dotFonte : agrup.getDotacoesFonte()) {
                    if (!dotFonte.equals(dotacaoFonte) && dotFonte.getFonteDespesaORC().equals(dotacaoFonte.getFonteDespesaORC())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A fonte despesa orçamentária já foi adicionada na lista.");
                        ve.lancarException();
                    }
                }
            }


            if (dotacaoFonte.getValorReservado().compareTo(dotacaoTipo.getValorProcessoOriginal()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Valor da Reserva' ultrapassa o valor total à reservar de " + Util.formataValor(dotacaoTipo.getValorProcessoOriginal()) + ".");

            } else if (isExercicioFonteIgualExercicioLogado() &&
                saldoFonteDespesaORC != null &&
                !isProcessoExercicioAnterior() &&
                dotacaoFonte.getValorReservado().compareTo(saldoFonteDespesaORC) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Valor da Reserva' ultrapassa o Saldo da Fonte de Recurso.");
            }
            ve.lancarException();

            if (dotacaoFonte.getValorReservado().compareTo(dotacaoFonte.getSaldoNormalReservado()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Valor da Reserva' ultrapassa o valor restante de " + Util.formataValor(dotacaoFonte.getSaldoNormalReservado()) + ".");
            }

        }
        if (dotacaoFonte.getTipoOperacao().isEstorno()) {
            BigDecimal saldoEstorno = dotacaoFonte.getSaldoEstornoReservado();
            if (dotacaoFonte.getValorReservado().compareTo(saldoEstorno) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor a estornar ultrapassa o saldo restante a estornar " + Util.formataValor(saldoEstorno) + ".");
            }
        }
        ve.lancarException();
    }

    public void cancelarFonte() {
        dotacaoTipo = null;
        dotacaoFonte = null;
        unidadeOrcamentaria = null;
        despesaORC = null;
        FacesUtil.executaJavaScript("dlgInserirFonte.hide()");
        FacesUtil.atualizarComponente("Formulario:panel-itens");
    }

    public void removerFonte(DotacaoSolicitacaoMaterialItemVO dotItem, DotacaoSolicitacaoMaterialItemFonteVO dotFonte) {
        dotItem.getAgrupadorFontes().forEach(agrup -> agrup.getDotacoesFonte().removeIf(fontVO -> fontVO.equals(dotFonte)));
        dotItem.getAgrupadorFontes().removeIf(agrup -> !agrup.hasDotacaoesFonte());
        if (dotFonte.getFonteDespesaORC() != null) {
            fontesReprocessamento.add(dotFonte.getFonteDespesaORC());
        }
    }

    public void estornarReserva(DotacaoSolicitacaoMaterialItemVO dotTipo, DotacaoSolicitacaoMaterialItemFonteVO dotFonte) {
        try {
            validarEstornoFonte(dotFonte);
            dotacaoTipo = dotTipo;
            dotacaoFonte = new DotacaoSolicitacaoMaterialItemFonteVO();
            dotacaoFonte.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
            if (isProcessoExercicioAnterior()) {
                dotacaoFonte.setDataLancamento(DataUtil.getDataComHoraAtual(facade.getSistemaFacade().getDataOperacao()));
            }
            dotacaoFonte.setValorReservado(dotFonte.getSaldoEstornoReservadoCalculado());
            dotacaoFonte.setSaldoEstornoReservado(dotFonte.getSaldoEstornoReservadoCalculado());
            dotacaoFonte.setFonteDespesaORC(dotFonte.getFonteDespesaORC());
            dotacaoFonte.setTipoOperacao(TipoOperacaoORC.ESTORNO);
            dotacaoFonte.setTipoReserva(TipoReservaSolicitacaoCompra.SOLICITACAO_COMPRA);

            fontesReprocessamento.add(dotFonte.getFonteDespesaORC());
            FacesUtil.executaJavaScript("dlgInserirFonte.show()");
            FacesUtil.atualizarComponente("formInserirFonte");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarEstornoFonte(DotacaoSolicitacaoMaterialItemFonteVO dotFonte) {
        ValidacaoException ve = new ValidacaoException();
        if (!dotFonte.getTipoReserva().isSolicitacaoCompra()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Estorno não Permitido!", "Só é permitido estornar uma reserva do tipo solicitação de compra, ou seja, que foi realizada no inicio do processo.");
            ve.adicionarMensagemWarn(" ", "Outros tipos de estornos, devem ser realizados em suas origens (Execução Contrato, Execução sem Contrato, Aditivo e Apostilamento).");
        }
        ve.lancarException();
    }

    public void editarFonte(DotacaoSolicitacaoMaterialItemVO dotTipo, DotacaoSolicitacaoMaterialItemFonteVO dotFonte) {
        dotacaoTipo = dotTipo;
        dotacaoFonte = (DotacaoSolicitacaoMaterialItemFonteVO) Util.clonarObjeto(dotFonte);
        dotacaoFonte.setSaldoNormalReservado(dotacaoTipo.getValorAReservarOriginal().add(dotFonte.getValorReservado()));
        if (dotFonte.getFonteDespesaORC() != null) {
            despesaORC = dotFonte.getFonteDespesaORC().getDespesaORC();
            unidadeOrcamentaria = despesaORC.getProvisaoPPADespesa().getUnidadeOrganizacional();
            buscarFontesDespesaOrc();
            buscarSaldoFonteDespesaORC();
            fontesReprocessamento.add(dotFonte.getFonteDespesaORC());
        }
        FacesUtil.executaJavaScript("dlgInserirFonte.show()");
        FacesUtil.atualizarComponente("formInserirFonte");
    }

    public List<SelectItem> getFontesDespesaOrcamentaria() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        if (despesaORC != null) {
            for (FonteDespesaORC fonte : fontesDespesaOrcamentaria) {
                toReturn.add(new SelectItem(fonte, fonte.getProvisaoPPAFonte().getDestinacaoDeRecursos().toString()));
            }
        }
        return toReturn;
    }

    public void buscarFontesDespesaOrc() {
        if (despesaORC != null) {
            fontesDespesaOrcamentaria = facade.getFonteDespesaORCFacade().completaFonteDespesaORC("", despesaORC);
        }
        FacesUtil.atualizarComponente("formInserirFonte:conta-dest");
        FacesUtil.executaJavaScript("setaFoco('formInserirFonte:conta-dest')");
    }

    public void buscarSaldoFonteDespesaORC() {
        setSaldoFonteDespesaORC(BigDecimal.ZERO);
        try {
            if (dotacaoFonte.getFonteDespesaORC() != null && dotacaoFonte.getTipoOperacao().isNormal()) {
                SaldoFonteDespesaORC saldo = this.facade.getSaldoFonteDespesaORCFacade().recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(
                    dotacaoFonte.getFonteDespesaORC(), this.selecionado.getRealizadaEm(),
                    despesaORC.getProvisaoPPADespesa().getUnidadeOrganizacional());

                BigDecimal saldoAtual = saldo != null ? saldo.getSaldoAtual() : BigDecimal.ZERO;

                Optional<DotacaoSolicitacaoMaterialItemFonteVO> first = dotacaoTipo.getAgrupadorFontes().stream()
                    .flatMap(ag -> ag.getDotacoesFonte().stream())
                    .filter(font -> font.getFonteDespesaORC() != null && font.getFonteDespesaORC().equals(dotacaoFonte.getFonteDespesaORC()) && font.getTipoOperacao().isNormal())
                    .findFirst();
                if (first.isPresent()) {
                    saldoAtual = saldoAtual.subtract(first.get().getValorReservado());
                }
                setSaldoFonteDespesaORC(saldoAtual);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void redirecionarVisualizar() {
        FacesUtil.redirecionamentoInterno("/dotacao-de-solicitacao-de-compra/ver/" + selecionado.getId() + "/");
    }

    private void validarDotacao() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        ve.lancarException();
        validarValoresDotacao();
    }

    private void validarValoresDotacao() {
        ValidacaoException ve = new ValidacaoException();
        for (DotacaoSolicitacaoMaterialItemVO dot : dotacoesTipo) {
            if (!dot.isReservadoTotalmente()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O tipo de objeto de compra: " + dot.getTipoObjetoCompra().getDescricao() + " não foi reservado completamente.");
            }
        }
        ve.lancarException();
    }

    public void atribuirNullParaDadosSolicitacao() {
        selecionado.setSolicitacaoMaterial(null);
        dotacoesTipo.clear();
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.RESERVA_SOLICITACAO_COMPRA);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

    public DotacaoSolicitacaoMaterialItemVO getDotacaoTipo() {
        return dotacaoTipo;
    }

    public void setDotacaoTipo(DotacaoSolicitacaoMaterialItemVO dotacaoTipo) {
        this.dotacaoTipo = dotacaoTipo;
    }

    public DotacaoSolicitacaoMaterialItemFonteVO getDotacaoFonte() {
        return dotacaoFonte;
    }

    public void setDotacaoFonte(DotacaoSolicitacaoMaterialItemFonteVO dotacaoFonte) {
        this.dotacaoFonte = dotacaoFonte;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public BigDecimal getSaldoFonteDespesaORC() {
        return saldoFonteDespesaORC;
    }

    public void setSaldoFonteDespesaORC(BigDecimal saldoFonteDespesaORC) {
        this.saldoFonteDespesaORC = saldoFonteDespesaORC;
    }

    public boolean hasValorOutrasReservas() {
        return dotacoesTipo.stream()
            .map(dotTipo -> dotTipo.getAgrupadorFontes().stream()
                .flatMap(ag -> ag.getDotacoesFonte().stream())
                .filter(font -> font.getTipoReserva().isOutrasReservas())
                .findFirst())
            .findFirst().filter(Optional::isPresent).isPresent();
    }

    public Boolean isProcessoExercicioAnterior() {
        return selecionado.getSolicitacaoMaterial().isExercicioProcessoDiferenteExercicioAtual(facade.getSistemaFacade().getDataOperacao());
    }

    public boolean isExercicioFonteIgualExercicioLogado() {
        return dotacaoFonte != null
            && dotacaoFonte.getExercicio() != null
            && facade.getSistemaFacade().getExercicioCorrente().equals(dotacaoFonte.getExercicio());
    }

    public void limparDotacaoFonte() {
        if (dotacaoFonte != null && !isExercicioFonteIgualExercicioLogado()) {
            dotacaoFonte.setFonteDespesaORC(null);
            dotacaoFonte.setValorReservado(this.dotacaoTipo.getValorAReservarOriginal());
        }
    }

    public void buscarMovimentosPorFonte(DotacaoSolicitacaoMaterialItemFonteVO fonte) {
        dotacaoFonte = fonte;

        FiltroDotacaoProcessoCompraVO filtro = new FiltroDotacaoProcessoCompraVO();
        filtro.setFonteDespesaORC(dotacaoFonte.getFonteDespesaORC());
        filtro.setSolicitacaoMaterial(selecionado.getSolicitacaoMaterial());

        List<MovimentoDespesaORCVO> movimentosDespesarOrc = facade.buscarMovimentosDespesasPorFonte(filtro);
        fonte.setMovimentosDespesarOrc(movimentosDespesarOrc);
        FacesUtil.executaJavaScript("dlgMovOrcProc.show()");
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public List<DotacaoSolicitacaoMaterialItemVO> getDotacoesTipo() {
        return dotacoesTipo;
    }

    public void setDotacoesTipo(List<DotacaoSolicitacaoMaterialItemVO> dotacoesTipo) {
        this.dotacoesTipo = dotacoesTipo;
    }


}
