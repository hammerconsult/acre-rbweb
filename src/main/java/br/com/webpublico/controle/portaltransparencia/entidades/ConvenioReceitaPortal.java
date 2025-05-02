package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.ConvenioReceita;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 17/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class ConvenioReceitaPortal extends EntidadePortalTransparencia {

    @ManyToOne
    private ConvenioReceita convenioReceita;

    public ConvenioReceitaPortal() {
    }

    public ConvenioReceitaPortal(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
        super.setTipo(TipoObjetoPortalTransparencia.CONVENIO_RECEITA);
    }

    public ConvenioReceita getConvenioReceita() {
        return convenioReceita;
    }

    public void setConvenioReceita(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
    }

    @Override
    public String toString() {
        return convenioReceita.toString();
    }
}
