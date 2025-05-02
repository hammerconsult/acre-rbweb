/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.FonteDespesaORC;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class DotacaoProcessoCompraVO implements Serializable {

    private FonteDespesaORC fonteDespesaORC;
    private BigDecimal valorReservado;
    private BigDecimal valorEstornoReservado;

    private BigDecimal valorExecutadoReservaInicial;
    private BigDecimal valorExecutadoReservaExecucao;
    private BigDecimal valorEstornoExecutadoReservaInicial;
    private BigDecimal valorEstornoExecutadoReservaExecucao;

    public DotacaoProcessoCompraVO() {
        this.valorReservado = BigDecimal.ZERO;
        this.valorEstornoReservado = BigDecimal.ZERO;

        this.valorExecutadoReservaInicial = BigDecimal.ZERO;
        this.valorExecutadoReservaExecucao = BigDecimal.ZERO;
        this.valorEstornoExecutadoReservaInicial = BigDecimal.ZERO;
        this.valorEstornoExecutadoReservaExecucao = BigDecimal.ZERO;
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
        return valorExecutadoReservaExecucao.add(valorExecutadoReservaInicial);
    }

    public BigDecimal getValorEstornoExecutado() {
        return valorEstornoExecutadoReservaExecucao.add(valorEstornoExecutadoReservaInicial);
    }

    public BigDecimal getValorExecutadoReservaInicial() {
        return valorExecutadoReservaInicial;
    }

    public void setValorExecutadoReservaInicial(BigDecimal valorExecutadoReservaInicial) {
        this.valorExecutadoReservaInicial = valorExecutadoReservaInicial;
    }

    public BigDecimal getValorExecutadoReservaExecucao() {
        return valorExecutadoReservaExecucao;
    }

    public void setValorExecutadoReservaExecucao(BigDecimal valorExecutadoReservaExecucao) {
        this.valorExecutadoReservaExecucao = valorExecutadoReservaExecucao;
    }

    public BigDecimal getValorEstornoExecutadoReservaInicial() {
        return valorEstornoExecutadoReservaInicial;
    }

    public void setValorEstornoExecutadoReservaInicial(BigDecimal valorEstornoExecutadoReservaInicial) {
        this.valorEstornoExecutadoReservaInicial = valorEstornoExecutadoReservaInicial;
    }

    public BigDecimal getValorEstornoExecutadoReservaExecucao() {
        return valorEstornoExecutadoReservaExecucao;
    }

    public void setValorEstornoExecutadoReservaExecucao(BigDecimal valorEstornoExecutadoReservaExecucao) {
        this.valorEstornoExecutadoReservaExecucao = valorEstornoExecutadoReservaExecucao;
    }

    public BigDecimal getValorExecutadoLiquido(){
        return getValorExecutado().subtract(getValorEstornoExecutado());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DotacaoProcessoCompraVO that = (DotacaoProcessoCompraVO) object;
        return Objects.equals(fonteDespesaORC, that.fonteDespesaORC);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fonteDespesaORC);
    }

}
