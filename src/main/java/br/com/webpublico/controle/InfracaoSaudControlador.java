package br.com.webpublico.controle;

import br.com.webpublico.entidades.InfracaoSaud;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.InfracaoSaudFacade;
import br.com.webpublico.negocios.MotoristaSaudFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Pedro
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarInfracaoSaud",
        pattern = "/tributario/saud/infracao-saud/listar/",
        viewId = "/faces/tributario/saud/infracaosaud/lista.xhtml"),
    @URLMapping(id = "novaInfracaoSaud",
        pattern = "/tributario/saud/infracao-saud/novo/",
        viewId = "/faces/tributario/saud/infracaosaud/edita.xhtml"),
    @URLMapping(id = "editarInfracaoSaud",
        pattern = "/tributario/saud/infracao-saud/editar/#{infracaoSaudControlador.id}/",
        viewId = "/faces/tributario/saud/infracaosaud/edita.xhtml"),
    @URLMapping(id = "verInfracaoSaud",
        pattern = "/tributario/saud/infracao-saud/ver/#{infracaoSaudControlador.id}/",
        viewId = "/faces/tributario/saud/infracaosaud/visualizar.xhtml")
})
public class InfracaoSaudControlador extends PrettyControlador<InfracaoSaud> implements Serializable, CRUD {

    @EJB
    private InfracaoSaudFacade facade;

    public InfracaoSaudControlador() {
        super(InfracaoSaud.class);
    }

    @Override
    public InfracaoSaudFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/saud/infracao-saud/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaInfracaoSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarInfracaoSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verInfracaoSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }


}
