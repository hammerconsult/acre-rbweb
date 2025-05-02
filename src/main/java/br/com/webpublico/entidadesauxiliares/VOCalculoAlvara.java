package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CalculoAlvaraConstrucaoHabitese;

import java.math.BigDecimal;
import java.util.Date;

public class VOCalculoAlvara {

    private BigDecimal area;
    private Date vencimento;
    private BigDecimal ufm;
    private BigDecimal valor;

    public VOCalculoAlvara(CalculoAlvaraConstrucaoHabitese calculoAlvaraConstrucaoHabitese) {
        this.area = calculoAlvaraConstrucaoHabitese.getAreaTotal();
        this.vencimento = calculoAlvaraConstrucaoHabitese.getVencimento();
        this.ufm = calculoAlvaraConstrucaoHabitese.getValorUFM();
        this.valor = calculoAlvaraConstrucaoHabitese.getValorReal();
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public BigDecimal getUfm() {
        return ufm;
    }

    public void setUfm(BigDecimal ufm) {
        this.ufm = ufm;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
