package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.LOA;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class LOAPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("LOA")
    private LOA loa;

    public LOAPortal() {
    }

    public LOAPortal(LOA loa) {
        this.loa = loa;
        super.setTipo(TipoObjetoPortalTransparencia.LOA);
    }

    public LOA getLoa() {
        return loa;
    }

    public void setLoa(LOA loa) {
        this.loa = loa;
    }

    @Override
    public String toString() {
        return loa.toString();
    }
}
