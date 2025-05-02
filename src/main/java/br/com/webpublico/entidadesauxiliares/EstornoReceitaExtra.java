package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Mateus on 29/01/2015.
 */
public class EstornoReceitaExtra {
    private String numero;
    private Date dataEstorno;
    private String statusPagamento;
    private BigDecimal valorEstorno;
    private String historico;
    private Date dataReceita;
    private String numeroReceita;
    private String situacaoReceitaExtra;
    private BigDecimal valorReceita;
    private BigDecimal saldo;
    private String pessoa;
    private String unidade;
    private String orgao;
    private String unidadeGestora;

    public EstornoReceitaExtra() {
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public String getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public BigDecimal getValorEstorno() {
        return valorEstorno;
    }

    public void setValorEstorno(BigDecimal valorEstorno) {
        this.valorEstorno = valorEstorno;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public Date getDataReceita() {
        return dataReceita;
    }

    public void setDataReceita(Date dataReceita) {
        this.dataReceita = dataReceita;
    }

    public String getNumeroReceita() {
        return numeroReceita;
    }

    public void setNumeroReceita(String numeroReceita) {
        this.numeroReceita = numeroReceita;
    }

    public BigDecimal getValorReceita() {
        return valorReceita;
    }

    public void setValorReceita(BigDecimal valorReceita) {
        this.valorReceita = valorReceita;
    }

    public String getSituacaoReceitaExtra() {
        return situacaoReceitaExtra;
    }

    public void setSituacaoReceitaExtra(String situacaoReceitaExtra) {
        this.situacaoReceitaExtra = situacaoReceitaExtra;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }
}
