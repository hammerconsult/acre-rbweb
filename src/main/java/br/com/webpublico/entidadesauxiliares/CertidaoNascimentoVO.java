package br.com.webpublico.entidadesauxiliares;

/**
 * Created by carlos on 03/04/17.
 */
public class CertidaoNascimentoVO {

    private String nomeCartorio;
    private String numeroLivro;
    private String numeroFolha;
    private Number numeroRegistro;
    private String cidade;

    public CertidaoNascimentoVO() {
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

    public String getNumeroFolha() {
        return numeroFolha;
    }

    public void setNumeroFolha(String numeroFolha) {
        this.numeroFolha = numeroFolha;
    }

    public Number getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(Number numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
}
