package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Mateus on 03/03/2015.
 */
public class ExtratoMovFinanceiraMovimentos {
    private String codigoOrgao;
    private String codigoUnidade;
    private Date dataMovimento;
    private Date dataConciliacao;
    private String mesMovimento;
    private String evento;
    private String tipoMovimento;
    private String numero;
    private String historico;
    private BigDecimal credito;
    private BigDecimal debito;
    private String fonteDeRecursos;
    private String contaFinanceira;
    private String codigoEvento;
    private String descricaoEvento;
    private String historicoLancamento;

    public ExtratoMovFinanceiraMovimentos() {
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(String tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
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

    public String getMesMovimento() {
        return mesMovimento;
    }

    public void setMesMovimento(String mesMovimento) {
        this.mesMovimento = mesMovimento;
    }

    public String getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(String fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public String getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(String contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public String getDescricaoEvento() {
        return descricaoEvento;
    }

    public void setDescricaoEvento(String descricaoEvento) {
        this.descricaoEvento = descricaoEvento;
    }

    public String getHistoricoLancamento() {
        return historicoLancamento;
    }

    public void setHistoricoLancamento(String historicoLancamento) {
        this.historicoLancamento = historicoLancamento;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }
}
