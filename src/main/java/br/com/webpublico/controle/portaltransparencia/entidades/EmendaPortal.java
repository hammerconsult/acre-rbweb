package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.Emenda;
import br.com.webpublico.entidades.PropostaConcessaoDiaria;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 13/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class EmendaPortal extends EntidadePortalTransparencia {

    @ManyToOne
    private Emenda emenda;

    public EmendaPortal() {
    }

    public EmendaPortal(Emenda emenda) {
        this.emenda = emenda;
        super.setTipo(TipoObjetoPortalTransparencia.EMENDA);
    }

    public Emenda getEmenda() {
        return emenda;
    }

    public void setEmenda(Emenda emenda) {
        this.emenda = emenda;
    }

    @Override
    public String toString() {
        return emenda.toString();
    }
}
