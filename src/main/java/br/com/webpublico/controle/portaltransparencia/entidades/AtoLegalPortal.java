package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 13/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class AtoLegalPortal extends EntidadePortalTransparencia {

    @ManyToOne
    private AtoLegal atoLegal;

    public AtoLegalPortal() {
    }

    public AtoLegalPortal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
        super.setTipo(TipoObjetoPortalTransparencia.ATO_LEGAL);
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    @Override
    public String toString() {
        return atoLegal.toString();
    }
}
