package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.DfeNacional;
import br.com.webpublico.nfse.facades.DfeNacionalFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "dfeNacionalListar",
            pattern = "/nfse/dfe-nacional/listar/",
            viewId = "/faces/tributario/nfse/dfe-nacional/lista.xhtml"),
        @URLMapping(id = "dfeNacionalVer",
            pattern = "/nfse/dfe-nacional/ver/#{dfeNacionalControlador.id}/",
            viewId = "/faces/tributario/nfse/dfe-nacional/visualizar.xhtml"),
})
public class DfeNacionalControlador extends PrettyControlador<DfeNacional> implements CRUD {

    @EJB
    private DfeNacionalFacade facade;

    public DfeNacionalControlador() {
        super(DfeNacional.class);
    }

    @Override
    public AbstractFacade<DfeNacional> getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/dfe-nacional/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "dfeNacionalVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

}
