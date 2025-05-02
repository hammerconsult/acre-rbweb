package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.LDO;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class LDOPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("LDO")
    private LDO ldo;

    public LDOPortal() {
    }

    public LDOPortal(LDO ldo) {
        this.ldo = ldo;
        super.setTipo(TipoObjetoPortalTransparencia.LDO);
    }

    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }

    @Override
    public String toString() {
        return ldo.toString();
    }
}
