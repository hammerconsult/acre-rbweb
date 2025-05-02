package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 17/05/2017.
 */
public class ApuracaoSuperavitFinanceiro {
    private String codigoUnidade;
    private String descricaoUnidade;
    private String codigoContaFinanceira;
    private String descricaoContaFinanceira;
    private String codigoFonte;
    private String descricaoFonte;
    private Date dataMovimento;
    private String historico;
    private BigDecimal valor;
    private List<ApuracaoSuperavitFinanceiro> disponibilidadesDeCaixaBruta;
    private List<ApuracaoSuperavitFinanceiro> restosAPagarProcessados;
    private List<ApuracaoSuperavitFinanceiro> restosAPagarNaoProcessados;
    private List<ApuracaoSuperavitFinanceiro> depositos;
    private List<ApuracaoSuperavitFinanceiro> contasExtras;

    public ApuracaoSuperavitFinanceiro() {
        disponibilidadesDeCaixaBruta = Lists.newArrayList();
        restosAPagarProcessados = Lists.newArrayList();
        restosAPagarNaoProcessados = Lists.newArrayList();
        depositos = Lists.newArrayList();
        contasExtras = Lists.newArrayList();
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

    public String getCodigoContaFinanceira() {
        return codigoContaFinanceira;
    }

    public void setCodigoContaFinanceira(String codigoContaFinanceira) {
        this.codigoContaFinanceira = codigoContaFinanceira;
    }

    public String getDescricaoContaFinanceira() {
        return descricaoContaFinanceira;
    }

    public void setDescricaoContaFinanceira(String descricaoContaFinanceira) {
        this.descricaoContaFinanceira = descricaoContaFinanceira;
    }

    public String getCodigoFonte() {
        return codigoFonte;
    }

    public void setCodigoFonte(String codigoFonte) {
        this.codigoFonte = codigoFonte;
    }

    public String getDescricaoFonte() {
        return descricaoFonte;
    }

    public void setDescricaoFonte(String descricaoFonte) {
        this.descricaoFonte = descricaoFonte;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<ApuracaoSuperavitFinanceiro> getDisponibilidadesDeCaixaBruta() {
        return disponibilidadesDeCaixaBruta;
    }

    public void setDisponibilidadesDeCaixaBruta(List<ApuracaoSuperavitFinanceiro> disponibilidadesDeCaixaBruta) {
        this.disponibilidadesDeCaixaBruta = disponibilidadesDeCaixaBruta;
    }

    public List<ApuracaoSuperavitFinanceiro> getRestosAPagarProcessados() {
        return restosAPagarProcessados;
    }

    public void setRestosAPagarProcessados(List<ApuracaoSuperavitFinanceiro> restosAPagarProcessados) {
        this.restosAPagarProcessados = restosAPagarProcessados;
    }

    public List<ApuracaoSuperavitFinanceiro> getRestosAPagarNaoProcessados() {
        return restosAPagarNaoProcessados;
    }

    public void setRestosAPagarNaoProcessados(List<ApuracaoSuperavitFinanceiro> restosAPagarNaoProcessados) {
        this.restosAPagarNaoProcessados = restosAPagarNaoProcessados;
    }

    public List<ApuracaoSuperavitFinanceiro> getDepositos() {
        return depositos;
    }

    public void setDepositos(List<ApuracaoSuperavitFinanceiro> depositos) {
        this.depositos = depositos;
    }

    public List<ApuracaoSuperavitFinanceiro> getContasExtras() {
        return contasExtras;
    }

    public void setContasExtras(List<ApuracaoSuperavitFinanceiro> contasExtras) {
        this.contasExtras = contasExtras;
    }
}
