/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.enums.TipoCalculoFP;
import br.com.webpublico.enums.TipoEventoFP;

import java.math.BigDecimal;

/**
 * @author everton
 * upgrade Peixe
 */
public class EventoTotalItemFicha {
    private EventoFP evento;
    private TipoCalculoFP tipoCalculoFP;
    private TipoEventoFP tipoEventoFP;
    private BigDecimal total;
    private BigDecimal referencia;
    private BigDecimal valorBase;
    private BigDecimal valorIntegral;
    private Integer mes;
    private Integer ano;


    public EventoTotalItemFicha() {
       total = BigDecimal.ZERO;
       referencia = BigDecimal.ZERO;
       valorBase = BigDecimal.ZERO;
       valorIntegral = BigDecimal.ZERO;
    }

    public BigDecimal getValorBase() {
        return valorBase;
    }

    public void setValorBase(BigDecimal valorBase) {
        this.valorBase = valorBase;
    }

    public EventoTotalItemFicha(EventoFP evento, BigDecimal total) {
        this.evento = evento;
        this.total = total;
    }

    public EventoTotalItemFicha(EventoFP evento, BigDecimal total, BigDecimal referencia) {
        this.evento = evento;
        this.total = total;
        this.referencia = referencia;

    }

    public EventoTotalItemFicha(EventoFP evento, BigDecimal total, BigDecimal referencia, BigDecimal valorBase) {
        this.evento = evento;
        this.total = total;
        this.referencia = referencia;
        this.valorBase = valorBase;

    }

    public EventoTotalItemFicha(EventoFP evento, TipoEventoFP tipoEventoFP, BigDecimal total, BigDecimal referencia, BigDecimal valorBase) {
        this.evento = evento;
        this.tipoEventoFP = tipoEventoFP;
        this.total = total;
        this.referencia = referencia;
        this.valorBase = valorBase;

    }

    public EventoTotalItemFicha(EventoFP evento, TipoEventoFP tipoEventoFP, BigDecimal total, BigDecimal referencia, BigDecimal valorBase, BigDecimal valorIntegral) {
        this.evento = evento;
        this.tipoEventoFP = tipoEventoFP;
        this.total = total;
        this.referencia = referencia;
        this.valorBase = valorBase;
        this.valorIntegral = valorIntegral;

    }

    public EventoTotalItemFicha(EventoFP evento, TipoEventoFP tipoEventoFP, BigDecimal total) {
        this.evento = evento;
        this.tipoEventoFP = tipoEventoFP;
        this.total = total;
    }

    public BigDecimal getValorIntegral() {
        return valorIntegral;
    }

    public void setValorIntegral(BigDecimal valorIntegral) {
        this.valorIntegral = valorIntegral;
    }

    public TipoEventoFP getTipoEventoFP() {
        return tipoEventoFP;
    }

    public void setTipoEventoFP(TipoEventoFP tipoEventoFP) {
        this.tipoEventoFP = tipoEventoFP;
    }

    public EventoFP getEvento() {
        return evento;
    }

    public void setEvento(EventoFP evento) {
        this.evento = evento;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getReferencia() {
        return referencia;
    }

    public void setReferencia(BigDecimal referencia) {
        this.referencia = referencia;
    }

    public TipoCalculoFP getTipoCalculoFP() {
        return tipoCalculoFP;
    }

    public void setTipoCalculoFP(TipoCalculoFP tipoCalculoFP) {
        this.tipoCalculoFP = tipoCalculoFP;
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

    @Override
    public String toString() {
        return evento + ", total=" + total;
    }
}
