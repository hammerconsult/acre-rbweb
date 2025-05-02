package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.DescansoSemanal;
import br.com.webpublico.enums.SituacaoVinculo;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author peixe on 12/04/2017  15:47.
 */
public class ImportacaoContratoFP implements Serializable {

    private Cargo cargo;
    private CBO cbo;
    private Date inicioVigencia;
    private ModalidadeContratoFP modalidadeContrato;
    private JornadaDeTrabalho jornadaDeTrabalho;
    private DescansoSemanal descansoSemanal;
    private TipoRegime tipoRegime;
    private SituacaoVinculo situacaoVinculo;
    private TipoPrevidenciaFP tipoPrevidenciaFP;

    private Boolean sefip;
    private CategoriaSEFIP categoriaSEFIP;
    private TipoAdmissaoFGTS tipoAdmissaoFGTS;
    private TipoAdmissaoSEFIP tipoAdmissaoSEFIP;
    private TipoAdmissaoRAIS tipoAdmissaoRAIS;
    private MovimentoCAGED movimentoCAGED;
    private OcorrenciaSEFIP ocorrenciaSEFIP;
    private RetencaoIRRF retencaoIRRF;
    private VinculoEmpregaticio vinculoEmpregaticio;
    private NaturezaRendimento naturezaRendimento;
    private HorarioDeTrabalho horarioDeTrabalho;
    private AtoLegal atoLegal;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private UnidadeOrganizacional unidadeOrganizacional;
    private RecursoFP recursoFP;


    private EnquadramentoPCS enquadramento;
    private PlanoCargosSalarios planoCargosSalarios;

    private ProgressaoPCS progressaoPCSPai;
    private CategoriaPCS categoriaPCSfilha;
    private CategoriaPCS categoriaPCS;
    private ProgressaoPCS progressaoPCS;
    private VinculoDeContratoFP vinculoDeContratoFP;

    public ImportacaoContratoFP() {
    }

    public ModalidadeContratoFP getModalidadeContrato() {
        return modalidadeContrato;
    }

    public void setModalidadeContrato(ModalidadeContratoFP modalidadeContrato) {
        this.modalidadeContrato = modalidadeContrato;
    }

    public JornadaDeTrabalho getJornadaDeTrabalho() {
        return jornadaDeTrabalho;
    }

    public void setJornadaDeTrabalho(JornadaDeTrabalho jornadaDeTrabalho) {
        this.jornadaDeTrabalho = jornadaDeTrabalho;
    }

    public DescansoSemanal getDescansoSemanal() {
        return descansoSemanal;
    }

    public void setDescansoSemanal(DescansoSemanal descansoSemanal) {
        this.descansoSemanal = descansoSemanal;
    }

    public TipoRegime getTipoRegime() {
        return tipoRegime;
    }

    public void setTipoRegime(TipoRegime tipoRegime) {
        this.tipoRegime = tipoRegime;
    }

    public SituacaoVinculo getSituacaoVinculo() {
        return situacaoVinculo;
    }

    public void setSituacaoVinculo(SituacaoVinculo situacaoVinculo) {
        this.situacaoVinculo = situacaoVinculo;
    }

    public TipoPrevidenciaFP getTipoPrevidenciaFP() {
        return tipoPrevidenciaFP;
    }

    public void setTipoPrevidenciaFP(TipoPrevidenciaFP tipoPrevidenciaFP) {
        this.tipoPrevidenciaFP = tipoPrevidenciaFP;
    }

    public Boolean getSefip() {
        return sefip;
    }

    public void setSefip(Boolean sefip) {
        this.sefip = sefip;
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

    public TipoAdmissaoSEFIP getTipoAdmissaoSEFIP() {
        return tipoAdmissaoSEFIP;
    }

    public void setTipoAdmissaoSEFIP(TipoAdmissaoSEFIP tipoAdmissaoSEFIP) {
        this.tipoAdmissaoSEFIP = tipoAdmissaoSEFIP;
    }

    public TipoAdmissaoRAIS getTipoAdmissaoRAIS() {
        return tipoAdmissaoRAIS;
    }

    public void setTipoAdmissaoRAIS(TipoAdmissaoRAIS tipoAdmissaoRAIS) {
        this.tipoAdmissaoRAIS = tipoAdmissaoRAIS;
    }

    public MovimentoCAGED getMovimentoCAGED() {
        return movimentoCAGED;
    }

    public void setMovimentoCAGED(MovimentoCAGED movimentoCAGED) {
        this.movimentoCAGED = movimentoCAGED;
    }

    public OcorrenciaSEFIP getOcorrenciaSEFIP() {
        return ocorrenciaSEFIP;
    }

    public void setOcorrenciaSEFIP(OcorrenciaSEFIP ocorrenciaSEFIP) {
        this.ocorrenciaSEFIP = ocorrenciaSEFIP;
    }

    public RetencaoIRRF getRetencaoIRRF() {
        return retencaoIRRF;
    }

    public void setRetencaoIRRF(RetencaoIRRF retencaoIRRF) {
        this.retencaoIRRF = retencaoIRRF;
    }

    public VinculoEmpregaticio getVinculoEmpregaticio() {
        return vinculoEmpregaticio;
    }

    public void setVinculoEmpregaticio(VinculoEmpregaticio vinculoEmpregaticio) {
        this.vinculoEmpregaticio = vinculoEmpregaticio;
    }

    public NaturezaRendimento getNaturezaRendimento() {
        return naturezaRendimento;
    }

    public void setNaturezaRendimento(NaturezaRendimento naturezaRendimento) {
        this.naturezaRendimento = naturezaRendimento;
    }

    public HorarioDeTrabalho getHorarioDeTrabalho() {
        return horarioDeTrabalho;
    }

    public void setHorarioDeTrabalho(HorarioDeTrabalho horarioDeTrabalho) {
        this.horarioDeTrabalho = horarioDeTrabalho;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public RecursoFP getRecursoFP() {
        return recursoFP;
    }

    public void setRecursoFP(RecursoFP recursoFP) {
        this.recursoFP = recursoFP;
    }

    public EnquadramentoPCS getEnquadramento() {
        return enquadramento;
    }

    public void setEnquadramento(EnquadramentoPCS enquadramento) {
        this.enquadramento = enquadramento;
    }

    public PlanoCargosSalarios getPlanoCargosSalarios() {
        return planoCargosSalarios;
    }

    public void setPlanoCargosSalarios(PlanoCargosSalarios planoCargosSalarios) {
        this.planoCargosSalarios = planoCargosSalarios;
    }

    public ProgressaoPCS getProgressaoPCSPai() {
        return progressaoPCSPai;
    }

    public void setProgressaoPCSPai(ProgressaoPCS progressaoPCSPai) {
        this.progressaoPCSPai = progressaoPCSPai;
    }

    public CategoriaPCS getCategoriaPCSfilha() {
        return categoriaPCSfilha;
    }

    public void setCategoriaPCSfilha(CategoriaPCS categoriaPCSfilha) {
        this.categoriaPCSfilha = categoriaPCSfilha;
    }

    public ProgressaoPCS getProgressaoPCS() {
        return progressaoPCS;
    }

    public void setProgressaoPCS(ProgressaoPCS progressaoPCS) {
        this.progressaoPCS = progressaoPCS;
    }

    public CategoriaPCS getCategoriaPCS() {
        return categoriaPCS;
    }

    public void setCategoriaPCS(CategoriaPCS categoriaPCS) {
        this.categoriaPCS = categoriaPCS;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public VinculoDeContratoFP getVinculoDeContratoFP() {
        return vinculoDeContratoFP;
    }

    public void setVinculoDeContratoFP(VinculoDeContratoFP vinculoDeContratoFP) {
        this.vinculoDeContratoFP = vinculoDeContratoFP;
    }

    public CBO getCbo() {
        return cbo;
    }

    public void setCbo(CBO cbo) {
        this.cbo = cbo;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }
}
