package br.com.webpublico.entidadesauxiliares;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 03/03/2015.
 */
public class ExtratoMovFinanceiraCabecalho {

    private String numeroBanco;
    private String numeroAgencia;
    private String numeroContaBancaria;
    private String descricaoContaBancaria;
    private String descricaoBanco;
    private String tipoContaBancaria;
    private String tipoContaFinanceira;
    private String numeroContaFinanceira;
    private String descricaoContaFinanceira;
    private String fonteDeRecursos;
    private String contaDeDestinacao;
    private String orgao;
    private String unidade;
    private String unidadeGestora;
    private List<ExtratoMovFinanceiraSaldo> saldos;
    private List<ExtratoMovFinanceiraMovimentos> movimentos;

    public ExtratoMovFinanceiraCabecalho() {
        saldos = new ArrayList<>();
        movimentos = new ArrayList<>();
    }

    public String getNumeroBanco() {
        return numeroBanco;
    }

    public void setNumeroBanco(String numeroBanco) {
        this.numeroBanco = numeroBanco;
    }

    public String getNumeroAgencia() {
        return numeroAgencia;
    }

    public void setNumeroAgencia(String numeroAgencia) {
        this.numeroAgencia = numeroAgencia;
    }

    public String getNumeroContaBancaria() {
        return numeroContaBancaria;
    }

    public void setNumeroContaBancaria(String numeroContaBancaria) {
        this.numeroContaBancaria = numeroContaBancaria;
    }

    public String getDescricaoContaBancaria() {
        return descricaoContaBancaria;
    }

    public void setDescricaoContaBancaria(String descricaoContaBancaria) {
        this.descricaoContaBancaria = descricaoContaBancaria;
    }

    public String getTipoContaBancaria() {
        return tipoContaBancaria;
    }

    public void setTipoContaBancaria(String tipoContaBancaria) {
        this.tipoContaBancaria = tipoContaBancaria;
    }

    public String getTipoContaFinanceira() {
        return tipoContaFinanceira;
    }

    public void setTipoContaFinanceira(String tipoContaFinanceira) {
        this.tipoContaFinanceira = tipoContaFinanceira;
    }

    public String getNumeroContaFinanceira() {
        return numeroContaFinanceira;
    }

    public void setNumeroContaFinanceira(String numeroContaFinanceira) {
        this.numeroContaFinanceira = numeroContaFinanceira;
    }

    public String getDescricaoContaFinanceira() {
        return descricaoContaFinanceira;
    }

    public void setDescricaoContaFinanceira(String descricaoContaFinanceira) {
        this.descricaoContaFinanceira = descricaoContaFinanceira;
    }

    public String getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(String fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public String getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(String contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public List<ExtratoMovFinanceiraSaldo> getSaldos() {
        return saldos;
    }

    public void setSaldos(List<ExtratoMovFinanceiraSaldo> saldos) {
        this.saldos = saldos;
    }

    public List<ExtratoMovFinanceiraMovimentos> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<ExtratoMovFinanceiraMovimentos> movimentos) {
        this.movimentos = movimentos;
    }

    public String getDescricaoBanco() {
        return descricaoBanco;
    }

    public void setDescricaoBanco(String descricaoBanco) {
        this.descricaoBanco = descricaoBanco;
    }
}
