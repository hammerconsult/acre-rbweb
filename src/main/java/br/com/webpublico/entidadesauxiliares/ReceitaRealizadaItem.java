package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 10/06/14
 * Time: 09:17
 * To change this template use File | Settings | File Templates.
 */
public class ReceitaRealizadaItem {

    private Long id;
    private Date dataLancamento;
    private String numeroReceita;
    private String numero;
    private String lote;
    private String pessoa;
    private String classe;
    private String conta;
    private String contaFinanceira;
    private String historico;
    private BigDecimal valor;
    private BigDecimal count;
    private String evento;
    private String unidade;
    private String orgao;
    private String unidadeGestora;
    private Date dataConciliacao;
    private List<ReceitaFonteItem> receitaFonteItens;
    private String operacao;
    private Date dataReferencia;


    public ReceitaRealizadaItem() {
        receitaFonteItens = new ArrayList<>();
    }

    public String getNumeroReceita() {
        return numeroReceita;
    }

    public void setNumeroReceita(String numeroReceita) {
        this.numeroReceita = numeroReceita;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
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

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(String contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
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

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
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

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public List<ReceitaFonteItem> getReceitaFonteItens() {
        return receitaFonteItens;
    }

    public void setReceitaFonteItens(List<ReceitaFonteItem> receitaFonteItens) {
        this.receitaFonteItens = receitaFonteItens;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }
}
