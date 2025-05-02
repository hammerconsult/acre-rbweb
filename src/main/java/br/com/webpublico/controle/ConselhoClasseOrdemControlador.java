/*
 * Codigo gerado automaticamente em Wed Dec 07 15:56:19 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConselhoClasseOrdem;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConselhoClasseOrdemFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "conselhoClasseOrdemControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConselhoClasseOrdem", pattern = "/conselhoclasseordem/novo/", viewId = "/faces/rh/administracaodepagamento/conselhoclasseordem/edita.xhtml"),
    @URLMapping(id = "editarConselhoClasseOrdem", pattern = "/conselhoclasseordem/editar/#{conselhoClasseOrdemControlador.id}/", viewId = "/faces/rh/administracaodepagamento/conselhoclasseordem/edita.xhtml"),
    @URLMapping(id = "listarConselhoClasseOrdem", pattern = "/conselhoclasseordem/listar/", viewId = "/faces/rh/administracaodepagamento/conselhoclasseordem/lista.xhtml"),
    @URLMapping(id = "verConselhoClasseOrdem", pattern = "/conselhoclasseordem/ver/#{conselhoClasseOrdemControlador.id}/", viewId = "/faces/rh/administracaodepagamento/conselhoclasseordem/visualizar.xhtml")
})
public class ConselhoClasseOrdemControlador extends PrettyControlador<ConselhoClasseOrdem> implements Serializable, CRUD {

    @EJB
    private ConselhoClasseOrdemFacade conselhoClasseOrdemFacade;

    public ConselhoClasseOrdemControlador() {
        super(ConselhoClasseOrdem.class);
    }

    public ConselhoClasseOrdemFacade getFacade() {
        return conselhoClasseOrdemFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return conselhoClasseOrdemFacade;
    }

    @URLAction(mappingId = "novaConselhoClasseOrdem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verConselhoClasseOrdem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarConselhoClasseOrdem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/conselhoclasseordem/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
