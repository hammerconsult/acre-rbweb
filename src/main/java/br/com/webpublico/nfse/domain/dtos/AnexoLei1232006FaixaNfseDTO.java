package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

public class AnexoLei1232006FaixaNfseDTO implements NfseDTO {

    private Long id;
    private String faixa;
    private BigDecimal valorMaximo;
    private BigDecimal aliquota;
    private BigDecimal valorDeduzir;
    private BigDecimal irpj;
    private BigDecimal csll;
    private BigDecimal cofins;
    private BigDecimal pisPasep;
    private BigDecimal cpp;
    private BigDecimal icms;
    private BigDecimal ipi;
    private BigDecimal iss;

    public AnexoLei1232006FaixaNfseDTO() {
    }

    public AnexoLei1232006FaixaNfseDTO(Long id, String faixa, BigDecimal valorMaximo,
                                       BigDecimal aliquota, BigDecimal valorDeduzir,
                                       BigDecimal irpj, BigDecimal csll, BigDecimal cofins,
                                       BigDecimal pisPasep, BigDecimal cpp, BigDecimal icms,
                                       BigDecimal ipi, BigDecimal iss) {
        this.id = id;
        this.faixa = faixa;
        this.valorMaximo = valorMaximo;
        this.aliquota = aliquota;
        this.valorDeduzir = valorDeduzir;
        this.irpj = irpj;
        this.csll = csll;
        this.cofins = cofins;
        this.pisPasep = pisPasep;
        this.cpp = cpp;
        this.icms = icms;
        this.ipi = ipi;
        this.iss = iss;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFaixa() {
        return faixa;
    }

    public void setFaixa(String faixa) {
        this.faixa = faixa;
    }

    public BigDecimal getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(BigDecimal valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public BigDecimal getValorDeduzir() {
        return valorDeduzir;
    }

    public void setValorDeduzir(BigDecimal valorDeduzir) {
        this.valorDeduzir = valorDeduzir;
    }

    public BigDecimal getIrpj() {
        return irpj;
    }

    public void setIrpj(BigDecimal irpj) {
        this.irpj = irpj;
    }

    public BigDecimal getCsll() {
        return csll;
    }

    public void setCsll(BigDecimal csll) {
        this.csll = csll;
    }

    public BigDecimal getCofins() {
        return cofins;
    }

    public void setCofins(BigDecimal cofins) {
        this.cofins = cofins;
    }

    public BigDecimal getPisPasep() {
        return pisPasep;
    }

    public void setPisPasep(BigDecimal pisPasep) {
        this.pisPasep = pisPasep;
    }

    public BigDecimal getCpp() {
        return cpp;
    }

    public void setCpp(BigDecimal cpp) {
        this.cpp = cpp;
    }

    public BigDecimal getIcms() {
        return icms;
    }

    public void setIcms(BigDecimal icms) {
        this.icms = icms;
    }

    public BigDecimal getIpi() {
        return ipi;
    }

    public void setIpi(BigDecimal ipi) {
        this.ipi = ipi;
    }

    public BigDecimal getIss() {
        return iss;
    }

    public void setIss(BigDecimal iss) {
        this.iss = iss;
    }
}
