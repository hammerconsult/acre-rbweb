/*
 * Codigo gerado automaticamente em Fri Aug 05 11:11:20 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.BarCode;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.administracaodepagamento.FichaFinanceiraFPSimulacao;
import br.com.webpublico.entidades.rh.cadastrofuncional.AssentamentoFuncional;
import br.com.webpublico.entidades.rh.cadastrofuncional.AvisoPrevio;
import br.com.webpublico.entidades.rh.concursos.CargoConcurso;
import br.com.webpublico.entidades.rh.concursos.ClassificacaoInscricao;
import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.entidades.rh.creditodesalario.CreditoSalario;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.VinculoFPEventoEsocial;
import br.com.webpublico.entidades.rh.portal.SolicitacaoAfastamentoPortal;
import br.com.webpublico.entidades.usertype.ModalidadeContratoFPData;
import br.com.webpublico.entidadesauxiliares.rh.esocial.VWContratoFP;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
import br.com.webpublico.enums.rh.TipoPeq;
import br.com.webpublico.enums.rh.TipoVinculoSicap;
import br.com.webpublico.enums.rh.esocial.HipoteseLegalContratacao;
import br.com.webpublico.enums.rh.esocial.ModalidadeContratacaoAprendiz;
import br.com.webpublico.enums.rh.esocial.SegregacaoMassa;
import br.com.webpublico.enums.rh.esocial.SituacaoQualificacaoCadastral;
import br.com.webpublico.enums.rh.previdencia.SituacaoAdmissaoBBPrev;
import br.com.webpublico.enums.rh.siope.SegmentoAtuacao;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.dto.OcorrenciaESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.administracaodepagamento.FichaFinanceiraFPSimulacaoFacade;
import br.com.webpublico.negocios.rh.cadastrofuncional.AssentamentoFuncionalFacade;
import br.com.webpublico.negocios.rh.cadastrofuncional.AvisoPrevioFacade;
import br.com.webpublico.negocios.rh.concursos.CargoConcursoFacade;
import br.com.webpublico.negocios.rh.concursos.ClassificacaoConcursoFacade;
import br.com.webpublico.negocios.rh.concursos.ConcursoFacade;
import br.com.webpublico.negocios.rh.portal.SolicitacaoAfastamentoPortalFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.validation.ConstraintViolationException;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean(name = "contratoFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoContratoFP", pattern = "/contratofp/novo/", viewId = "/faces/rh/administracaodepagamento/contratofp/edita.xhtml"),
    @URLMapping(id = "editarContratoFP", pattern = "/contratofp/editar/#{contratoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/contratofp/edita.xhtml"),
    @URLMapping(id = "editarContratoFPPorMatriculaEnumero", pattern = "/contratofp/editar/#{contratoFPControlador.numeroMatricula}/#{contratoFPControlador.numeroContrato}/", viewId = "/faces/rh/administracaodepagamento/contratofp/edita.xhtml"),
    @URLMapping(id = "listarContratoFP", pattern = "/contratofp/listar/", viewId = "/faces/rh/administracaodepagamento/contratofp/lista.xhtml"),
    @URLMapping(id = "consultarContratoFP", pattern = "/contratofp/consultar/", viewId = "/faces/rh/administracaodepagamento/contratofp/consultar.xhtml"),
    @URLMapping(id = "pesquisarContratoFP", pattern = "/contratofp/pesquisar/", viewId = "/faces/rh/administracaodepagamento/contratofp/pesquisar.xhtml"),
    @URLMapping(id = "novoContratoFPCandidatoConcurso", pattern = "/contratofp/novo/candidato-concurso/#{contratoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/contratofp/edita.xhtml"),
    @URLMapping(id = "verContratoFP", pattern = "/contratofp/ver/#{contratoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/contratofp/visualizar.xhtml"),
    @URLMapping(id = "verContratoFPPorMatriculaEnumero", pattern = "/contratofp/ver/#{contratoFPControlador.numeroMatricula}/#{contratoFPControlador.numeroContrato}/", viewId = "/faces/rh/administracaodepagamento/contratofp/visualizar.xhtml")
})
public class ContratoFPControlador extends PrettyControlador<ContratoFP> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ContratoFPControlador.class);
    public static final int TEMPORARIO = 106;
    public static final int APRENDIZ = 103;

    private Long numeroMatricula;
    private Long numeroContrato;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ConcursoFacade concursoFacade;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    @EJB
    private TipoPrevidenciaFPFacade tipoPrevidenciaFPFacade;
    private ConverterAutoComplete converterMatriculaFP;
    @EJB
    private CargoFacade cargoFacade;
    private ConverterGenerico converterTipoPrevidencia;
    @EJB
    private JornadaDeTrabalhoFacade jornadaDeTrabalhoFacade;
    private ConverterGenerico converterJornadaDeTrabalho;
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoServidorFacade;
    private ConverterGenerico converterModalidadeContratoServidor;
    @EJB
    private HorarioContratoFPFacade horarioContratoFPFacade;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;
    private ConverterGenerico converterHorarioContratoFP;
    private LotacaoFuncional lotacaoFuncional;
    @EJB
    private HorarioDeTrabalhoFacade horarioDeTrabalhoFacade;
    private ConverterGenerico converterHorarioDeTrabalho;
    private MoneyConverter moneyConverter;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    private ConverterGenerico converterContaCorrenteBancaria;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    private ConverterAutoComplete converterPessoaFisica;
    private MatriculaFP matriculaSelecionado;
    private String numeroSelecionado;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNovo;
    private HorarioContratoFP horarioContratoFP;
    private PrevidenciaVinculoFP previdenciaVinculoFP;
    private OpcaoValeTransporteFP opcaoValeTransporteFP;
    private HierarquiaOrganizacional hierarquiaOrganizacionalEnquadramento;
    @EJB
    private CBOFacade cboFacade;
    private ConverterGenerico converterTipoRegime;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    private ConverterAutoComplete converterRecursoFP;
    private ConverterGenerico converterOpcaoSalarial;
    @EJB
    private TipoRegimeFacade tipoRegimeFacade;
    private SindicatoVinculoFP sindicatoVinculoFP;
    @EJB
    private SindicatoFacade sindicatoFacade;
    private ConverterGenerico converterSindicato;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    private ConverterAutoComplete converterSituacaoFuncional;
    private SituacaoContratoFP situacaoContratoFP;
    @EJB
    private UFFacade uFFacade;
    private ConverterGenerico converterUf;
    @EJB
    private NaturezaRendimentoFacade naturezaRendimentoFacade;
    private ConverterGenerico converterNaturezaRendimento;
    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private CategoriaSEFIPFacade categoriaSEFIPFacade;
    private ConverterAutoComplete converterCategoriaSEFIP;
    @EJB
    private TipoAdmissaoFGTSFacade tipoAdmissaoFGTSFacade;
    private ConverterGenerico converterTipoAdmissaoFGTS;
    @EJB
    private TipoAdmissaoSEFIPFacade tipoAdmissaoSEFIPFacade;
    private ConverterGenerico converterTipoAdmissaoSEFIP;
    @EJB
    private MovimentoCAGEDFacade movimentoCAGEDFacade;
    private ConverterGenerico converterMovimentoCAGED;
    @EJB
    private TipoAdmissaoRAISFacade tipoAdmissaoRAISFacade;
    private ConverterGenerico converterTipoAdmissaoRAIS;
    @EJB
    private OcorrenciaSEFIPFacade ocorrenciaSEFIPFacade;
    private ConverterGenerico converterOcorrenciaSEFIP;
    @EJB
    private RetencaoIRRFFacade retencaoIRRFFacade;
    private ConverterGenerico converterRetencaoIRRF;
    private ConverterGenerico converterVinculoEmpregaticio;
    @EJB
    private VinculoEmpregaticioFacade vinculoEmpregaticioFacade;
    private AssociacaoVinculoFP associacaoVinculoFP;
    @EJB
    private AssociacaoFacade associacaoFacade;
    private ConverterGenerico converterAssociacao;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @ManagedProperty(name = "enquadramentoFuncionalControlador", value = "#{enquadramentoFuncionalControlador}")
    private EnquadramentoFuncionalControlador enquadramentoFuncionalControlador;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    private ConverterAutoComplete converterCargo;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private ReintegracaoFacade reintegracaoFacade;
    private AtoLegal atoLegal;
    private Reintegracao reintegracao;
    private ConverterAutoComplete converterFichario;
    private ConverterAutoComplete converterPastaGaveta;
    private ConverterAutoComplete converterGavetaFichario;
    private PastaGavetaContratoFP pastaGavetaContratoFPSelecionado;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    @EJB
    private VinculoDeContratoFPFacade vinculoDeContratoFPFacade;
    private ConverterAutoComplete converterVinculoDeContratoFP;
    private ContratoVinculoDeContrato contratoVinculoDeContrato;
    private Tipo tipo;
    private Date dataProvimento;
    private Arquivo arquivo;
    private Arquivo arquivoRecurso;
    private UploadedFile file;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private RecursoDoVinculoFPFacade recursoDoVinculoFPFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    private List<RecursoDoVinculoFP> recursoDoVinculoFPs;
    @EJB
    private ContratoFPDataModel contratoFPDataModel;
    @EJB
    private ContratoFPDataModelPesquisar contratoFPDataModelPesquisar;
    private Date testeDesempenho;
    private OpcaoSalarialVinculoFP opcaoSalarialVinculoFP;
    @EJB
    private OpcaoSalarialCCFacade opcaoSalarialCCFacade;
    private StreamedContent imagemFoto;
    @EJB
    private ConfiguracaoModalidadeContratoFPFacade configuracaoModalidadeContratoFPFacade;
    private ModalidadeContratoFPData modalidadeContratoFPData;
    private RecursoFP recursoFP;
    private ContratoFPCargo contratoFPCargoSelecionado;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private ClassificacaoInscricao classificacaoInscricaoSelecionada;
    @EJB
    private ClassificacaoConcursoFacade classificacaoConcursoFacade;
    private boolean contratoFPOriundoConcurso;
    private Concurso concursoSelecionado;
    private CargoConcurso cargoConcurso;
    @EJB
    private CargoConcursoFacade cargoConcursoFacade;
    private CBO cboSelecionado;
    private Boolean edicaoCargo;
    private Boolean novoCargo;
    @EJB
    private BasePeriodoAquisitivoFacade basePeriodoAquisitivoFacade;
    private boolean podeAlterarSituacaoFuncional;

    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private AverbacaoTempoServicoFacade averbacaoTempoServicoFacade;
    @EJB
    private BeneficiarioFacade beneficiarioFacade;
    @EJB
    private BloqueioBeneficioFacade bloqueioBeneficioFacade;
    @EJB
    private CargoConfiancaFacade cargoConfiancaFacade;
    @EJB
    private CedenciaContratoFPFacade cedenciaContratoFPFacade;
    @EJB
    private RetornoCedenciaContratoFPFacade retornoCedenciaContratoFPFacade;
    @EJB
    private DirfFacade dirfFacade;
    @EJB
    private FaltasFacade faltasFacade;
    @EJB
    private HoraExtraFacade horaExtraFacade;
    @EJB
    private PenalidadeFPFacade penalidadeFPFacade;
    @EJB
    private PensaoFacade pensaoFacade;
    @EJB
    private ResponsavelTecnicoFiscalFacade responsavelTecnicoFiscalFacade;
    @EJB
    private RetornoFuncaoGratificadaFacade retornoFuncaoGratificadaFacade;
    @EJB
    private AssentamentoFuncionalFacade assentamentoFuncionalFacade;
    @EJB
    private AvisoPrevioFacade avisoPrevioFacade;
    @EJB
    private SolicitacaoAfastamentoPortalFacade solicitacaoAfastamentoPortalFacade;
    @EJB
    private AlteracaoLocalTrabalhoPorLoteFacade alteracaoLocalTrabalhoPorLoteFacade;
    @EJB
    private BloqueioEventoFPFacade bloqueioEventoFPFacade;
    @EJB
    private ExperienciaExtraCurricularFacade experienciaExtraCurricularFacade;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private SextaParteFacade sextaParteFacade;
    @EJB
    private UtilImportaExportaFacade utilImportaExportaFacade;
    @EJB
    private CreditoSalarioFacade creditoSalarioFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private FichaFinanceiraFPSimulacaoFacade fichaFinanceiraFPSimulacaoFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private FuncaoGratificadaFacade funcaoGratificadaFacade;

    private String xml;
    private TipoArquivoESocial tipoArquivoESocial;
    private ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial;

    private List<OcorrenciaESocialDTO> ocorrencias;
    private boolean editandoOpcaoSalarial = false;

    private List<PrevidenciaArquivo> previdenciaArquivos;
    private Boolean edicaoPrevidencia;
    private String qrCode;
    private ConfiguracaoModalidadeContratoFP configuracaoModalidadeContratoFP;

    private EnderecoCorreio enderecoContratoTemporario;
    private TipoPessoa tipoPessoaEstabelecimentoEfetivacaoAprendiz;

    public ContratoFPControlador() {
        super(ContratoFP.class);
        ocorrencias = Lists.newArrayList();
        edicaoPrevidencia = false;
        previdenciaArquivos = new ArrayList<>();
        enderecoContratoTemporario = new EnderecoCorreio();
    }

    public List<OcorrenciaESocialDTO> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(List<OcorrenciaESocialDTO> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    public List<SelectItem> getListarTodasBasesPeriodoAquisitivo() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (BasePeriodoAquisitivo base : basePeriodoAquisitivoFacade.buscarTodasBasesPeriodoAquisitivo()) {
            toReturn.add(new SelectItem(base, base.toString()));
        }
        return toReturn;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public ModalidadeContratoFPData getModalidadeContratoFPData() {
        return modalidadeContratoFPData;
    }

    public void setModalidadeContratoFPData(ModalidadeContratoFPData modalidadeContratoFPData) {
        this.modalidadeContratoFPData = modalidadeContratoFPData;
    }

    public Boolean isNovoCargo() {
        return novoCargo;
    }

    public void setNovoCargo(Boolean novoCargo) {
        this.novoCargo = novoCargo;
    }

    public Boolean isEdicaoCargo() {
        return edicaoCargo;
    }

    public void setEdicaoCargo(Boolean edicaoCargo) {
        this.edicaoCargo = edicaoCargo;
    }

    public Boolean isEdicaoPrevidencia() {
        return edicaoPrevidencia;
    }

    public void setEdicaoPrevidencia(Boolean edicaoPrevidencia) {
        this.edicaoPrevidencia = edicaoPrevidencia;
    }

    public CargoConcurso getCargoConcurso() {
        return cargoConcurso;
    }

    public void setCargoConcurso(CargoConcurso cargoConcurso) {
        this.cargoConcurso = cargoConcurso;
    }

    public Concurso getConcursoSelecionado() {
        return concursoSelecionado;
    }

    public void setConcursoSelecionado(Concurso concursoSelecionado) {
        this.concursoSelecionado = concursoSelecionado;
    }

    public boolean isPodeAlterarSituacaoFuncional() {
        return podeAlterarSituacaoFuncional;
    }

    public void setPodeAlterarSituacaoFuncional(boolean podeAlterarSituacaoFuncional) {
        this.podeAlterarSituacaoFuncional = podeAlterarSituacaoFuncional;
    }

    public boolean isContratoFPOriundoConcurso() {
        return contratoFPOriundoConcurso;
    }

    public void setContratoFPOriundoConcurso(boolean contratoFPOriundoConcurso) {
        if (contratoFPOriundoConcurso && selecionado.getMatriculaFP() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Matrícula deve ser informado para poder prosseguir.");
            FacesUtil.atualizarComponente("Formulario:tab:panel-concursos");
            contratoFPOriundoConcurso = false;
            return;
        }
        this.contratoFPOriundoConcurso = contratoFPOriundoConcurso;
    }

    @URLAction(mappingId = "novoContratoFPCandidatoConcurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void criarMatriculaDecorrenteDeCandidatoConcurso() {
        classificacaoInscricaoSelecionada = classificacaoConcursoFacade.buscarClassificacaoInscricao(getId());
        Long pessoaId = pessoaFisicaFacade.buscarIdDePessoaPorCpf(classificacaoInscricaoSelecionada.getInscricaoConcurso().getCpf());
        Long matriculaFpId = matriculaFPFacade.buscarMatriculaDaPessoa(pessoaId);
        novo();
        selecionado.setMatriculaFP(matriculaFPFacade.recuperar(matriculaFpId));
        classificacaoInscricaoSelecionada.setContratoFP(selecionado);
        contratoFPOriundoConcurso = true;
        concursoSelecionado = concursoFacade.buscarConcursoComCargos(classificacaoInscricaoSelecionada.getClassificacaoConcurso().getCargo().getConcurso().getId());
        cargoConcurso = cargoConcursoFacade.buscarCargoComCBOsAndInscricoes(classificacaoInscricaoSelecionada.getClassificacaoConcurso().getCargo().getId());
        cargoConcurso.setCargo(cargoFacade.recuperar(cargoConcurso.getCargo().getId()));
        criarContratoFPCargoPeloModuloConcursos();
        matriculaSelecionado = selecionado.getMatriculaFP();
        executarValidacoesAoSelecionarMatricula();
        atribuirNumeroContratoParaSelecionado();
    }

    public ContratoFPCargo getContratoFPCargoSelecionado() {
        return contratoFPCargoSelecionado;
    }

    public void setContratoFPCargoSelecionado(ContratoFPCargo contratoFPCargoSelecionado) {
        this.contratoFPCargoSelecionado = contratoFPCargoSelecionado;
    }

    public RecursoFP getRecursoFP() {
        return recursoFP;
    }

    public void setRecursoFP(RecursoFP recursoFP) {
        this.recursoFP = recursoFP;
    }

    public ContratoFPDataModel getContratoFPDataModel() {
        return contratoFPDataModel;
    }

    public void setContratoFPDataModel(ContratoFPDataModel contratoFPDataModel) {
        this.contratoFPDataModel = contratoFPDataModel;
    }

    public ContratoFPDataModelPesquisar getContratoFPDataModelPesquisar() {
        return contratoFPDataModelPesquisar;
    }

    public void setContratoFPDataModelPesquisar(ContratoFPDataModelPesquisar contratoFPDataModelPesquisar) {
        this.contratoFPDataModelPesquisar = contratoFPDataModelPesquisar;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public Date getDataProvimento() {
        return dataProvimento;
    }

    public void setDataProvimento(Date dataProvimento) {
        this.dataProvimento = dataProvimento;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public TipoArquivoESocial getTipoArquivoESocial() {
        return tipoArquivoESocial;
    }

    public void setTipoArquivoESocial(TipoArquivoESocial tipoArquivoESocial) {
        this.tipoArquivoESocial = tipoArquivoESocial;
    }

    public void setaDatas(DateSelectEvent date) {
        Date e = date.getDate();
        previdenciaVinculoFP.setInicioVigencia(e);
        horarioContratoFP.setInicioVigencia(e);
        lotacaoFuncional.setInicioVigencia(e);
        sindicatoVinculoFP.setInicioVigencia(e);
        situacaoContratoFP.setInicioVigencia(e);
        definirDataInicioVigenciaNaPrevidencia(e, isOperacaoNovo());
        definirDataInicioVigenciaHorario(e, isOperacaoNovo());
        if (isOperacaoNovo() && !selecionado.getSituacaoFuncionals().isEmpty()) {
            SituacaoContratoFP s = selecionado.getSituacaoFuncionals().get(0);
            if (s != null) {
                s.setInicioVigencia(e);
            }
        }
    }

    public void definirTodasAsDatas() {
        Date e = selecionado.getDataNomeacao();
        if (e != null) {
            selecionado.setDataPosse(e);
            selecionado.setDataExercicio(e);
            selecionado.setInicioVigencia(e);
            definirDatas();
        }
    }

    public void definirDatas() {
        try {
            Date e = selecionado.getInicioVigencia();
            if (e != null) {
                previdenciaVinculoFP.setInicioVigencia(e);
                horarioContratoFP.setInicioVigencia(e);
                lotacaoFuncional.setInicioVigencia(e);
                sindicatoVinculoFP.setInicioVigencia(e);
                situacaoContratoFP.setInicioVigencia(e);
                modalidadeContratoFPData.setInicioVigencia(e);
                if (pastaGavetaContratoFPSelecionado != null) {
                    pastaGavetaContratoFPSelecionado.setInicioVigencia(e);
                }
                contratoVinculoDeContrato.setInicioVigencia(e);
                definirDataInicioVigenciaNaPrevidencia(e, isOperacaoNovo());
                definirDataInicioVigenciaHorario(e, isOperacaoNovo());
                definirDataInicioVigenciaSituacaoFuncional(e, isOperacaoNovo());
                definirDataInicioVigenciaCargos(e, isOperacaoNovo());
                definirDataInicioVigenciaSituacaoFuncional(e, isOperacaoNovo());
                definirDataInicioVigenciaSindicato(e, isOperacaoNovo());
                definirDataInicioVigenciaValeTransporte(e, isOperacaoNovo());
                definirDataInicioLotacaoFuncional(e, isOperacaoNovo());
                definirDataInicioLotacaoVinculoEconsig(e, isOperacaoNovo());
                definirDataInicioVigenciaRecursoFP(e, isOperacaoNovo());

                carregarDialogAlteracaoData();
            }
        } catch (Exception e) {
            logger.error("Não foi possivel definir as datas", e.getMessage());
            FacesUtil.addErrorGenerico(e);
        }
    }

    private void carregarDialogAlteracaoData() {
        if (isOperacaoEditar()) {
            FacesUtil.executaJavaScript("definirData.show();");
        }
    }

    public void definirDataInicioVigencia() {
        try {
            Date data = selecionado.getInicioVigencia();
            if (data != null) {
                definirDataInicioVigenciaNaPrevidencia(data, true);
                definirDataInicioVigenciaHorario(data, true);
                definirDataInicioVigenciaSituacaoFuncional(data, true);
                definirDataInicioVigenciaCargos(data, true);
                definirDataInicioVigenciaSituacaoFuncional(data, true);
                definirDataInicioVigenciaRecursoFP(data, true);
                definirDataInicioVigenciaSindicato(data, true);
                definirDataInicioVigenciaValeTransporte(data, true);
                definirDataInicioLotacaoFuncional(data, true);
                definirDataInicioLotacaoVinculoEconsig(data, true);
                definirDataInicioLotacaoEnquadramentoFuncional(data, true);
            }
        } catch (Exception e) {
            logger.error("Não foi possivel definir as datas", e.getMessage());
            FacesUtil.addErrorGenerico(e);
        }

    }

    private void definirDataInicioLotacaoEnquadramentoFuncional(Date data, boolean podeAlterar) {
        if (podeAlterar && data != null && selecionado.getEnquadramentos() != null && !selecionado.getEnquadramentos().isEmpty() && selecionado.getEnquadramentos().size() == 1) {
            EnquadramentoFuncional enqu = selecionado.getEnquadramentos().get(0);
            enqu.setInicioVigencia(data);
        }
    }

    private void definirDataInicioLotacaoVinculoEconsig(Date data, boolean podeAlterar) {
        if (podeAlterar && data != null && selecionado.getContratoVinculoDeContratos() != null && !selecionado.getContratoVinculoDeContratos().isEmpty() && selecionado.getContratoVinculoDeContratos().size() == 1) {
            ContratoVinculoDeContrato cont = selecionado.getContratoVinculoDeContratos().get(0);
            cont.setInicioVigencia(data);
        }
    }

    private void definirDataInicioLotacaoFuncional(Date data, boolean podeAlterar) {
        if (podeAlterar && data != null && selecionado.getLotacaoFuncionals() != null && !selecionado.getLotacaoFuncionals().isEmpty() && selecionado.getLotacaoFuncionals().size() == 1) {
            LotacaoFuncional lotacao = selecionado.getLotacaoFuncionals().get(0);
            lotacao.setInicioVigencia(data);
        }
    }

    private void definirDataInicioVigenciaValeTransporte(Date data, boolean podeAlterar) {
        if (podeAlterar && data != null && selecionado.getOpcaoValeTransporteFPs() != null && !selecionado.getOpcaoValeTransporteFPs().isEmpty() && selecionado.getOpcaoValeTransporteFPs().size() == 1) {
            OpcaoValeTransporteFP opcao = selecionado.getOpcaoValeTransporteFPs().get(0);
            opcao.setInicioVigencia(data);
        }
    }

    private void definirDataInicioVigenciaSindicato(Date data, boolean podeAlterar) {
        if (podeAlterar && data != null && selecionado.getSindicatosVinculosFPs() != null && !selecionado.getSindicatosVinculosFPs().isEmpty() && selecionado.getSindicatosVinculosFPs().size() == 1) {
            SindicatoVinculoFP sind = selecionado.getSindicatosVinculosFPs().get(0);
            sind.setInicioVigencia(data);
        }
    }

    private void definirDataInicioVigenciaRecursoFP(Date data, boolean podeAlterar) {
        if (podeAlterar && data != null && selecionado.getRecursosDoVinculoFP() != null && !selecionado.getRecursosDoVinculoFP().isEmpty() && selecionado.getRecursosDoVinculoFP().size() == 1) {
            RecursoDoVinculoFP rec = selecionado.getRecursosDoVinculoFP().get(0);
            rec.setInicioVigencia(data);
            setRecursoDoVinculoFPs(selecionado.getRecursosDoVinculoFP());
        }
    }

    private void definirDataInicioVigenciaSituacaoFuncional(Date data, boolean podeAlterar) {
        if (podeAlterar && data != null && selecionado.getSituacaoFuncionals() != null && !selecionado.getSituacaoFuncionals().isEmpty() && selecionado.getSituacaoFuncionals().size() == 1) {
            SituacaoContratoFP situacao = selecionado.getSituacaoFuncionals().get(0);
            situacao.setInicioVigencia(data);
        }
    }

    private void definirDataInicioVigenciaCargos(Date data, boolean podeAlterar) {
        if (podeAlterar && data != null && selecionado.getCargos() != null && !selecionado.getCargos().isEmpty() && selecionado.getCargos().size() == 1) {
            ContratoFPCargo cargo = selecionado.getCargos().get(0);
            cargo.setInicioVigencia(data);
        }
    }

    public void definirDataInicioVigenciaNaPrevidencia(Date date, boolean podeAlterarData) {
        if (podeAlterarData && date != null && selecionado.getPrevidenciaVinculoFPs() != null && !selecionado.getPrevidenciaVinculoFPs().isEmpty() && selecionado.getPrevidenciaVinculoFPs().size() == 1) {
            PrevidenciaVinculoFP prev = selecionado.getPrevidenciaVinculoFPs().get(0);
            prev.setInicioVigencia(date);
        }
    }

    public void definirDataInicioVigenciaHorario(Date date, boolean podeAlterar) {
        if (podeAlterar && date != null && selecionado.getHorarioContratoFPs() != null && selecionado.getHorarioContratoFPs().size() == 1) {
            for (HorarioContratoFP horarioContrato : selecionado.getHorarioContratoFPs()) {
                if (horarioContrato.getFimVigencia() == null) {
                    horarioContrato.setInicioVigencia(date);
                }
            }
        }
    }


    public List<SelectItem> getTipoRegime() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoRegime object : tipoRegimeFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoVinculo object : SituacaoVinculo.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getDescansoSemanals() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(""));
        for (DescansoSemanal obj : DescansoSemanal.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public void marcaFGTS() {
        if (selecionado.getTipoRegime() != null && selecionado.getTipoRegime().getCodigo() == 1) {
            selecionado.setFgts(Boolean.TRUE);
        } else {
            selecionado.setFgts(Boolean.FALSE);
        }
    }

    public ConverterGenerico getConverterTipoRegime() {
        if (converterTipoRegime == null) {
            converterTipoRegime = new ConverterGenerico(TipoRegime.class, tipoRegimeFacade);
        }
        return converterTipoRegime;
    }

    public void setaHierarquiaOrganizacional(SelectEvent item) {
        selecionado.setHierarquiaOrganizacional((HierarquiaOrganizacional) item.getObject());
        if (selecionado.getHierarquiaOrganizacional().getSubordinada().getObrigaQualificacaoCadastral()) {
            validarPessoaQualificada();
        }
    }

    public ConverterAutoComplete getConverterRecursoFP() {
        if (converterRecursoFP == null) {
            converterRecursoFP = new ConverterAutoComplete(RecursoFP.class, recursoFPFacade);
        }
        return converterRecursoFP;
    }

    public List<RecursoFP> completaRecursoFP(String parte) {
        if (hierarquiaOrganizacionalEnquadramento != null && hierarquiaOrganizacionalEnquadramento.getId() != null) {
            return recursoFPFacade.retornaRecursoFPDo2NivelDeHierarquia(parte, hierarquiaOrganizacionalEnquadramento, selecionado.getInicioVigencia());
        } else {
            List<RecursoFP> retorno = new ArrayList<>();
            retorno.add(new RecursoFP("A Hierarquia Organizacional Administrativa não foi selecionada!"));
            return retorno;
        }
    }

    public List<SelectItem> getRecursosFP() {
        List<SelectItem> toRetorno = new ArrayList<>();
        List<RecursoFP> recursoFPs = Lists.newLinkedList();
        if (hierarquiaOrganizacionalEnquadramento != null && hierarquiaOrganizacionalEnquadramento.getId() != null) {
            recursoFPs = recursoFPFacade.retornaRecursoFPDo2NivelDeHierarquia(hierarquiaOrganizacionalEnquadramento, selecionado.getInicioVigencia());
        } else {
            recursoFPs.add(new RecursoFP(1L, "0", "A Hierarquia Organizacional Administrativa não foi selecionada!"));
        }
        for (RecursoFP fp : recursoFPs) {
            toRetorno.add(new SelectItem(fp, fp.toString()));
        }
        return toRetorno;
    }

    public ContratoFPFacade getFacade() {
        return contratoFPFacade;
    }

    public PrevidenciaVinculoFP getPrevidenciaVinculoFP() {
        return previdenciaVinculoFP;
    }

    public void setPrevidenciaVinculoFP(PrevidenciaVinculoFP previdenciaVinculoFP) {
        this.previdenciaVinculoFP = previdenciaVinculoFP;
    }

    public OpcaoValeTransporteFP getOpcaoValeTransporteFP() {
        return opcaoValeTransporteFP;
    }

    public void setOpcaoValeTransporteFP(OpcaoValeTransporteFP opcaoValeTransporteFP) {
        this.opcaoValeTransporteFP = opcaoValeTransporteFP;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalEnquadramento() {
        return hierarquiaOrganizacionalEnquadramento;
    }

    public void setHierarquiaOrganizacionalEnquadramento(HierarquiaOrganizacional hierarquiaOrganizacionalEnquadramento) {
        this.hierarquiaOrganizacionalEnquadramento = hierarquiaOrganizacionalEnquadramento;
    }

    public SituacaoContratoFP getSituacaoContratoFP() {
        return situacaoContratoFP;
    }

    public void setSituacaoContratoFP(SituacaoContratoFP situacaoContratoFP) {
        this.situacaoContratoFP = situacaoContratoFP;
    }

    //caso o servidor não se enquadra nos criterios de ter TIPO de PEQ dever setar null
    private void validarInformacaoPeq() {
        if (selecionado.getTipoPeq() != null) {
            if (!isHabilitaTipoPEQ()) {
                selecionado.setTipoPeq(null);
            }
        }
    }

    public void validarContratoTemporario() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCategoriaTrabalhador() != null) {
            if (selecionado.getCategoriaTrabalhador().getCodigo() == TEMPORARIO) {
                if (selecionado.getHipoteseLegalContratacao() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("A Hipótese legal para contratação na aba E-social para servidor Temporário deve ser informado.");
                }
                if (Strings.isNullOrEmpty(selecionado.getJustificativaTrabalhadorTemp())) {
                    ve.adicionarMensagemDeCampoObrigatorio("A Justificativa na aba E-social para servidor Temporário deve ser informado.");
                }
                if (Strings.isNullOrEmpty(selecionado.getObjetoContratacao())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O Objeto determinante da contratação na aba E-social para servidor Temporário deve ser informado.");
                }
                if (Strings.isNullOrEmpty(enderecoContratoTemporario.getCep())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O CEP do endereço das Atividades na aba E-social para servidor Temporário deve ser informado.");
                }
                if (Strings.isNullOrEmpty(enderecoContratoTemporario.getUf())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O Estado do endereço das Atividades na aba E-social para servidor Temporário deve ser informado.");
                }
                if (Strings.isNullOrEmpty(enderecoContratoTemporario.getLocalidade())) {
                    ve.adicionarMensagemDeCampoObrigatorio("A Cidade do endereço das Atividades na aba E-social para servidor Temporário deve ser informado.");
                }
                if (Strings.isNullOrEmpty(enderecoContratoTemporario.getBairro())) {
                    ve.adicionarMensagemDeCampoObrigatorio("A Bairro do endereço das Atividades na aba E-social para servidor Temporário deve ser informado.");
                }
                if (Strings.isNullOrEmpty(enderecoContratoTemporario.getLogradouro())) {
                    ve.adicionarMensagemDeCampoObrigatorio("A Logradouro do endereço das Atividades na aba E-social para servidor Temporário deve ser informado.");
                }
                if (Strings.isNullOrEmpty(enderecoContratoTemporario.getNumero())) {
                    ve.adicionarMensagemDeCampoObrigatorio("A Numero do endereço das Atividades na aba E-social para servidor Temporário deve ser informado");
                }
                if (!ve.temMensagens()) {
                    selecionado.setEnderecoContratoTemporario(enderecoContratoTemporario);
                }
            }
        }
        ve.lancarException();


    }

    public void validarContratoAprendiz() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCategoriaTrabalhador() != null) {
            if (selecionado.getCategoriaTrabalhador().getCodigo() == APRENDIZ) {
                if (selecionado.getModalidadeContrAprendiz() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("A Modalidade de contratação do aprendiz na aba E-social para servidor Aprendiz deve ser informado.");
                } else {
                    if (ModalidadeContratacaoAprendiz.CONTRATACAO_DIRETA.equals(selecionado.getModalidadeContrAprendiz())) {
                        if (selecionado.getEntidadeQualificadora() == null) {
                            ve.adicionarMensagemDeCampoObrigatorio("A Entidade Qualificadora na aba E-social para servidor Aprendiz deve ser informado.");
                        }
                    } else {
                        if (selecionado.getEstabelecContratAprendiz() == null) {
                            ve.adicionarMensagemDeCampoObrigatorio("A Estabelecimento para o qual a contratação de aprendiz foi efetivada na aba E-social para servidor Aprendiz deve ser informado.");
                        }
                    }
                }
            }
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            processarDados();
            validarContratoFP();
            validarModalidadeContratoFPParaSalvar();
            validarInformacaoPeq();
            validarSegmentoAtuacaoSiope();
            validarContratoTemporario();
            validarContratoAprendiz();
            contratoFPFacade.salvarPrevidenciaArquivo(selecionado);
            if (isOperacaoNovo()) {
                if (Objects.equals(selecionado.getModalidadeContratoFP().getCodigo(), ModalidadeContratoFP.CARGO_COMISSAO) ||
                    Objects.equals(selecionado.getModalidadeContratoFP().getCodigo(), ModalidadeContratoFP.FUNCAO_COORDENACAO)) {
                    contratoFPFacade.verificarParametrosControleCargoComissaoAndValores(selecionado);
                }
                contratoFPFacade.salvarNovoContratoFP(selecionado);
            } else {
                atualizarDataAlteracao();
                contratoFPFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void processarDados() {
        selecionado.setDataAdmissao(selecionado.getInicioVigencia());
        selecionado.setUnidadeOrganizacional(selecionado.getHierarquiaOrganizacional().getSubordinada());
        anularDataOpcaoFGTS();
    }

    private void atualizarDataAlteracao() {
        selecionado.setDataAlteracao(UtilRH.getDataOperacao());
    }

    private void anularDataOpcaoFGTS() {
        if (!selecionado.getFgts()) {
            selecionado.setDataOpcaoFGTS(null);
        }
    }

    private void validarContratoFP() {
        ValidacaoException ve = new ValidacaoException();

        validarProvimentoTipoNomeacaoJaCadastrado(ve);
        lancarExcecao(ve);

        validarCamposObrigatoriosContratoFP(ve);
        lancarExcecao(ve);

        validarRegrasDeNegocio(ve);
        lancarExcecao(ve);

        validarDescontarIrMultiploVinculoJaMarcadoDaMesmaEntidade(ve);
        lancarExcecao(ve);
    }

    private void validarProvimentoTipoNomeacaoJaCadastrado(ValidacaoException ve) {
        if (tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.NOMEACAO) == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Provimento para Nomeação não foi cadastrado!");
        }
    }

    private void validarDescontarIrMultiploVinculoJaMarcadoDaMesmaEntidade(ValidacaoException ve) {
        List<VinculoFP> vinculoFPs = vinculoFPFacade.buscarVinculosVigentePorMatricula(selecionado.getMatriculaFP().getMatricula(), sistemaControlador.getDataOperacao());
        if (vinculoFPs != null && !vinculoFPs.isEmpty()) {
            List<BigDecimal> idEntidadesSelecionado = folhaDePagamentoFacade.buscarIdFontePagadora(selecionado, sistemaControlador.getDataOperacao());

            for (VinculoFP vinculo : vinculoFPs) {
                if (!vinculo.equals(selecionado) && vinculo.getDescontarIrMultiploVinculo() && selecionado.getDescontarIrMultiploVinculo()) {
                    List<BigDecimal> idEntidadesOutros = folhaDePagamentoFacade.buscarIdFontePagadora(vinculo, sistemaControlador.getDataOperacao());
                    if (idEntidadesOutros.containsAll(idEntidadesSelecionado)) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Existem contratos já marcados com IR sobre Múltiplos Vínculos para o vínculo atual: " + vinculo.getContratoFP());
                        return;
                    }
                }
            }
        }
    }

    private void validarRegrasDeNegocio(ValidacaoException ve) {
        validarDisponibilidadeDeNovoContratoPorServidor(ve);
        validarFinalVigencia(ve);
        validarDataPossePosteriorDataNomeacao(ve);
        validarMesmaDataParaNomeacaoAndPosseAndExercicio(ve);
        validarDataExercicioEmRelacaoDataNomeacao();
        validarHorasSemanaisReferenteJornadaTrabalho(ve);

        validarDataOpcaoFgts(ve);
        validarPrevidencia(ve);
        validarHorarioTrabalho(ve);
        validarLotacaoFuncional(ve);
        validarVinculoEConsig(ve);

        validarMovimentoCAGED(ve);
        validarNaturezaRendimentoDIRF(ve);
        validarTipoAdmissaoFGTS(ve);
        validarRetencaoIRRF(ve);
        validarTipoAdmissaoRAIS(ve);
        validarSEFIP(ve);
        validarVinculoEmpregaticio(ve);

        validarCargos(ve);
        validarNivelEscolaridadeDoServidorReferenteAoCargoDoContrato(ve);
        validarHabilidadesDoServidorReferenteAoCargoDoContrato(ve);
        validarAreasFormacaoDoServidorReferenteAoCargoDoContrato(ve);
    }

    private void validarLotacaoFuncional(ValidacaoException ve) {
        if (CollectionUtils.isEmpty(selecionado.getLotacaoFuncionals())) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione ao menos um Local de Trabalho!");
        }
    }

    private void validarHorarioTrabalho(ValidacaoException ve) {
        if (CollectionUtils.isEmpty(selecionado.getHorarioContratoFPs())) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione ao menos um Horário de Trabalho!");
        }
    }

    private void validarAreasFormacaoDoServidorReferenteAoCargoDoContrato(ValidacaoException ve) {
        if (selecionado.temCargo()) {
            PessoaFisica pf = pessoaFisicaFacade.recuperarComFormacoes(selecionado.getMatriculaFP().getPessoa().getId());
            Cargo cargo = cargoFacade.recuperarComAreasFormacao(selecionado.getCargo().getId());

            List<AreaFormacao> areasFormacaoPorPessoa = getAreasFormacaoPorPessoa(pf);
            List<AreaFormacao> areasFormacaoPorCargo = getAreasFormacaoPorCargo(cargo);

            if (!areasFormacaoPorPessoa.containsAll(areasFormacaoPorCargo)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Pessoa não possui todas as formações exigidas pelo cargo " + selecionado.getCargo());
            }
        }
    }

    private void validarHabilidadesDoServidorReferenteAoCargoDoContrato(ValidacaoException ve) {
        if (selecionado.temCargo()) {
            PessoaFisica pf = pessoaFisicaFacade.recuperarComHabilidades(selecionado.getMatriculaFP().getPessoa().getId());
            Cargo cargo = cargoFacade.recuperarComHabilidades(selecionado.getCargo().getId());

            List<Habilidade> habilidadesPorPessoa = getHabilidadesPorPessoa(pf);
            List<Habilidade> habilidadesPorCargo = getHabilidadesPorCargo(cargo);

            if (!habilidadesPorPessoa.containsAll(habilidadesPorCargo)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Pessoa não possui todas as habilidades exigidas pelo cargo " + selecionado.getCargo());
            }
        }
    }

    private void validarNivelEscolaridadeDoServidorReferenteAoCargoDoContrato(ValidacaoException ve) {
        if (selecionado.temCargo()) {
            try {
                NivelEscolaridade nivelEscolaridadePessoa = selecionado.getNivelEscolaridadePessoa();
                NivelEscolaridade nivelEscolaridadeCargo = selecionado.getNivelEscolaridadeCargo();
                if (nivelEscolaridadePessoa.getOrdem() < nivelEscolaridadeCargo.getOrdem()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O instituidor desta matrícula não está apto a assumir o cargo, " + selecionado.getCargo() + " devido ao seu nível de escolaridade!");
                }
            } catch (ExcecaoNegocioGenerica ex) {
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            } catch (Exception ex) {
                logger.debug(ex.getMessage());
            }
        }
    }

    private void validarHorasSemanaisReferenteJornadaTrabalho(ValidacaoException ve) {
        Integer horasSemanal;
        try {
            horasSemanal = selecionado.getJornadaDeTrabalho().getHorasSemanal();
        } catch (NullPointerException npe) {
            logger.debug("Não foi encontrado horas semanais para jornada de trabalho: ID: " + selecionado.getJornadaDeTrabalho().getId());
            return;
        }
        validarHorasSemanaisReferenteJornadaTrabalho(ve, horasSemanal);
    }

    private void validarHorasSemanaisReferenteJornadaTrabalho(ValidacaoException ve, Integer horasSemanal) {
        BigDecimal horasSemanaisPorMatricula = jornadaDeTrabalhoFacade.retornaHorasSemanaisSomadas(selecionado);
        BigDecimal horasSemanais = new BigDecimal(getHoraSemanalContratoVigente(horasSemanal));
        if (horasSemanaisPorMatricula != null) {
            BigDecimal total = horasSemanais.add(horasSemanaisPorMatricula);
            if (total.compareTo(new BigDecimal(60)) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Com a jornada de trabalho informada, o servidor " + selecionado.getMatriculaFP().toString() + " excede 60 horas semanais!");
            }
        }
    }

    private Integer getHoraSemanalContratoVigente(Integer horasSemanal) {
        if (selecionado.getFinalVigencia() == null) {
            return horasSemanal;
        }
        Date dataOperacao = Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao());
        if (selecionado.getInicioVigencia() != null && (selecionado.getInicioVigencia().compareTo(dataOperacao) <= 0 &&
            selecionado.getFinalVigencia().compareTo(dataOperacao) >= 0)) {
            return horasSemanal;
        }
        return 0;
    }

    private void validarDisponibilidadeDeNovoContratoPorServidor(ValidacaoException ve) {
        if (!verificaQuantidadeDeContratosVigentes()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível cadastrar um novo contrato para este servidor, pois já possui dois contratos ativos!");
        }
    }

    private void validarCargos(ValidacaoException ve) {
        if (CollectionUtils.isEmpty(selecionado.getCargos())) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione ao menos um Cargo!");
        } else if (!cargoFacade.cargoContemUnidadeOrganizacional(selecionado.getCargo().getId(), selecionado.getHierarquiaOrganizacional().getSubordinada().getId())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Cargo do servidor não pertence à Hierarquia/Lotação Funcional escolhida.");
        }
    }

    private void validarFinalVigencia(ValidacaoException ve) {
        if (selecionado.isModalidadeContratoFPCargoComissao()) {
            if (!selecionado.temFinalVigencia()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Final da Vigência deve ser informado para contrato de modalidade cargo em comissão!");
            }
        }
        if (selecionado.temFinalVigencia()) {
            if (selecionado.getFinalVigencia().before(selecionado.getInicioVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Final da Vigência deve ser posterior a Data de Exercício!");
            }
        }
    }

    private void validarVinculoEmpregaticio(ValidacaoException ve) {
        if (!selecionado.temVinculoEmpregaticio()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Vínculo Empregatício na aba Informações das Declarações deve ser informado!");
        }
    }

    private void validarTipoOcorrenciaSEFIP(ValidacaoException ve) {
        if (!selecionado.temTipoOcorrenciaSEFIP()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Ocorrência SEFIP na aba Informações das Declarações deve ser informado!");
        }
    }

    private void validarTipoAdmissaoSEFIP(ValidacaoException ve) {
        if (!selecionado.temTipoAdmissaoSEFIP()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Admissão SEFIP na aba Informações das Declarações deve ser informado!");
        }
    }

    private void validarCategoriaSEFIP(ValidacaoException ve) {
        if (!selecionado.temCategoriaSEFIP()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Categoria SEFIP na aba Informações das Declarações deve ser informado!");
        }
    }

    private void validarSEFIP(ValidacaoException ve) {
        if (selecionado.getSefip()) {
            validarCategoriaSEFIP(ve);
            validarTipoAdmissaoSEFIP(ve);
            validarTipoOcorrenciaSEFIP(ve);
        }
    }

    private void validarTipoAdmissaoRAIS(ValidacaoException ve) {
        if (!selecionado.temTipoAdmissaoRAIS()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Admissão RAIS na aba Informações das Declarações deve ser informado!");
        }
    }

    private void validarRetencaoIRRF(ValidacaoException ve) {
        if (!selecionado.temRetencaoIRRF()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Retenção IRRF na aba Informações das Declarações deve ser informado!");
        }
    }

    private void validarTipoAdmissaoFGTS(ValidacaoException ve) {
        if (!selecionado.temTipoAdmissaoFGTS()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Admissão FGTS na aba Informações das Declarações deve ser informado!");
        }
    }

    private void validarNaturezaRendimentoDIRF(ValidacaoException ve) {
        if (!selecionado.temNaturezaRendimentoDirf()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Natureza de Rendimento DIRF na aba Informações das Declarações deve ser informado!");
        }
    }

    private void validarMovimentoCAGED(ValidacaoException ve) {
        if (!selecionado.temMovimentoCAGED()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Movimento CAGED na aba Informações das Declarações deve ser informado!");
        }
    }

    private void validarDataExercicioEmRelacaoDataNomeacao() {
        if (selecionado.isModalidadeContratoFPConcursados()) {
            if (!dataExercicioEhInferiorOuIgualAQuinzeDiasEmRelacaoDataNomeacao(selecionado.getInicioVigencia(), selecionado.getDataNomeacao())) {
                FacesUtil.addOperacaoNaoPermitida("A Data de Exercício deve ser igual ou inferior a 15 dias em relação a Data de Nomeação!");
            }
        }
    }

    private boolean dataExercicioEhInferiorOuIgualAQuinzeDiasEmRelacaoDataNomeacao(Date inicioVigencia, Date dataNomeacao) {
        return DataUtil.diferencaDiasInteira(inicioVigencia, dataNomeacao) <= 15;
    }

    private void validarMesmaDataParaNomeacaoAndPosseAndExercicio(ValidacaoException ve) {
        if (!selecionado.isModalidadeContratoFPConcursados()) {
            if (selecionado.temDataPosse()) {
                if (!saoDatasIguais(selecionado.getDataNomeacao(), selecionado.getInicioVigencia(), selecionado.getDataPosse())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Os campos Data de Nomeação, Data de Posse e Data de Exercício devem ser iguais!");
                }
            }
        }
    }

    private boolean saoDatasIguais(Date dataNomeacao, Date inicioVigencia, Date dataPosse) {
        return saoDatasIguais(dataNomeacao, inicioVigencia)
            && saoDatasIguais(dataNomeacao, dataPosse)
            && saoDatasIguais(inicioVigencia, dataPosse);
    }

    private boolean saoDatasIguais(Date primeiraData, Date segundaData) {
        return primeiraData.equals(segundaData);
    }

    private void validarDataPossePosteriorDataNomeacao(ValidacaoException ve) {
        if (selecionado.temDataPosse()) {
            if (selecionado.getDataPosse().before(selecionado.getDataNomeacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Posse deve ser posterior a Data de Nomeação!");
            }
        }
    }


    private void validarVinculoEConsig(ValidacaoException ve) {
        if (CollectionUtils.isEmpty(selecionado.getContratoVinculoDeContratos())) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione ao menos um Vínculo (E-Consig)!");
        }
    }

    private void validarPrevidencia(ValidacaoException ve) {
        if (CollectionUtils.isEmpty(selecionado.getPrevidenciaVinculoFPs())) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione ao menos uma Previdência!");
        }
    }

    private void validarDataOpcaoFgts(ValidacaoException ve) {
        if (selecionado.getFgts()) {
            if (!selecionado.temDataOpcaoFgts()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Opção do FGTS deve ser informado!");
            }
        }
    }

    private void lancarExcecao(ValidacaoException ve) {
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void validarCamposObrigatoriosContratoFP(ValidacaoException ve) {
        validarMatriculaFP(ve);
        validarNumeroContratoFP(ve);
        validarModalidadeContratoFP(ve);
        validarDataNomeacao(ve);
        validarDataExercicio(ve);
        validarContaCorrenteBancaria(ve);
        validarDescansoSemanal(ve);
        validarTipoRegimeJuridico(ve);
        validarSituacaoVinculo(ve);
        validarHierarquiaOrganizacional(ve);
        validarCategoriaTrabalhador(ve);
        validarSicap(ve);
        validarBBPrev(ve);
        if (!Util.validaCampos(selecionado)) throw ve;
    }

    private void validarCategoriaTrabalhador(ValidacaoException ve) {
        if (selecionado.getCategoriaTrabalhador() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Categoria do Trabalhador na aba E-social deve ser informado!");
        }
    }

    private void validarSituacaoVinculo(ValidacaoException ve) {
        if (!selecionado.temSituacaoVinculoFP()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Situação do Vínculo deve ser informado!");
        }
    }

    private void validarHierarquiaOrganizacional(ValidacaoException ve) {
        if (!selecionado.temHierarquiaOrganizacional()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Hierarquia Organizacional deve ser informado!");
        }
    }

    private void validarTipoRegimeJuridico(ValidacaoException ve) {
        if (!selecionado.temTipoDeRegime()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Regime Jurídico deve ser informado!");
        }
    }

    private void validarDescansoSemanal(ValidacaoException ve) {
        if (!selecionado.temDescansoSemanal()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descanso Semanal deve ser informado!");
        }
    }

    private void validarContaCorrenteBancaria(ValidacaoException ve) {
        if (!selecionado.temContaCorrente()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Corrente Bancária deve ser informado!");
        }
    }

    private void validarDataExercicio(ValidacaoException ve) {
        if (!selecionado.temDataExercicio()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Exercício deve ser informado!");
        }
    }

    private void validarDataNomeacao(ValidacaoException ve) {
        if (!selecionado.temDataNomeacao()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Nomeação deve ser informado!");
        }
    }

    private void validarModalidadeContratoFP(ValidacaoException ve) {
        if (!selecionado.temModalidadeContrato()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Modalidade de Contrato deve ser informado!");
        }
    }

    private void validarNumeroContratoFP(ValidacaoException ve) {
        if (!selecionado.temNumeroContrato()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número do Contrato deve ser informado!");
        }
    }

    private void validarMatriculaFP(ValidacaoException ve) {
        if (!selecionado.temMatriculaFP()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Matrícula FP deve ser informado!");
        }
    }

    private void validarSicap(ValidacaoException ve) {
        if (selecionado.getTipoVinculoSicap() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Vínculo SICAP na aba Informações das Declarações deve ser informado!");
        }
    }

    private void validarBBPrev(ValidacaoException ve) {
        if (selecionado.getSituacaoAdmissaoBBPrev() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Situação na Admissão na aba Informações das Declarações/BBPrev deve ser informado!");
        }
    }

    @URLAction(mappingId = "novoContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        if (tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.NOMEACAO) == null) {
            FacesUtil.addWarn("Atenção !", "O Tipo de Provimentos para Nomeação não foi cadastrado !");
        }
        tipo = Tipo.NOVO;
        previdenciaVinculoFP = new PrevidenciaVinculoFP();
        lotacaoFuncional = new LotacaoFuncional();
        sindicatoVinculoFP = new SindicatoVinculoFP();
        horarioContratoFP = new HorarioContratoFP();
        situacaoContratoFP = new SituacaoContratoFP();
        contratoVinculoDeContrato = new ContratoVinculoDeContrato();
        modalidadeContratoFPData = new ModalidadeContratoFPData();
        selecionado.setModalidadeContratoFPDatas(new ArrayList<ModalidadeContratoFPData>());
        matriculaSelecionado = null;
        arquivoRecurso = new Arquivo();
        opcaoSalarialVinculoFP = new OpcaoSalarialVinculoFP();
        selecionado.setOpcaoSalarialVinculoFP(new ArrayList<OpcaoSalarialVinculoFP>());
        selecionado.setInicioVigencia(SistemaFacade.getDataCorrente());
        previdenciaVinculoFP.setInicioVigencia(SistemaFacade.getDataCorrente());
        novoOpcaoValeTransporte();
        horarioContratoFP.setInicioVigencia(SistemaFacade.getDataCorrente());
        lotacaoFuncional.setInicioVigencia(SistemaFacade.getDataCorrente());
        sindicatoVinculoFP.setInicioVigencia(SistemaFacade.getDataCorrente());
        situacaoContratoFP.setInicioVigencia(SistemaFacade.getDataCorrente());
        sindicatoVinculoFP.setVinculoFP(selecionado);
        selecionado.setSefip(Boolean.TRUE);
        recursoFP = null;
        selecionado.setSefip(true);
        podeAlterarSituacaoFuncional = vinculoFPFacade.hasAutorizacaoEspecialRH(getSistemaControlador().getUsuarioCorrente(), TipoAutorizacaoRH.PERMITIR_ALTERAR_EXCLUIR_SITUACAO_FUNCIONAL);
        carregaFoto();

        //Insere a situação funcional
        if (selecionado.getSituacaoFuncionals().isEmpty()) {
            SituacaoFuncional situacaoFuncional = situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.ATIVO_PARA_FOLHA);
            SituacaoContratoFP situacao = new SituacaoContratoFP();
            situacao.setContratoFP(selecionado);
            situacao.setSituacaoFuncional(situacaoFuncional);
            situacao.setInicioVigencia(SistemaFacade.getDataCorrente());
            selecionado.getSituacaoFuncionals().add(situacao);
        }
        inicializaPastaGaveta();
        definirSessao();
        carregarDaSessao();
        edicaoCargo = false;
        novoCargo = false;
        carregarMatriculaSessao();
        configuracaoModalidadeContratoFP = null;
        tipoPessoaEstabelecimentoEfetivacaoAprendiz = TipoPessoa.JURIDICA;
    }

    public void novoOpcaoValeTransporte() {
        opcaoValeTransporteFP = new OpcaoValeTransporteFP();
        opcaoValeTransporteFP.setInicioVigencia(selecionado.getInicioVigencia());
    }

    public void navegarMatricula() {
        Web.limpaNavegacao();
        Web.navegacao(getUrlAtual(), "/matriculafp/novo/", selecionado, selecionado.getMatriculaFP());

    }

    private void carregarMatriculaSessao() {
        MatriculaFP mat = (MatriculaFP) Web.pegaDaSessao(MatriculaFP.class);

        if (mat != null)
            selecionado.setMatriculaFP(mat);
    }

    @URLAction(mappingId = "verContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        modalidadeContratoFPData = new ModalidadeContratoFPData();
        super.ver();
        ordenarCargos();
        pastaGavetaContratoFPSelecionado = null;
        gerarQRCode();
    }

    public void realizarTratativasParaRecuperarInformacoesPorMatriculaEnumero() {
        if (numeroMatricula == null || numeroContrato == null) {
            redireciona();
        }

        VinculoFP v = contratoFPFacade.recuperarContratoMatricula("" + numeroMatricula, "" + numeroContrato);

        if (v == null) {
            FacesUtil.addOperacaoNaoPermitida("Não foram localizadas informações para os parâmetros digitados.");
            redireciona();
            throw new ExcecaoNegocioGenerica("");
        }

        if (!(v instanceof ContratoFP)) {
            FacesUtil.addOperacaoNaoPermitida("A matrícula e número do vínculo devem ser de um servidor (ContratoFP). O vínculo informado é do tipo: '" + v.getNomeDaEntidade() + "'");
            redireciona();
            throw new ExcecaoNegocioGenerica("");
        }

        setId(v.getId());
    }

    @URLAction(mappingId = "editarContratoFPPorMatriculaEnumero", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarPorMatriculaEnumero() {
        try {
            realizarTratativasParaRecuperarInformacoesPorMatriculaEnumero();
        } catch (ExcecaoNegocioGenerica eng) {
            return;
        }
        editar();
        gerarQRCode();
    }

    @URLAction(mappingId = "verContratoFPPorMatriculaEnumero", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verPorMatriculaEnumero() {
        try {
            realizarTratativasParaRecuperarInformacoesPorMatriculaEnumero();
        } catch (ExcecaoNegocioGenerica eng) {
            return;
        }
        ver();
        getEventoPorIdentificador();
    }

    @URLAction(mappingId = "editarContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        modalidadeContratoFPData = new ModalidadeContratoFPData();
        selecionado.setHorarioContratoFPs(new ArrayList<HorarioContratoFP>());
        horarioContratoFP = new HorarioContratoFP();
        cancelarOpcaoValeTransporte();
        previdenciaVinculoFP = new PrevidenciaVinculoFP();
        opcaoSalarialVinculoFP = new OpcaoSalarialVinculoFP();
        recursoDoVinculoFPs = Lists.newArrayList();

        for (LotacaoFuncional lf : selecionado.getLotacaoFuncionals()) {
            if (!selecionado.getHorarioContratoFPs().contains(lf.getHorarioContratoFP())) {
                selecionado.getHorarioContratoFPs().add(lf.getHorarioContratoFP());
            }
        }
        situacaoContratoFP = new SituacaoContratoFP();
        lotacaoFuncional = new LotacaoFuncional();
        sindicatoVinculoFP = new SindicatoVinculoFP();

        selecionado.setHierarquiaOrganizacional(new HierarquiaOrganizacional());
        selecionado.getHierarquiaOrganizacional().setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        HierarquiaOrganizacional h = hierarquiaOrganizacionalFacadeNovo.getHierarquiaOrganizacionalPorUnidade(UtilRH.getDataOperacao(), selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        numeroSelecionado = selecionado.getNumero();
        matriculaSelecionado = selecionado.getMatriculaFP();
        podeAlterarSituacaoFuncional = vinculoFPFacade.hasAutorizacaoEspecialRH(getSistemaControlador().getUsuarioCorrente(), TipoAutorizacaoRH.PERMITIR_ALTERAR_EXCLUIR_SITUACAO_FUNCIONAL);

        if (selecionado.getEnderecoContratoTemporario() != null) {
            enderecoContratoTemporario = selecionado.getEnderecoContratoTemporario();
        }
        if (h == null) {
            selecionado.setHierarquiaOrganizacional(new HierarquiaOrganizacional());
        } else {
            selecionado.setHierarquiaOrganizacional(h);
        }
        contratoVinculoDeContrato = new ContratoVinculoDeContrato();
        arquivo = null;
        arquivoRecurso = new Arquivo();
        recursoFP = null;
        carregaFoto();
        carregarOpcoesConcurso();
        edicaoCargo = false;
        novoCargo = false;
        ordenarCargos();
        carregarRecursoDoVinculoFP();
        pastaGavetaContratoFPSelecionado = null;
        getEventoPorIdentificador();
        gerarQRCode();
        tipoPessoaEstabelecimentoEfetivacaoAprendiz = selecionado.getEstabelecContratAprendiz() != null && (selecionado.getEstabelecContratAprendiz() instanceof PessoaFisica) ? TipoPessoa.FISICA : TipoPessoa.JURIDICA;
    }

    private void carregarOpcoesConcurso() {
        if (contratoFPOriundoConcurso = selecionado.getClassificacaoInscricao() != null) {
            cargoConcurso = cargoConcursoFacade.buscarCargoComCBOsAndInscricoes(selecionado.getClassificacaoInscricao().getClassificacaoConcurso().getCargo().getId());
            concursoSelecionado = concursoFacade.buscarConcursoComCargos(selecionado.getClassificacaoInscricao().getClassificacaoConcurso().getCargo().getConcurso().getId());
        }
    }

    public void carregaFoto() {
        if (selecionado.getMatriculaFP() != null && selecionado.getMatriculaFP().getId() != null) {
            Arquivo arq = selecionado.getMatriculaFP().getPessoa().getArquivo();
            if (arq != null) {
                try {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                    for (ArquivoParte a : arquivoFacade.recuperaDependencias(arq.getId()).getPartes()) {
                        buffer.write(a.getDados());
                    }

                    InputStream is = new ByteArrayInputStream(buffer.toByteArray());
                    imagemFoto = new DefaultStreamedContent(is, arq.getMimeType(), arq.getNome());
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
                } catch (Exception ex) {
                    logger.error("Erro: ", ex);
                }
            }
        } else {
            imagemFoto = null;
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
        }
    }

    public boolean mostraImagemSilhueta() {
        try {
            return ((StreamedContent) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("imagem-foto")).getName().trim().length() <= 0;
        } catch (Exception e) {
            return true;
        }
    }

    private void atribuirNumeroContratoParaSelecionado() {
        if ((selecionado.getId() == null) && (selecionado.getMatriculaFP() != null)) {
            if (!verificaQuantidadeDeContratosVigentes()) {
                FacesUtil.addWarn("Atenção", "Não é possível cadastrar um novo contrato para este servidor, pois já possui dois contratos ativos!");
                return;
            }

            selecionado.setNumero(contratoFPFacade.retornaCodigo(selecionado.getMatriculaFP()));
        } else if (!isOperacaoNovo()) {
            if (matriculaSelecionado.equals(selecionado.getMatriculaFP())) {
                selecionado.setNumero(numeroSelecionado);
            } else {
                selecionado.setNumero(contratoFPFacade.retornaCodigo(selecionado.getMatriculaFP()));
            }
        }
    }

    public void numero(ValueChangeEvent e) {
        selecionado.setMatriculaFP((MatriculaFP) e.getNewValue());
        atribuirNumeroContratoParaSelecionado();

    }

    @Override
    public AbstractFacade getFacede() {
        return contratoFPFacade;
    }

    private boolean verificaDatasEstatutario() {
        boolean valida = true;
        if (selecionado.getDataNomeacao() != null) {
            if (selecionado.getDataPosse() != null) {
                if (DataUtil.diferencaDiasInteira(selecionado.getDataNomeacao(), selecionado.getDataPosse()) > 15 || DataUtil.diferencaDiasInteira(selecionado.getDataNomeacao(), selecionado.getInicioVigencia()) > 15 || DataUtil.diferencaDiasInteira(selecionado.getDataPosse(), selecionado.getInicioVigencia()) > 15) {
                    valida = false;
                }
            } else {
                if (DataUtil.diferencaDiasInteira(selecionado.getDataNomeacao(), selecionado.getInicioVigencia()) > 15) {
                    valida = false;
                }
            }
        }
        return valida;
    }

    private boolean verificaDatasCeletista() {
        boolean valida = true;
        if (selecionado.getDataPosse() != null) {
            if (DataUtil.diferencaDiasInteira(selecionado.getDataNomeacao(), selecionado.getDataPosse()) > 0 || DataUtil.diferencaDiasInteira(selecionado.getDataNomeacao(), selecionado.getInicioVigencia()) > 0 || DataUtil.diferencaDiasInteira(selecionado.getDataPosse(), selecionado.getInicioVigencia()) > 0) {
                valida = false;
            }
        } else {
            if (DataUtil.diferencaDiasInteira(selecionado.getDataNomeacao(), selecionado.getInicioVigencia()) > 0) {
                valida = false;
            }
        }
        return valida;
    }

    public boolean validaDataContrato() {
        boolean retorno = true;
        if (!selecionado.getModalidadeContratoFP().getDescricao().equals("CONCURSADOS")) {
            if (selecionado.getDataNomeacao() == null && selecionado.getDataPosse() != null) {
                FacesUtil.addAtencao("O campo Data de Nomeação deve ser preenchida!");
                retorno = false;
            } else {
                if (!verificaDatasCeletista() && selecionado.getDataNomeacao().before(selecionado.getDataPosse())) {
                    FacesUtil.addAtencao("Os campos Data de Nomeação, Data de Posse (caso preenchida) e Data de Exercício devem possuir a mesma data!");
                    retorno = false;
                }
            }
        }
        return retorno;
    }

    public boolean validaExigencias(boolean valida, PessoaFisica p, Cargo c) {
        boolean retorno = valida;
        if (c != null && p != null) {
            List<Habilidade> habilidadesPessoa = getHabilidadesPorPessoa(p);
            List<Habilidade> habilidadesCargo = getHabilidadesPorCargo(c);
            List<AreaFormacao> areaFormacoesPessoa = getAreasFormacaoPorPessoa(p);
            List<AreaFormacao> areaFormacoesCargo = getAreasFormacaoPorCargo(c);

            if (!habilidadesPessoa.containsAll(habilidadesCargo)) {
                FacesUtil.addOperacaoNaoPermitida("A Pessoa não possui as habilidades exigidas pelo cargo.");
                retorno = false;
            }

            if (!areaFormacoesPessoa.containsAll(areaFormacoesCargo)) {
                FacesUtil.addOperacaoNaoPermitida("A Pessoa não possui as formações exigidas pelo cargo.");
                retorno = false;
            }
        }
        return retorno;
    }

    private List<AreaFormacao> getAreasFormacaoPorCargo(Cargo c) {
        List<AreaFormacao> areaFormacoesCargo = new ArrayList<>();
        for (CargoAreaFormacao obj : c.getAreasFormacao()) {
            if (obj.getAreaFormacao() != null) {
                areaFormacoesCargo.add(obj.getAreaFormacao());
            }
        }
        return areaFormacoesCargo;
    }

    private List<AreaFormacao> getAreasFormacaoPorPessoa(PessoaFisica p) {
        List<AreaFormacao> areaFormacoesPessoa = new ArrayList<>();
        for (MatriculaFormacao obj : p.getFormacoes()) {
            if (obj.getFormacao() != null && obj.getFormacao().getAreaFormacao() != null) {
                areaFormacoesPessoa.add(obj.getFormacao().getAreaFormacao());
            }
        }
        return areaFormacoesPessoa;
    }

    private List<Habilidade> getHabilidadesPorCargo(Cargo c) {
        List<Habilidade> habilidadesCargo = new ArrayList<>();
        for (CargoHabilidade obj : c.getHabilidades()) {
            habilidadesCargo.add(obj.getHabilidade());
        }
        return habilidadesCargo;
    }

    private List<Habilidade> getHabilidadesPorPessoa(PessoaFisica p) {
        List<Habilidade> habilidadesPessoa = new ArrayList<>();
        for (PessoaHabilidade obj : p.getHabilidades()) {
            habilidadesPessoa.add(obj.getHabilidade());
        }
        return habilidadesPessoa;
    }

    public ConverterGenerico getConverterTipoPrevidencia() {
        if (converterTipoPrevidencia == null) {
            converterTipoPrevidencia = new ConverterGenerico(TipoPrevidenciaFP.class, tipoPrevidenciaFPFacade);
        }
        return converterTipoPrevidencia;
    }

    public ConverterGenerico getConverterOcorrenciaSEFIP() {
        if (converterOcorrenciaSEFIP == null) {
            converterOcorrenciaSEFIP = new ConverterGenerico(OcorrenciaSEFIP.class, ocorrenciaSEFIPFacade);
        }
        return converterOcorrenciaSEFIP;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public void actionListenerHO(SelectEvent evt) {
        hierarquiaOrganizacionalEnquadramento = ((HierarquiaOrganizacional) evt.getObject());
    }

    public List<PrevidenciaVinculoFP> getListaPrevidencia() {
        return selecionado.getPrevidenciaVinculoFPs();
    }

    public List<SelectItem> getMatriculaFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (MatriculaFP object : matriculaFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getMatricula()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoPrevidenciaFp() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPrevidenciaFP object : tipoPrevidenciaFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public Converter getConverterMatriculaFP() {
        if (converterMatriculaFP == null) {
            converterMatriculaFP = new ConverterAutoComplete(MatriculaFP.class, matriculaFPFacade);
        }
        return converterMatriculaFP;
    }

    public List<Cargo> completaCargos(String parte) {
        return cargoFacade.buscarCargosVigentesPorUnidadeOrganizacionalAndUsuario(parte.trim());
    }

    public List<Cargo> completaCargosContrato(String parte) {
        return cargoFacade.buscarCargosVigentesPorUnidadesUsuarioAndUnidadeOrganizacional(parte.trim(), selecionado != null && selecionado.temHierarquiaOrganizacional() ? selecionado.getHierarquiaOrganizacional().getSubordinada().getId() : null, null);
    }

    public ConverterAutoComplete getConverterCargo() {
        return new ConverterAutoComplete(Cargo.class, cargoFacade);
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacadeNovo.listaFiltrandoPorOrgaoAndTipoAdministrativa(parte.trim());
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalLotacaoFuncional(String parte) {
        return hierarquiaOrganizacionalFacadeNovo.buscarHierarquiaFiltrandoTipoAdministrativaAndHierarquiaSemRaiz(parte.trim());
    }

    public List<SelectItem> getHierarquias() {
        List<SelectItem> toReturn = new ArrayList<>();
        List<HierarquiaOrganizacional> listaHierarquiasAdministrativasVigentes = hierarquiaOrganizacionalFacadeNovo.getListaHierarquiasAdministrativasVigentes(UtilRH.getDataOperacao());
        Collections.sort(listaHierarquiasAdministrativasVigentes);
        for (HierarquiaOrganizacional hierarquiaOrganizacional : listaHierarquiasAdministrativasVigentes) {
            toReturn.add(new SelectItem(hierarquiaOrganizacional, hierarquiaOrganizacional.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getJornadaDeTrabalho() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (JornadaDeTrabalho object : jornadaDeTrabalhoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterJornadaDeTrabalho() {
        if (converterJornadaDeTrabalho == null) {
            converterJornadaDeTrabalho = new ConverterGenerico(JornadaDeTrabalho.class, jornadaDeTrabalhoFacade);
        }
        return converterJornadaDeTrabalho;
    }

    public List<SelectItem> getModalidadeContratoServidor() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (ModalidadeContratoFP object : modalidadeContratoServidorFacade.modalidadesAtivas()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterModalidadeContratoServidor() {
        if (converterModalidadeContratoServidor == null) {
            converterModalidadeContratoServidor = new ConverterGenerico(ModalidadeContratoFP.class, modalidadeContratoServidorFacade);
        }
        return converterModalidadeContratoServidor;
    }

    public ConverterGenerico getConverterHorarioContratoFP() {
        if (converterHorarioContratoFP == null) {
            converterHorarioContratoFP = new ConverterGenerico(HorarioContratoFP.class, horarioContratoFPFacade);
        }
        return converterHorarioContratoFP;
    }

    public List<SelectItem> getHorarioDeTrabalho() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (HorarioDeTrabalho object : horarioDeTrabalhoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterHorarioDeTrabalho() {
        if (converterHorarioDeTrabalho == null) {
            converterHorarioDeTrabalho = new ConverterGenerico(HorarioDeTrabalho.class, horarioDeTrabalhoFacade);
        }
        return converterHorarioDeTrabalho;
    }

    public ConverterGenerico getConverterUf() {
        if (converterUf == null) {
            converterUf = new ConverterGenerico(UF.class, uFFacade);
        }
        return converterUf;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void adicionarLotacaoFuncional() {
        try {
            lotacaoFuncional.setDataRegistro(new Date());
            validarLotacaoFuncional();
            validarRecursoFP();
            validarLotacaoFuncionalVigencia();

            lotacaoFuncional.setUnidadeOrganizacional(hierarquiaOrganizacionalEnquadramento.getSubordinada());
            lotacaoFuncional.setVinculoFP(selecionado);
            adicionarRecursoDoVinculoFP(selecionado, recursoFP, lotacaoFuncional.getInicioVigencia(), lotacaoFuncional.getFinalVigencia());
            selecionado.getLotacaoFuncionals().add(lotacaoFuncional);
            lotacaoFuncional = new LotacaoFuncional();
            lotacaoFuncional.setInicioVigencia(selecionado.getInicioVigencia());
            hierarquiaOrganizacionalEnquadramento = new HierarquiaOrganizacional();
            tipo = Tipo.PRINCIPAL;

        } catch (ValidacaoException val) {
            if (val.temMensagens()) {
                FacesUtil.printAllFacesMessages(val.getMensagens());
            } else {
                FacesUtil.addAtencao(val.getMessage());
            }
        } catch (ExcecaoNegocioGenerica e) {
            logger.error("Erro", e);
        } catch (Exception e) {
            logger.error("Erro", e);
        }

    }

    public void validarLotacaoFuncional() {
        ValidacaoException val = new ValidacaoException();
        if (hierarquiaOrganizacionalEnquadramento == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("Selecione uma Hierarquia Organizacional Administrativa!");
        }
        lancar(val);
    }

    public void validarLotacaoFuncionalVigencia() {
        ValidacaoException val = new ValidacaoException();
        if (lotacaoFuncional.getInicioVigencia() == null) {
            val.adicionarMensagemDeCampoObrigatorio("O início de vigência da lotação funcional deve ser preenchido.");
        }
        for (LotacaoFuncional p : selecionado.getLotacaoFuncionals()) {
            if (p.getHorarioContratoFP().equals(lotacaoFuncional.getHorarioContratoFP())) {
                if (p.getFinalVigencia() != null && lotacaoFuncional.getInicioVigencia() != null) {
                    if (p.getFinalVigencia().after(lotacaoFuncional.getInicioVigencia())) {
                        val.adicionarMensagemDeOperacaoNaoPermitida("A data Final da vigência anterior não pode ser inferior a data inicial da nova vigência!");
                    }
                } else {
                    val.adicionarMensagemDeOperacaoNaoPermitida("A vigência da lista contem registro(s) com vigência aberta!");

                }
            }
        }
        lancar(val);
        if (lotacaoFuncional.getFinalVigencia() != null && lotacaoFuncional.getInicioVigencia().after(lotacaoFuncional.getFinalVigencia())) {
            val.adicionarMensagemDeOperacaoNaoPermitida("O final de Vigência não pode ser menor que o inicio da vigência.");
        }
        lancar(val);
    }

    public void lancar(ValidacaoException val) {
        if (val.temMensagens()) {
            throw val;
        }
    }

    public void validarRecursoFP() {
        ValidacaoException val = new ValidacaoException();
        if (recursoFP == null && isOperacaoNovo()) {
            val.adicionarMensagemDeOperacaoNaoRealizada("Selecione um Recurso FP(Lotação Pagamento)");
        }
        lancar(val);
    }

    private void adicionarRecursoDoVinculoFP(ContratoFP contrato, RecursoFP recurso, Date inicioVigencia, Date finalVigencia) {
        if (recurso != null) {
            RecursoDoVinculoFP rv = new RecursoDoVinculoFP();
            rv.setDataRegistro(new Date());
            rv.setVinculoFP(contrato);
            rv.setRecursoFP(recurso);
            rv.setInicioVigencia(inicioVigencia);
            rv.setFinalVigencia(finalVigencia);
            selecionado.getRecursosDoVinculoFP().add(rv);
        }
    }

    public void associa(HorarioContratoFP e) {
        lotacaoFuncional.setHorarioContratoFP(e);
        hierarquiaOrganizacionalEnquadramento = new HierarquiaOrganizacional();
        tipo = Tipo.LOCAL;
    }

    public void addHorarioContrato() {
        if (horarioContratoFP.getInicioVigencia() != null) {
            if (!horarioJaUsado(horarioContratoFP, selecionado.getMatriculaFP())) {
                for (HorarioContratoFP p : selecionado.getHorarioContratoFPs()) {
                    if (p.getFinalVigencia() != null) {
                        if (p.getFinalVigencia().after(horarioContratoFP.getInicioVigencia())) {
                            FacesUtil.addWarn("Atenção", "A data Final da vigência anterior não pode ser inferior a data inicial da nova vigência!");
                            return;
                        }
                    } else {
                        FacesUtil.addWarn("Atenção", "a vigência da lista contem registro(s) com vigência aberta!");
                        return;
                    }
                }
                if (horarioContratoFP.getFinalVigencia() != null && horarioContratoFP.getInicioVigencia().after(horarioContratoFP.getFinalVigencia())) {
                    FacesUtil.addWarn("Atenção", "O final de Vigência não pode ser menor que o inicio da vigência.");
                    return;
                }
                if (horarioContratoFP.getHorarioDeTrabalho() == null) {
                    FacesUtil.addWarn("Atenção", "Selecione o horário de trabalho.");
                    return;
                }
                selecionado.getHorarioContratoFPs().add(horarioContratoFP);
                horarioContratoFP = new HorarioContratoFP();
                horarioContratoFP.setInicioVigencia(selecionado.getInicioVigencia());
            }
        }
    }

    public HierarquiaOrganizacional getHierarquiaVigentePorHorario(HorarioContratoFP horario) {
        List<LotacaoFuncional> lotacoes = new ArrayList<>();
        for (LotacaoFuncional lotacao : selecionado.getLotacaoFuncionals()) {
            if (lotacao.getHorarioContratoFP() != null && lotacao.getHorarioContratoFP().equals(horario)) {
                lotacoes.add(lotacao);
            }
        }
        Collections.sort(lotacoes, new Comparator<LotacaoFuncional>() {
            @Override
            public int compare(LotacaoFuncional o1, LotacaoFuncional o2) {
                return (o1.getFinalVigencia() == null ? new Date() : o1.getFinalVigencia()).compareTo((o2.getFinalVigencia() == null ? new Date() : o2.getFinalVigencia()));
            }
        });
        if (!lotacoes.isEmpty()) {
            return hierarquiaPai(lotacoes.get(0));
        }
        return null;
    }

    public List<LotacaoFuncional> lotacaoFuncionais(HorarioContratoFP horario) {
        List<LotacaoFuncional> lotacoes = new ArrayList<>();
        for (LotacaoFuncional lotacao : selecionado.getLotacaoFuncionals()) {
            if (lotacao.getHorarioContratoFP().equals(horario)) {
                lotacoes.add(lotacao);
            }
        }
        return lotacoes;
    }

    public HierarquiaOrganizacional hierarquiaPai(LotacaoFuncional lotacaoFuncional) {
        return hierarquiaOrganizacionalFacadeNovo.recuperaHierarquiaOrganizacionalPelaUnidadeUltima(lotacaoFuncional.getUnidadeOrganizacional().getId());
    }

    public void removerLotacaoFuncionalFP(ActionEvent e) {
        LotacaoFuncional lotacao = (LotacaoFuncional) e.getComponent().getAttributes().get("objeto");
        if (lotacao.getId() == null || contratoFPFacade.podeExcluir(selecionado, lotacao.getInicioVigencia())) {
            if (selecionado.getLotacaoFuncionals().contains(lotacao)) {
                selecionado.getLotacaoFuncionals().remove(lotacao);
            }
            lotacaoFuncional = new LotacaoFuncional();
        } else {
            FacesUtil.addWarn("Atenção", "Não é possível excluir, já existe folha calculada para a vigência!");
        }
    }

    public void removerLotacaoRecursoFP(RecursoDoVinculoFP e) {
        if (e.getId() == null || contratoFPFacade.podeExcluir(selecionado, e.getInicioVigencia())) {
            if (selecionado.getRecursosDoVinculoFP().contains(e)) {
                selecionado.getRecursosDoVinculoFP().remove(e);
            }
            if (recursoDoVinculoFPs != null && recursoDoVinculoFPs.contains(e)) {
                recursoDoVinculoFPs.remove(e);
            }
        } else {
            FacesUtil.addWarn("Atenção", "Não é possível excluir, já existe folha calculada para a vigência!");
        }
    }

    public void removePrevidencia(ActionEvent e) {
        PrevidenciaVinculoFP previ = (PrevidenciaVinculoFP) e.getComponent().getAttributes().get("objeto");
        if (contratoFPFacade.podeExcluir(selecionado, previ.getInicioVigencia())) {
            selecionado.getPrevidenciaVinculoFPs().remove(previ);
            previdenciaArquivos = new ArrayList<>();
        } else {
            FacesUtil.addWarn("Atenção", "Não é possível excluir, já existe folha calculada para a vigência!");
        }
    }

    public void editarPrevidencia(PrevidenciaVinculoFP previdencia) {
        previdenciaVinculoFP = (PrevidenciaVinculoFP) Util.clonarObjeto(previdencia);
    }

    public void editarOpcaoValeTransporte(OpcaoValeTransporteFP opcaoValeTransporteFP) {
        this.opcaoValeTransporteFP = (OpcaoValeTransporteFP) Util.clonarObjeto(opcaoValeTransporteFP);
    }

    public void removeOpcaoValeTransporte(OpcaoValeTransporteFP opcaoValeTransporteFP) {
        selecionado.getOpcaoValeTransporteFPs().remove(opcaoValeTransporteFP);
    }

    public void removeHorarioCotratoFP(ActionEvent e) {
        horarioContratoFP = (HorarioContratoFP) e.getComponent().getAttributes().get("objeto");
        if (contratoFPFacade.podeExcluir(selecionado, horarioContratoFP.getInicioVigencia())) {
            removerLotacaoesDoHorario(horarioContratoFP);
            selecionado.getHorarioContratoFPs().remove(horarioContratoFP);
            horarioContratoFP = new HorarioContratoFP();
        } else {
            FacesUtil.addWarn("Atenção", "Não é possível excluir, já existe folha calculada para a vigência!");
        }
    }

    private void removerLotacaoesDoHorario(HorarioContratoFP horarioContratoFP) {
        List<LotacaoFuncional> lotacaoFuncionals = selecionado.getLotacaoFuncionals();
        List<LotacaoFuncional> aRemover = Lists.newArrayList();
        for (LotacaoFuncional funcional : lotacaoFuncionals) {
            if (funcional.getHorarioContratoFP().equals(horarioContratoFP)) {
                aRemover.add(funcional);
            }
        }
        if (!aRemover.isEmpty()) {
            for (LotacaoFuncional funcional : aRemover) {
                funcional.setHorarioContratoFP(null);
                selecionado.getLotacaoFuncionals().remove(funcional);
            }
        }

    }

    public List<HierarquiaOrganizacional> completaUnidade(String parte) {
        return hierarquiaOrganizacionalFacadeNovo.filtrandoHierarquiaOrganizacionalTipo(parte.trim(), "ADMINISTRATIVA", UtilRH.getDataOperacao());
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public ConverterGenerico getConverterConta() {
        if (converterContaCorrenteBancaria == null) {
            converterContaCorrenteBancaria = new ConverterGenerico(ContaCorrenteBancaria.class, contaCorrenteBancariaFacade);
        }
        return converterContaCorrenteBancaria;
    }

    public List<SelectItem> getContasCorrentesBancarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getMatriculaFP() != null) {
            for (ContaCorrenteBancaria object : contaCorrenteBancPessoaFacade.listaContasPorPessoaFisica(selecionado.getMatriculaFP().getPessoa())) {
                toReturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toReturn;
    }

    public List<ContaCorrenteBancaria> completaContaCorrente(String parte) {
        return contaCorrenteBancariaFacade.listaContasPorPessoaFisica(matriculaSelecionado.getPessoa());
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return pessoaFisicaFacade.listaFiltrando(parte.trim(), "nome");
    }

    public Converter getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, pessoaFisicaFacade);
        }
        return converterPessoaFisica;
    }

    public List<MatriculaFP> completaMatriculaFP(String parte) {
        return matriculaFPFacade.recuperaMatriculaFiltroPessoa(parte.trim());
    }

    public HorarioContratoFP getHorarioContratoFP() {
        return horarioContratoFP;
    }

    public void setHorarioContratoFP(HorarioContratoFP horarioContratoFP) {
        this.horarioContratoFP = horarioContratoFP;
    }

    public void adicionarPrevidenciaVinculoFP() {
        if (validaCamposPrevidencia() && validarVigenciaPrevidenciaVinculoFP()) {
            previdenciaVinculoFP.setPrevidenciaArquivos(new ArrayList<PrevidenciaArquivo>());
            if (!previdenciaArquivos.isEmpty()) {
                previdenciaVinculoFP.getPrevidenciaArquivos().addAll(previdenciaArquivos);
            }
            if (isEdicaoPrevidencia()) {
                Util.adicionarObjetoEmLista(selecionado.getPrevidenciaVinculoFPs(), previdenciaVinculoFP);
                setEdicaoPrevidencia(false);
            } else {
                previdenciaVinculoFP.setContratoFP(selecionado);
                selecionado.getPrevidenciaVinculoFPs().add(previdenciaVinculoFP);
            }
            previdenciaVinculoFP = new PrevidenciaVinculoFP();
            previdenciaArquivos = new ArrayList<>();
            if (isOperacaoNovo()) {
                previdenciaVinculoFP.setInicioVigencia(selecionado.getInicioVigencia());
            }
            RequestContext.getCurrentInstance().update("Formulario:tab:panelPrevidencia");
        }
    }

    public boolean validaCamposPrevidencia() {
        boolean retorno = true;
        if (previdenciaVinculoFP.getInicioVigencia() == null) {
            FacesUtil.addWarn("Atenção", "Informe a data inicial da vigência!");
            retorno = false;
        } else if (previdenciaVinculoFP.getFinalVigencia() != null) {
            if (previdenciaVinculoFP.getFinalVigencia().before(previdenciaVinculoFP.getInicioVigencia())) {
                FacesUtil.addWarn("Atenção", "A data final da vigência não pode ser inferior a data inicial da vigência!");
                retorno = false;
            }
        }
        if (previdenciaVinculoFP.getTipoPrevidenciaFP() == null) {
            FacesUtil.addWarn("Atenção", "O tipo de previdência é obrigatório.");
            retorno = false;
        }
        return retorno;
    }

    private Boolean validarVigenciaPrevidenciaVinculoFP() {
        for (PrevidenciaVinculoFP p : selecionado.getPrevidenciaVinculoFPs()) {
            if (p.getFinalVigencia() != null && !p.equals(previdenciaVinculoFP)) {
                if (previdenciaVinculoFP.getInicioVigencia().compareTo(p.getInicioVigencia()) > 0
                    && previdenciaVinculoFP.getInicioVigencia().compareTo(p.getFinalVigencia()) < 0) {
                    FacesUtil.addWarn("Atenção", "A data de início  da vigência selecionada está entre uma vigência já estabelecida: de "
                        + DataUtil.getDataFormatada(p.getInicioVigencia()) + " à " + DataUtil.getDataFormatada(p.getFinalVigencia()));
                    return false;
                }
                if (previdenciaVinculoFP.getFinalVigencia() != null && previdenciaVinculoFP.getFinalVigencia().compareTo(p.getInicioVigencia()) > 0
                    && previdenciaVinculoFP.getFinalVigencia().compareTo(p.getFinalVigencia()) < 0) {
                    FacesUtil.addWarn("Atenção", "A data final  da vigência selecionada está entre uma vigência já estabelecida: de "
                        + DataUtil.getDataFormatada(p.getInicioVigencia()) + " à " + DataUtil.getDataFormatada(p.getFinalVigencia()));
                    return false;
                }
            } else if (!isEdicaoPrevidencia()) {
                FacesUtil.addWarn("Atenção", " Vigência da lista contém registro com vigência aberta!");
                return false;
            }
        }
        return true;
    }

    public void adicionarValeTransporte() {
        try {
            opcaoValeTransporteFP.setContratoFP(selecionado);
            validarCamposValeTransporte();
            Util.adicionarObjetoEmLista(selecionado.getOpcaoValeTransporteFPs(), opcaoValeTransporteFP);
            cancelarOpcaoValeTransporte();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void cancelarOpcaoValeTransporte() {
        opcaoValeTransporteFP = null;
    }

    private void validarCamposValeTransporte() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(opcaoValeTransporteFP, ve);
        if (opcaoValeTransporteFP.getFinalVigencia() != null && opcaoValeTransporteFP.getInicioVigencia().after(opcaoValeTransporteFP.getFimVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Final da Vigência deve ser maior ou igual ao Início da Vigência!");
        }
        ve.lancarException();
        for (OpcaoValeTransporteFP p : opcaoValeTransporteFP.getContratoFP().getOpcaoValeTransporteFPs()) {
            if (p.getFinalVigencia() != null) {
                if (!p.equals(opcaoValeTransporteFP) && p.getFinalVigencia().after(opcaoValeTransporteFP.getInicioVigencia())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A data Final da vigência anterior não pode ser superior a data inicial da nova vigência!");
                }
            } else if (!p.equals(opcaoValeTransporteFP)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato selecionado contem registro(s) com vigência aberta!");
            }
        }
        ve.lancarException();
    }

    public MatriculaFP getMatriculaSelecionado() {
        return matriculaSelecionado;
    }

    public SindicatoVinculoFP getSindicatoVinculoFP() {
        return sindicatoVinculoFP;
    }

    public void setSindicatoVinculoFP(SindicatoVinculoFP sindicatoVinculoFP) {
        this.sindicatoVinculoFP = sindicatoVinculoFP;
    }

    public AssociacaoVinculoFP getAssociacaoVinculoFP() {
        return associacaoVinculoFP;
    }

    public void setAssociacaoVinculoFP(AssociacaoVinculoFP associacaoVinculoFP) {
        this.associacaoVinculoFP = associacaoVinculoFP;
    }

    public Converter getConverterCbo() {
        return new ConverterAutoComplete(CBO.class, cboFacade);
    }

    public List<CBO> completaCbo(String parte) {
        if (contratoFPCargoSelecionado.getCargo() == null) {
            FacesUtil.addOperacaoNaoPermitida("É necessário informar um cargo no campo anterior para poder listar os CBOs.");
            return null;
        }
        return cboFacade.listarFiltrandoPorCargo(parte.trim(), contratoFPCargoSelecionado.getCargo());
    }

    public ConverterGenerico getConverterSindicato() {
        if (converterSindicato == null) {
            converterSindicato = new ConverterGenerico(Sindicato.class, sindicatoFacade);
        }
        return converterSindicato;
    }

    public void addSindicatoVinculoFP() {
        if (sindicatoVinculoFP.getInicioVigencia() != null && sindicatoVinculoFP.getFinalVigencia() != null) {
            if (sindicatoVinculoFP.getInicioVigencia().after(sindicatoVinculoFP.getFinalVigencia())) {
                FacesUtil.addWarn("Atenção", "A data Final da vigência não pode ser inferior a data inicial da vigência !");
            } else if (sindicatoVinculoFP.getSindicato() == null) {
                FacesUtil.addWarn("Atenção", "Selecione um sindicato para poder incluir !");
            } else {
                adicionaSindicato();
            }
        } else {
            if (sindicatoVinculoFP.getInicioVigencia() == null) {
                FacesUtil.addWarn("Atenção", "Insira pelo menos o inicio da vigência!");
            } else if (sindicatoVinculoFP.getSindicato() == null) {
                FacesUtil.addWarn("Atenção", "Selecione um sindicato para poder incluir !");
            } else {
                adicionaSindicato();
            }
        }
    }

    private void validarModalidadeContratoFPParaSalvar() {
        ValidacaoException ve = new ValidacaoException();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (modalidadeContratoFPData.getInicioVigencia() != null || modalidadeContratoFPData.getModalidadeContratoFP() != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Atenção - Existem alterações não salvas! Modalidade de Contrato: " + modalidadeContratoFPData.getModalidadeContratoFP()
                + ", com Início de Vigência: " + sdf.format(modalidadeContratoFPData.getInicioVigencia()) + ", está em edição!");
        }
        ve.lancarException();
    }

    private void validarEditarModalidadeContratoFP(ModalidadeContratoFPData modalidadeContratoFPData) {
        ValidacaoException ve = new ValidacaoException();
        this.modalidadeContratoFPData = (ModalidadeContratoFPData) Util.clonarObjeto(modalidadeContratoFPData);
        if (this.modalidadeContratoFPData.getFinalVigencia() != null) {
            this.modalidadeContratoFPData = new ModalidadeContratoFPData();
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possivel editar Modalidades de Contrato com Final da Vigência definida");
        }
        ve.lancarException();
    }

    private void editarModalidade(ModalidadeContratoFPData modalidadeContratoFPData) {
        this.modalidadeContratoFPData = (ModalidadeContratoFPData) Util.clonarObjeto(modalidadeContratoFPData);
        selecionado.getModalidadeContratoFPDatas().remove(modalidadeContratoFPData);
    }

    public void editarModalidadeContratoFP(ModalidadeContratoFPData modalidadeContratoFPData) {
        this.modalidadeContratoFPData = (ModalidadeContratoFPData) Util.clonarObjeto(modalidadeContratoFPData);
        try {
            validarEditarModalidadeContratoFP(this.modalidadeContratoFPData);
            editarModalidade(this.modalidadeContratoFPData);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void editarPrevidenciaVinculoFP(PrevidenciaVinculoFP previdencia) {
        previdenciaArquivos = new ArrayList<>();
        previdenciaArquivos.addAll(previdencia.getPrevidenciaArquivos());
        previdenciaVinculoFP = (PrevidenciaVinculoFP) Util.clonarObjeto(previdencia);
        setEdicaoPrevidencia(true);
    }

    public void limparPrevidenciaVinculoFP() {
        previdenciaArquivos = new ArrayList<>();
        previdenciaVinculoFP = new PrevidenciaVinculoFP();
        setEdicaoPrevidencia(false);
    }

    private void validarModalidadeContratoFP() {
        ValidacaoException ve = new ValidacaoException();
        if (modalidadeContratoFPData.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Atenção - Insira pelo menos o inicio da vigência!");
        }
        if (modalidadeContratoFPData.getModalidadeContratoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Atenção - Selecione uma Modalidade de Contrato para adicionar");
        }
        if (modalidadeContratoFPData.getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Atenção - Selecione um Ato Legal para adicionar");
        }
        if (modalidadeContratoFPData.getFinalVigencia() == null) {
            for (ModalidadeContratoFPData m : selecionado.getModalidadeContratoFPDatas()) {
                if (m.getFinalVigencia() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Atenção - Existe vigência aberta, não é possivel criar outra enquanto a antiga não for encerrada!");
                    break;
                } else if (m.getFinalVigencia().after(modalidadeContratoFPData.getInicioVigencia())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Atenção - A data Inicial da nova vigência não pode ser inferior a data Final da vigência anterior!");
                    break;
                }
            }
        }
        if (modalidadeContratoFPData.getInicioVigencia() != null && modalidadeContratoFPData.getFinalVigencia() != null) {
            if (modalidadeContratoFPData.getInicioVigencia().after(modalidadeContratoFPData.getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Atenção - A data Final da vigência não pode ser inferior a data Inicial da vigência!");
            }
            for (ModalidadeContratoFPData m : selecionado.getModalidadeContratoFPDatas()) {
                if (m.getFinalVigencia() != null) {
                    if (m.getFinalVigencia().after(modalidadeContratoFPData.getInicioVigencia())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Atenção - A data Inicial da nova vigência não pode ser inferior a data Final da vigência anterior!");
                        break;
                    }
                } else if (m.getFinalVigencia() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Atenção - Já existe vigência aberta, não é possivel criar outra enquanto a anterior não foir encerrada.");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarModalidadesContratosServidores() {
        try {
            validarModalidadeContratoFP();
            buscaConfiguracaoModalidade();
            adicionarModalidadeContratoServidor();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void retornarVigenciaAtualModalidadeContratoFP() {
        ModalidadeContratoFPData novaModalidade = null;
        for (ModalidadeContratoFPData m : selecionado.getModalidadeContratoFPDatas()) {
            if (novaModalidade == null) {
                novaModalidade = m;
            }
            if (m.getFinalVigencia() == null) {
                novaModalidade = m;
                selecionado.setModalidadeContratoFP(m.getModalidadeContratoFP());
                selecionado.setAtoLegal(m.getAtoLegal());
                break;
            } else {
                if (novaModalidade.getFinalVigencia().before(m.getFinalVigencia())) {
                    novaModalidade = m;
                }
            }
        }
        selecionado.setModalidadeContratoFP(novaModalidade.getModalidadeContratoFP());
        selecionado.setAtoLegal(novaModalidade.getAtoLegal());
    }

    private void adicionarModalidadeContratoServidor() {
        modalidadeContratoFPData.setContratoFP(selecionado.getContratoFP());
        selecionado.getModalidadeContratoFPDatas().add(modalidadeContratoFPData);
        modalidadeContratoFPData = new ModalidadeContratoFPData();
        retornarVigenciaAtualModalidadeContratoFP();
    }

    public void removerModalidadeContratoServidor(ModalidadeContratoFPData modalidade) {
        selecionado.getModalidadeContratoFPDatas().remove(modalidade);
        modalidadeContratoFPData = new ModalidadeContratoFPData();
        if (selecionado.getModalidadeContratoFPDatas().size() > 1) {
            retornarVigenciaAtualModalidadeContratoFP();
        }
        if (selecionado.getModalidadeContratoFPDatas().isEmpty()) {
            selecionado.setModalidadeContratoFP(null);
        }
    }

    private void adicionaSindicato() {
        if (sindicatoVinculoFP.getInicioVigencia() != null) {
            for (SindicatoVinculoFP p : selecionado.getSindicatosVinculosFPs()) {
                if (p.getFinalVigencia() != null) {
                    if (p.getFinalVigencia().after(sindicatoVinculoFP.getInicioVigencia())) {
                        FacesUtil.addWarn("Atenção", "A data Final da vigência anterior não pode ser inferior a data inicial da nova vigência!");
                        return;
                    }
                } else {
                    FacesUtil.addWarn("Atenção", "a vigência da lista contem registro(s) com vigência aberta!");
                    return;
                }
            }
            sindicatoVinculoFP.setVinculoFP(selecionado);
            selecionado.getSindicatosVinculosFPs().add(sindicatoVinculoFP);
            sindicatoVinculoFP = new SindicatoVinculoFP();
            sindicatoVinculoFP.setVinculoFP(selecionado);
        }
    }

    public void removeSindicatoVinculoFP(ActionEvent e) {
        sindicatoVinculoFP = (SindicatoVinculoFP) e.getComponent().getAttributes().get("objeto");
        if (contratoFPFacade.podeExcluir(selecionado, sindicatoVinculoFP.getInicioVigencia())) {
            selecionado.getSindicatosVinculosFPs().remove(sindicatoVinculoFP);
            sindicatoVinculoFP = new SindicatoVinculoFP();
        } else {
            FacesUtil.addWarn("Atenção", "Não é possível excluir, já existe folha calculada para a vigência!");
        }
    }

    public List<SelectItem> getSindicato() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Sindicato object : sindicatoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterAssociacao() {
        if (converterAssociacao == null) {
            converterAssociacao = new ConverterGenerico(Associacao.class, associacaoFacade);
        }
        return converterAssociacao;
    }

    public void novaAssociacao() {
        associacaoVinculoFP = new AssociacaoVinculoFP(selecionado);
        associacaoVinculoFP.setInicioVigencia(UtilRH.getDataOperacao());
    }

    public void adicionarAssociacaoVinculoFP() {
        if (podeAdicionarAssociacaoVinculoFP()) {
            adicionarAssociacao();
            setarNullAssociacao();
        }
    }

    private boolean podeAdicionarAssociacaoVinculoFP() {
        if (!Util.validaCampos(associacaoVinculoFP)) {
            return false;
        }
        if (!DataUtil.isVigenciaValida(associacaoVinculoFP, selecionado.getAssociacaosVinculosFPs())) {
            return false;
        }
        return true;
    }

    public void cancelarAssociacaoVinculoFP() {
        if (associacaoVinculoFP.estaEditando()) {
            if (DataUtil.isVigenciaValida(associacaoVinculoFP, selecionado.getAssociacaosVinculosFPs())) {
                adicionarAssociacao();
            }
        }
        setarNullAssociacao();
    }

    public void setarNullAssociacao() {
        associacaoVinculoFP = null;
    }

    public void adicionarAssociacao() {
        selecionado.setAssociacaosVinculosFPs(Util.adicionarObjetoEmLista(selecionado.getAssociacaosVinculosFPs(), associacaoVinculoFP));
    }

    public void selecionarAssociacaoVinculoFP(AssociacaoVinculoFP av) {
        associacaoVinculoFP = av;
        associacaoVinculoFP.setOperacao(Operacoes.EDITAR);
        selecionado.getAssociacaosVinculosFPs().remove(av);
    }

    public void removerAssociacaoVinculoFP(AssociacaoVinculoFP av) {
        if (contratoFPFacade.podeExcluir(selecionado, av.getInicioVigencia())) {
            selecionado.getAssociacaosVinculosFPs().remove(av);
        } else {
            FacesUtil.addWarn("Atenção", "Não é possível excluir, já existe folha calculada para a vigência!");
        }
    }

    public List<SelectItem> getAssociacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Associacao object : associacaoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getEventosFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (EventoFP eventoFP : contratoFPFacade.getEventoFPFacade().lista()) {
            toReturn.add(new SelectItem(eventoFP, eventoFP.toString()));
        }
        return toReturn;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public Converter getConverterNaturezaRendimento() {
        if (converterNaturezaRendimento == null) {
            converterNaturezaRendimento = new ConverterGenerico(NaturezaRendimento.class, naturezaRendimentoFacade);
        }
        return converterNaturezaRendimento;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(parte, PropositoAtoLegal.ATO_DE_PESSOAL, "numero", "nome");
    }

    public List<SelectItem> listaNaturezaRendimento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (NaturezaRendimento object : naturezaRendimentoFacade.lista()) {
            String valor = object.toString();
            if (valor.length() > 70) {
                valor = valor.substring(0, 69);
            }
            toReturn.add(new SelectItem(object, valor));
        }
        return toReturn;
    }

    public Converter getConverterSituacaoFuncional() {
        if (converterSituacaoFuncional == null) {
            converterSituacaoFuncional = new ConverterAutoComplete(SituacaoFuncional.class, situacaoFuncionalFacade);
        }
        return converterSituacaoFuncional;
    }

    public List<SituacaoFuncional> completaSituacoesFuncionais(String parte) {
        return situacaoFuncionalFacade.listaFiltrando(parte.trim(), "descricao");
    }


    private void validarSituacaoFuncional() {
        ValidacaoException ve = new ValidacaoException();
        if (situacaoContratoFP == null || situacaoContratoFP.getSituacaoFuncional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma Situação Funcional.");
        }

        if (situacaoContratoFP != null && situacaoContratoFP.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Início da Vigência.");
        }

        if (situacaoContratoFP != null && situacaoContratoFP.getFinalVigencia() != null && situacaoContratoFP.getInicioVigencia().after(situacaoContratoFP.getFinalVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O data de início de vigência não pode ser maior que o final de vigência.");
        }

        if (situacaoContratoFP != null && situacaoContratoFP.getInicioVigencia() != null) {
            for (SituacaoContratoFP s : selecionado.getSituacaoFuncionals()) {
                if (situacaoContratoFP.equals(s)) {
                    continue;
                }
                if (s.getFinalVigencia() != null) {
                    if (s.getFinalVigencia().after(situacaoContratoFP.getInicioVigencia())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A data Final da vigência anterior não pode ser inferior a data inicial da nova vigência!");
                        break;
                    }
                } else if (!situacaoContratoFP.equals(s)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A vigência da lista contem registro(s) com vigência aberta!");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    private void atualizarSituacaoFuncionalVigenteNoContrato() {
        SituacaoContratoFP situacaoNova = null;
        for (SituacaoContratoFP situacao : selecionado.getSituacaoFuncionals()) {
            if (situacaoNova == null) {
                situacaoNova = situacao;
            }
            if (situacao.getFinalVigencia() == null) {
                selecionado.setSituacaoFuncional(situacao.getSituacaoFuncional());
                break;
            } else {
                if (situacao.getFinalVigencia().after(situacaoNova.getFinalVigencia())) {
                    situacaoNova = situacao;
                }
            }
        }
        if (situacaoNova != null) {
            selecionado.setSituacaoFuncional(situacaoNova.getSituacaoFuncional());
        }
    }

    public void adicionarSituacaoFuncional() {
        try {
            validarSituacaoFuncional();
            situacaoContratoFP.setContratoFP(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getSituacaoFuncionals(), situacaoContratoFP);
            atualizarSituacaoFuncionalVigenteNoContrato();
            instanciaSituacaoFuncional();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removeSituacaoFuncional(SituacaoContratoFP situacao) {
        selecionado.getSituacaoFuncionals().remove(situacao);
        atualizarSituacaoFuncionalVigenteNoContrato();
        instanciaSituacaoFuncional();
    }

    public void editarSituacaoFuncional(SituacaoContratoFP situacao) {
        situacaoContratoFP = (SituacaoContratoFP) Util.clonarObjeto(situacao);
    }

    private void instanciaSituacaoFuncional() {
        situacaoContratoFP = new SituacaoContratoFP();
        situacaoContratoFP.setInicioVigencia(SistemaFacade.getDataCorrente());
    }

    @Override
    public ContratoFP getSelecionado() {
        return selecionado;
    }

    public List<Dependente> getDependentes() {
        if (matriculaSelecionado != null) {
            return dependenteFacade.dependentesPorResponsavel(matriculaSelecionado.getPessoa());
        }
        return null;
    }

    public int buscarNumeroDeVagasDisponiveisDoCargo(Cargo c) {
        return cargoFacade.recuperaTotalVagasPorCargo(c) - cargoFacade.buscarTotalVagasOcupadasPorCargo(c);
    }

    public boolean isCargoComVagasDisponiveis() {
        if (isOperacaoNovo() && selecionado.getCargo() != null) {
            if (buscarNumeroDeVagasDisponiveisDoCargo(selecionado.getCargo()) <= 0) {
                return false;
            }
            return true;
        }

        if (isOperacaoEditar()) {
            if (cargoFacade.isServidorJaVinculadoAoCargo(selecionado, selecionado.getCargo())) {
                return true;
            }

            if (buscarNumeroDeVagasDisponiveisDoCargo(selecionado.getCargo()) <= 0) {
                return false;
            }

            return true;
        }

        return false;
    }

    public List<CategoriaSEFIP> completarCategoriaSefip(String parte) {
        return categoriaSEFIPFacade.buscarCategoriaSefip(parte);
    }

    public List<SelectItem> getCategoriaSEFIP() {
        return Util.getListSelectItem(categoriaSEFIPFacade.lista());
    }

    public ConverterAutoComplete getConverterCategoriaSEFIP() {
        if (converterCategoriaSEFIP == null) {
            converterCategoriaSEFIP = new ConverterAutoComplete(CategoriaSEFIP.class, categoriaSEFIPFacade);
        }
        return converterCategoriaSEFIP;
    }

    public List<SelectItem> getTipoAdmissaoFGTS() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdmissaoFGTS object : tipoAdmissaoFGTSFacade.lista()) {
            String valor = object.toString();
            if (valor.length() > 70) {
                valor = valor.substring(0, 69);
            }
            toReturn.add(new SelectItem(object, valor));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterTipoAdmissaoFGTS() {
        if (converterTipoAdmissaoFGTS == null) {
            converterTipoAdmissaoFGTS = new ConverterGenerico(TipoAdmissaoFGTS.class, tipoAdmissaoFGTSFacade);
        }
        return converterTipoAdmissaoFGTS;
    }

    public List<SelectItem> getTipoAdmissaoSEFIPs() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdmissaoSEFIP object : tipoAdmissaoSEFIPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getOcorrenciasSEFIP() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (OcorrenciaSEFIP obj : ocorrenciaSEFIPFacade.lista()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterTipoAdmissaoSEFIP() {
        if (converterTipoAdmissaoSEFIP == null) {
            converterTipoAdmissaoSEFIP = new ConverterGenerico(TipoAdmissaoSEFIP.class, tipoAdmissaoSEFIPFacade);
        }
        return converterTipoAdmissaoSEFIP;
    }

    public List<SelectItem> getMovimentosCAGEDs() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (MovimentoCAGED object : movimentoCAGEDFacade.listaMovimentoPorTipo(TipoCAGED.ADMISSAO)) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterMovimentoCAGED() {
        if (converterMovimentoCAGED == null) {
            converterMovimentoCAGED = new ConverterGenerico(MovimentoCAGED.class, movimentoCAGEDFacade);
        }
        return converterMovimentoCAGED;
    }

    public List<SelectItem> getTipoAdmissaoRAIS() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdmissaoRAIS object : tipoAdmissaoRAISFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterTipoAdmissaoRAIS() {
        if (converterTipoAdmissaoRAIS == null) {
            converterTipoAdmissaoRAIS = new ConverterGenerico(TipoAdmissaoRAIS.class, tipoAdmissaoRAISFacade);
        }
        return converterTipoAdmissaoRAIS;
    }

    public List<SelectItem> getRetencaoIRRF() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (RetencaoIRRF object : retencaoIRRFFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterRetencaoIRRF() {
        if (converterRetencaoIRRF == null) {
            converterRetencaoIRRF = new ConverterGenerico(RetencaoIRRF.class, retencaoIRRFFacade);
        }
        return converterRetencaoIRRF;
    }

    public List<SelectItem> getVinculosEmpregaticios() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (VinculoEmpregaticio object : vinculoEmpregaticioFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getContratoVinculos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (VinculoDeContratoFP object : vinculoDeContratoFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getCodigo() + "- " + object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterVinculoEmpregaticio() {
        if (converterVinculoEmpregaticio == null) {
            converterVinculoEmpregaticio = new ConverterGenerico(VinculoEmpregaticio.class, vinculoEmpregaticioFacade);
        }
        return converterVinculoEmpregaticio;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void validaNivelEscolaridade(MatriculaFP mat, Cargo car) {
        PessoaFisica p = new PessoaFisica();
        Cargo c = new Cargo();
        if (mat != null) {
            if (mat.getId() != null) {
                p = pessoaFisicaFacade.recuperar(mat.getPessoa().getId());
            }
        }

        if (car != null) {
            if (car.getId() != null) {
                c = cargoFacade.recuperar(car.getId());
            }
        }

        if (c.getNivelEscolaridade() != null) {
            if (p.getNivelEscolaridade() != null) {
                if (p.getNivelEscolaridade().getOrdem() < c.getNivelEscolaridade().getOrdem()) {
                    FacesUtil.addWarn("Atenção", "Nivel de escolaridade da pessoa não condiz com o nivel de escolaridade minimo exigido do cargo informado!");
                }
            } else {
                FacesUtil.addWarn("Atenção", "Nivel de escolaridade da pessoa não condiz com o nivel de escolaridade minimo exigido do cargo informado!");
            }
        }
    }


    @Override
    public void excluir() {
        try {
            validarExclusaoContrato();
            getFacede().remover(selecionado);
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "O registro selecionado foi excluído com sucesso!");
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ConstraintViolationException e) {
            FacesUtil.addError("Exceção do sistema! Há Referência(s) do ContratoFP em outras tabelas do Bancos dados. Erro:" + e.getMessage(), "Há Referência(s) do ContratoFP em outras tabelas do Bancos dados. Erro:" + e.getMessage());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), e.getMessage());
        }
    }

    private void validarExclusaoContrato() {
        ValidacaoException ve = new ValidacaoException();
        List<Afastamento> afastamentos = afastamentoFacade.recuperarTodosAfastamentos(selecionado.getId());
        afastamentos.forEach(afastamento -> {
            montarMensagem(ve, "/afastamento/ver/" + afastamento.getId(), "Afastamento");
        });

        List<EnquadramentoFuncional> enquadramentos = enquadramentoFuncionalFacade.recuperarEnquadramentoContratoFP(selecionado);
        enquadramentos.forEach(enquadramento -> {
            montarMensagem(ve, "/enquadramento-funcional/ver/" + enquadramento.getId(), "Enquadramento Funcional");
        });

        List<AverbacaoTempoServico> averbacoes = averbacaoTempoServicoFacade.buscarAverbacoesTempoServicoPorContrato(selecionado);
        averbacoes.forEach(averbacao -> {
            montarMensagem(ve, "/averbacao-tempo-contribuicao/ver/" + averbacao.getId(), "Averbação de Tempo de Contribuição");
        });

        List<Beneficiario> beneficiarios = beneficiarioFacade.buscarBeneficiarioPorInstituidor(selecionado);
        beneficiarios.forEach(beneficiario -> {
            montarMensagem(ve, "/beneficiario/ver/" + beneficiario.getId(), "Instituidor do Beneficiário de Pensão Judicial Tramitada e Julgada");
        });

        List<BloqueioBeneficio> bloqueios = bloqueioBeneficioFacade.buscarBloqueiosBeneficiosPorContrato(selecionado);
        bloqueios.forEach(bloqueio -> {
            montarMensagem(ve, "/bloqueio-beneficio/ver/" + bloqueio.getId(), "Bloqueio de Benefício do Servidor");
        });

        List<CargoConfianca> cargosConfianca = cargoConfiancaFacade.recuperaCargoConfiancaContratoSemVigencia(selecionado);
        cargosConfianca.forEach(cargoConfianca -> {
            montarMensagem(ve, "/acesso-cargo-comissao/ver/" + cargoConfianca.getId(), "Acesso a Cargo de Comissão");
        });

        List<CedenciaContratoFP> cedencias = cedenciaContratoFPFacade.buscarCedenciaContratoFP(selecionado.getId());
        cedencias.forEach(cedencia -> {
            montarMensagem(ve, "/cedencia/ver/" + cedencia.getId(), "Cedência");
        });

        List<RetornoCedenciaContratoFP> retornos = retornoCedenciaContratoFPFacade.buscarRetornosCedenciasPorContrato(selecionado);
        retornos.forEach(retorno -> {
            montarMensagem(ve, "/retornocedencia/ver/" + retorno.getId(), "Retorno da Cedência");
        });

        List<Dirf> dirfs = dirfFacade.buscarDirfPorResponsavel(selecionado);
        dirfs.forEach(dirf -> {
            montarMensagem(ve, "/dirf/ver/" + dirf.getId(), "Responsável DIRF");
        });

        List<Dirf> dirfsVinculo = dirfFacade.buscarDirfPorItemVinculo(selecionado);
        dirfsVinculo.forEach(dirf -> {
            montarMensagem(ve, "/dirf/ver/" + dirf.getId(), "Servidor vinculado ao arquivo da DIRF");
        });

        List<Faltas> faltas = faltasFacade.buscarFaltasPorContratoIndependenteDeJustificativa(selecionado);
        faltas.forEach(falta -> {
            montarMensagem(ve, "/faltas/ver/" + falta.getId(), "Faltas");
        });

        List<HoraExtra> horasExtras = horaExtraFacade.buscarHorasExtrasPorContrato(selecionado);
        horasExtras.forEach(horaExtra -> {
            montarMensagem(ve, "/hora-extra/ver/" + horaExtra.getId(), "Hora Extra");
        });

        List<PenalidadeFP> penalidades = penalidadeFPFacade.buscarPenalidadePorContrato(selecionado);
        penalidades.forEach(penalidade -> {
            montarMensagem(ve, "/penalidade/ver/" + penalidade.getId(), "Penalidade");
        });

        List<Pensao> pensoes = pensaoFacade.buscarPensoesPorInstituidor(selecionado);
        pensoes.forEach(pensao -> {
            montarMensagem(ve, "/pensao/ver/" + pensao.getId(), "Instituidor de Pensão Previdenciária");
        });

        List<PeriodoAquisitivoFL> periodos = periodoAquisitivoFLFacade.recuperaPeriodosPorContrato(selecionado);
        if (!periodos.isEmpty()) {
            montarMensagem(ve, "/periodo-aquisitivo/consultar/?contrato=" + selecionado.getId(), "Período Aquisitivo");
        }

        List<Reintegracao> reintegracoes = reintegracaoFacade.buscarReintegracoesPorContrato(selecionado);
        reintegracoes.forEach(reintegracao -> {
            montarMensagem(ve, "/reintegracao/ver/" + reintegracao.getId(), "Reintegração do Servidor");
        });

        List<ResponsavelTecnicoFiscal> fiscais = responsavelTecnicoFiscalFacade.buscarResponsaveisTecnicosFiscaisPorServidor(selecionado);
        fiscais.forEach(fiscal -> {
            montarMensagem(ve, "/fiscal/ver/" + fiscal.getId(), "Fiscal");
        });

        List<FuncaoGratificada> funcoesGratificadas = funcaoGratificadaFacade.listaFuncoesGratificadasTodas(selecionado);
        funcoesGratificadas.forEach(funcao -> {
            montarMensagem(ve, "/funcao-gratificada/ver/" + funcao.getId(), "Função Gratificada");
        });

        List<RetornoFuncaoGratificada> retornosFuncaoGratificada = retornoFuncaoGratificadaFacade.buscarRetornosFuncaoGratificadaPorContrato(selecionado);
        retornosFuncaoGratificada.forEach(retornoFuncaoGratificada -> {
            montarMensagem(ve, "/encerramento-funcao-gratificada/ver/" + retornoFuncaoGratificada.getId(), "Encerramento Função Gratificada");
        });

        List<AssentamentoFuncional> assentamentosFuncionais = assentamentoFuncionalFacade.buscarAssentamentosFuncionaisPorContrato(selecionado);
        assentamentosFuncionais.forEach(assentamentoFuncional -> {
            montarMensagem(ve, "/assentamento-funcional/ver/" + assentamentoFuncional.getId(), "Assentamento Funcional");
        });

        List<AvisoPrevio> avisos = avisoPrevioFacade.getAvisoPrevioPorContratoFP(selecionado);
        avisos.forEach(avisoPrevio -> {
            montarMensagem(ve, "/aviso-previo/ver/" + avisoPrevio.getId(), "Aviso Prévio");
        });

        List<SolicitacaoAfastamentoPortal> solicitacoes = solicitacaoAfastamentoPortalFacade.buscarSolicitacoesAfastamentoPortalPorContrato(selecionado);
        solicitacoes.forEach(solicitacaoAfastamentoPortal -> {
            montarMensagem(ve, "/solicitacao-afastamento/ver/" + solicitacaoAfastamentoPortal.getId(), "Solicitação de Afastamento");
        });

        List<AlteracaoLocalTrabalhoPorLote> alteracoesLocalTrabalhoPorLote = alteracaoLocalTrabalhoPorLoteFacade.buscarAlteracoesLocalTrabalhoPorLotePorVinculo(selecionado);
        alteracoesLocalTrabalhoPorLote.forEach(alteracaoLocalTrabalhoPorLote -> {
            montarMensagem(ve, "/transferencias-servidor-por-lote/ver/" + alteracaoLocalTrabalhoPorLote.getId(), "Transferência de Servidor por Lote");
        });

        List<BloqueioEventoFP> bloqueiosEventoFP = bloqueioEventoFPFacade.buscarBloqueiosEventoFPPorVinculoFP(selecionado);
        bloqueiosEventoFP.forEach(bloqueioEventoFP -> {
            montarMensagem(ve, "/bloqueio-eventofp/ver/" + bloqueioEventoFP.getId(), "Bloqueio Evento FP por Servidor");
        });

        List<ExperienciaExtraCurricular> experiencias = experienciaExtraCurricularFacade.buscarTodasExperienciasExtraCurricularesPorContrato(selecionado);
        experiencias.forEach(experiencia -> {
            montarMensagem(ve, "/experiencia-extra-curricular/ver/" + experiencia.getId(), "Experiência Extracurricular");
        });

        List<FichaFinanceiraFP> fichas = fichaFinanceiraFPFacade.recuperaFichaFinanceiraFPPorVinculo(selecionado.getId());
        fichas.forEach(fichaFinanceiraFP -> {
            String link = "/consultafichafinanceira/novo/?contrato=" + selecionado.getId() +
                "&mes=" + fichaFinanceiraFP.getFolhaDePagamento().getMes().getNumeroMes() +
                "&ano=" + fichaFinanceiraFP.getFolhaDePagamento().getAno() +
                "&versao=" + fichaFinanceiraFP.getFolhaDePagamento().getVersao() +
                "&tipo=" + fichaFinanceiraFP.getFolhaDePagamento().getTipoFolhaDePagamento().name();
            montarMensagem(ve, link, "Ficha Financeira FP");
        });

        List<FichaFinanceiraFPSimulacao> fichasSimulacao = fichaFinanceiraFPSimulacaoFacade.buscarFichasFinanceiraFPSimulacaoPorVinculoFP(selecionado);
        fichasSimulacao.forEach(fichaFinanceiraFPSimulacao -> {
            String link = "/consultafichafinanceira-simulacao/novo/?contrato=" + selecionado.getId() +
                "&mes=" + fichaFinanceiraFPSimulacao.getFolhaDePagamentoSimulacao().getMes().getNumeroMes() +
                "&ano=" + fichaFinanceiraFPSimulacao.getFolhaDePagamentoSimulacao().getAno() +
                "&tipo=" + fichaFinanceiraFPSimulacao.getFolhaDePagamentoSimulacao().getTipoFolhaDePagamento().name();
            montarMensagem(ve, link, "Ficha Financeira FP Simulação");
        });

        List<LancamentoFP> lancamentos = lancamentoFPFacade.buscarLancamentosFoPorVinculo(selecionado);
        lancamentos.forEach(lancamentoFP -> {
            montarMensagem(ve, "/lancamentofp/ver/" + lancamentoFP.getId(), "Lançamento Folha de Pagamento");
        });

        List<SextaParte> sextaPartes = sextaParteFacade.findSextaPartePorVinculoFP(selecionado);
        sextaPartes.forEach(sextaParte -> {
            montarMensagem(ve, "/sextaparte/ver/" + sextaParte.getId(), "Concessão de Direito à Sexta Parte");
        });

        List<Long> idExportacoesArquiviosMargem = utilImportaExportaFacade.buscarIdsExportacaoArquivoMargemPorVinculoFP(selecionado);
        idExportacoesArquiviosMargem.forEach(exportaArquivoMargem -> {
            montarMensagem(ve, "/exportar-arquivomargem/ver/" + exportaArquivoMargem, "Exportação do Arquivo de Margem");
        });

        List<CreditoSalario> creditosSalario = creditoSalarioFacade.buscarCreditosSalarioPorVinculoFP(selecionado);
        creditosSalario.forEach(creditoSalario -> {
            montarMensagem(ve, "/credito-de-salario/ver/" + creditoSalario.getId(), "Crédito de Salário");
        });

        List<VinculoFPEventoEsocial> vinculoEsocial = contratoFPFacade.buscarVinculosFPEventoEsocialPorVinculo(selecionado);
        if (!vinculoEsocial.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Foram encontrados dados dependentes conforme segue: Eventos do eSocial. Para exclusão entre em contato com o suporte técnico.");
        }

        List<ExoneracaoRescisao> exoneracoes = exoneracaoRescisaoFacade.recuperarExoneracaoRescisaoPorVinculoFP(selecionado.getId());
        exoneracoes.forEach(exoneracaoRescisao -> {
            montarMensagem(ve, "/exoneracaorescisao/ver/" + exoneracaoRescisao.getId(), " Exoneração / Rescisão do Servidor");
        });

        ve.lancarException();
    }

    private void montarMensagem(ValidacaoException ve, String link, String descricao) {
        String url = "<b><a href='" + FacesUtil.getRequestContextPath() + link + "' target='_blank'>" + descricao + "</a></b>";
        ve.adicionarMensagemDeOperacaoNaoPermitida("Foram encontrados dados dependentes conforme segue: " + url);
    }

    public void executarValidacoesAoSelecionarMatricula() {
        validaNivelEscolaridade(getSelecionado().getMatriculaFP(), selecionado.getCargo());
        validaExigencias(true, pessoaFisicaFacade.recuperar(selecionado.getMatriculaFP().getPessoa().getId()), selecionado.getCargo());
        // validaEspecialidades(getSelecionado().getMatriculaFP(), selecionado.getCargo());
        verificaMatriculaComContratoVigente();
        carregaFoto();
    }

    private void validarPessoaQualificada() {
        if (!matriculaSelecionado.getPessoa().getSituacaoQualificacaoCadastral().equals(SituacaoQualificacaoCadastral.QUALIFICADO)) {
            FacesUtil.addWarn("Atenção!", "A Hierarquia Organizacional escolhida para registro do servidor está configurada para impedir o salvamento de contratos sem que seja realizada a qualificação cadastral da pessoa.");
            selecionado.setHierarquiaOrganizacional(null);
            RequestContext.getCurrentInstance().update("Formulario:unidadeOrganizacional");
        }
    }

    public void atribuiMatriculaSelecionado(SelectEvent event) {
        MatriculaFP mat = (MatriculaFP) event.getObject();
        matriculaSelecionado = mat;
        executarValidacoesAoSelecionarMatricula();
    }

    public Boolean verificaQuantidadeDeContratosVigentes() {
        if (selecionado.getId() != null || selecionado.getMatriculaFP() == null) {
            return true;
        }

        List<ContratoFP> contratosVigentes = contratoFPFacade.recuperaContratosPorMatriculaFP(selecionado.getMatriculaFP());
        return contratosVigentes.size() < 2;
    }

    public List<ContratoFP> completaContratoFPs(String parte) {
        return contratoFPFacade.recuperaContrato(parte.trim());
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public void inicializa() {
        dataProvimento = new Date();
        selecionado.setHorarioContratoFPs(new ArrayList<HorarioContratoFP>());
        situacaoContratoFP = new SituacaoContratoFP();
        lotacaoFuncional = new LotacaoFuncional();
        sindicatoVinculoFP = new SindicatoVinculoFP();
        cancelarOpcaoValeTransporte();
        horarioContratoFP = new HorarioContratoFP();
        previdenciaVinculoFP = new PrevidenciaVinculoFP();
        enquadramentoFuncionalControlador.novo();

    }

    public List<EnquadramentoFuncional> enquadramentos() {
        List<EnquadramentoFuncional> enquadramentos = new ArrayList<>();
        if (selecionado != null && selecionado.getId() != null) {
            enquadramentos = enquadramentoFuncionalFacade.recuperaEnquadramentoContratoFP(selecionado);
            for (EnquadramentoFuncional ef : enquadramentos) {
                EnquadramentoPCS valor = enquadramentoPCSFacade.recuperaValor(ef.getCategoriaPCS(), ef.getProgressaoPCS(), ef.getInicioVigencia());
                if (valor != null) {
                    ef.setVencimentoBase(valor.getVencimentoBase());
                }
            }
        } else {
            return new ArrayList<>();
        }
        return enquadramentos;
    }

    @URLAction(mappingId = "novoReintegracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void instanciaContratoEnquadramento() {
        selecionado = new ContratoFP();
        inicializa();
        dataProvimento = new Date();
        tipo = Tipo.NOVO;
        reintegracao = new Reintegracao();
        operacao = Operacoes.NOVO;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    private void inicializaPastaGaveta() {

        pastaGavetaContratoFPSelecionado = new PastaGavetaContratoFP();
        pastaGavetaContratoFPSelecionado.setContratoFP(selecionado);
        pastaGavetaContratoFPSelecionado.setInicioVigencia(SistemaFacade.getDataCorrente());
    }

    private boolean horarioJaUsado(HorarioContratoFP horarioContratoFP, MatriculaFP mat) {
        List<ContratoFP> contratos = contratoFPFacade.listaContratosVigentesPorPessoaFisica(mat.getPessoa());

        for (ContratoFP c : contratos) {
            c = contratoFPFacade.recuperar(c.getId());
            if (selecionado.getId() != null && selecionado.getId().equals(c.getId())) {
                continue;
            }
            LotacaoFuncional lf = null;
            if (isOperacaoNovo()) {
                lf = lotacaoFuncionalFacade.recuperarLotacaoFuncionalVigentePorContratoFP(c, lotacaoFuncionalFacade.getSistemaFacade().getDataOperacao());
            } else {
                lf = c.getLotacaoFuncionalVigente(sistemaControlador.getDataOperacao());
            }
            if (lf != null && lf.getHorarioContratoFP() != null) {
                if (lf.getHorarioContratoFP().getHorarioDeTrabalho().getId().equals(horarioContratoFP.getHorarioDeTrabalho().getId())) {
                    FacesUtil.addWarn("Atenção!", "Essa Matrícula já possui um contrato vigente neste mesmo horário de trabalho.");
                    return true;
                }
            }
        }
        for (LotacaoFuncional l : selecionado.getLotacaoFuncionals()) {
            if (l.getHorarioContratoFP() != null && l.getHorarioContratoFP() != null) {
                if (l.getHorarioContratoFP().getHorarioDeTrabalho().getId().equals(horarioContratoFP.getHorarioDeTrabalho().getId())) {
                    if (sistemaControlador.getDataOperacao().before(l.getInicioVigencia()) && (l.getFinalVigencia() == null || sistemaControlador.getDataOperacao().after(l.getFinalVigencia()))) {
                        FacesUtil.addWarn("Atenção!", "Esse horário de trabalho já foi adicionado nesse contrato.");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contratofp/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Reintegracao getReintegracao() {
        return reintegracao;
    }

    public void setReintegracao(Reintegracao reintegracao) {
        this.reintegracao = reintegracao;
    }

    public EnquadramentoFuncionalControlador getEnquadramentoFuncionalControlador() {
        return enquadramentoFuncionalControlador;
    }

    public void setEnquadramentoFuncionalControlador(EnquadramentoFuncionalControlador enquadramentoFuncionalControlador) {
        this.enquadramentoFuncionalControlador = enquadramentoFuncionalControlador;
    }

    @Override
    public void cancelar() {
        super.cancelar();
    }

    public void adicionarVinculoDeContratoFP() {
        try {
            validarVinculoDeContratoFP();
            contratoVinculoDeContrato.setContratoFP(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getContratoVinculoDeContratos(), contratoVinculoDeContrato);
            contratoVinculoDeContrato = new ContratoVinculoDeContrato();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarVinculoDeContratoFP() {
        ValidacaoException ve = new ValidacaoException();
        if (contratoVinculoDeContrato.getVinculoDeContratoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Vínculo é Obrigatório");
        }

        if (contratoVinculoDeContrato.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Início de Vigência é Obrigatório");
        } else if (contratoVinculoDeContrato.getFinalVigencia() != null && contratoVinculoDeContrato.getInicioVigencia().after(contratoVinculoDeContrato.getFinalVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O final de Vigência não pode ser menor que o inicio da vigência.");
        }

        for (ContratoVinculoDeContrato c : selecionado.getContratoVinculoDeContratos()) {
            if (c.getFinalVigencia() != null && contratoVinculoDeContrato.getInicioVigencia() != null) {
                if (c.getFinalVigencia().after(contratoVinculoDeContrato.getInicioVigencia()) && (!c.equals(contratoVinculoDeContrato))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A data Final da vigência anterior não pode ser inferior a data inicial da nova vigência!");
                }
            } else if (!c.equals(contratoVinculoDeContrato)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A vigência da lista contem registro(s) com vigência aberta!");
            }
        }
        ve.lancarException();
    }

    public void removeVinculoDeContratoFP(ActionEvent e) {
        ContratoVinculoDeContrato c = (ContratoVinculoDeContrato) e.getComponent().getAttributes().get("objeto");
        selecionado.getContratoVinculoDeContratos().remove(c);
        c.setContratoFP(null);
    }

    public void editarVinculoDeContratoFP(ContratoVinculoDeContrato contratoVinculo) {
        contratoVinculoDeContrato = (ContratoVinculoDeContrato) Util.clonarObjeto(contratoVinculo);
    }

    public ConverterAutoComplete getConverterVinculoDeContratoFP() {
        if (converterVinculoDeContratoFP == null) {
            converterVinculoDeContratoFP = new ConverterAutoComplete(VinculoDeContratoFP.class, vinculoDeContratoFPFacade);
        }
        return converterVinculoDeContratoFP;
    }

    public List<VinculoDeContratoFP> completaVinculoDeContratoFP(String parte) {
        return vinculoDeContratoFPFacade.listaFiltrandoCodigoDescricao(parte.trim());
    }

    public ContratoVinculoDeContrato getContratoVinculoDeContrato() {
        return contratoVinculoDeContrato;
    }

    public void setContratoVinculoDeContrato(ContratoVinculoDeContrato contratoVinculoDeContrato) {
        this.contratoVinculoDeContrato = contratoVinculoDeContrato;
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            arquivo = new Arquivo();
            arquivo.setMimeType(event.getFile().getContentType());
            arquivo.setNome(event.getFile().getFileName());
            arquivo.setDescricao(event.getFile().getFileName());
            arquivo.setTamanho(event.getFile().getSize());
            arquivo.setInputStream(event.getFile().getInputstream());
            arquivo = arquivoFacade.novoArquivoMemoria(arquivo);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void adicionarArquivo() {
        if (validaArquivo()) {
            PrevidenciaArquivo previdenciaArquivo = new PrevidenciaArquivo();
            previdenciaArquivo.setArquivo(arquivo);
            previdenciaArquivo.setPrevidenciaVinculoFP(previdenciaVinculoFP);
            previdenciaArquivos.add(previdenciaArquivo);
            file = null;
            arquivo = null;
        }
    }

    public boolean validaArquivo() {
        boolean valida = true;

        if (arquivo == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Por favor, informe um arquivo. Clique no botão 'Selecionar' para escolher o arquivo desejado.");
            valida = false;
        }

        return valida;
    }

    public StreamedContent getArquivoStream(PrevidenciaArquivo previdenciaArquivo) throws IOException {
        arquivo = previdenciaArquivo.getArquivo();
        return recuperarArquivoParaDownload();
    }

    public void removeArquivo(PrevidenciaArquivo previdenciaArquivo) {
        if (previdenciaArquivo.getId() != null) {
            previdenciaArquivo.setExcluido(true);
        }
        previdenciaArquivos.remove(previdenciaArquivo);
    }

    public ContratoFP esteSelecionado() {
        return selecionado;
    }

    public void limpaCargoCBO() {
        if (selecionado.getMatriculaFP() == null || selecionado.getMatriculaFP().getId() == null) {
            selecionado.setCargo(null);
            atualizarCboDoVinculoFP(null);
            selecionado.setCargos(null);
            carregaFoto();
        }
    }

    public void verificaMatriculaComContratoVigente() {
        if (matriculaSelecionado != null && matriculaSelecionado.getId() != null) {
            List<ContratoFP> contratos = contratoFPFacade.recuperaContratosPorMatriculaFP(matriculaSelecionado);
            if (contratos.size() > 0) {
                for (ContratoFP c : contratos) {
                    if (!cargoPermiteAcumulo(c)) {
                        FacesUtil.addWarn("Atenção!", "Essa Matrícula possui contrato vigente em um outro Cargo que não permite acumulo de cargos.");
                        selecionado.setMatriculaFP(null);
                        selecionado.setNumero("");
                        RequestContext.getCurrentInstance().update("Formulario:matriculaFP");
                        RequestContext.getCurrentInstance().update("Formulario:numero");
                    }
                }
            }
        }
    }

    public boolean cargoPermiteAcumulo(ContratoFP contratoFP) {
        if (contratoFP.getCargo() != null) {
            Cargo cargo = cargoFacade.recuperar(contratoFP.getCargo().getId());
            if (cargo != null && cargo.getPermiteAcumulo() != null && cargo.getPermiteAcumulo()) {
                return true;
            }
        }
        return false;
    }

    public void ativouAba(ActionEvent ev) {
        RequestContext.getCurrentInstance().update("Formulario:contaCorrente");
    }

    public void novaContaCorrente() {
        Web.poeNaSessao(getSelecionado().getMatriculaFP().getPessoa());
        Web.navegacao(getUrlAtual(), "/conta-corrente-bancaria/novo/", getSelecionado());
    }

    public void carregarRecursoDoVinculoFP() {
        if (getSelecionado() != null) {
            if (getSelecionado().getId() != null) {
                recursoDoVinculoFPs = recursoDoVinculoFPFacade.recuperarRecursosDoVinculo(getSelecionado());
            }
        }
    }

    public List<RecursoDoVinculoFP> getRecursoDoVinculoFPs() {
        return recursoDoVinculoFPs;
    }

    public void setRecursoDoVinculoFPs(List<RecursoDoVinculoFP> recursoDoVinculoFPs) {
        this.recursoDoVinculoFPs = recursoDoVinculoFPs;
    }

    public void removerArquivo() {
        arquivo = null;
    }

    public StreamedContent recuperarArquivoParaDownload() {
        StreamedContent s = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : arquivo.getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }
        }
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        s = new DefaultStreamedContent(is, arquivo.getMimeType(), arquivo.getNome());
        return s;
    }

    public LotacaoFuncional recuperaLotacaoFuncionalVigentePorContratoFP(VinculoFP contratoFP) {
        return lotacaoFuncionalFacade.buscarUltimaLotacaoVigentePorVinculoFP(contratoFP);
    }

    public void iniciaTempo() {
        testeDesempenho = new Date();
    }

    public void finalizaTempo() {
        long differenceMilliSeconds = new Date().getTime() - testeDesempenho.getTime();
    }

    public OpcaoSalarialVinculoFP getOpcaoSalarialVinculoFP() {
        return opcaoSalarialVinculoFP;
    }

    public void setOpcaoSalarialVinculoFP(OpcaoSalarialVinculoFP opcaoSalarialVinculoFP) {
        this.opcaoSalarialVinculoFP = opcaoSalarialVinculoFP;
    }

    public List<SelectItem> getOpcaoSalariais() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (OpcaoSalarialCC object : opcaoSalarialCCFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterOpcaoSalarial() {
        if (converterOpcaoSalarial == null) {
            converterOpcaoSalarial = new ConverterGenerico(OpcaoSalarialCC.class, opcaoSalarialCCFacade);
        }
        return converterOpcaoSalarial;
    }

    public void adicionarOpcaoSalarialVinculo() {
        try {
            validaCamposOpcaoSalarial();
            opcaoSalarialVinculoFP.setVinculoFP(selecionado);
            selecionado.setOpcaoSalarialVinculoFP(Util.adicionarObjetoEmLista(selecionado.getOpcaoSalarialVinculoFP(), opcaoSalarialVinculoFP));
            opcaoSalarialVinculoFP = new OpcaoSalarialVinculoFP();
            editandoOpcaoSalarial = false;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removeOpcaoSalarialVinculo(OpcaoSalarialVinculoFP opcaoSalarialVinculoFP) {
        selecionado.getOpcaoSalarialVinculoFP().remove(opcaoSalarialVinculoFP);
        opcaoSalarialVinculoFP = new OpcaoSalarialVinculoFP();
    }

    public void editarOpcaoSalarialVinculo(OpcaoSalarialVinculoFP opcao) {
        editandoOpcaoSalarial = true;
        opcaoSalarialVinculoFP = (OpcaoSalarialVinculoFP) Util.clonarObjeto(opcao);
    }

    private void validaCamposOpcaoSalarial() {
        ValidacaoException ve = new ValidacaoException();
        if (opcaoSalarialVinculoFP != null) {
            if (opcaoSalarialVinculoFP.getInicioVigencia() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O início de vigência da Opção Salarial é Obrigatória.");
            }
            if (opcaoSalarialVinculoFP.getOpcaoSalarialCC() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A Opção Salarial é Obrigatória.");
            }
            if (opcaoSalarialVinculoFP.getPercentual() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O percentual é obrigatório.");
            }
            if (opcaoSalarialVinculoFP.getFinalVigencia() != null && opcaoSalarialVinculoFP.getInicioVigencia().after(opcaoSalarialVinculoFP.getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O data de início de vigência não pode ser maior que o final de vigência.");
            }

            for (OpcaoSalarialVinculoFP op : getSelecionado().getOpcaoSalarialVinculoFP()) {
                if (!editandoOpcaoSalarial && op.getFinalVigencia() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um registro vigente, encerre a vigência do mesmo para adicionar outro registro.");
                }
                if (op.getFinalVigencia() != null && opcaoSalarialVinculoFP.getInicioVigencia().before(op.getFinalVigencia())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A data de inicio de vigência não pode ser menor que a do ultimo registro vigente.");
                }
            }

        }
        ve.lancarException();
    }

    public void buscaConfiguracaoModalidade() {
        if (isOperacaoNovo()) {
            configuracaoModalidadeContratoFP = configuracaoModalidadeContratoFPFacade.recuperarPelaModalidade(modalidadeContratoFPData.getModalidadeContratoFP());
            if (configuracaoModalidadeContratoFP != null) {
                FacesUtil.executaJavaScript("dlgConfModalidade.show()");
                selecionado.setTipoRegime(configuracaoModalidadeContratoFP.getTipoRegime());
                selecionado.setSituacaoVinculo(configuracaoModalidadeContratoFP.getSituacaoVinculo());

                selecionado.getPrevidenciaVinculoFPs().clear();
                for (ConfiguracaoPrevidenciaVinculoFP configuracaoPrevidencia : configuracaoModalidadeContratoFP.getConfiguracaoPrevidenciaVinculoFPs()) {
                    PrevidenciaVinculoFP prev = new PrevidenciaVinculoFP();
                    prev.setContratoFP(selecionado);
                    prev.setInicioVigencia(selecionado.getInicioVigencia());
                    prev.setTipoPrevidenciaFP(configuracaoPrevidencia.getTipoPrevidenciaFP());
                    selecionado.getPrevidenciaVinculoFPs().add(prev);
                }
                selecionado.setSefip(configuracaoModalidadeContratoFP.getSefip());
                selecionado.setCategoriaSEFIP(configuracaoModalidadeContratoFP.getCategoriaSEFIP());
                selecionado.setTipoAdmissaoFGTS(configuracaoModalidadeContratoFP.getTipoAdmissaoFGTS());
                selecionado.setTipoAdmissaoSEFIP(configuracaoModalidadeContratoFP.getTipoAdmissaoSEFIP());
                selecionado.setTipoAdmissaoRAIS(configuracaoModalidadeContratoFP.getTipoAdmissaoRAIS());
                selecionado.setMovimentoCAGED(configuracaoModalidadeContratoFP.getMovimentoCAGED());
                selecionado.setOcorrenciaSEFIP(configuracaoModalidadeContratoFP.getOcorrenciaSEFIP());
                selecionado.setRetencaoIRRF(configuracaoModalidadeContratoFP.getRetencaoIRRF());
                selecionado.setVinculoEmpregaticio(configuracaoModalidadeContratoFP.getVinculoEmpregaticio());
                selecionado.setNaturezaRendimento(configuracaoModalidadeContratoFP.getNaturezaRendimento());

                selecionado.getLotacaoFuncionals().clear();
                selecionado.getHorarioContratoFPs().clear();
                if (configuracaoModalidadeContratoFP.getHorarioDeTrabalho() != null) {
                    horarioContratoFP.setHorarioDeTrabalho(configuracaoModalidadeContratoFP.getHorarioDeTrabalho());
                    HorarioContratoFP horario = new HorarioContratoFP();
                    horario.setInicioVigencia(selecionado.getInicioVigencia() != null ? selecionado.getInicioVigencia() : SistemaFacade.getDataCorrente());
                    horario.setHorarioDeTrabalho(configuracaoModalidadeContratoFP.getHorarioDeTrabalho());
                    selecionado.getHorarioContratoFPs().add(horario);
                }
            }
        }
    }

    public void atualizarCamposPelaConfiguracaoModalidade() {
        selecionado.setJornadaDeTrabalho(configuracaoModalidadeContratoFP.getJornadaDeTrabalho());
        selecionado.setDescansoSemanal(configuracaoModalidadeContratoFP.getDescansoSemanal());
    }

    public void novoContratoFPCargo() {
        novoCargo = true;
        edicaoCargo = false;
        contratoFPCargoSelecionado = new ContratoFPCargo();
        if (!CollectionUtils.isEmpty(selecionado.getCargos())) {
            Date fimVigencia;
            Collections.sort(selecionado.getCargos());
            fimVigencia = selecionado.getCargos().get(selecionado.getCargos().size() - 1).getFimVigencia();
            if (fimVigencia != null) {
                contratoFPCargoSelecionado.setInicioVigencia(DataUtil.adicionaDias(fimVigencia, 1));
            }
            return;
        }

        contratoFPCargoSelecionado.setInicioVigencia(selecionado.getInicioVigencia());
    }

    public void cancelarContratoFPCargo() {
        contratoFPCargoSelecionado = null;
        novoCargo = false;
        edicaoCargo = false;
        atualizarTabelaCargos();
    }

    public void confirmarContratoFPCargo() {
        try {
            Util.validarCampos(contratoFPCargoSelecionado);
            if (isEdicaoCargo()) {
                if (!isValidarEdicaoCargo())
                    return;
                validarEdicaoOuNovoContratoFPCargo();
                ContratoFPCargo ultimo = buscarCargoWithMaiorFimVigencia(selecionado.getCargos());
                if (ultimo != null)
                    ajustarFimVigencia(ultimo, contratoFPCargoSelecionado);

            } else {
                validarEdicaoOuNovoContratoFPCargo();
                ContratoFPCargo vigente = buscarCargoVigente();

                if (vigente != null)
                    vigente.setFimVigencia(DataUtil.adicionaDias(contratoFPCargoSelecionado.getInicioVigencia(), -1));
            }

            atribuirToSelecionado();
            addCargo();
            cancelarContratoFPCargo();
            ordenarCargos();
            atualizarTabelaCargos();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarEdicaoOuNovoContratoFPCargo() {
        ValidacaoException ve = new ValidacaoException();
        if (isEdicaoCargo()) {
            if (selecionado.getCargos() != null && !selecionado.getCargos().isEmpty()
                && selecionado.getCargos().size() > 1) {
                for (ContratoFPCargo cargo : selecionado.getCargos()) {
                    if (contratoFPCargoSelecionado.getInicioVigencia().before(cargo.getInicioVigencia())) {
                        ve.adicionarMensagemDeOperacaoNaoRealizada("O início da vigência do cargo não pode ser anterior ao início da vigência do cargo atual vigente!");
                    }
                    ve.lancarException();
                }
            }
        } else {
            if (selecionado.getCargos() != null && !selecionado.getCargos().isEmpty()) {
                for (ContratoFPCargo cargo : selecionado.getCargos()) {
                    if (contratoFPCargoSelecionado.getInicioVigencia().compareTo(cargo.getInicioVigencia()) == 0) {
                        ve.adicionarMensagemDeOperacaoNaoRealizada("O início da vigência do novo cargo não pode ser igual ao início da vigência do cargo atual vigente!");
                    }
                    if (contratoFPCargoSelecionado.getInicioVigencia().before(cargo.getInicioVigencia())) {
                        ve.adicionarMensagemDeOperacaoNaoRealizada("O início da vigência do novo cargo não pode ser anterior ao início da vigência do cargo atual vigente!");
                    }
                    ve.lancarException();
                }
            }
        }
    }

    private void atualizarTabelaCargos() {
        FacesUtil.atualizarComponente("Formulario:tab:panel-cargos");
    }

    private void ajustarFimVigencia(ContratoFPCargo ultimo, ContratoFPCargo cargo) {
        ultimo.setFimVigencia(DataUtil.adicionaDias(cargo.getInicioVigencia(), -1));
        cargo.setFimVigencia(null);
    }

    private ContratoFPCargo buscarCargoWithMaiorFimVigencia(List<ContratoFPCargo> cargos) {
        ContratoFPCargo ultimo = null;
        ordenarCargos();
        if (cargos.size() == 1) {
            if (cargos.get(0).isFimVigenciaNull())
                return null;
        }

        if (isCargosEmpty())
            return null;
        if (selecionado.getCargos().size() > 1) {
            ultimo = cargos.get(cargos.size() - 2);
        }
        return ultimo;
    }

    private void definirToVigente(List<ContratoFPCargo> cargos) {
        if (isCargosEmpty())
            return;

        ContratoFPCargo ultimo = cargos.get(0);

        // vai buscar o ultimo cargo com maior final de vigencia
        // se acontecer a exclusão de um cargo vigente

        for (int i = 1; i < cargos.size(); i++) {
            if (ultimo.getFimVigencia() == null || cargos.get(i).getFimVigencia() == null)
                return;
            if (!ultimo.isFimVigenciaNull() && !cargos.get(i).isFimVigenciaNull())
                if (cargos.get(i).getFimVigencia().compareTo(ultimo.getFimVigencia()) >= 0)
                    ultimo = cargos.get(i);
        }
        ordenarCargos();
        ultimo.setFimVigencia(null);
        atualizarCargoDoContratoFP(ultimo.getCargo());
        atualizarCboDoVinculoFP(ultimo.getCbo());
    }

    private void atualizarCargoDoContratoFP(Cargo cargo) {
        selecionado.setCargo(cargo);
    }

    private boolean isCargosEmpty() {
        return (selecionado.getCargos() == null) || selecionado.getCargos().isEmpty();
    }

    private void addCargo() {
        selecionado.setCargos(Util.adicionarObjetoEmLista(selecionado.getCargos(), contratoFPCargoSelecionado));
    }

    private ContratoFPCargo buscarCargoVigente() {
        if (isCargosEmpty())
            return null;

        for (ContratoFPCargo cargo : selecionado.getCargos()) {
            if (cargo.isFimVigenciaNull())
                return cargo;
        }
        return null;
    }

    private void atribuirToSelecionado() {
        contratoFPCargoSelecionado.setContratoFP(selecionado);
        atualizarCargoDoContratoFP(contratoFPCargoSelecionado.getCargo());
        atualizarCboDoVinculoFP(contratoFPCargoSelecionado.getCbo());
    }

    private void atualizarCboDoVinculoFP(CBO cbo) {
        selecionado.setCbo(cbo);
    }

    private void ordenarCargos() {
        if (!isCargosEmpty())
            Collections.sort(selecionado.getCargos());
    }

    private boolean validarNovoCargoContrato() {
        boolean isValido = true;

        if (selecionado.getInicioVigencia() == null) {
            FacesUtil.addOperacaoNaoPermitida("Para adicionar cargos é necessário informar o campo 'Data de exercício'");
            return false;
        }

        if (contratoFPCargoSelecionado == null) {
            FacesUtil.addCampoObrigatorio("O campo Cargo é obrigatório");
            return false;
        }

        if (!Util.validaCampos(contratoFPCargoSelecionado)) {
            return false;
        }

        if (isOperacaoNovo()) {
            Date inicio = contratoFPCargoSelecionado.getInicioVigencia();
            if (!isCargosEmpty()) {
                ContratoFPCargo cargo = buscarCargoVigente();
                if (cargo != null) {
                    if (inicio.compareTo(cargo.getInicioVigencia()) <= 0) {
                        String infoString = Util.formatterDiaMesAno.format(inicio);
                        FacesUtil.addOperacaoNaoPermitida("Já existe cargo vigente em <b> " + infoString + " </b>");
                        return false;
                    }
                }
            }
        }

        isValido = isValidarCargos(isValido);


        ordenarCargos();
        return isValido;
    }

    private boolean isValidarCargos(boolean isValido) {
        if (!isOperacaoNovo()) {
            Date depoisDe = null;
            if (selecionado.getId() != null) {
                depoisDe = folhaDePagamentoFacade.buscarUltimaDataFolhaProcessadaPorCargo(selecionado);
            }

            if (depoisDe != null) {
                if (contratoFPCargoSelecionado.getInicioVigencia().compareTo(depoisDe) <= 0) {
                    String infoString = Util.formatterDiaMesAno.format(contratoFPCargoSelecionado.getInicioVigencia());
                    FacesUtil.addOperacaoNaoPermitida("Já existem dados financeiros para este registro de  início de vigência <b>'" + infoString + "'</b>.");
                    return false;
                }
            }
            if (!isCargosEmpty()) {
                ContratoFPCargo vigente = buscarCargoVigente();
                if (vigente != null) {
                    Date inicio = contratoFPCargoSelecionado.getInicioVigencia();
                    if (inicio.compareTo(vigente.getInicioVigencia()) <= 0) {
                        String infoString = Util.formatterDiaMesAno.format(inicio);
                        FacesUtil.addOperacaoNaoPermitida("Já existe um cargo vigente em <b>'" + infoString + "'</b>.");
                        return false;
                    }

                }
            }
        }
        return isValido;
    }

    private boolean isValidarEdicaoCargo() {
        if (isEdicaoCargo()) {
            ContratoFPCargo ultimo = buscarCargoWithMaiorFimVigencia(selecionado.getCargos());
            if (ultimo != null) {
                Date inicio = contratoFPCargoSelecionado.getInicioVigencia();
                Date fim = ultimo.getFimVigencia();
                if (fim != null) {
                    if (inicio.compareTo(fim) <= 0) {
                        FacesUtil.addOperacaoNaoPermitida("O Fim de vigência não pode ser menor que o início da vigência do cargo " + ultimo.getCargo().getDescricao().toLowerCase() + ".");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void definirCargo(ContratoFPCargo cc) {
        contratoFPCargoSelecionado = null;
        edicaoCargo = true;
        selecionado.getCargos().remove(cc);
        definirToVigente(selecionado.getCargos());
        atualizarTabelaCargos();
    }

    private void atualizarCargos() {
        FacesUtil.atualizarComponente("Formulario:tab:panel-cargos");
    }

    private boolean validarCargoInformadoAoSalvarContrato() {
        if (CollectionUtils.isEmpty(selecionado.getCargos())) {
            FacesUtil.addOperacaoNaoPermitida("Por favor, informe um cargo para este servidor na aba 'Cargos'.");
            return false;
        }

        if (selecionado.getCargos().size() == 1 && Util.getDataHoraMinutoSegundoZerado(selecionado.getCargos().get(0).getInicioVigencia()).compareTo(selecionado.getInicioVigencia()) != 0) {
            FacesUtil.addOperacaoNaoPermitida("A 'Data de Exercício' do contrato deve ser igual a data de 'Início de Vigência' do cargo");
            return false;
        }

        return true;
    }

    public Converter getConverterConcurso() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {

                try {
                    return concursoFacade.buscarConcursoComCargos(Long.parseLong(value));
                } catch (Exception ex) {
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                String resultado = null;
                if (value == null) {
                    return null;
                } else {
                    if (value instanceof Long) {
                        resultado = String.valueOf(value);
                    } else {
                        try {
                            return "" + ((Concurso) value).getId();
                        } catch (Exception e) {
                            resultado = String.valueOf(value);
                        }
                    }
                }
                return resultado;
            }
        };
    }

    public List<SelectItem> getConcursos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Selecione um Concurso..."));
        for (Concurso c : concursoFacade.buscarConcursosParaTelaDeContratoFP()) {
            toReturn.add(new SelectItem(c, "" + c));
        }
        return toReturn;
    }

    public Converter getConverterCargoConcurso() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {

                try {
                    return cargoConcursoFacade.buscarCargoComCBOsAndInscricoes(Long.parseLong(value));
                } catch (Exception ex) {
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                String resultado = null;
                if (value == null) {
                    return null;
                } else {
                    if (value instanceof Long) {
                        resultado = String.valueOf(value);
                    } else {
                        try {
                            return "" + ((CargoConcurso) value).getId();
                        } catch (Exception e) {
                            resultado = String.valueOf(value);
                        }
                    }
                }
                return resultado;
            }
        };
    }

    public List<SelectItem> getCargos() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (concursoSelecionado == null) {
            toReturn.add(new SelectItem(null, ""));
            return toReturn;
        }
        toReturn.add(new SelectItem(null, "Selecione um Cargo..."));
        for (CargoConcurso c : concursoSelecionado.getCargos()) {
            toReturn.add(new SelectItem(c, "" + c));
        }
        return toReturn;
    }

    public boolean isCPFsIguais(String cpfCandidato, String cpfPessoa) {
        cpfCandidato = cpfCandidato.replace("-", "").replace(".", "");
        cpfPessoa = cpfPessoa.replace("-", "").replace(".", "");
        if (cpfCandidato == null || cpfCandidato.trim().isEmpty()) {
            return false;
        }

        if (cpfPessoa == null || cpfPessoa.trim().isEmpty()) {
            return false;
        }

        if (cpfPessoa.equals(cpfCandidato)) {
            return true;
        }
        return false;
    }

    public void criarContratoFPCargoPeloModuloConcursos() {
        if (cargoConcurso == null) {
            return;
        }
        for (ClassificacaoInscricao classificacaoInscricao : cargoConcurso.getClassificacaoConcurso().getInscricoes()) {
            if (isCPFsIguais(classificacaoInscricao.getInscricaoConcurso().getCpf(), selecionado.getMatriculaFP().getPessoa().getCpf())) {
                classificacaoInscricaoSelecionada = classificacaoInscricao;
                break;
            }
        }

        if (classificacaoInscricaoSelecionada == null) {
            FacesUtil.addOperacaoNaoRealizada("Não existem candidatos no concurso selecionado com o mesmo CPF da pessoa indicada na matrícula. Por favor, verifique as informações e tente novamente");
            return;
        }
        selecionado.setClassificacaoInscricao(classificacaoInscricaoSelecionada);
        criarCargoContratoFPPadrao();
    }

    public void criarCargoContratoFPPadrao() {
        if (isOperacaoEditar()) {
            return;
        }
        selecionado.setCargo(cargoConcurso.getCargo());
        cboSelecionado = selecionado.getCargo().getCbos() != null && selecionado.getCargo().getCbos().size() == 1 ? selecionado.getCargo().getCbos().get(0) : null;
        contratoFPCargoSelecionado = new ContratoFPCargo();
        contratoFPCargoSelecionado.setCargo(selecionado.getCargo());
        contratoFPCargoSelecionado.setContratoFP(selecionado);
        contratoFPCargoSelecionado.setInicioVigencia(UtilRH.getDataOperacao());
        if (selecionado.getCargo().getCbos().size() > 1) {
            return;
        }
        contratoFPCargoSelecionado.setCbo(cboSelecionado);
        atualizarCboDoVinculoFP(cboSelecionado);
        selecionado.setCargos(Util.adicionarObjetoEmLista(selecionado.getCargos(), contratoFPCargoSelecionado));
        cancelarContratoFPCargo();
    }

    public void removerCargo(ContratoFPCargo cc) {
        if (!isOperacaoNovo()) {
            if (folhaDePagamentoFacade.existeFolhaProcessadaParaContratoDepoisDe(cc.getContratoFP(), cc.getInicioVigencia())) {
                String infoString = Util.formatterDiaMesAno.format(cc.getInicioVigencia());
                FacesUtil.addOperacaoNaoPermitida("Já existem dados financeiros para este registro após seu início de vigência <b>'" + infoString + "'</b>.");
                return;
            }
            definirCargo(cc);
        } else {
            definirCargo(cc);
        }
    }

    public boolean isExcluirOrEditarCargo(ContratoFPCargo cargo) {
        boolean retorno = true;
        if (isOperacaoNovo()) {
            retorno = false;
        } else {
            retorno = folhaDePagamentoFacade.existeFolhaProcessadaParaContratoDepoisDe(cargo.getContratoFP(), cargo.getInicioVigencia());
        }
        if (!retorno && !cargo.isFimVigenciaNull()) {
            retorno = true;
        }
        return retorno;
    }

    public void editarContratoFPCargo(ContratoFPCargo cc) {
        contratoFPCargoSelecionado = (ContratoFPCargo) Util.clonarObjeto(cc);
        edicaoCargo = true;
        atualizarTabelaCargos();
    }

    public boolean existeFolhadePagamentoParaOCargo() {
        if (folhaDePagamentoFacade.existeFolhaProcessadaParaContratoDepoisDe(contratoFPCargoSelecionado.getContratoFP(), contratoFPCargoSelecionado.getInicioVigencia())) {
            return true;
        }
        return false;
    }

    public void limparCbo() {
        contratoFPCargoSelecionado.setCbo(null);
    }

    public boolean habilitarEdicaoCargoSomenteQuandoEditar() {
        return isOperacaoEditar();
    }

    public Long getNumeroMatricula() {
        return numeroMatricula;
    }

    public void setNumeroMatricula(Long numeroMatricula) {
        this.numeroMatricula = numeroMatricula;
    }

    public Long getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(Long numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public void selecionarPastaContratoFP(PastaGavetaContratoFP pasta) {
        pastaGavetaContratoFPSelecionado = (PastaGavetaContratoFP) Util.clonarEmNiveis(pasta, 2);
    }

    public void removerPastaContratoFP(PastaGavetaContratoFP pasta) {

        selecionado.getPastasContratosFP().remove(pasta);
    }

    public void adicionarPasta() {
        try {
            validarPastaGavetaDoContrato();
            if (validarVigenciaPasta()) {
                return;
            }
            if (selecionado.getPastasGavetasContratosFP() == null) {
                selecionado.setPastasGavetasContratosFP(Lists.<PastaGavetaContratoFP>newArrayList());
            }
            selecionado.getPastasGavetasContratosFP().add(pastaGavetaContratoFPSelecionado);
            Collections.sort(selecionado.getPastasGavetasContratosFP(), new Comparator<PastaGavetaContratoFP>() {
                @Override
                public int compare(PastaGavetaContratoFP o1, PastaGavetaContratoFP o2) {
                    try {
                        return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
                    } catch (NullPointerException np) {
                        return 0;
                    }
                }
            });
            pastaGavetaContratoFPSelecionado = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void inserirNovaPasta() {
        pastaGavetaContratoFPSelecionado = new PastaGavetaContratoFP();
        pastaGavetaContratoFPSelecionado.setContratoFP(selecionado);
    }

    private boolean validarVigenciaPasta() {
        if (!DataUtil.isVigenciaValida(pastaGavetaContratoFPSelecionado, selecionado.getPastasContratosFP())) {
            return true;
        }
        return false;
    }

    public void validarPastaGavetaDoContrato() {
        ValidacaoException ve = new ValidacaoException();

        if (pastaGavetaContratoFPSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Fichário é obrigatório.");
            ve.adicionarMensagemDeCampoObrigatorio("O campo Gaveta é obrigatório.");
            ve.adicionarMensagemDeCampoObrigatorio("O campo Pasta é obrigatório");
            ve.adicionarMensagemDeCampoObrigatorio("O campo Inicio de Vigência é obrigatório.");
        } else {
            if (pastaGavetaContratoFPSelecionado.getPastaGaveta() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Pasta é obrigatório, para selecionar a Pasta selecione primeiro o Fichário e depois a Gaveta.");
            } else if (pastaGavetaContratoFPSelecionado.getPastaGaveta().getGavetaFichario() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Gaveta é obrigatório.");
            } else if (pastaGavetaContratoFPSelecionado.getPastaGaveta().getGavetaFichario().getFichario() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Fichário é obrigatório.");
            }
            if (pastaGavetaContratoFPSelecionado.getInicioVigencia() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Inicio de Vigência é obrigatório.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getCategoriaTrabalhador() {
        return Util.getListSelectItem(contratoFPFacade.getCategoriaTrabalhadorFacade().lista());
    }

    public void cancelarAdicaoPasta() {
        pastaGavetaContratoFPSelecionado = null;
    }

    public PastaGavetaContratoFP getPastaGavetaContratoFPSelecionado() {
        return pastaGavetaContratoFPSelecionado;
    }

    public void setPastaGavetaContratoFPSelecionado(PastaGavetaContratoFP pastaGavetaContratoFPSelecionado) {
        this.pastaGavetaContratoFPSelecionado = pastaGavetaContratoFPSelecionado;
    }

    public enum Tipo {
        PRINCIPAL,
        LOCAL,
        NOVO;
    }

    private void getEventoPorIdentificador() {
        List<Long> fichasId = fichaFinanceiraFPFacade.buscarIdsFichaFinanceiraPorVinculo(selecionado);
        List<Long> exoneracoesId = exoneracaoRescisaoFacade.buscarIdsExoneracoesPorContratoFP(selecionado);
        List<Long> afastamentosId = afastamentoFacade.buscarIdsAfastamentosPorContratoFP(selecionado);
        List<Long> cedenciasId = cedenciaContratoFPFacade.buscarIdsCedenciasPorContratoFP(selecionado);
        selecionado.setEventosEsocial(contratoFPFacade.getEventoPorIdentificador(selecionado.getId(), StringUtil.retornaApenasNumeros(selecionado.getMatriculaFP().getPessoa().getCpf()), fichasId, exoneracoesId, afastamentosId, cedenciasId));
    }

    public List<SelectItem> getItemTipoEventoEsocial() {
        return Util.getListSelectItem(TipoArquivoESocial.getEventosBasicosContratoFP());
    }

    public void enviarEvento() {
        AssistenteBarraProgresso assistenteBarraProgresso = new AssistenteBarraProgresso();
        try {
            configuracaoEmpregadorESocial = contratoFPFacade.buscarEmpregadorPorVinculoFP(selecionado);
            validarEvento();
            PessoaFisica pf = pessoaFisicaFacade.recuperarComDocumentos(selecionado.getMatriculaFP().getPessoa().getId());
            selecionado.getMatriculaFP().setPessoa(pf);
            VWContratoFP contratoFPDto = new VWContratoFP();
            contratoFPDto.setId(selecionado.getId());
            if (TipoArquivoESocial.S2200.equals(tipoArquivoESocial)) {
                contratoFPFacade.enviarS2200ESocial(configuracaoEmpregadorESocial, contratoFPDto, sistemaControlador.getDataOperacao(), assistenteBarraProgresso);
            }
            if (TipoArquivoESocial.S1200.equals(tipoArquivoESocial)) {
                contratoFPFacade.enviarS2200ESocial(configuracaoEmpregadorESocial, contratoFPDto, sistemaControlador.getDataOperacao(), assistenteBarraProgresso);
            }
            FacesUtil.addOperacaoRealizada("Evento " + tipoArquivoESocial.getCodigo() + " enviado com sucesso!");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.printAllFacesMessages(assistenteBarraProgresso.getMensagensValidacaoFacesUtil());
        }
    }

    private void validarEvento() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoArquivoESocial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o evento a ser enviado para o e-social.");
        }
        if (configuracaoEmpregadorESocial == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado configuração de empregador do e-social para esse servidor.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTipoPeq() {
        return Util.getListSelectItem(TipoPeq.values(), false);
    }

    public boolean isHabilitaTipoPEQ() {
        boolean retorno = false;
        if (selecionado.getUnidadeOrganizacional() != null && selecionado.getUnidadeOrganizacional().getHabilitaTipoPeq()) {
            if (selecionado.getModalidadeContratoFP() != null && ModalidadeContratoFP.CONCURSADOS.equals(selecionado.getModalidadeContratoFP().getCodigo())) {
                retorno = true;
            }
        }
        return retorno;
    }

    public List<SelectItem> getTipoCadastroInicialESocial() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "Deixar na escolha do sistema de acordo com as regras do E-Social"));
        for (VinculoFP.TipoCadastroInicialVinculoFP tipo : VinculoFP.TipoCadastroInicialVinculoFP.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSegmentoAtuacao() {
        return Util.getListSelectItem(SegmentoAtuacao.values(), false);
    }

    public List<SelectItem> tiposVinculoSicap() {
        return Util.getListSelectItem(TipoVinculoSicap.values(), false);
    }

    private void validarSegmentoAtuacaoSiope() {
        ValidacaoException ve = new ValidacaoException();
        if (!Strings.isNullOrEmpty(selecionado.getLotacaoFuncionalVigente().getUnidadeOrganizacional().getCodigoInep())) {
            if (selecionado.getSegmentoAtuacao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Para a Lotação Funcional vigente deve ser preenchido o 'Segmento de Atuação' na aba 'Informações das Declarações' > 'Siope'");
            }
        }
        ve.lancarException();
    }


    public List<PrevidenciaArquivo> getPrevidenciaArquivos() {
        return previdenciaArquivos;
    }

    public void setPrevidenciaArquivos(List<PrevidenciaArquivo> previdenciaArquivos) {
        this.previdenciaArquivos = previdenciaArquivos;
    }

    public List<SelectItem> getPlanoSegregacaoMassa() {
        return Util.getListSelectItem(SegregacaoMassa.values(), false);
    }

    public List<SelectItem> getModalidadesContratacaoAprendiz() {
        return Util.getListSelectItem(ModalidadeContratacaoAprendiz.values(), false);
    }

    public List<SelectItem> getTiposPessoa() {
        return Util.getListSelectItemSemCampoVazio(TipoPessoa.values(), false);
    }

    public List<SelectItem> getHipoteseLegalContratacao() {
        return Util.getListSelectItem(HipoteseLegalContratacao.values(), false);
    }

    public List<SelectItem> getSituacoesAdmissaoBBPrev() {
        return Util.getListSelectItem(SituacaoAdmissaoBBPrev.values());
    }

    public EnderecoCorreio getEnderecoContratoTemporario() {
        return enderecoContratoTemporario;
    }

    public void setEnderecoContratoTemporario(EnderecoCorreio enderecoContratoTemporario) {
        this.enderecoContratoTemporario = enderecoContratoTemporario;
    }

    public TipoPessoa getTipoPessoaEstabelecimentoEfetivacaoAprendiz() {
        return tipoPessoaEstabelecimentoEfetivacaoAprendiz;
    }

    public void setTipoPessoaEstabelecimentoEfetivacaoAprendiz(TipoPessoa tipoPessoaEstabelecimentoEfetivacaoAprendiz) {
        this.tipoPessoaEstabelecimentoEfetivacaoAprendiz = tipoPessoaEstabelecimentoEfetivacaoAprendiz;
    }

    public void onRowSelect(SelectEvent event) {
        previdenciaArquivos = new ArrayList<>();
        previdenciaVinculoFP = new PrevidenciaVinculoFP();
        previdenciaVinculoFP = ((PrevidenciaVinculoFP) event.getObject());
        previdenciaArquivos = previdenciaVinculoFP.getPrevidenciaArquivos();
    }

    public String getQrCode() {
        gerarQRCode();
        return qrCode;
    }

    public void gerarQRCode() {
        if (selecionado.getId() == null ||
            (selecionado.getFinalVigencia() != null && selecionado.getFinalVigencia().before(new Date())) ||
            !Strings.isNullOrEmpty(qrCode)) return;
        try {
            qrCode = "data:image/png;base64, " + BarCode.generateBase64QRCodePng(contratoFPFacade.gerarLinkQRCode(selecionado), 175, 175);
        } catch (Exception e) {
            logger.error("Erro ao gerar qrCode do ContratoFP", e);
        }
    }

    public boolean isDataAdmissaoAposPromulgacaoConstituicao1988() {
        if (selecionado.getDataNomeacao() == null) {
            return true;
        }
        if (selecionado.getDataNomeacao().after(new Date(88, Calendar.OCTOBER, 5))) {
            return true;
        }
        return false;
    }

    public void atualizarLogradouros() {
        pessoaFacade.getConsultaCepFacade().atualizarLogradouros(enderecoContratoTemporario);
    }


    public Boolean isTemporario() {
        return selecionado != null && (selecionado.getCategoriaTrabalhador() != null && selecionado.getCategoriaTrabalhador().getCodigo() == TEMPORARIO);
    }

    public Boolean isAprendiz() {
        return selecionado != null && (selecionado.getCategoriaTrabalhador() != null && selecionado.getCategoriaTrabalhador().getCodigo() == APRENDIZ);
    }

}
