package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoLogradouroEnderecoCorreio;

import java.io.Serializable;

public class VOEndereco implements Serializable {
    private String logradouro;
    private String bairro;
    private String numero;
    private String complemento;
    private String cep;
    private String localidade;
    private String uf;
    private TipoLogradouroEnderecoCorreio tipoLogradouro;

    public VOEndereco() {
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public TipoLogradouroEnderecoCorreio getTipoLogradouro() {
        return tipoLogradouro;
    }

    public void setTipoLogradouro(TipoLogradouroEnderecoCorreio tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
    }
}
