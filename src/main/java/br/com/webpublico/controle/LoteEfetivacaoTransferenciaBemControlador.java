package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOItemSolicitacaoTransferencia;
import br.com.webpublico.enums.SituacaoDaSolicitacao;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LoteEfetivacaoTransferenciaBemFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 26/12/13
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "loteEfetivacaoTransferenciaBemControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaEfetivacaoTransferenciaMovel", pattern = "/efetivacao-de-transferencia-de-bem-movel/novo/", viewId = "/faces/administrativo/patrimonio/efetivacaotransferencia/movel/edita.xhtml"),
    @URLMapping(id = "editarEfetivacaoTransferenciaMovel", pattern = "/efetivacao-de-transferencia-de-bem-movel/editar/#{loteEfetivacaoTransferenciaBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaotransferencia/movel/edita.xhtml"),
    @URLMapping(id = "listarEfetivacaoTransferenciaMovel", pattern = "/efetivacao-de-transferencia-de-bem-movel/listar/", viewId = "/faces/administrativo/patrimonio/efetivacaotransferencia/movel/lista.xhtml"),
    @URLMapping(id = "verEfetivacaoTransferenciaMovel", pattern = "/efetivacao-de-transferencia-de-bem-movel/ver/#{loteEfetivacaoTransferenciaBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaotransferencia/movel/visualizar.xhtml"),

    @URLMapping(id = "novaEfetivacaoTransferenciaImovel", pattern = "/efetivacao-de-transferencia-de-bem-imovel/novo/", viewId = "/faces/administrativo/patrimonio/efetivacaotransferencia/imovel/edita.xhtml"),
    @URLMapping(id = "editarEfetivacaoTransferenciaImovel", pattern = "/efetivacao-de-transferencia-de-bem-imovel/editar/#{loteEfetivacaoTransferenciaBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaotransferencia/imovel/edita.xhtml"),
    @URLMapping(id = "listarEfetivacaoTransferenciaImovel", pattern = "/efetivacao-de-transferencia-de-bem-imovel/listar/", viewId = "/faces/administrativo/patrimonio/efetivacaotransferencia/imovel/lista.xhtml"),
    @URLMapping(id = "verEfetivacaoTransferenciaImovel", pattern = "/efetivacao-de-transferencia-de-bem-imovel/ver/#{loteEfetivacaoTransferenciaBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaotransferencia/imovel/visualizar.xhtml")
})
public class LoteEfetivacaoTransferenciaBemControlador extends PrettyControlador<LoteEfetivacaoTransferenciaBem> implements Serializable, CRUD {

    @EJB
    private LoteEfetivacaoTransferenciaBemFacade facade;
    private LoteTransferenciaBem solicitacaoTransferencia;
    private HierarquiaOrganizacional unidadeOrcamentariaDestino;
    private List<VOItemSolicitacaoTransferencia> bensSolicitacao;
    private Future<LoteEfetivacaoTransferenciaBem> futureEfetivacaoTransferencia;
    private Future<List<VOItemSolicitacaoTransferencia>> futurePesquisaLotes;
    private AssistenteMovimentacaoBens assistenteMovimentacao;
    private ConfigMovimentacaoBem configMovimentacaoBem;
    private List<HierarquiaOrganizacional> hierarquiasOrcamentarias;
    private List<LoteTransferenciaBem> solicitacoesTransferencia;
    private static String xhtmlNovoEditar;
    private static String MSG_VALIDACAO_UNIDADE_DESTINO_EXTERNA = "Para solicitações do tipo Externa, a Unidade Orçamentária de Destino não deve ser igual a Unidade Orçamentária de Origem.";
    private static String MSG_VALIDACAO_UNIDADE_DESTINO_INTERNA = "Para solicitações do tipo Interna, a Unidade Orçamentária de Destino deve ser igual a de Origem, quando esta estiver dentre as unidades listadas.";
    private boolean unidadeDestinoIgualOrigem = false;


    public LoteEfetivacaoTransferenciaBemControlador() {
        super(LoteEfetivacaoTransferenciaBem.class);
    }

    @URLAction(mappingId = "novaEfetivacaoTransferenciaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaEfetivacaoMovel() {
        novo();
        selecionado.setTipoBem(TipoBem.MOVEIS);
        xhtmlNovoEditar = "/administrativo/patrimonio/efetivacaotransferencia/movel/edita.xhtml";
    }

    @URLAction(mappingId = "novaEfetivacaoTransferenciaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaEfetivacaoImovel() {
        novo();
        selecionado.setTipoBem(TipoBem.IMOVEIS);
        xhtmlNovoEditar = "/administrativo/patrimonio/efetivacaotransferencia/imovel/edita.xhtml";
    }

    @Override
    public void novo() {
        try {
            super.novo();
            iniciarlizarAtributosOperacaoNovo();
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "verEfetivacaoTransferenciaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verEfetivacaoMovel() {
        super.ver();
        buscarSolicitacoesEfetivadas();
    }

    @URLAction(mappingId = "verEfetivacaoTransferenciaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verEfetivacaoImovel() {
        super.ver();
        buscarSolicitacoesEfetivadas();
    }

    @URLAction(mappingId = "editarEfetivacaoTransferenciaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarEfetivacaoMovel() {
        super.editar();
    }

    @URLAction(mappingId = "editarEfetivacaoTransferenciaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarEfetivacaoImovel() {
        super.editar();
    }

    @Override
    public void redireciona() {
        FacesUtil.executaJavaScript("closeDialog(dlgEfetivacao)");
        if (assistenteMovimentacao != null && assistenteMovimentacao.getSelecionado() != null && ((LoteEfetivacaoTransferenciaBem) assistenteMovimentacao.getSelecionado()).getId() != null && isOperacaoNovo()) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + ((LoteEfetivacaoTransferenciaBem) assistenteMovimentacao.getSelecionado()).getId() + "/");
        } else {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
        }
    }


    @Override
    public String getCaminhoPadrao() {
        try {
            switch (selecionado.getTipoBem()) {
                case IMOVEIS:
                    return "/efetivacao-de-transferencia-de-bem-imovel/";
                case MOVEIS:
                    return "/efetivacao-de-transferencia-de-bem-movel/";
                default:
                    return "";
            }
        } catch (Exception e) {
            return "/efetivacao-de-transferencia-de-bem-movel/";
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

    @Override
    public void salvar() {
        try {
            novoAssistneteMovimentacao();
            validarEfetivacao();
            atribuirValoresSolicitacao();
            atribuirTotalRegistrosAssistente();
            bloquearMovimentoSingleton();
            futureEfetivacaoTransferencia = facade.salvarEfetivacaoAsync(selecionado, bensSolicitacao, assistenteMovimentacao);
            FacesUtil.executaJavaScript("openDialog(dlgEfetivacao)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (MovimentacaoBemException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            redireciona();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentoSingleton();
            descobrirETratarException(e);
        }
    }

    private void buscarSolicitacoesEfetivadas() {
        solicitacoesTransferencia = Lists.newArrayList();
        if (selecionado.getSolicitacaoTransferencia() != null) {
            solicitacoesTransferencia.add(selecionado.getSolicitacaoTransferencia());
        } else {
            solicitacoesTransferencia = facade.getLoteTransferenciaFacade().buscarSolicitacaoTransferenciaPorEfetivacao(selecionado);
        }
    }

    public void buscarBensSolicitacaoEfetivados(LoteTransferenciaBem solicitacao) {
        bensSolicitacao = facade.buscarBensEfetivadosPorSolicitacaoTransferencia(solicitacao);
        solicitacaoTransferencia = solicitacao;
        FacesUtil.executaJavaScript("dlgInfoTransferencia.show()");
        FacesUtil.atualizarComponente("formInfoTransferencia");
    }

    public BigDecimal getValorTotalOriginal() {
        BigDecimal total = BigDecimal.ZERO;
        if (bensSolicitacao != null && !bensSolicitacao.isEmpty()) {
            for (VOItemSolicitacaoTransferencia item : bensSolicitacao) {
                total = total.add(item.getValorOriginal());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalAjuste() {
        BigDecimal total = BigDecimal.ZERO;
        if (bensSolicitacao != null && !bensSolicitacao.isEmpty()) {
            for (VOItemSolicitacaoTransferencia item : bensSolicitacao) {
                total = total.add(item.getValorAjuste());
            }
        }
        return total;
    }

    private void atribuirValoresSolicitacao() {
        selecionado.setSolicitacaoTransferencia(solicitacaoTransferencia);
    }

    private void atribuirTotalRegistrosAssistente() {
        assistenteMovimentacao.setTotal(bensSolicitacao.size());
    }

    public void atribuirUnidadeNaEfetivacao() {
        if (solicitacaoTransferencia != null) {
            selecionado.setUnidadeOrganizacional(solicitacaoTransferencia.getUnidadeDestino());
        }
    }

    private void bloquearMovimentoSingleton() {
        facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(LoteEfetivacaoTransferenciaBem.class, selecionado.getUnidadeOrganizacional(), assistenteMovimentacao);
    }

    private void desbloquearMovimentoSingleton() {
        if (selecionado.getUnidadeOrganizacional() != null) {
            facade.getSingletonBloqueioPatrimonio().desbloquearMovimentoPorUnidade(LoteEfetivacaoTransferenciaBem.class, selecionado.getUnidadeOrganizacional());
        }
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        ValidacaoException ve = new ValidacaoException();
        if (DataUtil.dataSemHorario(selecionado.getDataEfetivacao()).before(DataUtil.dataSemHorario(solicitacaoTransferencia.getDataHoraCriacao()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de efetivação da transferência deve ser posterior ou igual a data da solicitação de transferência n° " + solicitacaoTransferencia.getCodigo() + ".");
        }
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataEfetivacao(), getDataOperacao(), operacao);
        }
        ve.lancarException();
    }

    public void limparDadosUnidade() {
        selecionado.setUnidadeOrganizacional(null);
        limparDadosSolicitacao();
    }

    public void limparDadosSolicitacao() {
        setSolicitacaoTransferencia(null);
        bensSolicitacao = Lists.newArrayList();
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataEfetivacao(), OperacaoMovimentacaoBem.EFETIVACAO_TRANSFERENCIA_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    private void novoAssistneteMovimentacao() {
        if (configMovimentacaoBem == null) {
            recuperarConfiguracaoMovimentacaoBem();
        }
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataEfetivacao(), operacao);
        assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
        assistenteMovimentacao.zerarContadoresProcesso();
        assistenteMovimentacao.setDescricaoProcesso("Efetivando Transferência de Bem Móvel...");
        assistenteMovimentacao.setSelecionado(selecionado);
    }

    public void consultarFutureEfetivacao() {
        if (futureEfetivacaoTransferencia != null && futureEfetivacaoTransferencia.isDone()) {
            try {
                if (assistenteMovimentacao.getMensagens() != null && !assistenteMovimentacao.getMensagens().isEmpty()) {
                    ValidacaoException ve = new ValidacaoException();
                    for (String mensagemValidacao : assistenteMovimentacao.getMensagens()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida(mensagemValidacao);
                        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataEfetivacao(), operacao);
                    }
                    FacesUtil.executaJavaScript("closeDialog(dlgEfetivacao)");
                    FacesUtil.printAllFacesMessages(ve.getMensagens());
                } else {
                    LoteEfetivacaoTransferenciaBem efetivacaoSalva = futureEfetivacaoTransferencia.get();
                    if (efetivacaoSalva != null) {
                        FacesUtil.executaJavaScript("finalizarSalvar()");
                    }
                }
                desbloquearMovimentoSingleton();
                futureEfetivacaoTransferencia = null;
            } catch (Exception ex) {
                assistenteMovimentacao.descobrirETratarException(ex);
                assistenteMovimentacao.setBloquearAcoesTela(true);
                FacesUtil.executaJavaScript("clearInterval(timerSalvar)");
                FacesUtil.executaJavaScript("closeDialog(dlgEfetivacao)");
                FacesUtil.executaJavaScript("aguarde.hide()");
                futureEfetivacaoTransferencia = null;
                desbloquearMovimentoSingleton();
            }
        }
    }

    public void finalizarEfetivacao() {
        desbloquearMovimentoSingleton();
        redireciona();
        FacesUtil.addOperacaoRealizada("A efetivação de transferência código: " + selecionado.getCodigo() + " foi realizada com sucesso!");
        futureEfetivacaoTransferencia = null;
    }

    public void abrirDlgAtribuirUnidades() {
        if (bensSolicitacao == null || bensSolicitacao.isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("A efetivação não possui bens para atribuir unidade orçamentária de destino. Pesquise os bens para continuar.");
            return;
        }
        FacesUtil.executaJavaScript("dialogAtribuirUndOrc.show()");
        FacesUtil.atualizarComponente("formAtribuirUnidade");
    }

    private void validarEfetivacao() {
        ValidacaoException ve = new ValidacaoException();
        validarCamposObrigatorios(ve);
        validarUnidadeOrigemAndUnidadeDestino(ve);
        validarEntidadeCodigoPatrimonial(ve);
        validarDataLancamentoAndDataOperacaoBem();
        ve.lancarException();
        validarFase(ve);
    }

    private void validarFase(ValidacaoException ve) {
        Set<UnidadeOrganizacional> unidadesOrigem = Sets.newHashSet();
        for (VOItemSolicitacaoTransferencia vo : bensSolicitacao) {
            unidadesOrigem.add(vo.getUnidadeOrcamentariaOrigem().getSubordinada());
        }
        validarFasePorUnidade(ve, unidadesOrigem);
        ve.lancarException();
        Set<UnidadeOrganizacional> unidadesDestino = Sets.newHashSet();
        for (VOItemSolicitacaoTransferencia vo : bensSolicitacao) {
            unidadesDestino.add(vo.getUnidadeOrcamentariaDestino().getSubordinada());
        }
        validarFasePorUnidade(ve, unidadesDestino);
        ve.lancarException();
    }

    private void validarFasePorUnidade(ValidacaoException ve, Set<UnidadeOrganizacional> unidadesDestino) {
        for (UnidadeOrganizacional unidade : unidadesDestino) {
            Exercicio exercicio = facade.getExercicioFacade().getExercicioPorAno(DataUtil.getAno(selecionado.getDataEfetivacao()));
            if (facade.getFaseFacade().temBloqueioFaseParaRecurso(xhtmlNovoEditar, selecionado.getDataEfetivacao(), unidade, exercicio)) {
                HierarquiaOrganizacional hierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataEfetivacao(), unidade, TipoHierarquiaOrganizacional.ORCAMENTARIA);
                ve.adicionarMensagemDeOperacaoNaoRealizada("A data " + DataUtil.getDataFormatada(selecionado.getDataEfetivacao()) + " está fora do período fase para a unidade " + hierarquiaOrganizacional.toString() + "!");
                break;
            }
        }
    }

    private void validarEntidadeCodigoPatrimonial(ValidacaoException ve) {
        Entidade entidadeOrigem = facade.getEntidadeFacade().recuperarEntidadePorUnidadeOrganizacional(solicitacaoTransferencia.getUnidadeOrigem());
        Entidade entidadeDestino = facade.getEntidadeFacade().recuperarEntidadePorUnidadeOrganizacional(solicitacaoTransferencia.getUnidadeDestino());

        if (!solicitacaoTransferencia.getTransfHierarquiaEncerrada() && !entidadeOrigem.equals(entidadeDestino)) {
            EntidadeSequenciaPropria origem = facade.getParametroPatrimonioFacade().recuperarSequenciaPropriaPorTipoGeracao(entidadeOrigem, selecionado.getTipoBem());
            EntidadeSequenciaPropria destino = facade.getParametroPatrimonioFacade().recuperarSequenciaPropriaPorTipoGeracao(entidadeDestino, selecionado.getTipoBem());
            if (origem == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A unidade de origem da solicitação de transferência n°: " + solicitacaoTransferencia.getCodigo() + " não possui uma Sequência de Geração de Código de Identificação Patrimonial para entidade " + entidadeOrigem.getNome());
            }
            if (destino == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A unidade de destino da solicitação de transferência n°: " + solicitacaoTransferencia.getCodigo() + " não possui uma Sequência de Geração de Código de Identificação Patrimonial para entidade " + entidadeDestino.getNome());
            }
            if (origem != null && destino != null && !origem.equals(destino)) {
                ve.adicionarMensagemDeCampoObrigatorio("A unidade de origem e destino da solicitação de transferência n°: " + solicitacaoTransferencia.getCodigo() + " devem seguir a mesma Sequência de Geração de Código de Identificação Patrimonial.");
            }
        }
    }

    private void validarCamposObrigatorios(ValidacaoException ve) {
        if (solicitacaoTransferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Solicitação de Transferência deve ser informado.");
        }
        if (bensSolicitacao == null || bensSolicitacao.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A lista de bens para efetivação está vazia, selecione uma solicitação para buscar os bens para efetivar.");
        }
        ve.lancarException();
        for (VOItemSolicitacaoTransferencia item : bensSolicitacao) {
            if (item.getUnidadeOrcamentariaDestino() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade orçamentária deve ser preenchida para o bem: " + item.getBem());
            }
        }
        ve.lancarException();
    }

    private void validarUnidadeOrigemAndUnidadeDestino(ValidacaoException ve) {
        boolean controle = false;
        for (VOItemSolicitacaoTransferencia item : bensSolicitacao) {
            if (solicitacaoTransferencia.getTipoTransferencia().isInterna()) {
                if (!item.getUnidadeOrcamentariaOrigem().getSubordinada().equals(item.getUnidadeOrcamentariaDestino().getSubordinada())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A transferência não pode ser salva, pois transferências de unidades orçamentárias diferentes" +
                        " devem ser do tipo Externa e precisam de assinatura do responsável pelo Órgão de origem.");
                    ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, " Como a solicitação já foi aceita, será necessário excluir a aprovação para possibilitar a edição da solicitação.");
                    break;
                }
            } else {
                if (item.getUnidadeOrcamentariaOrigem().getSubordinada().equals(item.getUnidadeOrcamentariaDestino().getSubordinada())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O bem " + item.getBem() + ",  possui Unidade Orçamentária de Origem " +
                        "igual a Unidade Orçamentária de Destino <b> " + item.getUnidadeOrcamentariaDestino() + "</b>");
                    controle = true;
                }
            }
        }
        if (controle) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, " Caso a unidade de  origem e destino for a mesma. A solicitação de transferência deve ser do tipo interna.");
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, " Como a solicitação já foi aceita, será necessário excluir a aprovação para possibilitar a edição da solicitação.");
        }
        ve.lancarException();
    }

    private void iniciarlizarAtributosOperacaoNovo() {
        selecionado.setDataEfetivacao(getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataEfetivacao(), operacao);
    }

    private void validarPesquisaBens() {
        ValidacaoException ve = new ValidacaoException();
        if (solicitacaoTransferencia == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo solicitação de transferência deve ser informado.");
        }
        ve.lancarException();
        if (selecionado.getTipoBem().isMovel()) {
            if (!facade.getLoteTransferenciaFacade().getBemFacade().usuarioIsGestorUnidade(selecionado.getUsuarioSistema(), selecionado.getUnidadeOrganizacional(), getDataOperacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Você " + selecionado.getUsuarioSistema().getNome() + " não é gestor pela unidade organizacional " + selecionado.getUnidadeDestino() + " e portanto não tem autorizacão para efetivação da transferência.");
            }
        }
        if (selecionado.getTipoBem().isImovel()) {
            ResponsavelPatrimonio responsavelPatrimonio = facade.getParametroPatrimonioFacade().recuperarResponsavelVigente(selecionado.getUnidadeOrganizacional(), getDataOperacao());
            if (responsavelPatrimonio == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível recuperar o responsável do patrimônio: ");
            }
            if (responsavelPatrimonio == null || !selecionado.getUsuarioSistema().getPessoaFisica().equals(responsavelPatrimonio.getResponsavel())) {
                FacesUtil.addOperacaoNaoPermitida("Você " + selecionado.getUsuarioSistema().getNome() + " não é responsável pela unidade organizacional " + selecionado.getUnidadeDestino() + " e portanto não tem autorizacão para efetivação da transferência.");
            }
        }
        ve.lancarException();
    }

    public void pesquisar() {
        try {
            validarPesquisaBens();
            novoAssistneteMovimentacao();
            validarDataLancamentoAndDataOperacaoBem();
            hierarquiasOrcamentarias = facade.getHierarquiaOrganizacionalFacade().buscarFiltrandoHierarquiaOrcamentariaPorUnidadeAdministrativa("", solicitacaoTransferencia.getUnidadeDestino(), getDataOperacao());
            futurePesquisaLotes = facade.pesquisarBens(solicitacaoTransferencia, assistenteMovimentacao);
            FacesUtil.executaJavaScript("iniciarPesquisa()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void acompanharPesquisa() {
        if (futurePesquisaLotes != null && futurePesquisaLotes.isDone()) {
            if (!assistenteMovimentacao.getMensagens().isEmpty()) {
                futurePesquisaLotes = null;
                ValidacaoException ve = new ValidacaoException();
                for (String mensagem : assistenteMovimentacao.getMensagens()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
                }
                FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            }
            FacesUtil.executaJavaScript("terminarPesquisa()");
        }
    }

    public void finalizarPesquisa() throws ExecutionException, InterruptedException {
        if (futurePesquisaLotes != null) {
            bensSolicitacao = futurePesquisaLotes.get();
            atribuirUnidadeOrcamentariaDestinoIgualOrigem();
        }
    }

    public void abrirDlgDetalhesSolicitacao() {
        if (solicitacaoTransferencia == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo solicitação de transferência deve ser informado.");
            return;
        }
        FacesUtil.executaJavaScript("dlgInfoTransferencia.show()");
        FacesUtil.atualizarComponente("formInfoTransf");
    }

    public List<LoteTransferenciaBem> completarSolicitacaoTransferencia(String parte) {
        return facade.getLoteTransferenciaFacade().buscarSolicitacaoTransferenciaPorUnidade(parte.trim());
    }

    public List<SelectItem> getListSelectItemOpcaoAcaoTransferencia() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoDaSolicitacao situacao : SituacaoDaSolicitacao.values()) {
            if (SituacaoDaSolicitacao.ACEITA.equals(situacao) ||
                SituacaoDaSolicitacao.RECUSADA.equals(situacao)) {
                toReturn.add(new SelectItem(situacao, situacao.toString()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> retornaHierarquiaOrcamentaria() {
        if (solicitacaoTransferencia != null && solicitacaoTransferencia.getUnidadeDestino() != null) {
            List<SelectItem> toReturn = new ArrayList<>();
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : hierarquiasOrcamentarias) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
            return toReturn;
        }
        return null;
    }


    public void atribuirUnidadeOrcamentariaTodasTransferencia() {
        try {
            validarAtribuirUnidade();
            if (!bensSolicitacao.isEmpty()) {
                for (VOItemSolicitacaoTransferencia item : bensSolicitacao) {
                    item.setUnidadeOrcamentariaDestino(unidadeOrcamentariaDestino);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAtribuirUnidade() {
        ValidacaoException ve = new ValidacaoException();
        if (unidadeOrcamentariaDestino == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo unidade orçamentária de destino deve ser informado.");
        }
        ve.lancarException();
        for (VOItemSolicitacaoTransferencia bem : bensSolicitacao) {
            if (solicitacaoTransferencia.getTipoTransferencia().isExterna() && unidadeOrcamentariaDestino.equals(bem.getUnidadeOrcamentariaOrigem())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_UNIDADE_DESTINO_EXTERNA);
                ve.lancarException();
                break;
            } else if (solicitacaoTransferencia.getTipoTransferencia().isInterna() && unidadeDestinoIgualOrigem) {
                if (unidadeOrcamentariaDestino == null || !unidadeOrcamentariaDestino.equals(bem.getUnidadeOrcamentariaOrigem())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_UNIDADE_DESTINO_INTERNA);
                    ve.lancarException();
                    break;
                }
            }
        }
    }

    public void redirecionarParaEfetivacaoTransferenciaBemMovel(LoteEfetivacaoTransferenciaBem efetivacao) {
        this.selecionado = efetivacao;
        this.selecionado.setTipoBem(TipoBem.MOVEIS);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void gerarTermo(LoteTransferenciaBem loteTransferenciaBem) {
        try {
            String nomeRelatorio = "TERMO DE TRANSFERÊNCIA DE BENS " + selecionado.getTipoBem().getDescricao().toUpperCase();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("DATA_EFETIVACAO", DataUtil.getDataFormatada(selecionado.getDataEfetivacao()));
            dto.adicionarParametro("CODIGO_EFETIVACAO", selecionado.getCodigo().toString());
            dto.adicionarParametro("ENTIDADE", facade.getEntidadeFacade().recuperarEntidadePorUnidadeOrganizacional(loteTransferenciaBem.getUnidadeDestino()).getNome().toUpperCase());
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("ID", loteTransferenciaBem.getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/lote-efetivacao-transferencia/");
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

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteBarraProgresso) {
        this.assistenteMovimentacao = assistenteBarraProgresso;
    }

    public List<VOItemSolicitacaoTransferencia> getBensSolicitacao() {
        return bensSolicitacao;
    }

    public void setBensSolicitacao(List<VOItemSolicitacaoTransferencia> bensSolicitacao) {
        this.bensSolicitacao = bensSolicitacao;
    }

    public LoteTransferenciaBem getSolicitacaoTransferencia() {
        return solicitacaoTransferencia;
    }

    public void setSolicitacaoTransferencia(LoteTransferenciaBem solicitacaoTransferencia) {
        this.solicitacaoTransferencia = solicitacaoTransferencia;
    }

    public HierarquiaOrganizacional getUnidadeOrcamentariaDestino() {
        return unidadeOrcamentariaDestino;
    }

    public void setUnidadeOrcamentariaDestino(HierarquiaOrganizacional unidadeOrcamentariaDestino) {
        this.unidadeOrcamentariaDestino = unidadeOrcamentariaDestino;
    }

    public List<LoteTransferenciaBem> getSolicitacoesTransferencia() {
        return solicitacoesTransferencia;
    }

    public void setSolicitacoesTransferencia(List<LoteTransferenciaBem> solicitacoesTransferencia) {
        this.solicitacoesTransferencia = solicitacoesTransferencia;
    }

    public void atribuirUnidadeOrcamentariaDestinoIgualOrigem() {
        if (solicitacaoTransferencia.getTipoTransferencia().isInterna()) {
            if (bensSolicitacao != null && !bensSolicitacao.isEmpty()) {
                unidadeDestinoIgualOrigem = false;
                for (VOItemSolicitacaoTransferencia bem : bensSolicitacao) {
                    boolean conditionMet = false;
                    for (HierarquiaOrganizacional hoDestino : hierarquiasOrcamentarias) {
                        if (bem.getUnidadeOrcamentariaOrigem().equals(hoDestino)) {
                            bem.setUnidadeOrcamentariaDestino(hoDestino);
                            conditionMet = true;
                        }
                    }
                    if (conditionMet) {
                        unidadeDestinoIgualOrigem = true;
                    }
                }
            }
        }
    }

    public void validarUnidadeOrcamentariaDestino(VOItemSolicitacaoTransferencia bem) {
        if (solicitacaoTransferencia.getTipoTransferencia().isExterna()
            && bem.getUnidadeOrcamentariaDestino() != null && bem.getUnidadeOrcamentariaDestino().equals(bem.getUnidadeOrcamentariaOrigem())) {
            FacesUtil.addOperacaoNaoPermitida(MSG_VALIDACAO_UNIDADE_DESTINO_EXTERNA);
            bem.setUnidadeOrcamentariaDestino(null);
        }
    }

    public void validarUnidadeOrcamentariaDestinoIgualOrigem(VOItemSolicitacaoTransferencia bem) {
        if (solicitacaoTransferencia.getTipoTransferencia().isInterna() && hasUnidadeDestinoIgualOrigem()) {
            if (bem.getUnidadeOrcamentariaDestino() == null || !bem.getUnidadeOrcamentariaDestino().equals(bem.getUnidadeOrcamentariaOrigem())) {
                FacesUtil.addOperacaoNaoPermitida(MSG_VALIDACAO_UNIDADE_DESTINO_INTERNA);
                bem.setUnidadeOrcamentariaDestino(bem.getUnidadeOrcamentariaOrigem());
            }
        }
    }

    public boolean hasUnidadeDestinoIgualOrigem() {
        return unidadeDestinoIgualOrigem;
    }

    public void setUnidadeDestinoIgualOrigem(boolean unidadeDestinoIgualOrigem) {
        this.unidadeDestinoIgualOrigem = unidadeDestinoIgualOrigem;
    }
}
