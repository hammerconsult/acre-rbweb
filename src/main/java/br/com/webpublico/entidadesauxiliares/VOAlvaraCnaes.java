package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class VOAlvaraCnaes implements Serializable {
    private Long idCnae;

    public VOAlvaraCnaes() {
    }

    public Long getIdCnae() {
        return idCnae;
    }

    public void setIdCnae(Long idCnae) {
        this.idCnae = idCnae;
    }
}
