package br.com.webpublico.ws.model;

import java.io.Serializable;
import java.util.Date;

public class WsAutenticaNFSAvulsa implements Serializable {

    private String numeroNota;
    private String cpfCnpj;
    private String cpfCnpjUsuario;
    private Date dataLancamento;
    private String numeroAutenticidade;

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getNumeroAutenticidade() {
        return numeroAutenticidade;
    }

    public void setNumeroAutenticidade(String numeroAutenticidade) {
        this.numeroAutenticidade = numeroAutenticidade;
    }

    public String getCpfCnpjUsuario() {
        return cpfCnpjUsuario;
    }

    public void setCpfCnpjUsuario(String cpfCnpjUsuario) {
        this.cpfCnpjUsuario = cpfCnpjUsuario;
    }
}
