package br.com.webpublico.entidadesauxiliares;

/**
 * Created by mga on 03/07/2017.
 */
public class FiltroMalaDiretaGeralRural {

    private String inscricaoIncial;
    private String inscricaoFinal;

    public FiltroMalaDiretaGeralRural() {
        inscricaoIncial = null;
        inscricaoFinal = null;
    }

    public String getInscricaoIncial() {
        return inscricaoIncial;
    }

    public void setInscricaoIncial(String inscricaoIncial) {
        this.inscricaoIncial = inscricaoIncial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }
}
