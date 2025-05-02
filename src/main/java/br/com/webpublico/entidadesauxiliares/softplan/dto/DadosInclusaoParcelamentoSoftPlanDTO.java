package br.com.webpublico.entidadesauxiliares.softplan.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

@JsonTypeName("dadosInclusaoParcelamento")
public class DadosInclusaoParcelamentoSoftPlanDTO extends DadosServicoSoftPlanDTO {

    private Long idParcelamento;
    private Long idCda;
    private String nuParcelamento;
    private String dtParcelamento;
    private String nuParcelas;
    private BigDecimal vlTotalParcelamento;
    private List<CDA> listaCDA;

    public DadosInclusaoParcelamentoSoftPlanDTO() {
        listaCDA = Lists.newArrayList();
    }

    public Long getIdParcelamento() {
        return idParcelamento;
    }

    public void setIdParcelamento(Long idParcelamento) {
        this.idParcelamento = idParcelamento;
    }

    public Long getIdCda() {
        return idCda;
    }

    public void setIdCda(Long idCda) {
        this.idCda = idCda;
    }

    public String getNuParcelamento() {
        return nuParcelamento;
    }

    public void setNuParcelamento(String nuParcelamento) {
        this.nuParcelamento = nuParcelamento;
    }

    public String getDtParcelamento() {
        return dtParcelamento;
    }

    public void setDtParcelamento(String dtParcelamento) {
        this.dtParcelamento = dtParcelamento;
    }

    public String getNuParcelas() {
        return nuParcelas;
    }

    public void setNuParcelas(String nuParcelas) {
        this.nuParcelas = nuParcelas;
    }

    public BigDecimal getVlTotalParcelamento() {
        return vlTotalParcelamento;
    }

    public void setVlTotalParcelamento(BigDecimal vlTotalParcelamento) {
        this.vlTotalParcelamento = vlTotalParcelamento;
    }

    public List<CDA> getListaCDA() {
        return listaCDA;
    }

    public void setListaCDA(List<CDA> listaCDA) {
        this.listaCDA = listaCDA;
    }

    public static class CDA {

        private String nuCDA;
        private String cdTipoImposto;
        private String nuRefDivida;
        private String nuMesRefDivida;
        private String nuSeqComp;
        private String seq;

        public CDA() {
        }

        public String getNuCDA() {
            return nuCDA;
        }

        public void setNuCDA(String nuCDA) {
            this.nuCDA = nuCDA;
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

        public String getNuSeqComp() {
            return nuSeqComp;
        }

        public void setNuSeqComp(String nuSeqComp) {
            this.nuSeqComp = nuSeqComp;
        }

        public String getSeq() {
            return seq;
        }

        public void setSeq(String seq) {
            this.seq = seq;
        }
    }

}
