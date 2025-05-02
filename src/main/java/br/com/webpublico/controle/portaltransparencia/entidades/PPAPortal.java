package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.PPA;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class PPAPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("PPA")
    private PPA ppa;

    public PPAPortal() {
    }

    public PPAPortal(PPA ppa) {
        this.ppa = ppa;
        super.setTipo(TipoObjetoPortalTransparencia.PPA);
    }

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    @Override
    public String toString() {
        return ppa.toString();
    }
}
