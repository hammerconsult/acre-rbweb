package br.com.webpublico.entidadesauxiliares.softplan.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("dadosAlteracaoCDA")
public class DadosAlteracaoCDASoftplanDTO extends DadosServicoSoftPlanDTO {

    private Long idCda;
    private Long nuCDA;
    private Integer tpSituacao;
    private String dtEvento;
    private String deObservacao;

    public DadosAlteracaoCDASoftplanDTO() {
    }

    public Long getIdCda() {
        return idCda;
    }

    public void setIdCda(Long idCda) {
        this.idCda = idCda;
    }

    public Long getNuCDA() {
        return nuCDA;
    }

    public void setNuCDA(Long nuCDA) {
        this.nuCDA = nuCDA;
    }

    public Integer getTpSituacao() {
        return tpSituacao;
    }

    public void setTpSituacao(Integer tpSituacao) {
        this.tpSituacao = tpSituacao;
    }

    public String getDtEvento() {
        return dtEvento;
    }

    public void setDtEvento(String dtEvento) {
        this.dtEvento = dtEvento;
    }

    public String getDeObservacao() {
        return deObservacao;
    }

    public void setDeObservacao(String deObservacao) {
        this.deObservacao = deObservacao;
    }
}
