package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Etiqueta("Bem - Portal da Transparência")
public class BemPortal extends EntidadePortalTransparencia {
    @ManyToOne
    private Bem bem;

    public BemPortal() {
    }

    public BemPortal(Bem bem) {
        this.bem = bem;
        super.setTipo(TipoObjetoPortalTransparencia.BEM);
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    @Override
    public String toString() {
        return bem.toString();
    }
}
