package br.com.webpublico.entidadesauxiliares.contabil;

import java.math.BigDecimal;

public class ValidadorMatrizSaldoContabilBlc {

    private String codigo;
    private BigDecimal saldoAnterior;
    private BigDecimal credito;
    private BigDecimal debito;
    private BigDecimal atual;

    public ValidadorMatrizSaldoContabilBlc() {
        saldoAnterior = BigDecimal.ZERO;
        credito = BigDecimal.ZERO;
        debito = BigDecimal.ZERO;
        atual = BigDecimal.ZERO;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getCredito() {
        return credito;
    }

    public void setCredito(BigDecimal credito) {
        this.credito = credito;
    }

    public BigDecimal getDebito() {
        return debito;
    }

    public void setDebito(BigDecimal debito) {
        this.debito = debito;
    }

    public BigDecimal getAtual() {
        return atual;
    }

    public void setAtual(BigDecimal atual) {
        this.atual = atual;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }
}
