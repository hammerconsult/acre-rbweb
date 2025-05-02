package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.RelatorioFichaFinanceira;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.cadastrofuncional.AssentamentoFuncional;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoFP;
import br.com.webpublico.entidadesauxiliares.ItemConsignacao;
import br.com.webpublico.entidadesauxiliares.ObjetoPesquisa;
import br.com.webpublico.entidadesauxiliares.rh.calculo.MemoriaCalculoRHDTO;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.administracaopagamento.IdentificadorBaseFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.cadastrofuncional.AssentamentoFuncionalFacade;
import br.com.webpublico.negocios.rh.processo.PadFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.CampoDB;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.RegistroDB;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.YearMonth;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 28/03/14
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "servidor-informacoes", pattern = "/servidor/info/", viewId = "/faces/rh/estatisticas/informacoes/info.xhtml"),
    @URLMapping(id = "servidor-informacoes-link", pattern = "/servidor/info/#{informacoesServidorControlador.mes}/#{informacoesServidorControlador.ano}/#{informacoesServidorControlador.tipoFolha}/#{informacoesServidorControlador.idVinculo}/", viewId = "/faces/rh/estatisticas/informacoes/info.xhtml")
})
public class InformacoesServidorControlador extends SuperControladorCRUD implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(InformacoesServidorControlador.class);
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private PenalidadeFPFacade penalidadeFPFacade;
    @EJB
    private AssentamentoFuncionalFacade assentamentoFuncionalFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private FaltasFacade faltasFacade;
    @EJB
    private HoraExtraFacade horaExtraFacade;
    @EJB
    private ConcessaoFeriasLicencaFacade concessaoFeriasLicencaFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    @EJB
    private PadFacade padFacade;

    @ManagedProperty(name = "relatorioFichaFinanceira", value = "#{relatorioFichaFinanceira}")
    private RelatorioFichaFinanceira relatorioFichaFinanceira;
    private List<ItemFichaFinanceiraFP> listaItemFichaFinanceiraFP;
    private List<ItemFichaFinanceiraFP> itensFichaFinanceiraFPMesAnterior;
    private Double totalDescontos = 0.0;
    private Double totalVantagens = 0.0;
    private ObjetoPesquisa objetoPesquisa;
    private FichaFinanceiraFP fichaFinanceiraFP;
    private List<LancamentoFP> lancamentoFPs;
    private List<HoraExtra> horaExtras;
    private List<Faltas> faltas;
    private List<EnquadramentoFuncional> enquadramentoFuncionals;
    private List<Dependente> dependentes;
    private List<BeneficioPensaoAlimenticia> beneficioPensaoAlimenticias;
    private List<CargoConfianca> cargoConfiancas;
    private List<FuncaoGratificada> funcaoGratificadas;
    private List<ConcessaoFeriasLicenca> concessaoFerias;
    private List<ConcessaoFeriasLicenca> licencasPremio;
    private List<PenalidadeFP> penalidades;
    private List<AssentamentoFuncional> assentamentoFuncionalList;
    private List<PeriodoAquisitivoFL> periodoAquisitivos;
    private List<Afastamento> afastamentos;
    private List<ExperienciaExtraCurricular> experienciasExtraCurriculares;
    private List<CedenciaContratoFP> cedencias;
    private List<ProvimentoReversao> provimentoReversoes;


    private List<RegistroDB> registroDBList;
    private List<CampoDB> campos;
    private Map<String, BigDecimal> listaItemFichaMapVant;
    private Map<String, BigDecimal> listaItemFichaMapDesc;
    private CartesianChartModel cartesianChartModel;

    private int mes;
    private int ano;
    private String tipoFolha;
    private long idVinculo;
    private Integer versao;

    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private CargoConfiancaFacade cargoConfiancaFacade;
    @EJB
    private FuncaoGratificadaFacade funcaoGratificadaFacade;
    private List<ItemConsignacao> itemConsignacoes;
    @EJB
    private BaseFPFacade baseFPFacade;
    @EJB
    private RecursoDoVinculoFPFacade recursoDoVinculoFPFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private PensaoAlimenticiaFacade pensaoAlimenticiaFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private ExperienciaExtraCurricularFacade experienciaExtraCurricularFacade;
    @EJB
    private CedenciaContratoFPFacade cedenciaContratoFPFacade;
    @EJB
    private ProvimentoReversaoFacade provimentoReversaoFacade;

    private BigDecimal totalBaseConsignacao;
    private BigDecimal totalBaseEuConsigoMais;
    private BigDecimal valorMargemEuConsigoMais;
    private BigDecimal percentualMargemEuConsigoMais;
    private BigDecimal totalBase5;
    private BigDecimal totalBase10;
    private BigDecimal totalBase20;
    private BigDecimal restante5;
    private BigDecimal restante10;
    private BigDecimal restante20;
    private BigDecimal cambioEntreConvenioEmprestimo;

    private BigDecimal totalCartao;
    private BigDecimal totalConvenio;
    private BigDecimal totalEmprestimo;

    private Integer minimoDiasDireitoEuConsigoMais;

    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;

    private Map<EventoFP, List<MemoriaCalculoRHDTO>> mapMemoriaCalculo;


    public List<BeneficioPensaoAlimenticia> getBeneficioPensaoAlimenticias() {
        return beneficioPensaoAlimenticias;
    }

    public void setBeneficioPensaoAlimenticias(List<BeneficioPensaoAlimenticia> beneficioPensaoAlimenticias) {
        this.beneficioPensaoAlimenticias = beneficioPensaoAlimenticias;
    }

    public CartesianChartModel getCartesianChartModel() {
        return cartesianChartModel;
    }

    public void setCartesianChartModel(CartesianChartModel cartesianChartModel) {
        this.cartesianChartModel = cartesianChartModel;
    }

    public List<Dependente> getDependentes() {
        return dependentes;
    }

    public void setDependentes(List<Dependente> dependentes) {
        this.dependentes = dependentes;
    }

    public BigDecimal getRestante5() {
        return restante5;
    }

    public void setRestante5(BigDecimal restante5) {
        this.restante5 = restante5;
    }

    public BigDecimal getRestante10() {
        return restante10;
    }

    public void setRestante10(BigDecimal restante10) {
        this.restante10 = restante10;
    }

    public BigDecimal getRestante20() {
        return restante20;
    }

    public void setRestante20(BigDecimal restante20) {
        this.restante20 = restante20;
    }

    public List<CampoDB> getCampos() {
        return campos;
    }

    public void setCampos(List<CampoDB> campos) {
        this.campos = campos;
    }

    public BigDecimal getTotalBaseConsignacao() {
        return totalBaseConsignacao;
    }

    public void setTotalBaseConsignacao(BigDecimal totalBaseConsignacao) {
        this.totalBaseConsignacao = totalBaseConsignacao;
    }

    public BigDecimal getTotalBaseEuConsigoMais() {
        return totalBaseEuConsigoMais;
    }

    public void setTotalBaseEuConsigoMais(BigDecimal totalBaseEuConsigoMais) {
        this.totalBaseEuConsigoMais = totalBaseEuConsigoMais;
    }

    public BigDecimal getValorMargemEuConsigoMais() {
        return valorMargemEuConsigoMais;
    }

    public void setValorMargemEuConsigoMais(BigDecimal valorMargemEuConsigoMais) {
        this.valorMargemEuConsigoMais = valorMargemEuConsigoMais;
    }

    public BigDecimal getPercentualMargemEuConsigoMais() {
        return percentualMargemEuConsigoMais;
    }

    public void setPercentualMargemEuConsigoMais(BigDecimal percentualMargemEuConsigoMais) {
        this.percentualMargemEuConsigoMais = percentualMargemEuConsigoMais;
    }

    public Integer getMinimoDiasDireitoEuConsigoMais() {
        return minimoDiasDireitoEuConsigoMais;
    }

    public void setMinimoDiasDireitoEuConsigoMais(Integer minimoDiasDireitoEuConsigoMais) {
        this.minimoDiasDireitoEuConsigoMais = minimoDiasDireitoEuConsigoMais;
    }

    public List<AssentamentoFuncional> getAssentamentoFuncionalList() {
        return assentamentoFuncionalList;
    }

    public void setAssentamentoFuncionalList(List<AssentamentoFuncional> assentamentoFuncionalList) {
        this.assentamentoFuncionalList = assentamentoFuncionalList;
    }

    private void createLineModels() {
        cartesianChartModel = new CartesianChartModel();
        LineChartSeries serie1 = new LineChartSeries("Vantagens");
        serie1.setLabel("Vantagens");
        for (Map.Entry<String, BigDecimal> valores : listaItemFichaMapVant.entrySet()) {

            serie1.set(valores.getKey(), valores.getValue());
        }
        if (!listaItemFichaMapVant.isEmpty()) {
            cartesianChartModel.addSeries(serie1);
        }

        LineChartSeries serie2 = new LineChartSeries("Descontos");
        serie2.setLabel("Descontos");
        for (Map.Entry<String, BigDecimal> valores : listaItemFichaMapDesc.entrySet()) {
            serie2.set(valores.getKey(), valores.getValue());
        }
        if (!listaItemFichaMapDesc.isEmpty()) {
            cartesianChartModel.addSeries(serie2);
        }
    }


    @Override
    public AbstractFacade getFacede() {
        return contratoFPFacade;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void consultarFicha() {

        mapMemoriaCalculo = Maps.newHashMap();
        listaItemFichaFinanceiraFP = fichaFinanceiraFPFacade.recuperafichaFinanceiraFP(objetoPesquisa.getMes(), objetoPesquisa.getAno(), objetoPesquisa.getVinculoFP(), objetoPesquisa.getTipoFolhaDePagamentoWeb(), versao);
        if (!listaItemFichaFinanceiraFP.isEmpty()) {
            fichaFinanceiraFP = (((ItemFichaFinanceiraFP) listaItemFichaFinanceiraFP.get(0)).getFichaFinanceiraFP());
        }
        atualizaValores(listaItemFichaFinanceiraFP);
        buscarMemoriaCalculo(listaItemFichaFinanceiraFP);
        DateTime dataInicio = new DateTime(objetoPesquisa.getAno(), objetoPesquisa.getMes(), 1, 0, 0);
        DateTime dataFim = dataInicio;
        dataInicio = dataInicio.minusMonths(12);
        while (dataInicio.isBefore(dataFim)) {
            dataInicio = dataInicio.plusMonths(1);
            List<ItemFichaFinanceiraFP> itens = fichaFinanceiraFPFacade.recuperafichaFinanceiraFP(dataInicio.getMonthOfYear(), dataInicio.getYear(), objetoPesquisa.getVinculoFP(), objetoPesquisa.getTipoFolhaDePagamentoWeb(), versao);
            preenchaLista(listaItemFichaMapVant, itens, dataInicio, TipoEventoFP.VANTAGEM);
            preenchaLista(listaItemFichaMapDesc, itens, dataInicio, TipoEventoFP.DESCONTO);
        }

        createLineModels();
    }

    public void consultarFichaMesAnterior() {
        YearMonth mesAno = new YearMonth(objetoPesquisa.getAno(), objetoPesquisa.getMes());
        itensFichaFinanceiraFPMesAnterior = fichaFinanceiraFPFacade.recuperafichaFinanceiraFP(mesAno.minusMonths(1).getMonthOfYear(), mesAno.minusMonths(1).getYear(), objetoPesquisa.getVinculoFP(), objetoPesquisa.getTipoFolhaDePagamentoWeb(), versao);

    }

    public void atualizaValores(List<ItemFichaFinanceiraFP> fichas) {
        totalDescontos = 0.0;
        totalVantagens = 0.0;
        for (ItemFichaFinanceiraFP itens : fichas) {
            if (itens.getTipoEventoFP().equals(TipoEventoFP.DESCONTO)) {
                totalDescontos += itens.getValor().doubleValue();
            }
            if (itens.getTipoEventoFP().equals(TipoEventoFP.VANTAGEM)) {
                totalVantagens += itens.getValor().doubleValue();
            }
        }
    }

    public void preenchaLista(Map<String, BigDecimal> lista, List<ItemFichaFinanceiraFP> fichas, DateTime date, TipoEventoFP tipoEventoFP) {
        BigDecimal total = new BigDecimal(0);

        for (ItemFichaFinanceiraFP itens : fichas) {
            if (itens.getTipoEventoFP().equals(tipoEventoFP)) {
                total = total.add(itens.getValor());
            }
        }
        lista.put((Mes.getMesToInt(date.getMonthOfYear()).getDescricao() + "/" + date.getYear()), total);

    }

    public List<ItemConsignacao> getItemConsignacoes() {
        return itemConsignacoes;
    }

    public void setItemConsignacoes(List<ItemConsignacao> itemConsignacoes) {
        this.itemConsignacoes = itemConsignacoes;
    }

    public BigDecimal getTotalEmprestimo() {
        return totalEmprestimo;
    }

    public void setTotalEmprestimo(BigDecimal totalEmprestimo) {
        this.totalEmprestimo = totalEmprestimo;
    }

    public BigDecimal getTotalConvenio() {
        return totalConvenio;
    }

    public void setTotalConvenio(BigDecimal totalConvenio) {
        this.totalConvenio = totalConvenio;
    }

    public BigDecimal getTotalCartao() {
        return totalCartao;
    }

    public void setTotalCartao(BigDecimal totalCartao) {
        this.totalCartao = totalCartao;
    }

    public BigDecimal getTotalBase20() {
        return totalBase20;
    }

    public void setTotalBase20(BigDecimal totalBase20) {
        this.totalBase20 = totalBase20;
    }

    public BigDecimal getTotalBase10() {
        return totalBase10;
    }

    public void setTotalBase10(BigDecimal totalBase10) {
        this.totalBase10 = totalBase10;
    }

    public BigDecimal getTotalBase5() {
        return totalBase5;
    }

    public void setTotalBase5(BigDecimal totalBase5) {
        this.totalBase5 = totalBase5;
    }

    public List<FuncaoGratificada> getFuncaoGratificadas() {
        return funcaoGratificadas;
    }

    public void setFuncaoGratificadas(List<FuncaoGratificada> funcaoGratificadas) {
        this.funcaoGratificadas = funcaoGratificadas;
    }

    public List<PeriodoAquisitivoFL> getPeriodoAquisitivos() {
        return periodoAquisitivos;
    }

    public void setPeriodoAquisitivos(List<PeriodoAquisitivoFL> periodoAquisitivos) {
        this.periodoAquisitivos = periodoAquisitivos;
    }

    public List<CargoConfianca> getCargoConfiancas() {
        return cargoConfiancas;
    }

    public void setCargoConfiancas(List<CargoConfianca> cargoConfiancas) {
        this.cargoConfiancas = cargoConfiancas;
    }

    @URLAction(mappingId = "servidor-informacoes", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        objetoPesquisa = new ObjetoPesquisa();
        faltas = new LinkedList<>();
        lancamentoFPs = new LinkedList<>();
        horaExtras = new LinkedList<>();
        provimentoReversoes = new ArrayList<>();

        fichaFinanceiraFP = new FichaFinanceiraFP();
        listaItemFichaFinanceiraFP = new LinkedList<>();
        enquadramentoFuncionals = new LinkedList<>();
        concessaoFerias = new LinkedList<>();
        licencasPremio = new LinkedList<>();
        listaItemFichaMapVant = new LinkedHashMap<>();
        listaItemFichaMapDesc = new LinkedHashMap<>();
        dependentes = new LinkedList<>();
        cargoConfiancas = new LinkedList<>();
        funcaoGratificadas = new LinkedList<>();
        itemConsignacoes = new LinkedList<>();
        periodoAquisitivos = new LinkedList<>();
        penalidades = new LinkedList<>();
        assentamentoFuncionalList = new ArrayList<>();
        registroDBList = new LinkedList<>();
        campos = new LinkedList<>();
        afastamentos = Lists.newLinkedList();
        experienciasExtraCurriculares = Lists.newLinkedList();
        itensFichaFinanceiraFPMesAnterior = Lists.newLinkedList();
        mapMemoriaCalculo = Maps.newHashMap();
        cedencias = Lists.newLinkedList();

        mapMemoriaCalculo = Maps.newHashMap();
        inicializaValores();

        createLineModels();
    }

    @URLAction(mappingId = "servidor-informacoes-link", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void recuperarValoreLink() {
        novo();
        objetoPesquisa.setAno(ano);
        objetoPesquisa.setMes(mes);
        objetoPesquisa.setTipoFolhaDePagamentoWeb(TipoFolhaDePagamento.valueOf(tipoFolha));
        VinculoFP v = vinculoFPFacade.recuperar(idVinculo);
        objetoPesquisa.setVinculoFP(v);
        iniciarBusca();
    }

    private void inicializaValores() {
        totalBaseConsignacao = BigDecimal.ZERO;

        totalBase5 = BigDecimal.ZERO;
        totalBase10 = BigDecimal.ZERO;
        totalBase20 = BigDecimal.ZERO;

        totalCartao = BigDecimal.ZERO;
        totalConvenio = BigDecimal.ZERO;
        totalEmprestimo = BigDecimal.ZERO;

        restante5 = BigDecimal.ZERO;
        restante10 = BigDecimal.ZERO;
        restante20 = BigDecimal.ZERO;
    }

    public void iniciarBusca() {
        try {
            validarCampos();
            consultarFicha();
            buscarLancamentos();
            buscarFaltas();
            buscarHorasExtra();
            buscarProvimentoReversoes();
            buscarEnquadramento();
            buscarConcessaoFerias();
            buscarLicencaPremio();
            buscarECarregarPeriodoAquisitivo();
            buscarPenalidade();
            buscarAssentamentos();
            buscarDependentes();
            buscarDependentesPensaoAlimenticia();
            buscarCargosConfincas();
            buscarFuncaoGrafificadas();
            calcularConsignacao();
            buscarAfastamentos();
            buscarExperienciasExtraCurriculares();
            consultarFichaMesAnterior();
            buscarCedencias();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void buscarAfastamentos() {
        try {
            afastamentos = afastamentoFacade.buscarTodosAfastamentosPorContrato(objetoPesquisa.getVinculoFP());
        } catch (Exception e) {
            logger.error("Erro em buscarAfastamentos", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível recuperar os afastamentos do(a) servidor(a), erro: " + e.getMessage());
        }

    }

    private void buscarExperienciasExtraCurriculares() {
        try {
            experienciasExtraCurriculares = experienciaExtraCurricularFacade.buscarTodasExperienciasExtraCurricularesPorContrato(objetoPesquisa.getVinculoFP());
        } catch (Exception e) {
            logger.error("Não foi possível buscar experiencias extra curriculares ", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível recuperar as experiencias extra curriculares do(a) servidor(a), erro: " + e.getMessage());
        }
    }

    private void buscarDependentesPensaoAlimenticia() {
        List<BeneficioPensaoAlimenticia> retorno;
        beneficioPensaoAlimenticias = new LinkedList<>();
        List<PensaoAlimenticia> pensaoAlimenticias = pensaoAlimenticiaFacade.recuperarPensaoPorInstituidor(objetoPesquisa.getVinculoFP());
        beneficioPensaoAlimenticias = Lists.newArrayList();
        if (pensaoAlimenticias != null) {

            for (PensaoAlimenticia pensaoAlimenticia : pensaoAlimenticias) {
                beneficioPensaoAlimenticias.addAll(pensaoAlimenticia.getBeneficiosPensaoAlimenticia());
            }
        }
    }

    private void calcularConsignacao() {
        inicializaValores();
        itemConsignacoes = new LinkedList<>();
        for (TipoDeConsignacao t : TipoDeConsignacao.values()) {
            List<LancamentoFP> lancamentoFPList = lancamentoFPFacade.listaLancamentosPorTipoConsignacao(objetoPesquisa.getVinculoFP(), objetoPesquisa.getAno(), objetoPesquisa.getMes(), t);
            for (LancamentoFP lancamentoFP : lancamentoFPList) {
                itemConsignacoes.add(new ItemConsignacao(null, lancamentoFP));
            }
        }
        for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : listaItemFichaFinanceiraFP) {
            ItemConsignacao l = eventoContemNoLancamento(itemFichaFinanceiraFP.getEventoFP());
            if (l != null) l.setItemFichaFinanceiraFP(itemFichaFinanceiraFP);
        }
        ConfiguracaoFP configuracaoFP = baseFPFacade.buscarConfiguracaoFPVigente();
        totalBaseConsignacao = baseFPFacade.recuperarValoresBase(listaItemFichaFinanceiraFP, configuracaoFP);
        totalBaseEuConsigoMais = baseFPFacade.recurperarValoresEuConsigoMais(listaItemFichaFinanceiraFP, configuracaoFP);
        percentualMargemEuConsigoMais = configuracaoFP.getPercentualMargemEmprestimo();
        valorMargemEuConsigoMais = calcularMargemEuConsigoMais(configuracaoFP);
        minimoDiasDireitoEuConsigoMais = configuracaoFP.getQtdeMinimaDiasEuConsigMais();
        preemcherValoresDoPercentualDaConsignacao();
        preemcherValoresParaDescontados();
        verificarVerbasNaoDescontadas();
    }

    private BigDecimal calcularMargemEuConsigoMais(ConfiguracaoFP configuracaoFP) {
        return (totalBaseEuConsigoMais != null ? totalBaseEuConsigoMais : BigDecimal.ZERO).multiply(percentualMargemEuConsigoMais != null ?
            percentualMargemEuConsigoMais : BigDecimal.ZERO).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }

    private void verificarVerbasNaoDescontadas() {
        cambioEntreConvenioEmprestimo = lancamentoFPFacade.verificarConvenio(itemConsignacoes, totalBase20, totalBase10, cambioEntreConvenioEmprestimo, totalConvenio);
        lancamentoFPFacade.verificarEmprestimo(itemConsignacoes, cambioEntreConvenioEmprestimo);
        lancamentoFPFacade.verificarCartao(itemConsignacoes, totalBase5);
    }

    public String getCargo() {
        if (objetoPesquisa != null) {
            if (objetoPesquisa.getVinculoFP() != null) {
                if (objetoPesquisa.getVinculoFP() instanceof ContratoFP) {
                    return ((ContratoFP) objetoPesquisa.getVinculoFP()).getCargo() != null ? ((ContratoFP) objetoPesquisa.getVinculoFP()).getCargo().toString() : "";
                }
                if (objetoPesquisa.getVinculoFP() instanceof Pensionista) {
                    return "PENSIONISTA";
                }
                if (objetoPesquisa.getVinculoFP() instanceof Aposentadoria) {
                    return "Aposentado em " + ((Aposentadoria) objetoPesquisa.getVinculoFP()).getContratoFP().getCargo();
                }
            }
        }
        return " ";
    }

    public String getModalidadeContrato() {
        if (objetoPesquisa != null) {
            if (objetoPesquisa.getVinculoFP() != null) {
                if (objetoPesquisa.getVinculoFP() instanceof ContratoFP) {
                    return ((ContratoFP) objetoPesquisa.getVinculoFP()).getModalidadeContratoFP() != null ? ((ContratoFP) objetoPesquisa.getVinculoFP()).getModalidadeContratoFP().getDescricao() : "";
                }
            }
        }
        return " ";
    }

    public String getTipoRegimeJuridico() {
        if (objetoPesquisa != null) {
            if (objetoPesquisa.getVinculoFP() != null) {
                if (objetoPesquisa.getVinculoFP() instanceof ContratoFP) {
                    return ((ContratoFP) objetoPesquisa.getVinculoFP()).getTipoRegime() != null ? ((ContratoFP) objetoPesquisa.getVinculoFP()).getTipoRegime().getDescricao() : "";
                }
            }
        }
        return " ";
    }

    private void preemcherValoresParaDescontados() {

        for (ItemConsignacao itemConsignacao : itemConsignacoes) {
            if (itemConsignacao.getLancamentoFP().getTipoLancamentoFP().equals(TipoLancamentoFP.VALOR)) {
                if (itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao().equals(TipoDeConsignacao.CARTAO)) {
                    totalCartao = totalCartao.add(itemConsignacao.getLancamentoFP().getQuantificacao());
                }
                if (itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao().equals(TipoDeConsignacao.CONVENIO)) {
                    totalConvenio = totalConvenio.add(itemConsignacao.getLancamentoFP().getQuantificacao());
                }
                if (itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao().equals(TipoDeConsignacao.EMPRESTIMO)) {
                    totalEmprestimo = totalEmprestimo.add(itemConsignacao.getLancamentoFP().getQuantificacao());
                }
            } else {
                if (itemConsignacao.getItemFichaFinanceiraFP() != null) {
                    if (itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao().equals(TipoDeConsignacao.CARTAO)) {
                        totalCartao = totalCartao.add(itemConsignacao.getItemFichaFinanceiraFP().getValor());
                    }
                    if (itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao().equals(TipoDeConsignacao.CONVENIO)) {
                        totalConvenio = totalConvenio.add(itemConsignacao.getItemFichaFinanceiraFP().getValor());
                    }
                    if (itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao().equals(TipoDeConsignacao.EMPRESTIMO)) {
                        totalEmprestimo = totalEmprestimo.add(itemConsignacao.getItemFichaFinanceiraFP().getValor());
                    }
                }
            }
        }

    }

    private void preemcherValoresDoPercentualDaConsignacao() {
        if (totalBaseConsignacao.compareTo(BigDecimal.ZERO) != 0) {
            totalBase5 = totalBaseConsignacao.multiply(new BigDecimal("0.05"));
            totalBase10 = totalBaseConsignacao.multiply(new BigDecimal("0.1"));
            totalBase20 = totalBaseConsignacao.multiply(new BigDecimal("0.2"));
        }

    }

    private ItemConsignacao eventoContemNoLancamento(EventoFP eventoFP) {
        for (ItemConsignacao item : itemConsignacoes) {
            if (item.getLancamentoFP().getEventoFP().equals(eventoFP)) {
                return item;
            }
        }
        return null;
    }


    private void buscarFuncaoGrafificadas() {
        cargoConfiancas = new LinkedList<>();
        List<CargoConfianca> retorno = new LinkedList<>();
        if (objetoPesquisa.getVinculoFP() instanceof ContratoFP) {
            retorno = cargoConfiancaFacade.recuperaCargoConfiancaContratoSemVigencia((ContratoFP) objetoPesquisa.getVinculoFP());
        }
        if (objetoPesquisa.getVinculoFP() instanceof Pensionista) {
            retorno = cargoConfiancaFacade.recuperaCargoConfiancaContratoSemVigencia(((Pensionista) objetoPesquisa.getVinculoFP()).getPensao().getContratoFP());
        }
        if (objetoPesquisa.getVinculoFP() instanceof Aposentadoria) {
            retorno = cargoConfiancaFacade.recuperaCargoConfiancaContratoSemVigencia(((Aposentadoria) objetoPesquisa.getVinculoFP()).getContratoFP());
        }
        for (CargoConfianca cc : retorno) {
            cargoConfiancas.add(cargoConfiancaFacade.recuperar(cc.getId()));
        }
    }

    private void buscarCargosConfincas() {
        funcaoGratificadas = new LinkedList<>();
        List<FuncaoGratificada> retorno = new LinkedList<>();
        if (objetoPesquisa.getVinculoFP() instanceof ContratoFP) {
            retorno = funcaoGratificadaFacade.recuperaFuncaoGratificadaContratoSemVigencia((ContratoFP) objetoPesquisa.getVinculoFP());
        }
        if (objetoPesquisa.getVinculoFP() instanceof Pensionista) {
            retorno = funcaoGratificadaFacade.recuperaFuncaoGratificadaContratoSemVigencia(((Pensionista) objetoPesquisa.getVinculoFP()).getPensao().getContratoFP());
        }
        if (objetoPesquisa.getVinculoFP() instanceof Aposentadoria) {
            retorno = funcaoGratificadaFacade.recuperaFuncaoGratificadaContratoSemVigencia(((Aposentadoria) objetoPesquisa.getVinculoFP()).getContratoFP());
        }
        for (FuncaoGratificada fg : retorno) {
            FuncaoGratificada funcao = funcaoGratificadaFacade.recuperar(fg.getId());
            funcaoGratificadas.add(funcao);
            for (EnquadramentoFG fgpcs : funcao.getEnquadramentoFGs()) {
                BigDecimal valor = enquadramentoPCSFacade.recuperaValorDecimal(fgpcs.getCategoriaPCS(), fgpcs.getProgressaoPCS(), fg.getFinalVigencia());
                fgpcs.setVencimentoBase(valor == null ? BigDecimal.ZERO : valor);
                List<EnquadramentoPCS> pcss = enquadramentoPCSFacade.recuperaTodosEnquadramentos(fgpcs.getCategoriaPCS(), fgpcs.getProgressaoPCS());
                Collections.sort(pcss, EnquadramentoPCS.Comparators.INICIOVIGENCIADESC);
                fgpcs.setEnquadramentoPCSList(pcss);
            }
        }

    }

    private void buscarDependentes() {
        List<Dependente> retorno;
        dependentes = new LinkedList<>();
        retorno = dependenteFacade.dependentesPorResponsavel(objetoPesquisa.getVinculoFP().getMatriculaFP().getPessoa());
        for (Dependente d : retorno) {
            dependentes.add(dependenteFacade.recuperar(d.getId()));
        }
    }

    public String getIdade(Date data) {
        if (data != null) {
            return DataUtil.getIdade(data);
        }
        return "";
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (objetoPesquisa == null) {
            return;
        }
        if (objetoPesquisa.getVinculoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo servidor deve ser informado.");
        }
        if (objetoPesquisa.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo mês deve ser informado.");
        }
        if (objetoPesquisa.getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo ano deve ser informado.");
        }
        if (objetoPesquisa.getTipoFolhaDePagamentoWeb() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipoi de folha deve ser informado.");
        }

        ve.lancarException();

    }

    private boolean validaBuscaCamposSecundarios() {
        if (objetoPesquisa == null) {
            return false;
        }
        if (objetoPesquisa.getTipoFolhaDePagamentoWeb() == null) {
            FacesUtil.addError("Atenção", "Preencha o Tipo de Folha corretamente");
            return false;
        }

        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    private void buscarLancamentos() {
        if (validaBuscaCamposSecundarios()) {
            lancamentoFPs = lancamentoFPFacade.buscarLancamentosPorTipo(objetoPesquisa.getVinculoFP(), objetoPesquisa.getAno(), objetoPesquisa.getMes(), objetoPesquisa.getTipoFolhaDePagamentoWeb(), new Date(), false);
        }
    }

    private void buscarFaltas() {
        if (objetoPesquisa.getVinculoFP() instanceof ContratoFP)
            faltas = faltasFacade.recuperaFaltasPorContrato((ContratoFP) objetoPesquisa.getVinculoFP());
    }

    private void buscarHorasExtra() {
//        horaExtras = horaExtraFacade.totalHorasExtrasMesAnoPorServidor(objetoPesquisa.getVinculoFP(), objetoPesquisa.getAno(), objetoPesquisa.getMes());
        horaExtras = horaExtraFacade.totalHorasExtrasPorServidor(objetoPesquisa.getVinculoFP());
    }

    private void buscarProvimentoReversoes() {
        provimentoReversoes = provimentoReversaoFacade.buscarProvimentoReversaoPorVinculoFP(objetoPesquisa.getVinculoFP().getId());
    }

    private void buscarConcessaoFerias() {
        concessaoFerias = concessaoFeriasLicencaFacade.findConcessaoFeriasLincencaByServidor(objetoPesquisa.getVinculoFP().getId(), TipoPeriodoAquisitivo.FERIAS);
    }

    private void buscarLicencaPremio() {
        licencasPremio = concessaoFeriasLicencaFacade.findConcessaoFeriasLincencaByServidor(objetoPesquisa.getVinculoFP().getId(), TipoPeriodoAquisitivo.LICENCA);
    }

    private void buscarCedencias() {
        cedencias = cedenciaContratoFPFacade.buscarCedenciaContratoFP(objetoPesquisa.getVinculoFP().getId());
    }

    private void buscarECarregarPeriodoAquisitivo() {
        periodoAquisitivos = periodoAquisitivoFLFacade.recuperaPeriodosPorContratoSemConcessao(objetoPesquisa.getVinculoFP());
    }

    private void buscarPenalidade() {
        if (objetoPesquisa.getVinculoFP() instanceof ContratoFP) {
            penalidades = penalidadeFPFacade.buscarPenalidadePorContrato((ContratoFP) objetoPesquisa.getVinculoFP());
        }
        if (objetoPesquisa.getVinculoFP() instanceof Pensionista) {
            penalidades = penalidadeFPFacade.buscarPenalidadePorContrato(((Pensionista) objetoPesquisa.getVinculoFP()).getPensao().getContratoFP());
        }
        if (objetoPesquisa.getVinculoFP() instanceof Aposentadoria) {
            penalidades = penalidadeFPFacade.buscarPenalidadePorContrato(((Aposentadoria) objetoPesquisa.getVinculoFP()).getContratoFP());
        }
    }

    private void buscarAssentamentos() {
        if (objetoPesquisa.getVinculoFP() instanceof ContratoFP) {
            assentamentoFuncionalList = assentamentoFuncionalFacade.buscarAssentamentosFuncionaisPorContrato((ContratoFP) objetoPesquisa.getVinculoFP());
            assentamentoFuncionalList.sort(Comparator.comparingLong(AssentamentoFuncional::getSequencial));
        }
    }

    public boolean hasProcessoAdministrativoDisciplonar() {
        if (objetoPesquisa != null && objetoPesquisa.getVinculoFP() != null) {
            return padFacade.hasProcessoAdministrativoDisciplonarEmCurso(objetoPesquisa.getVinculoFP());
        }
        return false;
    }

    private void buscarEnquadramento() {
        if (objetoPesquisa.getVinculoFP() instanceof ContratoFP) {
            enquadramentoFuncionals = enquadramentoFuncionalFacade.recuperaEnquadramentoContratoFP((ContratoFP) objetoPesquisa.getVinculoFP());
        }
        for (EnquadramentoFuncional enquadramentoFuncional : enquadramentoFuncionals) {
            BigDecimal valor = enquadramentoPCSFacade.recuperaValorDecimal(enquadramentoFuncional.getCategoriaPCS(), enquadramentoFuncional.getProgressaoPCS(), enquadramentoFuncional.getFinalVigencia());
            enquadramentoFuncional.setVencimentoBase(valor == null ? BigDecimal.ZERO : valor);
            enquadramentoFuncional.setEnquadramentoPCSList(enquadramentoPCSFacade.recuperaTodosEnquadramentos(enquadramentoFuncional.getCategoriaPCS(), enquadramentoFuncional.getProgressaoPCS()));
        }
    }

    public void gerarRelatorioFicha() {
        relatorioFichaFinanceira.setVinculoFPSelecionado(objetoPesquisa.getVinculoFP());
        relatorioFichaFinanceira.setAno(objetoPesquisa.getAno());
        relatorioFichaFinanceira.setMes(objetoPesquisa.getMes());
        relatorioFichaFinanceira.setMesFinal(objetoPesquisa.getMes());
        relatorioFichaFinanceira.setTipoFolhaDePagamento(objetoPesquisa.getTipoFolhaDePagamentoWeb());
        relatorioFichaFinanceira.gerarRelatorio("PDF");
    }

    public Converter getConverterContratoFP() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
                return vinculoFPFacade.recuperar(VinculoFP.class, s);

            }

            @Override
            public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
                if (value == null) {
                    return null;
                } else {
                    if (value instanceof Long) {
                        return String.valueOf(value);
                    } else {
                        try {
                            return value.toString();
                        } catch (Exception e) {
                            return String.valueOf(value);
                        }
                    }
                }
            }

        };

    }

    public Double getTotal() {
        return totalVantagens - totalDescontos;
    }

    public Double getTotalVantagens() {
        return totalVantagens;
    }

    public void setTotalVantagens(Double totalVantagens) {
        this.totalVantagens = totalVantagens;
    }

    public Double getTotalDescontos() {
        return totalDescontos;
    }

    public void setTotalDescontos(Double totalDescontos) {
        this.totalDescontos = totalDescontos;
    }

    public ObjetoPesquisa getObjetoPesquisa() {
        return objetoPesquisa;
    }

    public void setObjetoPesquisa(ObjetoPesquisa objetoPesquisa) {
        this.objetoPesquisa = objetoPesquisa;
    }

    public RelatorioFichaFinanceira getRelatorioFichaFinanceira() {
        return relatorioFichaFinanceira;
    }

    public void setRelatorioFichaFinanceira(RelatorioFichaFinanceira relatorioFichaFinanceira) {
        this.relatorioFichaFinanceira = relatorioFichaFinanceira;
    }

    public FichaFinanceiraFP getFichaFinanceiraFP() {
        return fichaFinanceiraFP;
    }

    public void setFichaFinanceiraFP(FichaFinanceiraFP fichaFinanceiraFP) {
        this.fichaFinanceiraFP = fichaFinanceiraFP;
    }

    public List<LancamentoFP> getLancamentoFPs() {
        return lancamentoFPs;
    }

    public void setLancamentoFPs(List<LancamentoFP> lancamentoFPs) {
        this.lancamentoFPs = lancamentoFPs;
    }

    public List<HoraExtra> getHoraExtras() {
        return horaExtras;
    }

    public void setHoraExtras(List<HoraExtra> horaExtras) {
        this.horaExtras = horaExtras;
    }

    public List<Afastamento> getAfastamentos() {
        return afastamentos;
    }

    public void setAfastamentos(List<Afastamento> afastamentos) {
        this.afastamentos = afastamentos;
    }

    public List<ExperienciaExtraCurricular> getExperienciasExtraCurriculares() {
        return experienciasExtraCurriculares;
    }

    public void setExperienciasExtraCurriculares(List<ExperienciaExtraCurricular> experienciasExtraCurriculares) {
        this.experienciasExtraCurriculares = experienciasExtraCurriculares;
    }

    public List<Faltas> getFaltas() {
        return faltas;
    }

    public void setFaltas(List<Faltas> faltas) {
        this.faltas = faltas;
    }

    public List<ItemFichaFinanceiraFP> getListaItemFichaFinanceiraFP() {
        return listaItemFichaFinanceiraFP;
    }

    public void setListaItemFichaFinanceiraFP(List<ItemFichaFinanceiraFP> listaItemFichaFinanceiraFP) {
        this.listaItemFichaFinanceiraFP = listaItemFichaFinanceiraFP;
    }

    public List<RegistroDB> getRegistroDBList() {
        return registroDBList;
    }

    public void setRegistroDBList(List<RegistroDB> registroDBList) {
        this.registroDBList = registroDBList;
    }

    public List<EnquadramentoFuncional> getEnquadramentoFuncionals() {
        return enquadramentoFuncionals;
    }

    public void setEnquadramentoFuncionals(List<EnquadramentoFuncional> enquadramentoFuncionals) {
        this.enquadramentoFuncionals = enquadramentoFuncionals;
    }

    public List<ConcessaoFeriasLicenca> getConcessaoFerias() {
        return concessaoFerias;
    }

    public void setConcessaoFerias(List<ConcessaoFeriasLicenca> concessaoFerias) {
        this.concessaoFerias = concessaoFerias;
    }

    public List<ConcessaoFeriasLicenca> getLicencasPremio() {
        return licencasPremio;
    }

    public void setLicencasPremio(List<ConcessaoFeriasLicenca> licencasPremio) {
        this.licencasPremio = licencasPremio;
    }

    public List<RecursoDoVinculoFP> getRecursosFps() {
        if (objetoPesquisa != null && objetoPesquisa.getVinculoFP() != null) {
            return recursoDoVinculoFPFacade.recuperarRecursosDoVinculo(objetoPesquisa.getVinculoFP());
        }
        return null;
    }

    public List<LotacaoFuncional> getLotacaoFuncional() {
        if (objetoPesquisa != null && objetoPesquisa.getVinculoFP() != null) {
            return lotacaoFuncionalFacade.recuperaLotacaoFuncionalPorContratoFP(objetoPesquisa.getVinculoFP());
        }
        return null;
    }

    public String descricaoHierarquia(LotacaoFuncional lot) {
        if (lot != null) {
            return lotacaoFuncionalFacade.recuperaHierarquiaDaLotacao(lot.getUnidadeOrganizacional());
        }
        return "";
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getTipoFolha() {
        return tipoFolha;
    }

    public void setTipoFolha(String tipoFolha) {
        this.tipoFolha = tipoFolha;
    }

    public long getIdVinculo() {
        return idVinculo;
    }

    public void setIdVinculo(long idVinculo) {
        this.idVinculo = idVinculo;
    }

    public List<ItemFichaFinanceiraFP> getItensFichaFinanceiraFPMesAnterior() {
        return itensFichaFinanceiraFPMesAnterior;
    }

    public void setItensFichaFinanceiraFPMesAnterior(List<ItemFichaFinanceiraFP> itensFichaFinanceiraFPMesAnterior) {
        this.itensFichaFinanceiraFPMesAnterior = itensFichaFinanceiraFPMesAnterior;
    }

    public Boolean incidenciaINSS(EventoFP eventoFP) {
        return contratoFPFacade.getEventoFPFacade().incidenciaPorIdentificador(eventoFP, IdentificadorBaseFP.BASE_PREVIDENCIA_GERAL_MENSAL, IdentificadorBaseFP.BASE_PREVIDENCIA_GERAL_13);
    }

    public Boolean incidenciaIRRF(EventoFP eventoFP) {
        return contratoFPFacade.getEventoFPFacade().incidenciaPorIdentificador(eventoFP, IdentificadorBaseFP.BASE_IRRF_13, IdentificadorBaseFP.BASE_IRRF_MENSAL);
    }

    public Boolean incidenciaRPPS(EventoFP eventoFP) {
        return contratoFPFacade.getEventoFPFacade().incidenciaPorIdentificador(eventoFP, IdentificadorBaseFP.PREVIDENCIA_PROPRIA_13, IdentificadorBaseFP.PREVIDENCIA_PROPRIA);
    }

    public List<PenalidadeFP> getPenalidades() {
        return penalidades;
    }

    public void setPenalidades(List<PenalidadeFP> penalidades) {
        this.penalidades = penalidades;
    }

    public String retornaCursoOrComissaoParaExperienciaExtraCurricular(ExperienciaExtraCurricular obj) {
        if (obj != null) {
            if (obj.isTipoCadastroComissao()) {
                return "Tipo: " + obj.getTipoComissao() + ", Atribuição: " + obj.getAtribuicaoComissao();
            } else {
                return "Curso: " + obj.getCurso() + ", Instituição: " + obj.getInstituicao() + ", Carga Horária: " + obj.getCargaHoraria();
            }
        }
        return " ";
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            validarContratoFp();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", cargoConfiancaFacade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO DE PESSOAS");
            dto.adicionarParametro("TITULO", "INFORMAÇÕES DO SERVIDOR");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("MES", objetoPesquisa.getMes().toString());
            dto.adicionarParametro("ANO", objetoPesquisa.getAno().toString());
            dto.adicionarParametro("TIPO_FOLHA", objetoPesquisa.getTipoFolhaDePagamentoWeb().getDescricao());
            dto.adicionarParametro("idVinculoFp", objetoPesquisa.getVinculoFP().getId());
            dto.adicionarParametro("idContratoFp", getrIdContratoFpDoVinculo(objetoPesquisa.getVinculoFP()));
            dto.adicionarParametro("idPessoa", objetoPesquisa.getVinculoFP().getMatriculaFP().getPessoa().getId());
            dto.setNomeRelatorio("SERVIDOR-INFO");
            dto.setApi("rh/servidor-info/");
            ReportService.getInstance().gerarRelatorio(cargoConfiancaFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarContratoFp() {
        ValidacaoException ve = new ValidacaoException();
        if (objetoPesquisa.getVinculoFP().getContratoFP() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Contrato está nulo para o vinculoFP: " + objetoPesquisa.getVinculoFP());
        }
        if (objetoPesquisa.getVinculoFP().getContratoFP().getMatriculaFP() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Matrícula está nula para o contrato: " + objetoPesquisa.getVinculoFP().getContratoFP());
        }
        if (objetoPesquisa.getVinculoFP().getContratoFP().getMatriculaFP().getPessoa() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Pessoa está nula para a matrícula: " + objetoPesquisa.getVinculoFP().getContratoFP().getMatriculaFP());
        }
        ve.lancarException();
    }

    private Long getrIdContratoFpDoVinculo(VinculoFP vinculoFP) {
        if (vinculoFP instanceof Pensionista) {
            return ((Pensionista) vinculoFP).getPensao().getContratoFP().getId();
        }
        if (vinculoFP instanceof Aposentadoria) {
            return ((Aposentadoria) vinculoFP).getContratoFP().getId();
        }
        return vinculoFP.getId();
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        if (objetoPesquisa.getMes() != null && objetoPesquisa.getAno() != null && objetoPesquisa.getTipoFolhaDePagamentoWeb() != null && objetoPesquisa.getVinculoFP() != null) {
            retorno.add(new SelectItem(null, "TODAS"));
            for (Integer versao : folhaDePagamentoFacade.recuperarVersaoWithIntevaloMes(Mes.getMesToInt(objetoPesquisa.getMes()), objetoPesquisa.getAno(), objetoPesquisa.getTipoFolhaDePagamentoWeb(), Mes.getMesToInt(objetoPesquisa.getMes()), objetoPesquisa.getVinculoFP())) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public List<CedenciaContratoFP> getCedencias() {
        return cedencias;
    }

    public void setCedencias(List<CedenciaContratoFP> cedencias) {
        this.cedencias = cedencias;
    }

    private void buscarMemoriaCalculo(List<ItemFichaFinanceiraFP> itens) {
        for (ItemFichaFinanceiraFP iten : itens) {
            mapMemoriaCalculo.put(iten.getEventoFP(), MemoriaCalculoRHDTO.toDto(fichaFinanceiraFPFacade.buscarMemoriasCalculoPorEvento(iten)));
        }
    }

    public List<MemoriaCalculoRHDTO> getMemoriaCalculo(EventoFP e) {
        if (mapMemoriaCalculo.containsKey(e)) {
            return mapMemoriaCalculo.get(e);
        }
        return null;
    }

    public List<ProvimentoReversao> getProvimentoReversoes() {
        return provimentoReversoes;
    }

    public void setProvimentoReversoes(List<ProvimentoReversao> provimentoReversoes) {
        this.provimentoReversoes = provimentoReversoes;
    }

}
