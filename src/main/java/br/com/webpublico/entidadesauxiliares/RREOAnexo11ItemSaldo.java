package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 04/02/14
 * Time: 14:12
 * To change this template use File | Settings | File Templates.
 */
public class RREOAnexo11ItemSaldo {
    private String descricao;
    private BigDecimal saldoExercicioAnterior;
    private BigDecimal saldoExercicio;
    private BigDecimal saldoAtual;
    private Integer nivel;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getSaldoExercicioAnterior() {
        return saldoExercicioAnterior;
    }

    public void setSaldoExercicioAnterior(BigDecimal saldoExercicioAnterior) {
        this.saldoExercicioAnterior = saldoExercicioAnterior;
    }

    public BigDecimal getSaldoExercicio() {
        return saldoExercicio;
    }

    public void setSaldoExercicio(BigDecimal saldoExercicio) {
        this.saldoExercicio = saldoExercicio;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
