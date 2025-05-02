package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

public class RelatorioAtoPotencial {

    private Date data;
    private String numero;
    private String tipoLancamento;
    private String pessoa;
    private String classe;
    private String historico;
    private BigDecimal valor;
    private String unidade;
    private String orgao;
    private String unidadeGestora;
    private String tipoAtoPotencial;
    private String operacao;
    private String eventoContabil;
    private String numeroDataReferencia;

    public RelatorioAtoPotencial() {
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(String tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public String getTipoAtoPotencial() {
        return tipoAtoPotencial;
    }

    public void setTipoAtoPotencial(String tipoAtoPotencial) {
        this.tipoAtoPotencial = tipoAtoPotencial;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(String eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public String getNumeroDataReferencia() {
        return numeroDataReferencia;
    }

    public void setNumeroDataReferencia(String numeroDataReferencia) {
        this.numeroDataReferencia = numeroDataReferencia;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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
