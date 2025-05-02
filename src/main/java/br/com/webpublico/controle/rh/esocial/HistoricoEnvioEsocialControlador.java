package br.com.webpublico.controle.rh.esocial;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.esocial.HistoricoEnvioEsocial;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.esocial.HistoricoEnvioEsocialFacade;
import br.com.webpublico.util.esocial.UtilEsocial;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "historicoEnvioEsocialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "historico-esocial-lista", pattern = "/rh/e-social/historico-envio/listar/", viewId = "/faces/rh/esocial/historico/lista.xhtml"),
    @URLMapping(id = "ver-historico-esocial", pattern = "/rh/e-social/historico-envio/ver/#{historicoEnvioEsocialControlador.id}/", viewId = "/faces/rh/esocial/historico/visualizar.xhtml"),
})
public class HistoricoEnvioEsocialControlador extends PrettyControlador<HistoricoEnvioEsocial> implements Serializable, CRUD {

    @EJB
    private HistoricoEnvioEsocialFacade historicoEnvioEsocialFacade;

    public HistoricoEnvioEsocialControlador() {
        super(HistoricoEnvioEsocial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return historicoEnvioEsocialFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/e-social/historico-envio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "ver-historico-esocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public boolean isEventoPagamento() {
        return UtilEsocial.isEventoPagamento(selecionado.getTipoClasseESocial());
    }

}
