/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoBaixaBens;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoIngressoBaixa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoBaixaBensFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Claudio
 */
@ManagedBean(name = "tipoBaixaBensControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-tipo-baixa-bens", pattern = "/contabil/tipo-baixa-bens/novo/", viewId = "/faces/financeiro/orcamentario/bens/tipobaixabens/edita.xhtml"),
    @URLMapping(id = "editar-tipo-baixa-bens", pattern = "/contabil/tipo-baixa-bens/editar/#{tipoBaixaBensControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/tipobaixabens/edita.xhtml"),
    @URLMapping(id = "ver-tipo-baixa-bens", pattern = "/contabil/tipo-baixa-bens/ver/#{tipoBaixaBensControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/tipobaixabens/visualizar.xhtml"),
    @URLMapping(id = "listar-tipo-baixa-bens", pattern = "/contabil/tipo-baixa-bens/listar/", viewId = "/faces/financeiro/orcamentario/bens/tipobaixabens/lista.xhtml"),})

public class TipoBaixaBensControlador extends PrettyControlador<TipoBaixaBens> implements Serializable, CRUD {

    @EJB
    private TipoBaixaBensFacade tipoBaixaBensFacade;

    public TipoBaixaBensControlador() {
        super(TipoBaixaBens.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoBaixaBensFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabil/tipo-baixa-bens/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-tipo-baixa-bens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-tipo-baixa-bens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-tipo-baixa-bens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<SelectItem> getTiposBens() {
        return Util.getListSelectItem(TipoBem.values());
    }

    public List<SelectItem> getTiposDeBaixa() {
        return Util.getListSelectItem(TipoIngressoBaixa.buscarTiposBaixa());
    }
}
