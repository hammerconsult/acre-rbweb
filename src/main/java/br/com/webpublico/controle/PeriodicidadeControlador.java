/*
 * Codigo gerado automaticamente em Wed Apr 13 10:23:12 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Periodicidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PeriodicidadeFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "periodicidadeControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoperiodicidade", pattern = "/periodicidade/novo/", viewId = "/faces/financeiro/ppa/periodicidade/edita.xhtml"),
        @URLMapping(id = "editarperiodicidade", pattern = "/periodicidade/editar/#{periodicidadeControlador.id}/", viewId = "/faces/financeiro/ppa/periodicidade/edita.xhtml"),
        @URLMapping(id = "verperiodicidade", pattern = "/periodicidade/ver/#{periodicidadeControlador.id}/", viewId = "/faces/financeiro/ppa/periodicidade/visualizar.xhtml"),
        @URLMapping(id = "listarperiodicidade", pattern = "/periodicidade/listar/", viewId = "/faces/financeiro/ppa/periodicidade/lista.xhtml")
})
public class PeriodicidadeControlador extends PrettyControlador<Periodicidade> implements Serializable, CRUD {

    @EJB
    private PeriodicidadeFacade periodicidadeFacade;

    public PeriodicidadeControlador() {
        super(Periodicidade.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return periodicidadeFacade;
    }

    @URLAction(mappingId = "novoperiodicidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verperiodicidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarperiodicidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/periodicidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
