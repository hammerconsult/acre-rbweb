/*
 * Codigo gerado automaticamente em Tue Apr 17 10:59:29 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Tribunal;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TribunalFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "tribunalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-tribunal", pattern = "/tribunal/novo/", viewId = "/faces/financeiro/orcamentario/dividapublica/tribunal/edita.xhtml"),
    @URLMapping(id = "editar-tribunal", pattern = "/tribunal/editar/#{tribunalControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/tribunal/edita.xhtml"),
    @URLMapping(id = "ver-tribunal", pattern = "/tribunal/ver/#{tribunalControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/tribunal/visualizar.xhtml"),
    @URLMapping(id = "listar-tribunal", pattern = "/tribunal/listar/", viewId = "/faces/financeiro/orcamentario/dividapublica/tribunal/lista.xhtml")
})
public class TribunalControlador extends PrettyControlador<Tribunal> implements Serializable, CRUD {

    @EJB
    private TribunalFacade tribunalFacade;

    public TribunalControlador() {
        super(Tribunal.class);
    }

    public TribunalFacade getFacade() {
        return tribunalFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tribunalFacade;
    }

    @URLAction(mappingId = "novo-tribunal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-tribunal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-tribunal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tribunal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
