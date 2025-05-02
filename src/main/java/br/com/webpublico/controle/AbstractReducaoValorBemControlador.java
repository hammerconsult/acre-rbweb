package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VoReducaoValorBemContabil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoReducaoValorBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ReducaoValorBemFacade;
import br.com.webpublico.negocios.administrativo.reducaovalorbem.ProcessamentoReducaoValorBem;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.ejb.AsyncResult;
import javax.ejb.EJB;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;

/**
 * User: Wellington
 * Date: 23/06/17
 */
public abstract class AbstractReducaoValorBemControlador extends PrettyControlador<LoteReducaoValorBem> implements CRUD, Serializable {

    @EJB
    private ReducaoValorBemFacade facade;
    private Future<List<Bem>> futureBens;
    private List<Future<ProcessamentoReducaoValorBem>> futureProcessamento;
    private Future<LoteReducaoValorBem> futurePersistencia;
    private Future<LoteEstornoReducaoValorBem> futurePreparaEstorno;
    private Future futureEstorno;
    private boolean futureBensConcluida;
    private boolean futureProcessamentoConcluida;
    private boolean bloquearOpcoesDaTela;
    private AssistenteMovimentacaoBens assistenteMovimentacao;
    private LazyDataModel<ReducaoValorBem> model;
    private LazyDataModel<EstornoReducaoValorBem> modelEstorno;
    private FiltroReducoes filtro;
    private LoteEstornoReducaoValorBem loteEstorno;
    private ConfigMovimentacaoBem configMovimentacaoBem;
    private boolean mostrarTodosRegistroReducaoValorBemContabil;
    private List<ReducaoValorBemContabil> reducoesValorBemContabil;
    private List<ReducaoValorBemEstornoContabil> reducoesValorBemEstornoContabil;
    private List<VoReducaoValorBemContabil> valoresContabilizados;
    private Integer indiceAba;
    private String xhtmlNovoEditar;

    public AbstractReducaoValorBemControlador() {
        super(LoteReducaoValorBem.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void novo() {
        try {
            super.novo();
            bloquearOpcoesDaTela = false;
            loteEstorno = null;
            selecionado.setSituacao(SituacaoReducaoValorBem.EM_ELABORACAO);
            selecionado.setData(new Date());
            selecionado.setDataLancamento(DataUtil.getDataComHoraAtual(getDataOperacao()));
            selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
            recuperarConfiguracaoMovimentacaoBem(selecionado.getData(), OperacaoMovimentacaoBem.DEPRECIACAO);
            indiceAba = 0;
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        filtro = new FiltroReducoes();
        filtro.setLoteReducaoValorBem(selecionado);
        criarItensPaginacao();
        LoteEstornoReducaoValorBem loteEstorno = facade.recuperarLoteEstornoReducaoValorBem(selecionado);
        if (loteEstorno != null) {
            this.loteEstorno = loteEstorno;
            criarItensPaginacaoEstorno();
        }
        Collections.sort(selecionado.getLogReducaoOuEstorno().getMensagens());
    }

    @Override
    public void editar() {
        super.editar();
        indiceAba = 0;
        filtro = new FiltroReducoes();
        filtro.setLoteReducaoValorBem(selecionado);
        if (isConcluida() || isEstornoEmAndamento()) {
            criarItensPaginacao();
        }
        verificarInconsistenciaContabil();

        if (isConcluida() || isEstornoEmAndamento()) {
            LoteEstornoReducaoValorBem loteEstorno = facade.recuperarLoteEstornoReducaoValorBem(selecionado);
            if (loteEstorno != null) {
                this.loteEstorno = loteEstorno;
                criarItensPaginacaoEstorno();
                verificarInconsistenciaContabilEstorno();
            }
        }
        Collections.sort(selecionado.getLogReducaoOuEstorno().getMensagens());
    }

    private void verificarInconsistenciaContabil() {
        reducoesValorBemContabil = facade.buscarReducaoValorBemContabil(selecionado);
        if (reducoesValorBemContabil != null && !reducoesValorBemContabil.isEmpty()) {
            if (hasBensNaoContabilizadosReducao()) {
                setMostrarTodosRegistroReducaoValorBemContabil(false);
            }
        }
    }

    private void verificarInconsistenciaContabilEstorno() {
        reducoesValorBemEstornoContabil = facade.buscarReducaoValorBemEstornoContabil(loteEstorno);
        if (reducoesValorBemEstornoContabil != null && !reducoesValorBemEstornoContabil.isEmpty()) {
            if (hasBensNaoContabilizadosEstorno()) {
                facade.alterarSituacaoLoteReducaoValorBem(selecionado, SituacaoReducaoValorBem.ESTORNO_EM_ANDAMENTO);
                setMostrarTodosRegistroReducaoValorBemContabil(false);
                indiceAba = 5;
            }
        }
    }

    public boolean hasBensNaoContabilizadosReducao() {
        if (reducoesValorBemContabil != null && !reducoesValorBemContabil.isEmpty()) {
            for (ReducaoValorBemContabil red : reducoesValorBemContabil) {
                if (red.getBensMoveis() == null && red.isEmAndamento()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasBensNaoContabilizadosEstorno() {
        if (reducoesValorBemEstornoContabil != null && !reducoesValorBemEstornoContabil.isEmpty()) {
            for (ReducaoValorBemEstornoContabil red : reducoesValorBemEstornoContabil) {
                if (red.getBensMoveis() == null && red.isEmAndamento()) {
                    return true;
                }
            }
        }
        return false;
    }

    public BigDecimal getValorTotalBensContabilizados() {
        BigDecimal total = BigDecimal.ZERO;
        for (ReducaoValorBemContabil red : reducoesValorBemContabil) {
            if (mostrarTodosRegistroReducaoValorBemContabil) {
                total = total.add(red.getValor());
            } else {
                if (!red.isContabilizado()) {
                    total = total.add(red.getValor());
                }
            }
        }
        return total;
    }

    public BigDecimal getValorTotalBensContabilizadosEstorno() {
        BigDecimal total = BigDecimal.ZERO;
        for (ReducaoValorBemEstornoContabil red : reducoesValorBemEstornoContabil) {
            if (mostrarTodosRegistroReducaoValorBemContabil) {
                total = total.add(red.getValor());
            } else {
                if (!red.isContabilizado()) {
                    total = total.add(red.getValor());
                }
            }
        }
        return total;
    }


    public BigDecimal getValorTotalReducao() {
        BigDecimal total = BigDecimal.ZERO;
        for (VoReducaoValorBemContabil red : valoresContabilizados) {
            total = total.add(red.getReducao());
        }
        return total;
    }

    public BigDecimal getValorTotalBensMoveis() {
        BigDecimal total = BigDecimal.ZERO;
        for (VoReducaoValorBemContabil red : valoresContabilizados) {
            total = total.add(red.getBensMoveis());
        }
        return total;
    }

    public BigDecimal getValorTotalGrupoPatrimonial() {
        BigDecimal total = BigDecimal.ZERO;
        for (VoReducaoValorBemContabil red : valoresContabilizados) {
            total = total.add(red.getGrupoBem());
        }
        return total;
    }

    public BigDecimal getValorTotalContabil() {
        BigDecimal total = BigDecimal.ZERO;
        for (VoReducaoValorBemContabil red : valoresContabilizados) {
            total = total.add(red.getContabil());
        }
        return total;
    }

    public BigDecimal getValorTotalDiferenca() {
        BigDecimal total = BigDecimal.ZERO;
        for (VoReducaoValorBemContabil red : valoresContabilizados) {
            total = total.add(red.getDiferenca());
        }
        return total;
    }

    public List<HierarquiaOrganizacional> completarUnidadeOrcamentaria(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().buscarFiltrandoHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(
            facade.getSistemaFacade().getUsuarioCorrente(),
            getDataOperacao(),
            parte.trim());
    }

    public void recuperarProcessoReducaoValorBemNoEditar(String idComponente) {
        try {
            if (isOperacaoEditar() && (isEmElaboracao() || isEmAndamento())) {
                Object entity = Web.pegaDaSessao("SELECIONADO");
                Object assistente = Web.pegaDaSessao("ASSISTENTE");
                recuperarConfiguracaoMovimentacaoBem(selecionado.getData(), OperacaoMovimentacaoBem.DEPRECIACAO);
                novoAssistenteMovimentacao();
                selecionado.setReducoes(new ArrayList<ReducaoValorBem>());

                if (entity != null && assistente != null) {
                    selecionado.setReducoes(((LoteReducaoValorBem) entity).getReducoes());
                    assistenteMovimentacao.setBensSelecionados(((AssistenteMovimentacaoBens) assistente).getBensSelecionados());
                    assistenteMovimentacao.setBensBloqueados(((AssistenteMovimentacaoBens) assistente).getBensSelecionados());
                    futureBens = new AsyncResult<>(assistenteMovimentacao.getBensSelecionados());
                    setFutureBensConcluida(true);
                    FacesUtil.executaJavaScript("atualizarFormulario()");
                } else {
                    pesquisarBens(idComponente);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void pesquisarBens(String idComponente) {
        try {
            Util.validarCampos(selecionado);
            recuperarConfiguracaoMovimentacaoBem(selecionado.getData(), OperacaoMovimentacaoBem.DEPRECIACAO);
            novoAssistenteMovimentacao();
            validarDataLancamentoAndDataOperacaoBem();
            if (verificarBensBloqueadoSingletonAoPesquisar()) return;
            futureBens = facade.pesquisarBens(assistenteMovimentacao, selecionado);
            FacesUtil.executaJavaScript("openDialog(dialogAcompanhamento)");
            FacesUtil.executaJavaScript("acompanharConsultaBens('" + idComponente + "')");
        } catch (MovimentacaoBemException me) {
            fecharAguarde();
            FacesUtil.printAllFacesMessages(me.getMensagens());
        } catch (ValidacaoException ve) {
            fecharAguarde();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            fecharAguarde();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private boolean verificarBensBloqueadoSingletonAoPesquisar() {
        if (facade.getSingletonBloqueioPatrimonio().verificarBensBloqueadoSingletonAoPesquisar(assistenteMovimentacao)) {
            if (isOperacaoNovo()) {
                fecharAguarde();
                FacesUtil.executaJavaScript("atualizarFormulario()");
                assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
            } else {
                FacesUtil.addOperacaoNaoPermitida(assistenteMovimentacao.getMensagens().get(0));
                redireciona();
            }
        }
        return false;
    }

    private void fecharAguarde() {
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    private void desbloquearMovimentacaoSingleton() {
        facade.getSingletonBloqueioPatrimonio().desbloquearBens(assistenteMovimentacao.getBensSelecionados());
        fecharAguarde();
    }

    private void bloquearMovimentacaoBens() {
        if (facade.getSingletonBloqueioPatrimonio().verificarBensBloqueadoSingletonAoPesquisar(assistenteMovimentacao)) {
            FacesUtil.executaJavaScript("atualizarFormulario()");
            assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
        }
        if (isOperacaoNovo() && !assistenteMovimentacao.hasInconsistencias()) {
            facade.getConfigMovimentacaoBemFacade().verificarBensSelecionadosSeDisponivelConfiguracaoBloqueio(assistenteMovimentacao);
        }
        for (Bem bem : assistenteMovimentacao.getBensSelecionados()) {
            facade.getSingletonBloqueioPatrimonio().bloquearBem(bem, assistenteMovimentacao);
        }
        assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataLancamento(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void recuperarConfiguracaoMovimentacaoBem(Date dataOperacao, OperacaoMovimentacaoBem operacao) {
        if (configMovimentacaoBem == null) {
            ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(dataOperacao, operacao);
            if (configMovimentacaoBem != null) {
                this.configMovimentacaoBem = configMovimentacaoBem;
            }
        }
    }

    private void novoAssistenteMovimentacaoEstorno() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(getDataOperacao(), operacao);
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(getDataOperacao(), OperacaoMovimentacaoBem.ESTORNO_DEPRECIACAO);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
            assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
        }
        assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
    }

    private void novoAssistenteMovimentacao() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataLancamento(), operacao);
        assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
        assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistenteMovimentacao.setBensSelecionados(assistenteMovimentacao.getBensSalvos());
        assistenteMovimentacao.setSelecionado(selecionado);
        if (selecionado.getHierarquiaOrcamentaria() != null) {
            assistenteMovimentacao.setUnidadeOrganizacional(selecionado.getHierarquiaOrcamentaria().getSubordinada());
        }
    }

    public void simularReducaoValorBem(String idComponente) {
        try {
            List<Bem> bens = futureBens.get();
            if (bens == null || bens.isEmpty()) {
                FacesUtil.addAtencao("Nenhum bem encontrado para a unidade orçamentária: " + selecionado.getHierarquiaOrcamentaria() + ".");
                FacesUtil.executaJavaScript("closeDialog(dialogAcompanhamento)");
                fecharAguarde();
                if (hasInconsistenciaLogErro()) {
                    FacesUtil.addAtencao("Foram encontrado bens bloqueado para movimentação. Clique na aba 'Inconsistências' para ver os detalhes.");
                    FacesUtil.executaJavaScript("atualizarFormulario()");
                    Collections.sort(selecionado.getLogReducaoOuEstorno().getMensagens());
                }
            } else {
                assistenteMovimentacao.setDescricaoProcesso("Processando " + bens.size() + " bem(ns) encontrado(s).");
                assistenteMovimentacao.setTotal(bens.size());

                futureProcessamento = Lists.newArrayList();
                assistenteMovimentacao.getBensBloqueados().addAll(bens);
                assistenteMovimentacao.getBensSelecionados().addAll(bens);
                for (List<Bem> bensParticionados : Lists.partition(bens, bens.size() > 5 ? bens.size() / 5 : bens.size())) {
                    futureProcessamento.add(facade.simularContabilizacaoReducaoValorBem(assistenteMovimentacao, selecionado, bensParticionados));
                }
                FacesUtil.executaJavaScript("acompanharSimulacaoReducaoValorBem('" + idComponente + "')");
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao simular redução de valor dos bens");
            FacesUtil.executaJavaScript("closeDialog(dialogAcompanhamento)");
        }
    }

    public void finalizarSimulacaoReducaoValorBem() {
        try {
            zerarValoresReducao();
            for (Future<ProcessamentoReducaoValorBem> future : futureProcessamento) {
                ProcessamentoReducaoValorBem processamentoReducaoValorBem = future.get();
                atribuirDadosProcessamentoAoLoteReducaoValorBem(selecionado, processamentoReducaoValorBem);
            }
            Web.poeNaSessao("SELECIONADO", selecionado);
            bloquearOpcoesDaTela = true;
            Collections.sort(selecionado.getLogReducaoOuEstorno().getMensagens());
        } catch (Exception e) {
            logger.error("Erro na simulacao de depreciação [{}]", e);
            FacesUtil.addErrorGenerico(e);
        }
    }

    private void zerarValoresReducao() {
        selecionado.setQuantidadeDepreciacao(0);
        selecionado.setValorDepreciacao(BigDecimal.ZERO);
        selecionado.setQuantidadeExaustao(0);
        selecionado.setValorExaustao(BigDecimal.ZERO);
        selecionado.setQuantidadeAmortizacao(0);
        selecionado.setValorAmortizacao(BigDecimal.ZERO);
    }

    private void atribuirDadosProcessamentoAoLoteReducaoValorBem(LoteReducaoValorBem selecionado, ProcessamentoReducaoValorBem processamentoReducaoValorBem) {
        selecionado.getReducoes().addAll(processamentoReducaoValorBem.getReducoes());
        selecionado.getLogReducaoOuEstorno().getMensagens().addAll(processamentoReducaoValorBem.getMsgs());
        selecionado.getBensNaoAplicaveis().addAll(processamentoReducaoValorBem.getNaoAplicaeis());
        selecionado.getBensResidual().addAll(processamentoReducaoValorBem.getBensResidual());
        selecionado.setQuantidadeDepreciacao(selecionado.getQuantidadeDepreciacao() + processamentoReducaoValorBem.getBensDepreciacao().size());
        selecionado.setValorDepreciacao(selecionado.getValorDepreciacao().add(processamentoReducaoValorBem.getValorDepreciacao()));
        selecionado.setQuantidadeExaustao(selecionado.getQuantidadeExaustao() + processamentoReducaoValorBem.getBensExaustao().size());
        selecionado.setValorExaustao(selecionado.getValorExaustao().add(processamentoReducaoValorBem.getValorExaustao()));
        selecionado.setQuantidadeAmortizacao(selecionado.getQuantidadeAmortizacao() + processamentoReducaoValorBem.getBensAmortizacao().size());
        selecionado.setValorAmortizacao(selecionado.getValorAmortizacao().add(processamentoReducaoValorBem.getValorAmortizacao()));
    }


    private void validarSalvar() {
        Util.validarCampos(selecionado);
        validarListaBensVazia();
        validarDataLancamentoAndDataOperacaoBem();
        validarFase();
    }

    private void validarListaBensVazia() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getReducoes() == null || selecionado.getReducoes().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum bem encontrado para iniciar o processo de " + selecionado.getTipoReducao().getDescricao() + ".");
        }
        ve.lancarException();
    }

    private void validarFase() {
        ValidacaoException ve = new ValidacaoException();
        Exercicio exercicio = facade.getExercicioFacade().getExercicioPorAno(DataUtil.getAno(selecionado.getDataLancamento()));
        if (facade.getFaseFacade().temBloqueioFaseParaRecurso(getXhtmlNovoEditar(), selecionado.getDataLancamento(), selecionado.getHierarquiaOrcamentaria().getSubordinada(), exercicio)) {
            HierarquiaOrganizacional hierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataLancamento(), selecionado.getHierarquiaOrcamentaria().getSubordinada(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
            ve.adicionarMensagemDeOperacaoNaoRealizada("A data " + DataUtil.getDataFormatada(selecionado.getDataLancamento()) + " está fora do período fase para a unidade " + hierarquiaOrganizacional.toString() + "!");
        }
        ve.lancarException();
    }

    public String getXhtmlNovoEditar() {
        return xhtmlNovoEditar;
    }

    public void setXhtmlNovoEditar(String xhtmlNovoEditar) {
        this.xhtmlNovoEditar = xhtmlNovoEditar;
    }

    public boolean hasInconsistenciaLogErro() {
        return selecionado.getLogReducaoOuEstorno() != null
            && selecionado.getLogReducaoOuEstorno().getMensagens() != null
            && !selecionado.getLogReducaoOuEstorno().getMensagens().isEmpty();
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            bloquearMovimentacaoBens();
            futurePersistencia = facade.salvar(assistenteMovimentacao, selecionado);
            FacesUtil.executaJavaScript("openDialog(dialogPersistencia)");
            FacesUtil.executaJavaScript("acompanharPersistencia()");
        } catch (MovimentacaoBemException me) {
            FacesUtil.executaJavaScript("atualizarFormulario()");
            if (!isOperacaoNovo()) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(me.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            FacesUtil.executaJavaScript("atualizarFormulario()");
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentacaoSingleton();
            descobrirETratarException(e);
        }
    }

    public void processarAndSalvar() {
        if (hasInconsistenciaLogErro()) {
            FacesUtil.executaJavaScript("openDialog(dialogAlertaInconsistenciaAoSalvar)");
        } else {
            salvar();
        }
    }

    public void processarAndConcluir() {
        if (hasInconsistenciaLogErro()) {
            FacesUtil.executaJavaScript("openDialog(dialogAlertaInconsistenciaAoProcessar)");
        } else {
            concluir();
        }
    }

    public void abrirDlgValoresContabilizados(String idComponente) {
        if (isEmAndamento()) {
            valoresContabilizados = facade.buscarReducaoValorBemContabilValoresContabilizados(selecionado);
        }
        if (isEstornoEmAndamento()) {
            valoresContabilizados = facade.buscarReducaoValorBemEstornoContabilValoresContabilizados(loteEstorno);
        }
        FacesUtil.executaJavaScript("dlgValoresContabilizado.show()");
        FacesUtil.atualizarComponente(idComponente + ":formValoresContabilizado");
    }

    private List<ReducaoValorBem> getReducaoValorBensNaoContabilizadas() {
        List<ReducaoValorBem> reducoes = Lists.newArrayList();
        List<ReducaoValorBemContabil> list = facade.buscarReducaoValorBemContabil(selecionado, false);
        for (ReducaoValorBemContabil redCont : list) {
            for (ReducaoValorBem redBem : selecionado.getReducoes()) {
                if (redCont.getBem().equals(redBem.getBem())) {
                    reducoes.add(redBem);
                }
            }
        }
        return reducoes;
    }

    public void concluir() {
        try {
            validarFase();
            validarListaBensVazia();
            validarDataLancamentoAndDataOperacaoBem();
            bloquearMovimentacaoBens();
            facade.atribuirReducaoValorBemContabilEmAndamento(selecionado);
            if (selecionado.isEmElaboracao()) {
                facade.alterarSituacaoLoteReducaoValorBem(selecionado, SituacaoReducaoValorBem.EM_ANDAMENTO);
                futurePersistencia = facade.concluirReducao(assistenteMovimentacao, selecionado);
                FacesUtil.executaJavaScript("openDialog(dialogPersistencia)");
                FacesUtil.executaJavaScript("acompanharPersistencia()");
            } else {
                setBloquearOpcoesDaTela(true);
                List<ReducaoValorBem> reducoes = getReducaoValorBensNaoContabilizadas();
                futurePersistencia = facade.concluirReducaoEmAndamento(assistenteMovimentacao, selecionado, reducoes);
                FacesUtil.executaJavaScript("openDialog(dialogPersistencia)");
                FacesUtil.executaJavaScript("acompanharPersistencia()");
            }
        } catch (MovimentacaoBemException ex) {
            fecharAguarde();
            if (!isOperacaoNovo()) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            fecharAguarde();
            desbloquearMovimentacaoSingleton();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            fecharAguarde();
            desbloquearMovimentacaoSingleton();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            fecharAguarde();
            desbloquearMovimentacaoSingleton();
            descobrirETratarException(e);
        }
    }

    @Override
    public void excluir() {
        try {
            recuperarConfiguracaoMovimentacaoBem(selecionado.getData(), OperacaoMovimentacaoBem.DEPRECIACAO);
            facade.remover(selecionado, configMovimentacaoBem);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            redireciona();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarDatasEstorno() {
        ValidacaoException ve = new ValidacaoException();
        if (DataUtil.dataSemHorario(selecionado.getDataLancamento()).after(DataUtil.dataSemHorario(getDataOperacao()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data do Estorno da Depreciação não pode ser Inferior que a Data da Depreciação!");
        }
        ve.lancarException();
        LoteEstornoReducaoValorBem estorno = facade.recuperarLoteEstornoReducaoValorBem(selecionado);
        if (estorno != null && !isEstornoEmAndamento()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(selecionado.getTipoReducao().getDescricao() + " já foi estornada em outro processo.");
        }
        ve.lancarException();
        configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataLancamento(), getDataOperacao(), operacao);
    }

    public void finalizarProcesso() {
        if (isOperacaoEditar()) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(assistenteMovimentacao.getDescricaoProcesso());
        }
    }

    public void solicitarEstorno() {
        try {
            validarFase();
            recuperarConfiguracaoMovimentacaoBem(getDataOperacao(), OperacaoMovimentacaoBem.ESTORNO_DEPRECIACAO);
            validarDatasEstorno();
            novoAssistenteMovimentacao();
            FacesUtil.executaJavaScript("openDialog(dialogConfirmaEstorno)");
        } catch (ValidacaoException ve) {
            fecharAguarde();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            fecharAguarde();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void prepararEstorno() {
        try {
            futurePreparaEstorno = facade.prepararEstornoReducaoValorBem(assistenteMovimentacao, selecionado);
            FacesUtil.executaJavaScript("acompanharPreparaEstorno()");
            FacesUtil.executaJavaScript("openDialog(dialogEstorno)");
        } catch (MovimentacaoBemException ex) {
            redireciona();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            fecharAguarde();
            desbloquearMovimentacaoSingleton();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentacaoSingleton();
            fecharAguarde();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentacaoSingleton();
            fecharAguarde();
            descobrirETratarException(e);
        }
    }

    public void estornarEmAndamento() {
        try {
            validarFase();
            recuperarConfiguracaoMovimentacaoBem(loteEstorno.getDataEstorno(), OperacaoMovimentacaoBem.ESTORNO_DEPRECIACAO);
            novoAssistenteMovimentacao();
            futureEstorno = facade.estornarReducaoEmAndamento(assistenteMovimentacao, loteEstorno);
            FacesUtil.executaJavaScript("acompanharEstorno()");
            FacesUtil.executaJavaScript("openDialog(dialogEstorno)");
        } catch (ValidacaoException ex) {
            fecharAguarde();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            fecharAguarde();
        } catch (Exception e) {
            fecharAguarde();
            descobrirETratarException(e);
        }
    }

    public void estornar() {
        try {
            validarEstorno();
            bloquearMovimentacaoBens();
            assistenteMovimentacao.setDescricaoProcesso("Validando Contabilização do Estorno...");
            facade.simularContabilizacaoEstornoReducaoValorBem(loteEstorno);
            lancarMsgInconsistenciaPreparacaoEstorno();
            futureEstorno = facade.estornarReducao(assistenteMovimentacao, loteEstorno);
            FacesUtil.executaJavaScript("acompanharEstorno()");
            FacesUtil.executaJavaScript("openDialog(dialogEstorno)");
        } catch (MovimentacaoBemException ex) {
            redireciona();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            fecharAguarde();
            desbloquearMovimentacaoSingleton();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            fecharAguarde();
            desbloquearMovimentacaoSingleton();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            fecharAguarde();
            desbloquearMovimentacaoSingleton();
            descobrirETratarException(e);
        }
    }

    public void lancarMsgInconsistenciaPreparacaoEstorno() {
        ValidacaoException ve = new ValidacaoException();
        if (loteEstorno.getLogReducaoOuEstorno() != null && !loteEstorno.getMensagens().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Foram encontradas inconsistências durante a preparação do estorno. Clique na aba 'Inconsistências' para ver os detalhes.");
            FacesUtil.executaJavaScript("atualizarFormulario()");
            indiceAba = 0;
        }
        ve.lancarException();
    }

    private void validarEstorno() {
        ValidacaoException ve = new ValidacaoException();
        if (loteEstorno == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O estorno redução valor bem está nulo.");
        }
        ve.lancarException();
        if (loteEstorno.getEstornos() == null || loteEstorno.getEstornos().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum bem encontrado para ser estornado.");
        }
        ve.lancarException();
    }

    public boolean futureEstornoConcluida() {
        return futureEstorno != null && futureEstorno.isDone();
    }

    public boolean futurePreparaEstornoConcluida() {
        return futurePreparaEstorno != null && futurePreparaEstorno.isDone();
    }

    public void acompanharEstorno() {
        if (!futureEstornoConcluida()) {
            return;
        }
        desbloquearMovimentacaoSingleton();
        FacesUtil.executaJavaScript("pararTimer()");
    }

    public void acompanharPreparaEstorno() {
        if (!futurePreparaEstornoConcluida()) {
            return;
        }
        try {
            loteEstorno = futurePreparaEstorno.get();
            FacesUtil.executaJavaScript("estornarProcesso()");
        } catch (Exception e) {
            logger.error("Erro no acompanhamento do estorno: {}", e);
        }
    }

    private void criarItensPaginacao() {
        model = new LazyDataModel<ReducaoValorBem>() {

            @Override
            public List<ReducaoValorBem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                filtro = new FiltroReducoes();
                filtro.setFilters(filters);
                filtro.setLoteReducaoValorBem(selecionado);
                filtro.setPrimeiroRegistro(first);
                filtro.setQuantidadeRegistro(pageSize);
                setRowCount(facade.quantidadeReducoes(filtro));
                return facade.recuperarReducoes(filtro);
            }
        };
    }

    private void criarItensPaginacaoEstorno() {
        modelEstorno =
            new LazyDataModel<EstornoReducaoValorBem>() {
                @Override
                public List<EstornoReducaoValorBem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                    filtro = new FiltroReducoes();
                    filtro.setLoteReducaoValorBem(selecionado);
                    filtro.setLoteEstornoReducaoValorBem(loteEstorno);
                    filtro.setPrimeiroRegistro(first);
                    filtro.setQuantidadeRegistro(pageSize);
                    filtro.setFilters(filters);
                    setRowCount(facade.quantidadeReducoesEstorno(filtro));
                    return facade.recuperarEstornoReducaoValorBem(filtro);
                }
            };
    }

    public LazyDataModel<ReducaoValorBem> getModel() {
        return model;
    }

    public void setModel(LazyDataModel<ReducaoValorBem> model) {
        this.model = model;
    }

    public BigDecimal getValorOriginalReducao() {
        if (selecionado.getId() == null) {
            BigDecimal valorTotalReducao = BigDecimal.ZERO;
            for (ReducaoValorBem reducaoValorBem : selecionado.getReducoes()) {
                valorTotalReducao = valorTotalReducao.add(reducaoValorBem.getEstadoResultante().getValorOriginal());
            }
            return valorTotalReducao;
        }
        return facade.totalBens(filtro);
    }

    public BigDecimal getValorTotalAReduzir() {
        if (selecionado.getId() == null) {
            if (selecionado.getReducoes() != null) {
                BigDecimal valorTotalAReduzir = BigDecimal.ZERO;
                for (ReducaoValorBem reducaoValorBem : selecionado.getReducoes()) {
                    valorTotalAReduzir = valorTotalAReduzir.add(reducaoValorBem.getValorDoLancamento());
                }
                return valorTotalAReduzir;
            }
        }
        return facade.totalReducoes(filtro);
    }

    public Integer getQuantidadeGeral() {
        Integer total = 0;

        total += selecionado.getQuantidadeDepreciacao();
        total += selecionado.getQuantidadeExaustao();
        total += selecionado.getQuantidadeAmortizacao();

        if (hasInconsistenciaLogErro()) {
            total += selecionado.getLogReducaoOuEstorno().getMensagens().size();
        }
        if (selecionado.getBensNaoAplicaveis() != null && !selecionado.getBensNaoAplicaveis().isEmpty()) {
            total += selecionado.getBensNaoAplicaveis().size();
        }
        if (selecionado.getBensResidual() != null && !selecionado.getBensResidual().isEmpty()) {
            total += selecionado.getBensResidual().size();
        }
        return total;
    }

    public BigDecimal getValorTotalGeral() {
        BigDecimal total = BigDecimal.ZERO;

        total = total.add(selecionado.getValorDepreciacao());
        total = total.add(selecionado.getValorExaustao());
        total = total.add(selecionado.getValorAmortizacao());

        if (hasInconsistenciaLogErro()) {
            total = total.add(selecionado.getValorTotalInconsistencia());
        }

        if (selecionado.getBensNaoAplicaveis() != null && !selecionado.getBensNaoAplicaveis().isEmpty()) {
            total = total.add(selecionado.getValorTotalNaoAplicavel());
        }
        if (selecionado.getBensResidual() != null && !selecionado.getBensResidual().isEmpty()) {
            total = total.add(selecionado.getValorTotalResidual());
        }
        return total;
    }

    public int getQuantidadeReducoes() {
        if (selecionado.getId() == null) {
            return selecionado.getReducoes().size();
        }
        return facade.quantidadeReducoes(filtro);
    }

    public int getQuantidadeReducoesEstorno() {
        try {
            return facade.quantidadeReducoesEstorno(filtro);
        } catch (Exception e) {
            return 0;
        }
    }

    public LazyDataModel<EstornoReducaoValorBem> getModelEstorno() {
        return modelEstorno;
    }

    public void setModelEstorno(LazyDataModel<EstornoReducaoValorBem> modelEstorno) {
        this.modelEstorno = modelEstorno;
    }

    public BigDecimal getValorTotalEstornado() {
        try {
            return facade.totalReducoesEstorno(filtro);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public boolean isSalvo() {
        return selecionado.getId() != null;
    }

    public boolean isEmElaboracao() {
        return selecionado.getSituacao() != null && selecionado.isEmElaboracao();
    }

    public boolean isEmAndamento() {
        return selecionado.getSituacao() != null && selecionado.isEmAndamento();
    }

    public boolean isConcluida() {
        return selecionado.getSituacao() != null && selecionado.isConcluida();
    }

    public boolean isEstornada() {
        return selecionado.getSituacao() != null && selecionado.isEstornada();
    }

    public boolean isEstornoEmAndamento() {
        return selecionado.getSituacao() != null && selecionado.isEstornoEmAndamento();
    }

    public class FiltroReducoes {
        private int primeiroRegistro;
        private int quantidadeRegistro;
        private LoteReducaoValorBem loteReducaoValorBem;
        private LoteEstornoReducaoValorBem loteEstornoReducaoValorBem;
        private Map<String, String> filters;

        public FiltroReducoes() {
        }

        public int getPrimeiroRegistro() {
            return primeiroRegistro;
        }

        public void setPrimeiroRegistro(int primeiroRegistro) {
            this.primeiroRegistro = primeiroRegistro;
        }

        public int getQuantidadeRegistro() {
            return quantidadeRegistro;
        }

        public void setQuantidadeRegistro(int quantidadeRegistro) {
            this.quantidadeRegistro = quantidadeRegistro;
        }

        public LoteReducaoValorBem getLoteReducaoValorBem() {
            return loteReducaoValorBem;
        }

        public void setLoteReducaoValorBem(LoteReducaoValorBem loteReducaoValorBem) {
            this.loteReducaoValorBem = loteReducaoValorBem;
        }

        public LoteEstornoReducaoValorBem getLoteEstornoReducaoValorBem() {
            return loteEstornoReducaoValorBem;
        }

        public void setLoteEstornoReducaoValorBem(LoteEstornoReducaoValorBem loteEstornoReducaoValorBem) {
            this.loteEstornoReducaoValorBem = loteEstornoReducaoValorBem;
        }

        public Map<String, String> getFilters() {
            return filters;
        }

        public void setFilters(Map<String, String> filters) {
            this.filters = filters;
        }
    }


    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteMovimentacao) {
        this.assistenteMovimentacao = assistenteMovimentacao;
    }

    public Future<List<Bem>> getFutureBens() {
        return futureBens;
    }

    public void setFutureBens(Future<List<Bem>> futureBens) {
        this.futureBens = futureBens;
    }


    public boolean isFutureBensConcluida() {
        futureBensConcluida = false;
        if (futureBens != null && futureBens.isDone()) {
            futureBensConcluida = true;
        }
        return futureBensConcluida;
    }

    public void setFutureBensConcluida(boolean futureBensConcluida) {
        this.futureBensConcluida = futureBensConcluida;
    }

    public List<Future<ProcessamentoReducaoValorBem>> getFutureProcessamento() {
        return futureProcessamento;
    }

    public void setFutureProcessamento(List<Future<ProcessamentoReducaoValorBem>> futureProcessamento) {
        this.futureProcessamento = futureProcessamento;
    }

    public boolean isFutureProcessamentoConcluida() {
        futureProcessamentoConcluida = false;
        if (futureProcessamento != null && !futureProcessamento.isEmpty()) {
            for (Future future : futureProcessamento) {
                if (!future.isDone()) {
                    futureProcessamentoConcluida = false;
                    return futureProcessamentoConcluida;
                }
            }
            futureProcessamentoConcluida = true;
        }
        return futureProcessamentoConcluida;
    }

    public void setFutureProcessamentoConcluida(boolean futureProcessamentoConcluida) {
        this.futureProcessamentoConcluida = futureProcessamentoConcluida;
    }

    public boolean isBloquearOpcoesDaTela() {
        return bloquearOpcoesDaTela;
    }

    public void setBloquearOpcoesDaTela(boolean bloquearOpcoesDaTela) {
        this.bloquearOpcoesDaTela = bloquearOpcoesDaTela;
    }

    public Future<LoteReducaoValorBem> getFuturePersistencia() {
        return futurePersistencia;
    }

    public void setFuturePersistencia(Future<LoteReducaoValorBem> futurePersistencia) {
        this.futurePersistencia = futurePersistencia;
    }

    public void consultarFuturePersistencia() {
        if (futurePersistencia != null && futurePersistencia.isDone()) {
            try {
                selecionado = futurePersistencia.get();
                if (selecionado != null) {
                    if (isOperacaoNovo()) {
                        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
                        Web.poeNaSessao("ASSISTENTE", assistenteMovimentacao);
                        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                    } else {
                        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
                        FacesUtil.addOperacaoRealizada(assistenteMovimentacao.getDescricaoProcesso());
                    }
                    desbloquearMovimentacaoSingleton();
                }
                futurePersistencia = null;
                FacesUtil.executaJavaScript("pararTimerPersitencia()");
            } catch (Exception ex) {
                fecharAguarde();
                futurePersistencia = null;
                assistenteMovimentacao.setBloquearAcoesTela(true);
                FacesUtil.executaJavaScript("pararTimerPersitencia()");
                FacesUtil.executaJavaScript("closeDialog(dialogPersistencia)");
                FacesUtil.executaJavaScript("atualizarFormulario()");
                desbloquearMovimentacaoSingleton();
                assistenteMovimentacao.descobrirETratarException(ex);
            }
        }
    }

    public boolean isMostrarTodosRegistroReducaoValorBemContabil() {
        return mostrarTodosRegistroReducaoValorBemContabil;
    }

    public void setMostrarTodosRegistroReducaoValorBemContabil(boolean mostrarTodosRegistroReducaoValorBemContabil) {
        this.mostrarTodosRegistroReducaoValorBemContabil = mostrarTodosRegistroReducaoValorBemContabil;
    }

    public List<ReducaoValorBemContabil> getReducoesValorBemContabil() {
        return reducoesValorBemContabil;
    }

    public void setReducoesValorBemContabil(List<ReducaoValorBemContabil> reducoesValorBemContabil) {
        this.reducoesValorBemContabil = reducoesValorBemContabil;
    }

    public List<VoReducaoValorBemContabil> getValoresContabilizados() {
        return valoresContabilizados;
    }

    public void setValoresContabilizados(List<VoReducaoValorBemContabil> valoresContabilizados) {
        this.valoresContabilizados = valoresContabilizados;
    }

    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    public List<ReducaoValorBemEstornoContabil> getReducoesValorBemEstornoContabil() {
        return reducoesValorBemEstornoContabil;
    }

    public void setReducoesValorBemEstornoContabil(List<ReducaoValorBemEstornoContabil> reducoesValorBemEstornoContabil) {
        this.reducoesValorBemEstornoContabil = reducoesValorBemEstornoContabil;
    }

    public LoteEstornoReducaoValorBem getLoteEstorno() {
        return loteEstorno;
    }

    public void setLoteEstorno(LoteEstornoReducaoValorBem loteEstorno) {
        this.loteEstorno = loteEstorno;
    }
}
