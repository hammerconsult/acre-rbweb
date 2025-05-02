package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

public class DeclaracaoContaBancariaNfseDTO {

    private PlanoGeralContasComentadoNfseDTO conta;
    private BigDecimal saldoInicial;
    private BigDecimal valorDebito;
    private BigDecimal valorCredito;
    private BigDecimal saldoFinal;
    private BigDecimal baseCalculo;
    private BigDecimal aliquotaIssqn;

    public DeclaracaoContaBancariaNfseDTO() {
    }

    public PlanoGeralContasComentadoNfseDTO getConta() {
        return conta;
    }

    public void setConta(PlanoGeralContasComentadoNfseDTO conta) {
        this.conta = conta;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getValorDebito() {
        return valorDebito;
    }

    public void setValorDebito(BigDecimal valorDebito) {
        this.valorDebito = valorDebito;
    }

    public BigDecimal getValorCredito() {
        return valorCredito;
    }

    public void setValorCredito(BigDecimal valorCredito) {
        this.valorCredito = valorCredito;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getAliquotaIssqn() {
        return aliquotaIssqn;
    }

    public void setAliquotaIssqn(BigDecimal aliquotaIssqn) {
        this.aliquotaIssqn = aliquotaIssqn;
    }
}
