package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

public class TotalPorNaturezaSituacaoNfseDTO implements NfseDTO {

    private String naturezaOperacao;
    private String situacao;
    private BigDecimal totalServicos;
    private BigDecimal desconto;
    private BigDecimal deducoes;
    private BigDecimal valorLiquido;
    private BigDecimal baseCalculo;
    private BigDecimal issqn;
    private BigDecimal issqnRetido;
    private Integer quantidadeNotas;

    public TotalPorNaturezaSituacaoNfseDTO() {
    }

    public String getNaturezaOperacao() {
        return naturezaOperacao;
    }

    public void setNaturezaOperacao(String naturezaOperacao) {
        this.naturezaOperacao = naturezaOperacao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getDeducoes() {
        return deducoes;
    }

    public void setDeducoes(BigDecimal deducoes) {
        this.deducoes = deducoes;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getIssqn() {
        return issqn;
    }

    public void setIssqn(BigDecimal issqn) {
        this.issqn = issqn;
    }

    public BigDecimal getIssqnRetido() {
        return issqnRetido;
    }

    public void setIssqnRetido(BigDecimal issqnRetido) {
        this.issqnRetido = issqnRetido;
    }

    public Integer getQuantidadeNotas() {
        return quantidadeNotas;
    }

    public void setQuantidadeNotas(Integer quantidadeNotas) {
        this.quantidadeNotas = quantidadeNotas;
    }
}
