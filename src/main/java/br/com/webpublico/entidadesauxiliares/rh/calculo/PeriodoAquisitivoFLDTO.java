package br.com.webpublico.entidadesauxiliares.rh.calculo;

import java.util.Date;
import java.util.Objects;

public class PeriodoAquisitivoFLDTO {

    private Long id;
    private Date inicioVigencia;
    private Date finalVigencia;

    public PeriodoAquisitivoFLDTO() {
    }

    public PeriodoAquisitivoFLDTO(Long id, Date inicioVigencia, Date finalVigencia) {
        this.id = id;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeriodoAquisitivoFLDTO that = (PeriodoAquisitivoFLDTO) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
