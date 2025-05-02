/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.rh.TipoReintegracao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Reintegração do Servidor")
public class Reintegracao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Servidor")
    @ManyToOne
    private ContratoFP contratoFP;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Ato Legal")
    @ManyToOne
    private AtoLegal atoLegal;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Data da Reintegração")
    @Temporal(TemporalType.DATE)
    private Date dataReintegracao;
    @Temporal(TemporalType.DATE)
    private Date fimVigenciaAnterior;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Reintegração")
    private TipoReintegracao tipoReintegracao;
    @Etiqueta("Número do Processo Judicial")
    private String numeroProcessoJudicial;
    @Etiqueta("Número da Lei de Anistia")
    private String numeroLeiAnistia;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data de Início dos Efeitos Financeiros")
    private Date inicioEfeitosFinanceiros;
    private Boolean remuneracaoPagoEmJuizo;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private EnquadramentoFuncional enquadramentoFuncional;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private PrevidenciaVinculoFP previdenciaVinculoFP;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private OpcaoValeTransporteFP opcaoValeTransporteFP;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private SindicatoVinculoFP sindicatoVinculoFP;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private AssociacaoVinculoFP associacaoVinculoFP;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private HorarioContratoFP horarioContratoFP;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private ProvimentoFP provimentoFP;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private LotacaoFuncional lotacaoFuncional;
    @Transient
    @Invisivel
    private EnquadramentoFuncional enquadramentoAnterior;
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
    private SindicatoVinculoFP sindicatoAnterior;
    @Transient
    @Invisivel
    private AssociacaoVinculoFP associacaoAnterior;
    @Transient
    @Invisivel
    private SituacaoContratoFP situacaoAnterior;
    @Transient
    @Invisivel
    private LotacaoFuncional lotacaoFuncionalAnterior;
    private String observacao;
    @ManyToOne
    private ExoneracaoRescisao exoneracaoRescisao;

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getFimVigenciaAnterior() {
        return fimVigenciaAnterior;
    }

    public void setFimVigenciaAnterior(Date fimVigenciaAnterior) {
        this.fimVigenciaAnterior = fimVigenciaAnterior;
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getDataReintegracao() {
        return dataReintegracao;
    }

    public void setDataReintegracao(Date dataReintegracao) {
        this.dataReintegracao = dataReintegracao;
    }

    public EnquadramentoFuncional getEnquadramentoFuncional() {
        return enquadramentoFuncional;
    }

    public void setEnquadramentoFuncional(EnquadramentoFuncional enquadramentoFuncional) {
        this.enquadramentoFuncional = enquadramentoFuncional;
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

    public HorarioContratoFP getHorarioContratoFP() {
        return horarioContratoFP;
    }

    public void setHorarioContratoFP(HorarioContratoFP horarioContratoFP) {
        this.horarioContratoFP = horarioContratoFP;
    }

    public PrevidenciaVinculoFP getPrevidenciaAnterior() {
        return previdenciaAnterior;
    }

    public void setPrevidenciaAnterior(PrevidenciaVinculoFP previdenciaAnterior) {
        this.previdenciaAnterior = previdenciaAnterior;
    }

    public EnquadramentoFuncional getEnquadramentoAnterior() {
        return enquadramentoAnterior;
    }

    public void setEnquadramentoAnterior(EnquadramentoFuncional enquadramentoAnterior) {
        this.enquadramentoAnterior = enquadramentoAnterior;
    }

    public OpcaoValeTransporteFP getOpcaoValeTransporteAnterior() {
        return opcaoValeTransporteAnterior;
    }

    public void setOpcaoValeTransporteAnterior(OpcaoValeTransporteFP opcaoValeTransporteAnterior) {
        this.opcaoValeTransporteAnterior = opcaoValeTransporteAnterior;
    }

    public HorarioContratoFP getHorarioAnterior() {
        return horarioAnterior;
    }

    public void setHorarioAnterior(HorarioContratoFP horarioAnterior) {
        this.horarioAnterior = horarioAnterior;
    }

    public SindicatoVinculoFP getSindicatoAnterior() {
        return sindicatoAnterior;
    }

    public void setSindicatoAnterior(SindicatoVinculoFP sindicatoAnterior) {
        this.sindicatoAnterior = sindicatoAnterior;
    }

    public AssociacaoVinculoFP getAssociacaoAnterior() {
        return associacaoAnterior;
    }

    public void setAssociacaoAnterior(AssociacaoVinculoFP associacaoAnterior) {
        this.associacaoAnterior = associacaoAnterior;
    }

    public SituacaoContratoFP getSituacaoAnterior() {
        return situacaoAnterior;
    }

    public void setSituacaoAnterior(SituacaoContratoFP situacaoAnterior) {
        this.situacaoAnterior = situacaoAnterior;
    }

    public LotacaoFuncional getLotacaoFuncionalAnterior() {
        return lotacaoFuncionalAnterior;
    }

    public void setLotacaoFuncionalAnterior(LotacaoFuncional lotacaoFuncionalAnterior) {
        this.lotacaoFuncionalAnterior = lotacaoFuncionalAnterior;
    }

    public TipoReintegracao getTipoReintegracao() {
        return tipoReintegracao;
    }

    public void setTipoReintegracao(TipoReintegracao tipoReintegracao) {
        this.tipoReintegracao = tipoReintegracao;
    }

    public String getNumeroProcessoJudicial() {
        return numeroProcessoJudicial;
    }

    public void setNumeroProcessoJudicial(String numeroProcessoJudicial) {
        this.numeroProcessoJudicial = numeroProcessoJudicial;
    }

    public String getNumeroLeiAnistia() {
        return numeroLeiAnistia;
    }

    public void setNumeroLeiAnistia(String numeroLeiAnistia) {
        this.numeroLeiAnistia = numeroLeiAnistia;
    }

    public Date getInicioEfeitosFinanceiros() {
        return inicioEfeitosFinanceiros;
    }

    public void setInicioEfeitosFinanceiros(Date inicioEfeitosFinanceiros) {
        this.inicioEfeitosFinanceiros = inicioEfeitosFinanceiros;
    }

    public Boolean getRemuneracaoPagoEmJuizo() {
        return remuneracaoPagoEmJuizo != null ? remuneracaoPagoEmJuizo : false;
    }

    public void setRemuneracaoPagoEmJuizo(Boolean remuneracaoPagoEmJuizo) {
        this.remuneracaoPagoEmJuizo = remuneracaoPagoEmJuizo;
    }

    public ExoneracaoRescisao getExoneracaoRescisao() {
        return exoneracaoRescisao;
    }

    public void setExoneracaoRescisao(ExoneracaoRescisao exoneracaoRescisao) {
        this.exoneracaoRescisao = exoneracaoRescisao;
    }

    @Override
    public String toString() {
        return contratoFP + " - " + dataReintegracao + " - REINTEGRAÇÃO";
    }
}
