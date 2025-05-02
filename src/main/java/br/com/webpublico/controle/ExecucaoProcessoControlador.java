package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ExecucaoProcessoFacade;
import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;
import br.com.webpublico.pncp.enums.TipoEventoPncp;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
    @URLMapping(id = "novo-execucao-processo",
        pattern = "/execucao-sem-contrato/novo/",
        viewId = "/faces/administrativo/licitacao/execucao-sem-contrato/edita.xhtml"),

    @URLMapping(id = "ver-execucao-processo",
        pattern = "/execucao-sem-contrato/ver/#{execucaoProcessoControlador.id}/",
        viewId = "/faces/administrativo/licitacao/execucao-sem-contrato/visualizar.xhtml"),

    @URLMapping(id = "listar-execucao-processo",
        pattern = "/execucao-sem-contrato/listar/",
        viewId = "/faces/administrativo/licitacao/execucao-sem-contrato/lista.xhtml")
})
public class ExecucaoProcessoControlador extends PrettyControlador<ExecucaoProcesso> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ExecucaoProcessoControlador.class);
    @EJB
    private ExecucaoProcessoFacade facade;
    private ExecucaoProcessoReserva execucaoProcessoReserva;
    private ExecucaoProcessoFonteVO execucaoProcessoFonteVO;
    private ExecucaoProcessoFonteVO execucaoProcessoFonteSelecionadaVO;
    private List<AgrupadorSolicitacaoEmpenho> agrupadorSolicitacoesEmpenho;
    private List<ExecucaoProcessoFonteVO> reservasDotacaoProcessoCompra;
    private List<Pessoa> fornecedoresVencedores;
    private ConverterAutoComplete converterFonteDespesaORC;
    private HashMap<ExecucaoProcessoReserva, List<ExecucaoProcessoItem>> mapExecucaoReservaItens;
    private SaldoProcessoLicitatorioVO saldoProcesso;
    private Map<Long, List<SaldoProcessoLicitatorioOrigemVO>> mapOrigemSaldosPorFornecedor;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private List<ExecucaoProcessoItemVO> itensQuantidade;
    private List<ExecucaoProcessoItemVO> itensValor;
    private AtaRegistroPreco ataRegistroPreco;
    private DispensaDeLicitacao dispensaDeLicitacao;
    private FiltroResumoExecucaoVO filtroResumoExecucaoVO;
    private ExecucaoProcessoItemVO execucaoProcessoItemVO;
    private HierarquiaOrganizacional hierarquiaAdmProcessoCompra;

    public ExecucaoProcessoControlador() {
        super(ExecucaoProcesso.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-execucao-processo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
        itensQuantidade = Lists.newArrayList();
        itensValor = Lists.newArrayList();
    }

    @URLAction(mappingId = "ver-execucao-processo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarComDependencias(getId());
        Collections.sort(selecionado.getItens());
        facade.atribuirGrupoContaDespesaItemExecucao(selecionado.getItens());
        setHierarquiaOrcamentaria(facade.recuperarHierarquiaDaUnidade(selecionado));
        novoFiltroResumoExecucao();
        hierarquiaAdmProcessoCompra = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ADMINISTRATIVA", selecionado.getProcessoCompra().getUnidadeOrganizacional(), selecionado.getProcessoCompra().getDataProcesso());
        popularEventoPncpVO();

        if (selecionado.getOrigem().isAditovoOrApostilamento()) {
            saldoProcesso = new SaldoProcessoLicitatorioVO(selecionado.getTipoExecucao());
            saldoProcesso.setTipoProcesso(selecionado.getTipoExecucao());
            SaldoProcessoLicitatorioOrigemVO saldoOrigem = new SaldoProcessoLicitatorioOrigemVO();
            saldoOrigem.setIdOrigem(selecionado.getIdOrigem());
            saldoOrigem.setOrigem(selecionado.getOrigem());
            saldoOrigem.setDescricaoMovimento(facade.getSaldoProcessoLicitatorioFacade().buscarDescricaoOrigemSaldo(selecionado.getOrigem(), selecionado.getIdOrigem(), selecionado.getIdProcesso()));
            saldoProcesso.setSaldoOrigemSelecionado(saldoOrigem);
        }

    }

    @Override
    public String getCaminhoPadrao() {
        return "/execucao-sem-contrato/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void salvar() {
        try {
            validarSalvar();
            removerItemNaoUtilizado();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaExecucao(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public String onFlowProcess(FlowEvent event) {
        try {
            String newStep = event.getNewStep().toLowerCase();
            if (newStep.equalsIgnoreCase("execucao")) {
                FacesUtil.atualizarComponente("Formulario:gridProcesso");
                FacesUtil.atualizarComponente("Formulario:tab-view-geral:gridExecucao");
            }
            if (newStep.equalsIgnoreCase("dotacao")) {
                validarRegrasGerais();
                validarCampoFornecedor();
                validarItensDiponiveisParaExecucao();
                selecionado.setItens(Lists.<ExecucaoProcessoItem>newArrayList());
                criarExecucaoContratoItem(getItensQuantidade());
                criarExecucaoContratoItem(getItensValor());

                if (!selecionado.hasReserva()) {
                    atribuirValorTotal();
                    novaExecucaoReserva();
                }
                FacesUtil.atualizarComponente("Formulario:gridProcesso");
                FacesUtil.atualizarComponente("Formulario:tab-view-geral:gridExecucao");
            }
            if (newStep.equalsIgnoreCase("agrupador")) {
                for (ExecucaoProcessoReserva reserva : selecionado.getReservas()) {
                    if (reserva.getValorTotalReservado().compareTo(reserva.getValor()) != 0) {
                        FacesUtil.addOperacaoNaoPermitida("Para continuar, as fontes devem ser reservadas completamente para o tipo de objeto de compra: " + reserva.getTipoObjetoCompra().getDescricao() + ".");
                        return event.getOldStep();
                    }
                }
                hasReservaInformadaComFonte();
                validarTotalReservaComTotalFontes();
                criarExecucaoProcessoFonte();
                criarAgrupadorSolicitacaoEmpenho();
            }
            return newStep;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return event.getOldStep();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            return event.getOldStep();
        }
    }

    private void criarExecucaoContratoItem(List<ExecucaoProcessoItemVO> itensVO) {
        itensVO.forEach(itemVO -> {
            ExecucaoProcessoItem novoItemEx = new ExecucaoProcessoItem();
            novoItemEx.setExecucaoProcesso(selecionado);
            novoItemEx.setItemProcessoCompra(itemVO.getItemProcessoCompra());
            novoItemEx.setQuantidade(itemVO.getQuantidade());
            novoItemEx.setValorUnitario(itemVO.getValorUnitario());
            novoItemEx.setValorTotal(itemVO.getValorTotal());
            selecionado.getItens().add(novoItemEx);
        });
    }

    private void criarExecucaoProcessoFonte() {
        selecionado.getReservas().forEach(res -> {
            res.setFontes(Lists.newArrayList());

            res.getFontesVO().forEach(fonteVo -> {
                ExecucaoProcessoFonte novaFonte = new ExecucaoProcessoFonte();
                novaFonte.setExecucaoProcessoReserva(res);
                novaFonte.setFonteDespesaORC(fonteVo.getFonteDespesaORC());
                novaFonte.setGeraReserva(fonteVo.getGeraReserva());
                novaFonte.setValor(fonteVo.getValor());
                novaFonte.setItens(Lists.newArrayList());

                fonteVo.getItens().forEach(itemVo -> {
                    ExecucaoProcessoFonteItem novoItemFonte = new ExecucaoProcessoFonteItem();
                    novoItemFonte.setExecucaoProcessoFonte(novaFonte);
                    novoItemFonte.setExecucaoProcessoItem(itemVo.getExecucaoProcessoItem());
                    novoItemFonte.setContaDespesa(itemVo.getContaDespesa());
                    novoItemFonte.setQuantidade(itemVo.getQuantidade());
                    novoItemFonte.setValorUnitario(itemVo.getValorUnitario());
                    novoItemFonte.setValorTotal(itemVo.getValorTotal());
                    Util.adicionarObjetoEmLista(novaFonte.getItens(), novoItemFonte);
                });
                Util.adicionarObjetoEmLista(res.getFontes(), novaFonte);
            });
        });
    }

    private void novoFiltroResumoExecucao() {
        filtroResumoExecucaoVO = new FiltroResumoExecucaoVO(TipoResumoExecucao.PROCESSO);
        filtroResumoExecucaoVO.setExecucaoProcesso(selecionado);
        filtroResumoExecucaoVO.setIdProcesso(selecionado.getIdProcesso());
    }

    private void removerItemNaoUtilizado() {
        selecionado.getItens().removeIf(item -> !item.hasValor());
    }

    private void criarAgrupadorSolicitacaoEmpenho() {
        agrupadorSolicitacoesEmpenho = facade.criarAgrupadores(selecionado);
        for (AgrupadorSolicitacaoEmpenho agrupador : agrupadorSolicitacoesEmpenho) {
            SolicitacaoEmpenhoVo solicitacaoEmpenhoVo = facade.criarVoSolicitacaoEmpenho(selecionado, agrupador);
            SolicitacaoEmpenho solicitacaoEmpenho = facade.getSolicitacaoEmpenhoFacade().gerarSolicitacaoEmpenho(solicitacaoEmpenhoVo);
            agrupador.setSolicitacaoEmpenho(solicitacaoEmpenho);
        }
    }

    public BigDecimal getValorTotalAgrupador() {
        BigDecimal total = BigDecimal.ZERO;
        if (agrupadorSolicitacoesEmpenho != null && !agrupadorSolicitacoesEmpenho.isEmpty()) {
            for (AgrupadorSolicitacaoEmpenho agrupador : agrupadorSolicitacoesEmpenho) {
                total = total.add(agrupador.getValor());
            }
        }
        return total;
    }

    public boolean hasProcessoInformado() {
        return selecionado.getTipoExecucao() != null
            && (ataRegistroPreco != null || dispensaDeLicitacao != null);
    }

    public void redirecionarParaExecucao(ExecucaoProcesso selecionado) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaProcesso(ExecucaoProcesso execucaoProcesso) {
        selecionado = execucaoProcesso;
        if (selecionado.getTipoExecucao().isAta()) {
            FacesUtil.redirecionamentoInterno("/ata-registro-preco/ver/" + selecionado.getAtaRegistroPreco().getId() + "/");
            return;
        }
        FacesUtil.redirecionamentoInterno("/dispensa-licitacao/ver/" + selecionado.getDispensaLicitacao().getId() + "/");
    }

    public String getCaminhoMovimentoOrigemExecucao() {
        String caminho = "";
        if (saldoProcesso == null || saldoProcesso.getSaldoOrigemSelecionado() == null) {
            return "";
        }
        if (saldoProcesso.getSaldoOrigemSelecionado().getOrigem().isAditivo()) {
            caminho = "/aditivo-ata-registro-preco/";
        } else if (saldoProcesso.getSaldoOrigemSelecionado().getOrigem().isApostilamento()) {
            caminho = "/apostilamento-ata-registro-preco/";
        }
        return caminho + "ver/" + saldoProcesso.getSaldoOrigemSelecionado().getIdOrigem() + "/";
    }

    private void validarSalvar() {
        validarCampoProcesso();
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.hasReserva()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A execução: " + selecionado + " não possui reserva de dotação adicionada.");
        }
        ve.lancarException();
        validarTotalReservaComTotalFontes();
        if (agrupadorSolicitacoesEmpenho == null || agrupadorSolicitacoesEmpenho.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar é necessário gerar o agrupador de solicitação de empenho após informar as fontes.");
        }
        ve.lancarException();
    }

    private void validarTotalReservaComTotalFontes() {
        ValidacaoException ve = new ValidacaoException();
        for (ExecucaoProcessoReserva reserva : selecionado.getReservas()) {
            if (Util.isListNullOrEmpty(reserva.getFontesVO())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A reserva: " + reserva + "  não possui fontes adicionadas.");
            }
            BigDecimal valorTotalFonte = BigDecimal.ZERO;
            for (ExecucaoProcessoFonteVO fonte : reserva.getFontesVO()) {
                valorTotalFonte = valorTotalFonte.add(fonte.getValor());
            }
            if (reserva.getValor().compareTo(valorTotalFonte) != 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor reservado nas fontes deve ser o mesmo da reserva: " + Util.formataValor(reserva.getValor()) + ".");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getFormasEntrega() {
        return Util.getListSelectItem(FormaEntregaExecucao.values());
    }

    public List<SelectItem> getTiposExecucao() {
        return Util.getListSelectItem(TipoExecucaoProcesso.values());
    }

    public List<SelectItem> getClassesCredor() {
        List<SelectItem> list = new ArrayList<>();
        list.add(new SelectItem(null, ""));
        if (selecionado.getFornecedor() != null) {
            List<ClasseCredor> classes = facade.getClasseCredorFacade().buscarClassesPorPessoa("", selecionado.getFornecedor());
            if (classes.isEmpty()) {
                FacesUtil.addAtencao("O fornecedor: " + selecionado.getFornecedor() + " não possui classe credor em seu cadastro.");
                return list;
            }
            for (ClasseCredor classeCredor : classes) {
                list.add(new SelectItem(classeCredor));
            }
        }
        return list;
    }

    public List<SelectItem> getFornecedores() {
        List<SelectItem> list = new ArrayList<>();
        list.add(new SelectItem(null, ""));
        if (!Util.isListNullOrEmpty(fornecedoresVencedores)) {
            for (Pessoa pesssoa : fornecedoresVencedores) {
                list.add(new SelectItem(pesssoa));
            }
        }
        return list;
    }

    public void confirmarSelecaoFornecedor() {
        try {
            if (selecionado.getFornecedor() == null) {
                preencherSaldoComTodosFornecedor();
                return;
            }
            saldoProcesso.setSaldosAgrupadosPorOrigem(mapOrigemSaldosPorFornecedor.get(selecionado.getFornecedor().getId()));
            if (saldoProcesso.getSaldosAgrupadosPorOrigem().size() > 1) {
                FacesUtil.executaJavaScript("dlgOrigemSaldoExecucao.show()");
                FacesUtil.atualizarComponente("formOrigemSaldoExecucao");
            } else {
                saldoProcesso.setSaldoOrigemSelecionado(saldoProcesso.getSaldosAgrupadosPorOrigem().get(0));
                selecionado.setOrigem(saldoProcesso.getSaldoOrigemSelecionado().getOrigem());
                selecionado.setIdOrigem(saldoProcesso.getSaldoOrigemSelecionado().getIdOrigem());
                criarItensExecucaoPorFornecedor();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (IndexOutOfBoundsException ie) {
            preencherSaldoComTodosFornecedor();
        }
    }

    private void preencherSaldoComTodosFornecedor() {
        limparListasItens();
        saldoProcesso.setSaldosAgrupadosPorOrigem(Lists.newArrayList());
        mapOrigemSaldosPorFornecedor.forEach((key, value) -> saldoProcesso.getSaldosAgrupadosPorOrigem().addAll(value));
    }

    public void confirmarOrigemSaldoExecucao() {
        try {
            validarOrigemExecucao();
            FacesUtil.executaJavaScript("dlgOrigemSaldoExecucao.hide()");
            FacesUtil.atualizarComponente("Formulario");
            criarItensExecucaoPorFornecedor();
            selecionado.setOrigem(saldoProcesso.getSaldoOrigemSelecionado().getOrigem());
            selecionado.setIdOrigem(saldoProcesso.getSaldoOrigemSelecionado().getIdOrigem());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarOrigemSaldoExecucao() {
        try {
            saldoProcesso.setSaldoOrigemSelecionado(null);
            FacesUtil.executaJavaScript("dlgOrigemSaldoExecucao.hide()");
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarOrigemExecucao() {
        ValidacaoException ve = new ValidacaoException();
        if (saldoProcesso.getSaldoOrigemSelecionado() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione uma origem para o saldo da execução.");
        }
        ve.lancarException();
    }

    private FiltroSaldoProcessoLicitatorioVO criarFiltroSaldoProcesso() {
        FiltroSaldoProcessoLicitatorioVO filtro = new FiltroSaldoProcessoLicitatorioVO(selecionado.getTipoExecucao());
        filtro.setProcessoIrp(ataRegistroPreco != null && ataRegistroPreco.getLicitacao() != null && facade.getLicitacaoFacade().isLicitacaoIRP(ataRegistroPreco.getLicitacao()));
        filtro.setFornecedor(selecionado.getFornecedor());
        filtro.setIdProcesso(getIdProcesso());
        filtro.setIdAta(ataRegistroPreco != null ? ataRegistroPreco.getId() : null);
        filtro.setTipoAvaliacaoLicitacao(selecionado.getTipoExecucao().isAta() ? ataRegistroPreco.getLicitacao().getTipoAvaliacao() : dispensaDeLicitacao.getTipoDeAvaliacao());
        filtro.setUnidadeOrganizacional(selecionado.isExecucaoAta() ? ataRegistroPreco.getUnidadeOrganizacional() : dispensaDeLicitacao.getProcessoDeCompra().getUnidadeOrganizacional());
        filtro.setRecuperarOrigem(true);
        return filtro;
    }

    public void buscarFornecedoresVencedoresComItensHomologados() {
        fornecedoresVencedores = Lists.newArrayList();
        if (selecionado.isExecucaoAta()) {
            List<LicitacaoFornecedor> vencedoresAta = facade.getLicitacaoFacade().buscarFornecedoresVencedoresComItensHomologados(selecionado.getExecucaoProcessoAta().getAtaRegistroPreco().getLicitacao());
            vencedoresAta.forEach(forAta -> fornecedoresVencedores.add(forAta.getEmpresa()));
        }
        if (selecionado.isExecucaoDispensa()) {
            List<FornecedorDispensaDeLicitacao> vencedoresDispensa = facade.getDispensaDeLicitacaoFacade().buscarFornecedoresHabilitados(selecionado.getDispensaLicitacao());
            vencedoresDispensa.forEach(forDisp -> fornecedoresVencedores.add(forDisp.getPessoa()));
        }
    }

    public List<AtaRegistroPreco> completarAtaRegistroPreco(String parte) {
        return facade.getAtaRegistroPrecoFacade().buscarAtaRegistroPrecoVigenteOndeUsuarioGestorLicitacao(parte.trim());
    }

    public List<DispensaDeLicitacao> completarDispensaLicitacao(String parte) {
        return facade.getDispensaDeLicitacaoFacade().buscarDispensaLicitacaoUsuarioGestorLiciticaoSemContrato(parte.trim(), facade.getSistemaFacade().getUsuarioCorrente(), true);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().buscarFiltrandoHierarquiaOrcamentariaPorUnidadeAdministrativa(parte.trim(), selecionado.getUnidadeAdministrativa(), selecionado.getDataLancamento());
    }

    private void validarItensDiponiveisParaExecucao() {
        ValidacaoException ve = new ValidacaoException();
        if (!hasItensExecucaoQuantidade() && !hasItensExecucaoValor()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O fornecedor: " + selecionado.getFornecedor() + " não possui itens disponíveis para execução.");
        }
        ve.lancarException();
    }

    public void listenerTipoExecucao() {
        limparDadosProcesso();
        FacesUtil.executaJavaScript(selecionado.isExecucaoAta() ? "setaFoco('Formulario:ac-ata_input')" : "setaFoco('Formulario:ac-dispensa_input')");
    }

    public void listenerProcesso() {
        try {
            validarCampoProcesso();
            validarLancamentoEmExercicioAnterior();
            criarExecucaoProcesso();
            validarAtaGerenciadora();
            atribuirVariaveisSelecionado();
            buscarFornecedoresVencedoresComItensHomologados();

            limparListasItens();
            buscarSaldoGeralProcesso();
            if (!hasMaisDeUmFornecedor()) {
                selecionado.setFornecedor(fornecedoresVencedores.get(0));
                confirmarSelecaoFornecedor();
            }
            List<HierarquiaOrganizacional> hoOrcamentarias = facade.getHierarquiaOrganizacionalFacade().buscarFiltrandoHierarquiaOrcamentariaPorUnidadeAdministrativa("", selecionado.getUnidadeAdministrativa(), selecionado.getDataLancamento());
            if (hoOrcamentarias.size() == 1) {
                setHierarquiaOrcamentaria(hoOrcamentarias.get(0));
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            limparDadosProcesso();
            novo();
            FacesUtil.atualizarComponente("Formulario");
        } catch (IndexOutOfBoundsException a) {
            FacesUtil.addOperacaoNaoPermitida("O processo selecionado não possui saldo para gerar os itens para esta execução.");
            FacesUtil.executaJavaScript("limparDados()");
        } catch (Exception e) {
            descobrirETratarException(e);
            logger.error("Execução processo, recuperar dados do processo {}", e);
        }
    }

    private void validarLancamentoEmExercicioAnterior() {
        ValidacaoException ve = new ValidacaoException();
        if (Util.isExercicioLogadoDiferenteExercicioAtual(facade.getSistemaFacade().getDataOperacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido realizar uma execução sem contrato para exercício diferente do exercício atual.");
        }
        ve.lancarException();
    }

    private void atribuirVariaveisSelecionado() {
        selecionado.setUnidadeOrcamentaria(null);
        setHierarquiaOrcamentaria(null);
        selecionado.setFornecedor(null);
        selecionado.setNumero(facade.getProximoNumero(selecionado));
        selecionado.setFormaEntrega(FormaEntregaExecucao.UNICA);
    }

    private void buscarSaldoGeralProcesso() {
        FiltroSaldoProcessoLicitatorioVO filtroSaldoVO = criarFiltroSaldoProcesso();
        saldoProcesso = facade.getSaldoProcessoLicitatorioFacade().getSaldoProcesso(filtroSaldoVO);
        saldoProcesso.setTipoProcesso(selecionado.getTipoExecucao());
        preencherMapSaldoItensPorFornecedor();
    }

    private void preencherMapSaldoItensPorFornecedor() {
        mapOrigemSaldosPorFornecedor = Maps.newHashMap();
        saldoProcesso.getSaldosAgrupadosPorOrigem().forEach(saldo -> {
            if (!mapOrigemSaldosPorFornecedor.containsKey(saldo.getIdFornecedor())) {
                mapOrigemSaldosPorFornecedor.put(saldo.getIdFornecedor(), Lists.newArrayList());
            }
            List<SaldoProcessoLicitatorioOrigemVO> saldosMap = mapOrigemSaldosPorFornecedor.get(saldo.getIdFornecedor());
            saldosMap.add(saldo);
            mapOrigemSaldosPorFornecedor.put(saldo.getIdFornecedor(), saldosMap);
        });
    }

    private void criarExecucaoProcesso() {
        if (selecionado.getTipoExecucao().isAta()) {
            ExecucaoProcessoAta exAta = new ExecucaoProcessoAta(selecionado);
            exAta.setAtaRegistroPreco(ataRegistroPreco);
            selecionado.setExecucaoProcessoAta(exAta);
        } else {
            ExecucaoProcessoDispensa exDisp = new ExecucaoProcessoDispensa(selecionado);
            exDisp.setDispensaLicitacao(dispensaDeLicitacao);
            selecionado.setExecucaoProcessoDispensa(exDisp);
        }
    }

    private void validarAtaGerenciadora() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoExecucao().isAta() && !selecionado.getAtaRegistroPreco().getGerenciadora()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido realizar execução para ata que não for gerenciadora.");
        }
        ve.lancarException();
    }

    public void criarItensExecucaoPorFornecedor() {
        try {
            limparListasItens();
            criarItensExecucaoAtaRegistroPreco();
            atribuirValorTotal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (IndexOutOfBoundsException a) {
            FacesUtil.addOperacaoNaoPermitida("O processo selecionado não possui saldo para gerar os itens para esta execução.");
            FacesUtil.executaJavaScript("limparDadosAta()");
        } catch (Exception e) {
            descobrirETratarException(e);
            logger.error("Execução Processo, recuperar itens do processo {}", e);
        }
    }

    public void criarItensExecucaoAtaRegistroPreco() {
        for (SaldoProcessoLicitatorioItemVO itemSaldoVO : saldoProcesso.getSaldoOrigemSelecionado().getItens()) {
            ExecucaoProcessoItemVO execucaoProcessoItem = criarExecucaoProcessoItem(itemSaldoVO);
            classificarItensPorQuantidadeOrValor(execucaoProcessoItem);
        }
        Collections.sort(getItensQuantidade());
        Collections.sort(getItensValor());
    }

    private void classificarItensPorQuantidadeOrValor(ExecucaoProcessoItemVO itemVO) {
        if (itemVO.isExecucaoPorQuantidade()) {
            Util.adicionarObjetoEmLista(getItensQuantidade(), itemVO);
            return;
        }
        Util.adicionarObjetoEmLista(getItensValor(), itemVO);
    }

    private ExecucaoProcessoItemVO criarExecucaoProcessoItem(SaldoProcessoLicitatorioItemVO itemSaldoVO) {
        ExecucaoProcessoItemVO novoItem = new ExecucaoProcessoItemVO();
        novoItem.setItemProcessoCompra(itemSaldoVO.getItemProcessoCompra());

        novoItem.setItemSaldoVO(itemSaldoVO);
        novoItem.setValorUnitario(itemSaldoVO.getValorUnitarioHomologado());

        if (novoItem.isExecucaoPorQuantidade()) {
            novoItem.setQuantidade(itemSaldoVO.getQuantidadeDisponivel());
            novoItem.calcularValorTotal();
        } else {
            novoItem.setQuantidade(BigDecimal.ONE);
            novoItem.setValorTotal(itemSaldoVO.getSaldoDisponivel());
        }
        facade.atribuirGrupoContaDespesa(novoItem);
        return novoItem;
    }

    public Long getIdProcesso() {
        if (selecionado.getTipoExecucao().isAta()) {
            return selecionado.getAtaRegistroPreco().getLicitacao().getId();
        }
        return selecionado.getDispensaLicitacao().getId();
    }

    public void limparDadosProcesso() {
        selecionado.setExecucaoProcessoAta(null);
        selecionado.setExecucaoProcessoDispensa(null);
        setAtaRegistroPreco(null);
        setDispensaDeLicitacao(null);
        fornecedoresVencedores = Lists.newArrayList();
        selecionado.setReservas(Lists.<ExecucaoProcessoReserva>newArrayList());
        setSaldoProcesso(null);
        limparListasItens();
    }

    private void validarCampoProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoExecucao() != null) {
            if (selecionado.getTipoExecucao().isAta() && ataRegistroPreco == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo ata registro de preço deve ser informado.");
            }
            if (selecionado.getTipoExecucao().isDispensa() && dispensaDeLicitacao == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo dispensa de licitação/inexigibilidade deve ser informado.");
            }
        }
        ve.lancarException();
    }

    private void validarCampoFornecedor() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFornecedor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo fornecedor deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarRegrasGerais() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getUnidadeOrcamentaria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Orçamentária deve ser informado.");
        }
        ve.lancarException();
        if (saldoProcesso.getSaldoDisponivelComAcrescimo().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo não possui saldo para realizar execução.");
        }
        if (selecionado.getValor().compareTo(saldoProcesso.getSaldoDisponivelComAcrescimo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível realizar execução maior que o saldo do processo. Nesse momento, o processo possui saldo de "
                + Util.formataValor(saldoProcesso.getSaldoDisponivelComAcrescimo()) + ", menor que o valor executado de " + Util.formataValor(selecionado.getValor()) + ".");
        }
        ve.lancarException();
    }

    public void processarValorTotalItem(ExecucaoProcessoItemVO item) {
        try {
            validarValorTotalExecucaoItem(item);
            item.setValorUnitario(item.getValorTotal());
            atribuirValorTotal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            item.setValorTotal(item.getItemSaldoVO().getSaldoDisponivel());
            atribuirValorTotal();
        }
    }

    public Boolean isObjetoCompraConsumoOrPermanenteMovel() {
        return execucaoProcessoReserva != null && execucaoProcessoReserva.getTipoObjetoCompra() != null && execucaoProcessoReserva.getTipoObjetoCompra().isPermanenteOrConsumo();
    }

    public boolean hasSolicitacaoEmpenhoEmpenhada() {
        return isOperacaoVer() && selecionado.hasSolicitacaoEmpenhoEmpenhada();
    }

    public boolean hasMaisDeUmFornecedor() {
        return hasFornecedores() && fornecedoresVencedores.size() > 1;
    }

    public boolean hasFornecedores() {
        return fornecedoresVencedores != null && !fornecedoresVencedores.isEmpty();
    }

    public void removerItemDotacaoQuantidade(ExecucaoProcessoFonteItemVO itemDotacao) {
        execucaoProcessoFonteVO.getItensQuantidade().remove(itemDotacao);
        execucaoProcessoFonteVO.calcularValorTotal();
    }

    public void removerItemDotacaoValor(ExecucaoProcessoFonteItemVO itemDotacao) {
        execucaoProcessoFonteVO.getItensValor().remove(itemDotacao);
        execucaoProcessoFonteVO.calcularValorTotal();
    }

    public void processarQuantidadeItemDotacao(ExecucaoProcessoFonteItemVO itemDotacao) {
        try {
            validarQuantidadeItemFonte(itemDotacao);
            itemDotacao.calcularValorTotal();
            execucaoProcessoFonteVO.calcularValorTotal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            FacesUtil.atualizarComponente("Formulario:tab-view-geral:valorReserva");
        }
    }

    private void validarQuantidadeItemFonte(ExecucaoProcessoFonteItemVO item) {
        ValidacaoException ve = new ValidacaoException();
        if (item.getQuantidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo quantidade deve ser informado.");
        }
        if (item.getQuantidade() != null
            && item.getQuantidadeDisponivel() != null
            && item.getQuantidade().compareTo(item.getQuantidadeDisponivel()) > 0) {
            item.setQuantidade(item.getQuantidadeDisponivel());
            FacesUtil.addOperacaoNaoPermitida("A quantidade deve ser no máximo a quantidade disponível. (<b>" + Util.formataQuandoDecimal(item.getQuantidadeDisponivel(), item.getExecucaoProcessoItem().getMascaraQuantidade()) + "</b>)");
        }
        ve.lancarException();
    }

    public void processarValorItemFonte(ExecucaoProcessoFonteItemVO item) {
        try {
            validarValorItemFonte(item);
            item.setValorUnitario(item.getValorTotal());
            execucaoProcessoFonteVO.calcularValorTotal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            FacesUtil.atualizarComponente("Formulario:tab-view-geral:valorReserva");
        }
    }

    private void validarValorItemFonte(ExecucaoProcessoFonteItemVO item) {
        ValidacaoException ve = new ValidacaoException();
        if (item.getValorTotal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo valor deve ser informado.");
        }
        if (item.getValorTotal() != null
            && item.getValorDisponivel() != null
            && item.getValorTotal().compareTo(item.getValorDisponivel()) > 0) {
            item.setValorUnitario(BigDecimal.ZERO);
            item.setValorTotal(BigDecimal.ZERO);
            FacesUtil.addOperacaoNaoPermitida("A valor deve ser no máximo o valor disponível. (<b>" + Util.formataValor(item.getValorDisponivel()) + "</b>)");
        }
        ve.lancarException();
    }

    public void processarQuantidadeItem(ExecucaoProcessoItemVO item) {
        try {
            validarQuantidadeExecucaoItem(item);
            item.calcularValorTotal();
            atribuirValorTotal();
        } catch (ValidacaoException ve) {
            item.setQuantidade(item.getItemSaldoVO().getQuantidadeDisponivel());
            item.calcularValorTotal();
            atribuirValorTotal();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarQuantidadeExecucaoItem(ExecucaoProcessoItemVO item) {
        ValidacaoException ve = new ValidacaoException();
        if (item.getQuantidade() == null || item.getQuantidade().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A 'Quantidade' do item da execução " + item + " deve ser informada e não pode ser negativo.");
        }
        if (item.getItemSaldoVO().getQuantidadeDisponivel().compareTo(item.getQuantidade()) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Quantidade' do item da execução " + item + " ultrapassa a quantidade disponível de " + Util.formataValor(item.getItemSaldoVO().getQuantidadeDisponivel()) + ".");
        }
        ve.lancarException();
    }

    private void validarValorTotalExecucaoItem(ExecucaoProcessoItemVO item) {
        ValidacaoException ve = new ValidacaoException();
        if (item.getValorTotal() == null || item.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Valor Total' do item da execução " + item + " deve ser informado e não pode ser negativo.");
        }
        if (item.getItemSaldoVO().getSaldoDisponivel().compareTo(item.getValorTotal()) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Valor Total' do item da execução " + item + " ultrapassa o valor disponível de " + Util.formataValor(item.getItemSaldoVO().getSaldoDisponivel()) + ".");
        }
        ve.lancarException();
    }

    private void validarClasseCredor() {
        ValidacaoException ve = new ValidacaoException();
        if (execucaoProcessoReserva.getClasseCredor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Classe Credor deve ser informado.");
        }
        ve.lancarException();
    }

    public void atribuirNullExecucaoReserva() {
        execucaoProcessoReserva = null;
    }

    public void selecionarExecucaoProcessoItem(ExecucaoProcessoItemVO itemVO) {
        execucaoProcessoItemVO = itemVO;
    }

    public void selecionarExecucaoReserva(ExecucaoProcessoReserva reserva) {
        try {
            execucaoProcessoReserva = reserva;
            validarClasseCredor();
            novaExecucaoFonte();
            buscarReservaProcessoCompra();
            FacesUtil.atualizarComponente("Formulario:tab-view-geral:panelReservas");
            FacesUtil.executaJavaScript("scrollPanel()");
        } catch (ValidacaoException ve) {
            atribuirNullExecucaoReserva();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            atribuirNullExecucaoReserva();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void novaExecucaoReserva() {
        mapExecucaoReservaItens = new HashMap<>();
        selecionado.setReservas(Lists.<ExecucaoProcessoReserva>newArrayList());
        HashMap<TipoObjetoCompra, List<ExecucaoProcessoItem>> mapaItensPorTipoObjetoCompra = selecionado.agruparItensPorTipoObjetoCompra();
        for (Map.Entry<TipoObjetoCompra, List<ExecucaoProcessoItem>> entry : mapaItensPorTipoObjetoCompra.entrySet()) {
            BigDecimal valorTotalItem = BigDecimal.ZERO;
            for (ExecucaoProcessoItem item : entry.getValue()) {
                valorTotalItem = valorTotalItem.add(item.getValorTotal());
            }
            ExecucaoProcessoReserva novaReserva = new ExecucaoProcessoReserva();
            novaReserva.setExecucaoProcesso(selecionado);
            novaReserva.setTipoObjetoCompra(entry.getKey());
            novaReserva.setValor(valorTotalItem);
            Util.adicionarObjetoEmLista(selecionado.getReservas(), novaReserva);
            mapExecucaoReservaItens.put(novaReserva, new ArrayList<ExecucaoProcessoItem>(entry.getValue()));
        }
    }

    public void detalharReservaFonteItens(ExecucaoProcessoReserva reserva) {
        execucaoProcessoReserva = reserva;
        for (ExecucaoProcessoFonte fonte : execucaoProcessoReserva.getFontes()) {
            facade.atribuirGrupoContaDespesaItemFonte(fonte.getItens());
        }
    }

    public void buscarReservaProcessoCompra() {
        try {
            reservasDotacaoProcessoCompra = Lists.newArrayList();
            if (!execucaoProcessoFonteVO.getGeraReserva() && Util.isListNullOrEmpty(reservasDotacaoProcessoCompra)) {
                FiltroDotacaoProcessoCompraVO filtro = new FiltroDotacaoProcessoCompraVO();
                filtro.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
                filtro.setUnidadeOrganizacional(selecionado.getUnidadeOrcamentaria());
                filtro.setTipoObjetoCompra(execucaoProcessoReserva.getTipoObjetoCompra());
                filtro.setSolicitacaoMaterial(selecionado.getProcessoCompra().getSolicitacaoMaterial());

                List<DotacaoProcessoCompraVO> reservas = facade.getDotacaoProcessoCompraFacade().buscarValoresReservaDotacaoProcessoCompra(filtro);
                for (DotacaoProcessoCompraVO res : reservas) {
                    ExecucaoProcessoFonteVO fonte = new ExecucaoProcessoFonteVO();
                    fonte.setFonteDespesaORC(res.getFonteDespesaORC());
                    fonte.setValorReservado(res.getValorReservado());
                    fonte.setValorEstornoReservado(res.getValorEstornoReservado());
                    fonte.setValorExecutado(res.getValorExecutado());
                    fonte.setValorEstornoExecutado(res.getValorEstornoExecutado());
                    reservasDotacaoProcessoCompra.add(fonte);
                }
            }
            FacesUtil.atualizarComponente("Formulario:tab-view-geral:panelReservas");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void novaExecucaoFonte() {
        execucaoProcessoFonteVO = new ExecucaoProcessoFonteVO();
        execucaoProcessoFonteVO.setExecucaoProcessoReserva(execucaoProcessoReserva);
        execucaoProcessoFonteVO.setDespesaORC(null);
        execucaoProcessoFonteVO.setSaldoFonteDespesaORC(BigDecimal.ZERO);
        execucaoProcessoFonteVO.setGeraReserva(verificarSePodeGerarReservaNaExecucao());
        execucaoProcessoFonteSelecionadaVO = null;

        List<ExecucaoProcessoItem> itens = mapExecucaoReservaItens.get(execucaoProcessoReserva);
        for (ExecucaoProcessoItem itemExecucao : itens) {
            ExecucaoProcessoFonteItemVO itemDotacao = novaExecucaoProcessoFonteItem(itemExecucao);
            if (itemExecucao.isExecucaoPorQuantidade()) {
                Util.adicionarObjetoEmLista(execucaoProcessoFonteVO.getItensQuantidade(), itemDotacao);
            } else {
                Util.adicionarObjetoEmLista(execucaoProcessoFonteVO.getItensValor(), itemDotacao);
            }
        }
        execucaoProcessoFonteVO.calcularValorTotal();
    }

    private ExecucaoProcessoFonteItemVO novaExecucaoProcessoFonteItem(ExecucaoProcessoItem item) {
        ExecucaoProcessoFonteItemVO novoItemFonte = new ExecucaoProcessoFonteItemVO();
        novoItemFonte.setExecucaoProcessoFonteVO(execucaoProcessoFonteVO);
        novoItemFonte.setExecucaoProcessoItem(item);

        for (ExecucaoProcessoFonteVO fonte : execucaoProcessoReserva.getFontesVO()) {
            novoItemFonte.setQuantidadeReservada(novoItemFonte.getQuantidadeReservada().add(fonte.getQuantidadeReservadaItemDotacao(item)));
            novoItemFonte.setValorReservado(novoItemFonte.getValorReservado().add(fonte.getValorReservadoItemDotacao(item)));
        }
        novoItemFonte.setQuantidade(item.isExecucaoPorQuantidade() ? novoItemFonte.getQuantidadeDisponivel() : BigDecimal.ONE);
        novoItemFonte.setValorUnitario(item.isExecucaoPorQuantidade() ? item.getValorUnitario() : novoItemFonte.getValorDisponivel());
        novoItemFonte.setValorTotal(novoItemFonte.getValorDisponivel());
        novoItemFonte.calcularValorTotal();
        return novoItemFonte;
    }

    public void adicionarFonte() {
        try {
            validarCamposObrigatoriosDespesaOrcamentaria();
            validarExecucaoProcessoFonte();
            atribuirValorExecucaoProcessoFonte();
            removerItemFonteNaoUtilizado(execucaoProcessoFonteVO.getItensQuantidade());
            removerItemFonteNaoUtilizado(execucaoProcessoFonteVO.getItensValor());

            execucaoProcessoFonteVO.setItens(Lists.newArrayList());
            execucaoProcessoFonteVO.getItens().addAll(execucaoProcessoFonteVO.getItensQuantidade());
            execucaoProcessoFonteVO.getItens().addAll(execucaoProcessoFonteVO.getItensValor());
            Util.adicionarObjetoEmLista(execucaoProcessoReserva.getFontesVO(), execucaoProcessoFonteVO);
            definirValorUtilizadoFonte();
            novaExecucaoFonte();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void removerItemFonteNaoUtilizado(List<ExecucaoProcessoFonteItemVO> itens) {
        Iterator<ExecucaoProcessoFonteItemVO> iterator = itens.iterator();
        while (iterator.hasNext()) {
            ExecucaoProcessoFonteItemVO item = iterator.next();
            if (item.getExecucaoProcessoItem().isExecucaoPorQuantidade() && !item.hasQuantidade()) {
                iterator.remove();
            }
            if (item.getExecucaoProcessoItem().isExecucaoPorValor() && !item.hasValor()) {
                iterator.remove();
            }
        }
    }

    private void validarCamposObrigatoriosDespesaOrcamentaria() {
        ValidacaoException ve = new ValidacaoException();
        if (execucaoProcessoFonteVO.getGeraReserva()) {
            if (execucaoProcessoFonteVO.getDespesaORC() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo elemento de despesa deve ser informado.");
            }
            if (execucaoProcessoFonteVO.getFonteDespesaORC() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo conta de destinação de recurso deve ser informado.");
            }
        } else {
            if (execucaoProcessoFonteSelecionadaVO == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar uma reserva de dotação para continuar.");
            }
        }
        ve.lancarException();
    }

    private void atribuirValorExecucaoProcessoFonte() {
        if (execucaoProcessoFonteSelecionadaVO != null) {
            execucaoProcessoFonteVO.setFonteDespesaORC(execucaoProcessoFonteSelecionadaVO.getFonteDespesaORC());
            execucaoProcessoFonteVO.setValorReservado(execucaoProcessoFonteSelecionadaVO.getValorReservado());
            execucaoProcessoFonteVO.setValorExecutado(execucaoProcessoFonteSelecionadaVO.getValorExecutado());
            execucaoProcessoFonteVO.setValorReservaExecucao(execucaoProcessoFonteSelecionadaVO.getValorReservaExecucao());
            execucaoProcessoFonteVO.setDespesaORC(execucaoProcessoFonteSelecionadaVO.getFonteDespesaORC().getDespesaORC());
            execucaoProcessoFonteVO.setGeraReserva(false);
        }
    }

    private void definirValorUtilizadoFonte() {
        if (execucaoProcessoFonteSelecionadaVO != null) {
            if (reservasDotacaoProcessoCompra != null && !reservasDotacaoProcessoCompra.isEmpty()) {
                for (ExecucaoProcessoFonteVO res : reservasDotacaoProcessoCompra) {
                    if (res.equals(execucaoProcessoFonteSelecionadaVO)) {
                        res.setValorExecutado(res.getValorExecutado().add(execucaoProcessoFonteVO.getValor()));
                    }
                }
            }
        }
    }

    public void cancelarExecucaoReserva() {
        atribuirNullExecucaoReserva();
        FacesUtil.atualizarComponente("Formulario:tab-view-geral:panelReservas");
    }

    public void cancelarFonte() {
        try {
            execucaoProcessoFonteVO = null;
            cancelarExecucaoReserva();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void hasReservaInformadaComFonte() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.hasReserva()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A execução: " + selecionado + " não possui reserva de dotação adicionada.");
        }
        ve.lancarException();
        for (ExecucaoProcessoReserva reserva : selecionado.getReservas()) {
            if (Util.isListNullOrEmpty(reserva.getFontesVO())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A reserva: " + reserva + " não possui fontes adicionada.");
            }
        }
    }

    private void validarExecucaoProcessoFonte() {
        ValidacaoException ve = new ValidacaoException();
        if (execucaoProcessoFonteVO.getValor() == null || execucaoProcessoFonteVO.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor da Reserva deve ser informado.");
        }
        if (!hasItensFontePorQuantidade() && !hasItensFontePorValor()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A fonte não possui itens adicionados.");
        }
        ve.lancarException();
        if (execucaoProcessoFonteVO.getValor().add(execucaoProcessoReserva.getValorTotalReservado()).compareTo(execucaoProcessoFonteVO.getExecucaoProcessoReserva().getValor()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da reserva ultrapassa o valor total a reservar.");
        }
        if (execucaoProcessoFonteVO.getGeraReserva()) {
            if (execucaoProcessoFonteVO.getValor().compareTo(execucaoProcessoFonteVO.getSaldoFonteDespesaORC()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da reserva ultrapassa o saldo da fonte de despesa orçamentária.");
            }
        } else {
            if (execucaoProcessoFonteVO.getValor().compareTo(execucaoProcessoFonteSelecionadaVO.getSaldoDisponivel()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da reserva ultrapassa o saldo disponível reservado do processo de compra.");
            }
        }
        ve.lancarException();
    }

    public Boolean hasFonteSelecionada() {
        return execucaoProcessoFonteVO != null;
    }

    public Boolean hasItensFontePorQuantidade() {
        return execucaoProcessoFonteVO != null && !execucaoProcessoFonteVO.getItensQuantidade().isEmpty();
    }

    public Boolean hasItensFontePorValor() {
        return execucaoProcessoFonteVO != null && !execucaoProcessoFonteVO.getItensValor().isEmpty();
    }

    public Boolean hasReservaSelecionada() {
        return execucaoProcessoReserva != null;
    }

    public Boolean hasItensExecucaoQuantidade() {
        return !Util.isListNullOrEmpty(getItensQuantidade());
    }

    public Boolean hasItensExecucaoValor() {
        return !Util.isListNullOrEmpty(getItensValor());
    }

    public List<SelectItem> getFonteDespesaOrcamentaria() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        if (execucaoProcessoFonteVO != null && execucaoProcessoFonteVO.getDespesaORC() != null) {
            List<FonteDespesaORC> list = facade.getFonteDespesaORCFacade().completaFonteDespesaORC("", execucaoProcessoFonteVO.getDespesaORC());
            if (list == null || list.isEmpty()) {
                FacesUtil.addAtencao("Nenhum fonte encontrada para a despesa: " + execucaoProcessoFonteVO.getDespesaORC() + ".");
                return Lists.newArrayList();
            }
            for (FonteDespesaORC fonte : list) {
                toReturn.add(new SelectItem(fonte, fonte.getDescricaoFonteDeRecurso()));
            }
        }
        return toReturn;
    }

    public void removerFonte(ExecucaoProcessoFonteVO execucaoFonte) {
        if (reservasDotacaoProcessoCompra != null && !reservasDotacaoProcessoCompra.isEmpty()) {
            for (ExecucaoProcessoFonteVO fonte : reservasDotacaoProcessoCompra) {
                if (fonte.getFonteDespesaORC().equals(execucaoFonte.getFonteDespesaORC())) {
                    fonte.setValorExecutado(fonte.getValorExecutado().subtract(execucaoFonte.getValor()));
                }
            }
        }
        execucaoProcessoReserva.getFontesVO().remove(execucaoFonte);
        novaExecucaoFonte();
    }

    public void removerReserva(ExecucaoProcessoReserva execucaoReserva) {
        execucaoReserva.setFontes(new ArrayList<ExecucaoProcessoFonte>());
        reservasDotacaoProcessoCompra = Lists.newArrayList();
        selecionado.getReservas().remove(execucaoReserva);
        itensQuantidade.removeIf(item -> item.getObjetoCompra().getTipoObjetoCompra().equals(execucaoReserva.getTipoObjetoCompra()));
        itensValor.removeIf(item -> item.getObjetoCompra().getTipoObjetoCompra().equals(execucaoReserva.getTipoObjetoCompra()));
        if (!hasItensExecucaoQuantidade() && !hasItensExecucaoValor()) {
            criarItensExecucaoPorFornecedor();
        }
        atribuirValorTotal();
    }

    public void removerItemQuantidade(ExecucaoProcessoItemVO item) {
        getItensQuantidade().remove(item);
        atribuirValorTotal();
    }

    public void removerItemValor(ExecucaoProcessoItemVO item) {
        getItensValor().remove(item);
        atribuirValorTotal();
    }

    public void buscarSaldoFonteDespesaORC() {
        if (execucaoProcessoFonteVO != null) {
            execucaoProcessoFonteVO.setSaldoFonteDespesaORC(BigDecimal.ZERO);
            if (execucaoProcessoFonteVO.getFonteDespesaORC() != null) {
                try {
                    SaldoFonteDespesaORC saldoFonteDespesaORC = facade.getSaldoFonteDespesaORCFacade().recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(
                        execucaoProcessoFonteVO.getFonteDespesaORC(), new Date(), selecionado.getUnidadeOrcamentaria());
                    execucaoProcessoFonteVO.setSaldoFonteDespesaORC(saldoFonteDespesaORC.getSaldoAtual());
                } catch (Exception e) {
                    logger.error("buscarSaldoFonteDespesaORC() ", e);
                }
            }
        }
    }

    public void limparListasItens() {
        selecionado.setItens(Lists.<ExecucaoProcessoItem>newArrayList());
        setItensQuantidade(Lists.<ExecucaoProcessoItemVO>newArrayList());
        setItensValor(Lists.<ExecucaoProcessoItemVO>newArrayList());
    }

    public void limparCamposDespesa() {
        execucaoProcessoFonteVO.setSaldoFonteDespesaORC(BigDecimal.ZERO);
        execucaoProcessoFonteVO.setFonteDespesaORC(null);
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, facade.getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }

    public Boolean hasReservaDotacao() {
        return selecionado.hasReserva();
    }

    public void atribuirValorTotal() {
        try {
            selecionado.setValor(getValorTotalItemQuantidade().add(getValorTotalItemValor()));
        } catch (NullPointerException e) {
            selecionado.setValor(BigDecimal.ZERO);
        }
    }

    public BigDecimal getValorTotalItemQuantidade() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (ExecucaoProcessoItemVO item : getItensQuantidade()) {
                total = total.add(item.getValorTotal());
            }
            return total;
        } catch (NullPointerException e) {
            return total;
        }
    }

    public BigDecimal getValorTotalItemValor() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (ExecucaoProcessoItemVO item : getItensValor()) {
                total = total.add(item.getValorTotal());
            }
            return total;
        } catch (NullPointerException e) {
            return total;
        }
    }

    private boolean verificarSePodeGerarReservaNaExecucao() {
        boolean controle = false;
        if (ataRegistroPreco != null || dispensaDeLicitacao != null) {
            ConfiguracaoReservaDotacao config = facade.getConfiguracaoLicitacaoFacade().buscarConfiguracaoReservaDotacao(selecionado.getModalidadeProcesso(), selecionado.getNaturezaProcesso());
            if (config != null && config.getTipoReservaDotacao().isReservaExecucao()) {
                controle = true;
            }
            ProcessoDeCompra processoDeCompra = selecionado.getProcessoCompra();
            if (processoDeCompra != null && processoDeCompra.getSolicitacaoMaterial().isExercicioProcessoDiferenteExercicioAtual(processoDeCompra.getDataProcesso())) {
                controle = true;
            }
        }
        return controle;
    }

    private void popularEventoPncpVO() {
        for (ExecucaoProcessoEmpenho exProcEmp : selecionado.getEmpenhos()) {
            EventoPncpVO eventoPncpVO = new EventoPncpVO();
            eventoPncpVO.setTipoEventoPncp(TipoEventoPncp.CONTRATO_EMPENHO);
            eventoPncpVO.setIdOrigem(exProcEmp.getEmpenho() != null ? exProcEmp.getEmpenho().getId() : null);
            eventoPncpVO.setIdPncp(exProcEmp.getIdPncp());
            eventoPncpVO.setSequencialIdPncp(exProcEmp.getSequencialIdPncp());
            eventoPncpVO.setAnoPncp(selecionado.getAnoProcesso());
            exProcEmp.setEventoPncpVO(eventoPncpVO);
        }
    }

    public boolean isGeraReservaExecucao() {
        return execucaoProcessoFonteVO != null && execucaoProcessoFonteVO.getGeraReserva();
    }

    public ExecucaoProcessoReserva getExecucaoProcessoReserva() {
        return execucaoProcessoReserva;
    }

    public void setExecucaoProcessoReserva(ExecucaoProcessoReserva execucaoProcessoReserva) {
        this.execucaoProcessoReserva = execucaoProcessoReserva;
    }

    public ExecucaoProcessoFonteVO getExecucaoProcessoFonteVO() {
        return execucaoProcessoFonteVO;
    }

    public void setExecucaoProcessoFonteVO(ExecucaoProcessoFonteVO execucaoProcessoFonte) {
        this.execucaoProcessoFonteVO = execucaoProcessoFonte;
    }

    public ExecucaoProcessoFonteVO getExecucaoProcessoFonteVOSelecionada() {
        return execucaoProcessoFonteSelecionadaVO;
    }

    public void setExecucaoProcessoFonteVOSelecionada(ExecucaoProcessoFonteVO execucaoProcessoFonteSelecionada) {
        this.execucaoProcessoFonteSelecionadaVO = execucaoProcessoFonteSelecionada;
    }

    public List<AgrupadorSolicitacaoEmpenho> getAgrupadorSolicitacoesEmpenho() {
        return agrupadorSolicitacoesEmpenho;
    }

    public void setAgrupadorSolicitacoesEmpenho(List<AgrupadorSolicitacaoEmpenho> agrupadorSolicitacoesEmpenho) {
        this.agrupadorSolicitacoesEmpenho = agrupadorSolicitacoesEmpenho;
    }

    public List<ExecucaoProcessoFonteVO> getReservasDotacaoProcessoCompra() {
        return reservasDotacaoProcessoCompra;
    }

    public void setReservasDotacaoProcessoCompra(List<ExecucaoProcessoFonteVO> reservasDotacaoProcessoCompra) {
        this.reservasDotacaoProcessoCompra = reservasDotacaoProcessoCompra;
    }

    public SaldoProcessoLicitatorioVO getSaldoProcesso() {
        return saldoProcesso;
    }

    public void setSaldoProcesso(SaldoProcessoLicitatorioVO saldoProcesso) {
        this.saldoProcesso = saldoProcesso;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        if (hierarquiaOrcamentaria != null) {
            selecionado.setUnidadeOrcamentaria(hierarquiaOrcamentaria.getSubordinada());
        }
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public List<ExecucaoProcessoItemVO> getItensQuantidade() {
        return itensQuantidade;
    }

    public void setItensQuantidade(List<ExecucaoProcessoItemVO> itensQuantidade) {
        this.itensQuantidade = itensQuantidade;
    }

    public List<ExecucaoProcessoItemVO> getItensValor() {
        return itensValor;
    }

    public void setItensValor(List<ExecucaoProcessoItemVO> itensValor) {
        this.itensValor = itensValor;
    }

    public AtaRegistroPreco getAtaRegistroPreco() {
        return ataRegistroPreco;
    }

    public void setAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
    }

    public DispensaDeLicitacao getDispensaDeLicitacao() {
        return dispensaDeLicitacao;
    }

    public void setDispensaDeLicitacao(DispensaDeLicitacao dispensaDeLicitacao) {
        this.dispensaDeLicitacao = dispensaDeLicitacao;
    }

    public FiltroResumoExecucaoVO getFiltroResumoExecucaoVO() {
        return filtroResumoExecucaoVO;
    }

    public void setFiltroResumoExecucaoVO(FiltroResumoExecucaoVO filtroResumoExecucaoVO) {
        this.filtroResumoExecucaoVO = filtroResumoExecucaoVO;
    }

    public ExecucaoProcessoItemVO getExecucaoProcessoItemVO() {
        return execucaoProcessoItemVO;
    }

    public void setExecucaoProcessoItemVO(ExecucaoProcessoItemVO execucaoProcessoItemVO) {
        this.execucaoProcessoItemVO = execucaoProcessoItemVO;
    }

    public HierarquiaOrganizacional getHierarquiaAdmProcessoCompra() {
        return hierarquiaAdmProcessoCompra;
    }

    public void setHierarquiaAdmProcessoCompra(HierarquiaOrganizacional hierarquiaAdmProcessoCompra) {
        this.hierarquiaAdmProcessoCompra = hierarquiaAdmProcessoCompra;
    }
}
