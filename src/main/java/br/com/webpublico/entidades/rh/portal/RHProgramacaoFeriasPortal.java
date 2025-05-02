package br.com.webpublico.entidades.rh.portal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RHProgramacaoFeriasPortal implements Serializable{

    private Long id;
    private Date dataInicio;
    private Date dataFim;
    private Boolean abonoPecunia;
    private Integer diasAbono;
    private Long periodoAquisitivoFLId;

    public RHProgramacaoFeriasPortal() {
        abonoPecunia = false;
    }

    public RHProgramacaoFeriasPortal(ProgramacaoFeriasPortal portal) {
        this.id = portal.getId();
        this.dataInicio = portal.getDataInicio();
        this.dataFim = portal.getDataFim();
        this.abonoPecunia = portal.getAbonoPecunia();
        this.diasAbono = portal.getDiasAbono();
        this.periodoAquisitivoFLId = portal.getPeriodoAquisitivoFL().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Boolean getAbonoPecunia() {
        return abonoPecunia;
    }

    public void setAbonoPecunia(Boolean abonoPecunia) {
        this.abonoPecunia = abonoPecunia;
    }

    public Integer getDiasAbono() {
        return diasAbono;
    }

    public void setDiasAbono(Integer diasAbono) {
        this.diasAbono = diasAbono;
    }

    public Long getPeriodoAquisitivoFLId() {
        return periodoAquisitivoFLId;
    }

    public void setPeriodoAquisitivoFLId(Long periodoAquisitivoFLId) {
        this.periodoAquisitivoFLId = periodoAquisitivoFLId;
    }

    @Override
    public String toString() {
        return "ProgramacaoFeriasPortal{" +
            "id=" + id +
            ", dataInicio=" + dataInicio +
            ", dataFim=" + dataFim +
            ", abonoPecunia=" + abonoPecunia +
            ", diasAbono=" + diasAbono +
            ", periodoAquisitivoFLId=" + periodoAquisitivoFLId +
            '}';
    }
}
