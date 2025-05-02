package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.PeriodoAquisitivoFL;
import br.com.webpublico.entidades.rh.portal.RHProgramacaoFeriasPortal;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by peixe on 12/08/2015.
 */
public class WSPeriodoAquisitivo implements Serializable {

    private Long id;
    private WSVinculoFP wsVinculoFP;
    private Date inicioVigencia;
    private Date finalVigencia;
    private TipoPeriodoAquisitivo tipoPeriodoAquisitivo;
    private List<WSConcessaoFerias> wsConcessaoFerias;
    private List<WSProgramacaoFerias> wsProgramacaoFerias;
    private RHProgramacaoFeriasPortal programacaoFeriasPortalortal;
    private Integer saldo;

    public WSPeriodoAquisitivo() {
        wsConcessaoFerias = Lists.newLinkedList();
        wsProgramacaoFerias = Lists.newLinkedList();
    }

    public WSPeriodoAquisitivo(Long id, Date inicioVigencia, Date finalVigencia, TipoPeriodoAquisitivo tipoPeriodoAquisitivo, Integer saldo) {
        this.id = id;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.tipoPeriodoAquisitivo = tipoPeriodoAquisitivo;
        this.saldo = saldo;
    }

    public static List<WSPeriodoAquisitivo> convertPeriodoAquisitivoFLTOWsPeriodoAquisitivoList(List<PeriodoAquisitivoFL> periodosAquisitivo) {
        List<WSPeriodoAquisitivo> wsVinculoFPList = new ArrayList<>();
        for (PeriodoAquisitivoFL periodo : periodosAquisitivo) {
            WSPeriodoAquisitivo ws = new WSPeriodoAquisitivo(periodo.getId(),periodo.getInicioVigencia(), periodo.getFinalVigencia(), periodo.getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo(), periodo.getSaldo());
            wsVinculoFPList.add(ws);
        }
        return wsVinculoFPList;
    }

    public static WSPeriodoAquisitivo convertPeriodoAquisitivoFLTOWsPeriodoAquisitivo(PeriodoAquisitivoFL periodo) {
        WSPeriodoAquisitivo ws = new WSPeriodoAquisitivo(periodo.getId(),periodo.getInicioVigencia(), periodo.getFinalVigencia(), periodo.getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo(), periodo.getSaldo());
        return ws;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WSVinculoFP getWsVinculoFP() {
        return wsVinculoFP;
    }

    public void setWsVinculoFP(WSVinculoFP wsVinculoFP) {
        this.wsVinculoFP = wsVinculoFP;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public List<WSConcessaoFerias> getWsConcessaoFerias() {
        return wsConcessaoFerias;
    }

    public WSPeriodoAquisitivo(TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        this.tipoPeriodoAquisitivo = tipoPeriodoAquisitivo;
    }

    public void setWsConcessaoFerias(List<WSConcessaoFerias> wsConcessaoFerias) {
        this.wsConcessaoFerias = wsConcessaoFerias;
    }

    public List<WSProgramacaoFerias> getWsProgramacaoFerias() {
        return wsProgramacaoFerias;
    }

    public void setWsProgramacaoFerias(List<WSProgramacaoFerias> wsProgramacaoFerias) {
        this.wsProgramacaoFerias = wsProgramacaoFerias;
    }

    public TipoPeriodoAquisitivo getTipoPeriodoAquisitivo() {
        return tipoPeriodoAquisitivo;
    }

    public void setTipoPeriodoAquisitivo(TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        this.tipoPeriodoAquisitivo = tipoPeriodoAquisitivo;
    }

    public RHProgramacaoFeriasPortal getProgramacaoFeriasPortalortal() {
        return programacaoFeriasPortalortal;
    }

    public void setProgramacaoFeriasPortalortal(RHProgramacaoFeriasPortal programacaoFeriasPortalortal) {
        this.programacaoFeriasPortalortal = programacaoFeriasPortalortal;
    }

    public Integer getSaldo() {
        return saldo;
    }

    public void setSaldo(Integer saldo) {
        this.saldo = saldo;
    }
}
