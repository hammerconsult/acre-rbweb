package br.com.webpublico.entidadesauxiliares.contabil.relatoriosaldofinanceiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlos on 15/03/17.
 */
public class SaldoFinanceiroVO {
    private BigDecimal id;
    private String conta;
    private String bancoDescricao;
    private String nomeConta;
    private String numeroAgencia;
    private BigDecimal soma;
    private String unidade;
    private String orgao;
    private String unidadeGestora;
    private String codigo;
    private List<ContaFinanceiraDoSaldoFinanceiroVO> contaFinanceiraDoSaldoFinanceiroVOs;

    public SaldoFinanceiroVO() {
        contaFinanceiraDoSaldoFinanceiroVOs = new ArrayList<>();
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getBancoDescricao() {
        return bancoDescricao;
    }

    public void setBancoDescricao(String bancoDescricao) {
        this.bancoDescricao = bancoDescricao;
    }

    public String getNomeConta() {
        return nomeConta;
    }

    public void setNomeConta(String nomeConta) {
        this.nomeConta = nomeConta;
    }

    public String getNumeroAgencia() {
        return numeroAgencia;
    }

    public void setNumeroAgencia(String numeroAgencia) {
        this.numeroAgencia = numeroAgencia;
    }

    public BigDecimal getSoma() {
        return soma;
    }

    public void setSoma(BigDecimal soma) {
        this.soma = soma;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<ContaFinanceiraDoSaldoFinanceiroVO> getContaFinanceiraDoSaldoFinanceiroVOs() {
        return contaFinanceiraDoSaldoFinanceiroVOs;
    }

    public void setContaFinanceiraDoSaldoFinanceiroVOs(List<ContaFinanceiraDoSaldoFinanceiroVO> contaFinanceiraDoSaldoFinanceiroVOs) {
        this.contaFinanceiraDoSaldoFinanceiroVOs = contaFinanceiraDoSaldoFinanceiroVOs;
    }
}
