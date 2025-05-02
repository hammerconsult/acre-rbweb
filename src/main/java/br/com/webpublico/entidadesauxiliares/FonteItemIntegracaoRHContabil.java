package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.DespesaORC;
import br.com.webpublico.entidades.FonteDeRecursos;

import java.math.BigDecimal;

public class FonteItemIntegracaoRHContabil {

    private DespesaORC despesaORC;
    private FonteDeRecursos fonteDeRecursos;
    private BigDecimal saldoDisponivel;
    private BigDecimal valor;


    public FonteItemIntegracaoRHContabil() {
        this.valor = BigDecimal.ZERO;
        this.saldoDisponivel = BigDecimal.ZERO;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public BigDecimal getSaldoDisponivel() {
        return saldoDisponivel;
    }

    public void setSaldoDisponivel(BigDecimal saldoDisponivel) {
        this.saldoDisponivel = saldoDisponivel;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean isSaldoDisponivel(){
        return this.getValor().compareTo(this.getSaldoDisponivel()) <= 0;
    }
}
