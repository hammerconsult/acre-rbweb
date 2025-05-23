package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

public class RelatorioApuracaoISSQNDTO {
    private String operacao;
    private String cadastroEconomico;
    private String mes;
    private Integer quantidade;
    private BigDecimal iss;
    private BigDecimal baseCalculo;
    private BigDecimal deducoes;
    private BigDecimal descontosIncondicionados;
    private BigDecimal descontosCondicionados;
    private BigDecimal valorServico;
    private BigDecimal aliquotaServico;
    private String numero;
    private String servico;
    private BigDecimal valorPago;

    public RelatorioApuracaoISSQNDTO() {
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(String cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getIss() {
        return iss;
    }

    public void setIss(BigDecimal iss) {
        this.iss = iss;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getDeducoes() {
        return deducoes;
    }

    public void setDeducoes(BigDecimal deducoes) {
        this.deducoes = deducoes;
    }

    public BigDecimal getDescontosIncondicionados() {
        return descontosIncondicionados;
    }

    public void setDescontosIncondicionados(BigDecimal descontosIncondicionados) {
        this.descontosIncondicionados = descontosIncondicionados;
    }

    public BigDecimal getDescontosCondicionados() {
        return descontosCondicionados;
    }

    public void setDescontosCondicionados(BigDecimal descontosCondicionados) {
        this.descontosCondicionados = descontosCondicionados;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public BigDecimal getAliquotaServico() {
        return aliquotaServico;
    }

    public void setAliquotaServico(BigDecimal aliquotaServico) {
        this.aliquotaServico = aliquotaServico;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }
}
