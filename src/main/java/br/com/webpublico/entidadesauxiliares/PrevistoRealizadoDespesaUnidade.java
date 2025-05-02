package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Conta;
import java.math.BigDecimal;

/**
 * Created by renat on 13/10/2015.
 */
public class PrevistoRealizadoDespesaUnidade {
    private Exercicio exercicio;
    private br.com.webpublico.entidades.Conta conta;
    private BigDecimal valorPrevisto;
    private BigDecimal totalDespesa;
    private BigDecimal saldo;

    public PrevistoRealizadoDespesaUnidade(Exercicio ex, br.com.webpublico.entidades.Conta c, BigDecimal previsto, BigDecimal realizado) {
        this.exercicio = ex;
        this.conta = c;
        this.valorPrevisto = previsto != null ? previsto : BigDecimal.ZERO;
        this.totalDespesa = realizado != null ? realizado : BigDecimal.ZERO;
        saldo = (valorPrevisto.subtract(totalDespesa));
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public BigDecimal getValorPrevisto() {
        return valorPrevisto;
    }

    public void setValorPrevisto(BigDecimal valorPrevisto) {
        this.valorPrevisto = valorPrevisto;
    }

    public BigDecimal getTotalDespesa() {
        return totalDespesa;
    }

    public void setTotalDespesa(BigDecimal totalDespesa) {
        this.totalDespesa = totalDespesa;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
