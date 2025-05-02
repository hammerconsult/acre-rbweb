package br.com.webpublico.controle;

import br.com.webpublico.entidades.RecursoExterno;
import br.com.webpublico.entidades.RecursoSistema;
import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.enums.Sistema;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RecursoExternoFacade;
import br.com.webpublico.negocios.RecursoSistemaFacade;
import br.com.webpublico.seguranca.menu.LeitorMenuFacade;
import br.com.webpublico.seguranca.service.RecursoSistemaService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRecursoExterno",
            pattern = "/comum/recurso-externo/novo/",
            viewId = "/faces/comum/recurso-externo/edita.xhtml"),
        @URLMapping(id = "editarRecursoExterno",
            pattern = "/comum/recurso-externo/editar/#{recursoExternoControlador.id}/",
            viewId = "/faces/comum/recurso-externo/edita.xhtml"),
        @URLMapping(id = "listarRecursoExterno",
            pattern = "/comum/recurso-externo/listar/",
            viewId = "/faces/comum/recurso-externo/lista.xhtml"),
        @URLMapping(id = "verRecursoExterno",
            pattern = "/comum/recurso-externo/ver/#{recursoExternoControlador.id}/",
            viewId = "/faces/comum/recurso-externo/visualizar.xhtml")
})
public class RecursoExternoControlador extends PrettyControlador<RecursoExterno> implements Serializable, CRUD {

    @EJB
    private RecursoExternoFacade recursoExternoFacade;

    public RecursoExternoControlador() {
        super(RecursoExterno.class);
    }

    @URLAction(mappingId = "novoRecursoExterno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verRecursoExterno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarRecursoExterno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<SelectItem> getSistemas() {
        return Util.getListSelectItem(Sistema.values(), false);
    }

    @Override
    public AbstractFacade getFacede() {
        return recursoExternoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/comum/recurso-externo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void depoisDeSalvar() {
        recursoExternoFacade.limparCacheSistemasExternos();
    }
}
