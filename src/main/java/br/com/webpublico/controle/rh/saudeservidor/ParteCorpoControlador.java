package br.com.webpublico.controle.rh.saudeservidor;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.saudeservidor.ParteCorpo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.saudeservidor.ParteCorpoFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Alex
 * @since 22/09/2016 10:39
 */
@ManagedBean(name = "parteCorpoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaParteCorpo", pattern = "/parte-corpo/novo/", viewId = "/faces/rh/saudeservidor/parte-corpo/edita.xhtml"),
    @URLMapping(id = "editarParteCorpo", pattern = "/parte-corpo/editar/#{parteCorpoControlador.id}/", viewId = "/faces/rh/saudeservidor/parte-corpo/edita.xhtml"),
    @URLMapping(id = "listarParteCorpo", pattern = "/parte-corpo/listar/", viewId = "/faces/rh/saudeservidor/parte-corpo/lista.xhtml"),
    @URLMapping(id = "verParteCorpo", pattern = "/parte-corpo/ver/#{parteCorpoControlador.id}/", viewId = "/faces/rh/saudeservidor/parte-corpo/visualizar.xhtml")
})
public class ParteCorpoControlador extends PrettyControlador<ParteCorpo> implements Serializable, CRUD {

    @EJB
    private ParteCorpoFacade parteCorpoFacade;

    public ParteCorpoControlador() {
        super(ParteCorpo.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parte-corpo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return parteCorpoFacade;
    }

    @URLAction(mappingId = "novaParteCorpo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verParteCorpo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarParteCorpo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }
}
