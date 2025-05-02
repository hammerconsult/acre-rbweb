/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Edi
 */
public class ConciliacaoBancariaItem {

    private String banco;
    private String agencia;
    private String numero;
    private String tituloContaBancaria;
    private String tipoContaBancaria;
    private BigDecimal saldoContabil;
    private String unidade;
    private String orgao;
    private String subConta;
    private List<ConciliacaoBancariaMovimentos> movimentos;

    public ConciliacaoBancariaItem() {
        movimentos = new ArrayList<>();
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

    public BigDecimal getSaldoContabil() {
        return saldoContabil;
    }

    public void setSaldoContabil(BigDecimal saldoContabil) {
        this.saldoContabil = saldoContabil;
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

    public List<ConciliacaoBancariaMovimentos> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<ConciliacaoBancariaMovimentos> movimentos) {
        this.movimentos = movimentos;
    }
}
