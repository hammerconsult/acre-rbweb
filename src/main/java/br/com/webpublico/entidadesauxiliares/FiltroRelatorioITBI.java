package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;

/**
 * Criado por Mateus
 * Data: 18/04/2017.
 */
public class FiltroRelatorioITBI {
    private String setorInicial;
    private String setorFinal;
    private String quadraInicial;
    private String quadraFinal;
    private String loteInicial;
    private String loteFinal;
    private String inscricaoInicial;
    private String inscricaoFinal;
    private String numeroLaudo;
    private Pessoa transmitente;
    private Pessoa adquirente;

    public FiltroRelatorioITBI() {
    }

    public String getSetorInicial() {
        return setorInicial;
    }

    public void setSetorInicial(String setorInicial) {
        this.setorInicial = setorInicial;
    }

    public String getSetorFinal() {
        return setorFinal;
    }

    public void setSetorFinal(String setorFinal) {
        this.setorFinal = setorFinal;
    }

    public String getQuadraInicial() {
        return quadraInicial;
    }

    public void setQuadraInicial(String quadraInicial) {
        this.quadraInicial = quadraInicial;
    }

    public String getQuadraFinal() {
        return quadraFinal;
    }

    public void setQuadraFinal(String quadraFinal) {
        this.quadraFinal = quadraFinal;
    }

    public String getLoteInicial() {
        return loteInicial;
    }

    public void setLoteInicial(String loteInicial) {
        this.loteInicial = loteInicial;
    }

    public String getLoteFinal() {
        return loteFinal;
    }

    public void setLoteFinal(String loteFinal) {
        this.loteFinal = loteFinal;
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

    public String getNumeroLaudo() {
        return numeroLaudo;
    }

    public void setNumeroLaudo(String numeroLaudo) {
        this.numeroLaudo = numeroLaudo;
    }

    public Pessoa getTransmitente() {
        return transmitente;
    }

    public void setTransmitente(Pessoa transmitente) {
        this.transmitente = transmitente;
    }

    public Pessoa getAdquirente() {
        return adquirente;
    }

    public void setAdquirente(Pessoa adquirente) {
        this.adquirente = adquirente;
    }
}
