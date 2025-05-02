package br.com.webpublico.controle.rh.saudeservidor;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.saudeservidor.AgenteAcidenteTrabalho;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.saudeservidor.AgenteAcidenteTrabalhoFacade;
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
 * @since 22/09/2016 14:43
 */
@ManagedBean(name = "agenteAcidenteTrabalhoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAgenteAcidenteTrabalho", pattern = "/agente-causador-acidente-trabalho/novo/", viewId = "/faces/rh/saudeservidor/agente-acidente-trabalho/edita.xhtml"),
    @URLMapping(id = "editarAgenteAcidenteTrabalho", pattern = "/agente-causador-acidente-trabalho/editar/#{agenteAcidenteTrabalhoControlador.id}/", viewId = "/faces/rh/saudeservidor/agente-acidente-trabalho/edita.xhtml"),
    @URLMapping(id = "listarAgenteAcidenteTrabalho", pattern = "/agente-causador-acidente-trabalho/listar/", viewId = "/faces/rh/saudeservidor/agente-acidente-trabalho/lista.xhtml"),
    @URLMapping(id = "verAgenteAcidenteTrabalho", pattern = "/agente-causador-acidente-trabalho/ver/#{agenteAcidenteTrabalhoControlador.id}/", viewId = "/faces/rh/saudeservidor/agente-acidente-trabalho/visualizar.xhtml")
})
public class AgenteAcidenteTrabalhoControlador extends PrettyControlador<AgenteAcidenteTrabalho> implements Serializable, CRUD {

    @EJB
    private AgenteAcidenteTrabalhoFacade agenteAcidenteTrabalhoFacade;

    public AgenteAcidenteTrabalhoControlador() {
        super(AgenteAcidenteTrabalho.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/agente-causador-acidente-trabalho/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return agenteAcidenteTrabalhoFacade;
    }

    @URLAction(mappingId = "novoAgenteAcidenteTrabalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verAgenteAcidenteTrabalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarAgenteAcidenteTrabalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
