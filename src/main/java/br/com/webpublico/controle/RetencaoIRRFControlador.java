/*
 * Codigo gerado automaticamente em Tue Jan 03 15:21:40 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.RetencaoIRRF;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RetencaoIRRFFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "retencaoIRRFControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRetencaoIRRF", pattern = "/rh/retencao-do-irrf/novo/", viewId = "/faces/rh/administracaodepagamento/retencaoirrf/edita.xhtml"),
        @URLMapping(id = "listaRetencaoIRRF", pattern = "/rh/retencao-do-irrf/listar/", viewId = "/faces/rh/administracaodepagamento/retencaoirrf/lista.xhtml"),
        @URLMapping(id = "verRetencaoIRRF", pattern = "/rh/retencao-do-irrf/ver/#{retencaoIRRFControlador.id}/", viewId = "/faces/rh/administracaodepagamento/retencaoirrf/visualizar.xhtml"),
        @URLMapping(id = "editarRetencaoIRRF", pattern = "/rh/retencao-do-irrf/editar/#{retencaoIRRFControlador.id}/", viewId = "/faces/rh/administracaodepagamento/retencaoirrf/edita.xhtml"),
})
public class RetencaoIRRFControlador extends PrettyControlador<RetencaoIRRF> implements Serializable, CRUD {

    @EJB
    private RetencaoIRRFFacade retencaoIRRFFacade;

    public RetencaoIRRFControlador() {
        super(RetencaoIRRF.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return retencaoIRRFFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/retencao-do-irrf/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoRetencaoIRRF", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verRetencaoIRRF", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarRetencaoIRRF", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }
}
