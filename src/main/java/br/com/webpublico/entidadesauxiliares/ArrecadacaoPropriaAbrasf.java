package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;

public class ArrecadacaoPropriaAbrasf implements Serializable {
    private String periodoSemana;
    private Integer ano;
    private Integer anoAnterior;
    private BigDecimal iss;
    private BigDecimal issAnterior;
    private BigDecimal iptu;
    private BigDecimal iptuAnterior;
    private BigDecimal itbi;
    private BigDecimal itbiAnterior;
    private BigDecimal dividaAtiva;
    private BigDecimal dividaAtivaAnterior;

    public ArrecadacaoPropriaAbrasf(Integer ano) {
        if (ano != null) {
            this.ano = ano;
            this.anoAnterior = (ano - 1);
        }
        this.iss = BigDecimal.ZERO;
        this.issAnterior = BigDecimal.ZERO;
        this.iptu = BigDecimal.ZERO;
        this.iptuAnterior = BigDecimal.ZERO;
        this.itbi = BigDecimal.ZERO;
        this.itbiAnterior = BigDecimal.ZERO;
        this.dividaAtiva = BigDecimal.ZERO;
        this.dividaAtivaAnterior = BigDecimal.ZERO;
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

    public BigDecimal getIss() {
        return iss;
    }

    public void setIss(BigDecimal iss) {
        this.iss = iss;
    }

    public BigDecimal getIssAnterior() {
        return issAnterior;
    }

    public void setIssAnterior(BigDecimal issAnterior) {
        this.issAnterior = issAnterior;
    }

    public BigDecimal getIptu() {
        return iptu;
    }

    public void setIptu(BigDecimal iptu) {
        this.iptu = iptu;
    }

    public BigDecimal getIptuAnterior() {
        return iptuAnterior;
    }

    public void setIptuAnterior(BigDecimal iptuAnterior) {
        this.iptuAnterior = iptuAnterior;
    }

    public BigDecimal getItbi() {
        return itbi;
    }

    public void setItbi(BigDecimal itbi) {
        this.itbi = itbi;
    }

    public BigDecimal getItbiAnterior() {
        return itbiAnterior;
    }

    public void setItbiAnterior(BigDecimal itbiAnterior) {
        this.itbiAnterior = itbiAnterior;
    }

    public BigDecimal getDividaAtiva() {
        return dividaAtiva;
    }

    public void setDividaAtiva(BigDecimal dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public BigDecimal getDividaAtivaAnterior() {
        return dividaAtivaAnterior;
    }

    public void setDividaAtivaAnterior(BigDecimal dividaAtivaAnterior) {
        this.dividaAtivaAnterior = dividaAtivaAnterior;
    }
}
