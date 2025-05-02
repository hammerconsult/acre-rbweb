/*
 * Codigo gerado automaticamente em Thu Apr 05 09:30:03 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoInterveniente;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoIntervenienteFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "tipoIntervenienteControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novotipointerveniente", pattern = "/tipointerveniente/novo/", viewId = "/faces/financeiro/convenios/tipointerveniente/edita.xhtml"),
        @URLMapping(id = "editatipointerveniente", pattern = "/tipointerveniente/editar/#{tipoIntervenienteControlador.id}/", viewId = "/faces/financeiro/convenios/tipointerveniente/edita.xhtml"),
        @URLMapping(id = "vertipointerveniente", pattern = "/tipointerveniente/ver/#{tipoIntervenienteControlador.id}/", viewId = "/faces/financeiro/convenios/tipointerveniente/visualizar.xhtml"),
        @URLMapping(id = "listartipointerveniente", pattern = "/tipointerveniente/listar/", viewId = "/faces/financeiro/convenios/tipointerveniente/lista.xhtml")
})
public class TipoIntervenienteControlador extends PrettyControlador<TipoInterveniente> implements Serializable, CRUD {

    @EJB
    private TipoIntervenienteFacade tipoIntervenienteFacade;

    public TipoIntervenienteControlador() {
        super(TipoInterveniente.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoIntervenienteFacade;
    }

    @URLAction(mappingId = "novotipointerveniente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "vertipointerveniente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editatipointerveniente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipointerveniente/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
