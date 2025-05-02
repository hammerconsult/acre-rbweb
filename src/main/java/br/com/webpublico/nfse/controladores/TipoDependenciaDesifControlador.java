package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.TipoDependenciaDesif;
import br.com.webpublico.nfse.facades.TipoDependenciaDesifFacade;
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
    @URLMapping(id = "listarTipoDependenciaDesif", pattern = "/tributario/nfse/tipo-dependencia-desif/listar/", viewId = "/faces/tributario/nfse/tipo-dependencia-desif/lista.xhtml"),
    @URLMapping(id = "verTipoDependenciaDesif", pattern = "/tributario/nfse/tipo-dependencia-desif/ver/#{tipoDependenciaDesifControlador.id}/", viewId = "/faces/tributario/nfse/tipo-dependencia-desif/visualizar.xhtml")
})
public class TipoDependenciaDesifControlador extends PrettyControlador<TipoDependenciaDesif> implements Serializable, CRUD {

    @EJB
    private TipoDependenciaDesifFacade facade;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public TipoDependenciaDesifControlador() {
        super(TipoDependenciaDesif.class);
    }

    @URLAction(mappingId = "verTipoDependenciaDesif", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/nfse/tipo-dependencia-desif/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
