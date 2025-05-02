package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ExecucaoContratoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FlowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-execucao-contrato",
        pattern = "/execucao-contrato-adm/novo/",
        viewId = "/faces/administrativo/contrato/execucao-contrato-adm/edita.xhtml"),

    @URLMapping(id = "editar-execucao-contrato",
        pattern = "/execucao-contrato-adm/editar/#{execucaoContratoControlador.id}/",
        viewId = "/faces/administrativo/contrato/execucao-contrato-adm/edita.xhtml"),

    @URLMapping(id = "ver-execucao-contrato",
        pattern = "/execucao-contrato-adm/ver/#{execucaoContratoControlador.id}/",
        viewId = "/faces/administrativo/contrato/execucao-contrato-adm/visualizar.xhtml"),

    @URLMapping(id = "listar-execucao-contrato",
        pattern = "/execucao-contrato-adm/listar/",
        viewId = "/faces/administrativo/contrato/execucao-contrato-adm/lista.xhtml")
})
public class ExecucaoContratoControlador extends PrettyControlador<ExecucaoContrato> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ExecucaoContratoControlador.class);
    @EJB
    private ExecucaoContratoFacade facade;
    private ExecucaoContratoTipo execucaoContratoTipo;
    private ExecucaoContratoTipoFonte execucaoContratoTipoFonte;
    private ExecucaoContratoTipoFonte execucaoContratoTipoFonteSelecionada;
    private Contrato contrato;
    private Boolean podeReservarDotacao;
    private Boolean reservarDiferencaNaExecucao;
    private List<AgrupadorSolicitacaoEmpenho> agrupadoresSolicitacaoEmpenho;
    private List<ExecucaoContratoTipoFonte> reservasLicitacao;
    private ConverterAutoComplete converterFonteDespesaORC;
    private List<SaldoItemContratoOrigemVO> movimentosOrigemExecucao;
    private SaldoItemContratoOrigemVO movimentoOrigemExecucaoSelecionado;
    private FiltroResumoExecucaoVO filtroResumoExecucaoVO;
    private String origemContrato;
    private HashMap<ExecucaoContratoTipo, List<ExecucaoContratoItem>> mapExecucaoContratoTipoItens;

    public ExecucaoContratoControlador() {
        super(ExecucaoContrato.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-execucao-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        Object contrato = Web.pegaDaSessao("CONTRATO");
        podeReservarDotacao = false;
        reservarDiferencaNaExecucao = false;
        if (contrato != null) {
            this.contrato = (Contrato) contrato;
            recuperarDadosContrato();
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    @URLAction(mappingId = "ver-execucao-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarComDependencias(getId());
        Collections.sort(selecionado.getItens());
        classificarItensPorQuantidadeAndValor();
        movimentoOrigemExecucaoSelecionado = facade.recuperarMovimentoOrigemExecucao(selecionado);
        novoFiltroResumoExecucao();
        atribuirUnidadeContrato(selecionado.getContrato());
        origemContrato = facade.getContratoFacade().getOrigemContrato(selecionado.getContrato());
    }

    @URLAction(mappingId = "editar-execucao-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = facade.recuperarComDependencias(getId());
        redicionarParaVer();
        FacesUtil.addOperacaoNaoPermitida("Não é possível editar esta execução de contrato.");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/execucao-contrato-adm/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void salvarExecucoes() {
        try {
            validarSalvar();
            removerItemComValorZerado();
            selecionado = facade.salvarExecucaoContrato(selecionado);
            FacesUtil.executaJavaScript("dlgFinalizarProcesso.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void removerItemComValorZerado() {
        Iterator<ExecucaoContratoItem> iterator = selecionado.getItens().iterator();
        while (iterator.hasNext()) {
            ExecucaoContratoItem item = iterator.next();
            if (!item.hasValorTotal()) {
                iterator.remove();
            }
        }
    }

    private void validarItensExecucao() {
        ValidacaoException ve = new ValidacaoException();
        boolean hasValorMaiorQueZero = selecionado.getItens().stream()
                .anyMatch(item -> item.getValorTotal().compareTo(BigDecimal.ZERO) > 0);

        if (!hasValorMaiorQueZero) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para prosseguir, ao menos um dos itens deve possuir Valor Total(R$) maior que ZERO.");
        }
        ve.lancarException();
    }

    private void classificarItensPorQuantidadeAndValor() {
        for (ExecucaoContratoItem item : selecionado.getItens()) {
            facade.getContratoFacade().atribuirGrupoContaDespesa(item.getItemContrato());
            if (item.isExecucaoPorQuantidade()) {
                selecionado.getItensQuantidade().add(item);
                continue;
            }
            selecionado.getItensValor().add(item);
        }
    }

    @Override
    public void excluir() {
        try {
            Contrato contrato = selecionado.getContrato();
            facade.remover(selecionado);
            facade.getReprocessamentoSaldoContratoFacade().reprocessarUnicoContrato(contrato);
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public String onFlowProcess(FlowEvent event) {
        try {
            String newStep = event.getNewStep().toLowerCase();
            if (newStep.equalsIgnoreCase("dotacao")) {
                validarCampoContrato();
                validarRegrasGerais();
                selecionado.adicionarItensListaOriginal();
                validarItensExecucao();
                validarItensDiponiveisParaExecucao();
                validarGrupoContaDespesaObjetoCompra();

                if (!selecionado.hasReservas()) {
                    selecionado.calcularValorTotal();
                    novaExecucaoContratoTipo();
                }
                FacesUtil.atualizarComponente("Formulario:gridContrato");
            }
            if (newStep.equalsIgnoreCase("agrupador")) {
                if (execucaoContratoTipo != null) {
                    FacesUtil.addOperacaoNaoPermitida("Confirme a reserva de dotação no botão 'Confirmar' antes de continuar.");
                    return event.getOldStep();
                }
                for (ExecucaoContratoTipo reserva : selecionado.getReservas()) {
                    if (reserva.getValorTotalReservado().compareTo(reserva.getValor()) != 0) {
                        FacesUtil.addOperacaoNaoPermitida("As fontes não foram reservadas completamente para o tipo de objeto de compra " + reserva.getTipoObjetoCompra().getDescricao() + ".");
                        return event.getOldStep();
                    }
                }
                hasReservaInformadaComFonte();
                validarTotalReservaComTotalFontes();
                criarAgrupadorSolicitacaoEmpenho();
            }
            return newStep;
        } catch (ValidacaoException ve) {
            logger.debug("Problema ao avançar no wizard na execução de contrato, " + ve.getMessage());
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return event.getOldStep();
        } catch (Exception e) {
            logger.debug("Problema ao avançar no wizard na execução de contrato, " + e.getMessage());
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            return event.getOldStep();
        }
    }

    private void validarGrupoContaDespesaObjetoCompra() {
        ValidacaoException ve = new ValidacaoException();
        Set<String> msgs = Sets.newHashSet();
        selecionado.getItens().forEach(item -> {
            if (item.getObjetoCompra() != null
                && item.getObjetoCompra().getGrupoContaDespesa() != null
                && item.getObjetoCompra().getTipoObjetoCompra().isPermanenteOrConsumo()
                && item.getObjetoCompra().getGrupoContaDespesa().hasMensagensValidacao()) {
                msgs.addAll(item.getObjetoCompra().getGrupoContaDespesa().getMensagensValidacao());
            }
        });
        msgs.forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
        ve.lancarException();
    }

    private void criarAgrupadorSolicitacaoEmpenho() {
        agrupadoresSolicitacaoEmpenho = facade.criarAgrupadores(selecionado);

        for (AgrupadorSolicitacaoEmpenho agrupador : agrupadoresSolicitacaoEmpenho) {
            SolicitacaoEmpenhoVo solicitacaoEmpenhoVo = facade.criarVoSolicitacaoEmpenho(selecionado, agrupador);
            SolicitacaoEmpenho solicitacaoEmpenho = facade.getSolicitacaoEmpenhoFacade().gerarSolicitacaoEmpenho(solicitacaoEmpenhoVo);
            agrupador.setSolicitacaoEmpenho(solicitacaoEmpenho);
        }
    }

    public BigDecimal getValorTotalAgrupador() {
        BigDecimal total = BigDecimal.ZERO;
        if (agrupadoresSolicitacaoEmpenho != null && !agrupadoresSolicitacaoEmpenho.isEmpty()) {
            for (AgrupadorSolicitacaoEmpenho agrupador : agrupadoresSolicitacaoEmpenho) {
                total = total.add(agrupador.getValor());
            }
        }
        return total;
    }

    public void redirecionarParaExecucao(ExecucaoContrato execucaoContrato) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + execucaoContrato.getId() + "/");
    }

    public String getCaminhoMovimentoOrigemExecucao() {
        String caminho;
        if (movimentoOrigemExecucaoSelecionado == null) {
            return "";
        }
        switch (movimentoOrigemExecucaoSelecionado.getOrigem()) {
            case ADITIVO:
                caminho = "/aditivo-contrato/ver/";
                break;
            case APOSTILAMENTO:
                caminho = "/apostilamento-contrato/ver/";
                break;
            default:
                caminho = "/contrato-adm/ver/";
                break;
        }
        return caminho + movimentoOrigemExecucaoSelecionado.getIdMovimentoOrigem() + "/";
    }

    public void redirecionarOrigemContrato() {
        if (selecionado.getContrato().isDeLicitacao() || selecionado.getContrato().isDeAdesaoAtaRegistroPrecoInterna() || selecionado.getContrato().isDeIrp()) {
            FacesUtil.redirecionamentoInterno("/licitacao/ver/" + selecionado.getContrato().getLicitacao().getId() + "/");
            return;
        }
        if (selecionado.getContrato().isDeDispensaDeLicitacao()) {
            FacesUtil.redirecionamentoInterno("/dispensa-licitacao/ver/" + selecionado.getContrato().getDispensaDeLicitacao().getId() + "/");
            return;
        }
        if (selecionado.getContrato().isDeRegistroDePrecoExterno()) {
            FacesUtil.redirecionamentoInterno("/adesao-a-ata-de-registro-de-preco-externo/ver/" + selecionado.getContrato().getContratoRegistroPrecoExterno().getRegistroSolicitacaoMaterialExterno().getId() + "/");
            return;
        }
        if (selecionado.getContrato().isDeProcedimentosAuxiliares()) {
            FacesUtil.redirecionamentoInterno("/credenciamento/ver/" + selecionado.getContrato().getLicitacao().getId() + "/");
        }
    }

    public void redicionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void lancarNovaExecucao() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "novo/");
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        validarCampoContrato();
        if (!selecionado.hasReservas()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A execução: " + selecionado + " não possui reserva de dotação adicionada.");
        }
        ve.lancarException();
        validarTotalReservaComTotalFontes();
        if (agrupadoresSolicitacaoEmpenho == null || agrupadoresSolicitacaoEmpenho.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar é necessário gerar o agrupador de solicitação de empenho após informar as fontes.");
        }
        ve.lancarException();
    }

    private void validarTotalReservaComTotalFontes() {
        ValidacaoException ve = new ValidacaoException();
        for (ExecucaoContratoTipo reserva : selecionado.getReservas()) {
            if (reserva.getFontes() == null || reserva.getFontes().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A reserva: " + reserva + "  não possui fontes adicionadas.");
            }
            BigDecimal valorTotalFonte = BigDecimal.ZERO;
            for (ExecucaoContratoTipoFonte fonte : reserva.getFontes()) {
                valorTotalFonte = valorTotalFonte.add(fonte.getValor());
            }
            if (reserva.getValor().compareTo(valorTotalFonte) != 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor reservado nas fontes deve ser o mesmo da reserva: " + Util.formataValor(reserva.getValor()) + ".");
            }
        }
        ve.lancarException();
    }

    public boolean isControlePorQuantidade() {
        if (selecionado != null && selecionado.getItens() != null && !selecionado.getItens().isEmpty()) {
            for (ExecucaoContratoItem item : selecionado.getItens()) {
                return item.isExecucaoPorQuantidade();
            }
        }
        return false;
    }

    public List<SelectItem> getClassesCredor() {
        List<SelectItem> list = new ArrayList<>();
        list.add(new SelectItem(null, ""));
        if (contrato != null && contrato.getContratado() != null) {
            List<ClasseCredor> classes = facade.getClasseCredorFacade().buscarClassesPorPessoa("", contrato.getContratado());
            if (classes.isEmpty()) {
                FacesUtil.addAtencao("O fornecedor do contrato: " + contrato.getContratado() + " não possui classe credor em seu cadastro.");
                return list;
            }
            for (ClasseCredor classeCredor : classes) {
                list.add(new SelectItem(classeCredor));
            }
        }
        return list;
    }

    public List<SelectItem> getFormasEntrega() {
        return Util.getListSelectItem(FormaEntregaExecucao.values());
    }

    public List<Contrato> completarContrato(String parte) {
        return facade.getContratoFacade().buscarFiltrandoPorSituacao(parte.trim(), SituacaoContrato.situacoesDiferenteEmElaboracao);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        if (contrato != null) {
            return facade.getHierarquiaOrganizacionalFacade().buscarFiltrandoHierarquiaOrcamentariaPorUnidadeAdministrativa(parte.trim(), contrato.getUnidadeAdministrativa().getSubordinada(), selecionado.getDataLancamento());
        }
        return Lists.newArrayList();
    }

    public void novaExecucaoContrato() {
        try {
            validarCampoContrato();
            selecionado = facade.criarExecucaoContrato(contrato, movimentoOrigemExecucaoSelecionado);
            validarItensDiponiveisParaExecucao();
            selecionado.calcularValorTotal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addAtencao(e.getMessage());
        } catch (IndexOutOfBoundsException a) {
            FacesUtil.addOperacaoNaoPermitida("O contrato selecionado não possui saldo para gerar os itens desta execução.");
            contrato = null;
            FacesUtil.executaJavaScript("limparDadosContrato()");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }

    }

    private void buscarMovimentoOrigemExecucao() {
        if (contrato != null) {
            movimentosOrigemExecucao = facade.getSaldoItemContratoFacade().buscarSaldoOrigemVO(contrato, SubTipoSaldoItemContrato.EXECUCAO);
        }
        finalizarBuscaMovimentoOrigemExecucao();
    }

    private void finalizarBuscaMovimentoOrigemExecucao() {
        if (movimentosOrigemExecucao.size() > 1) {
            FacesUtil.executaJavaScript("dlgOrigemExecucao.show()");
            FacesUtil.atualizarComponente("formOrigemExecucao");
        } else {
            movimentoOrigemExecucaoSelecionado = movimentosOrigemExecucao.get(0);
            novaExecucaoContrato();
            atribuirGrupoContaDespesaItemExecucao(selecionado.getItensQuantidade());
            atribuirGrupoContaDespesaItemExecucao(selecionado.getItensValor());
        }
    }

    private void atribuirGrupoContaDespesaItemExecucao(List<ExecucaoContratoItem> itens) {
        for (ExecucaoContratoItem item : itens) {
            facade.getContratoFacade().atribuirGrupoContaDespesa(item.getItemContrato());
        }
    }

    private void validarItensDiponiveisParaExecucao() {
        ValidacaoException ve = new ValidacaoException();
        if (!hasItensExecucaoQuantidade() && !hasItensExecucaoValor()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O fornecedor: " + contrato.getContratado() + " não possui itens disponíveis para execução.");
        }
        ve.lancarException();
    }

    public void recuperarDadosContrato() {
        try {
            contrato = facade.getContratoFacade().recuperarContratoComDependenciasExecucao(contrato.getId());
            if (contrato != null) {
                validarLancamentoEmExercicioAnterior();
                verificarSeContratoPossuiAlteracaoContratualPendendeAprovacao(contrato);
                selecionado.setContrato(contrato);
                facade.getExecucaoContratoEstornoFacade().verificarSeExisteSolicitacaoEstornoExecucaoPendentes(selecionado);
                buscarMovimentoOrigemExecucao();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (IndexOutOfBoundsException a) {
            FacesUtil.addOperacaoNaoPermitida("O contrato selecionado não possui saldo para gerar os itens para esta execuçao.");
            FacesUtil.executaJavaScript("limparDadosContrato()");
        } catch (Exception e) {
            descobrirETratarException(e);
            logger.error("Execução contrato, recuperar dados do contrato {}", e);
        }
    }

    private void validarLancamentoEmExercicioAnterior() {
        ValidacaoException ve = new ValidacaoException();
        if (Util.isExercicioLogadoDiferenteExercicioAtual(facade.getSistemaFacade().getDataOperacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido realizar uma execução de contrato para exercício diferente do exercício atual.");
        }
        ve.lancarException();
    }

    private void verificarSeContratoPossuiAlteracaoContratualPendendeAprovacao(Contrato contrato) {
        ValidacaoException ve = new ValidacaoException();
        List<AlteracaoContratual> alteracoes = facade.getAlteracaoContratualFacade().buscarAlteracoesContratuaisPorContrato(contrato);
        if (!Util.isListNullOrEmpty(alteracoes)) {
            alteracoes.stream()
                .filter(alt -> alt.getSituacao().isEmElaboracao())
                .map(alt -> "O " + alt.getDescricaoTipoTermo() + " nº " + alt.getNumeroAnoTermo()
                    + " encontra-se " + alt.getSituacao().getDescricao() + ". Para continuar é necessário aprová-lo primeiro.")
                .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
        }

        List<AjusteContrato> ajustes = facade.getAjusteContratoFacade().buscarAjusteContratoPorSituacao(contrato, SituacaoAjusteContrato.EM_ELABORACAO);
        if (!Util.isListNullOrEmpty(ajustes)) {
            ajustes.stream()
                .map(alt -> "O ajuste nº " + alt.getNumeroAjuste() + " encontra-se " + alt.getSituacao().getDescricao() + ". Para continuar é necessário aprová-lo primeiro.")
                .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
        }
        ve.lancarException();
    }

    public void definirExecucaoContratoComoNull() {
        contrato = null;
        selecionado.setItens(Lists.<ExecucaoContratoItem>newArrayList());
        selecionado.setItensQuantidade(Lists.<ExecucaoContratoItem>newArrayList());
        selecionado.setItensValor(Lists.<ExecucaoContratoItem>newArrayList());
        selecionado.setReservas(Lists.<ExecucaoContratoTipo>newArrayList());
    }

    private void validarCampoContrato() {
        ValidacaoException ve = new ValidacaoException();
        if (contrato == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Contrato deve ser informado.");
        }
        ve.lancarException();
    }

    public void confirmarOrigemExecucaoContrato() {
        try {
            validarOrigemExecucao();
            FacesUtil.executaJavaScript("dlgOrigemExecucao.hide()");
            FacesUtil.atualizarComponente("Formulario:tab-view-geral:panel-execucao");
            novaExecucaoContrato();
            atribuirGrupoContaDespesaItemExecucao(selecionado.getItensQuantidade());
            atribuirGrupoContaDespesaItemExecucao(selecionado.getItensValor());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarOrigemExecucaoContrato() {
        try {
            setMovimentoOrigemExecucaoSelecionado(null);
            definirExecucaoContratoComoNull();
            FacesUtil.executaJavaScript("dlgOrigemExecucao.hide()");
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarOrigemExecucao() {
        ValidacaoException ve = new ValidacaoException();
        if (movimentoOrigemExecucaoSelecionado == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um movimento de origem da execução.");
        }
        ve.lancarException();
    }

    private void validarRegrasGerais() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFormaEntrega() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Forma de Entrega deve ser informado.");
        }
        if (selecionado.getUnidadeOrcamentaria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Orçamentária deve ser informado.");
        }
        ve.lancarException();
        if (contrato.getSaldoAtualContrato().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido fazer execução do contrato com saldo atual igual a zero.");
        }
        if (selecionado.getValor().compareTo(contrato.getSaldoAtualContrato()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível realizar execução maior que o saldo atual do contrato. Nesse momento, o contrato possui saldo atual de "
                + Util.formataValor(contrato.getSaldoAtualContrato()) + ", menor que o valor executado de " + Util.formataValor(selecionado.getValor()) + ".");
        }
        ve.lancarException();
    }

    public void processarValorTotalExecucaoContratoItem(ExecucaoContratoItem item) {
        try {
            validarValorTotalExecucaoContratoItem(item);
            selecionado.calcularValorTotal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            item.setValorTotal(item.getValorDisponivel());
            selecionado.calcularValorTotal();
        }
    }

    public boolean hasSolicitacaoEmpenhoEmpenhada() {
        return isOperacaoVer() && selecionado.hasSolicitacaoEmpenhoEmpenhada();
    }

    public void removerItemDotacao(ExecucaoContratoItemDotacao itemDotacao) {
        execucaoContratoTipoFonte.getItens().remove(itemDotacao);
        execucaoContratoTipoFonte.setValor(execucaoContratoTipoFonte.getValorTotalItemValor());
    }


    public void processarValorItemDotacao(ExecucaoContratoItemDotacao itemDotacao) {
        try {
            validarValorItemDotacao(itemDotacao);
            itemDotacao.setValorUnitario(itemDotacao.getExecucaoContratoItem().isExecucaoPorQuantidade() ? itemDotacao.getExecucaoContratoItem().getValorUnitario() : itemDotacao.getValorTotal());
            itemDotacao.setQuantidade(itemDotacao.getExecucaoContratoItem().isExecucaoPorQuantidade() ? itemDotacao.getQuantidadeCalculada() : BigDecimal.ONE);
            execucaoContratoTipoFonte.setValor(execucaoContratoTipoFonte.getValorTotalItemValor());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            FacesUtil.atualizarComponente("Formulario:tab-view-geral:valorReserva");
        }
    }

    private void validarValorItemDotacao(ExecucaoContratoItemDotacao itemDotacao) {
        ValidacaoException ve = new ValidacaoException();
        if (itemDotacao.getValorTotal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo valor deve ser informado.");
        }
        if (itemDotacao.getValorTotal() != null
            && itemDotacao.getValorDisponivel() != null
            && itemDotacao.getValorTotal().compareTo(itemDotacao.getValorDisponivel()) > 0) {
            itemDotacao.setValorUnitario(BigDecimal.ZERO);
            itemDotacao.setValorTotal(BigDecimal.ZERO);
            FacesUtil.addOperacaoNaoPermitida("A valor deve ser no máximo o valor disponível. (<b>" + Util.formataValor(itemDotacao.getValorDisponivel()) + "</b>)");
        }
        ve.lancarException();
    }

    public void processarQuantidadeExecucaoContratoItem(ExecucaoContratoItem item) {
        try {
            validarQuantidadeExecucaoContratoItem(item);
            item.calcularValorTotal();
            selecionado.calcularValorTotal();
        } catch (ValidacaoException ve) {
            item.setQuantidadeUtilizada(item.getQuantidadeDisponivel());
            item.calcularValorTotal();
            selecionado.calcularValorTotal();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarQuantidadeExecucaoContratoItem(ExecucaoContratoItem item) {
        ValidacaoException ve = new ValidacaoException();
        if (item.getQuantidadeUtilizada() == null || item.getQuantidadeUtilizada().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A 'Quantidade' do item da execução " + item + " deve ser informada e não pode ser negativo.");
        }
        if (item.getQuantidadeDisponivel().compareTo(item.getQuantidadeUtilizada()) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Quantidade' do item da execução " + item + " ultrapassa a quantidade disponível de " + Util.formataValor(item.getQuantidadeDisponivel()) + ".");
        }
        ve.lancarException();
    }

    private void validarValorTotalExecucaoContratoItem(ExecucaoContratoItem execucaoContratoItem) {
        ValidacaoException ve = new ValidacaoException();
        if (execucaoContratoItem.getValorTotal() == null || execucaoContratoItem.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Valor Total' do item da execução " + execucaoContratoItem + " deve ser informado e não pode ser negativo.");
        }
        if (execucaoContratoItem.getValorDisponivel().compareTo(execucaoContratoItem.getValorTotal()) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Valor Total' do item da execução " + execucaoContratoItem + " ultrapassa o valor disponível de " + Util.formataValor(execucaoContratoItem.getValorDisponivel()) + ".");
        }
        ve.lancarException();
    }

    private void validarClasseCredor() {
        ValidacaoException ve = new ValidacaoException();
        if (this.execucaoContratoTipo.getClasseCredor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Classe Credor deve ser informado.");
        }
        ve.lancarException();
    }

    public void atribuirNullExecucaoContratoTipo() {
        execucaoContratoTipo = null;
    }

    public void novaDotacao(ExecucaoContratoTipo execucaoContratoTipo) {
        try {
            this.execucaoContratoTipo = execucaoContratoTipo;
            validarClasseCredor();
            novaExecucaoContratoTipoFonte();
            selecionarDotacaoOrcamentaria();
            FacesUtil.atualizarComponente("Formulario:tab-view-geral:panelReservas");
            FacesUtil.executaJavaScript("scrollPanel()");
        } catch (ValidacaoException ve) {
            atribuirNullExecucaoContratoTipo();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            atribuirNullExecucaoContratoTipo();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void novaExecucaoContratoTipo() {
        mapExecucaoContratoTipoItens = new HashMap<>();
        selecionado.setReservas(Lists.<ExecucaoContratoTipo>newArrayList());
        HashMap<TipoObjetoCompra, List<ExecucaoContratoItem>> mapaItensPorTipoObjetoCompra = selecionado.agruparItensPorTipoObjetoCompra();
        for (Map.Entry<TipoObjetoCompra, List<ExecucaoContratoItem>> entry : mapaItensPorTipoObjetoCompra.entrySet()) {
            BigDecimal valorTotalItem = BigDecimal.ZERO;
            for (ExecucaoContratoItem item : entry.getValue()) {
                valorTotalItem = valorTotalItem.add(item.getValorTotal());
            }
            ExecucaoContratoTipo execucaoContratoTipo = new ExecucaoContratoTipo();
            execucaoContratoTipo.setExecucaoContrato(selecionado);
            execucaoContratoTipo.setTipoObjetoCompra(entry.getKey());
            execucaoContratoTipo.setValor(valorTotalItem);
            Util.adicionarObjetoEmLista(selecionado.getReservas(), execucaoContratoTipo);
            mapExecucaoContratoTipoItens.put(execucaoContratoTipo, new ArrayList<ExecucaoContratoItem>(entry.getValue()));
        }
    }

    private void recuperarSolicitcaoExternaParaContratoRegisgroPrecoExterno() {
        if (contrato.isDeRegistroDePrecoExterno()) {
            contrato.getRegistroSolicitacaoMaterialExterno().setSolicitacaoMaterialExterno(facade.getSolicitacaoMaterialExternoFacade().recuperar(contrato.getRegistroSolicitacaoMaterialExterno().getSolicitacaoMaterialExterno().getId()));
        }
    }

    public void selecionarDotacaoOrcamentaria() {
        try {
            reservasLicitacao = Lists.newArrayList();
            execucaoContratoTipoFonteSelecionada = null;
            execucaoContratoTipoFonte.setDespesaORC(null);
            execucaoContratoTipoFonte.setSaldoFonteDespesaORC(BigDecimal.ZERO);
            execucaoContratoTipoFonte.setGerarReserva(podeReservarDotacao);
            recuperarSolicitcaoExternaParaContratoRegisgroPrecoExterno();
            if (!podeReservarDotacao) {
                FiltroDotacaoProcessoCompraVO filtro = new FiltroDotacaoProcessoCompraVO();
                filtro.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
                filtro.setUnidadeOrganizacional(selecionado.getUnidadeOrcamentaria());
                filtro.setTipoObjetoCompra(execucaoContratoTipo.getTipoObjetoCompra());
                filtro.setSolicitacaoMaterial(contrato.getObjetoAdequado().getSolicitacaoMaterial());

                List<DotacaoProcessoCompraVO> reservas = facade.getDotacaoProcessoCompraFacade().buscarValoresReservaDotacaoProcessoCompra(filtro);
                for (DotacaoProcessoCompraVO res : reservas) {
                    ExecucaoContratoTipoFonte fonte = new ExecucaoContratoTipoFonte();
                    fonte.setFonteDespesaORC(res.getFonteDespesaORC());
                    fonte.setValorReservado(res.getValorReservado());
                    fonte.setValorEstornoReservado(res.getValorEstornoReservado());
                    fonte.setValorExecutado(res.getValorExecutado());
                    fonte.setValorEstornoExecutado(res.getValorEstornoExecutado());
                    reservasLicitacao.add(fonte);
                }
            }
            BigDecimal valorDiferencaReserva = BigDecimal.ZERO;
            BigDecimal valorReservado = BigDecimal.ZERO;
            if (contrato.getObjetoAdequado().getProcessoDeCompra() != null) {
                valorReservado = facade.getDotacaoSolicitacaoMaterialFacade().buscarValorReservadoPorProcesso(contrato.getObjetoAdequado().getProcessoDeCompra(), execucaoContratoTipo.getTipoObjetoCompra(), null);
            }
            boolean isValorExecucaoMaiorReservadoLicitacao = valorReservado.compareTo(BigDecimal.ZERO) > 0 && execucaoContratoTipo.getValor().compareTo(valorReservado) > 0;
            if (isValorExecucaoMaiorReservadoLicitacao) {
                valorDiferencaReserva = valorDiferencaReserva.add(execucaoContratoTipo.getValor().subtract(valorReservado));
                execucaoContratoTipoFonte.setValorReservaExecucao(valorDiferencaReserva);
                verificarSaldoDisponivelReservaInicial();
            }
            FacesUtil.atualizarComponente("Formulario:tab-view-geral:panelReservas");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void novaExecucaoContratoTipoFonte() {
        setPodeReservarDotacao(verificarSePodeGerarReservaNaExecucao());
        execucaoContratoTipoFonte = new ExecucaoContratoTipoFonte();
        execucaoContratoTipoFonte.setExecucaoContratoTipo(execucaoContratoTipo);
        execucaoContratoTipoFonte.setDespesaORC(null);
        execucaoContratoTipoFonte.setSaldoFonteDespesaORC(BigDecimal.ZERO);
        execucaoContratoTipoFonte.setGerarReserva(podeReservarDotacao);
        setExecucaoContratoTipoFonteSelecionada(null);

        List<ExecucaoContratoItem> itens = mapExecucaoContratoTipoItens.get(execucaoContratoTipo);
        for (ExecucaoContratoItem itemExecucao : itens) {
            if (itemExecucao.getValorTotal().compareTo(BigDecimal.ZERO) > 0) {
                ExecucaoContratoItemDotacao itemDotacao = novoExecucaoContratoItemDotacao(itemExecucao);
                Util.adicionarObjetoEmLista(execucaoContratoTipoFonte.getItens(), itemDotacao);
            }
        }
        facade.atribuirGrupoContaDespesa(execucaoContratoTipoFonte.getItens());
        execucaoContratoTipoFonte.setValor(execucaoContratoTipoFonte.getValorTotalItemValor());
    }

    private ExecucaoContratoItemDotacao novoExecucaoContratoItemDotacao(ExecucaoContratoItem itemExecucao) {
        ExecucaoContratoItemDotacao itemDotacao = new ExecucaoContratoItemDotacao();
        itemDotacao.setExecucaoContratoTipoFonte(execucaoContratoTipoFonte);
        itemDotacao.setExecucaoContratoItem(itemExecucao);

        for (ExecucaoContratoTipoFonte fonte : execucaoContratoTipo.getFontes()) {
            itemDotacao.setQuantidadeReservada(itemDotacao.getQuantidadeReservada().add(fonte.getQuantidadeReservadaItemDotacao(itemExecucao)));
            BigDecimal valorReservadoItemDotacao = fonte.getValorReservadoItemDotacao(itemExecucao);
            itemDotacao.setValorReservado(itemDotacao.getValorReservado().add(valorReservadoItemDotacao));
        }
        itemDotacao.setQuantidade(itemExecucao.isExecucaoPorQuantidade() ? itemDotacao.getQuantidadeDisponivel() : BigDecimal.ONE);
        itemDotacao.setValorUnitario(itemExecucao.isExecucaoPorQuantidade() ? itemExecucao.getValorUnitario() : itemDotacao.getValorDisponivel());
        itemDotacao.setValorTotal(itemDotacao.getValorDisponivel());
        return itemDotacao;
    }

    public void adicionarExecucaoContratoTipoFonte() {
        try {
            validarCamposObrigatoriosDespesaOrcamentaria();
            validarExecucaoContratoTipoFonte();
            atribuirValorExecucaoContratoTipoFonte();
            Util.adicionarObjetoEmLista(execucaoContratoTipo.getFontes(), execucaoContratoTipoFonte);
            definirValorUtilizadoFonte();
            novaExecucaoContratoTipoFonte();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void adicionarExecucaoContratoItemDotacao(List<ExecucaoContratoItemDotacao> itens) {
        Iterator<ExecucaoContratoItemDotacao> iterator = itens.iterator();
        while (iterator.hasNext()) {
            ExecucaoContratoItemDotacao item = iterator.next();
            if (!item.hasValor()) {
                iterator.remove();
            }
        }
        execucaoContratoTipoFonte.getItens().addAll(itens);
    }

    private void validarCamposObrigatoriosDespesaOrcamentaria() {
        ValidacaoException ve = new ValidacaoException();
        if (podeReservarDotacao) {
            if (execucaoContratoTipoFonte.getDespesaORC() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo elemento de despesa deve ser informado.");
            }
            if (execucaoContratoTipoFonte.getFonteDespesaORC() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo conta de destinação de recurso deve ser informado.");
            }
        } else {
            if (execucaoContratoTipoFonteSelecionada == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar uma reserva de dotação para continuar.");
            }
        }
        ve.lancarException();
    }

    private void atribuirValorExecucaoContratoTipoFonte() {
        if (execucaoContratoTipoFonteSelecionada != null) {
            execucaoContratoTipoFonte.setFonteDespesaORC(execucaoContratoTipoFonteSelecionada.getFonteDespesaORC());
            execucaoContratoTipoFonte.setValorReservado(execucaoContratoTipoFonteSelecionada.getValorReservado());
            execucaoContratoTipoFonte.setValorExecutado(execucaoContratoTipoFonteSelecionada.getValorExecutado());
            execucaoContratoTipoFonte.setValorReservaExecucao(execucaoContratoTipoFonteSelecionada.getValorReservaExecucao());
            execucaoContratoTipoFonte.setDespesaORC(execucaoContratoTipoFonteSelecionada.getFonteDespesaORC().getDespesaORC());
            execucaoContratoTipoFonte.setGerarReserva(false);
        }
    }

    private void definirValorUtilizadoFonte() {
        if (execucaoContratoTipoFonteSelecionada != null) {
            if (reservasLicitacao != null && !reservasLicitacao.isEmpty()) {
                for (ExecucaoContratoTipoFonte reservaLicitacao : reservasLicitacao) {
                    if (reservaLicitacao.equals(execucaoContratoTipoFonteSelecionada)) {
                        reservaLicitacao.setValorExecutado(reservaLicitacao.getValorExecutado().add(execucaoContratoTipoFonte.getValor()));
                    }
                }
            }
        }
    }

    private void verificarSaldoDisponivelReservaInicial() {
        if (reservasLicitacao != null && !reservasLicitacao.isEmpty()) {
            BigDecimal saldo = BigDecimal.ZERO;
            for (ExecucaoContratoTipoFonte fonteAdicionada : reservasLicitacao) {
                saldo = saldo.add(fonteAdicionada.getSaldoDisponivel());
            }
            reservarDiferencaNaExecucao = saldo.compareTo(BigDecimal.ZERO) == 0 && selecionado.getValor().compareTo(selecionado.getValorTotalReservas()) != 0;
        }
    }

    public void reservarDiferencaFonteDoValorReservaInicial() {
        try {
            if (execucaoContratoTipoFonte.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addOperacaoNaoPermitida("O Valor da Reserva deve ser maior que 0(zero).");
                return;
            }
            podeReservarDotacao = true;
            reservarDiferencaNaExecucao = false;
            execucaoContratoTipoFonte.setGerarReserva(podeReservarDotacao);
            FacesUtil.atualizarComponente("Formulario:tab-view-geral:panelReservas");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarExecucaoContratoTipo() {
        podeReservarDotacao = false;
        atribuirNullExecucaoContratoTipo();
        FacesUtil.atualizarComponente("Formulario:tab-view-geral:panelReservas");

    }

    public void confirmarExecucaoContratoTipo() {
        try {
            execucaoContratoTipoFonte = null;
            cancelarExecucaoContratoTipo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void hasReservaInformadaComFonte() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.hasReservas()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A execução: " + selecionado + " não possui reserva de dotação adicionada.");
        }
        ve.lancarException();
        for (ExecucaoContratoTipo reserva : selecionado.getReservas()) {
            if (reserva.getFontes() == null || reserva.getFontes().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A reserva: " + reserva + " não possui fontes adicionada.");
            }
        }
    }

    private void validarExecucaoContratoTipoFonte() {
        ValidacaoException ve = new ValidacaoException();
        if (execucaoContratoTipoFonte.getValor() == null || execucaoContratoTipoFonte.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor da Reserva deve ser informado.");
        }
        if (!hasItensDotacao()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A fonte não possui itens adicionados.");
        }
        ve.lancarException();
        if (execucaoContratoTipoFonte.getValor().add(execucaoContratoTipo.getValorTotalReservado()).compareTo(execucaoContratoTipoFonte.getExecucaoContratoTipo().getValor()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da reserva ultrapassa o valor total a reservar.");
        }
        if (!podeReservarDotacao) {
            if (execucaoContratoTipoFonte.getValor().compareTo(execucaoContratoTipoFonteSelecionada.getSaldoDisponivel()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da reserva ultrapassa o saldo disponível reservado.");
            }
        } else {
            if (execucaoContratoTipoFonte.getValor().compareTo(execucaoContratoTipoFonte.getSaldoFonteDespesaORC()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da reserva ultrapassa o saldo da fonte de despesa.");
            }
        }
        ve.lancarException();
    }

    public Boolean hasExecucaoContratoTipoFonteSelecionada() {
        return execucaoContratoTipoFonte != null;
    }

    public Boolean hasItensDotacao() {
        return execucaoContratoTipoFonte != null && !execucaoContratoTipoFonte.getItens().isEmpty();
    }

    public Boolean hasExecucaoContratoTipoSelecionada() {
        return execucaoContratoTipo != null;
    }

    public Boolean hasItensExecucaoQuantidade() {
        return selecionado != null && selecionado.hasItensExecucaoQuantidade();
    }

    public Boolean hasItensExecucaoValor() {
        return selecionado != null && selecionado.hasItensExecucaovalor();
    }

    public Boolean isObjetoCompraConsumoOrPermanenteMovel() {
        return execucaoContratoTipo != null && execucaoContratoTipo.getTipoObjetoCompra() != null && execucaoContratoTipo.getTipoObjetoCompra().isPermanenteOrConsumo();
    }

    public List<SelectItem> getFonteDespesaOrcamentaria() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        if (execucaoContratoTipoFonte != null && execucaoContratoTipoFonte.getDespesaORC() != null) {
            List<FonteDespesaORC> list = facade.getFonteDespesaORCFacade().completaFonteDespesaORC("", execucaoContratoTipoFonte.getDespesaORC());
            if (list == null || list.isEmpty()) {
                FacesUtil.addAtencao("Nenhum fonte encontrada para a despesa: " + execucaoContratoTipoFonte.getDespesaORC() + ".");
                return Lists.newArrayList();
            }
            for (FonteDespesaORC fonte : list) {
                toReturn.add(new SelectItem(fonte, fonte.getDescricaoFonteDeRecurso()));
            }
        }
        return toReturn;
    }

    public void removerExecucaoContratoTipoFonte(ExecucaoContratoTipoFonte execucaoFonte) {
        if (reservasLicitacao != null && !reservasLicitacao.isEmpty()) {
            for (ExecucaoContratoTipoFonte fonte : reservasLicitacao) {
                if (fonte.getFonteDespesaORC().equals(execucaoFonte.getFonteDespesaORC())) {
                    fonte.setValorExecutado(fonte.getValorExecutado().subtract(execucaoFonte.getValor()));
                }
            }
        }
        execucaoContratoTipo.getFontes().remove(execucaoFonte);
        novaExecucaoContratoTipoFonte();
    }

    public void removerExecucaoContratoTipo(ExecucaoContratoTipo execucaoContratoTipo) {
        execucaoContratoTipo.setFontes(new ArrayList<ExecucaoContratoTipoFonte>());
        reservasLicitacao = Lists.newArrayList();
        selecionado.getReservas().remove(execucaoContratoTipo);
        selecionado.getItensQuantidade().removeIf(item -> item.getObjetoCompra().getTipoObjetoCompra().equals(execucaoContratoTipo.getTipoObjetoCompra()));
        selecionado.getItensQuantidade().removeIf(item -> item.getObjetoCompra().getTipoObjetoCompra().equals(execucaoContratoTipo.getTipoObjetoCompra()));
        if (!hasItensExecucaoQuantidade() && !hasItensExecucaoValor()) {
            classificarItensPorQuantidadeAndValor();
        }
        selecionado.calcularValorTotal();
        FacesUtil.atualizarComponente("Formulario:gridContrato");
    }

    public void removerExecucaoContratoItemQuantidade(ExecucaoContratoItem item) {
        selecionado.getItensQuantidade().remove(item);
        selecionado.calcularValorTotal();
    }

    public void removerExecucaoContratoItemValor(ExecucaoContratoItem item) {
        selecionado.getItensValor().remove(item);
        selecionado.calcularValorTotal();
    }

    public void buscarSaldoFonteDespesaORC() {
        if (execucaoContratoTipoFonte != null) {
            execucaoContratoTipoFonte.setSaldoFonteDespesaORC(BigDecimal.ZERO);
            if (execucaoContratoTipoFonte.getFonteDespesaORC() != null) {
                try {
                    SaldoFonteDespesaORC saldoFonteDespesaORC = facade.getSaldoFonteDespesaORCFacade().recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(
                        execucaoContratoTipoFonte.getFonteDespesaORC(), new Date(), selecionado.getUnidadeOrcamentaria());
                    execucaoContratoTipoFonte.setSaldoFonteDespesaORC(saldoFonteDespesaORC.getSaldoAtual());
                } catch (Exception e) {
                    logger.error("buscarSaldoFonteDespesaORC() ", e);
                }
            }
        }
    }

    public void limparCamposDespesa() {
        execucaoContratoTipoFonte.setSaldoFonteDespesaORC(BigDecimal.ZERO);
        execucaoContratoTipoFonte.setFonteDespesaORC(null);
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, facade.getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }

    private boolean verificarSePodeGerarReservaNaExecucao() {
        boolean reserva = false;
        if (contrato != null) {
            if (contrato.isContratoVigente()) {
                reserva = true;
            }
            if (!movimentoOrigemExecucaoSelecionado.getOrigem().isContrato()) {
                reserva = true;
            }
            ConfiguracaoReservaDotacao config = facade.getConfiguracaoLicitacaoFacade().buscarConfiguracaoReservaDotacao(contrato.getModalidadeLicitacao(), contrato.getTipoNaturezaDoProcedimentoLicitacao());
            if (config != null && config.getTipoReservaDotacao().isReservaExecucao()) {
                reserva = true;
            }
            ProcessoDeCompra processoDeCompra = contrato.getObjetoAdequado().getProcessoDeCompra();
            if (processoDeCompra != null) {
                if (contrato.getObjetoAdequado().getSolicitacaoMaterial().isExercicioProcessoDiferenteExercicioAtual(processoDeCompra.getDataProcesso())) {
                    reserva = true;
                }
            }
        }
        return reserva;
    }

    private void novoFiltroResumoExecucao() {
        filtroResumoExecucaoVO = new FiltroResumoExecucaoVO(TipoResumoExecucao.CONTRATO);
        filtroResumoExecucaoVO.setContrato(selecionado.getContrato());
        filtroResumoExecucaoVO.setExecucaoContrato(selecionado);
        filtroResumoExecucaoVO.setIdProcesso(selecionado.getContrato().getId());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            String nomeRelatorio = "RELATÓRIO DE EXECUÇÃO DE CONTRATO";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Administrativo - Compras e Licitações");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("DESCRICAOORIGEMEXECUCAO", getDescricaoOrigem());
            dto.adicionarParametro("condicao", montarCondicao());
            dto.adicionarParametro("ISEXECUCAOCONTRATO", Boolean.TRUE);
            dto.adicionarParametro("FORMAENTREGA", selecionado.getFormaEntrega().getDescricao());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/execucao-processo/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarCondicao() {
        return " and excont.id = " + selecionado.getId();
    }

    public String getDescricaoOrigem() {
        return "Contrato " + selecionado.getContrato().toString() + " / " + movimentoOrigemExecucaoSelecionado + " / " + origemContrato;
    }

    public void atribuirUnidadeContrato(Contrato contrato) {
        HierarquiaOrganizacional ho = facade.getContratoFacade().buscarHierarquiaVigenteContrato(contrato, selecionado.getDataLancamento());
        contrato.setUnidadeAdministrativa(ho);
    }

    public ExecucaoContratoTipo getExecucaoContratoTipo() {
        return execucaoContratoTipo;
    }

    public void setExecucaoContratoTipo(ExecucaoContratoTipo execucaoContratoTipo) {
        this.execucaoContratoTipo = execucaoContratoTipo;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Boolean hasReservaDotacao() {
        return selecionado.hasReservas();
    }

    public Boolean getPodeReservarDotacao() {
        return podeReservarDotacao;
    }

    public void setPodeReservarDotacao(Boolean podeReservarDotacao) {
        this.podeReservarDotacao = podeReservarDotacao;
    }

    public Boolean getReservarDiferencaNaExecucao() {
        return reservarDiferencaNaExecucao;
    }

    public void setReservarDiferencaNaExecucao(Boolean reservarDiferencaNaExecucao) {
        this.reservarDiferencaNaExecucao = reservarDiferencaNaExecucao;
    }

    public List<SaldoItemContratoOrigemVO> getMovimentosOrigemExecucao() {
        return movimentosOrigemExecucao;
    }

    public void setMovimentosOrigemExecucao(List<SaldoItemContratoOrigemVO> movimentosOrigemExecucao) {
        this.movimentosOrigemExecucao = movimentosOrigemExecucao;
    }

    public SaldoItemContratoOrigemVO getMovimentoOrigemExecucaoSelecionado() {
        return movimentoOrigemExecucaoSelecionado;
    }

    public void setMovimentoOrigemExecucaoSelecionado(SaldoItemContratoOrigemVO movimentoOrigemExecucaoSelecionado) {
        this.movimentoOrigemExecucaoSelecionado = movimentoOrigemExecucaoSelecionado;
    }

    public List<ExecucaoContratoTipoFonte> getReservasLicitacao() {
        return reservasLicitacao;
    }

    public void setReservasLicitacao(List<ExecucaoContratoTipoFonte> reservasLicitacao) {
        this.reservasLicitacao = reservasLicitacao;
    }

    public ExecucaoContratoTipoFonte getExecucaoContratoTipoFonte() {
        return execucaoContratoTipoFonte;
    }

    public void setExecucaoContratoTipoFonte(ExecucaoContratoTipoFonte execucaoContratoTipoFonte) {
        this.execucaoContratoTipoFonte = execucaoContratoTipoFonte;
    }

    public ExecucaoContratoTipoFonte getExecucaoContratoTipoFonteSelecionada() {
        return execucaoContratoTipoFonteSelecionada;
    }

    public void setExecucaoContratoTipoFonteSelecionada(ExecucaoContratoTipoFonte execucaoContratoTipoFonteSelecionada) {
        this.execucaoContratoTipoFonteSelecionada = execucaoContratoTipoFonteSelecionada;
    }


    public List<AgrupadorSolicitacaoEmpenho> getAgrupadoresSolicitacaoEmpenho() {
        return agrupadoresSolicitacaoEmpenho;
    }

    public void setAgrupadoresSolicitacaoEmpenho(List<AgrupadorSolicitacaoEmpenho> agrupadoresSolicitacaoEmpenho) {
        this.agrupadoresSolicitacaoEmpenho = agrupadoresSolicitacaoEmpenho;
    }

    public FiltroResumoExecucaoVO getFiltroResumoExecucaoContrato() {
        return filtroResumoExecucaoVO;
    }

    public void setFiltroResumoExecucaoContrato(FiltroResumoExecucaoVO filtroResumoExecucaoVO) {
        this.filtroResumoExecucaoVO = filtroResumoExecucaoVO;
    }

    public String getOrigemContrato() {
        return origemContrato;
    }

    public void setOrigemContrato(String origemContrato) {
        this.origemContrato = origemContrato;
    }
}
