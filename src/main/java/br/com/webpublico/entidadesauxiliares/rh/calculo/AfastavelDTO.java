package br.com.webpublico.entidadesauxiliares.rh.calculo;

import java.util.Date;

public class AfastavelDTO {

    private Date inicio;
    private Date termino;
    private Tipo tipo;
    private Integer dias;

    public AfastavelDTO() {
        dias = 0;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getTermino() {
        return termino;
    }

    public void setTermino(Date termino) {
        this.termino = termino;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Integer getDias() {
        return dias;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    public enum Tipo {
        DIAS,
        HORAS
    }

}
