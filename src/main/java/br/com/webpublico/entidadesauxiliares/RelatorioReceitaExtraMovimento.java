package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Mateus on 13/08/2015.
 */
public class RelatorioReceitaExtraMovimento {
    private String numero;
    private Date dataReceita;
    private Date dataConciliacao;
    private String tipoLancamento;
    private String unidade;
    private String orgao;
    private String unidadeGestora;
    private String contaBancariaEntidade;
    private String contaFinanceira;
    private String contaExtraorcamentaria;
    private String fonteRecurso;
    private String evento;
    private String pessoa;
    private String classe;
    private String historico;
    private BigDecimal valor;
    private BigDecimal saldo;
    private String situacao;
    private String transportada;

    public RelatorioReceitaExtraMovimento() {
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataReceita() {
        return dataReceita;
    }

    public void setDataReceita(Date dataReceita) {
        this.dataReceita = dataReceita;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
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

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public String getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(String tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public String getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(String contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public String getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(String contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public String getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(String contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public String getFonteRecurso() {
        return fonteRecurso;
    }

    public void setFonteRecurso(String fonteRecurso) {
        this.fonteRecurso = fonteRecurso;
    }

    public String getTransportada() {
        return transportada;
    }

    public void setTransportada(String transportada) {
        this.transportada = transportada;
    }
}
