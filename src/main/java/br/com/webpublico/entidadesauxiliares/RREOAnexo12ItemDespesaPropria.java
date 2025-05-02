/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class RREOAnexo12ItemDespesaPropria {
    private String descricao;
    private BigDecimal dotacaoInicial;
    private BigDecimal dotacaoAtualizada;
    private BigDecimal despesaLiquidadaAteSemestre;
    private BigDecimal despesaLiquidadaPercentual;
    private Integer nivel;

    public RREOAnexo12ItemDespesaPropria() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getDespesaLiquidadaAteSemestre() {
        return despesaLiquidadaAteSemestre;
    }

    public void setDespesaLiquidadaAteSemestre(BigDecimal despesaLiquidadaAteSemestre) {
        this.despesaLiquidadaAteSemestre = despesaLiquidadaAteSemestre;
    }

    public BigDecimal getDespesaLiquidadaPercentual() {
        return despesaLiquidadaPercentual;
    }

    public void setDespesaLiquidadaPercentual(BigDecimal despesaLiquidadaPercentual) {
        this.despesaLiquidadaPercentual = despesaLiquidadaPercentual;
    }

    public BigDecimal getDotacaoAtualizada() {
        return dotacaoAtualizada;
    }

    public void setDotacaoAtualizada(BigDecimal dotacaoAtualizada) {
        this.dotacaoAtualizada = dotacaoAtualizada;
    }

    public BigDecimal getDotacaoInicial() {
        return dotacaoInicial;
    }

    public void setDotacaoInicial(BigDecimal dotacaoInicial) {
        this.dotacaoInicial = dotacaoInicial;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
