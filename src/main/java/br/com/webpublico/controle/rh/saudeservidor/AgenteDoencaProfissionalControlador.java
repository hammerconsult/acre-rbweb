package br.com.webpublico.controle.rh.saudeservidor;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.saudeservidor.AgenteDoencaProfissional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.saudeservidor.AgenteDoencaProfissionalFacade;
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
 * @since 22/09/2016 15:20
 */
@ManagedBean(name = "agenteDoencaProfissionalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaAgenteDoenca", pattern = "/agente-causador-doenca-profissional/novo/", viewId = "/faces/rh/saudeservidor/agente-doenca-profissional/edita.xhtml"),
    @URLMapping(id = "editarAgenteDoenca", pattern = "/agente-causador-doenca-profissional/editar/#{agenteDoencaProfissionalControlador.id}/", viewId = "/faces/rh/saudeservidor/agente-doenca-profissional/edita.xhtml"),
    @URLMapping(id = "listarAgenteDoenca", pattern = "/agente-causador-doenca-profissional/listar/", viewId = "/faces/rh/saudeservidor/agente-doenca-profissional/lista.xhtml"),
    @URLMapping(id = "verAgenteDoenca", pattern = "/agente-causador-doenca-profissional/ver/#{agenteDoencaProfissionalControlador.id}/", viewId = "/faces/rh/saudeservidor/agente-doenca-profissional/visualizar.xhtml")
})
public class AgenteDoencaProfissionalControlador extends PrettyControlador<AgenteDoencaProfissional> implements Serializable, CRUD {

    @EJB
    private AgenteDoencaProfissionalFacade agenteDoencaProfissionalFacade;

    public AgenteDoencaProfissionalControlador() {
        super(AgenteDoencaProfissional.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/agente-causador-doenca-profissional/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return agenteDoencaProfissionalFacade;
    }

    @URLAction(mappingId = "novaAgenteDoenca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verAgenteDoenca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarAgenteDoenca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
