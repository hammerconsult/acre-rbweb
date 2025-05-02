/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Edi
 */
public class RelatorioConciliacaoBancariaItem {

    private String banco;
    private String agencia;
    private String numero;
    private String numeroMovimento;
    private String tituloContaBancaria;
    private String tipoContaBancaria;
    private String historico;
    private String dataOperacao;
    private String mesMovimento;
    private String tipoMovimento;
    private String tipoOperacao;
    private BigDecimal idContaBancaria;
    private BigDecimal saldoContabil;
    private BigDecimal valor;
    private BigDecimal credito;
    private BigDecimal debito;
    private BigDecimal idSubConta;
    private BigDecimal codigoOperacao;
    private String unidade;
    private String orgao;
    private String subConta;
    private List<RelatorioConciliacaoBancariaItem> itens;

    public RelatorioConciliacaoBancariaItem() {
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTituloContaBancaria() {
        return tituloContaBancaria;
    }

    public void setTituloContaBancaria(String tituloContaBancaria) {
        this.tituloContaBancaria = tituloContaBancaria;
    }

    public String getTipoContaBancaria() {
        return tipoContaBancaria;
    }

    public void setTipoContaBancaria(String tipoContaBancaria) {
        this.tipoContaBancaria = tipoContaBancaria;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(String dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public String getMesMovimento() {
        return mesMovimento;
    }

    public void setMesMovimento(String mesMovimento) {
        this.mesMovimento = mesMovimento;
    }

    public String getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(String tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public String getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(String tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public BigDecimal getIdContaBancaria() {
        return idContaBancaria;
    }

    public void setIdContaBancaria(BigDecimal idContaBancaria) {
        this.idContaBancaria = idContaBancaria;
    }

    public BigDecimal getSaldoContabil() {
        return saldoContabil;
    }

    public void setSaldoContabil(BigDecimal saldoContabil) {
        this.saldoContabil = saldoContabil;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getCredito() {
        return credito;
    }

    public void setCredito(BigDecimal credito) {
        this.credito = credito;
    }

    public BigDecimal getDebito() {
        return debito;
    }

    public void setDebito(BigDecimal debito) {
        this.debito = debito;
    }

    public BigDecimal getIdSubConta() {
        return idSubConta;
    }

    public void setIdSubConta(BigDecimal idSubConta) {
        this.idSubConta = idSubConta;
    }

    public BigDecimal getCodigoOperacao() {
        return codigoOperacao;
    }

    public void setCodigoOperacao(BigDecimal codigoOperacao) {
        this.codigoOperacao = codigoOperacao;
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

    public String getSubConta() {
        return subConta;
    }

    public void setSubConta(String subConta) {
        this.subConta = subConta;
    }

    public List<RelatorioConciliacaoBancariaItem> getItens() {
        return itens;
    }

    public void setItens(List<RelatorioConciliacaoBancariaItem> itens) {
        this.itens = itens;
    }

    public String getNumeroMovimento() {
        return numeroMovimento;
    }

    public void setNumeroMovimento(String numeroMovimento) {
        this.numeroMovimento = numeroMovimento;
    }
}
