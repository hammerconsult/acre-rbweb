package br.com.webpublico.entidades;

import br.com.webpublico.entidades.usertype.ModalidadeContratoFPData;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Etiqueta("Provimento de Reversão")
@GrupoDiagrama(nome = "Recursos Humanos")
@Audited

public class ProvimentoReversao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Servidor")
    @Tabelavel
    @Obrigatorio
    private Aposentadoria aposentadoria;
    @ManyToOne
    @Etiqueta("Novo Contrato")
    @Tabelavel
    private ContratoFP contratoFP;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Final de Vigência")
    @Tabelavel
    @Pesquisavel
    private Date finalVigencia;
    @ManyToOne
    @Etiqueta("Ato Legal")
    @Obrigatorio
    private AtoLegal atoLegal;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Laudo do Provimento de Reversão")
    private LaudoProvimentoReversao laudoProvimentoReversao;
    @Transient
    @Etiqueta("Tipo de Aposentadoria")
    private TipoAposentadoria tipoAposentadoria;
    @Transient
    private Long criadoEm;
    @Temporal(TemporalType.DATE)
    private Date fimVigenciaAnterior;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private EnquadramentoFuncional enquadramentoFuncional;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private ModalidadeContratoFPData modalidadeContratoFPData;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private PrevidenciaVinculoFP previdenciaVinculoFP;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private OpcaoValeTransporteFP opcaoValeTransporteFP;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private HorarioContratoFP horarioContratoFP;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private LotacaoFuncional lotacaoFuncional;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private SindicatoVinculoFP sindicatoVinculoFP;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private AssociacaoVinculoFP associacaoVinculoFP;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private ContratoVinculoDeContrato contratoVinculoDeContrato;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private RecursoDoVinculoFP recursoDoVinculoFP;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private ContratoFPCargo contratoFPCargo;
    @Transient
    @Invisivel
    private ModalidadeContratoFPData modalidadeContratoFPDataAnterior;
    @Transient
    @Invisivel
    private EnquadramentoFuncional enquadramentoFuncionalAnterior;
    @Transient
    @Invisivel
    private PrevidenciaVinculoFP previdenciaAnterior;
    @Transient
    @Invisivel
    private OpcaoValeTransporteFP opcaoValeTransporteAnterior;
    @Transient
    @Invisivel
    private HorarioContratoFP horarioAnterior;
    @Transient
    @Invisivel
    private LotacaoFuncional lotacaoFuncionalAnterior;
    @Transient
    @Invisivel
    private SindicatoVinculoFP sindicatoAnterior;
    @Transient
    @Invisivel
    private AssociacaoVinculoFP associacaoAnterior;
    @Transient
    @Invisivel
    private ContratoVinculoDeContrato contratoVinculoDeContratoAnterior;
    @Transient
    @Invisivel
    private RecursoDoVinculoFP recursoDoVinculoFPAnterior;
    @Transient
    @Invisivel
    private ContratoFPCargo contratoFPCargoAnterior;

    public ProvimentoReversao() {
        this.criadoEm = System.nanoTime();
        laudoProvimentoReversao = new LaudoProvimentoReversao();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aposentadoria getAposentadoria() {
        return aposentadoria;
    }

    public void setAposentadoria(Aposentadoria aposentadoria) {
        this.aposentadoria = aposentadoria;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public LaudoProvimentoReversao getLaudoProvimentoReversao() {
        return laudoProvimentoReversao;
    }

    public void setLaudoProvimentoReversao(LaudoProvimentoReversao laudoProvimentoReversao) {
        this.laudoProvimentoReversao = laudoProvimentoReversao;
    }

    public TipoAposentadoria getTipoAposentadoria() {
        return tipoAposentadoria;
    }

    public void setTipoAposentadoria(TipoAposentadoria tipoAposentadoria) {
        this.tipoAposentadoria = tipoAposentadoria;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public EnquadramentoFuncional getEnquadramentoFuncional() {
        return enquadramentoFuncional;
    }

    public void setEnquadramentoFuncional(EnquadramentoFuncional enquadramentoFuncional) {
        this.enquadramentoFuncional = enquadramentoFuncional;
    }

    public EnquadramentoFuncional getEnquadramentoFuncionalAnterior() {
        return enquadramentoFuncionalAnterior;
    }

    public void setEnquadramentoFuncionalAnterior(EnquadramentoFuncional enquadramentoFuncionalAnterior) {
        this.enquadramentoFuncionalAnterior = enquadramentoFuncionalAnterior;
    }

    public ModalidadeContratoFPData getModalidadeContratoFPData() {
        return modalidadeContratoFPData;
    }

    public void setModalidadeContratoFPData(ModalidadeContratoFPData modalidadeContratoFPData) {
        this.modalidadeContratoFPData = modalidadeContratoFPData;
    }

    public Date getFimVigenciaAnterior() {
        return fimVigenciaAnterior;
    }

    public void setFimVigenciaAnterior(Date fimVigenciaAnterior) {
        this.fimVigenciaAnterior = fimVigenciaAnterior;
    }

    public ModalidadeContratoFPData getModalidadeContratoFPDataAnterior() {
        return modalidadeContratoFPDataAnterior;
    }

    public void setModalidadeContratoFPDataAnterior(ModalidadeContratoFPData modalidadeContratoFPDataAnterior) {
        this.modalidadeContratoFPDataAnterior = modalidadeContratoFPDataAnterior;
    }

    public PrevidenciaVinculoFP getPrevidenciaVinculoFP() {
        return previdenciaVinculoFP;
    }

    public void setPrevidenciaVinculoFP(PrevidenciaVinculoFP previdenciaVinculoFP) {
        this.previdenciaVinculoFP = previdenciaVinculoFP;
    }

    public PrevidenciaVinculoFP getPrevidenciaAnterior() {
        return previdenciaAnterior;
    }

    public void setPrevidenciaAnterior(PrevidenciaVinculoFP previdenciaAnterior) {
        this.previdenciaAnterior = previdenciaAnterior;
    }

    public OpcaoValeTransporteFP getOpcaoValeTransporteFP() {
        return opcaoValeTransporteFP;
    }

    public void setOpcaoValeTransporteFP(OpcaoValeTransporteFP opcaoValeTransporteFP) {
        this.opcaoValeTransporteFP = opcaoValeTransporteFP;
    }

    public OpcaoValeTransporteFP getOpcaoValeTransporteAnterior() {
        return opcaoValeTransporteAnterior;
    }

    public void setOpcaoValeTransporteAnterior(OpcaoValeTransporteFP opcaoValeTransporteAnterior) {
        this.opcaoValeTransporteAnterior = opcaoValeTransporteAnterior;
    }

    public HorarioContratoFP getHorarioContratoFP() {
        return horarioContratoFP;
    }

    public void setHorarioContratoFP(HorarioContratoFP horarioContratoFP) {
        this.horarioContratoFP = horarioContratoFP;
    }

    public HorarioContratoFP getHorarioAnterior() {
        return horarioAnterior;
    }

    public void setHorarioAnterior(HorarioContratoFP horarioAnterior) {
        this.horarioAnterior = horarioAnterior;
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public LotacaoFuncional getLotacaoFuncionalAnterior() {
        return lotacaoFuncionalAnterior;
    }

    public void setLotacaoFuncionalAnterior(LotacaoFuncional lotacaoFuncionalAnterior) {
        this.lotacaoFuncionalAnterior = lotacaoFuncionalAnterior;
    }

    public SindicatoVinculoFP getSindicatoVinculoFP() {
        return sindicatoVinculoFP;
    }

    public void setSindicatoVinculoFP(SindicatoVinculoFP sindicatoVinculoFP) {
        this.sindicatoVinculoFP = sindicatoVinculoFP;
    }

    public SindicatoVinculoFP getSindicatoAnterior() {
        return sindicatoAnterior;
    }

    public void setSindicatoAnterior(SindicatoVinculoFP sindicatoAnterior) {
        this.sindicatoAnterior = sindicatoAnterior;
    }

    public AssociacaoVinculoFP getAssociacaoVinculoFP() {
        return associacaoVinculoFP;
    }

    public void setAssociacaoVinculoFP(AssociacaoVinculoFP associacaoVinculoFP) {
        this.associacaoVinculoFP = associacaoVinculoFP;
    }

    public AssociacaoVinculoFP getAssociacaoAnterior() {
        return associacaoAnterior;
    }

    public void setAssociacaoAnterior(AssociacaoVinculoFP associacaoAnterior) {
        this.associacaoAnterior = associacaoAnterior;
    }

    public ContratoVinculoDeContrato getContratoVinculoDeContrato() {
        return contratoVinculoDeContrato;
    }

    public void setContratoVinculoDeContrato(ContratoVinculoDeContrato contratoVinculoDeContrato) {
        this.contratoVinculoDeContrato = contratoVinculoDeContrato;
    }

    public ContratoVinculoDeContrato getContratoVinculoDeContratoAnterior() {
        return contratoVinculoDeContratoAnterior;
    }

    public void setContratoVinculoDeContratoAnterior(ContratoVinculoDeContrato contratoVinculoDeContratoAnterior) {
        this.contratoVinculoDeContratoAnterior = contratoVinculoDeContratoAnterior;
    }

    public RecursoDoVinculoFP getRecursoDoVinculoFP() {
        return recursoDoVinculoFP;
    }

    public void setRecursoDoVinculoFP(RecursoDoVinculoFP recursoDoVinculoFP) {
        this.recursoDoVinculoFP = recursoDoVinculoFP;
    }

    public RecursoDoVinculoFP getRecursoDoVinculoFPAnterior() {
        return recursoDoVinculoFPAnterior;
    }

    public void setRecursoDoVinculoFPAnterior(RecursoDoVinculoFP recursoDoVinculoFPAnterior) {
        this.recursoDoVinculoFPAnterior = recursoDoVinculoFPAnterior;
    }

    public ContratoFPCargo getContratoFPCargo() {
        return contratoFPCargo;
    }

    public void setContratoFPCargo(ContratoFPCargo contratoFPCargo) {
        this.contratoFPCargo = contratoFPCargo;
    }

    public ContratoFPCargo getContratoFPCargoAnterior() {
        return contratoFPCargoAnterior;
    }

    public void setContratoFPCargoAnterior(ContratoFPCargo contratoFPCargoAnterior) {
        this.contratoFPCargoAnterior = contratoFPCargoAnterior;
    }
}
