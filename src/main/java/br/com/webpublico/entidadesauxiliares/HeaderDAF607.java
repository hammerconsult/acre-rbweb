package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.SuperEntidade;

/**
 * Created by William on 10/04/2017.
 */


public class HeaderDAF607 extends SuperEntidade {

    private String codigoRegistro;
    private String sequencialRegistro;
    private String codigoConvenio;
    private String dataGeracaoArquivo;
    private String numeroRemessaSerpro;
    private String numeroVersao;
    private String filler;
    private String usoBancoSerpro;
    private String codigoBancoArrecadador;
    private String dataCreditoConta;

    public HeaderDAF607() {
    }

    @Override
    public Long getId() {
        return null;
    }

    public String getCodigoRegistro() {
        return codigoRegistro;
    }

    public void setCodigoRegistro(String codigoRegistro) {
        this.codigoRegistro = codigoRegistro;
    }

    public String getSequencialRegistro() {
        return sequencialRegistro;
    }

    public void setSequencialRegistro(String sequencialRegistro) {
        this.sequencialRegistro = sequencialRegistro;
    }

    public String getCodigoConvenio() {
        return codigoConvenio;
    }

    public void setCodigoConvenio(String codigoConvenio) {
        this.codigoConvenio = codigoConvenio;
    }

    public String getDataGeracaoArquivo() {
        return dataGeracaoArquivo;
    }

    public void setDataGeracaoArquivo(String dataGeracaoArquivo) {
        this.dataGeracaoArquivo = dataGeracaoArquivo;
    }

    public String getNumeroRemessaSerpro() {
        return numeroRemessaSerpro;
    }

    public void setNumeroRemessaSerpro(String numeroRemessaSerpro) {
        this.numeroRemessaSerpro = numeroRemessaSerpro;
    }

    public String getNumeroVersao() {
        return numeroVersao;
    }

    public void setNumeroVersao(String numeroVersao) {
        this.numeroVersao = numeroVersao;
    }

    public String getFiller() {
        return filler;
    }

    public void setFiller(String filler) {
        this.filler = filler;
    }

    public String getUsoBancoSerpro() {
        return usoBancoSerpro;
    }

    public void setUsoBancoSerpro(String usoBancoSerpro) {
        this.usoBancoSerpro = usoBancoSerpro;
    }

    public String getCodigoBancoArrecadador() {
        return codigoBancoArrecadador;
    }

    public void setCodigoBancoArrecadador(String codigoBancoArrecadador) {
        this.codigoBancoArrecadador = codigoBancoArrecadador;
    }

    public String getDataCreditoConta() {
        return dataCreditoConta;
    }

    public void setDataCreditoConta(String dataCreditoConta) {
        this.dataCreditoConta = dataCreditoConta;
    }

    @Override
    public String toString() {
        return "HeaderDAF607{" +
            "codigoRegistro='" + codigoRegistro + '\'' +
            ", sequencialRegistro='" + sequencialRegistro + '\'' +
            ", codigoConvenio='" + codigoConvenio + '\'' +
            ", dataGeracaoArquivo='" + dataGeracaoArquivo + '\'' +
            ", numeroRemessaSerpro='" + numeroRemessaSerpro + '\'' +
            ", numeroVersao='" + numeroVersao + '\'' +
            ", filler='" + filler + '\'' +
            ", usoBancoSerpro='" + usoBancoSerpro + '\'' +
            ", codigoBancoArrecadador='" + codigoBancoArrecadador + '\'' +
            ", dataCreditoConta='" + dataCreditoConta + '\'' +
            '}';
    }
}
