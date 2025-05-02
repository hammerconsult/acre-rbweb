package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.TipoManual;
import br.com.webpublico.nfse.facades.TipoManualFacade;
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
 * Created by mateus on 16/10/17.
 */
@ManagedBean(name = "tipoManualControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-tipo-manual", pattern = "/tipo-manual/novo/", viewId = "/faces/tributario/nfse/tipomanual/edita.xhtml"),
    @URLMapping(id = "editar-tipo-manual", pattern = "/tipo-manual/editar/#{tipoManualControlador.id}/", viewId = "/faces/tributario/nfse/tipomanual/edita.xhtml"),
    @URLMapping(id = "ver-tipo-manual", pattern = "/tipo-manual/ver/#{tipoManualControlador.id}/", viewId = "/faces/tributario/nfse/tipomanual/visualizar.xhtml"),
    @URLMapping(id = "listar-tipo-manual", pattern = "/tipo-manual/listar/", viewId = "/faces/tributario/nfse/tipomanual/lista.xhtml")
})
public class TipoManualControlador extends PrettyControlador<TipoManual> implements Serializable, CRUD {

    @EJB
    private TipoManualFacade tipoManualFacade;

    public TipoManualControlador() {
        super(TipoManual.class);
    }

    @URLAction(mappingId = "novo-tipo-manual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-tipo-manual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-tipo-manual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }


    @Override
    public String getCaminhoPadrao() {
        return "/tipo-manual/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoManualFacade;
    }

    public List<SelectItem> getTiposAnexo() {
        return Util.getListSelectItem(TipoManual.TipoAnexo.values());
    }
}
