/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author juggernaut
 */
public class LRFAnexo02ItemRelatorio {

    private String descricao;
    private BigDecimal dotacaoInicial;
    private BigDecimal dotacaoAtualizada;
    private BigDecimal despesaEmpenhadasNoBimestre;
    private BigDecimal despesaEmpenhadasAteOBimestre;
    private BigDecimal despesaLiquidadaNoBimestre;
    private BigDecimal despesaLiquidadaAteOBimestre;
    private BigDecimal despesaLiquidadaAteOBimestrePercentualTotal;
    private BigDecimal despesaLiquidadaAteOBimestrePercentual;
    private BigDecimal saldoALiquidar;
    private Integer agrupador;

    public LRFAnexo02ItemRelatorio() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getDespesaEmpenhadasAteOBimestre() {
        return despesaEmpenhadasAteOBimestre;
    }

    public void setDespesaEmpenhadasAteOBimestre(BigDecimal despesaEmpenhadasAteOBimestre) {
        this.despesaEmpenhadasAteOBimestre = despesaEmpenhadasAteOBimestre;
    }

    public BigDecimal getDespesaEmpenhadasNoBimestre() {
        return despesaEmpenhadasNoBimestre;
    }

    public void setDespesaEmpenhadasNoBimestre(BigDecimal despesaEmpenhadasNoBimestre) {
        this.despesaEmpenhadasNoBimestre = despesaEmpenhadasNoBimestre;
    }

    public BigDecimal getDespesaLiquidadaAteOBimestre() {
        return despesaLiquidadaAteOBimestre;
    }

    public void setDespesaLiquidadaAteOBimestre(BigDecimal despesaLiquidadaAteOBimestre) {
        this.despesaLiquidadaAteOBimestre = despesaLiquidadaAteOBimestre;
        if (!this.despesaLiquidadaAteOBimestre.equals(BigDecimal.ZERO)) {
            this.despesaLiquidadaAteOBimestrePercentual = this.dotacaoAtualizada.divide(this.despesaLiquidadaAteOBimestre, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        } else {
            this.despesaLiquidadaAteOBimestrePercentual = new BigDecimal(0);
        }
        this.saldoALiquidar = this.dotacaoAtualizada.subtract(this.despesaLiquidadaAteOBimestre);
    }

    public BigDecimal getDespesaLiquidadaNoBimestre() {
        return despesaLiquidadaNoBimestre;
    }

    public void setDespesaLiquidadaNoBimestre(BigDecimal despesaLiquidadaNoBimestre) {
        this.despesaLiquidadaNoBimestre = despesaLiquidadaNoBimestre;
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

    public BigDecimal getDespesaLiquidadaAteOBimestrePercentual() {
        return despesaLiquidadaAteOBimestrePercentual;
    }

    public void setDespesaLiquidadaAteOBimestrePercentual(BigDecimal despesaLiquidadaAteOBimestrePercentual) {
        this.despesaLiquidadaAteOBimestrePercentual = despesaLiquidadaAteOBimestrePercentual;
    }

    public BigDecimal getDespesaLiquidadaAteOBimestrePercentualTotal() {
        return despesaLiquidadaAteOBimestrePercentualTotal;
    }

    public void setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal despesaLiquidadaAteOBimestrePercentualTotal) {
        this.despesaLiquidadaAteOBimestrePercentualTotal = despesaLiquidadaAteOBimestrePercentualTotal;
    }

    public BigDecimal getSaldoALiquidar() {
        return saldoALiquidar;
    }

    public void setSaldoALiquidar(BigDecimal saldoALiquidar) {
        this.saldoALiquidar = saldoALiquidar;
    }

    public Integer getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(Integer agrupador) {
        this.agrupador = agrupador;
    }
}
