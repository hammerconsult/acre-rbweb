package br.com.webpublico.ws.model;

import java.util.Date;

public class WSValidacaoCertidao {

    private Integer numero;
    private Integer ano;
    private Date emissao;
    private String chave;

    public WSValidacaoCertidao() {
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }
}
