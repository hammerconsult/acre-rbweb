package br.com.webpublico.controle;

import br.com.webpublico.entidades.ApiKey;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.ApiKeyFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-api-key",
        pattern = "/comum/api-key/novo/",
        viewId = "/faces/comum/api-key/edita.xhtml"),
    @URLMapping(id = "editar-api-key",
        pattern = "/comum/api-key/editar/#{apiKeyControlador.id}/",
        viewId = "/faces/comum/api-key/edita.xhtml"),
    @URLMapping(id = "ver-api-key",
        pattern = "/comum/api-key/ver/#{apiKeyControlador.id}/",
        viewId = "/faces/comum/api-key/visualizar.xhtml"),
    @URLMapping(id = "listar-api-key",
        pattern = "/comum/api-key/listar/",
        viewId = "/faces/comum/api-key/lista.xhtml")
})

public class ApiKeyControlador extends PrettyControlador<ApiKey> implements CRUD {

    @EJB
    private ApiKeyFacade facade;

    public ApiKeyControlador() {
        super(ApiKey.class);
    }

    @Override
    public ApiKeyFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/comum/api-key/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-api-key", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-api-key", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-api-key", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }


}
