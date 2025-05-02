package br.com.webpublico.nfse.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;


@JsonIgnoreProperties(ignoreUnknown = true)
public class TributosFederaisNfseDTO implements NfseDTO {

    private Long id;
    private BigDecimal pis;
    private BigDecimal percentualPis;
    private Boolean retencaoPis;
    private BigDecimal cofins;
    private BigDecimal percentualCofins;
    private Boolean retencaoCofins;
    private BigDecimal inss;
    private BigDecimal percentualInss;
    private Boolean retencaoInss;
    private BigDecimal irrf;
    private BigDecimal percentualIrrf;
    private Boolean retencaoIrrf;
    private BigDecimal csll;
    private BigDecimal percentualCsll;
    private Boolean retencaoCsll;
    private BigDecimal cpp;
    private BigDecimal percentualCpp;
    private Boolean retencaoCpp;
    private BigDecimal outrasRetencoes;
    private BigDecimal percentualOutrasRetencoes;
    private Boolean retencaoOutrasRetencoes;
    private Long prestadorServicosId;

    public TributosFederaisNfseDTO() {
        pis = BigDecimal.ZERO;
        cofins = BigDecimal.ZERO;
        inss = BigDecimal.ZERO;
        irrf = BigDecimal.ZERO;
        csll = BigDecimal.ZERO;
        cpp = BigDecimal.ZERO;
        percentualPis = BigDecimal.ZERO;
        percentualCofins = BigDecimal.ZERO;
        percentualInss = BigDecimal.ZERO;
        percentualIrrf = BigDecimal.ZERO;
        percentualCsll = BigDecimal.ZERO;
        percentualCpp = BigDecimal.ZERO;
        percentualOutrasRetencoes = BigDecimal.ZERO;
        outrasRetencoes = BigDecimal.ZERO;
        retencaoPis = false;
        retencaoCofins = false;
        retencaoInss = false;
        retencaoIrrf = false;
        retencaoCsll = false;
        retencaoCpp = false;
        retencaoOutrasRetencoes = false;
    }

    public TributosFederaisNfseDTO(DeclaracaoPrestacaoServicoNfseDTO declaracaoPrestacaoServico) {
        this.cofins = declaracaoPrestacaoServico.getTributosFederais().getCofins();
        this.percentualCofins = declaracaoPrestacaoServico.getTributosFederais().getPercentualCofins();
        this.csll = declaracaoPrestacaoServico.getTributosFederais().getCsll();
        this.percentualCsll = declaracaoPrestacaoServico.getTributosFederais().getPercentualCsll();
        this.inss = declaracaoPrestacaoServico.getTributosFederais().getInss();
        this.percentualInss = declaracaoPrestacaoServico.getTributosFederais().getPercentualInss();
        this.irrf = declaracaoPrestacaoServico.getTributosFederais().getIrrf();
        this.percentualIrrf = declaracaoPrestacaoServico.getTributosFederais().getPercentualIrrf();
        this.cpp = declaracaoPrestacaoServico.getTributosFederais().getCpp();
        this.percentualCpp = declaracaoPrestacaoServico.getTributosFederais().getPercentualCpp();
        this.outrasRetencoes = declaracaoPrestacaoServico.getTributosFederais().getOutrasRetencoes();
        this.percentualOutrasRetencoes = declaracaoPrestacaoServico.getTributosFederais().getPercentualOutrasRetencoes();
        this.pis = declaracaoPrestacaoServico.getTributosFederais().getPis();
        this.percentualPis = declaracaoPrestacaoServico.getTributosFederais().getPercentualPis();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPis() {
        return pis != null ? pis : BigDecimal.ZERO;
    }

    public void setPis(BigDecimal pis) {
        this.pis = pis;
    }

    public BigDecimal getPercentualPis() {
        return percentualPis != null ? percentualPis : BigDecimal.ZERO;
    }

    public void setPercentualPis(BigDecimal percentualPis) {
        this.percentualPis = percentualPis;
    }

    public Boolean getRetencaoPis() {
        return retencaoPis != null ? retencaoPis : Boolean.FALSE;
    }

    public void setRetencaoPis(Boolean retencaoPis) {
        this.retencaoPis = retencaoPis;
    }

    public BigDecimal getCofins() {
        return cofins != null ? cofins : BigDecimal.ZERO;
    }

    public void setCofins(BigDecimal cofins) {
        this.cofins = cofins;
    }

    public BigDecimal getPercentualCofins() {
        return percentualCofins != null ? percentualCofins : BigDecimal.ZERO;
    }

    public void setPercentualCofins(BigDecimal percentualCofins) {
        this.percentualCofins = percentualCofins;
    }

    public Boolean getRetencaoCofins() {
        return retencaoCofins != null ? retencaoCofins : Boolean.FALSE;
    }

    public void setRetencaoCofins(Boolean retencaoCofins) {
        this.retencaoCofins = retencaoCofins;
    }

    public BigDecimal getInss() {
        return inss != null ? inss : BigDecimal.ZERO;
    }

    public void setInss(BigDecimal inss) {
        this.inss = inss;
    }

    public BigDecimal getPercentualInss() {
        return percentualInss != null ? percentualInss : BigDecimal.ZERO;
    }

    public void setPercentualInss(BigDecimal percentualInss) {
        this.percentualInss = percentualInss;
    }

    public Boolean getRetencaoInss() {
        return retencaoInss != null ? retencaoInss : Boolean.FALSE;
    }

    public void setRetencaoInss(Boolean retencaoInss) {
        this.retencaoInss = retencaoInss;
    }

    public BigDecimal getIrrf() {
        return irrf != null ? irrf : BigDecimal.ZERO;
    }

    public void setIrrf(BigDecimal irrf) {
        this.irrf = irrf;
    }

    public BigDecimal getPercentualIrrf() {
        return percentualIrrf != null ? percentualIrrf : BigDecimal.ZERO;
    }

    public void setPercentualIrrf(BigDecimal percentualIrrf) {
        this.percentualIrrf = percentualIrrf;
    }

    public Boolean getRetencaoIrrf() {
        return retencaoIrrf != null ? retencaoIrrf : Boolean.FALSE;
    }

    public void setRetencaoIrrf(Boolean retencaoIrrf) {
        this.retencaoIrrf = retencaoIrrf;
    }

    public BigDecimal getCsll() {
        return csll != null ? csll : BigDecimal.ZERO;
    }

    public void setCsll(BigDecimal csll) {
        this.csll = csll;
    }

    public BigDecimal getPercentualCsll() {
        return percentualCsll != null ? percentualCsll : BigDecimal.ZERO;
    }

    public void setPercentualCsll(BigDecimal percentualCsll) {
        this.percentualCsll = percentualCsll;
    }

    public Boolean getRetencaoCsll() {
        return retencaoCsll != null ? retencaoCsll : Boolean.FALSE;
    }

    public void setRetencaoCsll(Boolean retencaoCsll) {
        this.retencaoCsll = retencaoCsll;
    }

    public BigDecimal getCpp() {
        return cpp != null ? cpp : BigDecimal.ZERO;
    }

    public void setCpp(BigDecimal cpp) {
        this.cpp = cpp;
    }

    public BigDecimal getPercentualCpp() {
        return percentualCpp != null ? percentualCpp : BigDecimal.ZERO;
    }

    public void setPercentualCpp(BigDecimal percentualCpp) {
        this.percentualCpp = percentualCpp;
    }

    public Boolean getRetencaoCpp() {
        return retencaoCpp != null ? retencaoCpp : Boolean.FALSE;
    }

    public void setRetencaoCpp(Boolean retencaoCpp) {
        this.retencaoCpp = retencaoCpp;
    }

    public BigDecimal getOutrasRetencoes() {
        return outrasRetencoes != null ? outrasRetencoes : BigDecimal.ZERO;
    }

    public void setOutrasRetencoes(BigDecimal outrasRetencoes) {
        this.outrasRetencoes = outrasRetencoes;
    }

    public BigDecimal getPercentualOutrasRetencoes() {
        return percentualOutrasRetencoes != null ? percentualOutrasRetencoes : BigDecimal.ZERO;
    }

    public void setPercentualOutrasRetencoes(BigDecimal percentualOutrasRetencoes) {
        this.percentualOutrasRetencoes = percentualOutrasRetencoes;
    }

    public Boolean getRetencaoOutrasRetencoes() {
        return retencaoOutrasRetencoes != null ? retencaoOutrasRetencoes : Boolean.FALSE;
    }

    public void setRetencaoOutrasRetencoes(Boolean retencaoOutrasRetencoes) {
        this.retencaoOutrasRetencoes = retencaoOutrasRetencoes;
    }

    public Long getPrestadorServicosId() {
        return prestadorServicosId;
    }

    public void setPrestadorServicosId(Long prestadorServicosId) {
        this.prestadorServicosId = prestadorServicosId;
    }

    @JsonIgnore
    public Boolean hasAlgumCampoPreenchido() {
        return pis != null || cofins != null || inss != null || irrf != null || csll != null || outrasRetencoes != null || cpp != null;
    }

    @JsonIgnore
    public BigDecimal getTotalRetencoes() {
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(getPis());
        total = total.add(getCofins());
        total = total.add(getInss());
        total = total.add(getIrrf());
        total = total.add(getCsll());
        total = total.add(getCpp());
        total = total.add(getOutrasRetencoes());
        return total;
    }


}
