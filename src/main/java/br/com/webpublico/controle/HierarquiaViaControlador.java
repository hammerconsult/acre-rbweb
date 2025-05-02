package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaVia;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.HierarquiaViaFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(
                id = "inserirHierarquiaVia",
                pattern = "/cadastro-imobiliario/alvara-imediato/hierarquia-via/novo/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/hierarquiavia/edita.xhtml"),
        @URLMapping(
                id = "editarHierarquiaVia",
                pattern = "/cadastro-imobiliario/alvara-imediato/hierarquia-via/editar/#{hierarquiaViaControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/hierarquiavia/edita.xhtml"),
        @URLMapping(
                id = "verHierarquiaVia",
                pattern = "/cadastro-imobiliario/alvara-imediato/hierarquia-via/ver/#{hierarquiaViaControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/hierarquiavia/visualiza.xhtml"),
        @URLMapping(
                id = "listarHierarquiaVia",
                pattern = "/cadastro-imobiliario/alvara-imediato/hierarquia-via/listar/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/hierarquiavia/lista.xhtml")
})
public class HierarquiaViaControlador extends PrettyControlador<HierarquiaVia> implements CRUD {

    @EJB
    private HierarquiaViaFacade facade;

    public HierarquiaViaControlador() {
        super(HierarquiaVia.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cadastro-imobiliario/alvara-imediato/hierarquia-via/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "inserirHierarquiaVia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarHierarquiaVia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verHierarquiaVia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }
}
