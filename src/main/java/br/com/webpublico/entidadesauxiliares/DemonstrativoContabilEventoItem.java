package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 13/06/14
 * Time: 07:58
 * To change this template use File | Settings | File Templates.
 */
public class DemonstrativoContabilEventoItem {

    private BigDecimal valorNormal;
    private BigDecimal valorEstorno;
    private BigDecimal valorTotal;
    private BigDecimal count;
    private String eventoTipo;
    private String eventoCodigo;
    private String eventoDescricao;
    private String eventoTce;

    public DemonstrativoContabilEventoItem() {
    }

    public BigDecimal getValorNormal() {
        return valorNormal;
    }

    public void setValorNormal(BigDecimal valorNormal) {
        this.valorNormal = valorNormal;
    }

    public BigDecimal getValorEstorno() {
        return valorEstorno;
    }

    public void setValorEstorno(BigDecimal valorEstorno) {
        this.valorEstorno = valorEstorno;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public String getEventoTipo() {
        return eventoTipo;
    }

    public void setEventoTipo(String eventoTipo) {
        this.eventoTipo = eventoTipo;
    }

    public String getEventoCodigo() {
        return eventoCodigo;
    }

    public void setEventoCodigo(String eventoCodigo) {
        this.eventoCodigo = eventoCodigo;
    }

    public String getEventoDescricao() {
        return eventoDescricao;
    }

    public void setEventoDescricao(String eventoDescricao) {
        this.eventoDescricao = eventoDescricao;
    }

    public String getEventoTce() {
        return eventoTce;
    }

    public void setEventoTce(String eventoTce) {
        this.eventoTce = eventoTce;
    }
}
