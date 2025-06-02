package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ImprimeDemonstrativoDebitos;
import br.com.webpublico.entidadesauxiliares.ObjetoCampoValor;
import br.com.webpublico.entidadesauxiliares.TotalSituacao;
import br.com.webpublico.entidadesauxiliares.VOConsultaDebito;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConsultaDebitoFacade;
import br.com.webpublico.negocios.FeriadoFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroImobiliarioDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroRuralDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonConsultaDebitos;
import br.com.webpublico.seguranca.SingletonMetricas;
import br.com.webpublico.singletons.CacheTributario;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

@ManagedBean(name = "consultaDebitoControlador")
@ViewScoped
@URLMappings(mappings = {

    @URLMapping(id = "consultaDebito",
        pattern = "/tributario/atendimento-ao-contribuinte/consulta-de-debitos/",
        viewId = "/faces/tributario/contacorrente/consultadebitos/consulta.xhtml"),


    @URLMapping(id = "consultaDebitoPorCadastro",
        pattern = "/tributario/atendimento-ao-contribuinte/consulta-de-debitos-por-cadastro/#{consultaDebitoControlador.idCadastro}",
        viewId = "/faces/tributario/contacorrente/consultadebitos/consulta.xhtml"),

    @URLMapping(id = "consultaDebitoPorPessoa",
        pattern = "/tributario/atendimento-ao-contribuinte/consulta-de-debitos-por-pessoa/#{consultaDebitoControlador.idCadastro}",
        viewId = "/faces/tributario/contacorrente/consultadebitos/consulta.xhtml")

})
public class ConsultaDebitoControlador extends AbstractReport implements Serializable, CleannerViewScoped {

    private static final Logger log = LoggerFactory.getLogger(ConsultaDebitoControlador.class);
    private final int maxResultDebitos = 500;
    LinkedList<ResultadoParcela> demosntrativo = Lists.newLinkedList();
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private SingletonConsultaDebitos singletonConsultaDebitos;
    @EJB
    private FeriadoFacade feriadoFacade;
    private SingletonMetricas singletonMetricas;
    @EJB
    private CacheTributario cacheTributario;
    private ConverterAutoComplete converterPessoa;
    private List<Divida> dividasSeleciondas;
    private Divida divida;
    private List<ResultadoParcela> selecionados;
    private Pessoa filtroContribuinte;
    private TipoCadastroTributario tipoCadastroTributario;
    private Cadastro cadastro;
    private Exercicio exercicioInicial, exercicioFinal;
    private ConverterAutoComplete converterCadastroImobiliario, converterCadastroEconomico, converterCadastroRural;
    private SituacaoParcela[] situacoes;
    private Date vencimentoInicial, vencimentoFinal, pagamentoInicial, pagamentoFinal, vencimentoDam;
    private BigDecimal valorDam;
    private Boolean consultarQuadroSocietario;
    private Boolean consultarCadastrosInativos;
    private ConsultaParcela consultaParcela;
    private Map<CadastroConsulta, Boolean> todasParcelaSelecionadas;
    private List<Propriedade> propriedades;
    private List<Compromissario> compromissarios;
    private Boolean pesquisaDividaAtiva, pesquisaExercicio, pesquisaDividaAjuizada, pesquisaParcelamentoDividaAtiva, pesquisaDebitoProtestado;
    private Boolean temDividaDeDividaAtiva;
    private Boolean temDividaDoExercicio;
    private Boolean temDividaDeParcelamento;
    private List<ConsultaParcela.Campo> camposOrdenacao;
    private ConsultaParcela.Campo campoOrdenacao;
    private Long idCadastro;
    private ResultadoParcela resultadoParcelaDetalhamento;
    private LinkedList<ResultadoParcela> parcelasDoParcelamentoOriginado;
    private Boolean todasParcelasEstaoSelecionadas;
    private List<ObjetoCampoValor> informacoesAdicionais;
    private Map<String, BigDecimal> totaisSituacao;
    private Map<String, BigDecimal> totaisSituacaoPorPagina;
    private Map<CadastroConsulta, List<ResultadoParcela>> mapa;
    private Map<CadastroConsulta, List<ResultadoParcela>> mapaPorPagina;
    private Map<CadastroConsulta, TotalTabelaParcelas> mapaTotais;
    private Map<CadastroConsulta, TotalTabelaParcelas> mapaTotaisPorPagina;
    private int paginaAtual = 0, totalPaginas = 0;
    private Boolean consultou = false, mostrarEnderecoCadastro = false;
    private List<Future<List<ResultadoParcela>>> futureConsultaParcela;
    private ContadorConsulta contadorConsulta;
    private String emails;
    private String mensagemEmail;
    private Map<Long, String> numeroDamParcela;
    private transient final ApplicationContext ap;
    private JdbcCadastroImobiliarioDAO jdbcCadastroImobiliarioDAO;
    private JdbcCadastroRuralDAO jdbcCadastroRuralDAO;
    private Boolean situacaoCadastroImobiliario;
    private SituacaoCadastralCadastroEconomico situacaoCadastroEconomico;
    private Boolean situacaoCadastroRural;
    private SituacaoCadastralPessoa situacaoCadastroPessoa;
    private UUID identificadorConsulta = UUID.randomUUID();

    @PostConstruct
    public void init() {
        super.init();
        singletonMetricas = (SingletonMetricas) Util.getSpringBeanPeloNome("singletonMetricas");
    }

    public void cleanScoped() {
        parcelasDoParcelamentoOriginado = null;
        numeroDamParcela = null;
        futureConsultaParcela = null;
        selecionados = null;
        situacoes = null;
        mapa = null;
        mapaPorPagina = null;
        consultaParcela = null;
        demosntrativo = null;
        resultadoParcelaDetalhamento = null;
        FacesContext.getCurrentInstance().getViewRoot()
            .getViewMap().clear();
    }

    public ConsultaDebitoControlador() {
        this.ap = ContextLoader.getCurrentWebApplicationContext();
        this.jdbcCadastroImobiliarioDAO = (JdbcCadastroImobiliarioDAO) this.ap.getBean("cadastroImobiliarioJDBCDAO");
        this.jdbcCadastroRuralDAO = (JdbcCadastroRuralDAO) this.ap.getBean("cadastroRuralDAO");
    }

    public Boolean getConsultou() {
        return consultou;
    }

    public Long getIdCadastro() {
        return idCadastro;
    }

    public void setIdCadastro(Long idCadastro) {
        this.idCadastro = idCadastro;
    }

    public Boolean getMostrarEnderecoCadastro() {
        return mostrarEnderecoCadastro;
    }

    public void setMostrarEnderecoCadastro(Boolean mostrarEnderecoCadastro) {
        this.mostrarEnderecoCadastro = mostrarEnderecoCadastro;
    }

    public ContadorConsulta getContadorConsulta() {
        return contadorConsulta;
    }

    public String getCaminhoPadrao() {
        return "/tributario/atendimento-ao-contribuinte/consulta-de-debitos/";
    }

    public ConsultaParcela getConsultaParcela() {
        return consultaParcela;
    }

    public void setConsultaParcela(ConsultaParcela consultaParcela) {
        this.consultaParcela = consultaParcela;
    }

    public ConsultaParcela.Campo getCampoOrdenacao() {
        return campoOrdenacao;
    }

    public void setCampoOrdenacao(ConsultaParcela.Campo campoOrdenacao) {
        this.campoOrdenacao = campoOrdenacao;
    }

    public List<ResultadoParcela> getResultados() {
        return consultaParcela != null ? consultaParcela.getResultados() : Lists.<ResultadoParcela>newArrayList();
    }

    public Date getDataAtual() {
        return getDataOperacao();
    }

    public SituacaoParcela[] getSituacoes() {
        return situacoes;
    }

    public void setSituacoes(SituacaoParcela[] situacoes) {
        this.situacoes = situacoes;
    }

    public List<ResultadoParcela> getSelecionados() {
        return selecionados;
    }

    public void setSelecionados(List<ResultadoParcela> selecionados) {
        this.selecionados = selecionados;
    }

    public Boolean getConsultarQuadroSocietario() {
        return consultarQuadroSocietario;
    }

    public void setConsultarQuadroSocietario(Boolean consultarQuadroSocietario) {
        this.consultarQuadroSocietario = consultarQuadroSocietario;
    }

    public Boolean getConsultarCadastrosInativos() {
        return consultarCadastrosInativos;
    }

    public void setConsultarCadastrosInativos(Boolean consultarCadastrosInativos) {
        this.consultarCadastrosInativos = consultarCadastrosInativos;
    }

    public List<ObjetoCampoValor> getInformacoesAdicionais() {
        return informacoesAdicionais;
    }

    public void setInformacoesAdicionais(List<ObjetoCampoValor> informacoesAdicionais) {
        this.informacoesAdicionais = informacoesAdicionais;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public ResultadoParcela getResultadoParcelaDetalhamento() {
        return resultadoParcelaDetalhamento;
    }

    public void setResultadoParcelaDetalhamento(ResultadoParcela resultadoParcelaDetalhamento) {
        this.resultadoParcelaDetalhamento = resultadoParcelaDetalhamento;
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;

    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, consultaDebitoFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterGenericoCacheTributario getConverterDivida() {
        return cacheTributario.getConverterDivida();
    }

    public Pessoa getFiltroContribuinte() {
        return filtroContribuinte;
    }

    public void setFiltroContribuinte(Pessoa filtroContribuinte) {
        this.filtroContribuinte = filtroContribuinte;
    }

    public ConverterExercicioCache getConverterExercicio() {
        return cacheTributario.getConverterExercicio();
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastroInicial) {
        this.cadastro = cadastroInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Date getVencimentoFinal() {
        return vencimentoFinal;
    }

    public void setVencimentoFinal(Date vencimentoFinal) {
        this.vencimentoFinal = vencimentoFinal;
    }

    public Date getVencimentoInicial() {
        return vencimentoInicial;
    }

    public void setVencimentoInicial(Date vencimentoInicial) {
        this.vencimentoInicial = vencimentoInicial;
    }

    public Date getPagamentoInicial() {
        return pagamentoInicial;
    }

    public void setPagamentoInicial(Date pagamentoInicial) {
        this.pagamentoInicial = pagamentoInicial;
    }

    public Date getPagamentoFinal() {
        return pagamentoFinal;
    }

    public void setPagamentoFinal(Date pagamentoFinal) {
        this.pagamentoFinal = pagamentoFinal;
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, consultaDebitoFacade.getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, consultaDebitoFacade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public ConverterAutoComplete getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, consultaDebitoFacade.getCadastroRuralFacade());
        }
        return converterCadastroRural;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public List<Divida> getDividasSeleciondas() {
        return dividasSeleciondas;
    }

    public Date getVencimentoDam() {
        return vencimentoDam;
    }

    public BigDecimal getValorDam() {
        return valorDam;
    }

    public Map<String, BigDecimal> getTotaisSituacao() {
        return totaisSituacao;
    }

    public Map<String, BigDecimal> getTotaisSituacaoPorPagina() {
        return totaisSituacaoPorPagina;
    }

    public Map<CadastroConsulta, List<ResultadoParcela>> getMapaPorPagina() {
        return mapaPorPagina;
    }

    public SituacaoCadastralPessoa getSituacaoCadastroPessoa() {
        return situacaoCadastroPessoa;
    }

    public void setSituacaoCadastroPessoa(SituacaoCadastralPessoa situacaoCadastroPessoa) {
        this.situacaoCadastroPessoa = situacaoCadastroPessoa;
    }

    public Boolean getSituacaoCadastroRural() {
        return situacaoCadastroRural;
    }

    public void setSituacaoCadastroRural(Boolean situacaoCadastroRural) {
        this.situacaoCadastroRural = situacaoCadastroRural;
    }

    public SituacaoCadastralCadastroEconomico getSituacaoCadastroEconomico() {
        return situacaoCadastroEconomico;
    }

    public void setSituacaoCadastroEconomico(SituacaoCadastralCadastroEconomico situacaoCadastroEconomico) {
        this.situacaoCadastroEconomico = situacaoCadastroEconomico;
    }

    public Boolean getSituacaoCadastroImobiliario() {
        return situacaoCadastroImobiliario;
    }

    public void setSituacaoCadastroImobiliario(Boolean situacaoCadastroImobiliario) {
        this.situacaoCadastroImobiliario = situacaoCadastroImobiliario;
    }

    public List<String> getSituacoesDasParcelas() {
        List<String> retorno = new ArrayList<>();
        if (totaisSituacao != null) {
            Set<String> strings = totaisSituacao.keySet();
            retorno.addAll(strings);
        }
        return retorno;
    }

    public List<String> getSituacoesDasParcelasPorPagina() {
        List<String> retorno = new ArrayList<>();
        if (totaisSituacaoPorPagina != null) {
            Set<String> strings = totaisSituacaoPorPagina.keySet();
            retorno.addAll(strings);
        }
        return retorno;
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricaoLonga()));
        }
        return toReturn;
    }

    public List<ConsultaParcela.Campo> getCamposOrdenacao() {
        return camposOrdenacao;
    }

    public List<SelectItem> getCamposParaOrdenar() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(ConsultaParcela.Campo.PARCELA_NUMERO, ConsultaParcela.Campo.PARCELA_DIVIDA_SEQUENCIA_PARCELA.getDescricao()));
        toReturn.add(new SelectItem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Campo.PARCELA_VENCIMENTO.getDescricao()));
        toReturn.add(new SelectItem(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Campo.EXERCICIO_ANO.getDescricao()));
        toReturn.add(new SelectItem(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Campo.PARCELA_SITUACAO.getDescricao()));
        toReturn.add(new SelectItem(ConsultaParcela.Campo.PARCELA_SALDO_TOTAL_ATUALIZADO, ConsultaParcela.Campo.PARCELA_SALDO_TOTAL_ATUALIZADO.getDescricao()));
        toReturn.add(new SelectItem(ConsultaParcela.Campo.REFERENCIA_PARCELA, ConsultaParcela.Campo.REFERENCIA_PARCELA.getDescricao()));
        return toReturn;
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (tipoCadastroTributario != null) {
            List<Divida> dividas = tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA) ?
                cacheTributario.getDividas() :
                cacheTributario.getDividasPorTipoCadastro(tipoCadastroTributario);
            for (Divida divida : dividas) {
                toReturn.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return toReturn;
    }

    @URLAction(mappingId = "consultaDebito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtroContribuinte = null;
        cadastro = null;
        exercicioInicial = null;
        exercicioFinal = null;
        vencimentoFinal = null;
        vencimentoInicial = null;
        pagamentoInicial = null;
        pagamentoFinal = null;
        tipoCadastroTributario = TipoCadastroTributario.PESSOA;
        situacoes = new SituacaoParcela[]{SituacaoParcela.EM_ABERTO};
        selecionados = null;
        dividasSeleciondas = Lists.newArrayList();
        valorDam = BigDecimal.ZERO;
        vencimentoDam = getDataOperacao();
        totaisSituacao = new HashMap<>();
        consultarQuadroSocietario = Boolean.FALSE;
        consultarCadastrosInativos = Boolean.TRUE;
        mapa = Maps.newHashMap();
        todasParcelaSelecionadas = Maps.newHashMap();
        mapaTotais = Maps.newHashMap();
        selecionados = Lists.newArrayList();
        consultaParcela = new ConsultaParcela();
        pesquisaDividaAjuizada = false;
        pesquisaDividaAtiva = false;
        pesquisaExercicio = false;
        pesquisaParcelamentoDividaAtiva = false;
        pesquisaDebitoProtestado = false;
        camposOrdenacao = Lists.newArrayList();
        limpaCadastro();
        todasParcelasEstaoSelecionadas = false;
        informacoesAdicionais = Lists.newArrayList();
        mapaPorPagina = Maps.newHashMap();
        totaisSituacaoPorPagina = Maps.newHashMap();
        emails = null;
        mensagemEmail = null;
        preencherFiltrosDaSessao();
        limparSituacaoCadastros();
    }

    @URLAction(mappingId = "consultaDebitoPorCadastro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoPorCadastro() {
        novo();
        cadastro = consultaDebitoFacade.recuperaCadastro(idCadastro);
        tipoCadastroTributario = cadastro.getTipoDeCadastro();
        iniciarPesquisaParaContaCorrente();
    }

    @URLAction(mappingId = "consultaDebitoPorPessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoPorPessoa() {
        novo();
        filtroContribuinte = consultaDebitoFacade.recuperaPessoa(idCadastro);
        tipoCadastroTributario = TipoCadastroTributario.PESSOA;
        iniciarPesquisaParaContaCorrente();
    }


    private void limparSituacaoCadastros() {
        situacaoCadastroImobiliario = true;
        situacaoCadastroEconomico = SituacaoCadastralCadastroEconomico.ATIVA_REGULAR;
        situacaoCadastroRural = true;
        situacaoCadastroPessoa = null;
    }

    private void preencherFiltrosDaSessao() {
        VOConsultaDebito consultaDebito = (VOConsultaDebito) Web.pegaDaSessao("CONSULTA");
        if (consultaDebito != null) {
            tipoCadastroTributario = consultaDebito.getTipoCadastroTributario();
            if (!consultaDebito.isContribuinteGeral()) {
                cadastro = consultaDebito.getCadastro();
            } else {
                filtroContribuinte = consultaDebito.getPessoa();
            }
        }
    }

    private Date getDataOperacao() {
        return new Date();
    }


    public void executarDependenciasDaConsulta() {

        if (futureConsultaParcela != null && !futureConsultaParcela.isEmpty()) {
            for (Future<List<ResultadoParcela>> future : futureConsultaParcela) {
                if (!future.isDone()) {
                    return;
                }
            }
            try {
                LinkedList<ResultadoParcela> resultados = Lists.newLinkedList();
                for (Future<List<ResultadoParcela>> future : futureConsultaParcela) {
                    resultados.addAll(future.get());
                }
                adicionarCompletentosConsulta(resultados);
                ajustarOrdemParcelasCompensadasCanceladasParcelamento();
                montarMapa();
                montarMapaPagina();
                ajustaOrdemPadraoParcela();
                ajustaOrdemCronologicaParcela();
                ajustarComponentesView();
                futureConsultaParcela = null;
                consultou = true;
                contadorConsulta = new ContadorConsulta(1);
                singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), getDescricaoParaMetrica(), System.currentTimeMillis(), MetricaSistema.Tipo.ROTINA_USUARIO);
            } catch (Exception e) {
                log.error("Erro ao executar dependencias da consulta: {}", e);
            }
        }

    }

    private String getDescricaoParaMetrica() {
        return "Consulta de débitos (" + getPessoaDaConsulta() + ")";
    }

    private void ajustarOrdemParcelasCompensadasCanceladasParcelamento() {
        List<ResultadoParcela> parcelasParaOrdenar = Lists.newArrayList();
        for (ResultadoParcela resultadoParcela : consultaParcela.getResultados()) {
            if (resultadoParcela.getTipoCalculoEnumValue().isDependenteDeOutroValorDivida()
                && resultadoParcela.getQuantidadeParcelasDoValorDivida() > 1) {
                parcelasParaOrdenar.add(resultadoParcela);
            }
        }
        consultaParcela.getResultados().removeAll(parcelasParaOrdenar);
        List<ResultadoParcela> temporaria = Lists.newArrayList(consultaParcela.getResultados());
        for (Iterator<ResultadoParcela> iteratorReOrdenar = parcelasParaOrdenar.iterator(); iteratorReOrdenar.hasNext(); ) {
            ResultadoParcela reOrdenar = iteratorReOrdenar.next();
            boolean encontrouOndeAdicionar = false;
            for (Iterator<ResultadoParcela> next = temporaria.iterator(); next.hasNext(); ) {
                ResultadoParcela proximo = next.next();
                if (reOrdenar.getReferencia().startsWith(proximo.getReferencia())
                    && reOrdenar.getParcela().equals(proximo.getParcela())
                    && reOrdenar.getCadastro().equals(proximo.getCadastro())) {
                    int i = consultaParcela.getResultados().indexOf(proximo);
                    consultaParcela.getResultados().add(i + 1, reOrdenar);
                    encontrouOndeAdicionar = true;
                }
            }
            if (!encontrouOndeAdicionar) {
                consultaParcela.getResultados().add(reOrdenar);
            }
        }
    }

    private void ajustarComponentesView() {
        FacesUtil.executaJavaScript("terminaConsulta()");
        FacesUtil.atualizarComponente("Formulario");
        FacesUtil.executaJavaScript("consultando.hide()");
    }

    public void iniciarPesquisaParaContaCorrente() {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), getDescricaoParaMetrica(), System.currentTimeMillis(), MetricaSistema.Tipo.ROTINA_USUARIO);
        contadorConsulta = new ContadorConsulta(1);
        todasParcelasEstaoSelecionadas = false;
        consultaParcela = new ConsultaParcela();
        todasParcelaSelecionadas = Maps.newHashMap();
        if (validarCamposContaCorrente()) {
            totaisSituacao = new HashMap<>();
            parcelasDoParcelamentoOriginado = Lists.newLinkedList();
            try {
                futureConsultaParcela = Lists.newArrayList();
                selecionados = Lists.newArrayList();
                adicionarParametro(consultaParcela);
                definirOrdem(consultaParcela);
                if (filtroContribuinte != null) {
                    List<BigDecimal> cadastros = consultaDebitoFacade.getCadastroFacade().buscarIdsCadastrosDaPessoa(filtroContribuinte.getId(), consultarQuadroSocietario, !consultarCadastrosInativos);

                    singletonConsultaDebitos.publicarCadastros(identificadorConsulta, cadastros);
                    contadorConsulta = new ContadorConsulta(cadastros.size() + 1);
                    AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
                    assistente.setUsuarioSistema(consultaDebitoFacade.getSistemaFacade().getUsuarioCorrente());
                    assistente.setDescricaoProcesso("Consultando débitos...");
                    assistente.setTotal(singletonConsultaDebitos.getTotalCalcular(identificadorConsulta));
                    ConsultaParcela consultaCadastros = new ConsultaParcela();
                    adicionarParametro(consultaCadastros);
                    definirOrdem(consultaCadastros);

                    futureConsultaParcela.add(consultaDebitoFacade.executarConsultaDeParcelas(assistente,
                        this, consultaCadastros, contadorConsulta));


                    ConsultaParcela consulta = new ConsultaParcela();
                    adicionarParametro(consulta);
                    definirOrdem(consulta);
                    consulta.addComplementoDoWhere(" and vw.cadastro_id is null");
                    futureConsultaParcela.add(consultaDebitoFacade.executaConsulta(consulta, contadorConsulta));
                } else {
                    futureConsultaParcela.add(consultaDebitoFacade.executaConsulta(consultaParcela, contadorConsulta));
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                FacesUtil.addError("Impossível continuar", e.getMessage());
            }
        } else {
            ajustarComponentesView();
        }

    }

    public UUID getIdentificadorConsulta() {
        return identificadorConsulta;
    }

    private void adicionarCompletentosConsulta(LinkedList<ResultadoParcela> resultados) {
        if (pesquisaExercicio || pesquisaDividaAtiva || pesquisaDividaAjuizada || pesquisaDebitoProtestado) {
            pesquisarTipoDeDebito(resultados);
        }

        if (pesquisaParcelamentoDividaAtiva) {
            pesquisarParcelamentoDividaAtiva(resultados);
            if (!parcelasDoParcelamentoOriginado.isEmpty()) {
                ordenarParcelasDoParcelamento();
                resultados.addAll((parcelasDoParcelamentoOriginado));
                parcelasDoParcelamentoOriginado = Lists.newLinkedList();
                adicionarParcelasOriginaisDosParcelamentoJaAdicionados(resultados);
                if (!parcelasDoParcelamentoOriginado.isEmpty()) {
                    ordenarParcelasDoParcelamento();
                    resultados.addAll((parcelasDoParcelamentoOriginado));
                }
            }

        }
        consultaParcela.setResultados(resultados);
        demosntrativo = resultados;
        removerSituacaoNaoSelecionadasVindasDoParcelamento();
        removerDebitosDeParcelamentoQueAsOriginaisDoParcelamentoNaoPertencemAoExercicioInformado();
    }

    private void ordenarParcelasDoParcelamento() {
        Comparator<ResultadoParcela> comparator = new Comparator<ResultadoParcela>() {
            @Override
            public int compare(ResultadoParcela um, ResultadoParcela dois) {
                int i = um.getExercicio().compareTo(dois.getExercicio());
                if (i != 0) {
                    return i;
                }
                i = um.getIdValorDivida().compareTo(dois.getIdValorDivida());
                if (i != 0) {
                    return i;
                }
                i = um.getSequenciaParcela().compareTo(dois.getSequenciaParcela());
                if (i != 0) {
                    return i;
                }
                i = um.getVencimento().compareTo(dois.getVencimento());
                if (i != 0) {
                    return i;
                }
                i = um.getSd().compareTo(dois.getSd());
                if (i != 0) {
                    return i;
                }
                i = um.getIdParcela().compareTo(dois.getIdParcela());
                if (i != 0) {
                    return i;
                }
                return 0;
            }
        };
        Collections.sort(parcelasDoParcelamentoOriginado, comparator);
    }

    private void removerDebitosDeParcelamentoQueAsOriginaisDoParcelamentoNaoPertencemAoExercicioInformado() {
        if (pesquisaParcelamentoDividaAtiva) {
            List<ResultadoParcela> aRemover = Lists.newArrayList();
            if (exercicioInicial != null) {
                if (exercicioFinal == null) {
                    exercicioFinal = getExercicioCorrente();
                }

                for (ResultadoParcela parcela : consultaParcela.getResultados()) {
                    if (Calculo.TipoCalculo.PARCELAMENTO.equals(parcela.getTipoCalculoEnumValue())) {
                        if (!consultaDebitoFacade.getProcessoParcelamentoFacade()
                            .parcelasOriginaisDoParcelamentoEstaoEntreOsExercicios(parcela.getIdParcela(), exercicioInicial.getAno(), exercicioFinal.getAno())) {
                            aRemover.add(parcela);
                        }
                    }
                }
            }
            consultaParcela.getResultados().removeAll(aRemover);
        }
    }

    private void removerSituacaoNaoSelecionadasVindasDoParcelamento() {
        List<SituacaoParcela> situacoesComparacao = Lists.newArrayList(situacoes);
        List<ResultadoParcela> aRemover = Lists.newArrayList();
        for (ResultadoParcela rp : consultaParcela.getResultados()) {
            if (!situacoesComparacao.contains(SituacaoParcela.fromDto(rp.getSituacaoEnumValue()))) {
                aRemover.add(rp);
            }
        }
        for (ResultadoParcela rp : aRemover) {
            consultaParcela.getResultados().remove(rp);
        }
    }

    private void definirOrdem(ConsultaParcela consultaParcela) {
        consultaParcela.getOrdenacao().clear();
        adicionarOrdemPrioritaria(consultaParcela);

        if (camposOrdenacao == null || camposOrdenacao.isEmpty()) {
            adicionarOrdemPadrao(consultaParcela);
        } else {
            for (ConsultaParcela.Campo campo : camposOrdenacao) {
                if (campo.equals(ConsultaParcela.Campo.PARCELA_NUMERO)) {
                    consultaParcela.addOrdem(ConsultaParcela.Campo.QUANTIDADE_PARCELA_VALOR_DIVIDA, ConsultaParcela.Ordem.TipoOrdem.ASC);
                }
                consultaParcela.addOrdem(campo, ConsultaParcela.Ordem.TipoOrdem.ASC);
            }
            if (camposOrdenacao.contains(ConsultaParcela.Campo.EXERCICIO_ANO)
                && !camposOrdenacao.contains(ConsultaParcela.Campo.PARCELA_NUMERO)) {
                consultaParcela.addOrdem(ConsultaParcela.Campo.PARCELA_NUMERO, ConsultaParcela.Ordem.TipoOrdem.ASC);
            }
        }
    }

    private void adicionarOrdemPrioritaria(ConsultaParcela consultaParcela) {
        consultaParcela
            .addOrdem(ConsultaParcela.Campo.CMC_INSCRICAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.BCI_INSCRICAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.BCR_CODIGO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.ORDEM_APRESENTACAO, ConsultaParcela.Ordem.TipoOrdem.ASC);
    }

    private void adicionarOrdemPadrao(ConsultaParcela consultaParcela) {
        consultaParcela
            .addOrdem(ConsultaParcela.Campo.DIVIDA_DESCRICAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.SD, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PROMOCIAL, ConsultaParcela.Ordem.TipoOrdem.DESC)

            .addOrdem(ConsultaParcela.Campo.VALOR_DIVIDA_ID, ConsultaParcela.Ordem.TipoOrdem.ASC)
            //.addOrdem(ConsultaParcela.Campo.VALOR_DIVIDA_ID, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Ordem.TipoOrdem.ASC)

            .addOrdem(ConsultaParcela.Campo.QUANTIDADE_PARCELA_VALOR_DIVIDA, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PARCELA_NUMERO, ConsultaParcela.Ordem.TipoOrdem.ASC)

            .addOrdem(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.REFERENCIA_PARCELA, ConsultaParcela.Ordem.TipoOrdem.ASC);
    }

    public int getPaginaAtual() {
        return paginaAtual;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public void irParaProximaPagina() {
        if (paginaAtual < totalPaginas) {
            paginaAtual++;
            montarMapaPagina();
        }
    }

    public void irParaUltimaPagina() {
        if (paginaAtual < totalPaginas) {
            paginaAtual = totalPaginas - 1;
            montarMapaPagina();
        }
    }

    public void irParaPaginaAnterior() {
        if (paginaAtual > 0) {
            paginaAtual--;
            montarMapaPagina();
        }
    }

    public void irParaPrimeiraPagina() {
        if (paginaAtual > 0) {
            paginaAtual = 0;
            montarMapaPagina();
        }
    }

    private void montarMapa() {
        mapa = Maps.newHashMap();
        mapaTotais = Maps.newHashMap();
        totaisSituacao = Maps.newHashMap();
        List<ResultadoParcela> resultados = consultaParcela.getResultados();
        montarMapaConformeParcelas(mapa, mapaTotais, resultados);
        somarTotaisDoCadatro(mapaTotais, resultados);
        somarTotaisPorSituacao(totaisSituacao, resultados);
    }

    private void montarMapaPagina() {
        mapaPorPagina = Maps.newHashMap();
        totaisSituacaoPorPagina = Maps.newHashMap();
        mapaTotaisPorPagina = Maps.newHashMap();
        List<ResultadoParcela> resultados = definePaginacao(true);
        montarMapaConformeParcelas(mapaPorPagina, mapaTotaisPorPagina, resultados);
        somarTotaisPorSituacao(totaisSituacaoPorPagina, resultados);
        somarTotaisDoCadatro(mapaTotaisPorPagina, resultados);
        ajustaOrdemPadraoParcela();
        ajustaOrdemCronologicaParcela();
    }


    private void somarTotaisDoCadatro(Map<CadastroConsulta, TotalTabelaParcelas> mapaTotais, List<ResultadoParcela> parcelas) {
        List<ResultadoParcela> parcelasSoma = agruparParcelasPorValorDivida(parcelas);
        for (ResultadoParcela resultado : parcelasSoma) {
            CadastroConsulta cadastro = new CadastroConsulta(resultado.getIdCadastro(), resultado.getCadastro(), TipoCadastroTributario.valueOf(resultado.getTipoCadastro()), null, null);
            if (resultado.isPago()) {
                mapaTotais.get(cadastro).soma(resultado);
                resultado.setSomaNoDemonstrativo(true);
            } else {
                boolean soma = false;
                if ((!resultado.getVencido() && resultado.getCotaUnica())) {
                    soma = true;
                }
                if (!resultado.getCotaUnica()) {
                    soma = true;
                }
                if ((resultado.getCotaUnica() && (resultado.isInscrito() || resultado.isCancelado()))) {
                    soma = true;
                }
                if (soma) {
                    mapaTotais.get(cadastro).soma(resultado);
                    resultado.setSomaNoDemonstrativo(true);
                }
            }
        }
    }

    private void somarTotaisPorSituacao(Map<String, BigDecimal> totaisSituacao, List<ResultadoParcela> parcelas) {
        List<ResultadoParcela> parcelasSoma = agruparParcelasPorValorDivida(parcelas);
        for (ResultadoParcela resultado : parcelasSoma) {
            String situacaoParaProcessamento = resultado.getSituacaoParaProcessamento(getDataOperacao());
            if (!totaisSituacao.containsKey(situacaoParaProcessamento)) {
                totaisSituacao.put(situacaoParaProcessamento, BigDecimal.ZERO);
            }
            if (resultado.isPago()) {
                BigDecimal total = totaisSituacao.get(situacaoParaProcessamento).add(resultado.getValorPago());
                totaisSituacao.put(situacaoParaProcessamento, total);
            } else {
                BigDecimal total = totaisSituacao.get(situacaoParaProcessamento).add(resultado.getValorTotal());
                totaisSituacao.put(situacaoParaProcessamento, total);
            }
        }
    }

    private boolean getSituacoesDoValorDividaIguais(List<ResultadoParcela> parcelas) {
        SituacaoParcela situacaoParcela = SituacaoParcela.fromDto(parcelas.get(0).getSituacaoEnumValue());
        for (ResultadoParcela resultadoParcela : parcelas) {
            if (!situacaoParcela.equals(resultadoParcela.getSituacaoEnumValue())) {
                return false;
            }
        }

        return true;
    }

    private List<ResultadoParcela> agruparParcelasPorValorDivida(List<ResultadoParcela> resultados) {
        Map<Long, List<ResultadoParcela>> parcelaPorVd = Maps.newHashMap();
        List<ResultadoParcela> parcelasSoma = Lists.newArrayList();
        for (ResultadoParcela resultado : resultados) {
            if (!parcelaPorVd.containsKey(resultado.getIdValorDivida())) {
                parcelaPorVd.put(resultado.getIdValorDivida(), new ArrayList<ResultadoParcela>());
            }
            parcelaPorVd.get(resultado.getIdValorDivida()).add(resultado);
        }

        for (Long idValorDivida : parcelaPorVd.keySet()) {
            List<ResultadoParcela> parcelasDaDivida = Lists.newArrayList();
            for (ResultadoParcela resultadoParcela : parcelaPorVd.get(idValorDivida)) {
                if (getSituacoesDoValorDividaIguais(parcelaPorVd.get(idValorDivida)) && parcelaPorVd.get(idValorDivida).size() > 1) {
                    if (resultadoParcela.getCotaUnica()
                        && !resultadoParcela.getVencido()
                        && (resultadoParcela.getSituacaoNameEnum().equals(SituacaoParcela.EM_ABERTO.name())
                        || resultadoParcela.getSituacaoNameEnum().equals(SituacaoParcela.CANCELADO_RECALCULO.name()))) {
                        parcelasDaDivida.clear();
                        parcelasDaDivida.add(resultadoParcela);
                        break;
                    } else if (!resultadoParcela.getCotaUnica()) {
                        parcelasDaDivida.add(resultadoParcela);
                    }
                } else {
                    parcelasDaDivida.add(resultadoParcela);
                }
            }
            parcelasSoma.addAll(parcelasDaDivida);
        }
        return parcelasSoma;
    }

    private List<ResultadoParcela> definePaginacao(Boolean pagina) {
        List<ResultadoParcela> resultados;
        if (consultaParcela.getResultados().size() > maxResultDebitos && pagina) {
            List<List<ResultadoParcela>> partition = Lists.partition(consultaParcela.getResultados(), maxResultDebitos);
            resultados = partition.get(paginaAtual);
            totalPaginas = partition.size();
        } else {
            resultados = consultaParcela.getResultados();
            totalPaginas = 1;
        }
        return resultados;
    }

    private void montarMapaConformeParcelas(Map<CadastroConsulta, List<ResultadoParcela>> mapa, Map<CadastroConsulta, TotalTabelaParcelas> mapaTotais, List<ResultadoParcela> resultados) {
        for (ResultadoParcela resultado : resultados) {
            CadastroConsulta cadastro = new CadastroConsulta(resultado.getIdCadastro(), resultado.getCadastro(), TipoCadastroTributario.valueOf(resultado.getTipoCadastro()), null, null);
            if (!mapa.containsKey(cadastro)) {
                mapa.put(cadastro, new ArrayList<ResultadoParcela>());
                montarDetalhesCadastro(resultado, cadastro);
            }
            if (!mapaTotais.containsKey(cadastro)) {
                mapaTotais.put(cadastro, new TotalTabelaParcelas());
            }
            mapa.get(cadastro).add(resultado);
        }
    }

    private void montarDetalhesCadastro(ResultadoParcela resultado, CadastroConsulta cadastro) {
        if (mostrarEnderecoCadastro) {
            if (TipoCadastroTributario.IMOBILIARIO.equals(cadastro.getTipo())) {
                cadastro.setEndereco(consultaDebitoFacade.getCadastroImobiliarioFacade().recuperaEnderecoCadastro(cadastro.getId()));
            } else if (TipoCadastroTributario.ECONOMICO.equals(cadastro.getTipo())) {
                cadastro.setEndereco(consultaDebitoFacade.getCadastroEconomicoFacade().recuperaEnderecoCadastro(cadastro.getId()));
            } else if (TipoCadastroTributario.RURAL.equals(cadastro.getTipo())) {
                cadastro.setEndereco(consultaDebitoFacade.getCadastroRuralFacade().recuperaEnderecoCadastro(cadastro.getId()));
            }
            resultado.setEnderecoCadastro(cadastro.getEndereco());
        }
        if (TipoCadastroTributario.IMOBILIARIO.equals(cadastro.getTipo())) {
            Boolean ativo = consultaDebitoFacade.getCadastroImobiliarioFacade().buscarSituacaoCadastroImobiliario(cadastro.getId());
            if (!ativo) {
                cadastro.setSituacao("Inativo");
            }
        } else if (TipoCadastroTributario.ECONOMICO.equals(cadastro.getTipo())) {
            SituacaoCadastralCadastroEconomico situacaoCadastral = consultaDebitoFacade.getCadastroEconomicoFacade().recuperarUltimaSituacaoDoCadastroEconomico(cadastro.getId()).getSituacaoCadastral();
            if (!SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.equals(situacaoCadastral) && !SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.equals(situacaoCadastral)) {
                cadastro.setSituacao(situacaoCadastral.getDescricao());
            }
        } else if (TipoCadastroTributario.RURAL.equals(cadastro.getTipo())) {
            Boolean ativo = consultaDebitoFacade.getCadastroRuralFacade().buscarSituacaoCadastroRural(cadastro.getId());
            if (!ativo) {
                cadastro.setSituacao("Inativo");
            }
        }
    }

    private void adicionarParametro(ConsultaParcela consultaParcela) {
        consultaParcela.getFiltros().clear();

        if (cadastro != null && cadastro.getId() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cadastro.getId());
        }

        if (filtroContribuinte != null && filtroContribuinte.getId() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, filtroContribuinte.getId());
        }

        if (exercicioInicial != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, exercicioInicial.getAno());
        }

        if (exercicioFinal != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, exercicioFinal.getAno());
        }

        if (vencimentoInicial != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, vencimentoInicial);
        }

        if (vencimentoFinal != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, vencimentoFinal);
        }

        if (pagamentoInicial != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.DATA_PAGAMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, pagamentoInicial);
        }

        if (pagamentoFinal != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.DATA_PAGAMENTO, ConsultaParcela.Operador.MENOR_IGUAL, pagamentoFinal);
        }

        if (situacoes.length > 0) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, Arrays.asList(situacoes));
        }

        if (!dividasSeleciondas.isEmpty()) {
            List<BigDecimal> idsDividas = Lists.newArrayList();
            for (Divida div : dividasSeleciondas) {
                idsDividas.add(BigDecimal.valueOf(div.getId()));
            }
            consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, idsDividas);
        } else {
            addParamDividaAtivaOuAjuizada(consultaParcela);
        }
        consultaParcela.setConsultarQuadroSocietario(consultarQuadroSocietario);
    }


    private void pesquisarParcelamentoDividaAtiva(LinkedList<ResultadoParcela> parcelas) {
        List<Long> listaIdsCalculoParcelamento = recuperarParcelamentosDasParcelasFiltradas(parcelas);
        if (!listaIdsCalculoParcelamento.isEmpty()) {
            adicionarParcelamentos(listaIdsCalculoParcelamento, parcelas);
        }
    }

    private List<Long> recuperarParcelamentosDasParcelasFiltradas(LinkedList<ResultadoParcela> parcelas) {
        List<Long> listaIdsCalculoParcelamento = Lists.newArrayList();
        for (ResultadoParcela resultado : parcelas) {
            if (SituacaoParcela.SUBSTITUIDA_POR_COMPENSACAO.equals(resultado.getSituacaoEnumValue())) {
                List<Long> compensacoes = consultaDebitoFacade.getPagamentoJudicialFacade().recuperarIDDoPagamentoJudicialDaParcelaOriginal(resultado.getIdParcela());
                for (Long id : compensacoes) {
                    if (!listaIdsCalculoParcelamento.contains(id)) {
                        listaIdsCalculoParcelamento.add(id);
                    }
                }
            } else {
                List<Long> parcelamentos = consultaDebitoFacade.getProcessoParcelamentoFacade().recuperarIDDoParcelamentoDaParcelaOriginal(resultado.getIdParcela());
                if (!parcelamentos.isEmpty()) {
                    for (Long id : parcelamentos) {
                        if (!listaIdsCalculoParcelamento.contains(id)) {
                            listaIdsCalculoParcelamento.add(id);
                        }
                    }
                }
            }
        }
        return listaIdsCalculoParcelamento;
    }

    private void adicionarParcelasOriginaisDosParcelamentoJaAdicionados(List<ResultadoParcela> parcelas) {
        List<Long> parcelamentoAdicionado = Lists.newArrayList();

        for (ResultadoParcela parcela : parcelas) {
            if (Calculo.TipoCalculo.PARCELAMENTO.equals(parcela.getTipoCalculoEnumValue())) {
                if (!parcelamentoAdicionado.contains(parcela.getIdCalculo())) {
                    parcelamentoAdicionado.add(parcela.getIdCalculo());
                    ProcessoParcelamento processo = consultaDebitoFacade.getProcessoParcelamentoFacade()
                        .recuperar(parcela.getIdCalculo());
                    List<Long> ids = Lists.newArrayList();
                    if (processo != null
                        && !SituacaoParcelamento.CANCELADO.equals(processo.getSituacaoParcelamento())
                        && !SituacaoParcelamento.ESTORNADO.equals(processo.getSituacaoParcelamento())) {
                        for (ItemProcessoParcelamento item : processo.getItensProcessoParcelamento()) {
                            Long idCalculo = consultaDebitoFacade.buscarIdCalculoDaParcela(item.getParcelaValorDivida());
                            if (idCalculo != null) {
                                ids.add(idCalculo);
                            }
                        }
                        if (!ids.isEmpty()) {
                            ConsultaParcela cp = new ConsultaParcela();
                            List<List<Long>> dividido = Lists.partition(ids, 1000);
                            for (List<Long> novosIds : dividido) {
                                cp.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, novosIds);
                            }
                            adicionarOrdemPrioritaria(cp);
                            adicionarOrdemPadrao(cp);
                            cp.executaConsulta();
                            for (ResultadoParcela resultadoParcela : cp.getResultados()) {
                                if (!parcelas.contains(resultadoParcela)
                                    && !parcelasDoParcelamentoOriginado.contains(resultadoParcela)) {
                                    parcelasDoParcelamentoOriginado.add(resultadoParcela);
                                }
                            }
                        }
                    }
                }
            } else if (SituacaoParcela.SUBSTITUIDA_POR_COMPENSACAO.equals(parcela.getSituacaoEnumValue())) {
                LinkedList<ResultadoParcela> listaRp = Lists.newLinkedList();
                listaRp.add(parcela);
                pesquisarParcelamentoDividaAtiva(listaRp);
            }
        }
    }

    private void adicionarParcelamentos(List<Long> listaIdsCalculoParcelamento, List<ResultadoParcela> parcelas) {
        if (!listaIdsCalculoParcelamento.isEmpty()) {
            ConsultaParcela cp = new ConsultaParcela();
            List<List<Long>> dividido = Lists.partition(listaIdsCalculoParcelamento, 1000);
            for (List<Long> novosIds : dividido) {
                cp.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, novosIds);
            }
            adicionarOrdemPrioritaria(cp);
            adicionarOrdemPadrao(cp);
            cp.executaConsulta();
            for (ResultadoParcela rp : cp.getResultados()) {
                if (!parcelas.contains(rp)
                    && !parcelasDoParcelamentoOriginado.contains(rp)) {
                    parcelasDoParcelamentoOriginado.add(rp);

                    LinkedList<ResultadoParcela> listaRp = Lists.newLinkedList();
                    listaRp.add(rp);
                    pesquisarParcelamentoDividaAtiva(listaRp);

                    if (SituacaoParcela.SUBSTITUIDA_POR_COMPENSACAO.equals(rp.getSituacaoEnumValue())) {
                        List<Long> compensacoes = consultaDebitoFacade.getPagamentoJudicialFacade()
                            .recuperarIDDoPagamentoJudicialDaParcelaOriginal(rp.getIdParcela());
                        adicionarParcelamentos(compensacoes, parcelas);
                    }
                }
            }
        }
    }

    private void pesquisarTipoDeDebito(LinkedList<ResultadoParcela> resultados) {
        if ((!pesquisaDividaAtiva && !pesquisaDividaAjuizada && !pesquisaDebitoProtestado) || dividasSeleciondas.isEmpty()) {
            return;
        }
        ConsultaParcela cp = new ConsultaParcela();
        if (cadastro != null && cadastro.getId() != null) {
            cp.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cadastro.getId());
        }
        if (filtroContribuinte != null && filtroContribuinte.getId() != null) {
            cp.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, filtroContribuinte.getId());
        }
        if (exercicioInicial != null) {
            cp.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, exercicioInicial.getAno());
        }
        if (exercicioFinal != null) {
            cp.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, exercicioFinal.getAno());
        }
        if (vencimentoInicial != null) {
            cp.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, vencimentoInicial);
        }
        if (vencimentoFinal != null) {
            cp.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, vencimentoFinal);
        }
        if (pagamentoInicial != null) {
            cp.addParameter(ConsultaParcela.Campo.DATA_PAGAMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, pagamentoInicial);
        }
        if (pagamentoFinal != null) {
            cp.addParameter(ConsultaParcela.Campo.DATA_PAGAMENTO, ConsultaParcela.Operador.MENOR_IGUAL, pagamentoFinal);
        }
        if (situacoes.length > 0) {
            cp.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, Arrays.asList(situacoes));
        }
        List<BigDecimal> idsDividas = Lists.newArrayList();
        for (Divida div : dividasSeleciondas) {
            if (div.getDivida() != null) {
                idsDividas.add(BigDecimal.valueOf(div.getDivida().getId()));
            }
        }
        if (!idsDividas.isEmpty()) {
            cp.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, idsDividas);
        }
        addParamDividaAtivaOuAjuizada(cp);
        adicionarOrdemPrioritaria(cp);
        adicionarOrdemPadrao(cp);
        if (!cp.getFiltros().isEmpty()) {
            cp.executaConsulta();
            for (ResultadoParcela rp : cp.getResultados()) {
                if (!resultados.contains(rp)) {
                    resultados.add(rp);
                }
            }
        } else {
            FacesUtil.addOperacaoNaoPermitida("Para executar a consulta deve ser informado ao menos um filtros");
        }

    }

    private void addParamDividaAtivaOuAjuizada(ConsultaParcela cp) {
        if (pesquisaExercicio || pesquisaDividaAtiva || pesquisaDividaAjuizada || pesquisaDebitoProtestado) {
            final StringBuilder where = new StringBuilder("and (");

            if (pesquisaExercicio && pesquisaDividaAtiva && pesquisaDividaAjuizada && pesquisaDebitoProtestado) {
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, Operador.IN, "(0, 1)", OperadorLogico.OR.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IGUAL, "1", OperadorLogico.OR.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "1"));
            } else if (pesquisaExercicio && pesquisaDividaAtiva && pesquisaDividaAjuizada) {
                where.append("(").append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, Operador.IN, "(0, 1)", OperadorLogico.OR.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IGUAL, "1)", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "0"));
            } else if (pesquisaExercicio && pesquisaDividaAtiva && pesquisaDebitoProtestado) {
                where.append("(").append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, Operador.IN, "(0, 1)", OperadorLogico.OR.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "1)", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IGUAL, "0"));
            } else if (pesquisaExercicio && pesquisaDividaAjuizada && pesquisaDebitoProtestado) {
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, Operador.IGUAL, "0", OperadorLogico.OR.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IGUAL, "1", OperadorLogico.OR.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "1"));
            } else if (pesquisaDividaAtiva && pesquisaDividaAjuizada && pesquisaDebitoProtestado) {
                where.append("(").append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IN, "(0, 1)", OperadorLogico.OR.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "1)", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, Operador.IGUAL, "1"));
            } else if (pesquisaExercicio && pesquisaDividaAtiva) {
                where.append("(").append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, Operador.IN, "(0, 1))", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IGUAL, "0", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "0"));
            } else if (pesquisaExercicio && pesquisaDividaAjuizada) {
                where.append("(").append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, Operador.IGUAL, "0", OperadorLogico.OR.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IGUAL, "1)", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "0"));
            } else if (pesquisaExercicio && pesquisaDebitoProtestado) {
                where.append("(").append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, Operador.IGUAL, "0", OperadorLogico.OR.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "1)", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IGUAL, "0"));
            } else if (pesquisaDividaAtiva && pesquisaDividaAjuizada) {
                where.append("(").append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, Operador.IGUAL, "1", OperadorLogico.OR.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IGUAL, "1)", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "0"));
            } else if (pesquisaDividaAtiva && pesquisaDebitoProtestado) {
                where.append("(").append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, Operador.IGUAL, "1", OperadorLogico.OR.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "1)", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IGUAL, "0"));
            } else if (pesquisaDividaAjuizada && pesquisaDebitoProtestado) {
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IGUAL, "1", OperadorLogico.OR.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "1"));
            } else if (pesquisaExercicio) {
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, Operador.IGUAL, "0", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IGUAL, "0", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "0"));
            } else if (pesquisaDividaAtiva) {
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, Operador.IGUAL, "1", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IGUAL, "0", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "0"));
            } else if (pesquisaDividaAjuizada) {
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, Operador.IGUAL, "1", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IGUAL, "1", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "0"));
            } else {
                where.append("(").append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, Operador.IGUAL, "1", OperadorLogico.OR.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, Operador.IGUAL, "1)", OperadorLogico.AND.getCondicional()));
                where.append(montarCondicaoTipoDeDebito(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, Operador.IGUAL, "1"));
            }
            cp.addComplementoDoWhere(where.toString().trim() + ")");
        }
    }

    public StringBuilder montarCondicaoTipoDeDebito(ConsultaParcela.Campo campo, Operador operador, String valor) {
        return montarCondicaoTipoDeDebito(campo, operador, valor, "");
    }

    public StringBuilder montarCondicaoTipoDeDebito(ConsultaParcela.Campo campo, Operador operador, String valor, String operadorLogico) {
        final String espaco = " ";
        return new StringBuilder().append(campo.getCampo()).append(espaco).append(operador.getOperador()).append(espaco).append(valor).append(espaco).append(operadorLogico).append(espaco);
    }

    public void ajustarTiposDeDividas() {
        temDividaDeDividaAtiva = false;
        temDividaDoExercicio = false;
        temDividaDeParcelamento = false;
        if (divida != null) {
            if (divida.getIsDividaAtiva()) {
                temDividaDeDividaAtiva = true;
            } else {
                temDividaDoExercicio = true;
            }
            if (divida.getIsParcelamento()) {
                temDividaDeParcelamento = true;
            }
        }
        for (Divida dv : dividasSeleciondas) {
            if (dv.getIsDividaAtiva()) {
                temDividaDeDividaAtiva = true;
            } else {
                temDividaDoExercicio = true;
            }
        }

        if (temDividaDeDividaAtiva) {
            pesquisaDividaAtiva = false;
            pesquisaDividaAjuizada = false;
        }

        if (!temDividaDeDividaAtiva && !temDividaDeParcelamento) {
            pesquisaExercicio = false;
        }

        if (temDividaDoExercicio) {
            pesquisaExercicio = false;
        }

        if ((!pesquisaDividaAtiva && !pesquisaDividaAjuizada && !temDividaDeDividaAtiva) || temDividaDeParcelamento) {
            pesquisaParcelamentoDividaAtiva = false;
        }
    }

    public void limparCampos() {
        novo();
    }

    public List<DAM> gerarDamIndividual() {
        List<DAM> dams = Lists.newArrayList();
        for (ResultadoParcela parcela : selecionados) {
            DAM dam = consultaDebitoFacade.getDamFacade().gerarDAMUnicoViaApi(usuarioLogado(), parcela);
            dams.add(dam);
        }
        return dams;
    }

    public void imprimirDamIndividual() {
        try {
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            List<DAM> dams = gerarDamIndividual();
            if (filtroContribuinte != null) {
                imprimeDAM.imprimirDamUnicoViaApi(dams, filtroContribuinte.getId());
            } else {
                imprimeDAM.imprimirDamUnicoViaApi(dams);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            log.error("Erro ao imprimir o Dam Individual: {}", e);
            FacesUtil.addError("Não foi possível imprimir o DAM", "Ocorreu um problema no servidor, tente de novo, se o problema persistir entre em contato com o suporte");
        }
    }

    public DAM gerarDamAgrupado(List<ResultadoParcela> resultadoParcelas) {
        List<ParcelaValorDivida> parcelas = Lists.newArrayList();
        try {
            boolean gerarNovoDam = true;
            BigDecimal valorTotalParcelas = BigDecimal.ZERO;
            for (ResultadoParcela parcela : resultadoParcelas) {
                valorTotalParcelas = valorTotalParcelas.add(parcela.getValorTotal());
            }
            List<DAM> damsAgrupados = consultaDebitoFacade.getDamFacade().buscarDamsAgrupadosDaParcela(resultadoParcelas.get(0).getIdParcela());
            DAM damAgrupado = null;
            for (DAM dam : damsAgrupados) {
                if (dam.getValorTotal().compareTo(valorTotalParcelas) == 0
                    && verificarParcelasDoDam(dam, resultadoParcelas)
                    && !dam.isVencido()) {
                    gerarNovoDam = false;
                    damAgrupado = dam;
                    break;
                }
            }
            if (gerarNovoDam && damAgrupado == null) {
                for (ResultadoParcela parcela : resultadoParcelas) {
                    ParcelaValorDivida p = consultaDebitoFacade.recuperaParcela(parcela.getIdParcela());
                    parcelas.add(p);
                }
                return consultaDebitoFacade.getDamFacade().geraDAM(parcelas, vencimentoDam);
            } else {
                return damAgrupado;
            }
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível gerar o DAM", "Ocorreu um problema no servidor, tente denovo, se o problema persistir entre em contato com o suporte");
            log.error(e.getMessage());
        }
        return null;
    }

    private boolean verificarParcelasDoDam(DAM dam, List<ResultadoParcela> resultadoParcelas) {
        for (ItemDAM itemDAM : dam.getItens()) {
            boolean temParcela = false;
            for (ResultadoParcela resultadoParcela : resultadoParcelas) {
                if (resultadoParcela.getIdParcela().equals(itemDAM.getParcela().getId())) {
                    temParcela = true;
                    break;
                }
            }
            if (!temParcela) {
                return false;
            }
        }
        return !dam.getItens().isEmpty();
    }

    public void imprimirDamAgrupado() throws Exception {
        try {
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            List<ResultadoParcela> parcelasDoDam = Lists.newArrayList(selecionados);
            ordenarParcelas(ResultadoParcela.getComparatorByValuePadrao(), parcelasDoDam);
            DAM dam = consultaDebitoFacade.getDamFacade().gerarDAMCompostoViaApi(usuarioLogado(),
                parcelasDoDam, vencimentoDam);
            imprimeDAM.imprimirDamCompostoViaApi(dam, getPessoaDoDam());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rae) {
            logger.error("Erro ao acessar a api do dam.", rae);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível se conectar a api para geração do DAM.");
        }
    }

    private Pessoa getPessoaDoDam() {
        Pessoa pessoaDoDam = null;
        if (tipoCadastroTributario.isPessoa()) {
            pessoaDoDam = filtroContribuinte != null ? filtroContribuinte : null;
        } else if (tipoCadastroTributario.isImobiliario()) {
            if (propriedades != null && !propriedades.isEmpty()) {
                Collections.sort(propriedades, new Comparator<Propriedade>() {
                    public int compare(final Propriedade object1, final Propriedade object2) {
                        return object1.getPessoa().getNome().compareTo(object2.getPessoa().getNome());
                    }
                });
                pessoaDoDam = propriedades.get(0).getPessoa(); // TODO ultima posição para pegar o mesmo proprietário do DAM individual (max(id))
            }
        } else if (tipoCadastroTributario.isEconomico()) {
            pessoaDoDam = cadastro != null && cadastro instanceof CadastroEconomico ? ((CadastroEconomico) cadastro).getPessoa() : null;
        }
        return pessoaDoDam;
    }

    private void validarParcelasSelecionadasParaDamAgrupado() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionados.isEmpty()) {
            List<ConfiguracaoDAM> configuracoesDAM = Lists.newArrayList();
            for (ResultadoParcela resultadoParcela : selecionados) {
                Divida dividaDam = consultaDebitoFacade.getDividaFacade().recuperar(resultadoParcela.getIdDivida());
                if (!configuracoesDAM.contains(dividaDam.getConfiguracaoDAM())) {
                    configuracoesDAM.add(dividaDam.getConfiguracaoDAM());
                }
            }
            if (configuracoesDAM.size() > 1) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("As parcelas selecionadas não podem ser agrupadas, pois os Documentos de Arrecadação Municipal – DAM, possuem configurações diferentes!");
            }
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione as parcelas desejadas para emissão do DAM.");
        }
        ve.lancarException();
    }

    public Boolean getPesquisaDividaAtiva() {
        return pesquisaDividaAtiva;
    }

    public void setPesquisaDividaAtiva(Boolean pesquisaDividaAtiva) {
        this.pesquisaDividaAtiva = pesquisaDividaAtiva;
    }

    public Boolean getPesquisaExercicio() {
        return pesquisaExercicio;
    }

    public void setPesquisaExercicio(Boolean pesquisaExercicio) {
        this.pesquisaExercicio = pesquisaExercicio;
    }

    public Boolean getPesquisaDividaAjuizada() {
        return pesquisaDividaAjuizada;
    }

    public void setPesquisaDividaAjuizada(Boolean pesquisaDividaAjuizada) {
        this.pesquisaDividaAjuizada = pesquisaDividaAjuizada;
    }

    public Boolean getPesquisaParcelamentoDividaAtiva() {
        return pesquisaParcelamentoDividaAtiva;
    }

    public void setPesquisaParcelamentoDividaAtiva(Boolean pesquisaParcelamentoDividaAtiva) {
        this.pesquisaParcelamentoDividaAtiva = pesquisaParcelamentoDividaAtiva;
    }

    public Boolean getPesquisaDebitoProtestado() {
        return pesquisaDebitoProtestado;
    }

    public void setPesquisaDebitoProtestado(Boolean pesquisaDebitoProtestado) {
        this.pesquisaDebitoProtestado = pesquisaDebitoProtestado;
    }

    public Boolean getTemDividaDeDividaAtiva() {
        return temDividaDeDividaAtiva;
    }

    public void setTemDividaDeDividaAtiva(Boolean temDividaDeDividaAtiva) {
        this.temDividaDeDividaAtiva = temDividaDeDividaAtiva;
    }

    public Boolean getTemDividaDoExercicio() {
        return temDividaDoExercicio;
    }

    public void desmarcaParcelamentoOriginados() {
        if (!pesquisaDividaAtiva && !pesquisaDividaAjuizada) {
            pesquisaParcelamentoDividaAtiva = false;
        }
    }

    public Boolean getTemDividaDeParcelamento() {
        return temDividaDeParcelamento;
    }

    public void setTemDividaDeParcelamento(Boolean temDividaDeParcelamento) {
        this.temDividaDeParcelamento = temDividaDeParcelamento;
    }

    private boolean validarCamposContaCorrente() {
        if ((filtroContribuinte == null || filtroContribuinte.getId() == null)
            && (cadastro == null)
            && (exercicioInicial == null)
            && (exercicioFinal == null)
            && (vencimentoInicial == null)
            && (vencimentoFinal == null)
            && (tipoCadastroTributario == null)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Informe ao menos um filtro"));
            return false;
        }
        if (tipoCadastroTributario != null && cadastro == null && filtroContribuinte == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Quando informado o tipo de cadastro, é necessário que informe o cadastro."));
            return false;
        }
        if (tipoCadastroTributario == null && cadastro != null && filtroContribuinte != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Quando informado o cadastro, é necessário que informe o tipo de cadastro."));
            return false;
        }
        if (divida != null && !dividasSeleciondas.contains(divida)) {
            dividasSeleciondas.add(divida);
            divida = new Divida();
        }
        if (campoOrdenacao != null && !camposOrdenacao.contains(campoOrdenacao)) {
            camposOrdenacao.add(campoOrdenacao);
            campoOrdenacao = null;
        }
        return true;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return consultaDebitoFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public void limpaCadastro() {
        cadastro = null;
        filtroContribuinte = null;
        propriedades = null;
        compromissarios = null;
    }

    public void limparCadastroSituacao() {
        limparSituacaoCadastros();
        cadastro = null;
        filtroContribuinte = null;
        propriedades = null;
        compromissarios = null;
    }

    public List<CadastroImobiliario> completarCadastroImobiliario(String parte) {
        return jdbcCadastroImobiliarioDAO.lista(parte.trim(), getSituacaoCadastroImobiliario());
    }

    public List<CadastroEconomico> completarCadastroEconomico(String parte) {
        if (getSituacaoCadastroEconomico() == null) {
            return consultaDebitoFacade.getCadastroEconomicoFacade().listaCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJPorSituacao(parte, SituacaoCadastralCadastroEconomico.values());
        }
        return consultaDebitoFacade.getCadastroEconomicoFacade().listaCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJPorSituacao(parte, getSituacaoCadastroEconomico());
    }

    public List<CadastroRural> completarCadastroRural(String parte) {
        return jdbcCadastroRuralDAO.lista(parte.trim(), getSituacaoCadastroRural());
    }

    public List<SituacaoParcela> getTodasAsSituacoes() {
        return SituacaoParcela.getValues();
    }

    public void adicionarDivida() {
        if (validarDivida()) {
            dividasSeleciondas.add(divida);
            divida = new Divida();
        }
    }

    public void adicionarOrdenacao() {
        if (validarOrdenacao()) {
            camposOrdenacao.add(campoOrdenacao);
            campoOrdenacao = null;
        }
    }

    public void removerDivida(Divida divida) {
        if (dividasSeleciondas.contains(divida)) {
            dividasSeleciondas.remove(divida);
        }
        ajustarTiposDeDividas();
    }

    public void removerOrdenacao(ConsultaParcela.Campo campo) {
        if (camposOrdenacao.contains(campo)) {
            camposOrdenacao.remove(campo);
        }
    }

    private boolean validarDivida() {
        boolean valida = true;
        if (divida == null || divida.getId() == null) {
            FacesUtil.addError("Atenção", "Selecione uma dívida para adicionar");
            valida = false;
        } else if (dividasSeleciondas.contains(divida)) {
            FacesUtil.addError("Atenção", "Essa Dívida já foi selecionada.");
            valida = false;
        }
        return valida;
    }

    private boolean validarOrdenacao() {
        boolean valida = true;
        if (campoOrdenacao == null) {
            FacesUtil.addError("Atenção", "Selecione uma um campo para adicionar");
            valida = false;
        } else if (camposOrdenacao.contains(campoOrdenacao)) {
            FacesUtil.addError("Atenção", "Esse campo já foi selecionado.");
            valida = false;
        }
        return valida;
    }

    public void prepararImpressaoOrEnvioDam(boolean enviarEmail) {
        try {
            validarParcelasSelecionadas();
            valorDam = BigDecimal.ZERO;
            for (ResultadoParcela resultado : selecionados) {
                valorDam = valorDam.add((resultado.getValorTotal()));
            }
            Calendar c = DataUtil.ultimoDiaMes(getDataOperacao());
            c = DataUtil.ultimoDiaUtil(c, feriadoFacade);
            vencimentoDam = c.getTime();
            if (enviarEmail) {
                emails = getEmailPessoaPrincipalSelecionada();
                mensagemEmail = null;
                FacesUtil.executaJavaScript("listaEmails.show()");
            } else {
                FacesUtil.executaJavaScript("confirmaDam.show()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private String getEmailPessoaPrincipalSelecionada() {
        if (tipoCadastroTributario != null) {
            if (TipoCadastroTributario.IMOBILIARIO.equals(tipoCadastroTributario)) {
                for (Propriedade propriedade : propriedades) {
                    if (propriedade.getPessoa().getEmail() != null && !propriedade.getPessoa().getEmail().isEmpty())
                        return propriedade.getPessoa().getEmail();
                }
            }
            if (TipoCadastroTributario.ECONOMICO.equals(tipoCadastroTributario)) {
                return cadastro != null ? ((CadastroEconomico) cadastro).getPessoa().getEmail() : null;
            }
            if (TipoCadastroTributario.PESSOA.equals(tipoCadastroTributario)) {
                return filtroContribuinte != null ? filtroContribuinte.getEmail() : null;
            }
        }
        return null;
    }

    private void validarParcelasSelecionadas() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (selecionados.size() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione as parcelas desejadas para emissão do DAM.");
        }
        ve.lancarException();
    }

    public boolean getMaisDeUmaParcela() {
        return selecionados != null && selecionados.size() > 1;
    }

    public void imprimirConsulta() throws JRException, IOException {
        Pessoa pessoa = getPessoaDaConsulta();
        if (pessoa != null) {
            pessoa = consultaDebitoFacade.getPessoaFacade().recarregar(pessoa);
            EnderecoCorreio enderecoCorrespondencia = pessoa.getEnderecoDomicilioFiscal();
            List<TotalSituacao> totalPorSituacao = Lists.newArrayList();

            for (String situacao : totaisSituacao.keySet()) {
                TotalSituacao total = new TotalSituacao();
                total.setSituacao(situacao);
                total.setValor(totaisSituacao.get(situacao));
                totalPorSituacao.add(total);
            }

            demosntrativo = Lists.newLinkedList();
            for (CadastroConsulta cadastroConsulta : getCadastros()) {
                for (ResultadoParcela resultadoParcela : mapa.get(cadastroConsulta)) {
                    resultadoParcela.setDebitoProtestado(consultaDebitoFacade.buscarProcessoAtivoDaParcela(
                        resultadoParcela.getIdParcela()) == null ? Boolean.FALSE : Boolean.TRUE);
                    demosntrativo.add(resultadoParcela);
                }
            }

            ImprimeDemonstrativoDebitos imprime = new ImprimeDemonstrativoDebitos("RelatorioConsultaDebitos.jasper", demosntrativo);
            imprime.adicionarParametro("BRASAO", getCaminhoImagem());
            imprime.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            imprime.adicionarParametro("PESSOA", pessoa.getNome());
            imprime.adicionarParametro("CPF_CNPJ", pessoa.getCpf_Cnpj());
            if (enderecoCorrespondencia != null) {
                imprime.adicionarParametro("ENDERECO", enderecoCorrespondencia.getEnderecoCompleto());
            }
            imprime.adicionarParametro("FILTROS", montarStringFiltros());
            imprime.adicionarParametro("TOTALPORSITUACAO", totalPorSituacao);
            imprime.adicionarParametro("SUBREPORT_DIR", getCaminho());
            imprime.imprimeRelatorio();
        }
    }

    private Pessoa getPessoaDaConsulta() {
        Pessoa pessoa;
        if (cadastro != null) {
            List<Pessoa> pessoas = consultaDebitoFacade.getPessoaFacade().recuperaPessoasDoCadastro(cadastro);
            pessoa = pessoas != null && !pessoas.isEmpty() ? pessoas.get(0) : null;
        } else {
            pessoa = filtroContribuinte;
        }
        return pessoa;
    }

    public String montarStringFiltros() {
        StringBuilder sb = new StringBuilder("");
        if (exercicioInicial != null) {
            sb.append("Exercício Inicial: " + exercicioInicial.getAno());
        } else {
            sb.append("Exercício Inicial: 0");
        }
        sb.append(", ");
        if (exercicioFinal != null) {
            sb.append("Exercício Final: " + exercicioFinal.getAno());
        } else {
            sb.append("Exercício Final: 0");
        }
        sb.append(", ");
        if (vencimentoInicial != null) {
            sb.append("Vencimento Inicial: " + Util.formatterDiaMesAno.format(vencimentoInicial));
        } else {
            sb.append("Vencimento Inicial: 0");
        }
        sb.append(", ");
        if (vencimentoFinal != null) {
            sb.append("Vencimento Final: " + Util.formatterDiaMesAno.format(vencimentoFinal));
        } else {
            sb.append("Vencimento Final: 0");
        }
        sb.append(", ");
        sb.append("Situações Selecionadas: ");
        for (SituacaoParcela s : situacoes) {
            sb.append(s.getDescricao()).append(", ");
        }
        if (situacoes.length == 0) {
            sb.append("0, ");
        }
        sb.append("Dívidas Selecionadas: ");
        for (Divida d : dividasSeleciondas) {
            sb.append(d.getDescricao()).append(", ");
        }
        if (dividasSeleciondas.isEmpty()) {
            sb.append("0, ");
        }
        return sb.toString();
    }

    public List<CadastroConsulta> getCadastros() {
        ArrayList<CadastroConsulta> cadastroConsultas = Lists.newArrayList(mapa.keySet());
        Collections.sort(cadastroConsultas);
        return cadastroConsultas;
    }

    public List<CadastroConsulta> getCadastrosPorPagina() {
        if (mapaPorPagina != null && !mapaPorPagina.isEmpty()) {
            ArrayList<CadastroConsulta> cadastroConsultas = Lists.newArrayList(mapaPorPagina.keySet());
            Collections.sort(cadastroConsultas);
            return cadastroConsultas;
        }
        return Lists.newArrayList();
    }


    public Map<CadastroConsulta, List<ResultadoParcela>> getMapa() {
        return mapa;
    }

    public Map<CadastroConsulta, TotalTabelaParcelas> getMapaTotais() {
        return mapaTotais;
    }

    public Map<CadastroConsulta, TotalTabelaParcelas> getMapaTotaisPorPagina() {
        return mapaTotaisPorPagina;
    }

    public boolean isTodasParcelasEstaoSelecionadas(CadastroConsulta cadastro) {
        if (!todasParcelaSelecionadas.containsKey(cadastro)) {
            todasParcelaSelecionadas.put(cadastro, false);
        }
        return todasParcelaSelecionadas.get(cadastro);
    }

    public void selecionarParcela(ResultadoParcela parcela) {
        if (selecionados.contains(parcela)) {
            selecionados.remove(parcela);
        } else {
            selecionados.add(parcela);
        }
    }

    public void selecionarTodasParcelas() {
        todasParcelasEstaoSelecionadas = !todasParcelasEstaoSelecionadas;
        for (CadastroConsulta cadastroConsulta : mapa.keySet()) {
            for (ResultadoParcela parcela : mapa.get(cadastroConsulta)) {
                if (!naoPodeSelecionarParcela(parcela)) {
                    if (!selecionados.contains(parcela) && todasParcelasEstaoSelecionadas) {
                        selecionados.add(parcela);
                    } else if (!todasParcelasEstaoSelecionadas) {
                        selecionados.remove(parcela);
                    }
                }
            }
            todasParcelaSelecionadas.put(cadastroConsulta, todasParcelasEstaoSelecionadas);
        }

    }

    public void selecionarParcelasDoCadastro(CadastroConsulta cadastro) {
        for (ResultadoParcela parcela : mapa.get(cadastro)) {
            if (!selecionados.contains(parcela) && !naoPodeSelecionarParcela(parcela)) {
                selecionados.add(parcela);
            }
        }
        todasParcelaSelecionadas.put(cadastro, true);
    }

    private boolean naoPodeSelecionarParcela(ResultadoParcela parcela) {
        return (!SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue())
            && !SituacaoParcela.AGUARDANDO.equals(parcela.getSituacaoEnumValue())
            && !SituacaoParcela.AGUARDANDO_PAGAMENTO_BLOQUEIO_JUDICIAL.equals(parcela.getSituacaoEnumValue()))
            || (parcela.getCotaUnica() && parcela.getVencido())
            || (parcela.getBloqueiaImpressao());
    }

    public void removerParcelasDoCadastro(CadastroConsulta cadastro) {
        for (ResultadoParcela parcela : mapa.get(cadastro)) {
            if (selecionados.contains(parcela)) {
                selecionados.remove(parcela);
            }
        }
        todasParcelaSelecionadas.put(cadastro, false);
    }

    public Boolean getTodasParcelasEstaoSelecionadas() {
        return todasParcelasEstaoSelecionadas;
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        if (obj instanceof Cadastro) {
            cadastro = (Cadastro) obj;
            if (obj instanceof CadastroImobiliario) {
                cadastro = consultaDebitoFacade.getCadastroImobiliarioFacade().recuperarSemCalcular(cadastro.getId());
                selecionarPropriedades();
                selecionarCompromissarios();
            }
        } else if (obj instanceof Pessoa) {
            filtroContribuinte = (Pessoa) obj;
        }
    }

    public ComponentePesquisaGenerico getComponentePesquisa() {
        if (tipoCadastroTributario != null) {
            switch (tipoCadastroTributario) {
                case IMOBILIARIO:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroImobiliarioControlador");
                case ECONOMICO:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
                case PESSOA:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pessoaPesquisaGenerico");
            }
        }
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("componentePesquisaGenerico");
    }

    public String getNomeClasse() {
        if (tipoCadastroTributario != null) {
            switch (tipoCadastroTributario) {
                case IMOBILIARIO:
                    return CadastroImobiliario.class.getSimpleName();
                case ECONOMICO:
                    return CadastroEconomico.class.getSimpleName();
                case PESSOA:
                    return Pessoa.class.getSimpleName();
            }
        }
        return CadastroRural.class.getSimpleName();

    }

    public List<Propriedade> getPropriedades() {
        return propriedades;
    }

    public List<Compromissario> getCompromissarios() {
        return compromissarios;
    }

    public void selecionarCadastroImobiliario(SelectEvent e) {
        cadastro = (Cadastro) e.getObject();
        cadastro = consultaDebitoFacade.getCadastroImobiliarioFacade().recuperarSemCalcular(cadastro.getId());
        selecionarPropriedades();
        selecionarCompromissarios();
    }

    private void selecionarPropriedades() {
        propriedades = ((CadastroImobiliario) cadastro).getPropriedadeVigente();
    }

    private void selecionarCompromissarios() {
        compromissarios = ((CadastroImobiliario) cadastro).getCompromissarioVigente();
    }

    public List<SituacaoCadastralPessoa> getSituacoesDisponiveis() {
        if (getSituacaoCadastroPessoa() == null) {
            return Lists.newArrayList(SituacaoCadastralPessoa.values());
        }
        return Lists.newArrayList(getSituacaoCadastroPessoa());
    }

    public void ajustarValorDividaAtiva() {
        try {
            consultaDebitoFacade.ajustaValorDividaAtiva(cadastro);
        } catch (Exception e) {
            System.out.println("Deu Exception.");
        }
    }

    public void atribuirResultadoDetalhamento(ResultadoParcela resultadoParcela) {
        informacoesAdicionais = consultaDebitoFacade.carregarListaObjetoCampoValorParcelaSelecionada(resultadoParcela);
    }

    public String mostrarNumeroDAM(Long idParcela, Boolean emitido) {
        if (numeroDamParcela == null) {
            numeroDamParcela = Maps.newHashMap();
        }
        if (emitido) {
            if (!numeroDamParcela.containsKey(idParcela)) {
                List<String> numerosDams = consultaDebitoFacade.getDamFacade().recuperaNumeroDamParcela(idParcela);
                if (numerosDams != null && !numerosDams.isEmpty()) {
                    numeroDamParcela.put(idParcela, numerosDams.get(0));
                }
            }
            return numeroDamParcela.get(idParcela);
        }
        return "";
    }

    private void ajustaOrdemCronologicaParcela() {
        if (camposOrdenacao.contains(ConsultaParcela.Campo.PARCELA_SALDO_TOTAL_ATUALIZADO)) {
            ordenarParcelasEMapa(getComparatorByValue(), consultaParcela.getResultados());
        }
    }

    private void ajustaOrdemPadraoParcela() {
        ordenarParcelasEMapa(ResultadoParcela.getComparatorByValuePadrao(), consultaParcela.getResultados());
    }

    private void ordenarParcelasEMapa(Comparator<ResultadoParcela> comparator, List<ResultadoParcela> parcelasParaOrdenar) {
        Collections.sort(parcelasParaOrdenar, comparator);
        ordenarMapa(comparator);
    }

    private void ordenarParcelas(Comparator<ResultadoParcela> comparator, List<ResultadoParcela> parcelasParaOrdenar) {
        Collections.sort(parcelasParaOrdenar, comparator);
    }

    private void ordenarMapa(Comparator<ResultadoParcela> comparator) {
        for (CadastroConsulta cadastroConsulta : mapa.keySet()) {
            Collections.sort(mapa.get(cadastroConsulta), comparator);
            if (mapaPorPagina != null && mapaPorPagina.containsKey(cadastroConsulta)) {
                Collections.sort(mapaPorPagina.get(cadastroConsulta), comparator);
            }
        }
    }

    private Comparator<ResultadoParcela> getComparatorByValue() {
        return new Comparator<ResultadoParcela>() {
            @Override
            public int compare(ResultadoParcela um, ResultadoParcela dois) {
                return um.getValorTotal().compareTo(dois.getValorTotal());
            }
        };
    }

    public void atualizarTabela() {
        FacesUtil.atualizarComponente("tabelaConsultas");
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getMensagemEmail() {
        return mensagemEmail;
    }

    public void setMensagemEmail(String mensagemEmail) {
        this.mensagemEmail = mensagemEmail;
    }

    public String[] validarAndSepararEmails() throws AddressException {
        ValidacaoException ve = new ValidacaoException();
        String emailsQuebrados[] = null;
        if (emails == null || emails.trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo E-mail é obrigatório.");
        } else {
            emailsQuebrados = emails.split(Pattern.quote(","));
            for (String emailsQuebrado : emailsQuebrados) {
                InternetAddress emailAddr = new InternetAddress(emailsQuebrado);
                emailAddr.validate();
            }
        }
        if (mensagemEmail == null || mensagemEmail.trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mensagem é obrigatório.");
        }
        ve.lancarException();
        return emailsQuebrados;
    }

    public void enviarDAMsPorEmailIndividual() {
        try {
            String[] EmailsSeparados = validarAndSepararEmails();
            List<DAM> dams = gerarDamIndividual();
            enviarEmailsDAMs(dams, EmailsSeparados, Boolean.FALSE);
        } catch (AddressException e) {
            FacesUtil.addOperacaoNaoRealizada("O e-mail informado é invalido!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void enviarDAMsPorEmailAgrupado() {
        try {
            validarParcelasSelecionadasParaDamAgrupado();
            String[] emailsSeparados = validarAndSepararEmails();

            List<ResultadoParcela> parcelasDoDam = Lists.newArrayList(selecionados);
            ordenarParcelas(ResultadoParcela.getComparatorByValuePadrao(), parcelasDoDam);
            DAM dam = gerarDamAgrupado(parcelasDoDam);

            List<DAM> dams = new ArrayList<>();
            dams.add(dam);
            enviarEmailsDAMs(dams, emailsSeparados, Boolean.TRUE);
        } catch (AddressException e) {
            FacesUtil.addOperacaoNaoRealizada("O e-mail informado é invalido!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void enviarEmailsDAMs(List<DAM> dams, String[] emailsSeparados, Boolean agrupado) {
        try {
            consultaDebitoFacade.enviarEmailsDAMs(dams, emailsSeparados, agrupado, mensagemEmail, selecionados);
            FacesUtil.addOperacaoRealizada("E-mail enviado com sucesso!");
            FacesUtil.executaJavaScript("listaEmails.hide()");
        } catch (Exception e) {
            log.error("Ocorreu um erro ao enviar o e-mail: {}", e);
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao enviar o e-mail com o DAM: " + e.getMessage());
        }
    }

    public static class CadastroConsulta implements Serializable, Comparable<CadastroConsulta> {
        private Long id;
        private String cadastro;
        private TipoCadastroTributario tipo;
        private String endereco;
        private String situacao;

        public CadastroConsulta(Long id, String cadastro, TipoCadastroTributario tipo, String endereco, String situacao) {
            this.id = id;
            this.cadastro = cadastro;
            this.tipo = tipo;
            this.endereco = endereco;
            this.situacao = situacao;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCadastro() {
            return cadastro;
        }

        public void setCadastro(String cadastro) {
            this.cadastro = cadastro;
        }

        public TipoCadastroTributario getTipo() {
            return tipo;
        }

        public void setTipo(TipoCadastroTributario tipo) {
            this.tipo = tipo;
        }

        public String getEndereco() {
            return endereco;
        }

        public void setEndereco(String endereco) {
            this.endereco = endereco;
        }

        public String getSituacao() {
            return situacao != null ? " (" + situacao + ")" : null;
        }

        public void setSituacao(String situacao) {
            this.situacao = situacao;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CadastroConsulta)) return false;

            CadastroConsulta that = (CadastroConsulta) o;

            if (cadastro != null ? !cadastro.equals(that.cadastro) : that.cadastro != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return cadastro != null ? cadastro.hashCode() : 0;
        }

        @Override
        public int compareTo(CadastroConsulta o) {
            return this.tipo.getOrdem().compareTo(o.tipo.getOrdem());  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class TotalTabelaParcelas implements Serializable {
        private BigDecimal totalValor, totalImposto, totalTaxa, totalDesconto, totalJuros, totalMulta, totalCorrecao, totalGeral, totalHonorarios, totalPago;

        public TotalTabelaParcelas() {
            totalValor = BigDecimal.ZERO;
            totalImposto = BigDecimal.ZERO;
            totalTaxa = BigDecimal.ZERO;
            totalDesconto = BigDecimal.ZERO;
            totalJuros = BigDecimal.ZERO;
            totalCorrecao = BigDecimal.ZERO;
            totalMulta = BigDecimal.ZERO;
            totalHonorarios = BigDecimal.ZERO;
            totalGeral = BigDecimal.ZERO;
            totalPago = BigDecimal.ZERO;
        }

        public void soma(ResultadoParcela resultado) {
            try {
                if (resultado.podeSomar()) {
                    totalValor = totalValor.add(resultado.getValorOriginal());
                    totalImposto = totalImposto.add(resultado.getValorImposto());
                    totalTaxa = totalTaxa.add(resultado.getValorTaxa());
                    totalCorrecao = totalCorrecao.add(resultado.getValorCorrecao());
                    totalDesconto = totalDesconto.add(resultado.getValorDesconto());
                    totalJuros = totalJuros.add(resultado.getValorJuros());
                    totalMulta = totalMulta.add(resultado.getValorMulta());
                    totalHonorarios = totalHonorarios.add(resultado.getValorHonorarios());
                    totalGeral = totalGeral.add(resultado.getValorTotal());
                    totalPago = totalPago.add(resultado.getValorPago());
                }
            } catch (Exception e) {

            }
        }

        public BigDecimal getTotalValor() {
            return totalValor;
        }

        public BigDecimal getTotalImposto() {
            return totalImposto;
        }

        public BigDecimal getTotalTaxa() {
            return totalTaxa;
        }

        public BigDecimal getTotalDesconto() {
            return totalDesconto;
        }

        public BigDecimal getTotalJuros() {
            return totalJuros;
        }

        public BigDecimal getTotalHonorarios() {
            return totalHonorarios;
        }

        public BigDecimal getTotalMulta() {
            return totalMulta;
        }

        public BigDecimal getTotalGeral() {
            return totalGeral;
        }

        public BigDecimal getTotalCorrecao() {
            return totalCorrecao;
        }

        public BigDecimal getTotalPago() {
            return totalPago;
        }

    }

    public static class ContadorConsulta {
        private Integer total;
        private Integer calculados;

        public ContadorConsulta(int total) {
            this.total = total;
            calculados = 0;
        }

        public synchronized void contar() {
            calculados = calculados + 1;
        }

        public synchronized void contar(Integer ids) {
            calculados = calculados + ids;
        }

        public int getCalculados() {
            return calculados;
        }

        public Double getPorcentagemDoCalculo() {
            if (calculados == null || total == null) {
                return 0d;
            }
            return (calculados.doubleValue() / total.doubleValue()) * 100;
        }

        public int getTotal() {
            return total;
        }
    }
}
