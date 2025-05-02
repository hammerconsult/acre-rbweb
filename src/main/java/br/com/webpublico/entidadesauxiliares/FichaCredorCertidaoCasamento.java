package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class FichaCredorCertidaoCasamento {

    private String nomeCartorio;
    private String numeroLivro;
    private BigDecimal numeroFolha;
    private BigDecimal numeroRegistro;
    private String localTrabalhoConjuge;
    private String nomeConjuge;
    private String nacionalidade;
    private String uf;

    public FichaCredorCertidaoCasamento() {
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

    public BigDecimal getNumeroFolha() {
        return numeroFolha;
    }

    public void setNumeroFolha(BigDecimal numeroFolha) {
        this.numeroFolha = numeroFolha;
    }

    public BigDecimal getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(BigDecimal numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public String getLocalTrabalhoConjuge() {
        return localTrabalhoConjuge;
    }

    public void setLocalTrabalhoConjuge(String localTrabalhoConjuge) {
        this.localTrabalhoConjuge = localTrabalhoConjuge;
    }

    public String getNomeConjuge() {
        return nomeConjuge;
    }

    public void setNomeConjuge(String nomeConjuge) {
        this.nomeConjuge = nomeConjuge;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
