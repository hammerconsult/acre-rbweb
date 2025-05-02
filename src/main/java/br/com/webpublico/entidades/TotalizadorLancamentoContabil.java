/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author leonardo
 */
public class TotalizadorLancamentoContabil implements Comparable<TotalizadorLancamentoContabil>, Serializable {

    private BigDecimal aliquota;
    private Mes mes;
    private Integer ano;
    private BigDecimal valor;
    private Boolean tributado;

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public boolean isTributado() {
        return tributado;
    }

    public void setTributado(boolean tributado) {
        this.tributado = tributado;
    }

    @Override
    public int compareTo(TotalizadorLancamentoContabil o) {
        int retorno = this.mes.compareTo(o.getMes());

        if (retorno == 0) {
            retorno = this.ano.compareTo(o.getAno());
        }
        if (retorno == 0) {
            retorno = this.aliquota.compareTo(o.getAliquota());
        }

        if (retorno == 0) {
            retorno = this.tributado.compareTo(o.isTributado());
        }

        return retorno;
    }

    @Override
    public String toString() {
        return "Ano..: " + ano + " mes .>: " + mes + " aliquota> " + aliquota + " tributado > " + tributado;
    }
}
