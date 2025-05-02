package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 25/10/13
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class RREOAnexo08Item04 {
    private String descricao;
    private BigDecimal dotacaoInicial;
    private BigDecimal dotacaoAtualizada;
    private BigDecimal despesaEmpenhadaAteBimestre;
    private BigDecimal despesaEmpenhadaPercentual;
    private BigDecimal despesaLiquidadaAteBimestre;
    private BigDecimal despesaLiquidadaPercentual;
    private BigDecimal restoAPagar;
    private Integer nivel;

    public RREOAnexo08Item04() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getDotacaoInicial() {
        return dotacaoInicial;
    }

    public void setDotacaoInicial(BigDecimal dotacaoInicial) {
        this.dotacaoInicial = dotacaoInicial;
    }

    public BigDecimal getDotacaoAtualizada() {
        return dotacaoAtualizada;
    }

    public void setDotacaoAtualizada(BigDecimal dotacaoAtualizada) {
        this.dotacaoAtualizada = dotacaoAtualizada;
    }


    public BigDecimal getDespesaLiquidadaAteBimestre() {
        return despesaLiquidadaAteBimestre;
    }

    public void setDespesaLiquidadaAteBimestre(BigDecimal despesaLiquidadaAteBimestre) {
        this.despesaLiquidadaAteBimestre = despesaLiquidadaAteBimestre;
    }

    public BigDecimal getDespesaLiquidadaPercentual() {
        return despesaLiquidadaPercentual;
    }

    public void setDespesaLiquidadaPercentual(BigDecimal despesaLiquidadaPercentual) {
        this.despesaLiquidadaPercentual = despesaLiquidadaPercentual;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public BigDecimal getDespesaEmpenhadaAteBimestre() {
        return despesaEmpenhadaAteBimestre;
    }

    public void setDespesaEmpenhadaAteBimestre(BigDecimal despesaEmpenhadaAteBimestre) {
        this.despesaEmpenhadaAteBimestre = despesaEmpenhadaAteBimestre;
    }

    public BigDecimal getDespesaEmpenhadaPercentual() {
        return despesaEmpenhadaPercentual;
    }

    public void setDespesaEmpenhadaPercentual(BigDecimal despesaEmpenhadaPercentual) {
        this.despesaEmpenhadaPercentual = despesaEmpenhadaPercentual;
    }

    public BigDecimal getRestoAPagar() {
        return restoAPagar;
    }

    public void setRestoAPagar(BigDecimal restoAPagar) {
        this.restoAPagar = restoAPagar;
    }
}
