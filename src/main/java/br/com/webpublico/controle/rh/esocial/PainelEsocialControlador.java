package br.com.webpublico.controle.rh.esocial;

/**
 * Created by William on 21/06/2018.
 */

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.esocial.*;
import br.com.webpublico.entidades.rh.saudeservidor.CAT;
import br.com.webpublico.entidades.rh.saudeservidor.RiscoOcupacional;
import br.com.webpublico.entidadesauxiliares.rh.esocial.FiltroEventosEsocial;
import br.com.webpublico.entidadesauxiliares.rh.esocial.ReativacaoBeneficio;
import br.com.webpublico.entidadesauxiliares.rh.esocial.VWContratoFP;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.esocial.*;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.dto.OcorrenciaESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.esocial.*;
import br.com.webpublico.negocios.rh.saudeservidor.CATFacade;
import br.com.webpublico.negocios.rh.saudeservidor.RiscoOcupacionalFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ManagedBean(name = "painelEsocialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "consulta-ocorrencias-esocial", pattern = "/rh/e-social/consultar-ocorrencias-evento/", viewId = "/faces/rh/esocial/consultar-ocorrencias/visualizar.xhtml"),
    @URLMapping(id = "painel-esocial", pattern = "/rh/e-social/painel/", viewId = "/faces/rh/esocial/painel/visualizar.xhtml"),
})
public class PainelEsocialControlador {
    private static final Logger logger = LoggerFactory.getLogger(PainelEsocialControlador.class);

    private AssistenteBarraProgresso assistenteBarraProgresso;

    private Future<AssistenteBarraProgresso> future;

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private RegistroESocialFacade registroESocialFacade;
    @EJB
    private ASOFacade asoFacade;
    @EJB
    private RiscoOcupacionalFacade riscoOcupacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private RegistroS1200Facade registroS1200Facade;
    @EJB
    private Registro1202Facade registroS1202Facade;
    @EJB
    private RegistroS1207Facade registroS1207Facade;
    @EJB
    private RegistroS1210Facade registroS1210Facade;
    @EJB
    private ExclusaoEventoEsocialFacade exclusaoEventoEsocialFacade;
    @EJB
    private RegistroS1299Facade registroS1299Facade;
    @EJB
    private RegistroS1298Facade registroS1298Facade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private RegistroEventoEsocialS1299 registroEventoEsocialS1299;
    @EJB
    private PrestadorServicoAlteracaoFacade prestadorServicoAlteracaoFacade;

    @EJB
    private LogErroEnvioEventoFacade logErroEnvioEventoFacade;

    @EJB
    private PrestadorServicosFacade prestadorServicosFacade;

    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @EJB
    private CATFacade catFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    private TipoRegimePrevidenciario tipoRegimePrevidenciario;


    private TipoRegimePrevidenciario tipoRegimePrevidenciarioS1210;
    private PrestadorServicos prestadorServicos;
    private PrestadorServicos prestadorServicosS2306;
    private PrestadorServicos prestadorServicosS2399;

    private TipoTrabalhadorEsocial tipoTrabalhadorEsocial;
    private VinculoFP vinculoFP;
    private VinculoFP aposentado;
    private Entidade empregador;
    private ContratoFP contratoFP;
    private ContratoFP contratoFPS2206;
    private ContratoFP contratoFPS2230;
    private ASO aso;
    private VinculoFP beneficiario;
    private RiscoOcupacional riscoOcupacional;
    private ExoneracaoRescisao exoneracaoRescisao;
    private RegistroEventoEsocial registroEventoEsocialS1200;
    private RegistroEventoEsocial registroEventoEsocialS1202;
    private RegistroEventoEsocial registroEventoEsocialS1207;
    private RegistroEventoEsocial registroEventoEsocialS1210;
    private RegistroEventoEsocial registroEventoEsocialS1298;
    private List<PrestadorServicos> itemRegistroEventoEsocial2306;
    private RegistroExclusaoS3000 registroExclusaoS3000;
    private List<RegistroExclusaoS3000> itemExclusao;
    private List<Entidade> itemEmpregadorS1000;
    private List<Entidade> itemEmpregadors1005;
    private List<EventoFP> itemEventoFPS1010;
    private List<Entidade> itemEmpregadorS1020;
    private List<Cargo> itemCargoS1030;
    private List<Cargo> itemCargoS1035;
    private List<Cargo> itemCargoS1040;
    private List<HorarioDeTrabalho> itemHorarioTrabalhoS1050;
    private List<ProcessoAdministrativoJudicial> itemProcessoAdministrativoS1070;

    private List<IndicativoContribuicao> itemEventoS1280;

    private List<VWContratoFP> itemContratoFPS2190;
    private List<VWContratoFP> itemContratoFPS2205;
    private Map<ContratoFP, Date> mapItemContratoFPS2206;
    private List<VWContratoFP> itemContratoFPS2200;
    private VWContratoFP[] itemContratoFPS2200Selecionado;
    private Boolean marcarTodosContratos2200;
    private List<RiscoOcupacional> itemRiscoOcupacional;
    private List<ASO> itemContratoFPS2220;
    private List<ExoneracaoRescisao> itemExoneracaoS2299;
    private ExoneracaoRescisao[] itemExoneracaoS2299Selecionado;
    private Boolean marcarTodasExoneracoesS2299;
    private List<PrestadorServicos> itemPrestadorServico2300;

    private List<VinculoFP> itemAposentadoriaS2405;
    private List<VinculoFP> itemEventoS2400;
    private List<VinculoFP> itemEventoS2410;
    private List<ReativacaoBeneficio> itemReativacaoBeneficioS2418;
    private List<CedenciaContratoFP> itemCedenciaS2231;
    private CedenciaContratoFP cedenciaContratoFP;
    private Boolean marcarTodasCedenciaS2231;
    private Boolean marcarTodosExclusaoSS3000;
    private CedenciaContratoFP[] itemCedenciaS2231Selecionados;
    private RegistroExclusaoS3000[] itemExclusaoS3000Selecionados;
    private Date dataInicialS2231;
    private Date dataFinalS2231;
    private List<Afastamento> itemAfastanentoS2230;
    private List<VinculoFP> itemTermBeneficioS2420;

    private List<CAT> itemEventoS2210;

    private List<DetalheLogErroEnvioEvento> itemDetalheLogErro;
    private String xml;
    private String evento;
    private Integer activeIndex;
    private Date dataInicial;
    private Date dataFinal;

    private Date inicioAfastamento;
    private Date finalAfastamento;

    private Date dataInicialContrato;
    private Date dataFinalContrato;
    private Date dataInicialDesligamento;
    private Date dataFinalDesligamento;
    private Date dataInicioVigenciaS2400;
    private Date dataFinalVigenciaS2400;
    private Date dataInicioVigenciaS2410;
    private Date dataFinalVigenciaS2410;
    private List<EventoESocialDTO> eventos;
    private LazyDataModel<EventoESocialDTO> eventosTabela;
    private List<EventoESocialDTO> eventosPesquisa;
    private List<TipoArquivoESocial> tipoArquivoESocial;
    private List<OcorrenciaESocialDTO> ocorrencias;

    private Integer codigoRespostaFiltro;
    private String descricaoRespostaFiltro;
    private TipoOperacaoESocial tipoOperacaoFiltro;
    private String reciboEntrega;
    private SituacaoESocial situacaoESocial;
    private VinculoFP servidor;
    private String codigoEvento;

    private String codigoEventoFP;
    private Boolean apenasNaoEnviados;
    private FiltroEventosEsocial filtroEventosEsocial;
    private TipoCessao2231 tipoCessao2231;
    private Mes mesFiltro;
    private Exercicio exercicioFiltro;
    private Boolean decimoTerceiro;
    private PessoaFisica pessoaVinculo;
    private Date dataInicialContratoPrestador;
    private Date dataFinalContratoPrestador;

    private Date dataInicialContratoPrestadorS2306;
    private Date dataFinalContratoPrestadorS2306;
    private Date dataInicialContratoPrestadorS2399;
    private Date dataFinalContratoPrestadorS2399;

    private Date dataInicialContratoS2299;
    private Date dataFinalContratoS2299;

    private Boolean apenasVigentes = true;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais;
    private Date dataAlteracaoContrato;

    private CAT cat;

    private List<TodosEventosEsocial> todosEventosEsocial = Lists.newArrayList();

    private TipoOperacaoESocial tipoOperacaoESocialS2200;
    private TipoOperacaoESocial tipoOperacaoESocialS2299;
    private TipoOperacaoESocial tipoOperacaoESocialS2300;
    private TipoOperacaoESocial tipoOperacaoESocialS2399;
    private TipoOperacaoESocial tipoOperacaoESocialS2400;
    private TipoOperacaoESocial tipoOperacaoESocialS2410;
    private TipoOperacaoESocial tipoOperacaoESocialS1010;
    private List<LogErroEnvioEvento> itemLogErroEnvioEvento;
    private boolean eventoRemuneracao;
    private boolean mostrarDescricaoEvento;
    private List<VinculoFP> filtroS2400;
    private List<VinculoFP> filtroS2410;
    private int qtdeRegistrosParaEnvio = 0;

    private List<Future<AssistenteBarraProgresso>> futureAssistenteBarraProgresso;


    public Entidade getEmpregador() {
        return empregador;
    }

    public void setEmpregador(Entidade empregador) {
        this.empregador = empregador;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public List<EventoESocialDTO> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoESocialDTO> eventos) {
        this.eventos = eventos;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public PrestadorServicos getPrestadorServicos() {
        return prestadorServicos;
    }

    public void setPrestadorServicos(PrestadorServicos prestadorServicos) {
        this.prestadorServicos = prestadorServicos;
    }

    public Mes getMesFiltro() {
        return mesFiltro;
    }

    public void setMesFiltro(Mes mesFiltro) {
        this.mesFiltro = mesFiltro;
    }

    public Exercicio getExercicioFiltro() {
        return exercicioFiltro;
    }

    public boolean isEventoRemuneracao() {
        return eventoRemuneracao;
    }

    public void setEventoRemuneracao(boolean eventoRemuneracao) {
        this.eventoRemuneracao = eventoRemuneracao;
    }

    public boolean isMostrarDescricaoEvento() {
        return mostrarDescricaoEvento;
    }

    public void setMostrarDescricaoEvento(boolean mostrarDescricaoEvento) {
        this.mostrarDescricaoEvento = mostrarDescricaoEvento;
    }

    public void setExercicioFiltro(Exercicio exercicioFiltro) {
        this.exercicioFiltro = exercicioFiltro;
    }

    public Date getDataInicialContratoS2299() {
        return dataInicialContratoS2299;
    }

    public void setDataInicialContratoS2299(Date dataInicialContratoS2299) {
        this.dataInicialContratoS2299 = dataInicialContratoS2299;
    }

    public Date getDataFinalContratoS2299() {
        return dataFinalContratoS2299;
    }

    public void setDataFinalContratoS2299(Date dataFinalContratoS2299) {
        this.dataFinalContratoS2299 = dataFinalContratoS2299;
    }

    public Boolean getDecimoTerceiro() {
        return decimoTerceiro != null ? decimoTerceiro : false;
    }

    public void setDecimoTerceiro(Boolean decimoTerceiro) {
        this.decimoTerceiro = decimoTerceiro;
    }

    public List<IndicativoContribuicao> getItemEventoS1280() {
        return itemEventoS1280;
    }

    public void setItemEventoS1280(List<IndicativoContribuicao> itemEventoS1280) {
        this.itemEventoS1280 = itemEventoS1280;
    }

    public Date getDataInicialDesligamento() {
        return dataInicialDesligamento;
    }

    public void setDataInicialDesligamento(Date dataInicialDesligamento) {
        this.dataInicialDesligamento = dataInicialDesligamento;
    }

    public Date getDataFinalDesligamento() {
        return dataFinalDesligamento;
    }

    public void setDataFinalDesligamento(Date dataFinalDesligamento) {
        this.dataFinalDesligamento = dataFinalDesligamento;
    }

    public List<TodosEventosEsocial> getTodosEventosEsocial() {
        return todosEventosEsocial;
    }

    public void setTodosEventosEsocial(List<TodosEventosEsocial> todosEventosEsocial) {
        this.todosEventosEsocial = todosEventosEsocial;
    }

    public TipoRegimePrevidenciario getTipoRegimePrevidenciario() {
        return tipoRegimePrevidenciario;
    }

    public void setTipoRegimePrevidenciario(TipoRegimePrevidenciario tipoRegimePrevidenciario) {
        this.tipoRegimePrevidenciario = tipoRegimePrevidenciario;
    }

    public List<OcorrenciaESocialDTO> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(Set<OcorrenciaESocialDTO> ocorrencias) {
        this.ocorrencias = Lists.newArrayList(ocorrencias);
    }

    public ASO getAso() {
        return aso;
    }

    public void setAso(ASO aso) {
        this.aso = aso;
    }

    public List<VinculoFP> getItemEventoS2400() {
        return itemEventoS2400;
    }

    public void setItemEventoS2400(List<VinculoFP> itemEventoS2400) {
        this.itemEventoS2400 = itemEventoS2400;
    }

    public List<VinculoFP> getItemEventoS2410() {
        return itemEventoS2410;
    }

    public void setItemEventoS2410(List<VinculoFP> itemEventoS2410) {
        this.itemEventoS2410 = itemEventoS2410;
    }

    public FiltroEventosEsocial getFiltroEventosEsocial() {
        return filtroEventosEsocial;
    }

    public void setFiltroEventosEsocial(FiltroEventosEsocial filtroEventosEsocial) {
        this.filtroEventosEsocial = filtroEventosEsocial;
    }

    public List<PrestadorServicos> getItemRegistroEventoEsocial2306() {
        return itemRegistroEventoEsocial2306;
    }

    public void setItemRegistroEventoEsocial2306(List<PrestadorServicos> itemRegistroEventoEsocial2306) {
        this.itemRegistroEventoEsocial2306 = itemRegistroEventoEsocial2306;
    }

    public RegistroEventoEsocial getRegistroEventoEsocialS1298() {
        return registroEventoEsocialS1298;
    }

    public void setRegistroEventoEsocialS1298(RegistroEventoEsocial registroEventoEsocialS1298) {
        this.registroEventoEsocialS1298 = registroEventoEsocialS1298;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public void setarEvento(String evento) {
        this.evento = evento;
        definirEventoRemuneracao(evento);
    }

    private void definirEventoRemuneracao(String evento) {
        if (TipoArquivoESocial.S1200.name().equals(evento) || TipoArquivoESocial.S1202.name().equals(evento)
            || TipoArquivoESocial.S1207.name().equals(evento) || TipoArquivoESocial.S1210.name().equals(evento)) {
            eventoRemuneracao = true;
        }
        if (TipoArquivoESocial.S1010.name().equals(evento)) {
            mostrarDescricaoEvento = true;
        }
    }

    public void atualizarOcorrencias() {
        alterarTabs();
        setarEvento("");
    }

    public ExoneracaoRescisao getExoneracaoRescisao() {
        return exoneracaoRescisao;
    }

    public void setExoneracaoRescisao(ExoneracaoRescisao exoneracaoRescisao) {
        this.exoneracaoRescisao = exoneracaoRescisao;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getInicioAfastamento() {
        return inicioAfastamento;
    }

    public void setInicioAfastamento(Date inicioAfastamento) {
        this.inicioAfastamento = inicioAfastamento;
    }

    public Date getFinalAfastamento() {
        return finalAfastamento;
    }

    public void setFinalAfastamento(Date finalAfastamento) {
        this.finalAfastamento = finalAfastamento;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public ContratoFP getContratoFPS2206() {
        return contratoFPS2206;
    }

    public void setContratoFPS2206(ContratoFP contratoFPS2206) {
        this.contratoFPS2206 = contratoFPS2206;
    }

    public ContratoFP getContratoFPS2230() {
        return contratoFPS2230;
    }

    public void setContratoFPS2230(ContratoFP contratoFPS2230) {
        this.contratoFPS2230 = contratoFPS2230;
    }

    public List<TipoArquivoESocial> getTipoArquivoESocial() {
        return tipoArquivoESocial;
    }

    public void setTipoArquivoESocial(List<TipoArquivoESocial> tipoArquivoESocial) {
        this.tipoArquivoESocial = tipoArquivoESocial;
    }

    public List<SelectItem> buscarOperacoes() {
        return Util.getListSelectItem(TipoOperacaoESocial.values());
    }

    public List<SelectItem> getSituacoesEsocial() {
        return Util.getListSelectItem(SituacaoESocial.getSituacoesRelatorioInconsistencia());
    }

    public Integer getActiveIndex() {
        return activeIndex;
    }

    public List<HorarioDeTrabalho> getItemHorarioTrabalhoS1050() {
        return itemHorarioTrabalhoS1050;
    }

    public void setItemHorarioTrabalhoS1050(List<HorarioDeTrabalho> itemHorarioTrabalhoS1050) {
        this.itemHorarioTrabalhoS1050 = itemHorarioTrabalhoS1050;
    }

    public void setActiveIndex(Integer activeIndex) {
        this.activeIndex = activeIndex;
    }

    public RegistroEventoEsocialS1299 getRegistroEventoEsocialS1299() {
        return registroEventoEsocialS1299;
    }

    public void setRegistroEventoEsocialS1299(RegistroEventoEsocialS1299 registroEventoEsocialS1299) {
        this.registroEventoEsocialS1299 = registroEventoEsocialS1299;
    }

    public Integer getCodigoRespostaFiltro() {
        return codigoRespostaFiltro;
    }

    public void setCodigoRespostaFiltro(Integer codigoRespostaFiltro) {
        this.codigoRespostaFiltro = codigoRespostaFiltro;
    }

    public String getDescricaoRespostaFiltro() {
        return descricaoRespostaFiltro;
    }

    public void setDescricaoRespostaFiltro(String descricaoRespostaFiltro) {
        this.descricaoRespostaFiltro = descricaoRespostaFiltro;
    }

    public List<VWContratoFP> getItemContratoFPS2190() {
        return itemContratoFPS2190;
    }

    public void setItemContratoFPS2190(List<VWContratoFP> itemContratoFPS2190) {
        this.itemContratoFPS2190 = itemContratoFPS2190;
    }

    public List<ASO> getItemContratoFPS2220() {
        return itemContratoFPS2220;
    }

    public void setItemContratoFPS2220(List<ASO> itemContratoFPS2220) {
        this.itemContratoFPS2220 = itemContratoFPS2220;
    }

    public RegistroEventoEsocial getRegistroEventoEsocialS1200() {
        return registroEventoEsocialS1200;
    }

    public void setRegistroEventoEsocialS1200(RegistroEventoEsocial registroEventoEsocial) {
        registroEventoEsocialS1200 = registroEventoEsocial;
    }


    public RegistroExclusaoS3000 getRegistroExclusaoS3000() {
        return registroExclusaoS3000;
    }

    public void setRegistroExclusaoS3000(RegistroExclusaoS3000 registroExclusaoS3000) {
        this.registroExclusaoS3000 = registroExclusaoS3000;
    }

    public RegistroEventoEsocial getRegistroEventoEsocialS1202() {
        return registroEventoEsocialS1202;
    }

    public void setRegistroEventoEsocialS1202(RegistroEventoEsocial registroEventoEsocialS1202) {
        this.registroEventoEsocialS1202 = registroEventoEsocialS1202;
    }

    public RegistroEventoEsocial getRegistroEventoEsocialS1207() {
        return registroEventoEsocialS1207;
    }

    public void setRegistroEventoEsocialS1207(RegistroEventoEsocial registroEventoEsocialS1207) {
        this.registroEventoEsocialS1207 = registroEventoEsocialS1207;
    }

    public RegistroEventoEsocial getRegistroEventoEsocialS1210() {
        return registroEventoEsocialS1210;
    }

    public void setRegistroEventoEsocialS1210(RegistroEventoEsocial registroEventoEsocialS1210) {
        this.registroEventoEsocialS1210 = registroEventoEsocialS1210;
    }

    public TipoOperacaoESocial getTipoOperacaoFiltro() {
        return tipoOperacaoFiltro;
    }

    public void setTipoOperacaoFiltro(TipoOperacaoESocial tipoOperacaoFiltro) {
        this.tipoOperacaoFiltro = tipoOperacaoFiltro;
    }

    public String getReciboEntrega() {
        return reciboEntrega;
    }

    public void setReciboEntrega(String reciboEntrega) {
        this.reciboEntrega = reciboEntrega;
    }

    public TipoTrabalhadorEsocial getTipoTrabalhadorEsocial() {
        return tipoTrabalhadorEsocial;
    }

    public void setTipoTrabalhadorEsocial(TipoTrabalhadorEsocial tipoTrabalhadorEsocial) {
        this.tipoTrabalhadorEsocial = tipoTrabalhadorEsocial;
    }

    public List<RegistroExclusaoS3000> getItemExclusao() {
        return itemExclusao;
    }

    public void setItemExclusao(List<RegistroExclusaoS3000> itemExclusao) {
        this.itemExclusao = itemExclusao;
    }

    public SituacaoESocial getSituacaoESocial() {
        return situacaoESocial;
    }

    public void setSituacaoESocial(SituacaoESocial situacaoESocial) {
        this.situacaoESocial = situacaoESocial;
    }

    public VinculoFP getServidor() {
        return servidor;
    }

    public void setServidor(VinculoFP servidor) {
        this.servidor = servidor;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public List<EventoESocialDTO> getEventosPesquisa() {
        return eventosPesquisa;
    }

    public void setEventosPesquisa(List<EventoESocialDTO> eventosPesquisa) {
        this.eventosPesquisa = eventosPesquisa;
    }

    public LazyDataModel<EventoESocialDTO> getEventosTabela() {
        return eventosTabela;
    }

    public void setEventosTabela(LazyDataModel<EventoESocialDTO> eventosTabela) {
        this.eventosTabela = eventosTabela;
    }

    public void setOcorrencias(List<OcorrenciaESocialDTO> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    public Boolean getApenasNaoEnviados() {
        return apenasNaoEnviados;
    }

    public void setApenasNaoEnviados(Boolean apenasNaoEnviados) {
        this.apenasNaoEnviados = apenasNaoEnviados;
    }

    public List<Entidade> getItemEmpregadorS1000() {
        return itemEmpregadorS1000;
    }

    public void setItemEmpregadorS1000(List<Entidade> itemEmpregadorS1000) {
        this.itemEmpregadorS1000 = itemEmpregadorS1000;
    }

    public List<Entidade> getItemEmpregadors1005() {
        return itemEmpregadors1005;
    }

    public void setItemEmpregadors1005(List<Entidade> itemEmpregadors1005) {
        this.itemEmpregadors1005 = itemEmpregadors1005;
    }

    public List<EventoFP> getItemEventoFPS1010() {
        return itemEventoFPS1010;
    }

    public void setItemEventoFPS1010(List<EventoFP> itemEventoFPS1010) {
        this.itemEventoFPS1010 = itemEventoFPS1010;
    }

    public List<Entidade> getItemEmpregadorS1020() {
        return itemEmpregadorS1020;
    }

    public void setItemEmpregadorS1020(List<Entidade> itemEmpregadorS1020) {
        this.itemEmpregadorS1020 = itemEmpregadorS1020;
    }

    public List<ProcessoAdministrativoJudicial> getItemProcessoAdministrativoS1070() {
        return itemProcessoAdministrativoS1070;
    }

    public void setItemProcessoAdministrativoS1070(List<ProcessoAdministrativoJudicial> itemProcessoAdministrativoS1070) {
        this.itemProcessoAdministrativoS1070 = itemProcessoAdministrativoS1070;
    }

    public List<Afastamento> getItemAfastanentoS2230() {
        return itemAfastanentoS2230;
    }

    public void setItemAfastanentoS2230(List<Afastamento> itemAfastanentoS2230) {
        this.itemAfastanentoS2230 = itemAfastanentoS2230;
    }

    public List<Cargo> getItemCargoS1030() {
        return itemCargoS1030;
    }

    public void setItemCargoS1030(List<Cargo> itemCargoS1030) {
        this.itemCargoS1030 = itemCargoS1030;
    }

    public List<Cargo> getItemCargoS1035() {
        return itemCargoS1035;
    }

    public void setItemCargoS1035(List<Cargo> itemCargoS1035) {
        this.itemCargoS1035 = itemCargoS1035;
    }

    public List<Cargo> getItemCargoS1040() {
        return itemCargoS1040;
    }

    public void setItemCargoS1040(List<Cargo> itemCargoS1040) {
        this.itemCargoS1040 = itemCargoS1040;
    }

    public List<VWContratoFP> getItemContratoFPS2205() {
        return itemContratoFPS2205;
    }

    public void setItemContratoFPS2205(List<VWContratoFP> itemContratoFPS2205) {
        this.itemContratoFPS2205 = itemContratoFPS2205;
    }

    public Map<ContratoFP, Date> getMapItemContratoFPS2206() {
        return mapItemContratoFPS2206;
    }

    public void setMapItemContratoFPS2206(Map<ContratoFP, Date> mapItemContratoFPS2206) {
        this.mapItemContratoFPS2206 = mapItemContratoFPS2206;
    }

    public Date getDataAlteracaoContrato() {
        return dataAlteracaoContrato;
    }

    public void setDataAlteracaoContrato(Date dataAlteracaoContrato) {
        this.dataAlteracaoContrato = dataAlteracaoContrato;
    }

    public List<ExoneracaoRescisao> getItemExoneracaoS2299() {
        return itemExoneracaoS2299;
    }

    public void setItemExoneracaoS2299(List<ExoneracaoRescisao> itemExoneracaoS2299) {
        this.itemExoneracaoS2299 = itemExoneracaoS2299;
    }

    public List<PrestadorServicos> getItemPrestadorServico2300() {
        return itemPrestadorServico2300;
    }

    public void setItemPrestadorServico2300(List<PrestadorServicos> itemPrestadorServico2300) {
        this.itemPrestadorServico2300 = itemPrestadorServico2300;
    }

    public List<CedenciaContratoFP> getItemCedenciaS2231() {
        return itemCedenciaS2231;
    }

    public void setItemCedenciaS2231(List<CedenciaContratoFP> itemCedenciaS2231) {
        this.itemCedenciaS2231 = itemCedenciaS2231;
    }

    public CedenciaContratoFP getCedenciaContratoFP() {
        return cedenciaContratoFP;
    }

    public void setCedenciaContratoFP(CedenciaContratoFP cedenciaContratoFP) {
        this.cedenciaContratoFP = cedenciaContratoFP;
    }

    public Boolean getMarcarTodasCedenciaS2231() {
        return marcarTodasCedenciaS2231;
    }

    public void setMarcarTodasCedenciaS2231(Boolean marcarTodasCedenciaS2231) {
        this.marcarTodasCedenciaS2231 = marcarTodasCedenciaS2231;
    }

    public Boolean getMarcarTodosExclusaoSS3000() {
        return marcarTodosExclusaoSS3000;
    }

    public void setMarcarTodosExclusaoSS3000(Boolean marcarTodosExclusaoSS3000) {
        this.marcarTodosExclusaoSS3000 = marcarTodosExclusaoSS3000;
    }

    public CedenciaContratoFP[] getItemCedenciaS2231Selecionados() {
        return itemCedenciaS2231Selecionados;
    }

    public void setItemCedenciaS2231Selecionados(CedenciaContratoFP[] itemCedenciaS2231Selecionados) {
        this.itemCedenciaS2231Selecionados = itemCedenciaS2231Selecionados;
    }

    public RegistroExclusaoS3000[] getItemExclusaoS3000Selecionados() {
        return itemExclusaoS3000Selecionados;
    }

    public void setItemExclusaoS3000Selecionados(RegistroExclusaoS3000[] itemExclusaoS3000Selecionados) {
        this.itemExclusaoS3000Selecionados = itemExclusaoS3000Selecionados;
    }

    public Date getDataInicialS2231() {
        return dataInicialS2231;
    }

    public void setDataInicialS2231(Date dataInicialS2231) {
        this.dataInicialS2231 = dataInicialS2231;
    }

    public Date getDataFinalS2231() {
        return dataFinalS2231;
    }

    public void setDataFinalS2231(Date dataFinalS2231) {
        this.dataFinalS2231 = dataFinalS2231;
    }

    public TipoCessao2231 getTipoCessao2231() {
        return tipoCessao2231;
    }

    public void setTipoCessao2231(TipoCessao2231 tipoCessao2231) {
        this.tipoCessao2231 = tipoCessao2231;
    }

    public List<VWContratoFP> getItemContratoFPS2200() {
        return itemContratoFPS2200;
    }

    public void setItemContratoFPS2200(List<VWContratoFP> itemContratoFPS2200) {
        this.itemContratoFPS2200 = itemContratoFPS2200;
    }

    public VWContratoFP[] getItemContratoFPS2200Selecionado() {
        return itemContratoFPS2200Selecionado;
    }

    public void setItemContratoFPS2200Selecionado(VWContratoFP[] itemContratoFPS2200Selecionado) {
        this.itemContratoFPS2200Selecionado = itemContratoFPS2200Selecionado;
    }

    public Boolean getMarcarTodosContratos2200() {
        return marcarTodosContratos2200;
    }

    public void setMarcarTodosContratos2200(Boolean marcarTodosContratos2200) {
        this.marcarTodosContratos2200 = marcarTodosContratos2200;
    }

    public ExoneracaoRescisao[] getItemExoneracaoS2299Selecionado() {
        return itemExoneracaoS2299Selecionado;
    }

    public void setItemExoneracaoS2299Selecionado(ExoneracaoRescisao[] itemExoneracaoS2299Selecionado) {
        this.itemExoneracaoS2299Selecionado = itemExoneracaoS2299Selecionado;
    }

    public Boolean getMarcarTodasExoneracoesS2299() {
        return marcarTodasExoneracoesS2299;
    }

    public void setMarcarTodasExoneracoesS2299(Boolean marcarTodasExoneracoesS2299) {
        this.marcarTodasExoneracoesS2299 = marcarTodasExoneracoesS2299;
    }

    public List<ReativacaoBeneficio> getItemReativacaoBeneficioS2418() {
        return itemReativacaoBeneficioS2418;
    }

    public void setItemReativacaoBeneficioS2418(List<ReativacaoBeneficio> itemReativacaoBeneficioS2418) {
        this.itemReativacaoBeneficioS2418 = itemReativacaoBeneficioS2418;
    }

    public List<VinculoFP> getItemTermBeneficioS2420() {
        return itemTermBeneficioS2420;
    }

    public void setItemTermBeneficioS2420(List<VinculoFP> itemTermBeneficioS2420) {
        this.itemTermBeneficioS2420 = itemTermBeneficioS2420;
    }

    public List<RiscoOcupacional> getItemRiscoOcupacional() {
        return itemRiscoOcupacional;
    }

    public void setItemRiscoOcupacional(List<RiscoOcupacional> itemRiscoOcupacional) {
        this.itemRiscoOcupacional = itemRiscoOcupacional;
    }

    public VinculoFP getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(VinculoFP beneficiario) {
        this.beneficiario = beneficiario;
    }

    public RiscoOcupacional getRiscoOcupacional() {
        return riscoOcupacional;
    }

    public void setRiscoOcupacional(RiscoOcupacional riscoOcupacional) {
        this.riscoOcupacional = riscoOcupacional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionais() {
        return hierarquiasOrganizacionais;
    }

    public void setHierarquiasOrganizacionais(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        this.hierarquiasOrganizacionais = hierarquiasOrganizacionais;
    }

    public CAT getCat() {
        return cat;
    }

    public void setCat(CAT cat) {
        this.cat = cat;
    }

    public TipoOperacaoESocial getTipoOperacaoESocialS2200() {
        return tipoOperacaoESocialS2200;
    }

    public void setTipoOperacaoESocialS2200(TipoOperacaoESocial tipoOperacaoESocialS2200) {
        this.tipoOperacaoESocialS2200 = tipoOperacaoESocialS2200;
    }

    public TipoOperacaoESocial getTipoOperacaoESocialS2299() {
        return tipoOperacaoESocialS2299;
    }

    public void setTipoOperacaoESocialS2299(TipoOperacaoESocial tipoOperacaoESocialS2299) {
        this.tipoOperacaoESocialS2299 = tipoOperacaoESocialS2299;
    }

    public TipoOperacaoESocial getTipoOperacaoESocialS2300() {
        return tipoOperacaoESocialS2300;
    }

    public void setTipoOperacaoESocialS2300(TipoOperacaoESocial tipoOperacaoESocialS2300) {
        this.tipoOperacaoESocialS2300 = tipoOperacaoESocialS2300;
    }

    public TipoOperacaoESocial getTipoOperacaoESocialS1010() {
        return tipoOperacaoESocialS1010;
    }

    public void setTipoOperacaoESocialS1010(TipoOperacaoESocial tipoOperacaoESocialS1010) {
        this.tipoOperacaoESocialS1010 = tipoOperacaoESocialS1010;
    }

    public TipoOperacaoESocial getTipoOperacaoESocialS2399() {
        return tipoOperacaoESocialS2399;
    }

    public void setTipoOperacaoESocialS2399(TipoOperacaoESocial tipoOperacaoESocialS2399) {
        this.tipoOperacaoESocialS2399 = tipoOperacaoESocialS2399;
    }

    public TipoOperacaoESocial getTipoOperacaoESocialS2400() {
        return tipoOperacaoESocialS2400;
    }

    public void setTipoOperacaoESocialS2400(TipoOperacaoESocial tipoOperacaoESocialS2400) {
        this.tipoOperacaoESocialS2400 = tipoOperacaoESocialS2400;
    }

    public TipoOperacaoESocial getTipoOperacaoESocialS2410() {
        return tipoOperacaoESocialS2410;
    }

    public void setTipoOperacaoESocialS2410(TipoOperacaoESocial tipoOperacaoESocialS2410) {
        this.tipoOperacaoESocialS2410 = tipoOperacaoESocialS2410;
    }

    @URLAction(mappingId = "consulta-ocorrencias-esocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        inicializarAtributos();
    }

    @URLAction(mappingId = "painel-esocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoPainel() {
        inicializarAtributos();
    }

    public List<VinculoFP> getItemAposentadoriaS2405() {
        return itemAposentadoriaS2405;
    }

    public void setItemAposentadoriaS2405(List<VinculoFP> itemAposentadoriaS2405) {
        this.itemAposentadoriaS2405 = itemAposentadoriaS2405;
    }

    public String getCodigoEventoFP() {
        return codigoEventoFP;
    }

    public void setCodigoEventoFP(String codigoEventoFP) {
        this.codigoEventoFP = codigoEventoFP;
    }

    public List<DetalheLogErroEnvioEvento> getItemDetalheLogErro() {
        return itemDetalheLogErro;
    }

    public void setItemDetalheLogErro(List<DetalheLogErroEnvioEvento> itemDetalheLogErro) {
        this.itemDetalheLogErro = itemDetalheLogErro;
    }

    public List<LogErroEnvioEvento> getItemLogErroEnvioEvento() {
        return itemLogErroEnvioEvento;
    }

    public void setItemLogErroEnvioEvento(List<LogErroEnvioEvento> itemLogErroEnvioEvento) {
        this.itemLogErroEnvioEvento = itemLogErroEnvioEvento;
    }

    public Boolean getApenasVigentes() {
        return apenasVigentes != null ? apenasVigentes : false;
    }

    public void setApenasVigentes(Boolean apenasVigentes) {
        this.apenasVigentes = apenasVigentes;
    }

    public List<CAT> getItemEventoS2210() {
        return itemEventoS2210;
    }

    public void setItemEventoS2210(List<CAT> itemEventoS2210) {
        this.itemEventoS2210 = itemEventoS2210;
    }

    public List<VinculoFP> getFiltroS2400() {
        return filtroS2400;
    }

    public void setFiltroS2400(List<VinculoFP> filtroS2400) {
        this.filtroS2400 = filtroS2400;
    }

    public List<VinculoFP> getFiltroS2410() {
        return filtroS2410;
    }

    public void setFiltroS2410(List<VinculoFP> filtroS2410) {
        this.filtroS2410 = filtroS2410;
    }


    private void inicializarAtributos() {
        filtroEventosEsocial = new FiltroEventosEsocial();
        eventos = Lists.newArrayList();
        eventosPesquisa = Lists.newArrayList();
        tipoArquivoESocial = Lists.newArrayList();
        registroEventoEsocialS1200 = new RegistroEventoEsocial();
        registroEventoEsocialS1202 = new RegistroEventoEsocial();
        registroEventoEsocialS1207 = new RegistroEventoEsocial();
        registroEventoEsocialS1210 = new RegistroEventoEsocial();
        Collections.addAll(tipoArquivoESocial, TipoArquivoESocial.values());
        eventosTabela = new LazyEventosDataModel(new ArrayList<EventoESocialDTO>());
        registroExclusaoS3000 = new RegistroExclusaoS3000();
        registroEventoEsocialS1299 = new RegistroEventoEsocialS1299();
        registroEventoEsocialS1298 = new RegistroEventoEsocial();
        itemRegistroEventoEsocial2306 = Lists.newArrayList();
        itemEmpregadorS1000 = Lists.newArrayList();
        itemEmpregadors1005 = Lists.newArrayList();
        itemEventoFPS1010 = Lists.newArrayList();
        itemEmpregadorS1020 = Lists.newArrayList();
        itemCargoS1030 = Lists.newArrayList();
        itemCargoS1035 = Lists.newArrayList();
        itemCargoS1040 = Lists.newArrayList();
        itemHorarioTrabalhoS1050 = Lists.newArrayList();
        itemProcessoAdministrativoS1070 = Lists.newArrayList();
        itemEventoS1280 = Lists.newArrayList();
        itemContratoFPS2190 = Lists.newArrayList();
        itemContratoFPS2200 = Lists.newArrayList();
        itemAposentadoriaS2405 = Lists.newArrayList();
        itemContratoFPS2205 = Lists.newArrayList();
        mapItemContratoFPS2206 = new HashMap<>();
        itemContratoFPS2220 = Lists.newArrayList();
        itemExoneracaoS2299 = Lists.newArrayList();
        itemPrestadorServico2300 = Lists.newArrayList();
        itemEventoS2400 = Lists.newArrayList();
        itemEventoS2410 = Lists.newArrayList();
        itemCedenciaS2231 = Lists.newArrayList();
        itemTermBeneficioS2420 = Lists.newArrayList();
        itemRiscoOcupacional = Lists.newArrayList();
        itemExclusao = Lists.newArrayList();
        itemReativacaoBeneficioS2418 = Lists.newArrayList();
        itemEventoS2210 = Lists.newArrayList();
        itemDetalheLogErro = Lists.newArrayList();
        apenasNaoEnviados = Boolean.TRUE;
        dataInicial = DataUtil.getPrimeiroDiaAno(sistemaFacade.getExercicioCorrente().getAno());
        dataFinal = DataUtil.getUltimoDiaAno(sistemaFacade.getExercicioCorrente().getAno());
        inicioAfastamento = DataUtil.getPrimeiroDiaAno(sistemaFacade.getExercicioCorrente().getAno());
        finalAfastamento = DataUtil.getUltimoDiaAno(sistemaFacade.getExercicioCorrente().getAno());
        dataInicioVigenciaS2410 = DataUtil.getPrimeiroDiaAno(sistemaFacade.getExercicioCorrente().getAno());
        dataFinalVigenciaS2410 = DataUtil.getUltimoDiaAno(sistemaFacade.getExercicioCorrente().getAno());
        tipoCessao2231 = TipoCessao2231.INI_CESSAO;
        hierarquiasOrganizacionais = Lists.newArrayList();
        itemAfastanentoS2230 = Lists.newArrayList();
        eventoRemuneracao = false;
        LocalDate dataOperacao = sistemaFacade.getDataOperacao().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        dataInicialDesligamento = DataUtil.primeiroDiaMes(dataOperacao);
        dataFinalDesligamento = DataUtil.ultimoDiaMes(dataOperacao);
    }

    public Date getDataInicialContrato() {
        return dataInicialContrato;
    }

    public void setDataInicialContrato(Date dataInicialContrato) {
        this.dataInicialContrato = dataInicialContrato;
    }

    public Date getDataFinalContrato() {
        return dataFinalContrato;
    }

    public Date getDataInicioVigenciaS2400() {
        return dataInicioVigenciaS2400;
    }

    public void setDataInicioVigenciaS2400(Date dataInicioVigenciaS2400) {
        this.dataInicioVigenciaS2400 = dataInicioVigenciaS2400;
    }

    public Date getDataFinalVigenciaS2400() {
        return dataFinalVigenciaS2400;
    }

    public void setDataFinalVigenciaS2400(Date dataFinalVigenciaS2400) {
        this.dataFinalVigenciaS2400 = dataFinalVigenciaS2400;
    }

    public Date getDataInicioVigenciaS2410() {
        return dataInicioVigenciaS2410;
    }

    public void setDataInicioVigenciaS2410(Date dataInicioVigenciaS2410) {
        this.dataInicioVigenciaS2410 = dataInicioVigenciaS2410;
    }

    public Date getDataFinalVigenciaS2410() {
        return dataFinalVigenciaS2410;
    }

    public void setDataFinalVigenciaS2410(Date dataFinalVigenciaS2410) {
        this.dataFinalVigenciaS2410 = dataFinalVigenciaS2410;
    }

    public void setDataFinalContrato(Date dataFinalContrato) {
        this.dataFinalContrato = dataFinalContrato;
    }

    public List<SelectItem> getEmpregadores() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        List<ConfiguracaoEmpregadorESocial> empregadores = Lists.newArrayList();
        List<HierarquiaOrganizacional> hoUsuario = configuracaoEmpregadorESocialFacade.getHierarquiaOrganizacionalFacade().buscarHierarquiaUsuarioPorTipo(
            sistemaFacade.getUsuarioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, 1, 2);

        if (apenasVigentes) {
            empregadores = configuracaoEmpregadorESocialFacade.recuperarConfiguracaoEmpregadorVigente();
        } else {
            empregadores = configuracaoEmpregadorESocialFacade.lista();
        }

        for (ConfiguracaoEmpregadorESocial empregador : empregadores) {
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hoUsuario) {
                try {
                    if (Objects.equals(empregador.getEntidade().getId(), hierarquiaOrganizacional.getSubordinada().getEntidade().getId())) {
                        toReturn.add(new SelectItem(empregador.getEntidade(), empregador.getEntidade() + " "));
                    }
                } catch (Exception e) {
//                    logger.debug("Não encontrado entidade empregador " + empregador.getEntidade() + " entidade usuario " +
//                        hierarquiaOrganizacional.getSubordinada().getEntidade());
                }

            }
        }
        return toReturn;
    }

    private void validarEmpregador() {
        ValidacaoException ve = new ValidacaoException();
        if (itemEmpregadorS1000.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione o empregador para envio do evento.");
        }
        ve.lancarException();
    }

    private void validarEnvioEvento(List lista) {
        ValidacaoException ve = new ValidacaoException();
        if (lista.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Não foi encontrado Evento para envio do e-social.");
        }
        ve.lancarException();
    }

    private void validarBuscaEmpregador() {
        ValidacaoException ve = new ValidacaoException();
        if (empregador == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Empregador deve ser informado.");
        }
        ve.lancarException();
    }

    public void buscarEventos() {
        try {
            validarBuscaEmpregador();
            eventos = configuracaoEmpregadorESocialFacade.getEventosEmpregadorAndTipoArquivo(empregador, TipoArquivoESocial.S1200, 1, 1, null);
            if (eventos == null) {
                FacesUtil.addOperacaoNaoRealizada("Não foi encontrado evento para o empregador.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao buscar eventos: ", rax.getMessage());
        } catch (Exception e) {
            FacesUtil.addError("Erro ao buscar eventos: ", e.toString());
        }
    }

    public void alterarTabs() {
        inicializarFiltros();
        if (empregador != null && empregador.getId() != null) {
        } else {
            FacesUtil.addOperacaoNaoRealizada("Selecione o empregador para consultar o arquivo do E-social.");
        }
    }

    public RegistroEventoEsocial getRegistroEventoEsocialPorTipo(TipoArquivoESocial tipoEvento) {
        if (TipoArquivoESocial.S1200.equals(tipoEvento)) {
            return registroEventoEsocialS1200;
        }
        if (TipoArquivoESocial.S1207.equals(tipoEvento)) {
            return registroEventoEsocialS1207;
        }
        if (TipoArquivoESocial.S1210.equals(tipoEvento)) {
            return registroEventoEsocialS1210;
        }
        if (TipoArquivoESocial.S1298.equals(tipoEvento)) {
            return registroEventoEsocialS1298;
        }
        if (TipoArquivoESocial.S1202.equals(tipoEvento)) {
            return registroEventoEsocialS1202;
        }


        return new RegistroEventoEsocial();
    }

    public VinculoFP getAposentado() {
        return aposentado;
    }

    public void setAposentado(VinculoFP aposentado) {
        this.aposentado = aposentado;
    }

    private void filtrarEventos(List<EventoESocialDTO> eventosPesquisados) {
        eventos = Lists.newArrayList();
        if (!eventosPesquisados.isEmpty()) {
            eventos.addAll(eventosPesquisados);
        }
    }

    private void inicializarFiltros() {
        codigoRespostaFiltro = null;
        descricaoRespostaFiltro = null;
        tipoOperacaoFiltro = null;
        reciboEntrega = null;
        situacaoESocial = null;
        servidor = null;
        codigoEvento = null;
    }

    public boolean hasCodigoEventoFP() {
        if (eventos != null && !eventos.isEmpty() && eventos.get(0).getTipoArquivo() != null) {
            return TipoArquivoESocial.S1010.equals(eventos.get(0).getTipoArquivo());
        }
        return false;
    }

    public boolean hasEventosLista() {
        return eventosTabela != null || !eventosPesquisa.isEmpty();
    }

    public class LazyEventosDataModel extends LazyDataModel<EventoESocialDTO> {

        private List<EventoESocialDTO> datasource;

        public LazyEventosDataModel(List<EventoESocialDTO> datasource) {
            this.datasource = datasource;
        }

        @Override
        public EventoESocialDTO getRowData(String rowKey) {
            for (EventoESocialDTO evento : datasource) {
                if (evento.toString().equals(rowKey)) {
                    return evento;
                }
            }
            return null;
        }

        @Override
        public Object getRowKey(EventoESocialDTO evento) {
            return evento;
        }

        @Override
        public List<EventoESocialDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
            try {
                List<EventoESocialDTO> data = new ArrayList<>();

                DataTable dataTable = (DataTable)
                    FacesContext
                        .getCurrentInstance()
                        .getViewRoot()
                        .findComponent("Formulario:eventos");

                Integer dataSize = 0;
                if (dataTable == null) {
                    dataTable = (DataTable)
                        FacesContext
                            .getCurrentInstance()
                            .getViewRoot()
                            .findComponent("Formulario:tabViewEnvioEventos:eventos");
                }

                if (filtroEventosEsocial.isFiltroPreenchido()) {
                    try {
                        validarMesAno();
                        filtroEventosEsocial.setEmpregador(empregador);
                        filtroEventosEsocial.setSituacaoESocial(situacaoESocial);
                        data = configuracaoEmpregadorESocialFacade.getEventosPorXML(first, pageSize, filtroEventosEsocial, TipoArquivoESocial.valueOf(evento));
                        int rowCount = configuracaoEmpregadorESocialFacade.contarEventosPorXML(filtroEventosEsocial, TipoArquivoESocial.valueOf(evento));
                        setRowCount(rowCount);

                        if (data != null && eventoRemuneracao) {
                            data.forEach(evento -> {
                                evento.setDescricao(getVinculoFPOrPrestadorEventoPagamento(evento.getIdentificadorWP()));
                            });
                        }
                        return data;
                    } catch (ValidacaoException ve) {
                        FacesUtil.printAllFacesMessages(ve.getMensagens());
                    }

                }

                if (!Strings.isNullOrEmpty(evento)) {
                    data = configuracaoEmpregadorESocialFacade.getEventosEmpregadorAndTipoArquivo(empregador, TipoArquivoESocial.valueOf(evento), dataTable.getPage(), pageSize, situacaoESocial);
                    dataSize = configuracaoEmpregadorESocialFacade.getQuantidadeEventosPorEmpregadorAndTipoArquivo(empregador, TipoArquivoESocial.valueOf(evento), situacaoESocial);
                } else if (activeIndex != null) {
                    data = configuracaoEmpregadorESocialFacade.getEventosEmpregadorAndTipoArquivo(empregador, TipoArquivoESocial.values()[activeIndex], dataTable.getPage(), pageSize, situacaoESocial);
                    dataSize = configuracaoEmpregadorESocialFacade.getQuantidadeEventosPorEmpregadorAndTipoArquivo(empregador, TipoArquivoESocial.values()[activeIndex], situacaoESocial);
                }
                setRowCount(dataSize);
                return data;
            } catch (Exception e) {
                FacesUtil.addMessageError("Evento não localizado!", "Não foi possível localizar os eventos para o empregador;");

            }
            return null;
        }
    }

    public void validarMesAno() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroEventosEsocial.getMes() != null && filtroEventosEsocial.getExercicio() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para filtrar indicando o mês deve ser indicado o Ano.");
        }
        if (filtroEventosEsocial.getMes() == null && filtroEventosEsocial.getExercicio() != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para filtrar indicando o Ano deve ser indicado o Mês.");
        }
        ve.lancarException();
    }

    public void enviarS1000() {
        try {
            validarEmpregador();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            configuracaoEmpregadorESocialFacade.enviarS1000(config);

            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(Lists.newArrayList(config),
                config, TipoClasseESocial.S1000, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.CONFIGURACAOEMPREGADORESOCIAL, null, null);

            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao Enviar o registro " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Empregador não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void enviarS1005() {
        try {
            validarEnvioEvento(itemEmpregadors1005);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            configuracaoEmpregadorESocialFacade.enviarS1005(config);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");

            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemEmpregadors1005,
                config, TipoClasseESocial.S1005, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.ENTIDADE, null, null);

        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }


    public void enviarS1050() {
        try {
            validarEnvioEvento(itemHorarioTrabalhoS1050);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            configuracaoEmpregadorESocialFacade.enviarS1050(config, itemHorarioTrabalhoS1050);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void enviarS1070() {
        try {
            validarEnvioEvento(itemProcessoAdministrativoS1070);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            configuracaoEmpregadorESocialFacade.enviarS1070(config, itemProcessoAdministrativoS1070);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void enviarS2190() {
        try {
            validarEnvioEvento(itemContratoFPS2190);
            itemDetalheLogErro.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            configuracaoEmpregadorESocialFacade.enviarlS2190(config, itemContratoFPS2190, itemDetalheLogErro);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");

            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemContratoFPS2190,
                config, TipoClasseESocial.S2190, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.VINCULOFP, null, null);
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void buscarS1000() {
        if (empregador != null) {
            itemEmpregadorS1000.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            if (apenasNaoEnviados && registroESocialFacade.hasEventoEnviado(TipoArquivoESocial.S1000, null, config)) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemEmpregadorS1000.add(empregador);
            }
        }
    }

    public void enviarS1010() {
        try {
            validarEnvioEvento(itemEventoFPS1010);
            itemDetalheLogErro.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            configuracaoEmpregadorESocialFacade.enviarS1010(config, itemEventoFPS1010, itemDetalheLogErro);

            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemEventoFPS1010,
                config, TipoClasseESocial.S1010, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.EVENTOFP, null, null);

            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");

        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void enviarS1020() {
        try {
            validarEnvioEvento(itemEmpregadorS1020);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            configuracaoEmpregadorESocialFacade.enviarS1020(config, empregador);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");

            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemEmpregadorS1020,
                config, TipoClasseESocial.S1020, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.ENTIDADE, null, null);
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado! ", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    private void validarRegistrosAdicionadosS2200(VWContratoFP[] vinculo) {
        ValidacaoException ve = new ValidacaoException();
        if (vinculo == null || vinculo.length == 0) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum registro selecionado para envio!");
        }
        ve.lancarException();
    }

    public void enviarS2200() {
        try {
            validarRegistrosAdicionadosS2200(itemContratoFPS2200Selecionado);
            validarEnvioEvento(Lists.newArrayList(itemContratoFPS2200Selecionado));
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            ArrayList<VWContratoFP> contratos = Lists.newArrayList(itemContratoFPS2200Selecionado);
            contratos.forEach(contrato -> {
                contrato.setTipoOperacaoESocial(tipoOperacaoESocialS2200);
            });
            itemDetalheLogErro.clear();
            future = configuracaoEmpregadorESocialFacade.enviarS2200(config, contratos, assistenteBarraProgresso,
                sistemaFacade.getDataOperacao(), itemDetalheLogErro);
            FacesUtil.executaJavaScript("acompanhaEnvioEvento()");
            FacesUtil.executaJavaScript("aguarde.show()");

            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(contratos,
                config, TipoClasseESocial.S2200, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.VINCULOFP, null, null);

        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Empregador não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public String getLogComLink(String texto) {
        if (texto.contains("/pessoa")) {
            int posicaoLink = texto.indexOf("/pessoa");
            String textoSemLink = texto.substring(0, posicaoLink);
            String link = texto.substring(posicaoLink);
            return textoSemLink + "<b>Sugestão: " + Util.linkBlack(link, "Clique aqui para efetuar a correção.") + "</b>";
        }
        return texto;
    }

    public void verificarSeTerminou() {
        if (future != null && future.isDone()) {
            if (assistenteBarraProgresso.getMensagensValidacaoFacesUtil() != null && !assistenteBarraProgresso.getMensagensValidacaoFacesUtil().isEmpty()) {
                for (FacesMessage facesMessage : assistenteBarraProgresso.getMensagensValidacaoFacesUtil()) {
                    FacesUtil.addWarn("Atenção", facesMessage.getDetail());
                }
            } else {
                FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
            }
            future = null;
            FacesUtil.executaJavaScript("terminaEnvio()");
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
            FacesUtil.executaJavaScript("dialogo.hide()");
        }
    }

    public void enviarS2405() {
        try {
            validarEnvioEvento(itemAposentadoriaS2405);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            itemDetalheLogErro.clear();
            configuracaoEmpregadorESocialFacade.enviarS2405(config, itemAposentadoriaS2405, itemDetalheLogErro);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");

            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemAposentadoriaS2405,
                config, TipoClasseESocial.S2405, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.VINCULOFP, null, null);
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void enviarS2230() {
        try {
            validarEnvioEvento(itemAfastanentoS2230);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            itemDetalheLogErro.clear();
            configuracaoEmpregadorESocialFacade.enviarS2230(config, itemAfastanentoS2230, itemDetalheLogErro);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");

            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemAfastanentoS2230,
                config, TipoClasseESocial.S2230, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.AFASTAMENTO, null, null);
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }


    private void validarRegistrosAdicionadosS2231(CedenciaContratoFP[] cedenciaContratoFPS) {
        ValidacaoException ve = new ValidacaoException();
        if (cedenciaContratoFPS == null || cedenciaContratoFPS.length == 0) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum registro selecionado para envio!");
        }
        ve.lancarException();
    }

    public void removerSelecionadoS2231() {
        if (itemCedenciaS2231Selecionados.length == 0) {
            FacesUtil.addOperacaoNaoRealizada("Nenhum registro selecionado para remover!");
        } else {
            itemCedenciaS2231.removeAll(Arrays.asList(itemCedenciaS2231Selecionados));
            FacesUtil.addOperacaoRealizada("Selecionados removido com sucesso !");
        }
    }

    public void enviarS2231() {
        try {
            validarRegistrosAdicionadosS2231(itemCedenciaS2231Selecionados);
            validarEnvioEvento(Lists.newArrayList(itemCedenciaS2231Selecionados));
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            ArrayList<CedenciaContratoFP> cedencias = Lists.newArrayList(itemCedenciaS2231Selecionados);
            itemDetalheLogErro.clear();
            configuracaoEmpregadorESocialFacade.enviarS2231(config, cedencias, tipoCessao2231, itemDetalheLogErro);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");

            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(cedencias,
                config, TipoClasseESocial.S2231, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.CEDENCIACONTRATOFP, null, null);
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void enviarS2240() {
        try {
            validarEnvioEvento(itemRiscoOcupacional);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            configuracaoEmpregadorESocialFacade.enviarS2240(config, itemRiscoOcupacional);

            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemRiscoOcupacional,
                config, TipoClasseESocial.S2240, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.ASO, null, null);

            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void enviarS2420() {
        try {
            validarEnvioEvento(itemTermBeneficioS2420);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            itemDetalheLogErro.clear();
            configuracaoEmpregadorESocialFacade.enviarS2420(config, itemTermBeneficioS2420, itemDetalheLogErro);
            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemTermBeneficioS2420,
                config, TipoClasseESocial.S2420, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.VINCULOFP, null, null);

            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void enviarS2205() {
        try {
            validarEnvioEvento(itemContratoFPS2205);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            itemDetalheLogErro.clear();
            future = configuracaoEmpregadorESocialFacade.enviarS2205(config, itemContratoFPS2205, assistenteBarraProgresso,
                itemDetalheLogErro);
            FacesUtil.executaJavaScript("acompanhaEnvioEvento()");
            FacesUtil.executaJavaScript("aguarde.show()");

            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemContratoFPS2205,
                config, TipoClasseESocial.S2205, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.VINCULOFP, null, null);
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void enviarS2206() {
        try {
            validarEnvioEvento(Lists.newArrayList(mapItemContratoFPS2206));
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            itemDetalheLogErro.clear();
            configuracaoEmpregadorESocialFacade.enviarS2206(config, mapItemContratoFPS2206, itemDetalheLogErro);

            ArrayList<ContratoFP> contratos = Lists.newArrayList(mapItemContratoFPS2206.keySet());

            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(contratos,
                config, TipoClasseESocial.S2206, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.VINCULOFP, null, null);
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void enviarS2220() {
        try {
            validarEnvioEvento(itemContratoFPS2220);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            itemDetalheLogErro.clear();
            configuracaoEmpregadorESocialFacade.enviarS2220(config, itemContratoFPS2220, itemDetalheLogErro);

            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemContratoFPS2220,
                config, TipoClasseESocial.S2200, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.ASO, null, null);
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    private void validarRegistrosAdicionadosS2299(ExoneracaoRescisao[] exoneracao) {
        ValidacaoException ve = new ValidacaoException();
        if (exoneracao == null || exoneracao.length == 0) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum registro selecionado para envio!");
        }
        ve.lancarException();
    }

    public void enviarS2299() {
        try {
            validarRegistrosAdicionadosS2299(itemExoneracaoS2299Selecionado);
            ArrayList<ExoneracaoRescisao> exoneracoes = Lists.newArrayList(itemExoneracaoS2299Selecionado);
            validarEnvioEvento(exoneracoes);

            exoneracoes.forEach(exoneracao -> {
                exoneracao.setTipoOperacaoESocial(tipoOperacaoESocialS2299);
            });

            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            itemDetalheLogErro.clear();
            configuracaoEmpregadorESocialFacade.enviarS2299(config, exoneracoes, itemDetalheLogErro);
            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(exoneracoes,
                config, TipoClasseESocial.S2299, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.EXONERACAORESCISAO, null, null);

            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");

        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void enviarS2300() {
        try {
            validarEnvioEvento(itemPrestadorServico2300);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            itemPrestadorServico2300.forEach(prestador -> {
                prestador.setTipoOperacaoESocial(tipoOperacaoESocialS2300);
            });
            itemDetalheLogErro.clear();
            configuracaoEmpregadorESocialFacade.enviarS2300(config, itemPrestadorServico2300, itemDetalheLogErro);
            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemPrestadorServico2300,
                config, TipoClasseESocial.S2300, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.PRESTADORSERVICOS, null, null);
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Empregador não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void enviarS2400() {
        try {
            validarEnvioEvento(itemEventoS2400);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            itemEventoS2400.forEach(beneficiario -> beneficiario.setTipoOperacaoESocial(tipoOperacaoESocialS2400));
            itemDetalheLogErro.clear();
            configuracaoEmpregadorESocialFacade.enviarS2400(config, itemEventoS2400, itemDetalheLogErro);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemEventoS2400,
                config, TipoClasseESocial.S2400, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.VINCULOFP, null, null);
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    private void validarVinculoFPEventoEsocial(VinculoFPEventoEsocial[] vinculo) {
        ValidacaoException ve = new ValidacaoException();
        if (vinculo == null || vinculo.length == 0) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum registro selecionado para envio!");
        }
        ve.lancarException();
    }

    public void enviarS1200() {
        try {
            ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigenteDataAtual();

            validarVinculoFPEventoEsocial(registroEventoEsocialS1200.getItemVinculoFPSelecionados());
            validarEnvioEvento(Lists.newArrayList(registroEventoEsocialS1200.getItemVinculoFPSelecionados()));
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Enviando Evento E-social S-1200");
            assistenteBarraProgresso.zerarContadoresProcesso();
            assistenteBarraProgresso.setTotal(registroEventoEsocialS1200.getItemVinculoFPSelecionados().length);

            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);

            int partes = registroEventoEsocialS1200.getItemVinculoFPSelecionados().length > 10 ? (registroEventoEsocialS1200.getItemVinculoFPSelecionados().length / 4) : registroEventoEsocialS1200.getItemVinculoFPSelecionados().length;

            List<List<VinculoFPEventoEsocial>> itensParticionados = Lists.partition(Lists.newArrayList(registroEventoEsocialS1200.getItemVinculoFPSelecionados()), partes);
            futureAssistenteBarraProgresso = Lists.newArrayList();
            qtdeRegistrosParaEnvio = registroEventoEsocialS1200.getItemVinculoFPSelecionados().length;

            AtomicInteger quantidadeErros = new AtomicInteger(0);

            itemDetalheLogErro.clear();
            for (List<VinculoFPEventoEsocial> vinculos : itensParticionados) {
                futureAssistenteBarraProgresso.add(
                    configuracaoEmpregadorESocialFacade.enviarS1200(registroEventoEsocialS1200, vinculos, empregador, config,
                        assistenteBarraProgresso, configuracaoRH, quantidadeErros, itemDetalheLogErro));
            }
            FacesUtil.executaJavaScript("enviarEventoParticionadoEsocial()");

            configuracaoEmpregadorESocialFacade.criarHistoricoEventosPagamento(registroEventoEsocialS1200, config, TipoClasseESocial.S1200,
                new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.FICHAFINANCEIRAFP_OU_RPA);
        } catch (ValidacaoException ve) {
            if (assistenteBarraProgresso != null) {
                for (FacesMessage facesMessage : ve.getMensagens()) {
                    assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(facesMessage);
                }
            } else {
                FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            }

        } catch (Exception e) {
            assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(e.getMessage()));
        }
    }

    public void enviarS1202() {
        try {
            validarVinculoFPEventoEsocial(registroEventoEsocialS1202.getItemVinculoFPSelecionados());
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Enviando Evento E-social S-1202");
            assistenteBarraProgresso.zerarContadoresProcesso();
            assistenteBarraProgresso.setTotal(registroEventoEsocialS1202.getItemVinculoFPSelecionados().length);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        try {
            validarEnvioEvento(Lists.newArrayList(registroEventoEsocialS1202.getItemVinculoFPSelecionados()));
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);

            int partes = registroEventoEsocialS1202.getItemVinculoFPSelecionados().length > 10 ? (registroEventoEsocialS1202.getItemVinculoFPSelecionados().length / 4) : registroEventoEsocialS1202.getItemVinculoFPSelecionados().length;

            List<List<VinculoFPEventoEsocial>> itensParticionados = Lists.partition(Lists.newArrayList(registroEventoEsocialS1202.getItemVinculoFPSelecionados()), partes);
            futureAssistenteBarraProgresso = Lists.newArrayList();
            qtdeRegistrosParaEnvio = registroEventoEsocialS1202.getItemVinculoFPSelecionados().length;
            AtomicInteger quantidadeErros = new AtomicInteger(0);
            itemDetalheLogErro.clear();
            for (List<VinculoFPEventoEsocial> vinculos : itensParticionados) {
                futureAssistenteBarraProgresso.add(
                    configuracaoEmpregadorESocialFacade.enviarS1202(registroEventoEsocialS1202, vinculos, empregador,
                        config, assistenteBarraProgresso, quantidadeErros, itemDetalheLogErro));
            }
            FacesUtil.executaJavaScript("enviarEventoParticionadoEsocial()");

            configuracaoEmpregadorESocialFacade.criarHistoricoEventosPagamento(registroEventoEsocialS1202, config, TipoClasseESocial.S1202,
                new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.FICHAFINANCEIRAFP_OU_RPA);
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            for (FacesMessage facesMessage : ve.getMensagens()) {
                assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(facesMessage);
            }
        } catch (Exception e) {
            assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(e.getMessage()));
        }
    }

    public void enviarS1207() {
        try {
            validarEnvioEvento(registroEventoEsocialS1207.getItemVinculoFP());
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Enviando Evento E-social S-1207");
            assistenteBarraProgresso.zerarContadoresProcesso();
            assistenteBarraProgresso.setTotal(registroEventoEsocialS1207.getItemVinculoFP().size());
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            itemDetalheLogErro.clear();
            future = configuracaoEmpregadorESocialFacade.enviarS1207(registroEventoEsocialS1207, empregador, config,
                assistenteBarraProgresso, itemDetalheLogErro);
            FacesUtil.executaJavaScript("enviarEventoEsocial()");

            configuracaoEmpregadorESocialFacade.criarHistoricoEventosPagamento(registroEventoEsocialS1207, config, TipoClasseESocial.S1207,
                new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.FICHAFINANCEIRAFP_OU_RPA);
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void enviarS1210() {
        try {
            validarVinculoFPEventoEsocial(registroEventoEsocialS1210.getItemVinculoFPSelecionados());
            validarEnvioEvento(Lists.newArrayList(registroEventoEsocialS1210.getItemVinculoFPSelecionados()));
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Enviando Evento E-social S-1210");
            assistenteBarraProgresso.zerarContadoresProcesso();
            assistenteBarraProgresso.setTotal(registroEventoEsocialS1210.getItemVinculoFPSelecionados().length);

            int partes = registroEventoEsocialS1210.getItemVinculoFPSelecionados().length > 10 ? (registroEventoEsocialS1210.getItemVinculoFPSelecionados().length / 4) : registroEventoEsocialS1210.getItemVinculoFPSelecionados().length;

            List<List<VinculoFPEventoEsocial>> itensParticionados = Lists.partition(Lists.newArrayList(registroEventoEsocialS1210.getItemVinculoFPSelecionados()), partes);
            futureAssistenteBarraProgresso = Lists.newArrayList();
            qtdeRegistrosParaEnvio = registroEventoEsocialS1210.getItemVinculoFPSelecionados().length;

            AtomicInteger quantidadeErros = new AtomicInteger(0);
            itemDetalheLogErro.clear();
            for (List<VinculoFPEventoEsocial> vinculos : itensParticionados) {
                futureAssistenteBarraProgresso.add(
                    configuracaoEmpregadorESocialFacade.enviarS1210(registroEventoEsocialS1210, vinculos, empregador,
                        config, assistenteBarraProgresso, tipoRegimePrevidenciarioS1210, quantidadeErros, itemDetalheLogErro));
            }
            FacesUtil.executaJavaScript("enviarEventoParticionadoEsocial()");

            configuracaoEmpregadorESocialFacade.criarHistoricoEventosPagamento(registroEventoEsocialS1210, config, TipoClasseESocial.S1210,
                new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.FICHAFINANCEIRAFP_OU_RPA);
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (ValidacaoException ve) {
            if (assistenteBarraProgresso != null) {
                for (FacesMessage facesMessage : ve.getMensagens()) {
                    assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(facesMessage);
                }
            } else {
                FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            }

        } catch (Exception e) {
            assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(e.getMessage()));
            logger.error(e.getMessage());
        }
    }

    public void enviarS2418() {
        try {
            validarEnvioEvento(itemReativacaoBeneficioS2418);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            itemDetalheLogErro.clear();
            configuracaoEmpregadorESocialFacade.enviarS2418(config, itemReativacaoBeneficioS2418, itemDetalheLogErro);
            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemReativacaoBeneficioS2418,
                config, TipoClasseESocial.S2418, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.REATIVACAOBENEFICIO, null, null);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void buscarS1005() {
        if (empregador != null) {
            itemEmpregadors1005.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            if (apenasNaoEnviados && registroESocialFacade.hasEventoEnviado(TipoArquivoESocial.S1005, null, config)) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemEmpregadors1005.add(empregador);
            }
        }
    }

    public void buscarS1280() {
        if (empregador != null) {
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            itemEventoS1280.clear();
            List<IndicativoContribuicao> indicativo = configuracaoEmpregadorESocialFacade.getConfiguracaoEmpregadorS1280(config, exercicioFiltro, mesFiltro, decimoTerceiro, apenasNaoEnviados);
            if (indicativo == null || indicativo.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemEventoS1280.addAll(indicativo);
            }
        }
    }

    public void enviarS1280() {
        try {
            validarEnvioEvento(itemEventoS1280);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            configuracaoEmpregadorESocialFacade.enviarS1280(itemEventoS1280);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void buscarS1010() {
        if (empregador != null) {
            itemEventoFPS1010.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            List<EventoFP> eventos = registroESocialFacade.buscarEventoFPParaEnvioEsocialS1010(config, codigoEventoFP, apenasNaoEnviados, sistemaFacade.getDataOperacao(), tipoOperacaoESocialS1010);
            if (eventos == null || eventos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemEventoFPS1010.addAll(eventos);
            }
        }
    }

    public void removerS1010(EventoFP eventoFP) {
        itemEventoFPS1010.remove(eventoFP);
    }

    public void buscarS1020() {
        if (empregador != null) {
            itemEmpregadorS1020.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            if (apenasNaoEnviados && registroESocialFacade.hasEventoEnviado(TipoArquivoESocial.S1020, null, config)) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemEmpregadorS1020.add(empregador);
            }
        }
    }

    public void buscarS1030() {
        if (empregador != null) {
            itemCargoS1030.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            List<Cargo> cargos = registroESocialFacade.buscarCargoParaEnvioEsocial(config.getEntidade(), apenasNaoEnviados, TipoPCS.values());
            if (cargos == null || cargos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemCargoS1030.addAll(cargos);
            }
        }
    }

    public void buscarS1035() {
        if (empregador != null) {
            itemCargoS1035.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            List<Cargo> cargos = registroESocialFacade.buscarCargoParaEnvioEsocial(config.getEntidade(), apenasNaoEnviados, TipoPCS.QUADRO_EFETIVO);
            if (cargos == null || cargos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemCargoS1035.addAll(cargos);
            }
        }
    }

    public void buscarS1040() {
        if (empregador != null) {
            itemCargoS1040.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            List<Cargo> cargos = registroESocialFacade.buscarCargoParaEnvioEsocial(config.getEntidade(), apenasNaoEnviados, TipoPCS.QUADRO_EFETIVO);
            if (cargos == null || cargos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemCargoS1040.addAll(cargos);
            }
        }
    }

    public void buscarS1050() {
        if (empregador != null) {
            itemHorarioTrabalhoS1050.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            List<HorarioDeTrabalho> horarios = registroESocialFacade.buscarHorarioTrabalhoParaEnvioEsocial(config.getEntidade(), apenasNaoEnviados, TipoPCS.QUADRO_EFETIVO);
            if (horarios == null || horarios.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemHorarioTrabalhoS1050.addAll(horarios);
            }
        }
    }

    public void buscarS1070() {
        if (empregador != null) {
            itemProcessoAdministrativoS1070.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            List<ProcessoAdministrativoJudicial> processos = registroESocialFacade.buscarProcessoJudicialAdministrativo(config.getEntidade(), apenasNaoEnviados, TipoPCS.QUADRO_EFETIVO);
            if (processos == null || processos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemProcessoAdministrativoS1070.addAll(processos);
            }
        }
    }

    public void buscarS2190() {
        if (empregador != null) {
            itemContratoFPS2190.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<VWContratoFP> contratos = registroESocialFacade.buscarContratoFPPorTipoArquivo(config, apenasNaoEnviados, TipoArquivoESocial.S2190,
                null, null, null, UtilRH.getDataOperacao());
            if (contratos == null || contratos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemContratoFPS2190.addAll(contratos);
            }
        }
    }

    public void buscarS2200() {
        if (empregador != null) {
            assistenteBarraProgresso = null;
            itemContratoFPS2200.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<VWContratoFP> contratos = registroESocialFacade.buscarContratoFPPorTipoArquivo(config, apenasNaoEnviados, TipoArquivoESocial.S2200,
                dataInicialContrato, dataFinalContrato, hierarquiaOrganizacional, UtilRH.getDataOperacao());
            if (contratos == null || contratos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemContratoFPS2200.addAll(contratos);
            }
        }
    }

    public void buscarS2230() {
        if (empregador != null) {
            itemAfastanentoS2230.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<Afastamento> afastamentos = registroESocialFacade.buscarAfastamentos(config, contratoFPS2230, apenasNaoEnviados,
                TipoArquivoESocial.S2230, inicioAfastamento, finalAfastamento, new Date());

            if (afastamentos == null || afastamentos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemAfastanentoS2230.addAll(afastamentos);
            }
            contratoFP = null;
            dataInicial = DataUtil.getPrimeiroDiaAno(sistemaFacade.getExercicioCorrente().getAno());
            dataFinal = DataUtil.getUltimoDiaAno(sistemaFacade.getExercicioCorrente().getAno());
        }
    }

    public void buscarS2231() {
        try {
            validarPesquisaEnvioEventoS2231();
            if (empregador != null) {
                itemCedenciaS2231.clear();
                ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
                config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
                List<CedenciaContratoFP> cedencias = registroESocialFacade.buscarCedenciasEsocial(null, config, apenasNaoEnviados, dataInicialS2231, dataFinalS2231, tipoCessao2231);
                if (cedencias == null || cedencias.isEmpty()) {
                    FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
                } else {
                    itemCedenciaS2231.addAll(cedencias);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void buscarS2205() {
        if (empregador != null) {
            itemContratoFPS2205.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<VWContratoFP> contratos = registroESocialFacade.buscarContratoFPPorTipoArquivo(config, apenasNaoEnviados,
                TipoArquivoESocial.S2205,
                null, null, null, UtilRH.getDataOperacao());
            if (contratos == null || contratos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemContratoFPS2205.addAll(contratos);
            }
        }
    }

    public void buscarS2220() {
        if (empregador != null) {
            itemContratoFPS2220.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<ASO> asos = registroESocialFacade.buscarASO(config, apenasNaoEnviados, TipoArquivoESocial.S2220);
            if (asos == null || asos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemContratoFPS2220.addAll(asos);
            }
        }
    }

    public void buscarS2240() {
        if (empregador != null) {
            itemRiscoOcupacional.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<RiscoOcupacional> riscos = registroESocialFacade.buscaRiscoOcupacional(config, apenasNaoEnviados, TipoArquivoESocial.S2240);
            if (riscos == null || riscos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemRiscoOcupacional.addAll(riscos);
            }
        }
    }

    public void buscarS2299() {
        if (empregador != null) {
            assistenteBarraProgresso = null;
            itemExoneracaoS2299.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<ExoneracaoRescisao> exoneracao = registroESocialFacade.buscarExoneracaoPorEmpregador(config, apenasNaoEnviados,
                TipoArquivoESocial.S2299, dataInicial, dataFinal, dataInicialContrato, dataFinalContratoS2299, UtilRH.getDataOperacao(),
                hierarquiaOrganizacional);
            if (exoneracao == null || exoneracao.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemExoneracaoS2299.addAll(exoneracao);
            }
        }
    }

    public void buscarS2300() {
        if (empregador != null) {
            itemPrestadorServico2300.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<PrestadorServicos> prestador = prestadorServicosFacade.buscarPrestadorServicoPorEmpregador(config, apenasNaoEnviados, prestadorServicos, dataInicialContratoPrestador, dataFinalContratoPrestador);
            if (prestador == null || prestador.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemPrestadorServico2300.addAll(prestador);
            }
        }
    }

    public void buscarS2400() {
        if (empregador != null) {
            itemEventoS2400.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<VinculoFP> vinculos = registroESocialFacade.getVinculoFPFacade()
                .buscarBeneficiariosParaEnvioEsocial(config, apenasNaoEnviados, sistemaFacade.getDataOperacao(), dataInicioVigenciaS2400, dataFinalVigenciaS2400, TipoArquivoESocial.S2400);
            if (vinculos == null || vinculos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemEventoS2400.addAll(vinculos);
            }
        }
    }

    public void buscarS2405() {
        if (empregador != null) {
            itemAposentadoriaS2405.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<VinculoFP> vinculos = registroESocialFacade.getVinculoFPFacade()
                .buscarBeneficiariosParaEnvioEsocial(config, apenasNaoEnviados, sistemaFacade.getDataOperacao(), null, null, TipoArquivoESocial.S2405);
            if (vinculos == null || vinculos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemAposentadoriaS2405.addAll(vinculos);
            }
        }
    }

    public void buscarS2410() {
        if (empregador != null) {
            itemEventoS2410.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<VinculoFP> contratos = vinculoFPFacade.buscarBeneficiariosParaEnvioEsocial(config, apenasNaoEnviados, new Date(), dataInicioVigenciaS2410, dataFinalVigenciaS2410, TipoArquivoESocial.S2410);
            if (contratos == null || contratos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemEventoS2410.addAll(contratos);
            }
        }
    }

    public void enviarS2410() {
        try {
            validarEnvioEvento(itemEventoS2410);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Enviando Evento E-social S-2410");
            assistenteBarraProgresso.zerarContadoresProcesso();
            assistenteBarraProgresso.setTotal(itemEventoS2410.size());

            itemEventoS2410.forEach(beneficiario -> beneficiario.setTipoOperacaoESocial(tipoOperacaoESocialS2410));
            future = vinculoFPFacade.enviarS2410ESocial(config, itemEventoS2410, assistenteBarraProgresso);
            FacesUtil.executaJavaScript("enviarEventoEsocial()");
            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemEventoS2410,
                config, TipoClasseESocial.S2410, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.VINCULOFP, null, null);
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void buscarS2418() {
        if (empregador != null) {
            itemReativacaoBeneficioS2418.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<ReativacaoBeneficio> reativacoes = Lists.newArrayList();
            if (reativacoes == null || reativacoes.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemReativacaoBeneficioS2418.addAll(reativacoes);
            }
        }
    }

    public void buscarS2420() {
        if (empregador != null) {
            itemTermBeneficioS2420.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<VinculoFP> vinculos = vinculoFPFacade.buscarBeneficiariosComTerminoParaEnvioEsocial(config, apenasNaoEnviados, TipoArquivoESocial.S2420);
            if (vinculos == null || vinculos.isEmpty()) {
                FacesUtil.addMessageWarn("Atenção!", "Nenhum registro encontrado para envio.");
            } else {
                itemTermBeneficioS2420.addAll(vinculos);
            }
        }
    }

    public List<ContratoFP> buscarContratoFPPorEntidade(String parte) {
        return buscarContratoFPPorEntidade(parte, null);
    }

    public List<ContratoFP> buscarContratoFPPorEntidadeS2200(String parte) {
        return buscarContratoFPPorEntidade(parte, hierarquiaOrganizacional);
    }

    public List<ContratoFP> buscarContratoFPPorEntidade(String parte, HierarquiaOrganizacional ho) {
        if (empregador != null) {
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            return registroESocialFacade.recuperaContratoTipoPCSEsocial(parte, config, ho);
        }
        return null;
    }

    public List<ASO> buscarContratoFPAso(String parte) {
        if (empregador != null) {
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            return asoFacade.buscarBeneficiariosPorEntidade(config, parte);
        }
        return null;
    }

    public List<VinculoFP> buscarBeneficiarioPorEntidade(String parte) {
        if (empregador != null) {
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            return vinculoFPFacade.buscarBeneficiariosPorEntidade(config, parte);
        }
        return null;
    }

    public List<VinculoFP> buscarBeneficiarioVigentePorEntidade(String parte) {
        if (empregador != null) {
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            return vinculoFPFacade.buscarBeneficiariosVigentePorEntidade(config, sistemaFacade.getDataOperacao(), parte);
        }
        return null;
    }

    public List<RiscoOcupacional> buscarRiscoOcupacionalPorEntidade(String parte) {
        if (empregador != null) {
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            return riscoOcupacionalFacade.buscarRiscoOcupacionalPorEntidade(config, parte);
        }
        return null;
    }

    public List<ExoneracaoRescisao> buscarExoneracaoPorEntidade(String parte) {
        if (empregador != null) {
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            return registroESocialFacade.buscarExoneracaoEsocial(parte, config, hierarquiaOrganizacional);
        }
        return null;
    }

    public List<ContratoFP> buscarContratoFPExoneradoPorEntidade(String parte) {
        if (empregador != null) {
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            return registroESocialFacade.recuperaContratoTipoPCSEsocial(parte, config, hierarquiaOrganizacional);
        }
        return null;
    }

    public List<CedenciaContratoFP> completarCedencias(String parte) {
        if (empregador != null && dataInicialS2231 != null && dataFinalS2231 != null) {
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<CedenciaContratoFP> cedencias = registroESocialFacade.buscarCedenciasEsocial(parte, config, apenasNaoEnviados, dataInicialS2231, dataFinalS2231, tipoCessao2231);
            return cedencias;
        }
        return null;
    }

    public void adicionarASO() {
        itemContratoFPS2220.add(aso);
        aso = new ASO();
    }


    public void adicionarContratoFP(String tipoArquivoESocial) {
        try {
            if (TipoArquivoESocial.S2205.equals(TipoArquivoESocial.valueOf(tipoArquivoESocial))) {
                validarContratoAdicionado(itemContratoFPS2205);
                VWContratoFP dto = new VWContratoFP();
                dto.setId(contratoFP.getId());
                dto.setNome(contratoFP.getMatriculaFP().getPessoa().getNome());
                dto.setMatricula(contratoFP.getMatriculaFP().getMatricula() + "/" + contratoFP.getNumero());
                itemContratoFPS2205.add(dto);
            }
            if (TipoArquivoESocial.S2200.equals(TipoArquivoESocial.valueOf(tipoArquivoESocial))) {
                VWContratoFP dto = new VWContratoFP();
                dto.setId(contratoFP.getId());
                dto.setNome(contratoFP.getMatriculaFP().getPessoa().getNome());
                dto.setMatricula(contratoFP.getMatriculaFP().getMatricula() + "/" + contratoFP.getNumero());
                validarContratoAdicionado(itemContratoFPS2200);
                itemContratoFPS2200.add(dto);
            }
            if (TipoArquivoESocial.S2405.equals(TipoArquivoESocial.valueOf(tipoArquivoESocial))) {
                validarVinculoAdicionado(itemAposentadoriaS2405);
                itemAposentadoriaS2405.add(aposentado);
            }
            if (TipoArquivoESocial.S2231.equals(TipoArquivoESocial.valueOf(tipoArquivoESocial))) {
                validarCedenciaAdicionada(itemCedenciaS2231);
                itemCedenciaS2231.add(cedenciaContratoFP);
            }
            cedenciaContratoFP = null;
            aposentado = null;
            contratoFP = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarContratoS2206() {
        try {
            validarCamposS2206();
            mapItemContratoFPS2206.put(contratoFPS2206, dataAlteracaoContrato);
            contratoFPS2206 = null;
            dataAlteracaoContrato = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    public void adicionarBeneficiario(String tipoArquivoESocial) {
        try {
            if (TipoArquivoESocial.S2420.equals(TipoArquivoESocial.valueOf(tipoArquivoESocial))) {
                validarBeneficiarioAdicionado(itemTermBeneficioS2420);
                itemTermBeneficioS2420.add(beneficiario);
            }
            if (TipoArquivoESocial.S2400.equals(TipoArquivoESocial.valueOf(tipoArquivoESocial))) {
                validarBeneficiarioAdicionado(itemEventoS2400);
                itemEventoS2400.add(beneficiario);
            }
            if (TipoArquivoESocial.S2410.equals(TipoArquivoESocial.valueOf(tipoArquivoESocial))) {
                validarBeneficiarioAdicionado(itemEventoS2410);
                itemEventoS2410.add(beneficiario);
            }
            beneficiario = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarExoneracaoRescisao() {
        try {
            validarExoneracaoRescisao(itemExoneracaoS2299);
            itemExoneracaoS2299.add(exoneracaoRescisao);
            exoneracaoRescisao = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarExoneracaoRescisao(List<ExoneracaoRescisao> lista) {
        ValidacaoException ve = new ValidacaoException();
        if (lista.contains(exoneracaoRescisao)) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Exoneração já adicionada.");
        }
        ve.lancarException();
    }

    private void validarContratoAdicionado(List<VWContratoFP> lista) {
        ValidacaoException ve = new ValidacaoException();
        lista.stream()
            .filter(vwContratoFP -> vwContratoFP.getId().equals(contratoFP.getId()))
            .findFirst()
            .ifPresent(vwContratoFP -> ve.adicionarMensagemDeOperacaoNaoRealizada("Contrato já adicionado."));

        ve.lancarException();
    }

    private void validarCamposS2206() {
        ValidacaoException ve = new ValidacaoException();
        if (dataAlteracaoContrato == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data de alteração do servidor.");
        }

        if (contratoFPS2206 == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o servidor.");
        } else if (mapItemContratoFPS2206.containsKey(contratoFPS2206)) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Servidor já adicionado.");
        }
        ve.lancarException();
    }

    private void validarVinculoAdicionado(List<VinculoFP> lista) {
        ValidacaoException ve = new ValidacaoException();
        if (lista.contains(aposentado)) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Servidor já adicionado.");
        }
        ve.lancarException();
    }

    private void validarBeneficiarioAdicionado(List<VinculoFP> lista) {
        ValidacaoException ve = new ValidacaoException();
        if (lista.contains(beneficiario)) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Beneficiário já adicionado.");
        }
        ve.lancarException();
    }

    private void validarCedenciaAdicionada(List<CedenciaContratoFP> lista) {
        ValidacaoException ve = new ValidacaoException();
        if (lista.contains(cedenciaContratoFP)) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Cedência já adicionada.");
            cedenciaContratoFP = null;
        }
        ve.lancarException();
    }

    private void validarPesquisaEnvioEvento(RegistroEventoEsocial registroEventoEsocial) {
        ValidacaoException ve = new ValidacaoException();
        if (registroEventoEsocial.getTipoApuracaoFolha() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Apuração.");
        }
        if (registroEventoEsocial.getMes() == null && TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Mês.");
        }
        if (registroEventoEsocial.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Ano.");
        }
        if (empregador == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o CNPJ/Entidade.");
        }
        ve.lancarException();
    }

    private void validarHierarquiasEmpregador() {
        ValidacaoException ve = new ValidacaoException();
        ConfiguracaoEmpregadorESocial configuracao = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
        configuracao = configuracaoEmpregadorESocialFacade.recuperar(configuracao.getId());
        boolean orgaoVigente = false;
        if (configuracao.getItemConfiguracaoEmpregadorESocial() != null) {
            for (ItemConfiguracaoEmpregadorESocial item : configuracao.getItemConfiguracaoEmpregadorESocial()) {
                HierarquiaOrganizacional hierarquia = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalVigentePorUnidade(item.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, sistemaFacade.getDataOperacao());
                if (hierarquia != null) {
                    orgaoVigente = true;
                }
            }
        }
        if (!orgaoVigente || configuracao.getItemConfiguracaoEmpregadorESocial().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Empregador sem órgãos vigentes vinculados a ele.");
        }
        ve.lancarException();
    }

    private void validarPesquisaEnvioEventoS1202() {
        ValidacaoException ve = new ValidacaoException();
        if (registroEventoEsocialS1202.getTipoApuracaoFolha() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Apuração.");
        }
        if (registroEventoEsocialS1202.getMes() == null && TipoApuracaoFolha.MENSAL.equals(registroEventoEsocialS1202.getTipoApuracaoFolha())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Mês.");
        }
        if (registroEventoEsocialS1202.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Ano.");
        }
        if (empregador == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o CNPJ/Entidade.");
        }
        ve.lancarException();
    }

    private void validarPesquisaEnvioEventoS1207() {
        ValidacaoException ve = new ValidacaoException();
        if (registroEventoEsocialS1207.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Mês.");
        }
        if (registroEventoEsocialS1207.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Ano.");
        }
        if (empregador == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o CNPJ/Entidade.");
        }
        ve.lancarException();
    }


    private void validarPesquisaEnvioEventoS1210() {
        ValidacaoException ve = new ValidacaoException();
        if (registroEventoEsocialS1210.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Mês.");
        }
        if (registroEventoEsocialS1210.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Ano.");
        }
        if (empregador == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o CNPJ/Entidade.");
        }
        ve.lancarException();
    }

    private void validarPesquisaEnvioEventoS2231() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicialS2231 == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data Inicial.");
        }
        if (dataFinalS2231 == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data Final.");
        }
        ve.lancarException();
    }

    public void pesquisarVinculoComFolhaEfetivadaNaCompetencia() {
        try {
            validarPesquisaEnvioEvento(registroEventoEsocialS1200);
            validarHierarquiasEmpregador();
            List<VinculoFPEventoEsocial> itens = getVinculoComFolhaEfetivadaNaCompetencia(registroEventoEsocialS1200);
            registroEventoEsocialS1200.setItemVinculoFP(itens);
            vinculoFP = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void pesquisarVinculoComFolhaEfetivadaNaCompetenciaS1202() {
        try {
            assistenteBarraProgresso = null;
            validarPesquisaEnvioEventoS1202();
            List<VinculoFPEventoEsocial> itens = getVinculoComFolhaEfetivadaNaCompetenciaS1202();
            registroEventoEsocialS1202.setItemVinculoFP(itens);
            vinculoFP = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void pesquisarVinculoComFolhaEfetivadaNaCompetenciaS1207() {
        try {
            validarPesquisaEnvioEventoS1207();
            registroEventoEsocialS1207.setItemVinculoFP(getVinculoComFolhaEfetivadaNaCompetenciaS1207());
            registroEventoEsocialS1207.setPessoaFisica(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void pesquisarVinculoComFolhaEfetivadaNaCompetenciaS1210() {
        registroEventoEsocialS1210.setItemVinculoFP(Lists.<VinculoFPEventoEsocial>newArrayList());
        try {
            assistenteBarraProgresso = null;
            validarPesquisaEnvioEventoS1210();
            List<VinculoFPEventoEsocial> itens = getVinculoComFolhaEfetivadaNaCompetenciaS1210();
            registroEventoEsocialS1210.setItemVinculoFP(itens);
            vinculoFP = null;
            pessoaVinculo = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void limparRegistrosS1200() {
        registroEventoEsocialS1200.getItemVinculoFP().clear();
    }

    public void removerServidorS1200(VinculoFPEventoEsocial servidor) {
        registroEventoEsocialS1200.getItemVinculoFP().remove(servidor);
    }

    public void removerServidorS1202(VinculoFPEventoEsocial servidor) {
        registroEventoEsocialS1202.getItemVinculoFP().remove(servidor);
    }


    public void removerServidorS1207(VinculoFPEventoEsocial servidor) {
        registroEventoEsocialS1207.getItemVinculoFP().remove(servidor);
    }

    public void removerServidorS1210(VinculoFPEventoEsocial servidor) {
        registroEventoEsocialS1210.getItemVinculoFP().remove(servidor);
    }

    private List<VinculoFPEventoEsocial> getVinculoComFolhaEfetivadaNaCompetencia(RegistroEventoEsocial
                                                                                      registroEventoEsocial) {
        try {
            registroEventoEsocial.setEntidade(empregador);
            return registroS1200Facade.getVinculoComFolhaEfetivadaNaCompetencia(registroEventoEsocial, empregador, pessoaVinculo, apenasNaoEnviados, hierarquiaOrganizacional);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return null;
        }
    }

    private List<VinculoFPEventoEsocial> getVinculoComFolhaEfetivadaNaCompetenciaS1202() {
        try {
            registroEventoEsocialS1202.setEntidade(empregador);
            return registroS1202Facade.getVinculoFPEventoEsocial(registroEventoEsocialS1202, pessoaVinculo,
                apenasNaoEnviados, hierarquiaOrganizacional);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return null;
        }
    }

    private List<VinculoFPEventoEsocial> getVinculoComFolhaEfetivadaNaCompetenciaS1207() {
        try {
            registroEventoEsocialS1207.setEntidade(empregador);
            return registroS1207Facade.getVinculoFPEventoEsocial(registroEventoEsocialS1207, apenasNaoEnviados);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return null;
        }
    }

    private List<VinculoFPEventoEsocial> getVinculoComFolhaEfetivadaNaCompetenciaS1210() {
        try {
            registroEventoEsocialS1210.setEntidade(empregador);
            registroEventoEsocialS1210.setTipoClasseESocial(TipoClasseESocial.S1210);
            return registroS1210Facade.getVinculoFPEventoEsocials1210(registroEventoEsocialS1210, empregador, null, apenasNaoEnviados, tipoRegimePrevidenciarioS1210, pessoaVinculo, hierarquiaOrganizacional);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return null;
        }
    }

    public void adicionarVinculo(String tipoEventoString) {
        TipoArquivoESocial tipoEvento = TipoArquivoESocial.valueOf(tipoEventoString);
        RegistroEventoEsocial registroEventoEsocial = getRegistroEventoEsocialPorTipo(tipoEvento);
        try {
            validarPesquisaEnvioEvento(registroEventoEsocial);
            List<VinculoFPEventoEsocial> item = Lists.newArrayList();
            if (TipoArquivoESocial.S1202.name().equals(tipoEvento.name())) {
                item = getVinculoComFolhaEfetivadaNaCompetenciaS1202();
            }
            if (item != null) {
                validarItemEvento(item, registroEventoEsocial);
                registroEventoEsocial.getItemVinculoFP().addAll(item);
                vinculoFP = null;
            }
        } catch (ValidacaoException ve) {
            vinculoFP = null;
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarPessoaS1200(SelectEvent event) {
        try {
            pessoaVinculo = (PessoaFisica) event.getObject();
            validarPesquisaEnvioEvento(registroEventoEsocialS1200);
            List<VinculoFPEventoEsocial> item = getVinculoComFolhaEfetivadaNaCompetencia(registroEventoEsocialS1200);
            if (item != null) {
                validarItemEventoS1200(item, registroEventoEsocialS1200);
                registroEventoEsocialS1200.getItemVinculoFP().addAll(item);
                List<VinculoFPEventoEsocial> itensSelecionados = registroEventoEsocialS1200.getItemVinculoFPSelecionados() != null ? Lists.newArrayList(registroEventoEsocialS1200.getItemVinculoFPSelecionados()) : Lists.newArrayList();
                itensSelecionados.addAll(item);
                registroEventoEsocialS1200.setItemVinculoFPSelecionados(itensSelecionados.toArray(new VinculoFPEventoEsocial[0]));
                vinculoFP = null;
                pessoaVinculo = null;
            }
        } catch (ValidacaoException ve) {
            vinculoFP = null;
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarPessoaS1210(SelectEvent event) {
        try {
            pessoaVinculo = (PessoaFisica) event.getObject();
            validarPesquisaEnvioEventoS1210();
            List<VinculoFPEventoEsocial> item = getVinculoComFolhaEfetivadaNaCompetenciaS1210();
            if (item != null) {
                validarItemEventoS1200(item, registroEventoEsocialS1210);
                registroEventoEsocialS1210.getItemVinculoFP().addAll(item);
                List<VinculoFPEventoEsocial> itensSelecionados = registroEventoEsocialS1210.getItemVinculoFPSelecionados() != null ? Lists.newArrayList(registroEventoEsocialS1210.getItemVinculoFPSelecionados()) : Lists.newArrayList();
                itensSelecionados.addAll(item);
                registroEventoEsocialS1210.setItemVinculoFPSelecionados(itensSelecionados.toArray(new VinculoFPEventoEsocial[0]));
                pessoaVinculo = null;
            }
        } catch (ValidacaoException ve) {
            pessoaVinculo = null;
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    private void validarItemEvento(List<VinculoFPEventoEsocial> item, RegistroEventoEsocial
        registroEventoEsocial) {
        ValidacaoException ve = new ValidacaoException();
        if (registroEventoEsocial.getItemVinculoFP() != null && !registroEventoEsocial.getItemVinculoFP().isEmpty()) {
            for (VinculoFPEventoEsocial vinculoFPEventoEsocial : item) {
                for (VinculoFPEventoEsocial fpEventoEsocial : registroEventoEsocial.getItemVinculoFP()) {
                    if (TipoTrabalhadorEsocial.PRESTADOR_SERVICO.equals(registroEventoEsocial.getTipoTrabalhadorEsocial())) {
                        if (vinculoFPEventoEsocial.getIdPessoa().equals(fpEventoEsocial.getIdPessoa())) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("Servidor já adicionado!");
                            break;
                        }
                    } else {
                        if (vinculoFPEventoEsocial.getIdVinculo().equals(fpEventoEsocial.getIdVinculo())) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("Servidor já adicionado!");
                            break;
                        }
                    }
                }
            }
        }
        ve.lancarException();
    }

    private void validarItemEventoS1200(List<VinculoFPEventoEsocial> item, RegistroEventoEsocial
        registroEventoEsocial) {
        ValidacaoException ve = new ValidacaoException();
        if (registroEventoEsocial.getItemVinculoFP() != null && !registroEventoEsocial.getItemVinculoFP().isEmpty()) {
            for (VinculoFPEventoEsocial vinculoFPEventoEsocial : item) {
                for (VinculoFPEventoEsocial fpEventoEsocial : registroEventoEsocial.getItemVinculoFP()) {
                    if (vinculoFPEventoEsocial.getCpf().equals(fpEventoEsocial.getCpf())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Servidor já adicionado!");
                        break;
                    }
                }
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getMes() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<SelectItem> getTipoFolhaPagamento() {
        return Util.getListSelectItem(TipoFolhaDePagamento.getFolhasPorPrioridadeDeUso(), false);
    }

    public List<SelectItem> getSituacaoEvento() {
        return Util.getListSelectItem(SituacaoESocial.getSituacoesRelatorioInconsistencia(), false);
    }


    public boolean isEventoDePagamento() {
        return registroExclusaoS3000.getTipoArquivoESocial() != null && TipoArquivoESocial.getEventoFolhaPagamento().contains(registroExclusaoS3000.getTipoArquivoESocial()) && !isTipoArquivoESocialEhS1210();
    }

    public boolean isEventoPagamentoMensal() {
        return registroExclusaoS3000.getTipoExclusaoEventoFolha() != null && TipoExclusaoEventoFolha.MENSAL.equals(registroExclusaoS3000.getTipoExclusaoEventoFolha()) || isTipoArquivoESocialEhS1210();
    }

    public boolean isTipoArquivoESocialEhS1210() {
        if (registroExclusaoS3000.getTipoArquivoESocial() != null && registroExclusaoS3000.getTipoArquivoESocial().equals(TipoArquivoESocial.S1210)) {
            registroExclusaoS3000.setTipoExclusaoEventoFolha(TipoExclusaoEventoFolha.MENSAL);
            return true;
        }
        return false;
    }

    public boolean isEventoPagamentoAnual() {
        return registroExclusaoS3000.getTipoExclusaoEventoFolha() != null && TipoExclusaoEventoFolha.SALARIO_13.equals(registroExclusaoS3000.getTipoExclusaoEventoFolha());
    }

    public boolean isEventoDesligamento() {
        return TipoArquivoESocial.S2299.equals(registroExclusaoS3000.getTipoArquivoESocial()) || TipoArquivoESocial.S2399.equals(registroExclusaoS3000.getTipoArquivoESocial());
    }

    public void limparInformacoesFolha() {
        registroExclusaoS3000.setCompetencia(null);
        registroExclusaoS3000.setExercicio(null);
    }

    public void removerRegistro(RegistroExclusaoS3000 registro) {
        itemExclusao.remove(registro);
    }

    public void removerRegistroS2306(PrestadorServicos prestador) {
        itemRegistroEventoEsocial2306.remove(prestador);
    }

    public void adicionarTodosEventosByFolhaAndCompetenciaS3000() {
        try {
            validarEventoS3000();
            ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial = configuracaoEmpregadorESocialFacade.buscarConfiguracaoPorEntidade(empregador);
            configuracaoEmpregadorESocial = configuracaoEmpregadorESocialFacade.recuperar(configuracaoEmpregadorESocial.getId());

            List<Long> idsUnidade = hierarquiaOrganizacional != null ? Lists.newArrayList(hierarquiaOrganizacional.getSubordinada().getId()) : contratoFPFacade.montarIdOrgaoEmpregador(configuracaoEmpregadorESocial);
            itemExclusao = Lists.newArrayList();
            Integer ano = null;
            Integer mes = null;
            if (TipoExclusaoEventoFolha.MENSAL.equals(registroExclusaoS3000.getTipoExclusaoEventoFolha())) {
                ano = DataUtil.getAno(registroExclusaoS3000.getCompetencia());
                mes = DataUtil.getMes(registroExclusaoS3000.getCompetencia());
            } else if (registroExclusaoS3000.getExercicio() != null) {
                ano = registroExclusaoS3000.getExercicio().getAno();
            }
            String idVinculo = registroExclusaoS3000.getVinculoFP() != null ? registroExclusaoS3000.getVinculoFP().getId().toString() :
                (registroExclusaoS3000.getPrestadorServicos() != null ? registroExclusaoS3000.getPrestadorServicos().getId().toString() : null);
            String cpfVinculo = registroExclusaoS3000.getVinculoFP() != null ? StringUtil.retornaApenasNumeros(registroExclusaoS3000.getVinculoFP().getMatriculaFP().getPessoa().getCpf()) :
                (registroExclusaoS3000.getPrestadorServicos() != null ? StringUtil.retornaApenasNumeros(registroExclusaoS3000.getPrestadorServicos().getPrestador().getCpf_Cnpj()) : "");

            List<String> idsExonecarao = null;
            if (isEventoDesligamento()) {
                idsExonecarao = exoneracaoRescisaoFacade.getIdsEsocialExoneracaoPorEmpregadorFiltrandoPorInicioAndFinalVigenciaAndTipoArquivoEsocial(dataInicialDesligamento,
                    dataFinalDesligamento, idsUnidade, registroExclusaoS3000.getTipoArquivoESocial());
                if (idsExonecarao == null || idsExonecarao.isEmpty()) {
                    FacesUtil.addWarn("Atenção!", "Não foi encontrado nenhum evento para os filtros informados.");
                    return;
                }
            }

            List<EventoESocialDTO> eventos = new ArrayList<>(configuracaoEmpregadorESocialFacade.getEventosFolhaByEmpregador(configuracaoEmpregadorESocial.getEntidade().getPessoaJuridica().getCnpj(), registroExclusaoS3000.getTipoArquivoESocial(), mes, ano, registroExclusaoS3000.getRecibo(), idVinculo, registroExclusaoS3000.getIdXML(), cpfVinculo, idsExonecarao));

            if (tipoTrabalhadorEsocial != null) {
                eventos = filtrarPorTipoTrabalhadorEsocialEventosDePagamento(eventos);
            }

            if (tipoTrabalhadorEsocial != null) {
                eventos = filtrarPorTipoTrabalhadorEsocialEventosNormal(eventos);
            }


            if (hierarquiaOrganizacional != null) {
                filtrarEventosPorOrgao(eventos);
            }

            itemExclusao.clear();

            if (Util.isListNullOrEmpty(eventos)) {
                FacesUtil.addWarn("Atenção!", "Não foi encontrado nenhum evento para os filtros informados.");
            } else {
                ValidacaoException ve = new ValidacaoException();
                List<String> idsXml = Lists.newArrayList();
                itemExclusao.forEach(item -> {
                    idsXml.add(item.getIdXML().trim());
                });
                for (EventoESocialDTO evento : eventos) {
                    if (!idsXml.contains(evento.getIdXMLEvento().trim())) {
                        RegistroExclusaoS3000 newRegistroExclusaoS3000 = new RegistroExclusaoS3000(
                            registroExclusaoS3000.getTipoArquivoESocial(),
                            evento.getIdXMLEvento(),
                            recuperarVinculoFP(evento),
                            registroExclusaoS3000.getTipoExclusaoEventoFolha(),
                            registroExclusaoS3000.getCompetencia(),
                            registroExclusaoS3000.getExercicio(),
                            registroExclusaoS3000.getExclusaoEventoEsocial(),
                            registroExclusaoS3000.getEntidade(),
                            evento.getReciboEntrega(),
                            recuperarPrestadorDeServicos(evento),
                            recuperarPessoaFisica(evento));
                        Util.adicionarObjetoEmLista(itemExclusao, newRegistroExclusaoS3000);
                    } else {
                        ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "O evento com o ID Xml " + evento.getIdXMLEvento().trim() + " já se encontra adicionado.");
                    }
                }
                ve.lancarException();
            }
            registroExclusaoS3000 = new RegistroExclusaoS3000();
            tipoTrabalhadorEsocial = null;
        } catch (ValidacaoException validacao) {
            FacesUtil.printAllFacesMessages(validacao.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private List<EventoESocialDTO> filtrarPorTipoTrabalhadorEsocialEventosDePagamento(List<EventoESocialDTO> eventos) {
        if (isEventoDePagamento() || isTipoArquivoESocialEhS1210()) {
            return eventos.stream()
                .filter(evento -> {
                    boolean prestadorExiste = registroESocialFacade.getPrestadorServicosFichaRPA(evento.getIdentificadorWP()) != null;
                    return (tipoTrabalhadorEsocial == TipoTrabalhadorEsocial.PRESTADOR_SERVICO) == prestadorExiste;
                })
                .collect(Collectors.toList());
        }
        return eventos;
    }

    private List<EventoESocialDTO> filtrarPorTipoTrabalhadorEsocialEventosNormal(List<EventoESocialDTO> eventos) {
        if (tipoTrabalhadorEsocial == TipoTrabalhadorEsocial.SERVIDOR && isEventosPrestador(eventos)) {
            return eventos.stream()
                .filter(evento -> configuracaoEmpregadorESocialFacade.recuperarIdPrestadorPorUnidadeEId(null, evento.getIdentificadorWP()) != null)
                .collect(Collectors.toList());
        }
        return eventos;
    }

    private VinculoFP recuperarVinculoFP(EventoESocialDTO evento) {
        if (evento == null || StringUtils.isBlank(evento.getIdentificadorWP())) {
            return null;
        }

        List<EventoESocialDTO> listaComUm = Lists.newArrayList(evento);

        if (isEventoDePagamento() || isTipoArquivoESocialEhS1210()) {
            PrestadorServicos prestadorServico = registroESocialFacade.getPrestadorServicosFichaRPA(evento.getIdentificadorWP());
                if(prestadorServico == null){
                    Long id = configuracaoEmpregadorESocialFacade.recuperarIdFichaPorOrgaoEIdentificadorWP(null, evento.getIdentificadorWP());
                    if (id != null) {
                        return vinculoFPFacade.recuperarSimples(id);
                    }
                }
        }

        if (isEventosVinculoFP(listaComUm)) {
            Long idWP = Long.parseLong(evento.getIdentificadorWP());
            return vinculoFPFacade.recuperarSimples(idWP);
        }

        if (isEventosComUnidadeNaClasse(listaComUm) || isEventosContratoFP(listaComUm)) {
            Long id = configuracaoEmpregadorESocialFacade.recuperarIdContratoFPPorUnidadeEId(null,evento.getIdentificadorWP(), evento.getClasseWP().name());
            if (id != null) {
                return vinculoFPFacade.recuperarSimples(id);
            }
        }

        if (isEventosComVinculoFPNaClasse(eventos)) {
            Long id = configuracaoEmpregadorESocialFacade.recuperarIdVinculoFPAssociadoPorUnidadeEId(null, evento.getIdentificadorWP(), evento.getClasseWP().name());
            if (id != null) {
                return vinculoFPFacade.recuperarSimples(id);
            }
        }
        return null;
    }

    private PessoaFisica recuperarPessoaFisica(EventoESocialDTO evento){
        if (evento == null || StringUtils.isBlank(evento.getIdentificadorWP())) {
            return null;
        }

        List<EventoESocialDTO> listaComUm = Lists.newArrayList(evento);
        if (isEventosPessoa(listaComUm)) {
            Long id = configuracaoEmpregadorESocialFacade.recuperarIdPessoaPorUnidadeEId(null, evento.getIdentificadorWP());
            if (id != null) {
                return pessoaFisicaFacade.recuperarSimples(id);
            }
        }
        return null;
    }

    private PrestadorServicos recuperarPrestadorDeServicos(EventoESocialDTO evento) {
        if (evento == null || StringUtils.isBlank(evento.getIdentificadorWP())) {
            return null;
        }

        PrestadorServicos prestadorServico = registroESocialFacade.getPrestadorServicosFichaRPA(evento.getIdentificadorWP());

        if(prestadorServico != null) {
            return prestadorServicosFacade.recuperar(prestadorServico.getId());
        }

        List<EventoESocialDTO> listaComUm = Lists.newArrayList(evento);
        if (isEventosPrestador(listaComUm)) {
            Long id = configuracaoEmpregadorESocialFacade.recuperarIdPrestadorPorUnidadeEId(null,evento.getIdentificadorWP());
            return prestadorServicosFacade.recuperar(id);
        }
        return null;
    }


    private void filtrarEventosPorOrgao(List<EventoESocialDTO> eventos) {

            if (!eventos.isEmpty()) {
                if (isEventoDePagamento() || isTipoArquivoESocialEhS1210()) {
                    filtrarEventosDePagamentoPorOrgao(eventos);
                }
                if (isEventosVinculoFP(eventos)) {
                    filtrarEventosPorUnidadeEVinculoFP(eventos);
                }
                if (isEventosContratoFP(eventos)) {
                    filtrarEventosPorUnidadeEContratoFP(eventos);
                }
                if (isEventosComUnidadeNaClasse(eventos)) {
                    filtrarEventosComUnidadeNaClasse(eventos);
                }
                if (isEventosComVinculoFPNaClasse(eventos)) {
                    filtrarEventosVinculoFPNaClasse(eventos);
                }
                if (isEventosPrestador(eventos)) {
                    filtrarEventosPorPrestador(eventos);
                }
                if (isEventosPessoa(eventos)) {
                    filtrarEventosPorOrgaoEPessoa(eventos);
                }
            }
    }

    private void filtrarEventosDePagamentoPorOrgao(List<EventoESocialDTO> eventos){
        List<EventoESocialDTO> eventosFiltradosPorOrgao = Lists.newArrayList();
            for (EventoESocialDTO evento : eventos) {
                Long id = configuracaoEmpregadorESocialFacade.recuperarIdFichaPorOrgaoEIdentificadorWP(hierarquiaOrganizacional.getSubordinada().getId(), evento.getIdentificadorWP());
                    if (id != null) {
                        eventosFiltradosPorOrgao.add(evento);
                    }
                }
            eventos.clear();
            eventos.addAll(eventosFiltradosPorOrgao);
    }

    private void filtrarEventosPorUnidadeEVinculoFP(List<EventoESocialDTO> eventos){
        List<EventoESocialDTO> eventosFiltradosPorOrgao = Lists.newArrayList();
        for (EventoESocialDTO evento : eventos) {
            Long id = configuracaoEmpregadorESocialFacade.recuperarIdVinculoFPPorUnidadeEId(hierarquiaOrganizacional.getSubordinada().getId(), evento.getIdentificadorWP());
                if (id != null) {
                    eventosFiltradosPorOrgao.add(evento);
                }
            }
        eventos.clear();
        eventos.addAll(eventosFiltradosPorOrgao);
    }

    private void filtrarEventosComUnidadeNaClasse(List<EventoESocialDTO> eventos){
        List<EventoESocialDTO> eventosFiltradosPorOrgao = Lists.newArrayList();
        for (EventoESocialDTO evento : eventos) {
            Long id = configuracaoEmpregadorESocialFacade.recuperarIdRegistroPorUnidadeEId(hierarquiaOrganizacional.getSubordinada().getId(), evento.getIdentificadorWP(), evento.getClasseWP().name());
            if (id != null) {
                eventosFiltradosPorOrgao.add(evento);
            }
        }
        eventos.clear();
        eventos.addAll(eventosFiltradosPorOrgao);
    }

    private void filtrarEventosPorUnidadeEContratoFP(List<EventoESocialDTO> eventos){
        List<EventoESocialDTO> eventosFiltradosPorOrgao = Lists.newArrayList();
        for (EventoESocialDTO evento : eventos) {
            Long id = configuracaoEmpregadorESocialFacade.recuperarIdContratoFPPorUnidadeEId(hierarquiaOrganizacional.getSubordinada().getId(), evento.getIdentificadorWP(), evento.getClasseWP().name());
            if (id != null) {
                eventosFiltradosPorOrgao.add(evento);
            }
        }
        eventos.clear();
        eventos.addAll(eventosFiltradosPorOrgao);
    }

    private void filtrarEventosVinculoFPNaClasse(List<EventoESocialDTO> eventos){
        List<EventoESocialDTO> eventosFiltradosPorOrgao = Lists.newArrayList();
        for (EventoESocialDTO evento : eventos) {
            Long id = configuracaoEmpregadorESocialFacade.recuperarIdVinculoFPAssociadoPorUnidadeEId(hierarquiaOrganizacional.getSubordinada().getId(), evento.getIdentificadorWP(), evento.getClasseWP().name());
            if (id != null) {
                eventosFiltradosPorOrgao.add(evento);
            }
        }
        eventos.clear();
        eventos.addAll(eventosFiltradosPorOrgao);
    }

    private void filtrarEventosPorPrestador(List<EventoESocialDTO> eventos){
        List<EventoESocialDTO> eventosFiltradosPorOrgao = Lists.newArrayList();
        for (EventoESocialDTO evento : eventos) {
            Long id = configuracaoEmpregadorESocialFacade.recuperarIdPrestadorPorUnidadeEId(hierarquiaOrganizacional.getSubordinada().getId(), evento.getIdentificadorWP());
            if (id != null) {
                eventosFiltradosPorOrgao.add(evento);
            }
        }
        eventos.clear();
        eventos.addAll(eventosFiltradosPorOrgao);
    }

    private void filtrarEventosPorOrgaoEPessoa(List<EventoESocialDTO> eventos){
        List<EventoESocialDTO> eventosFiltradosPorOrgao = Lists.newArrayList();
        for (EventoESocialDTO evento : eventos) {
            Long id = configuracaoEmpregadorESocialFacade.recuperarIdPessoaPorUnidadeEId(hierarquiaOrganizacional.getSubordinada().getId(), evento.getIdentificadorWP());
            if (id != null) {
                eventosFiltradosPorOrgao.add(evento);
            }
        }
        eventos.clear();
        eventos.addAll(eventosFiltradosPorOrgao);
    }


    public boolean isEventosComUnidadeNaClasse(List<EventoESocialDTO> eventos){
        List<ClasseWP> eventosContraFP = Lists.newLinkedList();
        eventosContraFP.add(ClasseWP.ASO);
        return eventos.stream().anyMatch(evento -> evento.getClasseWP() != null && eventosContraFP.contains(evento.getClasseWP()));
    }

    public boolean isEventosVinculoFP(List<EventoESocialDTO> eventos){
        return eventos.stream().anyMatch(evento -> evento.getClasseWP() != null && evento.getClasseWP().equals(ClasseWP.VINCULOFP));
    }

    public boolean isEventosComVinculoFPNaClasse(List<EventoESocialDTO> eventos){
        List<ClasseWP> eventosContraFP = Lists.newLinkedList();
        eventosContraFP.add(ClasseWP.EXONERACAORESCISAO);
        eventosContraFP.add(ClasseWP.REATIVACAOBENEFICIO);
        eventosContraFP.add(ClasseWP.RISCOOCUPACIONAL);
        return eventos.stream().anyMatch(evento -> evento.getClasseWP() != null && eventosContraFP.contains(evento.getClasseWP()));
    }

    public boolean isEventosContratoFP(List<EventoESocialDTO> eventos){
        List<ClasseWP> eventosContraFP = Lists.newLinkedList();
        eventosContraFP.add(ClasseWP.AFASTAMENTO);
        eventosContraFP.add(ClasseWP.CEDENCIACONTRATOFP);
        eventosContraFP.add(ClasseWP.REINTEGRACAO);
        return eventos.stream().anyMatch(evento -> evento.getClasseWP() != null && eventosContraFP.contains(evento.getClasseWP()));
    }


    public boolean isEventosPrestador(List<EventoESocialDTO> eventos){
        return eventos.stream().anyMatch(evento -> evento.getClasseWP() != null && evento.getClasseWP().equals(ClasseWP.PRESTADORSERVICOS));
    }

    public boolean isEventosPessoa(List<EventoESocialDTO> eventos){
        return eventos.stream().anyMatch(evento -> evento.getClasseWP() != null && evento.getClasseWP().equals(ClasseWP.CAT));
    }


    public void buscarS2306() {
        List<PrestadorServicos> prestadores = Lists.newArrayList();
        ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
        config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
        itemRegistroEventoEsocial2306.clear();
        prestadores = prestadorServicoAlteracaoFacade.buscarPrestadorServicoAlteracaoPorEmpregador(config, apenasNaoEnviados, prestadorServicosS2306, dataInicialContratoPrestadorS2306, dataFinalContratoPrestadorS2306);
        itemRegistroEventoEsocial2306.addAll(prestadores);
    }

    public void enviarEventoS2306() {
        try {
            validarEnvioEvento(itemRegistroEventoEsocial2306);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            itemDetalheLogErro.clear();
            configuracaoEmpregadorESocialFacade.enviarS2306(config, itemRegistroEventoEsocial2306, itemDetalheLogErro);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemRegistroEventoEsocial2306,
                config, TipoClasseESocial.S2306, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.PRESTADORSERVICOS, null, null);
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }


    public void enviarEventoS3000() {
        try {
            validarRegistrosAdicionadosS3000(itemExclusaoS3000Selecionados);
            validarEnvioEvento(Lists.newArrayList(itemExclusaoS3000Selecionados));
            validarBuscaEmpregador();
            ArrayList<RegistroExclusaoS3000> registrosExclusaoS3000 = Lists.newArrayList(itemExclusaoS3000Selecionados);
            ExclusaoEventoEsocial s3000 = new ExclusaoEventoEsocial();
            s3000.setDataCriacao(new Date());
            s3000.setDataEnvio(new Date());
            s3000.setStatus(TipoSituacaoGeracaoEsocial.ARQUIVO_GERADO);
            s3000.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            s3000.setItemExclusao(registrosExclusaoS3000);
            s3000 = exclusaoEventoEsocialFacade.salvarRetornando(s3000);

            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            configuracaoEmpregadorESocialFacade.enviarS3000(s3000, config);

            Mes mes = null;
            Exercicio exercicio = null;
            if (itemExclusao != null && itemExclusao.get(0).getCompetencia() != null) {
                mes = Mes.getMesToInt(DataUtil.getMes(itemExclusao.get(0).getCompetencia()));
                exercicio = exercicioFacade.getExercicioPorAno(DataUtil.getAno(itemExclusao.get(0).getCompetencia()));
            }

            if (itemExclusao != null) {
                configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(Collections.unmodifiableList(itemExclusao),
                    config, TipoClasseESocial.S3000, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.REGISTROEXCLUSAOS3000,
                    mes, exercicio);
            }

            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Registro não enviado!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    private void validarRegistrosAdicionadosS3000(RegistroExclusaoS3000[] registrosExclusaoS3000) {
        ValidacaoException ve = new ValidacaoException();
        if (registrosExclusaoS3000 == null || registrosExclusaoS3000.length == 0) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum registro selecionado para envio!");
        }
        ve.lancarException();
    }

    public void removerSelecionadoS3000() {
        if (itemExclusaoS3000Selecionados.length == 0) {
            FacesUtil.addOperacaoNaoRealizada("Nenhum registro selecionado para remover!");
        } else {
            itemExclusao.removeAll(Arrays.asList(itemExclusaoS3000Selecionados));
            FacesUtil.addOperacaoRealizada("Selecionados removido com sucesso !");
        }
    }


    private void validarEventoS3000() {
        ValidacaoException ve = new ValidacaoException();
        if (registroExclusaoS3000.getTipoArquivoESocial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Registro.");
        }
        if (empregador == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Empregador deve ser informado.");
        }
        if (registroExclusaoS3000.getTipoArquivoESocial() != null && TipoArquivoESocial.getEventoFolhaPagamento().contains(registroExclusaoS3000.getTipoArquivoESocial())) {
            if (registroExclusaoS3000.getTipoExclusaoEventoFolha() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para exclusão desse evento é necessário informar o tipo de folha.");
            } else {
                if (TipoExclusaoEventoFolha.MENSAL.equals(registroExclusaoS3000.getTipoExclusaoEventoFolha()) && registroExclusaoS3000.getCompetencia() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Informe a Competência da folha de pagamento.");
                }
                if (TipoExclusaoEventoFolha.SALARIO_13.equals(registroExclusaoS3000.getTipoExclusaoEventoFolha()) && registroExclusaoS3000.getExercicio() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Ano da folha de pagamento.");
                }
            }
        }
        ve.lancarException();
        if (registroExclusaoS3000.getVinculoFP() != null || registroExclusaoS3000.getPrestadorServicos() != null) {
            ConfiguracaoEmpregadorESocial empregador;
            if (registroExclusaoS3000.getVinculoFP() != null) {
                empregador = exclusaoEventoEsocialFacade.getContratoFPFacade().buscarEmpregadorPorVinculoFP(registroExclusaoS3000.getVinculoFP());
            } else {
                empregador = exclusaoEventoEsocialFacade.getContratoFPFacade().buscarEmpregadorPorPrestadorServicos(registroExclusaoS3000.getPrestadorServicos());
            }
            validarConfiguracaoEmpregador(empregador, registroExclusaoS3000);
        }

        if (itemExclusao != null) {
            for (RegistroExclusaoS3000 exclusaoS3000 : itemExclusao) {
                if (registroExclusaoS3000.getVinculoFP() != null && exclusaoS3000.getVinculoFP() != null &&
                    exclusaoS3000.getVinculoFP().getId().equals(registroExclusaoS3000.getVinculoFP().getId())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Contrato já informado.");
                    break;
                }
                if (registroExclusaoS3000.getPrestadorServicos() != null && exclusaoS3000.getPrestadorServicos() != null &&
                    exclusaoS3000.getPrestadorServicos().getId().equals(registroExclusaoS3000.getPrestadorServicos().getId())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Prestador de Serviço já informado.");
                    break;
                }
                if (exclusaoS3000.getIdXML().trim().equals(registroExclusaoS3000.getIdXML())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("ID Xml já informado.");
                    break;
                }
            }
        }
        if (isEventoDesligamento()) {
            if (dataInicialDesligamento != null && dataFinalDesligamento == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Foi informado a Data Inicial do filtro, portanto informe a Data Final.");
            }
            if (dataInicialDesligamento == null && dataFinalDesligamento != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Foi informado a Data Final do filtro, portanto informe a Data Inicial.");
            }
        }
        ve.lancarException();
    }

    private void validarConfiguracaoEmpregador(ConfiguracaoEmpregadorESocial empregador, RegistroExclusaoS3000
        registroExclusaoS3000) {
        ValidacaoException ve = new ValidacaoException();
        if (empregador == null || empregador.getCertificado() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrato configuração de empregador e certificado digital para o servidor " + registroExclusaoS3000.getVinculoFP());
        }
        ve.lancarException();
    }

    public void enviarEventoS1298() {
        try {
            validarEnvioEventoS1298();
            registroEventoEsocialS1298.setEntidade(empregador);
            registroEventoEsocialS1298 = registroS1298Facade.salvarRetornando(registroEventoEsocialS1298);
            configuracaoEmpregadorESocialFacade.enviarS1298(registroEventoEsocialS1298);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    private void validarEnvioEventoS1298() {
        ValidacaoException ve = new ValidacaoException();
        if (empregador == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Empregador deve ser informado.");
        }
        if (registroEventoEsocialS1298.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (registroEventoEsocialS1298.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (registroEventoEsocialS1298.getTipoFolhaDePagamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha de Pagamento deve ser informado.");
        }
        ve.lancarException();
    }

    public void enviarEventoS1299() {
        try {
            validarEnvioEventoS1299();
            configuracaoEmpregadorESocialFacade.enviarS1299(registroEventoEsocialS1299);
            registroS1299Facade.salvar(registroEventoEsocialS1299);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    public void enviarEventoS2210() {
        try {
            validarEnvioEvento(itemEventoS2210);
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            configuracaoEmpregadorESocialFacade.enviarS2210(itemEventoS2210, config);

            configuracaoEmpregadorESocialFacade.criarHistoricoEsocial(itemEventoS2210,
                config, TipoClasseESocial.S2210, new Date(), sistemaFacade.getUsuarioCorrente(), ClasseWP.CAT, null, null);

            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada("Registro não enviado! " + e.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    private void validarEnvioEventoS1299() {
        ValidacaoException ve = new ValidacaoException();
        if (registroEventoEsocialS1299.getTipoApuracaoFolha() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Apuração.");
        }
        if (registroEventoEsocialS1299.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Mês.");
        }
        if (registroEventoEsocialS1299.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Ano.");
        }
        if (registroEventoEsocialS1299.getEntidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o CNPJ/Entidade.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposCessao2231() {
        return Util.getListSelectItemSemCampoVazio(TipoCessao2231.values(), false);
    }

    public void adicionarRiscoOcupacional() {
        try {
            itemRiscoOcupacional.add(riscoOcupacional);
            riscoOcupacional = new RiscoOcupacional();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void buscarDetalhesLog() {
        if (empregador != null) {
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            itemDetalheLogErro = logErroEnvioEventoFacade.buscarDetalhesLog(config);
        }

    }

    public void removerItemS2200(ContratoFP contrato) {
        itemContratoFPS2200.remove(contrato);
    }

    public void removerItemS2231(CedenciaContratoFP cedenciaContratoFP) {
        itemCedenciaS2231.remove(cedenciaContratoFP);
    }

    public void removerS2190(ContratoFP itemS2190) {
        itemContratoFPS2190.remove(itemS2190);
    }

    public void removerS2205(ContratoFP itemS2205) {
        itemContratoFPS2205.remove(itemS2205);
    }

    public void removerS2206(ContratoFP itemS2206) {
        mapItemContratoFPS2206.remove(itemS2206);
    }

    public void removerS2299(ExoneracaoRescisao exoneracao) {
        itemExoneracaoS2299.remove(exoneracao);
    }

    public void removerAfastamento(Afastamento afastamento) {
        itemAfastanentoS2230.remove(afastamento);
    }

    public void removerS2300(PrestadorServicos prestador) {
        itemPrestadorServico2300.remove(prestador);
    }

    public void removerS2400(VinculoFP beneficiario) {
        itemEventoS2400.remove(beneficiario);
    }

    public void removerS2410(VinculoFP beneficiario) {
        itemEventoS2410.remove(beneficiario);
    }

    public boolean isArquivoServidor(RegistroEventoEsocial registroEventoEsocial) {
        return registroEventoEsocial.getTipoTrabalhadorEsocial() != null && TipoTrabalhadorEsocial.SERVIDOR.equals(registroEventoEsocial.getTipoTrabalhadorEsocial());
    }

    public boolean isArquivoPrestador(RegistroEventoEsocial registroEventoEsocial) {
        return registroEventoEsocial.getTipoTrabalhadorEsocial() != null && TipoTrabalhadorEsocial.PRESTADOR_SERVICO.equals(registroEventoEsocial.getTipoTrabalhadorEsocial());
    }

    public void enviarEventosIniciais() {
        if (!itemEmpregadors1005.isEmpty()) {
            enviarS1005();
        }
        if (!itemEventoFPS1010.isEmpty()) {
            enviarS1010();
        }
        if (!itemEmpregadorS1020.isEmpty()) {
            enviarS1020();
        }
        if (!itemProcessoAdministrativoS1070.isEmpty()) {
            enviarS1070();
        }
    }


    public void buscarEventosIniciaisParaEnvio() {
        todosEventosEsocial.clear();
        buscarS1005();
        buscarS1010();
        buscarS1020();
        buscarS1070();

        adicionarS1005();
        adicionarS1010();
        adicionarS1020();
        adicionarS1070();
    }

    private void adicionarS1005() {
        for (Entidade entidade : itemEmpregadors1005) {
            TodosEventosEsocial item1005 = new TodosEventosEsocial();
            item1005.setTipoArquivoESocial(TipoArquivoESocial.S1005);
            item1005.setDescricao(entidade.toString());
            todosEventosEsocial.add(item1005);
        }
    }

    private void adicionarS1010() {
        for (EventoFP evento : itemEventoFPS1010) {
            TodosEventosEsocial item1010 = new TodosEventosEsocial();
            item1010.setTipoArquivoESocial(TipoArquivoESocial.S1010);
            item1010.setDescricao(evento.toString());
            todosEventosEsocial.add(item1010);
        }
    }

    private void adicionarS1020() {
        for (Entidade entidade : itemEmpregadorS1020) {
            TodosEventosEsocial itemS1020 = new TodosEventosEsocial();
            itemS1020.setTipoArquivoESocial(TipoArquivoESocial.S1020);
            itemS1020.setDescricao(entidade.toString());
            todosEventosEsocial.add(itemS1020);
        }
    }

    private void adicionarS1070() {
        for (ProcessoAdministrativoJudicial processo : itemProcessoAdministrativoS1070) {
            TodosEventosEsocial itemS1070 = new TodosEventosEsocial();
            itemS1070.setTipoArquivoESocial(TipoArquivoESocial.S1070);
            itemS1070.setDescricao(processo.toString());
            todosEventosEsocial.add(itemS1070);
        }
    }

    public boolean isEventoPrestadorServico(RegistroExclusaoS3000 registro, boolean validarTipoTrabalhador) {
        if (validarTipoTrabalhador) {
            return TipoTrabalhadorEsocial.PRESTADOR_SERVICO.equals(tipoTrabalhadorEsocial) &&
                (TipoArquivoESocial.S1200.equals(registro.getTipoArquivoESocial()) || TipoArquivoESocial.S1210.equals(registro.getTipoArquivoESocial())
                    || TipoArquivoESocial.S2300.equals(registro.getTipoArquivoESocial()) || TipoArquivoESocial.S2306.equals(registro.getTipoArquivoESocial())
                    || TipoArquivoESocial.S2399.equals(registro.getTipoArquivoESocial()));
        }
        return TipoArquivoESocial.S1200.equals(registro.getTipoArquivoESocial()) || TipoArquivoESocial.S1210.equals(registro.getTipoArquivoESocial())
            || TipoArquivoESocial.S2300.equals(registro.getTipoArquivoESocial()) || TipoArquivoESocial.S2306.equals(registro.getTipoArquivoESocial())
            || TipoArquivoESocial.S2399.equals(registro.getTipoArquivoESocial());
    }

    public class TodosEventosEsocial {
        private TipoArquivoESocial tipoArquivoESocial;
        private String descricao;

        public TipoArquivoESocial getTipoArquivoESocial() {
            return tipoArquivoESocial;
        }

        public void setTipoArquivoESocial(TipoArquivoESocial tipoArquivoESocial) {
            this.tipoArquivoESocial = tipoArquivoESocial;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public PessoaFisica getPessoaVinculo() {
        return pessoaVinculo;
    }

    public void setPessoaVinculo(PessoaFisica pessoaVinculo) {
        this.pessoaVinculo = pessoaVinculo;
    }

    public Date getDataInicialContratoPrestador() {
        return dataInicialContratoPrestador;
    }

    public void setDataInicialContratoPrestador(Date dataInicialContratoPrestador) {
        dataInicialContratoPrestador = dataInicialContratoPrestador;
    }

    public PrestadorServicos getPrestadorServicosS2306() {
        return prestadorServicosS2306;
    }

    public void setPrestadorServicosS2306(PrestadorServicos prestadorServicosS2306) {
        this.prestadorServicosS2306 = prestadorServicosS2306;
    }

    public Date getDataInicialContratoPrestadorS2306() {
        return dataInicialContratoPrestadorS2306;
    }

    public void setDataInicialContratoPrestadorS2306(Date dataInicialContratoPrestadorS2306) {
        this.dataInicialContratoPrestadorS2306 = dataInicialContratoPrestadorS2306;
    }

    public Date getDataFinalContratoPrestadorS2306() {
        return dataFinalContratoPrestadorS2306;
    }

    public void setDataFinalContratoPrestadorS2306(Date dataFinalContratoPrestadorS2306) {
        this.dataFinalContratoPrestadorS2306 = dataFinalContratoPrestadorS2306;
    }

    public PrestadorServicos getPrestadorServicosS2399() {
        return prestadorServicosS2399;
    }

    public void setPrestadorServicosS2399(PrestadorServicos prestadorServicosS2399) {
        this.prestadorServicosS2399 = prestadorServicosS2399;
    }

    public Date getDataInicialContratoPrestadorS2399() {
        return dataInicialContratoPrestadorS2399;
    }

    public void setDataInicialContratoPrestadorS2399(Date dataInicialContratoPrestadorS2399) {
        this.dataInicialContratoPrestadorS2399 = dataInicialContratoPrestadorS2399;
    }

    public Date getDataFinalContratoPrestadorS2399() {
        return dataFinalContratoPrestadorS2399;
    }

    public void setDataFinalContratoPrestadorS2399(Date dataFinalContratoPrestadorS2399) {
        this.dataFinalContratoPrestadorS2399 = dataFinalContratoPrestadorS2399;
    }

    public Date getDataFinalContratoPrestador() {
        return dataFinalContratoPrestador;
    }

    public void setDataFinalContratoPrestador(Date dataFinalContratoPrestador) {
        this.dataFinalContratoPrestador = dataFinalContratoPrestador;
    }

    public List<PessoaFisica> completaPessoa(String s) {
        return pessoaFisicaFacade.buscaPessoaPrestadorServicosOrVinculoFiltrando(s.trim());
    }


    public TipoRegimePrevidenciario getTipoRegimePrevidenciarioS1210() {
        return tipoRegimePrevidenciarioS1210;
    }

    public void setTipoRegimePrevidenciarioS1210(TipoRegimePrevidenciario tipoRegimePrevidenciarioS1210) {
        this.tipoRegimePrevidenciarioS1210 = tipoRegimePrevidenciarioS1210;
    }

    public List<SelectItem> getTipoRegime() {
        return Util.getListSelectItem(TipoRegimePrevidenciario.values(), false);
    }

    public List<VinculoFP> completaContratoPorEmpregador(String s) {
        try {
            validarBuscaEmpregador();
            Date data;
            if (isEventoDePagamento()) {
                if (registroExclusaoS3000.getCompetencia() != null) {
                    data = DataUtil.ultimoDiaMes(registroExclusaoS3000.getCompetencia()).getTime();
                } else {
                    data = DataUtil.montaData(31, 12, registroExclusaoS3000.getExercicio().getAno()).getTime();
                }
            } else {
                data = sistemaFacade.getDataOperacao();
            }
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            return contratoFPFacade.buscaContratoFiltrandoAtributosVinculoMatriculaFPPorEmpregador(s.trim(), config, data);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        return Lists.newArrayList();
    }

    public List<PrestadorServicos> completaPrestadorPorEmpregador(String s) {
        try {
            validarBuscaEmpregador();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            return prestadorServicosFacade.buscaPrestadorServicosNomeCPFEmpregador(s.trim(), config);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getTipoOperacao() {
        List<SelectItem> retorno = new ArrayList();
        retorno.add(new SelectItem(null, "Automático"));
        for (TipoOperacaoESocial value : TipoOperacaoESocial.values()) {
            retorno.add(new SelectItem(value, value.toString()));
        }
        return retorno;
    }

    public void marcarTodosItensS2200() {
        if (marcarTodosContratos2200) {
            itemContratoFPS2200Selecionado = itemContratoFPS2200.toArray(new VWContratoFP[0]);
        } else {
            itemContratoFPS2200Selecionado = null;
        }
    }

    public void marcarTodosItensS2231() {
        if (marcarTodasCedenciaS2231) {
            itemCedenciaS2231Selecionados = itemCedenciaS2231.toArray(new CedenciaContratoFP[0]);
        } else {
            itemCedenciaS2231Selecionados = null;
        }
    }

    public void marcarTodosItensS3000() {
        if (marcarTodosExclusaoSS3000) {
            itemExclusaoS3000Selecionados = itemExclusao.toArray(new RegistroExclusaoS3000[0]);
        } else {
            itemExclusaoS3000Selecionados = null;
        }
    }

    public void marcarTodosItensS2299() {
        if (marcarTodasExoneracoesS2299) {
            itemExoneracaoS2299Selecionado = itemExoneracaoS2299.toArray(new ExoneracaoRescisao[0]);
        } else {
            itemExoneracaoS2299Selecionado = null;
        }
    }

    public List<SelectItem> getHierarquias() {
        return Util.getListSelectItem(hierarquiasOrganizacionais);
    }

    public void carregarHierarquiasOrganizacionais() {
        hierarquiasOrganizacionais = Lists.newArrayList();
        hierarquiaOrganizacional = null;
        hierarquiasOrganizacionais = hierarquiaOrganizacionalFacade.buscarHierarquiasDoEmpregadorEsocial(empregador, sistemaFacade.getDataOperacao());
    }

    public List<SelectItem> getTiposApuracoesFolha() {
        return Util.getListSelectItem(TipoApuracaoFolha.values());
    }

    public List<SelectItem> getTiposArquivoEsocial() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (TipoArquivoESocial value : TipoArquivoESocial.values()) {
            if (!getEventosPrimeiraFase().contains(value) && !getEventosExcluidosDeS3000().contains(value)) {
                retorno.add(new SelectItem(value, value.getCodigo()));
            }
        }
        return retorno;
    }
    private List<TipoArquivoESocial> getEventosExcluidosDeS3000() {
        List<TipoArquivoESocial> retorno = Lists.newArrayList();
        retorno.add(TipoArquivoESocial.S1298);
        retorno.add(TipoArquivoESocial.S1299);
        retorno.add(TipoArquivoESocial.S1300);
        retorno.add(TipoArquivoESocial.S1295);
        retorno.add(TipoArquivoESocial.S2245);
        retorno.add(TipoArquivoESocial.S2250);
        retorno.add(TipoArquivoESocial.S3000);
        retorno.add(TipoArquivoESocial.S5001);
        retorno.add(TipoArquivoESocial.S5002);
        retorno.add(TipoArquivoESocial.S5003);
        retorno.add(TipoArquivoESocial.S5011);
        retorno.add(TipoArquivoESocial.S5012);
        retorno.add(TipoArquivoESocial.S5013);
        return retorno;
    }

    private List<TipoArquivoESocial> getEventosPrimeiraFase() {
        List<TipoArquivoESocial> retorno = Lists.newArrayList();
        retorno.add(TipoArquivoESocial.S1000);
        retorno.add(TipoArquivoESocial.S1005);
        retorno.add(TipoArquivoESocial.S1010);
        retorno.add(TipoArquivoESocial.S1020);
        retorno.add(TipoArquivoESocial.S1030);
        retorno.add(TipoArquivoESocial.S1035);
        retorno.add(TipoArquivoESocial.S1040);
        retorno.add(TipoArquivoESocial.S1050);
        retorno.add(TipoArquivoESocial.S1060);
        retorno.add(TipoArquivoESocial.S1070);
        return retorno;
    }

    public List<SelectItem> getTiposExclusaoEventoFolha() {
        return Util.getListSelectItem(TipoExclusaoEventoFolha.values());
    }

    public void limparInformacoesFolha(RegistroEventoEsocial registroEventoEsocial) {
        registroEventoEsocial.setMes(null);
        registroEventoEsocial.setExercicio(null);
    }

    public void atribuirApuracaoFolha(RegistroEventoEsocial registroEventoEsocial) {
        if (TipoTrabalhadorEsocial.PRESTADOR_SERVICO.equals(registroEventoEsocial.getTipoTrabalhadorEsocial())) {
            registroEventoEsocial.setTipoApuracaoFolha(TipoApuracaoFolha.MENSAL);
            limparInformacoesFolha(registroEventoEsocial);
        }
    }

    public List<ContratoFP> getItensS2206() {
        return Lists.newArrayList(mapItemContratoFPS2206.keySet());
    }

    public List<SelectItem> getEntidade() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (ConfiguracaoEmpregadorESocial configuracao : configuracaoEmpregadorESocialFacade.lista()) {
            toReturn.add(new SelectItem(configuracao.getEntidade(), configuracao.getEntidade().getPessoaJuridica().getCnpj() + " - " + configuracao.getEntidade().getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> buscarTiposPessoaESocial() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (TipoTrabalhadorEsocial tipo : TipoTrabalhadorEsocial.values()) {
            if (!TipoTrabalhadorEsocial.TRABALHADOR_SEM_VINCULO.equals(tipo))
                retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<CAT> completarCAT(String parte) {
        ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
        config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
        return catFacade.buscarCatPorPessoaCPF(parte.trim(), apenasNaoEnviados, config);
    }

    public void adicionarCat() {
        itemEventoS2210.add(cat);
        cat = new CAT();
    }

    public void removerCAT(CAT cat) {
        itemEventoS2210.remove(cat);
    }

    public void buscarCAT() {
        try {
            validarCAT();
            itemEventoS2210.clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
            config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
            List<CAT> itemCat = catFacade.buscarCatPorPessoaCPF(null, apenasNaoEnviados, config);
            itemEventoS2210.addAll(itemCat);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCAT() {
        ValidacaoException ve = new ValidacaoException();
        if (empregador == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Empregador.");
        }
        ve.lancarException();
    }

    public void limparInformacoesFolha(RegistroEventoEsocialS1299 registroEventoEsocial) {
        registroEventoEsocial.setMes(null);
        registroEventoEsocial.setExercicio(null);
    }

    public String getVinculoFPOrPrestadorEventoPagamento(String identificadorWP) {
        if (Strings.isNullOrEmpty(identificadorWP))
            return null;

        VinculoFP vinculoFp = registroESocialFacade.getVinculoFPFichaFinanceiraFP(identificadorWP);
        if (vinculoFp != null)
            return vinculoFp.toString();

        PrestadorServicos prestador = registroESocialFacade.getPrestadorServicosFichaRPA(identificadorWP);
        if (prestador != null)
            return prestador.toString();

        return "";
    }


    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        setarEvento(tab);
    }

    private boolean futuresDone() {
        if (futureAssistenteBarraProgresso != null) {
            boolean terminou = true;
            for (Future<AssistenteBarraProgresso> future : futureAssistenteBarraProgresso) {
                if (!future.isDone()) {
                    terminou = false;
                }
            }
            return terminou;
        }
        return false;
    }

    public void verificarFimEnvioEvento() {
        if (futuresDone()) {
            futureAssistenteBarraProgresso = null;
            FacesUtil.executaJavaScript("terminaEnvio()");
            if (assistenteBarraProgresso.getQuantidadeErros() > 0) {
                String mensagem = "Atenção! Ocorreu <b>" + assistenteBarraProgresso.getQuantidadeErros() + "</b> erro(s) de <b>" + qtdeRegistrosParaEnvio + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.";
                assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(mensagem));
            }
            if (assistenteBarraProgresso.getMensagensValidacaoFacesUtil() != null && !assistenteBarraProgresso.getMensagensValidacaoFacesUtil().isEmpty()) {
                for (FacesMessage facesMessage : assistenteBarraProgresso.getMensagensValidacaoFacesUtil()) {
                    FacesUtil.addWarn("Atenção", facesMessage.getDetail());
                }
            } else {
                FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
            }
            FacesUtil.executaJavaScript("dialogo.hide()");
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:detalhe-log");
        }
    }
}
