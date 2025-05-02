package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.tributario.TipoNumeroDoctoOficial;
import com.google.common.base.Objects;

import java.io.Serializable;

public class NumeroDoctoOficial implements Serializable {
    private final Long id;
    private final TipoNumeroDoctoOficial tipo;
    private Long idExercicio;

    public NumeroDoctoOficial(Long id, TipoNumeroDoctoOficial tipo) {
        this.id = id;
        this.tipo = tipo;
    }

    public NumeroDoctoOficial(Long id, Long idExercicio, TipoNumeroDoctoOficial tipo) {
        this.id = id;
        this.idExercicio = idExercicio;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public Long getIdExercicio() {
        return idExercicio;
    }

    public TipoNumeroDoctoOficial getTipo() {
        return tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumeroDoctoOficial that = (NumeroDoctoOficial) o;
        if (idExercicio != null)
            return Objects.equal(id, that.id) && Objects.equal(idExercicio, that.idExercicio) && tipo == that.tipo;
        return Objects.equal(id, that.id) && tipo == that.tipo;
    }

    @Override
    public int hashCode() {
        if (idExercicio != null)
            return Objects.hashCode(id, idExercicio, tipo);
        return Objects.hashCode(id, tipo);
    }
}
