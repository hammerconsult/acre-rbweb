package br.com.webpublico.controle;

import br.com.webpublico.entidades.CancelamentoObjetoFrota;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CancelamentoObjetoFrotaFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Octavio
 */

@ManagedBean(name = "cancelamentoObjetoFrotaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCancelamentoObjetoFrota", pattern = "/frota/cancelamento/novo/", viewId = "/faces/administrativo/frota/cancelamento/edita.xhtml"),
    @URLMapping(id = "editarCancelamentoObjetoFrota", pattern = "/frota/cancelamento/editar/#{cancelamentoObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/cancelamento/edita.xhtml"),
    @URLMapping(id = "listarCancelamentoObjetoFrota", pattern = "/frota/cancelamento/listar/", viewId = "/faces/administrativo/frota/cancelamento/lista.xhtml"),
    @URLMapping(id = "verCancelamentoObjetoFrota", pattern = "/frota/cancelamento/ver/#{cancelamentoObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/cancelamento/visualizar.xhtml")
})

public class CancelamentoObjetoFrotaControlador extends PrettyControlador<CancelamentoObjetoFrota> implements Serializable, CRUD {

    @EJB
    private CancelamentoObjetoFrotaFacade facade;

    public CancelamentoObjetoFrotaControlador() {
        super(CancelamentoObjetoFrota.class);
    }

    @URLAction(mappingId = "novoCancelamentoObjetoFrota", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCanceladoEm(new Date());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @URLAction(mappingId = "verCancelamentoObjetoFrota", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarCancelamentoObjetoFrota", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/cancelamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }
}
