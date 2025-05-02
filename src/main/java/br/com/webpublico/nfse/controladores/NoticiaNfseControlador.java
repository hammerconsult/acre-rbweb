package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.NoticiaNfse;
import br.com.webpublico.nfse.facades.NoticiaNfseFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "noticia-nfse-novo", pattern = "/nfse/noticia/novo/",
        viewId = "/faces/tributario/nfse/noticia/edita.xhtml"),
    @URLMapping(id = "noticia-nfse-listar", pattern = "/nfse/noticia/listar/",
        viewId = "/faces/tributario/nfse/noticia/lista.xhtml"),
    @URLMapping(id = "noticia-nfse-editar", pattern = "/nfse/noticia/editar/#{noticiaNfseControlador.id}/",
        viewId = "/faces/tributario/nfse/noticia/edita.xhtml"),
    @URLMapping(id = "noticia-nfse-ver", pattern = "/nfse/noticia/ver/#{noticiaNfseControlador.id}/",
        viewId = "/faces/tributario/nfse/noticia/visualizar.xhtml"),
})
public class NoticiaNfseControlador extends PrettyControlador<NoticiaNfse> implements CRUD {

    @EJB
    private NoticiaNfseFacade facade;

    public NoticiaNfseControlador() {
        super(NoticiaNfse.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/noticia/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "noticia-nfse-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "noticia-nfse-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "noticia-nfse-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }
}
