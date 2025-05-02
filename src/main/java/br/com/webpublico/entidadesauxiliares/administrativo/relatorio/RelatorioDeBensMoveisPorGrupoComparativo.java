package br.com.webpublico.entidadesauxiliares.administrativo.relatorio;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by William on 06/03/2018.
 */
public class RelatorioDeBensMoveisPorGrupoComparativo implements Serializable {
    private String grupoPatrimonial;
    private BigDecimal quantidade;
    private BigDecimal valorContabil;
    private BigDecimal ajusteContabil;
    private BigDecimal diferencaContabil;
    private BigDecimal valorBem;
    private BigDecimal valorAjusteBem;
    private BigDecimal diferencaBem;
    private BigDecimal valorConciliacao;
    private BigDecimal ajusteConciliacao;
    private BigDecimal valorAtual;

    public String getGrupoPatrimonial() {
        return grupoPatrimonial;
    }

    public void setGrupoPatrimonial(String grupoPatrimonial) {
        this.grupoPatrimonial = grupoPatrimonial;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorContabil() {
        return valorContabil;
    }

    public void setValorContabil(BigDecimal valorContabil) {
        this.valorContabil = valorContabil;
    }

    public BigDecimal getAjusteContabil() {
        return ajusteContabil;
    }

    public void setAjusteContabil(BigDecimal ajusteContabil) {
        this.ajusteContabil = ajusteContabil;
    }

    public BigDecimal getDiferencaContabil() {
        return diferencaContabil;
    }

    public void setDiferencaContabil(BigDecimal diferencaContabil) {
        this.diferencaContabil = diferencaContabil;
    }

    public BigDecimal getValorBem() {
        return valorBem;
    }

    public void setValorBem(BigDecimal valorBem) {
        this.valorBem = valorBem;
    }

    public BigDecimal getValorAjusteBem() {
        return valorAjusteBem;
    }

    public void setValorAjusteBem(BigDecimal valorAjusteBem) {
        this.valorAjusteBem = valorAjusteBem;
    }

    public BigDecimal getDiferencaBem() {
        return diferencaBem;
    }

    public void setDiferencaBem(BigDecimal diferencaBem) {
        this.diferencaBem = diferencaBem;
    }

    public BigDecimal getValorConciliacao() {
        return valorConciliacao;
    }

    public void setValorConciliacao(BigDecimal valorConciliacao) {
        this.valorConciliacao = valorConciliacao;
    }

    public BigDecimal getAjusteConciliacao() {
        return ajusteConciliacao;
    }

    public void setAjusteConciliacao(BigDecimal ajusteConciliacao) {
        this.ajusteConciliacao = ajusteConciliacao;
    }

    public BigDecimal getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }
}
