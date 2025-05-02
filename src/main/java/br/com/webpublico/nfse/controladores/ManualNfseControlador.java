package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.ManualNfse;
import br.com.webpublico.nfse.domain.TipoManual;
import br.com.webpublico.nfse.facades.ManualNfseFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mateus on 16/10/17.
 */
@ManagedBean(name = "manualNfseControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-manual-nfse", pattern = "/manual/novo/", viewId = "/faces/tributario/nfse/manual/edita.xhtml"),
    @URLMapping(id = "editar-manual-nfse", pattern = "/manual/editar/#{manualNfseControlador.id}/", viewId = "/faces/tributario/nfse/manual/edita.xhtml"),
    @URLMapping(id = "ver-manual-nfse", pattern = "/manual/ver/#{manualNfseControlador.id}/", viewId = "/faces/tributario/nfse/manual/visualizar.xhtml"),
    @URLMapping(id = "listar-manual-nfse", pattern = "/manual/listar/", viewId = "/faces/tributario/nfse/manual/lista.xhtml")
})
public class ManualNfseControlador extends PrettyControlador<ManualNfse> implements Serializable, CRUD {

    @EJB
    private ManualNfseFacade manualNfseFacade;

    public ManualNfseControlador() {
        super(ManualNfse.class);
    }

    @URLAction(mappingId = "novo-manual-nfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-manual-nfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-manual-nfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<TipoManual> completarTiposManuais(String filtro) {
        return manualNfseFacade.getTipoManualFacade().listaFiltrando(filtro.trim(), "descricao");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/manual/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return manualNfseFacade;
    }
}
