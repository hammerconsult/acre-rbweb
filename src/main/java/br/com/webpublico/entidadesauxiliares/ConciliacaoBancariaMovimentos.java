package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 12/03/2015.
 */
public class ConciliacaoBancariaMovimentos {
    private BigDecimal credito;
    private BigDecimal debito;
    private BigDecimal codigoOperacao;
    private String historico;
    private String dataOperacao;
    private String dataConciliacao;
    private String mesMovimento;
    private String tipoMovimento;
    private String tipoOperacao;
    private String numeroMovimento;

    public ConciliacaoBancariaMovimentos() {
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

    public BigDecimal getCodigoOperacao() {
        return codigoOperacao;
    }

    public void setCodigoOperacao(BigDecimal codigoOperacao) {
        this.codigoOperacao = codigoOperacao;
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

    public String getNumeroMovimento() {
        return numeroMovimento;
    }

    public void setNumeroMovimento(String numeroMovimento) {
        this.numeroMovimento = numeroMovimento;
    }

    public String getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(String dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }
}
