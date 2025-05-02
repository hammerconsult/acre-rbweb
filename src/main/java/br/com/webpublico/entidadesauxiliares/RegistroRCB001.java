package br.com.webpublico.entidadesauxiliares;


import java.math.BigDecimal;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Claudio
 */
public class RegistroRCB001 {
    private String codigoRegistro;
    private String agenciaBanco;
    private String contaBanco;
    private String digitoBanco;
    private String dataPagamento;
    private String dataCredito;
    private String codigoBarras;
    private BigDecimal valorRecebido;
    private BigDecimal valorTarifa;
    private String numeroSequencial;
    private String codigoAgenciaArrecadadora;
    private String formaArrecadacao;

    public String getAgenciaBanco() {
        return agenciaBanco;
    }

    public void setAgenciaBanco(String agenciaBanco) {
        this.agenciaBanco = agenciaBanco;
    }

    public String getCodigoAgenciaArrecadadora() {
        return codigoAgenciaArrecadadora;
    }

    public void setCodigoAgenciaArrecadadora(String codigoAgenciaArrecadadora) {
        this.codigoAgenciaArrecadadora = codigoAgenciaArrecadadora;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getCodigoRegistro() {
        return codigoRegistro;
    }

    public void setCodigoRegistro(String codigoRegistro) {
        this.codigoRegistro = codigoRegistro;
    }

    public String getContaBanco() {
        return contaBanco;
    }

    public void setContaBanco(String contaBanco) {
        this.contaBanco = contaBanco;
    }

    public String getDataCredito() {
        return dataCredito;
    }

    public void setDataCredito(String dataCredito) {
        this.dataCredito = dataCredito;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getDigitoBanco() {
        return digitoBanco;
    }

    public void setDigitoBanco(String digitoBanco) {
        this.digitoBanco = digitoBanco;
    }

    public String getFormaArrecadacao() {
        return formaArrecadacao;
    }

    public void setFormaArrecadacao(String formaArrecadacao) {
        this.formaArrecadacao = formaArrecadacao;
    }

    public String getNumeroSequencial() {
        return numeroSequencial;
    }

    public void setNumeroSequencial(String numeroSequencial) {
        this.numeroSequencial = numeroSequencial;
    }

    public BigDecimal getValorTarifa() {
        return valorTarifa;
    }

    public void setValorTarifa(BigDecimal valorTarifa) {
        this.valorTarifa = valorTarifa;
    }

    public BigDecimal getValorRecebido() {
        return valorRecebido;
    }

    public void setValorRecebido(BigDecimal valorRecebido) {
        this.valorRecebido = valorRecebido;
    }

}
