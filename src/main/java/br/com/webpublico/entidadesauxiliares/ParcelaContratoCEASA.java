/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Andre
 */
public class ParcelaContratoCEASA implements Serializable {
    private Integer parcela;
    private BigDecimal valor;
    private BigDecimal valorRateio;
    private BigDecimal valorTotal;
    private Date vencimento;

    public ParcelaContratoCEASA() {
    }

    public Integer getParcela() {
        return parcela;
    }

    public void setParcela(Integer parcela) {
        this.parcela = parcela;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public BigDecimal getValorRateio() {
        return valorRateio;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void setValorRateio(BigDecimal valorRateio) {
        this.valorRateio = valorRateio;
    }
}
