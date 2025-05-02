package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoDescontoItemRequisicao;

import java.math.BigDecimal;
import java.util.Objects;

public class DescontoItemRequisicaoCompra {

    private TipoDescontoItemRequisicao tipoDesconto;
    private BigDecimal descontoUnitario;
    private BigDecimal descontoTotal;
    private Boolean selecionado;
    private String mascaraUnitario;
    private String mascaraTotal;

    public DescontoItemRequisicaoCompra(TipoDescontoItemRequisicao tipoDesconto, BigDecimal descontoUnitario, BigDecimal descontoTotal) {
        this.descontoUnitario = descontoUnitario;
        this.descontoTotal = descontoTotal;
        this.tipoDesconto = tipoDesconto;
        this.selecionado = false;
    }

    public String getMascaraUnitario() {
        return mascaraUnitario;
    }

    public void setMascaraUnitario(String mascaraUnitario) {
        this.mascaraUnitario = mascaraUnitario;
    }

    public String getMascaraTotal() {
        return mascaraTotal;
    }

    public void setMascaraTotal(String mascaraTotal) {
        this.mascaraTotal = mascaraTotal;
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

    public String montarMascara(BigDecimal valor) {
        String mascara = "#,##0.00";
        if (getTipoDesconto() != null && getTipoDesconto().isNaoArredondar()) {
            String valorStr = valor.toString();
            String valorDecimal = valorStr.substring(valorStr.indexOf(".") + 1);

            String qtdeDecimal = "";
            for (int x = 0; x < valorDecimal.length(); x++) {
                qtdeDecimal += "0";
            }
            mascara = "#,##0." + qtdeDecimal;
        }
        return mascara;
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
