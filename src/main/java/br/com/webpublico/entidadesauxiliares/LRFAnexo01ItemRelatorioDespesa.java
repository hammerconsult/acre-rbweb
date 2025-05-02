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
public class LRFAnexo01ItemRelatorioDespesa {

    private String descricao;
    private String nome;
    private BigDecimal dotacaoInicial;
    private BigDecimal creditosAdicionais;
    private BigDecimal dotacaoAtualizada;
    private BigDecimal despesasEmpenhadasNoBimestre;
    private BigDecimal despesasEmpenhadasAteBimestre;
    private BigDecimal despesasLiquidadasNoBimestre;
    private BigDecimal despesasLiquidadasAteBimestre;
    private BigDecimal despesasPagasAteBimestre;
    private BigDecimal saldoLiquidar;
    private BigDecimal saldo;
    private BigDecimal inscritosRestoPagar;
    private Integer nivel;
    private Integer numeroLinhaNoXLS;
    private Integer ordem;

    public LRFAnexo01ItemRelatorioDespesa() {
        dotacaoInicial = BigDecimal.ZERO;
        creditosAdicionais = BigDecimal.ZERO;
        dotacaoAtualizada = BigDecimal.ZERO;
        despesasEmpenhadasNoBimestre = BigDecimal.ZERO;
        despesasEmpenhadasAteBimestre = BigDecimal.ZERO;
        despesasLiquidadasNoBimestre = BigDecimal.ZERO;
        despesasLiquidadasAteBimestre = BigDecimal.ZERO;
        despesasPagasAteBimestre = BigDecimal.ZERO;
        saldoLiquidar = BigDecimal.ZERO;
        inscritosRestoPagar = BigDecimal.ZERO;
    }

    public BigDecimal getCreditosAdicionais() {
        return creditosAdicionais;
    }

    public void setCreditosAdicionais(BigDecimal creditosAdicionais) {
        this.creditosAdicionais = creditosAdicionais;
        this.dotacaoAtualizada = this.dotacaoInicial.add(this.creditosAdicionais);
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getDespesasEmpenhadasAteBimestre() {
        return despesasEmpenhadasAteBimestre;
    }

    public void setDespesasEmpenhadasAteBimestre(BigDecimal despesasEmpenhadasAteBimestre) {
        this.despesasEmpenhadasAteBimestre = despesasEmpenhadasAteBimestre;
    }

    public BigDecimal getDespesasEmpenhadasNoBimestre() {
        return despesasEmpenhadasNoBimestre;
    }

    public void setDespesasEmpenhadasNoBimestre(BigDecimal despesasEmpenhadasNoBimestre) {
        this.despesasEmpenhadasNoBimestre = despesasEmpenhadasNoBimestre;
    }

    public BigDecimal getDespesasLiquidadasAteBimestre() {
        return despesasLiquidadasAteBimestre;
    }

    public void setDespesasLiquidadasAteBimestre(BigDecimal despesasLiquidadasAteBimestre) {
        this.despesasLiquidadasAteBimestre = despesasLiquidadasAteBimestre;
        this.saldoLiquidar = dotacaoAtualizada.subtract(this.despesasLiquidadasAteBimestre);
    }

    public BigDecimal getDespesasLiquidadasNoBimestre() {
        return despesasLiquidadasNoBimestre;
    }

    public void setDespesasLiquidadasNoBimestre(BigDecimal despesasLiquidadasNoBimestre) {
        this.despesasLiquidadasNoBimestre = despesasLiquidadasNoBimestre;
    }

    public BigDecimal getDespesasPagasAteBimestre() {
        return despesasPagasAteBimestre;
    }

    public void setDespesasPagasAteBimestre(BigDecimal despesasPagasAteBimestre) {
        this.despesasPagasAteBimestre = despesasPagasAteBimestre;
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

    public BigDecimal getSaldoLiquidar() {
        return saldoLiquidar;
    }

    public void setSaldoLiquidar(BigDecimal saldoLiquidar) {
        this.saldoLiquidar = saldoLiquidar;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getInscritosRestoPagar() {
        return inscritosRestoPagar;
    }

    public void setInscritosRestoPagar(BigDecimal inscritosRestoPagar) {
        this.inscritosRestoPagar = inscritosRestoPagar;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Integer getNumeroLinhaNoXLS() {
        return numeroLinhaNoXLS;
    }

    public void setNumeroLinhaNoXLS(Integer numeroLinhaNoXLS) {
        this.numeroLinhaNoXLS = numeroLinhaNoXLS;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }
}
