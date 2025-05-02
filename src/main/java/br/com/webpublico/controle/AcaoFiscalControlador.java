package br.com.webpublico.controle;

import br.com.webpublico.DateUtils;
import br.com.webpublico.consultaentidade.*;
import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ImpressaoDemonstrativoAcaoFiscal;
import br.com.webpublico.entidadesauxiliares.ImprimeRelacaoNotas;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.DocumentoFiscalEmail;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AcaoFiscalFacade;
import br.com.webpublico.negocios.AutoInfracaoFiscalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.nfse.domain.NotaFiscal;
import br.com.webpublico.nfse.domain.dtos.FiltroNotaFiscal;
import br.com.webpublico.nfse.domain.dtos.TotalizadorRelatorioNotasFiscaisDTO;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.nfse.relatorio.controladores.RelatorioNotasFiscaisControlador;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

@ManagedBean(name = "acaoFiscalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "editarAcaoFiscal", pattern = "/acao-fiscal/editar/#{acaoFiscalControlador.id}/", viewId = "/faces/tributario/fiscalizacao/acaofiscal/edita.xhtml"),
    @URLMapping(id = "listarAcaoFiscal", pattern = "/acao-fiscal/listar/", viewId = "/faces/tributario/fiscalizacao/acaofiscal/lista.xhtml"),
    @URLMapping(id = "verAcaoFiscal", pattern = "/acao-fiscal/ver/#{acaoFiscalControlador.id}/", viewId = "/faces/tributario/fiscalizacao/acaofiscal/visualizar.xhtml")
})
public class AcaoFiscalControlador extends PrettyControlador<AcaoFiscal> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(AcaoFiscalControlador.class);

    @EJB
    private AcaoFiscalFacade acaoFiscalFacade;
    @EJB
    private NotaFiscalFacade notaFiscalFacade;

    private BigDecimal valorIndiceEncontrado;
    private BigDecimal multa;
    private Integer ano;
    private Long numeroRegistro;

    private String fundamentacaoAutoInfracao;
    private String historicoAutoInfracao;
    private String descricaoArquivoUpload;
    private String mensagemEmail;
    private String emails;

    private Date dataParaValorIndice;
    private AlteracaoDataArbitramento alteracaoDataArbitramento;

    private Boolean supervisor;
    private Boolean ehAlteracaoNotaCanceladaExtraviada;
    private Boolean emailAnexo;

    private SituacoesAcaoFiscal situacoesAcaoFiscal;
    private SituacaoAcaoFiscal situacaoAcaoFiscal;
    private ConverterAutoComplete converterCadastroEconomico;
    private DocumentoOficial documentoOficial;
    private LancamentoDoctoFiscal lancamentoDoctoFiscal;
    private LancamentoDoctoFiscal lancamentoDoctoFiscalAuxiliar;
    private LancamentoMultaFiscal lancamentoMultaFiscalRecuperada;
    private LancamentoMultaFiscal lancamentoMultaFiscal;
    private LevantamentoContabil[] levantamentosContabeisSelecionados;
    private LevantamentoContabil levantamentoContabil;

    private EnquadramentoFiscal enquadramentoFiscal;

    private AutoInfracaoFiscal autoInfracaoFiscalSelecionado;
    private ParametroFiscalizacao parametroFiscalizacao;
    private TotalizadorLancamentoContabil totalizador;
    private ProgramacaoFiscal programacaoRecuperada;
    private RegistroLancamentoContabil registro;

    private DoctoAcaoFiscal documentoSelecionado;
    private ArquivoAcaoFiscal anexoSelecionado;
    private FiltroAcaoFiscal filtros;
    private CadastroAidf aidf;

    private AssistenteBarraProgresso assistente;
    private Future<AcaoFiscal> futureAcaoFiscal;

    private List<TotalizadorLancamentoContabil> listaTotalizador;
    private List<LancamentoMultaFiscal> multasALancar;
    private List<LancamentoMultaFiscal> multasSelecionadas;
    private List<LancamentoContabilFiscal> lancamentoParaMulta;
    private List<LancamentoDoctoFiscal> notasParaMulta;
    private Map<Integer, Integer> diaVencimentoPorAno = Maps.newHashMap();
    private List<LancamentoDoctoFiscal> notasFiscaisDoLancamento;
    private List<LancamentoMultaFiscal> lancamentosMultasFiscais;
    private Map<String, ItemCalculoIss> aliquotasVariadas;
    private Map<ValoresIssPago, BigDecimal> valoresPagos;
    private LazyDataModel<NotaFiscal> notasFiscaisModel;
    private TotalizadorRelatorioNotasFiscaisDTO totalizadorNotasFiscais;
    private List<FieldConsultaEntidade> fieldsConsultaNsfe;
    private List<FiltroConsultaEntidade> filtrosConsultaNsfe;
    private ConsultaEntidadeController.ConverterFieldConsultaEntidade converterFieldConsulta;

    private Boolean fiscalTributarioSupervisor;

    public AcaoFiscalControlador() {
        super(AcaoFiscal.class);
        emailAnexo = Boolean.TRUE;
    }

    public List<LancamentoMultaFiscal> getMultasALancar() {
        return multasALancar;
    }

    public void setMultasALancar(List<LancamentoMultaFiscal> multasALancar) {
        this.multasALancar = multasALancar;
    }

    public List<LancamentoMultaFiscal> getMultasSelecionadas() {
        return multasSelecionadas;
    }

    public void setMultasSelecionadas(List<LancamentoMultaFiscal> multasSelecionadas) {
        this.multasSelecionadas = multasSelecionadas;
    }

    public AlteracaoDataArbitramento getAlteracaoDataArbitramento() {
        return alteracaoDataArbitramento;
    }

    public void setAlteracaoDataArbitramento(AlteracaoDataArbitramento alteracaoDataArbitramento) {
        this.alteracaoDataArbitramento = alteracaoDataArbitramento;
    }

    public String getFundamentacaoAutoInfracao() {
        return fundamentacaoAutoInfracao;
    }

    public void setFundamentacaoAutoInfracao(String fundamentacaoAutoInfracao) {
        this.fundamentacaoAutoInfracao = fundamentacaoAutoInfracao;
    }

    public String getHistoricoAutoInfracao() {
        return historicoAutoInfracao;
    }

    public void setHistoricoAutoInfracao(String historicoAutoInfracao) {
        this.historicoAutoInfracao = historicoAutoInfracao;
    }

    public List<LancamentoDoctoFiscal> getNotasFiscaisDoLancamento() {
        return notasFiscaisDoLancamento;
    }

    public void setNotasFiscaisDoLancamento(List<LancamentoDoctoFiscal> notasFiscaisDoLancamento) {
        this.notasFiscaisDoLancamento = notasFiscaisDoLancamento;
    }

    public EnquadramentoFiscal getEnquadramentoFiscal() {
        return enquadramentoFiscal;
    }

    public void setEnquadramentoFiscal(EnquadramentoFiscal enquadramentoFiscal) {
        this.enquadramentoFiscal = enquadramentoFiscal;
    }

    public Boolean getFiscalTributarioSupervisor() {
        return fiscalTributarioSupervisor;
    }

    public void setFiscalTributarioSupervisor(Boolean fiscalTributarioSupervisor) {
        this.fiscalTributarioSupervisor = fiscalTributarioSupervisor;
    }

    public TotalizadorRelatorioNotasFiscaisDTO getTotalizadorNotasFiscais() {
        return totalizadorNotasFiscais;
    }

    public void setTotalizadorNotasFiscais(TotalizadorRelatorioNotasFiscaisDTO totalizadorNotasFiscais) {
        this.totalizadorNotasFiscais = totalizadorNotasFiscais;
    }

    public RegistroLancamentoContabil getRegistroAtivo() {
        for (RegistroLancamentoContabil registro : selecionado.getLancamentosContabeis()) {
            if (!RegistroLancamentoContabil.Situacao.CANCELADO.equals(registro.getSituacao()) &&
                !RegistroLancamentoContabil.Situacao.ESTORNADO.equals(registro.getSituacao())) {
                return registro;
            }
        }
        return null;
    }

    public RegistroLancamentoContabil getRegistro() {
        return registro;
    }

    public void setRegistro(RegistroLancamentoContabil registro) {
        this.registro = registro;
    }

    public FiltroAcaoFiscal getFiltros() {
        if (filtros == null) {
            filtros = new FiltroAcaoFiscal();
        }
        return filtros;
    }

    public void setFiltros(FiltroAcaoFiscal filtro) {
        this.filtros = filtro;
    }

    public LancamentoMultaFiscal getLancamentoMultaFiscalRecuperada() {
        return lancamentoMultaFiscalRecuperada;
    }

    public void setLancamentoMultaFiscalRecuperada(LancamentoMultaFiscal lancamentoMultaFiscalRecuperada) {
        this.lancamentoMultaFiscalRecuperada = lancamentoMultaFiscalRecuperada;
    }

    public Date getDataParaValorIndice() {
        return dataParaValorIndice;
    }

    public void setDataParaValorIndice(Date dataParaValorIndice) {
        this.dataParaValorIndice = dataParaValorIndice;
    }

    public BigDecimal getValorIndiceEncontrado() {
        return valorIndiceEncontrado;
    }

    public void setValorIndiceEncontrado(BigDecimal valorIndiceEncontrado) {
        this.valorIndiceEncontrado = valorIndiceEncontrado;
    }

    public LevantamentoContabil[] getLevantamentosContabeisSelecionados() {
        return levantamentosContabeisSelecionados;
    }

    public void setLevantamentosContabeisSelecionados(LevantamentoContabil[] levantamentosContabeisSelecionados) {
        this.levantamentosContabeisSelecionados = levantamentosContabeisSelecionados;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public SituacaoAcaoFiscal getSituacaoAcaoFiscal() {
        return situacaoAcaoFiscal;
    }

    public void setSituacaoAcaoFiscal(SituacaoAcaoFiscal situacaoAcaoFiscal) {
        this.situacaoAcaoFiscal = situacaoAcaoFiscal;
    }

    public AcaoFiscal getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(AcaoFiscal selecionado) {
        this.selecionado = selecionado;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return acaoFiscalFacade.getCadastroEconomicoFacade().listaCadastroEconomicoPorPessoa(parte.trim());
    }

    public List<NumeroSerie> completaSerie(String parte) {
        return acaoFiscalFacade.getNumeroSerieFacade().listaFiltrando(parte.trim(), "numeroSerie");
    }

    public List<MultaFiscalizacao> completarMulta(String parte) {
        return acaoFiscalFacade.getMultaFiscalizacaoFacade().listaFiltrando(parte.trim());
    }

    public List<MultaFiscalizacao> completarMultaMensal(String parte) {
        return acaoFiscalFacade.getMultaFiscalizacaoFacade().listaFiltrando(parte.trim(), true);
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public LancamentoDoctoFiscal getLancamentoDoctoFiscal() {
        return lancamentoDoctoFiscal;
    }

    public void setLancamentoDoctoFiscal(LancamentoDoctoFiscal lancamentoDoctoFiscal) {
        this.lancamentoDoctoFiscal = lancamentoDoctoFiscal;
    }

    public LancamentoMultaFiscal getLancamentoMultaFiscal() {
        return lancamentoMultaFiscal;
    }

    public void setLancamentoMultaFiscal(LancamentoMultaFiscal lancamentoMultaFiscal) {
        this.lancamentoMultaFiscal = lancamentoMultaFiscal;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public Boolean getEhAlteracaoNotaCanceladaExtraviada() {
        return ehAlteracaoNotaCanceladaExtraviada;
    }

    public void setEhAlteracaoNotaCanceladaExtraviada(Boolean ehAlteracaoNotaCanceladaExtraviada) {
        this.ehAlteracaoNotaCanceladaExtraviada = ehAlteracaoNotaCanceladaExtraviada;
    }

    public List<LancamentoContabilFiscal> getLancamentoParaMulta() {
        return lancamentoParaMulta;
    }

    public void setLancamentoParaMulta(List<LancamentoContabilFiscal> lancamentoParaMulta) {
        this.lancamentoParaMulta = lancamentoParaMulta;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/acao-fiscal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public void novo() {
        super.novo();
        this.registro = new RegistroLancamentoContabil(selecionado);
        this.situacoesAcaoFiscal = new SituacoesAcaoFiscal();
        this.lancamentoDoctoFiscal = new LancamentoDoctoFiscal();
        this.lancamentoDoctoFiscal.setPorcentagemISS(primeiraAliquotaIss());
        this.lancamentoDoctoFiscalAuxiliar = new LancamentoDoctoFiscal();
        this.lancamentoMultaFiscal = new LancamentoMultaFiscal();
        this.lancamentosMultasFiscais = new ArrayList<>();
        this.listaTotalizador = new ArrayList<>();
        this.lancamentoParaMulta = Lists.newArrayList();
        this.notasParaMulta = Lists.newArrayList();
        ehAlteracaoNotaCanceladaExtraviada = false;
        programacaoRecuperada = acaoFiscalFacade.getProgramacaoFiscalFacade().recuperar(selecionado.getProgramacaoFiscal().getId());
    }

    @URLAction(mappingId = "editarAcaoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        try {
            super.editar();
            this.registro = new RegistroLancamentoContabil(selecionado);
            this.selecionado.setCadastroEconomico(acaoFiscalFacade.buscarCadastroEconomicoDaAcaoFiscal(selecionado));
            this.situacoesAcaoFiscal = new SituacoesAcaoFiscal();
            this.lancamentoDoctoFiscal = new LancamentoDoctoFiscal();
            this.lancamentoDoctoFiscal.setPorcentagemISS(primeiraAliquotaIss());
            this.lancamentoDoctoFiscalAuxiliar = new LancamentoDoctoFiscal();
            this.lancamentoMultaFiscal = new LancamentoMultaFiscal();
            this.lancamentosMultasFiscais = new ArrayList<>();
            this.parametroFiscalizacao = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarParametroFiscalizacao(getSistemaControlador().getExercicioCorrente());
            parametroFiscalizacao = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperar(parametroFiscalizacao.getId());
            this.listaTotalizador = new ArrayList<>();
            this.lancamentoParaMulta = Lists.newArrayList();
            this.notasParaMulta = Lists.newArrayList();
            if (selecionado.getDataArbitramento() == null
                || selecionado.getUfmArbitramento() == null
                || selecionado.getLancamentosContabeis() == null
                || selecionado.getLancamentosContabeis().isEmpty()) {
                carregarMesesPorPeriodo();
            }
            this.multasSelecionadas = Lists.newArrayList();
            inicializarVariaveis();
            buildFieldsNotaFiscal();
            this.enquadramentoFiscal = acaoFiscalFacade.getCadastroEconomicoFacade().buscarEnquadramentoFiscalVigente(selecionado.getCadastroEconomico());
        } catch (Exception e) {
            logger.debug("Erro ao Editar Ação Fiscal", e);
        }
    }

    private void inicializarVariaveis() {
        emails = selecionado.getCadastroEconomico().getPessoa().getEmail();
        programacaoRecuperada = acaoFiscalFacade.getProgramacaoFiscalFacade().recuperar(selecionado.getProgramacaoFiscal().getId());
        UsuarioSistema usuarioCorrente = acaoFiscalFacade.getSistemaFacade().getUsuarioCorrente();
        usuarioCorrente = acaoFiscalFacade.getUsuarioSistemaFacade().recuperar(usuarioCorrente.getId());
        fiscalTributarioSupervisor = usuarioCorrente.isFiscalTributarioSupervisor();
    }

    @URLAction(mappingId = "verAcaoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        inicializarVariaveis();
        buildFieldsNotaFiscal();
        consultarNotasFiscais();
    }

    public void salvarAcaoToda() {
        ano = null;
        salvar();
    }

    @Override
    public void salvar() {
        try {
            if (numeroRegistro != null && ano != null) {
                validarCamposAcaoFiscalExercicio();
                Util.validarCampos(selecionado);
                salvarExercicioSelecionado();
            } else {
                validarCamposAcaoFiscal();
                salvarAssincrono();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar Ação Fiscal ", e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar Ação Fiscal. Detalhes: " + e.getMessage());
        }
    }

    public void atribuirExercicioSalvar(Long numeroRegistro, Integer ano) {
        this.ano = ano;
        this.numeroRegistro = numeroRegistro;
    }

    public void addFiltrosPadraoConsultaNotas(ConsultaEntidade consulta) {
        if (consulta == null) return;
        List<FiltroConsultaEntidade> filtros = Lists.newArrayList();
        for (FieldConsultaEntidade pesquisavel : consulta.getPesquisaveis()) {
            if (pesquisavel.getValor().equals("vw.prestador_inscricaocadastral")) {
                FiltroConsultaEntidade filtro = new FiltroConsultaEntidade(pesquisavel);
                filtro.setOperacao(Operador.IGUAL);
                filtro.setOperadorLogico(OperadorLogico.AND);
                filtro.setValor(selecionado.getCadastroEconomico().getInscricaoCadastral());
                filtros.add(filtro);
            }
            if (pesquisavel.getValor().equals("vw.emissao")) {
                FiltroConsultaEntidade filtroPeriodoInicial = new FiltroConsultaEntidade(pesquisavel);
                FiltroConsultaEntidade filtroPeriodoFinal = new FiltroConsultaEntidade(pesquisavel);

                filtroPeriodoInicial.setOperacao(Operador.MAIOR_IGUAL);
                filtroPeriodoInicial.setOperadorLogico(OperadorLogico.AND);
                filtroPeriodoInicial.setValor(selecionado.getDataLevantamentoInicial());

                filtroPeriodoFinal.setOperacao(Operador.MENOR_IGUAL);
                filtroPeriodoFinal.setOperadorLogico(OperadorLogico.AND);
                filtroPeriodoFinal.setValor(selecionado.getDataLevantamentoFinal());

                filtros.add(filtroPeriodoInicial);
                filtros.add(filtroPeriodoFinal);
            }
        }
        consulta.setFiltros(filtros);
    }

    private void salvarAssincrono() {
        assistente = new AssistenteBarraProgresso();
        assistente.setExecutando(true);
        assistente.setSelecionado(selecionado);
        assistente.setDescricaoProcesso("Ação Fiscal " + selecionado.toString());
        assistente.setUsuarioSistema(acaoFiscalFacade.getSistemaFacade().getUsuarioCorrente());
        futureAcaoFiscal = acaoFiscalFacade.salvarAssincrono(assistente);
        abrirDialogProgressBar("dialogProgresSalvar.show()");
        executarPoll("pollSalvar.start()");
    }

    private void abrirDialogProgressBar(String dialog) {
        FacesUtil.executaJavaScript(dialog);
    }

    private void executarPoll(String idPoll) {
        FacesUtil.executaJavaScript(idPoll);
    }

    public void abortar() {
        if (futureAcaoFiscal != null) {
            futureAcaoFiscal.cancel(true);
            assistente.setExecutando(false);
        }
    }

    public void finalizarBarraProgressao() {
        if (!assistente.isExecutando()) {
            FacesUtil.executaJavaScript("dialogProgresSalvar.hide()");
            FacesUtil.atualizarComponente("formDialogProgresSalvar:panelBarraSalvar");
            try {
                if (futureAcaoFiscal.get() != null) {
                    selecionado = futureAcaoFiscal.get();
                    FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                    FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
                }
            } catch (Exception e) {
                logger.error("Erro ao salvar ação fiscal. ", e);
                FacesUtil.addError("Erro ao salvar ação fiscal!", e.getMessage());
            }
        }
    }

    private void salvarExercicioSelecionado() {
        if (selecionado.getLancamentosContabeis() != null) {
            for (RegistroLancamentoContabil registroLancamento : selecionado.getLancamentosContabeis()) {
                if (registroLancamento.getNumeroRegistro().equals(numeroRegistro)) {
                    for (LancamentoContabilFiscal lancamentoContabil : registroLancamento.getLancamentosContabeis()) {
                        if (lancamentoContabil.getAno().equals(ano)) {
                            acaoFiscalFacade.salvarLancamentoContabil(lancamentoContabil);
                        }
                    }
                }
            }
        }
        editar();
        FacesUtil.atualizarComponente("Formulario");
    }

    public void salvarSemValidar() {
        try {
            getFacede().salvar(selecionado);
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void validarCamposNegativosDoctoFiscal(ValidacaoException ve) {
        if (lancamentoDoctoFiscal.getNumeroNotaFiscal() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Número da Nota Fiscal Maior que Zero.");
        }
        if ((!lancamentoDoctoFiscal.getNotaCancelada() && !lancamentoDoctoFiscal.getNotaExtraviada()) && lancamentoDoctoFiscal.getValorApurado().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Valor Apurado Maior que Zero.");
        }
        if ((!lancamentoDoctoFiscal.getNotaCancelada() && !lancamentoDoctoFiscal.getNotaExtraviada()) && lancamentoDoctoFiscal.getValorNotaFiscal().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Valor da Nota Fiscal Maior que Zero.");
        }
        if (((!lancamentoDoctoFiscal.getNotaCancelada() && !lancamentoDoctoFiscal.getNotaExtraviada()) && !lancamentoDoctoFiscal.getNaoTributada()) && lancamentoDoctoFiscal.getPorcentagemISS().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Valor do ISS Maior que Zero.");
        }
        if (lancamentoDoctoFiscal.getNaoTributada() && lancamentoDoctoFiscal.getTipoNaoTributacao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe um tipo de não tributação para a nota fiscal");
        }
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = new ArrayList<>();
        for (Mes mes : Mes.values()) {
            retorno.add(new SelectItem(mes, mes.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getSeries() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (NumeroSerie serie : this.acaoFiscalFacade.getNumeroSerieFacade().lista()) {
            retorno.add(new SelectItem(serie, serie.getDescricao()));
        }
        return retorno;
    }

    private void validarCamposAcaoFiscal() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCadastroEconomico() == null || selecionado.getCadastroEconomico().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Cadastro Econômico!");
        }
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (!selecionado.getLancamentosContabeis().isEmpty()) {
            for (RegistroLancamentoContabil reg : selecionado.getLancamentosContabeis()) {
                if (reg.getSituacao().equals(RegistroLancamentoContabil.Situacao.AGUARDANDO)) {
                    for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
                        valorTotal = valorTotal.add(lcf.getCorrecao()).add(lcf.getIssDevido()).add(lcf.getJuros()).add(lcf.getMulta());
                        if (lcf.getAliquotaISS().compareTo(BigDecimal.ZERO) < 0) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("Valores negativos de alíquota na tabela de lançamentos contábeis!");
                        } else if (lcf.getValorDeclaradoInvalido()) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe ISS Lançado para o mês " + lcf.getMes() + "/" + lcf.getAno() + ". Informe o Valor Declarado!");
                        }
                    }
                }
            }
        }
        if (valorTotal.compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Valores Totais negativos na tabela de lançamentos contábeis!");
        }
        ve.lancarException();
    }

    private void validarCamposAcaoFiscalExercicio() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCadastroEconomico() == null || selecionado.getCadastroEconomico().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Cadastro Econômico!");
        }
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (!selecionado.getLancamentosContabeis().isEmpty()) {
            for (RegistroLancamentoContabil reg : selecionado.getLancamentosContabeis()) {
                if (reg.getNumeroRegistro().equals(numeroRegistro) && RegistroLancamentoContabil.Situacao.AGUARDANDO.equals(reg.getSituacao())) {
                    for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
                        if (lcf.getAno().equals(ano)) {
                            valorTotal = valorTotal.add(lcf.getCorrecao()).add(lcf.getIssDevido()).add(lcf.getJuros()).add(lcf.getMulta());
                            if (lcf.getAliquotaISS().compareTo(BigDecimal.ZERO) < 0) {
                                ve.adicionarMensagemDeOperacaoNaoPermitida("Valores negativos de alíquota na tabela de lançamentos contábeis!");
                            } else if (lcf.getValorDeclaradoInvalido()) {
                                ve.adicionarMensagemDeOperacaoNaoPermitida("Existe ISS Lançado para o mês " + lcf.getMes() + "/" + lcf.getAno() + ". Informe o Valor Declarado!");
                            }
                        }
                    }
                }
            }
        }
        if (valorTotal.compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Valores Totais negativos na tabela de lançamentos contábeis!");
        }
        ve.lancarException();
    }

    public List<SelectItem> getListaSituacaoAcaoFiscal() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (SituacaoAcaoFiscal object : SituacaoAcaoFiscal.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao().toString()));
        }
        return toReturn;
    }

    public void addSituacaoAcaoFiscal() {
        situacoesAcaoFiscal.setData(new Date());
        situacoesAcaoFiscal.setAcaoFiscal(selecionado);
        selecionado.setSituacaoAcaoFiscal(situacoesAcaoFiscal.getSituacaoAcaoFiscal());
        selecionado.getSituacoesAcaoFiscal().add(situacoesAcaoFiscal);
        situacoesAcaoFiscal = new SituacoesAcaoFiscal();
    }

    public void adicionaDoctoFiscal() {
        try {
            boolean ehAlteracao = lancamentoDoctoFiscal.getId() != null;

            lancamentoDoctoFiscal.setAcaoFiscal(selecionado);

            validarCamposDoctoFiscal();
            validarNotaFiscal();
            validarNotaFiscalPrazoFiscalizacao();

            if (!ehAlteracao && lancamentoDoctoFiscalAuxiliar != null && lancamentoDoctoFiscalAuxiliar.getAidf() != null) {
                if (lancamentoDoctoFiscal.getAidf() != null) {
                    if (lancamentoDoctoFiscalAuxiliar.getAidf().equals(lancamentoDoctoFiscal.getAidf()) && (lancamentoDoctoFiscalAuxiliar.getNumeroNotaFiscal() + 1)
                        != lancamentoDoctoFiscal.getNumeroNotaFiscal()) {
                        FacesUtil.addAtencao("Houve quebra de sequência nos números das notas fiscais!");
                    }
                }
            }
            BigDecimal aliquotaUtilizada = lancamentoDoctoFiscal.getPorcentagemISS();
            if (!lancamentoDoctoFiscal.getNotaCancelada()) {
                lancamentoDoctoFiscal.setNotaCancelada(false);
            }
            if (!lancamentoDoctoFiscal.getNotaExtraviada()) {
                lancamentoDoctoFiscal.setNotaExtraviada(false);
            }
            recuperaDadosLancamentoDoctoOficial();

            if (lancamentoDoctoFiscal.getId() != null) {
                selecionado.getLancamentoDoctoFiscal().remove(lancamentoDoctoFiscal);
            }
            selecionado.getLancamentoDoctoFiscal().add(lancamentoDoctoFiscal);

            aidf = lancamentoDoctoFiscal.getAidf();
            lancamentoDoctoFiscal = new LancamentoDoctoFiscal();
            lancamentoDoctoFiscal.setPorcentagemISS(aliquotaUtilizada);
            if (!ehAlteracao) {
                setDadosLancamentoDoctoOficial();
            }

            Collections.sort(selecionado.getLancamentoDoctoFiscal());
            ehAlteracaoNotaCanceladaExtraviada = false;
            selecionado = acaoFiscalFacade.salvaRetornando(selecionado);

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.debug("Erro Adicionando Docto Fiscal", e);
        }
    }

    private void recuperaDadosLancamentoDoctoOficial() {
        this.lancamentoDoctoFiscalAuxiliar = (LancamentoDoctoFiscal) Util.clonarObjeto(this.lancamentoDoctoFiscal);
    }

    private void setDadosLancamentoDoctoOficial() {
        if (lancamentoDoctoFiscalAuxiliar != null && aidf != null) {
            if (aidf.equals(lancamentoDoctoFiscalAuxiliar.getAidf())) {
                this.lancamentoDoctoFiscal.setAidf(lancamentoDoctoFiscalAuxiliar.getAidf());
                this.lancamentoDoctoFiscal.setNumeroNotaFiscal(lancamentoDoctoFiscalAuxiliar.getNumeroNotaFiscal() + 1);
                this.lancamentoDoctoFiscal.setSerie(lancamentoDoctoFiscalAuxiliar.getSerie());
            }
        }
    }

    private void validarCamposDoctoFiscal() {
        ValidacaoException ve = new ValidacaoException();
        if (lancamentoDoctoFiscal.getNumeroNotaFiscal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Número da Nota Fiscal.");
        }
        if (lancamentoDoctoFiscal.getSerie() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Série.");
        }
        if ((!lancamentoDoctoFiscal.getNotaCancelada() && !lancamentoDoctoFiscal.getNotaExtraviada()) && lancamentoDoctoFiscal.getDataEmissao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data de Emissão.");
        }
        if (lancamentoDoctoFiscal.getDataEmissao() != null && !notaFiscalEstaNoPeriodoFiscalizacao(lancamentoDoctoFiscal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Emissão da Nota Fiscal deve estar entre o(s) mese(s) e ano(s) do periodo fiscalização.");
        }
        if ((!lancamentoDoctoFiscal.getNotaCancelada() && !lancamentoDoctoFiscal.getNotaExtraviada()) && lancamentoDoctoFiscal.getValorNotaFiscal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Valor da Nota Fiscal.");
        }
        if (lancamentoDoctoFiscal.getPorcentagemISS() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Porcentagem do ISS.");
        }
        if (lancamentoDoctoFiscal.getValorIndice() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Valor do Índice.");
        }
        if ((!lancamentoDoctoFiscal.getNotaCancelada() && !lancamentoDoctoFiscal.getNotaExtraviada()) && lancamentoDoctoFiscal.getValorApurado() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Valor Apurado.");
        }
        if ((lancamentoDoctoFiscal.getCpfCnpjTomador() != null && !"".equals(lancamentoDoctoFiscal.getCpfCnpjTomador())) && !Util.valida_CpfCnpj(lancamentoDoctoFiscal.getCpfCnpjTomador())) {
            String tipoCpfCnpj = "CPF";
            if (TipoPessoa.JURIDICA.equals(lancamentoDoctoFiscal.getTipoPessoaTomador())) {
                tipoCpfCnpj = "CNPJ";
            }
            ve.adicionarMensagemDeOperacaoNaoPermitida("O " + tipoCpfCnpj + " do tomador é inválido.");
        }

        if (ve.getMensagens() != null && ve.getMensagens().isEmpty()) {
            if (lancamentoDoctoFiscal.getAidf() != null && !existeNotaCadastroAidf()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A nota informada não foi autorizada para a impressão no cadastro AIDF.");
            }
        }

        if (ve.getMensagens() != null && ve.getMensagens().isEmpty()) {
            validarCamposNegativosDoctoFiscal(ve);
        }

        ve.lancarException();
    }

    public boolean existeNotaCadastroAidf() {
        if (lancamentoDoctoFiscal.getAidf() != null) {
            if (lancamentoDoctoFiscal.getAidf().getNotaFiscalInicial() != null && lancamentoDoctoFiscal.getAidf().getNotaFiscalFinalAutorizado() != null) {
                if (lancamentoDoctoFiscal.getNumeroNotaFiscal() >= Long.valueOf(lancamentoDoctoFiscal.getAidf().getNotaFiscalInicial())
                    && lancamentoDoctoFiscal.getNumeroNotaFiscal() <= Long.valueOf(lancamentoDoctoFiscal.getAidf().getNotaFiscalFinalAutorizado())
                    && lancamentoDoctoFiscal.getSerie().equals(lancamentoDoctoFiscal.getAidf().getNumeroSerie())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void excluirDoctoFiscalSelecionado(LancamentoDoctoFiscal lancamento) {
        selecionado.getLancamentoDoctoFiscal().remove(lancamento);
        selecionado = acaoFiscalFacade.salvaRetornando(selecionado);
    }

    public void alterarDoctoFiscalSelecionado(LancamentoDoctoFiscal lancamento) {
        lancamentoDoctoFiscal = (LancamentoDoctoFiscal) Util.clonarObjeto(lancamento);
        ehAlteracaoNotaCanceladaExtraviada = (lancamentoDoctoFiscal.getNotaCancelada() || lancamentoDoctoFiscal.getNotaExtraviada());
    }

    public void excluirMultaLancada(LancamentoMultaFiscal multa, RegistroLancamentoContabil reg) {
        reg.getMultas().remove(multa);
    }

    public CadastroEconomico recuperaPessoaCadastroEconomico(AcaoFiscal acaoFiscal) {
        return acaoFiscalFacade.buscarCadastroEconomicoDaAcaoFiscal(acaoFiscal);
    }

    public String inscricaoCadastralCMC(AcaoFiscal acaoFiscal) {
        CadastroEconomico ce = acaoFiscalFacade.buscarCadastroEconomicoDaAcaoFiscal(acaoFiscal);
        if (ce != null) {
            return ce.getInscricaoCadastral();
        }
        return "";
    }

    public String nomeCMC(AcaoFiscal acaoFiscal) {
        return acaoFiscalFacade.buscarCadastroEconomicoDaAcaoFiscal(acaoFiscal).getPessoa().getNome();
    }

    public void gerarDocumentoTermoFiscalizacao() {
        if (verificaDoctoOrdemServico(selecionado, TipoDoctoAcaoFiscal.ORDEMSERVICO)) {
            documentoOficial = gerarDocumento(TipoDoctoAcaoFiscal.FISCALIZACAO);
            FacesUtil.executaJavaScript("dialogImprimirDocto.show()");
        } else {
            FacesUtil.addError("Impossível Continuar!", "Para fazer a impressão do Termo de Fiscalização é necessário gerar a Ordem de Serviço da Ação Fiscal.");
        }
    }

    public boolean desabilitaEditorDocumento() {
        if (documentoOficial != null) {
            try {
                if (documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getImprimirDiretoPDF()) {
                    return true;
                }
            } catch (Exception e) {
                logger.trace("", e);
                return false;
            }
        }
        return false;
    }

    public DocumentoOficial gerarDocumento(TipoDoctoAcaoFiscal tipoDoctoAcaoFiscal) {
        if (selecionado != null) {
            DoctoAcaoFiscal docto = acaoFiscalFacade.getProgramacaoFiscalFacade().buscarDocumentoFiscalPorTipo(selecionado, tipoDoctoAcaoFiscal);
            if (docto == null || docto.getDocumentoOficial() == null || docto.getDocumentoOficial().getDataEmissao() == null) {
                docto = new DoctoAcaoFiscal();
            }
            selecionado.setCadastroEconomico(acaoFiscalFacade.buscarCadastroEconomicoDaAcaoFiscal(selecionado));
            TipoDoctoOficial tipo = null;
            if (TipoDoctoAcaoFiscal.FISCALIZACAO.equals(tipoDoctoAcaoFiscal)) {
                tipo = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarTipoDoctoTermoFiscalizacaoPorExercicio(getSistemaControlador().getExercicioCorrente());
                if (SituacaoAcaoFiscal.DESIGNADO.equals(selecionado.getSituacaoAcaoFiscal())) {
                    selecionado.setSituacaoAcaoFiscal(SituacaoAcaoFiscal.EXECUTANDO);
                }
                selecionado.setDataTermoFiscalizacao(new Date());
                atribuirDataArbitramento(new Date());
            } else if (TipoDoctoAcaoFiscal.AUTOINFRACAO.equals(tipoDoctoAcaoFiscal)) {
                tipo = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarTipoDoctoAutoInfracaoPorExercicio(getSistemaControlador().getExercicioCorrente());
                if (tipo != null && getRegistroAtivo().getAutoInfracaoValido() == null) {
                    docto = new DoctoAcaoFiscal();
                    AutoInfracaoFiscal aif = new AutoInfracaoFiscal();
                    aif.setSituacaoAutoInfracao(SituacaoAutoInfracao.GERADO);

                    AutoInfracaoFiscal ultimoAuto = acaoFiscalFacade.buscarUltimoAutoInfracao(getRegistroAtivo());
                    if (ultimoAuto != null && SituacaoAutoInfracao.RETIFICADO.equals(ultimoAuto.getSituacaoAutoInfracao())) {
                        aif.setAno(ultimoAuto.getAno());
                        aif.setNumero(ultimoAuto.getNumero());
                    } else {
                        aif.setAno(getSistemaControlador().getExercicioCorrenteAsInteger());
                        aif.setNumero(acaoFiscalFacade.buscarNumeroAutoInfracaoMaisUm(aif.getAno()));
                    }
                    BigDecimal valor = BigDecimal.ZERO;
                    BigDecimal multa = BigDecimal.ZERO;
                    BigDecimal juros = BigDecimal.ZERO;
                    BigDecimal correcao = BigDecimal.ZERO;
                    for (LancamentoContabilFiscal lcf : getRegistroAtivo().getLancamentosContabeis()) {
                        valor = valor.add(lcf.getIssDevido());
                        multa = multa.add(lcf.getMulta());
                        juros = juros.add(lcf.getJuros());
                        correcao = correcao.add(lcf.getCorrecao());
                    }

                    aif.setValorAutoInfracao(valor);
                    aif.setMulta(multa);
                    aif.setJuros(juros);
                    aif.setCorrecao(correcao);
                    aif.setDoctoAcaoFiscal(docto);
                    aif.setRegistro(getRegistroAtivo());
                    aif.setFundamentacao(fundamentacaoAutoInfracao);
                    aif.setHistoricoFiscal(historicoAutoInfracao);
                    getRegistroAtivo().getAutosInfracao().add(aif);
                }
            } else if (TipoDoctoAcaoFiscal.HOMOLOGACAO.equals(tipoDoctoAcaoFiscal)) {
                tipo = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarTipoDoctoHomologacaoPorExercicio(getSistemaControlador().getExercicioCorrente());
                if (tipo != null && selecionado.getNumeroHomologacao() == null) {
                    selecionado.setNumeroHomologacao(getNumeroHomologacao());
                }
            } else if (TipoDoctoAcaoFiscal.RELATORIO_FISCAL.equals(tipoDoctoAcaoFiscal)) {
                tipo = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarTipoDoctoRelatorioFiscalPorExercicio(getSistemaControlador().getExercicioCorrente());
            } else if (TipoDoctoAcaoFiscal.FINALIZACAO.equals(tipoDoctoAcaoFiscal)) {
                tipo = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarTipoDoctoTermoFinalizacaoPorExercicio(getSistemaControlador().getExercicioCorrente());
            }

            if (tipo != null) {
                docto.setAcaoFiscal(selecionado);
                docto.setTipoDoctoAcaoFiscal(tipoDoctoAcaoFiscal);
                docto.setAtivo(true);
                try {
                    if (selecionado.getNumeroTermoFiscalizacao() == null) {
                        selecionado.setNumeroTermoFiscalizacao(acaoFiscalFacade.getNumeroTermo());
                    }
                    selecionado.getDoctosAcaoFiscal().add(docto);
                    selecionado = acaoFiscalFacade.mergeAcaoFiscal(selecionado);

                    if (TipoDoctoAcaoFiscal.AUTOINFRACAO.equals(tipoDoctoAcaoFiscal)) {
                        docto.setDocumentoOficial(acaoFiscalFacade.getDocumentoOficialFacade().geraDocumentoAutoInfracao(getRegistroAtivo().getAutoInfracaoValido(), docto.getDocumentoOficial(), selecionado.getCadastroEconomico(), null, tipo, getSistemaControlador(), acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarParametroFiscalizacao(getSistemaControlador().getExercicioCorrente())));
                    } else {
                        docto.setDocumentoOficial(acaoFiscalFacade.getDocumentoOficialFacade().geraDocumentoFiscalizacao(selecionado, docto.getDocumentoOficial(), selecionado.getCadastroEconomico(), null, tipo, getSistemaControlador(), acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarParametroFiscalizacao(getSistemaControlador().getExercicioCorrente())));
                    }

                    selecionado = acaoFiscalFacade.mergeAcaoFiscal(selecionado);
                } catch (Exception e) {
                    logger.debug("Erro gerando Documento", e);
                }
                return docto.getDocumentoOficial();
            }
        }
        return null;
    }

    private void atribuirDataArbitramento(Date data) {
        selecionado.setDataArbitramento(data);
        selecionado.setUfmArbitramento(acaoFiscalFacade.getMoedaFacade().recuperaValorUFMPorExercicio(DateUtils.getAno(data)));
    }

    public void imprimirDocumento() {
        documentoOficial = acaoFiscalFacade.getDocumentoOficialFacade().salvarRetornando(documentoOficial);
        acaoFiscalFacade.getDocumentoOficialFacade().emiteDocumentoOficial(documentoOficial);
    }

    private void validarPeriodoLevantamentoContabil() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataLevantamentoInicial() == null || selecionado.getDataLevantamentoFinal() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o período correto");
        } else if (selecionado.getDataLevantamentoInicial().compareTo(selecionado.getDataLevantamentoFinal()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial não pode ser anterior a data final.");
        }
        ve.lancarException();
    }

    public void carregarMesesPorPeriodo() {
        try {
            validarPeriodoLevantamentoContabil();

            parametroFiscalizacao = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarParametroFiscalizacao(getSistemaControlador().getExercicioCorrente());
            selecionado.getLevantamentosContabeis().clear();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selecionado.getDataLevantamentoInicial());
            int mesInicio = calendar.get(Calendar.MONTH) + 1;
            int anoInicio = calendar.get(Calendar.YEAR);
            int anoMesInicio = Integer.parseInt(anoInicio + "" + StringUtil.preencheString("" + mesInicio, 2, '0'));
            calendar.setTime(selecionado.getDataLevantamentoFinal());
            int mesFim = calendar.get(Calendar.MONTH) + 1;
            int anoFim = calendar.get(Calendar.YEAR);
            int anoMesFim = Integer.parseInt(anoFim + "" + StringUtil.preencheString("" + mesFim, 2, '0'));
            while (anoMesInicio <= anoMesFim) {
                if (mesInicio <= 12) {
                    levantamentoContabil = new LevantamentoContabil();
                    levantamentoContabil.setAcaoFiscal(selecionado);
                    levantamentoContabil.setMes(Mes.getMesToInt(mesInicio));
                    levantamentoContabil.setAno(anoInicio);
                    levantamentoContabil.setValorIndice(acaoFiscalFacade.getMoedaFacade().recuperaValorUFMPorExercicio(anoInicio));
                    selecionado.getLevantamentosContabeis().add(levantamentoContabil);
                    mesInicio++;
                } else {
                    mesInicio = 1;
                    anoInicio++;
                }
                anoMesInicio = Integer.parseInt(anoInicio + "" + StringUtil.preencheString("" + mesInicio, 2, '0'));
            }
            Calendar ultimo = Calendar.getInstance();
            ultimo.set(Calendar.MONTH, mesInicio - 1);
            ultimo.set(Calendar.YEAR, anoInicio);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void reabrirAcaoFiscal() {
        try {
            validarReaberturaAcaoFiscal();

            SituacoesAcaoFiscal sit = new SituacoesAcaoFiscal(new Date(), selecionado, SituacaoAcaoFiscal.REABERTO);
            selecionado.setSituacaoAcaoFiscal(SituacaoAcaoFiscal.REABERTO);
            selecionado.getSituacoesAcaoFiscal().add(sit);
            salvarSemValidar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarReaberturaAcaoFiscal() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (!permitirReabrirPelaSituacaoDosRegistros()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Essa ação fiscal não pode ser reaberta. Verifique a situação dos lançamentos contábeis!");
        } else if (acaoFiscalFacade.temDebitosDiferenteDeAbertoOuCancelado(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Esta ação fiscal possui débitos com situação diferente de EM ABERTO, CANCELADO ou ESTORNADO e isto não permite a reabertura.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public String getFormataNumeroOrdemServico() {
        AcaoFiscal acao = selecionado;
        String retorno = "";
        if (acao != null && acao.getOrdemServico() != null) {
            retorno = String.valueOf(acao.getOrdemServico());
        }
        return retorno;
    }

    private boolean permitirReabrirPelaSituacaoDosRegistros() {
        boolean permite = true;
        for (RegistroLancamentoContabil reg : selecionado.getLancamentosContabeis()) {
            permite = false;
            if (RegistroLancamentoContabil.Situacao.AGUARDANDO.equals(reg.getSituacao()) ||
                RegistroLancamentoContabil.Situacao.AUTO_INFRACAO.equals(reg.getSituacao())) {
                return true;
            }
        }
        return permite;
    }


    public boolean verificaDoctoOrdemServico(AcaoFiscal acaoFiscal, TipoDoctoAcaoFiscal tipoDoctoAcaoFiscal) {
        boolean retorno = true;
        DoctoAcaoFiscal docto = this.acaoFiscalFacade.getProgramacaoFiscalFacade().buscarDocumentoFiscalPorTipo(acaoFiscal, tipoDoctoAcaoFiscal);
        if (docto == null) {
            retorno = false;
        }
        return retorno;
    }

    public void getValorIndicePorData() {
        if (dataParaValorIndice != null) {
            parametroFiscalizacao = acaoFiscalFacade
                .getParametroFiscalizacaoFacade()
                .recuperarParametroFiscalizacao(getSistemaControlador().getExercicioCorrente());
            valorIndiceEncontrado = acaoFiscalFacade.getMoedaFacade().recuperaValorUFMParaData(dataParaValorIndice);
            if (valorIndiceEncontrado == null) {
                valorIndiceEncontrado = acaoFiscalFacade.getMoedaFacade().recuperaValorVigenteUFM();
            }
        } else {
            FacesUtil.addError("Impossível continuar.", "Informe a data correta.");
        }
    }

    public void alterarLevantamento() {
        if (levantamentosContabeisSelecionados.length == 0) {
            FacesUtil.addError("Impossível continuar.", "Selecione ao menos um mês.");
        } else {
            this.setDataParaValorIndice(null);
            this.setValorIndiceEncontrado(null);
            RequestContext.getCurrentInstance().execute("dialogLevantamentoContabil.show()");
        }
    }

    public void confirmarAlteracaoLevantamento() {
        if (valorIndiceEncontrado == null) {
            FacesUtil.addError("Impossível continuar.", "Informe o valor do índice.");
        } else if (valorIndiceEncontrado.compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addError("Impossível continuar.", "O valor não pode ser negativo.");
        } else {
            for (LevantamentoContabil lc : levantamentosContabeisSelecionados) {
                lc.setValorIndice(valorIndiceEncontrado);
            }
            for (RegistroLancamentoContabil reg : selecionado.getLancamentosContabeis()) {
                for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
                    atualizarValoresTabelaLancamentoContabil(lcf);
                }
                RequestContext.getCurrentInstance().execute("dialogLevantamentoContabil.hide()");
            }
        }
    }

    public BigDecimal retornarValorTotalApurado() {
        BigDecimal soma = BigDecimal.ZERO;
        if (this.selecionado != null && this.selecionado.getLancamentoDoctoFiscal() != null && !this.selecionado.getLancamentoDoctoFiscal().isEmpty()) {
            for (LancamentoDoctoFiscal docto : this.selecionado.getLancamentoDoctoFiscal()) {
                if (!docto.getNotaCancelada()) {
                    soma = soma.add(docto.getValorApurado());
                }
            }
        }
        return soma;
    }

    public BigDecimal retornarValorTotalIndice() {
        BigDecimal soma = BigDecimal.ZERO;
        if (this.selecionado != null && this.selecionado.getLancamentoDoctoFiscal() != null && !this.selecionado.getLancamentoDoctoFiscal().isEmpty()) {
            for (LancamentoDoctoFiscal docto : this.selecionado.getLancamentoDoctoFiscal()) {
                if (!docto.getNotaCancelada()) {
                    soma = soma.add(docto.getValorIndice());
                }
            }
        }
        return soma;
    }

    public BigDecimal retornarValorTotalISS() {
        BigDecimal soma = BigDecimal.ZERO;
        if (this.selecionado != null && this.selecionado.getLancamentoDoctoFiscal() != null && !this.selecionado.getLancamentoDoctoFiscal().isEmpty()) {
            for (LancamentoDoctoFiscal docto : this.selecionado.getLancamentoDoctoFiscal()) {
                if (!docto.getNotaCancelada()) {
                    if (docto.getValorISS() != null) {
                        soma = soma.add(docto.getValorISS());
                    }
                }
            }
        }
        return soma;
    }

    public BigDecimal retornarValorTotalMulta() {
        return acaoFiscalFacade.retornarValorTotalMulta(this.selecionado);
    }

    public BigDecimal retornarValorTotalMultaIndice() {
        return acaoFiscalFacade.retornarValorTotalMultaIndice(this.selecionado);
    }

    public BigDecimal retornarValorTotalNota() {
        BigDecimal soma = BigDecimal.ZERO;
        if (this.selecionado != null && this.selecionado.getLancamentoDoctoFiscal() != null && !this.selecionado.getLancamentoDoctoFiscal().isEmpty()) {
            for (LancamentoDoctoFiscal docto : this.selecionado.getLancamentoDoctoFiscal()) {
                if (!docto.getNotaCancelada()) {
                    soma = soma.add(docto.getValorNotaFiscal());
                }
            }
        }
        return soma;
    }

    public void validarNotaFiscal() {
        ValidacaoException ve = new ValidacaoException();
        boolean retorno = true;
        if (lancamentoDoctoFiscal.getId() == null) {
            for (LancamentoDoctoFiscal docto : this.selecionado.getLancamentoDoctoFiscal()) {
                if (docto.getAidf() != null && docto.getAidf().equals(this.lancamentoDoctoFiscal.getAidf()) && docto.getNumeroNotaFiscal().equals(this.lancamentoDoctoFiscal.getNumeroNotaFiscal())) {
                    retorno = false;
                    break;
                }
            }
            if (!retorno) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A nota fiscal N° " + lancamentoDoctoFiscal.getNumeroNotaFiscal() + " já foi inclusa na AIDF " + lancamentoDoctoFiscal.getAidf().getNumeroAidf() + ".");
            }
        }
        ve.lancarException();
    }

    private void validarNotaFiscalPrazoFiscalizacao() {
        ValidacaoException ve = new ValidacaoException();
        if (lancamentoDoctoFiscal.getDataEmissao() != null) {
            if (!lancamentoDoctoFiscal.getNotaCancelada() && ((selecionado.getDataLevantamentoInicial().compareTo(lancamentoDoctoFiscal.getDataEmissao()) > 0) ||
                (selecionado.getDataLevantamentoFinal().compareTo(lancamentoDoctoFiscal.getDataEmissao()) < 0))) {

                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de emissão da nota fiscal deve estar dentro do prazo de fiscalização.");
            }
        }
        ve.lancarException();
    }

    public void adicionarMulta() {
        try {
            validarAdicionarMulta();
            for (LancamentoMultaFiscal lancamento : lancamentosMultasFiscais) {
                if (lancamento != null) {
                    lancamento.setRegistroLancamentoContabil(getRegistroAtivo());
                    getRegistroAtivo().getMultas().add(lancamento);
                    this.lancamentoMultaFiscal = new LancamentoMultaFiscal();
                    this.multasALancar = Lists.newArrayList();
                }
            }
            FacesUtil.addOperacaoRealizada("As Multas foram lançadas e associadas ao Registro " + getRegistroAtivo().getNumeroAno());
            limparCamposMulta();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public void podeLancarMulta() {
        try {
            validarAdicionarMulta();
            FacesUtil.executaJavaScript("confirmaMulta.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void podeLancarMultaNotas() {
        try {
            validarAdicionarMulta();
            adicionarMulta();
            FacesUtil.executaJavaScript("lancamentoMultaNotas.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public boolean verificaPermissaoUsuario(AcaoFiscal af) {
        if (af.getSituacaoAcaoFiscal().equals(SituacaoAcaoFiscal.DESIGNADO)) {
            af.setPermissaoUsuario(acaoFiscalFacade.hasUsuarioDesignado(af, getSistemaControlador().getUsuarioCorrente()));
            return af.isPermissaoUsuario();
        }
        return false;
    }

    public void carregarMesesLancamentoContabil() {
        this.selecionado.setNumeroLevantamento(this.acaoFiscalFacade.retornaUltimoNumeroLancamentoFiscal());
        selecionado.setSituacaoLancamento(SituacaoLancamento.AGUARDANDO);
        parametroFiscalizacao = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarParametroFiscalizacao(getSistemaControlador().getExercicioCorrente());
        preparaTotalizador();
        carregarLancamentoContabil();
        selecionado = acaoFiscalFacade.salvaRetornando(selecionado);
    }

    public void duplicarLancamentoContabil(LancamentoContabilFiscal l, RegistroLancamentoContabil r) {
        int posicao = r.getLancamentosContabeis().indexOf(l);
        LancamentoContabilFiscal lancamentoDuplicado = new LancamentoContabilFiscal();
        lancamentoDuplicado.setRegistroLancamentoContabil(r);
        lancamentoDuplicado.setAno(l.getAno());
        lancamentoDuplicado.setMes(l.getMes());
        lancamentoDuplicado.setId(null);
        lancamentoDuplicado.zeraValores();
        r.getLancamentosContabeis().add(posicao + 1, lancamentoDuplicado);
        r.getLancamentosPorAno().clear();
        reorganizaSequenciaLancamentoContabil(r);
    }

    public LancamentoContabilFiscal duplicarLancamentoContabilSemEvento(LancamentoContabilFiscal lancamento, BigDecimal aliquota) {
        LancamentoContabilFiscal lancamentoDuplicado = new LancamentoContabilFiscal();
        lancamentoDuplicado.setRegistroLancamentoContabil(lancamento.getRegistroLancamentoContabil());
        lancamentoDuplicado.setAno(lancamento.getAno());
        lancamentoDuplicado.setMes(lancamento.getMes());
        lancamentoDuplicado.setId(null);
        lancamentoDuplicado.setSequencia(null);
        lancamentoDuplicado.setAliquotaISS(aliquota);
        lancamentoDuplicado.setValorDeclarado(lancamento.getValorDeclarado());
        lancamentoDuplicado.setIssPago(lancamento.getIssPago());
        return lancamentoDuplicado;
    }

    private void reorganizaSequenciaLancamentoContabil(RegistroLancamentoContabil r) {
        Collections.sort(r.getLancamentosContabeis());
        for (LancamentoContabilFiscal lcf : r.getLancamentosContabeis()) {
            lcf.setSequencia(Long.valueOf(r.getLancamentosContabeis().indexOf(lcf) + 1));
        }
    }

    public void validarExclusaoLancamentoContabil(LancamentoContabilFiscal l, RegistroLancamentoContabil r) {
        if (r.getLancamentosContabeis().stream().filter(lcf -> lcf.getMes() == l.getMes()
            && lcf.getAno().compareTo(l.getAno()) == 0).count() <= 1) {
            throw new ValidacaoException("Não existe registro duplicado para o mês " + l.getMes().getDescricao() +
                "/" + l.getAno() + ". Somente é permitido a exclusão de meses duplicados.");
        }
    }

    public void excluirLancamentoContabil(LancamentoContabilFiscal lancamentoContabilFiscal, RegistroLancamentoContabil registro) {
        try {
            validarExclusaoLancamentoContabil(lancamentoContabilFiscal, registro);
            registro.getLancamentosContabeis().remove(lancamentoContabilFiscal);
            registro.getLancamentosPorAno().clear();
            reorganizaSequenciaLancamentoContabil(registro);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public BigDecimal buscarValorDeclaradoISS(LancamentoContabilFiscal lancamento) {
        List<ItemCalculoIss> itens = acaoFiscalFacade.buscarLancamentoDeISSParaAcaoFiscal(lancamento.getMes(), lancamento.getAno(), selecionado.getCadastroEconomico());
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCalculoIss item : itens) {
            if (item.getAliquota().compareTo(lancamento.getAliquotaISS()) == 0) {
                total = total.add(item.getBaseCalculo());
            } else {
                String identificador = montaIdentificadorRegistro(item);
                if (aliquotasVariadas.containsKey(identificador)) {
                    aliquotasVariadas.get(identificador).setBaseCalculo(aliquotasVariadas.get(identificador).getBaseCalculo().add(item.getBaseCalculo()));
                    aliquotasVariadas.get(identificador).setFaturamento(aliquotasVariadas.get(identificador).getFaturamento().add(item.getFaturamento()));
                } else {
                    aliquotasVariadas.put(identificador, item);
                }
            }
        }
        return total;
    }

    private String montaIdentificadorRegistro(ItemCalculoIss item) {
        String identificador = Util.zerosAEsquerda(item.getAliquota().intValue(), 2)
            + Util.zerosAEsquerda(item.getCalculo().getProcessoCalculoISS().getMesReferencia(), 2)
            + Util.zerosAEsquerda(item.getCalculo().getProcessoCalculoISS().getExercicio().getAno(), 4)
            + (item.getCalculo().getAusenciaMovimento() ? '1' : '0');
        return identificador;
    }

    public void atualizarValoresTabelaLancamentoContabilComUpdate(LancamentoContabilFiscal lancamento, Long registro) {
        atualizarValoresTabelaLancamentoContabil(lancamento);
        updateRodapes(registro);
    }

    private void atualizarValoresTabelaLancamentoContabil(LancamentoContabilFiscal lancamento) {
        if (lancamento.getTributado()) {
            lancamento.setBaseCalculo(lancamento.getValorDeclarado().subtract(lancamento.getValorApurado()));
            lancamento.setIssApurado(lancamento.getValorApurado().multiply(lancamento.getAliquotaISS().divide(CEM)));
            if (!lancamento.getNfse() && lancamento.getIssPago().compareTo(BigDecimal.ZERO) == 0) {
                lancamento.setIssPago(buscarValorPagoIss(lancamento));
            }
            arbitrar(lancamento);
            BigDecimal saldoIss = (lancamento.getIssApurado().subtract(lancamento.getIssPago()));
            lancamento.setIssDevido(saldoIss);

            lancamento.setCorrecao(calcularCorrecaoLancamento(lancamento));
            lancamento.setJuros(calcularJurosLancamento(lancamento));
            lancamento.setMulta(calcularMultaLancamento(lancamento));
        }
    }

    public void arbitrar(LancamentoContabilFiscal lancamento) {
        BigDecimal valor = acaoFiscalFacade.getMoedaFacade().recuperaValorVigenteUFM();
        if (valor != null) {
            for (LevantamentoContabil lv : selecionado.getLevantamentosContabeis()) {
                if (lv.getMes().equals(lancamento.getMes()) && lv.getAno().compareTo(lancamento.getAno()) == 0) {
                    lancamento.setIndiceCorrecao(lv.getValorIndice());
                }
            }
        }
    }

    public void gerarAutoInfracao() {
        try {
            validarRegistrosRepetidos(getRegistroAtivo());

            if (getRegistroAtivo().getSituacao().equals(RegistroLancamentoContabil.Situacao.AGUARDANDO)) {
                getRegistroAtivo().setSituacao(RegistroLancamentoContabil.Situacao.AUTO_INFRACAO);
            }
            if (getRegistroAtivo().getAutoInfracaoValido() != null) {
                getRegistroAtivo().getAutoInfracaoValido().setFundamentacao(fundamentacaoAutoInfracao);
            }
            if (getRegistroAtivo().getAutoInfracaoValido() != null) {
                getRegistroAtivo().getAutoInfracaoValido().setHistoricoFiscal(historicoAutoInfracao);
            }
            documentoOficial = gerarDocumento(TipoDoctoAcaoFiscal.AUTOINFRACAO);

            validarImpressaoDocumento();
            imprimirDocumento();

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } finally {
            RequestContext.getCurrentInstance().execute("imprimeAutoInfracao.hide()");
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    private void validarImpressaoDocumento() {
        ValidacaoException ve = new ValidacaoException();
        if (documentoOficial == null || documentoOficial.getConteudo() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível gerar o documento de auto de infração, verifique a configuração de documentos oficiais!");
        }
        ve.lancarException();
    }

    public void cancelarLancamentoContabilFiscal(RegistroLancamentoContabil registro) {
        if (registro.getAutoInfracaoValido() == null
            || registro.getAutoInfracaoValido().getSituacaoAutoInfracao().equals(SituacaoAutoInfracao.ESTORNADO)
            || registro.getAutoInfracaoValido().getSituacaoAutoInfracao().equals(SituacaoAutoInfracao.CANCELADO)) {
            registro.setSituacao(RegistroLancamentoContabil.Situacao.CANCELADO);
        } else {
            FacesUtil.addError("Impossível Cancelar!", "Existe um auto de infração gerado para essa ação fiscal.");
        }
    }

    public BigDecimal retornarValorTotalDeclaradoLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return acaoFiscalFacade.retornaValorTotalDeclaradoLancamento(ano, reg, this.selecionado);
    }

    public BigDecimal retornarValorTotalApuradoLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return acaoFiscalFacade.retornaValorTotalApuradoLancamento(ano, reg, this.selecionado);
    }

    public BigDecimal retornarValorTotalBaseCalculoLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return acaoFiscalFacade.retornaValorTotalBaseCalculoLancamento(ano, reg, this.selecionado);
    }

    public BigDecimal retornarValorTotalIssDevidoLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return acaoFiscalFacade.retornaValorTotalIssDevidoLancamento(ano, reg, this.selecionado);
    }

    public BigDecimal retornarValorTotalIssApuradoLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return acaoFiscalFacade.retornaValorTotalIssApuradoLancamento(ano, reg, this.selecionado);
    }

    public BigDecimal retornarValorTotalIssPagoLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return acaoFiscalFacade.retornaValorTotalIssPagoLancamento(ano, reg, this.selecionado);
    }

    public BigDecimal retornarValorTotalMultaMoraLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return acaoFiscalFacade.retornarValorTotalMultaMoraLancamento(ano, reg, selecionado);
    }

    public BigDecimal retornarValorTotalJurosMoraLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return acaoFiscalFacade.retornarValorTotalJurosMoraLancamento(ano, reg, this.selecionado);
    }

    public BigDecimal retornarValorTotalTotalLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return acaoFiscalFacade.retornarValorTotalTotalLancamento(ano, reg, selecionado);
    }

    public BigDecimal retornarValorTotalCorrecaoMonetariaLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return acaoFiscalFacade.retornarValorTotalCorrecaoMonetariaLancamento(ano, reg, this.selecionado);
    }

    public BigDecimal retornarValorTotalCorrigidoLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return acaoFiscalFacade.retornarValorTotalCorrigidoLancamento(ano, reg, this.selecionado);
    }

    public void estornarAutoInfracao() {
        executarRetificacaoEstornoAutoInfracao(SituacaoAutoInfracao.ESTORNADO);
    }

    public void retificarAutoInfracao() {
        executarRetificacaoEstornoAutoInfracao(SituacaoAutoInfracao.RETIFICADO);
    }

    private void executarRetificacaoEstornoAutoInfracao(SituacaoAutoInfracao situacaoAutoInfracao) {
        try {
            validarCamposEstornoRetificacaoAutoInfracao(situacaoAutoInfracao);
            autoInfracaoFiscalSelecionado.getRegistro().setSituacao(RegistroLancamentoContabil.Situacao.AGUARDANDO);
            autoInfracaoFiscalSelecionado.setSituacaoAutoInfracao(situacaoAutoInfracao);
            acaoFiscalFacade.salvar(selecionado);
            if (!SituacaoAutoInfracao.RETIFICADO.equals(situacaoAutoInfracao)) {
                acaoFiscalFacade.estornarDebitosFiscalizacao(selecionado);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void gerarDocumentoHomologacao() {
        if (retornarValorTotalIssDevido().compareTo(BigDecimal.ZERO) == 0) {
            if (!existeAutoInfracaoValido()) {
                if (!selecionado.getSituacaoAcaoFiscal().equals(SituacaoAcaoFiscal.CONCLUIDO)) {
                    SituacoesAcaoFiscal situacao = new SituacoesAcaoFiscal();
                    situacao.setAcaoFiscal(selecionado);
                    situacao.setData(new Date());
                    situacao.setSituacaoAcaoFiscal(SituacaoAcaoFiscal.CONCLUIDO);
                    selecionado.setSituacaoAcaoFiscal(SituacaoAcaoFiscal.CONCLUIDO);
                    selecionado.getSituacoesAcaoFiscal().add(situacao);
                }
                for (RegistroLancamentoContabil reg : selecionado.getLancamentosContabeis()) {
                    if (reg.getSituacao().equals(RegistroLancamentoContabil.Situacao.AGUARDANDO)) {
                        reg.setSituacao(RegistroLancamentoContabil.Situacao.HOMOLOGADO);
                    }
                }
                documentoOficial = gerarDocumento(TipoDoctoAcaoFiscal.HOMOLOGACAO);
                RequestContext.getCurrentInstance().execute("dialogImprimirDocto.show()");
            }
        } else {
            DecimalFormat df = new DecimalFormat("#,###,##0.00");
            FacesUtil.addWarn("Atenção.", "Não é possível gerar o Termo de Homologação. O Saldo do ISS deve ser R$0,00 (ZERO). Encontrado R$" + df.format(retornarValorTotalIssDevido()) + " de Saldo do ISS.");
        }
    }

    public BigDecimal retornarValorTotalIssDevido() {
        BigDecimal soma = BigDecimal.ZERO;
        if (!this.selecionado.getLancamentosContabeis().isEmpty() || this.selecionado.getLancamentosContabeis() != null) {
            for (RegistroLancamentoContabil reg : selecionado.getLancamentosContabeis()) {
                for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
                    soma = soma.add(lcf.getIssDevido());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public Long getNumeroHomologacao() {
        return acaoFiscalFacade.getNumeroHomologacaoMaisUm();
    }

    private BigDecimal getValorAnual(Integer ano) {
        BigDecimal total = BigDecimal.ZERO;
        if (MultaFiscalizacao.BaseCalculo.LANCAMENTO.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getBaseCalculo())) {
            for (LancamentoContabilFiscal lancamento : getRegistroAtivo().getLancamentosContabeis()) {
                if (lancamento.getAno().equals(ano)) {
                    if ((lancamentoMultaFiscal.getMultaFiscalizacao().getIncidenciaMultaFiscalizacao().equals(IncidenciaMultaFiscalizacao.MULTA_PUNITIVA) &&
                        lancamento.getValorDeclarado().compareTo(BigDecimal.ZERO) <= 0) || !lancamentoMultaFiscal.getMultaFiscalizacao().getIncidenciaMultaFiscalizacao().equals(IncidenciaMultaFiscalizacao.MULTA_PUNITIVA)) {
                        total = total.add(lancamento.getValorCorrigido());
                    }
                }
            }
        } else if (MultaFiscalizacao.BaseCalculo.MULTA.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getBaseCalculo())) {
            for (LancamentoMultaFiscal multaFiscal : getRegistroAtivo().getMultas()) {
                if (multaFiscal.getLancamentoDoctoFiscal() != null) {
                    if (ano.equals(multaFiscal.getLancamentoDoctoFiscal().getAno())) {
                        total = total.add(multaFiscal.getValorMulta());
                    }
                } else if (multaFiscal.getAno() != null && ano.equals(multaFiscal.getAno())) {
                    total = total.add(multaFiscal.getValorMulta());
                }
            }
        }
        return total;
    }

    private BigDecimal getValorMensal(LancamentoContabilFiscal lancamento) {
        BigDecimal total = BigDecimal.ZERO;
        if (MultaFiscalizacao.BaseCalculo.LANCAMENTO.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getBaseCalculo())) {
            for (LancamentoContabilFiscal lcf : getRegistroAtivo().getLancamentosContabeis()) {
                if (lcf.equals(lancamento)) {
                    if (!IncidenciaMultaFiscalizacao.MULTA_PUNITIVA.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getIncidenciaMultaFiscalizacao())
                        || lcf.getValorDeclarado().compareTo(BigDecimal.ZERO) <= 0) {

                        total = total.add(lcf.getValorCorrigido());
                    }
                }
            }
        } else if (MultaFiscalizacao.BaseCalculo.MULTA.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getBaseCalculo())) {
            for (LancamentoMultaFiscal multaFiscal : getRegistroAtivo().getMultas()) {
                if (multaFiscal.getLancamentoDoctoFiscal() != null) {
                    if (lancamento.getAno().equals(multaFiscal.getLancamentoDoctoFiscal().getAno()) && lancamento.getMes().getNumeroMes().equals(multaFiscal.getLancamentoDoctoFiscal().getMes())) {
                        total = total.add(multaFiscal.getValorMulta());
                    }
                }
                if (multaFiscal.getLancamentoContabilFiscal() != null) {
                    if (multaFiscal.getLancamentoContabilFiscal().getId() != null && lancamento.getId() != null
                        && multaFiscal.getLancamentoContabilFiscal().getId().equals(lancamento.getId()))
                        total = total.add(multaFiscal.getValorMulta());
                } else {
                    if ((multaFiscal.getAno() != null && lancamento.getAno().equals(multaFiscal.getAno()))
                        && (multaFiscal.getMes() != null && lancamento.getMes().equals(multaFiscal.getMes())))
                        total = total.add(multaFiscal.getValorMulta());
                }
            }
        }
        return total;
    }

    private BigDecimal getValorMensalNotas(LancamentoDoctoFiscal lancamento) {
        BigDecimal total = BigDecimal.ZERO;
        if (MultaFiscalizacao.BaseCalculo.LANCAMENTO.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getBaseCalculo())) {
            BigDecimal baseCalculo = lancamento.getValorCorrigido();

            if ((lancamentoMultaFiscal.getMultaFiscalizacao().getIncidenciaMultaFiscalizacao().equals(IncidenciaMultaFiscalizacao.MULTA_PUNITIVA) &&
                baseCalculo.compareTo(BigDecimal.ZERO) <= 0) || !lancamentoMultaFiscal.getMultaFiscalizacao().getIncidenciaMultaFiscalizacao().equals(IncidenciaMultaFiscalizacao.MULTA_PUNITIVA)) {
                total = total.add(baseCalculo);
            }
        } else if (MultaFiscalizacao.BaseCalculo.MULTA.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getBaseCalculo())) {
            for (LancamentoMultaFiscal multaFiscal : getRegistroAtivo().getMultas()) {
                if (multaFiscal.getLancamentoDoctoFiscal() != null) {
                    if (lancamento.getAno() == multaFiscal.getLancamentoDoctoFiscal().getAno() && lancamento.getMes() == multaFiscal.getLancamentoDoctoFiscal().getMes()) {
                        total = total.add(multaFiscal.getValorMulta());
                    }
                } else if ((multaFiscal.getAno() != null && multaFiscal.getAno().equals(lancamento.getAno()))
                    && (multaFiscal.getMes() != null && multaFiscal.getMes().equals(lancamento.getMes()))) {
                    total = total.add(multaFiscal.getValorMulta());
                }
            }
        }
        return total;
    }

    public BigDecimal converterIndiceParaReal(BigDecimal valor, BigDecimal indice) {
        BigDecimal retorno = BigDecimal.ZERO;
        if (valor != null && indice != null && indice != BigDecimal.ZERO) {
            retorno = valor.multiply(indice);
        }
        return retorno;
    }

    public BigDecimal converterRealParaIndice(BigDecimal valor, BigDecimal indice) {
        BigDecimal retorno = BigDecimal.ZERO;
        if (valor != null && indice != null && indice.compareTo(BigDecimal.ZERO) > 0) {
            retorno = valor.divide(indice, 4, RoundingMode.HALF_UP);
        }
        return retorno;
    }

    private BigDecimal calculaMultaPercentual(BigDecimal valorIndiceAtual, BigDecimal aliquotaMulta, LancamentoContabilFiscal lancamentoContabil) {
        BigDecimal valorMulta = recuperaValoresLancamentoMulta(lancamentoContabil);
        valorMulta = valorMulta.multiply(aliquotaMulta.divide(CEM));
        return valorMulta.divide(valorIndiceAtual, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculaMultaPercentualNotas(BigDecimal valorIndiceAtual, BigDecimal aliquotaMulta, LancamentoDoctoFiscal lancamento) {
        BigDecimal valorMulta = recuperaValoresLancamentoMultaNotas(lancamento);
        valorMulta = valorMulta.multiply(aliquotaMulta.divide(CEM));
        return valorMulta.divide(valorIndiceAtual, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculaMultaFixo(LancamentoContabilFiscal lancamento) {
        if (TipoCalculoMultaFiscalizacao.ANUAL.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getTipoCalculoMultaFiscalizacao())) {
            return this.lancamentoMultaFiscal.getMultaFiscalizacao().getValorMulta();
        } else {
            for (LancamentoContabilFiscal lcf : getRegistroAtivo().getLancamentosContabeis()) {
                if (lcf.equals(lancamento) && TipoCalculoMultaFiscalizacao.MENSAL.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getTipoCalculoMultaFiscalizacao())) {
                    if ((lancamentoMultaFiscal.getMultaFiscalizacao().getIncidenciaMultaFiscalizacao().equals(IncidenciaMultaFiscalizacao.MULTA_PUNITIVA) &&
                        lcf.getValorDeclarado().compareTo(BigDecimal.ZERO) <= 0) || !lancamentoMultaFiscal.getMultaFiscalizacao().getIncidenciaMultaFiscalizacao().equals(IncidenciaMultaFiscalizacao.MULTA_PUNITIVA)) {
                        return this.lancamentoMultaFiscal.getMultaFiscalizacao().getValorMulta();
                    }
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculaMultaFixoNotas(LancamentoDoctoFiscal lancamento) {
        if (TipoCalculoMultaFiscalizacao.ANUAL.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getTipoCalculoMultaFiscalizacao())) {
            return this.lancamentoMultaFiscal.getMultaFiscalizacao().getValorMulta();
        } else {
            for (LancamentoDoctoFiscal lancamentoDocto : selecionado.getLancamentoDoctoFiscal()) {
                if (lancamentoDocto.equals(lancamento) && TipoCalculoMultaFiscalizacao.MENSAL.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getTipoCalculoMultaFiscalizacao())) {
                    if ((lancamentoMultaFiscal.getMultaFiscalizacao().getIncidenciaMultaFiscalizacao().equals(IncidenciaMultaFiscalizacao.MULTA_PUNITIVA) &&
                        lancamentoDocto.getValorNotaFiscal().compareTo(BigDecimal.ZERO) <= 0) || !lancamentoMultaFiscal.getMultaFiscalizacao().getIncidenciaMultaFiscalizacao().equals(IncidenciaMultaFiscalizacao.MULTA_PUNITIVA)) {
                        return this.lancamentoMultaFiscal.getMultaFiscalizacao().getValorMulta();
                    }
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal recuperaValoresLancamentoMulta(LancamentoContabilFiscal lancamento) {
        BigDecimal valorMulta = BigDecimal.ZERO;
        if (this.lancamentoMultaFiscal.getMultaFiscalizacao().getTipoCalculoMultaFiscalizacao().equals(TipoCalculoMultaFiscalizacao.ANUAL)) {
            valorMulta = getValorAnual(lancamento.getAno());
        }
        if (this.lancamentoMultaFiscal.getMultaFiscalizacao().getTipoCalculoMultaFiscalizacao().equals(TipoCalculoMultaFiscalizacao.MENSAL)) {
            valorMulta = getValorMensal(lancamento);
        }
        return valorMulta;
    }

    public BigDecimal recuperaValoresLancamentoMultaNotas(LancamentoDoctoFiscal lancamento) {
        BigDecimal valorMulta = BigDecimal.ZERO;
        if (this.lancamentoMultaFiscal.getMultaFiscalizacao().getTipoCalculoMultaFiscalizacao().equals(TipoCalculoMultaFiscalizacao.ANUAL)) {
            valorMulta = getValorAnual(lancamento.getAno());
        }
        if (this.lancamentoMultaFiscal.getMultaFiscalizacao().getTipoCalculoMultaFiscalizacao().equals(TipoCalculoMultaFiscalizacao.MENSAL)) {
            valorMulta = getValorMensalNotas(lancamento);
        }
        return valorMulta;
    }

    public void calcularMulta() {
        try {
            validarCamposMulta();
            lancamentosMultasFiscais.clear();
            multasALancar.clear();
            lancarMulta();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void calcularMultaNotas() {
        try {
            validarCamposMultaNotas();
            lancamentosMultasFiscais.clear();
            multasALancar.clear();
            lancarMultaNotas();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private BigDecimal calculaValorMulta(MultaFiscalizacao multa, LancamentoContabilFiscal lancamento) {
        BigDecimal valorMultaUfm = BigDecimal.ZERO;
        if (this.lancamentoMultaFiscal.getMultaFiscalizacao().getTipoMultaFiscalizacao().equals(TipoMultaFiscalizacao.PERCENTUAL)) {
            valorMultaUfm = calculaMultaPercentual(selecionado.getUfmArbitramento(), multa.getAliquotaMulta(), lancamento);
        } else if (this.lancamentoMultaFiscal.getMultaFiscalizacao().getTipoMultaFiscalizacao().equals(TipoMultaFiscalizacao.FIXO)) {
            valorMultaUfm = calculaMultaFixo(lancamento);
        }
        if (this.lancamentoMultaFiscal.getMultaFiscalizacao().getFormaCalculoMultaFiscalizacao().equals(FormaCalculoMultaFiscalizacao.QUANTIDADE)) {
            valorMultaUfm = valorMultaUfm.multiply(new BigDecimal(this.lancamentoMultaFiscal.getQuantidade()));
        }
        return valorMultaUfm;
    }

    private BigDecimal calculaValorMultaNotas(MultaFiscalizacao multa, LancamentoDoctoFiscal lancamento) {
        BigDecimal valorMultaUfm = BigDecimal.ZERO;
        if (this.lancamentoMultaFiscal.getMultaFiscalizacao().getTipoMultaFiscalizacao().equals(TipoMultaFiscalizacao.PERCENTUAL)) {
            valorMultaUfm = calculaMultaPercentualNotas(selecionado.getUfmArbitramento(), multa.getAliquotaMulta(), lancamento);
        } else if (this.lancamentoMultaFiscal.getMultaFiscalizacao().getTipoMultaFiscalizacao().equals(TipoMultaFiscalizacao.FIXO)) {
            valorMultaUfm = calculaMultaFixoNotas(lancamento);
        }
        if (this.lancamentoMultaFiscal.getMultaFiscalizacao().getFormaCalculoMultaFiscalizacao().equals(FormaCalculoMultaFiscalizacao.QUANTIDADE)) {
            valorMultaUfm = valorMultaUfm.multiply(new BigDecimal(this.lancamentoMultaFiscal.getQuantidade()));
        }
        return valorMultaUfm;
    }

    private BigDecimal getBaseCalculoPorAno(Integer ano, MultaFiscalizacao.BaseCalculo tipoBaseCalculo) {
        BigDecimal baseCalculo = BigDecimal.ZERO;
        if (MultaFiscalizacao.BaseCalculo.LANCAMENTO.equals(tipoBaseCalculo)) {
            for (LancamentoContabilFiscal lcf : getRegistroAtivo().getLancamentosContabeis()) {
                if (lcf.getAno().equals(ano)) {
                    baseCalculo = baseCalculo.add(lcf.getValorCorrigido());
                }
            }
        } else {
            for (LancamentoMultaFiscal multaFiscal : getRegistroAtivo().getMultas()) {
                if (multaFiscal.getAno().equals(ano)) {
                    baseCalculo = baseCalculo.add(multaFiscal.getValorMulta());
                }
            }
        }
        return baseCalculo;
    }

    private BigDecimal getBaseCalculoPorMes(LancamentoContabilFiscal lancamento, Mes mes, Integer ano, MultaFiscalizacao.BaseCalculo tipoBaseCalculo) {
        BigDecimal baseCalculo = BigDecimal.ZERO;
        if (MultaFiscalizacao.BaseCalculo.LANCAMENTO.equals(tipoBaseCalculo)) {
            for (LancamentoContabilFiscal lcf : getRegistroAtivo().getLancamentosContabeis()) {
                if (lcf.equals(lancamento)) {
                    baseCalculo = baseCalculo.add(lcf.getValorCorrigido());
                }
            }
            if (baseCalculo.compareTo(BigDecimal.ZERO) == 0) {
                for (LancamentoContabilFiscal lcf : getRegistroAtivo().getLancamentosContabeis()) {
                    if (lcf.getAno().equals(ano) && (lcf.getMes() != null && lcf.getMes().equals(mes))) {
                        baseCalculo = baseCalculo.add(lcf.getValorCorrigido());
                    }
                }
            }
        } else {
            for (LancamentoMultaFiscal multaFiscal : getRegistroAtivo().getMultas()) {
                if (lancamento.equals(multaFiscal.getLancamentoContabilFiscal())) {
                    baseCalculo = baseCalculo.add(multaFiscal.getValorMulta());
                }
            }
            if (baseCalculo.compareTo(BigDecimal.ZERO) == 0) {
                for (LancamentoMultaFiscal multaFiscal : getRegistroAtivo().getMultas()) {
                    if (multaFiscal.getAno().equals(lancamento.getAno()) && (multaFiscal.getMes() != null && multaFiscal.getMes().equals(lancamento.getMes()))) {
                        baseCalculo = baseCalculo.add(multaFiscal.getValorMulta());
                    }
                }
            }
        }
        return baseCalculo;
    }

    private void lancarMulta() {
        List<Mes> mesesNaoAdicionados = Lists.newArrayList();
        BigDecimal valorUfm = BigDecimal.ZERO;
        BigDecimal valorMulta = BigDecimal.ZERO;
        BigDecimal baseCalculo = BigDecimal.ZERO;
        if (!lancamentoParaMulta.isEmpty() && TipoCalculoMultaFiscalizacao.MENSAL.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getTipoCalculoMultaFiscalizacao())) {
            for (LancamentoContabilFiscal lcf : lancamentoParaMulta) {
                LancamentoMultaFiscal lancamento = new LancamentoMultaFiscal();
                lancamento.setLancamentoContabilFiscal(lcf);
                lancamento.setMes(lcf.getMes());
                valorUfm = calculaValorMulta(lancamentoMultaFiscal.getMultaFiscalizacao(), lcf);
                valorMulta = converterIndiceParaReal(valorUfm, selecionado.getUfmArbitramento());
                baseCalculo = getBaseCalculoPorMes(lcf, lcf.getMes(), lcf.getAno(), lancamentoMultaFiscal.getMultaFiscalizacao().getBaseCalculo());
                criaLancamentoDeMulta(lcf, lancamento, valorUfm, valorMulta, baseCalculo, mesesNaoAdicionados);
            }
        } else {
            LancamentoMultaFiscal lancamento = new LancamentoMultaFiscal();
            LancamentoContabilFiscal lcf = new LancamentoContabilFiscal();
            lcf.setAno(lancamentoMultaFiscal.getAno());
            baseCalculo = getBaseCalculoPorAno(lcf.getAno(), lancamentoMultaFiscal.getMultaFiscalizacao().getBaseCalculo());
            valorUfm = calculaValorMulta(lancamentoMultaFiscal.getMultaFiscalizacao(), lcf);
            valorMulta = converterIndiceParaReal(valorUfm, selecionado.getUfmArbitramento());
            criaLancamentoDeMulta(null, lancamento, valorUfm, valorMulta, baseCalculo, mesesNaoAdicionados);
        }

        try {
            validarMesesNaoAdicionados(mesesNaoAdicionados);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarMesesNaoAdicionados(List<Mes> mesesNaoAdicionados) {
        ValidacaoException ve = new ValidacaoException();
        for (Mes mes : mesesNaoAdicionados) {
            if (!IncidenciaMultaFiscalizacao.MULTA_PUNITIVA.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getIncidenciaMultaFiscalizacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Em " + (mes != null ? mes.getDescricao() + " de " : "") + lancamentoMultaFiscal.getAno() + " não foi encontrado valor de lançamento para o cálculo da multa.");
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Em " + (mes != null ? mes.getDescricao() + " de " : "") + lancamentoMultaFiscal.getAno() + " não foi gerado " + IncidenciaMultaFiscalizacao.MULTA_PUNITIVA.getDescricao() + ".");
            }
        }
        ve.lancarException();
    }

    private void lancarMultaNotas() {
        List<Mes> mesesNaoAdicionados = Lists.newArrayList();
        BigDecimal valorUfm = BigDecimal.ZERO;
        BigDecimal valorMulta = BigDecimal.ZERO;
        BigDecimal baseCalculo = BigDecimal.ZERO;
        if (!notasParaMulta.isEmpty()) {
            for (LancamentoDoctoFiscal l : notasParaMulta) {
                LancamentoMultaFiscal lancamento = new LancamentoMultaFiscal();
                lancamento.setAno(l.getAno());
                lancamento.setMes(Mes.getMesToInt(l.getMes()));
                lancamento.setLancamentoDoctoFiscal(l);
                lancamento.setRegistroLancamentoContabil(lancamentoMultaFiscal.getRegistroLancamentoContabil());

                valorUfm = calculaValorMultaNotas(lancamentoMultaFiscal.getMultaFiscalizacao(), l);
                valorMulta = converterIndiceParaReal(valorUfm, selecionado.getUfmArbitramento());
                baseCalculo = l.getValorCorrigido();

                lancamentoMultaFiscal.setAno(lancamento.getAno());
                criaLancamentoDeMulta(null, lancamento, valorUfm, valorMulta, baseCalculo, mesesNaoAdicionados);
            }
        }

        for (Mes mes : mesesNaoAdicionados) {
            if (!IncidenciaMultaFiscalizacao.MULTA_PUNITIVA.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getIncidenciaMultaFiscalizacao())) {
                FacesUtil.addOperacaoNaoRealizada("Em " + (mes != null ? mes.getDescricao() + " de " : "") + lancamentoMultaFiscal.getAno() + " não foi encontrado valor de lançamento para o cálculo da multa.");
            } else {
                FacesUtil.addOperacaoNaoRealizada("Em " + (mes != null ? mes.getDescricao() + " de " : "") + lancamentoMultaFiscal.getAno() + " não foi gerado " + IncidenciaMultaFiscalizacao.MULTA_PUNITIVA.getDescricao() + ".");
            }
        }
    }

    private void criaLancamentoDeMulta(LancamentoContabilFiscal lancamentoDoctoFiscal, LancamentoMultaFiscal lancamento, BigDecimal valorUfm, BigDecimal valorMulta, BigDecimal baseCalculo, List<Mes> mesesNaoAdicionados) {
        if (multasALancar == null) {
            multasALancar = Lists.newArrayList();
        }
        if (valorMulta.compareTo(BigDecimal.ZERO) > 0) {
            lancamento.setLancamentoContabilFiscal(lancamentoDoctoFiscal);
            lancamento.setValorMulta(valorMulta);
            lancamento.setValorMultaIndice(valorUfm);
            lancamento.setBaseCalculo(baseCalculo);
            lancamento.setAliquota(lancamentoMultaFiscal.getMultaFiscalizacao().getAliquotaMulta());
            lancamento.setAno(lancamentoMultaFiscal.getAno());
            lancamento.setDescricao(lancamentoMultaFiscal.getDescricao());
            lancamento.setQuantidade(lancamentoMultaFiscal.getQuantidade());
            lancamento.setObservacao(lancamentoMultaFiscal.getObservacao());
            lancamento.setMultaFiscalizacao(lancamentoMultaFiscal.getMultaFiscalizacao());
            lancamento.setRegistroLancamentoContabil(lancamentoMultaFiscal.getRegistroLancamentoContabil());
            lancamento.setNumeroLancamento(lancamentoMultaFiscal.getNumeroLancamento());
            lancamentosMultasFiscais.add(lancamento);
            multasALancar.add(lancamento);
        } else {
            mesesNaoAdicionados.add(lancamento.getMes());
        }
    }

    private void validarCamposMulta() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getUfmArbitramento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Não há um data de arbitramento, informe antes de lançar as  multas.");
        }
        if (lancamentoMultaFiscal.getMultaFiscalizacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Não é possível calcular, informe a multa.");
        } else if (lancamentoMultaFiscal.getMultaFiscalizacao().getTipoCalculoMultaFiscalizacao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível calcular, pois não foi associado um tipo de cálculo a essa multa, verifique o cadastro desta multa.");
        }
        if (lancamentoParaMulta.isEmpty() && TipoCalculoMultaFiscalizacao.MENSAL.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getTipoCalculoMultaFiscalizacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Você selecionou uma multa do tipo MENSAL e não selecionou os meses. Selecione os meses desejados e tente novamente.");
        } else if (lancamentoMultaFiscal.getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Não é possível calcular, pois é necessário que informe o ano.");
        } else if (lancamentoMultaFiscal.getAno() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe um ano maior que zero.");
        }

        if (ve.getMensagens() != null && ve.getMensagens().isEmpty()) {
            boolean existeAno = false;
            boolean existeMesEAno = false;

            if (TipoCalculoMultaFiscalizacao.MENSAL.equals(lancamentoMultaFiscal.getMultaFiscalizacao().getTipoCalculoMultaFiscalizacao()) && lancamentoMultaFiscal.getMes() != null) {
                for (LevantamentoContabil levantamento : selecionado.getLevantamentosContabeis()) {
                    if (lancamentoMultaFiscal.getMes().equals(levantamento.getMes()) && lancamentoMultaFiscal.getAno().equals(levantamento.getAno())) {
                        existeMesEAno = true;
                    }
                }
                if (!existeMesEAno) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível calcular pois o ano e mês informado não está cadastrado no levantamento contábil.");
                }
            } else {
                for (LevantamentoContabil levantamento : selecionado.getLevantamentosContabeis()) {
                    if (this.lancamentoMultaFiscal.getAno().equals(levantamento.getAno())) {
                        existeAno = true;
                        break;
                    }
                }
                if (!existeAno) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível calcular pois o ano informado não está cadastrado no levantamento contábil.");
                }
            }
        }
        ve.lancarException();
    }

    private void validarCamposMultaNotas() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getUfmArbitramento() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não há um data de arbitramento, informe antes de lançar as  multas.");
        }
        if (this.lancamentoMultaFiscal.getMultaFiscalizacao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível calcular, informe a multa.");
        } else if (this.lancamentoMultaFiscal.getMultaFiscalizacao().getTipoCalculoMultaFiscalizacao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível calcular, pois não foi associado um tipo de cálculo a essa multa, verifique o cadastro desta multa.");
        }
        ve.lancarException();
    }

    public void gerarDocumentoTermoFinalizacaoFiscalizacao() {
        documentoOficial = gerarDocumento(TipoDoctoAcaoFiscal.FINALIZACAO);
    }

    public boolean podeGerarAuto(RegistroLancamentoContabil registro) {
        if (!registro.isCancelado() && !registro.isEstornado()) {
            return existeMulta(registro) || selecionado.getProgramacaoFiscal().getDenunciaEspontanea();
        }
        return false;
    }

    public boolean existeTermoFiscalizacaoGerado() {
        DoctoAcaoFiscal docto = acaoFiscalFacade.getProgramacaoFiscalFacade().buscarDocumentoFiscalPorTipo(selecionado, TipoDoctoAcaoFiscal.FISCALIZACAO);
        if (docto == null) {
            return false;
        } else {
            return true;
        }
    }

    public BigDecimal buscarValorPagoIss(LancamentoContabilFiscal lancamento) {
        if (lancamento.getAliquotaISS().compareTo(BigDecimal.ZERO) > 0 && lancamento.getTributado()) {
            ValoresIssPago valores = new ValoresIssPago(lancamento);
            if (valoresPagos == null) {
                valoresPagos = Maps.newHashMap();
            }
            if (valoresPagos.containsKey(valores)) {
                return valoresPagos.get(valores);
            }
            valoresPagos.put(valores, acaoFiscalFacade.buscaValorPagoISS(lancamento, selecionado.getCadastroEconomico()));
            return valoresPagos.get(valores);
        }
        return lancamento.getIssPago();
    }

    public boolean verificaFiscalSupervisor() {
        return supervisor = acaoFiscalFacade.verificarFiscalSupervisor(getSistemaControlador().getUsuarioCorrente());
    }

    public BigDecimal getValorIndice() {
        BigDecimal valor = BigDecimal.ZERO;
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (this.lancamentoDoctoFiscal.getDataEmissao() != null) {
            BigDecimal indice = recuperaIndiceLevantamentoFiscal(this.lancamentoDoctoFiscal.getDataEmissao());
            if (this.lancamentoDoctoFiscal.getBaseCalculoISS() != null
                && this.lancamentoDoctoFiscal.getPorcentagemISS() != null) {
                valor = this.lancamentoDoctoFiscal.getBaseCalculoISS().multiply(this.lancamentoDoctoFiscal.getPorcentagemISS().divide(CEM, 4, RoundingMode.HALF_UP));
                valorTotal = converterRealParaIndice(valor, indice);
            }
        }
        lancamentoDoctoFiscal.setValorIndice(valorTotal);
        return valorTotal;
    }

    public BigDecimal getValorISS() {
        BigDecimal valor = BigDecimal.ZERO;
        if (this.lancamentoDoctoFiscal.getPorcentagemISS() != null && this.lancamentoDoctoFiscal.getPorcentagemISS().compareTo(BigDecimal.ZERO) > 0) {
            valor = this.lancamentoDoctoFiscal.getBaseCalculoISS().multiply(this.lancamentoDoctoFiscal.getPorcentagemISS().divide(CEM, 4, RoundingMode.HALF_UP));
        }
        lancamentoDoctoFiscal.setValorISS(valor);
        return valor;
    }

    public void copiaValorNotaFiscalParaBaseCalculo() {
        if (!this.lancamentoDoctoFiscal.getNaoTributada()) {
            this.lancamentoDoctoFiscal.setBaseCalculoISS(this.lancamentoDoctoFiscal.getValorNotaFiscal());
            this.lancamentoDoctoFiscal.setValorApurado(this.lancamentoDoctoFiscal.getValorNotaFiscal());
        }
    }

    public void zeraBaseCalculoAliquota() {
        if (this.lancamentoDoctoFiscal.getNaoTributada()) {
            this.lancamentoDoctoFiscal.setBaseCalculoISS(BigDecimal.ZERO);
        } else {
            this.lancamentoDoctoFiscal.setTipoNaoTributacao(null);
        }
    }

    public BigDecimal recuperaIndiceLevantamentoFiscal(Date dataNotaFiscal) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataNotaFiscal);

        int mes = c.get(Calendar.MONTH) + 1;
        int ano = c.get(Calendar.YEAR);

        for (LevantamentoContabil levantamentoContabil : this.selecionado.getLevantamentosContabeis()) {
            if (levantamentoContabil.getMes().getNumeroMes() == mes && levantamentoContabil.getAno() == ano) {
                return levantamentoContabil.getValorIndice();
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal recuperaIndiceLevantamentoFiscal(int ano, int mes) {
        for (LevantamentoContabil levantamentoContabil : this.selecionado.getLevantamentosContabeis()) {
            if (levantamentoContabil.getMes().getNumeroMes() == mes && levantamentoContabil.getAno() == ano) {
                return levantamentoContabil.getValorIndice();
            }
        }
        return BigDecimal.ZERO;
    }

    private void validarAdicionarMulta() {
        ValidacaoException ve = new ValidacaoException();
        if (this.lancamentoMultaFiscal.getValorMulta() == null && this.lancamentoMultaFiscal.getValorMultaIndice() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível adicionar a multa pois não foi calculada. Clique em calcular antes de adicioná-la");
        } else if (getValorMulta().compareTo(BigDecimal.ZERO) <= 0 && getValorMultaUFM().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível adicionar multa que apresenta valores zerados ou abaixo de zero.");
        }
        if (!lancamentoMultaFiscal.getDescricao().isEmpty() && lancamentoMultaFiscal.getDescricao().length() > 3000) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Descrição muito longa.");
        }
        if (!lancamentoMultaFiscal.getObservacao().isEmpty() && lancamentoMultaFiscal.getObservacao().length() > 254) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Observação muito longa.");
        }
        if (getRegistroAtivo() == null || getRegistroAtivo().getNumeroAno() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Lancamento Contábil está sem número de registro.");
        }
        ve.lancarException();
    }

    public void limparCamposMulta() {
        this.lancamentosMultasFiscais = Lists.newArrayList();
        this.lancamentoMultaFiscal = new LancamentoMultaFiscal();
        this.lancamentoMultaFiscal.setRegistroLancamentoContabil(getRegistroAtivo());
    }

    public void novoEstornoAutoInfracao(AutoInfracaoFiscal autoInfracaoFiscal) {
        autoInfracaoFiscalSelecionado = autoInfracaoFiscal;
        autoInfracaoFiscalSelecionado.setDataEstorno(new Date());
        autoInfracaoFiscalSelecionado.setMotivoEstorno("");
        RequestContext.getCurrentInstance().update("formEstornoAutoInfracao");
    }

    public void novaRetificacaoAutoInfracao(AutoInfracaoFiscal autoInfracaoFiscal) {
        autoInfracaoFiscalSelecionado = autoInfracaoFiscal;
        autoInfracaoFiscalSelecionado.setDataEstorno(new Date());
        autoInfracaoFiscalSelecionado.setMotivoEstorno("");
        RequestContext.getCurrentInstance().update("formRetificacaoAutoInfracao");
    }

    public AutoInfracaoFiscal getAutoInfracaoFiscalSelecionado() {
        return autoInfracaoFiscalSelecionado;
    }

    public void setAutoInfracaoFiscalSelecionado(AutoInfracaoFiscal autoInfracaoFiscalSelecionado) {
        this.autoInfracaoFiscalSelecionado = autoInfracaoFiscalSelecionado;
    }

    private void validarCamposEstornoRetificacaoAutoInfracao(SituacaoAutoInfracao situacaoAutoInfracao) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (autoInfracaoFiscalSelecionado == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Auto de Infração não selecionado!");
        }
        if (autoInfracaoFiscalSelecionado.getDataEstorno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data " + (situacaoAutoInfracao.equals(SituacaoAutoInfracao.ESTORNADO) ? "do Estorno." : "da Retificação."));
        }
        if (situacaoAutoInfracao.equals(SituacaoAutoInfracao.ESTORNADO)) {
            if (!RegistroLancamentoContabil.Situacao.AGUARDANDO.equals(autoInfracaoFiscalSelecionado.getRegistro().getSituacao()) &&
                !RegistroLancamentoContabil.Situacao.AUTO_INFRACAO.equals(autoInfracaoFiscalSelecionado.getRegistro().getSituacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Auto de Infração só pode ser estornado se o seu registro estiver com a situação igual a 'Aguardando' ou 'Auto de Infração'");
            }
        } else {
            if (!RegistroLancamentoContabil.Situacao.AUTO_INFRACAO.equals(autoInfracaoFiscalSelecionado.getRegistro().getSituacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Auto de Infração só pode ser retificado se o seu registro estiver com a situação igual a 'Auto de Infração'");
            }
        }
        if (acaoFiscalFacade.temDebitosDiferenteDeAbertoOuCancelado(selecionado)) {
            String mensagem = "Esta ação fiscal possui débitos com situação diferente de EM ABERTO ou CANCELADO e isto não permite ";
            if (situacaoAutoInfracao.equals(SituacaoAutoInfracao.ESTORNADO)) {
                mensagem += "o estorno ou cancelamento";
            } else {
                mensagem += "a retificação";
            }
            mensagem += " do Auto de Infração";
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
        }
        if (autoInfracaoFiscalSelecionado.getMotivoEstorno() == null || autoInfracaoFiscalSelecionado.getMotivoEstorno().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o motivo " + (situacaoAutoInfracao.equals(SituacaoAutoInfracao.ESTORNADO) ? "do Estorno." : "da Retificação."));
        } else if (autoInfracaoFiscalSelecionado.getMotivoEstorno().length() > 500) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Motivo muito longo.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void gerarRelatorioFiscal() {
        documentoOficial = gerarDocumento(TipoDoctoAcaoFiscal.RELATORIO_FISCAL);
    }

    public List<BigDecimal> listaAliquotasIss() {
        List<BigDecimal> retorno = Lists.newLinkedList();
        if (lancamentoDoctoFiscal.getNaoTributada() || lancamentoDoctoFiscal.getNotaCancelada()) {
            lancamentoDoctoFiscal.setPorcentagemISS(BigDecimal.ZERO);
            retorno.clear();
            retorno.add(BigDecimal.ZERO);
            return retorno;
        }
        boolean aliquotaRepetida = false;
        for (Servico s : acaoFiscalFacade.getServicoFacade().listaPorCadastroEconomico(selecionado.getCadastroEconomico())) {
            if (s.getAliquotaISSHomologado() != null && s.getAliquotaISSHomologado().compareTo(BigDecimal.ZERO) > 0) {
                if (!retorno.isEmpty()) {
                    for (BigDecimal bd : retorno) {
                        if (bd.compareTo(s.getAliquotaISSHomologado()) == 0) {
                            aliquotaRepetida = true;
                        }
                    }
                    if (!aliquotaRepetida) {
                        retorno.add(s.getAliquotaISSHomologado());
                        aliquotaRepetida = false;
                    }

                } else {
                    retorno.add(s.getAliquotaISSHomologado());
                }
            }
        }
        if (retorno.isEmpty()) {
            retorno.add(new BigDecimal("5"));
        }
        return retorno;
    }

    public BigDecimal primeiraAliquotaIss() {
        return BigDecimal.ZERO;
    }

    public void pegaDescricaoDaMulta(SelectEvent event) {
        MultaFiscalizacao m = (MultaFiscalizacao) event.getObject();
        selecionaMulta(m);
    }

    private void selecionaMulta(MultaFiscalizacao m) {
        lancamentoMultaFiscal.setDescricao(m.getEmbasamento());
        multasALancar = Lists.newArrayList();
        lancamentosMultasFiscais = Lists.newArrayList();
        lancamentoMultaFiscal.setQuantidade(1);
        if (!lancamentoParaMulta.isEmpty()) {
            lancamentoMultaFiscal.setAno(lancamentoParaMulta.get(0).getAno());
            if (TipoCalculoMultaFiscalizacao.ANUAL.equals(m.getTipoCalculoMultaFiscalizacao())) {
                FacesUtil.addAtencao("Você selecionou uma multa do tipo ANUAL. Os meses selecionados serão ignorados e o ano informado será considerado para o cálculo.");
            }
        }
    }

    public void pegarDescricaoDaMultaNotas(SelectEvent event) {
        MultaFiscalizacao m = (MultaFiscalizacao) event.getObject();
        selecionarMultaNotas(m);
    }

    private void selecionarMultaNotas(MultaFiscalizacao m) {
        lancamentoMultaFiscal.setDescricao(m.getEmbasamento());
        multasALancar = Lists.newArrayList();
        lancamentosMultasFiscais = Lists.newArrayList();
        lancamentoMultaFiscal.setQuantidade(1);
    }

    public void recuperaMulta(LancamentoMultaFiscal multa) {
        lancamentoMultaFiscalRecuperada = multa;
    }

    public void carregaListaTotalizador() {
        int ano = 0;
        int mes = 0;
        List<String> notasNaoCarregadas = Lists.newArrayList();
        for (LancamentoDoctoFiscal lancamento : this.selecionado.getLancamentoDoctoFiscal()) {
            if (lancamento.getDataEmissao() != null && notaFiscalEstaNoPeriodoFiscalizacao(lancamento)) {
                totalizador = new TotalizadorLancamentoContabil();
                totalizador.setAliquota(lancamento.getPorcentagemISS());
                mes = lancamento.getMes();
                ano = lancamento.getAno();
                totalizador.setMes(Mes.getMesToInt(mes));
                totalizador.setAno(ano);
                totalizador.setValor(lancamento.getValorApurado());
                totalizador.setTributado(!lancamento.getNaoTributada());
                if (listaTotalizador.contains(totalizador)) {
                    BigDecimal valor = listaTotalizador.get(listaTotalizador.indexOf(totalizador)).getValor();
                    valor = valor.add(lancamento.getValorApurado());
                    listaTotalizador.get(listaTotalizador.indexOf(totalizador)).setValor(valor);
                } else {
                    listaTotalizador.add(totalizador);
                }
            } else {
                notasNaoCarregadas.add(lancamento.getNumeroNotaFiscal().toString());
            }
        }
        if (!notasNaoCarregadas.isEmpty()) {
            FacesUtil.addError("Atenção", "Não foi possível carregar os lançamentos contábeis para as notas fiscais: " + StringUtils.join(notasNaoCarregadas, ", ") + ". A data de emissão não é válida.");
        }
    }

    private void carregarLancamentoContabil() {
        valoresPagos = null;
        aliquotasVariadas = new HashMap<>();
        carregaListaTotalizador();
        Collections.sort(listaTotalizador);
        if (getRegistroAtivo() == null) {
            selecionado.getLancamentosContabeis().add(new RegistroLancamentoContabil(selecionado));
            getRegistroAtivo().setNumeroProcessoAdministrativo(registro.getNumeroProcessoAdministrativo());
        }
        getRegistroAtivo().setAcaoFiscal(selecionado);
        for (TotalizadorLancamentoContabil totalizador : listaTotalizador) {
            adicionarLancamentoContabilPelaNota(totalizador);
        }
        for (String index : aliquotasVariadas.keySet()) {
            adicionarLancamentoContabilPelaAliquota(index);
        }
        adicionarLancamentoContabilNfse();
        List<LancamentoContabilFiscal> duplicados = Lists.newArrayList();
        for (LancamentoContabilFiscal lancamento : getRegistroAtivo().getLancamentosContabeis()) {
            if (semValorDeclaradoSemAliquotaComMovimento(lancamento)) {
                duplicados.addAll(definirAliquotaParaLancamentosZerados(lancamento));
            }
        }
        if (!duplicados.isEmpty()) {
            for (LancamentoContabilFiscal duplicado : duplicados) {
                getRegistroAtivo().getLancamentosContabeis().add(duplicado);
            }
        }
        removerValoresDeclaradosSemAliquota();
        ordenarLancamentosContabeis();
        mesclarLancamentosCompativeis();
        definirSequenciaZeraAliquotaRegistrosSemValores();
        Collections.sort(getRegistroAtivo().getLancamentosContabeis());
        removerLancamentosZeradosQuandoTemOutroLancamentoComValorMesmoPeriodo();
    }

    private void definirSequenciaZeraAliquotaRegistrosSemValores() {
        Long sequencia = 1l;
        for (LancamentoContabilFiscal lancamento : getRegistroAtivo().getLancamentosContabeis()) {
            lancamento.setSequencia(sequencia);
            sequencia++;
            if (lancamento.getValorDeclarado().compareTo(BigDecimal.ZERO) == 0
                && lancamento.getValorApurado().compareTo(BigDecimal.ZERO) == 0
                && lancamento.getIssPago().compareTo(BigDecimal.ZERO) == 0) {
                lancamento.setAliquotaISS(BigDecimal.ZERO);
            }
            atualizarValoresTabelaLancamentoContabil(lancamento);
        }
    }

    private void removerValoresDeclaradosSemAliquota() {
        List<LancamentoContabilFiscal> lancamentosParaExcluir = Lists.newArrayList();

        for (LancamentoContabilFiscal lancamento : getRegistroAtivo().getLancamentosContabeis()) {
            if (semValorDeclaradoSemAliquotaComMovimento(lancamento)) {
                lancamentosParaExcluir.add(lancamento);
            }
        }
        for (LancamentoContabilFiscal excluir : lancamentosParaExcluir) {
            getRegistroAtivo().getLancamentosContabeis().remove(excluir);
        }

    }

    private void mesclarLancamentosCompativeis() {
        List<LancamentoContabilFiscal> listaParaSeremRemovidos = new ArrayList<>();
        int tamanho = getRegistroAtivo().getLancamentosContabeis().size() - 1;
        for (int i = 0; i < tamanho; i++) {
            LancamentoContabilFiscal lancamento = getRegistroAtivo().getLancamentosContabeis().get(i);
            if (i < tamanho) {
                int indiceDoProximo = i + 1;
                LancamentoContabilFiscal proximoLancamento = getRegistroAtivo().getLancamentosContabeis().get(indiceDoProximo);
                if ((isMesmoMesAnoComDeclaradoApuradoZerado(lancamento, proximoLancamento)
                    || mesmoMesAnoAliquotaZeradaOuIgual(lancamento, proximoLancamento))
                    && ambosTemMesmoTipoTributacao(lancamento, proximoLancamento)) {
                    proximoLancamento.setValorApurado(proximoLancamento.getValorApurado().add(lancamento.getValorApurado()));
                    if (lancamento.getTributado()) {
                        proximoLancamento.setBaseCalculo(proximoLancamento.getValorDeclarado().subtract(proximoLancamento.getValorApurado()));
                        proximoLancamento.setIssApurado(proximoLancamento.getValorApurado().multiply(proximoLancamento.getAliquotaISS().divide(CEM)));
                        proximoLancamento.setValorDeclarado(buscarValorDeclaradoISS(proximoLancamento));
                        proximoLancamento.setIssPago(buscarValorPagoIss(proximoLancamento));
                        arbitrar(proximoLancamento);
                        proximoLancamento.setIssDevido((proximoLancamento.getIssApurado().subtract(proximoLancamento.getIssPago())).multiply(getRegistroAtivo().getLancamentosContabeis().get(indiceDoProximo).getIndiceCorrecao()));
                    } else {
                        proximoLancamento.setBaseCalculo(BigDecimal.ZERO);
                        proximoLancamento.setIssApurado(BigDecimal.ZERO);
                        proximoLancamento.setValorDeclarado(proximoLancamento.getValorDeclarado().add(lancamento.getValorDeclarado()));
                        proximoLancamento.setIssPago(BigDecimal.ZERO);
                        proximoLancamento.setIssDevido(BigDecimal.ZERO);
                    }
                    listaParaSeremRemovidos.add(lancamento);

                } else {
                    proximoLancamento = getRegistroAtivo().getLancamentosContabeis().get(i);
                }
            }
        }

        for (LancamentoContabilFiscal lancamento : listaParaSeremRemovidos) {
            getRegistroAtivo().getLancamentosContabeis().remove(lancamento);
        }
    }

    private void removerLancamentosZeradosQuandoTemOutroLancamentoComValorMesmoPeriodo() {
        List<LancamentoContabilFiscal> listaParaSeremRemovidos = new ArrayList<>();
        int tamanho = getRegistroAtivo().getLancamentosContabeis().size() - 1;
        for (int i = 0; i < tamanho; i++) {
            LancamentoContabilFiscal lancamento = getRegistroAtivo().getLancamentosContabeis().get(i);
            if (i < tamanho) {
                int indiceDoProximo = i + 1;
                LancamentoContabilFiscal proximoLancamento = getRegistroAtivo().getLancamentosContabeis().get(indiceDoProximo);
                if (isMesmoMesAnoComDeclaradoApuradoZerado(lancamento, proximoLancamento)) {
                    listaParaSeremRemovidos.add(lancamento);
                }
            }
        }

        for (LancamentoContabilFiscal lancamento : listaParaSeremRemovidos) {
            getRegistroAtivo().getLancamentosContabeis().remove(lancamento);
        }
    }

    private boolean ambosTemMesmoTipoTributacao(LancamentoContabilFiscal lancamento, LancamentoContabilFiscal proximoLancamento) {
        return lancamento.getTributado().equals(proximoLancamento.getTributado());
    }

    private boolean mesmoMesAnoAliquotaZeradaOuIgual(LancamentoContabilFiscal lancamento, LancamentoContabilFiscal proximoLancamento) {
        return ((lancamento.getAliquotaISS().compareTo(BigDecimal.ZERO) == 0
            || lancamento.getAliquotaISS().equals(proximoLancamento.getAliquotaISS()))
            && lancamento.getMes().equals(proximoLancamento.getMes())
            && lancamento.getAno().equals(proximoLancamento.getAno()));
    }

    private boolean isMesmoMesAnoComDeclaradoApuradoZerado(LancamentoContabilFiscal lancamento, LancamentoContabilFiscal proximoLancamento) {
        boolean retorno = lancamento.getValorDeclarado().compareTo(BigDecimal.ZERO) == 0
            && lancamento.getValorApurado().compareTo(BigDecimal.ZERO) == 0
            && lancamento.getIssPago().compareTo(BigDecimal.ZERO) == 0
            && lancamento.getMes().equals(proximoLancamento.getMes())
            && lancamento.getAno().equals(proximoLancamento.getAno());
        return retorno;
    }

    private boolean semValorDeclaradoSemAliquotaComMovimento(LancamentoContabilFiscal lancamento) {
        boolean retorno = lancamento.getValorDeclarado().compareTo(BigDecimal.ZERO) == 0
            && lancamento.getAliquotaISS().compareTo(BigDecimal.ZERO) == 0
            && lancamento.getIssPago().compareTo(BigDecimal.ZERO) == 0
            && !lancamento.getSemMovimento();
        return retorno;
    }

    private List<LancamentoContabilFiscal> definirAliquotaParaLancamentosZerados(LancamentoContabilFiscal lancamento) {
        List<LancamentoContabilFiscal> duplicados = Lists.newArrayList();
        List<BigDecimal> aliquotas = acaoFiscalFacade.buscarAliquotasServicos(selecionado.getCadastroEconomico());
        if (aliquotas.isEmpty()) {
            aliquotas.add(new BigDecimal("5"));
        }

        for (BigDecimal aliquota : aliquotas) {
            if (aliquota != null) {
                if (aliquotas.size() == 1) {
                    lancamento.setAliquotaISS(aliquota);
                    lancamento.setValorDeclarado(buscarValorDeclaradoISS(lancamento));
                    lancamento.setIssPago(buscarValorPagoIss(lancamento));
                } else {
                    duplicados.add(duplicarLancamentoContabilSemEvento(lancamento, aliquota));
                }
            }
        }
        return duplicados;
    }

    private void adicionarLancamentoContabilPelaAliquota(String index) {
        LancamentoContabilFiscal lancamento = new LancamentoContabilFiscal();
        lancamento.setRegistroLancamentoContabil(getRegistroAtivo());
        lancamento.setMes(Mes.getMesToInt(aliquotasVariadas.get(index).getCalculo().getProcessoCalculoISS().getMesReferencia()));
        lancamento.setAno(aliquotasVariadas.get(index).getCalculo().getProcessoCalculoISS().getExercicio().getAno());
        lancamento.setSequencia(Long.valueOf(selecionado.getLancamentosContabeis().size() + 1));
        lancamento.setIndiceCorrecao(BigDecimal.ONE);
        lancamento.setAliquotaISS(aliquotasVariadas.get(index).getAliquota());
        lancamento.setSemMovimento(aliquotasVariadas.get(index).getCalculo().getAusenciaMovimento());

        if (!lancamento.getSemMovimento()) {
            lancamento.setValorDeclarado(aliquotasVariadas.get(index).getBaseCalculo());
            lancamento.setIssPago(buscarValorPagoIss(lancamento));
            lancamento.setIssApurado(lancamento.getValorApurado().multiply(lancamento.getAliquotaISS().divide(CEM)));
        } else {
            lancamento.setValorDeclarado(BigDecimal.ZERO);
            lancamento.setIssPago(BigDecimal.ZERO);
            lancamento.setIssApurado(BigDecimal.ZERO);
        }
        lancamento.setBaseCalculo(lancamento.getValorDeclarado().subtract(lancamento.getValorApurado()));
        arbitrar(lancamento);
        lancamento.setIssDevido((lancamento.getIssApurado().subtract(lancamento.getIssPago())).multiply(lancamento.getIndiceCorrecao()));
        getRegistroAtivo().getLancamentosContabeis().add(lancamento);
    }

    private void adicionarLancamentoContabilPelaNota(TotalizadorLancamentoContabil totalizador) {
        LancamentoContabilFiscal lancamento = new LancamentoContabilFiscal();
        lancamento.setRegistroLancamentoContabil(getRegistroAtivo());
        lancamento.setMes(totalizador.getMes());
        lancamento.setAno(totalizador.getAno());
        lancamento.setSequencia(Long.valueOf(selecionado.getLancamentosContabeis().size() + 1));
        lancamento.setIndiceCorrecao(BigDecimal.ONE);
        lancamento.setAliquotaISS(totalizador.getAliquota());
        lancamento.setValorApurado(totalizador.getValor());
        lancamento.setTributado(totalizador.isTributado());
        if (lancamento.getTributado()) {
            lancamento.setValorDeclarado(buscarValorDeclaradoISS(lancamento));
            lancamento.setIssPago(buscarValorPagoIss(lancamento));
            lancamento.setIssApurado(lancamento.getValorApurado().multiply(lancamento.getAliquotaISS().divide(CEM)));
        } else {
            lancamento.setValorDeclarado(totalizador.getValor());
            lancamento.setIssPago(BigDecimal.ZERO);
            lancamento.setIssApurado(BigDecimal.ZERO);
        }
        lancamento.setBaseCalculo(lancamento.getValorDeclarado().subtract(lancamento.getValorApurado()));
        arbitrar(lancamento);
        BigDecimal saldoIss = (lancamento.getIssApurado().subtract(lancamento.getIssPago())).multiply(lancamento.getIndiceCorrecao());
        lancamento.setIssDevido(saldoIss);
        getRegistroAtivo().getLancamentosContabeis().add(lancamento);
    }

    private void adicionarLancamentoContabilNfse() {
        List<TotalizadorLancamentoContabil> totalizadorNfse = acaoFiscalFacade.buscarLancamentosNfse(selecionado);
        for (TotalizadorLancamentoContabil nfse : totalizadorNfse) {
            LancamentoContabilFiscal lancamento = new LancamentoContabilFiscal();
            lancamento.setRegistroLancamentoContabil(getRegistroAtivo());
            lancamento.setMes(nfse.getMes());
            lancamento.setAno(nfse.getAno());
            lancamento.setSequencia(Long.valueOf(selecionado.getLancamentosContabeis().size() + 1));
            lancamento.setIndiceCorrecao(BigDecimal.ONE);
            lancamento.setAliquotaISS(nfse.getAliquota());
            lancamento.setValorApurado(BigDecimal.ZERO);
            lancamento.setTributado(nfse.isTributado());
            lancamento.setNfse(true);

            lancamento.setIssPago(nfse.getValor());
            lancamento.setValorDeclarado(BigDecimal.ZERO);
            lancamento.setIssApurado(BigDecimal.ZERO);
            lancamento.setBaseCalculo(BigDecimal.ZERO);

            lancamento.setIssDevido(BigDecimal.ZERO);
            getRegistroAtivo().getLancamentosContabeis().add(lancamento);
        }
    }

    private void ordenarLancamentosContabeis() {
        Collections.sort(getRegistroAtivo().getLancamentosContabeis(), new Comparator<LancamentoContabilFiscal>() {
            @Override
            public int compare(LancamentoContabilFiscal o1, LancamentoContabilFiscal o2) {
                try {
                    int retorno = o2.getAno().compareTo(o1.getAno());

                    if (retorno == 0) {
                        retorno = o2.getMes().compareTo(o1.getMes());
                    }
                    if (retorno == 0) {
                        retorno = o2.getAliquotaISS().compareTo(o1.getAliquotaISS());
                    }
                    if (retorno == 0) {
                        retorno = o2.getTributado().compareTo(o1.getTributado());
                    }
                    return retorno;
                } catch (Exception e) {
                    logger.trace("Erro ordenando Lançamentos Contábeis", e);
                    return Integer.MAX_VALUE;
                }
            }
        });
    }

    @Override
    public AbstractFacade getFacede() {
        return this.acaoFiscalFacade;
    }

    public void limparCamposLancamentoDoctoFiscal() {
        this.lancamentoDoctoFiscal = new LancamentoDoctoFiscal();
        this.lancamentoDoctoFiscal.setPorcentagemISS(primeiraAliquotaIss());
    }

    public BigDecimal retornaTotalBaseCalculoISS() {
        BigDecimal soma = BigDecimal.ZERO;
        if (!this.selecionado.getLancamentoDoctoFiscal().isEmpty() || this.selecionado.getLancamentoDoctoFiscal() != null) {
            for (LancamentoDoctoFiscal docto : this.selecionado.getLancamentoDoctoFiscal()) {
                if (!docto.getNotaCancelada()) {
                    soma = soma.add(docto.getBaseCalculoISS());
                }
            }
        }
        return soma;
    }

    public boolean todosAutoInfracaoValidos() {
        if (selecionado.getLancamentosContabeis().isEmpty()) {
            return false;
        }
        for (RegistroLancamentoContabil registro : selecionado.getLancamentosContabeis()) {
            if (RegistroLancamentoContabil.Situacao.AUTO_INFRACAO.equals(registro.getSituacao())) {
                return true;
            }
        }
        return false;
    }

    public boolean existeAutoInfracaoValido() {
        for (RegistroLancamentoContabil registro : selecionado.getLancamentosContabeis()) {
            if (registro.getAutoInfracaoValido() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean existeAutoInfracaoEntregue() {
        if (selecionado != null && selecionado.getLancamentosContabeis() != null) {
            for (RegistroLancamentoContabil registro : selecionado.getLancamentosContabeis()) {
                if (registro.getAutosInfracao().isEmpty()) {
                    return true;
                } else if (registro.getAutoInfracaoEntregue() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean existeMulta(RegistroLancamentoContabil r) {
        if (!r.getMultas().isEmpty()) {
            return true;
        }
        return false;
    }

    private void preparaTotalizador() throws NumberFormatException {
        listaTotalizador.clear();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selecionado.getDataLevantamentoInicial());
        int mesInicio = calendar.get(Calendar.MONTH) + 1;
        int anoInicio = calendar.get(Calendar.YEAR);
        int anoMesInicio = Integer.parseInt(anoInicio + "" + StringUtil.preencheString("" + mesInicio, 2, '0'));

        calendar.setTime(selecionado.getDataLevantamentoFinal());
        int mesFim = calendar.get(Calendar.MONTH) + 1;
        int anoFim = calendar.get(Calendar.YEAR);
        int anoMesFim = Integer.parseInt(anoFim + "" + StringUtil.preencheString("" + mesFim, 2, '0'));
        while (anoMesInicio <= anoMesFim) {
            if (mesInicio <= 12) {
                this.totalizador = new TotalizadorLancamentoContabil();
                this.totalizador.setAliquota(BigDecimal.ZERO);
                this.totalizador.setMes(Mes.getMesToInt(mesInicio));
                this.totalizador.setAno(anoInicio);
                this.totalizador.setValor(BigDecimal.ZERO);
                this.listaTotalizador.add(this.totalizador);
                this.totalizador.setTributado(true);
                mesInicio++;
            } else {
                mesInicio = 1;
                anoInicio++;
            }
            anoMesInicio = Integer.parseInt(anoInicio + "" + StringUtil.preencheString("" + mesInicio, 2, '0'));
        }
    }

    private boolean notaFiscalEstaNoPeriodoFiscalizacao(LancamentoDoctoFiscal lanc) {
        Long inicio = DataUtil.dataSemHorario(selecionado.getDataLevantamentoInicial()).getTime();
        Long fim = DataUtil.dataSemHorario(selecionado.getDataLevantamentoFinal()).getTime();
        return DataUtil.dataSemHorario(lanc.getDataEmissao()).getTime() >= inicio
            && DataUtil.dataSemHorario(lanc.getDataEmissao()).getTime() <= fim;
    }

    public void defineRegistroDaMulta(RegistroLancamentoContabil registro) {
        boolean anoDeferente = false;
        if (!lancamentoParaMulta.isEmpty()) {
            Integer ano = 0;
            for (LancamentoContabilFiscal lcf : lancamentoParaMulta) {
                if (ano <= 0) {
                    ano = lcf.getAno();
                } else {
                    if (lcf.getAno().compareTo(ano) != 0) {
                        anoDeferente = true;
                        break;
                    }
                }
            }
        }

        if (!anoDeferente) {
            multasALancar = Lists.newArrayList();
            lancamentosMultasFiscais.clear();
            lancamentoMultaFiscal.setRegistroLancamentoContabil(registro);
            FacesUtil.executaJavaScript("lancamentoMulta.show()");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Você selecionou registros de anos diferentes. Selecione registros de um mesmo ano para continuar.");
        }
    }

    public void removeRegistro(RegistroLancamentoContabil registro) {
        if (registro.getAutosInfracao().isEmpty()) {
            selecionado.getLancamentosContabeis().remove(registro);
        } else {
            FacesUtil.addError("Atenção", "Não é possível excluir o Lançamento pois o Auto de Infração já foi gerado.");
        }
    }

    public boolean getExisteMulta() {
        for (RegistroLancamentoContabil r : selecionado.getLancamentosContabeis()) {
            if (existeMulta(r)) {
                return true;
            }
        }
        return false;
    }

    private void updateRodapes(Long registro) {
        FacesUtil.atualizarComponente("Formulario:tabView:tableLancamentoContabil" + registro + ":valorTatolDeclarado");
        FacesUtil.atualizarComponente("Formulario:tabView:tableLancamentoContabil" + registro + ":valorTotalApurado");
        FacesUtil.atualizarComponente("Formulario:tabView:tableLancamentoContabil" + registro + ":valorTotalBase");
        FacesUtil.atualizarComponente("Formulario:tabView:tableLancamentoContabil" + registro + ":valorTotalIssPago");
        FacesUtil.atualizarComponente("Formulario:tabView:tableLancamentoContabil" + registro + ":valorTotalIssApurado");
        FacesUtil.atualizarComponente("Formulario:tabView:tableLancamentoContabil" + registro + ":valorTotalSaldo");
        FacesUtil.atualizarComponente("Formulario:tabView:tableLancamentoContabil" + registro + ":valorTotalCorrecao");
        FacesUtil.atualizarComponente("Formulario:tabView:tableLancamentoContabil" + registro + ":valorTotalCorrigido");
        FacesUtil.atualizarComponente("Formulario:tabView:tableLancamentoContabil" + registro + ":valorTotalJuros");
        FacesUtil.atualizarComponente("Formulario:tabView:tableLancamentoContabil" + registro + ":valorTotalMulta");
        FacesUtil.atualizarComponente("Formulario:tabView:tableLancamentoContabil" + registro + ":valorTotal");
    }

    public void updateTabela(Long registro, int var2) {
        FacesUtil.atualizarComponente("Formulario:tabView:tableLancamentoContabil" + registro + var2);
    }

    public void novoRegistro() {
        try {
            validarNovoRegistro();
            registro = new RegistroLancamentoContabil(selecionado);
            registro.setNumeroRegistro(new Long(selecionado.getLancamentosContabeis().size() + 1));
            FacesUtil.executaJavaScript("lancamentoContabil.show();");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarNovoRegistro() {
        ValidacaoException ve = new ValidacaoException();
        for (RegistroLancamentoContabil registroLancamentoContabil : selecionado.getLancamentosContabeis()) {
            if (!RegistroLancamentoContabil.Situacao.ESTORNADO.equals(registroLancamentoContabil.getSituacao())
                && !RegistroLancamentoContabil.Situacao.CANCELADO.equals(registroLancamentoContabil.getSituacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Há um Registro de Lançamento Contábil válido, cancele ou remova este registro antes de continuar.");
                break;
            }
        }
        if (parametroFiscalizacao == null) {
            parametroFiscalizacao = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarParametroFiscalizacao(getSistemaControlador().getExercicioCorrente());
        }
        parametroFiscalizacao = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperar(parametroFiscalizacao.getId());
        if (parametroFiscalizacao.getDividasIssqn().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existem Dívidas do Lançamento Contábil nos Parâmetros de Fiscalização de " + parametroFiscalizacao.getExercicio().getAno());
        }
        ve.lancarException();
    }

    public List<Integer> obterAnosDosLancamentos(RegistroLancamentoContabil rlc) {
        List<Integer> retorno = new ArrayList<>();
        retorno.addAll(rlc.getLancamentosPorAno().keySet());
        return retorno;
    }

    public void selecionaAidf() {
        try {
            lancamentoDoctoFiscal.setNumeroNotaFiscal(Long.valueOf(lancamentoDoctoFiscal.getAidf().getNotaFiscalInicial()));
            lancamentoDoctoFiscal.setSerie(lancamentoDoctoFiscal.getAidf().getNumeroSerie());
        } catch (Exception e) {
            logger.debug("Erro selecionando AIDF", e);
            lancamentoDoctoFiscal.setSerie(null);
        }
    }

    public boolean getConclusaoManual() {
        if (!selecionado.getSituacaoAcaoFiscal().equals(SituacaoAcaoFiscal.CANCELADO)
            && !selecionado.getSituacaoAcaoFiscal().equals(SituacaoAcaoFiscal.CONCLUIDO)) {
            for (RegistroLancamentoContabil r : selecionado.getLancamentosContabeis()) {
                if (r.getSituacao().equals(RegistroLancamentoContabil.Situacao.AGUARDANDO)
                    && r.getSituacao().equals(RegistroLancamentoContabil.Situacao.AUTO_INFRACAO)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void concluiAcao() {
        selecionado = acaoFiscalFacade.concluiAcao(selecionado);
    }

    public BigDecimal getValorMulta() {
        BigDecimal multa = BigDecimal.ZERO;
        for (LancamentoMultaFiscal lancamento : lancamentosMultasFiscais) {
            multa = multa.add(lancamento.getValorMulta());
        }
        return multa;
    }

    public BigDecimal getValorMultaUFM() {
        BigDecimal multa = BigDecimal.ZERO;
        for (LancamentoMultaFiscal lancamento : lancamentosMultasFiscais) {
            multa = multa.add(lancamento.getValorMultaIndice());
        }
        return multa;
    }

    public BigDecimal getValorAliquota() {
        for (LancamentoMultaFiscal lancamento : lancamentosMultasFiscais) {
            return (lancamento.getAliquota());
        }
        return BigDecimal.ZERO;
    }

    public void validarRegistrosRepetidos(RegistroLancamentoContabil atual) {
        ValidacaoException ve = new ValidacaoException();
        if (RegistroLancamentoContabil.Situacao.AGUARDANDO.equals(atual.getSituacao())) {
            List<LancamentoContabilFiscal> lancamentos = new ArrayList<>();
            for (RegistroLancamentoContabil reg : selecionado.getLancamentosContabeis()) {
                if (RegistroLancamentoContabil.Situacao.HOMOLOGADO.equals(reg.getSituacao())
                    || RegistroLancamentoContabil.Situacao.AUTO_INFRACAO.equals(reg.getSituacao())
                    || RegistroLancamentoContabil.Situacao.CIENCIA.equals(reg.getSituacao())
                    || RegistroLancamentoContabil.Situacao.REVELIA.equals(reg.getSituacao())) {
                    lancamentos.addAll(reg.getLancamentosContabeis());
                }
            }
            for (LancamentoContabilFiscal lcf : atual.getLancamentosContabeis()) {
                for (LancamentoContabilFiscal lcf2 : lancamentos) {
                    if (lcf.getAno().equals(lcf2.getAno())
                        && lcf.getMes().equals(lcf2.getMes())
                        && !lcf.getRegistroLancamentoContabil().equals(lcf2.getRegistroLancamentoContabil())) {

                        ve.adicionarMensagemDeOperacaoNaoPermitida("Existem dois lançamentos contábeis em registros diferentes para o mesmo mês e ano, registro "
                            + lcf.getRegistroLancamentoContabil().getNumeroAno() + " lançamento nº " + lcf.getSequencia() + " e registro "
                            + lcf.getRegistroLancamentoContabil().getNumeroAno() + " lançamento nº " + lcf2.getSequencia() + " para o mês " + lcf.getMes().getDescricao() + " do ano de " + lcf.getAno());
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void preparaImpressaoAuto(RegistroLancamentoContabil reg) {
        try {
            abreDialogImpressaoAuto(reg);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void abreDialogImpressaoAuto(RegistroLancamentoContabil reg) {
        ValidacaoException ve = new ValidacaoException();
        if (!validarAuto(reg)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Os valores do lançamento contábil não são válidos para a geração do Auto de Infração");
        } else if (!podeGerarAuto(reg)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível gerar um novo auto de infração, verifique a situação do registro ou se possui multas lançadas!");
        } else {
            if (reg.getAutoInfracaoValido() != null) {
                fundamentacaoAutoInfracao = reg.getAutoInfracaoValido().getFundamentacao();
                historicoAutoInfracao = reg.getAutoInfracaoValido().getHistoricoFiscal();
            }
            FacesUtil.executaJavaScript("imprimeAutoInfracao.show()");
        }
        ve.lancarException();
    }

    public BigDecimal calcularJurosLancamento(LancamentoContabilFiscal lancamento) {
        return acaoFiscalFacade.calcularJurosLancamento(lancamento, this.selecionado, diaVencimentoPorAno);
    }

    private Calendar getDataVencimentoAcrescimo(LancamentoContabilFiscal lancamento) {
        return acaoFiscalFacade.getDataVencimentoAcrescimo(lancamento, diaVencimentoPorAno);
    }

    private void carregaDiasPorAno(LancamentoContabilFiscal lancamento) {
        acaoFiscalFacade.carregaDiasPorAno(lancamento, diaVencimentoPorAno);
    }

    public BigDecimal calcularMultaLancamento(LancamentoContabilFiscal lancamento) {
        return acaoFiscalFacade.calcularMultaLancamento(lancamento, this.selecionado, this.diaVencimentoPorAno);
    }

    public BigDecimal calculaTotalLancamento(LancamentoContabilFiscal lancamento) {
        return acaoFiscalFacade.calcularTotalLancamento(lancamento);
    }

    public BigDecimal calcularCorrecaoLancamento(LancamentoContabilFiscal lancamento) {
        lancamento.setCorrecao(acaoFiscalFacade.calcularCorrecaoLancamento(lancamento, this.selecionado));
        return lancamento.getCorrecao();
    }

    public BigDecimal calcularCorrecaoPorArbitramento(int ano, int mes, BigDecimal valor) {
        return acaoFiscalFacade.calcularCorrecaoPorArbitramento(ano, mes, valor, this.selecionado);
    }

    private BigDecimal calcularCorrecaoPorArbitramento(Date data, BigDecimal valor) {
        if (data != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            return calcularCorrecaoPorArbitramento(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), valor);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calcularValorCorrigidoDaNotaFiscal(LancamentoDoctoFiscal doctoFiscal) {
        BigDecimal correcao = calcularCorrecaoPorArbitramento(doctoFiscal.getDataEmissao(), doctoFiscal.getValorISS());
        doctoFiscal.setValorCorrigido(doctoFiscal.getValorISS().add(correcao));
        return doctoFiscal.getValorCorrigido();
    }

    public BigDecimal calculaValorCorrigido(LancamentoContabilFiscal lancamento) {
        return acaoFiscalFacade.calcularValorCorrigido(lancamento);
    }

    public boolean getReabrir() {
        return acaoFiscalFacade.usuarioPodeReabrir(getSistemaControlador().getUsuarioCorrente());
    }

    public void imprimeDemonstrativo() {
        selecionado = acaoFiscalFacade.mergeAcaoFiscal(selecionado);
        new ImpressaoDemonstrativoAcaoFiscal().imprimeViaAcao(selecionado.getId());
    }

    public void imprimeDemonstrativo(RegistroLancamentoContabil registro) {
        Long idRegistro = registro.getId();
        selecionado = acaoFiscalFacade.mergeAcaoFiscal(selecionado);
        for (RegistroLancamentoContabil reg : selecionado.getLancamentosContabeis()) {
            if ((reg.getNumeroRegistro() != null && reg.getNumeroRegistro().equals(registro.getNumeroRegistro())) && reg.getAno().equals(registro.getAno())) {
                idRegistro = reg.getId();
                break;
            }
        }
        new ImpressaoDemonstrativoAcaoFiscal().imprimeViaRegistro(idRegistro);
    }

    public void imprimirDemonstrativoNotas() {
        try {
            validarImpressaoNotasFiscais();

            selecionado = acaoFiscalFacade.mergeAcaoFiscal(selecionado);
            ImprimeRelacaoNotas imprimeRelacaoNotas = new ImprimeRelacaoNotas();
            imprimeRelacaoNotas.condicao = " where acao.id = " + selecionado.getId();
            imprimeRelacaoNotas.filtros = " " + selecionado.getId();
            imprimeRelacaoNotas.usuario = getSistemaControlador().getUsuarioCorrente().getLogin();
            imprimeRelacaoNotas.imprime();

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarImpressaoNotasFiscais() {
        ValidacaoException ve = new ValidacaoException();
        boolean valida = true;
        for (LancamentoDoctoFiscal nota : selecionado.getLancamentoDoctoFiscal()) {
            if (nota.getId() == null) {
                valida = false;
                break;
            }
        }
        if (!valida) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Há notas fiscais não salvas, clique em salvar para imprimir o a relação de notas");
        }

        ve.lancarException();
    }

    public String actionSelecionar() {
        return "edita";
    }

    public void selecionarMulta(ActionEvent evento) {
        if (lancamentoMultaFiscal != null) {
            MultaFiscalizacao m = (MultaFiscalizacao) evento.getComponent().getAttributes().get("objeto");
            lancamentoMultaFiscal.setMultaFiscalizacao(m);
            selecionaMulta(m);
        }
        FacesUtil.executaJavaScript("dialogPesquisaMultas.hide()");
        FacesUtil.atualizarComponente("formLancamentoMulta");
    }

    public String getDescricaoArquivoUpload() {
        return descricaoArquivoUpload;
    }

    public void setDescricaoArquivoUpload(String descricaoArquivoUpload) {
        this.descricaoArquivoUpload = descricaoArquivoUpload;
    }

    public void uploadArquivo(FileUploadEvent event) {
        try {
            ArquivoAcaoFiscal arquivoAcao = new ArquivoAcaoFiscal();
            arquivoAcao.setDescricao(descricaoArquivoUpload);
            arquivoAcao.setAcaoFiscal(selecionado);
            arquivoAcao.setFile(event.getFile());

            Arquivo arq = new Arquivo();
            arq.setNome(event.getFile().getFileName());
            arq.setMimeType(acaoFiscalFacade.getArquivoFacade().getMimeType(event.getFile().getInputstream()));
            arq.setDescricao(null);
            arq.setTamanho(event.getFile().getSize());
            arq.setInputStream(event.getFile().getInputstream());

            arquivoAcao.setArquivo(acaoFiscalFacade.getArquivoFacade().novoArquivoMemoria(arq, event.getFile().getInputstream()));
            selecionado.getArquivos().add(arquivoAcao);
            descricaoArquivoUpload = "";


        } catch (Exception ex) {
            logger.error("{}", ex);
            FacesUtil.addError(
                "O Arquivo não pode ser enviado ao servido", "Verifique as propriedades do arquivo que você está tentando enviar, o mesmo não pode ser lido.");
        }
    }

    public boolean getPodeEditar() {
        if (!getEhSupervisor()) {
            UsuarioSistema u = getSistemaControlador().getUsuarioCorrente();
            for (FiscalDesignado f : selecionado.getFiscalDesignados()) {
                if (f.getUsuarioSistema().equals(u)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public boolean getEhSupervisor() {
        return acaoFiscalFacade.verificarFiscalSupervisor(getSistemaControlador().getUsuarioCorrente());
    }

    public List<SelectItem> getTiposNaoTributacao() {
        List<SelectItem> retorno = Lists.newLinkedList();
        retorno.add(new SelectItem(null, " "));
        for (LancamentoDoctoFiscal.TipoNaoTributacao tipo : LancamentoDoctoFiscal.TipoNaoTributacao.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposPessoaTomador() {
        List<SelectItem> retorno = Lists.newLinkedList();
        for (TipoPessoa tipo : TipoPessoa.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getAidfs() {
        List<SelectItem> retorno = Lists.newLinkedList();
        retorno.add(new SelectItem(null, ""));
        List<CadastroAidf> lista = acaoFiscalFacade.getAifFacade().listaPorCadastroEconomico(selecionado.getCadastroEconomico());
        for (CadastroAidf aidf : lista) {
            retorno.add(new SelectItem(aidf, aidf.getNumeroAidf()));
        }
        return retorno;
    }

    public List<SelectItem> getNumerosDeSerie() {
        List<SelectItem> retorno = Lists.newLinkedList();
        retorno.add(new SelectItem(null, " "));
        for (NumeroSerie numero : acaoFiscalFacade.getNumeroSerieFacade().lista()) {
            retorno.add(new SelectItem(numero, numero.getDescricao()));
        }
        return retorno;
    }

//    public void cancelaNota() {
//        if (lancamentoDoctoFiscal.getDataEmissao() == null) {
//            if (selecionado.getLancamentoDoctoFiscal().isEmpty()) {
//                Calendar c = Calendar.getInstance();
//                lancamentoDoctoFiscal.setDataEmissao(selecionado.getDataLevantamentoInicial());
//                FacesUtil.addInfo("Atenção", "A Data da nota fiscal foi automaticamente preenchida com a inicial do levantamento contábil, a data não pode ser nula pois é necessária no lançamento contábil para a categorização de mês e ano.");
//            } else {
//                lancamentoDoctoFiscal.setDataEmissao(selecionado.getLancamentoDoctoFiscal().get(selecionado.getLancamentoDoctoFiscal().size() - 1).getDataEmissao());
//                FacesUtil.addInfo("Atenção", "A Data da nota fiscal foi automaticamente preenchida com a data da ultima nota lançada, a data não pode ser nula pois é necessária no lançamento contábil para a categorização de mês e ano.");
//            }
//
//        }
//    }

    public void removeAnexo(ArquivoAcaoFiscal arquivo) {
        selecionado.getArquivos().remove(arquivo);
    }

    public StreamedContent download(ArquivoAcaoFiscal arquivo) throws IOException {
        UploadedFile download = (UploadedFile) arquivo.getFile();
        return new DefaultStreamedContent(download.getInputstream(), download.getContentType(), download.getFileName());
    }

    public void verAutoInfracao(AutoInfracaoFiscal auto) {
        Web.navegacao(getCaminhoPadrao() + "editar/" + getUrlKeyValue() + "/", "/auto-de-infracao-fiscal/ver/" + auto.getId() + "/");
    }

    public void valorDoPercentualISS() {
        if (lancamentoDoctoFiscal.getPorcentagemISS().compareTo(new BigDecimal(2)) < 0) {
            lancamentoDoctoFiscal.setPorcentagemISS(new BigDecimal(2));
            FacesUtil.addWarn("Atenção!", "A aliquota do ISS não pode ser menor que 2%!");
        }

        if (lancamentoDoctoFiscal.getPorcentagemISS().compareTo(new BigDecimal(5)) > 0) {
            lancamentoDoctoFiscal.setPorcentagemISS(new BigDecimal(5));
            FacesUtil.addWarn("Atenção!", "A aliquota do ISS não pode ser maior que 5%!");
        }
    }

    public boolean validarAuto(RegistroLancamentoContabil reg) {
        BigDecimal valor = BigDecimal.ZERO;
        for (LancamentoContabilFiscal lancamento : reg.getLancamentosContabeis()) {
            valor = valor.add(lancamento.getIssDevido());
            valor = valor.add(lancamento.getMulta());
            valor = valor.add(lancamento.getJuros());
            valor = valor.add(lancamento.getCorrecao());
        }
        return valor.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean mostraConclusao() {
        return !"".equals(selecionado.getConclusao().trim());
    }

    public boolean temNotaExtraviadaSemMultaLancada() {
        boolean temNotaExtraviada = false;
        for (LancamentoDoctoFiscal nf : selecionado.getLancamentoDoctoFiscal()) {
            if (nf.getNotaExtraviada()) {
                temNotaExtraviada = true;
                break;
            }
        }

        if (temNotaExtraviada) {
            if (!selecionado.getLancamentosContabeis().isEmpty()) {
                for (RegistroLancamentoContabil reg : selecionado.getLancamentosContabeis()) {
                    if (reg.getAutosInfracao() != null) {
                        return true;
                    }
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public void iniciarAlteracaoDataArbitramento() {
        alteracaoDataArbitramento = new AlteracaoDataArbitramento();
        alteracaoDataArbitramento.setUsuarioSistema(acaoFiscalFacade.getSistemaFacade().getUsuarioCorrente());
        alteracaoDataArbitramento.setDataArbitramentoAnterior(selecionado.getDataArbitramento());
    }

    public void validarAlteracaoDataArbitramento(BigDecimal ufmPorExercicio) {
        if (alteracaoDataArbitramento.getDataArbitramento() == null) {
            throw new ValidacaoException("O campo Data de Arbitramento deve ser informado.");
        }
        if (ufmPorExercicio == null || ufmPorExercicio.compareTo(BigDecimal.ZERO) == 0) {
            throw new ValidacaoException("UFM não encontrada para o exercício de " + ano + ".");
        }
    }

    public void alterarDataArbitramento() {
        try {
            Integer ano = alteracaoDataArbitramento.getDataArbitramento() != null ?
                DateUtils.getAno(alteracaoDataArbitramento.getDataArbitramento()) : null;
            BigDecimal ufmPorExercicio = ano != null ?
                acaoFiscalFacade.getMoedaFacade().recuperaValorUFMPorExercicio(ano) : null;
            validarAlteracaoDataArbitramento(ufmPorExercicio);
            alteracaoDataArbitramento.setAcaoFiscal(selecionado);
            selecionado.getAlteracoesDataArbitramento().add(alteracaoDataArbitramento);
            atribuirDataArbitramento(alteracaoDataArbitramento.getDataArbitramento());
            alteracaoDataArbitramento = null;
            recalcularMultas();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private void recalcularMultas() {
        lancamentosMultasFiscais.clear();
        for (LancamentoMultaFiscal multaFiscal : getRegistroAtivo().getMultas()) {
            lancamentoMultaFiscal = multaFiscal;
            lancarMulta();
        }
        getRegistroAtivo().getMultas().clear();
        getRegistroAtivo().getMultas().addAll(lancamentosMultasFiscais);
    }

    public void adicionarLancamentoParaMulta(LancamentoContabilFiscal lancamento) {
        if (lancamentoParaMulta == null) {
            lancamentoParaMulta = Lists.newArrayList();
        }
        lancamentoParaMulta.add(lancamento);
    }

    public void removerLancamentoParaMulta(LancamentoContabilFiscal lancamento) {
        lancamentoParaMulta.remove(lancamento);
    }

    public boolean containsLancamentoParaMulta(LancamentoContabilFiscal lancamento) {
        return lancamentoParaMulta.contains(lancamento);
    }

    public void adicionarTodosLancamentoParaMulta(RegistroLancamentoContabil registro, Integer ano) {
        for (LancamentoContabilFiscal lancamento : registro.getLancamentosPorAno().get(ano)) {
            if (!lancamentoParaMulta.contains(lancamento)) {
                lancamentoParaMulta.add(lancamento);
            }
        }
    }

    public void removerTodosLancamentoParaMulta(RegistroLancamentoContabil registro, Integer ano) {
        for (LancamentoContabilFiscal lancamento : registro.getLancamentosPorAno().get(ano)) {
            lancamentoParaMulta.remove(lancamento);
        }
    }

    public boolean containsTodosLancamentoParaMulta(RegistroLancamentoContabil registro, Integer ano) {
        boolean todos = true;
        if (registro != null && !registro.getLancamentosPorAno().isEmpty()) {
            for (LancamentoContabilFiscal lancamento : registro.getLancamentosPorAno().get(ano)) {
                if (!lancamentoParaMulta.contains(lancamento)) {
                    todos = false;
                    break;
                }
            }
        } else {
            todos = false;
        }
        return todos;
    }

    public boolean containsTodasNotasParaMulta() {
        boolean todos = true;
        for (LancamentoDoctoFiscal l : notasFiscaisDoLancamento) {
            if (!notasParaMulta.contains(l)) {
                todos = false;
                break;
            }
        }
        return todos;
    }

    public void removerTodasNotasParaMulta() {
        notasParaMulta.clear();
    }

    public void adicionarTodasNotasParaMulta() {
        for (LancamentoDoctoFiscal l : notasFiscaisDoLancamento) {
            if (!notasParaMulta.contains(l)) {
                notasParaMulta.add(l);
            }
        }
    }

    public void adicionarNotasParaMulta(LancamentoDoctoFiscal lancamento) {
        if (notasParaMulta == null) {
            notasParaMulta = Lists.newArrayList();
        }
        notasParaMulta.add(lancamento);
    }

    public void removerNotasParaMulta(LancamentoDoctoFiscal lancamento) {
        notasParaMulta.remove(lancamento);
    }

    public boolean containsNotasParaMulta(LancamentoDoctoFiscal lancamento) {
        return notasParaMulta.contains(lancamento);
    }

    // para funcionar a pesquisa generica de multas //
    public String getCaminho() {
        return "";
    }

    public void setCaminho(String caminho) {
    }
// para funcionar a pesquisa generica de multas //

    public void atualizaDialogPesquisaMulta() {
        FacesUtil.atualizarComponente("Formulario_dialog_pesquisa_multa");
    }

    public void imprimeListagemNostasFiscais() {
        try {
            acaoFiscalFacade.imprimeListagemNostasFiscais(selecionado);
        } catch (JRException | IOException e) {
            logger.debug("Erro emitindo Listagem de Notas Fiscais", e);
            FacesUtil.addError("Erro", "Não foi possível emitir o relatório!");
        }
    }

    public String labelCpfCnpjTomador() {
        if (lancamentoDoctoFiscal != null) {
            return TipoPessoa.FISICA.equals(lancamentoDoctoFiscal.getTipoPessoaTomador()) ? "CPF do Tomador" : "CNPJ do Tomador";
        }
        return "CPF do Tomador";
    }

    public String mascaraCpfCnpjTomador() {
        if (lancamentoDoctoFiscal != null) {
            return TipoPessoa.FISICA.equals(lancamentoDoctoFiscal.getTipoPessoaTomador()) ? "999.999.999-99" : "99.999.999/9999-99";
        }
        return "999.999.999-99";
    }

    public void redirecionaFiscaisDesignados() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + getUrlKeyValue());
    }

    public void redirecionaHistoricoSituacoes() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar-historico-situacoes/" + getUrlKeyValue());
    }

    public void redirecionaLevantamentoContabil() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar-levantamento-contabil/" + getUrlKeyValue());
    }

    public void redirecionaNotasFiscais() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar-notas-fiscais/" + getUrlKeyValue());
    }

    public void redirecionaLancamentoContabil() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar-lancamento-contabil/" + getUrlKeyValue());
    }

    public void redirecionaMultas() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar-multas/" + getUrlKeyValue());
    }

    public void redirecionaAnexos() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar-anexos/" + getUrlKeyValue());
    }

    public void abrirDialogDeLancamentoDeMultasDasNotasFiscais() {
        if (getRegistroAtivo() != null && !selecionado.getLancamentosContabeis().isEmpty()) {
            multasALancar = Lists.newArrayList();
            if (!notasParaMulta.isEmpty()) {
                lancamentoMultaFiscal.setRegistroLancamentoContabil(getRegistroAtivo());
                FacesUtil.executaJavaScript("lancamentoMultaNotas.show()");
            } else {
                FacesUtil.addOperacaoNaoPermitida("Selecione pelo menos uma Multa!");
            }
        } else {
            FacesUtil.addOperacaoNaoPermitida("Não é possível lançar multas para as Notas Fiscais, pois não existe registro de Lançamento Contábil criado!");
        }
    }

    public String montarReferenciaMulta(LancamentoMultaFiscal lancamentoMultaFiscal) {
        return acaoFiscalFacade.montarReferenciaMulta(lancamentoMultaFiscal);
    }

    public void verNotasFiscaisDoLancamento(LancamentoContabilFiscal lancamento) {
        notasFiscaisDoLancamento = Lists.newArrayList();
        if (lancamento.getValorApurado().compareTo(BigDecimal.ZERO) > 0) {
            notasFiscaisDoLancamento = acaoFiscalFacade.buscarNotasFiscaisDoLocamento(lancamento);
            for (LancamentoDoctoFiscal doctoFiscal : notasFiscaisDoLancamento) {
                doctoFiscal.setValorCorrigido(calcularValorCorrigidoDaNotaFiscal(doctoFiscal));
            }
        }
    }

    public class FiltroAcaoFiscal {

        private String numeroDaAcao, numeroProgramacao, NumeroOrde, cmc, razaoSocial, cnpj, situacao, periodo;

        public FiltroAcaoFiscal() {
        }

        public String getNumeroDaAcao() {
            return numeroDaAcao;
        }

        public void setNumeroDaAcao(String numeroDaAcao) {
            this.numeroDaAcao = numeroDaAcao;
        }

        public String getNumeroProgramacao() {
            return numeroProgramacao;
        }

        public void setNumeroProgramacao(String numeroProgramacao) {
            this.numeroProgramacao = numeroProgramacao;
        }

        public String getNumeroOrde() {
            return NumeroOrde;
        }

        public void setNumeroOrde(String NumeroOrde) {
            this.NumeroOrde = NumeroOrde;
        }

        public String getCmc() {
            return cmc;
        }

        public void setCmc(String cmc) {
            this.cmc = cmc;
        }

        public String getRazaoSocial() {
            return razaoSocial;
        }

        public void setRazaoSocial(String razaoSocial) {
            this.razaoSocial = razaoSocial;
        }

        public String getCnpj() {
            return cnpj;
        }

        public void setCnpj(String cnpj) {
            this.cnpj = cnpj;
        }

        public String getSituacao() {
            return situacao;
        }

        public void setSituacao(String situacao) {
            this.situacao = situacao;
        }

        public String getPeriodo() {
            return periodo;
        }

        public void setPeriodo(String periodo) {
            this.periodo = periodo;
        }
    }

    public class ValoresIssPago {
        private Integer mes;
        private Integer ano;
        private BigDecimal aliquota;

        public ValoresIssPago(LancamentoContabilFiscal lancamento) {
            this.mes = lancamento.getMes().getNumeroMes();
            this.ano = lancamento.getAno();
            this.aliquota = lancamento.getAliquotaISS();
        }

        public ValoresIssPago(LancamentoDoctoFiscal lancamentoDoctoFiscal) {
            this.mes = lancamentoDoctoFiscal.getMes();
            this.ano = lancamentoDoctoFiscal.getAno();
            this.aliquota = lancamentoDoctoFiscal.getPorcentagemISS();
        }

        public Integer getMes() {
            return mes;
        }

        public void setMes(Integer mes) {
            this.mes = mes;
        }

        public Integer getAno() {
            return ano;
        }

        public void setAno(Integer ano) {
            this.ano = ano;
        }

        public BigDecimal getAliquota() {
            return aliquota;
        }

        public void setAliquota(BigDecimal aliquota) {
            this.aliquota = aliquota;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ValoresIssPago that = (ValoresIssPago) o;

            if (aliquota != null ? !aliquota.equals(that.aliquota) : that.aliquota != null) return false;
            if (ano != null ? !ano.equals(that.ano) : that.ano != null) return false;
            if (mes != null ? !mes.equals(that.mes) : that.mes != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = mes != null ? mes.hashCode() : 0;
            result = 31 * result + (ano != null ? ano.hashCode() : 0);
            result = 31 * result + (aliquota != null ? aliquota.hashCode() : 0);
            return result;
        }
    }

    public List<SelectItem> getTiposDocumentosAnexos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (ArquivoAcaoFiscal.TipoDocumentoAnexo tipo : ArquivoAcaoFiscal.TipoDocumentoAnexo.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
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

    public ArquivoAcaoFiscal getAnexoSelecionado() {
        return anexoSelecionado;
    }

    public void setAnexoSelecionado(ArquivoAcaoFiscal anexoSelecionado) {
        this.anexoSelecionado = anexoSelecionado;
    }

    public DoctoAcaoFiscal getDocumentoSelecionado() {
        return documentoSelecionado;
    }

    public void setDocumentoSelecionado(DoctoAcaoFiscal documentoSelecionado) {
        this.documentoSelecionado = documentoSelecionado;
    }

    public Boolean getEmailAnexo() {
        return emailAnexo;
    }

    public void setEmailAnexo(Boolean emailAnexo) {
        this.emailAnexo = emailAnexo;
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

    public void enviarEmailAcaoFiscal() {
        try {
            String[] emailsSeparados = validarAndSepararEmails();
            enviarEmail(emailsSeparados);
        } catch (AddressException e) {
            FacesUtil.addOperacaoNaoRealizada("O e-mail informado é invalido!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void enviarEmail(String[] emailsSeparados) {
        try {
            validarEnvioEmail();
            ConfiguracaoTributario configuracaoTributario = acaoFiscalFacade.getConfiguracaoTributarioFacade().retornaUltimo();
            String assunto = "Prefeitura Municipal de " + configuracaoTributario.getCidade().getNome() + " / " + configuracaoTributario.getCidade().getUf() + " - Documento Ação Fiscal";
            enviarEmailAnexoAcaoFiscal(emailsSeparados, assunto);
            acaoFiscalFacade.getDocumentoOficialFacade()
                .enviarEmailDocumentoOficial(emailsSeparados, documentoSelecionado.getDocumentoOficial(), assunto, mensagemEmail);
            salvarComunicacaoAcaoFiscal();
            FacesUtil.addOperacaoRealizada("E-mail enviado com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível enviar o e-mail");
            logger.error("Não foi possível enviar o e-mail: " + e);
        }
    }

    private void validarEnvioEmail() {
        ValidacaoException ve = new ValidacaoException();
        if (anexoSelecionado == null && documentoSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor selecione ao menos um anexo para o e-mail.");
        }
        ve.lancarException();
    }

    private void salvarComunicacaoAcaoFiscal() {
        ComunicacaoAcaoFiscal comunicacao = new ComunicacaoAcaoFiscal();
        comunicacao.setAcaoFiscal(selecionado);
        comunicacao.setDataEnvioEmail(new Date());
        comunicacao.setEmailsEnvio(emails);
        comunicacao.setNomeArquivo(anexoSelecionado != null ? anexoSelecionado.getArquivo().getNome() : documentoSelecionado.getTipoDoctoAcaoFiscal().getDescricao());
        comunicacao.setTextoEmail(mensagemEmail);
        comunicacao.setUsuario(acaoFiscalFacade.getSistemaFacade().getUsuarioCorrente());
        acaoFiscalFacade.salvarComunicaoAcaoFiscal(comunicacao);
        limparSelecionados();
        mensagemEmail = null;
        emails = null;
    }

    private void enviarEmailAnexoAcaoFiscal(String[] emailsSeparados, String assunto) throws MessagingException {
        ByteArrayOutputStream outputStream;
        if (anexoSelecionado != null) {
            Arquivo arquivoRecuperado = acaoFiscalFacade.getArquivoFacade().recuperaDependencias(anexoSelecionado.getArquivo().getId());
            byte[] byteArrayDosDados = arquivoRecuperado.getByteArrayDosDados();
            outputStream = new ByteArrayOutputStream(byteArrayDosDados.length);
            outputStream.write(byteArrayDosDados, 0, byteArrayDosDados.length);
            String mimeType = acaoFiscalFacade.getArquivoFacade().getMimeType(new ByteArrayInputStream(arquivoRecuperado.getByteArrayDosDados()));

            AnexoEmailDTO anexoDto = new AnexoEmailDTO(outputStream, "anexo", mimeType, acaoFiscalFacade.getArquivoFacade().getExtension(mimeType));

            EmailService.getInstance()
                .enviarEmail(mensagemEmail, assunto, mensagemEmail, anexoDto);
        }
    }

    public void selecionarAnexo(ArquivoAcaoFiscal anexo) {
        anexo.setSelecionado(true);
        anexoSelecionado = anexo;
    }

    public void removerAnexoSelecionado(ArquivoAcaoFiscal anexo) {
        anexo.setSelecionado(false);
        anexoSelecionado = null;
    }

    public void selecionarDocumento(DoctoAcaoFiscal doc) {
        documentoSelecionado = doc;
    }

    public void removerDocumentoSelecionado() {
        documentoSelecionado = null;
    }

    public void limparSelecionados() {
        for (ArquivoAcaoFiscal arquivoAcaoFiscal : selecionado.getArquivos()) {
            arquivoAcaoFiscal.setSelecionado(false);
        }
        documentoSelecionado = null;
        anexoSelecionado = null;
    }

    public ProgramacaoFiscal getProgramacaoRecuperada() {
        return programacaoRecuperada;
    }

    public List<ComunicacaoAcaoFiscal> getComunicacoesAcaoFiscal() {
        return acaoFiscalFacade.buscarComunicacaoAcaoFiscalPorAcaoFiscal(selecionado);
    }

    public List<ArquivoAcaoFiscal> getArquivosAnexosFiscais() {
        List<ArquivoAcaoFiscal> toReturn = new ArrayList<>();
        for (ArquivoAcaoFiscal arquivo : selecionado.getArquivos()) {
            if (arquivo.getId() != null) {
                toReturn.add(arquivo);
            }
        }
        return toReturn;
    }

    public List<DocumentoFiscalEmail> getDocumentosAcaoFiscal() {
        List<DocumentoFiscalEmail> documentos = new ArrayList<>();
        a:
        for (DoctoAcaoFiscal doc : selecionado.getDoctosAcaoFiscal()) {
            b:
            for (DocumentoFiscalEmail documento : documentos) {
                if (documento.getIdentificacao().equals(doc.getIdentificacao()) && doc.getTipoDoctoAcaoFiscal().equals(documento.getTipoDocumento())) {
                    continue a;
                }
            }
            if (doc.getDocumentoOficial() != null && doc.getDocumentoOficial().getConteudo() != null) {
                TipoDoctoOficial tipo = recuperarTipoDoctoFiscal(doc);
                ModeloDoctoOficial mod = acaoFiscalFacade.getTipoDoctoOficialFacade().recuperaModeloVigente(tipo);
                if (!TipoDoctoAcaoFiscal.AUTOINFRACAO.equals(doc.getTipoDoctoAcaoFiscal()) && mod != null) {
                    documentos.add(doc);
                }
            }
        }
        for (RegistroLancamentoContabil registro : selecionado.getLancamentosContabeisAtivos()) {
            AutoInfracaoFiscal autoInfracaoValido = registro.getAutoInfracaoValido();
            if (autoInfracaoValido != null) {
                documentos.add(autoInfracaoValido);
            }
        }
        return documentos;
    }

    private TipoDoctoOficial recuperarTipoDoctoFiscal(DoctoAcaoFiscal doc) {
        TipoDoctoOficial tipo = null;
        if (TipoDoctoAcaoFiscal.RELATORIO_FISCAL.equals(doc.getTipoDoctoAcaoFiscal())) {
            tipo = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarTipoDoctoRelatorioFiscalPorExercicio(getSistemaControlador().getExercicioCorrente());
        } else if (TipoDoctoAcaoFiscal.FINALIZACAO.equals(doc.getTipoDoctoAcaoFiscal())) {
            tipo = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarTipoDoctoTermoFinalizacaoPorExercicio(getSistemaControlador().getExercicioCorrente());
        } else if (TipoDoctoAcaoFiscal.ORDEMSERVICO.equals(doc.getTipoDoctoAcaoFiscal())) {
            tipo = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarTipoDoctoOrdemPorExercicio(getSistemaControlador().getExercicioCorrente());
        } else if (TipoDoctoAcaoFiscal.HOMOLOGACAO.equals(doc.getTipoDoctoAcaoFiscal())) {
            tipo = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarTipoDoctoHomologacaoPorExercicio(getSistemaControlador().getExercicioCorrente());
        } else if (TipoDoctoAcaoFiscal.FISCALIZACAO.equals(doc.getTipoDoctoAcaoFiscal())) {
            tipo = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarTipoDoctoTermoFiscalizacaoPorExercicio(getSistemaControlador().getExercicioCorrente());
        } else if (TipoDoctoAcaoFiscal.AUTOINFRACAO.equals(doc.getTipoDoctoAcaoFiscal())) {
            tipo = acaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarTipoDoctoAutoInfracaoPorExercicio(getSistemaControlador().getExercicioCorrente());
        }
        return tipo;
    }

    public boolean habilitarEdicaoIssLancado(Integer ano, Integer mes) {
        if (parametroFiscalizacao != null && parametroFiscalizacao.getCompetencia() != null) {
            return parametroFiscalizacao.getCompetencia().before(DataUtil.criarDataComMesEAno(mes, ano).toDate());
        }
        return true;
    }

    public boolean canEmitirTermoFinalizacaoFiscalizacao() {
        return existeAutoInfracaoEntregue()
            && !selecionado.getSituacaoAcaoFiscal().equals(SituacaoAcaoFiscal.CANCELADO);
    }

    public void onTabChange(TabChangeEvent event) {
        String idTab = event.getTab().getId();
        if ("tabNotasFiscais".equals(idTab)) {
            consultarNotasFiscais();
            FacesUtil.atualizarComponente("Formulario:tabView:panel-notas");
        }
    }

    public LazyDataModel<NotaFiscal> getNotasFiscaisModel() {
        return notasFiscaisModel;
    }

    public void setNotasFiscaisModel(LazyDataModel<NotaFiscal> notasFiscaisModel) {
        this.notasFiscaisModel = notasFiscaisModel;
    }

    public List<FiltroConsultaEntidade> getFiltrosConsultaNsfe() {
        return filtrosConsultaNsfe;
    }

    public List<FieldConsultaEntidade> getFieldsConsultaNsfe() {
        return fieldsConsultaNsfe;
    }

    public List<SelectItem> getPesquisaveisNotaFiscal() {
        return Util.getListSelectItem(fieldsConsultaNsfe, false);
    }

    public ConsultaEntidadeController.ConverterFieldConsultaEntidade getConverterFieldConsulta() {
        return converterFieldConsulta;
    }

    public void buildFieldsNotaFiscal() {
        fieldsConsultaNsfe = Lists.newArrayList();
        filtrosConsultaNsfe = Lists.newArrayList();
        fieldsConsultaNsfe.add(new FieldConsultaEntidade("Número", "nf.numero", TipoCampo.INTEGER));
        fieldsConsultaNsfe.add(new FieldConsultaEntidade("Emissão", "nf.emissao", TipoCampo.DATE));
        fieldsConsultaNsfe.add(new FieldConsultaEntidade("Codigo de Verificação ", "nf.codigoVerificacao", TipoCampo.STRING));
        fieldsConsultaNsfe.add(new FieldConsultaEntidade("Nome do Tomador", "dptnf.nomeRazaoSocial", TipoCampo.STRING));
        fieldsConsultaNsfe.add(new FieldConsultaEntidade("CPF/CNPJ do Tomador", "dptnf.cpfCnpj", TipoCampo.STRING));
        fieldsConsultaNsfe.add(new FieldConsultaEntidade("Situação", "dps.situacao","br.com.webpublico.nfse.enums.SituacaoNota", TipoCampo.ENUM, TipoAlinhamento.CENTRO));
        addFiltroNotaFiscal();
        converterFieldConsulta = new ConsultaEntidadeController.ConverterFieldConsultaEntidade(fieldsConsultaNsfe);
    }

    public void addFiltroNotaFiscal() {
        filtrosConsultaNsfe.add(new FiltroConsultaEntidade());
    }

    public void removeFiltroNotaFiscal(int index) {
        filtrosConsultaNsfe.remove(index);
        if (filtrosConsultaNsfe.isEmpty()) {
            addFiltroNotaFiscal();
        }
    }

    public void consultarNotasFiscais() {
        try {
            StringBuilder complemento = new StringBuilder(" where nf.prestador_id = " + selecionado.getCadastroEconomico().getId()
                + " and to_date(to_char(nf.emissao, 'dd/MM/yyyy')) between " + convertDateToStringToDateSQL(selecionado.getDataLevantamentoInicial())
                + " and " + convertDateToStringToDateSQL(selecionado.getDataLevantamentoFinal()));

            notasFiscaisModel = new LazyDataModel<NotaFiscal>() {
                @Override
                public List<NotaFiscal> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                    setRowCount(notaFiscalFacade.contarNotasFiscais(complemento.toString(), filtrosConsultaNsfe));
                    return notaFiscalFacade.buscarNotasFiscais(complemento.toString(), first, pageSize, filtrosConsultaNsfe);
                }
            };

            totalizadorNotasFiscais = notaFiscalFacade.buscarValoresTotalizador(complemento.toString(), filtrosConsultaNsfe);
        } catch (Exception e) {
            Util.loggingError(this.getClass(), "Erro ao buscar notas fiscais da ação fiscal " + selecionado.getId(), e);
        }
    }


    private String convertDateToStringToDateSQL(Date date) {
        return "to_date('" + DataUtil.getDataFormatada(date) + "', 'dd/MM/yyyy')";
    }



    public void excluirLancamentosContabil(RegistroLancamentoContabil registro) {
        if (lancamentoParaMulta == null || lancamentoParaMulta.isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Nenhum lançamento foi selecionado para ser excluído.");
        } else {
            for (LancamentoContabilFiscal lancamentoContabilFiscal : lancamentoParaMulta) {
                if (registro.equals(lancamentoContabilFiscal.getRegistroLancamentoContabil())) {
                    excluirLancamentoContabil(lancamentoContabilFiscal, registro);
                }
            }
        }
    }

    public void selecionarTodasMultas(RegistroLancamentoContabil registro) {
        for (LancamentoMultaFiscal lancamento : registro.getMultas()) {
            if (!multasSelecionadas.contains(lancamento)) {
                multasSelecionadas.add(lancamento);
            }
        }
    }

    public void naoSelecionarTodasMultas(RegistroLancamentoContabil registro) {
        for (LancamentoMultaFiscal lancamento : registro.getMultas()) {
            multasSelecionadas.remove(lancamento);
        }
    }

    public boolean todasMultasSelecionadas(RegistroLancamentoContabil registro) {
        if (registro.getMultas() != null && !registro.getMultas().isEmpty()) {
            return registro.getMultas().stream().noneMatch(m -> !multasSelecionadas.contains(m));
        }
        return false;
    }

    public boolean multaSelecionada(LancamentoMultaFiscal lancamento) {
        return multasSelecionadas.contains(lancamento);
    }

    public void selecionarMulta(LancamentoMultaFiscal lancamento) {
        multasSelecionadas.add(lancamento);
    }

    public void naoSelecionarMulta(LancamentoMultaFiscal lancamento) {
        multasSelecionadas.remove(lancamento);
    }

    public void excluirMultas(RegistroLancamentoContabil registro) {
        if (multasSelecionadas == null || multasSelecionadas.isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Nenhuma multa foi selecionada para ser excluída.");
        } else {
            for (LancamentoMultaFiscal multaFiscal : multasSelecionadas) {
                if (registro.equals(multaFiscal.getRegistroLancamentoContabil())) {
                    excluirMultaLancada(multaFiscal, registro);
                }
            }
        }
    }


    public void aplicarFieldsConsultaEntidade(FiltroNotaFiscal filtroNotaFiscal) {
        for (FiltroConsultaEntidade filtroConsultaEntidade : filtrosConsultaNsfe) {
            if (filtroConsultaEntidade.getValor() == null) continue;
            Object valor = filtroConsultaEntidade.getValor();
            switch (filtroConsultaEntidade.getField().getValor().trim().toLowerCase()) {
                case "nf.numero": {
                    filtroNotaFiscal.setNumero((String) valor);
                    break;
                }
                case "nf.emissao": {
                    filtroNotaFiscal.setDataInicial((Date) valor);
                    filtroNotaFiscal.setDataFinal((Date) valor);
                    break;
                }
                case "nf.codigoverificacao": {
                    filtroNotaFiscal.setCodigoVerificacao((String) valor);
                    break;
                }
                case "dptnf.nomerazaosocial": {
                    filtroNotaFiscal.setNomeTomador((String) valor);
                    break;
                }
                case "dptnf.cpfcnpj": {
                    filtroNotaFiscal.setCpfCnpjTomadorInicial((String) valor);
                    filtroNotaFiscal.setCpfCnpjTomadorFinal((String) valor);
                    break;
                }
                case "dps.situacao": {
                    filtroNotaFiscal.setSituacoes(Lists.newArrayList(SituacaoNota.valueOf((String) valor)));
                }
            }
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            FiltroNotaFiscal filtro = new FiltroNotaFiscal();
            filtro.setCadastroEconomico(selecionado.getCadastroEconomico());
            filtro.setDataInicial(selecionado.getDataLevantamentoInicial());
            filtro.setDataFinal(selecionado.getDataLevantamentoFinal());
            aplicarFieldsConsultaEntidade(filtro);
            RelatorioDTO dto = RelatorioNotasFiscaisControlador.criarRelatorioDTO(tipoRelatorioExtensao,
                acaoFiscalFacade.getSistemaFacade().getUsuarioCorrente().getNome(), filtro);
            ReportService.getInstance().gerarRelatorio(acaoFiscalFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException op) {
            FacesUtil.printAllFacesMessages(op);
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatorio de notas fiscais na ação fiscal. Erro {}", ex.getMessage());
            logger.debug("Detalhes do erro ao gerar o relatorio de notas fiscais na ação fiscal.", ex);
            FacesUtil.addErrorPadrao(ex);
        }
    }
}
