package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Fabio
 */
public class PeriodoGrupoUsuario implements Serializable{

    private String domingo;
    private String segunda;
    private String terca;
    private String quarta;
    private String quinta;
    private String sexta;
    private String sabado;
    private Date inicio;
    private Date termino;

    public String getDomingo() {
        return domingo;
    }

    public void setDomingo(String domingo) {
        this.domingo = domingo;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public String getQuarta() {
        return quarta;
    }

    public void setQuarta(String quarta) {
        this.quarta = quarta;
    }

    public String getQuinta() {
        return quinta;
    }

    public void setQuinta(String quinta) {
        this.quinta = quinta;
    }

    public String getSabado() {
        return sabado;
    }

    public void setSabado(String sabado) {
        this.sabado = sabado;
    }

    public String getSegunda() {
        return segunda;
    }

    public void setSegunda(String segunda) {
        this.segunda = segunda;
    }

    public String getSexta() {
        return sexta;
    }

    public void setSexta(String sexta) {
        this.sexta = sexta;
    }

    public String getTerca() {
        return terca;
    }

    public void setTerca(String terca) {
        this.terca = terca;
    }

    public Date getTermino() {
        return termino;
    }

    public void setTermino(Date termino) {
        this.termino = termino;
    }

    @Override
    public String toString() {
        return "PeriodoGrupoUsuario{" + "domingo=" + domingo + ", segunda=" + segunda + ", terca=" + terca + ", quarta=" + quarta + ", quinta=" + quinta + ", sexta=" + sexta + ", sabado=" + sabado + ", inicio=" + inicio + ", termino=" + termino + '}';
    }

}
