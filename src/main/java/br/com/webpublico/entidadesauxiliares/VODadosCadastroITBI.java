package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;

public class VODadosCadastroITBI implements Serializable {
    private String codigoSetor;
    private String codigoQuadra;
    private String nomePropriedade;
    private String codigoPropriedade;
    private String codigoLote;
    private String descricaoLote;
    private String tipoLogradouro;
    private String logradouro;
    private String numero;
    private String complementoLote;
    private String complemento;
    private String bairro;
    private BigDecimal areaTerreno;
    private BigDecimal areaConstruida;
    private BigDecimal valorVenal;

    public VODadosCadastroITBI() {
        this.areaTerreno = BigDecimal.ZERO;
        this.areaConstruida = BigDecimal.ZERO;
        this.valorVenal = BigDecimal.ZERO;
    }

    public String getCodigoSetor() {
        return codigoSetor;
    }

    public void setCodigoSetor(String codigoSetor) {
        this.codigoSetor = codigoSetor;
    }

    public String getCodigoQuadra() {
        return codigoQuadra;
    }

    public void setCodigoQuadra(String codigoQuadra) {
        this.codigoQuadra = codigoQuadra;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public String getDescricaoLote() {
        return descricaoLote;
    }

    public void setDescricaoLote(String descricaoLote) {
        this.descricaoLote = descricaoLote;
    }

    public String getTipoLogradouro() {
        return tipoLogradouro;
    }

    public void setTipoLogradouro(String tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplementoLote() {
        return complementoLote;
    }

    public void setComplementoLote(String complementoLote) {
        this.complementoLote = complementoLote;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public BigDecimal getAreaTerreno() {
        return areaTerreno;
    }

    public void setAreaTerreno(BigDecimal areaTerreno) {
        this.areaTerreno = areaTerreno;
    }

    public BigDecimal getAreaConstruida() {
        return areaConstruida;
    }

    public void setAreaConstruida(BigDecimal areaConstruida) {
        this.areaConstruida = areaConstruida;
    }

    public BigDecimal getValorVenal() {
        return valorVenal;
    }

    public void setValorVenal(BigDecimal valorVenal) {
        this.valorVenal = valorVenal;
    }

    public String getNomePropriedade() {
        return nomePropriedade;
    }

    public void setNomePropriedade(String nomePropriedade) {
        this.nomePropriedade = nomePropriedade;
    }

    public String getCodigoPropriedade() {
        return codigoPropriedade;
    }

    public void setCodigoPropriedade(String codigoPropriedade) {
        this.codigoPropriedade = codigoPropriedade;
    }
}
