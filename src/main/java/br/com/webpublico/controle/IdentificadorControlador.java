package br.com.webpublico.controle;

import br.com.webpublico.entidades.Identificador;
import br.com.webpublico.enums.Situacao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.IdentificadorFacade;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mateus on 31/07/17.
 */
@ManagedBean(name = "identificadorControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-identificador", pattern = "/identificador/novo/", viewId = "/faces/financeiro/conciliacao/identificador/edita.xhtml"),
    @URLMapping(id = "editar-identificador", pattern = "/identificador/editar/#{identificadorControlador.id}/", viewId = "/faces/financeiro/conciliacao/identificador/edita.xhtml"),
    @URLMapping(id = "listar-identificador", pattern = "/identificador/listar/", viewId = "/faces/financeiro/conciliacao/identificador/lista.xhtml"),
    @URLMapping(id = "ver-identificador", pattern = "/identificador/ver/#{identificadorControlador.id}/", viewId = "/faces/financeiro/conciliacao/identificador/visualizar.xhtml")
})
public class IdentificadorControlador extends PrettyControlador<Identificador> implements Serializable, CRUD {

    @EJB
    private IdentificadorFacade identificadorFacade;

    public IdentificadorControlador() {
        super(Identificador.class);
    }

    @URLAction(mappingId = "novo-identificador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setData(identificadorFacade.getSistemaFacade().getDataOperacao());
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItemSemCampoVazio(Situacao.values());
    }

    @URLAction(mappingId = "ver-identificador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-identificador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/identificador/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return identificadorFacade;
    }
}
