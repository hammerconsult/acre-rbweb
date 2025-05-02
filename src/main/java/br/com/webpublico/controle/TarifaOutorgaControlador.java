/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.IndiceEconomico;
import br.com.webpublico.entidades.TarifaOutorga;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BairroFacade;
import br.com.webpublico.negocios.TarifaOutorgaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "tarifaOutorgaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaTarifaOutorga", pattern = "/outorga/tarifa/novo/",
        viewId = "/faces/tributario/rbtrans/tarifa/edita.xhtml"),

    @URLMapping(id = "editarTarifaOutorga", pattern = "/outorga/tarifa/editar/#{tarifaOutorgaControlador.id}/",
        viewId = "/faces/tributario/rbtrans/tarifa/edita.xhtml"),

    @URLMapping(id = "listarTarifaOutorga", pattern = "/outorga/tarifa/listar/",
        viewId = "/faces/tributario/rbtrans/tarifa/lista.xhtml"),

    @URLMapping(id = "verTarifaOutorga", pattern = "/outorga/tarifa/ver/#{tarifaOutorgaControlador.id}/",
        viewId = "/faces/tributario/rbtrans/tarifa/visualizar.xhtml")})

public class TarifaOutorgaControlador extends PrettyControlador<TarifaOutorga> implements Serializable, CRUD {

    @EJB
    private TarifaOutorgaFacade tarifaOutorgaFacade;
    private Converter converterTarifa;

    public TarifaOutorgaControlador() {
        super(TarifaOutorga.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tarifaOutorgaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/outorga/tarifa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaTarifaOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verTarifaOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTarifaOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (selecionado.getId() == null) {
            selecionado.setCodigo(getFacede().retornaUltimoCodigoLong());
        }
        super.salvar();
    }

    public Converter getConverterTarifa() {
        if (converterTarifa == null) {
            converterTarifa = new ConverterAutoComplete(TarifaOutorga.class, tarifaOutorgaFacade);
        }
        return converterTarifa;
    }

    public List<SelectItem> listarTarifasVigentes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TarifaOutorga object : tarifaOutorgaFacade.listarTarifasVigentes()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }
}
