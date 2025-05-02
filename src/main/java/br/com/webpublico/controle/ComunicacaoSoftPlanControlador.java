package br.com.webpublico.controle;

import br.com.webpublico.entidades.ComunicacaoSoftPlan;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ComunicacaoSoftPlanFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "listarComunicacaoSoftPlan",
            pattern = "/tributario/comunicacao-softplan/listar/",
            viewId = "/faces/tributario/comunicacaosoftplan/lista.xhtml"),
    @URLMapping(id = "verComunicacaoSoftPlan",
        pattern = "/tributario/comunicacao-softplan/ver/#{comunicacaoSoftPlanControlador.id}/",
        viewId = "/faces/tributario/comunicacaosoftplan/visualizar.xhtml")
})
public class ComunicacaoSoftPlanControlador extends PrettyControlador<ComunicacaoSoftPlan> implements Serializable, CRUD {

    @EJB
    private ComunicacaoSoftPlanFacade facade;

    public ComunicacaoSoftPlanControlador() {
        super(ComunicacaoSoftPlan.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/comunicacao-softplan/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verComunicacaoSoftPlan", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

}
