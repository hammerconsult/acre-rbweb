/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.FonteDespesaORC;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class ReservaDotacaoProcessoCompraVO implements Serializable {

    private FonteDespesaORC fonteDespesaORC;
    private BigDecimal valorReservado;
    private BigDecimal valorEstornoReservado;
    private BigDecimal valorExecutado;
    private BigDecimal valorEstornoExecutado;

    public ReservaDotacaoProcessoCompraVO() {
        this.valorReservado = BigDecimal.ZERO;
        this.valorEstornoReservado = BigDecimal.ZERO;
        this.valorExecutado = BigDecimal.ZERO;
        this.valorEstornoExecutado = BigDecimal.ZERO;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public BigDecimal getValorReservado() {
        return valorReservado;
    }

    public void setValorReservado(BigDecimal valorReservado) {
        this.valorReservado = valorReservado;
    }

    public BigDecimal getValorEstornoReservado() {
        return valorEstornoReservado;
    }

    public void setValorEstornoReservado(BigDecimal valorEstornoReservado) {
        this.valorEstornoReservado = valorEstornoReservado;
    }

    public BigDecimal getValorExecutado() {
        return valorExecutado;
    }

    public void setValorExecutado(BigDecimal valorExecutado) {
        this.valorExecutado = valorExecutado;
    }

    public BigDecimal getValorEstornoExecutado() {
        return valorEstornoExecutado;
    }

    public void setValorEstornoExecutado(BigDecimal valorEstornoExecutado) {
        this.valorEstornoExecutado = valorEstornoExecutado;
    }

    public BigDecimal getValorExecutadoLiquido(){
        return getValorExecutado().subtract(getValorEstornoExecutado());
    }

    public BigDecimal getValorReservadoLiquido(){
        return getValorReservado().subtract(getValorEstornoReservado());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ReservaDotacaoProcessoCompraVO that = (ReservaDotacaoProcessoCompraVO) object;
        return Objects.equals(fonteDespesaORC, that.fonteDespesaORC);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fonteDespesaORC);
    }
}
