package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.enums.Exigibilidade;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class ArquivoDesifRegistro0440 extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private Long linha;
    @ManyToOne
    private ArquivoDesif arquivoDesif;
    private String cnpjDependencia;
    @ManyToOne
    private CodigoTributacao codigoTributacao;
    private BigDecimal valorReceitaTributavel;
    private BigDecimal valorDeducaoConta;
    private BigDecimal valorDeducaoConsolidado;
    private String discriminacaoDeducao;
    private BigDecimal baseCalculo;
    private BigDecimal aliquota;
    private BigDecimal valorIssqn;
    private BigDecimal valorIssqnRetido;
    private BigDecimal valorIncentivoConta;
    private BigDecimal valorIncentivoConsolidado;
    private String discriminacaoIncentivo;
    private BigDecimal valorCompensacao;
    private String discriminacaoCompensacao;
    private BigDecimal valorIssqnRecolhido;
    @Enumerated(EnumType.STRING)
    private Exigibilidade exigibilidadeSuspensao;
    private String processoSuspensao;
    private BigDecimal valorIssqnRecolher;

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

    public String getCnpjDependencia() {
        return cnpjDependencia;
    }

    public void setCnpjDependencia(String cnpjDependencia) {
        this.cnpjDependencia = cnpjDependencia;
    }

    public CodigoTributacao getCodigoTributacao() {
        return codigoTributacao;
    }

    public void setCodigoTributacao(CodigoTributacao codigoTributacao) {
        this.codigoTributacao = codigoTributacao;
    }

    public BigDecimal getValorReceitaTributavel() {
        return valorReceitaTributavel;
    }

    public void setValorReceitaTributavel(BigDecimal valorReceitaTributavel) {
        this.valorReceitaTributavel = valorReceitaTributavel;
    }

    public BigDecimal getValorDeducaoConta() {
        return valorDeducaoConta;
    }

    public void setValorDeducaoConta(BigDecimal valorDeducaoConta) {
        this.valorDeducaoConta = valorDeducaoConta;
    }

    public BigDecimal getValorDeducaoConsolidado() {
        return valorDeducaoConsolidado;
    }

    public void setValorDeducaoConsolidado(BigDecimal valorDeducaoConsolidado) {
        this.valorDeducaoConsolidado = valorDeducaoConsolidado;
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

    public BigDecimal getValorIssqn() {
        return valorIssqn;
    }

    public void setValorIssqn(BigDecimal valorIssqn) {
        this.valorIssqn = valorIssqn;
    }

    public BigDecimal getValorIssqnRetido() {
        return valorIssqnRetido;
    }

    public void setValorIssqnRetido(BigDecimal valorIssqnRetido) {
        this.valorIssqnRetido = valorIssqnRetido;
    }

    public BigDecimal getValorIncentivoConta() {
        return valorIncentivoConta;
    }

    public void setValorIncentivoConta(BigDecimal valorIncentivoConta) {
        this.valorIncentivoConta = valorIncentivoConta;
    }

    public BigDecimal getValorIncentivoConsolidado() {
        return valorIncentivoConsolidado;
    }

    public void setValorIncentivoConsolidado(BigDecimal valorIncentivoConsolidado) {
        this.valorIncentivoConsolidado = valorIncentivoConsolidado;
    }

    public String getDiscriminacaoIncentivo() {
        return discriminacaoIncentivo;
    }

    public void setDiscriminacaoIncentivo(String discriminacaoIncentivo) {
        this.discriminacaoIncentivo = discriminacaoIncentivo;
    }

    public BigDecimal getValorCompensacao() {
        return valorCompensacao;
    }

    public void setValorCompensacao(BigDecimal valorCompensacao) {
        this.valorCompensacao = valorCompensacao;
    }

    public String getDiscriminacaoCompensacao() {
        return discriminacaoCompensacao;
    }

    public void setDiscriminacaoCompensacao(String discriminacaoCompensacao) {
        this.discriminacaoCompensacao = discriminacaoCompensacao;
    }

    public BigDecimal getValorIssqnRecolhido() {
        return valorIssqnRecolhido;
    }

    public void setValorIssqnRecolhido(BigDecimal valorIssqnRecolhido) {
        this.valorIssqnRecolhido = valorIssqnRecolhido;
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

    public BigDecimal getValorIssqnRecolher() {
        return valorIssqnRecolher;
    }

    public void setValorIssqnRecolher(BigDecimal valorIssqnRecolher) {
        this.valorIssqnRecolher = valorIssqnRecolher;
    }
}
