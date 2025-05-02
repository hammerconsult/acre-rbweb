/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtividadeEconomica;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AtividadeEconomicaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "atividadeEconomicaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoAtividadeEconomica", pattern = "/tributario/atividadeeconomica/novo/", viewId = "/faces/tributario/cadastromunicipal/atividadeeconomica/edita.xhtml"),
        @URLMapping(id = "editarAtividadeEconomica", pattern = "/tributario/atividadeeconomica/editar/#{atividadeEconomicaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/atividadeeconomica/edita.xhtml"),
        @URLMapping(id = "listarAtividadeEconomica", pattern = "/tributario/atividadeeconomica/listar/", viewId = "/faces/tributario/cadastromunicipal/atividadeeconomica/lista.xhtml"),
        @URLMapping(id = "verAtividadeEconomica", pattern = "/tributario/atividadeeconomica/ver/#{atividadeEconomicaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/atividadeeconomica/visualizar.xhtml")
})
public class AtividadeEconomicaControlador extends PrettyControlador<AtividadeEconomica> implements Serializable, CRUD {

    @EJB
    private AtividadeEconomicaFacade facade;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public AtividadeEconomicaControlador() {
        super(AtividadeEconomica.class);
    }

    @URLAction(mappingId = "novoAtividadeEconomica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarAtividadeEconomica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verAtividadeEconomica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/atividadeeconomica/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }
}
