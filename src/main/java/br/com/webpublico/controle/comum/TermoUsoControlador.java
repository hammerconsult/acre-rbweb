package br.com.webpublico.controle.comum;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.comum.TermoUso;
import br.com.webpublico.enums.Sistema;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.comum.TermoUsoFacade;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "termoUsoNovo", pattern = "/termo-uso/novo/", viewId = "/faces/comum/termouso/edita.xhtml"),
    @URLMapping(id = "termoUsoListar", pattern = "/termo-uso/listar/", viewId = "/faces/comum/termouso/lista.xhtml"),
    @URLMapping(id = "termoUsoEditar", pattern = "/termo-uso/editar/#{termoUsoControlador.id}/", viewId = "/faces/comum/termouso/edita.xhtml"),
    @URLMapping(id = "termoUsoVer", pattern = "/termo-uso/ver/#{termoUsoControlador.id}/", viewId = "/faces/comum/termouso/visualizar.xhtml"),
})
public class TermoUsoControlador extends PrettyControlador<TermoUso> implements CRUD {

    @EJB
    private TermoUsoFacade termoUsoFacade;

    public TermoUsoControlador() {
        super(TermoUso.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return termoUsoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/termo-uso/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "termoUsoNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(new Date());
    }

    @Override
    @URLAction(mappingId = "termoUsoEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "termoUsoVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public List<SelectItem> getSistemas() {
        return Util.getListSelectItem(Sistema.values());
    }

    @Override
    public void salvar() {
        super.salvar();
    }
}
