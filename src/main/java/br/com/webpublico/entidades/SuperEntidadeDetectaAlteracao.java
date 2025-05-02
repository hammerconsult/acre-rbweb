package br.com.webpublico.entidades;

import javax.persistence.Transient;

public abstract class SuperEntidadeDetectaAlteracao extends SuperEntidade {

    @Transient
    private boolean modificado;

    public final boolean isModificado() {
        return modificado;
    }

    public final void setModificado(boolean modificado) {
        this.modificado = modificado;
    }
}
