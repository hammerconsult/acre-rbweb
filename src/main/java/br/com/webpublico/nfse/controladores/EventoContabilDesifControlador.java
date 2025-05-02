package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.EventoContabilDesif;
import br.com.webpublico.nfse.facades.EventoContabilDesifFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarEventoContabilDesif", pattern = "/tributario/nfse/evento-contabil-desif/listar/", viewId = "/faces/tributario/nfse/evento-contabil-desif/lista.xhtml"),
    @URLMapping(id = "verEventoContabilDesif", pattern = "/tributario/nfse/evento-contabil-desif/ver/#{eventoContabilDesifControlador.id}/", viewId = "/faces/tributario/nfse/evento-contabil-desif/visualizar.xhtml")
})
public class EventoContabilDesifControlador extends PrettyControlador<EventoContabilDesif> implements Serializable, CRUD {

    @EJB
    private EventoContabilDesifFacade facade;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public EventoContabilDesifControlador() {
        super(EventoContabilDesif.class);
    }

    @URLAction(mappingId = "verEventoContabilDesif", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/nfse/evento-contabil-desif/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
