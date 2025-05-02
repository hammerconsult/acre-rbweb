package br.com.webpublico.controle.comum;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.comum.TemplateEmail;
import br.com.webpublico.entidades.comum.TipoTemplateEmail;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.comum.TemplateEmailFacade;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "templateEmailNovo", pattern = "/comum/template-email/novo/", viewId = "/faces/comum/templateemail/edita.xhtml"),
    @URLMapping(id = "templateEmailListar", pattern = "/comum/template-email/listar/", viewId = "/faces/comum/templateemail/lista.xhtml"),
    @URLMapping(id = "templateEmailEditar", pattern = "/comum/template-email/editar/#{templateEmailControlador.id}/", viewId = "/faces/comum/templateemail/edita.xhtml"),
    @URLMapping(id = "templateEmailVer", pattern = "/comum/template-email/ver/#{templateEmailControlador.id}/", viewId = "/faces/comum/templateemail/visualizar.xhtml"),})
public class TemplateEmailControlador extends PrettyControlador<TemplateEmail> implements CRUD {

    @EJB
    private TemplateEmailFacade facade;

    public TemplateEmailControlador() {
        super(TemplateEmail.class);
    }

    @Override
    @URLAction(mappingId = "templateEmailNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "templateEmailEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "templateEmailVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public TemplateEmailFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/comum/template-email/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getTiposTemplate() {
        return Util.getListSelectItem(TipoTemplateEmail.values());
    }
}
