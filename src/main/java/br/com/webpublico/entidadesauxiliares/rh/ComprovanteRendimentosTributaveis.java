package br.com.webpublico.entidadesauxiliares.rh;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by zaca on 23/02/17.
 */
public class ComprovanteRendimentosTributaveis implements Serializable {
    private String mes;
    private BigDecimal valor;


    public ComprovanteRendimentosTributaveis(String mes, BigDecimal valor) {
        this.mes = mes;
        this.valor = valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComprovanteRendimentosTributaveis that = (ComprovanteRendimentosTributaveis) o;

        if (mes != null ? !mes.equals(that.mes) : that.mes != null) return false;
        return valor != null ? valor.equals(that.valor) : that.valor == null;

    }

    @Override
    public int hashCode() {
        int result = mes != null ? mes.hashCode() : 0;
        result = 31 * result + (valor != null ? valor.hashCode() : 0);
        return result;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
