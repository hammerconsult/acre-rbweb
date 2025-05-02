package br.com.webpublico.dte.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.dte.entidades.NotificacaoContribuinteDte;
import br.com.webpublico.dte.facades.NotificacaoContribuinteDteFacade;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarNotificacaoContribuinteDte", pattern = "/dte/notificacao-contribuinte/listar/",
        viewId = "/faces/tributario/dte/notificacaocontribuinte/lista.xhtml"),
    @URLMapping(id = "verNotificacaoContribuinteDte", pattern = "/dte/notificacao-contribuinte/ver/#{notificacaoContribuinteDteControlador.id}/",
        viewId = "/faces/tributario/dte/notificacaocontribuinte/visualizar.xhtml")
})
public class NotificacaoContribuinteDteControlador extends PrettyControlador<NotificacaoContribuinteDte> implements CRUD {

    @EJB
    private NotificacaoContribuinteDteFacade facade;

    public NotificacaoContribuinteDteControlador() {
        super(NotificacaoContribuinteDte.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/dte/notificacao-contribuinte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verNotificacaoContribuinteDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }
}
