package br.com.webpublico.entidadesauxiliares.comum;

import java.io.Serializable;

public class PeriodoVO implements Serializable {
    private String periodo;
    private String inicio;
    private String termino;

    public PeriodoVO() {
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getTermino() {
        return termino;
    }

    public void setTermino(String termino) {
        this.termino = termino;
    }
}
