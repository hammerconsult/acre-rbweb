package br.com.webpublico.controle;

import br.com.webpublico.entidades.Procedencia;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProcedenciaFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
@ManagedBean(name = "procedenciaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProcedencia", pattern = "/procedencia/novo/", viewId = "/faces/funeral/procedencia/edita.xhtml"),
    @URLMapping(id = "editarProcedencia", pattern = "/procedencia/editar/#{procedenciaControlador.id}/", viewId = "/faces/funeral/procedencia/edita.xhtml"),
    @URLMapping(id = "listarProcedencia", pattern = "/procedencia/listar/", viewId = "/faces/funeral/procedencia/lista.xhtml"),
    @URLMapping(id = "verProcedencia", pattern = "/procedencia/ver/#{procedenciaControlador.id}/", viewId = "/faces/funeral/procedencia/visualizar.xhtml")
})
public class ProcedenciaControlador extends PrettyControlador<Procedencia> implements Serializable, CRUD {

    @EJB
    private ProcedenciaFacade procedenciaFacade;

    public ProcedenciaControlador() {
        super(Procedencia.class);
    }

    @URLAction(mappingId = "novoProcedencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verProcedencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarProcedencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/procedencia/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return procedenciaFacade;
    }
}
