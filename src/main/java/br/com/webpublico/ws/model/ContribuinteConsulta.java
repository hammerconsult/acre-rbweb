package br.com.webpublico.ws.model;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 29/01/14
 * Time: 15:30
 * To change this template use File | Settings | File Templates.
 */
public class ContribuinteConsulta {
    private String inscricaoCadastral;
    private String nome;

    public ContribuinteConsulta() {
    }

    public ContribuinteConsulta(String inscricaoCadastral, String nome) {
        this.inscricaoCadastral = inscricaoCadastral;
        this.nome = nome;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
