package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;

public class NotasFiscaisAbrasf implements Serializable {
    private String periodoSemana;
    private Integer ano;
    private Integer anoAnterior;
    private Integer notasEmitidas;
    private Integer notasEmitidasAnterior;
    private Integer notasAvulsasEmitidas;
    private Integer notasAvulsasEmitidasAnterior;
    private BigDecimal valorNotas;
    private BigDecimal valorNotasAnterior;
    private BigDecimal valorNotasAvulsas;
    private BigDecimal valorNotasAvulsasAnterior;
    private BigDecimal valorTaxasDiversas;
    private BigDecimal valorTaxasDiversasAnterior;

    public NotasFiscaisAbrasf(Integer ano) {
        if (ano != null) {
            this.ano = ano;
            this.anoAnterior = (ano - 1);
        }
        this.valorNotas = BigDecimal.ZERO;
        this.valorNotasAnterior = BigDecimal.ZERO;
        this.valorNotasAvulsas = BigDecimal.ZERO;
        this.valorNotasAvulsasAnterior = BigDecimal.ZERO;
        this.valorTaxasDiversas = BigDecimal.ZERO;
        this.valorTaxasDiversasAnterior = BigDecimal.ZERO;
    }

    public String getPeriodoSemana() {
        return periodoSemana;
    }

    public void setPeriodoSemana(String periodoSemana) {
        this.periodoSemana = periodoSemana;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getAnoAnterior() {
        return anoAnterior;
    }

    public void setAnoAnterior(Integer anoAnterior) {
        this.anoAnterior = anoAnterior;
    }

    public Integer getNotasEmitidas() {
        return notasEmitidas;
    }

    public void setNotasEmitidas(Integer notasEmitidas) {
        this.notasEmitidas = notasEmitidas;
    }

    public Integer getNotasEmitidasAnterior() {
        return notasEmitidasAnterior;
    }

    public void setNotasEmitidasAnterior(Integer notasEmitidasAnterior) {
        this.notasEmitidasAnterior = notasEmitidasAnterior;
    }

    public Integer getNotasAvulsasEmitidas() {
        return notasAvulsasEmitidas;
    }

    public void setNotasAvulsasEmitidas(Integer notasAvulsasEmitidas) {
        this.notasAvulsasEmitidas = notasAvulsasEmitidas;
    }

    public Integer getNotasAvulsasEmitidasAnterior() {
        return notasAvulsasEmitidasAnterior;
    }

    public void setNotasAvulsasEmitidasAnterior(Integer notasAvulsasEmitidasAnterior) {
        this.notasAvulsasEmitidasAnterior = notasAvulsasEmitidasAnterior;
    }

    public BigDecimal getValorNotas() {
        return valorNotas;
    }

    public void setValorNotas(BigDecimal valorNotas) {
        this.valorNotas = valorNotas;
    }

    public BigDecimal getValorNotasAnterior() {
        return valorNotasAnterior;
    }

    public void setValorNotasAnterior(BigDecimal valorNotasAnterior) {
        this.valorNotasAnterior = valorNotasAnterior;
    }

    public BigDecimal getValorNotasAvulsas() {
        return valorNotasAvulsas;
    }

    public void setValorNotasAvulsas(BigDecimal valorNotasAvulsas) {
        this.valorNotasAvulsas = valorNotasAvulsas;
    }

    public BigDecimal getValorNotasAvulsasAnterior() {
        return valorNotasAvulsasAnterior;
    }

    public void setValorNotasAvulsasAnterior(BigDecimal valorNotasAvulsasAnterior) {
        this.valorNotasAvulsasAnterior = valorNotasAvulsasAnterior;
    }

    public BigDecimal getValorTaxasDiversas() {
        return valorTaxasDiversas;
    }

    public void setValorTaxasDiversas(BigDecimal valorTaxasDiversas) {
        this.valorTaxasDiversas = valorTaxasDiversas;
    }

    public BigDecimal getValorTaxasDiversasAnterior() {
        return valorTaxasDiversasAnterior;
    }

    public void setValorTaxasDiversasAnterior(BigDecimal valorTaxasDiversasAnterior) {
        this.valorTaxasDiversasAnterior = valorTaxasDiversasAnterior;
    }
}
