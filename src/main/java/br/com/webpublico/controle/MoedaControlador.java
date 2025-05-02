/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.IndiceEconomico;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.entidades.Moeda;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.IndiceEconomicoFacade;
import br.com.webpublico.negocios.MoedaFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.interfaces.CRUD;
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

/**
 * @author terminal4
 */
@ManagedBean(name = "moedaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMoeda", pattern = "/moeda/novo/", viewId = "/faces/tributario/cadastromunicipal/moeda/edita.xhtml"),
        @URLMapping(id = "editarMoeda", pattern = "/moeda/editar/#{moedaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/moeda/edita.xhtml"),
        @URLMapping(id = "listarMoeda", pattern = "/moeda/listar/", viewId = "/faces/tributario/cadastromunicipal/moeda/lista.xhtml"),
        @URLMapping(id = "verMoeda", pattern = "/moeda/ver/#{moedaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/moeda/visualizar.xhtml")
})
public class MoedaControlador extends PrettyControlador<Moeda> implements Serializable, CRUD {

    @EJB
    private MoedaFacade facade;
    @EJB
    private IndiceEconomicoFacade indiceFacade;
    private ConverterGenerico converterIndice;
    private MoneyConverter moneyConverter;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoMoeda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verMoeda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarMoeda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<SelectItem> getIndices() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (IndiceEconomico object : indiceFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Mes object : Mes.values()) {
            toReturn.add(new SelectItem(object.getNumeroMes(), object.getDescricao()));
        }
        return toReturn;
    }

    public Converter getConverterIndice() {
        if (converterIndice == null) {
            converterIndice = new ConverterGenerico(IndiceEconomico.class, indiceFacade);
        }
        return converterIndice;
    }

    public MoedaControlador() {
        super(Moeda.class);
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public boolean validaMoeda() {
        boolean retorno = true;
        if (facade.existeMoeda((Moeda) selecionado)) {
            FacesUtil.addError("Atenção!", "Moeda já existente no sistema.");
            retorno = false;
        }
        return retorno;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/moeda/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        if (validaMoeda()) {
            super.salvar();
        }
    }
}
