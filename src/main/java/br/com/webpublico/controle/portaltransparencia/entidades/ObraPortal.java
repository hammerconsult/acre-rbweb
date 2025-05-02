package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.Obra;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class ObraPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("Obra")
    private Obra obra;

    public ObraPortal() {
    }

    public ObraPortal(Obra obra) {
        this.obra = obra;
        super.setTipo(TipoObjetoPortalTransparencia.OBRA);
    }

    public Obra getObra() {
        return obra;
    }

    public void setObra(Obra obra) {
        this.obra = obra;
    }

    @Override
    public String toString() {
        return obra.toString();
    }
}
