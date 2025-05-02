package br.com.webpublico.controle;

import br.com.webpublico.entidades.Zona;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ZonaFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(
                id = "inserirZona",
                pattern = "/cadastro-imobiliario/alvara-imediato/zona/novo/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/zona/edita.xhtml"),
        @URLMapping(
                id = "editarZona",
                pattern = "/cadastro-imobiliario/alvara-imediato/zona/editar/#{zonaControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/zona/edita.xhtml"),
        @URLMapping(
                id = "verZona",
                pattern = "/cadastro-imobiliario/alvara-imediato/zona/ver/#{zonaControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/zona/visualiza.xhtml"),
        @URLMapping(
                id = "listarZona",
                pattern = "/cadastro-imobiliario/alvara-imediato/zona/listar/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/zona/lista.xhtml")
})
public class ZonaControlador extends PrettyControlador<Zona> implements CRUD {

    @EJB
    private ZonaFacade facade;

    public ZonaControlador() {
        super(Zona.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cadastro-imobiliario/alvara-imediato/zona/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "inserirZona", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarZona", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verZona", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public List<Zona> completarZonas(String parte) {
        return facade.buscarZonas(parte);
    }
}
