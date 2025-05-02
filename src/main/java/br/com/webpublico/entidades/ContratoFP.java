/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.SituacaoContratoFPBkp;
import br.com.webpublico.entidades.rh.concursos.ClassificacaoInscricao;
import br.com.webpublico.entidades.usertype.ModalidadeContratoFPData;
import br.com.webpublico.enums.DescansoSemanal;
import br.com.webpublico.enums.TipoContagemEspecial;
import br.com.webpublico.enums.rh.TipoPeq;
import br.com.webpublico.enums.rh.TipoVinculoSicap;
import br.com.webpublico.enums.rh.esocial.HipoteseLegalContratacao;
import br.com.webpublico.enums.rh.esocial.ModalidadeContratacaoAprendiz;
import br.com.webpublico.enums.rh.esocial.SegregacaoMassa;
import br.com.webpublico.enums.rh.esocial.TipoOperacaoESocial;
import br.com.webpublico.enums.rh.previdencia.SituacaoAdmissaoBBPrev;
import br.com.webpublico.enums.rh.siope.SegmentoAtuacao;
import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.IHistoricoEsocial;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.anotacoes.*;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@CorEntidade(value = "#00FF00")
@Etiqueta(value = "Contrato Folha de Pagamento (Servidor)", genero = "M")
@Inheritance(strategy = InheritanceType.JOINED)
public class ContratoFP extends VinculoFP implements Serializable, IHistoricoEsocial {

    @Transient
    @Tabelavel
    @Etiqueta("Matrícula/Contrato")
    private String descricaoMatricula;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Admissão")
    private Date dataAdmissao;
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Cargo")
    private Cargo cargo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Modalidade de Contrato")
    @ManyToOne
    private ModalidadeContratoFP modalidadeContratoFP;
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Jornada de Trabalho")
    private JornadaDeTrabalho jornadaDeTrabalho;

    @Etiqueta("Vale Transporte")
    @OneToMany(mappedBy = "contratoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("inicioVigencia desc")
    private List<OpcaoValeTransporteFP> opcaoValeTransporteFPs;
    @Etiqueta("Previdência")
    @OneToMany(mappedBy = "contratoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("inicioVigencia desc")
    private List<PrevidenciaVinculoFP> previdenciaVinculoFPs;
    @OneToMany(mappedBy = "vinculoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataRescisao desc")
    @Invisivel
    private List<ExoneracaoRescisao> exoneracoesRescisao;
    @OneToMany(mappedBy = "contratoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("inicioVigencia desc")
    @Invisivel
    private List<PeriodoAquisitivoFL> periodosAquisitivosFLs;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Regime Jurídico")
    private TipoRegime tipoRegime;
    @Etiqueta("FGTS")
    private Boolean fgts;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de opção do FGTS")
    private Date dataOpcaoFGTS;
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Ato Legal")
    @OneToOne
    private AtoLegal atoLegal;
    @Etiqueta("Situação Funcional")
    @OneToMany(mappedBy = "contratoFP", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("inicioVigencia desc")
    private List<SituacaoContratoFP> situacaoFuncionals;

    @OneToMany(mappedBy = "contratoFP", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<SituacaoContratoFPBkp> situacaoFuncionalsBkp;

    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Nomeação")
    private Date dataNomeacao;
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Posse")
    private Date dataPosse;
    @Etiqueta("Categoria SEFIP")
    @ManyToOne
    private CategoriaSEFIP categoriaSEFIP;
    @Etiqueta("Tipo de Admissão FGTS")
    @ManyToOne
    private TipoAdmissaoFGTS tipoAdmissaoFGTS;
    @ManyToOne
    @Etiqueta("Tipo de Admissão SEFIP")
    private TipoAdmissaoSEFIP tipoAdmissaoSEFIP;
    @Etiqueta("Movimento CAGED")
    @ManyToOne
    private MovimentoCAGED movimentoCAGED;
    @Etiqueta("Tipo de Admissão RAIS")
    @ManyToOne
    private TipoAdmissaoRAIS tipoAdmissaoRAIS;
    @Etiqueta("Tipo de Ocorrência SEFIP")
    @ManyToOne
    private OcorrenciaSEFIP ocorrenciaSEFIP;
    @Etiqueta("Retenção IRRF")
    @ManyToOne
    private RetencaoIRRF retencaoIRRF;
    @ManyToOne
    @Etiqueta("Vínculo Empregatício")
    private VinculoEmpregaticio vinculoEmpregaticio;
    @Etiqueta("Fichário")
    @OrderBy("inicioVigencia desc")
    @OneToMany(mappedBy = "contratoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PastaGavetaContratoFP> pastasGavetasContratosFP;
    //atributos criados na migracao de dados
    @Invisivel
    private String codigoLotacao;
    @Invisivel
    private String descricaoLotacao;
    @Invisivel
    private String descricaoVinculo;
    @Etiqueta("Descanso Semanal")
    @Enumerated(EnumType.STRING)
    private DescansoSemanal descansoSemanal;
    @Etiqueta("Início da Isenção")
    @Temporal(TemporalType.DATE)
    private Date inicioIsencao;
    @Etiqueta("Final da Isensão")
    @Temporal(TemporalType.DATE)
    private Date finalIsencao;
    @OneToMany(mappedBy = "contratoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("inicioVigencia desc")
    @Invisivel
    private List<ContratoVinculoDeContrato> contratoVinculoDeContratos;
    @Invisivel
    @Etiqueta("Histórico de Lotações")
    @OrderBy("inicioVigencia desc")
    @OneToMany(mappedBy = "contratoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LotacaoFolhaExercicio> lotacaoFolhaExercicios;
    @ManyToOne
    private AtoLegal atoLegalPrevidencia;
    @Invisivel
    @Etiqueta("Fim do estárgio probatório")
    @Temporal(TemporalType.DATE)
    private Date fimEstagioProbatorio;
    @OneToMany(mappedBy = "contratoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("inicioVigencia desc")
    @Invisivel
    private List<ContratoFPCargo> cargos;
    @OneToMany(mappedBy = "contratoServidor", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("inicioVigencia desc")
    @Invisivel
    private List<EnquadramentoFuncional> enquadramentos;
    @OneToOne(mappedBy = "contratoFP", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private ClassificacaoInscricao classificacaoInscricao;
    @Temporal(TemporalType.DATE)
    private Date dataExercicio;
    @ManyToOne
    private SituacaoFuncional situacaoFuncional;
    @Etiqueta("Local de Trabalho")
    @Transient
    @Tabelavel
    private String localDeTrabalhoVigente;
    @Enumerated(EnumType.STRING)
    private SegmentoAtuacao segmentoAtuacao;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Transient
    private List<HorarioContratoFP> horarioContratoFPs;
    @Etiqueta("Observação")
    private String observacao;
    @Enumerated(EnumType.STRING)
    private TipoPeq tipoPeq;
    @Etiqueta("Vigências das Modalidade de Contrato FP")
    @OrderBy("inicioVigencia desc")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "contratoFP")
    private List<ModalidadeContratoFPData> modalidadeContratoFPDatas;

    @ManyToOne
    private PessoaFisica trabalhadorSubstituido;

    private String objetoContratacao;

    @Enumerated
    @Transient
    private TipoOperacaoESocial tipoOperacaoESocial;

    public List<ModalidadeContratoFPData> getModalidadeContratoFPDatas() {
        return modalidadeContratoFPDatas;
    }


    @Enumerated(EnumType.STRING)
    private SegregacaoMassa segregacaoMassa;

    @Enumerated(EnumType.STRING)
    private HipoteseLegalContratacao hipoteseLegalContratacao;

    private String justificativaTrabalhadorTemp;

    @ManyToOne(cascade = CascadeType.ALL)
    private EnderecoCorreio enderecoContratoTemporario;

    private Boolean tetoRgps;
    private Boolean abonoPermanencia;
    @Temporal(TemporalType.DATE)
    private Date inicioAbono;
    @Enumerated(EnumType.STRING)
    private TipoVinculoSicap tipoVinculoSicap;

    @Enumerated(EnumType.STRING)
    private ModalidadeContratacaoAprendiz modalidadeContrAprendiz;
    @ManyToOne
    private PessoaJuridica entidadeQualificadora;
    @ManyToOne
    private PessoaJuridica estabelecAtividadpraticas;
    @ManyToOne
    private Pessoa estabelecContratAprendiz;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação na Admissão BBPrev")
    private SituacaoAdmissaoBBPrev situacaoAdmissaoBBPrev;

    public void setModalidadeContratoFPDatas(List<ModalidadeContratoFPData> modalidadeContratoFPDatas) {
        this.modalidadeContratoFPDatas = modalidadeContratoFPDatas;
    }

    public ContratoFP() {
        contratoVinculoDeContratos = new ArrayList<>();
        fgts = false;

        setHorarioContratoFPs(new ArrayList<HorarioContratoFP>());
        setLotacaoFuncionals(new ArrayList<LotacaoFuncional>());
        setOpcaoValeTransporteFPs(new ArrayList<OpcaoValeTransporteFP>());
        setPrevidenciaVinculoFPs(new ArrayList<PrevidenciaVinculoFP>());
        setSindicatosVinculosFPs(new ArrayList<SindicatoVinculoFP>());
        setAssociacaosVinculosFPs(new ArrayList<AssociacaoVinculoFP>());
        setSituacaoFuncionals(new ArrayList<SituacaoContratoFP>());
        setLotacaoFolhaExercicios(new ArrayList<LotacaoFolhaExercicio>());
        setOpcaoSalarialVinculoFP(new ArrayList<OpcaoSalarialVinculoFP>());
        setRecursosDoVinculoFP(new ArrayList<RecursoDoVinculoFP>());
        setPeriodosAquisitivosFLs(new ArrayList<PeriodoAquisitivoFL>());
        setExoneracoesRescisao(new ArrayList<ExoneracaoRescisao>());
        setLotacaoOutrosVinculos(new ArrayList<LotacaoOutrosVinculos>());
        setFichasFinanceiraFP(new ArrayList<FichaFinanceiraFP>());
    }

    public ContratoFP(Long id) {
        setId(id);
    }

    public ContratoFP(ModalidadeContratoFP modalidadeContratoFP, BigDecimal vencimentoBase, Cargo cargo, JornadaDeTrabalho jornadaDeTrabalho, List<LotacaoFuncional> lotacaoFuncionals, UnidadeOrganizacional unidadeOrganizacional, Date inicioVigencia, Date finalVigencia, MatriculaFP matriculaFP) {
        super(unidadeOrganizacional, inicioVigencia, finalVigencia, matriculaFP);
        this.modalidadeContratoFP = modalidadeContratoFP;
        this.cargo = cargo;
        this.jornadaDeTrabalho = jornadaDeTrabalho;
        setLotacaoFuncionals(lotacaoFuncionals);
    }

    public ContratoFP(Long id, MatriculaFP matriculaFP, Date dataAdmissao, TipoAdmissaoRAIS tipoAdmissaoRAIS, JornadaDeTrabalho jornadaDeTrabalho, CBO cbo, VinculoEmpregaticio vinculoEmpregaticio) {
        setId(id);
        setMatriculaFP(matriculaFP);
        setDataAdmissao(dataAdmissao);
        setTipoAdmissaoRAIS(tipoAdmissaoRAIS);
        setJornadaDeTrabalho(jornadaDeTrabalho);
        setCbo(cbo);
        setVinculoEmpregaticio(vinculoEmpregaticio);
    }

    public ContratoFP(Long id, String descricao) {
        setId(id);
        descricaoMatricula = descricao;
    }

    //no pesquisa genérica está utilizando este construtor. Adicionado o final de vigência em 09/12/13
    public ContratoFP(Long id, String descricao, String localDeTrabalho, Date inicioVig, Cargo cargo, ModalidadeContratoFP modalidadeContratoFP1, TipoRegime tipoRegime1, MatriculaFP matriculaFP, Date finalVig) {
        setId(id);
        descricaoMatricula = descricao;
        localDeTrabalhoVigente = localDeTrabalho;
        setInicioVigencia(inicioVig);
        setFinalVigencia(finalVig);
        this.cargo = cargo;
        modalidadeContratoFP = modalidadeContratoFP1;
        tipoRegime = tipoRegime1;
        setMatriculaFP(matriculaFP);
    }

    //no pesquisa genérica está utilizando este construtor.
    public ContratoFP(Long id, String descricao, String localDeTrabalho, Date inicioVig, Cargo cargo, ModalidadeContratoFP modalidadeContratoFP1, TipoRegime tipoRegime1, MatriculaFP matriculaFP) {
        setId(id);
        descricaoMatricula = descricao;
        localDeTrabalhoVigente = localDeTrabalho;
        setInicioVigencia(inicioVig);
        this.cargo = cargo;
        modalidadeContratoFP = modalidadeContratoFP1;
        tipoRegime = tipoRegime1;
        setMatriculaFP(matriculaFP);
    }

    public ContratoFP(Long id, String localDeTrabalho, Date inicioVig, Cargo cargo, ModalidadeContratoFP modalidadeContratoFP1, TipoRegime tipoRegime1, MatriculaFP matriculaFP) {
        setId(id);
        localDeTrabalhoVigente = localDeTrabalho;
        setInicioVigencia(inicioVig);
        this.cargo = cargo;
        modalidadeContratoFP = modalidadeContratoFP1;
        tipoRegime = tipoRegime1;
        setMatriculaFP(matriculaFP);
    }

    public ContratoFP(Long id, Cargo cargo, Date inicioVigencia) {
        setId(id);
        this.cargo = cargo;
        setInicioVigencia(inicioVigencia);
    }

    public ContratoFP(Long id, Date inicioVigencia) {
        setId(id);
        cargo = cargo;
        setInicioVigencia(inicioVigencia);
    }

    public SegmentoAtuacao getSegmentoAtuacao() {
        return segmentoAtuacao;
    }

    public void setSegmentoAtuacao(SegmentoAtuacao segmentoAtuacao) {
        this.segmentoAtuacao = segmentoAtuacao;
    }

    public List<EnquadramentoFuncional> getEnquadramentos() {
        return enquadramentos;
    }

    public void setEnquadramentos(List<EnquadramentoFuncional> enquadramentos) {
        this.enquadramentos = enquadramentos;
    }

    public Date getFimEstagioProbatorio() {
        calcularFimEstagioProbatorio();
        return fimEstagioProbatorio;
    }

    public void setFimEstagioProbatorio(Date fimEstagioProbatorio) {
        this.fimEstagioProbatorio = fimEstagioProbatorio;
    }

    public ClassificacaoInscricao getClassificacaoInscricao() {
        return classificacaoInscricao;
    }

    public void setClassificacaoInscricao(ClassificacaoInscricao classificacaoInscricao) {
        this.classificacaoInscricao = classificacaoInscricao;
    }

    public List<ContratoFPCargo> getCargos() {
        return cargos;
    }

    public void setCargos(List<ContratoFPCargo> cargos) {
        this.cargos = cargos;
    }

    public List<ExoneracaoRescisao> getExoneracoesRescisao() {
        return exoneracoesRescisao;
    }

    public void setExoneracoesRescisao(List<ExoneracaoRescisao> exoneracoesRescisao) {
        this.exoneracoesRescisao = exoneracoesRescisao;
    }

    public AtoLegal getAtoLegalPrevidencia() {
        return atoLegalPrevidencia;
    }

    public void setAtoLegalPrevidencia(AtoLegal atoLegalPrevidencia) {
        this.atoLegalPrevidencia = atoLegalPrevidencia;
    }

    public VinculoEmpregaticio getVinculoEmpregaticio() {
        return vinculoEmpregaticio;
    }

    public void setVinculoEmpregaticio(VinculoEmpregaticio vinculoEmpregaticio) {
        this.vinculoEmpregaticio = vinculoEmpregaticio;
    }

    public DescansoSemanal getDescansoSemanal() {
        return descansoSemanal;
    }

    public void setDescansoSemanal(DescansoSemanal descansoSemanal) {
        this.descansoSemanal = descansoSemanal;
    }

    public TipoAdmissaoSEFIP getTipoAdmissaoSEFIP() {
        return tipoAdmissaoSEFIP;
    }

    public void setTipoAdmissaoSEFIP(TipoAdmissaoSEFIP tipoAdmissaoSEFIP) {
        this.tipoAdmissaoSEFIP = tipoAdmissaoSEFIP;
    }

    @Override
    public TipoRegime getTipoRegime() {
        return tipoRegime;
    }

    public void setTipoRegime(TipoRegime tipoRegime) {
        this.tipoRegime = tipoRegime;
    }


    public OcorrenciaSEFIP getOcorrenciaSEFIP() {
        return ocorrenciaSEFIP;
    }

    public void setOcorrenciaSEFIP(OcorrenciaSEFIP ocorrenciaSEFIP) {
        this.ocorrenciaSEFIP = ocorrenciaSEFIP;
    }

    @Override
    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public JornadaDeTrabalho getJornadaDeTrabalho() {
        return jornadaDeTrabalho;
    }

    public void setJornadaDeTrabalho(JornadaDeTrabalho jornadaDeTrabalho) {
        this.jornadaDeTrabalho = jornadaDeTrabalho;
    }

    public ModalidadeContratoFP getModalidadeContratoFP() {
        return modalidadeContratoFP;
    }

    public void setModalidadeContratoFP(ModalidadeContratoFP modalidadeContratoServidor) {
        modalidadeContratoFP = modalidadeContratoServidor;
    }

    public List<OpcaoValeTransporteFP> getOpcaoValeTransporteFPs() {
        return opcaoValeTransporteFPs;
    }

    public void setOpcaoValeTransporteFPs(List<OpcaoValeTransporteFP> opcaoValeTransporteFPs) {
        this.opcaoValeTransporteFPs = opcaoValeTransporteFPs;
    }

    public List<PrevidenciaVinculoFP> getPrevidenciaVinculoFPs() {
        return previdenciaVinculoFPs;
    }

    public void setPrevidenciaVinculoFPs(List<PrevidenciaVinculoFP> previdenciaVinculoFPs) {
        this.previdenciaVinculoFPs = previdenciaVinculoFPs;
    }

    public List<PeriodoAquisitivoFL> getPeriodosAquisitivosFLs() {
        return periodosAquisitivosFLs;
    }

    public void setPeriodosAquisitivosFLs(List<PeriodoAquisitivoFL> periodosAquisitivosFLs) {
        this.periodosAquisitivosFLs = periodosAquisitivosFLs;
    }

    public Date getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(Date dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public Boolean getFgts() {
        if (fgts == null) return Boolean.FALSE;
        return fgts;
    }

    public void setFgts(Boolean fgts) {
        this.fgts = fgts;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getDataOpcaoFGTS() {
        return dataOpcaoFGTS;
    }

    public void setDataOpcaoFGTS(Date dataOpcaoFGTS) {
        this.dataOpcaoFGTS = dataOpcaoFGTS;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public List<SituacaoContratoFP> getSituacaoFuncionals() {
        return situacaoFuncionals;
    }

    public void setSituacaoFuncionals(List<SituacaoContratoFP> situacaoFuncionals) {
        this.situacaoFuncionals = situacaoFuncionals;
    }

    public Date getDataNomeacao() {
        return dataNomeacao;
    }

    public void setDataNomeacao(Date dataNomeacao) {
        this.dataNomeacao = dataNomeacao;
    }

    public Date getDataPosse() {
        return dataPosse;
    }

    public void setDataPosse(Date dataPosse) {
        this.dataPosse = dataPosse;
    }

    public CategoriaSEFIP getCategoriaSEFIP() {
        return categoriaSEFIP;
    }

    public void setCategoriaSEFIP(CategoriaSEFIP categoriaSEFIP) {
        this.categoriaSEFIP = categoriaSEFIP;
    }

    public TipoAdmissaoFGTS getTipoAdmissaoFGTS() {
        return tipoAdmissaoFGTS;
    }

    public void setTipoAdmissaoFGTS(TipoAdmissaoFGTS tipoAdmissaoFGTS) {
        this.tipoAdmissaoFGTS = tipoAdmissaoFGTS;
    }

    public MovimentoCAGED getMovimentoCAGED() {
        return movimentoCAGED;
    }

    public void setMovimentoCAGED(MovimentoCAGED movimentoCAGED) {
        this.movimentoCAGED = movimentoCAGED;
    }

    public TipoAdmissaoRAIS getTipoAdmissaoRAIS() {
        return tipoAdmissaoRAIS;
    }

    public void setTipoAdmissaoRAIS(TipoAdmissaoRAIS tipoAdmissaoRAIS) {
        this.tipoAdmissaoRAIS = tipoAdmissaoRAIS;
    }

    public RetencaoIRRF getRetencaoIRRF() {
        return retencaoIRRF;
    }

    public void setRetencaoIRRF(RetencaoIRRF retencaoIRRF) {
        this.retencaoIRRF = retencaoIRRF;
    }

    public List<PastaGavetaContratoFP> getPastasContratosFP() {
        return pastasGavetasContratosFP;
    }

    public void setPastasContratosFP(List<PastaGavetaContratoFP> pastasContratosFP) {
        pastasGavetasContratosFP = pastasContratosFP;
    }

    public List<LotacaoFolhaExercicio> getLotacaoFolhaExercicios() {
        return lotacaoFolhaExercicios;
    }

    public void setLotacaoFolhaExercicios(List<LotacaoFolhaExercicio> lotacaoFolhaExercicios) {
        this.lotacaoFolhaExercicios = lotacaoFolhaExercicios;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<HorarioContratoFP> getHorarioContratoFPs() {
        return horarioContratoFPs;
    }

    public void setHorarioContratoFPs(List<HorarioContratoFP> horarioContratoFPs) {
        this.horarioContratoFPs = horarioContratoFPs;
    }

    public Date getDataExercicio() {
        return dataExercicio;
    }

    public void setDataExercicio(Date dataExercicio) {
        this.dataExercicio = dataExercicio;
    }

    public SituacaoFuncional getSituacaoFuncional() {
        return situacaoFuncional;
    }

    public void setSituacaoFuncional(SituacaoFuncional situacaoFuncional) {
        this.situacaoFuncional = situacaoFuncional;
    }

    public TipoVinculoSicap getTipoVinculoSicap() {
        return tipoVinculoSicap;
    }

    public void setTipoVinculoSicap(TipoVinculoSicap tipoVinculoSicap) {
        this.tipoVinculoSicap = tipoVinculoSicap;
    }

    @Override
    public String toString() {
        if (descricaoMatricula != null && !descricaoMatricula.trim().isEmpty()) {
            return descricaoMatricula;
        }
        String matricula = "";
        String nome = "";

        if (getMatriculaFP() != null) {
            matricula = getMatriculaFP().getMatricula();
        }
        if (getMatriculaFP() != null && getMatriculaFP().getPessoa() != null) {
            if (getMatriculaFP().getPessoa().getNomeTratamento() != null) {
                nome = getMatriculaFP().getPessoa().getNome() + " (" + getMatriculaFP().getPessoa().getNomeTratamento().trim() + ")";
            } else {
                nome = getMatriculaFP().getPessoa().getNome();
            }
        }
        if (getNumero() != null) {
            return matricula + "/" + getNumero() + " - " + nome;
        } else {
            return "";
        }
    }

    public Date getFinalIsencao() {
        return finalIsencao;
    }

    public void setFinalIsencao(Date finalIsencao) {
        this.finalIsencao = finalIsencao;
    }

    public Date getInicioIsencao() {
        return inicioIsencao;
    }

    public void setInicioIsencao(Date inicioIsencao) {
        this.inicioIsencao = inicioIsencao;
    }

    /**
     * Validacao de campos
     */
    public boolean temMatriculaFP() {
        return getMatriculaFP() != null;
    }

    public boolean temNumeroContrato() {
        return getNumero() != null;
    }

    public boolean temModalidadeContrato() {
        return modalidadeContratoFP != null;
    }

    public boolean temDataNomeacao() {
        return dataNomeacao != null;
    }

    public boolean temDataExercicio() {
        return getDataExercicio() != null;
    }

    public boolean temContaCorrente() {
        return getContaCorrente() != null;
    }

    public boolean temCargo() {
        return cargo != null;
    }

    public boolean temCBO() {
        return getCbo() != null;
    }

    public boolean temJornadaDeTrabalho() {
        return jornadaDeTrabalho != null;
    }

    public boolean temTipoDeRegime() {
        return tipoRegime != null;
    }

    public boolean temAtoDePessoal() {
        return atoLegal != null;
    }

    public boolean temNaturezaRendimentoDirf() {
        return getNaturezaRendimento() != null;
    }

    public boolean temUnidadeOrganizacional() {
        return getUnidadeOrganizacional() != null;
    }

    public boolean temTipoPrevidencia() {
        return !previdenciaVinculoFPs.isEmpty();
    }

    public boolean isEmptyLotacaoFuncional() {
        return getLotacaoFuncionals().isEmpty();
    }

    public boolean temCategoriaSEFIP() {
        return categoriaSEFIP != null;
    }

    public boolean temTipoAdmissaoFGTS() {
        return tipoAdmissaoFGTS != null;
    }

    public boolean temTipoAdmissaoSEFIP() {
        return tipoAdmissaoSEFIP != null;
    }

    public boolean temTipoAdmissaoRAIS() {
        return tipoAdmissaoRAIS != null;
    }

    public boolean temMovimentoCAGED() {
        return tipoAdmissaoRAIS != null;
    }

    public boolean temTipoOcorrenciaSEFIP() {
        return ocorrenciaSEFIP != null;
    }

    public boolean temRetencaoIRRF() {
        return retencaoIRRF != null;
    }

    public boolean temVinculoEmpregaticio() {
        return vinculoEmpregaticio != null;
    }

    public boolean temSituacaoContratoFP() {
        return !CollectionUtils.isEmpty(situacaoFuncionals);
    }

    public boolean temPrevidenciaVinculoFP() {
        return !CollectionUtils.isEmpty(previdenciaVinculoFPs);
    }

    public boolean temOpcaoValeTransporteFP() {
        return !CollectionUtils.isEmpty(opcaoValeTransporteFPs);
    }

    public boolean temEnquadramentoFuncional() {
        return !CollectionUtils.isEmpty(enquadramentos);
    }

    public String getCodigoLotacao() {
        return codigoLotacao;
    }

    public void setCodigoLotacao(String codigoLotacao) {
        this.codigoLotacao = codigoLotacao;
    }

    public String getDescricaoLotacao() {
        return descricaoLotacao;
    }

    public void setDescricaoLotacao(String descricaoLotacao) {
        this.descricaoLotacao = descricaoLotacao;
    }

    public String getDescricaoVinculo() {
        return descricaoVinculo;
    }

    public void setDescricaoVinculo(String descricaoVinculo) {
        this.descricaoVinculo = descricaoVinculo;
    }

    public List<ContratoVinculoDeContrato> getContratoVinculoDeContratos() {
        return contratoVinculoDeContratos;
    }

    public void setContratoVinculoDeContratos(List<ContratoVinculoDeContrato> contratoVinculoDeContratos) {
        this.contratoVinculoDeContratos = contratoVinculoDeContratos;
    }

    public String getLotacaoVigente() {
        for (LotacaoFuncional lotacaoFuncional : getLotacaoFuncionals()) {
            if (lotacaoFuncional.getFinalVigencia() == null) {
                localDeTrabalhoVigente = lotacaoFuncional.getUnidadeOrganizacional().getDescricao();
                break;
            }
        }
        return localDeTrabalhoVigente;
    }

    public List<PastaGavetaContratoFP> getPastasGavetasContratosFP() {
        return pastasGavetasContratosFP;
    }

    public void setPastasGavetasContratosFP(List<PastaGavetaContratoFP> pastasGavetasContratosFP) {
        this.pastasGavetasContratosFP = pastasGavetasContratosFP;
    }

    public String getDescricaoMatricula() {
        return descricaoMatricula;
    }

    public void setDescricaoMatricula(String descricaoMatricula) {
        this.descricaoMatricula = descricaoMatricula;
    }

    public String getLocalDeTrabalhoVigente() {
        return localDeTrabalhoVigente;
    }

    public void setLocalDeTrabalhoVigente(String localDeTrabalhoVigente) {
        this.localDeTrabalhoVigente = localDeTrabalhoVigente;
    }

    public void calcularFimEstagioProbatorio() {
        if (cargo != null && cargo.getTempoEstagioProbatorio() != null && dataNomeacao != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(dataNomeacao);
            c.add(Calendar.DAY_OF_YEAR, cargo.getTempoEstagioProbatorio());
            fimEstagioProbatorio = c.getTime();
        } else {
            fimEstagioProbatorio = null;
        }
    }

    public boolean isMultiploVinculo() {
        return getOcorrenciaSEFIP().getCodigo().compareTo(Long.parseLong("5")) == 0;
    }

    @Override
    public ContratoFP getContratoFP() {
        return this;
    }

    public ContratoFPCargo getCargoVigente(Date dataOperacao) {
        try {
            if (getCargos() != null && !getCargos().isEmpty()) {
                for (ContratoFPCargo cc : getCargos()) {
                    if (cc.isFimVigenciaNull()) {
                        return cc;
                    }

                    if (new Interval(new DateTime(cc.getInicioVigencia()), new DateTime(cc.getFimVigencia())).contains(new DateTime(dataOperacao))) {
                        return cc;
                    }
                }
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public LotacaoFuncional getLotacaoFuncionalVigente(Date dataOperacao) {
        try {
            if (getLotacaoFuncionals() != null && !getLotacaoFuncionals().isEmpty()) {
                for (LotacaoFuncional lf : getLotacaoFuncionals()) {
                    if (lf.isFinalVigenciaNull()) {
                        return lf;
                    }
                    if (new Interval(new DateTime(lf.getInicioVigencia()), new DateTime(lf.getFinalVigencia())).contains(new DateTime(dataOperacao))) {
                        return lf;
                    }
                }
            }
            return null;
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public boolean temContratoFPCargoAdicionado(ContratoFPCargo contratoFPCargo) {
        return cargos.contains(contratoFPCargo);
    }

    public boolean temLotacaoFuncionalAdicionada(LotacaoFuncional lotacaoFuncional) {
        return getLotacaoFuncionals().contains(lotacaoFuncional);
    }

    public boolean temEnquadramentoFuncionalAdicionado(EnquadramentoFuncional enquadramentoFuncional) {
        return getEnquadramentos().contains(enquadramentoFuncional);
    }

    public ContratoFPCargo getUltimoCargoEmRelacaoAoInicioDeVigencia() {
        if (cargos != null && !cargos.isEmpty()) {
            return cargos.get(0);
        }
        return null;
    }

    public LotacaoFuncional getUltimaLotacaoFuncionalEmRelacaoAoInicioDeVigencia() {
        if (getLotacaoFuncionals() != null && !getLotacaoFuncionals().isEmpty()) {
            return getLotacaoFuncionals().get(getLotacaoFuncionals().size() - 1);
        }
        return null;
    }

    public EnquadramentoFuncional getUltimoEnquadramentoFuncionalEmRelacaoAoInicioDeVigencia() {
        if (enquadramentos != null && !enquadramentos.isEmpty()) {
            Collections.sort(enquadramentos);
            return enquadramentos.get(enquadramentos.size() - 1);
        }
        return null;
    }

    public PrevidenciaVinculoFP getPrevidenciaVinculoFPVigente() {
        if (temPrevidenciaVinculoFP()) {
            ordenarPrevidenciasInicioVigenciaDesc();
            return previdenciaVinculoFPs.get(0);
        }
        return null;
    }

    public SituacaoContratoFP getSituacaoContratoFPVigente() {
        if (temSituacaoContratoFP()) {
            ordenarSituacoesContratoFPPorInicioVigenciaDesc();
            return situacaoFuncionals.get(0);
        }
        return null;
    }

    public OpcaoValeTransporteFP getOpcaoValeTransporteFPVigente() {
        if (temOpcaoValeTransporteFP()) {
            ordenarOpcoesValeTransporteFPPorInicioVigenciaDesc();
            return opcaoValeTransporteFPs.get(0);
        }
        return null;
    }

    public EnquadramentoFuncional getEnquadramentoFuncionalVigente() {
        if (temEnquadramentoFuncional()) {
            ordenarEnquadramentosPorInicioVigenciaDesc();
            return enquadramentos.get(0);
        }
        return null;
    }

    public void ordenarPrevidenciasInicioVigenciaDesc() {
        Collections.sort(previdenciaVinculoFPs, new Comparator<PrevidenciaVinculoFP>() {
            @Override
            public int compare(PrevidenciaVinculoFP o1, PrevidenciaVinculoFP o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
    }

    public void ordenarSituacoesContratoFPPorInicioVigenciaDesc() {
        Collections.sort(situacaoFuncionals, new Comparator<SituacaoContratoFP>() {
            @Override
            public int compare(SituacaoContratoFP o1, SituacaoContratoFP o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
    }

    public void ordenarOpcoesValeTransporteFPPorInicioVigenciaDesc() {
        Collections.sort(opcaoValeTransporteFPs, new Comparator<OpcaoValeTransporteFP>() {
            @Override
            public int compare(OpcaoValeTransporteFP o1, OpcaoValeTransporteFP o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
    }

    public void ordenarEnquadramentosPorInicioVigenciaDesc() {
        Collections.sort(enquadramentos, new Comparator<EnquadramentoFuncional>() {
            @Override
            public int compare(EnquadramentoFuncional o1, EnquadramentoFuncional o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
    }

    public boolean isModalidadeContratoFPConcursados() {
        return ModalidadeContratoFP.CONCURSADOS.equals(modalidadeContratoFP.getCodigo());
    }

    public boolean isModalidadeContratoFPTrabalhoTempoIndeterminado() {
        return ModalidadeContratoFP.CONTRATO_TRABALHO_TEMPO_INDETERMINADO.equals(modalidadeContratoFP.getCodigo());
    }

    public boolean isModalidadeContratoFPCargoComissao() {
        return ModalidadeContratoFP.CARGO_COMISSAO.equals(modalidadeContratoFP.getCodigo());
    }

    public boolean isModalidadeContratoFPCargoEletivo() {
        return ModalidadeContratoFP.CARGO_ELETIVO.equals(modalidadeContratoFP.getCodigo());
    }

    public boolean isTipoRegimeCeletista() {
        return TipoRegime.CELETISTA.equals(tipoRegime.getCodigo());
    }

    public boolean isTipoRegimeEstatutario() {
        return TipoRegime.ESTATUTARIO.equals(tipoRegime.getCodigo());
    }

    public boolean temTipoPrevidenciaRPPS() {
        for (PrevidenciaVinculoFP previdencia : previdenciaVinculoFPs) {
            if (previdencia.getTipoPrevidenciaFP().getCodigo() == 3) {
                return true;
            }
        }
        return false;
    }

    public boolean temDescansoSemanal() {
        return descansoSemanal != null;
    }

    public boolean temHierarquiaOrganizacional() {
        return hierarquiaOrganizacional != null;
    }

    public boolean temDataOpcaoFgts() {
        return dataOpcaoFGTS != null;
    }

    public boolean temDataPosse() {
        return dataPosse != null;
    }

    public boolean temInicioIsencao() {
        return inicioIsencao != null;
    }

    public boolean temAtoLegalPrevidencia() {
        return atoLegalPrevidencia != null;
    }

    public TipoPeq getTipoPeq() {
        return tipoPeq;
    }

    public void setTipoPeq(TipoPeq tipoPeq) {
        this.tipoPeq = tipoPeq;
    }

    public List<SituacaoContratoFPBkp> getSituacaoFuncionalsBkp() {
        return situacaoFuncionalsBkp;
    }

    public void setSituacaoFuncionalsBkp(List<SituacaoContratoFPBkp> situacaoFuncionalsBkp) {
        this.situacaoFuncionalsBkp = situacaoFuncionalsBkp;
    }

    public SegregacaoMassa getSegregacaoMassa() {
        return segregacaoMassa;
    }

    public void setSegregacaoMassa(SegregacaoMassa segregacaoMassa) {
        this.segregacaoMassa = segregacaoMassa;
    }

    public Boolean getTetoRgps() {
        return tetoRgps != null ? tetoRgps : false;
    }

    public void setTetoRgps(Boolean tetoRgps) {
        this.tetoRgps = tetoRgps;
    }

    public Boolean getAbonoPermanencia() {
        return abonoPermanencia != null ? abonoPermanencia : false;
    }

    public void setAbonoPermanencia(Boolean abonoPermanencia) {
        this.abonoPermanencia = abonoPermanencia;
    }

    public Date getInicioAbono() {
        return inicioAbono;
    }

    public void setInicioAbono(Date inicioAbono) {
        this.inicioAbono = inicioAbono;
    }

    public NivelEscolaridade getNivelEscolaridadeCargo() {
        if (!cargo.temNivelEscolaridade()) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado nível de escolaridade do cargo: " + cargo);
        }
        return cargo.getNivelEscolaridade();
    }

    public void removerEnquadramentoFuncional(EnquadramentoFuncional ef) {
        if (enquadramentos.contains(ef)) {
            enquadramentos.remove(ef);
        }
    }

    public void abrirVigenciaUltimoEnquadramentoFuncional() {
        EnquadramentoFuncional ultimoEnquadramentoFuncionalEmRelacaoAoInicioDeVigencia = getUltimoEnquadramentoFuncionalEmRelacaoAoInicioDeVigencia();
        if (ultimoEnquadramentoFuncionalEmRelacaoAoInicioDeVigencia != null) {
            ultimoEnquadramentoFuncionalEmRelacaoAoInicioDeVigencia.setFinalVigencia(null);
        }
    }

    @Override
    public Integer getCarreira() {
        return TipoContagemEspecial.getCodigoAtuarial(getCargo().getTipoContagemSIPREV());
    }

    public HipoteseLegalContratacao getHipoteseLegalContratacao() {
        return hipoteseLegalContratacao;
    }

    public void setHipoteseLegalContratacao(HipoteseLegalContratacao hipoteseLegalContratacao) {
        this.hipoteseLegalContratacao = hipoteseLegalContratacao;
    }

    public String getJustificativaTrabalhadorTemp() {
        return justificativaTrabalhadorTemp;
    }

    public void setJustificativaTrabalhadorTemp(String justificativaTrabalhadorTemp) {
        this.justificativaTrabalhadorTemp = justificativaTrabalhadorTemp;
    }

    public EnderecoCorreio getEnderecoContratoTemporario() {
        return enderecoContratoTemporario;
    }

    public void setEnderecoContratoTemporario(EnderecoCorreio enderecoContratoTemporario) {
        this.enderecoContratoTemporario = enderecoContratoTemporario;
    }

    public PessoaFisica getTrabalhadorSubstituido() {
        return trabalhadorSubstituido;
    }

    public void setTrabalhadorSubstituido(PessoaFisica trabalhadorSubstituido) {
        this.trabalhadorSubstituido = trabalhadorSubstituido;
    }

    public String getObjetoContratacao() {
        return objetoContratacao;
    }

    public void setObjetoContratacao(String objetoContratacao) {
        this.objetoContratacao = objetoContratacao;
    }

    public TipoOperacaoESocial getTipoOperacaoESocial() {
        return tipoOperacaoESocial;
    }

    public void setTipoOperacaoESocial(TipoOperacaoESocial tipoOperacaoESocial) {
        this.tipoOperacaoESocial = tipoOperacaoESocial;
    }

    public ModalidadeContratacaoAprendiz getModalidadeContrAprendiz() {
        return modalidadeContrAprendiz;
    }

    public void setModalidadeContrAprendiz(ModalidadeContratacaoAprendiz modalidadeContrAprendiz) {
        this.modalidadeContrAprendiz = modalidadeContrAprendiz;
    }

    public PessoaJuridica getEntidadeQualificadora() {
        return entidadeQualificadora;
    }

    public void setEntidadeQualificadora(PessoaJuridica entidadeQualificadora) {
        this.entidadeQualificadora = entidadeQualificadora;
    }

    public PessoaJuridica getEstabelecAtividadpraticas() {
        return estabelecAtividadpraticas;
    }

    public void setEstabelecAtividadpraticas(PessoaJuridica estabelecAtividadpraticas) {
        this.estabelecAtividadpraticas = estabelecAtividadpraticas;
    }

    public Pessoa getEstabelecContratAprendiz() {
        return estabelecContratAprendiz;
    }

    public void setEstabelecContratAprendiz(Pessoa estabelecContratAprendiz) {
        this.estabelecContratAprendiz = estabelecContratAprendiz;
    }

    public SituacaoAdmissaoBBPrev getSituacaoAdmissaoBBPrev() {
        return situacaoAdmissaoBBPrev;
    }

    public void setSituacaoAdmissaoBBPrev(SituacaoAdmissaoBBPrev situacaoAdmissaoBBPrev) {
        this.situacaoAdmissaoBBPrev = situacaoAdmissaoBBPrev;
    }


    @Override
    public String getDescricaoCompleta() {
        return toString();
    }

    @Override
    public String getIdentificador() {
        return this.getId().toString();
    }
}

