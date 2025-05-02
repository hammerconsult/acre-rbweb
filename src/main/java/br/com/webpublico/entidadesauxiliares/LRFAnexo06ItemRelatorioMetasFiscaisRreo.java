/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class LRFAnexo06ItemRelatorioMetasFiscaisRreo {

    private String descricao;
    private BigDecimal valorCorrente;
    private BigDecimal saldoDotacaoAtualizada;
    private BigDecimal saldoDespesasLiquidadasNoBimestre;
    private BigDecimal saldoDespesasLiquidadasAteOBimestre;
    private BigDecimal saldoDespesasLiquidadasAteOBimestreExAnterior;
    private Integer nivel;

    public LRFAnexo06ItemRelatorioMetasFiscaisRreo() {
    }

    public BigDecimal getValorCorrente() {
        return valorCorrente;
    }

    public void setValorCorrente(BigDecimal valorCorrente) {
        this.valorCorrente = valorCorrente;
    }

    public BigDecimal getSaldoDespesasLiquidadasAteOBimestre() {
        return saldoDespesasLiquidadasAteOBimestre;
    }

    public void setSaldoDespesasLiquidadasAteOBimestre(BigDecimal saldoDespesasLiquidadasAteOBimestre) {
        this.saldoDespesasLiquidadasAteOBimestre = saldoDespesasLiquidadasAteOBimestre;
    }

    public BigDecimal getSaldoDespesasLiquidadasAteOBimestreExAnterior() {
        return saldoDespesasLiquidadasAteOBimestreExAnterior;
    }

    public void setSaldoDespesasLiquidadasAteOBimestreExAnterior(BigDecimal saldoDespesasLiquidadasAteOBimestreExAnterior) {
        this.saldoDespesasLiquidadasAteOBimestreExAnterior = saldoDespesasLiquidadasAteOBimestreExAnterior;
    }

    public BigDecimal getSaldoDespesasLiquidadasNoBimestre() {
        return saldoDespesasLiquidadasNoBimestre;
    }

    public void setSaldoDespesasLiquidadasNoBimestre(BigDecimal saldoDespesasLiquidadasNoBimestre) {
        this.saldoDespesasLiquidadasNoBimestre = saldoDespesasLiquidadasNoBimestre;
    }

    public BigDecimal getSaldoDotacaoAtualizada() {
        return saldoDotacaoAtualizada;
    }

    public void setSaldoDotacaoAtualizada(BigDecimal saldoDotacaoAtualizada) {
        this.saldoDotacaoAtualizada = saldoDotacaoAtualizada;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
