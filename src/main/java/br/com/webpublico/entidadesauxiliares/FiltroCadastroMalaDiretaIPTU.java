package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 20/03/14
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
public class FiltroCadastroMalaDiretaIPTU implements Serializable {

    private String inscricaoInicial;
    private String inscricaoFinal;

    public FiltroCadastroMalaDiretaIPTU() {
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }
}
