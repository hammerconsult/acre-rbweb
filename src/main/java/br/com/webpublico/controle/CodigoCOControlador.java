package br.com.webpublico.controle;

import br.com.webpublico.entidades.CodigoCO;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CodigoCOFacade;
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
    @URLMapping(id = "novo-codigo-co", pattern = "/codigo-co/novo/", viewId = "/faces/financeiro/orcamentario/codigo-co/edita.xhtml"),
    @URLMapping(id = "editar-codigo-co", pattern = "/codigo-co/editar/#{codigoCOControlador.id}/", viewId = "/faces/financeiro/orcamentario/codigo-co/edita.xhtml"),
    @URLMapping(id = "ver-codigo-co", pattern = "/codigo-co/ver/#{codigoCOControlador.id}/", viewId = "/faces/financeiro/orcamentario/codigo-co/visualizar.xhtml"),
    @URLMapping(id = "listar-codigo-co", pattern = "/codigo-co/listar/", viewId = "/faces/financeiro/orcamentario/codigo-co/lista.xhtml")
})
public class CodigoCOControlador extends PrettyControlador<CodigoCO> implements Serializable, CRUD {
    @EJB
    private CodigoCOFacade facade;

    public CodigoCOControlador() {
        super(CodigoCO.class);
    }

    @URLAction(mappingId = "novo-codigo-co", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-codigo-co", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-codigo-co", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/codigo-co/";
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
