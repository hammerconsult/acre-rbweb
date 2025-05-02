/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import java.io.Serializable;

/**
 * @author peixe
 */
public class FebrabanTrailerArquivo implements Serializable {

    private String bancoTrailer;
    private String loteTrailer;
    private String registroTrailer;
    private String usoExclusivoCnabTrailer;
    private Integer qtdeLotes;
    private Integer qtdeRegistros;
    private Integer qtdeContasConcil;

    public FebrabanTrailerArquivo() {
    }

    public String getBancoTrailer() {
        return bancoTrailer;
    }

    public void setBancoTrailer(String bancoTrailer) {
        this.bancoTrailer = bancoTrailer;
    }

    public String getLoteTrailer() {
        return loteTrailer;
    }

    public void setLoteTrailer(String loteTrailer) {
        this.loteTrailer = loteTrailer;
    }

    public Integer getQtdeContasConcil() {
        return qtdeContasConcil;
    }

    public void setQtdeContasConcil(Integer qtdeContasConcil) {
        this.qtdeContasConcil = qtdeContasConcil;
    }

    public Integer getQtdeLotes() {
        return qtdeLotes;
    }

    public void setQtdeLotes(Integer qtdeLotes) {
        this.qtdeLotes = qtdeLotes;
    }

    public Integer getQtdeRegistros() {
        return qtdeRegistros;
    }

    public void setQtdeRegistros(Integer qtdeRegistros) {
        this.qtdeRegistros = qtdeRegistros;
    }

    public String getRegistroTrailer() {
        return registroTrailer;
    }

    public void setRegistroTrailer(String registroTrailer) {
        this.registroTrailer = registroTrailer;
    }

    public String getUsoExclusivoCnabTrailer() {
        return usoExclusivoCnabTrailer;
    }

    public void setUsoExclusivoCnabTrailer(String usoExclusivoCnabTrailer) {
        this.usoExclusivoCnabTrailer = usoExclusivoCnabTrailer;
    }
}
