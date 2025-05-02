package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 25/10/13
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
public class RREOAnexo08Item02 {
    private String descricao;
    private BigDecimal previsaoInicial;
    private BigDecimal previsaoAtualizada;
    private BigDecimal receitaRealizadaNoBimestre;
    private BigDecimal receitaRealizadaAteBimestre;
    private BigDecimal receitaRealizadaPercentual;
    private Integer nivel;

    public RREOAnexo08Item02() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPrevisaoInicial() {
        return previsaoInicial;
    }

    public void setPrevisaoInicial(BigDecimal previsaoInicial) {
        this.previsaoInicial = previsaoInicial;
    }

    public BigDecimal getPrevisaoAtualizada() {
        return previsaoAtualizada;
    }

    public void setPrevisaoAtualizada(BigDecimal previsaoAtualizada) {
        this.previsaoAtualizada = previsaoAtualizada;
    }

    public BigDecimal getReceitaRealizadaNoBimestre() {
        return receitaRealizadaNoBimestre;
    }

    public void setReceitaRealizadaNoBimestre(BigDecimal receitaRealizadaNoBimestre) {
        this.receitaRealizadaNoBimestre = receitaRealizadaNoBimestre;
    }

    public BigDecimal getReceitaRealizadaAteBimestre() {
        return receitaRealizadaAteBimestre;
    }

    public void setReceitaRealizadaAteBimestre(BigDecimal receitaRealizadaAteBimestre) {
        this.receitaRealizadaAteBimestre = receitaRealizadaAteBimestre;
    }

    public BigDecimal getReceitaRealizadaPercentual() {
        return receitaRealizadaPercentual;
    }

    public void setReceitaRealizadaPercentual(BigDecimal receitaRealizadaPercentual) {
        this.receitaRealizadaPercentual = receitaRealizadaPercentual;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
