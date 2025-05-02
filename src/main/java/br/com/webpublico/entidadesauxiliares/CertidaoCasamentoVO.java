package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by carlos on 03/04/17.
 */
public class CertidaoCasamentoVO {
    private String nomeConjuge;
    private String nomeCartorio;
    private String numeroLivro;
    private Number numeroFolha;
    private Number numeroRegistro;
    private String nacionalidade;
    private String estado;
    private String localTrabalhoConjuge;
    private Date dataCasamento;
    private Date dataNascimentoConjuge;
    private String cidadeCartorio;
    private String naturalidadeConjuge;

    public CertidaoCasamentoVO() {
    }

    public String getNomeConjuge() {
        return nomeConjuge;
    }

    public void setNomeConjuge(String nomeConjuge) {
        this.nomeConjuge = nomeConjuge;
    }

    public String getNomeCartorio() {
        return nomeCartorio;
    }

    public void setNomeCartorio(String nomeCartorio) {
        this.nomeCartorio = nomeCartorio;
    }

    public String getNumeroLivro() {
        return numeroLivro;
    }

    public void setNumeroLivro(String numeroLivro) {
        this.numeroLivro = numeroLivro;
    }

    public Number getNumeroFolha() {
        return numeroFolha;
    }

    public void setNumeroFolha(Number numeroFolha) {
        this.numeroFolha = numeroFolha;
    }

    public Number getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(Number numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLocalTrabalhoConjuge() {
        return localTrabalhoConjuge;
    }

    public void setLocalTrabalhoConjuge(String localTrabalhoConjuge) {
        this.localTrabalhoConjuge = localTrabalhoConjuge;
    }

    public Date getDataCasamento() {
        return dataCasamento;
    }

    public void setDataCasamento(Date dataCasamento) {
        this.dataCasamento = dataCasamento;
    }

    public Date getDataNascimentoConjuge() {
        return dataNascimentoConjuge;
    }

    public void setDataNascimentoConjuge(Date dataNascimentoConjuge) {
        this.dataNascimentoConjuge = dataNascimentoConjuge;
    }

    public String getCidadeCartorio() {
        return cidadeCartorio;
    }

    public void setCidadeCartorio(String cidadeCartorio) {
        this.cidadeCartorio = cidadeCartorio;
    }

    public String getNaturalidadeConjuge() {
        return naturalidadeConjuge;
    }

    public void setNaturalidadeConjuge(String naturalidadeConjuge) {
        this.naturalidadeConjuge = naturalidadeConjuge;
    }
}
