package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoCalculoRBTRans;

import java.io.Serializable;

public class VOCalculoRBTrans implements Serializable {
    private Long idCalculo;
    private Long idCalculoISS;
    private TipoCalculoRBTRans tipoCalculoRBTRans;

    public VOCalculoRBTrans() {
    }

    public Long getIdCalculo() {
        return idCalculo;
    }

    public void setIdCalculo(Long idCalculo) {
        this.idCalculo = idCalculo;
    }

    public Long getIdCalculoISS() {
        return idCalculoISS;
    }

    public void setIdCalculoISS(Long idCalculoISS) {
        this.idCalculoISS = idCalculoISS;
    }

    public TipoCalculoRBTRans getTipoCalculoRBTRans() {
        return tipoCalculoRBTRans;
    }

    public void setTipoCalculoRBTRans(TipoCalculoRBTRans tipoCalculoRBTRans) {
        this.tipoCalculoRBTRans = tipoCalculoRBTRans;
    }
}
