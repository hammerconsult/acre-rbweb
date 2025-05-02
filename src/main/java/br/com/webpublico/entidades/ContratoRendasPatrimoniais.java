/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.DateUtils;
import br.com.webpublico.enums.SituacaoContratoRendasPatrimoniais;
import br.com.webpublico.enums.TipoUtilizacaoRendasPatrimoniais;
import br.com.webpublico.negocios.FeriadoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author daniel
 */
@Entity

@Audited
@Etiqueta("Contrato de Rendas Patrimoniais")
public class ContratoRendasPatrimoniais implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel(ordemApresentacao = 1)
    @Pesquisavel
    @Etiqueta("Nº Contrato")
    private String numeroContrato;
    @ManyToOne
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 3)
    @Etiqueta("Locatário")
    private Pessoa locatario;
    @Transient
    @Etiqueta("CPF/CNPJ")
    private String cpfCnpjLocatario;
    @Temporal(TemporalType.DATE)
    @Tabelavel(ordemApresentacao = 5)
    @Pesquisavel
    @Etiqueta("Data de Início")
    private Date dataInicio;
    @Enumerated(EnumType.STRING)
    private TipoUtilizacaoRendasPatrimoniais tipoUtilizacao;
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 7)
    @Etiqueta("Vigência (meses)")
    private Integer periodoVigencia;
    private Integer diaVencimento;
    @Enumerated(EnumType.STRING)
    @Tabelavel(ordemApresentacao = 10)
    @Etiqueta("Situação")
    private SituacaoContratoRendasPatrimoniais situacaoContrato;
    @ManyToOne
    private UsuarioSistema usuarioInclusao;
    private String sequenciaContrato;
    @OneToOne
    private ContratoRendasPatrimoniais originario;
    @ManyToOne
    private AtividadePonto atividadePonto;
    @Tabelavel(ordemApresentacao = 8)
    @Etiqueta("Quantidade de Parcelas")
    private Integer quantidadeParcelas;
    @ManyToOne
    private LotacaoVistoriadora lotacaoVistoriadora;
    @OneToMany(mappedBy = "contratoRendasPatrimoniais", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(ordemApresentacao = 2)
    @Etiqueta("Localização")
    private List<PontoComercialContratoRendasPatrimoniais> pontoComercialContratoRendasPatrimoniais;
    @ManyToOne
    @Etiqueta("Índice Econômico")
    private IndiceEconomico indiceEconomico;
    @Transient
    @Tabelavel(ordemApresentacao = 9, ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Valor do Contrato")
    private String somaDoValorTotalContratoFormatado;
    @Transient
    private BigDecimal valorUfm;
    @OneToMany(mappedBy = "contratoRendasPatrimoniais", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContratoRendaCNAE> contratoRendaCNAEs;
    private BigDecimal valorUfmDoContrato;
    @Temporal(TemporalType.DATE)
    @Etiqueta(" Data de Término")
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 6)
    private Date dataFim;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOperacao;
    private String motivoOperacao;
    @ManyToOne
    private UsuarioSistema usuarioOperacao;
    @Transient
    private String tipoOperacao;
    @Transient
    private BigDecimal valorPorMesUFM;
    @Transient
    private BigDecimal valorPorMesRS;
    @Transient
    private BigDecimal valorTotalUFM;

    public ContratoRendasPatrimoniais(ContratoRendasPatrimoniais c) {
        this.setNumeroContrato(c.getNumeroContrato());
        this.setLocatario(c.getLocatario());
        this.setDataInicio(c.getDataInicio());
        this.setDataFim(c.getDataFim());
        this.setSituacaoContrato(c.getSituacaoContrato());
        this.setPontoComercialContratoRendasPatrimoniais(c.getPontoComercialContratoRendasPatrimoniais());
    }


    public ContratoRendasPatrimoniais() {
        setContratoRendaCNAEs(new ArrayList<ContratoRendaCNAE>());
        setPontoComercialContratoRendasPatrimoniais(new ArrayList<PontoComercialContratoRendasPatrimoniais>());
        this.valorPorMesUFM = BigDecimal.ZERO;
        this.valorPorMesRS = BigDecimal.ZERO;
        this.valorTotalUFM = BigDecimal.ZERO;
    }

    public ContratoRendasPatrimoniais(List<PontoComercialContratoRendasPatrimoniais> pontos) {
        ContratoRendasPatrimoniais contrato = pontos.get(0).getContratoRendasPatrimoniais();
        this.atividadePonto = contrato.getAtividadePonto();
        this.cpfCnpjLocatario = contrato.getLocatario().getCpf_Cnpj();
        this.dataInicio = contrato.getDataInicio();
        this.diaVencimento = contrato.getDiaVencimento();
        this.indiceEconomico = contrato.getIndiceEconomico();
        this.locatario = contrato.getLocatario();
        this.lotacaoVistoriadora = contrato.getLotacaoVistoriadora();
        this.numeroContrato = contrato.getNumeroContrato();
        this.originario = contrato.getOriginario();
        this.periodoVigencia = contrato.getPeriodoVigencia();
        this.quantidadeParcelas = contrato.getQuantidadeParcelas();
        this.sequenciaContrato = contrato.getSequenciaContrato();
        this.situacaoContrato = contrato.getSituacaoContrato();
        this.tipoUtilizacao = contrato.getTipoUtilizacao();
        this.usuarioInclusao = contrato.getUsuarioInclusao();

        this.pontoComercialContratoRendasPatrimoniais = pontos;
        this.somaDoValorTotalContratoFormatado = getSomaDoValorTotalContratoFormatado();
    }


    public BigDecimal getValorTotalUFM() {
        return valorTotalUFM != null ? valorTotalUFM.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorTotalUFM(BigDecimal valorTotalUFM) {
        this.valorTotalUFM = valorTotalUFM;
    }

    public BigDecimal getValorPorMesRS() {
        return valorPorMesRS != null ? valorPorMesRS.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorPorMesRS(BigDecimal valorPorMesRS) {
        this.valorPorMesRS = valorPorMesRS;
    }

    public BigDecimal getValorPorMesUFM() {
        return valorPorMesUFM != null ? valorPorMesUFM.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorPorMesUFM(BigDecimal valorPorMesUFM) {
        this.valorPorMesUFM = valorPorMesUFM;
    }

    public String getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(String tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public UsuarioSistema getUsuarioOperacao() {
        return usuarioOperacao;
    }

    public void setUsuarioOperacao(UsuarioSistema usuarioOperacao) {
        this.usuarioOperacao = usuarioOperacao;
    }

    public String getMotivoOperacao() {
        return motivoOperacao;
    }

    public void setMotivoOperacao(String motivoOperacao) {
        this.motivoOperacao = motivoOperacao;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Integer getDiaVencimento() {
        return diaVencimento;
    }

    public void setDiaVencimento(Integer diaVencimento) {
        this.diaVencimento = diaVencimento;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public BigDecimal getValorUfmDoContrato() {
        return valorUfmDoContrato != null ? valorUfmDoContrato.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorUfmDoContrato(BigDecimal valorUfmDoContrato) {
        this.valorUfmDoContrato = valorUfmDoContrato;
    }

    public Pessoa getLocatario() {
        return locatario;
    }

    public void setLocatario(Pessoa locatario) {
        this.locatario = locatario;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public Integer getPeriodoVigencia() {
        return periodoVigencia;
    }

    public void setPeriodoVigencia(Integer periodoVigencia) {
        this.periodoVigencia = periodoVigencia;
    }

    public SituacaoContratoRendasPatrimoniais getSituacaoContrato() {
        return situacaoContrato;
    }

    public void setSituacaoContrato(SituacaoContratoRendasPatrimoniais situacaoContrato) {
        this.situacaoContrato = situacaoContrato;
    }

    public TipoUtilizacaoRendasPatrimoniais getTipoUtilizacao() {
        return tipoUtilizacao;
    }

    public void setTipoUtilizacao(TipoUtilizacaoRendasPatrimoniais tipoUtilizacao) {
        this.tipoUtilizacao = tipoUtilizacao;
    }

    public UsuarioSistema getUsuarioInclusao() {
        return usuarioInclusao;
    }

    public void setUsuarioInclusao(UsuarioSistema usuarioInclusao) {
        this.usuarioInclusao = usuarioInclusao;
    }

    public ContratoRendasPatrimoniais getOriginario() {
        return originario;
    }

    public boolean temContratoOriginal() {
        return this.originario != null;
    }

    public void setOriginario(ContratoRendasPatrimoniais originario) {
        this.originario = originario;
    }

    public String getSequenciaContrato() {
        return sequenciaContrato;
    }

    public void setSequenciaContrato(String sequenciaContrato) {
        this.sequenciaContrato = sequenciaContrato;
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

    public void setLotacaoVistoriadora(LotacaoVistoriadora lotacaoVistoriadora) {
        this.lotacaoVistoriadora = lotacaoVistoriadora;
    }

    public List<PontoComercialContratoRendasPatrimoniais> getPontoComercialContratoRendasPatrimoniais() {
        return pontoComercialContratoRendasPatrimoniais;
    }

    public void setPontoComercialContratoRendasPatrimoniais(List<PontoComercialContratoRendasPatrimoniais> pontoComercialContratoRendasPatrimoniais) {
        this.pontoComercialContratoRendasPatrimoniais = pontoComercialContratoRendasPatrimoniais;
    }

    public IndiceEconomico getIndiceEconomico() {
        return indiceEconomico;
    }

    public void setIndiceEconomico(IndiceEconomico indiceEconomico) {
        this.indiceEconomico = indiceEconomico;
    }

    public void setValorUfm(BigDecimal valorUfm) {
        this.valorUfm = valorUfm;
    }

    public BigDecimal getSomaDoValorCalculadoMes() {
        BigDecimal valor = BigDecimal.ZERO;
        for (PontoComercialContratoRendasPatrimoniais p : getPontoComercialContratoRendasPatrimoniais()) {
            valor = valor.add(p.getValorCalculadoMes());
        }
        return valor;
    }

    public BigDecimal getSomaDoValorTotalContrato() {
        BigDecimal valor = BigDecimal.ZERO;
        for (PontoComercialContratoRendasPatrimoniais p : getPontoComercialContratoRendasPatrimoniais()) {
            valor = valor.add(p.getValorTotalContrato());
        }
        if (getValorUfm().compareTo(BigDecimal.ZERO) > 0) {
            valor = valor.multiply(getValorUfm());
        }
        return valor;
    }

    public BigDecimal getValorUfm() {
        return valorUfm != null ? valorUfm.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public BigDecimal getSomaTotalArea() {
        BigDecimal valor = BigDecimal.ZERO;
        for (PontoComercialContratoRendasPatrimoniais p : getPontoComercialContratoRendasPatrimoniais()) {
            valor = valor.add(p.getArea());
        }
        return valor;
    }

    public BigDecimal getSomaValorMetroQuadrado() {
        BigDecimal valor = BigDecimal.ZERO;
        for (PontoComercialContratoRendasPatrimoniais p : getPontoComercialContratoRendasPatrimoniais()) {
            valor = valor.add(p.getValorMetroQuadrado());
        }
        return valor;
    }

    public String getSomaDoValorTotalContratoFormatado() {
        NumberFormat formato = new DecimalFormat("R$ #,##0.00");
        return formato.format(getSomaDoValorTotalContrato().doubleValue());
    }

    public void setSomaDoValorTotalContratoFormatado(String somaDoValorTotalContratoFormatado) {
        this.somaDoValorTotalContratoFormatado = somaDoValorTotalContratoFormatado;
    }

    public String getCpfCnpjLocatario() {
        if (this.getLocatario() != null) {
            return this.getLocatario().getCpf_Cnpj();
        }
        return this.cpfCnpjLocatario;
    }

    public void setCpfCnpjLocatario(String cpfCnpjLocatario) {
        this.cpfCnpjLocatario = cpfCnpjLocatario;
    }

    public List<ContratoRendaCNAE> getContratoRendaCNAEs() {
        return contratoRendaCNAEs;
    }

    public void setContratoRendaCNAEs(List<ContratoRendaCNAE> contratoRendaCNAEs) {
        this.contratoRendaCNAEs = contratoRendaCNAEs;
    }

    @Override
    public String toString() {
        return id + ", " + numeroContrato;
    }

//    @Override
//    public String getNumeroCadastro() {
//        return numeroContrato;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getPrimeiroVencimentoContrato(FeriadoFacade feriadoFacade) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataInicio);
        int diaInicio = c.get(Calendar.DAY_OF_MONTH);
        if (diaInicio > diaVencimento) {
            c.add(Calendar.MONTH, 1);
        }
        int ultimoDiaMes = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, diaVencimento <= ultimoDiaMes ? diaVencimento : ultimoDiaMes);
        while (DataUtil.ehDiaNaoUtil(c.getTime(), feriadoFacade)) {
            c.setTime(DataUtil.adicionaDias(c.getTime(), -1));
        }
        return c;
    }

    public Calendar getPrimeiroVencimentoContratoAlterado(int qtdParcelasPagas, FeriadoFacade feriadoFacade) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataInicio);
        int diaInicio = c.get(Calendar.DAY_OF_MONTH);
        if (qtdParcelasPagas == 0 && diaInicio > diaVencimento) {
            c.add(Calendar.MONTH, 1);
        }
        if (qtdParcelasPagas > 0) {
            c.add(Calendar.MONTH, qtdParcelasPagas);
        }
        int ultimoDiaMes = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, diaVencimento <= ultimoDiaMes ? diaVencimento : ultimoDiaMes);
        while (DataUtil.ehDiaNaoUtil(c.getTime(), feriadoFacade)) {
            c.setTime(DataUtil.adicionaDias(c.getTime(), -1));
        }
        return c;
    }

    public void proximoVencimento(FeriadoFacade feriadoFacade, Calendar vencimentoAtual) {
        vencimentoAtual.add(Calendar.MONTH, 1);
        int diaVencimento = getDiaVencimento();
        int ultimoDiaMes = vencimentoAtual.getActualMaximum(Calendar.DAY_OF_MONTH);
        vencimentoAtual.set(Calendar.DAY_OF_MONTH, Math.min(diaVencimento, ultimoDiaMes));
        while (DataUtil.ehDiaNaoUtil(vencimentoAtual.getTime(), feriadoFacade)) {
            vencimentoAtual.setTime(DataUtil.adicionaDias(vencimentoAtual.getTime(), -1));
        }
    }
}
