package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class BoletimCadastroEconomicoEndereco implements Serializable {
    private String inscricaoImobiliaria;
    private String situacaoFiscal;
    private String logradouro;
    private String numero;
    private String bairro;
    private String cep;

    public BoletimCadastroEconomicoEndereco() {
    }

    public String getInscricaoImobiliaria() {
        return inscricaoImobiliaria;
    }

    public void setInscricaoImobiliaria(String inscricaoImobiliaria) {
        this.inscricaoImobiliaria = inscricaoImobiliaria;
    }

    public String getSituacaoFiscal() {
        return situacaoFiscal;
    }

    public void setSituacaoFiscal(String situacaoFiscal) {
        this.situacaoFiscal = situacaoFiscal;
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

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}
