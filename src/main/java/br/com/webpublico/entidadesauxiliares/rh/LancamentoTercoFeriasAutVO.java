package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.PeriodoAquisitivoFL;
import br.com.webpublico.enums.rh.administracaopagamento.StatusReprocessamentoLancamentoTercoFeriasAutomatico;

public class LancamentoTercoFeriasAutVO {

    private Integer saldoPeriodoAquisitivo;
    private Integer mes;
    private Integer ano;
    private ContratoFP contratoFP;
    private PeriodoAquisitivoFL periodoAquisitivoFL;
    private StatusReprocessamentoLancamentoTercoFeriasAutomatico status;

    public LancamentoTercoFeriasAutVO() {
    }

    public LancamentoTercoFeriasAutVO(Integer saldoPeriodoAquisitivo, Integer mes, Integer ano, ContratoFP contratoFP, PeriodoAquisitivoFL periodoAquisitivoFL, StatusReprocessamentoLancamentoTercoFeriasAutomatico status) {
        this.saldoPeriodoAquisitivo = saldoPeriodoAquisitivo;
        this.mes = mes;
        this.ano = ano;
        this.contratoFP = contratoFP;
        this.periodoAquisitivoFL = periodoAquisitivoFL;
        this.status = status;
    }

    public Integer getSaldoPeriodoAquisitivo() {
        return saldoPeriodoAquisitivo;
    }

    public void setSaldoPeriodoAquisitivo(Integer saldoPeriodoAquisitivo) {
        this.saldoPeriodoAquisitivo = saldoPeriodoAquisitivo;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public PeriodoAquisitivoFL getPeriodoAquisitivoFL() {
        return periodoAquisitivoFL;
    }

    public void setPeriodoAquisitivoFL(PeriodoAquisitivoFL periodoAquisitivoFL) {
        this.periodoAquisitivoFL = periodoAquisitivoFL;
    }

    public StatusReprocessamentoLancamentoTercoFeriasAutomatico getStatus() {
        return status;
    }

    public void setStatus(StatusReprocessamentoLancamentoTercoFeriasAutomatico status) {
        this.status = status;
    }
}
