package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Criado por Mateus
 * Data: 13/10/2016.
 */
public class SaldoContaFinanceiraXSaldoBancarioItem {

    private String subConta;
    private BigDecimal saldoContabil;
    private BigDecimal debito;
    private BigDecimal credito;
    private BigDecimal contaLinha;
    private Boolean temMovimentos;

    public SaldoContaFinanceiraXSaldoBancarioItem() {
        temMovimentos = false;
    }


    public BigDecimal getContaLinha() {
        return contaLinha;
    }

    public void setContaLinha(BigDecimal contaLinha) {
        this.contaLinha = contaLinha;
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

    public BigDecimal getSaldoContabil() {
        return saldoContabil;
    }

    public void setSaldoContabil(BigDecimal saldoContabil) {
        this.saldoContabil = saldoContabil;
    }

    public String getSubConta() {
        return subConta;
    }

    public void setSubConta(String subConta) {
        this.subConta = subConta;
    }

    public Boolean getTemMovimentos() {
        return temMovimentos;
    }

    public void setTemMovimentos(Boolean temMovimentos) {
        this.temMovimentos = temMovimentos;
    }
}
