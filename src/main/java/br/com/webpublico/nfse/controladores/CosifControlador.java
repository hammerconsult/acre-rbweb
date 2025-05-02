package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.Cosif;
import br.com.webpublico.nfse.facades.CosifFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "cosifControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "CosifListar", pattern = "/nfse/cosif/listar/", viewId = "/faces/tributario/nfse/cosif/lista.xhtml"),
    @URLMapping(id = "CosifVer", pattern = "/nfse/cosif/ver/#{cosifControlador.id}/", viewId = "/faces/tributario/nfse/cosif/visualizar.xhtml"),
    @URLMapping(id = "CosifNovo", pattern = "/nfse/cosif/novo/", viewId = "/faces/tributario/nfse/cosif/edita.xhtml"),
    @URLMapping(id = "CosifEditar", pattern = "/nfse/cosif/editar/#{cosifControlador.id}/", viewId = "/faces/tributario/nfse/cosif/edita.xhtml")
})
public class CosifControlador extends PrettyControlador<Cosif> implements CRUD {

    @EJB
    private CosifFacade cosifFacade;

    public CosifControlador() {
        super(Cosif.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return cosifFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/cosif/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "CosifNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "CosifVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "CosifEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public void mudouVersao() {
        selecionado.setConta("");
    }
}

