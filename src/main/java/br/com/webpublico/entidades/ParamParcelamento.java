package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoDebito;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoSimNao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Parâmetros de Parcelamento")
public class ParamParcelamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @Enumerated(EnumType.STRING)
    private SituacaoDebito situacaoDebito;
    @ManyToOne
    private Divida dividaParcelamento;
    private Boolean incluiValorOriginal;
    private Boolean incluiMulta;
    private Boolean incluiJuros;
    private Boolean incluiCorrecao;
    private Boolean exigePercentualEntrada;
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Início de Vigência")
    private Date vigenciaInicio;
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Final de Vigência")
    private Date vigenciaFim;
    @Transient
    private Long criadoEm;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "paramParcelamento")
    private List<ParamParcelamentoDivida> dividas;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "paramParcelamento")
    private List<ParamParcelamentoFaixa> faixas;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Cadastro")
    private TipoCadastroTributario tipoCadastroTributario;
    private BigDecimal valorPercentualEntrada;
    private BigDecimal valorMinimoParcelaUfm;
    private Integer quantidadeReparcelamento;
    private Integer parcelasInadimplencia;
    private Boolean inadimplenciaSucessiva;
    @Enumerated(EnumType.STRING)
    private TipoInadimplimento tipoInadimplimento;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paramParcelamento", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ParamParcelamentoTributo> tributos;
    @Enumerated(EnumType.STRING)
    private TipoLancamentoDesconto tipoLancamentoDesconto;
    @Enumerated(EnumType.STRING)
    private TipoVigenciaDesconto tipoVigenciaDesconto;
    @ManyToOne
    private TipoDoctoOficial termoCadastro;
    @ManyToOne
    private TipoDoctoOficial termoPessoaFisica;
    @ManyToOne
    private TipoDoctoOficial termoPessoaJuridica;
    private Integer diasVencimentoEntrada;
    @Temporal(TemporalType.DATE)
    private Date inicioDesconto;
    @Temporal(TemporalType.DATE)
    private Date finalDesconto;
    private Integer diasVencidosCancelamento;
    @Enumerated(EnumType.STRING)
    private TipoVerificacaoCancelamentoAutomatico verificaCancelamentoAutomatico;
    @Enumerated(EnumType.STRING)
    private TipoSimNao parcelamentoCpfCnpjInvalido;

    public ParamParcelamento() {
        criadoEm = System.nanoTime();
        incluiCorrecao = false;
        incluiValorOriginal = true;
        incluiMulta = false;
        incluiJuros = false;
        valorPercentualEntrada = BigDecimal.ZERO;
        valorMinimoParcelaUfm = BigDecimal.ZERO;
        dividas = Lists.newArrayList();
        faixas = Lists.newArrayList();
        tributos = Lists.newArrayList();
        quantidadeReparcelamento = 1;
        parcelasInadimplencia = 12;
        inadimplenciaSucessiva = false;
        tipoVigenciaDesconto = TipoVigenciaDesconto.VENCIMENTO;
        tipoInadimplimento = TipoInadimplimento.SUCESSIVO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ParamParcelamentoTributo> getTributos() {
        return tributos;
    }

    public Integer getParcelasInadimplencia() {
        return parcelasInadimplencia;
    }

    public void setParcelasInadimplencia(Integer mesesInadinplencia) {
        this.parcelasInadimplencia = mesesInadinplencia;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Divida getDividaParcelamento() {
        return dividaParcelamento;
    }

    public void setDividaParcelamento(Divida dividaParcelamento) {
        this.dividaParcelamento = dividaParcelamento;
    }

    public Boolean getIncluiCorrecao() {
        return incluiCorrecao;
    }

    public void setIncluiCorrecao(Boolean incluiCorrecao) {
        this.incluiCorrecao = incluiCorrecao;
    }

    public Boolean getIncluiJuros() {
        return incluiJuros;
    }

    public void setIncluiJuros(Boolean incluiJuros) {
        this.incluiJuros = incluiJuros;
    }

    public Boolean getIncluiMulta() {
        return incluiMulta;
    }

    public void setIncluiMulta(Boolean incluiMulta) {
        this.incluiMulta = incluiMulta;
    }

    public Boolean getIncluiValorOriginal() {
        return incluiValorOriginal;
    }

    public void setIncluiValorOriginal(Boolean incluiValorOriginal) {
        this.incluiValorOriginal = incluiValorOriginal;
    }

    public SituacaoDebito getSituacaoDebito() {
        return situacaoDebito;
    }

    public void setSituacaoDebito(SituacaoDebito situacaoDebito) {
        this.situacaoDebito = situacaoDebito;
    }

    public Date getVigenciaFim() {
        return vigenciaFim;
    }

    public void setVigenciaFim(Date vigenciaFim) {
        this.vigenciaFim = vigenciaFim;
    }

    public Date getVigenciaInicio() {
        return vigenciaInicio;
    }

    public void setVigenciaInicio(Date vigenciaInicio) {
        this.vigenciaInicio = vigenciaInicio;
    }

    public List<ParamParcelamentoDivida> getDividas() {
        return dividas;
    }

    public void setDividas(List<ParamParcelamentoDivida> dividas) {
        this.dividas = dividas;
    }

    public List<ParamParcelamentoFaixa> getFaixas() {
        return faixas;
    }

    public void setFaixas(List<ParamParcelamentoFaixa> faixas) {
        this.faixas = faixas;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public Boolean getExigePercentualEntrada() {
        return exigePercentualEntrada;
    }

    public void setExigePercentualEntrada(Boolean exigePercentualEntrada) {
        this.exigePercentualEntrada = exigePercentualEntrada;
    }

    public BigDecimal getValorMinimoParcelaUfm() {
        return valorMinimoParcelaUfm;
    }

    public void setValorMinimoParcelaUfm(BigDecimal valorMinimoParcelaUfm) {
        this.valorMinimoParcelaUfm = valorMinimoParcelaUfm;
    }

    public BigDecimal getValorPercentualEntrada() {
        return valorPercentualEntrada;
    }

    public void setValorPercentualEntrada(BigDecimal valorPercentualEntrada) {
        this.valorPercentualEntrada = valorPercentualEntrada;
    }

    public Integer getQuantidadeReparcelamento() {
        return quantidadeReparcelamento != null ? quantidadeReparcelamento : 0;
    }

    public void setQuantidadeReparcelamento(Integer quantidadeReparcelamento) {
        this.quantidadeReparcelamento = quantidadeReparcelamento;
    }

    public TipoLancamentoDesconto getTipoLancamentoDesconto() {
        return tipoLancamentoDesconto;
    }

    public void setTipoLancamentoDesconto(TipoLancamentoDesconto tipoLancamentoDesconto) {
        this.tipoLancamentoDesconto = tipoLancamentoDesconto;
    }

    public TipoDoctoOficial getTermoCadastro() {
        return termoCadastro;
    }

    public void setTermoCadastro(TipoDoctoOficial tipoDocumentoTermo) {
        this.termoCadastro = tipoDocumentoTermo;
    }

    public TipoDoctoOficial getTermoPessoaFisica() {
        return termoPessoaFisica;
    }

    public void setTermoPessoaFisica(TipoDoctoOficial termoPessoaFisica) {
        this.termoPessoaFisica = termoPessoaFisica;
    }

    public TipoDoctoOficial getTermoPessoaJuridica() {
        return termoPessoaJuridica;
    }

    public void setTermoPessoaJuridica(TipoDoctoOficial termoPessoaJuridica) {
        this.termoPessoaJuridica = termoPessoaJuridica;
    }

    public Date getInicioDesconto() {
        return inicioDesconto;
    }

    public void setInicioDesconto(Date inicioDesconto) {
        this.inicioDesconto = inicioDesconto;
    }

    public Date getFinalDesconto() {
        return finalDesconto;
    }

    public void setFinalDesconto(Date finalDesconto) {
        this.finalDesconto = finalDesconto;
    }

    public TipoVigenciaDesconto getTipoVigenciaDesconto() {
        return tipoVigenciaDesconto;
    }

    public void setTipoVigenciaDesconto(TipoVigenciaDesconto tipoVigenciaDesconto) {
        this.tipoVigenciaDesconto = tipoVigenciaDesconto;
    }

    public boolean isInadimplenciaSucessiva() {
        return TipoInadimplimento.SUCESSIVO.equals(this.tipoInadimplimento);
    }

    public boolean isInadimplenciaIntercadala() {
        return TipoInadimplimento.INTERCALADO.equals(this.tipoInadimplimento);
    }

    public void setInadimplenciaSucessiva(Boolean inadimplenciaSucessiva) {
        this.inadimplenciaSucessiva = inadimplenciaSucessiva;
    }

    public TipoInadimplimento getTipoInadimplimento() {
        return tipoInadimplimento;
    }

    public void setTipoInadimplimento(TipoInadimplimento tipoInadimplimento) {
        this.tipoInadimplimento = tipoInadimplimento;
    }

    public Integer getDiasVencimentoEntrada() {
        return diasVencimentoEntrada;
    }

    public void setDiasVencimentoEntrada(Integer diasVencimentoEntrada) {
        this.diasVencimentoEntrada = diasVencimentoEntrada;
    }

    public ParamParcelamentoFaixa getParamParcelamentoFaixa(BigDecimal valorTotal) {
        for (ParamParcelamentoFaixa faixa : faixas) {
            if (faixa.getValorInicial().compareTo(valorTotal) >= 0
                && faixa.getValorFinal().compareTo(valorTotal) <= 0) {
                return faixa;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getCodigo() + " - " + this.getDescricao();
    }

    public String getTipoParcelamento() {
        StringBuilder sb = new StringBuilder();
        if (incluiValorOriginal) {
            sb.append("Valor Original");
        }
        if (incluiJuros) {
            incluiMais(sb);
            sb.append("Juros");
        }
        if (incluiMulta) {
            incluiMais(sb);
            sb.append("Multa");
        }
        if (incluiCorrecao) {
            incluiMais(sb);
            sb.append("Correção Monetária");
        }
        return sb.toString();
    }

    public Integer getDiasVencidosCancelamento() {
        return diasVencidosCancelamento;
    }

    public void setDiasVencidosCancelamento(Integer diasVencidosCancelamento) {
        this.diasVencidosCancelamento = diasVencidosCancelamento;
    }

    public TipoVerificacaoCancelamentoAutomatico getVerificaCancelamentoAutomatico() {
        return verificaCancelamentoAutomatico;
    }

    public void setVerificaCancelamentoAutomatico(TipoVerificacaoCancelamentoAutomatico verificaCancelamentoAutomatico) {
        this.verificaCancelamentoAutomatico = verificaCancelamentoAutomatico;
    }

    private void incluiMais(StringBuilder sb) {
        if (sb.length() > 0) {
            sb.append(" + ");
        }
    }

    public TipoSimNao getParcelamentoCpfCnpjInvalido() {
        return parcelamentoCpfCnpjInvalido;
    }

    public void setParcelamentoCpfCnpjInvalido(TipoSimNao parcelamentoCpfCnpjInvalido) {
        this.parcelamentoCpfCnpjInvalido = parcelamentoCpfCnpjInvalido;
    }

    public static enum TipoLancamentoDesconto {
        VENCIMENTO_FINAL("Vencimento Final"), NUMERO_PARCELA("Número da Parcela");
        private String descricao;

        private TipoLancamentoDesconto(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TipoVigenciaDesconto {
        VENCIMENTO("Data de Vencimento"), PAGAMENTO("Data de Pagamento"), PARCELA_INADIMPLIDA("Parcela(s) Inadimplida(s)");
        private String descricao;

        private TipoVigenciaDesconto(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TipoInadimplimento {
        SUCESSIVO("Sucessivo"),
        INTERCALADO("Intercalado"),
        AMBOS("Ambos");
        private String descricao;

        TipoInadimplimento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TipoVerificacaoCancelamentoAutomatico implements EnumComDescricao {
        SIM("Sim"),
        NAO("Não");

        private final String descricao;

        TipoVerificacaoCancelamentoAutomatico(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }
}
