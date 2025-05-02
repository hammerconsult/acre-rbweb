package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.DotacaoSolicitacaoMaterialItemFonte;
import br.com.webpublico.enums.TipoObjetoCompra;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class TrasnferenciaDotacaoAlteracaoContratualVO implements Serializable {

    private DotacaoSolicitacaoMaterialItemFonte dotacaoSolicitacaoMaterialItemFonte;
    private TipoObjetoCompra tipoObjetoCompra;
    private BigDecimal valorExecutado;
    private BigDecimal valorReservado;
    private BigDecimal valorEstornadoReservado;
    private BigDecimal valorATransferir;

    public TrasnferenciaDotacaoAlteracaoContratualVO() {
        valorATransferir = BigDecimal.ZERO;
    }

    public BigDecimal getValorExecutado() {
        return valorExecutado;
    }

    public void setValorExecutado(BigDecimal valorExecutado) {
        this.valorExecutado = valorExecutado;
    }


    public DotacaoSolicitacaoMaterialItemFonte getDotacaoSolicitacaoMaterialItemFonte() {
        return dotacaoSolicitacaoMaterialItemFonte;
    }

    public void setDotacaoSolicitacaoMaterialItemFonte(DotacaoSolicitacaoMaterialItemFonte dotacaoSolicitacaoMaterialItemFonte) {
        this.dotacaoSolicitacaoMaterialItemFonte = dotacaoSolicitacaoMaterialItemFonte;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public BigDecimal getValorATransferir() {
        return valorATransferir;
    }

    public void setValorATransferir(BigDecimal valorATransferir) {
        this.valorATransferir = valorATransferir;
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

    public BigDecimal getSaldo() {
        return valorReservado.subtract(valorExecutado).subtract(valorEstornadoReservado);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrasnferenciaDotacaoAlteracaoContratualVO that = (TrasnferenciaDotacaoAlteracaoContratualVO) o;
        return Objects.equals(dotacaoSolicitacaoMaterialItemFonte, that.dotacaoSolicitacaoMaterialItemFonte);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dotacaoSolicitacaoMaterialItemFonte);
    }
}
