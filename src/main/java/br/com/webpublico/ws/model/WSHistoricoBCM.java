package br.com.webpublico.ws.model;

import java.util.Date;

public class WSHistoricoBCM {

    private Long id;
    private Date dataOperacao;
    private String nomeUsuario;
    private String autenticidade;
    private String pessoa;
    private String inscricaoCadastral;
    private String status;

    public WSHistoricoBCM() {
    }

    public WSHistoricoBCM(Long id, Date dataOperacao, String nomeUsuario, String autenticidade) {
        this.id = id;
        this.dataOperacao = dataOperacao;
        this.nomeUsuario = nomeUsuario;
        this.autenticidade = autenticidade;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getAutenticidade() {
        return autenticidade;
    }

    public void setAutenticidade(String autenticidade) {
        this.autenticidade = autenticidade;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
