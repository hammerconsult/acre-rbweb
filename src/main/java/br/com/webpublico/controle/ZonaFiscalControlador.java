package br.com.webpublico.controle;

import br.com.webpublico.entidades.ZonaFiscal;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ZonaFiscalFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoZonaFiscal",
        pattern = "/tributario/zona-fiscal/novo/",
        viewId = "/faces/tributario/cadastromunicipal/zonafiscal/edita.xhtml"),
    @URLMapping(id = "editarZonaFiscal",
        pattern = "/tributario/zona-fiscal/editar/#{zonaFiscalControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/zonafiscal/edita.xhtml"),
    @URLMapping(id = "listarZonaFiscal",
        pattern = "/tributario/zona-fiscal/listar/",
        viewId = "/faces/tributario/cadastromunicipal/zonafiscal/lista.xhtml"),
    @URLMapping(id = "verZonaFiscal",
        pattern = "/tributario/zona-fiscal/ver/#{zonaFiscalControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/zonafiscal/visualizar.xhtml")
})
public class ZonaFiscalControlador extends PrettyControlador<ZonaFiscal> implements Serializable, CRUD {

    @EJB
    private ZonaFiscalFacade zonaFiscalFacade;

    public ZonaFiscalControlador() {
        super(ZonaFiscal.class);
    }

    @URLAction(mappingId = "novoZonaFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verZonaFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarZonaFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public AbstractFacade getFacede() {
        return zonaFiscalFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/zona-fiscal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<ZonaFiscal> completarZonasFiscais(String parte) {
        return zonaFiscalFacade.listaFiltrando(parte.trim(), "codigo");
    }

}
