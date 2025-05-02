package br.com.webpublico.entidades;

import br.com.webpublico.enums.DescansoSemanal;
import br.com.webpublico.enums.SituacaoVinculo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@Etiqueta("Configuração de Modalidade de Contrato")
@Table(name = "CONFMODALIDADECONTRATOFP")
public class ConfiguracaoModalidadeContratoFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Modalidade do Contrato")
    @ManyToOne
    private ModalidadeContratoFP modalidadeContrato;
    @Etiqueta("Jornada de Trabalho")
    @ManyToOne
    private JornadaDeTrabalho jornadaDeTrabalho;
    @Etiqueta("Descanso Semanal")
    @Enumerated(EnumType.STRING)
    private DescansoSemanal descansoSemanal;
    @ManyToOne
    @Etiqueta("Tipo de Regime Jurídico")
    private TipoRegime tipoRegime;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação do Vínculo")
    private SituacaoVinculo situacaoVinculo;
    @Etiqueta("Previdência")
    @OneToMany(mappedBy = "confModalidadeContratoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfiguracaoPrevidenciaVinculoFP> configuracaoPrevidenciaVinculoFPs;
    @Etiqueta("SEFIP")
    private Boolean sefip;
    @Etiqueta("Categoria SEFIP")
    @ManyToOne
    private CategoriaSEFIP categoriaSEFIP;
    @Etiqueta("Tipo de Admissão FGTS")
    @ManyToOne
    private TipoAdmissaoFGTS tipoAdmissaoFGTS;
    @ManyToOne
    @Etiqueta("Tipo de Admissão SEFIP")
    private TipoAdmissaoSEFIP tipoAdmissaoSEFIP;
    @Etiqueta("Tipo de Admissão RAIS")
    @ManyToOne
    private TipoAdmissaoRAIS tipoAdmissaoRAIS;
    @Etiqueta("Movimento CAGED")
    @ManyToOne
    private MovimentoCAGED movimentoCAGED;
    @Etiqueta("Tipo de Ocorrência SEFIP")
    @ManyToOne
    private OcorrenciaSEFIP ocorrenciaSEFIP;
    @Etiqueta("Retenção IRRF")
    @ManyToOne
    private RetencaoIRRF retencaoIRRF;
    @ManyToOne
    @Etiqueta("Vínculo Empregatício")
    private VinculoEmpregaticio vinculoEmpregaticio;
    @Etiqueta("Natureza de Rendimento DIRF")
    @ManyToOne
    private NaturezaRendimento naturezaRendimento;
    @ManyToOne
    private HorarioDeTrabalho horarioDeTrabalho;
    @Transient
    private ConfiguracaoPrevidenciaVinculoFP configuracaoPrevidencia;

    public ConfiguracaoModalidadeContratoFP() {
        this.configuracaoPrevidenciaVinculoFPs = Lists.newArrayList();
    }

    public ConfiguracaoModalidadeContratoFP(ModalidadeContratoFP modalidade) {
        this.modalidadeContrato = modalidade;
        this.configuracaoPrevidenciaVinculoFPs = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<ConfiguracaoPrevidenciaVinculoFP> getConfiguracaoPrevidenciaVinculoFPs() {
        return configuracaoPrevidenciaVinculoFPs;
    }

    public void setConfiguracaoPrevidenciaVinculoFPs(List<ConfiguracaoPrevidenciaVinculoFP> configuracaoPrevidenciaVinculoFPs) {
        this.configuracaoPrevidenciaVinculoFPs = configuracaoPrevidenciaVinculoFPs;
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

    public ConfiguracaoPrevidenciaVinculoFP getConfiguracaoPrevidencia() {
        return configuracaoPrevidencia;
    }

    public void setConfiguracaoPrevidencia(ConfiguracaoPrevidenciaVinculoFP configuracaoPrevidencia) {
        this.configuracaoPrevidencia = configuracaoPrevidencia;
    }

    public HorarioDeTrabalho getHorarioDeTrabalho() {
        return horarioDeTrabalho;
    }

    public void setHorarioDeTrabalho(HorarioDeTrabalho horarioDeTrabalho) {
        this.horarioDeTrabalho = horarioDeTrabalho;
    }

    public void addConfiguracaoPrevidencia(ConfiguracaoPrevidenciaVinculoFP config){
        this.configuracaoPrevidenciaVinculoFPs.add(config);
        limpaConfiguracaoPrevidencia();
    }

    public void limpaConfiguracaoPrevidencia(){
        this.configuracaoPrevidencia = new ConfiguracaoPrevidenciaVinculoFP();
    }
}
