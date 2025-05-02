package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ResultadoValorDivida;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConsultaDebitoFacade;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.LoteBaixaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.SingletonConcorrenciaParcela;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroImobiliarioDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroRuralDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcConsultaDebitoIntegralDAO;
import br.com.webpublico.singletons.CacheTributario;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@ManagedBean(name = "consultaDebitoIntegralControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConsultaDebitoIntegral", pattern = "/consulta-debito-integral/listar/", viewId = "/faces/tributario/contacorrente/consultaintegrallancamentodebitos/consulta.xhtml"),
    @URLMapping(id = "verDebitos", pattern = "/consulta-debito-integral/ver/#{consultaDebitoIntegralControlador.id}/", viewId = "/faces/tributario/contacorrente/consultaintegrallancamentodebitos/visualizar.xhtml")
})
public class ConsultaDebitoIntegralControlador {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaDebitoIntegralControlador.class);
    private final String CONSULTADEBITODAO = "consultaDebitoIntegralDAO";
    private transient final ApplicationContext ap;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private LoteBaixaFacade loteBaixaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private SingletonConcorrenciaParcela concorrenciaParcela;
    @EJB
    private CacheTributario cacheTributario;
    private List<ResultadoParcela> resultadosParcela;
    private ResultadoValorDivida selecionado;
    private JdbcConsultaDebitoIntegralDAO consultaDebitoIntegralDAO;
    private JdbcCadastroImobiliarioDAO jdbcCadastroImobiliarioDAO;
    private JdbcCadastroRuralDAO jdbcCadastroRuralDAO;
    private Map<String, BigDecimal> totalizadorOpcaoPagamento;
    private Map<Long, BigDecimal> totalizadorItemParcelaValorDivida;
    private BigDecimal valorTotalItemValoDivida;
    private List<ParcelaValorDivida> listaParcelasValorDivida;
    private RelatorioConsultaIntegralLancamentoDebitos relatorio;
    private ConverterGenerico converterDivida;
    private Date vencimentoDam;
    private CalculoIPTU calculoIPTU;
    private ConverterAutoComplete converterCadastroImobiliario, converterCadastroEconomico, converterCadastroRural;
    private boolean mostarDetalhesCalculo;
    private Divida divida;
    private List<Divida> dividas;
    private Long id;
    private String listaDeIdsDAMs;
    private List<ResultadoParcela> parcelasBloqueadas;
    private Filtro filtrosConsulta;

    public Filtro getFiltrosConsulta() {
        return filtrosConsulta;
    }

    public void setFiltrosConsulta(Filtro filtrosConsulta) {
        this.filtrosConsulta = filtrosConsulta;
    }

    private void selecionarPropriedades() {
        filtrosConsulta.setPropriedades(((CadastroImobiliario) filtrosConsulta.getCadastro()).getPropriedadeVigente());
    }

    private void selecionarCompromissarios() {
        filtrosConsulta.setCompromissarios(((CadastroImobiliario) filtrosConsulta.getCadastro()).getCompromissarioVigente());
    }

    public void filtrarParcelas() {
        try {
            validarFiltros();
            resultadosParcela = Lists.newArrayList();
            ConsultaParcela consulta = new ConsultaParcela();
            adicionarParametro(consulta);
            resultadosParcela.addAll(consulta.executaConsulta().getResultados());
            if (!resultadosParcela.isEmpty()) {
                resultadosParcela.sort(getComparatorByValuePadrao());
            } else {
                FacesUtil.addOperacaoRealizada("Nenhum Débito foi encontrado para os filtros informados.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    private Comparator<ResultadoParcela> getComparatorByValuePadrao() {
        return new Comparator<ResultadoParcela>() {
            @Override
            public int compare(ResultadoParcela um, ResultadoParcela dois) {
                int retorno;

                retorno = um.getCadastro().compareTo(dois.getCadastro());
                if (retorno == 0) {
                    retorno = um.getOrdemApresentacao().compareTo(dois.getOrdemApresentacao());
                }
                if (retorno == 0) {
                    retorno = um.getDivida().compareTo(dois.getDivida());
                }
                if (retorno == 0) {
                    retorno = um.getExercicio().compareTo(dois.getExercicio());
                }
                if (retorno == 0) {
                    retorno = um.getReferencia().compareTo(dois.getReferencia());
                }
                if (retorno == 0) {
                    retorno = um.getSd().compareTo(dois.getSd());
                }
                if (retorno == 0) {
                    retorno = dois.getCotaUnica().compareTo(um.getCotaUnica());
                }
                if (retorno == 0) {
                    retorno = um.getVencimento().compareTo(dois.getVencimento());
                }
                if (retorno == 0) {
                    retorno = um.getParcela().compareTo(dois.getParcela());
                }
                if (retorno == 0) {
                    retorno = um.getSituacao().compareTo(dois.getSituacao());
                }
                return retorno;
            }
        };
    }

    private void adicionarParametro(ConsultaParcela consultaParcela) {
        consultaParcela.getFiltros().clear();

        if (filtrosConsulta.getCadastro() != null && filtrosConsulta.getCadastro().getId() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, filtrosConsulta.getCadastro().getId());
        }

        if (filtrosConsulta.getContribuinte() != null && filtrosConsulta.getContribuinte().getId() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, filtrosConsulta.getContribuinte().getId());
        }

        if (filtrosConsulta.getExercicioInicial() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, filtrosConsulta.getExercicioInicial().getAno());
        }

        if (filtrosConsulta.getExercicioFinal() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, filtrosConsulta.getExercicioFinal().getAno());
        }

        if (filtrosConsulta.getVencimentoInicial() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, filtrosConsulta.getVencimentoInicial());
        }

        if (filtrosConsulta.getVencimentoFinal() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, filtrosConsulta.getVencimentoFinal());
        }

        if (filtrosConsulta.getSituacoes().length > 0) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, Arrays.asList(filtrosConsulta.getSituacoes()));
        }

        if (!filtrosConsulta.getDividasSelecionadas().isEmpty()) {
            List<BigDecimal> idsDividas = Lists.newArrayList();
            for (Divida div : filtrosConsulta.getDividasSelecionadas()) {
                idsDividas.add(BigDecimal.valueOf(div.getId()));
            }
            consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, idsDividas);
        } else {
            if ((filtrosConsulta.getPesquisaDividaAtiva() || filtrosConsulta.getPesquisaDividaAjuizada())) {
                if (filtrosConsulta.getPesquisaExercicio()) {
                    consultaParcela.addComplementoDoWhere(" and (" + ConsultaParcela.Campo.TIPO_CALCULO.getCampo() + " <> '" + Calculo.TipoCalculo.PARCELAMENTO.name() + "' or " +
                        ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA.getCampo() + " = 0 ) ");
                } else {
                    consultaParcela.addComplementoDoWhere(" and (" + ConsultaParcela.Campo.TIPO_CALCULO.getCampo() + " = '" + Calculo.TipoCalculo.INSCRICAO_DA.name() + "' or " +
                        ConsultaParcela.Campo.TIPO_CALCULO.getCampo() + " = '" + Calculo.TipoCalculo.CANCELAMENTO_PARCELAMENTO.name() + "' ) ");
                }
            }
            if (filtrosConsulta.getPesquisaExercicio() && !filtrosConsulta.getPesquisaDividaAtiva() && !filtrosConsulta.getPesquisaDividaAjuizada()) {
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
            } else if (filtrosConsulta.getPesquisaExercicio() && filtrosConsulta.getPesquisaDividaAtiva() && !filtrosConsulta.getPesquisaDividaAjuizada()) {
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
            } else if (filtrosConsulta.getPesquisaExercicio() && !filtrosConsulta.getPesquisaDividaAtiva() && filtrosConsulta.getPesquisaDividaAjuizada()) {
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
            } else if (!filtrosConsulta.getPesquisaExercicio() && filtrosConsulta.getPesquisaDividaAtiva() && filtrosConsulta.getPesquisaDividaAjuizada()) {
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IN, Lists.newArrayList(0, 1));
            } else if (!filtrosConsulta.getPesquisaExercicio() && filtrosConsulta.getPesquisaDividaAtiva() && !filtrosConsulta.getPesquisaDividaAjuizada()) {
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
            } else if (!filtrosConsulta.getPesquisaExercicio() && !filtrosConsulta.getPesquisaDividaAtiva() && filtrosConsulta.getPesquisaDividaAjuizada()) {
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);

            }
        }
    }

    private void definirOrdem(ConsultaParcela consultaParcela) {
        consultaParcela.getOrdenacao().clear();
        adicionarOrdemPadrao(consultaParcela);
    }

    private void adicionarOrdemPadrao(ConsultaParcela consultaParcela) {
        consultaParcela
            .addOrdem(ConsultaParcela.Campo.CMC_INSCRICAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.BCI_INSCRICAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.BCR_CODIGO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.ORDEM_APRESENTACAO, ConsultaParcela.Ordem.TipoOrdem.ASC)

            .addOrdem(ConsultaParcela.Campo.DIVIDA_DESCRICAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.SD, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PROMOCIAL, ConsultaParcela.Ordem.TipoOrdem.DESC)

            .addOrdem(ConsultaParcela.Campo.VALOR_DIVIDA_ID, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Ordem.TipoOrdem.ASC)

            .addOrdem(ConsultaParcela.Campo.QUANTIDADE_PARCELA_VALOR_DIVIDA, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PARCELA_NUMERO, ConsultaParcela.Ordem.TipoOrdem.ASC)

            .addOrdem(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.REFERENCIA_PARCELA, ConsultaParcela.Ordem.TipoOrdem.ASC);
    }

    public void selecionar(ResultadoValorDivida resultadoValorDivida) {
        selecionado = resultadoValorDivida;
        Web.navegacao("/consulta-debito-integral/listar/", "/consulta-debito-integral/ver/" + selecionado.getId(), selecionado);
    }

    @URLAction(mappingId = "verDebitos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void visualizarDebito() {
        inicializaVisualizaDebito();
        this.selecionado = consultaDebitoIntegralDAO.recuperarResultadoValorDivida(this.getId());
    }

    @URLAction(mappingId = "novaConsultaDebitoIntegral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaConsultaDebito() {
        filtrosConsulta = (Filtro) Web.pegaDaSessao(Filtro.class);
        if (filtrosConsulta == null) {
            inicializaConsulta();
        } else {
            FacesUtil.executaJavaScript("aguarde.show()");
            filtrarParcelas();
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
        relatorio = new RelatorioConsultaIntegralLancamentoDebitos();
        listaDeIdsDAMs = "";
        consultarParcelasBloqueadas();
    }

    public void consultarParcelasBloqueadas() {
        List<Long> ids = concorrenciaParcela.getParcelas();
        if (!ids.isEmpty()) {
            parcelasBloqueadas = new ConsultaParcela()
                .addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, ids)
                .executaConsulta()
                .getResultados();
        }
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (filtrosConsulta.getTipoCadastroTributario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Tipo de Cadastro deve ser informado.");
        }
        if (!filtrosConsulta.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA) && filtrosConsulta.getCadastro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O cadastro deve ser informado.");
        }
        if (filtrosConsulta.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA) && filtrosConsulta.getContribuinte() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O contribuinte deve ser informado.");
        }
        ve.lancarException();
    }

    public ConsultaDebitoIntegralControlador() {
        this.ap = ContextLoader.getCurrentWebApplicationContext();
        this.consultaDebitoIntegralDAO = (JdbcConsultaDebitoIntegralDAO) this.ap.getBean(CONSULTADEBITODAO);
        jdbcCadastroImobiliarioDAO = (JdbcCadastroImobiliarioDAO) ap.getBean("cadastroImobiliarioJDBCDAO");
        jdbcCadastroRuralDAO = (JdbcCadastroRuralDAO) ap.getBean("cadastroRuralDAO");
        mostarDetalhesCalculo = false;
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        filtrosConsulta.setCadastro((CadastroEconomico) obj);
    }

    public void inicializaConsulta() {
        filtrosConsulta = new Filtro();
        filtrosConsulta.setSituacoes(new SituacaoParcela[]{SituacaoParcela.EM_ABERTO});
        filtrosConsulta.setTipoCadastroTributario(TipoCadastroTributario.PESSOA);
        filtrosConsulta.setExercicioInicial(null);
        filtrosConsulta.setExercicioFinal(null);
        filtrosConsulta.setDividasSelecionadas(new ArrayList<Divida>());
        resultadosParcela = new ArrayList<>();
        relatorio = new RelatorioConsultaIntegralLancamentoDebitos();
        dividas = Lists.newArrayList();
        limpaFiltrosCadastro();
    }

    public void inicializaVisualizaDebito() {
        selecionado = new ResultadoValorDivida();
        relatorio = new RelatorioConsultaIntegralLancamentoDebitos();
        listaParcelasValorDivida = new ArrayList<>();
        totalizadorItemParcelaValorDivida = new HashMap<>();
        totalizadorOpcaoPagamento = new HashMap<>();
        valorTotalItemValoDivida = new BigDecimal(BigInteger.ZERO);
    }

    public List<ResultadoParcela> getResultadosParcela() {
        return resultadosParcela;
    }

    public void setResultadosParcela(List<ResultadoParcela> resultadosParcela) {
        this.resultadosParcela = resultadosParcela;
    }

    public ResultadoValorDivida getSelecionado() {
        return selecionado;
    }

    public BigDecimal valorTotalItemParcelaVD(Long idParcela) {
        return totalizadorItemParcelaValorDivida.get(idParcela);
    }

    public RelatorioConsultaIntegralLancamentoDebitos getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(RelatorioConsultaIntegralLancamentoDebitos relatorio) {
        this.relatorio = relatorio;
    }

    public List<SelectItem> getTiposDeCadastroTributario() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricaoLonga()));
        }
        return lista;
    }

    public ConverterGenerico getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterGenerico(Divida.class, dividaFacade);
        }
        return converterDivida;
    }

    public void limpaFiltrosCadastro() {
        filtrosConsulta.setCadastro(null);
        filtrosConsulta.setContribuinte(null);
        dividas = dividaFacade.listaDividasPorTipoCadastro(filtrosConsulta.getTipoCadastroTributario());
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

    public List<SituacaoCadastralPessoa> getSituacoesDisponiveis() {
        return Lists.newArrayList(filtrosConsulta.getSituacaoCadastroPessoa());
    }

    public void limparCadastro() {
        filtrosConsulta.setCadastro(null);
        filtrosConsulta.setContribuinte(null);
        filtrosConsulta.setPropriedades(null);
        filtrosConsulta.setCompromissarios(null);
    }

    public void limparCadastroSituacao() {
        limparSituacaoCadastros();
        filtrosConsulta.setCadastro(null);
        filtrosConsulta.setContribuinte(null);
        filtrosConsulta.setPropriedades(null);
        filtrosConsulta.setCompromissarios(null);
    }

    private void limparSituacaoCadastros() {
        filtrosConsulta.setSituacaoCadastroImobiliario(true);
        filtrosConsulta.setSituacaoCadastroEconomico(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        filtrosConsulta.setSituacaoCadastroRural(true);
        filtrosConsulta.setSituacaoCadastroPessoa(SituacaoCadastralPessoa.ATIVO);
    }

    public List<CadastroImobiliario> completarCadastroImobiliario(String parte) {
        return jdbcCadastroImobiliarioDAO.lista(parte.trim(), filtrosConsulta.getSituacaoCadastroImobiliario());
    }

    public List<CadastroEconomico> completarCadastroEconomico(String parte) {
        return consultaDebitoFacade.getCadastroEconomicoFacade().listaCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJPorSituacao(parte, filtrosConsulta.getSituacaoCadastroEconomico());
    }

    public List<CadastroRural> completarCadastroRural(String parte) {
        return jdbcCadastroRuralDAO.lista(parte.trim(), filtrosConsulta.getSituacaoCadastroRural());
    }

    public void selecionarCadastroImobiliario(SelectEvent e) {
        filtrosConsulta.setCadastro((Cadastro) e.getObject());
        filtrosConsulta.setCadastro(consultaDebitoFacade.getCadastroImobiliarioFacade().recuperarSemCalcular(filtrosConsulta.getCadastro().getId()));
        selecionarPropriedades();
        selecionarCompromissarios();
    }

    public String pessoasDoCadastroDoValorDivida(Long id) {
        String resultado = "";
        List<Pessoa> pessoas = consultaDebitoIntegralDAO.listarPessoasDoCadastroDoValorDivida(id);
        for (Pessoa p : pessoas) {
            resultado += p.getNome() + " - " + (p.getCpf_Cnpj() == null ? "" : p.getCpf_Cnpj()) + "<br/>";
        }
        return resultado;
    }

    public String pessoasDoCalculoDoValorDivida(Long id) {
        return consultaDebitoIntegralDAO.pessoasDoCalculoDoValorDivida(id);
    }

    public List<ParcelaValorDivida> listaParcelaValorDivida() {
        List<ParcelaValorDivida> lista = consultaDebitoIntegralDAO.listaParcelaValorDivida(getId());
        totalizadorOpcaoPagamento = new HashMap();
        for (ParcelaValorDivida parcelaValorDivida : lista) {
            BigDecimal total = parcelaValorDivida.getValor();
            if (totalizadorOpcaoPagamento.containsKey(parcelaValorDivida.getOpcaoPagamento().getDescricao())) {
                total = total.add(totalizadorOpcaoPagamento.get(parcelaValorDivida.getOpcaoPagamento().getDescricao()));
            }
            totalizadorOpcaoPagamento.put(parcelaValorDivida.getOpcaoPagamento().getDescricao(), total);
        }
        listaParcelasValorDivida = lista;
        return lista;
    }

    public List<String> getOpcoesPagamentoTotalizador() {
        Set<String> strings = totalizadorOpcaoPagamento.keySet();
        List<String> retorno = new ArrayList<String>();
        retorno.addAll(strings);
        return retorno;
    }

    public Map<String, BigDecimal> getTotalizadorOpcaoPagamento() {
        return totalizadorOpcaoPagamento;
    }

    public void setTotalizadorOpcaoPagamento(Map<String, BigDecimal> totalizadorOpcaoPagamento) {
        this.totalizadorOpcaoPagamento = totalizadorOpcaoPagamento;
    }

    public List<ItemParcelaValorDivida> listaItensParcelaVD(Long idParcela) {
        List<ItemParcelaValorDivida> lista = consultaDebitoIntegralDAO.listaItemParcelaValorDividas(idParcela);
        BigDecimal total = BigDecimal.ZERO;
        for (ItemParcelaValorDivida item : lista) {
            total = total.add(item.getValor());
        }
        totalizadorItemParcelaValorDivida.put(idParcela, total);
        return lista;
    }

    public List<SituacaoParcelaValorDivida> listaSituacaoParcelaVD(Long idParcela) {
        return consultaDebitoIntegralDAO.listaSituacaoParcela(idParcela);
    }

    public List<DescontoItemParcela> listaDescontosParcelaVD(Long idParcela) {
        return consultaDebitoIntegralDAO.listaDescontosParcelaVD(idParcela);
    }

    public List<ItemValorDivida> listaItemValorDivida() {
        List<ItemValorDivida> lista = consultaDebitoIntegralDAO.listaItemValorDivida(getId());
        valorTotalItemValoDivida = BigDecimal.ZERO;
        for (ItemValorDivida itemValorDivida : lista) {
            valorTotalItemValoDivida = valorTotalItemValoDivida.add(itemValorDivida.getValor());
        }
        return lista;
    }

    public BigDecimal getValorTotalItemValoDivida() {
        return valorTotalItemValoDivida;
    }

    public void setValorTotalItemValoDivida(BigDecimal valorTotalItemValoDivida) {
        this.valorTotalItemValoDivida = valorTotalItemValoDivida;
    }

    public List<DAM> listaDAMsValorDivida() {
        return consultaDebitoIntegralDAO.listaDamsValorDivida(getId());
    }

    public List<ItemDAM> listaItensDAM(Long idDAM) {
        return consultaDebitoIntegralDAO.listaItensDAM(idDAM);
    }

    public void setListaParcelasValorDivida(List<ParcelaValorDivida> listaParcelasValorDivida) {
        this.listaParcelasValorDivida = listaParcelasValorDivida;
    }

    public List<ParcelaValorDivida> getListaParcelasValorDivida() {
        return listaParcelasValorDivida;
    }

    public boolean parcelaPertencenteAoValorDivida(Long idParcela) {
        return getId().equals(idParcela);
    }

    public class RelatorioConsultaIntegralLancamentoDebitos extends AbstractReport {

        private StringBuilder where;
        private StringBuilder semDados;
        private StringBuilder filtros;

        public RelatorioConsultaIntegralLancamentoDebitos() {
            this.where = new StringBuilder();
            this.semDados = new StringBuilder();
            this.filtros = new StringBuilder();
        }

        public StringBuilder getWhere() {
            return where;
        }

        public void setWhere(StringBuilder where) {
            this.where = where;
        }

        public StringBuilder getSemDados() {
            return semDados;
        }

        public void setSemDados(StringBuilder semDados) {
            this.semDados = semDados;
        }

        public StringBuilder getFiltros() {
            return filtros;
        }

        public void setFiltros(StringBuilder filtros) {
            this.filtros = filtros;
        }

        public void geraRelatorio(Long idParcelaValorDivida) throws IOException, JRException {
            where = new StringBuilder();
            semDados = new StringBuilder("Não foram encontrados registros");
            filtros = new StringBuilder();

            String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
            subReport += "/report/";
            String arquivoJasper = "RelatorioConsultaIntegralDebitos.jasper";
            HashMap parameters = new HashMap();

            if (filtrosConsulta.getTipoCadastroTributario() != null) {
                filtros.append("Tipo de Cadastro: ").append(filtrosConsulta.getTipoCadastroTributario().getDescricao()).append(" ");
            }
            if (filtrosConsulta.getExercicioInicial() != null && filtrosConsulta.getExercicioInicial().getId() != null) {
                where.append(" AND EX.ANO >= ").append(filtrosConsulta.getExercicioInicial().getAno()).append(" ");
                filtros.append("Exercício Inicial: ").append(filtrosConsulta.getExercicioInicial()).append(" ");
            }
            if (filtrosConsulta.getExercicioFinal() != null && filtrosConsulta.getExercicioFinal().getId() != null) {
                where.append(" AND EX.ANO <= ").append(filtrosConsulta.getExercicioFinal().getAno()).append(" ");
                filtros.append("Exercício Final: ").append(filtrosConsulta.getExercicioFinal()).append(" ");
            }
            if (filtrosConsulta.getCadastro() != null && filtrosConsulta.getCadastro().getId() != null) {
                where.append(" AND CLC.CADASTRO_ID = ").append(filtrosConsulta.getCadastro().getId());
                filtros.append("Cadastro  ").append(filtrosConsulta.getCadastro().getNumeroCadastro()).append(" ");
            }
            if (filtrosConsulta.getContribuinte() != null && filtrosConsulta.getContribuinte().getId() != null) {
                where.append(" AND EXISTS (SELECT 1 FROM CALCULOPESSOA CP WHERE CP.CALCULO_ID = CLC.ID AND CP.PESSOA_ID = ").append(filtrosConsulta.getContribuinte().getId()).append(" ) ");
                filtros.append("Pessoa ID: ").append(filtrosConsulta.getContribuinte().getId()).append(" ");
            }
            if (idParcelaValorDivida != null) {
                where.append(" AND PVD.ID = ").append(idParcelaValorDivida).append(" ");
            }

            where.append(" ORDER BY D.DESCRICAO");
            parameters.put("SUBREPORT_DIR", subReport);
            parameters.put("CONDICAO", where.toString());
            parameters.put("BRASAO", getCaminhoImagem());
            parameters.put("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
            parameters.put("MODULO", "Tributário");
            parameters.put("NOMERELATORIO", "RELATÓRIO DE CONSULTA INTEGRAL DE LANÇAMENTO DE DÉBITOS");
            parameters.put("SECRETARIA", "SECRETARIA DE FINANÇAS");
            parameters.put("FILTROS", filtros.toString());

            try {
                gerarRelatorio(arquivoJasper, parameters);
            } catch (JRException e) {
                logger.error(e.getMessage());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void recuperarCalculo() {
        mostarDetalhesCalculo = true;
        recuperarCalculoIPTU();
    }

    private void recuperarCalculoIPTU() {
        calculoIPTU = consultaDebitoIntegralDAO.buscarCalculoIPTU(getId());
        if (calculoIPTU != null) {
            consultaDebitoIntegralDAO.recuperarProcessoCalculoIPTU(calculoIPTU);
            consultaDebitoIntegralDAO.recuperarMemoriaCalculoIPTU(calculoIPTU);
        }
    }

    public CalculoIPTU getCalculoIPTU() {
        return calculoIPTU;
    }

    public void setCalculoIPTU(CalculoIPTU calculoIPTU) {
        this.calculoIPTU = calculoIPTU;
    }

    public boolean isMostarDetalhesCalculo() {
        return mostarDetalhesCalculo;
    }

    public void setMostarDetalhesCalculo(boolean mostarDetalhesCalculo) {
        this.mostarDetalhesCalculo = mostarDetalhesCalculo;
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (filtrosConsulta.getTipoCadastroTributario() != null) {
            List<Divida> dividas = TipoCadastroTributario.PESSOA.equals(filtrosConsulta.getTipoCadastroTributario()) ?
                cacheTributario.getDividas() :
                cacheTributario.getDividasPorTipoCadastro(filtrosConsulta.getTipoCadastroTributario());
            for (Divida divida : dividas) {
                toReturn.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return toReturn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getListaDeIdsDAMs() {
        return listaDeIdsDAMs;
    }

    public void setListaDeIdsDAMs(String listaDeIdsDAMs) {
        this.listaDeIdsDAMs = listaDeIdsDAMs;
    }

    public void corrigirDAMsPelaLista() {
        if (!listaDeIdsDAMs.isEmpty()) {
            List<Long> idsDams = Lists.newArrayList();
            String[] split = listaDeIdsDAMs.split(",");
            for (String s : split) {
                idsDams.add(Long.valueOf(s));
            }
            loteBaixaFacade.corrigirDAMsPorIds(idsDams);
        }
    }

    public List<ResultadoParcela> getParcelasBloqueadas() {
        return parcelasBloqueadas;
    }

    public void liberar(ResultadoParcela parcela) {
        concorrenciaParcela.unLock(parcela.getIdParcela());
        FacesUtil.redirecionamentoInterno("/consulta-debito-integral/listar/");
    }

    public void removerDivida(Divida divida) {
        if (filtrosConsulta.getDividasSelecionadas().contains(divida)) {
            filtrosConsulta.getDividasSelecionadas().remove(divida);
        }
    }

    public void adicionarDivida() {
        try {
            validarDivida();
            filtrosConsulta.getDividasSelecionadas().add(divida);
            divida = new Divida();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDivida() {
        ValidacaoException ve = new ValidacaoException();
        if (divida == null || divida.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma dívida para adicionar");
        } else if (filtrosConsulta.getDividasSelecionadas().contains(divida)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Essa Dívida já foi selecionada.");
        }
        ve.lancarException();
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Date getVencimentoDam() {
        return vencimentoDam;
    }

    public void setVencimentoDam(Date vencimentoDam) {
        this.vencimentoDam = vencimentoDam;
    }

    public boolean calculoTemDetalhamento() {
        if (selecionado != null) {
            return Calculo.TipoCalculo.IPTU.name().equals(selecionado.getTipoCalculo());
        }
        return false;
    }

    public List<SituacaoParcela> getTodasAsSituacoes() {
        return SituacaoParcela.getValues();
    }

    public void irParaVerParcela(Long idParcela) {
        Web.poeNaSessao(filtrosConsulta);
        FacesUtil.redirecionamentoInterno("/consulta-debito-integral/ver/" + idParcela + "/");
    }

    public class Filtro {

        private Exercicio exercicioInicial;
        private Exercicio exercicioFinal;
        private Cadastro cadastro;
        private Pessoa contribuinte;
        private SituacaoParcela[] situacoes;
        private TipoCadastroTributario tipoCadastroTributario;
        private Boolean pesquisaDividaAtiva, pesquisaExercicio, pesquisaDividaAjuizada;
        private List<Divida> dividasSelecionadas;
        private Date vencimentoInicial, vencimentoFinal;
        private List<Propriedade> propriedades;
        private List<Compromissario> compromissarios;
        private Boolean situacaoCadastroImobiliario;
        private SituacaoCadastralCadastroEconomico situacaoCadastroEconomico;
        private Boolean situacaoCadastroRural;
        private SituacaoCadastralPessoa situacaoCadastroPessoa;

        public Exercicio getExercicioInicial() {
            return exercicioInicial;
        }

        public void setExercicioInicial(Exercicio exercicioInicial) {
            this.exercicioInicial = exercicioInicial;
        }

        public Exercicio getExercicioFinal() {
            return exercicioFinal;
        }

        public void setExercicioFinal(Exercicio exercicioFinal) {
            this.exercicioFinal = exercicioFinal;
        }

        public Cadastro getCadastro() {
            return cadastro;
        }

        public void setCadastro(Cadastro cadastro) {
            this.cadastro = cadastro;
        }

        public Pessoa getContribuinte() {
            return contribuinte;
        }

        public void setContribuinte(Pessoa contribuinte) {
            this.contribuinte = contribuinte;
        }

        public SituacaoParcela[] getSituacoes() {
            return situacoes;
        }

        public void setSituacoes(SituacaoParcela[] situacoes) {
            this.situacoes = situacoes;
        }

        public TipoCadastroTributario getTipoCadastroTributario() {
            return tipoCadastroTributario;
        }

        public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
            this.tipoCadastroTributario = tipoCadastroTributario;
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

        public List<Divida> getDividasSelecionadas() {
            return dividasSelecionadas;
        }

        public void setDividasSelecionadas(List<Divida> dividasSelecionadas) {
            this.dividasSelecionadas = dividasSelecionadas;
        }

        public Date getVencimentoInicial() {
            return vencimentoInicial;
        }

        public void setVencimentoInicial(Date vencimentoInicial) {
            this.vencimentoInicial = vencimentoInicial;
        }

        public Date getVencimentoFinal() {
            return vencimentoFinal;
        }

        public void setVencimentoFinal(Date vencimentoFinal) {
            this.vencimentoFinal = vencimentoFinal;
        }

        public List<Propriedade> getPropriedades() {
            return propriedades;
        }

        public void setPropriedades(List<Propriedade> propriedades) {
            this.propriedades = propriedades;
        }

        public List<Compromissario> getCompromissarios() {
            return compromissarios;
        }

        public void setCompromissarios(List<Compromissario> compromissarios) {
            this.compromissarios = compromissarios;
        }

        public Boolean getSituacaoCadastroImobiliario() {
            return situacaoCadastroImobiliario != null ? situacaoCadastroImobiliario : true;
        }

        public void setSituacaoCadastroImobiliario(Boolean situacaoCadastroImobiliario) {
            this.situacaoCadastroImobiliario = situacaoCadastroImobiliario;
        }

        public SituacaoCadastralCadastroEconomico getSituacaoCadastroEconomico() {
            return situacaoCadastroEconomico != null ? situacaoCadastroEconomico : SituacaoCadastralCadastroEconomico.ATIVA_REGULAR;
        }

        public void setSituacaoCadastroEconomico(SituacaoCadastralCadastroEconomico situacaoCadastroEconomico) {
            this.situacaoCadastroEconomico = situacaoCadastroEconomico;
        }

        public Boolean getSituacaoCadastroRural() {
            return situacaoCadastroRural != null ? situacaoCadastroRural : true;
        }

        public void setSituacaoCadastroRural(Boolean situacaoCadastroRural) {
            this.situacaoCadastroRural = situacaoCadastroRural;
        }

        public SituacaoCadastralPessoa getSituacaoCadastroPessoa() {
            return situacaoCadastroPessoa != null ? situacaoCadastroPessoa : SituacaoCadastralPessoa.ATIVO;
        }

        public void setSituacaoCadastroPessoa(SituacaoCadastralPessoa situacaoCadastroPessoa) {
            this.situacaoCadastroPessoa = situacaoCadastroPessoa;
        }
    }
}
