/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class RREOAnexo14ItemDespesa {

    private String descricao;
    private BigDecimal dotacaoAtualizada;
    private BigDecimal despesaLiquidada;
    private BigDecimal despesaInscritaRestoPagarNaoProcessado;
    private BigDecimal saldoAExecutar;

    public RREOAnexo14ItemDespesa() {
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
}
