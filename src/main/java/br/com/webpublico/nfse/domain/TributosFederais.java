package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.domain.dtos.TributosFederaisNfseDTO;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * A TributosFederaisNfseDTO.
 */
@Entity
@Audited
public class TributosFederais extends SuperEntidade implements Cloneable, Serializable, NfseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    public TributosFederais() {
        pis = BigDecimal.ZERO;
        cofins = BigDecimal.ZERO;
        inss = BigDecimal.ZERO;
        irrf = BigDecimal.ZERO;
        csll = BigDecimal.ZERO;
        cpp = BigDecimal.ZERO;
        outrasRetencoes = BigDecimal.ZERO;
        percentualPis = BigDecimal.ZERO;
        percentualCofins = BigDecimal.ZERO;
        percentualInss = BigDecimal.ZERO;
        percentualIrrf = BigDecimal.ZERO;
        percentualCsll = BigDecimal.ZERO;
        percentualCpp = BigDecimal.ZERO;
        percentualOutrasRetencoes = BigDecimal.ZERO;
        retencaoPis = false;
        retencaoCofins = false;
        retencaoInss = false;
        retencaoIrrf = false;
        retencaoCsll = false;
        retencaoCpp = false;
        retencaoOutrasRetencoes = false;
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

    public BigDecimal getCofins() {
        return cofins != null ? cofins : BigDecimal.ZERO;
    }

    public void setCofins(BigDecimal cofins) {
        this.cofins = cofins;
    }

    public BigDecimal getInss() {
        return inss != null ? inss : BigDecimal.ZERO;
    }

    public void setInss(BigDecimal inss) {
        this.inss = inss;
    }

    public BigDecimal getIrrf() {
        return irrf != null ? irrf : BigDecimal.ZERO;
    }

    public void setIrrf(BigDecimal irrf) {
        this.irrf = irrf;
    }

    public BigDecimal getCsll() {
        return csll != null ? csll : BigDecimal.ZERO;
    }

    public void setCsll(BigDecimal csll) {
        this.csll = csll;
    }

    public BigDecimal getOutrasRetencoes() {
        return outrasRetencoes != null ? outrasRetencoes : BigDecimal.ZERO;
    }

    public void setOutrasRetencoes(BigDecimal outrasRetencoes) {
        this.outrasRetencoes = outrasRetencoes;
    }

    public BigDecimal getCpp() {
        return cpp;
    }

    public void setCpp(BigDecimal cpp) {
        this.cpp = cpp;
    }

    public Boolean getRetencaoPis() {
        return getBooleanFalse(retencaoPis);
    }

    public void setRetencaoPis(Boolean retencaoPis) {
        this.retencaoPis = retencaoPis;
    }

    public Boolean getRetencaoCofins() {
        return getBooleanFalse(retencaoCofins);
    }

    public void setRetencaoCofins(Boolean retencaoCofins) {
        this.retencaoCofins = retencaoCofins;
    }

    public Boolean getRetencaoInss() {
        return getBooleanFalse(retencaoInss);
    }

    public void setRetencaoInss(Boolean retencaoInss) {
        this.retencaoInss = retencaoInss;
    }

    public Boolean getRetencaoIrrf() {
        return getBooleanFalse(retencaoIrrf);
    }

    public void setRetencaoIrrf(Boolean retencaoIrrf) {
        this.retencaoIrrf = retencaoIrrf;
    }

    public Boolean getRetencaoCsll() {
        return getBooleanFalse(retencaoCsll);
    }

    public void setRetencaoCsll(Boolean retencaoCsll) {
        this.retencaoCsll = retencaoCsll;
    }

    public Boolean getRetencaoCpp() {
        return getBooleanFalse(retencaoCpp);
    }

    public void setRetencaoCpp(Boolean retencaoCpp) {
        this.retencaoCpp = retencaoCpp;
    }

    public Boolean getRetencaoOutrasRetencoes() {
        return getBooleanFalse(retencaoOutrasRetencoes);
    }

    public void setRetencaoOutrasRetencoes(Boolean retencaoOutrasRetencoes) {
        this.retencaoOutrasRetencoes = retencaoOutrasRetencoes;
    }

    public BigDecimal getPercentualPis() {
        return percentualPis;
    }

    public void setPercentualPis(BigDecimal percentualPis) {
        this.percentualPis = percentualPis;
    }

    public BigDecimal getPercentualCofins() {
        return percentualCofins;
    }

    public void setPercentualCofins(BigDecimal percentualCofins) {
        this.percentualCofins = percentualCofins;
    }

    public BigDecimal getPercentualInss() {
        return percentualInss;
    }

    public void setPercentualInss(BigDecimal percentualInss) {
        this.percentualInss = percentualInss;
    }

    public BigDecimal getPercentualIrrf() {
        return percentualIrrf;
    }

    public void setPercentualIrrf(BigDecimal percentualIrrf) {
        this.percentualIrrf = percentualIrrf;
    }

    public BigDecimal getPercentualCsll() {
        return percentualCsll;
    }

    public void setPercentualCsll(BigDecimal percentualCsll) {
        this.percentualCsll = percentualCsll;
    }

    public BigDecimal getPercentualCpp() {
        return percentualCpp;
    }

    public void setPercentualCpp(BigDecimal percentualCpp) {
        this.percentualCpp = percentualCpp;
    }

    public BigDecimal getPercentualOutrasRetencoes() {
        return percentualOutrasRetencoes;
    }

    public void setPercentualOutrasRetencoes(BigDecimal percentualOutrasRetencoes) {
        this.percentualOutrasRetencoes = percentualOutrasRetencoes;
    }

    public BigDecimal getTotalTributos() {
        BigDecimal total = BigDecimal.ZERO;
        if (this.pis != null) {
            total = total.add(this.pis);
        }
        if (this.cofins != null) {
            total = total.add(this.cofins);
        }
        if (this.inss != null) {
            total = total.add(this.inss);
        }
        if (this.irrf != null) {
            total = total.add(this.irrf);
        }
        if (this.csll != null) {
            total = total.add(this.csll);
        }
        if (this.cpp != null) {
            total = total.add(this.cpp);
        }
        if (this.outrasRetencoes != null) {
            total = total.add(this.outrasRetencoes);
        }
        return total;
    }

    public BigDecimal getTotalRetencoes() {
        BigDecimal total = BigDecimal.ZERO;
        if (this.pis != null) {
            total = total.add(this.pis);
        }
        if (this.cofins != null) {
            total = total.add(this.cofins);
        }
        if (this.inss != null) {
            total = total.add(this.inss);
        }
        if (this.irrf != null) {
            total = total.add(this.irrf);
        }
        if (this.csll != null) {
            total = total.add(this.csll);
        }
        if (this.cpp != null) {
            total = total.add(this.cpp);
        }
        return total;
    }

    @Override
    public String toString() {
        return "TributosFederaisNfseDTO{" +
            "id=" + id +
            ", pis='" + pis + "'" +
            ", cofins='" + cofins + "'" +
            ", inss='" + inss + "'" +
            ", irrf='" + irrf + "'" +
            ", csll='" + csll + "'" +
            ", cpp='" + cpp + "'" +
            ", outrasRetencoes='" + outrasRetencoes + "'" +
            '}';
    }

    @Override
    public TributosFederaisNfseDTO toNfseDto() {
        TributosFederaisNfseDTO dto = new TributosFederaisNfseDTO();
        dto.setCofins(getCofins());
        dto.setPercentualCofins(getPercentualCofins());
        dto.setRetencaoCofins(getRetencaoCofins());
        dto.setCsll(getCsll());
        dto.setPercentualCsll(getPercentualCsll());
        dto.setRetencaoCsll(getRetencaoCsll());
        dto.setInss(getInss());
        dto.setPercentualInss(getPercentualInss());
        dto.setRetencaoInss(getRetencaoInss());
        dto.setIrrf(getIrrf());
        dto.setPercentualIrrf(getPercentualIrrf());
        dto.setRetencaoIrrf(getRetencaoIrrf());
        dto.setOutrasRetencoes(getOutrasRetencoes());
        dto.setPercentualOutrasRetencoes(getPercentualOutrasRetencoes());
        dto.setRetencaoOutrasRetencoes(getRetencaoOutrasRetencoes());
        dto.setPis(getPis());
        dto.setPercentualPis(getPercentualPis());
        dto.setRetencaoPis(getRetencaoPis());
        dto.setCpp(getCpp());
        dto.setPercentualCpp(getPercentualCpp());
        dto.setRetencaoCpp(getRetencaoCpp());
        return dto;
    }

    @Override
    public TributosFederais clone() {
        TributosFederais dto = new TributosFederais();
        dto.setCofins(getCofins());
        dto.setCsll(getCsll());
        dto.setInss(getInss());
        dto.setIrrf(getIrrf());
        dto.setPis(getPis());
        dto.setCpp(getCpp());
        dto.setOutrasRetencoes(getOutrasRetencoes());

        dto.setPercentualCofins(getPercentualCofins());
        dto.setPercentualCsll(getPercentualCsll());
        dto.setPercentualInss(getPercentualInss());
        dto.setPercentualIrrf(getPercentualIrrf());
        dto.setPercentualPis(getPercentualPis());
        dto.setPercentualCpp(getPercentualCpp());
        dto.setPercentualOutrasRetencoes(getPercentualOutrasRetencoes());

        dto.setRetencaoCofins(getRetencaoCofins());
        dto.setRetencaoCsll(getRetencaoCsll());
        dto.setRetencaoInss(getRetencaoInss());
        dto.setRetencaoIrrf(getRetencaoIrrf());
        dto.setRetencaoOutrasRetencoes(getRetencaoOutrasRetencoes());
        dto.setRetencaoPis(getRetencaoPis());
        dto.setRetencaoCpp(getRetencaoCpp());
        return dto;
    }

    public static Boolean getBooleanFalse(Boolean valor) {
        return valor != null ? valor : false;
    }
}
