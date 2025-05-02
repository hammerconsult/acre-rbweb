package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoMonitoramentoFiscal;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author octavio
 */
@GrupoDiagrama(nome = "Fiscalização")
@Etiqueta(value = "Monitoramento Fiscal")
@Entity
@Audited
public class MonitoramentoFiscal extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta(value = "Ordem Geral de Monitoramento")
    private OrdemGeralMonitoramento ordemGeralMonitoramento;
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta(value = "C.M.C")
    private CadastroEconomico cadastroEconomico;
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta(value = "Data Inicial do Monitoramento")
    private Date dataInicialMonitoramento;
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta(value = "Data Final do Monitoramento")
    private Date dataFinalMonitoramento;
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Situação Monitoramento Fiscal")
    private SituacaoMonitoramentoFiscal situacaoMF;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "monitoramentoFiscal", orphanRemoval = true)
    private List<FiscalMonitoramento> fiscaisMonitoramento;
    private String motivoCancelamento;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "monitoramentoFiscal", orphanRemoval = true)
    private List<HistoricoSituacaoMonitoramentoFiscal> historicoSituacoesMonitoramentoFiscal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "monitoramentoFiscal", orphanRemoval = true)
    private List<LevantamentoUFMMonitoramentoFiscal> levantamentosUFM;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do Arbitramento")
    private Date dataArbitramento;
    @Etiqueta("UFM do Arbitramento")
    @Monetario
    private BigDecimal ufmArbitramento;
    @Etiqueta(value = "Data Inicial do Levant.")
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataLevantamentoInicial;
    @Tabelavel
    @Etiqueta(value = "Data Final do Levant.")
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataLevantamentoFinal;
    private String conclusao;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "monitoramentoFiscal", orphanRemoval = true)
    private List<RegistroLancamentoContabilMonitoramentoFiscal> registrosLancamentoContabilMonitoramentoFiscal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "monitoramentoFiscal", orphanRemoval = true)
    private List<MalaDiretaMonitoramentoFiscal> malasDiretasMonitoramentoFiscal;
    private String observacao;
    private Boolean empresaIrregular;
    @ManyToOne
    private Irregularidade irregularidade;
    private String observacaoIrregularidade;
    @ManyToOne
    private UsuarioSistema usuarioIrregularidade;

    public MonitoramentoFiscal() {
        fiscaisMonitoramento = Lists.newArrayList();
        historicoSituacoesMonitoramentoFiscal = Lists.newArrayList();
        levantamentosUFM = Lists.newArrayList();
        registrosLancamentoContabilMonitoramentoFiscal = Lists.newArrayList();
        malasDiretasMonitoramentoFiscal = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Date getDataInicialMonitoramento() {
        return dataInicialMonitoramento;
    }

    public void setDataInicialMonitoramento(Date dataInicialMonitoramento) {
        this.dataInicialMonitoramento = dataInicialMonitoramento;
    }

    public Date getDataFinalMonitoramento() {
        return dataFinalMonitoramento;
    }

    public void setDataFinalMonitoramento(Date dataFinalMonitoramento) {
        this.dataFinalMonitoramento = dataFinalMonitoramento;
    }

    public SituacaoMonitoramentoFiscal getSituacaoMF() {
        return situacaoMF;
    }

    public void setSituacaoMF(SituacaoMonitoramentoFiscal situacaoMF) {
        this.situacaoMF = situacaoMF;
    }

    public List<FiscalMonitoramento> getFiscaisMonitoramento() {
        return fiscaisMonitoramento;
    }

    public void setFiscaisMonitoramento(List<FiscalMonitoramento> fiscaisMonitoramento) {
        this.fiscaisMonitoramento = fiscaisMonitoramento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento != null ? motivoCancelamento : "";
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public OrdemGeralMonitoramento getOrdemGeralMonitoramento() {
        return ordemGeralMonitoramento;
    }

    public void setOrdemGeralMonitoramento(OrdemGeralMonitoramento ordemGeralMonitoramento) {
        this.ordemGeralMonitoramento = ordemGeralMonitoramento;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public List<HistoricoSituacaoMonitoramentoFiscal> getHistoricoSituacoesMonitoramentoFiscal() {
        Collections.sort(historicoSituacoesMonitoramentoFiscal);
        return historicoSituacoesMonitoramentoFiscal;
    }

    public void setHistoricoSituacoesMonitoramentoFiscal(List<HistoricoSituacaoMonitoramentoFiscal> historicoSituacoesMonitoramentoFiscal) {
        this.historicoSituacoesMonitoramentoFiscal = historicoSituacoesMonitoramentoFiscal;
    }

    public Date getDataArbitramento() {
        return dataArbitramento;
    }

    public void setDataArbitramento(Date dataArbitramento) {
        this.dataArbitramento = dataArbitramento;
    }

    public BigDecimal getUfmArbitramento() {
        return ufmArbitramento;
    }

    public void setUfmArbitramento(BigDecimal ufmArbitramento) {
        this.ufmArbitramento = ufmArbitramento;
    }

    public List<LevantamentoUFMMonitoramentoFiscal> getLevantamentosUFM() {
        return levantamentosUFM;
    }

    public void setLevantamentosUFM(List<LevantamentoUFMMonitoramentoFiscal> levantamentosUFM) {
        this.levantamentosUFM = levantamentosUFM;
    }

    public Date getDataLevantamentoInicial() {
        return dataLevantamentoInicial;
    }

    public void setDataLevantamentoInicial(Date dataLevantamentoInicial) {
        this.dataLevantamentoInicial = dataLevantamentoInicial;
    }

    public Date getDataLevantamentoFinal() {
        return dataLevantamentoFinal;
    }

    public void setDataLevantamentoFinal(Date dataLevantamentoFinal) {
        this.dataLevantamentoFinal = dataLevantamentoFinal;
    }

    public String getConclusao() {
        return conclusao;
    }

    public void setConclusao(String conclusao) {
        this.conclusao = conclusao;
    }

    public List<RegistroLancamentoContabilMonitoramentoFiscal> getRegistrosLancamentoContabilMonitoramentoFiscal() {
        return registrosLancamentoContabilMonitoramentoFiscal;
    }

    public void setRegistrosLancamentoContabilMonitoramentoFiscal(List<RegistroLancamentoContabilMonitoramentoFiscal> registrosLancamentoContabilMonitoramentoFiscal) {
        this.registrosLancamentoContabilMonitoramentoFiscal = registrosLancamentoContabilMonitoramentoFiscal;
    }

    public List<MalaDiretaMonitoramentoFiscal> getMalasDiretasMonitoramentoFiscal() {
        return malasDiretasMonitoramentoFiscal;
    }

    public void setMalasDiretasMonitoramentoFiscal(List<MalaDiretaMonitoramentoFiscal> malasDiretasMonitoramentoFiscal) {
        this.malasDiretasMonitoramentoFiscal = malasDiretasMonitoramentoFiscal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Boolean getEmpresaIrregular() {
        return empresaIrregular;
    }

    public void setEmpresaIrregular(Boolean empresaIrregular) {
        this.empresaIrregular = empresaIrregular;
    }

    public Irregularidade getIrregularidade() {
        return irregularidade;
    }

    public void setIrregularidade(Irregularidade irregularidade) {
        this.irregularidade = irregularidade;
    }

    public String getObservacaoIrregularidade() {
        return observacaoIrregularidade;
    }

    public void setObservacaoIrregularidade(String observacaoIrregularidade) {
        this.observacaoIrregularidade = observacaoIrregularidade;
    }

    public UsuarioSistema getUsuarioIrregularidade() {
        return usuarioIrregularidade;
    }

    public void setUsuarioIrregularidade(UsuarioSistema usuarioIrregularidade) {
        this.usuarioIrregularidade = usuarioIrregularidade;
    }

    public void alterarSituacaoAdicionandoHistorico(SituacaoMonitoramentoFiscal situacao) {
        if (this.getSituacaoMF() == null || !situacao.equals(this.getSituacaoMF())) {
            this.setSituacaoMF(situacao);

            HistoricoSituacaoMonitoramentoFiscal historico = new HistoricoSituacaoMonitoramentoFiscal();
            historico.setMonitoramentoFiscal(this);
            historico.setData(new Date());
            historico.setSituacaoMonitoramentoFiscal(situacao);
            this.getHistoricoSituacoesMonitoramentoFiscal().add(historico);
        }
    }

    @Override
    public String toString() {
        return (ordemGeralMonitoramento != null ? ordemGeralMonitoramento.getNumero() : "") + " " +
            cadastroEconomico.toString() + " - " + DataUtil.getDataFormatada(dataInicialMonitoramento);
    }
}
