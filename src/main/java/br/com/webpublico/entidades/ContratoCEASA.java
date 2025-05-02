/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoContratoCEASA;
import br.com.webpublico.enums.TipoUtilizacaoRendasPatrimoniais;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Andre
 */
@Entity
@Audited

@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Contrato de CEASA")
public class ContratoCEASA implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DocumentoOficial documentoOficial;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Nº Contrato")
    private String numeroContrato;
    @ManyToOne
    private CadastroEconomico locatario;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Início")
    private Date dataInicio;
    @Enumerated(EnumType.STRING)
    private TipoUtilizacaoRendasPatrimoniais tipoUtilizacao;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Vigência")
    private Integer periodoVigencia;
    private Integer diaVencimento;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Situação")
    private SituacaoContratoCEASA situacaoContrato;
    @ManyToOne
    private UsuarioSistema usuarioInclusao;
    @OneToOne
    private ContratoCEASA originario;
    @ManyToOne
    private AtividadePonto atividadePonto;
    @Tabelavel
    @Etiqueta("Quantidade de Parcelas")
    private Integer quantidadeParcelas;
    @ManyToOne
    private LotacaoVistoriadora lotacaoVistoriadora;
    @OneToMany(mappedBy = "contratoCEASA", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PontoComercialContratoCEASA> pontoComercialContratoCEASAs;
    @Etiqueta("Valor da Licitação")
    @Pesquisavel
    private BigDecimal valorLicitacao;
    @ManyToOne
    private IndiceEconomico indiceEconomico;
    private BigDecimal valorUFMAtual;
    private BigDecimal valorTotalContrato;
    private BigDecimal areaTotalRateio;
    private BigDecimal valorServicosRateio;
    private BigDecimal valorMensalRateio;
    @Etiqueta("Valor do RDC")
    @Tabelavel
    @Monetario
    @Pesquisavel
    private BigDecimal valorTotalRateio;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOperacao;
    private String motivoOperacao;
    @ManyToOne
    private UsuarioSistema usuarioOperacao;
    @Temporal(TemporalType.DATE)
    private Date dataFim;
    @Transient
    private String valorUFMString;
    @Transient
    @Etiqueta("CMC")
    @Tabelavel
    private String numeroCMC;
    @Transient
    @Etiqueta("Nome/Razão Social")
    @Tabelavel
    private String nomeLocatario;
    @Transient
    @Etiqueta("Valor da Licitação")
    @Tabelavel
    private String valorLicitacaoFormatado;
    @Transient
    @Etiqueta("Valor do Contrato")
    @Tabelavel
    private String valorContratoFormatado;
    @Transient
    private BigDecimal valorUFM;


    public ContratoCEASA() {
        setPontoComercialContratoCEASAs(new ArrayList<PontoComercialContratoCEASA>());
        setValorLicitacao(BigDecimal.ZERO);
        valorMensalRateio = BigDecimal.ZERO;
        valorTotalRateio = BigDecimal.ZERO;
    }


    public BigDecimal getValorTotalContrato() {
        return valorTotalContrato != null ? valorTotalContrato.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorTotalContrato(BigDecimal valorTotalContrato) {
        this.valorTotalContrato = valorTotalContrato;
    }

    public BigDecimal getAreaTotalRateio() {
        return areaTotalRateio != null ? areaTotalRateio.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public String getMotivoOperacao() {
        return motivoOperacao;
    }

    public void setMotivoOperacao(String motivoOperacao) {
        this.motivoOperacao = motivoOperacao;
    }


    public UsuarioSistema getUsuarioOperacao() {
        return usuarioOperacao;
    }

    public void setUsuarioOperacao(UsuarioSistema usuarioOperacao) {
        this.usuarioOperacao = usuarioOperacao;
    }

    public void setAreaTotalRateio(BigDecimal areaTotalRateio) {
        this.areaTotalRateio = areaTotalRateio;
    }

    public BigDecimal getValorServicosRateio() {
        return valorServicosRateio != null ? valorServicosRateio.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorServicosRateio(BigDecimal valorServicosRateio) {
        this.valorServicosRateio = valorServicosRateio;
    }

    public BigDecimal getValorMensalRateio() {
        return valorMensalRateio != null ? valorMensalRateio.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorMensalRateio(BigDecimal valorMensalRateio) {
        this.valorMensalRateio = valorMensalRateio;
    }

    public BigDecimal getValorTotalRateio() {
        return valorTotalRateio != null ? valorTotalRateio.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorTotalRateio(BigDecimal valorTotalRateio) {
        this.valorTotalRateio = valorTotalRateio;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public CadastroEconomico getLocatario() {
        return locatario;
    }

    public void setLocatario(CadastroEconomico locatario) {
        this.locatario = locatario;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public TipoUtilizacaoRendasPatrimoniais getTipoUtilizacao() {
        return tipoUtilizacao;
    }

    public void setTipoUtilizacao(TipoUtilizacaoRendasPatrimoniais tipoUtilizacao) {
        this.tipoUtilizacao = tipoUtilizacao;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Integer getPeriodoVigencia() {
        return periodoVigencia;
    }

    public void setPeriodoVigencia(Integer periodoVigencia) {
        this.periodoVigencia = periodoVigencia;
    }

    public Integer getDiaVencimento() {
        return diaVencimento;
    }

    public void setDiaVencimento(Integer diaVencimento) {
        this.diaVencimento = diaVencimento;
    }

    public boolean temContratoOriginal() {
        return this.originario != null;
    }

    public SituacaoContratoCEASA getSituacaoContrato() {
        return situacaoContrato;
    }

    public void setSituacaoContrato(SituacaoContratoCEASA situacaoContrato) {
        this.situacaoContrato = situacaoContrato;
    }

    public UsuarioSistema getUsuarioInclusao() {
        return usuarioInclusao;
    }

    public void setUsuarioInclusao(UsuarioSistema usuarioInclusao) {
        this.usuarioInclusao = usuarioInclusao;
    }

    public ContratoCEASA getOriginario() {
        return originario;
    }

    public void setOriginario(ContratoCEASA originario) {
        this.originario = originario;
    }

    public AtividadePonto getAtividadePonto() {
        return atividadePonto;
    }

    public void setAtividadePonto(AtividadePonto atividadePonto) {
        this.atividadePonto = atividadePonto;
    }

    public Integer getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(Integer quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public LotacaoVistoriadora getLotacaoVistoriadora() {
        return lotacaoVistoriadora;
    }

    public BigDecimal getValorUFMAtual() {
        return valorUFMAtual != null ? valorUFMAtual.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorUFMAtual(BigDecimal valorUFMAtual) {
        this.valorUFMAtual = valorUFMAtual;
    }

    public void setLotacaoVistoriadora(LotacaoVistoriadora lotacaoVistoriadora) {
        this.lotacaoVistoriadora = lotacaoVistoriadora;
    }

    public List<PontoComercialContratoCEASA> getPontoComercialContratoCEASAs() {
        return pontoComercialContratoCEASAs;
    }

    public void setPontoComercialContratoCEASAs(List<PontoComercialContratoCEASA> pontoComercialContratoCEASAs) {
        this.pontoComercialContratoCEASAs = pontoComercialContratoCEASAs;
    }

    public BigDecimal getValorLicitacao() {
        return valorLicitacao != null ? valorLicitacao.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorLicitacao(BigDecimal valorLicitacao) {
        this.valorLicitacao = valorLicitacao;
    }

    public BigDecimal getSomaDoValorCalculadoMes() {
        BigDecimal valor = BigDecimal.ZERO;
        for (PontoComercialContratoCEASA p : getPontoComercialContratoCEASAs()) {
            valor = valor.add(p.getValorCalculadoMes());
        }
        return valor;
    }

    public BigDecimal getSomaTotalArea() {
        BigDecimal valor = BigDecimal.ZERO;
        for (PontoComercialContratoCEASA p : getPontoComercialContratoCEASAs()) {
            valor = valor.add(p.getArea());
        }
        return valor;
    }

    public BigDecimal getSomaDoValorTotalContrato() {
        BigDecimal valor = BigDecimal.ZERO;
        for (PontoComercialContratoCEASA p : getPontoComercialContratoCEASAs()) {
            valor = valor.add(p.getValorTotalContrato());
        }
        return valor;
    }


    public IndiceEconomico getIndiceEconomico() {
        return indiceEconomico;
    }

    public void setIndiceEconomico(IndiceEconomico indiceEconomico) {
        this.indiceEconomico = indiceEconomico;
    }

    public String getNumeroCMC() {
        return numeroCMC;
    }

    public void setNumeroCMC(String numeroCMC) {
        this.numeroCMC = numeroCMC;
    }

    public String getValorUFMString() {
        return valorUFMString;
    }

    public void setValorUFMString(String valorUFMString) {
        this.valorUFMString = valorUFMString;
    }

    public String getNomeLocatario() {
        return nomeLocatario;
    }

    public void setNomeLocatario(String nomeLocatario) {
        this.nomeLocatario = nomeLocatario;
    }

    public String getValorLicitacaoFormatado() {
        NumberFormat formato = NumberFormat.getCurrencyInstance();
        return formato.format(getValorLicitacao().doubleValue());
    }

    public void setValorLicitacaoFormatado(String valorLicitacaoFormatado) {
        this.valorLicitacaoFormatado = valorLicitacaoFormatado;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public String getValorContratoFormatado() {
        NumberFormat formato = NumberFormat.getCurrencyInstance();
        BigDecimal valor = getSomaDoValorTotalContrato();
        if (getValorUFM().compareTo(BigDecimal.ZERO) > 0) {
            valor = valor.multiply(getValorUFM());
        }
        return formato.format(valor.doubleValue());
    }

    public void setValorContratoFormatado(String valorContratoFormatado) {
        this.valorContratoFormatado = valorContratoFormatado;
    }

    public void setValorUFM(BigDecimal valorUFM) {
        this.valorUFM = valorUFM;
    }

    public BigDecimal getValorUFM() {
        return valorUFM != null ? valorUFM.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
