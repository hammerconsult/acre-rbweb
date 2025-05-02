package br.com.webpublico.negocios.tributario.consultaparcela.DTO;

import java.math.BigDecimal;
import java.util.Date;

public class ValoresPagosParcela {
    private BigDecimal original;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcaoMonetaria;
    private BigDecimal honorarios;
    private BigDecimal descontos;
    private BigDecimal totalPago;
    private Date dataPagamento;

    public ValoresPagosParcela() {
        this.original = BigDecimal.ZERO;
        this.juros = BigDecimal.ZERO;
        this.multa = BigDecimal.ZERO;
        this.correcaoMonetaria = BigDecimal.ZERO;
        this.honorarios = BigDecimal.ZERO;
        this.descontos = BigDecimal.ZERO;
        this.totalPago = BigDecimal.ZERO;
        this.dataPagamento = null;
    }

    public ValoresPagosParcela(Object[] obj) {
        this.original = (BigDecimal) obj[0];
        this.juros = (BigDecimal) obj[1];
        this.multa = (BigDecimal) obj[2];
        this.correcaoMonetaria = (BigDecimal) obj[3];
        this.honorarios = (BigDecimal) obj[4];
        this.descontos = (BigDecimal) obj[5];
        this.totalPago = (BigDecimal) obj[6];
        this.dataPagamento = obj[7] != null ? (Date) obj[7] : null;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecaoMonetaria() {
        return correcaoMonetaria;
    }

    public void setCorrecaoMonetaria(BigDecimal correcaoMonetaria) {
        this.correcaoMonetaria = correcaoMonetaria;
    }

    public BigDecimal getHonorarios() {
        return honorarios;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public BigDecimal getDescontos() {
        return descontos;
    }

    public void setDescontos(BigDecimal descontos) {
        this.descontos = descontos;
    }

    public BigDecimal getTotalPago() {
        return totalPago != null ? totalPago : BigDecimal.ZERO;
    }

    public void setTotalPago(BigDecimal totalPago) {
        this.totalPago = totalPago;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BigDecimal getOriginal() {
        return original;
    }

    public void setOriginal(BigDecimal original) {
        this.original = original;
    }
}
