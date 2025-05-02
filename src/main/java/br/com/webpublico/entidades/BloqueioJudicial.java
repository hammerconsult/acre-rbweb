package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributário")
@Etiqueta("Processo de Bloqueio Judicial de Débitos")
public class BloqueioJudicial extends SuperEntidade implements PossuidorArquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do Processo")
    private Date dataProcesso;
    @Pesquisavel
    @Tabelavel(protocolo = true)
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    @Pesquisavel
    @Etiqueta("Ano do Protocolo")
    private String anoProtocolo;
    @Etiqueta("Motivo")
    private String motivo;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioBloqueio;
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 7)
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoProcessoDebito situacao;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Penhora")
    private Date dataPenhora;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Contribuinte da Penhora")
    private Pessoa contribuintePenhora;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Processo Judicial")
    private ProcessoJudicial processoJudicial;
    @Etiqueta("Valor Penhorado")
    private BigDecimal valorPenhora;
    @OneToMany(mappedBy = "bloqueioJudicial", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Parcelas")
    private List<ParcelaBloqueioJudicial> parcelas;
    @OneToMany(mappedBy = "bloqueioJudicial", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Histórico de Situações")
    private List<HistoricoBloqueioJudicial> historicoSituacoes;
    @OneToMany(mappedBy = "bloqueioJudicial", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("CDA's do Bloqueio Judicial")
    private List<CDABloqueioJudicial> cdasBloqueioJudicial;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Bloqueio")
    private Date dataBloqueio;
    @ManyToOne
    @Etiqueta("Contribuinte do Bloqueio")
    private Pessoa contribuinteBloqueio;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Valor Bloqueado")
    private BigDecimal valorBloqueio;
    @ManyToOne
    @Etiqueta("Processo de Cálculo")
    private ProcessoCalcBloqJudicial processoCalculo;
    private BigDecimal valorSomaParcelas;
    private BigDecimal valorResidual;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorArquivoComposicao detentorArquivo;

    @Transient
    LoteBaixa loteBaixa;

    public BloqueioJudicial() {
        this.parcelas = Lists.newArrayList();
        this.historicoSituacoes = Lists.newArrayList();
        this.cdasBloqueioJudicial = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataProcesso() {
        return dataProcesso;
    }

    public void setDataProcesso(Date dataProcesso) {
        this.dataProcesso = dataProcesso;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public UsuarioSistema getUsuarioBloqueio() {
        return usuarioBloqueio;
    }

    public void setUsuarioBloqueio(UsuarioSistema usuarioBloqueio) {
        this.usuarioBloqueio = usuarioBloqueio;
    }

    public SituacaoProcessoDebito getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProcessoDebito situacao) {
        this.situacao = situacao;
    }

    public Date getDataPenhora() {
        return dataPenhora;
    }

    public void setDataPenhora(Date dataPenhora) {
        this.dataPenhora = dataPenhora;
    }

    public Pessoa getContribuintePenhora() {
        return contribuintePenhora;
    }

    public void setContribuintePenhora(Pessoa contribuintePenhora) {
        this.contribuintePenhora = contribuintePenhora;
    }

    public ProcessoJudicial getProcessoJudicial() {
        return processoJudicial;
    }

    public void setProcessoJudicial(ProcessoJudicial processoJudicial) {
        this.processoJudicial = processoJudicial;
    }

    public BigDecimal getValorPenhora() {
        return valorPenhora;
    }

    public void setValorPenhora(BigDecimal valorPenhora) {
        this.valorPenhora = valorPenhora;
    }

    public List<ParcelaBloqueioJudicial> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<ParcelaBloqueioJudicial> parcelas) {
        this.parcelas = parcelas;
    }

    public List<HistoricoBloqueioJudicial> getHistoricoSituacoes() {
        return historicoSituacoes;
    }

    public void setHistoricoSituacoes(List<HistoricoBloqueioJudicial> historicoSituacoes) {
        this.historicoSituacoes = historicoSituacoes;
    }

    public List<CDABloqueioJudicial> getCdasBloqueioJudicial() {
        return cdasBloqueioJudicial;
    }

    public void setCdasBloqueioJudicial(List<CDABloqueioJudicial> cdasBloqueioJudicial) {
        this.cdasBloqueioJudicial = cdasBloqueioJudicial;
    }

    public Date getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public Pessoa getContribuinteBloqueio() {
        return contribuinteBloqueio;
    }

    public void setContribuinteBloqueio(Pessoa contribuinteBloqueio) {
        this.contribuinteBloqueio = contribuinteBloqueio;
    }

    public BigDecimal getValorBloqueio() {
        return valorBloqueio;
    }

    public void setValorBloqueio(BigDecimal valorBloqueio) {
        this.valorBloqueio = valorBloqueio;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivo;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivo) {
        this.detentorArquivo = detentorArquivo;
    }

    public ProcessoCalcBloqJudicial getProcessoCalculo() {
        return processoCalculo;
    }

    public void setProcessoCalculo(ProcessoCalcBloqJudicial processoCalculo) {
        this.processoCalculo = processoCalculo;
    }

    public BigDecimal getValorSomaParcelas() {
        return valorSomaParcelas;
    }

    public void setValorSomaParcelas(BigDecimal valorSomaParcelas) {
        this.valorSomaParcelas = valorSomaParcelas;
    }

    public BigDecimal getValorResidual() {
        return valorResidual;
    }

    public void setValorResidual(BigDecimal valorResidual) {
        this.valorResidual = valorResidual;
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public void setLoteBaixa(LoteBaixa loteBaixa) {
        this.loteBaixa = loteBaixa;
    }
}
