package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.TemplateNfse;
import br.com.webpublico.nfse.enums.TipoTemplateNfse;
import br.com.webpublico.nfse.facades.TemplateNfseFacade;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mateus on 20/10/17.
 */
@ManagedBean(name = "templateNfseControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-template-nfse", pattern = "/nfse/template-nfse/novo/", viewId = "/faces/tributario/nfse/template-nfse/edita.xhtml"),
    @URLMapping(id = "listar-template-nfse", pattern = "/nfse/template-nfse/listar/", viewId = "/faces/tributario/nfse/template-nfse/lista.xhtml"),
    @URLMapping(id = "editar-template-nfse", pattern = "/nfse/template-nfse/editar/#{templateNfseControlador.id}/", viewId = "/faces/tributario/nfse/template-nfse/edita.xhtml"),
    @URLMapping(id = "ver-template-nfse", pattern = "/nfse/template-nfse/ver/#{templateNfseControlador.id}/", viewId = "/faces/tributario/nfse/template-nfse/visualizar.xhtml"),
})
public class TemplateNfseControlador extends PrettyControlador<TemplateNfse> implements Serializable, CRUD {
    @EJB
    private TemplateNfseFacade templateNfseFacade;

    public TemplateNfseControlador() {
        super(TemplateNfse.class);
    }

    @Override
    @URLAction(mappingId = "novo-template-nfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editar-template-nfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-template-nfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public List<SelectItem> getTiposTemplateNfse() {
        return Util.getListSelectItem(TipoTemplateNfse.values());
    }

    @Override
    public AbstractFacade getFacede() {
        return templateNfseFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/template-nfse/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
