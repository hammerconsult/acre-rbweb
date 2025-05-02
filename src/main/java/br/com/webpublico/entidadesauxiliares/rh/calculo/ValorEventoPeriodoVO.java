package br.com.webpublico.entidadesauxiliares.rh.calculo;
import br.com.webpublico.entidadesauxiliares.rh.FichaFinanceiraFPCamposVO;

import java.math.BigDecimal;

public class ValorEventoPeriodoVO extends FichaFinanceiraFPCamposVO {

    private BigDecimal valorReferencia;
    private Integer anoItem;
    private Integer mesItem;
    private String codigoEvento;
    private String tipoEvento;
    private String tipoEventoFicha;
    private String tipoFolhaDePagamento;

    public ValorEventoPeriodoVO() {
    }

    public BigDecimal getValorReferencia() {
        return valorReferencia;
    }

    public void setValorReferencia(BigDecimal valorReferencia) {
        this.valorReferencia = valorReferencia;
    }

    public Integer getAnoItem() {
        return anoItem;
    }

    public void setAnoItem(Integer anoItem) {
        this.anoItem = anoItem;
    }

    public Integer getMesItem() {
        return mesItem;
    }

    public void setMesItem(Integer mesItem) {
        this.mesItem = mesItem;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getTipoEventoFicha() {
        return tipoEventoFicha;
    }

    public void setTipoEventoFicha(String tipoEventoFicha) {
        this.tipoEventoFicha = tipoEventoFicha;
    }

    public String getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(String tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }
}
