package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;

public class RepasseFinanceiroAbrasf implements Serializable {
    private String periodoSemana;
    private String descricaoOutros;
    private Integer ano;
    private Integer anoAnterior;
    private BigDecimal qpIcms;
    private BigDecimal qpIcmsAnterior;
    private BigDecimal ipva;
    private BigDecimal ipvaAnterior;
    private BigDecimal fpm;
    private BigDecimal fpmAnterior;
    private BigDecimal outros;
    private BigDecimal outrosAnterior;

    public RepasseFinanceiroAbrasf(Integer ano) {
        if (ano != null) {
            this.ano = ano;
            this.anoAnterior = (ano - 1);
        }
        this.qpIcms = BigDecimal.ZERO;
        this.qpIcmsAnterior = BigDecimal.ZERO;
        this.ipva = BigDecimal.ZERO;
        this.ipvaAnterior = BigDecimal.ZERO;
        this.fpm = BigDecimal.ZERO;
        this.fpmAnterior = BigDecimal.ZERO;
        this.outros = BigDecimal.ZERO;
        this.outrosAnterior = BigDecimal.ZERO;
    }

    public String getPeriodoSemana() {
        return periodoSemana;
    }

    public void setPeriodoSemana(String periodoSemana) {
        this.periodoSemana = periodoSemana;
    }

    public String getDescricaoOutros() {
        return descricaoOutros;
    }

    public void setDescricaoOutros(String descricaoOutros) {
        this.descricaoOutros = descricaoOutros;
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

    public BigDecimal getQpIcms() {
        return qpIcms;
    }

    public void setQpIcms(BigDecimal qpIcms) {
        this.qpIcms = qpIcms;
    }

    public BigDecimal getQpIcmsAnterior() {
        return qpIcmsAnterior;
    }

    public void setQpIcmsAnterior(BigDecimal qpIcmsAnterior) {
        this.qpIcmsAnterior = qpIcmsAnterior;
    }

    public BigDecimal getIpva() {
        return ipva;
    }

    public void setIpva(BigDecimal ipva) {
        this.ipva = ipva;
    }

    public BigDecimal getIpvaAnterior() {
        return ipvaAnterior;
    }

    public void setIpvaAnterior(BigDecimal ipvaAnterior) {
        this.ipvaAnterior = ipvaAnterior;
    }

    public BigDecimal getFpm() {
        return fpm;
    }

    public void setFpm(BigDecimal fpm) {
        this.fpm = fpm;
    }

    public BigDecimal getFpmAnterior() {
        return fpmAnterior;
    }

    public void setFpmAnterior(BigDecimal fpmAnterior) {
        this.fpmAnterior = fpmAnterior;
    }

    public BigDecimal getOutros() {
        return outros;
    }

    public void setOutros(BigDecimal outros) {
        this.outros = outros;
    }

    public BigDecimal getOutrosAnterior() {
        return outrosAnterior;
    }

    public void setOutrosAnterior(BigDecimal outrosAnterior) {
        this.outrosAnterior = outrosAnterior;
    }
}
