/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 *
 * @author wiplash
 */
public class RelatorioAnexoDezesseisItem {

    private String descricao;
    private BigDecimal saldoExercicioAnterior;
    private BigDecimal inscricao;
    private BigDecimal incorporacao;
    private BigDecimal amortizacao;
    private BigDecimal desincorporacao;
    private BigDecimal saldoExercicioSeguinte;
    private Integer nivel;

    public RelatorioAnexoDezesseisItem() {
    }

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

    public BigDecimal getInscricao() {
        return inscricao;
    }

    public void setInscricao(BigDecimal inscricao) {
        this.inscricao = inscricao;
    }

    public BigDecimal getIncorporacao() {
        return incorporacao;
    }

    public void setIncorporacao(BigDecimal incorporacao) {
        this.incorporacao = incorporacao;
    }

    public BigDecimal getAmortizacao() {
        return amortizacao;
    }

    public void setAmortizacao(BigDecimal amortizacao) {
        this.amortizacao = amortizacao;
    }

    public BigDecimal getDesincorporacao() {
        return desincorporacao;
    }

    public void setDesincorporacao(BigDecimal desincorporacao) {
        this.desincorporacao = desincorporacao;
    }

    public BigDecimal getSaldoExercicioSeguinte() {
        return saldoExercicioSeguinte;
    }

    public void setSaldoExercicioSeguinte(BigDecimal saldoExercicioSeguinte) {
        this.saldoExercicioSeguinte = saldoExercicioSeguinte;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
