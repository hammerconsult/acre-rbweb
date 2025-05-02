package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AjusteContratoAprovacaoVO;
import br.com.webpublico.entidadesauxiliares.ContratoValidacao;
import br.com.webpublico.entidadesauxiliares.ItemAjusteContratoDadosCadastraisVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AjusteContratoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-ajuste-contrato",
        pattern = "/ajuste-contrato/novo/",
        viewId = "/faces/administrativo/contrato/ajuste-contrato/edita.xhtml"),

    @URLMapping(id = "ver-ajuste-contrato",
        pattern = "/ajuste-contrato/ver/#{ajusteContratoControlador.id}/",
        viewId = "/faces/administrativo/contrato/ajuste-contrato/visualizar.xhtml"),

    @URLMapping(id = "editar-ajuste-contrato",
        pattern = "/ajuste-contrato/editar/#{ajusteContratoControlador.id}/",
        viewId = "/faces/administrativo/contrato/ajuste-contrato/edita.xhtml"),

    @URLMapping(id = "listar-ajuste-contrato",
        pattern = "/ajuste-contrato/listar/",
        viewId = "/faces/administrativo/contrato/ajuste-contrato/lista.xhtml")
})
public class AjusteContratoControlador extends PrettyControlador<AjusteContrato> implements Serializable, CRUD {

    @EJB
    private AjusteContratoFacade facade;
    private AjusteContratoDadosCadastrais ajustesDadosAlteracao;
    private AjusteContratoDadosCadastrais ajustesDadosOriginais;
    private AlteracaoContratual alteracaoContratual;
    private List<ItemAjusteContratoDadosCadastraisVO> itensAjusteDadosVO;
    private ObjetoCompra objetoCompra;
    private TipoControleLicitacao tipoControle;
    private boolean contratoSemExecucoesAndAlteracoes = false;
    private boolean permiteSubstituirObjetoCompra = false;
    private AjusteContratoAprovacaoVO ajusteContratoAprovacaoVO;
    private List<MovimentoAlteracaoContratualItem> itensMovimento;

    public AjusteContratoControlador() {
        super(AjusteContrato.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/ajuste-contrato/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novo-ajuste-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
        selecionado.setAjustesDadosCadastrais(Lists.newArrayList());
        selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica());
        itensAjusteDadosVO = Lists.newArrayList();
        novoAjusteDadosContrato();
        verificarConfiguracaoQuePermiteSubstituirObjetoCompra();
    }

    @Override
    @URLAction(mappingId = "ver-ajuste-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        recuperarAjusteDadosContrato();
        buscarItensAjusteDadosVisualizacao();
    }

    @Override
    @URLAction(mappingId = "editar-ajuste-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        recuperarAjusteDadosContrato();
        buscarItensAjusteDadosVO();
        buscarItensMovimentoAditivo();
        verificarContratoSemExecucoesAndAlteracoes();
        verificarConfiguracaoQuePermiteSubstituirObjetoCompra();
    }

    private void buscarItensAjusteDadosVisualizacao() {
        if (selecionado.isAjusteControleExecucao() || selecionado.isSubstituicaoObjetoCompra() || selecionado.isAjusteOperacaoAditivo()) {
            itensAjusteDadosVO = facade.buscarAjustesDadosOriginalPopulandoAjusteAlteracao(selecionado);
            if (selecionado.isAjusteControleExecucao() || selecionado.isSubstituicaoObjetoCompra()) {
                for (ItemAjusteContratoDadosCadastraisVO itemVO : itensAjusteDadosVO) {
                    facade.atribuirGrupoContaDespesaObjetoCompra(itemVO.getAjusteDadosOriginais().getObjetoCompra());
                    if (itemVO.getAjusteAlteracaoDados().getObjetoCompra() != null) {
                        facade.atribuirGrupoContaDespesaObjetoCompra(itemVO.getAjusteAlteracaoDados().getObjetoCompra());
                    }
                }
            }
            buscarItensMovimentoAditivo();
        }
    }

    private void verificarConfiguracaoQuePermiteSubstituirObjetoCompra() {
        ConfiguracaoSubstituicaoObjetoCompra configSubstituicaoObj = facade.getConfiguracaoLicitacaoFacade().buscarConfiguracaoSubstituicaoObjetoCompra(facade.getSistemaFacade().getUsuarioCorrente());
        permiteSubstituirObjetoCompra = configSubstituicaoObj != null;
    }

    public void recuperarDadosContrato() {
        try {

            selecionado.setContrato(facade.getContratoFacade().recuperarContratoComDependenciasEmpenho(selecionado.getContrato().getId()));
            facade.getContratoFacade().atribuirHierarquiaAdministrativaContrato(selecionado.getContrato());
            validarSolicitacaoEmpenhoEmpenhada();
            validarSeUltimoAjusteContratoEstaAprovado();
            verificarContratoSemExecucoesAndAlteracoes();
            criarAjusteContratoDadosOriginais();
            buscarItensAjusteDadosVO();
            if (selecionado.isAjusteControleExecucao()) {
                verificarControleDeExecucaoComTipoControleSomenteValor();
            }
        } catch (ValidacaoException ve) {
            setNullAjustes();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            setNullAjustes();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void recuperarDadosAlteracaoContratual() {
        try {
            setAlteracaoContratual(facade.getAlteracaoContratualFacade().recuperar(alteracaoContratual.getId()));

            if (alteracaoContratual != null && isOperacaoNovo()) {
                atribuirDadosAlteracaoContratualNoAjuste();
                validarSeUltimoAjusteContratoEstaAprovado();
                criarAjusteAlteracaoContratualDadosOriginais();

                buscarItensAjusteDadosVO();
                buscarItensMovimentoAditivo();
                ajustesDadosAlteracao.setSituacaoContrato(alteracaoContratual.getSituacao());
                validarOperacaoAditivo();
            }
        } catch (ValidacaoException ve) {
            setNullAjustes();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            setNullAjustes();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void recuperarDadosControleExecucao() {
        try {
            if ((selecionado.isAjusteControleExecucao() || selecionado.isSubstituicaoObjetoCompra()) && selecionado.getContrato() != null) {
                facade.getContratoFacade().atribuirHierarquiaAdministrativaContrato(selecionado.getContrato());
                buscarItensAjusteDadosVO();
                if (selecionado.isAjusteControleExecucao()) {
                    verificarControleDeExecucaoComTipoControleSomenteValor();
                }
                if (isOperacaoNovo()) {
                    validarSeUltimoAjusteContratoEstaAprovado();
                }
            }
        } catch (ValidacaoException ve) {
            setNullAjustes();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            setNullAjustes();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void verificarControleDeExecucaoComTipoControleSomenteValor() {
        if (itensAjusteDadosVO.isEmpty()) {
            setNullAjustes();
            FacesUtil.addAtencao("Este contrato não pode ser ajustado, pois não possui itens com o tipo controle por quantidade para ser ajustado para valor.");
        }
    }

    public void validarOperacaoAditivo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.isAjusteOperacaoAditivo()) {
            if (facade.getExecucaoContratoFacade().hasOrigemExecucaoComRequisicao(alteracaoContratual.getId())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("As operações deste aditivo não podem ser ajustadas, pois este está vinculado a uma Execução de Contrato que possui Requisição de Compra.");
            }
            if (itensAjusteDadosVO.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("As operações deste aditivo não podem ser ajustadas, pois este não possui movimentos com operação referente a valor.");
            }
        }
        ve.lancarException();
    }

    private void validarSeUltimoAjusteContratoEstaAprovado() {
        if (isOperacaoNovo()) {
            ValidacaoException ve = new ValidacaoException();
            AjusteContrato ajuste = facade.buscarUltimoAjusteContratoAprovado(selecionado);
            if (ajuste != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para lançar um novo ajuste é necessário que anterior esteja aprovado.");
            }
            ve.lancarException();
        }
    }

    private void validarSolicitacaoEmpenhoEmpenhada() {
        ValidacaoException ve = new ValidacaoException();
        List<ExecucaoContratoEmpenho> execucoes = facade.getContratoFacade().getExecucaoContratoFacade().buscarExecucaoContratoEmpenhoPorContrato(selecionado.getContrato());
        if (!Util.isListNullOrEmpty(execucoes) && execucoes.stream().anyMatch(exec -> exec.getEmpenho() == null)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível adicionar um ajuste antes de empenhar a(s) execução(ções) existente(s) do contrato.");
            setNullAjustes();
            ve.lancarException();
        }
    }

    private void setNullAjustes() {
        selecionado.setContrato(null);
        setAlteracaoContratual(null);
        setItensAjusteDadosVO(null);
    }

    public void definirAjusteContratoComoNull() {
        setNullAjustes();
        selecionado.setAjustesDadosCadastrais(Lists.newArrayList());
    }

    public List<SelectItem> getTiposAjuste() {
        List<SelectItem> list = Lists.newArrayList();
        list.add(new SelectItem(TipoAjusteContrato.CONTRATO, TipoAjusteContrato.CONTRATO.getDescricao()));
        list.add(new SelectItem(TipoAjusteContrato.ADITIVO, TipoAjusteContrato.ADITIVO.getDescricao()));
        list.add(new SelectItem(TipoAjusteContrato.APOSTILAMENTO, TipoAjusteContrato.APOSTILAMENTO.getDescricao()));
        list.add(new SelectItem(TipoAjusteContrato.CONTROLE_EXECUCAO, TipoAjusteContrato.CONTROLE_EXECUCAO.getDescricao()));
        list.add(new SelectItem(TipoAjusteContrato.OPERACAO_ADITIVO, TipoAjusteContrato.OPERACAO_ADITIVO.getDescricao()));
        if (permiteSubstituirObjetoCompra) {
            list.add(new SelectItem(TipoAjusteContrato.SUBSTITUICAO_OBJETO_COMPRA, TipoAjusteContrato.SUBSTITUICAO_OBJETO_COMPRA.getDescricao()));
        }
        return list;
    }

    public List<Contrato> completarContratos(String parte) {
        if (selecionado.getTipoAjuste().isControleExecucao()) {
            return facade.getContratoFacade().buscarFiltrandoPorSituacao(parte.trim(), SituacaoContrato.DEFERIDO, SituacaoContrato.APROVADO, SituacaoContrato.EM_ELABORACAO);
        }
        return facade.getContratoFacade().buscarFiltrandoPorSituacao(parte.trim(), SituacaoContrato.DEFERIDO, SituacaoContrato.APROVADO);
    }

    public List<AlteracaoContratual> completarAlteracaoContratual(String parte) {
        if (selecionado.getTipoAjuste().isAditivo() || selecionado.getTipoAjuste().isOperacaoAditivo()) {
            return facade.getAlteracaoContratualFacade().buscarAlteracaoContratualContratoPorSituacoes(parte.trim(), TipoAlteracaoContratual.ADITIVO, SituacaoContrato.DEFERIDO);
        }
        return facade.getAlteracaoContratualFacade().buscarAlteracaoContratualContratoPorSituacoes(parte.trim(), TipoAlteracaoContratual.APOSTILAMENTO, SituacaoContrato.DEFERIDO);
    }

    public void aprovarAjuste() {
        try {
            adicionarAjusteDadosContrato();
            popularAjusteAprovacaoVO();
            validarSalvar();
            selecionado.setDataAprovacao(new Date());
            FacesUtil.executaJavaScript("dlgAprovarAjuste.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void cancelarAprovacao() {
        selecionado.setDataAprovacao(null);
        FacesUtil.executaJavaScript("dlgAprovarAjuste.hide()");
    }

    public void confirmarAprovacao() {
        try {
            validarDataAprovacao();
            selecionado = facade.aprovarAjuste(selecionado, ajusteContratoAprovacaoVO);

            if (selecionado.isAjusteOperacaoAditivo()) {
                facade.getReprocessamentoSaldoContratoFacade().reprocessarUnicoContratoAlteracaoContratual(selecionado.getContrato(), alteracaoContratual);
            }
            FacesUtil.addOperacaoRealizada("Ajuste nº " + selecionado.getNumeroAjuste() + " foi salvo com sucesso.");
            redirecionarParaVer();
        } catch (ValidacaoException ve) {
            cancelarAprovacao();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void popularAjusteAprovacaoVO() {
        ajusteContratoAprovacaoVO = new AjusteContratoAprovacaoVO();
        Contrato contrato = selecionado.getContrato();

        if (selecionado.isAjusteContrato()) {
            AjusteContratoDadosCadastrais alteracaoDados = selecionado.getAjusteContratoDadosCadastraisDe(TipoAjusteDadosCadastrais.ALTERACAO);
            ajusteContratoAprovacaoVO.setAjusteDadosOriginal(selecionado.getAjusteContratoDadosCadastraisDe(TipoAjusteDadosCadastrais.ORIGINAL));
            ajusteContratoAprovacaoVO.setAjusteDadosAlteracao(alteracaoDados);

            contrato.setSituacaoContrato(alteracaoDados.getSituacaoContrato() != null ? alteracaoDados.getSituacaoContrato() : contrato.getSituacaoContrato());
            contrato.setNumeroTermo(!Util.isStringNulaOuVazia(alteracaoDados.getNumeroTermo()) ? alteracaoDados.getNumeroTermo() : contrato.getNumeroTermo());
            contrato.setExercicioContrato(Util.isNotNull(alteracaoDados.getExercicio()) ? alteracaoDados.getExercicio() : contrato.getExercicioContrato());
            contrato.setDataAprovacao(Util.isNotNull(alteracaoDados.getDataAprovacao()) ? alteracaoDados.getDataAprovacao() : contrato.getDataAprovacao());
            contrato.setDataDeferimento(Util.isNotNull(alteracaoDados.getDataDeferimento()) ? alteracaoDados.getDataDeferimento() : contrato.getDataDeferimento());
            contrato.setDataAssinatura(Util.isNotNull(alteracaoDados.getDataAssinatura()) ? alteracaoDados.getDataAssinatura() : contrato.getDataAssinatura());
            contrato.setInicioVigencia(Util.isNotNull(alteracaoDados.getInicioVigencia()) ? alteracaoDados.getInicioVigencia() : contrato.getInicioVigencia());
            contrato.setTerminoVigencia(Util.isNotNull(alteracaoDados.getTerminoVigencia()) ? alteracaoDados.getTerminoVigencia() : contrato.getTerminoVigencia());
            contrato.setTerminoVigenciaAtual(Util.isNotNull(alteracaoDados.getTerminoVigenciaAtual()) ? alteracaoDados.getTerminoVigenciaAtual() : contrato.getTerminoVigenciaAtual());
            contrato.setInicioExecucao(Util.isNotNull(alteracaoDados.getInicioExecucao()) ? alteracaoDados.getInicioExecucao() : contrato.getInicioExecucao());
            contrato.setTerminoExecucao(Util.isNotNull(alteracaoDados.getTerminoExecucao()) ? alteracaoDados.getTerminoExecucao() : contrato.getTerminoExecucao());
            contrato.setTerminoExecucaoAtual(Util.isNotNull(alteracaoDados.getTerminoExecucaoAtual()) ? alteracaoDados.getTerminoExecucaoAtual() : contrato.getTerminoExecucaoAtual());
            contrato.setObjeto(!Util.isStringNulaOuVazia(alteracaoDados.getObjeto()) ? alteracaoDados.getObjeto() : contrato.getObjeto());
            contrato.setObservacoes(!Util.isStringNulaOuVazia(alteracaoDados.getObservacao()) ? alteracaoDados.getObservacao() : contrato.getObservacoes());
            contrato.setResponsavelEmpresa(Util.isNotNull(alteracaoDados.getResponsavelEmpresa()) ? alteracaoDados.getResponsavelEmpresa() : contrato.getResponsavelEmpresa());
            contrato.setResponsavelPrefeitura(Util.isNotNull(alteracaoDados.getResponsavelPrefeitura()) ? alteracaoDados.getResponsavelPrefeitura() : contrato.getResponsavelPrefeitura());

            if (ajusteContratoAprovacaoVO.isAjusteAprovadoParaEmElaboracao()) {
                contrato.setNumeroTermo(null);
                contrato.setDataAprovacao(null);
                contrato.setDataAssinatura(null);
                contrato.setDataDeferimento(null);
                contrato.setSaldoAtualContrato(BigDecimal.ZERO);
                contrato.setValorAtualContrato(BigDecimal.ZERO);
                ajusteContratoAprovacaoVO.setItensContratoDeAprovadoParaEmElaboracao(facade.getContratoFacade().buscarItensContrato(contrato));
            }
            ajusteContratoAprovacaoVO.setContrato(contrato);

        } else if (selecionado.isAjusteAditivoOrApostilamento()) {
            AjusteContratoDadosCadastrais alteracaoDados = selecionado.getAjusteContratoDadosCadastraisDe(TipoAjusteDadosCadastrais.ALTERACAO);
            ajusteContratoAprovacaoVO.setAjusteDadosOriginal(selecionado.getAjusteContratoDadosCadastraisDe(TipoAjusteDadosCadastrais.ORIGINAL));
            ajusteContratoAprovacaoVO.setAjusteDadosAlteracao(alteracaoDados);

            AlteracaoContratual alteracaoCadastral = alteracaoDados.getAlteracaoContratual();
            alteracaoCadastral.setNumeroTermo(Util.isNotNull(alteracaoDados.getNumeroTermo()) ? alteracaoDados.getNumeroTermo() : alteracaoCadastral.getNumeroTermo());
            alteracaoCadastral.setExercicio(Util.isNotNull(alteracaoDados.getExercicio()) ? alteracaoDados.getExercicio() : alteracaoCadastral.getExercicio());
            alteracaoCadastral.setDataLancamento(Util.isNotNull(alteracaoDados.getDataLancamento()) ? alteracaoDados.getDataLancamento() : alteracaoCadastral.getDataLancamento());
            alteracaoCadastral.setDataAprovacao(Util.isNotNull(alteracaoDados.getDataAprovacao()) ? alteracaoDados.getDataAprovacao() : alteracaoCadastral.getDataAprovacao());
            alteracaoCadastral.setDataDeferimento(Util.isNotNull(alteracaoDados.getDataDeferimento()) ? alteracaoDados.getDataDeferimento() : alteracaoCadastral.getDataDeferimento());
            alteracaoCadastral.setDataAssinatura(Util.isNotNull(alteracaoDados.getDataAssinatura()) ? alteracaoDados.getDataAssinatura() : alteracaoCadastral.getDataAssinatura());
            if (selecionado.isAjusteAditivo()) {
                alteracaoCadastral.setJustificativa(!Util.isStringNulaOuVazia(alteracaoDados.getObservacao()) ? alteracaoDados.getObservacao() : alteracaoCadastral.getJustificativa());
            }
            ajusteContratoAprovacaoVO.setAlteracaoContratual(alteracaoCadastral);

        } else if (selecionado.isAjusteControleExecucao()) {
            itensAjusteDadosVO.forEach(itemVO -> {
                ItemContrato itemContrato = itemVO.getAjusteDadosOriginais().getItemContrato();
                AjusteContratoDadosCadastrais ajusteAlteracao = itemVO.getAjusteAlteracaoDados();

                if (Util.isNotNull(ajusteAlteracao.getTipoControle())) {
                    itemContrato.setTipoControle(Util.isNotNull(ajusteAlteracao.getTipoControle()) ? ajusteAlteracao.getTipoControle() : itemContrato.getTipoControle());
                    itemContrato.setQuantidadeTotalContrato(Util.isNotNull(ajusteAlteracao.getQuantidade()) ? ajusteAlteracao.getQuantidade() : itemContrato.getQuantidadeTotalContrato());
                    itemContrato.setValorUnitario(Util.isNotNull(ajusteAlteracao.getValorUnitario()) ? ajusteAlteracao.getValorUnitario() : itemContrato.getValorUnitario());
                    itemContrato.setDescricaoComplementar(!Util.isStringNulaOuVazia(ajusteAlteracao.getDescricaoComplementar()) ? ajusteAlteracao.getDescricaoComplementar() : itemContrato.getItemAdequado().getDescricaoComplementar());

                    itemVO.getAjusteAlteracaoDados().setItemContrato(itemContrato);
                    ajusteContratoAprovacaoVO.getItensAjusteDadosCadastrais().add(itemVO);
                }

            });
        } else if (selecionado.isSubstituicaoObjetoCompra()) {
            itensAjusteDadosVO.forEach(itemVO -> {
                ItemContrato itemContrato = itemVO.getAjusteDadosOriginais().getItemContrato();
                AjusteContratoDadosCadastrais ajusteAlteracao = itemVO.getAjusteAlteracaoDados();

                if (Util.isNotNull(ajusteAlteracao.getObjetoCompra()) || !Util.isStringNulaOuVazia(ajusteAlteracao.getDescricaoComplementar())) {
                    itemContrato.setObjetoCompraContrato(Util.isNotNull(ajusteAlteracao.getObjetoCompra()) ? ajusteAlteracao.getObjetoCompra() : itemContrato.getItemAdequado().getObjetoCompra());
                    itemContrato.setDescricaoComplementar(!Util.isStringNulaOuVazia(ajusteAlteracao.getDescricaoComplementar()) ? ajusteAlteracao.getDescricaoComplementar() : itemContrato.getItemAdequado().getDescricaoComplementar());

                    itemVO.getAjusteAlteracaoDados().setItemContrato(itemContrato);
                    ajusteContratoAprovacaoVO.getItensAjusteDadosCadastrais().add(itemVO);
                }
            });
        } else if (selecionado.isAjusteOperacaoAditivo()) {
            ajusteContratoAprovacaoVO.setAlteracaoContratual(alteracaoContratual);

            itensAjusteDadosVO.forEach(itemVO -> {
                MovimentoAlteracaoContratual movimentoAditivo = itemVO.getAjusteDadosOriginais().getMovimentoAlteracaoCont();
                AjusteContratoDadosCadastrais ajusteAlteracao = itemVO.getAjusteAlteracaoDados();

                if (Util.isNotNull(ajusteAlteracao.getOperacao())) {
                    movimentoAditivo.setOperacao(Util.isNotNull(ajusteAlteracao.getOperacao()) ? ajusteAlteracao.getOperacao() : movimentoAditivo.getOperacao());

                    itemVO.getAjusteAlteracaoDados().setMovimentoAlteracaoCont(movimentoAditivo);
                    ajusteContratoAprovacaoVO.getItensAjusteDadosCadastrais().add(itemVO);
                }
            });
        }
    }

    private void validarDataAprovacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataAprovacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de aprovação deve ser informado.");
        }
        ve.lancarException();
        if (DataUtil.dataSemHorario(selecionado.getDataAprovacao()).before(DataUtil.dataSemHorario(selecionado.getContrato().getInicioVigencia()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data de aprovação deve ser superior ou igual a da de inicío de vigência do contrato.");
        }
        if (selecionado.isAjusteContrato()) {
            facade.getContratoFacade().atribuirHierarquiaAdministrativaContrato(selecionado.getContrato());
            ContratoValidacao contratoValidacao = new ContratoValidacao(selecionado);
            if (!selecionado.getContrato().isContratoEmElaboracao()) {
                facade.getContratoFacade().validarNumeroTermoExistente(contratoValidacao);
                facade.getContratoFacade().validarDataAprovacao(contratoValidacao);
            }
            if (selecionado.getContrato().isContratoDeferido()) {
                facade.getContratoFacade().validarDataDeferimentoContrato(contratoValidacao);
            }
        }
        ve.lancarException();
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public void salvarAjuste() {
        try {
            adicionarAjusteDadosContrato();
            validarSalvar();
            selecionado = facade.salvarAjuste(selecionado);
            FacesUtil.addOperacaoRealizada("Ajuste nº " + selecionado.getNumeroAjuste() + " foi salvo com sucesso.");
            redirecionarParaVer();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void adicionarAjusteDadosContrato() {
        if (selecionado.isAjusteControleExecucao() || selecionado.isSubstituicaoObjetoCompra() || selecionado.isAjusteOperacaoAditivo()) {
            selecionado.setAjustesDadosCadastrais(new ArrayList<>());
            if (itensAjusteDadosVO != null) {
                for (ItemAjusteContratoDadosCadastraisVO item : itensAjusteDadosVO) {
                    item.getAjusteAlteracaoDados().setAjusteContrato(selecionado);
                    item.getAjusteDadosOriginais().setAjusteContrato(selecionado);
                    Util.adicionarObjetoEmLista(selecionado.getAjustesDadosCadastrais(), item.getAjusteAlteracaoDados());
                    Util.adicionarObjetoEmLista(selecionado.getAjustesDadosCadastrais(), item.getAjusteDadosOriginais());
                }
            }
        } else {
            Util.adicionarObjetoEmLista(selecionado.getAjustesDadosCadastrais(), ajustesDadosAlteracao);
        }
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        switch (selecionado.getTipoAjuste()) {
            case CONTRATO:
                if (selecionado.getContrato() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo contrato deve ser informado.");

                } else if (ajustesDadosAlteracao.hasCamposAlteracaoNulos()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório lançar alteração de dados para o " + selecionado.getTipoAjuste().getTitulo() + ".");
                }
                if (ajustesDadosAlteracao.getSituacaoContrato() != null && ajustesDadosAlteracao.getSituacaoContrato().isDeferido()) {
                    if (ajustesDadosAlteracao.getDataDeferimento() == null || ajustesDadosAlteracao.getDataAssinatura() == null) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato está sendo deferido. Por favor, informe a data de deferimento e assinatura do contrato.");
                    }
                    ve.lancarException();
                    ContratoValidacao contratoValidacao = new ContratoValidacao(selecionado);
                    facade.getContratoFacade().validarDataAprovacao(contratoValidacao);
                }
                break;
            case ADITIVO:
            case APOSTILAMENTO:
                if (alteracaoContratual == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo " + selecionado.getTipoAjuste().getTitulo() + " deve ser informado.");

                } else if (ajustesDadosAlteracao.hasCamposAlteracaoNulos()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório lançar alteração de dados para o " + selecionado.getTipoAjuste().getTitulo() + ".");

                } else if (alteracaoContratual.getDataAssinatura() != null && selecionado.getDataLancamento().before(DataUtil.dataSemHorario(alteracaoContratual.getDataAssinatura()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O ajuste deve ser lançado posterior a " + DataUtil.getDataFormatada(alteracaoContratual.getDataAssinatura())
                        + " referente a assinatura do " + selecionado.getTipoAjuste().getTitulo() + ".");
                }
                ve.lancarException();
                if (alteracaoContratual.getSituacao().isEmElaboracao()) {
                    facade.getAlteracaoContratualFacade().validarDataLancamento(alteracaoContratual, selecionado.getContrato());
                }
                if (alteracaoContratual.getSituacao().isAprovado()) {
                    facade.getAlteracaoContratualFacade().validarDataAprovacao(alteracaoContratual);
                }
                if (alteracaoContratual.getSituacao().isDeferido()) {
                    facade.getAlteracaoContratualFacade().validarDataAssinatura(alteracaoContratual);
                }
                break;
            case SUBSTITUICAO_OBJETO_COMPRA:
                if (selecionado.getContrato() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo contrato deve ser informado.");

                } else if (itensAjusteDadosVO.stream()
                    .noneMatch(item -> item.getAjusteAlteracaoDados().getObjetoCompra() != null
                        || !Util.isStringNulaOuVazia(item.getAjusteAlteracaoDados().getDescricaoComplementar()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório lançar alteração de dados a substituição do objeto de compra.");
                }
                break;
            case CONTROLE_EXECUCAO:
                if (selecionado.getContrato() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo contrato deve ser informado.");

                } else if (itensAjusteDadosVO.stream().noneMatch(item -> item.getAjusteAlteracaoDados().getTipoControle() != null)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório lançar alteração de dados o controle de execução.");
                }
                break;
            case OPERACAO_ADITIVO:
                if (alteracaoContratual == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Aditivo Contrato deve ser informado.");
                } else if (itensAjusteDadosVO.stream().noneMatch(mov -> mov.getAjusteAlteracaoDados().getOperacao() != null)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório lançar alteração de dados para a operação do aditivo.");
                }
                break;
        }
        ve.lancarException();
    }


    public void validarAlteracaoObjetoCompra(ItemAjusteContratoDadosCadastraisVO item) {
        if (item.getAjusteDadosOriginais().getObjetoCompra().equals(item.getAjusteAlteracaoDados().getObjetoCompra())) {
            if (item.getAjusteDadosOriginais().getDescricaoComplementar() != null && item.getAjusteDadosOriginais().getDescricaoComplementar().equals(item.getAjusteAlteracaoDados().getDescricaoComplementar())) {
                FacesUtil.addOperacaoNaoPermitida("Informe uma especificação diferente da original.");
                item.getAjusteAlteracaoDados().setDescricaoComplementar(null);
            }
        }
    }

    public void recuperarAjusteDadosContrato() {
        ajustesDadosAlteracao = selecionado.getAjusteContratoDadosCadastraisDe(TipoAjusteDadosCadastrais.ALTERACAO);
        ajustesDadosOriginais = selecionado.getAjusteContratoDadosCadastraisDe(TipoAjusteDadosCadastrais.ORIGINAL);

        if (selecionado.isAjusteAditivoOrApostilamento() || selecionado.isAjusteOperacaoAditivo()) {
            alteracaoContratual = selecionado.getAjustesDadosCadastrais().get(0).getAlteracaoContratual();
        }
    }

    public void novoAjusteDadosContrato() {
        ajustesDadosAlteracao = new AjusteContratoDadosCadastrais();
        ajustesDadosAlteracao.setAjusteContrato(selecionado);
    }

    private void criarAjusteContratoDadosOriginais() {
        Contrato contrato = selecionado.getContrato();
        ajustesDadosOriginais = new AjusteContratoDadosCadastrais();
        ajustesDadosOriginais.setTipoAjuste(TipoAjusteDadosCadastrais.ORIGINAL);
        ajustesDadosOriginais.setAjusteContrato(selecionado);
        ajustesDadosOriginais.setSituacaoContrato(contrato.getSituacaoContrato());
        ajustesDadosOriginais.setNumeroTermo(contrato.getNumeroTermo());
        ajustesDadosOriginais.setExercicio(contrato.getExercicioContrato());
        ajustesDadosOriginais.setDataLancamento(contrato.getDataLancamento());
        ajustesDadosOriginais.setDataAprovacao(contrato.getDataAprovacao());
        ajustesDadosOriginais.setDataDeferimento(contrato.getDataDeferimento());
        ajustesDadosOriginais.setDataAssinatura(contrato.getDataAssinatura());
        ajustesDadosOriginais.setInicioVigencia(contrato.getInicioVigencia());
        ajustesDadosOriginais.setTerminoVigencia(contrato.getTerminoVigencia());
        ajustesDadosOriginais.setTerminoVigenciaAtual(contrato.getTerminoVigenciaAtual());
        ajustesDadosOriginais.setInicioExecucao(contrato.getInicioExecucao());
        ajustesDadosOriginais.setTerminoExecucao(contrato.getTerminoExecucao());
        ajustesDadosOriginais.setTerminoExecucaoAtual(contrato.getTerminoExecucaoAtual());
        ajustesDadosOriginais.setObjeto(contrato.getObjeto());
        ajustesDadosOriginais.setResponsavelPrefeitura(contrato.getResponsavelPrefeitura());
        ajustesDadosOriginais.setResponsavelEmpresa(contrato.getResponsavelEmpresa());
        Util.adicionarObjetoEmLista(selecionado.getAjustesDadosCadastrais(), ajustesDadosOriginais);
    }

    private void criarAjusteAlteracaoContratualDadosOriginais() {
        ajustesDadosOriginais = new AjusteContratoDadosCadastrais();
        ajustesDadosOriginais.setTipoAjuste(TipoAjusteDadosCadastrais.ORIGINAL);
        ajustesDadosOriginais.setAjusteContrato(selecionado);
        ajustesDadosOriginais.setSituacaoContrato(alteracaoContratual.getSituacao());
        ajustesDadosOriginais.setNumeroTermo(alteracaoContratual.getNumeroTermo());
        ajustesDadosOriginais.setExercicio(alteracaoContratual.getExercicio());
        ajustesDadosOriginais.setDataLancamento(alteracaoContratual.getDataLancamento());
        ajustesDadosOriginais.setDataAprovacao(alteracaoContratual.getDataAprovacao());
        ajustesDadosOriginais.setDataDeferimento(alteracaoContratual.getDataDeferimento());
        ajustesDadosOriginais.setDataAssinatura(alteracaoContratual.getDataAssinatura());
        ajustesDadosOriginais.setObservacao(alteracaoContratual.getJustificativa());
        ajustesDadosOriginais.setAlteracaoContratual(alteracaoContratual);
        Util.adicionarObjetoEmLista(selecionado.getAjustesDadosCadastrais(), ajustesDadosOriginais);

        ajustesDadosAlteracao.setAlteracaoContratual(alteracaoContratual);
    }

    private void atribuirDadosAlteracaoContratualNoAjuste() {
        try {
            selecionado.setContrato(alteracaoContratual.getContrato());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void limparCamposAjusteContrato() {
        ajustesDadosAlteracao.setDataAssinatura(null);
        ajustesDadosAlteracao.setDataDeferimento(null);
    }

    public boolean hasItens() {
        return itensAjusteDadosVO != null && !itensAjusteDadosVO.isEmpty();
    }

    public List<ObjetoCompra> completarObjetoCompra(String parte) {
        return facade.getObjetoCompraFacade().buscarObjetoCompraPorSituacao(parte.trim(), SituacaoObjetoCompra.DEFERIDO);
    }

    public void listenerObjetoCompraContrato(ItemAjusteContratoDadosCadastraisVO item) {
        try {
            facade.atribuirGrupoContaDespesaObjetoCompra(item.getAjusteAlteracaoDados().getObjetoCompra());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void recuperarObjetoCompra(ItemAjusteContratoDadosCadastraisVO item) {
        if (item.getAjusteAlteracaoDados().getObjetoCompra() == null) {
            FacesUtil.addCampoObrigatorio("O campo Objeto de Compra Para deve ser informado.");
            return;
        }
        objetoCompra = facade.getObjetoCompraFacade().recuperar(item.getAjusteAlteracaoDados().getObjetoCompra().getId());
        FacesUtil.executaJavaScript("$('#modalTabelaEspecificacao').modal('show')");
    }

    public List<SelectItem> getTiposControle() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoControleLicitacao.VALOR, TipoControleLicitacao.VALOR.getDescricao()));
        return toReturn;
    }

    public List<SelectItem> getOperacoes() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (OperacaoMovimentoAlteracaoContratual op : OperacaoMovimentoAlteracaoContratual.getOperacoesValor()) {
            toReturn.add(new SelectItem(op, op.getDescricao()));
        }
        return toReturn;
    }

    public void aplicarTipoControleParaTodosItens() {
        boolean todosTipoControleValor = true;

        for (ItemAjusteContratoDadosCadastraisVO item : itensAjusteDadosVO) {
            if (item.getAjusteAlteracaoDados().getTipoControle() != TipoControleLicitacao.VALOR) {
                todosTipoControleValor = false;
                break;
            }
        }
        TipoControleLicitacao tipoControleSelecionado = todosTipoControleValor ? null : TipoControleLicitacao.VALOR;

        for (ItemAjusteContratoDadosCadastraisVO item : itensAjusteDadosVO) {
            recalcularValoresMovimentos(item, tipoControleSelecionado);
        }
    }

    public void recalcularValoresMovimentos(ItemAjusteContratoDadosCadastraisVO item, TipoControleLicitacao tipoControleSelecionado) {
        AjusteContratoDadosCadastrais ajuste = item.getAjusteAlteracaoDados();
        ajuste.setTipoControle(tipoControleSelecionado);
        if (tipoControleSelecionado == null) {
            ajuste.setQuantidade(item.getAjusteDadosOriginais().getQuantidade());
            ajuste.setValorUnitario(item.getAjusteDadosOriginais().getValorUnitario());
        } else {
            ajuste.setQuantidade(BigDecimal.ONE);
            ajuste.setValorUnitario(ajuste.getItemContrato().getValorTotal());
        }
    }

    public void selecionarEspecificacao(ActionEvent evento) {
        ObjetoDeCompraEspecificacao especificacao = (ObjetoDeCompraEspecificacao) evento.getComponent().getAttributes().get("objeto");
        itensAjusteDadosVO.stream()
            .filter(item -> item.getAjusteAlteracaoDados().getObjetoCompra() != null && item.getAjusteAlteracaoDados().getObjetoCompra().equals(objetoCompra))
            .forEach(item -> {
                item.getAjusteAlteracaoDados().setDescricaoComplementar(especificacao.getTexto());
                validarAlteracaoObjetoCompra(item);
            });
    }

    public void buscarItensAjusteDadosVO() {
        if (selecionado.isAjusteControleExecucao() || selecionado.isSubstituicaoObjetoCompra()) {
            itensAjusteDadosVO = facade.criarItemAjusteContratoDadosCadastraisVO(selecionado);
            if (selecionado.isSubstituicaoObjetoCompra()) {
                itensAjusteDadosVO.forEach(itemVO -> facade.atribuirGrupoContaDespesaObjetoCompra(itemVO.getAjusteDadosOriginais().getObjetoCompra()));
            }
        } else if (selecionado.isAjusteOperacaoAditivo()) {
            itensAjusteDadosVO = facade.criarMovimentosOperacaoAditivo(selecionado, alteracaoContratual);
        }
    }

    private void buscarItensMovimentoAditivo() {
        if (selecionado.isAjusteOperacaoAditivo() && !Util.isListNullOrEmpty(itensAjusteDadosVO)) {
            for (ItemAjusteContratoDadosCadastraisVO itemVO : itensAjusteDadosVO) {
                Long idMovimento = itemVO.getAjusteAlteracaoDados().getMovimentoAlteracaoCont().getId();
                itensMovimento = facade.getAlteracaoContratualFacade().buscarItensMovimento(idMovimento);
                if (itensMovimento != null) {
                    itemVO.getAjusteDadosOriginais().getMovimentoAlteracaoCont().setItens(itensMovimento);
                }
            }
        }
    }

    public void verificarContratoSemExecucoesAndAlteracoes() {
        if (selecionado.getTipoAjuste().isContrato()) {
            Contrato contrato = facade.getContratoFacade().recuperarContratoComDependenciasExecucao(selecionado.getContrato().getId());
            contratoSemExecucoesAndAlteracoes = !contrato.hasExecucoes() && !contrato.hasAlteracoesContratuais();
        }
    }

    public List<SelectItem> getSituacoesContrato() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(""));
        toReturn.add(new SelectItem(SituacaoContrato.DEFERIDO, SituacaoContrato.DEFERIDO.toString()));
        if (contratoSemExecucoesAndAlteracoes) {
            toReturn.add(new SelectItem(SituacaoContrato.EM_ELABORACAO, SituacaoContrato.EM_ELABORACAO.toString()));
        }
        return toReturn;
    }

    public void listenerTipoAjuste() {
        setNullAjustes();
    }

    public AjusteContratoDadosCadastrais getAjustesDadosAlteracao() {
        return ajustesDadosAlteracao;
    }

    public void setAjustesDadosAlteracao(AjusteContratoDadosCadastrais ajustesDadosAlteracao) {
        this.ajustesDadosAlteracao = ajustesDadosAlteracao;
    }

    public AjusteContratoDadosCadastrais getAjustesDadosOriginais() {
        return ajustesDadosOriginais;
    }

    public void setAjustesDadosOriginais(AjusteContratoDadosCadastrais ajustesDadosOriginais) {
        this.ajustesDadosOriginais = ajustesDadosOriginais;
    }

    public AlteracaoContratual getAlteracaoContratual() {
        return alteracaoContratual;
    }

    public void setAlteracaoContratual(AlteracaoContratual alteracaoContratual) {
        this.alteracaoContratual = alteracaoContratual;
    }

    public List<ItemAjusteContratoDadosCadastraisVO> getItensAjusteDadosVO() {
        return itensAjusteDadosVO;
    }

    public void setItensAjusteDadosVO(List<ItemAjusteContratoDadosCadastraisVO> itensAjusteDadosVO) {
        this.itensAjusteDadosVO = itensAjusteDadosVO;
    }


    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public List<ContratoFP> completarContratosFP(String parte) {
        return facade.getContratoFacade().getContratoFPFacade().buscarContratos(parte.trim());
    }

    public List<Pessoa> completaPessoaFisica(String parte) {
        return facade.getContratoFacade().getPessoaFacade().listaPessoasFisicas(parte.trim());
    }

    public AjusteContratoAprovacaoVO getAjusteContratoAprovacaoVO() {
        return ajusteContratoAprovacaoVO;
    }

    public void setAjusteContratoAprovacaoVO(AjusteContratoAprovacaoVO ajusteContratoAprovacaoVO) {
        this.ajusteContratoAprovacaoVO = ajusteContratoAprovacaoVO;
    }

    public List<MovimentoAlteracaoContratualItem> getItensMovimento() {
        return itensMovimento;
    }

    public void setItensMovimento(List<MovimentoAlteracaoContratualItem> itensMovimento) {
        this.itensMovimento = itensMovimento;
    }
}
