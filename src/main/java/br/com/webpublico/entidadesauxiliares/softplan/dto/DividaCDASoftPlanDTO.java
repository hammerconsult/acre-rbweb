package br.com.webpublico.entidadesauxiliares.softplan.dto;

import java.math.BigDecimal;

public class DividaCDASoftPlanDTO {

    private String cdTipoImposto;
    private String nuRefDivida;
    private String nuMesRefDivida;
    private String deFundamentoLegal;
    private String dtInscricao;
    private String dtPrescricao;
    private String dtVencimento;
    private String nuAnoComp;
    private String nuLivroComp;
    private String nuSeqComp;
    private String nuPagComp;
    private BigDecimal vlPrincipalOriginal;
    private BigDecimal vlPrincipalAtualizado;
    private BigDecimal vlCorrecaoAtualizado;
    private BigDecimal vlJurosAtualizado;
    private BigDecimal vlMultaAtualizado;
    private BigDecimal vlTotalAtualizado;
    private String seq;

    public DividaCDASoftPlanDTO() {
    }

    public String getCdTipoImposto() {
        return cdTipoImposto;
    }

    public void setCdTipoImposto(String cdTipoImposto) {
        this.cdTipoImposto = cdTipoImposto;
    }

    public String getNuRefDivida() {
        return nuRefDivida;
    }

    public void setNuRefDivida(String nuRefDivida) {
        this.nuRefDivida = nuRefDivida;
    }

    public String getNuMesRefDivida() {
        return nuMesRefDivida;
    }

    public void setNuMesRefDivida(String nuMesRefDivida) {
        this.nuMesRefDivida = nuMesRefDivida;
    }

    public String getDeFundamentoLegal() {
        return deFundamentoLegal;
    }

    public void setDeFundamentoLegal(String deFundamentoLegal) {
        this.deFundamentoLegal = deFundamentoLegal;
    }

    public String getDtInscricao() {
        return dtInscricao;
    }

    public void setDtInscricao(String dtInscricao) {
        this.dtInscricao = dtInscricao;
    }

    public String getDtPrescricao() {
        return dtPrescricao;
    }

    public void setDtPrescricao(String dtPrescricao) {
        this.dtPrescricao = dtPrescricao;
    }

    public String getDtVencimento() {
        return dtVencimento;
    }

    public void setDtVencimento(String dtVencimento) {
        this.dtVencimento = dtVencimento;
    }

    public String getNuAnoComp() {
        return nuAnoComp;
    }

    public void setNuAnoComp(String nuAnoComp) {
        this.nuAnoComp = nuAnoComp;
    }

    public String getNuLivroComp() {
        return nuLivroComp;
    }

    public void setNuLivroComp(String nuLivroComp) {
        this.nuLivroComp = nuLivroComp;
    }

    public String getNuSeqComp() {
        return nuSeqComp;
    }

    public void setNuSeqComp(String nuSeqComp) {
        this.nuSeqComp = nuSeqComp;
    }

    public String getNuPagComp() {
        return nuPagComp;
    }

    public void setNuPagComp(String nuPagComp) {
        this.nuPagComp = nuPagComp;
    }

    public BigDecimal getVlPrincipalOriginal() {
        return vlPrincipalOriginal;
    }

    public void setVlPrincipalOriginal(BigDecimal vlPrincipalOriginal) {
        this.vlPrincipalOriginal = vlPrincipalOriginal;
    }

    public BigDecimal getVlPrincipalAtualizado() {
        return vlPrincipalAtualizado;
    }

    public void setVlPrincipalAtualizado(BigDecimal vlPrincipalAtualizado) {
        this.vlPrincipalAtualizado = vlPrincipalAtualizado;
    }

    public BigDecimal getVlCorrecaoAtualizado() {
        return vlCorrecaoAtualizado;
    }

    public void setVlCorrecaoAtualizado(BigDecimal vlCorrecaoAtualizado) {
        this.vlCorrecaoAtualizado = vlCorrecaoAtualizado;
    }

    public BigDecimal getVlJurosAtualizado() {
        return vlJurosAtualizado;
    }

    public void setVlJurosAtualizado(BigDecimal vlJurosAtualizado) {
        this.vlJurosAtualizado = vlJurosAtualizado;
    }

    public BigDecimal getVlMultaAtualizado() {
        return vlMultaAtualizado;
    }

    public void setVlMultaAtualizado(BigDecimal vlMultaAtualizado) {
        this.vlMultaAtualizado = vlMultaAtualizado;
    }

    public BigDecimal getVlTotalAtualizado() {
        return vlTotalAtualizado;
    }

    public void setVlTotalAtualizado(BigDecimal vlTotalAtualizado) {
        this.vlTotalAtualizado = vlTotalAtualizado;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
}
