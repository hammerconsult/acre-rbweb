package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.enums.Exigibilidade;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class ArquivoDesifRegistro0430 extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private Long linha;
    @ManyToOne
    private ArquivoDesif arquivoDesif;
    private String codigoDependencia;
    @ManyToOne
    private PlanoGeralContasComentado conta;
    @ManyToOne
    private CodigoTributacao codigoTributacao;
    private BigDecimal valorCredito;
    private BigDecimal valorDebito;
    private BigDecimal valorReceitaTributavel;
    private BigDecimal valorDeducaoLegal;
    private String discriminacaoDeducao;
    private BigDecimal baseCalculo;
    private BigDecimal aliquota;
    private BigDecimal valorIncentivoFiscal;
    private String discriminacaoIncentivo;
    private BigDecimal valorIssqnRetido;
    @Enumerated(EnumType.STRING)
    private Exigibilidade exigibilidadeSuspensao;
    private String processoSuspensao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLinha() {
        return linha;
    }

    public void setLinha(Long linha) {
        this.linha = linha;
    }

    public ArquivoDesif getArquivoDesif() {
        return arquivoDesif;
    }

    public void setArquivoDesif(ArquivoDesif arquivoDesif) {
        this.arquivoDesif = arquivoDesif;
    }

    public String getCodigoDependencia() {
        return codigoDependencia;
    }

    public void setCodigoDependencia(String codigoDependencia) {
        this.codigoDependencia = codigoDependencia;
    }

    public PlanoGeralContasComentado getConta() {
        return conta;
    }

    public void setConta(PlanoGeralContasComentado conta) {
        this.conta = conta;
    }

    public CodigoTributacao getCodigoTributacao() {
        return codigoTributacao;
    }

    public void setCodigoTributacao(CodigoTributacao codigoTributacao) {
        this.codigoTributacao = codigoTributacao;
    }

    public BigDecimal getValorCredito() {
        return valorCredito;
    }

    public void setValorCredito(BigDecimal valorCredito) {
        this.valorCredito = valorCredito;
    }

    public BigDecimal getValorDebito() {
        return valorDebito;
    }

    public void setValorDebito(BigDecimal valorDebito) {
        this.valorDebito = valorDebito;
    }

    public BigDecimal getValorReceitaTributavel() {
        return valorReceitaTributavel;
    }

    public void setValorReceitaTributavel(BigDecimal valorReceitaTributavel) {
        this.valorReceitaTributavel = valorReceitaTributavel;
    }

    public BigDecimal getValorDeducaoLegal() {
        return valorDeducaoLegal;
    }

    public void setValorDeducaoLegal(BigDecimal valorDeducaoLegal) {
        this.valorDeducaoLegal = valorDeducaoLegal;
    }

    public String getDiscriminacaoDeducao() {
        return discriminacaoDeducao;
    }

    public void setDiscriminacaoDeducao(String discriminacaoDeducao) {
        this.discriminacaoDeducao = discriminacaoDeducao;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public BigDecimal getValorIncentivoFiscal() {
        return valorIncentivoFiscal;
    }

    public void setValorIncentivoFiscal(BigDecimal valorIncentivoFiscal) {
        this.valorIncentivoFiscal = valorIncentivoFiscal;
    }

    public String getDiscriminacaoIncentivo() {
        return discriminacaoIncentivo;
    }

    public void setDiscriminacaoIncentivo(String discriminacaoIncentivo) {
        this.discriminacaoIncentivo = discriminacaoIncentivo;
    }

    public BigDecimal getValorIssqnRetido() {
        return valorIssqnRetido;
    }

    public void setValorIssqnRetido(BigDecimal valorIssqnRetido) {
        this.valorIssqnRetido = valorIssqnRetido;
    }

    public Exigibilidade getExigibilidadeSuspensao() {
        return exigibilidadeSuspensao;
    }

    public void setExigibilidadeSuspensao(Exigibilidade exigibilidadeSuspensao) {
        this.exigibilidadeSuspensao = exigibilidadeSuspensao;
    }

    public String getProcessoSuspensao() {
        return processoSuspensao;
    }

    public void setProcessoSuspensao(String processoSuspensao) {
        this.processoSuspensao = processoSuspensao;
    }
}
