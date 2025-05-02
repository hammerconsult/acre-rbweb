package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.enums.TipoObjetoCompra;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
public class DotacaoAlteracaoContratualVO implements Serializable {

    private FonteDespesaORC fonteDespesaORC;
    private TipoObjetoCompra tipoObjetoCompra;
    private BigDecimal valorExecutado;
    private BigDecimal valorReservado;
    private BigDecimal valorEstornadoReservado;

    public DotacaoAlteracaoContratualVO() {
        valorExecutado = BigDecimal.ZERO;
        valorReservado = BigDecimal.ZERO;
        valorEstornadoReservado = BigDecimal.ZERO;
    }

    public BigDecimal getValorExecutado() {
        return valorExecutado;
    }

    public void setValorExecutado(BigDecimal valorExecutado) {
        this.valorExecutado = valorExecutado;
    }


    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public BigDecimal getValorReservado() {
        return valorReservado;
    }

    public void setValorReservado(BigDecimal valorReservado) {
        this.valorReservado = valorReservado;
    }

    public BigDecimal getValorEstornadoReservado() {
        return valorEstornadoReservado;
    }

    public void setValorEstornadoReservado(BigDecimal valorEstornadoReservado) {
        this.valorEstornadoReservado = valorEstornadoReservado;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public BigDecimal getSaldo() {
        return valorReservado.subtract(valorExecutado).subtract(valorEstornadoReservado);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DotacaoAlteracaoContratualVO that = (DotacaoAlteracaoContratualVO) o;
        return Objects.equals(fonteDespesaORC, that.fonteDespesaORC);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fonteDespesaORC);
    }
}
