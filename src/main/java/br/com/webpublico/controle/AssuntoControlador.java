package br.com.webpublico.controle;

import br.com.webpublico.entidades.Assunto;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AssuntoFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "assuntoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoAssunto", pattern = "/assunto/novo/", viewId = "/faces/tributario/cadastromunicipal/assunto/edita.xhtml"),
        @URLMapping(id = "editarAssunto", pattern = "/assunto/editar/#{assuntoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/assunto/edita.xhtml"),
        @URLMapping(id = "listarAssunto", pattern = "/assunto/listar/", viewId = "/faces/tributario/cadastromunicipal/assunto/lista.xhtml"),
        @URLMapping(id = "verAssunto", pattern = "/assunto/ver/#{assuntoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/assunto/visualizar.xhtml")
})
public class AssuntoControlador extends PrettyControlador<Assunto> implements Serializable, CRUD {

    @EJB
    private AssuntoFacade assuntoFacade;

    public AssuntoControlador() {
        super(Assunto.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return assuntoFacade;
    }

    @URLAction(mappingId = "novoAssunto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verAssunto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarAssunto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/assunto/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
