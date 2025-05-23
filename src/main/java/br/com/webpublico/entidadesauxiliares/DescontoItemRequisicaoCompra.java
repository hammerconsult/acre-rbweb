package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoDescontoItemRequisicao;

import java.math.BigDecimal;
import java.util.Objects;

public class DescontoItemRequisicaoCompra {

    private TipoDescontoItemRequisicao tipoDesconto;
    private BigDecimal descontoUnitario;
    private BigDecimal descontoTotal;
    private Boolean selecionado;

    public DescontoItemRequisicaoCompra(TipoDescontoItemRequisicao tipoDesconto, BigDecimal descontoUnitario, BigDecimal descontoTotal) {
        this.descontoUnitario = descontoUnitario;
        this.descontoTotal = descontoTotal;
        this.tipoDesconto = tipoDesconto;
        this.selecionado = false;
    }

    public BigDecimal getDescontoTotal() {
        return descontoTotal;
    }

    public void setDescontoTotal(BigDecimal descontoTotal) {
        this.descontoTotal = descontoTotal;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public TipoDescontoItemRequisicao getTipoDesconto() {
        return tipoDesconto;
    }

    public void setTipoDesconto(TipoDescontoItemRequisicao tipoDesconto) {
        this.tipoDesconto = tipoDesconto;
    }

    public BigDecimal getDescontoUnitario() {
        return descontoUnitario;
    }

    public void setDescontoUnitario(BigDecimal descontoUnitario) {
        this.descontoUnitario = descontoUnitario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DescontoItemRequisicaoCompra that = (DescontoItemRequisicaoCompra) o;
        return tipoDesconto == that.tipoDesconto;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoDesconto);
    }
}
