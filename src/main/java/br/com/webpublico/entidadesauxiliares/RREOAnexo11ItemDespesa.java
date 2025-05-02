/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class RREOAnexo11ItemDespesa {

    private String descricao;
    private BigDecimal dotacaoAtualizada;
    private BigDecimal despesaEmpenhada;
    private BigDecimal despesaLiquidada;
    private BigDecimal despesaInscritaRestoPagarNaoProcessado;
    private BigDecimal saldoAExecutar;
    private Integer nivel;

    public RREOAnexo11ItemDespesa() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getDespesaInscritaRestoPagarNaoProcessado() {
        return despesaInscritaRestoPagarNaoProcessado;
    }

    public void setDespesaInscritaRestoPagarNaoProcessado(BigDecimal despesaInscritaRestoPagarNaoProcessado) {
        this.despesaInscritaRestoPagarNaoProcessado = despesaInscritaRestoPagarNaoProcessado;
    }

    public BigDecimal getDespesaLiquidada() {
        return despesaLiquidada;
    }

    public void setDespesaLiquidada(BigDecimal despesaLiquidada) {
        this.despesaLiquidada = despesaLiquidada;
    }

    public BigDecimal getDotacaoAtualizada() {
        return dotacaoAtualizada;
    }

    public void setDotacaoAtualizada(BigDecimal dotacaoAtualizada) {
        this.dotacaoAtualizada = dotacaoAtualizada;
    }

    public BigDecimal getSaldoAExecutar() {
        return saldoAExecutar;
    }

    public void setSaldoAExecutar(BigDecimal saldoAExecutar) {
        this.saldoAExecutar = saldoAExecutar;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public BigDecimal getDespesaEmpenhada() {
        return despesaEmpenhada;
    }

    public void setDespesaEmpenhada(BigDecimal despesaEmpenhada) {
        this.despesaEmpenhada = despesaEmpenhada;
    }
}
