package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.ComunicadoNotaPremiada;
import br.com.webpublico.nfse.domain.UsuarioNotaPremiada;
import br.com.webpublico.nfse.facades.ComunicadoNotaPremiadaFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "comunicadoNotaPremiadaNovo", pattern = "/nfse/nota-premiada/comunicado/novo/", viewId = "/faces/tributario/nfse/notapremiada/comunicado/edita.xhtml"),
    @URLMapping(id = "comunicadoNotaPremiadaListar", pattern = "/nfse/nota-premiada/comunicado/listar/", viewId = "/faces/tributario/nfse/notapremiada/comunicado/lista.xhtml"),
    @URLMapping(id = "comunicadoNotaPremiadaEditar", pattern = "/nfse/nota-premiada/comunicado/editar/#{comunicadoNotaPremiadaControlador.id}/", viewId = "/faces/tributario/nfse/notapremiada/comunicado/edita.xhtml"),
    @URLMapping(id = "comunicadoNotaPremiadaVer", pattern = "/nfse/nota-premiada/comunicado/ver/#{comunicadoNotaPremiadaControlador.id}/", viewId = "/faces/tributario/nfse/notapremiada/comunicado/visualizar.xhtml"),
})
public class ComunicadoNotaPremiadaControlador extends PrettyControlador<ComunicadoNotaPremiada> implements CRUD {

    @EJB
    private ComunicadoNotaPremiadaFacade facade;
    private UsuarioNotaPremiada usuarioNotaPremiada;

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/nota-premiada/comunicado/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    public ComunicadoNotaPremiadaControlador() {
        super(ComunicadoNotaPremiada.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public UsuarioNotaPremiada getUsuarioNotaPremiada() {
        return usuarioNotaPremiada;
    }

    public void setUsuarioNotaPremiada(UsuarioNotaPremiada usuarioNotaPremiada) {
        this.usuarioNotaPremiada = usuarioNotaPremiada;
    }

    @Override
    @URLAction(mappingId = "comunicadoNotaPremiadaNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "comunicadoNotaPremiadaEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "comunicadoNotaPremiadaVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }
}
