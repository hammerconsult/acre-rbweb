/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.EsferaGoverno;
import br.com.webpublico.entidades.contabil.emendagoverno.EmendaGovernoParlamentar;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EsferaGovernoFacade;
import br.com.webpublico.interfaces.CRUD;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

/**
 * @author terminal3
 */
@ManagedBean(name = "esferaGovernoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-esfera-governo", pattern = "/esfera-governo/novo/", viewId = "/faces/tributario/cadastromunicipal/esferagoverno/edita.xhtml"),
    @URLMapping(id = "editar-esfera-governo", pattern = "/esfera-governo/editar/#{esferaGovernoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/esferagoverno/edita.xhtml"),
    @URLMapping(id = "ver-esfera-governo", pattern = "/esfera-governo/ver/#{esferaGovernoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/esferagoverno/visualizar.xhtml"),
    @URLMapping(id = "listar-esfera-governo", pattern = "/esfera-governo/listar/", viewId = "/faces/tributario/cadastromunicipal/esferagoverno/lista.xhtml")
})
public class EsferaGovernoControlador extends PrettyControlador<EsferaGoverno> implements Serializable, CRUD {

    @EJB
    private EsferaGovernoFacade esferaGovernoFacade;

    public EsferaGovernoControlador() {
        super(EsferaGoverno.class);
    }

    public EsferaGovernoFacade getFacade() {
        return esferaGovernoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return esferaGovernoFacade;
    }

    @URLAction(mappingId = "novo-esfera-governo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-esfera-governo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-esfera-governo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/esfera-governo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getEsferas() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (EsferaGoverno eg : esferaGovernoFacade.lista()) {
            retorno.add(new SelectItem(eg, eg.toString()));
        }
        return retorno;
    }
}
