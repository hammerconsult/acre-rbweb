/*
 * Codigo gerado automaticamente em Tue Dec 20 17:59:34 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.IntegracaoTributarioContabilFiltros;
import br.com.webpublico.entidadesauxiliares.VOUsuarioArrecadacaoTributaria;
import br.com.webpublico.entidadesauxiliares.contabil.LoteBaixaIntegracaoTributarioContabil;
import br.com.webpublico.entidadesauxiliares.contabil.ResumoIntegracaoTributarioContabil;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ManagedBean(name = "integracaoArrecadacaoTributariaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "integrar-arrecadacao", pattern = "/tributario-contabil/integracao-arrecadacao/", viewId = "/faces/financeiro/integracao/arrecadacao/integracao.xhtml"),
    @URLMapping(id = "integrar-estorno", pattern = "/tributario-contabil/integracao-estorno-arrecadacao/", viewId = "/faces/financeiro/integracao/estorno/integracao.xhtml")
})
public class IntegracaoArrecadacaoTributariaControlador implements Serializable {

    private final String URL_FASE_ARRECADACAO = "/financeiro/integracao/arrecadacao/integracao.xhtml";
    private final String URL_FASE_ESTORNO = "/financeiro/integracao/estorno/integracao.xhtml";
    protected static final Logger logger = LoggerFactory.getLogger(IntegracaoArrecadacaoTributariaControlador.class);
    @EJB
    private LancamentoReceitaOrcFacade lancamentoReceitaOrcFacade;
    @EJB
    private ReceitaORCEstornoFacade receitaORCEstornoFacade;
    @EJB
    private CreditoReceberFacade creditoReceberFacade;
    @EJB
    private DividaAtivaContabilFacade dividaAtivaContabilFacade;
    @EJB
    private IntegracaoTributarioContabilFacade integracaoTributarioContabilFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ReceitaLOAFonte receitaLOAFonte;
    private ContaReceita contaReceita;
    private LoteBaixa loteBaixa;
    private List<FonteDeRecursos> fonteDeRecursosesSubConta;
    private TiposCredito tiposCredito;
    private Date dataInicial;
    private Date dataFinal;
    private List<LancamentoReceitaOrc> lancamentoIntegracao;
    private List<ReceitaORCEstorno> lancamentoIntegracaoEstorno;
    private List<CreditoReceber> creditosReceberIntegracao;
    private List<CreditoReceber> creditosReceberIntegracaoEstorno;
    private List<DividaAtivaContabil> dividasAtivaIntegracao;
    private List<DividaAtivaContabil> dividasAtivaIntegracaoEstorno;
    private ContaBancariaEntidade contaBancariaEntidade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private UnidadeOrganizacional unidadeOrganizacional;
    private List<LancamentoReceitaOrc> bloqueadasPorFase;
    private List<ReceitaORCEstorno> bloqueadasPorFaseEstorno;
    private Date novaDataContabilizacao;
    private Boolean podeImportarLote;
    private Boolean somenteNaoIntegrados;
    private CompletableFuture<List<LancamentoReceitaOrc>> futureLancamentosLancados;
    private CompletableFuture<List<ReceitaORCEstorno>> futureLancamentosLancadosEstorno;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private List<LoteBaixa> lotesBaixa;
    private ResumoIntegracaoTributarioContabil resumo;

    public IntegracaoArrecadacaoTributariaControlador() {
        dataInicial = new Date();
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public ConverterAutoComplete getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, lancamentoReceitaOrcFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public Date getNovaDataContabilizacao() {
        return novaDataContabilizacao;
    }

    public void setNovaDataContabilizacao(Date novaDataContabilizacao) {
        this.novaDataContabilizacao = novaDataContabilizacao;
    }

    public List<HierarquiaOrganizacional> completaUnidadeOrganizacional(String parte) {
        return lancamentoReceitaOrcFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorTipo(parte.trim(), getSistemaControlador().getUsuarioCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getDataOperacao(), "ORCAMENTARIA");
    }

    public List<Conta> completaContaReceita(String parte) {
        return lancamentoReceitaOrcFacade.getContaFacade().listaFiltrandoReceita(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public void adicionaUnidadeHierarquiaSelecionada() {
        if (hierarquiaOrganizacional.getSubordinada() != null) {
            unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
        }
    }

    public Boolean getSomenteNaoIntegrados() {
        return somenteNaoIntegrados;
    }

    public void setSomenteNaoIntegrados(Boolean somenteNaoIntegrados) {
        this.somenteNaoIntegrados = somenteNaoIntegrados;
    }

    @URLAction(mappingId = "integrar-arrecadacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void integrarArrecadacao() {
        iniciarCampos();
    }

    @URLAction(mappingId = "integrar-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void integrarEstorno() {
        iniciarCampos();
    }

    private void iniciarCampos() {
        contaReceita = new ContaReceita();
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        unidadeOrganizacional = null;
        limparCampos();
        podeImportarLote = false;
        somenteNaoIntegrados = true;
    }

    public Boolean getPodeImportarLote() {
        return podeImportarLote;
    }

    public void setPodeImportarLote(Boolean podeImportarLote) {
        this.podeImportarLote = podeImportarLote;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public TiposCredito getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TiposCredito tiposCredito) {
        this.tiposCredito = tiposCredito;
    }

    public List<LancamentoReceitaOrc> getLancamentoIntegracao() {
        return lancamentoIntegracao;
    }

    public void setLancamentoIntegracao(List<LancamentoReceitaOrc> lancamentoIntegracao) {
        this.lancamentoIntegracao = lancamentoIntegracao;
    }

    public List<ReceitaORCEstorno> getLancamentoIntegracaoEstorno() {
        return lancamentoIntegracaoEstorno;
    }

    public void setLancamentoIntegracaoEstorno(List<ReceitaORCEstorno> lancamentoIntegracaoEstorno) {
        this.lancamentoIntegracaoEstorno = lancamentoIntegracaoEstorno;
    }

    public List<CreditoReceber> getCreditosReceberIntegracao() {
        return creditosReceberIntegracao;
    }

    public void setCreditosReceberIntegracao(List<CreditoReceber> creditosReceberIntegracao) {
        this.creditosReceberIntegracao = creditosReceberIntegracao;
    }

    public List<CreditoReceber> getCreditosReceberIntegracaoEstorno() {
        return creditosReceberIntegracaoEstorno;
    }

    public void setCreditosReceberIntegracaoEstorno(List<CreditoReceber> creditosReceberIntegracaoEstorno) {
        this.creditosReceberIntegracaoEstorno = creditosReceberIntegracaoEstorno;
    }

    public List<DividaAtivaContabil> getDividasAtivaIntegracao() {
        return dividasAtivaIntegracao;
    }

    public void setDividasAtivaIntegracao(List<DividaAtivaContabil> dividasAtivaIntegracao) {
        this.dividasAtivaIntegracao = dividasAtivaIntegracao;
    }

    public List<DividaAtivaContabil> getDividasAtivaIntegracaoEstorno() {
        return dividasAtivaIntegracaoEstorno;
    }

    public void setDividasAtivaIntegracaoEstorno(List<DividaAtivaContabil> dividasAtivaIntegracaoEstorno) {
        this.dividasAtivaIntegracaoEstorno = dividasAtivaIntegracaoEstorno;
    }


    public void importarReceitasTributariasArrecadacao() {
        importarReceitasPorTipoIntegracao(TipoIntegracao.ARRECADACAO);
    }

    public void importarReceitasTributariasEstorno() {
        importarReceitasPorTipoIntegracao(TipoIntegracao.ESTORNO_ARRECADACAO);
    }

    private void importarReceitasPorTipoIntegracao(TipoIntegracao tipoIntegracao) {
        try {
            validarConcorrenciaItemLoteBaixa();
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(reprocessamentoLancamentoContabilSingleton.getMensagemConcorrenciaEnquantoReprocessa());
            }
            if (integracaoTributarioContabilFacade.getSingletonArrecadacaoTributaria().isExecutandoIntegracao()) {
                throw new ExcecaoNegocioGenerica(integracaoTributarioContabilFacade.getSingletonArrecadacaoTributaria().getMensagemJaExecutando());
            }
            bloquearItensDoLote();
            importarReceitasTributarias(tipoIntegracao);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            executarAcoesConcorrenciaItemLoteBaixa();
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
            executarAcoesConcorrenciaItemLoteBaixa();
        }
    }

    private void executarAcoesConcorrenciaItemLoteBaixa() {
        desbloquearItensDoLote();
        limparCampos();
        FacesUtil.atualizarComponente("Formulario");
    }

    private void importarReceitasTributarias(TipoIntegracao tipoIntegracao) {
        try {
            validarImportarReceita(tipoIntegracao);
            if (validarReceitasBloqueadasAoSalvar(tipoIntegracao)) {
                FacesUtil.executaJavaScript("iniciaImportacao()");
                integracaoTributarioContabilFacade.getSingletonArrecadacaoTributaria().setExecutandoIntegracao(true);
                assistenteBarraProgresso = new AssistenteBarraProgresso();
                assistenteBarraProgresso.setDescricaoProcesso("Importação de Receitas Tributárias na Contabilidade.");
                assistenteBarraProgresso.setUsuarioSistema(getSistemaControlador().getUsuarioCorrente());

                if (TipoIntegracao.ARRECADACAO.equals(tipoIntegracao)) {
                    assistenteBarraProgresso.setTotal(lancamentoIntegracao.size() +
                        creditosReceberIntegracao.size() + dividasAtivaIntegracao.size());
                    assistenteBarraProgresso.removerProcessoDoAcompanhamento();
                    futureLancamentosLancados = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                        () -> integracaoTributarioContabilFacade.importarReceitasTributariasArrecadacao(lancamentoIntegracao,
                            creditosReceberIntegracao, dividasAtivaIntegracao, assistenteBarraProgresso));
                } else {
                    assistenteBarraProgresso.setTotal(lancamentoIntegracaoEstorno.size() +
                        creditosReceberIntegracaoEstorno.size() + dividasAtivaIntegracaoEstorno.size());
                    assistenteBarraProgresso.removerProcessoDoAcompanhamento();
                    futureLancamentosLancadosEstorno =
                        AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                            () -> integracaoTributarioContabilFacade.importarReceitasTributariasEstorno(lancamentoIntegracaoEstorno,
                                creditosReceberIntegracaoEstorno, dividasAtivaIntegracaoEstorno, assistenteBarraProgresso));
                }
            }
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearItensDoLote();
            lancamentoReceitaOrcFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(ex);
            assistenteBarraProgresso.setExecutando(false);
            integracaoTributarioContabilFacade.getSingletonArrecadacaoTributaria().setExecutandoIntegracao(false);
        } catch (ValidacaoException ve) {
            desbloquearItensDoLote();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            assistenteBarraProgresso.setExecutando(false);
            integracaoTributarioContabilFacade.getSingletonArrecadacaoTributaria().setExecutandoIntegracao(false);
        } catch (Exception ex) {
            desbloquearItensDoLote();
            FacesUtil.addErrorPadrao(ex);
            assistenteBarraProgresso.setExecutando(false);
            integracaoTributarioContabilFacade.getSingletonArrecadacaoTributaria().setExecutandoIntegracao(false);
        }
    }

    private void validarConcorrenciaItemLoteBaixa() {
        ValidacaoException ve = new ValidacaoException();
        for (LoteBaixa lb : lotesBaixa) {
            VOUsuarioArrecadacaoTributaria voUsuarioArrecadacaoTributaria = integracaoTributarioContabilFacade.getSingletonArrecadacaoTributaria().verificarDisponibilidadeDosItens(lb);
            if (!voUsuarioArrecadacaoTributaria.isDisponivel()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Os itens do Lote Baixa <b>" + lb.getCodigoLote() + "</b> já estão sendo integrados pelo usuário: " + voUsuarioArrecadacaoTributaria.getUsuario());
            }
        }
        ve.lancarException();
    }

    public void finalizarImportacaoReceitaTributariaArrecadacao() {
        finalizarImportacaoReceitaTributaria(TipoIntegracao.ARRECADACAO);
    }

    public void finalizarImportacaoReceitaTributariaEstorno() {
        finalizarImportacaoReceitaTributaria(TipoIntegracao.ESTORNO_ARRECADACAO);
    }

    private void finalizarImportacaoReceitaTributaria(TipoIntegracao tipoIntegracao) {
        try {
            if (TipoIntegracao.ARRECADACAO.equals(tipoIntegracao)) {
                if (futureLancamentosLancados != null && futureLancamentosLancados.isDone()) {
                    if (!assistenteBarraProgresso.getMensagensValidacaoFacesUtil().isEmpty()) {
                        for (FacesMessage facesMessage : assistenteBarraProgresso.getMensagensValidacaoFacesUtil()) {
                            FacesUtil.addOperacaoNaoRealizada(facesMessage.getDetail());
                        }
                        FacesUtil.executaJavaScript("terminaImportacao();");
                    } else {
                        for (LoteBaixa lb : lotesBaixa) {
                            integracaoTributarioContabilFacade.marcarLoteBaixaComoIntegradoArrecadacao(lb);
                        }
                        finalizarIntegracaoReceita(futureLancamentosLancados.get(), tipoIntegracao);
                        futureLancamentosLancados = null;
                        FacesUtil.executaJavaScript("terminaImportacao();");
                    }
                }
            } else {
                if (futureLancamentosLancadosEstorno != null && futureLancamentosLancadosEstorno.isDone()) {
                    if (!assistenteBarraProgresso.getMensagensValidacaoFacesUtil().isEmpty()) {
                        for (FacesMessage facesMessage : assistenteBarraProgresso.getMensagensValidacaoFacesUtil()) {
                            FacesUtil.addOperacaoNaoRealizada(facesMessage.getDetail());
                        }
                        FacesUtil.executaJavaScript("terminaImportacao();");
                    } else {
                        for (LoteBaixa lb : lotesBaixa) {
                            integracaoTributarioContabilFacade.marcarLoteBaixaComoIntegradoEstorno(lb);
                        }
                        finalizarIntegracaoReceitaEstorno(futureLancamentosLancadosEstorno.get(), tipoIntegracao);
                        futureLancamentosLancadosEstorno = null;
                        FacesUtil.executaJavaScript("terminaImportacao();");
                    }
                }
            }
            desbloquearItensDoLote();
            limparListas();
        } catch (InterruptedException | ExecutionException e) {
            desbloquearItensDoLote();
            logger.error("Erro ao finalizar importação receita: {}", e);
        }
    }

    private void desbloquearItensDoLote() {
        for (LoteBaixa lb : lotesBaixa) {
            integracaoTributarioContabilFacade.getSingletonArrecadacaoTributaria().desbloquearItens(lb);
        }
    }

    private void bloquearItensDoLote() {
        for (LoteBaixa lb : lotesBaixa) {
            integracaoTributarioContabilFacade.getSingletonArrecadacaoTributaria().bloquearItens(lb, Util.recuperarUsuarioCorrente());
        }
    }

    private void finalizarIntegracaoReceita(List<LancamentoReceitaOrc> lancamentoIntegracaoDeuCerto, TipoIntegracao tipoIntegracao) {
        if (!lancamentoIntegracaoDeuCerto.isEmpty()) {
            FacesUtil.addOperacaoRealizada("Foram importadas " + lancamentoIntegracaoDeuCerto.size() + " receitas de arrecadações tributárias.");
            if (!lancamentoIntegracaoDeuCerto.containsAll(lancamentoIntegracao)) {
                buscarReceitaTributarias(tipoIntegracao);
            }
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    private void finalizarIntegracaoReceitaEstorno(List<ReceitaORCEstorno> lancamentoIntegracaoDeuCerto, TipoIntegracao tipoIntegracao) {
        if (!lancamentoIntegracaoDeuCerto.isEmpty()) {
            FacesUtil.addOperacaoRealizada("Foram importadas " + lancamentoIntegracaoDeuCerto.size() + " receitas de arrecadações tributárias.");
            if (lancamentoIntegracaoDeuCerto.containsAll(lancamentoIntegracao)) {
                if (TipoIntegracao.ARRECADACAO.equals(tipoIntegracao)) {
                    FacesUtil.redirecionamentoInterno("/tributario-contabil/integracao-arrecadacao/");
                } else {
                    FacesUtil.redirecionamentoInterno("/tributario-contabil/integracao-estorno-arrecadacao/");
                }
            } else {
                buscarReceitaTributarias(tipoIntegracao);
                FacesUtil.atualizarComponente("Formulario");
            }
        }
    }

    private void validarImportarReceita(TipoIntegracao tipoIntegracao) {
        ValidacaoException ve = new ValidacaoException();
        if (TipoIntegracao.ARRECADACAO.equals(tipoIntegracao)) {
            if (lancamentoIntegracao.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado arrecadações tributárias para importação.");
            }
        } else {
            if (lancamentoIntegracaoEstorno.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado arrecadações tributárias de estorno para importação.");
            }
        }
        ve.lancarException();
    }

    private void validarValorDoLoteBaixaComLancamentosGerados() {
        ValidacaoException ve = new ValidacaoException();
        for (LoteBaixa lb : lotesBaixa) {
            BigDecimal valorTotalParaIntegrar = getValorTotal(lb);
            if (lb.getValorTotal().compareTo(valorTotalParaIntegrar) != 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do Lote de Baixa (" + Util.formataValor(lb.getValorTotal()) + ") " +
                    "não corresponde com o valor a ser integrado (R$ " + Util.formataValor(valorTotalParaIntegrar) + ")!");
            }
        }
        ve.lancarException();
    }

    private void validarValorDoLoteBaixaComEstornosGerados() {
        ValidacaoException ve = new ValidacaoException();
        for (LoteBaixa lb : lotesBaixa) {
            BigDecimal valorTotalEstornoParaIntegrar = getValorTotalEstorno(lb);
            if (lb.getValorTotal().compareTo(valorTotalEstornoParaIntegrar) != 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do Lote de Baixa (" + Util.formataValor(lb.getValorTotal()) + ") " +
                    "não corresponde com o valor a ser integrado de estorno (R$ " + Util.formataValor(valorTotalEstornoParaIntegrar) + ")!");
            }
        }
        ve.lancarException();
    }

    public void buscarReceitaTributariasArrecadacao() {
        buscarReceitaTributarias(TipoIntegracao.ARRECADACAO);
    }

    public void buscarReceitaTributariasEstorno() {
        buscarReceitaTributarias(TipoIntegracao.ESTORNO_ARRECADACAO);
    }

    private void buscarReceitaTributarias(TipoIntegracao tipoIntegracao) {
        podeImportarLote = true;
        lancamentoIntegracao = Lists.newArrayList();
        try {
            IntegracaoTributarioContabilFiltros filtros = new IntegracaoTributarioContabilFiltros(unidadeOrganizacional, dataInicial, dataFinal, contaReceita, lotesBaixa);

            validarFiltrosIntegrecao();
            ConfiguracaoContabil configuracaoContabil = lancamentoReceitaOrcFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
            Preconditions.checkNotNull(configuracaoContabil.getClasseTribContReceitaRea(), "A Classe Credor configurada para Integração Contábil/Tributário para Receita Realizada não foi informada.");

            if (TipoIntegracao.ARRECADACAO.equals(tipoIntegracao)) {
                lancamentoIntegracao = integracaoTributarioContabilFacade.getLancamentoReceitaOrcContabilizar(filtros);
                creditosReceberIntegracao = integracaoTributarioContabilFacade.getCreditoReceberArrecadacaoContabilizar(filtros, tipoIntegracao);
                dividasAtivaIntegracao = integracaoTributarioContabilFacade.getDividaAtivaArrecadacaoContabilizar(filtros, tipoIntegracao);
            } else {
                lancamentoIntegracaoEstorno = integracaoTributarioContabilFacade.getLancamentoReceitaOrcEstornoContabilizar(filtros);
                creditosReceberIntegracaoEstorno = integracaoTributarioContabilFacade.getCreditoReceberArrecadacaoContabilizar(filtros, tipoIntegracao);
                dividasAtivaIntegracaoEstorno = integracaoTributarioContabilFacade.getDividaAtivaArrecadacaoContabilizar(filtros, tipoIntegracao);
            }
            podeImportarLote = true;

            if (TipoIntegracao.ARRECADACAO.equals(tipoIntegracao)) {
                validarLancamentoCreditoReceber(configuracaoContabil, creditosReceberIntegracao);
                validarLancamentoDividaAtiva(configuracaoContabil, dividasAtivaIntegracao);
                validarLancamentosReceitaOrc(configuracaoContabil);
                validarValorDoLoteBaixaComLancamentosGerados();
            } else {
                validarLancamentoCreditoReceber(configuracaoContabil, creditosReceberIntegracaoEstorno);
                validarLancamentoDividaAtiva(configuracaoContabil, dividasAtivaIntegracaoEstorno);
                validarLancamentosEstornoReceitaOrc(configuracaoContabil);
                validarValorDoLoteBaixaComEstornosGerados();
            }
            montarResumos(tipoIntegracao);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            podeImportarLote = false;
        } catch (Exception ex) {
            lancamentoIntegracao = new ArrayList<>();
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            podeImportarLote = false;
        }
    }

    private void validarLancamentosReceitaOrc(ConfiguracaoContabil configuracaoContabil) {
        if (!lancamentoIntegracao.isEmpty()) {
            bloqueadasPorFase = Lists.newArrayList();
            ValidacaoException ve = new ValidacaoException();
            for (LancamentoReceitaOrc l : lancamentoIntegracao) {
                try {
                    if (lancamentoReceitaOrcFacade.getFaseFacade().temBloqueioFaseParaRecurso(URL_FASE_ARRECADACAO, l.getDataLancamento(), unidadeOrganizacional, getSistemaControlador().getExercicioCorrente())) {
                        bloqueadasPorFase.add(l);
                    } else {
                        if (validarLancamentoIntegracao(configuracaoContabil, l)) continue;
                        lancamentoReceitaOrcFacade.contabilizarLancamentoReceitaOrcFonte(l, true);
                    }
                } catch (ExcecaoNegocioGenerica ex) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(ex.getMessage());
                }
            }
            if (ve.temMensagens()) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
                podeImportarLote = false;
            } else {
                if (!bloqueadasPorFase.isEmpty()) {
                    FacesUtil.atualizarComponente("formMudaData");
                    FacesUtil.executaJavaScript("mudaData.show()");
                }
            }
        } else {
            FacesUtil.addOperacaoNaoRealizada(" Não foi encontrado arrecadações tributárias para importação. ");
        }
    }

    private void validarLancamentosEstornoReceitaOrc(ConfiguracaoContabil configuracaoContabil) {
        if (!lancamentoIntegracaoEstorno.isEmpty()) {
            bloqueadasPorFaseEstorno = Lists.newArrayList();
            ValidacaoException ve = new ValidacaoException();
            for (ReceitaORCEstorno roe : lancamentoIntegracaoEstorno) {
                try {
                    if (lancamentoReceitaOrcFacade.getFaseFacade().temBloqueioFaseParaRecurso(URL_FASE_ESTORNO, roe.getDataEstorno(), unidadeOrganizacional, getSistemaControlador().getExercicioCorrente())) {
                        bloqueadasPorFaseEstorno.add(roe);
                    } else {
                        if (validarLancamentoEstornoIntegracao(configuracaoContabil, roe)) continue;
                        receitaORCEstornoFacade.contabilizarReceitaOrcEstorno(roe, true);
                    }
                } catch (ExcecaoNegocioGenerica ex) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(ex.getMessage());
                }
            }
            if (ve.temMensagens()) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
                podeImportarLote = false;
            } else {
                if (!bloqueadasPorFaseEstorno.isEmpty()) {
                    FacesUtil.atualizarComponente("formMudaData");
                    FacesUtil.executaJavaScript("mudaData.show()");
                }
            }
        } else {
            FacesUtil.addOperacaoNaoRealizada(" Não foi encontrado arrecadações tributárias para importação. ");
        }
    }

    private void validarLancamentoCreditoReceber(ConfiguracaoContabil configuracaoContabil, List<CreditoReceber> lancamentos) {
        ValidacaoException ve = new ValidacaoException();
        for (CreditoReceber cr : lancamentos) {
            try {
                cr.setPessoa(buscarPessoaParaLancamento(cr.getUnidadeOrganizacional(), cr.getDataCredito()));
                cr.setClasseCredor(configuracaoContabil.getClasseTribContReceitaRea());
                cr.setIntegracao(true);

                creditoReceberFacade.contabilizarCreditoReceber(cr, true);
            } catch (ExcecaoNegocioGenerica ex) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(ex.getMessage());
            }
        }
        if (ve.temMensagens()) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            podeImportarLote = false;
        }
    }

    private void validarLancamentoDividaAtiva(ConfiguracaoContabil configuracaoContabil, List<DividaAtivaContabil> lancamentos) {
        ValidacaoException ve = new ValidacaoException();
        for (DividaAtivaContabil da : lancamentos) {
            try {
                da.setPessoa(buscarPessoaParaLancamento(da.getUnidadeOrganizacional(), da.getDataDivida()));
                da.setClasseCredorPessoa(configuracaoContabil.getClasseTribContReceitaRea());
                da.setIntegracao(true);

                dividaAtivaContabilFacade.contabilizarDividaAtiva(da, true);
            } catch (ExcecaoNegocioGenerica ex) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(ex.getMessage());
            }
        }
        if (ve.temMensagens()) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            podeImportarLote = false;
        }
    }

    public Boolean marcarReceitaBloqueadaEstorno(ReceitaORCEstorno roe) {
        return marcarReceitaBloqueada(roe.getDataEstorno(), roe.getUnidadeOrganizacionalOrc(), TipoIntegracao.ESTORNO_ARRECADACAO);
    }

    public Boolean marcarReceitaBloqueadaLRO(LancamentoReceitaOrc l) {
        return marcarReceitaBloqueada(l.getDataLancamento(), l.getUnidadeOrganizacional(), TipoIntegracao.ARRECADACAO);
    }

    private Boolean marcarReceitaBloqueada(Date data, UnidadeOrganizacional unidadeOrganizacional, TipoIntegracao tipoIntegracao) {
        String urlFase = TipoIntegracao.ARRECADACAO.equals(tipoIntegracao) ? URL_FASE_ARRECADACAO : URL_FASE_ESTORNO;
        if (lancamentoReceitaOrcFacade.getFaseFacade().temBloqueioFaseParaRecurso(urlFase, data, unidadeOrganizacional, getSistemaControlador().getExercicioCorrente())) {
            return true;
        }
        return false;
    }

    public Boolean validarReceitasBloqueadasAoSalvar(TipoIntegracao tipoIntegracao) {
        String urlFase = TipoIntegracao.ARRECADACAO.equals(tipoIntegracao) ? URL_FASE_ARRECADACAO : URL_FASE_ESTORNO;
        for (LancamentoReceitaOrc l : lancamentoIntegracao) {
            if (lancamentoReceitaOrcFacade.getFaseFacade().temBloqueioFaseParaRecurso(urlFase, l.getDataLancamento(), l.getUnidadeOrganizacional(), getSistemaControlador().getExercicioCorrente())) {
                FacesUtil.executaJavaScript("mudaData.show()");
                return false;
            }
        }
        return true;
    }

    private boolean validarLancamentoIntegracao(ConfiguracaoContabil configuracaoContabil, LancamentoReceitaOrc l) {
        try {
            l.setPessoa(buscarPessoaParaLancamento(l.getUnidadeOrganizacional(), l.getDataLancamento()));
            l.setClasseCredor(configuracaoContabil.getClasseTribContReceitaRea());
            l.setReceitaDeIntegracao(true);
            Preconditions.checkNotNull(l.getSubConta().getId(), " A Conta Financeira não foi preenchida.");
            Preconditions.checkNotNull(l.getPessoa().getId(), " A Pessoa configurada para Integração Contábil/Tributário para Receita Realizada não foi informada.");
            lancamentoReceitaOrcFacade.getConfigReceitaRealizadaFacade().buscarEventoPorContaReceita(l.getReceitaLOA().getContaDeReceita(), TipoLancamento.NORMAL, l.getDataLancamento(), l.getOperacaoReceitaRealizada());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    private boolean validarLancamentoEstornoIntegracao(ConfiguracaoContabil configuracaoContabil, ReceitaORCEstorno roe) {
        try {
            roe.setPessoa(buscarPessoaParaLancamento(roe.getUnidadeOrganizacionalOrc(), roe.getDataEstorno()));
            roe.setClasseCredor(configuracaoContabil.getClasseTribContReceitaRea());
            roe.setIntegracao(true);
            Preconditions.checkNotNull(roe.getPessoa().getId(), " A Pessoa configurada para Integração Contábil/Tributário para Receita Realizada não foi informada.");
            lancamentoReceitaOrcFacade.getConfigReceitaRealizadaFacade().buscarEventoPorContaReceita(roe.getReceitaLOA().getContaDeReceita(), TipoLancamento.ESTORNO, roe.getDataEstorno(), roe.getOperacaoReceitaRealizada());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    private Pessoa buscarPessoaParaLancamento(UnidadeOrganizacional unidadeOrganizacional, Date dataLancamento) {
        return lancamentoReceitaOrcFacade.getPessoaFacade().recuperaPessoaDaEntidade(unidadeOrganizacional, dataLancamento);
    }

    private void validarFiltrosIntegrecao() {
        ValidacaoException ve = new ValidacaoException();
        if (unidadeOrganizacional == null || unidadeOrganizacional.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Unidade Organizacional!");
        }
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data Inicial!");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data Final!");
        }
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data final deve ser maior que a data inicial.");
        }
        if (!Util.getMesDaData(dataInicial).equals(Util.getMesDaData(dataFinal)) || !Util.getAnoDaData(dataInicial).equals(Util.getAnoDaData(dataFinal))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas devem ter o mesmo mês.");
        }
        ve.lancarException();
    }

    public void limparCampos() {
        hierarquiaOrganizacional = null;
        unidadeOrganizacional = null;
        contaReceita = null;
        dataInicial = getSistemaControlador().getDataOperacao();
        Calendar c = Calendar.getInstance();
        c.setTime(dataInicial);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
        dataInicial = c.getTime();
        dataFinal = getSistemaControlador().getDataOperacao();
        somenteNaoIntegrados = true;
        limparListas();
    }

    private void limparListas() {
        loteBaixa = null;
        lotesBaixa = Lists.newArrayList();
        lancamentoIntegracao = Lists.newArrayList();
        creditosReceberIntegracao = Lists.newArrayList();
        dividasAtivaIntegracao = Lists.newArrayList();
        lancamentoIntegracaoEstorno = Lists.newArrayList();
        creditosReceberIntegracaoEstorno = Lists.newArrayList();
        dividasAtivaIntegracaoEstorno = Lists.newArrayList();
        resumo = null;
    }

    public BigDecimal getValorTotal(LoteBaixa lb) {
        if (lb != null) {
            return integracaoTributarioContabilFacade.buscarTotalDeItens(lb, TipoIntegracao.ARRECADACAO);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorTotalEstorno(LoteBaixa lb) {
        if (lb != null) {
            return integracaoTributarioContabilFacade.buscarTotalDeItens(lb, TipoIntegracao.ESTORNO_ARRECADACAO);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorTotalCreditoReceber() {
        BigDecimal valor = BigDecimal.ZERO;
        if (creditosReceberIntegracao != null) {
            for (CreditoReceber creditoReceber : creditosReceberIntegracao) {
                if (creditoReceber != null) {
                    valor = valor.add(creditoReceber.getValor());
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalDividaAtiva() {
        BigDecimal valor = BigDecimal.ZERO;
        if (dividasAtivaIntegracao != null) {
            for (DividaAtivaContabil dividaAtiva : dividasAtivaIntegracao) {
                if (dividaAtiva != null) {
                    valor = valor.add(dividaAtiva.getValor());
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalCreditoReceberEstorno() {
        BigDecimal valor = BigDecimal.ZERO;
        if (creditosReceberIntegracao != null) {
            for (CreditoReceber creditoReceber : creditosReceberIntegracaoEstorno) {
                if (creditoReceber != null) {
                    valor = valor.add(creditoReceber.getValor());
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalDividaAtivaEstorno() {
        BigDecimal valor = BigDecimal.ZERO;
        if (dividasAtivaIntegracao != null) {
            for (DividaAtivaContabil dividaAtiva : dividasAtivaIntegracaoEstorno) {
                if (dividaAtiva != null) {
                    valor = valor.add(dividaAtiva.getValor());
                }
            }
        }
        return valor;
    }

    public void mudarDatasIntegracoesArrecadacao() {
        mudarDatasIntegracoes(TipoIntegracao.ARRECADACAO);
    }

    public void mudarDatasIntegracoesEstorno() {
        mudarDatasIntegracoes(TipoIntegracao.ESTORNO_ARRECADACAO);
    }

    private void mudarDatasIntegracoes(TipoIntegracao tipoIntegracao) {
        ConfiguracaoContabil configuracaoContabil = lancamentoReceitaOrcFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
        if (validarNovaDataContabilizacao()) {
            if (TipoIntegracao.ARRECADACAO.equals(tipoIntegracao)) {
                for (LancamentoReceitaOrc l : this.bloqueadasPorFase) {
                    l.setDataLancamento(novaDataContabilizacao);
                    l.setDataConciliacao(novaDataContabilizacao);
                    l.setPessoa(buscarPessoaParaLancamento(l.getUnidadeOrganizacional(), l.getDataLancamento()));
                    l.setClasseCredor(configuracaoContabil.getClasseTribContReceitaRea());
                }
                for (CreditoReceber creditoReceber : creditosReceberIntegracao) {
                    creditoReceber.setDataCredito(novaDataContabilizacao);
                    creditoReceber.setDataReferencia(novaDataContabilizacao);
                    creditoReceber.setPessoa(buscarPessoaParaLancamento(creditoReceber.getUnidadeOrganizacional(), creditoReceber.getDataCredito()));
                    creditoReceber.setClasseCredor(configuracaoContabil.getClasseTribContReceitaRea());
                }
                for (DividaAtivaContabil dividaAtivaContabil : dividasAtivaIntegracao) {
                    dividaAtivaContabil.setDataDivida(novaDataContabilizacao);
                    dividaAtivaContabil.setDataReferencia(novaDataContabilizacao);
                    dividaAtivaContabil.setPessoa(buscarPessoaParaLancamento(dividaAtivaContabil.getUnidadeOrganizacional(), dividaAtivaContabil.getDataDivida()));
                    dividaAtivaContabil.setClasseCredorPessoa(configuracaoContabil.getClasseTribContReceitaRea());
                }
                FacesUtil.executaJavaScript("mudaData.hide()");
                validarLancamentoCreditoReceber(configuracaoContabil, creditosReceberIntegracao);
                validarLancamentoDividaAtiva(configuracaoContabil, dividasAtivaIntegracao);
                validarLancamentosReceitaOrc(configuracaoContabil);
            } else {
                for (ReceitaORCEstorno roe : this.bloqueadasPorFaseEstorno) {
                    roe.setDataEstorno(novaDataContabilizacao);
                    roe.setDataConciliacao(novaDataContabilizacao);
                    roe.setPessoa(buscarPessoaParaLancamento(roe.getUnidadeOrganizacionalOrc(), roe.getDataEstorno()));
                    roe.setClasseCredor(configuracaoContabil.getClasseTribContReceitaRea());
                }
                for (CreditoReceber creditoReceber : creditosReceberIntegracaoEstorno) {
                    creditoReceber.setDataCredito(novaDataContabilizacao);
                    creditoReceber.setDataReferencia(novaDataContabilizacao);
                    creditoReceber.setPessoa(buscarPessoaParaLancamento(creditoReceber.getUnidadeOrganizacional(), creditoReceber.getDataCredito()));
                    creditoReceber.setClasseCredor(configuracaoContabil.getClasseTribContReceitaRea());
                }
                for (DividaAtivaContabil dividaAtivaContabil : dividasAtivaIntegracaoEstorno) {
                    dividaAtivaContabil.setDataDivida(novaDataContabilizacao);
                    dividaAtivaContabil.setDataReferencia(novaDataContabilizacao);
                    dividaAtivaContabil.setPessoa(buscarPessoaParaLancamento(dividaAtivaContabil.getUnidadeOrganizacional(), dividaAtivaContabil.getDataDivida()));
                    dividaAtivaContabil.setClasseCredorPessoa(configuracaoContabil.getClasseTribContReceitaRea());
                }
                FacesUtil.executaJavaScript("mudaData.hide()");
                validarLancamentoCreditoReceber(configuracaoContabil, creditosReceberIntegracaoEstorno);
                validarLancamentoDividaAtiva(configuracaoContabil, dividasAtivaIntegracaoEstorno);
                validarLancamentosEstornoReceitaOrc(configuracaoContabil);
            }
        }
    }

    public Boolean validarNovaDataContabilizacao() {
        if (novaDataContabilizacao == null) {
            FacesUtil.addCampoObrigatorio("O campo Data deve ser informado.");
            return false;
        }
        return true;
    }

    public BigDecimal getValorTotalLancamentoIntegracao() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (LancamentoReceitaOrc lancamentoReceitaOrc : lancamentoIntegracao) {
            retorno = retorno.add(lancamentoReceitaOrc.getValor());
        }
        return retorno;
    }

    public BigDecimal getValorTotalLancamentoIntegracaoEstorno() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ReceitaORCEstorno receitaORCEstorno : lancamentoIntegracaoEstorno) {
            retorno = retorno.add(receitaORCEstorno.getValor());
        }
        return retorno;
    }

    public void adicionarLoteBaixa() {
        try {
            validarLoteBaixa();
            Util.adicionarObjetoEmLista(lotesBaixa, loteBaixa);
            loteBaixa = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarLoteBaixa() {
        ValidacaoException ve = new ValidacaoException();
        if (loteBaixa == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Lote Baixa deve ser informado.");
        }
        ve.lancarException();
        if (lotesBaixa.contains(loteBaixa)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Lote Baixa: " + loteBaixa.toStringAutoComplete() + " já foi adicionado na lista.");
        }
        if (!lotesBaixa.isEmpty()) {
            Date dataLote = lotesBaixa.get(0).getDataPagamento();
            if (!dataLote.equals(loteBaixa.getDataPagamento())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Lote Baixa deve ter a data igual a: " + DataUtil.getDataFormatada(dataLote) + ".");
            }
        }
        ve.lancarException();
    }

    public void removerLoteBaixa(LoteBaixa loteBaixa) {
        lotesBaixa.remove(loteBaixa);
    }

    public void montarResumos(TipoIntegracao tipoIntegracao) {
        this.resumo = new ResumoIntegracaoTributarioContabil();
        if (TipoIntegracao.ARRECADACAO.equals(tipoIntegracao)) {
            atualizarLotesResumoPorLancamentoReceitaOrc(lancamentoIntegracao);
            atualizarLotesResumoPorCreditoReceber(creditosReceberIntegracao);
            atualizarLotesResumoPorDividaAtivaContabil(dividasAtivaIntegracao);
        } else {
            atualizarLotesResumoPorReceitaORCEstorno(lancamentoIntegracaoEstorno);
            atualizarLotesResumoPorCreditoReceber(creditosReceberIntegracaoEstorno);
            atualizarLotesResumoPorDividaAtivaContabil(dividasAtivaIntegracaoEstorno);
        }

        for (LoteBaixaIntegracaoTributarioContabil lote : this.resumo.getLotes()) {
            this.resumo.setValorReceita(this.resumo.getValorReceita().add(lote.getValorReceita()));
            this.resumo.setValorCreditoReceber(this.resumo.getValorCreditoReceber().add(lote.getValorCreditoReceber()));
            this.resumo.setValorDividaAtiva(this.resumo.getValorDividaAtiva().add(lote.getValorDividaAtiva()));
        }
    }

    private void atualizarLotesResumoPorLancamentoReceitaOrc(List<LancamentoReceitaOrc> receitas) {
        for (LancamentoReceitaOrc lancamentoReceitaOrc : receitas) {
            if (lancamentoReceitaOrc.getIntegracaoTribCont() != null) {
                for (IntegracaoTributarioContabil integracao : lancamentoReceitaOrc.getIntegracaoTribCont().getItens()) {
                    boolean existeLote = false;
                    for (LoteBaixaIntegracaoTributarioContabil lote : this.resumo.getLotes()) {
                        if (lote.getLoteBaixa().equals(integracao.getItem().getLoteBaixa())) {
                            lote.setValorReceita(lote.getValorReceita().add(integracao.getItem().getValor()));
                            existeLote = true;
                            break;
                        }
                    }
                    if (!existeLote) {
                        this.resumo.getLotes().add(new LoteBaixaIntegracaoTributarioContabil(integracao.getItem().getLoteBaixa(), lancamentoReceitaOrc.getDataConciliacao(), integracao.getItem().getLoteBaixa().getValorTotal(), integracao.getItem().getValor()));
                    }
                }
            }
        }
    }

    private void atualizarLotesResumoPorReceitaORCEstorno(List<ReceitaORCEstorno> receitasEstornos) {
        for (ReceitaORCEstorno lancamentoReceitaOrc : receitasEstornos) {
            if (lancamentoReceitaOrc.getIntegracaoTribCont() != null) {
                for (IntegracaoTributarioContabil integracao : lancamentoReceitaOrc.getIntegracaoTribCont().getItens()) {
                    boolean existeLote = false;
                    for (LoteBaixaIntegracaoTributarioContabil lote : this.resumo.getLotes()) {
                        if (lote.getLoteBaixa().equals(integracao.getItem().getLoteBaixa())) {
                            lote.setValorReceita(lote.getValorReceita().add(integracao.getItem().getValor()));
                            existeLote = true;
                            break;
                        }
                    }
                    if (!existeLote) {
                        this.resumo.getLotes().add(new LoteBaixaIntegracaoTributarioContabil(integracao.getItem().getLoteBaixa(), lancamentoReceitaOrc.getDataConciliacao(), integracao.getItem().getLoteBaixa().getValorTotal(), integracao.getItem().getValor()));
                    }
                }
            }
        }
    }

    private void atualizarLotesResumoPorCreditoReceber(List<CreditoReceber> creditos) {
        for (CreditoReceber cr : creditos) {
            if (cr.getIntegracaoTribCont() != null) {
                for (IntegracaoTributarioContabil integracao : cr.getIntegracaoTribCont().getItens()) {
                    boolean existeLote = false;
                    for (LoteBaixaIntegracaoTributarioContabil lote : this.resumo.getLotes()) {
                        if (lote.getLoteBaixa().equals(integracao.getItem().getLoteBaixa())) {
                            lote.setValorCreditoReceber(lote.getValorCreditoReceber().add(integracao.getItem().getValor()));
                            existeLote = true;
                            break;
                        }
                    }
                    if (!existeLote) {
                        this.resumo.getLotes().add(new LoteBaixaIntegracaoTributarioContabil(integracao.getItem().getLoteBaixa(), cr.getDataCredito(), integracao.getItem().getLoteBaixa().getValorTotal(), integracao.getItem().getValor()));
                    }
                }
            }
        }
    }

    private void atualizarLotesResumoPorDividaAtivaContabil(List<DividaAtivaContabil> dividas) {
        for (DividaAtivaContabil dac : dividas) {
            if (dac.getIntegracaoTribCont() != null) {
                for (IntegracaoTributarioContabil integracao : dac.getIntegracaoTribCont().getItens()) {
                    boolean existeLote = false;
                    for (LoteBaixaIntegracaoTributarioContabil lote : this.resumo.getLotes()) {
                        if (lote.getLoteBaixa().equals(integracao.getItem().getLoteBaixa())) {
                            lote.setValorDividaAtiva(lote.getValorDividaAtiva().add(integracao.getItem().getValor()));
                            existeLote = true;
                            break;
                        }
                    }
                    if (!existeLote) {
                        this.resumo.getLotes().add(new LoteBaixaIntegracaoTributarioContabil(integracao.getItem().getLoteBaixa(), dac.getDataDivida(), integracao.getItem().getLoteBaixa().getValorTotal(), integracao.getItem().getValor()));
                    }
                }
            }
        }
    }

    public ReceitaLOAFonte getReceitaLOAFonte() {
        return receitaLOAFonte;
    }

    public void setReceitaLOAFonte(ReceitaLOAFonte receitaLOAFonte) {
        this.receitaLOAFonte = receitaLOAFonte;
    }

    public List<LoteBaixa> completaLotePorNumeroSituacaoData(String parte) {
        return lancamentoReceitaOrcFacade.getLoteBaixaFacade().buscarLotePorNumeroSituacaoData(parte, dataInicial, dataFinal, somenteNaoIntegrados, SituacaoLoteBaixa.BAIXADO, SituacaoLoteBaixa.BAIXADO_INCONSITENTE);
    }

    public List<LoteBaixa> completaLotePorNumeroSituacaoDataEstornado(String parte) {
        return lancamentoReceitaOrcFacade.getLoteBaixaFacade().buscarLotePorNumeroSituacaoData(parte, dataInicial, dataFinal, somenteNaoIntegrados, SituacaoLoteBaixa.ESTORNADO);
    }

    public void redirecionaParaLista() {
        FacesUtil.redirecionamentoInterno("/receita-realizada/listar/");
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public void setLoteBaixa(LoteBaixa loteBaixa) {
        this.loteBaixa = loteBaixa;
    }

    public List<LancamentoReceitaOrc> getBloqueadasPorFase() {
        return bloqueadasPorFase;
    }

    public void setBloqueadasPorFase(List<LancamentoReceitaOrc> bloqueadasPorFase) {
        this.bloqueadasPorFase = bloqueadasPorFase;
    }

    public List<FonteDeRecursos> getFonteDeRecursosesSubConta() {
        return fonteDeRecursosesSubConta;
    }

    public void setFonteDeRecursosesSubConta(List<FonteDeRecursos> fonteDeRecursosesSubConta) {
        this.fonteDeRecursosesSubConta = fonteDeRecursosesSubConta;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public List<LoteBaixa> getLotesBaixa() {
        return lotesBaixa;
    }

    public void setLotesBaixa(List<LoteBaixa> lotesBaixa) {
        this.lotesBaixa = lotesBaixa;
    }

    public ResumoIntegracaoTributarioContabil getResumo() {
        return resumo;
    }

    public void setResumo(ResumoIntegracaoTributarioContabil resumo) {
        this.resumo = resumo;
    }

}

