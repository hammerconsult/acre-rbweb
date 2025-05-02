package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 26/06/15
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 */
public class VOAgrupamentoIptuPorCadastro implements Serializable {

    private String cadastro;
    private BigDecimal valorImposto;
    private BigDecimal valorTaxa;
    private BigDecimal valorCorrecao;
    private BigDecimal valorJuros;
    private BigDecimal valorMulta;
    private BigDecimal valorHonorarios;
    private BigDecimal valorDesconto;
    private BigDecimal valorPago;
    private BigDecimal valorTotal;

    public VOAgrupamentoIptuPorCadastro() {
        this.valorImposto = BigDecimal.ZERO;
        this.valorTaxa = BigDecimal.ZERO;
        this.valorJuros = BigDecimal.ZERO;
        this.valorMulta = BigDecimal.ZERO;
        this.valorCorrecao = BigDecimal.ZERO;
        this.valorHonorarios = BigDecimal.ZERO;
        this.valorDesconto = BigDecimal.ZERO;
        this.valorPago = BigDecimal.ZERO;
        this.valorTotal = BigDecimal.ZERO;
    }

    public VOAgrupamentoIptuPorCadastro(Object[] obj) {
        this.preencher(obj);
    }

    public void preencher(Object[] obj) {
        this.cadastro = ((String) obj[0]);
        this.valorImposto = ((BigDecimal) obj[1]);
        this.valorTaxa = ((BigDecimal) obj[2]);
        this.valorJuros = ((BigDecimal) obj[3]);
        this.valorMulta = ((BigDecimal) obj[4]);
        this.valorCorrecao = ((BigDecimal) obj[5]);
        this.valorDesconto = ((BigDecimal) obj[6]);
        this.valorHonorarios = ((BigDecimal) obj[7]);
        this.valorTotal = ((BigDecimal) obj[8]);
    }

    public String getCadastro() {
        return cadastro;
    }

    public void setCadastro(String cadastro) {
        this.cadastro = cadastro;
    }

    public BigDecimal getValorImposto() {
        return valorImposto;
    }

    public void setValorImposto(BigDecimal valorImposto) {
        this.valorImposto = valorImposto;
    }

    public BigDecimal getValorTaxa() {
        return valorTaxa;
    }

    public void setValorTaxa(BigDecimal valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    public BigDecimal getValorCorrecao() {
        return valorCorrecao;
    }

    public void setValorCorrecao(BigDecimal valorCorrecao) {
        this.valorCorrecao = valorCorrecao;
    }

    public BigDecimal getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getValorHonorarios() {
        return valorHonorarios;
    }

    public void setValorHonorarios(BigDecimal valorHonorarios) {
        this.valorHonorarios = valorHonorarios;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
