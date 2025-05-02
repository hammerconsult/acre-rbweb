/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoIngresso;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoIngressoBaixa;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoIngressoFacade;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author Claudio
 */
@ManagedBean(name = "tipoIngressoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-tipo-ingresso-bens", pattern = "/contabil/tipo-ingresso-bens/novo/", viewId = "/faces/financeiro/orcamentario/bens/tipoingresso/edita.xhtml"),
        @URLMapping(id = "editar-tipo-ingresso-bens", pattern = "/contabil/tipo-ingresso-bens/editar/#{tipoIngressoControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/tipoingresso/edita.xhtml"),
        @URLMapping(id = "ver-tipo-ingresso-bens", pattern = "/contabil/tipo-ingresso-bens/ver/#{tipoIngressoControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/tipoingresso/visualizar.xhtml"),
        @URLMapping(id = "listar-tipo-ingresso-bens", pattern = "/contabil/tipo-ingresso-bens/listar/", viewId = "/faces/financeiro/orcamentario/bens/tipoingresso/lista.xhtml"),})

public class TipoIngressoControlador extends PrettyControlador<TipoIngresso> implements Serializable, CRUD {

    @EJB
    private TipoIngressoFacade tipoIngressoFacade;

    public TipoIngressoControlador() {
        super(TipoIngresso.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoIngressoFacade;
    }


    @Override
    public String getCaminhoPadrao() {
        return "/contabil/tipo-ingresso-bens/";
    }


    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-tipo-ingresso-bens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();

    }

    @URLAction(mappingId = "ver-tipo-ingresso-bens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-tipo-ingresso-bens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<SelectItem> getTiposBens() {
        return Util.getListSelectItem(Arrays.asList(TipoBem.values()));
    }

    public List<SelectItem> getCategoriasTipoIngresso() {
        return Util.getListSelectItem(TipoIngressoBaixa.buscarTiposIngresso());
    }
}
