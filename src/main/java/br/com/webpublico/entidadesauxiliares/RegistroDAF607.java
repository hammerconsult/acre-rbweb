package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ArquivoDAF607;
import br.com.webpublico.entidades.SuperEntidade;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RegistroDAF607 implements Serializable {

    private BigDecimal valorPrincipal;
    private BigDecimal valorMulta;
    private BigDecimal valorJuros;
    private String cnpjContribuinte;
    private String competenciaConstanteGuia;
    private Date dataArrecadacaoDocumento;
    private String codigoIdentificadorGuia;
    private Date dataVencimentoDocumento;
    private ArquivoDAF607 arquivoDAF607;
    private String codigoRegistro;
    private String sequencialRegistro;
    private String esferaReceita;
    private String dataValidaQualquer;
    private String valorAutenticacaoGuia;
    private String numeroAutenticacaoBancoArrecadador;
    private String codigoBancoArrecadadorGuia;
    private String prefixoAgenciaArrecadadora;
    private String codigoSiafi;
    private Boolean atualizaPessoa;

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

    public Date getDataArrecadacaoDocumento() {
        return dataArrecadacaoDocumento;
    }

    public void setDataArrecadacaoDocumento(Date dataArrecadacaoDocumento) {
        this.dataArrecadacaoDocumento = dataArrecadacaoDocumento;
    }

    public Date getDataVencimentoDocumento() {
        return dataVencimentoDocumento;
    }

    public void setDataVencimentoDocumento(Date dataVencimentoDocumento) {
        this.dataVencimentoDocumento = dataVencimentoDocumento;
    }

    public String getCnpjContribuinte() {
        return cnpjContribuinte;
    }

    public void setCnpjContribuinte(String cnpjContribuinte) {
        this.cnpjContribuinte = cnpjContribuinte;
    }

    public String getEsferaReceita() {
        return esferaReceita;
    }

    public void setEsferaReceita(String esferaReceita) {
        this.esferaReceita = esferaReceita;
    }

    public String getCompetenciaConstanteGuia() {
        return competenciaConstanteGuia;
    }

    public void setCompetenciaConstanteGuia(String competenciaConstanteGuia) {
        this.competenciaConstanteGuia = competenciaConstanteGuia;
    }

    public ArquivoDAF607 getArquivoDAF607() {
        return arquivoDAF607;
    }

    public void setArquivoDAF607(ArquivoDAF607 arquivoDAF607) {
        this.arquivoDAF607 = arquivoDAF607;
    }

    public BigDecimal getValorPrincipal() {
        return valorPrincipal;
    }

    public void setValorPrincipal(BigDecimal valorPrincipal) {
        this.valorPrincipal = valorPrincipal;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public String getDataValidaQualquer() {
        return dataValidaQualquer;
    }

    public void setDataValidaQualquer(String dataValidaQualquer) {
        this.dataValidaQualquer = dataValidaQualquer;
    }

    public String getValorAutenticacaoGuia() {
        return valorAutenticacaoGuia;
    }

    public void setValorAutenticacaoGuia(String valorAutenticacaoGuia) {
        this.valorAutenticacaoGuia = valorAutenticacaoGuia;
    }

    public String getNumeroAutenticacaoBancoArrecadador() {
        return numeroAutenticacaoBancoArrecadador;
    }

    public void setNumeroAutenticacaoBancoArrecadador(String numeroAutenticacaoBancoArrecadador) {
        this.numeroAutenticacaoBancoArrecadador = numeroAutenticacaoBancoArrecadador;
    }

    public String getCodigoBancoArrecadadorGuia() {
        return codigoBancoArrecadadorGuia;
    }

    public void setCodigoBancoArrecadadorGuia(String codigoBancoArrecadadorGuia) {
        this.codigoBancoArrecadadorGuia = codigoBancoArrecadadorGuia;
    }

    public String getPrefixoAgenciaArrecadadora() {
        return prefixoAgenciaArrecadadora;
    }

    public void setPrefixoAgenciaArrecadadora(String prefixoAgenciaArrecadadora) {
        this.prefixoAgenciaArrecadadora = prefixoAgenciaArrecadadora;
    }

    public String getCodigoSiafi() {
        return codigoSiafi;
    }

    public void setCodigoSiafi(String codigoSiafi) {
        this.codigoSiafi = codigoSiafi;
    }

    public String getCodigoIdentificadorGuia() {
        return codigoIdentificadorGuia;
    }

    public void setCodigoIdentificadorGuia(String codigoIdentificadorGuia) {
        this.codigoIdentificadorGuia = codigoIdentificadorGuia;
    }

    public Boolean getAtualizaPessoa() {
        return atualizaPessoa != null ? atualizaPessoa : false;
    }

    public void setAtualizaPessoa(Boolean atualizaPessoa) {
        this.atualizaPessoa = atualizaPessoa;
    }

    @Override
    public String toString() {
        return "RegistroDAF607{" +
            "codigoRegistro='" + codigoRegistro + '\'' +
            ", sequencialRegistro='" + sequencialRegistro + '\'' +
            ", dataArrecadacaoDocumento='" + dataArrecadacaoDocumento + '\'' +
            ", dataVencimentoDocumento='" + dataVencimentoDocumento + '\'' +
            ", cnpjContribuinte='" + cnpjContribuinte + '\'' +
            ", esferaReceita='" + esferaReceita + '\'' +
            ", competenciaConstanteGuia='" + competenciaConstanteGuia + '\'' +
            ", valorPrincipal='" + valorPrincipal + '\'' +
            ", valorMulta='" + valorMulta + '\'' +
            ", valorJuros='" + valorJuros + '\'' +
            ", dataValidaQualquer='" + dataValidaQualquer + '\'' +
            ", valorAutenticacaoGuia='" + valorAutenticacaoGuia + '\'' +
            ", numeroAutenticacaoBancoArrecadador='" + numeroAutenticacaoBancoArrecadador + '\'' +
            ", codigoBancoArrecadadorGuia='" + codigoBancoArrecadadorGuia + '\'' +
            ", prefixoAgenciaArrecadadora='" + prefixoAgenciaArrecadadora + '\'' +
            ", codigoSiafi='" + codigoSiafi + '\'' +
            ", codigoIdentificadorGuia='" + codigoIdentificadorGuia + '\'' +
            '}';
    }
}
