package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Criado por Mateus
 * Data: 17/05/2017.
 */
public class DemonstrativoDebitosPrecatorios {
    private String credor;
    private String numeroProcesso;
    private Date data;
    private String natureza;
    private BigDecimal saldoAnterior;
    private BigDecimal desconto;
    private BigDecimal atualizacao;
    private BigDecimal inclusao;
    private BigDecimal pagamentos;
    private BigDecimal cancelamento;
    private BigDecimal saldoAtual;

    public DemonstrativoDebitosPrecatorios() {
        saldoAnterior = BigDecimal.ZERO;
        desconto = BigDecimal.ZERO;
        atualizacao = BigDecimal.ZERO;
        inclusao = BigDecimal.ZERO;
        pagamentos = BigDecimal.ZERO;
        cancelamento = BigDecimal.ZERO;
        saldoAtual = BigDecimal.ZERO;
    }

    public String getCredor() {
        return credor;
    }

    public void setCredor(String credor) {
        this.credor = credor;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getNatureza() {
        return natureza;
    }

    public void setNatureza(String natureza) {
        this.natureza = natureza;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(BigDecimal atualizacao) {
        this.atualizacao = atualizacao;
    }

    public BigDecimal getInclusao() {
        return inclusao;
    }

    public void setInclusao(BigDecimal inclusao) {
        this.inclusao = inclusao;
    }

    public BigDecimal getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(BigDecimal pagamentos) {
        this.pagamentos = pagamentos;
    }

    public BigDecimal getCancelamento() {
        return cancelamento;
    }

    public void setCancelamento(BigDecimal cancelamento) {
        this.cancelamento = cancelamento;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }
}
