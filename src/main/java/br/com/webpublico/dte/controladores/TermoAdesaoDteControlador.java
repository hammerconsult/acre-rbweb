package br.com.webpublico.dte.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.dte.entidades.TermoAdesaoDte;
import br.com.webpublico.dte.facades.TermoAdesaoDteFacade;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarTermoAdesaoDte", pattern = "/tributario/dte/termo-adesao/listar/",
        viewId = "/faces/tributario/dte/termoadesao/lista.xhtml"),
    @URLMapping(id = "verTermoAdesaoDte", pattern = "/tributario/dte/termo-adesao/ver/#{termoAdesaoDteControlador.id}/",
        viewId = "/faces/tributario/dte/termoadesao/visualizar.xhtml"),
    @URLMapping(id = "novoTermoAdesaoDte", pattern = "/tributario/dte/termo-adesao/novo/",
        viewId = "/faces/tributario/dte/termoadesao/edita.xhtml"),
    @URLMapping(id = "editarTermoAdesaoDte", pattern = "/tributario/dte/termo-adesao/editar/#{termoAdesaoDteControlador.id}/",
        viewId = "/faces/tributario/dte/termoadesao/edita.xhtml")
})
public class TermoAdesaoDteControlador extends PrettyControlador<TermoAdesaoDte> implements CRUD {

    @EJB
    private TermoAdesaoDteFacade facade;

    public TermoAdesaoDteControlador() {
        super(TermoAdesaoDte.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/dte/termo-adesao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "novoTermoAdesaoDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioEm(new Date());
    }

    @URLAction(mappingId = "editarTermoAdesaoDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verTermoAdesaoDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }
}
