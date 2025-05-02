package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Mateus on 01/04/2015.
 */
public class LiquidacaoPagamentoItem {
    private Date dataMovimento;
    private String numero;
    private String codigoOrgao;
    private String descricaoOrgao;
    private String codigoUnidade;
    private String descricaoUnidade;
    private String descricaoUnidadeGestora;
    private String evento;
    private Integer tipo;
    private String conta;
    private String classeCredor;
    private String fonte;
    private BigDecimal valorLiquidacao;
    private BigDecimal valorPagamento;
    private BigDecimal saldo;

    public LiquidacaoPagamentoItem() {

    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getDescricaoOrgao() {
        return descricaoOrgao;
    }

    public void setDescricaoOrgao(String descricaoOrgao) {
        this.descricaoOrgao = descricaoOrgao;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public String getDescricaoUnidadeGestora() {
        return descricaoUnidadeGestora;
    }

    public void setDescricaoUnidadeGestora(String descricaoUnidadeGestora) {
        this.descricaoUnidadeGestora = descricaoUnidadeGestora;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(String classeCredor) {
        this.classeCredor = classeCredor;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public BigDecimal getValorLiquidacao() {
        return valorLiquidacao;
    }

    public void setValorLiquidacao(BigDecimal valorLiquidacao) {
        this.valorLiquidacao = valorLiquidacao;
    }

    public BigDecimal getValorPagamento() {
        return valorPagamento;
    }

    public void setValorPagamento(BigDecimal valorPagamento) {
        this.valorPagamento = valorPagamento;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
