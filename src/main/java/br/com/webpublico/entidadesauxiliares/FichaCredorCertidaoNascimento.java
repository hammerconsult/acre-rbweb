package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class FichaCredorCertidaoNascimento {

    private String nomeCartorio;
    private String numeroLivro;
    private BigDecimal numeroRegistro;
    private String numeroFolha;
    private String cidade;

    public FichaCredorCertidaoNascimento() {
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

    public BigDecimal getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(BigDecimal numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public String getNumeroFolha() {
        return numeroFolha;
    }

    public void setNumeroFolha(String numeroFolha) {
        this.numeroFolha = numeroFolha;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
}
