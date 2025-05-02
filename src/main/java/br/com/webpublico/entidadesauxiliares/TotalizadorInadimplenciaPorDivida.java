package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Fabio on 03/07/2017.
 */
public class TotalizadorInadimplenciaPorDivida implements Comparable<TotalizadorInadimplenciaPorDivida> {

    private String descricaoDivida;
    private BigDecimal valorTotal;

    public TotalizadorInadimplenciaPorDivida(String descricaoDivida, BigDecimal valorTotal) {
        this.descricaoDivida = descricaoDivida;
        this.valorTotal = valorTotal;
    }

    public BigDecimal somar(BigDecimal valor) {
        valorTotal = valorTotal.add(valor);
        return valorTotal;
    }

    public String getDescricaoDivida() {
        return descricaoDivida != null ? descricaoDivida : "";
    }

    public void setDescricaoDivida(String descricaoDivida) {
        this.descricaoDivida = descricaoDivida;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalizadorInadimplenciaPorDivida that = (TotalizadorInadimplenciaPorDivida) o;
        return !(descricaoDivida != null ? !descricaoDivida.equals(that.descricaoDivida) : that.descricaoDivida != null);
    }

    @Override
    public int hashCode() {
        return descricaoDivida != null ? descricaoDivida.hashCode() : 0;
    }

    @Override
    public int compareTo(TotalizadorInadimplenciaPorDivida o) {
        return this.getDescricaoDivida().compareTo(o.getDescricaoDivida());
    }
}
